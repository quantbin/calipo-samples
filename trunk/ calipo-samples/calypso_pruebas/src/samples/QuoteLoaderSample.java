
package samples;

import com.calypso.tk.core.JDate;
import com.calypso.tk.core.Log;
import com.calypso.tk.core.Product;
import com.calypso.tk.marketdata.QuoteValue;
import com.calypso.tk.service.DSConnection;
import com.calypso.tk.util.ConnectionUtil;


/**
 * A simple client application that connects to the Data Server
 * and loads the latest price quote of the saved financial
 * instrument indicated by the passed product id number.
 * The user must also pass in the String name of his or her
 * QuoteSet.
 */
public class QuoteLoaderSample
{

    static public void main(String args[]) {

        if(args.length < 2) {
            Log.debug("samples", "Usage: java QuoteLoaderSample <Product Id> "
                               + "<Quote Set Name>");

            return;
        }

        int          inputProductId = Integer.parseInt(args[0]);
        String       inputQtSetName = args[1];
        DSConnection ds             = null;

        try {
            ds = ConnectionUtil.connect(args, "QuoteLoader");
        } catch(Exception e) {
            Log.error(Log.CALYPSOX, e);

            return;
        }

        Product prod = null;

        try {
            prod = ds.getRemoteProduct().getProduct(inputProductId);
        } catch(Exception e) {
            Log.error(Log.CALYPSOX, e);
            Log.debug("samples", "ERROR: QuoteLoaderSample ran into an "
                               + "error when loading the product");
            System.exit(0);
        }

        QuoteValue qt          = null;
        JDate      currentDate = JDate.getNow();

        qt = loadPriceFromQuote(prod, currentDate, inputQtSetName, ds);

        if(qt == null) {
            Log.debug("samples", "ERROR:  Quote not found");
            System.exit(0);
        }

        Log.debug("samples", "Quote name is " + qt.getName());
        Log.debug("samples", "Current price for instrument no."
                           + inputProductId + " is " + qt.getBid() + "/"
                           + qt.getAsk());
        Log.debug("samples", "Formatted price is " + qt.getBidAsObject() + "/"
                           + qt.getAskAsObject());
        Log.debug("samples", "Product description: " + prod.toString());
        Log.debug("samples", "Quote type: " + qt.getQuoteType());
        System.exit(0);
    }

    static public QuoteValue loadPriceFromQuote(Product product,
                                                JDate quoteDate,
                                                String qtsName,
                                                DSConnection ds) {

        QuoteValue q = new QuoteValue( qtsName,  product.getQuoteName(), 
                                      quoteDate,  QuoteValue.NONE );

        try {
            q = ds.getRemoteMarketData().getQuoteValue(q);
        } catch(Exception e) {
            Log.error(Log.CALYPSOX, e);
            Log.debug("samples", "ERROR: Could not retrieve QuoteValue.");
        }

        return q;
    }
}


