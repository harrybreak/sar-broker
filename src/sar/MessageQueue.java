package sar;

abstract class MessageQueue {
	
	boolean closed;
	
	interface Listener {
		void received(Message msg);
		void sent(Message msg);
		void closed();
	}
	
	Listener l;
	
	void setListener(Listener l) { this.l = l; }
	
	boolean send(Message msg) {
		// Process ...
		
		this.l.sent(msg);
		
		return false;
	}
	
	void close() {}
	boolean closed() { return this.closed; }
}
