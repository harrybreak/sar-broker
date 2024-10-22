package event;

import java.util.Queue;
import java.util.LinkedList;

class SendEvent implements Runnable {
	
	boolean shallRun;
	boolean alive;
	
	Channel.RWListener writing_listener;
	Channel.RWListener reading_listener;
	
    Queue<byte[]> messageQueue;
    byte[] current;
    int offset;
    int sent;
    
    // To prevent a writing event to overload the event pump
    public static final int MAXFRAME = 1500;
	
	SendEvent() {
		
		this.shallRun = true;
		this.alive = true;
		
		this.writing_listener = null;
		this.reading_listener = null;
    	
    	this.messageQueue = new LinkedList<byte[]>();
    	this.current = null;
    	this.offset = 0;
    	this.sent = 0;
	}
	
	void setWritingListener(Channel.RWListener w) {
		
		this.writing_listener = w;
	}

	void setReadingListener(Channel.RWListener r) {
		
		this.reading_listener = r;
	}
	
	public void requestStop() {
		
		this.shallRun = false;
	}
	
	public boolean isAlive() {
		
		return this.alive;
	}
	
	void push(byte[] new_data) throws IllegalStateException {
		
    	this.messageQueue.add(new_data);
    	
    	if (!this.shallRun)
    		throw new IllegalStateException("Write Event has been stopped.");
    	
    	if (this.current == null)
    		EventPump.inst().post(this);
	}

	@Override
	public void run() {
		
		if (!this.shallRun && this.alive) {
			
			this.reading_listener.closed();
			this.writing_listener.closed();
			
			this.alive = false; // Death of the event due to the death of the channel
		}
		
		if (this.messageQueue.peek() != null) {
			
			this.sent = this.offset;
			
			// This array is re-created as a passthrough for leaving total ownership to the user
			// when it receives the message.
			this.current = new byte[SendEvent.MAXFRAME];

			while (this.offset < this.messageQueue.peek().length
				&& this.offset - this.sent < SendEvent.MAXFRAME)
				
				this.current[this.offset - this.sent] = this.messageQueue.peek()[this.offset++];
			
			// Refresh sent bytes
			this.sent = this.offset - this.sent;
			
			// Permit ownership to user who receives this message
			byte to_send[] = new byte[this.sent];
			for (int i = 0 ; i < this.sent ; i++)
				to_send[i] = this.current[i];
			
			// Call on listeners
			this.writing_listener.sent(this.sent);
			this.reading_listener.received(to_send);
			
			if (this.offset == this.messageQueue.peek().length) {
				// Everything has been sent, we must flush the current data buffer
				this.current = this.messageQueue.poll();
				this.offset = 0;
			}
			
			// Start again to send next bytes
			EventPump.inst().post(this);
		}
	}
}
