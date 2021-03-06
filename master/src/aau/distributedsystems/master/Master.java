package aau.distributedsystems.master;

import aau.distributedsystems.shared.*;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

public class Master {

    public static void main(String[] args) {
        if(args.length < 3) {
            System.out.println("the number of arguments does not match the required amount");
            System.out.println("params are: port, max-slave-number, waiting-timeout, slave-failure-timeout (optional)");
            return;
        }

        //parse the command line parameters
        int port = Integer.parseInt(args[0]);
        int maxSlaves = Integer.parseInt(args[1]);
        int connectionTimeout = Integer.parseInt(args[2]);
        int slaveFailureTimeout;
        try {
            slaveFailureTimeout = Integer.parseInt(args[3]);
        } catch (Exception e) {
            slaveFailureTimeout = 30;
        }

        long time = System.currentTimeMillis();
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            System.out.println("Wating for connections...");
            ExecutorService executor = Executors.newCachedThreadPool();

            List<Future<ClientSocketWrapper>> initFutures = new ArrayList<>();

            int numberOfSlaves = 0;
            //wait for all slaves to connect and create initialization Task for each one
            serverSocket.setSoTimeout(connectionTimeout*1000);
            try{
                //accept incoming connections as long as the maxSlave number is not reached
                //OR as long as the timeout is not expired
                while(numberOfSlaves < maxSlaves) {
                    //create a new clientSocket wrapper to remember the slave
                    ClientSocketWrapper clientSocket = new ClientSocketWrapper(serverSocket.accept(), executor);
                    numberOfSlaves++;
                    System.out.println(numberOfSlaves + " slave(s) connected...");

                    //start a task where we wait for the slave to send its init message
                    initFutures.add(executor.submit(new SlaveInitTask(clientSocket)));
                }
            } catch(SocketTimeoutException ste) {
                //after the timout expired we either go with the slaves we got or shutdown if no slaves connected
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
                try {
                    availableSlaves.add(initTask.get());
                } catch(Exception e) {
                    System.out.println("Unable to get 'init' from slave...");
                }
            }

            //if none of the connected slaves managed to send a initialize message shutdown
            if(availableSlaves.size() == 0) {
                System.out.println("None of the connected slaves was able to initialize...");
                serverSocket.close();
                return;
            }

            System.out.println("All connected slaves initialized...");

            int r = 4;
            int q = 100;
            int s = 100;
            int[][] a = MatrixUtil.generateMatrix(r, s);
            int[][] b = MatrixUtil.generateMatrix(q, r);

            //the result matrix has q * s fields from [0][0] to [q][s]
            //the idea here is to create a task for each field, that can then be solved by a slave
            int[][] resultMatrix = new int[q][s];

            List<ResultTuple> results = new ArrayList<>();

            //get multiplication tasks
            List<MatrixBlockTuple> multiplicationTasks = getMultiplicationTasks(a, b);

            //iterate through exercises and distribute them to the available slaves
            //first perform all multiplications
            distributeTasksAndCollectResults(multiplicationTasks, availableSlaves, results, slaveFailureTimeout, MessageType.EXERCISE_MULTI);

            //get needed additions from the calculated results
            List<MatrixBlockTuple> additionTasks = getAdditionTasks(results);
            results = new ArrayList<>();

            //let the slaves perform additions
            distributeTasksAndCollectResults(additionTasks, availableSlaves, results, slaveFailureTimeout, MessageType.EXERCISE_ADD);

            //the slaves may shutdown now
            shutDownSlaves(availableSlaves);

            //last we have to combine the results to one matrix again
            MatrixUtil.combineBlocks(results, resultMatrix);
            time = (System.currentTimeMillis() - time);
            MatrixUtil.printMatrix(resultMatrix);

            executor.shutdown();
            serverSocket.close();
            
            System.out.println("All results have been collected...");
            System.out.println("Execution time: " + time);
            System.in.read();
        } catch(Exception e) {
            e.printStackTrace();
        }
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

    private static void distributeTasksAndCollectResults(List<MatrixBlockTuple> tasks, List<ClientSocketWrapper> availableSlaves,
                                                         List<ResultTuple> results, int slaveFailureTimeout, MessageType exerciseType) {
        List<ClientSocketWrapper> workingSlaves = new ArrayList<>();
        Iterator slaveIterator;
        //iterate through exercises and distribute them to the available slaves
        while(tasks.size() > 0) {
            MatrixBlockTuple exercise = tasks.get(0);
            tasks.remove(exercise);

            //get the next available slave
            slaveIterator = availableSlaves.iterator();
            ClientSocketWrapper availableSlave = (ClientSocketWrapper) slaveIterator.next();

            //distribute some work to the slave
            availableSlave.work(exercise, exerciseType);

            //remove slave from available slaves and add it to the working slaves
            workingSlaves.add(availableSlave);
            availableSlaves.remove(availableSlave);

            //if we ran out of available slaves, collect the calculated results from the working slaves
            if(availableSlaves.size() == 0 || tasks.size() == 0){
                collectResults(workingSlaves, availableSlaves, results, tasks, slaveFailureTimeout);
            }
        }
    }

    private static void collectResults(List<ClientSocketWrapper> workingSlaves,
                                       List<ClientSocketWrapper> availableSlaves,
                                       List<ResultTuple> results,
                                       List<MatrixBlockTuple> notDoneTasks,
                                       int slaveFailureTimeout) {

        //iterate through working slaves and also exile the slaves that where not able to deliver a result
        Iterator workingSlavesIterator = workingSlaves.iterator();
        List<ClientSocketWrapper> failedSlaves = new ArrayList<>();
        while(workingSlavesIterator.hasNext()) {
            ClientSocketWrapper workingSlave = (ClientSocketWrapper) workingSlavesIterator.next();
            ResultTuple result;
            try {
                result = workingSlave.getResult(slaveFailureTimeout);
                if(result != null) {
                    results.add(result);
                } else {
                    notDoneTasks.add(workingSlave.getCurrentTask());
                }
                availableSlaves.add(workingSlave);
            } catch (Exception e) {
                System.out.println("Slave " + workingSlave.getSocketIdentifier() + " not able to deliver result;");
                notDoneTasks.add(workingSlave.getCurrentTask());
                failedSlaves.add(workingSlave);
            }

        }
        workingSlaves.removeAll(availableSlaves);
        workingSlaves.removeAll(failedSlaves);
    }


    private static  List<MatrixBlockTuple> getMultiplicationTasks(int[][] matrixA, int[][] matrixB) {

        //split the given matrices in 4x4 blocks each
        //we could also do this step recursively until we have X 2x2 blocks and multiply those
        //to distribute the work even better
        //but for simplicity just split each one time
        List<MatrixBlock> blocksA = MatrixUtil.splitInBlocks(matrixA);
        List<MatrixBlock> blocksB = MatrixUtil.splitInBlocks(matrixB);

        //hardcoded multiplications
        List<MatrixBlockTuple> tasks = new ArrayList<>();

        //C11
        tasks.add(new MatrixBlockTuple(blocksA.get(0), blocksB.get(0), ResultMatrixPart.C11)); //a11 * b11
        tasks.add(new MatrixBlockTuple(blocksA.get(1), blocksB.get(2), ResultMatrixPart.C11)); //a12 * b21

        //C12
        tasks.add(new MatrixBlockTuple(blocksA.get(0), blocksB.get(1), ResultMatrixPart.C12)); //a11 * b12
        tasks.add(new MatrixBlockTuple(blocksA.get(1), blocksB.get(3), ResultMatrixPart.C12)); //a12 * b22

        //C21
        tasks.add(new MatrixBlockTuple(blocksA.get(2), blocksB.get(0), ResultMatrixPart.C21)); //a21 * b11
        tasks.add(new MatrixBlockTuple(blocksA.get(3), blocksB.get(2), ResultMatrixPart.C21)); //a22 * b21

        //C22
        tasks.add(new MatrixBlockTuple(blocksA.get(2), blocksB.get(1), ResultMatrixPart.C22)); //a21 * b12
        tasks.add(new MatrixBlockTuple(blocksA.get(3), blocksB.get(3), ResultMatrixPart.C22)); //a22 * b22

        return tasks;
    }

    private static List<MatrixBlockTuple> getAdditionTasks(List<ResultTuple> multiplicationResults) {
        //after the multiplications, the blocks have to sum the matrices
        List<MatrixBlockTuple> additionTasks = new ArrayList<>();
        for(ResultMatrixPart part : ResultMatrixPart.values()) {
            List<ResultTuple> rT = multiplicationResults.stream().filter(t -> part.equals(t.getPart())).collect(Collectors.toList());
            if(rT.size() == 2) {
                additionTasks.add(new MatrixBlockTuple(new MatrixBlock(rT.get(0).getMatrix()), new MatrixBlock(rT.get(1).getMatrix()), part));
            }
        }

        return additionTasks;
    }



}
