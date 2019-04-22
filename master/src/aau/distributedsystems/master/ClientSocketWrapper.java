package aau.distributedsystems.master;

import java.io.*;
import java.net.Socket;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

public class ClientSocketWrapper {
    private Socket socket;
    private int socketIdentifier;
    private ObjectOutputStream dous;
    private ObjectInputStream dis;
    private ExecutorService executorService;
    private Future<String> runningTask;
    private static String Stop = "Stop";

    public ClientSocketWrapper(Socket socket, int socketIdentifier, ExecutorService executor) {
        this.socket = socket;
        this.socketIdentifier = socketIdentifier;
        this.executorService = executor;
        this.runningTask = null;
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
        runningTask = this.executorService.submit(new SlaveExerciseTask(this, task));
    }

    public String getResult() {
        String result = null;
        if(runningTask != null) {
            try {
                result = runningTask.get();
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
            runningTask = null;
        }
        return result;
    }
}
