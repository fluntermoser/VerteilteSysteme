package aau.distributedsystems.master;

import java.io.*;
import java.net.Socket;
import java.util.concurrent.*;

public class ClientSocketWrapper {
    private Socket socket;
    private int socketIdentifier;
    private ObjectOutputStream dous;
    private ObjectInputStream dis;
    private ExecutorService executorService;
    private Future<String> runningTask;
    private String currentTask;

    public ClientSocketWrapper(Socket socket, int socketIdentifier, ExecutorService executor) {
        this.socket = socket;
        this.socketIdentifier = socketIdentifier;
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

    public void work(String task) {
        currentTask = task;
        runningTask = this.executorService.submit(new SlaveExerciseTask(this, task));
    }

    public String getResult(long timeout) throws TimeoutException, ExecutionException, InterruptedException {
        String result = null;
        if(runningTask != null) {
            result = runningTask.get(timeout, TimeUnit.SECONDS);
            runningTask = null;
        }
        return result;
    }

    public String getCurrentTask() {
        return currentTask;
    }
}
