package remote.client;

import java.awt.Cursor;
import java.awt.Panel;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import remote.server.InterfaceServer;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class ChatClient extends UnicastRemoteObject implements InterfaceClient{
    private final InterfaceServer server;
    private final String uname;
    private final JTextField input;
    private final JTextArea output;
    private final Panel panel;
    

    public ChatClient(String uname, InterfaceServer server, JTextField inputMsg, JTextArea chatbox, Panel panel) throws RemoteException {
        super();
        this.uname = uname;
        this.server = server;
        this.input = inputMsg;
        this.output = chatbox;
        this.panel = panel;
        server.addClient(this);
    }
    
    //this function to retrieve chat messages from server
    @Override
    public void retrieveMessage(String message) throws RemoteException {
        output.setText(output.getText() + "\n" + message);
    }

    
    //this function to retrieve shared discussion files from server
    @Override
    public void retrieveMessage(String filename, ArrayList<Integer> inc) throws RemoteException {
        JLabel label = new JLabel("<HTML><U><font size=\"4\" color=\"#365899\">" + filename + "</font></U></HTML>");
        label.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        label.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                try {
                    FileOutputStream out;
                    String separator;
                    if (System.getProperty("os.name").startsWith("Linux") || System.getProperty("os.name").startsWith("MacOS")) separator = "/";
                    else separator = "\\";
                    out = new FileOutputStream(System.getProperty("user.home") + separator + filename);
                    String[] extension = filename.split("\\.");
                    for (int i = 0; i < inc.size(); i++) {
                        int cc = inc.get(i);
                        if (extension[extension.length - 1].equals("txt") ||
                            extension[extension.length - 1].equals("java") ||
                            extension[extension.length - 1].equals("php") ||
                            extension[extension.length - 1].equals("c") ||
                            extension[extension.length - 1].equals("cpp") ||
                            extension[extension.length - 1].equals("xml")) {
                            out.write((char) cc);
                        } else {
                            out.write((byte) cc);
                        }
                    }
                    out.flush();
                    out.close();
                    JOptionPane.showMessageDialog(new JFrame(), "your file saved at " + System.getProperty("user.home") + separator + filename, "File Saved", JOptionPane.INFORMATION_MESSAGE);

                    // Write the filename to a CSV file
                    try (FileWriter writer = new FileWriter("chat_files.csv", true)) {
                        writer.append(uname); // log the client's name
                        writer.append(",");
                        writer.append(filename.replace(",", " ")); // replace commas to prevent breaking CSV format
                        writer.append("\n");
                    } catch (IOException ex) {
                        System.out.println("Error writing filename to CSV: " + ex.getMessage());
                    }

                } catch (FileNotFoundException ex) {
                    System.out.println("Error: " + ex.getMessage());
                } catch (IOException ex) {
                    System.out.println("Error: " + ex.getMessage());
                }
            }
            @Override
            public void mousePressed(MouseEvent e) {}

            @Override
            public void mouseReleased(MouseEvent e) {}

            @Override
            public void mouseEntered(MouseEvent e) {}

            @Override
            public void mouseExited(MouseEvent e) {}
        });
        panel.add(label);
        panel.repaint();
        panel.revalidate();
    }


    //this function to send a message to the server
    @Override
    public void sendMessage(List<String> list) {
        try {
            server.broadcastMessage(uname, uname + " : " + input.getText() + "\n",list );
        } catch (RemoteException ex) {
            System.out.println("Error: " + ex.getMessage());
        }
    }
    
    
    //this function to retrieve the name of a connected client
    @Override
    public String getUsername() {
        return uname;
    }
    
    @Override
    public void closeChat(String message) throws RemoteException {
        SwingUtilities.invokeLater(() -> {
        input.setEditable(false);
        input.setEnabled(false);
        JOptionPane.showMessageDialog(new JFrame(), message, "Alert", JOptionPane.WARNING_MESSAGE);
        });
    }
    
    //this function to enable a client to send a message
    @Override
    public void openChat(String message) throws RemoteException {
        input.setEditable(true);
        input.setEnabled(true);    
    }

}
