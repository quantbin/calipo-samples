
package samples;



import java.util.Vector;

import com.calypso.apps.startup.AppStarter;
import com.calypso.tk.core.Amount;
import com.calypso.tk.core.DefaultsBase;
import com.calypso.tk.core.JDate;
import com.calypso.tk.core.Log;
import com.calypso.tk.marketdata.PricingEnv;
import com.calypso.tk.marketdata.QuoteValue;
import com.calypso.tk.service.DSConnection;
import com.calypso.tk.util.CacheConnection;
import com.calypso.tk.util.ConnectionUtil;


/**
 * 
 */
public class TestMultiThreadQuotesGuille
{

    public static String       QUOTE_NAME = "FX.USD.EUR";
    public static JDate        _date      = JDate.getNow();
    public static PricingEnv   _envPrincing  = null;
    public static boolean      _useLocalB = false;
    public static DSConnection _dsConnection  = null;
    public static boolean      _random = false;
    public static boolean      _print = false;

    public static String getAppName() {
        return "TestMultiThreadQuotes";
    }

    static public void main(String args[])
    {

        if(args.length < 8) {
            Log.debug("samples", "Usage -env <envName> -user <UserName> "
                               + "-password <password> -local -pe <name>" +
                               " -ramdom <true|false> -print <true|false>");
            System.out.println("Usage -env <envName> -user <UserName> "
                    + "-password <password> -local -pe <name>" +
                    " -ramdom <true|false> -print <true|false>");

            return;
        }
        //var guille. Usamos metodo getOption para leer de la linea de comandos
        _random = DefaultsBase.getOption (args, "-random").equals("true");
        _print = DefaultsBase.getOption (args, "-print").equals("true");
        
        //se obtiene la conexion
        
        try {
            _dsConnection = ConnectionUtil.connect(args, "TestMultiThreadQuotes");
        } catch(Exception e) {
            Log.debug("samples", e);
            System.exit(-1);
        }

        String useLocal = AppStarter.getOption(args, "-local");

        if(useLocal != null) {
            _useLocalB = true;

            String peName = AppStarter.getOption(args, "-pe"); //pe, usuario name
            @SuppressWarnings("unused")
			CacheConnection cacheConnection = null;

            try {
                cacheConnection = new CacheConnection(DSConnection.getDefault());
                _envPrincing = _dsConnection.getRemoteMarketData().getPricingEnv(peName);

                if(_envPrincing == null) {
                    Log.debug("samples", "Could not load Pricing Env "
                                       + peName);
                } else {
                    Log.debug("samples", "Loaded Pricing Env " + peName);
                }
            } catch(Exception ex) {
                Log.error("main", ex);
            }
        }

        Thread t1 = new Thread(new QuoteReader());
        Thread t2 = new Thread(new QuoteWriter());

        t1.start();
        t2.start();
        
        
    }

    static public QuoteValue makeQuoteValue()
    {

        QuoteValue q = new QuoteValue();
        
        q.setName(QUOTE_NAME);
        q.setQuoteType("Price");
        q.setDate(_date);
        q.setUser(_dsConnection.getUser());
        q.setQuoteSetName("default");
        q.setVersion(0);
        q.setIsEstimatedB(false);
        q.setKnownDate(_date);
        
        if (_random)
        	q.setLast(Math.random());

        return q;
    }

    static class QuoteReader implements Runnable
    {

        public void run() {

            try {
                QuoteValue qv = makeQuoteValue();
                
                if (!_print)
                	Log.debug("samples", "Reading " + qv);
                else
                	System.out.println ("TestMultiThreadQuotes:QuouteReader Thread -> QuoteRead: " + qv.toString());

                if(_useLocalB) {
                    if((_envPrincing != null) && (_envPrincing.getQuoteSet() != null)) {
                        Log.debug("samples", "Got QuoteSet "
                                           + _envPrincing.getQuoteSet().getName());
                    }

                    qv = _envPrincing.getQuoteSet().getQuote(qv);
                } else {
                    qv = _dsConnection.getRemoteMarketData().getQuoteValue(qv);
                }

                if(qv == null) {
                    Log.debug("samples", "quote is NULL!!!!!");
                } else {
                    Log.debug("samples", qv.toString());
                }
            } catch(Exception e) {
                Log.error(Log.CALYPSOX, e);
            }
        }
    }
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
    static class QuoteWriter implements Runnable
    {

        
		public void run() {

            try {                
				Vector     v = new Vector();
                QuoteValue q = makeQuoteValue();
                Amount     a = new Amount(0, 5);

                a.set(1.10);
                q.setBid(a);

                a = new Amount(0, 5);

                a.set(1.15);
                q.setAsk(a);
                v.addElement(q);
                
                if (!_print)
                	Log.debug("samples", "Writing " + q);
                else
                	System.out.println ("TestMultiThreadQuotes:QuoteWriter Thread -> QuoteWrite: " + q.toString());
                
                _dsConnection.getRemoteMarketData().saveQuoteValues(v, 1);
            } catch(Exception e) {
                Log.error(Log.CALYPSOX, e);
            }
            
        }
    }
}

