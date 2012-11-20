package tk.service;
//ejercicio EquityBasket
import java.io.Serializable;
import tk.product.EquityBasket;
import com.calypso.tk.service.DSTransactionInput;

/**
 * COOKBOOK EXAMPLE:
 *    How do I create a custom persistent object that does not extend from an
 *    existing Calypso object?
 * -----
 * This example is an extension to the Calypso API.  It processes a custom
 * persistent object that does not extend from an existing Calypso object.  In
 * general, this class models the input that a handler needs in order to process
 * persistence transactions on the custom object.  In most cases, this input
 * consists of the object itself, and optionally, some information that tells
 * the handler what type of persistence transaction to perform.  This particular
 * class contains the transaction input needed to save a very simple model of an
 * equity basket.
 * -----
 *
 * Note that objects passed to or returned from a remote method must be
 * Serializable.  The transaction input will be passed to the process remote
 * method of the RemoteAccess RMI service.  Therefore, it must implement
 * Serializable.
 *
 * When the transaction input is passed to the process remote method, it will be
 * deep-copy serialized on the client machine, passed across the network, then
 * deserialized on the server machine running the transaction handler.  This
 * requires that all class fields needed by the transaction handler (in this
 * case, both_equityBasket and _transactionType) are themselves Serializable.
 *
 * For more information on serialization, see EquityBasket.java or Sun's
 * document, "Object Serialization".
 */
public class EquityBasketTransactionInput
        implements DSTransactionInput, Serializable {

    protected EquityBasket _equityBasket;    // this object must be Serializable
    protected String       _transactionType;

    // serialVersionUID represents a class's unique "fingerprint"; normally, its
    // value changes as the class definition changes.  During default
    // serialization, the serialVersionUID of the class is compared to the
    // serialVersionUID of the serialized class. If there is a mismatch, the
    // deserialization fails.  By fixing this field's value, a class forces there
    // to *never* be a mismatch during deserialization, regardless of whether the
    // class has changed.  This can be useful for evolving classes in which
    // different version of serialized forms can be made compatible.
    static final long serialVersionUID = 3100753104406665931L;

    // DSTransactionInput declares serialVersionUID.  To avoid inheriting the
    // value, which might cause unexpected deserialization behavior if this
    // transaction input class changes later, it declares its own
    // serialVersionUID.  This requires that, if this class changes in the
    // future, it will handle the deserialization of any older-version serialized
    // forms that have been kept persistent.  To create this declaration:
    //
    //    1. Compile this class
    //    2. Run the serialver command on this class:
    //          serialver samples.cookbook.EquityBasketTransactionInput
    //    3. Add the declaration statement returned by the command to this class
    //    4. Recompile this class
    public EquityBasketTransactionInput(EquityBasket equityBasket,
                                        String transactionType) {
        _equityBasket    = equityBasket;
        _transactionType = transactionType;
    }

    /**
     * From DSTransactionInput interface.
     */
    //Este es el punto que se encarga de llamar al Handler especifico dado por la interfaz
    //Éste creará un evento para acceso persistente a la BBDD (PSEventEquityBasket) y, a traves de este, 
    //se llamara de forma estática a la base de datos.
    public String getHandler() {
        return "tk.service.EquityBasketTransactionHandler";
    }

    /**
     * Used by the transaction handler.  Returns the object which to apply the
     * persitence transaction to.
     */
    public EquityBasket getEquityBasket() {
        return _equityBasket;
    }

    /**
     * Used by the transaction handler.  Tells the transaction handler what type
     * of persistence transaction to perform.
     */
    public String getTransactionType() {
        return _transactionType;
    }
}
