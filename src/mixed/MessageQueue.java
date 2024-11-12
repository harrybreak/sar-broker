package mixed;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/**
 * The MessageQueue class is responsible for managing the sending and receiving of messages
 * over a specified channel. It handles the queuing of messages and the communication with a listener to notify about message 
 * events such as sent, received, and closed.
 * 
 * <p>This class uses an internal queue to manage messages that are to be sent, and it 
 * ensures that messages are sent in the order they are added to the queue. It also 
 * provides functionality to receive messages from the channel and notify the listener 
 * when a message is received.</p>
 * 
 * <p>The MessageQueue class contains nested classes for handling the sending and receiving 
 * of messages, as well as a nested Message class to represent individual messages.</p>
 * 
 * <p>Usage example:</p>
 * <pre>
 * {@code
 * Channel channel = ...;
 * Broker broker = ...;
 * MessageQueue messageQueue = new MessageQueue(channel, broker);
 * messageQueue.setListener(new MessageQueue.Listener() {
 *     @Override
 *     public void received(byte[] msg) {
 *         // Handle received message
 *     }
 * 
 *     @Override
 *     public void sent(byte[] msg) {
 *         // Handle sent message
 *     }
 * 
 *     @Override
 *     public void closed() {
 *         // Handle closed connection
 *     }
 * });
 * 
 * byte[] message = ...;
 * messageQueue.send(message, 0, message.length);
 * }
 * </pre>
 * 
 * <p>Thread Safety:</p>
 * <p>This class is thread-safe. Synchronization is used to ensure that the internal state 
 * of the MessageQueue is consistent when accessed by multiple threads. The send and 
 * close methods are synchronized to prevent concurrent modifications to the queue and 
 * the channel. The Receiver and Sender nested classes also use synchronization to 
 * ensure thread safety when reading from and writing to the channel.</p>
 * 
 * @see Channel
 * @see Broker
 * @see Listener
 */
public class MessageQueue {
    private Listener listener;
    private Channel channel;
    private List<Message> queue;
    private Receiver receiver;
    private Broker broker;
    private volatile boolean sending = false;
    
    class Message {
        byte[] bytes;
        int offset;
        int length;
        Message(byte[] bytes, int offset, int length){
            this.bytes = bytes;
            this.offset = offset;
            this.length = length;
        }
    }

    private static byte[] intToByteArray(int value) {
        return new byte[] {
            (byte)(value >>> 24),
            (byte)(value >>> 16),
            (byte)(value >>> 8),
            (byte)value
        };
    }

    private static int byteArrayToInt(byte[] bytes) {
        if (bytes.length != 4) {
            throw new IllegalArgumentException("Le tableau doit contenir exactement 4 octets");
        }
        return ((bytes[0] & 0xFF) << 24) |
               ((bytes[1] & 0xFF) << 16) |
               ((bytes[2] & 0xFF) << 8) |
               (bytes[3] & 0xFF);
    }

    class Sender implements Runnable{
        final Channel channel;
        final Listener listener;
        final byte[] messageBytes;
        final int messageOffset;
        final int messageLength;
        final MessageQueue parentMessageQueue;

        Sender(Channel channel, Listener listener, byte[] bytes, int offset, int length, MessageQueue parentMessageQueue){
            this.channel = channel;
            this.messageBytes = bytes;
            this.messageOffset = offset;
            this.messageLength = length;
            this.listener = listener;
            this.parentMessageQueue = parentMessageQueue;
        }

        @Override
        public void run(){
            byte[] messageLengthByte = intToByteArray(messageLength);
            int written = 0;
            synchronized (channel){
                while (written < 4){
                    try {
                        written += channel.write(messageLengthByte, written, 4);
                    } catch (IOException e){
                        listener.closed();
                        return;
                    }
                }

                int offset = messageOffset;
                while(offset < messageLength){
                    try {
                        offset += channel.write(messageBytes, offset, messageLength);
                    } catch (IOException e) {
                        listener.closed();
                        return;
                    }
                }
            
                listener.sent(messageBytes);
                synchronized (queue) {
                    if (!queue.isEmpty()){
                        Message nextMessage = queue.remove(0);
                        Sender sender = new Sender(channel, listener, nextMessage.bytes, nextMessage.offset, nextMessage.length, parentMessageQueue);
                        Task senderTask = new Task(broker, sender);
                        senderTask.start();
                    } else {
                        sending = false;
                    }
                }
            }
        }
    }

    class Receiver implements Runnable{
        final Channel channel;
        final Listener listener;
        final MessageQueue parentMessageQueue;
        volatile boolean alive = true;

        Receiver(Channel channel, Listener listener, MessageQueue parentMessageQueue){
            this.channel = channel;
            this.listener  = listener;
            this.parentMessageQueue = parentMessageQueue;
        }

        @Override
        public void run() {
            byte[] lengthMessage;
            int read;
            while(alive){
                lengthMessage = intToByteArray(0);
                read = 0;
                synchronized (channel){
                    while (read < 4){
                        try{
                            read += channel.read(lengthMessage, read, 4);
                        } catch (IOException e) { 
                            listener.closed();
                            return;
                        }
                    }
                    int lengthMessageInt = byteArrayToInt(lengthMessage);
                    byte[] message = new byte[lengthMessageInt];
                    int offsetRead = 0;
                    while(offsetRead < lengthMessageInt){
                        try{
                            offsetRead += channel.read(message, offsetRead, lengthMessageInt);
                        } catch (IOException e) { 
                            listener.closed();
                            return;
                        }
                    }
                    if(alive){
                        listener.received(message);
                    }
                }
            }
        }
    }
    
    public MessageQueue(Channel channel, Broker broker){
        this.channel = channel;
        this.broker = broker;
        this.queue = new LinkedList<>();
    }

    public interface Listener {
        void received(byte[] msg);
        void sent(byte[] msg);
        void closed();
    }

    public synchronized void setListener(Listener l){
        this.listener = l;
        receiver = new Receiver(channel, this.listener, this);
        Task receiverTask = new Task(broker, receiver);
        receiverTask.start();
    }

    public boolean send(byte[] message, int offset, int length){
        byte[] messageCopy = new byte[length];
        int offsetCopy = offset;
        int lengthCopy = length;
        System.arraycopy(message, offset, messageCopy, offsetCopy, lengthCopy);
        synchronized (queue) {
            if (channel.disconnected){
                return false;
            } else if(sending){
                Message messageObj = new Message(messageCopy, offsetCopy, lengthCopy);
                queue.add(messageObj);
            } else{
                Sender sender = new Sender(channel, listener, messageCopy, offsetCopy, lengthCopy, this);
                Task senderTask = new Task(broker, sender);
                senderTask.start();
                sending = true; 
            }
        }
        return true;
    }

    public synchronized void close(){
        receiver.alive = false;
        channel.disconnect();
    }

    public synchronized boolean closed(){
        return channel.disconnected;
    }
}
