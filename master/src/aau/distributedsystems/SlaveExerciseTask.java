package aau.distributedsystems;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.concurrent.Callable;

public class SlaveExerciseTask implements Callable<String> {

    private static String Exercise = "Exercise";
    private static String Result = "Result";
    private String exercise;
    private Slave clientSocket;

    public SlaveExerciseTask(Slave clientSocket, String exercise) {
        this.clientSocket = clientSocket;
        this.exercise = exercise;
    }

    @Override
    public String call() throws Exception {
        DataInputStream din = clientSocket.getInputStream();
        DataOutputStream dout = clientSocket.getOutputStream();

        //send the exercise to the client
        dout.writeUTF(Exercise + ": " + exercise);
        dout.flush();
        String clientMessage = "";

        //wait for the result of the client
        while(!clientMessage.contains(Result)){
            clientMessage=din.readUTF();
        }

        System.out.println("Client " + clientSocket.getSocketIdentifier() + " sent result...");

        return clientMessage;
    }
}
