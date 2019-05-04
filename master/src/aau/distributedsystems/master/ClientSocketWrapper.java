package aau.distributedsystems.master;

import aau.distributedsystems.shared.MatrixBlockTuple;
import aau.distributedsystems.shared.Message;
import aau.distributedsystems.shared.MessageType;
import aau.distributedsystems.shared.ResultTuple;

import java.io.*;
import java.net.Socket;
import java.util.concurrent.*;

public class ClientSocketWrapper {
    private Socket socket;
    private int socketIdentifier;
    private ObjectOutputStream dous;
    private ObjectInputStream dis;
    private ExecutorService executorService;
    private Future<ResultTuple> runningTask;
    private MatrixBlockTuple currentTask;

    public ClientSocketWrapper(Socket socket, ExecutorService executor) {
        this.socket = socket;
        this.executorService = executor;
        this.runningTask = null;
        this.currentTask = null;
        try {
            this.dous = new ObjectOutputStream(socket.getOutputStream());
            this.dis = new ObjectInputStream(socket.getInputStream());
        } catch (IOException ex) {
            ex.printStackTrace();
            System.out.println("error creating clientSocket");
        }

    }

    public void shutdown() throws IOException {
        //tell slave to shut down
        dous.writeObject(new Message(MessageType.SHUTDOWN,0, null));
        dous.flush();
        this.dis.close();
        this.dous.close();
        this.socket.close();
    }

    public ObjectOutputStream getOutputStream() {
        return dous;
    }

    public ObjectInputStream getInputStream() {
        return dis;
    }

    public int getSocketIdentifier() {
        return socketIdentifier;
    }

    public void work(MatrixBlockTuple task, MessageType exerciseType) {
        currentTask = task;
        runningTask = this.executorService.submit(new SlaveExerciseTask(this, task, exerciseType));
    }

    public ResultTuple getResult(long timeout) throws TimeoutException, ExecutionException, InterruptedException {
        ResultTuple result = null;
        if(runningTask != null) {
            result = runningTask.get(timeout, TimeUnit.SECONDS);
            runningTask = null;
        }
        return result;
    }

    public MatrixBlockTuple getCurrentTask() {
        return currentTask;
    }

    public void setSocketIdentifier(int socketIdentifier) {
        this.socketIdentifier = socketIdentifier;
    }
}
