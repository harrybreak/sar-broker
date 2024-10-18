package sar;

public class TestMain {

	public static final int PORT = 12345;
	public static final String NAME = "Broker";

	public static byte data_received[] = null;
	public static byte data_sent[] = {5,4,3,2,1};
	
	public static int nb_received = 0;
	public static int nb_sent = 0;

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

		QueueBroker b = new QueueBroker(NAME);
		QueueBroker bis =new QueueBroker("a");

		Task t2 = new Task(b, new Runnable() {
			@Override
			public void run() {
				try {
					QueueBroker brokerRef = Task.getQueueBroker();
					MessageQueue remote = brokerRef.accept(PORT);
					TestMain.data_received = remote.receive();
				} catch (DisconnectChannelException e) {
					e.printStackTrace();
				}
			}
		});

		Task t1 = new Task(bis, new Runnable() {
			@Override
			public void run() {
				try {
					QueueBroker brokerRef = Task.getQueueBroker();
					MessageQueue remote = brokerRef.connect(NAME, PORT);
					remote.send(TestMain.data_sent, 0, 5);
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

		System.out.printf("Sent|Received:\n");
		for (int i = 0 ; i < 5 ; i++)
			System.out.printf("(%d) | (%d)\n", TestMain.data_sent[i], TestMain.data_received[i]);
	}
}
