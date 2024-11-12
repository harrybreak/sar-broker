package mixed;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class testEchoServer {
    public static final int PORT = 800;
    public static final String MESSAGE = "Hello ";
    public static final String MESSAGE2 = "world, ";
    public static final String MESSAGE3 = "0Ld! ";
    public static final String MESSAGE4 = "woold";
    public static final String ENDTOKENSTRING = "///";

    public static final byte[] message = MESSAGE.getBytes();
    public static final byte[] message2 = MESSAGE2.getBytes();
    public static final byte[] message3 = MESSAGE3.getBytes();
    public static final byte[] message4 = MESSAGE4.getBytes();
    public static final byte[] ENDTOKEN = ENDTOKENSTRING.getBytes();
    public static final byte[][] MESSAGES = {message, message2, message3, message4};

    public static void main(String[] args) throws InterruptedException{
        Broker brokerClient = new Broker( "brokerClient");
        Broker brokerServer = new Broker( "brokerServer");
        QueueBroker queueBrokerClient = new QueueBroker("queueBrokerClient", brokerClient);
        QueueBroker queueBrokerServer =  new QueueBroker("queueBrokerServer", brokerServer);

        class ServerAcceptListener implements QueueBroker.AcceptListener{
            @Override
            public void accepted(MessageQueue queue) {
                System.out.println("... SERVER accepted ...");
                queue.setListener(new MessageQueueListener(queue));
                if(!queueBrokerServer.unbind(PORT)){
                    System.out.println("... SERVER unbind failed ...");
                }
            }

            class MessageQueueListener implements MessageQueue.Listener{
                MessageQueue msgQueue;

                MessageQueueListener(MessageQueue messageQueue){
                    this.msgQueue = messageQueue;
                }

                @Override
                public void received(byte[] msg) {
                    EventPump.getInstance().post(new Runnable(){
                        @Override
                        public void run(){
                            System.out.println("SERVER Received: " + new String(msg, StandardCharsets.UTF_8));  
                            msgQueue.send(msg, 0, msg.length);
                        }
                    });
                }

                @Override
                public void sent(byte[] msg) {
                    EventPump.getInstance().post(new Runnable(){
                        @Override
                        public void run(){
                            System.out.println("SERVER Sent: " + new String(msg, StandardCharsets.UTF_8));  

                        }
                    });
                }

                @Override
                public void closed() {
                    System.out.println("SERVER messageQueue Disconnected: ");  
                    EventPump.getInstance().kill();
                }
            }
        }

        class clientConnectListener implements QueueBroker.ConnectListener{
            ByteArrayOutputStream receivedBytes = new ByteArrayOutputStream();
            private final clientConnectListener parentListener = this;
            
            class MessageQueueListener implements MessageQueue.Listener{

                MessageQueue messageQueueClient;
                
                MessageQueueListener(MessageQueue messageQueue){
                    this.messageQueueClient = messageQueue;
                }

                @Override
                public void received(byte[] msg) {
                    EventPump.getInstance().post(new Runnable(){
                        @Override
                        public void run(){
                            
                            String receivedMessage = new String(msg, StandardCharsets.UTF_8);
                            System.out.println("CLIENT Received: " + receivedMessage);  
                        

                            if(receivedMessage.equals(ENDTOKENSTRING)){ 
                                System.out.println("... CLIENT ENDTOKEN");
                                ByteArrayOutputStream Verficiation = new ByteArrayOutputStream();
                                for(byte[] message : MESSAGES){
                                    try {
                                        Verficiation.write(message);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                                if(receivedBytes.toString().equals(Verficiation.toString())){
                                    System.out.println("SUCCES:" + receivedBytes.toString());
                                } else {
                                    System.out.println("FAILED: " + receivedBytes.toString());
                                }
                                messageQueueClient.close();
                                return;
                            }
                            try {
                                receivedBytes.write(msg);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }

                @Override
                public void sent(byte[] msg) {
                    EventPump.getInstance().post(new Runnable(){
                        @Override
                        public void run(){
                            System.out.println("CLIENT Sent: " + new String(msg, StandardCharsets.UTF_8));  
                        }
                    });
                }

                @Override
                public void closed() {
                    System.out.println("CLIENT messageQueue Disconnected: ");  
                }

            }

            @Override
            public void connected(MessageQueue msgQueue) {
                EventPump.getInstance().post(new Runnable(){
                    @Override
                    public void run(){
                        System.out.println("... CLIENT connected ...");
                        
                        msgQueue.setListener(new MessageQueueListener(msgQueue));
                        for (byte[] msg : MESSAGES) {
                            msgQueue.send(msg, 0, msg.length);
                        }
                        msgQueue.send(ENDTOKEN, 0, ENDTOKEN.length);
                    }
                });
            }

            @Override
            public void refused() {
                EventPump.getInstance().post(new Runnable(){
                    @Override
                    public void run(){
                        System.out.println("... CLIENT refused retry ... ");
                        queueBrokerClient.connect(brokerServer.getName(), PORT, parentListener);
                    }
                });
            }
        }

        System.out.println("... SERVER accepting ..." );
        queueBrokerServer.bind(PORT, new ServerAcceptListener());


        System.out.println("... CLIENT connecting ..." );
        queueBrokerClient.connect(brokerServer.getName(), PORT, new clientConnectListener());

        System.out.println("endofmain");
    }
}
