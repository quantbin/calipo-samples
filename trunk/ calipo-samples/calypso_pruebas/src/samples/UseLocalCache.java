package samples;

import java.util.Vector;

//import sun.security.util.Cache;

import com.calypso.tk.core.Log;
import com.calypso.tk.service.DSConnection;
import com.calypso.tk.service.LocalCache;
//import com.calypso.tk.service.RemoteReferenceData;
//import com.calypso.tk.util.CacheConnection;
import com.calypso.tk.util.ConnectException;
import com.calypso.tk.util.ConnectionUtil;

//Core Calypso Development pag. 24, section 3.2
/**
 * This example is a client program that can be run from the command line.  It
 * demonstrates the use of LocalCache, a feature that allows the client to cache
 * copies of commonly-used but relatively stable reference data (e.g., domain
 * values, currency and rate indexes, FX resets, holidays, fee definitions) in
 * local memory space.  Use of this feature increases local performance, as well
 * as reducing network traffic and reducing the load on the data server.  This
 * example uses LocalCache to load a set of rate indexes, then demonstrates the
 * use of master version vs. cloned version domain value loading.
 */
/*
 * Local cache permite en la parte cliente "cachear" (más eficiente) datos estaticos 
 * como curvas, precios, currencies, dominios, etc.
 */
@SuppressWarnings({"rawtypes","deprecation"} )
public class UseLocalCache
{

	public static void main(String args[])
	{
        // 1. CONNECT TO THE DATA SERVER
        DSConnection ds = null;

        try {
        	//conexion al servidor
            ds = ConnectionUtil.connect(args, "UseLocalCache");
            
        } catch(ConnectException e) {
            Log.error(Log.CALYPSOX, e);
            Log.debug("samples", "ERROR: Connection to data server failed.");
            System.exit(-1);
        }

        // 2. LOAD PRINCIPAL STRUCTURE DOMAIN
        /*
         * Si se obtiene el DomainValues NO SE puede modificar directamente. Si es el caso, 
         * usar el metodo clone y modificar sobre esta instancia. Pag. 24
         */
        Vector domainValues = LocalCache.getDomainValues(ds,
                                  "principalStructure");
        
        /**
         * A partir de los metodos de LocalCache se accede a varios datos estaticos.
         */

        Log.debug("samples", "Principal structure domain");
        Log.debug("samples", "----------");

        for(int i = 0; i < domainValues.size(); i++) {
            Log.debug("samples", "" + domainValues.elementAt(i));
        }

        // 3 MODIFY THE LIST
        // If for any reason you need to modify the list returned from LocalCache
        // make sure you make a copy of the list (or call the appropriate method in LocalChace
        // that returns a copy of the list) and modify the copy.  Do not
        // modify the returned list directly otherwise the master list in LocalCache
        // will be changed.  
        // Make a copy of the list then modify

        Vector copyDomainValues = LocalCache.cloneDomainValues(ds,
                                      "principalStructure");

        copyDomainValues.removeElement("Mortgage");
        

        // copyDomainValues is safe to modify.  domainValues is NOT.
        // 3. DISCONNECT FROM THE DATA SERVER
        ds.disconnect();
    }
}
