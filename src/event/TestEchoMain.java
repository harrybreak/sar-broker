package event;

public class TestEchoMain {
	
	public final static int PORT = 14848;
	public final static String SERVER_NAME = "Server-Side";
	public final static String CLIENT_NAME = "Client-Side";

	public static void main(String[] args) throws InterruptedException {

		Thread server = new Thread(new TestEchoServer());
		Thread client = new Thread(new TestEchoClient());
		
		server.start();
		client.start();
		
		server.join();
		client.join();
	}
}
