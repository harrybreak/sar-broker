package sar;

public class MessageQueue {
	
	boolean closed;

	MessageQueue() { this.closed = false; }
	
	public void send(byte[] bytes, int offset, int length) {}
	
	public byte[] receive() { return null; }
	
	public void close() { this.closed = true; }
	
	public boolean closed() { return this.closed; }
	
}
