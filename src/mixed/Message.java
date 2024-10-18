package mixed;

public class Message {

	byte[] data;
	int offset;
	int length;
	
	public Message(byte[] data) {
		
		this.data = data.clone();
		this.length = data.length;
		this.offset = 0;
	}
	
	public Message(byte[] data, int offset) {
		
		this.data = data.clone();
		this.length = data.length;
		this.offset = offset;
	}
	
	public Message(byte[] data, int offset, int length) {
		
		this.data = data.clone();
		this.length = length;
		this.offset = length;
	}
	
	@Override
	public boolean equals(Object o) {
		
		return (o instanceof Message)
			&& (this.length  ==  ((Message) o).length)
			&& (this.offset  ==  ((Message) o).offset)
			&& (this.data.equals(((Message) o).data ));
	}
	
	public int size() {
		
		return this.length;
	}
	
	public byte at(int index) {
		
		return this.data[offset];
	}
}
