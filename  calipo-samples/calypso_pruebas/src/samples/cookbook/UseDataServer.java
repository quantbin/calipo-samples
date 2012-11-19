package samples.cookbook;

import java.rmi.RemoteException;

import com.calypso.tk.core.Action;
import com.calypso.tk.core.Defaults;
import com.calypso.tk.core.Log;
import com.calypso.tk.core.Product;
import com.calypso.tk.core.Status;
import com.calypso.tk.core.Trade;
import com.calypso.tk.service.DSConnection;
import com.calypso.tk.util.ConnectException;
import com.calypso.tk.util.ConnectionUtil;

/*
 * Este ejemplo muestra como: 1º conectar a la BBDD (UseDataServer), 2º recuperar un producto,
 * 3º recuperar un trade, 4º guardar un producto y 5º guardar el trade
 */

/**
 * This example is a client program that can be run from the command line.  It
 * gets a connection to the data server, loads a product and trade, then re-saves
 * the product and trade as new objects.
 */
public class UseDataServer {

    @SuppressWarnings("deprecation")
	public static void main(String args[]) {

        if(args.length < 4) {
            Log.debug("samples", "Usage: java UseDataServer"
                               + " -pid <productId>" + " -tid <tradeId>"
                               + " <connectionArguments...>");
            System.exit(0);
        }

        int inputProductId = Integer.parseInt(Defaults.getOption(args,"-pid"));
        int inputTradeId = Integer.parseInt(Defaults.getOption(args, "-tid"));

        // --
        // 1. CONNECT TO THE DATA SERVER
        // --
        // Invoke the connect method.  Note that there are several overloaded
        // forms of the method ConnectionUtil.connect.  Each such method
        // returns a DSConnection.  This example invokes the form that takes as
        // arguments an array of connection information and the name of the
        // connecting client and returns a DSConnection. 
        DSConnection ds = null;

        try {
            Log.error("samples", "Invoking connect()...");

            ds = ConnectionUtil.connect(args, "UseDataServer");
        } catch(ConnectException e) {

            // Use the method Log.error() to report the stack trace for any
            // caught exception.
            Log.error(Log.CALYPSOX, e);
            Log.debug("samples", "ERROR: Connection to data server failed.");
            System.exit(-1);
        }

        Log.debug("samples", "Connected.");

        // --
        // 2. LOAD A PRODUCT
        // --
        // Use the RemoteProduct RMI service to load a product by id #.
        Product product = null;

        try {
            Log.debug("samples", "Invoking getProduct()...");
            //aqui vemos como recuperar un producto
            product = ds.getRemoteProduct().getProduct(inputProductId);
        } catch(RemoteException e) {
            Log.error(Log.CALYPSOX, e);
            Log.debug("samples", "ERROR: Failed to load product, id #: "
                               + inputProductId);
        }

        if(product == null) {
            Log.debug("samples", "ERROR: Product not found, id #: "
                               + inputProductId);
        } else {

            //Display product description
            Log.debug("samples", "Product: " + product.getId() + " "
                               + product.getDescription());
        }

        // --
        // 3. LOAD A TRADE
        // --
        // Use the RemoteTrade RMI service to load a trade by id #.
        Trade trade = null;

        try {
            Log.debug("samples", "Invoking getTrade()...");
            //recuperar un producto
            trade = ds.getRemoteTrade().getTrade(inputTradeId);
        } catch(RemoteException e) {
            Log.error(Log.CALYPSOX, e);
            Log.debug("samples", "ERROR: Failed to load trade, id #: "
                               + inputTradeId);
        }

        if(trade == null) {
            Log.debug("samples", "ERROR: Trade not found, id #: "
                               + inputTradeId);
        } else {

            //Display some of the loaded trade's basic information
            Log.debug("samples", "Trade.getProductId:    "
                               + trade.getProductId());
            Log.debug("samples", "Trade.getProductType:  "
                               + trade.getProductType());
            Log.debug("samples", "Trade.getBook:         " + trade.getBook());
            Log.debug("samples", "Trade.getCounterParty: "
                               + trade.getCounterParty());
        }

        // --
        // 4. RE-SAVE THE PRODUCT TO THE DATABASE AS A NEW OBJECT
        // --
        // Use the RemoteProduct RMI service to save the product with a new id #.
        // First, mark the product as a completely new object by re-initializing
        // its current id # to zero.
        if(product != null) {
            product.setId(0);

            int newProductId = 0;

            try {
                Log.debug("samples", "Invoking saveProduct()...");
                //obtenemos le producto y lo salvamos!
                //OJO! al salvar nos devuelve el nuevo ID del producto!!
                newProductId = ds.getRemoteProduct().saveProduct(product);

                // NOTE: it is important to set the newProductId to the product othewise
                // the product will continue to have an id of 0.
                product.setId(newProductId);
                Log.debug("samples", "Re-saved as new product, id #: "+ newProductId);
                
            } catch(RemoteException e) {
                Log.error(Log.CALYPSOX, e);
                Log.debug("samples", 
                    "ERROR: Failed to re-save as new product, id #: "
                    + inputProductId);
            }
        }

        // --
        // 5. RE-SAVE THE TRADE TO THE DATABASE AS A NEW OBJECT
        // --
        // Use the RemoteTrade RMI service to save the trade with a new id #.
        // First, mark the trade as a completely new object by re-initializing
        // its id # and resetting its action and status.  Additionally, ensure
        // that, if the trade's underlying product is a unique over-the-counter
        // instrument, that that underlying product is also saved as a new
        // object.
        if(trade != null) {
            trade.setId(0);
            trade.setAction(Action.NEW);
            trade.setStatus(Status.S_NONE);

            int newTradeId = 0;

            if( !trade.getProduct().hasSecondaryMarket()) {
                trade.getProduct().setId(0);
            }

            try {
                Log.debug("samples", "Invoking getRemoteTrade().save()...");
                //salvamos la operacion - trade
                newTradeId = ds.getRemoteTrade().save(trade);

                // NOTE: it is important to set the newTradeId to the trade othewise
                // the trade will continue to have an id of 0.
                trade.setId(newTradeId);
                Log.debug("samples", "Re-saved as new trade, id #: "
                                   + newTradeId);
            } catch(RemoteException e) {
                Log.error(Log.CALYPSOX, e);
                Log.debug("samples", 
                    "ERROR: Failed to re-save as new trade, id #: "
                    + inputTradeId);
            }
        }

        // --
        // 6. DISCONNECT FROM THE DATA SERVER
        // --
        // Call the disconnect method.  This method disconnects the connecting
        // client application from the data server.  If this method is not
        // called, the application will not exit properly.
        Log.debug("samples", "Calling Disconnect...");
        ds.disconnect();
        Log.debug("samples", "Disconnected.");
    }
}
