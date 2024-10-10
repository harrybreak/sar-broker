package sar;

import java.util.LinkedList;
import java.util.List;

public class EventPump extends Thread {
	
	/**
	 * Singleton that represents the execution flow
	 * which executes all runnable events.
	 */
	
	List<Runnable> queue;
	static EventPump instance;
	
	private EventPump() {
		
		this.queue = new LinkedList<Runnable>();
	}
	
	public static EventPump inst() {
		
		synchronized (EventPump.class) {
			
			if (EventPump.instance == null) {
				
				EventPump.instance = new EventPump();
				EventPump.instance.start();
			}
		}
		
		return instance;
	}
	
	public synchronized void run() {
		
		Runnable r;
		while (true) {
			
			r = this.queue.remove(0);
			while (r!=null) {
				
				r.run();
				r = this.queue.remove(0);
			}
			sleep();
		}
	}
	
	public synchronized void post(Runnable r) {
		
		this.queue.add(r); // at the end…
		notify();
	}
	
	private void sleep() {
		
		try {
			wait();
		} catch (InterruptedException ex){
			// nothing to do here.
		}
	}
}
