/*
 * OstazRobotView.java
 */
package ostazrobot;

import Actions.KeyValue;
import Actions.RobotAction;
import java.awt.AWTException;
import java.awt.Robot;
import java.awt.MouseInfo;
//import org.pushingpixels.substance.api.skin.SubstanceGeminiLookAndFeel
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jdesktop.application.Action;
import org.jdesktop.application.ResourceMap;
import org.jdesktop.application.SingleFrameApplication;
import org.jdesktop.application.FrameView;
import org.jdesktop.application.Task;
import org.jdesktop.application.TaskMonitor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.Vector;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.Timer;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.text.Keymap;

/**
 * The application's main frame.
 */
public class OstazRobotView extends FrameView {

    Robot mainrobot;
    Vector<RobotAction> listofactions = new Vector<RobotAction>();
    DefaultListModel list = new DefaultListModel();

    public OstazRobotView(SingleFrameApplication app) {
        super(app);

        initComponents();

        // status bar initialization - message timeout, idle icon and busy animation, etc
        ResourceMap resourceMap = getResourceMap();
        int messageTimeout = resourceMap.getInteger("StatusBar.messageTimeout");
        messageTimer = new Timer(messageTimeout, new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                statusMessageLabel.setText("");
            }
        });
        messageTimer.setRepeats(false);
        int busyAnimationRate = resourceMap.getInteger("StatusBar.busyAnimationRate");
        for (int i = 0; i < busyIcons.length; i++) {
            busyIcons[i] = resourceMap.getIcon("StatusBar.busyIcons[" + i + "]");
        }
        busyIconTimer = new Timer(busyAnimationRate, new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                busyIconIndex = (busyIconIndex + 1) % busyIcons.length;
                statusAnimationLabel.setIcon(busyIcons[busyIconIndex]);
            }
        });
        idleIcon = resourceMap.getIcon("StatusBar.idleIcon");
        statusAnimationLabel.setIcon(idleIcon);
        progressBar.setVisible(false);

        // connecting action tasks to status bar via TaskMonitor
        TaskMonitor taskMonitor = new TaskMonitor(getApplication().getContext());
        taskMonitor.addPropertyChangeListener(new java.beans.PropertyChangeListener() {

            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                String propertyName = evt.getPropertyName();
                if ("started".equals(propertyName)) {
                    if (!busyIconTimer.isRunning()) {
                        statusAnimationLabel.setIcon(busyIcons[0]);
                        busyIconIndex = 0;
                        busyIconTimer.start();
                    }
                    progressBar.setVisible(true);
                    progressBar.setIndeterminate(true);
                } else if ("done".equals(propertyName)) {
                    busyIconTimer.stop();
                    statusAnimationLabel.setIcon(idleIcon);
                    progressBar.setVisible(false);
                    progressBar.setValue(0);
                } else if ("message".equals(propertyName)) {
                    String text = (String) (evt.getNewValue());
                    statusMessageLabel.setText((text == null) ? "" : text);
                    messageTimer.restart();
                } else if ("progress".equals(propertyName)) {
                    int value = (Integer) (evt.getNewValue());
                    progressBar.setVisible(true);
                    progressBar.setIndeterminate(false);
                    progressBar.setValue(value);
                }
            }
        });
        try {
            mainrobot = new Robot();
        } catch (AWTException ex) {
            Logger.getLogger(OstazRobotView.class.getName()).log(Level.SEVERE, null, ex);
        }
        initkeys();
        DefaultComboBoxModel keysmodel = new DefaultComboBoxModel();
        for (int i = 0; i < keys.size(); i++) {
            keysmodel.addElement(keys.get(i).getKey());
        }
        COM_Keys.setModel(keysmodel);

    }
    Vector<KeyValue> keys = new Vector<KeyValue>();

    void initkeys() {
        keys.add(new KeyValue("ENTER", '\n'));
        keys.add(new KeyValue("BACK_SPACE", '\b'));
        keys.add(new KeyValue("TAB", '\t'));
        keys.add(new KeyValue("CANCEL", 0x03));
        keys.add(new KeyValue("CLEAR", 0x0C));
        keys.add(new KeyValue("SHIFT", 0x10));
        keys.add(new KeyValue("CONTROL", 0x11));
        keys.add(new KeyValue("ALT", 0x12));
        keys.add(new KeyValue("PAUSE", 0x13));
        keys.add(new KeyValue("CAPS_LOCK", 0x14));
        keys.add(new KeyValue("ESCAPE", 0x1B));
        keys.add(new KeyValue("SPACE", 0x20));
        keys.add(new KeyValue("PAGE_UP", 0x21));
        keys.add(new KeyValue("PAGE_DOWN", 0x22));
        keys.add(new KeyValue("END", 0x23));
        keys.add(new KeyValue("HOME", 0x24));
        keys.add(new KeyValue("LEFT", 0x25));
        keys.add(new KeyValue("UP", 0x26));
        keys.add(new KeyValue("RIGHT", 0x27));
        keys.add(new KeyValue("DOWN", 0x28));
        keys.add(new KeyValue("COMMA", 0x2C));
        keys.add(new KeyValue("MINUS", 0x2D));
        keys.add(new KeyValue("PERIOD", 0x2E));
        keys.add(new KeyValue("SLASH", 0x2F));
        keys.add(new KeyValue("0", 0x30));
        keys.add(new KeyValue("1", 0x31));
        keys.add(new KeyValue("2", 0x32));
        keys.add(new KeyValue("3", 0x33));
        keys.add(new KeyValue("4", 0x34));
        keys.add(new KeyValue("5", 0x35));
        keys.add(new KeyValue("6", 0x36));
        keys.add(new KeyValue("7", 0x37));
        keys.add(new KeyValue("8", 0x38));
        keys.add(new KeyValue("9", 0x39));
        keys.add(new KeyValue("SEMICOLON", 0x3B));
        keys.add(new KeyValue("EQUALS", 0x3D));
        keys.add(new KeyValue("A", 0x41));
        keys.add(new KeyValue("B", 0x42));
        keys.add(new KeyValue("C", 0x43));
        keys.add(new KeyValue("D", 0x44));
        keys.add(new KeyValue("E", 0x45));
        keys.add(new KeyValue("F", 0x46));
        keys.add(new KeyValue("G", 0x47));
        keys.add(new KeyValue("H", 0x48));
        keys.add(new KeyValue("I", 0x49));
        keys.add(new KeyValue("J", 0x4A));
        keys.add(new KeyValue("K", 0x4B));
        keys.add(new KeyValue("L", 0x4C));
        keys.add(new KeyValue("M", 0x4D));
        keys.add(new KeyValue("N", 0x4E));
        keys.add(new KeyValue("O", 0x4F));
        keys.add(new KeyValue("P", 0x50));
        keys.add(new KeyValue("Q", 0x51));
        keys.add(new KeyValue("R", 0x52));
        keys.add(new KeyValue("S", 0x53));
        keys.add(new KeyValue("T", 0x54));
        keys.add(new KeyValue("U", 0x55));
        keys.add(new KeyValue("V", 0x56));
        keys.add(new KeyValue("W", 0x57));
        keys.add(new KeyValue("X", 0x58));
        keys.add(new KeyValue("Y", 0x59));
        keys.add(new KeyValue("Z", 0x5A));
        keys.add(new KeyValue("[", 0x5B));
        keys.add(new KeyValue("\\", 0x5C));

        keys.add(new KeyValue("]", 0x5D));
        keys.add(new KeyValue("*", 0x6A));
        keys.add(new KeyValue("+", 0x6B));
        keys.add(new KeyValue("|", 0x6C));
        keys.add(new KeyValue("-", 0x6D));
        keys.add(new KeyValue(".", 0x6E));
        keys.add(new KeyValue("/", 0x6F));
        keys.add(new KeyValue("DELETE", 0x7F));
        keys.add(new KeyValue("NUM_LOCK", 0x90));
        keys.add(new KeyValue("SCROLL_LOCK", 0x91));
        keys.add(new KeyValue("F1", 0x70));
        keys.add(new KeyValue("F2", 0x71));
        keys.add(new KeyValue("F3", 0x72));
        keys.add(new KeyValue("F4", 0x73));
        keys.add(new KeyValue("F5", 0x74));
        keys.add(new KeyValue("F6", 0x75));
        keys.add(new KeyValue("F7", 0x76));
        keys.add(new KeyValue("F8", 0x77));
        keys.add(new KeyValue("F9", 0x78));
        keys.add(new KeyValue("F10", 0x79));
        keys.add(new KeyValue("F11", 0x7A));
        keys.add(new KeyValue("F12", 0x7B));
        keys.add(new KeyValue("F13", 0xF000));
        keys.add(new KeyValue("F14", 0xF001));
        keys.add(new KeyValue("F15", 0xF002));
        keys.add(new KeyValue("F16", 0xF003));
        keys.add(new KeyValue("F17", 0xF004));
        keys.add(new KeyValue("F18", 0xF005));
        keys.add(new KeyValue("F19", 0xF006));
        keys.add(new KeyValue("F20", 0xF007));
        keys.add(new KeyValue("F21", 0xF008));
        keys.add(new KeyValue("F22", 0xF009));
        keys.add(new KeyValue("F23", 0xF00A));
        keys.add(new KeyValue("F24", 0xF00B));
        keys.add(new KeyValue("PRINTSCREEN", 0x9A));
        keys.add(new KeyValue("INSERT", 0x9B));
        keys.add(new KeyValue("HELP", 0x9C));
        keys.add(new KeyValue("META", 0x9D));
        keys.add(new KeyValue("BACK_QUOTE", 0xC0));
        keys.add(new KeyValue("QUOTE", 0xDE));
        keys.add(new KeyValue("UP", 0xE0));
        keys.add(new KeyValue("DOWN", 0xE1));
        keys.add(new KeyValue("LEFT", 0xE2));
        keys.add(new KeyValue("RIGHT", 0xE3));
        keys.add(new KeyValue("@", 0x0200));
        keys.add(new KeyValue(":", 0x0201));
        keys.add(new KeyValue("^", 0x0202));
        keys.add(new KeyValue("$", 0x0203));
        keys.add(new KeyValue("!", 0x0205));
        keys.add(new KeyValue("(", 0x0207));
        keys.add(new KeyValue("#", 0x0208));
        keys.add(new KeyValue("+", 0x0209));
        keys.add(new KeyValue(")", 0x020A));
        keys.add(new KeyValue("_", 0x020B));
        keys.add(new KeyValue("WINDOWS", 0x020C));
        keys.add(new KeyValue("CONTEXT_MENU", 0x020D));
    }

    @Action
    public void showAboutBox() {
        if (aboutBox == null) {
            JFrame mainFrame = OstazRobotApp.getApplication().getMainFrame();
            aboutBox = new OstazRobotAboutBox(mainFrame);
            aboutBox.setLocationRelativeTo(mainFrame);
        }
        OstazRobotApp.getApplication().show(aboutBox);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        mainPanel = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        List_Actions = new javax.swing.JList();
        Com_Mouse_Actions = new javax.swing.JComboBox();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        Com_KB_Actions = new javax.swing.JComboBox();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        Com_Other_Actions = new javax.swing.JComboBox();
        jButton3 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        txt_X = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        txt_Y = new javax.swing.JTextField();
        jButton5 = new javax.swing.JButton();
        jButton6 = new javax.swing.JButton();
        jButton7 = new javax.swing.JButton();
        jLabel6 = new javax.swing.JLabel();
        COM_Keys = new javax.swing.JComboBox();
        jLabel7 = new javax.swing.JLabel();
        counter = new javax.swing.JSpinner();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jSpinner1 = new javax.swing.JSpinner();
        menuBar = new javax.swing.JMenuBar();
        javax.swing.JMenu fileMenu = new javax.swing.JMenu();
        jMenuItem1 = new javax.swing.JMenuItem();
        jMenuItem2 = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JPopupMenu.Separator();
        javax.swing.JMenuItem exitMenuItem = new javax.swing.JMenuItem();
        javax.swing.JMenu helpMenu = new javax.swing.JMenu();
        javax.swing.JMenuItem aboutMenuItem = new javax.swing.JMenuItem();
        statusPanel = new javax.swing.JPanel();
        javax.swing.JSeparator statusPanelSeparator = new javax.swing.JSeparator();
        statusMessageLabel = new javax.swing.JLabel();
        statusAnimationLabel = new javax.swing.JLabel();
        progressBar = new javax.swing.JProgressBar();

        mainPanel.setMaximumSize(new java.awt.Dimension(453, 297));
        mainPanel.setName("mainPanel"); // NOI18N
        mainPanel.setPreferredSize(new java.awt.Dimension(453, 297));

        jScrollPane1.setName("jScrollPane1"); // NOI18N

        List_Actions.setName("List_Actions"); // NOI18N
        jScrollPane1.setViewportView(List_Actions);

        Com_Mouse_Actions.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Move To", "Left Press", "Left Release", "Right Press", "Right Release" }));
        Com_Mouse_Actions.setName("Com_Mouse_Actions"); // NOI18N
        Com_Mouse_Actions.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                changePanel(evt);
            }
        });

        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(ostazrobot.OstazRobotApp.class).getContext().getResourceMap(OstazRobotView.class);
        jLabel1.setText(resourceMap.getString("jLabel1.text")); // NOI18N
        jLabel1.setName("jLabel1"); // NOI18N

        jLabel2.setText(resourceMap.getString("jLabel2.text")); // NOI18N
        jLabel2.setName("jLabel2"); // NOI18N

        Com_KB_Actions.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "press key", "Release key" }));
        Com_KB_Actions.setName("Com_KB_Actions"); // NOI18N

        javax.swing.ActionMap actionMap = org.jdesktop.application.Application.getInstance(ostazrobot.OstazRobotApp.class).getContext().getActionMap(OstazRobotView.class, this);
        jButton1.setAction(actionMap.get("AddAction")); // NOI18N
        jButton1.setName("jButton1"); // NOI18N

        jButton2.setAction(actionMap.get("AddKBAction")); // NOI18N
        jButton2.setName("jButton2"); // NOI18N

        jLabel3.setText(resourceMap.getString("jLabel3.text")); // NOI18N
        jLabel3.setName("jLabel3"); // NOI18N

        Com_Other_Actions.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Delay" }));
        Com_Other_Actions.setName("Com_Other_Actions"); // NOI18N

        jButton3.setAction(actionMap.get("AddOthers")); // NOI18N
        jButton3.setName("jButton3"); // NOI18N

        jButton4.setAction(actionMap.get("GetMouseLocation")); // NOI18N
        jButton4.setName("jButton4"); // NOI18N

        txt_X.setText(resourceMap.getString("txt_X.text")); // NOI18N
        txt_X.setName("txt_X"); // NOI18N

        jLabel4.setText(resourceMap.getString("jLabel4.text")); // NOI18N
        jLabel4.setName("jLabel4"); // NOI18N

        jLabel5.setText(resourceMap.getString("jLabel5.text")); // NOI18N
        jLabel5.setName("jLabel5"); // NOI18N

        txt_Y.setText(resourceMap.getString("txt_Y.text")); // NOI18N
        txt_Y.setName("txt_Y"); // NOI18N

        jButton5.setAction(actionMap.get("SaveSequence")); // NOI18N
        jButton5.setName("jButton5"); // NOI18N

        jButton6.setAction(actionMap.get("RemoveAction")); // NOI18N
        jButton6.setName("jButton6"); // NOI18N

        jButton7.setAction(actionMap.get("DoActions")); // NOI18N
        jButton7.setName("jButton7"); // NOI18N

        jLabel6.setText(resourceMap.getString("jLabel6.text")); // NOI18N
        jLabel6.setName("jLabel6"); // NOI18N

        COM_Keys.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        COM_Keys.setName("COM_Keys"); // NOI18N

        jLabel7.setText(resourceMap.getString("jLabel7.text")); // NOI18N
        jLabel7.setName("jLabel7"); // NOI18N

        counter.setName("counter"); // NOI18N

        jLabel8.setText(resourceMap.getString("jLabel8.text")); // NOI18N
        jLabel8.setName("jLabel8"); // NOI18N

        jLabel9.setText(resourceMap.getString("jLabel9.text")); // NOI18N
        jLabel9.setName("jLabel9"); // NOI18N

        jSpinner1.setName("jSpinner1"); // NOI18N

        javax.swing.GroupLayout mainPanelLayout = new javax.swing.GroupLayout(mainPanel);
        mainPanel.setLayout(mainPanelLayout);
        mainPanelLayout.setHorizontalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mainPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 360, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, mainPanelLayout.createSequentialGroup()
                            .addComponent(jLabel4)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(txt_X, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(jLabel5)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(txt_Y, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(jButton4)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(jLabel6)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(COM_Keys, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, mainPanelLayout.createSequentialGroup()
                            .addComponent(jButton5)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(jButton6)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(jButton7)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(jLabel7)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(counter, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(jLabel8))
                        .addGroup(mainPanelLayout.createSequentialGroup()
                            .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jLabel2)
                                .addComponent(jLabel1)
                                .addComponent(jLabel3))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(Com_Mouse_Actions, 0, 224, Short.MAX_VALUE)
                                .addComponent(Com_KB_Actions, 0, 224, Short.MAX_VALUE)
                                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, mainPanelLayout.createSequentialGroup()
                                    .addComponent(Com_Other_Actions, 0, 131, Short.MAX_VALUE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                    .addComponent(jLabel9)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                    .addComponent(jSpinner1, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jButton3, javax.swing.GroupLayout.DEFAULT_SIZE, 71, Short.MAX_VALUE)
                                .addComponent(jButton1, javax.swing.GroupLayout.DEFAULT_SIZE, 71, Short.MAX_VALUE)
                                .addComponent(jButton2, javax.swing.GroupLayout.DEFAULT_SIZE, 71, Short.MAX_VALUE)))))
                .addGap(83, 83, 83))
        );
        mainPanelLayout.setVerticalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mainPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(Com_Mouse_Actions, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1)
                    .addComponent(jButton1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(Com_KB_Actions, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(Com_Other_Actions, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton3)
                    .addComponent(jSpinner1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(txt_X, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5)
                    .addComponent(txt_Y, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton4)
                    .addComponent(jLabel6)
                    .addComponent(COM_Keys, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 124, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jButton5)
                        .addComponent(jButton6)
                        .addComponent(jButton7))
                    .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel7)
                        .addComponent(counter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel8)))
                .addContainerGap())
        );

        menuBar.setName("menuBar"); // NOI18N

        fileMenu.setText(resourceMap.getString("fileMenu.text")); // NOI18N
        fileMenu.setName("fileMenu"); // NOI18N

        jMenuItem1.setAction(actionMap.get("OpenSeq")); // NOI18N
        jMenuItem1.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_O, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItem1.setName("jMenuItem1"); // NOI18N
        fileMenu.add(jMenuItem1);

        jMenuItem2.setAction(actionMap.get("SaveSequence")); // NOI18N
        jMenuItem2.setName("jMenuItem2"); // NOI18N
        fileMenu.add(jMenuItem2);

        jSeparator1.setName("jSeparator1"); // NOI18N
        fileMenu.add(jSeparator1);

        exitMenuItem.setAction(actionMap.get("quit")); // NOI18N
        exitMenuItem.setName("exitMenuItem"); // NOI18N
        fileMenu.add(exitMenuItem);

        menuBar.add(fileMenu);

        helpMenu.setText(resourceMap.getString("helpMenu.text")); // NOI18N
        helpMenu.setName("helpMenu"); // NOI18N

        aboutMenuItem.setAction(actionMap.get("showAboutBox")); // NOI18N
        aboutMenuItem.setName("aboutMenuItem"); // NOI18N
        helpMenu.add(aboutMenuItem);

        menuBar.add(helpMenu);

        statusPanel.setName("statusPanel"); // NOI18N

        statusPanelSeparator.setName("statusPanelSeparator"); // NOI18N

        statusMessageLabel.setName("statusMessageLabel"); // NOI18N

        statusAnimationLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        statusAnimationLabel.setName("statusAnimationLabel"); // NOI18N

        progressBar.setName("progressBar"); // NOI18N

        javax.swing.GroupLayout statusPanelLayout = new javax.swing.GroupLayout(statusPanel);
        statusPanel.setLayout(statusPanelLayout);
        statusPanelLayout.setHorizontalGroup(
            statusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(statusPanelSeparator, javax.swing.GroupLayout.DEFAULT_SIZE, 380, Short.MAX_VALUE)
            .addGroup(statusPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(statusMessageLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 210, Short.MAX_VALUE)
                .addComponent(progressBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(statusAnimationLabel)
                .addContainerGap())
        );
        statusPanelLayout.setVerticalGroup(
            statusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(statusPanelLayout.createSequentialGroup()
                .addComponent(statusPanelSeparator, javax.swing.GroupLayout.PREFERRED_SIZE, 2, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(statusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(statusMessageLabel)
                    .addComponent(statusAnimationLabel)
                    .addComponent(progressBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(3, 3, 3))
        );

        setComponent(mainPanel);
        setMenuBar(menuBar);
        setStatusBar(statusPanel);
    }// </editor-fold>//GEN-END:initComponents

    private void changePanel(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_changePanel
    }//GEN-LAST:event_changePanel
    private void DoThisAction(RobotAction robotAction) {
        try {
            Robot myRobot = new Robot();
            if (robotAction.getName().equalsIgnoreCase("Move To")) {
                myRobot.mouseMove(Integer.parseInt(robotAction.getValues().get(0)), Integer.parseInt(robotAction.getValues().get(1)));
            } else if (robotAction.getName().equalsIgnoreCase("Left mouse Press")) {
                myRobot.mousePress(KeyEvent.BUTTON1_MASK);
            } else if (robotAction.getName().equalsIgnoreCase("Left mouse Release")) {
                myRobot.mouseRelease(KeyEvent.BUTTON1_MASK);
            } else if (robotAction.getName().equalsIgnoreCase("Right mouse Press")) {
                myRobot.mousePress(KeyEvent.BUTTON2_MASK);
            } else if (robotAction.getName().equalsIgnoreCase("Right mouse Release")) {
                myRobot.mouseRelease(KeyEvent.BUTTON2_MASK);
            } else if (robotAction.getName().equalsIgnoreCase("Delay")) {
                myRobot.delay(Integer.parseInt(robotAction.getValues().get(0)));
            } else if (robotAction.getName().equalsIgnoreCase("Press Key")) {
                int val = 0;
                for (int i = 0; i < keys.size(); i++) {
                    if (keys.get(i).getKey().equalsIgnoreCase(robotAction.getValues().get(0))) {
                        val = keys.get(i).getValue();
                    }
                }
                myRobot.keyPress(val);
            } else if (robotAction.getName().equalsIgnoreCase("Release Key")) {
                int val = 0;
                for (int i = 0; i < keys.size(); i++) {
                    if (keys.get(i).getKey().equalsIgnoreCase(robotAction.getValues().get(0))) {
                        val = keys.get(i).getValue();
                    }
                }
                myRobot.keyRelease(val);
            }
        } catch (AWTException ex) {
        }

    }

    @Action
    public Task AddAction() {
        return new AddActionTask(getApplication());
    }

    private class AddActionTask extends org.jdesktop.application.Task<Object, Void> {

        AddActionTask(org.jdesktop.application.Application app) {
            // Runs on the EDT.  Copy GUI state that
            // doInBackground() depends on from parameters
            // to AddActionTask fields, here.
            super(app);
        }

        @Override
        protected Object doInBackground() {
            RobotAction action = new RobotAction();
            Vector<String> values = new Vector<String>();
            switch (Com_Mouse_Actions.getSelectedIndex()) {
                case 0:// MOVE TO
                    action.setName("Move To");
                    if (!txt_X.getText().isEmpty() && !txt_Y.getText().isEmpty()) {
                        values.add(txt_X.getText());
                        values.add(txt_Y.getText());
                        action.setValues(values);
                        AddActionToList(action, true);
                    }
                    break;
                case 1:// LEFT PRESS
                    action.setName("Left mouse Press");
                    action.setValues(values);
                    AddActionToList(action, true);
                    break;
                case 2:
                    action.setName("left mouse Release");
                    action.setValues(values);
                    AddActionToList(action, true);
                    break;
                case 3:
                    action.setName("right mouse Press");
                    action.setValues(values);
                    AddActionToList(action, true);
                    break;
                case 4:
                    action.setName("right mouse Press");
                    action.setValues(values);
                    AddActionToList(action, true);
                    break;
            }
            return null;  // return your result
        }

        @Override
        protected void succeeded(Object result) {
            // Runs on the EDT.  Update the GUI based on
            // the result computed by doInBackground().
        }
    }

    void AddActionToList(RobotAction ac, boolean addtovector) {
        StringBuilder actionstring = new StringBuilder();
        if (addtovector) {
            listofactions.add(ac);
        }
        actionstring.append(ac.getName() + " [ ");
        for (String value : ac.getValues()) {
            actionstring.append(value + ",");
        }
        try {
            actionstring.deleteCharAt(actionstring.lastIndexOf(","));
            actionstring.append(" ]");
        } catch (Exception ex) {
            actionstring.deleteCharAt(actionstring.lastIndexOf("["));
        }
        list.addElement(actionstring);
        List_Actions.setModel(list);
    }

    @Action
    public Task RemoveAction() {
        return new RemoveActionTask(getApplication());
    }

    private class RemoveActionTask extends org.jdesktop.application.Task<Object, Void> {

        RemoveActionTask(org.jdesktop.application.Application app) {
            // Runs on the EDT.  Copy GUI state that
            // doInBackground() depends on from parameters
            // to RemoveActionTask fields, here.
            super(app);
        }

        @Override
        protected Object doInBackground() {
            listofactions.remove(List_Actions.getSelectedIndex());
            fillList();
            return null;  // return your result
        }

        @Override
        protected void succeeded(Object result) {
            // Runs on the EDT.  Update the GUI based on
            // the result computed by doInBackground().
        }
    }

    @Action
    public Task AddKBAction() {
        return new AddKBActionTask(getApplication());
    }

    private class AddKBActionTask extends org.jdesktop.application.Task<Object, Void> {

        AddKBActionTask(org.jdesktop.application.Application app) {
            // Runs on the EDT.  Copy GUI state that
            // doInBackground() depends on from parameters
            // to AddKBActionTask fields, here.
            super(app);
        }

        @Override
        protected Object doInBackground() {
            RobotAction action = new RobotAction();
            Vector<String> values = new Vector<String>();
            switch (Com_KB_Actions.getSelectedIndex()) {
                case 0:
                    action.setName("Press Key");
                    values.add(COM_Keys.getSelectedItem().toString());
                    action.setValues(values);
                    AddActionToList(action, true);
                    break;
                case 1:
                    action.setName("Release Key");
                    values.add(COM_Keys.getSelectedItem().toString());
                    action.setValues(values);
                    AddActionToList(action, true);
                    break;
            }
            return null;  // return your result
        }

        @Override
        protected void succeeded(Object result) {
            // Runs on the EDT.  Update the GUI based on
            // the result computed by doInBackground().
        }
    }

    @Action
    public Task AddOthers() {
        return new AddOthersTask(getApplication());
    }

    private class AddOthersTask extends org.jdesktop.application.Task<Object, Void> {

        AddOthersTask(org.jdesktop.application.Application app) {
            // Runs on the EDT.  Copy GUI state that
            // doInBackground() depends on from parameters
            // to AddOthersTask fields, here.
            super(app);
        }

        @Override
        protected Object doInBackground() {
            RobotAction action = new RobotAction();
            Vector<String> values = new Vector<String>();
            switch (Com_Other_Actions.getSelectedIndex()) {
                case 0://Delay
                    String time = jSpinner1.getValue().toString();
                    if (!time.isEmpty()) {
                        action.setName("Delay");
                        values.add(time);
                        action.setValues(values);
                        AddActionToList(action, true);
                    }
                    break;
            }
            return null;  // return your result
        }

        @Override
        protected void succeeded(Object result) {
            // Runs on the EDT.  Update the GUI based on
            // the result computed by doInBackground().
        }
    }

    @Action
    public Task GetMouseLocation() {
        return new GetMouseLocationTask(getApplication());
    }

    private class GetMouseLocationTask extends org.jdesktop.application.Task<Object, Void> {

        GetMouseLocationTask(org.jdesktop.application.Application app) {
            // Runs on the EDT.  Copy GUI state that
            // doInBackground() depends on from parameters
            // to GetMouseLocationTask fields, here.
            super(app);
        }

        @Override
        protected Object doInBackground() {
            txt_X.setText(MouseInfo.getPointerInfo().getLocation().x + "");
            txt_Y.setText(MouseInfo.getPointerInfo().getLocation().y + "");
            return null;  // return your result
        }

        @Override
        protected void succeeded(Object result) {
            // Runs on the EDT.  Update the GUI based on
            // the result computed by doInBackground().
        }
    }

    @Action
    public Task SaveSequence() {
        return new SaveSequenceTask(getApplication());
    }

    private class SaveSequenceTask extends org.jdesktop.application.Task<Object, Void> {

        SaveSequenceTask(org.jdesktop.application.Application app) {
            // Runs on the EDT.  Copy GUI state that
            // doInBackground() depends on from parameters
            // to SaveSequenceTask fields, here.
            super(app);
        }

        @Override
        protected Object doInBackground() {
            try {
                String filename = File.separator + "tmp";
                JFileChooser fc = new JFileChooser(new File(filename));
                fc.showSaveDialog(null);
                File selFile = fc.getSelectedFile();
                ObjectOutput out = new ObjectOutputStream(new FileOutputStream(selFile));
                out.writeObject(listofactions);
            } catch (Exception ex) {
            }
            return null; // return your result
        }

        @Override
        protected void succeeded(Object result) {
            // Runs on the EDT.  Update the GUI based on
            // the result computed by doInBackground().
        }
    }

    @Action
    public Task OpenSeq() {
        return new OpenSeqTask(getApplication());
    }

    private class OpenSeqTask extends org.jdesktop.application.Task<Object, Void> {

        OpenSeqTask(org.jdesktop.application.Application app) {
            // Runs on the EDT.  Copy GUI state that
            // doInBackground() depends on from parameters
            // to OpenSeqTask fields, here.
            super(app);
        }

        @Override
        protected Object doInBackground() {
            try {
                String filename = File.separator + "tmp";
                JFileChooser fc = new JFileChooser(new File(filename));
                fc.showOpenDialog(null);
                File selFile = fc.getSelectedFile();
                ObjectInput out = new ObjectInputStream(new FileInputStream(selFile));
                listofactions = (Vector<RobotAction>) out.readObject();
                fillList();
            } catch (Exception ex) {
                System.out.println("EXX :" + ex.getMessage());
            }
            return null;  // return your result
        }

        @Override
        protected void succeeded(Object result) {
            // Runs on the EDT.  Update the GUI based on
            // the result computed by doInBackground().
        }
    }

    private void fillList() {

        list.removeAllElements();
        for (int i = 0; i < listofactions.size(); i++) {
            AddActionToList(listofactions.get(i), false);
        }
        List_Actions.setModel(list);
    }

    @Action
    public Task DoActions() {
        return new DoActionsTask(getApplication());
    }

    private void DoActionList() {
        int count = Integer.parseInt(counter.getValue().toString());
        for (int k = 0; k < count; k++) {
            for (int i = 0; i < listofactions.size(); i++) {
                DoThisAction(listofactions.get(i));
            }
        }
    }

    private class DoActionsTask extends org.jdesktop.application.Task<Object, Void> {

        DoActionsTask(org.jdesktop.application.Application app) {
            // Runs on the EDT.  Copy GUI state that
            // doInBackground() depends on from parameters
            // to DoActionsTask fields, here.
            super(app);
        }

        @Override
        protected Object doInBackground() {
            for (int i = 0; i < 2; i++) {
                DoActionList();
            }
            return null;  // return your result
        }

        @Override
        protected void succeeded(Object result) {
            // Runs on the EDT.  Update the GUI based on
            // the result computed by doInBackground().
        }
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox COM_Keys;
    private javax.swing.JComboBox Com_KB_Actions;
    private javax.swing.JComboBox Com_Mouse_Actions;
    private javax.swing.JComboBox Com_Other_Actions;
    private javax.swing.JList List_Actions;
    private javax.swing.JSpinner counter;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton7;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JPopupMenu.Separator jSeparator1;
    private javax.swing.JSpinner jSpinner1;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JMenuBar menuBar;
    private javax.swing.JProgressBar progressBar;
    private javax.swing.JLabel statusAnimationLabel;
    private javax.swing.JLabel statusMessageLabel;
    private javax.swing.JPanel statusPanel;
    private javax.swing.JTextField txt_X;
    private javax.swing.JTextField txt_Y;
    // End of variables declaration//GEN-END:variables
    private final Timer messageTimer;
    private final Timer busyIconTimer;
    private final Icon idleIcon;
    private final Icon[] busyIcons = new Icon[15];
    private int busyIconIndex = 0;
    private JDialog aboutBox;
}
