package event;

import java.util.LinkedList;
import java.util.Queue;

public class EventPump extends Thread {
	
	/**
	 * Singleton that represents the execution flow
	 * which executes all runnable events.
	 */
	
	private Queue<Runnable> queue;
	private static EventPump instance = null;
	
	private EventPump() {
		
		this.queue = new LinkedList<Runnable>();
	}
	
	@Override
	public void run() {
		
		while (true) {
			
			while (!queue.isEmpty()) {
				
				synchronized (this) {

					Runnable next = queue.remove();
					System.out.println("Running next event of " + next.getClass().getName());
					next.run();
				}
			}
			
			synchronized (this) {
				
				try {
					wait(500);
				} catch (InterruptedException ex){
					// nothing to do here.
				}
			}
		}
	}
	
	public synchronized static EventPump inst() {
		
		if (EventPump.instance == null) {
			
			EventPump.instance = new EventPump();
			EventPump.instance.start();
		}
		
		return instance;
	}
	
	public synchronized void post(Runnable r) {
		
		this.queue.add(r); // at the end…
		System.out.println("Queue is now " + this.queue.size() + " events long.");
		notify();
	}
}
