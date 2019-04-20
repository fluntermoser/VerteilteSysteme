package aau.distributedsystems;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.concurrent.Callable;

public class SlaveExerciseTask implements Callable<String> {

    private static String Exercise = "Exercise";
    private static String Result = "Result";
    private static String Stop = "Stop";
    private String exercise;
    private Socket clientSocket;
    private int clientNumber;

    public SlaveExerciseTask(Socket clientSocket, String exercise, Integer clientNumber) {
        this.clientSocket = clientSocket;
        this.exercise = exercise;
        this.clientNumber = clientNumber;
    }

    @Override
    public String call() throws Exception {
        DataInputStream din = new DataInputStream(clientSocket.getInputStream());
        DataOutputStream dout = new DataOutputStream(clientSocket.getOutputStream());

        //send the exercise to the client
        dout.writeUTF(Exercise + ": " + exercise);
        dout.flush();
        String clientMessage = "";

        //wait for the result of the client
        while(!clientMessage.contains(Result)){
            clientMessage=din.readUTF();
        }

        System.out.println("Client " + clientNumber + " sent result...");

        //tell slave to shut down
        dout.writeUTF(Stop);
        dout.flush();

        dout.close();
        din.close();
        clientSocket.close();
        return clientMessage;
    }
}
