/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package remote.client;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

//ChatServer
public interface InterfaceClient extends Remote{
    
    //this function to retrieve chat messages from server
    void retrieveMessage(String message) throws RemoteException;
    
    //this function to retrieve shared discussion files from server
    void retrieveMessage(String filename,ArrayList<Integer> inc) throws RemoteException;
    
    //this function to send a message from a client to the server
    void sendMessage(List<String> list) throws RemoteException;
    
    //this function to retrieve the name of connected clients (client identifier) ​​==> username
    String getUsername()throws RemoteException;
    
    //this function to deactivate the functionality of sending a message to a client
    void closeChat(String message) throws RemoteException;
    
    //this function to enable a client to send a message
    void openChat(String message) throws RemoteException;


}
