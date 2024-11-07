package event;

import java.util.LinkedList;
import java.util.Queue;

public class EventPump extends Thread {
	
	/**
	 * Singleton that represents the execution flow
	 * which executes all runnable events.
	 */
	
	private boolean alive;
	private Queue<Runnable> queue;
	private static EventPump instance = null;
	
	private EventPump() {
		
		this.queue = new LinkedList<>();
		this.alive = true;
	}
	
	@Override
	public void run() {
		
		while (this.alive) {
			
			while (!this.queue.isEmpty()) {
				
				try {
					Runnable next = this.queue.poll();
					System.out.println("[INFO][EXECOR] Running next event of " + next.getClass().getName());
					next.run();
					
				} catch (NullPointerException e) {
					System.out.println("[WARN][EXECOR] Running out of events to execute!");
				}
			}
			
			synchronized (this) {
				
				try {
					wait(500);
				} catch (InterruptedException ex){
					// Sleep unless someone put some event
				}
			}
		}
		
		System.out.println("[WARN][EXECOR] The event pump was stopped. Number of cancelled events: " + this.queue.size());
	}
	
	public synchronized static EventPump inst() {
		
		if (EventPump.instance == null) {
			
			EventPump.instance = new EventPump();
			EventPump.instance.start();
		}
		
		return instance;
	}
	
	synchronized void wakeUp() {
		
		notifyAll();
	}
	
	public void kill() {
		
		this.alive = false;
		this.wakeUp();
	}
	
	public void post(Runnable r) {
		
		this.queue.add(r); // at the end…
		System.out.println("[INFO][EXECOR] Queue is now " + this.queue.size() + " events long.");
		this.wakeUp();
	}
}
