package aau.distributedsystems.shared;

import java.io.Serializable;

public class Message implements Serializable {
    private MessageType messageType;
    private short slaveId;
    private long length;
    private byte[] data;

    public Message(MessageType messageType, long length, byte[] data) {
        this.messageType = messageType;
        this.length = length;
        this.data = data;
    }

    public Message(MessageType messageType, short id) {
        this.messageType = messageType;
        this.slaveId = id;
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
