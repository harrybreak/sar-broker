package event;

import mixed.CircularBuffer;

public class Channel {

	int port;
	boolean disconnected;
	boolean dangling;
	Channel remote;
    CircularBuffer in, out;
    public static final int MAXSIZE = 64; // Leave 1 byte 
    
    
    Channel(int port) {
    	
    	this.port = port;
    	this.disconnected = false;
    	this.dangling = true; // Set to true unless remote channel is no longer null
    	this.in = new CircularBuffer(MAXSIZE);
    	this.out = null;
    	this.remote = null;
    }

    public void disconnect() {
    	
    	this.remote.dangling = true;
    	this.disconnected = true;
    	this.dangling = true;
    }
    
    public boolean disconnected() {
    	
    	return this.disconnected;
    }
    
    void plug(Channel c) throws IllegalStateException {
    	
    	if (c.disconnected()) {
    		// This shall never occur
    		throw new IllegalStateException("Channel cannot be plugged to a disconnected channel!");
    	}
    	
    	this.remote = c;
    	this.out = c.in;
    	
    	this.dangling = false;
    	
    	c.remote = this;
    	c.in = this.out;
    	c.dangling = false;
    }
}
