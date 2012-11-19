package com.calypso.apps.startup;

import com.calypso.engine.Engine;
import com.calypso.tk.core.Defaults;
import com.calypso.tk.core.Log;
import com.calypso.tk.event.PSEvent;
import com.calypso.tk.service.DSConnection;
import com.calypso.tk.util.ConnectionUtil;

import com.calypso.apps.startup.AppStarter;

/**
 * Engine que es configurado para recibir todos los eventos y crear un log con el contenido de los mismos
 */
public class EverisLogEngine extends Engine {
    
	//Nombre del engine
    final static protected String ENGINE_NAME = "EverisLogEngine";

    public EverisLogEngine(DSConnection ds, String host, int port) {
        super(ds, host, port);
    }

    public String getEngineName() {
        return ENGINE_NAME;
    }

    /**
     * Creacion de las trazas
     */
    public boolean process(PSEvent event) {

        boolean      result = true;
        
        //En este caxo no necesitamos conectar ya que solo queremos mostrar trazas.
        //DSConnection ds     = getDS();
        
        Log.system(this, "Nuevo evento (ID " + event.getId() + "), de tipo: " + event.getEventType(), null);
        Log.system(this, "Contenido: " + event.toString(), null);

        return result;
    }


    static public void main(String args[]) {

        DSConnection ds = null;
        
        //Iniciamos el LOG
        Defaults.setIsEngine(true);
        AppStarter.startLog(args, "EverisLogEngine");

        try {
        	//Nombre que se mostrara en la ventana Running Engines
            ds = ConnectionUtil.connect(args, "EverisLogEngine");
        } catch(Exception e) {
            Log.system("samples", "Usage -env <envName> -user <UserName> -password <password>");
            Log.error(Log.CALYPSOX, e);
            return;
        }

        String       host   = Defaults.getESHost();
        int          port   = Defaults.getESPort();
        EverisLogEngine engine = new EverisLogEngine(ds, host, port);

        try {
            engine.start();
        } catch(Exception ee) {
            Log.error(Log.CALYPSOX, ee);
            System.exit(-1);
        }
    }
}