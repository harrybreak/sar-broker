package mixed;

import java.util.HashMap;

/**
 * The QueueBroker class is responsible for managing connections and message queues
 * between different brokers. It provides methods to bind to a port and accept connections,
 * as well as to connect to a remote broker.
 * 
 * <p>This class contains two inner classes, Acceptor and Connector, which handle the 
 * acceptance and connection of channels respectively. It also defines two interfaces, 
 * AcceptListener and ConnectListener, for handling events related to acceptance and 
 * connection of message queues.</p>
 * 
 * <p>Usage example:</p>
 * <pre>
 * {@code
 * Broker broker = new Broker();
 * QueueBroker queueBroker = new QueueBroker("QueueBrokerName", broker);
 * 
 * queueBroker.bind(8080, new QueueBroker.AcceptListener() {
 *     @Override
 *     public void accepted(MessageQueue queue) {
 *         // Handle accepted connection
 *     }
 * });
 * 
 * queueBroker.connect("RemoteBrokerName", 9090, new QueueBroker.ConnectListener() {
 *     @Override
 *     public void connected(MessageQueue queue) {
 *         // Handle successful connection
 *     }
 * 
 *     @Override
 *     public void refused() {
 *         // Handle refused connection
 *     }
 * });
 * }
 * </pre>
 * 
 * @see Broker
 * @see Channel
 * @see MessageQueue
 * @see task1.Task
 */
public class QueueBroker {
    Broker broker;
    String name;
    HashMap <Integer, Acceptor> acceptorMap;
    HashMap <Integer, Connector> connectorMap;

    public QueueBroker(String name, Broker broker){
        this.name = name;
        this.broker = broker;
        this.acceptorMap = new HashMap<>();
        this.connectorMap = new HashMap<>();
    };

    class Acceptor implements Runnable{
        boolean run;
        int port;
        AcceptListener listener;

        Acceptor(int port, AcceptListener listener){
            this.port = port;
            this.listener=listener;
            run=true;
        }
        void stop(){run=false;}

        @Override
        public void run(){
            Channel channelAccept;
            while(run){
                try {
                    channelAccept = broker.accept(port);
                    final Channel finalChannel = channelAccept;
                    MessageQueue mq = new MessageQueue(finalChannel, broker);
                    listener.accepted(mq);
                } catch (IllegalStateException e) {
                    //nothing
                } catch (InterruptedException e) {
                    //nothing
                }
                
            }

        }
    }

    class Connector implements Runnable{
        boolean run;
        int port;
        ConnectListener listener;
        String name;

        Connector(String name, int port, ConnectListener listener){
            this.port = port;
            this.listener=listener;
            this.name = name;
            run=true;
        }
        void stop(){run=false;}

        @Override
        public void run(){
            Channel channelConnect;
            try {
                channelConnect = broker.connect(name, port);
                final Channel finalChannel = channelConnect; 
                MessageQueue mq = new MessageQueue(finalChannel, broker);
                connectorMap.remove(port);
                listener.connected(mq);
            } catch (IllegalStateException e) {
                listener.refused();
            } catch (InterruptedException e) {
                listener.refused();
            }
        }

    }


    public interface AcceptListener {
        void accepted(MessageQueue queue);
    }

    public interface ConnectListener {
        void connected(MessageQueue queue);
        void refused();
    }
    
    public boolean bind(int port, AcceptListener listener){
        Acceptor acceptor = new Acceptor(port, listener);
        Task acceptorTask = new Task(broker, acceptor);
        acceptorMap.put(port, acceptor);
        acceptorTask.start();
        return true;
    };

    boolean unbind(int port){
        if (acceptorMap.containsKey(port)){
            acceptorMap.get(port).stop();
            acceptorMap.remove(port);
            return true;
        }
        else{
            return false;
        }
    };
    
    public boolean connect(String name, int port, ConnectListener listener){
        Connector connector = new Connector(name, port, listener);
        Task connectorTask = new Task(broker, connector);
        connectorMap.put(port, connector);
        connectorTask.start();
        return true;
    };
}

// class qbAcceptListener implements Acceptlistener{
//     public void accepted(MessageQueue queue){

//     };
// }

// class qbConnectListener implements ConnectListener{

//     public void connected(MessageQueue queue){

//     };
//     public void refused(){

//     };
// }