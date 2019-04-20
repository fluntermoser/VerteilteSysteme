package aau.distributedsystems;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

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
        int numberOfSlaves = 0;
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            System.out.println("Wating for connections...");
            ExecutorService executor = Executors.newCachedThreadPool();
            List<Future<String>> futures = new ArrayList<>();
            while(numberOfSlaves < maxSlaves) {
                Socket clientSocket = serverSocket.accept();
                numberOfSlaves++;
                System.out.println(numberOfSlaves + " slave(s) connected...");
                futures.add(executor.submit(new SlaveTask(clientSocket, "Task " + numberOfSlaves)));
            }
            for (Future<String> future: futures) {
                System.out.println(future.get());
            }

            serverSocket.close();
            System.out.println("All results have been collected...");
        } catch(Exception e) {
            System.out.println(e);
        }
    }

}
