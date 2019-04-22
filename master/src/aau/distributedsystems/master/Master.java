package aau.distributedsystems.master;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Master {

    public static void main(String[] args) {
        /*if(args.length < 3) {
            System.out.println("the number of arguments does not match the required amount");
            System.out.println("params are: port, max-slave-number, waiting-timeout");
            return;
        }*/

        int port = 6666; //Integer.parseInt(args[0]);
        int maxSlaves = 2; //Integer.parseInt(args[1]);
        int timeout = 30;// Integer.parseInt(args[2]);

        try {
            ServerSocket serverSocket = new ServerSocket(port);
            System.out.println("Wating for connections...");
            ExecutorService executor = Executors.newCachedThreadPool();

            List<Future<ClientSocketWrapper>> initFutures = new ArrayList<>();

            int numberOfSlaves = 0;
            //wait for all slaves to connect and create initialization Task for each one
            serverSocket.setSoTimeout(timeout*1000);
            try{
                while(numberOfSlaves < maxSlaves) {
                    ClientSocketWrapper clientSocket = new ClientSocketWrapper(serverSocket.accept(), numberOfSlaves + 1, executor);
                    numberOfSlaves++;
                    System.out.println(numberOfSlaves + " slave(s) connected...");
                    initFutures.add(executor.submit(new SlaveInitTask(clientSocket)));
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
            ArrayList<ClientSocketWrapper> availableSlaves = new ArrayList<>();
            for (Future<ClientSocketWrapper> initTask: initFutures) {
                availableSlaves.add(initTask.get());
            }

            System.out.println("All connected slaves initialized...");

            //dummy exercises for slaves
            List<String> exercises = new ArrayList<>();
            exercises.add("ex1"); exercises.add("ex2"); exercises.add("ex3");
            exercises.add("ex4"); exercises.add("ex5"); exercises.add("ex6");
            exercises.add("ex7"); exercises.add("ex8"); exercises.add("ex9");


            Iterator slaveIterator;
            List<ClientSocketWrapper> workingSlaves = new ArrayList<>();
            List<String> results = new ArrayList<>();

            //iterate through exercises and distribute them to the available slaves
            for (String exercise: exercises) {
                //if we ran out of available slaves, collect the calculated results
                if(availableSlaves.size() == 0){
                    collectResults(workingSlaves, availableSlaves, results);
                }

                //iterate through available slaves and distribute a task to the next available slave
                slaveIterator = availableSlaves.iterator();
                ClientSocketWrapper availableSlave = (ClientSocketWrapper) slaveIterator.next();
                availableSlave.work(exercise);
                workingSlaves.add(availableSlave);
                availableSlaves.remove(availableSlave);
            }
            collectResults(workingSlaves, availableSlaves, results);
            shutDownSlaves(availableSlaves);

            System.out.println(results);
            serverSocket.close();
            System.out.println("All results have been collected...");
            return;
        } catch(Exception e) {
            System.out.println(e);
        }
    }

    private static void collectResults(List<ClientSocketWrapper> workingSlaves, List<ClientSocketWrapper> availableSlaves, List<String> results) {
        Iterator workingSlavesIterator = workingSlaves.iterator();
        while(workingSlavesIterator.hasNext()) {
            ClientSocketWrapper workingSlave = (ClientSocketWrapper) workingSlavesIterator.next();
            String result = workingSlave.getResult();
            if(result != null)
                results.add(result);
            availableSlaves.add(workingSlave);
        }
        workingSlaves.removeAll(availableSlaves);
    }

    private static void shutDownSlaves(List<ClientSocketWrapper> slaves) {
        for(ClientSocketWrapper slave: slaves)  {
            try {
                slave.shutdown();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
