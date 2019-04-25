package aau.distributedsystems.master;

import aau.distributedsystems.shared.Message;
import aau.distributedsystems.shared.MessageType;

import java.io.ObjectInputStream;
import java.util.concurrent.Callable;

public class SlaveInitTask implements Callable<ClientSocketWrapper> {

    private static String Initialize = "Initialize";

    private ClientSocketWrapper clientSocket;

    public SlaveInitTask(ClientSocketWrapper clientSocket) {
        this.clientSocket = clientSocket;
    }

    @Override
    public ClientSocketWrapper call() throws Exception {
        ObjectInputStream din = clientSocket.getInputStream();
        Message clientMessage = null;

        //waiting for the clients initialize message
        while(clientMessage == null || !clientMessage.getMessageType().equals(MessageType.INITIALIZE)){
            clientMessage = (Message)din.readObject();
        }
        System.out.println("Client " + clientSocket.getSocketIdentifier() + " initialized...");
        return clientSocket;
    }
}
