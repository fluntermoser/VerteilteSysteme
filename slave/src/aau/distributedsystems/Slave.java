package aau.distributedsystems;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.Socket;

public class Slave {

    public static void main(String[] args) {
        if(args.length < 2) {
            System.out.println("the number of arguments does not match the required amount");
            System.out.println("params are: server-address, server-port");
            return;
        }

        int port = Integer.parseInt(args[1]);
        String serverAddress = args[0];
        try {
            Socket s = new Socket(serverAddress,port);
            DataInputStream din = new DataInputStream(s.getInputStream());
            DataOutputStream dout = new DataOutputStream(s.getOutputStream());
            System.out.println("Connected to server...");
            String message="Hi server";
            String serverInput="";
            while(serverInput.equals("")){
                dout.writeUTF(message);
                dout.flush();
                serverInput = din.readUTF();
                System.out.println("Server says: "+serverInput);
            }

            dout.close();
            s.close();
        } catch(Exception e) {
            System.out.println(e);
        }
    }
}
