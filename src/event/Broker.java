package event;

import java.util.HashMap;

public class Broker {
    
    String name;
    HashMap<Integer, AcceptEvent> listening;
    
    public Broker(String name) {
        
        this.name = name;
        this.listening = new HashMap<>();
        
        BrokerManager.getSelf().add(this);
    }

    public interface AcceptListener {
        
        public void accepted(Channel c);
    }
    
    public void bind(AcceptListener l, int port) {
        
        if (this.listening.containsKey(port))
            throw new IllegalStateException("Port already listening!");
        
        AcceptEvent e = new AcceptEvent(this, l, port);
        
        this.listening.put(port, e);
        
        EventPump.inst().post(e);
    }
    
    public void unbind(int port) {
        
        try {
            this.listening.remove(port).requestStop();
        } catch (NullPointerException e) {
            throw new IllegalStateException("Port was not listening!");
        }
    }
    
    public interface ConnectListener {
        
        public void connected(Channel c);
        public void refused();
    }
    
    public void connect(ConnectListener l, String name, int port) {
        
        EventPump.inst().post(new ConnectEvent(this, l, name, port));
    }
}
