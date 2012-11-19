/*
 * Lista de suscripcion y eventos. Ejemplo de uso.
 * MySubscriber, clase interna, implementa newEvent(..). Siempre que una clase en 
 * subscriptionList es llamado, pasará por este método.
 *
 */

package samples;



//import java.util.*;

import com.calypso.tk.core.*;
import com.calypso.tk.event.*;
import com.calypso.tk.service.*;
import com.calypso.tk.util.*;


public class PSSample {

    private static final String LOGCAT = "PSSample";
    
    @SuppressWarnings({ "unused", "rawtypes" })
	static public void main(String args[]) throws Exception
	{
        Log.system(LOGCAT, "Connecting to ds...");
        
        DSConnection ds = 
            ConnectionUtil.connect(args, "PSSample");

        // create a subscriber
        MySubscriber eventListener = new MySubscriber();
        
        // events we are interested in
        Class[] subscriptionList = new Class[] {
                                                PSEventTrade.class,
                                                PSEventMessage.class,
                                                PSEventTime.class,
                                            };
        
        Log.system(LOGCAT, "Connecting to jms bus...");
        PSConnection ps = 
            ESStarter.startConnection(eventListener, subscriptionList);

        Log.system(LOGCAT, "Waiting before publishing 5 events...");
        Thread.sleep(3000);
        
        for(int i=0; i < 5; i++) {
            // build an event
            PSEventTime eventTime = new PSEventTime();
            eventTime.setTime(System.currentTimeMillis());
            eventTime.setComment("PSSample generated event "+i);

            Log.system(LOGCAT, "Publishing event "+eventTime+"...");
            ps.publish(eventTime);
        }
    }
    
    /**
     * MySubscriber class will be the call back point for 
     * all incoming events.  newEvent will be invoked when 
     * an event matching the subscription list is received. 
     */
    private static class MySubscriber implements PSSubscriber {
        
        public void newEvent(PSEvent event) {
            Log.system(LOGCAT, "Recieved event" 
                               +", id="+event.getId()
                               +", type="+event.getEventType()
                               +", event="+event
                               );
        }
        
        public void onDisconnect() {
            Log.system(LOGCAT, "Event bus has disconnected!");
        }
    }
}
