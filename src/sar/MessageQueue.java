package sar;

public class MessageQueue {
	
	Channel channel;

	MessageQueue(Channel c) {
		
		this.channel = c;
	}
	
	public void send(byte[] bytes, int offset, int length) {
		
		Message msg = new Message(bytes, offset, length);
		
		byte data[] = msg.getData();
		
		int alreadySent = 0;
		
		synchronized (this) {
			
			while (alreadySent < data.length) {
				
				alreadySent += this.channel.write(data, alreadySent, data.length - alreadySent);
			}
		}
	}
	
	public byte[] receive() {
		
		byte headerData[] = new byte[Message.HEADER_SIZE];
		byte bodyData[];
		int alreadyReadHeaderData = 0;
		int alreadyReadBodyData = 0;
		
		synchronized (this) {
			
			while (alreadyReadHeaderData < Message.HEADER_SIZE) {
				
				alreadyReadHeaderData += this.channel.read(headerData,
						alreadyReadHeaderData,
						Message.HEADER_SIZE - alreadyReadHeaderData);
			}
			
			int length = Message.getLengthFromRawData(headerData);
			bodyData = new byte[length];
			
			while (alreadyReadBodyData < length) {
				
				alreadyReadBodyData += this.channel.read(bodyData,
						alreadyReadBodyData,
						length - alreadyReadBodyData);
			}
		}
		
		return bodyData;
	}
	
	public void close() {
		
		this.channel.disconnect();
	}
	
	public boolean closed() { 
		
		return this.channel.disconnected();
	}
}
