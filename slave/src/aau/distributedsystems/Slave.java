package aau.distributedsystems;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

public class Slave {

    private static String Initialize = "Initialize";
    private static String Exercise = "Exercise";
    private static String Result = "Result";
    private static String Stop = "Stop";

    public static void main(String[] args) {
        if(args.length < 2) {
            System.out.println("the number of arguments does not match the required amount");
            System.out.println("params are: server-address, server-port");
            return;
        }

        int port = 6666;// Integer.parseInt(args[1]);
        String serverAddress = "localhost";// args[0];
        try {
            Socket s = new Socket(serverAddress,port);
            DataInputStream din = new DataInputStream(s.getInputStream());
            DataOutputStream dout = new DataOutputStream(s.getOutputStream());
            System.out.println("Connected to server...");
            System.out.println("Sending initialize...");
            dout.writeUTF(Initialize);

            System.out.println("Waiting for Task...");

            String serverInput="";
            while(!serverInput.equals(Stop)){
                serverInput = din.readUTF();
                if(serverInput.contains(Exercise)) {
                    System.out.println("Received exercise: " + serverInput);
                    //pretend to be working
                    Thread.sleep(1000);
                    dout.writeUTF(Result + ": " + serverInput);
                    dout.flush();
                }
            }

            System.out.println("Shutting down...");
            dout.close();
            s.close();
        } catch(Exception e) {
            System.out.println(e);
        }
    }
}
