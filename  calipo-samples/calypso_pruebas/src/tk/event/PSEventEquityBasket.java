package tk.event;
//ejercicio EquityBasket
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import tk.product.EquityBasket;

import com.calypso.tk.core.Util;
import com.calypso.tk.event.PSEvent;


/**
 * COOKBOOK EXAMPLE:
 *    How do I create and use a custom event?
 * -----
 * This example is an extension to the Calypso API.  It creates a custom type of
 * event that can be published or subscribed to.  This particular class
 * represents a persistence transaction that was conducted on an equity basket.
 * The event contains the action that was performed on the object (e.g., save or
 * delete), and the object that the transaction was actually performed on.  For
 * more information on the equity basket class, see "How do I create a custom
 * persistent object that does not extend from an existing Calypso object" in the
 * Calypso Cookbook.
 */
public class PSEventEquityBasket extends PSEvent implements Externalizable {

    // Because the Event Server transports events across the network anytime they
    // are published or consumed, events must be serializable.  Another example,
    // EquityBasket.java, demonstrates the simplest form of default
    // serialization.  In some cases, however, default serialization may not be
    // adequate, and a class can instead implement Externalizable to indicate
    // that it takes full responsibility for its own serialization and
    // versioning.  This requires properly implementing the writeExternal and
    // readExternal methods (as opposed to the writeObject and readObject
    // methods), as well as managing the serialized version #.
    //
    // For illustrative purposes, this class implements Externalizable
    // (although implementing Serializable would work as well).  Note the
    // declaration of serialVersionUID, as well as the implementation of the
    // writeExternal and readExternal methods below.
    //
    // For further information, see EquityBasket.java for a note on
    // serialization, or see EquityBasketTransactionInput.java for an explanation
    // of serialVersionUID.
    static final long serialVersionUID = -155492888168016549L;

    // The type of persitence transaction that was performed.
    protected String _action;

    // The equity basket that the transaction was actually performed on.  If
    // implementing custom persistence logic for this class, see
    // PSEventEquityBasketSQL.java for special considerations for this field.
    protected EquityBasket _equityBasket;

    /**
     * Helper constructor for subscribers.  Can be used to create an argument for
     * the method PSConnection.subscribe.
     */
    public PSEventEquityBasket() {}

    public PSEventEquityBasket(EquityBasket equityBasket, String action) {
        _equityBasket = equityBasket;
        _action       = action;
    }

    final public EquityBasket getEquityBasket() {
        return _equityBasket;
    }

    final public void setEquityBasket(EquityBasket t) {
        _equityBasket = t;
    }

    final public String getAction() {
        return _action;
    }

    final public void setAction(String action) {
        _action = action;
    }

    /**
     * From Externalizable interface.
     * -----
     * Serialize the superclass, then serialize *this* class's fields, invoking
     * Calypso's Util write methods for each.  The Util serialization methods
     * provide convenient wrapping that checks for potentially null objects.
     * This feature can be foregone by using the writeObject method instead for
     * default serialization.  But in any case, the choice of
     * serialization/deserialization methods must *match* between the
     * writeExternal and readExternal methods.  Also, note that if one of the
     * class fields here was *itself* externalizable, then this method must
     * instead invoke that field's writeExternal method.  In this case, however,
     * neither EquityBasket nor String are externalizable, so the Util methods
     * are appropriate.  The corresponding comments apply for the readExternal
     * method below.
     */
    @SuppressWarnings("deprecation")
	public void writeExternal(ObjectOutput out) throws IOException {

        super.writeExternal(out);
        Util.writeUTF(_action, out);
        Util.writeObject(_equityBasket, out);
    }

    /**
     * From Externalizable interface.
     * -----
     * Deserialize the superclass, then deserialize *this* class's fields,
     * invoking Calypso's Util read methods for each.  See the comments above.
     */
    @SuppressWarnings("deprecation")
	public void readExternal(ObjectInput in)
            throws IOException, ClassNotFoundException {

        super.readExternal(in);

        _action       = Util.readUTF(in);
        _equityBasket = (EquityBasket) Util.readObject(in);
    }
}
