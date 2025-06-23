/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package remote.server;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.net.MalformedURLException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import remote.client.InterfaceClient;



//Chat server is a remote server that can be called by clients
public class ChatServer extends UnicastRemoteObject implements InterfaceServer{
    // Database connection details
    private final String SUrl = "jdbc:MySql://localhost:3306/user_database";
    private final String SUser = "root";
    private final String SPass = "";
    
    //GLobal Variables for Allowed Time. 
    private Date allowedEndTime; 
    private Date allowedStartTime;
    
    private final ArrayList<InterfaceClient> clients; //list contains all clients but which are not blocked
    private final ArrayList<InterfaceClient> holdClients; //list contains all blocked clients
    private Map<String, String> clientOptions = new HashMap<>();
    
   
    public ChatServer() throws RemoteException{
        super(); 
        
        this.clients = new ArrayList<>();
        holdClients = new ArrayList<>();
        
    }
    
    @Override
    public boolean registerUser(String fname, String lname, String uname, String pword, String securityQ, String securityA, String userRole) throws RemoteException {
        String query = "INSERT INTO user (fname, lname, username, password, security_question, security_answer, user_role) " +
                       "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection con = DriverManager.getConnection(SUrl, SUser, SPass);
             PreparedStatement stmt = con.prepareStatement(query)) {
            stmt.setString(1, fname);
            stmt.setString(2, lname);
            stmt.setString(3, uname);
            stmt.setString(4, pword);
            stmt.setString(5, securityQ);
            stmt.setString(6, securityA);
            stmt.setString(7, userRole);
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RemoteException("Error during user registration: " + e.getMessage());
        }
    }
    
    @Override
    public boolean login(String uname, String pword, String userRole) throws RemoteException {
        // SQL query to select all columns from the 'user' table where the username matches the provided one.
        String query = "SELECT * FROM user WHERE username= ?";
        try (Connection con = DriverManager.getConnection(SUrl, SUser, SPass);
            PreparedStatement stmt = con.prepareStatement(query)) {
            // Set the username in the prepared statement
            stmt.setString(1, uname);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String passDb = rs.getString("password");
                String user_auth = rs.getString("user_role");
                return pword.equals(passDb) && userRole.equals(user_auth);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    // Method to store the user query in the database
    @Override
    public void storeUserQuery(String username, String selectedOption) throws RemoteException {
        try {
            Connection con = DriverManager.getConnection(SUrl, SUser, SPass);
            String query = "INSERT INTO user_queries (username, selectedOption) VALUES (?, ?)";
            PreparedStatement stmt = con.prepareStatement(query);
            stmt.setString(1, username);
            stmt.setString(2, selectedOption);
            stmt.executeUpdate();
            con.close();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RemoteException("Error while storing user query: " + e.getMessage());
        }
    }
    
    @Override
    public String getUserRole(String uname) throws RemoteException {
        String query = "SELECT user_role FROM user WHERE username= ?";
        try (Connection con = DriverManager.getConnection(SUrl, SUser, SPass);
             PreparedStatement stmt = con.prepareStatement(query)) {
            stmt.setString(1, uname);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("user_role");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    // Method to get the security question based on the username
    @Override
    public String getSecurityQuestion(String username) throws RemoteException {
      String securityQuestion = "";

      try {
          Class.forName("com.mysql.cj.jdbc.Driver");
          Connection con = DriverManager.getConnection(SUrl, SUser, SPass);
          java.sql.Statement st = con.createStatement();
          ResultSet rs = st.executeQuery("SELECT security_question FROM user WHERE username= '" + username + "'");

          if (rs.next()) {
              securityQuestion = rs.getString(1);
          } else {
              securityQuestion = "Please enter correct Username";
          }
          con.close();
          rs.close();
      } catch (ClassNotFoundException | SQLException e) {
          throw new RemoteException("Error fetching security question: " + e.getMessage(), e);
      }
      return securityQuestion;
    }
    
    // Method to update the password if the security answer is correct
    @Override
    public boolean updatePassword(String username, String newPassword, String securityAnswer) throws RemoteException {
        boolean isUpdated = false;

        try {
            Connection con = DriverManager.getConnection(SUrl, SUser, SPass);
            String query = "SELECT * FROM user WHERE username = ? AND security_answer = ?";
            PreparedStatement stmt = con.prepareStatement(query);
            stmt.setString(1, username);
            stmt.setString(2, securityAnswer);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                // If the security answer is correct, update the password
                String updateQuery = "UPDATE user SET password = ? WHERE username = ? AND security_answer = ?";
                PreparedStatement updateStmt = con.prepareStatement(updateQuery);
                updateStmt.setString(1, newPassword);
                updateStmt.setString(2, username);
                updateStmt.setString(3, securityAnswer);
                updateStmt.executeUpdate();
                isUpdated = true;
            }
            
            con.close();
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RemoteException("Error while updating password: " + e.getMessage());
        }

        return isUpdated;
    }
    
    @Override
    //function to broadcast message to all for student & admin and agent can private message. 
    public synchronized void broadcastMessage(String uname, String message, List<String> recipientList) throws RemoteException {
        boolean sentToSender = false;

        // Get the role of the user
        String senderRole = getRole(uname);

        if ("Student".equals(senderRole)) {
            // Filter out other students from the recipient list
            recipientList.removeIf(recipient -> "Student".equals(getRole(recipient))); // Remove students from the list

            // If no valid recipients (admins/agents) are left, return early and do nothing
            if (recipientList.isEmpty()) {
                System.out.println("Students cannot send messages to other students. Only admins or agents.");
                return;
            }

            // Students can only send to agents or admins (private messages)
            for (InterfaceClient client : clients) {
                if (recipientList.contains(client.getUsername())) {
                    client.retrieveMessage(message); // Send to allowed recipients (admins/agents)

                    // Store the message only if it's going to another user
                    if (!client.getUsername().equals(uname)) {
                        storeMessage(uname, client.getUsername(), message); // Store message for the recipient
                    }

                    // Check if the client is the sender
                    if (client.getUsername().equals(uname)) {
                        sentToSender = true;
                    }
                }
            }

            // Send the message back to the sender if not already sent
            if (!sentToSender) {
                for (InterfaceClient client : clients) {
                    if (client.getUsername().equals(uname)) {
                        client.retrieveMessage(message); // Ensure sender gets their message
                        break;
                    }
                }
            }

        } else {
            // Admins and agents can broadcast or send to selected clients
            if (recipientList.isEmpty()) {
                // Broadcast to all clients
                for (InterfaceClient client : clients) {
                    client.retrieveMessage(message);

                    // Store message only if it's going to another user
                    if (!client.getUsername().equals(uname)) {
                        storeMessage(uname, client.getUsername(), message); // Store message for the recipient
                    }
                }
            } else {
                // Private messaging for admins and agents (send to selected clients)
                for (InterfaceClient client : clients) {
                    if (recipientList.contains(client.getUsername())) {
                        client.retrieveMessage(message); // Send to selected clients

                        // Store the message only if it's going to another user
                        if (!client.getUsername().equals(uname)) {
                            storeMessage(uname, client.getUsername(), message); // Store message for the recipient
                        }

                        // Check if the client is the sender
                        if (client.getUsername().equals(uname)) {
                            sentToSender = true;
                        }
                    }
                }

                // Send to the sender if not already sent
                if (!sentToSender) {
                    for (InterfaceClient client : clients) {
                        if (client.getUsername().equals(uname)) {
                            client.retrieveMessage(message); // Ensure sender gets their message
                            break;
                        }
                    }
                }
            }
        }
    }

    // Method to get the role of a user by username AD CODE
    private String getRole(String uname) {
    
        String user_role = "Student"; // Default role in case the user is not found
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            uname = uname.trim();
            // Establish a database connection
            conn = DriverManager.getConnection(SUrl, SUser, SPass);

            // Prepare the SQL query to get the user's role from the user table
            String sql = "SELECT user_role FROM user WHERE username = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, uname);

            // Execute the query
            rs = pstmt.executeQuery();

            // If a role is found, set it
            if (rs.next()) {
                user_role = rs.getString("user_role"); 
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            // Close the ResultSet, PreparedStatement, and Connection to avoid memory leaks
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return user_role;
  
    }
   
    private void storeMessage(String sender, String receiver, String message) {
        // Get the current system date and time.
        java.util.Date utilDate = new Date();
        java.sql.Timestamp timestamp = new java.sql.Timestamp(utilDate.getTime());

        String sql = "INSERT INTO chat_messages (timeSent, sender, receiver, message) VALUES (?, ?, ?, ?)";

        try (Connection con = DriverManager.getConnection(SUrl, SUser, SPass);
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setTimestamp(1, timestamp);
            stmt.setString(2, sender);
            stmt.setString(3, receiver);
            stmt.setString(4, message);

            // Debug print before executing the update
            System.out.println("Executing SQL: " + stmt);

            stmt.executeUpdate();
            System.out.println("Message stored successfully: " + message);

        } catch (SQLException e) {
            System.err.println("Error storing message: " + e.getMessage());
            e.printStackTrace();
        }
    }


    
      //this function to distribute a file to all connected clients, or a list specified by the client (private discussion)
    @Override
    public synchronized void broadcastMessage(ArrayList<Integer> inc, List<String> list,String filename) throws RemoteException {
        if(list.isEmpty()){
            int i= 0;
            while (i < clients.size()){
                clients.get(i++).retrieveMessage(filename,inc);
            }
        }else{
            // Send only to selected clients
            for (InterfaceClient client : clients) {
                if (list.contains(client.getUsername())) {
                    client.retrieveMessage(filename, inc); // Send to the selected clients
                }
            }  
        }
    }
        
    @Override
    //this function to add a connected client to the list of clients on the server
    public synchronized void addClient(InterfaceClient client) throws RemoteException {
        boolean clientExists =false;
        
        //Check if client is already in the client list
        for (InterfaceClient existingClient : clients) {
            if(existingClient.getUsername().equals(client.getUsername())) {
                clientExists = true;
                break;
            }
        }
        
        if(!clientExists) {
            this.clients.add(client);
        }
    }
    
    //this function to retrieve the name of connected clients
    public synchronized List<String>  getListofClient(String name) throws RemoteException {
        List<String> list = new ArrayList<>();
        for (InterfaceClient client : clients) {
            if(!client.getUsername().equals(name)){
                list.add(client.getUsername());
            }
        }
        return list;
    }
    
    @Override
    public String getClientOption(String username) throws RemoteException {
            String query = "SELECT selectedOption FROM user_queries WHERE username = ?";
       try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/user_database", "root", "");
           PreparedStatement pst = con.prepareStatement(query)) {
           pst.setString(1, username);
           ResultSet rs = pst.executeQuery();
           if (rs.next()) {
               return rs.getString("selectedOption");
           }
       } catch (SQLException e) {
           throw new RemoteException("Error accessing the database", e);
       }
       return "";
    }

    // Method to store the option when a student submits an enquiry
    public void storeEnquiry(String username, String option) throws RemoteException {
        clientOptions.put(username, option);
    }
    
    //Function to load regisered agents from the database
    public  List<String> loadRegisteredAgents(String currentUsername) throws RemoteException {
        List<String> registeredAgentsList = new ArrayList<>();

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection(SUrl, SUser, SPass);// Connect to the database
            java.sql.Statement st = con.createStatement();
            String query = "SELECT username FROM user WHERE user_role = 'Agent'";// Query to select all users with the role of 'Agent'
            ResultSet rs = st.executeQuery(query);

            while (rs.next()) {// Iterate through the result set and add agents to the list
                 String agentUsername = rs.getString("username");
                 if (!agentUsername.equals(currentUsername) && !registeredAgentsList.contains(agentUsername)) {// Ensure agent is not the current user & not already in the list.
                        registeredAgentsList.add(agentUsername); //add agent name to registeredAgentsList arrray list. 
                 }
            }
            // Close the connection
            rs.close();
            st.close();
            con.close();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RemoteException("Error loading agents from database: " + e.getMessage());
        }
        return registeredAgentsList;
    }
    
    //works
    //this function to block a client from sending a message, but it can receive messages
    @Override
    public synchronized void holdClient(List<String> clientsToHold) {
        for (String username : clientsToHold) {
            Iterator<InterfaceClient> iterator = this.clients.iterator();
            while (iterator.hasNext()) {
                InterfaceClient client = iterator.next();
                try {
                    if (client.getUsername().equals(username)) {
                        // Notify the client that they are blocked but should remain visible
                        client.closeChat(username + " are on hold by an agent, we will be right back");

                        // Add to hold clients list
                        holdClients.add(client);

                        // Trigger UI to update the blocked client's status (e.g., greyed out but visible)
                        updateUIForHoldClient(client.getUsername(), true);

                        // Remove from the active clients list
                        iterator.remove();
                        break;
                    }
                } catch (RemoteException ex) {
                    System.out.println("Error: " + ex.getMessage());
                }
            }
        }
    }

   
    //this function to completely delete a list of chat clients (kick-out)
    @Override
    public synchronized void removeClient(List<String> clientUsernames) {
        Iterator<InterfaceClient> iterator = this.clients.iterator();
        while (iterator.hasNext()) {
            InterfaceClient client = iterator.next();
            try {
                if (clientUsernames.contains(client.getUsername())) {
                    client.closeChat(client.getUsername() + " have been logged out of the chat");
                    iterator.remove(); // Safe removal
                }
            } catch (RemoteException ex) {
                System.out.println("Error: " + ex.getMessage());
            }
        }
    }
    
    //removes a single client
    @Override
    public synchronized void removeClient(String clientUsername) {
        Iterator<InterfaceClient> iterator = this.clients.iterator();
        while (iterator.hasNext()) {
            InterfaceClient client = iterator.next();
            try {
                if (client.getUsername().equals(clientUsername)) {
                    client.closeChat(clientUsername + " have been logged out of the chat");
                    iterator.remove(); // Safe removal
                    break; // Exit loop after removal
                }
            } catch (RemoteException ex) {
                System.out.println("Error: " + ex.getMessage());
            }
        }
    }
    
    public void RemoveClient(List<String> clientNames) throws RemoteException {
        
        try (Connection connection = DriverManager.getConnection(SUrl, SUser, SPass)) { // Establish connection
            for (String clientName : clientNames) {
                try {
                    // Prepare the query to delete the agent with specific username and role
                    String query = "DELETE FROM user WHERE user_role = 'agent' AND username = ?";
                    PreparedStatement stmt = connection.prepareStatement(query);
                    stmt.setString(1, clientName);  // Set the username (client name)

                    int rowsAffected = stmt.executeUpdate();  // Execute the query

                    if (rowsAffected == 0) {
                        System.out.println("No agent found with username: " + clientName);
                    } else {
                        System.out.println("Successfully removed agent: " + clientName);
                    }

                    stmt.close();  // Close the statement after execution

                } catch (SQLException e) {
                    e.printStackTrace();
                    throw new RemoteException("Error removing agent from database", e);
                }
            } 
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RemoteException("Error connecting to the database", e);
        }
    }
    
    //reactivate client 
    @Override
    public synchronized void reactiveClient(List<String> clientsToReactivate) throws RemoteException {
        Iterator<InterfaceClient> iterator = this.holdClients.iterator();
        while (iterator.hasNext()) {
            InterfaceClient holdClient = iterator.next();
            try {
                if (clientsToReactivate.contains(holdClient.getUsername())) {

                    // Check if the client already exists in the active clients list
                    boolean clientExists = false;
                    for (InterfaceClient existingClient : clients) {
                        if (existingClient.getUsername().equals(holdClient.getUsername())) {
                            clientExists = true;
                            break;
                        }
                    }

                    // Add the client back to the active clients list only if not already present
                    if (!clientExists) {
                        this.clients.add(holdClient);
                    }

                    // Reopen the chat for the reactivated client
                    holdClient.openChat("Agent is back");

                    // Update the UI to show them as active (not greyed out)
                    updateUIForHoldClient(holdClient.getUsername(), false);

                    // Remove them from the blocked clients list
                    iterator.remove();
                }
            } catch (RemoteException ex) {
                System.out.println("Error: " + ex.getMessage());
            }
        }
    }

    
    //this function to check whether a username already exists in the server or not, because username is the identifier on chat
    @Override
    public boolean checkUsername(String username) throws RemoteException {
        boolean exist = false;
        for(int i=0;i<clients.size();i++){
            if(clients.get(i).getUsername().equals(username)){
                exist = true;
            }
        }
        for(int i=0;i<holdClients.size();i++){
            if(holdClients.get(i).getUsername().equals(username)){
                exist = true;
            }
        }
        return exist;
    }
    
    public static void main(String[] args) throws MalformedURLException {
        try {
            Registry registry = LocateRegistry.createRegistry(200);
            ChatServer server = new ChatServer();
            registry.rebind("ChatServer", server);
            System.out.println("Server ready");
            
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void updateUIForHoldClient(String username, boolean b) {
        throw new UnsupportedOperationException("Not supported yet."); 
    }
    
    //Method to store the allowed chat times set by the Admin in the Admin_UI.
    public void setAllowedChatTimes(Date start, Date end) throws RemoteException {
        this.allowedStartTime = start;
        this.allowedEndTime = end;
        
        //Debugging statements to check if it works --yeah brutha im dying
        System.out.println("Allowed chat times updated: " + start + " to " + end);
    }
    
    //helper function.. if no time range is set, the chat will not be allowed. 
    public boolean isChatAllowed(String username) {
        Date currentTime = Calendar.getInstance().getTime();
        
          // Debug statement to print the current time and the allowed start/end times
            System.out.println("Current time: " + currentTime);
            System.out.println("Allowed start time: " + allowedStartTime);
            System.out.println("Allowed end time: " + allowedEndTime);
        
        if (allowedStartTime != null && allowedEndTime != null) {
            return currentTime.after(allowedStartTime) && currentTime.before(allowedEndTime);
        }
        
         System.out.println("Chat not allowed by default (no time range set).");
        return false; // Chat is not allowed if no time range is set...
             
    }

}
