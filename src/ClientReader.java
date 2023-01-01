import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class ClientReader extends Thread{

    private Socket clientSocket;
    private BufferedReader dataIn;
    private String message;
    private Client client;
    private boolean usernameSet = false;
    public ClientReader(Socket socket, Client client){
        this.clientSocket = socket;
        this.client = client;
    }

    @Override
    public void run(){
        try {
            dataIn = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            while ((message = dataIn.readLine()) != null){
                // TO differentiate user and System message
                String[] splitMessage = message.split(" ", 2);
                switch (splitMessage[0]){
                    case "/SYSTEMMESSAGE":
                        if(splitMessage[1].equals("username set")){
                            usernameSet = true;
                            continue;
                        } else if (splitMessage[1].equals("set username")) {
                            System.out.println("Set Username: ");
                        }
                        break;


                }
                if(usernameSet){
                    System.out.println(message);
                }

            }
            client.active = false;


            System.out.println("ClientReader Shutdown");
            System.out.println("Press Enter to exit..");

        } catch (IOException ex){
            //TODO: Handle Exception
            System.out.println(ex.getMessage());
        }
    }
}
