package sar;

public class Channel {

	int port;
	boolean disconnected;
	boolean dangling;
	Channel remote;
    CircularBuffer in, out;
    public static final int MAXSIZE = 5;
    
    
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
        
    	int i = offset;
    	
		while (this.out.empty()) {
    		
			this.checkLConnection();
			
			try {
				wait();
			} catch (InterruptedException e) {
				// Wait until first byte is available
			}
		}
    	
    	synchronized (this) {
    		
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
    	
    	return i - offset;
    }

    public int write(byte bytes[], int offset, int length) throws DisconnectChannelException {
    	
    	this.checkLandRConnection();
        
    	int i = offset;
    	
		while (i == offset && this.in.full()) {
    		
			this.checkLandRConnection();
    		
			try {
				wait();
			} catch (InterruptedException e) {
				// Wait until last byte case is not busy anymore
			}
		}
    	
    	synchronized (this) {
    		
    		while (i < offset + length) {
        		
        		this.checkLandRConnection();
        		
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
