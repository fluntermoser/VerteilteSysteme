package aau.distributedsystems;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class Master {

    public static void main(String[] args) {
        if(args.length < 3) {
            System.out.println("the number of arguments does not match the required amount");
            System.out.println("params are: port, max-slave-number, waiting-timeout");
            return;
        }
        int port = Integer.parseInt(args[0]);
        int maxSlaves = Integer.parseInt(args[1]);
        int timeout = Integer.parseInt(args[2]);

        try {
            ServerSocket ss=new ServerSocket(port);
            System.out.println("Wating for connections...");
            Socket s = ss.accept();
            DataInputStream din = new DataInputStream(s.getInputStream());
            DataOutputStream dout = new DataOutputStream(s.getOutputStream());


            String clientMessage="";
            String messageForClient="Hi client";
            while(clientMessage == ""){
                clientMessage=din.readUTF();
                System.out.println("client says: "+clientMessage);
                dout.writeUTF(messageForClient);
                dout.flush();
            }
            din.close();
            s.close();
            ss.close();
        } catch(Exception e) {
            System.out.println(e);
        }
    }

}
