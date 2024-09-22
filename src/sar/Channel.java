package sar;

public class Channel {

	boolean disconnected;
    CircularBuffer buffer;
    public static int readingThreads = 0;
    public static final int MAXSIZE = 1024;
    
    Channel() {
    	this.disconnected = false;
    	this.buffer = new CircularBuffer(MAXSIZE);
    }

    int read(byte bytes[], int offset, int length) throws DisconnectChannelException {
    	
    	if (this.disconnected)
    		throw new DisconnectChannelException("Reading on a disconnected channel !");
    	
    	Channel.readingThreads += 1;
        int total_read = 0;

        try {
            for (; total_read < length; total_read++) {
                bytes[offset + total_read] = buffer.pull();
            }
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
        
        try {
        	return total_read;
        } finally {
        	Channel.readingThreads -= 1;
        }
    }

    int write(byte bytes[], int offset, int length) throws NotYetImplementedException {
        // MAKE THIS EXCLUSIVE
    	throw new NotYetImplementedException("");
    }

    void disconnect() {
    	while (Channel.readingThreads > 0);
    	this.disconnected = true;
    }
    
    boolean disconnected() {
    	return this.disconnected;
    }
}
