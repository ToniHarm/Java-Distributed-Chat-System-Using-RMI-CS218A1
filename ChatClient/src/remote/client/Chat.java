/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package remote.client;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.DefaultListModel;
import javax.swing.GroupLayout;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileSystemView;
import remote.server.InterfaceServer;


public class Chat extends javax.swing.JFrame implements Runnable {
    private ChatClient client;
    private InterfaceServer server;
    private DefaultListModel<String> model = new DefaultListModel<>();
    private DefaultListModel<String> optionModel = new DefaultListModel<>();
    private List<String> listClients = new ArrayList<>();
    private String uname, authorization;
    private String option;
    private GroupLayout groupLayout;

   
    public Chat(String uname, String authorization, InterfaceServer server) {
        initComponents();
        
        
        this.server = server;
        this.uname = uname;
        this.authorization = authorization;
        jPopupMenu1 = new javax.swing.JPopupMenu();
        jMenuItem1 = new javax.swing.JMenuItem("Remove Users");
        jMenuItem2 = new javax.swing.JMenuItem("Hold Users");
        jMenuItem3 = new javax.swing.JMenuItem("Reactivate Users");

        //TO ACCESS THE MENU< RIGHT CLICK ON THE CONNECTED USERS, BUT U MUST BE AN ADMIN
         // Menu Item to Remove Users
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                try {
                    List<String> selectedClients = connectedList.getSelectedValuesList();
                    server.removeClient(selectedClients);
                } catch (RemoteException ex) {
                    JOptionPane.showMessageDialog(null, "Error removing users: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        jPopupMenu1.add(jMenuItem1);

        // Menu Item to Hold Users
        jMenuItem2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                try {
                    List<String> selectedClients = connectedList.getSelectedValuesList();
                    server.holdClient(selectedClients);
                } catch (RemoteException ex) {
                    JOptionPane.showMessageDialog(null, "Error blocking users: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        jPopupMenu1.add(jMenuItem2);

        // Menu Item to Reactivate Users
        jMenuItem3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                try {
                    List<String> selectedClients = connectedList.getSelectedValuesList();
                    server.reactiveClient(selectedClients);
                } catch (RemoteException ex) {
                    JOptionPane.showMessageDialog(null, "Error reactivating users: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        jPopupMenu1.add(jMenuItem3);


        
        //allows only Agent to access the popup menu
        if(authorization.equals("Agent")) {
            System.out.println(authorization);
            connectedList.setComponentPopupMenu(jPopupMenu1);
        }
        
        if(authorization.equals("Student")) {
            optionList.setVisible(false);
            optionsSelected.setVisible(false);
            backBtn.setVisible(false);
        }
        
        this.setLocationRelativeTo(null);
        this.setTitle("Chat (" + uname + ")");
        setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("chat.jpg")));
        
        //For the shared files
        groupLayout = new GroupLayout(panel);
        panel.setLayout(new GridLayout(100,1));

        
        //question the customer before closing the chat, if so we delete it from the customer list
        this.addWindowListener(new java.awt.event.WindowAdapter() {    
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                if (JOptionPane.showConfirmDialog(new JFrame(), 
                    "Are you sure you want to close this chat ?", "Close chat?", 
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION){
                    try {
                        server.removeClient(uname);
                    } catch (RemoteException ex) {
                        System.out.println("Error: " + ex.getMessage());
                    }
                    System.exit(0);
                }else{
                   
                }
            }
        });
        
        
        //a placeholder on the textfield to send message
        inputMsg.setForeground(Color.GRAY);
        inputMsg.setText("Enter your Message ...");
        inputMsg.addFocusListener(new FocusListener() {
        @Override
         public void focusGained(FocusEvent e) {
            if (inputMsg.getText().equals("Enter your Message ...")) {
                inputMsg.setText("");
                inputMsg.setForeground(Color.BLACK);
            }
        }
        @Override
         public void focusLost(FocusEvent e) {
            if (inputMsg.getText().isEmpty()) {
                inputMsg.setForeground(Color.GRAY);
                inputMsg.setText("Enter your Message ...");
            }
        }
        });
        
        listClients = new ArrayList<>();
        model.clear(); // Clear the model
        optionModel.clear(); 
        
        
        for (String client : listClients) {
            model.addElement(client); // Add elements to the model
        }
        connectedList.setModel(model); // Set the model to the list
        
        try{
            client = new ChatClient(uname,server,inputMsg,listMessage,panel);
        } catch (RemoteException ex) {
            System.out.println("Error: " + ex.getMessage());
        }
        
        //timer for every 20s will update the list of connected clients
        Timer minute = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                try {
                    int[] indices = connectedList.getSelectedIndices();
                    model.clear();
                    optionModel.clear(); 
                    
                    listClients = server.getListofClient(uname);
                    
                    for (String clientName : listClients) {
                        String option = server.getClientOption(clientName);
                        String displayText = String.format("%s (%s)", clientName, option); // Format display text
                        model.addElement(clientName); // Add username to model
                        optionModel.addElement(displayText); // Add display text with option to optionModel
                    }
                    connectedList.setModel(model);
                    optionList.setModel(optionModel);
                    connectedList.setSelectedIndices(indices);
                } catch (RemoteException ex) {
                    System.out.println("Error: " + ex.getMessage());
                }
            }
        };
        minute.schedule(task,0,20000);
        
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPopupMenu1 = new javax.swing.JPopupMenu();
        jMenuItem1 = new javax.swing.JMenuItem();
        jMenuItem2 = new javax.swing.JMenuItem();
        jPopupMenu2 = new javax.swing.JPopupMenu();
        jMenuItem3 = new javax.swing.JMenuItem();
        jScrollPane1 = new javax.swing.JScrollPane();
        listMessage = new javax.swing.JTextArea();
        jScrollPane2 = new javax.swing.JScrollPane();
        connectedList = new javax.swing.JList<>();
        optionsSelected = new javax.swing.JLabel();
        inputMsg = new javax.swing.JTextField();
        btnSend = new javax.swing.JButton();
        btnRefresh = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        panel = new java.awt.Panel();
        logout = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        optionList = new javax.swing.JList<>();
        jLabel2 = new javax.swing.JLabel();
        backBtn = new javax.swing.JLabel();

        jMenuItem1.setText("jMenuItem1");

        jMenuItem2.setText("jMenuItem2");

        jMenuItem3.setText("jMenuItem3");

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setBackground(new java.awt.Color(255, 255, 255));
        setFont(new java.awt.Font("Segoe UI Semibold", 1, 24)); // NOI18N

        listMessage.setBackground(new java.awt.Color(255, 255, 255));
        listMessage.setColumns(20);
        listMessage.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        listMessage.setRows(5);
        listMessage.setBorder(null);
        listMessage.setCursor(new java.awt.Cursor(java.awt.Cursor.TEXT_CURSOR));
        jScrollPane1.setViewportView(listMessage);

        connectedList.setBackground(new java.awt.Color(237, 204, 255));
        connectedList.setBorder(null);
        connectedList.setFont(new java.awt.Font("Inter SemiBold", 0, 18)); // NOI18N
        connectedList.setForeground(new java.awt.Color(0, 0, 0));
        connectedList.setModel(new javax.swing.AbstractListModel<String>() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public String getElementAt(int i) { return strings[i]; }
        });
        connectedList.setSelectionBackground(new java.awt.Color(0, 150, 255));
        jScrollPane2.setViewportView(connectedList);

        optionsSelected.setFont(new java.awt.Font("Segoe UI Semibold", 0, 18)); // NOI18N
        optionsSelected.setForeground(new java.awt.Color(0, 0, 0));
        optionsSelected.setText("Options Selected");

        inputMsg.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                inputMsgActionPerformed(evt);
            }
        });

        btnSend.setBackground(new java.awt.Color(0, 71, 171));
        btnSend.setFont(new java.awt.Font("Inter SemiBold", 0, 14)); // NOI18N
        btnSend.setForeground(new java.awt.Color(255, 255, 255));
        btnSend.setText("Send");
        btnSend.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSendActionPerformed(evt);
            }
        });

        btnRefresh.setBackground(new java.awt.Color(0, 150, 255));
        btnRefresh.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N
        btnRefresh.setForeground(new java.awt.Color(255, 255, 255));
        btnRefresh.setText("Refresh");
        btnRefresh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRefreshActionPerformed(evt);
            }
        });

        jButton3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/remote/client/file-upload.png"))); // NOI18N
        jButton3.setBorder(null);
        jButton3.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jButton3.setMaximumSize(new java.awt.Dimension(90, 55));
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelLayout = new javax.swing.GroupLayout(panel);
        panel.setLayout(panelLayout);
        panelLayout.setHorizontalGroup(
            panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        panelLayout.setVerticalGroup(
            panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        logout.setFont(new java.awt.Font("Segoe UI Semibold", 0, 14)); // NOI18N
        logout.setForeground(new java.awt.Color(0, 0, 0));
        logout.setIcon(new javax.swing.ImageIcon(getClass().getResource("/remote/client/logout.png"))); // NOI18N
        logout.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                logoutMouseClicked(evt);
            }
        });

        optionList.setBackground(new java.awt.Color(204, 255, 255));
        optionList.setFont(new java.awt.Font("Segoe UI Semibold", 0, 18)); // NOI18N
        optionList.setForeground(new java.awt.Color(0, 0, 0));
        optionList.setModel(new javax.swing.AbstractListModel<String>() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public String getElementAt(int i) { return strings[i]; }
        });
        jScrollPane3.setViewportView(optionList);

        jLabel2.setFont(new java.awt.Font("Segoe UI Semibold", 0, 18)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(0, 0, 0));
        jLabel2.setText("Online Users");

        backBtn.setFont(new java.awt.Font("Segoe UI Black", 0, 14)); // NOI18N
        backBtn.setText("<- Go Back");
        backBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                backBtnMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2)
                            .addComponent(backBtn))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(logout)
                        .addGap(20, 20, 20))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(jScrollPane2)
                                .addComponent(btnRefresh, javax.swing.GroupLayout.DEFAULT_SIZE, 172, Short.MAX_VALUE)
                                .addComponent(jScrollPane3))
                            .addComponent(optionsSelected))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(inputMsg, javax.swing.GroupLayout.PREFERRED_SIZE, 260, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnSend, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addContainerGap())
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 616, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(20, 20, 20))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(panel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addContainerGap())))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(16, 16, 16)
                        .addComponent(logout, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(backBtn)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel2)
                        .addGap(3, 3, 3)))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 255, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnRefresh, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(optionsSelected)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 196, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 361, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(inputMsg, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(btnSend, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(panel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap(40, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    //action on the "send" button to send the message, check if the message is empty or not before sending it
    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        // TODO add your handling code here:
        JFileChooser jfc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
        int returnValue = jfc.showOpenDialog(null);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File file = jfc.getSelectedFile();
            String[] extension = file.getName().split("\\.");
            System.out.println(extension.length);
            if(extension[extension.length - 1].equals("txt")||
                extension[extension.length - 1].equals("java")||
                extension[extension.length - 1].equals("php")||
                extension[extension.length - 1].equals("c")||
                extension[extension.length - 1].equals("cpp")||
                extension[extension.length - 1].equals("xml")||
                extension[extension.length - 1].equals("exe")||
                extension[extension.length - 1].equals("png")||
                extension[extension.length - 1].equals("jpg")||
                extension[extension.length - 1].equals("jpeg")||
                extension[extension.length - 1].equals("pdf")||
                extension[extension.length - 1].equals("jar")||
                extension[extension.length - 1].equals("rar")||
                extension[extension.length - 1].equals("zip")
            ){
                try {
                    ArrayList<Integer> inc;
                    try (FileInputStream in = new FileInputStream(file)) {
                        inc = new ArrayList<>();
                        int c=0;
                        while((c=in.read()) != -1) {
                            inc.add(c);
                        }
                        in.close();
                    }
                    server.broadcastMessage(inc, listClients,file.getName());
                } catch (FileNotFoundException ex) {
                    System.out.println("Error: " + ex.getMessage());
                } catch (RemoteException ex) {
                    System.out.println("Error: " + ex.getMessage());
                } catch (IOException ex) {
                    System.out.println("Error: " + ex.getMessage());
                }

                JLabel jfile = new JLabel(file.getName() + " Uploaded ...");
                panel.add(jfile);
                panel.repaint();
                panel.revalidate();
            }else{
                JOptionPane.showMessageDialog(this,"You can only upload file have an extension like: xml,exe,jpg,png,jpeg,pdf,c,cpp,jar,java,txt,php ","Alert",JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }//GEN-LAST:event_jButton3ActionPerformed

    private void addMenuItemToPopup(String title, ActionListener actionListener) {
        JMenuItem menuItem = new JMenuItem(title);
        menuItem.addActionListener(actionListener);
        jPopupMenu1.add(menuItem);
    }
    
    private void inputMsgActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_inputMsgActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_inputMsgActionPerformed

    private void btnSendActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSendActionPerformed
        // TODO add your handling code here:
        if(!inputMsg.getText().equals("")){
            if(!inputMsg.getText().equals("Enter you Message ...")){
                client.sendMessage(connectedList.getSelectedValuesList());
                inputMsg.setText("");
            }else{
            JOptionPane.showMessageDialog(this,"Please insert something to set your message","Alert",JOptionPane.WARNING_MESSAGE);
        }
        }else{
            JOptionPane.showMessageDialog(this,"Please insert something to send your message","Alert",JOptionPane.WARNING_MESSAGE);
        }
    }//GEN-LAST:event_btnSendActionPerformed
    //refresh client list
    private void btnRefreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRefreshActionPerformed
        
        Thread thread = new Thread(this);
        thread.start();
    }//GEN-LAST:event_btnRefreshActionPerformed

    private void logoutMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_logoutMouseClicked

        try {
            server.removeClient(uname);
            Login lg = new Login();
            lg.setVisible(true);
            lg.pack();
            lg.setLocationRelativeTo(null);
            lg.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            this.dispose();
        } catch (RemoteException ex) {
            System.out.println("Error: " + ex.getMessage());
        }
      
    }//GEN-LAST:event_logoutMouseClicked

    private void backBtnMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_backBtnMouseClicked
        // TODO add your handling code here:
        Admin_UI admin = new Admin_UI(uname, authorization, server);
        admin.setVisible(true);
        admin.pack();
        admin.setLocationRelativeTo(null);
        admin.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }//GEN-LAST:event_backBtnMouseClicked

    //FUNCTION to implement the glass pane
    void applyBlurEffect() {
         
        // Create a custom glass pane
        JComponent glassPane = new JComponent() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                
                // Set a semi-transparent color for the blur effect
                g.setColor(new Color(0, 0, 0, 150));  // 150 is the alpha (transparency)
                
                // Fill the entire pane with this color
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        
         // Set the glass pane to intercept mouse events (blocking interaction)
        glassPane.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // Prevent any click actions
            }
        });

        // Enable the glass pane
        this.setGlassPane(glassPane);
        glassPane.setVisible(true);
    }
    /**
     * @param args the command line arguments
     */
    public void run() {
        try {
           
            model.clear();
            listClients = server.getListofClient(uname);
            int i=0;
            while(i<listClients.size()){
                model.addElement(listClients.get(i));
                i++;
            }
            connectedList.setModel(model);
        } catch (RemoteException ex) {
            System.out.println("Error: " + ex.getMessage());
        }
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel backBtn;
    private javax.swing.JButton btnRefresh;
    private javax.swing.JButton btnSend;
    private javax.swing.JList<String> connectedList;
    private javax.swing.JTextField inputMsg;
    private javax.swing.JButton jButton3;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JMenuItem jMenuItem3;
    private javax.swing.JPopupMenu jPopupMenu1;
    private javax.swing.JPopupMenu jPopupMenu2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTextArea listMessage;
    private javax.swing.JLabel logout;
    private javax.swing.JList<String> optionList;
    private javax.swing.JLabel optionsSelected;
    private java.awt.Panel panel;
    // End of variables declaration//GEN-END:variables
}
