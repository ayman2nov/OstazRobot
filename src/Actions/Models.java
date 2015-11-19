/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Actions;

import javax.swing.DefaultComboBoxModel;

/**
 *
 * @author Ayman
 */
public class Models {
    static DefaultComboBoxModel MouseModel ;
    static DefaultComboBoxModel KBModel ;
    static DefaultComboBoxModel OthersModel ;

    public Models() {
        MouseModel = new DefaultComboBoxModel();

        MouseModel.addElement("MoveTO");
        MouseModel.addElement("Left Press");
        MouseModel.addElement("Left Release");
        MouseModel.addElement("Right Press");
        MouseModel.addElement("Right Release");
        KBModel = new DefaultComboBoxModel();
        OthersModel = new DefaultComboBoxModel();
        
    }
    
}
