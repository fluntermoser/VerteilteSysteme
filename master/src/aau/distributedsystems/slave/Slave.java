package aau.distributedsystems.slave;


import aau.distributedsystems.master.Message;
import aau.distributedsystems.master.MessageType;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class Slave {

    public static void main(String[] args) {
        /*if(args.length < 2) {
            System.out.println("the number of arguments does not match the required amount");
            System.out.println("params are: server-address, server-port");
            return;
        }*/

        int port = 6666;// Integer.parseInt(args[1]);
        String serverAddress = "localhost";// args[0];
        try {
            Socket s = new Socket(serverAddress,port);
            ObjectInputStream oin = new ObjectInputStream(s.getInputStream());
            ObjectOutputStream oous = new ObjectOutputStream(s.getOutputStream());
            System.out.println("Connected to server...");
            System.out.println("Sending initialize...");
            oous.writeObject(new Message(MessageType.INITIALIZE, (int) Math.random()));

            System.out.println("Waiting for Task...");

            Message serverInput = null;
            while(serverInput == null || !serverInput.getMessageType().equals(MessageType.SHUTDOWN)){
                serverInput = (Message) oin.readObject();
                if(serverInput != null && serverInput.getMessageType().equals(MessageType.EXERCISE)) {
                    System.out.println("Received exercise: " + serverInput.getData());
                    //pretend to be working
                    Thread.sleep(1000);
                    oous.writeObject(new Message(MessageType.RESULT, serverInput.getData().length, serverInput.getData()));
                    oous.flush();
                }
            }

            System.out.println("Shutting down...");
            oous.close();
            s.close();
        } catch(Exception e) {
            System.out.println(e);
        }
    }
}
