package aau.distributedsystems;

import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.*;
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

        try {
            ServerSocket serverSocket = new ServerSocket(port);
            System.out.println("Wating for connections...");
            ExecutorService executor = Executors.newCachedThreadPool();

            List<Future<AbstractMap.SimpleEntry<Integer, Socket>>> initFutures = new ArrayList<>();

            int numberOfSlaves = 0;
            //wait for all slaves to connect and create initialization Task for each one
            serverSocket.setSoTimeout(timeout*1000);
            try{
                while(numberOfSlaves < maxSlaves) {
                    Socket clientSocket = serverSocket.accept();
                    numberOfSlaves++;
                    System.out.println(numberOfSlaves + " slave(s) connected...");
                    initFutures.add(executor.submit(new SlaveInitTask(clientSocket, numberOfSlaves)));
                }
            } catch(SocketTimeoutException ste) {
                System.out.println("Connection timeout expired...");
                if(numberOfSlaves < 1) {
                    System.out.println("NO slaves connected within the defined timeout...");
                    serverSocket.close();
                    return;
                }
            }


            //wait for each slave to send its initialize message
            //putting all slave-sockets in a map to remember which slave is which
            HashMap<Integer, Socket> slaves = new HashMap<>();
            for (Future<AbstractMap.SimpleEntry<Integer, Socket>> future: initFutures) {
                AbstractMap.SimpleEntry<Integer, Socket> result = future.get();
                slaves.put(result.getKey(), result.getValue());
            }

            System.out.println("All connected slaves initialized...");

            //send exercises to all initialized slaves
            List<Future<String>> exerciseFutures = new ArrayList<>();
            for (Map.Entry<Integer, Socket> client: slaves.entrySet()) {
                exerciseFutures.add(executor.submit(new SlaveExerciseTask(client.getValue(), "ex: " + Math.random(), client.getKey())));
            }

            //wait for all slaves to finish their tasks and print results
            for(Future<String> exerciseFuture: exerciseFutures) {
                System.out.println(exerciseFuture.get());
            }

            serverSocket.close();
            System.out.println("All results have been collected...");
        } catch(Exception e) {
            System.out.println(e);
        }
    }

}
