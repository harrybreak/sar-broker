package event;

import java.util.Queue;
import java.util.LinkedList;

class WritingEvent implements Runnable {
	
	boolean shallRun;
	boolean onWork;
	
	Channel.RWListener writing_listener;
	Channel.RWListener reading_listener;
	
    Queue<byte[]> messageQueue;
    byte[] current;
    int offset;
    int sent;
    
    // To prevent a writing event to overload the event pump
    public static final int MAXFRAME = 1500;
	
	WritingEvent() {
		
		this.shallRun = true;
		this.onWork = false;
		
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
	
	boolean push(byte[] new_data) {
    	
    	if (!this.shallRun)
    		return false;
		
    	this.messageQueue.add(new_data);
    	
    	if (!this.onWork) {
    		
    		this.onWork = true;
    		EventPump.inst().post(this);
    	}
    	
    	return true;
	}

	@Override
	public void run() {
		
		if (!this.shallRun) {
			
			this.reading_listener.closed();
			this.writing_listener.closed();
		}
		
		if (this.messageQueue.peek() != null) {
			
			// Temporarly set the "sent" variable to the current position in the current message
			this.sent = this.offset;
			
			// This array is re-created as a passthrough for leaving total ownership to the user
			// when it receives the message.
			this.current = new byte[WritingEvent.MAXFRAME];

			while (this.offset < this.messageQueue.peek().length
				&& this.offset - this.sent < WritingEvent.MAXFRAME)
				
				this.current[this.offset - this.sent] = this.messageQueue.peek()[this.offset++];
			
			// Refresh sent bytes
			this.sent = this.offset - this.sent;
			
			// Permit ownership to user who receives this message
			byte to_send[] = new byte[this.sent];
			System.arraycopy(this.current, 0, to_send, 0, this.sent);
			
			// Call on listeners and send them clones
			// to give full ownership of bytes to the user
			this.writing_listener.sent(to_send.clone());
			this.reading_listener.received(to_send.clone());
			
			if (this.offset == this.messageQueue.peek().length) {
				// Everything has been sent, we must reset the current offset
				// and remove the peeking message from the queue.
				this.offset = 0;
				this.messageQueue.remove();
			}
			
			// Start again to send next bytes
			EventPump.inst().post(this);
		}
		
		else this.onWork = false;
	}
}
