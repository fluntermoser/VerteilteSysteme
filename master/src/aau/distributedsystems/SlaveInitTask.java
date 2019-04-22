package aau.distributedsystems;

import java.io.DataInputStream;
import java.util.concurrent.Callable;

public class SlaveInitTask implements Callable<Slave> {

    private static String Initialize = "Initialize";

    private Slave clientSocket;

    public SlaveInitTask(Slave clientSocket) {
        this.clientSocket = clientSocket;
    }

    @Override
    public Slave call() throws Exception {
        DataInputStream din = clientSocket.getInputStream();
        String clientMessage = "";

        //waiting for the clients initialize message
        while(!clientMessage.equals(Initialize)){
            clientMessage=din.readUTF();
        }
        System.out.println("Client " + clientSocket.getSocketIdentifier() + " initialized...");
        return clientSocket;
    }
}
