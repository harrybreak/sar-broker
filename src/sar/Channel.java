package sar;

public class Channel {

	int port;
	boolean disconnected;
	boolean dangling;
	Channel remote;
    CircularBuffer in, out;
    public static final int MAXSIZE = 4;
    
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

    public int read(byte bytes[], int offset, int length) throws DisconnectChannelException {
    	
    	if (this.disconnected) {
    		
    		throw new DisconnectChannelException("Current channel is disconnected!");
    	}
        
    	int i = offset;
    	
    	while (i < offset + length) {
    		
    		synchronized (this.out) {
    			
        		while (i == offset && this.out.empty()) {
            		
            		if (this.disconnected) {
            			
            			throw new DisconnectChannelException("Current channel is disconnected!");
            		}
        			
        			try {
        				wait();
        			} catch (InterruptedException e) {
        				// Wait until first byte is available
        			}
        		}
    		}
    		
    		synchronized (this.out) {
    			
    	    	if (this.disconnected) {
    	    		
    	    		throw new DisconnectChannelException("Current channel is disconnected!");
    	    	}
    			
        		if (i > offset && this.out.empty()) {
        			
        			return i - offset;
        		}
        		
        		bytes[i] = this.out.pull();
        		notifyAll(); // To unlock writing threads blocked due to willfulness
        		
        		i++;
    		}
    	}
    	
    	return i - offset;
    }

    public int write(byte bytes[], int offset, int length) throws DisconnectChannelException {
    	
    	if (this.disconnected) {
    		
    		throw new DisconnectChannelException("Current channel is disconnected!");
    	}
        
    	int i = offset;
    	
    	while (i < offset + length) {
    		
    		synchronized (this.in) {
    			
        		while (i == offset && this.in.full()) {
            		
            		if (this.dangling || this.remote.disconnected) {
            			
            			throw new DisconnectChannelException("Remote channel is disconnected!");
            		}
        			
                	if (this.disconnected) {
                		
                		throw new DisconnectChannelException("Current channel is disconnected!");
                	}
            		
        			try {
        				wait();
        			} catch (InterruptedException e) {
        				// Wait until last byte case is not busy anymore
        			}
        		}
    		}
    		
    		synchronized (this.in) {
        		
        		if (this.dangling || this.remote.disconnected) {
        			
        			throw new DisconnectChannelException("Remote channel is disconnected!");
        		}
    			
            	if (this.disconnected) {
            		
            		throw new DisconnectChannelException("Current channel is disconnected!");
            	}
        		
        		if (i > offset && this.in.full()) {
        			
        			return i - offset;
        		}
        		
        		this.in.push(bytes[i]);
        		notifyAll(); // To unlock reading threads blocked due to emptiness
        		
        		i++;
    		}
    	}
    	
    	return i - offset;
    }

    public void disconnect() {
    	
    	this.remote.dangling = true;
    	this.disconnected = true;
    	this.dangling = true;
    }
    
    public boolean disconnected() {
    	
    	return this.disconnected;
    }
}
