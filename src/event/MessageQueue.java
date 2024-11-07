package event;

public class MessageQueue {
	
	public static final int HEADER_SIZE = 4;

	Channel core;
	
	public interface RWListener {
		
		public void received(byte[] msg);
		public void sent(byte[] msg);
		public void closed();
	}
	
	RWListener listener;
	
	class ChannelListener implements Channel.RWListener {
		
		int r_offset;
		int r_length;
		byte[] r_msg;
		
		int s_offset;
		int s_length;
		byte[] s_msg;
		
		ChannelListener() {
			
			this.r_length = 0;
			this.r_offset = 0;
			this.r_msg = null;
			
			this.s_length = 0;
			this.s_offset = 0;
			this.s_msg = null;
		}
		
		int getLength(byte[] frame) {
			
			if (frame.length < HEADER_SIZE)
				throw new IllegalStateException("This frame is not at least 4 bytes long");
			
			int length = ((int) frame[0] << 0) +
						 ((int) frame[1] << 8) +
						((int) frame[2] << 16) +
						((int) frame[3] << 24);
			
			return length;
		}

		@Override
		public void received(byte[] frame) {
			
			// Fill the message with data from received frame
			if (this.r_msg == null) {
				// In this situation, we catch the first frame of the message.
				// Since every message sent from this MessageQueue is at least
				// 4 bytes long due to attached header, we can safely retrieve
				// the original whole message header directly from this frame.
				this.r_length = getLength(frame);
				
				// Now we can allocate needed memory to the message
				this.r_msg = new byte[this.r_length];
				
				// Decrease the length of the frame by HEADER_SIZE once the header is read
				for (int i = HEADER_SIZE ; i < frame.length ; i++)
					
					this.r_msg[this.r_offset++] = frame[i];
				
			} else {
				// In this situation, we already caugth the first frame before.
				for (int i = 0 ; i < frame.length ; i++)
					
					this.r_msg[this.r_offset++] = frame[i];
			}
			
			
			if (this.r_offset >= this.r_length) {
				// When the entire message was finally received,
				// create a new array to permit full ownership to
				// the user when they receive the message.
				byte[] to_send = new byte[this.r_length];
				System.arraycopy(this.r_msg, 0, to_send, 0, this.r_length);
				
				// Send the copy to the user
				listener.received(to_send);
				
				// Flush data
				this.r_msg = null;
				this.r_offset = 0;
				this.r_length = 0;
			}
		}

		@Override
		public void sent(byte[] frame) {

			// Fill the message with data from sent frame
			if (this.s_msg == null) {
				// In this situation, we sent the first frame of the message.
				// Since every message sent from this MessageQueue is at least
				// 4 bytes long due to attached header, we can safely retrieve
				// the original whole message header directly from this frame.
				this.s_length = getLength(frame);
				
				// Now we can allocate needed memory to the message
				this.s_msg = new byte[this.s_length];
				
				// Decrease the length of the frame by HEADER_SIZE once the header is read
				for (int i = HEADER_SIZE ; i < frame.length ; i++)
					
					this.s_msg[this.s_offset++] = frame[i];
				
			} else {
				// In this situation, we already caugth the first frame before.
				for (int i = 0 ; i < frame.length ; i++)
					
					this.s_msg[this.s_offset++] = frame[i];
			}
			
			
			if (this.s_offset >= this.s_length) {
				// When the entire message was finally sent,
				// create a new array to permit full ownership to
				// the user when they sent the message.
				byte[] to_send = new byte[this.s_length];
				System.arraycopy(this.s_msg, 0, to_send, 0, this.s_length);
				
				// Send the copy to the user
				listener.sent(to_send);
				
				// Flush data
				this.s_msg = null;
				this.s_offset = 0;
				this.s_length = 0;
			}
		}

		@Override
		public void closed() {

			listener.closed();
		}
	}
	
	ChannelListener cl;
	
	MessageQueue(Channel c) {
		
		this.core = c;
		this.listener = null;
		this.cl = new ChannelListener();
		this.core.setListener(this.cl);
	}
	
	public void disconnect() {
		
		this.core.disconnect();
	}
	
	public boolean disconnected() {
		
		return this.core.disconnected();
	}
	
	public boolean dangling() {
		
		return this.core.dangling();
	}
	
	public void setListener(RWListener l) {
		
		EventPump.inst().post(new Runnable() {

			@Override
			public void run() {

				listener = l;
			}
		});
	}
	
	public void send(byte[] bytes) {
		// All those operations are done user-side in order
		// to prevent the user to overflow the event pump.
        if (this.disconnected() || this.dangling())
            throw new IllegalStateException("Message queue not available for use!");
        
        if (bytes == null)
            throw new NullPointerException("Byte array is null");
        
		byte[] frame = new byte[bytes.length + HEADER_SIZE];
		
		frame[0] = (byte) ((bytes.length & 0x000000ff) >>> 0);
		frame[1] = (byte) ((bytes.length & 0x0000ff00) >>> 8);
		frame[2] = (byte) ((bytes.length & 0x00ff0000) >>> 16);
		frame[3] = (byte) ((bytes.length & 0xff000000) >>> 24);
		
		System.arraycopy(bytes, 0, frame, HEADER_SIZE, bytes.length);
		
		EventPump.inst().post(new Runnable() {

			@Override
			public void run() {
				
		        core.send(frame);
			}
		});
	}
}
