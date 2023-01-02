import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class ClientWriter extends Thread{
    private Socket clientSocket;
    private PrintWriter dataOut;
    private Scanner scanner;
    private Client client;
    public ClientWriter(Socket socket, Client client){
        this.clientSocket = socket;
        this.client = client;
    }
    @Override
    public void run(){
        try {
            dataOut = new PrintWriter(clientSocket.getOutputStream(), true);
            scanner = new Scanner(System.in);
            String msgToSend;
            while(client.active){
                msgToSend = scanner.nextLine();
                if(msgToSend.equals("")){
                    continue;
                }
                dataOut.println(msgToSend);
            }
            // When client.active gets False -> writer stucks in scanner.nextLine()
            // So Reader sout's press Enter to exit, to trigger the last Input and then exit the Loop

        } catch (IOException ex){
            //TODO: Handle Exception
            System.out.println(ex.getMessage());
        }
    }
}
