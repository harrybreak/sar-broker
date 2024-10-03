package sar;

public class Message {

	byte[] data;
	int offset;
	int length;
	
	public Message(byte[] data) {
		
		this.data = data;
		this.length = data.length;
		this.offset = 0;
	}
	
	public int size() {
		
		return this.length;
	}
	
	public void seek(int offset) {
		
		this.offset = offset;
	}
	
	public byte at(int offset) {
		
		return this.data[offset];
	}
	
	public byte get() {
		
		return this.data[this.offset];
	}
}
