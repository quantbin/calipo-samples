package samples;
//ejercicio EquityBasket
import java.rmi.RemoteException;
import tk.product.EquityBasket;
import tk.service.EquityBasketTransactionInput;

import com.calypso.tk.core.Log;
import com.calypso.tk.service.DSConnection;
import com.calypso.tk.util.ConnectException;
import com.calypso.tk.util.ConnectionUtil;


/**
 * COOKBOOK EXAMPLE:
 *    How do I create a custom persistent object that does not extend from an
 *    existing Calypso object?
 * -----
 * This example is a client program that uses the Calypso API and can be run
 * from the command line.  It demonstrates the use of a custom persistent object
 * that does not extend from an existing Calypso object.  This example creates a
 * new equity basket object, saves it to the database, then loads it back from
 * the database to demonstrate that it was made persistent.  The program then
 * deletes the new equity basket from the database and attempts to load it back
 * from the database again, to demonstrate that it was indeed removed.
 */
public class UseEquityBasket {

    @SuppressWarnings("deprecation")
	public static void main(String[] args) {

        if(args.length < 2) {
            Log.system("samples", "Usage: java UseEquityBasket"
                               + "-env <envName> -password <password>");
            System.exit(0);
        }

        String inputEquityBasketName = "Basket_1";

        // 1. CONNECT TO THE DATA SERVER
        DSConnection ds = null;

        try {
            ds = ConnectionUtil.connect(args, "UseEquityBasket");
        } catch(ConnectException e) {
            Log.error(Log.CALYPSOX, e);
            Log.system("samples", "ERROR: UseEquityBasket: "
                               + "Connection to data server failed.");

            return;
        }

        try {

            // --
            // 2. CREATE AN EQUITY BASKET
            // --
            EquityBasket newEB = new EquityBasket();

            newEB.setName(inputEquityBasketName);
            newEB.addEquityWeight("ABC", .5);
            newEB.addEquityWeight("DEF", .25);
            newEB.addEquityWeight("XYZ", .25);

            // --
            // 3. SAVE THE EQUITY BASKET
            // --
            EquityBasketTransactionInput itemToSave = new EquityBasketTransactionInput(newEB, "save");

            // Pass the TransactionInput class to the DataServer through RemoteAccess.
            ds.getRemoteAccess().process(itemToSave);
            Log.system("samples", "UseEquityBasket: Saved equity basket.");

            // --
            // 4. LOAD THE EQUITY BASKET JUST SAVED
            // --
            // For load operations, the transaction handler expects a transaction
            // input object that contains an equity basket with the same name as
            // the equity basket intended to be loaded.  This new equity basket
            // (ebWithNameOnly) is just a throwaway object.
            EquityBasket ebWithNameOnly = new EquityBasket();

            ebWithNameOnly.setName(inputEquityBasketName);

            EquityBasketTransactionInput loadInstructions = new EquityBasketTransactionInput(ebWithNameOnly, "load");
            EquityBasket loadedEB = (EquityBasket) ds.getRemoteAccess().process(loadInstructions);

            Log.system("samples", "UseEquityBasket: Loaded equity basket.");
            Log.system("samples", "" + loadedEB);

            // --
            // 5. DELETE THE LOADED EQUITY BASKET
            // --
            //EquityBasketTransactionInput itemToDelete = new EquityBasketTransactionInput(loadedEB, "delete");

            //ds.getRemoteAccess().process(itemToDelete);
            //Log.system("samples", "UseEquityBasket: Deleted equity basket.");
        } catch(RemoteException e) {
            Log.error(Log.CALYPSOX, e);
            Log.system("samples", "ERROR: UseEquityBasket: "+ "Error invoking RemoteAccess.process...");
        }
        Log.system("samples", "Disconecting...");
        // --
        // 8. DISCONNECT FROM THE DATA SERVER
        // --
        ds.disconnect();
        Log.system("samples", "Disconected...");
    }
}
