package mixed;

public class MessageQueue {
	
	boolean closed;
	Channel remote;
	
	Listener l;
	
	MessageQueue(Channel c) {
		
		this.closed = false;
		this.remote = c;
		this.l = null;
	}
	
	public boolean available() {
		
		return !(this.closed)
			&& !(this.remote == null)
			&& !(this.remote.disconnected());
	}
	
	interface Listener {
		
		void received(Message msg);
		void sent(Message msg);
		void closed();
	}
	
	void setListener(Listener l) { this.l = l; }
	
	boolean send(Message msg) {
		
		this.l.sent(msg);
		
		return false;
	}
	
	Message receive() {
		
		return null;
	}
	
	void close() {}
	boolean closed() { return this.closed; }
}
