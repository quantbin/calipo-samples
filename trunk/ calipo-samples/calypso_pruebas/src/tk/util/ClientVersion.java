package tk.util;

import java.awt.Component;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.calypso.apps.util.CalypsoComboBox;
import com.calypso.apps.util.EnvLoginInterface;
import com.calypso.tk.util.ClientVersionInterface; 


public class ClientVersion implements ClientVersionInterface{
	
    	JPanel everismainPanel = new JPanel();
	JPanel everisclientCustomPanel = new JPanel();   
    	JLabel everisJLabel = new JLabel();
    	JTextField everisText = new JTextField();
	JButton everisButton = new JButton();

    CalypsoComboBox FranChoice;

    public EnvLoginInterface _interface;
    static JFrame FRAME = new JFrame();

    
	public String getName (){
		return "Calypso Testing Environment";
	}
	public String getVersionDate (){
		return "11-10-2012";
	}
	public String getVersion (){
		return "EVERIS 1.0";
	}
	public Component getGUIComponent (){
		
		
        	//Contenido a añadir a la ventana de login
		
        
		return null;
	}
}