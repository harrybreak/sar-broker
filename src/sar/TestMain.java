package sar;

public class TestMain {
	
	public static final int PORT = 12345;
	public static final String NAME = "Broker";

	public static byte data_received[] = {0,0,0,0,0,0};
	public static byte data_sent[] = {5,4,3,2,1,0};
	
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
                	remote.read(data_received, 0, 5);
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
					remote.write(data_sent, 1, 5);
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

         if (data_received[0] != data_sent[0] && // While sending data, offset=1 so first byte is not concerned about this data exchange
             data_received[1] == data_sent[1] && // While receiving data, offset=1
             data_received[2] == data_sent[2])
             System.out.println("Test succeed !");
         else {

             System.out.println("Test failed...");
             for (int i = 0 ; i < 6 ; i++)
            	 System.out.printf("(%d) | (%d)\n", data_sent[i], data_received[i]);
         }
    }
}
