/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package remote.server;

import java.rmi.Remote;
import remote.client.InterfaceClient;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public interface InterfaceServer extends Remote {
    
    boolean registerUser(String fname, String lname, String uname, String pword, String securityQ, String securityA, String userRole) throws RemoteException;
    
    boolean login(String uname, String pword, String userRole) throws RemoteException;
    
    void storeUserQuery(String username, String selectedOption) throws RemoteException;
    
    public String getSecurityQuestion(String username) throws RemoteException;
    
    public boolean updatePassword(String username, String newPassword, String securityAnswer) throws RemoteException;
    
    public String getUserRole(String uname) throws RemoteException;
    
    void addClient(InterfaceClient client) throws RemoteException;
    
    List<String> getListofClient(String name) throws RemoteException;
    
    boolean checkUsername(String username) throws RemoteException;
    
    //this function to distribute a message to all connected clients
    void broadcastMessage(String uname,String message,List<String> list) throws RemoteException;
    
    //this function to distribute a shared file to all connected clients
    void broadcastMessage(ArrayList<Integer> inc,List<String> list,String filename) throws RemoteException;
    
    //this function to put client on hold from sending a message, but it can receive messages
    void holdClient(List<String> clients) throws RemoteException;
    
    //this function to completely delete a list of chat clients (kick-out)
    void removeClient(List<String> clients) throws RemoteException;
    
    //this function to completely delete a single chat client (kick-out)
    void removeClient(String clients) throws RemoteException;
    
     // New overridden method to remove multiple clients from the database--Specifically for Admin_UI.
    public void RemoveClient(List<String> clientNames) throws RemoteException;
    
    //this function to activate a client in chat, after being in the case of "block"
    void reactiveClient(List<String> clients) throws RemoteException;
    
    //Method to store the allowed chat times set by the Admin in the Admin_UI.
    public void setAllowedChatTimes(Date start, Date end)throws RemoteException;
    
    //function to load registered agents from the database when Admin refreshes from Admin_UI
    public List<String> loadRegisteredAgents(String uname)throws RemoteException;
    
    public boolean isChatAllowed(String uname) throws RemoteException;
    
    String getClientOption(String username) throws RemoteException;

    void storeEnquiry(String username, String option) throws RemoteException;
    
    
}
