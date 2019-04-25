package aau.distributedsystems.master;

import aau.distributedsystems.shared.MatrixBlockTuple;
import aau.distributedsystems.shared.Message;
import aau.distributedsystems.shared.MessageSerializer;
import aau.distributedsystems.shared.MessageType;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.concurrent.Callable;

public class SlaveExerciseTask implements Callable<int[][]> {

    private MatrixBlockTuple exercise;
    private ClientSocketWrapper clientSocket;

    public SlaveExerciseTask(ClientSocketWrapper clientSocket, MatrixBlockTuple exercise) {
        this.clientSocket = clientSocket;
        this.exercise = exercise;
    }

    @Override
    public int[][] call() throws Exception {
        ObjectInputStream din = clientSocket.getInputStream();
        ObjectOutputStream dout = clientSocket.getOutputStream();

        //send the exercise to the client
        byte[] data = MessageSerializer.objectToByteArray(exercise);
        dout.writeObject(new Message(MessageType.EXERCISE, data.length, data));
        dout.flush();
        Message clientMessage = null;

        //wait for the result of the client
        while(clientMessage == null || !clientMessage.getMessageType().equals(MessageType.RESULT)){
            clientMessage = (Message) din.readObject();
        }

        System.out.println("Client " + clientSocket.getSocketIdentifier() + " sent result...");

        return (int[][]) MessageSerializer.objectFromByteArray(clientMessage.getData());
    }
}
