package aau.distributedsystems.master;

import aau.distributedsystems.shared.*;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.concurrent.Callable;

public class SlaveExerciseTask implements Callable<ResultTuple> {

    private MatrixBlockTuple exercise;
    private ClientSocketWrapper clientSocket;
    private MessageType exerciseType;

    public SlaveExerciseTask(ClientSocketWrapper clientSocket, MatrixBlockTuple exercise, MessageType exerciseType) {
        this.clientSocket = clientSocket;
        this.exercise = exercise;
        this.exerciseType = exerciseType;
    }

    @Override
    public ResultTuple call() throws Exception {
        ObjectInputStream din = clientSocket.getInputStream();
        ObjectOutputStream dout = clientSocket.getOutputStream();

        //send the exercise to the client
        byte[] data = MessageSerializer.objectToByteArray(exercise);
        dout.writeObject(new Message(exerciseType, data.length, data));
        dout.flush();
        Message clientMessage = null;

        //wait for the result of the client
        while(clientMessage == null || !clientMessage.getMessageType().equals(MessageType.RESULT)){
            clientMessage = (Message) din.readObject();
        }

        System.out.println("Client " + clientSocket.getSocketIdentifier() + " sent result...");

        return (ResultTuple) MessageSerializer.objectFromByteArray(clientMessage.getData());
    }
}
