package aau.distributedsystems;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.AbstractMap;
import java.util.concurrent.Callable;

public class SlaveInitTask implements Callable<AbstractMap.SimpleEntry<Integer, Socket>> {

    private static String Initialize = "Initialize";

    private Socket clientSocket;
    private int clientNumber;

    public SlaveInitTask(Socket clientSocket, int clientNumber) {
        this.clientSocket = clientSocket;
        this.clientNumber = clientNumber;
    }

    @Override
    public AbstractMap.SimpleEntry<Integer, Socket> call() throws Exception {
        DataInputStream din = new DataInputStream(clientSocket.getInputStream());
        String clientMessage = "";

        //waiting for the clients initialize message
        while(!clientMessage.equals(Initialize)){
            clientMessage=din.readUTF();
        }
        System.out.println("Client " + clientNumber + " initialized...");
        return new AbstractMap.SimpleEntry<>(clientNumber, clientSocket);
    }
}
