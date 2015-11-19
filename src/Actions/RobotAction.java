/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Actions;

import java.io.Serializable;
import java.util.Vector;

/**
 *
 * @author Ayman
 */
public class RobotAction implements Serializable{
    String Name;
    Vector<String> Values ;

    public void setName(String Name) {
        this.Name = Name;
    }

    public void setValues(Vector<String> Values) {
        this.Values = Values;
    }

    public String getName() {
        return Name;
    }

    public Vector<String> getValues() {
        return Values;
    }

    public RobotAction(String Name, Vector<String> Values) {
        this.Name = Name;
        this.Values = Values;
    }

    public RobotAction() {
    }

}
