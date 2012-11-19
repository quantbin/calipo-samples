package tk.bo.workflow.rule;

import java.util.Vector;
import com.calypso.tk.bo.Task;
import com.calypso.tk.bo.TaskWorkflowConfig;
import com.calypso.tk.bo.workflow.WfTradeRule;
import com.calypso.tk.core.Trade;
import com.calypso.tk.service.DSConnection;

@SuppressWarnings("rawtypes")
public class NoTokyoTradeRule implements WfTradeRule {

    
	public boolean check (TaskWorkflowConfig wc,
			  Trade trade,
			  Trade oldTrade,
			  Vector messages,
			  DSConnection dsCon,
			  Vector excps,
			  Task task,
			  Object dbCon,
			  Vector events) {
	 return ( trade.getCounterParty().getAuthName().compareToIgnoreCase("tokyo")==0 );    
    }
     
    public String getDescription() {
	return "Update the comment with information of other fields";
    }

    public boolean update (TaskWorkflowConfig wc,
			   Trade trade,
			   Trade oldTrade,
			   Vector messages,
			   DSConnection dsCon,
			   Vector excps,
			   Task task,
			   Object dbCon,
			   Vector events) {
    	
    	String agent = trade.getAgent();
    	String stlcurr = trade.getTradeCurrency();
    	String book = trade.getBook().getName();

    	trade.setComment("Agente: "+agent+" Settle Curr: "+stlcurr+" Book: "+book +"\n es Tokyo!");

	return true;
	
    }
}