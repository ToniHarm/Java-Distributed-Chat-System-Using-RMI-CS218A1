/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package remote.client;

import java.rmi.RemoteException;
import javax.swing.JOptionPane;
import remote.server.InterfaceServer;

/**
 *
 * @author tonih
 */
public class Menu extends javax.swing.JFrame {
    private InterfaceServer server;
    private String username, authorization;

    /**
     * Creates new form Menu
     */
    public Menu(String username,String authorization, InterfaceServer server) {
        initComponents();
        this.server = server;
        this.username = username;
        this.authorization = authorization;
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jFrame1 = new javax.swing.JFrame();
        jLabel1 = new javax.swing.JLabel();
        option = new javax.swing.JComboBox<>();
        jLabel3 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();

        javax.swing.GroupLayout jFrame1Layout = new javax.swing.GroupLayout(jFrame1.getContentPane());
        jFrame1.getContentPane().setLayout(jFrame1Layout);
        jFrame1Layout.setHorizontalGroup(
            jFrame1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        jFrame1Layout.setVerticalGroup(
            jFrame1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(0, 0, 0));
        jLabel1.setText("Menu");

        option.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Fees", "Exams", "Enrollment" }));

        jLabel3.setText("Enquiry Category:");

        jButton1.setBackground(new java.awt.Color(0, 102, 255));
        jButton1.setText("Send");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(95, 95, 95)
                        .addComponent(jLabel1))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(27, 27, 27)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(option, javax.swing.GroupLayout.PREFERRED_SIZE, 217, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel3)
                            .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 219, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(31, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(42, 42, 42)
                .addComponent(jLabel1)
                .addGap(18, 18, 18)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(option, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(33, 33, 33)
                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(59, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        String opt = (String) option.getSelectedItem();

        try {
            // Store the selected option in the database via the server
            server.storeUserQuery(username, opt);

            JOptionPane.showMessageDialog(this, "Enquiry submitted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);

            // Navigate to the Chat window
            Chat chat = new Chat(username, authorization, server);
            chat.setVisible(true);
            this.dispose();

            // Perform RMI call to check if the user is allowed to chat (only for non-admins)
            boolean isAllowedToChat = server.isChatAllowed(username); // Call the server method

            // Apply the blur effect and show the error if not allowed to chat
            if (!isAllowedToChat) {
                chat.applyBlurEffect();  // Apply blur effect

                // Show the chat is currently closed message after the blur effect
                JOptionPane.showMessageDialog(null, 
                    "<html>"
                        + "<body style='text-align: center; font-family: Arial, sans-serif; color: #333;'>"
                            + "<b><span style='font-size: 16px; color: #E74C3C;'>Sorry, We're Closed.</span></b><br><br>"
                            + "Please send an email with your request to <u style='color: #3498DB;'>contactus@usp.ac.fj</u><br>"
                            + "and we will get back to you as soon as possible.<br><br>"
                            + "<b><span style='font-size: 12px; color: #2C3E50;'>Business Hours (Pacific/Fiji):</span></b><br>"
                            + "<span style='font-size: 10px;'>Monday - Friday: 08:00 - 20:00</span><br>" 
                            + "<span style='font-size: 10px;'>Saturday - Sunday: 09:00 - 18:00</span>"
                        + "</body></html>", 
                    "Notice", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
        } catch (RemoteException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error submitting enquiry: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_jButton1ActionPerformed

    /**
     * @param args the command line arguments
     */    public static void main(String args[]) {
      
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JFrame jFrame1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JComboBox<String> option;
    // End of variables declaration//GEN-END:variables
}
