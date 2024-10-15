package sar;

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
    
    
    void checkLandRConnection() throws DisconnectChannelException {
    	
    	if (this.disconnected) {
    		
    		throw new DisconnectChannelException("Current channel is disconnected!");
    	}
    	
		if (this.dangling || this.remote.disconnected) {
			
			throw new DisconnectChannelException("Remote channel is disconnected!");
		}
    }
    
    
    void checkLConnection() throws DisconnectChannelException {
    	
    	if (this.disconnected) {
    		
    		throw new DisconnectChannelException("Current channel is disconnected!");
    	}
    }
    
    void plug(Channel c) throws DisconnectChannelException {
    	
    	if (c.disconnected()) {
    		// This shall never occur
    		throw new DisconnectChannelException("Channel cannot be plugged to a disconnected channel!");
    	}
    	
    	this.remote = c;
    	this.out = c.in;
    	
    	this.dangling = false;
    	
    }

    public int read(byte bytes[], int offset, int length) throws DisconnectChannelException {
    	
    	this.checkLConnection(); // Raises an exception if the channel is disconnected
        
    	int i = offset; // 1r
    	
		synchronized (this) {
			
			while (this.out.empty()) {
			try {
				wait(500); // 3r
			} catch (InterruptedException e) {
				// Wait until first byte is available
			}}
			
			this.checkLConnection(); // 2r
    		
    		while (i < offset + length) {
    			
    			this.checkLConnection();
    			
        		if (i > offset && this.out.empty()) {
        			
        			return i - offset;
        		}
        		
        		bytes[i] = this.out.pull();
        		notifyAll(); // To unlock writing threads blocked due to willfulness
        		
        		i++;
    		}
    	}
    	
    	return i - offset; // 4r (end)
    }

    public int write(byte bytes[], int offset, int length) throws DisconnectChannelException {
    	
    	this.checkLandRConnection();
        
    	int i = offset; // 1w
    	
		synchronized (this) {
    		
			while (i == offset && this.in.full()) {
			try {
				wait(500);
			} catch (InterruptedException e) {
				// Wait until last byte case is not busy anymore
			}}
    		
			this.checkLandRConnection();
    		
    		while (i < offset + length) {
        		
        		this.checkLandRConnection(); // 2w
        		
        		if (i > offset && this.in.full()) {

            		notifyAll(); // To unlock reading threads blocked due to emptiness
        			return i - offset;
        		}
        		
        		else {
        			
            		this.in.push(bytes[i]);
            		notifyAll(); // To unlock reading threads blocked due to emptiness
            		
            		i++;
        		}
    		}
    	}
    	
    	return i - offset; // 3w (end)
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
