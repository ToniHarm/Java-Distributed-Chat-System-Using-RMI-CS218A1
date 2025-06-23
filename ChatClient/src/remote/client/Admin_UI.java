
package remote.client;

import javax.swing.*;
import java.rmi.RemoteException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JMenuItem;
import javax.swing.DefaultListModel;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JSpinner;
import javax.swing.SpinnerDateModel;
import remote.server.InterfaceServer;

public class Admin_UI extends javax.swing.JFrame {
    
    //Global Variables to store time. 
    private Date allowedStartTime;
    private Date allowedEndTime;

    private final String uname, authorization;
    private final InterfaceServer server;

    public Admin_UI(String uname, String authorization, InterfaceServer server) {   
        
        initComponents();
        jList1.setModel(new DefaultListModel<>());
        
        this.server = server;
        this.uname = uname;
        this.authorization = authorization;
        
        // Load the registered agents immediately after initialization
        SwingUtilities.invokeLater(() -> {
            btnRefreshActionPerformed(null);
        });
        
        //----------------------------------------------------------------------------------------------------------> right click agents code starts here...
        
        JPopupMenu jPopupMenu1 = new javax.swing.JPopupMenu();//Create the popup menu and menu items
        
        JMenuItem jMenuItem1 = new javax.swing.JMenuItem("Remove / De-Register Agent");
        JMenuItem jMenuItem2 = new javax.swing.JMenuItem("Block Agent");
        JMenuItem jMenuItem3 = new javax.swing.JMenuItem("Reactivate Agent"); 
        
        jPopupMenu1.add(jMenuItem1);// Add the menu item to the popup menu
        
        //...De-Register (Remove) Agent functionality........
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                
                List<String> selectedAgent = jList1.getSelectedValuesList(); // Get selected agent from the list
                
                if (selectedAgent.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "No agents selected!", "Warning", JOptionPane.WARNING_MESSAGE);
                
                } else {
                    
                    try {
                        // Call server to remove the selected agent
                        server.RemoveClient(selectedAgent);
                    } catch (RemoteException ex) {
                        Logger.getLogger(Admin_UI.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    
                    JOptionPane.showMessageDialog(null, "Selected agents de-registered successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                    
                    btnRefreshActionPerformed(null); // Refresh the agent list after removal
                }
            }
        });
        
        // Menu Item to Block Agents
        jMenuItem2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                try {
                    List<String> selectedClients = jList1.getSelectedValuesList();
                    server.holdClient(selectedClients);
                } catch (RemoteException ex) {
                    JOptionPane.showMessageDialog(null, "Error holding users: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        jPopupMenu1.add(jMenuItem2);
        
        
        
        // Menu Item to Reactivate Users
        jMenuItem3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                try {
                    List<String> selectedClients = jList1.getSelectedValuesList();
                    server.reactiveClient(selectedClients);
                } catch (RemoteException ex) {
                    JOptionPane.showMessageDialog(null, "Error reactivating users: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        jPopupMenu1.add(jMenuItem3); 
    
    // Add a MouseListener to the JList for the popup menu
    jList1.addMouseListener(new java.awt.event.MouseAdapter() {
        public void mousePressed(java.awt.event.MouseEvent evt) {
            if (evt.isPopupTrigger()) {
                showPopup(evt);
            }
        }

        public void mouseReleased(java.awt.event.MouseEvent evt) {
            if (evt.isPopupTrigger()) {
                showPopup(evt);
            }
        }

        private void showPopup(java.awt.event.MouseEvent evt) {
            int index = jList1.locationToIndex(evt.getPoint());
            if (index >= 0) {
                jList1.setSelectedIndex(index); // Select the item at the right-click position
                jPopupMenu1.show(evt.getComponent(), evt.getX(), evt.getY()); // Show the popup menu at the click location
            }
        }
    });
        
// -------------------------------------------------------------------------------------------------------------
        //Model for Start Spinner
        SpinnerDateModel startModel = new SpinnerDateModel(new Date(), null, null, java.util.Calendar.HOUR_OF_DAY);
        startSpinner.setModel(startModel);
        JSpinner.DateEditor startEditor = new JSpinner.DateEditor(startSpinner, "HH:mm");
        startSpinner.setEditor(startEditor);
           
        // Get the current date and time
        Date currentDate = new Date();
        //Model for end Spinner
        SpinnerDateModel endModel = new SpinnerDateModel(new Date(), null, null, java.util.Calendar.HOUR_OF_DAY);
        endSpinner.setModel(endModel);
        JSpinner.DateEditor endEditor = new JSpinner.DateEditor(endSpinner, "HH:mm");
        endSpinner.setEditor(endEditor);
        
        // Set the endSpinner value to 1 hour later than the current time
        endSpinner.setValue(new Date(System.currentTimeMillis() + 3600 * 1000)); // Sets to 1 hour later
             
//---------------------------------------------------------------------------------------------------------------------
        
    }

//    public Admin_UI() {
//        throw new UnsupportedOperationException("Not supported yet."); 
//    }
    
   
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jList1 = new javax.swing.JList<>();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        btnRefresh = new javax.swing.JButton();
        startSpinner = new javax.swing.JSpinner();
        jLabel3 = new javax.swing.JLabel();
        applyButton = new javax.swing.JButton();
        endSpinner = new javax.swing.JSpinner();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        gotoChat = new javax.swing.JButton();

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jList1.setModel(new javax.swing.AbstractListModel<String>() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public String getElementAt(int i) { return strings[i]; }
        });
        jScrollPane1.setViewportView(jList1);

        jLabel1.setFont(new java.awt.Font("Segoe UI Semibold", 1, 14)); // NOI18N
        jLabel1.setText("Registered Agents");

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel2.setText("Administration Menu");

        btnRefresh.setBackground(new java.awt.Color(0, 102, 255));
        btnRefresh.setFont(new java.awt.Font("Segoe UI Semibold", 0, 12)); // NOI18N
        btnRefresh.setForeground(new java.awt.Color(255, 255, 255));
        btnRefresh.setText("Refresh");
        btnRefresh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRefreshActionPerformed(evt);
            }
        });

        startSpinner.setName("startSpinner"); // NOI18N

        jLabel3.setFont(new java.awt.Font("Segoe UI Semibold", 1, 14)); // NOI18N
        jLabel3.setText("Time Set-Up");

        applyButton.setBackground(new java.awt.Color(51, 51, 51));
        applyButton.setFont(new java.awt.Font("Segoe UI Semibold", 0, 12)); // NOI18N
        applyButton.setForeground(new java.awt.Color(255, 255, 255));
        applyButton.setText("Apply");
        applyButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                applyButtonActionPerformed(evt);
            }
        });

        endSpinner.setName("endSpinner"); // NOI18N

        jLabel4.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel4.setText("Start Time");

        jLabel5.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel5.setText("End Time");

        gotoChat.setBackground(new java.awt.Color(20, 69, 98));
        gotoChat.setFont(new java.awt.Font("Segoe UI Semibold", 0, 12)); // NOI18N
        gotoChat.setForeground(new java.awt.Color(255, 255, 255));
        gotoChat.setText("Go to Chat...");
        gotoChat.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                gotoChatActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(23, 23, 23)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                .addComponent(jLabel1)
                                .addGap(0, 156, Short.MAX_VALUE))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addComponent(btnRefresh, javax.swing.GroupLayout.PREFERRED_SIZE, 114, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel3)
                                .addGap(48, 48, 48))
                            .addGroup(layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                            .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                            .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(endSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(startSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addComponent(applyButton, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(gotoChat, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                                .addGap(23, 23, 23))))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(26, 26, 26)
                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel4)
                            .addComponent(startSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(9, 9, 9)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(endSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel5))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(applyButton, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(gotoChat, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnRefresh, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(37, Short.MAX_VALUE))
        );

        startSpinner.getAccessibleContext().setAccessibleName("startSpinner");
        applyButton.getAccessibleContext().setAccessibleName("applyButton");
        endSpinner.getAccessibleContext().setAccessibleName("endSpinner");

        pack();
    }// </editor-fold>//GEN-END:initComponents

    //button logic to set up the time for chatbox to open
    private void applyButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_applyButtonActionPerformed
        
        // Retrieve the selected start and end times from the spinners
        allowedStartTime = (Date) startSpinner.getValue();
        allowedEndTime = (Date) endSpinner.getValue();

        // Format the times to display them as "HH:mm" (hours:minutes)
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
        String formattedStartTime = timeFormat.format(allowedStartTime);
        String formattedEndTime = timeFormat.format(allowedEndTime);
        
        // Display the selected time range using a dialog box
        JOptionPane.showMessageDialog(this, "Chat allowed from " + formattedStartTime + " to " + formattedEndTime);
        
        try {
            // Notify the server of the new time restrictions
            server.setAllowedChatTimes(allowedStartTime, allowedEndTime);
        } catch (RemoteException ex) {
            Logger.getLogger(Admin_UI.class.getName()).log(Level.SEVERE, null, ex);
        }
       
    }//GEN-LAST:event_applyButtonActionPerformed
    
    //Refresh Button to update the Registered Agents from the server [WORKS!!]
    private void btnRefreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRefreshActionPerformed
        new Thread(() -> {
            try {
                List<String> agents = server.loadRegisteredAgents(uname); // Fetching registered agents only
               
                // Safely update the UI on the Event Dispatch Thread. 
                SwingUtilities.invokeLater(() ->{
                    DefaultListModel<String> model = (DefaultListModel<String>) jList1.getModel(); 
                    model.clear(); // Clear the existing list
                    for (String agent : agents) {
                        model.addElement(agent); // Add each registered agent to the model
                    }
                });
            }catch (RemoteException ex) {
                Logger.getLogger(Admin_UI.class.getName()).log(Level.SEVERE, null, ex);
            }       
         }).start();
    }//GEN-LAST:event_btnRefreshActionPerformed
    
    //Function to take the Admin to the Chat Interface.....
    private void gotoChatActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_gotoChatActionPerformed
        Chat chatUI = new Chat(uname, authorization, server); // Pass the current username (admin) and server instance
        chatUI.setVisible(true);// Show the Chat interface
        this.setVisible(false);// Hide/close the current Admin UI.
    }//GEN-LAST:event_gotoChatActionPerformed

    /**
     * @param args the command line arguments
     */
    
    public void run() {
       //new Admin_UI().setVisible(true); // Set the UI visible first       
    }
        
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton applyButton;
    private javax.swing.JButton btnRefresh;
    private javax.swing.JSpinner endSpinner;
    private javax.swing.JButton gotoChat;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JList<String> jList1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSpinner startSpinner;
    // End of variables declaration//GEN-END:variables

   
}
