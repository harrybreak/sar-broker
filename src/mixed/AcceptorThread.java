package mixed;

public class AcceptorThread extends Thread {
	
	/**
	 * The thread that is in charge of executing the looping over accepting calls.
	 * 
	 * It provides a connection between the QueueBroker
	 * and the running thread via the function "request Stop".
	 */

	AcceptorWorker r;
	
	public AcceptorThread(AcceptorWorker r) {

		super(r);
		this.r = r;
		super.start();
	}
	
	public void requestStop() {
		
		this.r.stop();
	}
}
