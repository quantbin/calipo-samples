package tk.product.sql;
//ejercicio EquityBasket
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Enumeration;

import tk.product.EquityBasket;

import com.calypso.tk.core.Log;
import com.calypso.tk.core.PersistenceException;
import com.calypso.tk.core.sql.JResultSet;
import com.calypso.tk.core.sql.ioSQL;


/**
 * COOKBOOK EXAMPLE:
 *    How do I create a custom persistent object that does not extend from an
 *    existing Calypso object?
 * -----
 * This example is an extension to the Calypso API.  It processes a custom
 * persistent object that does not extend from an existing Calypso object.  This
 * particular class contains the relational persistence logic for a very simple
 * model of an equity basket.
 * -----
 *
 * The general sequence for each of the SQL methods is:
 *    1. For each statement:
 *          a. Construct the statement
 *          b. Execute the statement on the passed connection
 *          d. Finally, close the statement
 *    2. Return results, if applicable
 *    3. If any of the above fails, throw an exception
 *
 * Note that, in addition to implementing relational persistence logic, this
 * class is also the preferred location for implementing server-side caching.
 * By managing a cache of persistent objects here (e.g., as a static HashTable
 * of EquityBaskets), custom persistent objects can be kept in data server memory
 * and made available to multiple distributed clients.
 *
 * For example, when the load method is invoked, it could first try to find the
 * EquityBasket in the hashtable. If it was not found, it could then try to find
 * the object in the database.  If found, it could add that object to the cache
 * before returning it.  Subsequent calls to the load method for this object
 * would get the object directly from the cache, not the database.  Of course,
 * invocations of save, update, and delete would need to update the cache
 * accordingly.  For simplicity, this example does not demonstrate caching.
 */
@SuppressWarnings("deprecation")
public class EquityBasketSQL extends ioSQL {

    // This class extends ioSQL strictly for convenience; it is not required.
    // ioSQL contains only static members; simiarly, this class contains only
    // static members.

    /**
     * Save the passed equityBasket to the database using the passed connection.
     */
    @SuppressWarnings("rawtypes")
	public static void save(EquityBasket equityBasket, Connection passedConnection) throws PersistenceException {

        // --
        // 1. FOR EACH EQUITY/WEIGHT PAIRING IN THE EQUITY BASKET
        // --
        String      equityBasketName = equityBasket.getName();
        Enumeration equityNames      = equityBasket.getEquityNames();

        while(equityNames.hasMoreElements()) {

            // statement is closed below, outside of the try block.
            Statement statement = null;

            try {

                // --
                // A. CONSTRUCT THE STATEMENT
                // --
                String equityName = (String) equityNames.nextElement();
                double equityWeight = equityBasket.getEquityWeight(equityName);
                String statementString = "INSERT INTO EQUITY_BASQUET (" + "name," + "equity_name," + "equity_weight" + ") VALUES ("
                                         + "'" + equityBasketName + "',"
                                         + "'" + equityName + "',"
                                         + equityWeight + ")";

                // Since the transaction handler is executed within data server
                // memory, error output and logging will be associated with the
                // data server.
                ioSQL.trace(statementString);
                statement = ioSQL.newStatement(passedConnection);

                // --
                // B. EXECUTE THE STATEMENT ON THE PASSED CONNECTION
                // --
                statement.executeUpdate(statementString);
            } catch(SQLException e) {
                Log.debug("samples", "ERROR: EquityBasketSQL: " + "Unable to execute statement...");

                // Throw an exception back to the transaction handler and do not
                // continue to iterate through the while loop.
                throw new PersistenceException(e);
            }

            // --
            // C. CLOSE THE STATEMENT
            // --
            finally {
                ioSQL.close(statement);
            }
        }
    }

    /**
     * Load and return the named equity basket from the database.
     */
    public static EquityBasket load(String equityBasketName, Connection passedConnection) throws PersistenceException {

        // statement is closed below, outside of the try block.
        Statement statement = null;

        try {

            // --
            // A. CONSTRUCT THE STATEMENT
            // --
            String statementString = "SELECT " + "equity_name, " + "equity_weight " + "FROM EQUITY_BASQUET "
                                     + "WHERE name = '" + equityBasketName + "'";

            ioSQL.trace(statementString);
            statement = ioSQL.newStatement(passedConnection);

            // --
            // B. EXECUTE THE STATEMENT ON THE PASSED CONNECTION
            // --
            JResultSet rs = new JResultSet(statement.executeQuery(statementString));

            // --
            // C. CONSTRUCT AND RETURN THE OBJECT
            // --
            EquityBasket equityBasket = new EquityBasket();
            equityBasket.setName(equityBasketName);

            int j = 0;

            // The SELECT statement returns multiple rows.  Iterate through each
            // row, adding an equity weight for each.
            while(rs.next()) {
                j = 1;
                equityBasket.addEquityWeight(rs.getString(j++), rs.getDouble(j++));
            }

            rs.close();

            if(j == 0) {    // No results found.
                return null;
            } else {
                return equityBasket;
            }
        } catch(SQLException e) {
            Log.system("samples", "ERROR: EquityBasketSQL: " + "Unable to execute statement...");

            // Throw an exception back to the transaction handler.
            throw new PersistenceException(e);
        }

        // --
        // D. CLOSE THE STATEMENT
        // --
        finally {
            ioSQL.close(statement);
        }
    }

    /**
     * Delete the passed equityBasket from the database using the passed
     * connection.
     */
    public static void delete(String equityBasketName, Connection passedConnection) throws PersistenceException {

        // statement is closed below, outside of the try block.
        Statement statement = null;

        try {

            // --
            // A. CONSTRUCT THE STATEMENT
            // --
            String statementString = "DELETE EQUITY_BASQUET " + "WHERE name = '" + equityBasketName + "'";

            ioSQL.trace(statementString);
            statement = ioSQL.newStatement(passedConnection);

            // --
            // B. EXECUTE THE STATEMENT ON THE PASSED CONNECTION
            // --
            statement.executeUpdate(statementString);
        } catch(SQLException e) {
            e.printStackTrace();
            Log.system("samples", "ERROR: EquityBasketSQL: " + "Unable to execute statement...");

            // Throw an exception back to the transaction handler
            throw new PersistenceException(e);
        }

        // --
        // C. CLOSE THE STATEMENT
        // --
        finally {
            ioSQL.close(statement);
        }
    }
}