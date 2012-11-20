package tk.product;
//ejercicio EquityBasket
import java.io.Serializable;
import java.util.Enumeration;
import java.util.Hashtable;



/**
 * COOKBOOK EXAMPLE:
 *    How do I create a custom persistent object that does not extend from an
 *    existing Calypso object?
 * -----
 * This example is an extension to the Calypso API.  It creates a custom
 * persistent object that does not extend from an existing Calypso object.  This
 * particular class is a very simple model of an equity basket, a collection of
 * equity names and corresponding weights that represent the percentage of shares
 * that each equity contributes to the entire basket.  This example is for
 * illustrative purposes only, and should not be used to model an actual,
 * full-fledged equity basket.
 * -----
 *
 * NOTE ON SERIALIZATION:
 * In order for the data server to properly handle EquityBaskiet as a persistent
 * object, the class must implement Serializable.  In short, serialization is
 * the process of converting an object to a stream of bytes.  Deserialization is
 * the inverse process of reconstructing an object from a stream of bytes.
 *
 * In general, serialization/deserialization is needed to transfer objects across
 * the network (e.g., as arguments in RMI service remote method invocations),
 * and it can also be used to store objects in a non-relational (flat) structure
 * in a file or database and to later reconstruct those objects in memory.
 *
 * For the Calypso system in particular, there are many important benefits to the
 * data server's use of serialized objects.  For example, serialization allows
 * the data server to pass the object between the client machine and the database
 * via the data server's RemoteAccess RMI service.  Furthermore, it allows the
 * data server to distribute a single object across multiple machines.  Other
 * important data server benefits that can be made use of by serializable objects
 * include caching, fault-tolerant transaction handling, event generation, data
 * security, data authorization, and data auditing and versioning.  For more
 * information, see the Developer's Guide entry for the Data Server.
 *
 * For simplicity, this example uses default serialization/deserialization
 * (i.e., it does not declare the serialVersionUID field, nor does it implement
 * the writeObject or readObject methods).  For an example that overrides default
 * serialization by implementing Externalizable instead of Serializable, see "How
 * do I create and use a custom event?" in the Calypso Cookbook
 * (PSEventEquityBasket.java).  For further information, see Sun's document,
 * "Object Serilization".
 */
@SuppressWarnings({"unchecked","rawtypes"})
public class EquityBasket implements Serializable {

    private static final long serialVersionUID = -3350651080498261165L;
    
	protected String    _name;
    protected String    _audit;
    protected Hashtable _equityWeights = new Hashtable();

    public void setName(String name) {
        _name = name;
    }

    public String getName() {
        return _name;
    }

    public void setAudit(String audit) {
        _audit = audit;
    }

    public String getAudit() {
        return _audit;
    }
    

	public void addEquityWeight(String equity, double weight) {
        _equityWeights.put(equity, new Double(weight));
    }

    public double getEquityWeight(String equity) {
        return((Double) _equityWeights.get(equity)).doubleValue();
    }

    public Enumeration getEquityNames() {
        return _equityWeights.keys();
    }

    public String toString() {

        String      s           = _name;
        Enumeration equityNames = getEquityNames();

        while(equityNames.hasMoreElements()) {
            String equityName   = (String) equityNames.nextElement();
            double equityWeight = getEquityWeight(equityName);

            s += " " + equityName + ":" + equityWeight;
        }

        return s;
    }
}
