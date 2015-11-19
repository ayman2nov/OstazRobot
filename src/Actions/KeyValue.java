/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Actions;

/**
 *
 * @author Ayman
 */
public class KeyValue {
    String Key;
    int value;

    public KeyValue(String Key, int value) {
        this.Key = Key;
        this.value = value;
    }

    public String getKey() {
        return Key;
    }

    public int getValue() {
        return value;
    }

    public void setKey(String Key) {
        this.Key = Key;
    }

    public void setValue(int value) {
        this.value = value;
    }
    
}
