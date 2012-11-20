package com.calypso.apps.startup;

import tk.event.PSEventEquityBasket;
import tk.product.EquityBasket;

import com.calypso.engine.Engine;
import com.calypso.tk.bo.BOTransfer;
import com.calypso.tk.core.Defaults;
import com.calypso.tk.core.Log;
import com.calypso.tk.event.PSEvent;
import com.calypso.tk.event.PSEventTransfer;
import com.calypso.tk.refdata.SettleDeliveryInstruction;
import com.calypso.tk.service.DSConnection;
import com.calypso.tk.util.ConnectionUtil;


/**
 *
 */
@SuppressWarnings("unused")
public class EverisCustomEngine extends Engine {

	//Nombre del engine
    final static protected String ENGINE_NAME = "EverisCustomEngine";

    public EverisCustomEngine(DSConnection ds, String host, int port) {
        super(ds, host, port);
    }

    public String getEngineName() {
        return ENGINE_NAME;
    }

    /**
     * GSM: handle del eventos de este engine.
     */
    public boolean process(PSEvent event) {

        boolean      result = true;
        DSConnection ds     = getDS();
        //Aqui es donde se gestiona el evento creado de forma especifica para la logica de
        //EquityBasket. El evento ha sido generado por EquityBasketTransactionHandler
        //para asegurar la atomicidad de la operacion. 
        //El DS se asegurará de declarar que se ha realizado el commit de la operación en la BBDD.
        if(event instanceof PSEventEquityBasket) {
            handleEvent((PSEventEquityBasket) event);

            try {
                ds.getRemoteTrade().eventProcessed(event.getId(), ENGINE_NAME);

            } catch(Exception e) {
                Log.error(Log.CALYPSOX, e);
                result = false;
            }
        }

        return result;
    }

    /**
     * 
     */

	public void handleEvent(PSEventEquityBasket event) {

    	Log.system("samples", "Received Equity Basket: " + event.toString());
    	
        DSConnection ds = getDS();
       
        try {
        	EquityBasket eb = event.getEquityBasket();
        	String name = eb.getName();
        	
            eb.setAudit("Everis Custom Engine audit done for:" + name + ".");
            Log.system("samples", "Audit done by Everis Custom Engine for: " + name);
        	
        } catch(Exception e) {
            Log.error(Log.CALYPSOX, e);
        }
    }

    static public void main(String args[]) {

        DSConnection ds = null;

        try {
        	//Nombre que se mostrara en la ventana Running Engines
            ds = ConnectionUtil.connect(args, "EverisEngine");
        } catch(Exception e) {
            Log.system("samples", "Usage -env <envName> -user <UserName> -password <password>");
            Log.error(Log.CALYPSOX, e);
            return;
        }

        String       host   = Defaults.getESHost();
        int          port   = Defaults.getESPort();
        EverisCustomEngine engine = new EverisCustomEngine(ds, host, port);

        try {
            engine.start();
        } catch(Exception ee) {
            Log.error(Log.CALYPSOX, ee);
            System.exit(-1);
        }
    }
}