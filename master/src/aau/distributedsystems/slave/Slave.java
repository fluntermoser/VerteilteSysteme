package aau.distributedsystems.slave;


import aau.distributedsystems.shared.*;

import java.io.*;
import java.net.Socket;

public class Slave {

    public static void main(String[] args) {
        if (args.length < 3) {
            System.out.println("the number of arguments does not match the required amount");
            System.out.println("params are: server-address, server-port, unique slave id");
            return;
        }

        int port = Integer.parseInt(args[1]);
        String serverAddress = args[0];
        int id = Integer.parseInt(args[2]);
        try {
            Socket s = new Socket(serverAddress, port);
            ObjectInputStream oin = new ObjectInputStream(s.getInputStream());
            ObjectOutputStream oous = new ObjectOutputStream(s.getOutputStream());
            System.out.println("Connected to server...");
            System.out.println("Sending initialize...");
            oous.writeObject(new Message(MessageType.INITIALIZE, id));

            System.out.println("Waiting for Task...");

            Message serverInput = null;
            while (serverInput == null || !serverInput.getMessageType().equals(MessageType.SHUTDOWN)) {
                serverInput = (Message) oin.readObject();

                //check if we have an input and if we received any type of exercise
                if (serverInput != null && (serverInput.getMessageType().equals(MessageType.EXERCISE_MULTI)
                || serverInput.getMessageType().equals(MessageType.EXERCISE_ADD))) {

                    MatrixBlockTuple exercise = (MatrixBlockTuple) MessageSerializer.objectFromByteArray(serverInput.getData());
                    if (exercise != null) {
                        System.out.println("Received exercise: " + serverInput.getData());
                        int[][] result;

                        //if the type is EXERCISE_MULTI the slave has to perform a multiplication
                        if(serverInput.getMessageType().equals(MessageType.EXERCISE_MULTI)) {
                            System.out.println("Performing multiplication...");
                            result = MatrixUtil.multiply(exercise.getBlockA().getBlock(), exercise.getBlockB().getBlock());
                        }
                        //otherwise the slave has to perform an addition
                        else {
                            System.out.println("Performing addition...");
                            result = MatrixUtil.add(exercise.getBlockA().getBlock(), exercise.getBlockB().getBlock());
                        }

                        ResultTuple rt = new ResultTuple(result, exercise.getRp());
                        byte[] data = MessageSerializer.objectToByteArray(rt);
                        oous.writeObject(new Message(MessageType.RESULT, data.length, data));
                        oous.flush();
                    }
                }
            }

            System.out.println("Shutting down...");
            oous.close();
            s.close();
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
