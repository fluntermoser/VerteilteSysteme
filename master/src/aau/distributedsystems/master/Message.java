package aau.distributedsystems.master;

import java.io.Serializable;

public class Message implements Serializable {
    private MessageType messageType;
    private int slaveId;
    private long length;
    private byte[] data;

    public Message(MessageType messageType, long length, byte[] data) {
        this.messageType = messageType;
        this.length = length;
        this.data = data;
    }

    public Message(MessageType messageType, int id) {
        this.messageType = messageType;
        this.slaveId = id;
    }

    public Message(MessageType messageType, int id, long length, byte[] data) {
        this.messageType = messageType;
        this.slaveId = id;
        this.length = length;
        this.data = data;
    }

    public MessageType getMessageType() {
        return messageType;
    }

    public int getSlaveId() {
        return slaveId;
    }

    public long getLength() {
        return length;
    }

    public byte[] getData() {
        return data;
    }
}
