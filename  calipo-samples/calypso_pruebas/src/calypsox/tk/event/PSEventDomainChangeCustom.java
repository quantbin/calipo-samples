
package calypsox.tk.event;

import java.io.Externalizable;

import com.calypso.tk.event.PSEventDomainChange;

public class PSEventDomainChangeCustom extends PSEventDomainChange
        implements
        Externalizable {

    private static final long serialVersionUID = 3106154260482397788L;

    /**
     * Integer defining the type of modified data, must be superior to ID_MAX
     * which is defined in PSEventDomainChange.
     */
    final static public int MY_DATA = ID_MAX + 1;

    protected int    _type;
    protected int    _action;
    protected String _value;
    protected int    _valueId;

    public PSEventDomainChangeCustom() { }

    public PSEventDomainChangeCustom(int type, int action, String value) {
        super(type, action, value);
    }

    public PSEventDomainChangeCustom(int type, int action, int valueId) {
        super(type, action, valueId);
    }

    public PSEventDomainChangeCustom(int type,
            int action,
            String value,
            int valueId) {
        super(type, action, value, valueId);
    }

    public String getEventType() {
        if (_type < ID_MAX) {
            return super.getEventType();
        }
        String action = "NEW";
        if (_action == REMOVE) {
            action = "REMOVE";
        }
        if (_action == MODIFY) {
            action = "MODIFY";
        }
        if (_type == MY_DATA) {
            return action
                    + "_MY_DATA";
        }
        else {
            return null;
        }
    }

    public boolean isAuthorizableObject() {
        if (_type < ID_MAX) {
            return super.isAuthorizableObject();
        } else if (_type == MY_DATA) {
            return false; //can be true
        } else {
            return false;
        }
    }

}
