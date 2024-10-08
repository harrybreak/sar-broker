package sar;

import java.util.HashMap;

public class Broker {
	
    String name;
    HashMap<Integer, RdV> accepts;

    Broker(String name) {
    	
        this.name = name;
        this.accepts = new HashMap<Integer, RdV>();
        BrokerManager.getSelf().add(this);
    }
    
    String getName() {
    	
    	return this.name;
    }

    Channel connect(String name, int port) throws NotFoundBrokerException, IllegalStateException {
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

    Channel accept(int port) {
        
    	if (this.accepts.containsKey(port)) {
    		
    		synchronized (this) {
			
	    		RdV r = this.accepts.get(port);
	    		
	    		notifyAll();
	    		
	    		try {
	    			return r.join(this);
	    		} catch (DisconnectChannelException e) {
	    			return null;
	    		}
    		}
    	}
    		
    	else {
    	
    		synchronized (this) {
    			
    			RdV r = new RdV(port, this);
    			this.accepts.put(port, r);
    			
    			while (!r.connected()) {
    				
    				try {
    					wait(1000);
    				} catch (InterruptedException e) {
    					// Nothing to do here
    				}
    			}
    			
    			return r.connectChannel;
    		}
    	}
    }
};

