import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class UserHandler extends Thread{
    private Socket clientSocket;
    private PrintWriter dataOut; // Sends Data to other Sockets
    private BufferedReader dataIn; // Recieves Data from other Sockets
    public String username = "";
    private String message;
    private Server server;
    public UserHandler(Socket clientSocket, Server server){
        this.clientSocket = clientSocket;
        this.server = server;
    }

    // Override Thread Run
    @Override
    public void run(){
        try {
            dataOut = new PrintWriter(clientSocket.getOutputStream(), true);
            dataIn = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            boolean checkUsernameResult = false;
            String possibleUserName;
            do{
                // Check with Server if Username is available
                systemMessage("set username");
                possibleUserName = dataIn.readLine();
                if(possibleUserName == null) continue;
                checkUsernameResult = server.checkUsername(possibleUserName);
            }while (!checkUsernameResult);
            // Username is available, set Username
            username = possibleUserName;
            systemMessage("username set");
            // Message to Client, Username successful set
            dataOut.println(String.format("Successful set Username: %s", username));
            // Message to all Clients, new User out there
            server.broadcast(String.format("New user %s connected", username), this);
            // Info Message
            dataOut.println("-> '/username newUserName' to change username ");
            dataOut.println("-> '/exit' to exit ");

            // Loop for Reading and Writing
            while ((message = dataIn.readLine()) != null){
                // Split Message to allow Commands like "/username newUsername"
                String[] command = message.split(" ", 2);

                switch(command[0]){
                    case "/exit":
                        closeConnection();
                        break;
                    case "/username":
                        setUsername(command[1]);
                        break;
                    default:
                        // No Command Send
                        String broadMessage = String.format("[%s]: %s ", username, message);
                        server.broadcast(broadMessage, this);
                }

            }
            System.out.println("Userhandler shutdown");
        }catch (IOException ex){
            // TODO: Handle Exception
            System.out.println(ex.getMessage());
        }
    }

    private void systemMessage(String msg){
        // Formats message to a System message
        // ClientReader can differentiate between user and system message
        dataOut.println(String.format("/SYSTEMMESSAGE %s", msg));
    }

    private void setUsername(String name){
        if(server.checkUsername(name)){
            server.broadcast(String.format("%s changed Username to: %s", username, name), this);
            username = name;
            dataOut.println(String.format("Username changed to: %s", username));
        }else {
            dataOut.println("Username not available");
        }
    }

    private void closeConnection(){
        try {
            server.broadcast(String.format("User %s hast left the Room", username), this);
            clientSocket.close();
            server.closeConnection(this);
        }catch (IOException ex){
            //TODO: Handle Exception
        }

    }
    public void broadcast(String msg){
        dataOut.println(msg);
    }
}
