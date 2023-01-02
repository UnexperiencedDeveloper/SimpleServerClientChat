import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    public boolean active = true; // when client Disconnects it gets false;
    private Socket clientSocket;
    private final int port;
    public Client(int port){
        this.port = port;
    }

    public void connectToServer(){
        try {
            clientSocket = new Socket("localhost", port);
            ClientReader reader = new ClientReader(clientSocket, this);
            ClientWriter writer = new ClientWriter(clientSocket, this);
            reader.start();
            writer.start();
        }catch (IOException ex){
            //TODO: Handle Exception
            System.out.println(ex.getMessage());
        }

    }






    public static void main(String[] args){

        if(args.length < 1){
            System.out.println("You must specify the Port Number");
            System.out.println("E.g. java Client 1234");
            return;
        }
        try {
            int port = Integer.parseInt(args[0]);
            Client client = new Client(port);
            client.connectToServer();
        } catch (NumberFormatException ex){
            System.out.println("Port Number must be an Integer");
        }

    }
}
