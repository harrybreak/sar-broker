package mixed;
import java.util.HashMap;

public class BrokerManager {

    private static BrokerManager self;
    
    HashMap <String, Broker> brokers;

    public static BrokerManager getSelf(){
        return self;
    }

    static {
        self = new BrokerManager();
    };

    BrokerManager() {
        brokers = new HashMap<String,Broker>();
    };

    public synchronized void add(Broker broker) {
        String name= broker.getName();
        Broker b = brokers.get(name);
        if (b!= null)
            throw new IllegalStateException("Broker " + name + " already exists!");
        brokers.put(name, broker);
    };
    public synchronized void remove(Broker broker){
        String name = broker.getName();
        brokers.remove(name);
    };
    public synchronized Broker get(String name){
        return brokers.get(name);
    };
}