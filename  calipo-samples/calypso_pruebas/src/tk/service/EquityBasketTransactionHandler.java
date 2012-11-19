package tk.service;
//ejercicio EquityBasket

import java.rmi.RemoteException;
import java.sql.Connection;

import tk.event.PSEventEquityBasket;
import tk.product.EquityBasket;
import tk.product.sql.EquityBasketSQL;

import com.calypso.tk.core.Log;
import com.calypso.tk.core.sql.ioSQL;
import com.calypso.tk.event.PSConnection;
import com.calypso.tk.event.sql.PSEventSQL;
import com.calypso.tk.service.DSTransactionHandler;
import com.calypso.tk.service.DSTransactionInput;


/**
 * COOKBOOK EXAMPLES:
 *    How do I create a custom persistent object that does not extend from an
 *    existing Calypso object?
 *    How do I create and use a custom event?
 * -----
 * This example is an extension to the Calypso API.  It processes a custom
 * persistent object that does not extend from an existing Calypso object.  This
 * particular class contains the transactional persistence logic for a very
 * simple model of an equity basket -- namely, saving, loading, and deleting.
 * Additionally, this example saves and publishes a corresponding custom event
 * for each transaction type.
 * -----
 *
 * Adding a transaction handler is the most basic mechanism for implementing
 * transactional persistence for a custom object.  In general, this mechanism is
 * most suitable for processing a small range of simple custom objects that
 * require only a limited set of persistence services.  In order to process a
 * larger range of more complex or related custom objects, or to process objects
 * requiring more sophisticated persistence services, Calypso provides a separate
 * mechanism -- the addition of custom RMI services.  For more information, see
 * "How do I add a custom RMI service?" in the Calypso Cookbook.
 *
 * The equity basket example is well-suited for processing by a transaction
 * handler.  This handler supports 3 basic operations: saving, loading, and
 * deleting.  Additionally, it handles the publication of persistent events to
 * notify other applications when those operations have been performed.
 *
 * In order to maintain the boundary between transaction handling and SQL, the
 * transaction handler will generally take responsibility for connection
 * management, commit, rollback, etc.  It should pass the connection to the SQL
 * class.  The SQL class, in turn, will generally take responsibility for SQL
 * statement construction and execution using the passed connection.
 */
public class EquityBasketTransactionHandler implements DSTransactionHandler {

    /**
     * From DSTransactionHandler interface.
     * The general sequence for the process method is:
     *
     *    1. Conduct the transaction
     *          a. Get a connection
     *          b. Execute the command
     *          c. Save the event
     *          d. Publish the event
     *          e. Commit the transaction
     *   2. If any of the above fails, roll back the transaction
     *   3. Finally, release the connection
     *   4. Return results, if applicable
     *
     * Note that this method is originally invoked via a call to the process
     * remote method of the RemoteAccess RMI service.  Therefore, the transaction
     * handler will actually be executed within data server memory, *not* within
     * client program memory.
     */
    @SuppressWarnings("deprecation")
	public Object process(DSTransactionInput input, PSConnection ps)
            throws RemoteException {

        // PSConnection parameter enables event publication within the
        // transaction.
        Object result = null;     // Object for any results returned by load.

        // --
        // 1. CONDUCT THE TRANSACTION
        // --
        // For simplicity, this particular example does not actually wrap the
        // transaction in an explicit begin and end trans.  Nevertheless, the
        // commit method must still be invoked at the end of the sequence (see
        // below).
        Connection con = null;    // Released below, outside of the try block

        try {

            // Initialize the event.  Will be published for save and delete.
            PSEventEquityBasket ebEvent = new PSEventEquityBasket();

            // --
            // A. GET A CONNECTION
            // --
            con = ioSQL.newConnection();                       // auto-commit mode set to false

            // --
            // B. EXECUTE THE COMMAND
            // --
            EquityBasketTransactionInput ebInput =
                (EquityBasketTransactionInput) input;
            EquityBasket equityBasket    = ebInput.getEquityBasket();
            String       transactionType = ebInput.getTransactionType();

            if(equityBasket != null) {
                if(transactionType.equals("save")) {
                    EquityBasketSQL.save(equityBasket, con);

                    // Create event for save.
                    ebEvent.setEquityBasket(equityBasket);
                    ebEvent.setAction("save");
                } else if(transactionType.equals("load")) {    // Pass in name only.
                    result = EquityBasketSQL.load(equityBasket.getName(),
                                                  con);

                    // Do not create an event for load.  Flag non-event as null.
                    ebEvent = null;
                } else if(transactionType.equals("delete")) {
                    EquityBasketSQL.delete(equityBasket.getName(), con);

                    // Create event for delete.
                    ebEvent.setEquityBasket(equityBasket);
                    ebEvent.setAction("delete");
                }
            }

            // --
            // C. SAVE THE EVENT
            // --
            // In order to ensure transactional atomicity, persistent events
            // *must* be saved and published within the same transaction that the
            // persistent object is handled.  Therefore, save and publish the
            // event here -- event handling for the persistent object should
            // *not* be handled separately by the client application with a
            // separate database connection.
            //
            // Also note that the save method *must* be invoked via the
            // PSEventSQL superclass, and *not* via the SQL subclass (if one
            // exists).  For more information, see PSEventEquityBasketSQL.java.
            // Save an event for save or delete transactions.  Do not save an
            // event for load transactions.
            if(ebEvent != null) {
                if( !PSEventSQL.save(ebEvent, con)) {
                    throw new RemoteException(
                        "EquityBasketTransactionHandler: "
                        + "Unable to save event...");
                }
            }

            // --
            // D. PUBLISH THE EVENT
            // --
            // Publish an event for save or delete transactions.  Do not publish
            // an event for load transactions.
            if(ebEvent != null) {
                ps.publish(ebEvent);
            }

            // --
            // E. COMMIT THE TRANSACTION
            // --
            // Invoking commit is required to actually save any changes to
            // the database, since the Connection object returned by
            // ioSQL.newConnection has auto-commit mode set to false.  If
            // commit is not invoked, then the database changes will not
            // actually be saved.
            ioSQL.commit(con);
        } catch(Exception e) {

            // --
            // ROLLBACK THE TRANSACTION IF ANY ERROR IS ENCOUNTERED
            // --
            // Since the transaction handler is executed within data server
            // memory, error output and logging will be associated with the
            // data server.
            Log.debug("samples", "ERROR: EquityBasketTransactionHandler: "
                               + "Could not conduct transaction...");
            ioSQL.rollback(con);

            throw new RemoteException(e.getMessage());
        }

        // --
        // 3. RELEASE THE CONNECTION
        // --
        finally {
            ioSQL.releaseConnection(con);
        }

        // --
        // 4. RETURN RESULTS
        // --
        // For save and delete transactions, simply returns null.  For load
        // transactions, returns an equity basket.
        return result;
    }
}
