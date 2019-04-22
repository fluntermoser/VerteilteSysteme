package aau.distributedsystems.master;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.concurrent.Callable;

public class SlaveExerciseTask implements Callable<String> {

    private static String Exercise = "Exercise";
    private static String Result = "Result";
    private String exercise;
    private ClientSocketWrapper clientSocket;

    public SlaveExerciseTask(ClientSocketWrapper clientSocket, String exercise) {
        this.clientSocket = clientSocket;
        this.exercise = exercise;
    }

    @Override
    public String call() throws Exception {
        ObjectInputStream din = clientSocket.getInputStream();
        ObjectOutputStream dout = clientSocket.getOutputStream();

        //send the exercise to the client
        byte[] data = exercise.getBytes();
        dout.writeObject(new Message(MessageType.EXERCISE, data.length, data));
        dout.flush();
        Message clientMessage = null;

        //wait for the result of the client
        while(clientMessage == null || !clientMessage.getMessageType().equals(MessageType.RESULT)){
            clientMessage = (Message) din.readObject();
        }

        System.out.println("Client " + clientSocket.getSocketIdentifier() + " sent result...");

        return new String(clientMessage.getData());
    }
}
