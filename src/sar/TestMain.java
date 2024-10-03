package sar;

public class TestMain {
	
	public static final int PORT = 12345;
	public static final String NAME = "Broker";

	public static byte data_received[] = {0,0,0,0,0,0};
	public static byte data_sent[] = {5,4,3,2,1,0};
	
	public static MessageQueue mq = null;
	
    /**
     * WHAT :
     * 
     * A simple usage of tasks exchanging 5 bytes of data.
     * 
     * First, a broker called "Broker" is instantiated on this machine to allow tasks to communicate each others.
     * 
     * Then, a first task is created. It aims to read 5 bytes of data from the local channel port 80 and store it into an array.
     * This task is going to wait in an inner thread until enough data is read.
     * 
     * The main thread creates a second task which sends 5 bytes of data to the local channel port 80 via the broker "Broker".
     * 
     * EXPECTATIONS :
     * 
     * - Last five bytes from ``data_sent`` array are sent into last five memory spaces of ``data_received`` in the same order.
     */
    public static void main(String[] args) throws InterruptedException {

        Broker b = new Broker(NAME);
        Broker bis =new Broker("a");

        Task t2 = new Task(b, new Runnable() {
            @Override
            public void run() {
                try {
                	Broker brokerRef = Task.getBroker();
                	Channel remote = brokerRef.accept(PORT);
                	remote.read(TestMain.data_received, 0, 5);
				} catch (DisconnectChannelException e) {
					e.printStackTrace();
				}
            }
        });

        Task t1 = new Task(bis, new Runnable() {
            @Override
            public void run() {
                try {
					Broker brokerRef = Task.getBroker();
					Channel remote = brokerRef.connect(NAME, PORT);
					remote.write(TestMain.data_sent, 1, 5);
				} catch (DisconnectChannelException e) {
					e.printStackTrace();
				} catch (NotFoundBrokerException e) {
					e.printStackTrace();
				}
            }
        });
        
        t2.start();
        t1.start();
        
        t1.join();
        t2.join();

         if(TestMain.data_received[0] != TestMain.data_sent[0] && // While sending data, offset=1 so first byte is not concerned about this data exchange
        	TestMain.data_received[1] == TestMain.data_sent[1] && // While receiving data, offset=1
        	TestMain.data_received[2] == TestMain.data_sent[2])
             System.out.println("Test succeed !");
         else {

             System.out.println("Test failed...");
             for (int i = 0 ; i < 6 ; i++)
            	 System.out.printf("(%d) | (%d)\n", TestMain.data_sent[i], TestMain.data_received[i]);
         }
         
         // "public static MessageQueue mq = null" higher
         
         class AListener implements QueueBroker.AcceptListener {
			@Override
			public void accepted(MessageQueue queue) {
				
				TestMain.mq = queue;
				System.out.println("I well received the connected queue");
		
			}
         }
         // Create one instance of the accepting listener...
         AListener l = new AListener();
         // ... and send it to the QueueBroker
         Task.getQueueBroker().bind(PORT, l);
         
         // ... Process ...
         
         class RWListener implements MessageQueue.Listener {

			@Override
			public void received(byte[] msg) {
			}

			@Override
			public void sent(Message msg) {
				System.out.println("Message has been sent!");
			}

			@Override
			public void closed() {
			}
        	 
        	 
         }
         
         RWListener rwl = new RWListener();
         
         mq.setListener(rwl);
         
         byte bytes[] = {1,2,3};
         
         mq.send(new Message(bytes));
         
         
    }
}
