package mixed;

import java.util.HashMap;

public class Broker {
	
    String name;
    HashMap<Integer, RdV> accepts;

    public Broker(String name) {
    	
        this.name = name;
        this.accepts = new HashMap<Integer, RdV>();
        BrokerManager.getSelf().add(this);
    }
    
    public String getName() {
    	
    	return this.name;
    }

    public Channel connect(String name, int port) throws NotFoundBrokerException, IllegalStateException {
        // Get wanted broker from BrokerManager
    	Broker remoteBroker = BrokerManager.getSelf().get(name);
    	
    	// Raise an exception in case the Broker Manager cannot resolve this broker name
    	if (remoteBroker == null) {
    		throw new NotFoundBrokerException(name + " cannot be resolved as broker name!");
    	}
    	
    	// Request a RdV to the remote broker
    	// The thread waits unless the broker
    	// joined by accepting task in the RdV
    	Channel c = remoteBroker.accept(port);
    	
    	// Raise an exception in case the Channel is null
    	if (c == null) {
    		throw new IllegalStateException("Channel is null!");
    	}
    	
    	// Otherwise, return the corresponding channel
    	return c;
    }

    public synchronized Channel accept(int port) {
        
    	if (this.accepts.containsKey(port)) {
			
    		RdV r = this.accepts.remove(port);
    		Channel c = r.join(this);
    		
    		try {
    			c = r.join(this);
    		} catch (DisconnectChannelException e) {
    			return null;
    		}
    		
    		notifyAll();
    		
    		return c;
    	}
    		
    	else {
    			
			RdV r = new RdV(port, this);
			
			if (this.accepts.get(port) != null)
				throw new IllegalStateException("Port " + port + "already listening!");
			
			this.accepts.put(port, r);
			
			while (!r.connected()) {
				
				try {
					wait(500);
				} catch (InterruptedException e) {
					// Nothing to do here
				}
			}
			
			return r.connectChannel;
    	}
    }
};
