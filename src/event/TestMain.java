package event;

public class TestMain {
	
	public final static int PORT = 14848;
	public final static String SERVER_NAME = "Server-Side";
	public final static String CLIENT_NAME = "Client-Side";
	
	public final static byte[] message0 = new byte[3162];
	public final static byte[] message1 = {65, 69, 83, 84, 72, 69, 84, 73, 67, 83};
	

	public static void main(String[] args) throws InterruptedException {

		// true --> use a QueueBroker
		// false -> use a Broker
		boolean useAQueueBroker = false;
		
		Thread server = new Thread(new TestServer(useAQueueBroker));
		Thread client = new Thread(new TestClient(useAQueueBroker));
		
		server.start();
		client.start();
		
		server.join();
		client.join();
	}
}
