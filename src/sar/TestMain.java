package sar;

public class TestMain {

	public static final int PORT = 12345;
	
	public static final String SERVER_NAME = "Server-Side";
	public static final String CLIENT_NAME = "Client-Side";

	public static byte data_received[] = {0,0,0,0,0,0};
	public static byte data_sent[] = {5,4,3,2,1,0};
	
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
	}
}
