package aau.distributedsystems;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.concurrent.Callable;

public class SlaveTask implements Callable<String> {

    private Socket clientSocket;
    private String clientTask;

    public SlaveTask(Socket clientSocket, String clientTask) {
        this.clientSocket = clientSocket;
        this.clientTask = clientTask;
    }

    @Override
    public String call() throws Exception {
        DataInputStream din = new DataInputStream(clientSocket.getInputStream());
        DataOutputStream dout = new DataOutputStream(clientSocket.getOutputStream());

        String clientMessage = "";
        String messageForClient = clientTask;

        //send a task to the client and wait for its response
        while(clientMessage == ""){
            dout.writeUTF(messageForClient);
            clientMessage=din.readUTF();
            dout.flush();
        }
        din.close();
        clientSocket.close();
        return clientMessage;
    }
}
