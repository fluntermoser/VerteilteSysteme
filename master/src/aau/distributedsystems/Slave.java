package aau.distributedsystems;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

public class Slave {
    private Socket socket;
    private int socketIdentifier;
    private DataOutputStream dous;
    private DataInputStream dis;
    private ExecutorService executorService;
    private Future<String> runningTask;
    private static String Stop = "Stop";

    public Slave(Socket socket, int socketIdentifier, ExecutorService executor) {
        this.socket = socket;
        this.socketIdentifier = socketIdentifier;
        this.executorService = executor;
        this.runningTask = null;
        try {
            this.dous = new DataOutputStream(socket.getOutputStream());
            this.dis = new DataInputStream(socket.getInputStream());
        } catch (IOException ex) {
            System.out.println("error creating clientSocket");
        }

    }

    public void shutdown() throws IOException {
        //tell slave to shut down
        dous.writeUTF(Stop);
        dous.flush();
        this.dis.close();
        this.dous.close();
        this.socket.close();
    }

    public DataOutputStream getOutputStream() {
        return dous;
    }

    public DataInputStream getInputStream() {
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
