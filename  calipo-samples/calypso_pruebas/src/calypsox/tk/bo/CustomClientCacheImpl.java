package calypsox.tk.bo;

import java.util.HashMap;
import java.util.Vector;

import com.calypso.tk.bo.CustomClientCache;
import com.calypso.tk.core.Log;
import com.calypso.tk.event.PSEventDomainChange;
import com.calypso.tk.event.PSEventQuote;
import com.calypso.tk.event.PSEventQuoteRemoved;
import com.calypso.tk.marketdata.QuoteValue;
import com.calypso.tk.service.DSConnection;

/**
 * This example implements the CustomClientCache interface, which  provides an
 * extension mechanism for caching additional types of data not cached by
 * BOCache.  In this example, the type of additional data cached by the extension
 * is a set of all book names.
 *
 * For the client program code and more detailed information on how to use this
 * custom client cache, see COOKBXXX_UseCustomClientCache.java.
 */
@SuppressWarnings({"rawtypes","unchecked"})
public class CustomClientCacheImpl implements CustomClientCache
{
/* la interfaz fuerza los siguientes metodos:
    public abstract void clear();
    public abstract void newEvent(DSConnection dsconnection, PSEventDomainChange pseventdomainchange);
    public abstract void newEvent(DSConnection dsconnection, PSEventQuote pseventquote);
    public abstract void newEvent(DSConnection dsconnection, PSEventQuoteRemoved pseventquoteremoved);
 */

	static Vector _allBookNames = null;
    protected HashMap _quotes = new HashMap();
    protected HashMap _notFoundQuotes = new HashMap();

    /**
     * From CustomClientCache interface.
     *
     * Clears the Vector of book names.  Note that the client program using the
     * custom client cache need not invoke the clear method directly; BOCache
     * will invoke this method when necessary.
     */
    public void clear() {
	synchronized(this) {
            _allBookNames = null;
	}
    }
    

    /**
     * From CustomClientCache interface.
     *
     * This method gets called anytime a domain change event might have changed
     * the set of books.  So this method should force the cached Vector of book
     * names to be reinitialized from the data server.  This is accomplished by
     * clearing the Vector.
     */
    public void newEvent(DSConnection ds,PSEventDomainChange event) {
		switch(event.getType()) {
			case PSEventDomainChange.BOOK:
		            // Simply setting the vector of book names to null will force an
		            // explicit re-load from the data server on the next call to
		            // getBookNames.
		            Log.debug("calypsox", "CustomClientCacheImpl.newEvent:");
		            Log.debug("calypsox", "  Book data modified, re-initializing custom"
		                               + " client cache of book names...");
			    synchronized(this) {
		                _allBookNames = null;
		            }
		            break;
		        }
    }

    /**
     * From CustomClientCache interface.
     */
    public void newEvent(DSConnection ds,PSEventQuote event)
    {
    	
		QuoteValue qv=event.getQuote();
		QuoteValue q = null;
		synchronized(_quotes) {
		    q = (QuoteValue)_quotes.get(qv);
		}
		if(q != null) {
		    synchronized(_quotes) {
			_quotes.put(qv,qv);
		    }
		    Log.debug("calypsox", "CustomClientCacheImpl.newEvent:");
		    Log.debug("calypsox", "  Quote " + qv.getName() + " updated...");
		}
		else {
		    Log.debug("calypsox", "CustomClientCacheImpl.newEvent:");
		    Log.debug("calypsox", "  Quote (" + qv.getName() + ") change received - not used yet...");
		    _notFoundQuotes.remove(qv);
		}
    }
    /**
     * From CustomClientCache interface.
     */
    public void newEvent(DSConnection ds,PSEventQuoteRemoved event)
    {
	QuoteValue qv=event.getQuote();
	Log.debug("calypsox", "CustomClientCacheImpl.newEvent:");
	Log.debug("calypsox", "  Quote " + qv.getName() + " removed received - removing from cache ...");
	synchronized(_quotes) {
	    _quotes.remove(qv);
	}
    }

    /**
     * Returns the complete Vector of cached book names from the cache.
     *
     * Note that this method returns an object reference to the actual cache of book
     * names, not a cloned copy of the cache.
     */
    public Vector getBookNames(DSConnection dsCon) {
	if (_allBookNames != null) {
            Log.debug("calypsox", "CustomClientCacheImpl:"
                               + " Using cached Vector of book names...");
	    return _allBookNames;
        }

        Log.debug("calypsox", "CustomClientCacheImpl:"
                           + " NOT using cached Vector of book names,"
                           + " loading directly from data server...");
        Vector bookNames = new Vector();
	try {
	    bookNames = dsCon.getRemoteReferenceData().getBookNames();  
            synchronized (this) {
                _allBookNames = bookNames;
            }
	} catch (Exception e) {
            Log.debug("calypsox", "Error: Unable to get book names");
        }
	return _allBookNames;
    }

    public QuoteValue getQuote(DSConnection ds,QuoteValue qv) {
	QuoteValue q = null;
	synchronized(_quotes) {
	    q = (QuoteValue)_quotes.get(qv);
	}
	if (q != null) {
	    Log.debug("calypsox", "CustomClientCacheImpl.getQuote:");
	    Log.debug("calypsox", "  Retrieved " + q.getName() + " from cache ...");
	    return q;
	}
	//Look if it is not found already
	synchronized(_notFoundQuotes) { 
	    q = (QuoteValue)_notFoundQuotes.get(qv);
	}
	if (q != null) {
	    Log.debug("calypsox", "CustomClientCacheImpl.getQuote:");
	    Log.debug("calypsox", "  " + qv.getName() + " not available ...");
	    return null;
	}
	//Try it from DS now
	try {
	    q = ds.getRemoteMarketData().getQuoteValue(qv);
	    if (q != null) {
		synchronized(_quotes) {
		    _quotes.put(q,q);
		}
		Log.debug("calypsox", "CustomClientCacheImpl.getQuote:");
		Log.debug("calypsox", "  From Data Server: " + q.getName() + " added to cache ...");
		return q;
	    }
	    //cache unset values
	    QuoteValue copy=null;
	    copy = (QuoteValue)qv.clone();
	    synchronized(_notFoundQuotes) {
		_notFoundQuotes.put(copy,copy);
	    }
	    Log.debug("calypsox", "CustomClientCacheImpl.getQuote:");
	    Log.debug("calypsox", "  " + qv.getName() + " not found ...");
	    return null;
	}
	catch (Exception e) {
	    Log.debug("calypsox", "CustomClientCacheImpl.getQuote: Error: Cannot get quote");
	}
	return null;
    }
    
}
