package event;

public class Channel {
    
    public interface RWListener {
        
        void received(byte[] frame);
        void sent(byte[] frame);
        void closed();
    }

    
    WritingEvent writing;
    
    int port;
    
    boolean disconnected;
    boolean dangling;
    
    Channel remote;
    
    
    Channel(int port) {
        
        this.writing = new WritingEvent();
        EventPump.inst().post(this.writing);
        
        this.port = port;
        
        this.disconnected = false;
        this.dangling = true; // Set to true unless remote channel is no longer null

        this.remote = null;
    }

    public void disconnect() {
    	
    	EventPump.inst().post(new Runnable() {

			@Override
			public void run() {

		        // Both (event-level)
		        writing.requestStop();
		        
		        // Remote
		        remote.dangling = true;
		        
		        // Local
		        disconnected = true;
		        dangling = true;
			}
    	});
    }
    
    public boolean disconnected() {
        
        return this.disconnected;
    }
    
    public boolean dangling() {
        
        return this.dangling;
    }
    
    void plug(Channel c) throws IllegalStateException {
        
        if (c.disconnected())
        	// This shalls never occur
            throw new IllegalStateException("Channel cannot be plugged to a disconnected channel!");
        
        this.remote = c;
        
        this.dangling = false;
        
        c.remote = this;
        c.dangling = false;
    }
    
    public void setListener(RWListener l) {
    	
    	EventPump.inst().post(new Runnable() {

			@Override
			public void run() {
		        
		        writing.setWritingListener(l);
		        remote.writing.setReadingListener(l);
			}
    	});
    }
    
    /**
     * Leave ownership to the user by copying needed data in the message queue
     * @param bytes
     */
    public void send(byte[] bytes) { this.send(bytes, 0, bytes.length); }
    
    /**
     * Leave ownership to the user by copying needed data in the message queue
     * @param bytes: data to send
     * @param offset
     * @param length
     */
    public void send(byte[] bytes, int offset, int length) throws IllegalStateException {
        
        if (this.disconnected || this.dangling)
            throw new IllegalStateException("Channel not available for use!");
        
        if (bytes == null || bytes.length < 1 || length < 1)
            return; // Nothing happens when an empty message is sent.
        
        byte[] data = new byte[length];
        System.arraycopy(bytes, offset, data, 0, length);
        
        EventPump.inst().post(new Runnable() {

			@Override
			public void run() {

		        writing.push(data);
			}
        });
    }
}
