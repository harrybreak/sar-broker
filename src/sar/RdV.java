package sar;

public class RdV {

	int port;
	Broker acceptBroker;
	Broker connectBroker;
	Channel acceptChannel;
	Channel connectChannel;
	
	RdV(int port, Broker cb) {
		
		this.port = port;
		this.connectBroker = cb;
		
	}
	
	Channel join(Broker ab) throws DisconnectChannelException {
		
		this.acceptBroker = ab;
		this.connectChannel = new Channel(port);
		this.acceptChannel = new Channel(port);
		
		this.acceptChannel.plug(this.connectChannel);
		this.connectChannel.plug(this.acceptChannel);
		
		return this.acceptChannel;
		
	}
	
	boolean connected() {
		return this.acceptChannel != null && this.connectChannel != null;
	}
}
