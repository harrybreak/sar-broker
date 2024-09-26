package sar;

public class Channel {

	int port;
	boolean disconnected;
	boolean dangling;
	Channel remote;
    CircularBuffer in, out;
    public static final int MAXSIZE = 8;
    
    Channel(int port) {
    	
    	this.port = port;
    	this.disconnected = false;
    	this.dangling = true; // Set to true unless remote channel is no longer null
    	this.in = new CircularBuffer(MAXSIZE);
    	this.out = null;
    	this.remote = null;
    	
    }
    
    void plug(Channel c) throws DisconnectChannelException {
    	
    	if (c.disconnected()) {
    		// This shall never occur
    		throw new DisconnectChannelException("Channel cannot be plugged to a disconnected channel!");
    	}
    	
    	this.remote = c;
    	this.out = c.in;
    	
    	this.dangling = false;
    	c.dangling = false;
    	
    }

    public int read(byte bytes[], int offset, int length) throws NotYetImplementedException {
        // MAKE THIS EXCLUSIVE
    	throw new NotYetImplementedException("");
    }

    public int write(byte bytes[], int offset, int length) throws NotYetImplementedException {
        // MAKE THIS EXCLUSIVE
    	throw new NotYetImplementedException("");
    }

    public void disconnect() {
    }
    
    public boolean disconnected() {
    	return this.disconnected;
    }
}
