import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server{
    private ServerSocket serverSocket;
    private Socket clientSocket;
    private ArrayList<UserHandler> connectedUsers = new ArrayList<UserHandler>();
    private int port;

    public Server(int port){
        this.port = port;
    }
    public void startServer(){
        try {
            serverSocket = new ServerSocket(port);
            System.out.println(String.format("Server stared on Port %d.", port));
            while(true){
                // Accept Client Connection, start Thread, Add to List
                clientSocket = serverSocket.accept();
                UserHandler handler = new UserHandler(clientSocket, this);
                connectedUsers.add(handler);
                handler.start();
            }
        }catch (IOException ex){
            // TODO: Handle Exception
            System.out.println(ex.getMessage());
        }
    }

    public void broadcast(String msg, UserHandler except){
        for (UserHandler handler : connectedUsers) {
            // Send Message to all Clients, except sending Client
            if(handler != except){
                handler.broadcast(msg);
            }

        }
    }

    public ArrayList<String> getAllUsers(){
        ArrayList<String> allUsernames = new ArrayList<String>();
        for (UserHandler handler: connectedUsers) {
            allUsernames.add(handler.username);
        }
        return allUsernames;
    }

    public boolean checkUsername(String name){
        // Checks if desired Username is free
        // Looping through all UserHandler
        System.out.println(connectedUsers.toString());
        if(name.isBlank()){
            return false;
        }

        if(connectedUsers.size() < 2){
            // User is alone on Server
            return true;
        }

        for (UserHandler handler: connectedUsers) {
            name = name.toLowerCase();
            if(name.equals(handler.username.toLowerCase())){
                // Name already set to another Client
                return false;
            }
        }
        return true;
    }

    public void closeConnection(UserHandler handlerToClose){
        connectedUsers.remove(handlerToClose);
        handlerToClose.interrupt();
    }
    public static void main(String[] args){
        if(args.length < 1){
            System.out.println("You must specify the Port Number");
            System.out.println("E.g. java Server 1234");
            return;
        }
        try {
            int port = Integer.parseInt(args[0]);
            Server newServer = new Server(4444);
            newServer.startServer();
        } catch (NumberFormatException ex){
            System.out.println("Port Number must be an Integer");
        }


    }
}
