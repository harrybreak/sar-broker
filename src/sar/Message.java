package sar;

public class Message {

	byte data[];
	public static final int HEADER_SIZE = 4;
	
	public Message(byte msg[], int offset, int length) {
		
		this.data = new byte[length + HEADER_SIZE];

		this.data[0] = (byte) ((length & 0x000000ff) >>> 0);
		this.data[1] = (byte) ((length & 0x0000ff00) >>> 8);
		this.data[2] = (byte) ((length & 0x00ff0000) >>> 16);
		this.data[3] = (byte) ((length & 0xff000000) >>> 24);
		
		for (int cur = 0 ; cur < length ; cur++) {
			
			this.data[cur+HEADER_SIZE] = msg[cur];
		}
	}
	
	public byte[] getData() {
		
		return this.data;
	}
	
	public int getLength() {
		
		int length = ((int) this.data[0] << 0) +
				((int) this.data[1] << 8) +
				((int) this.data[2] << 16) +
				((int) this.data[3] << 24);
		
		return length;
	}
	
	public static int getLengthFromRawData(byte[] data) {
		
		int length = ((int) data[0] << 0) +
				((int) data[1] << 8) +
				((int) data[2] << 16) +
				((int) data[3] << 24);
		
		return length;
	}
}
