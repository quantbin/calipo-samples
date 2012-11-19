package calypsox.tk.util;

import com.calypso.tk.util.GetPackages;
import java.util.*;

/**
* A utility interface to search for the particular package.
* That class will be used to get the Packages in Calypso.
*/

public class CustomGetPackages implements GetPackages {
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Vector getPackages() {
		
		Vector v = new Vector();
		v.addElement("EverisClientApp");
		return v;
		
		}
}
