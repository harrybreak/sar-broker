package sar;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

public class EventPump extends Thread {
    
    /**
     * Singleton that represents the execution flow
     * which executes all runnable events.
     */
    
    private static final EventPump instance = new EventPump();
    private final BlockingQueue<Runnable> queue;
    private final AtomicBoolean running = new AtomicBoolean(false);

    private EventPump() {
    	
        this.queue = new LinkedBlockingQueue<>();
    }

    public static EventPump inst() {
    	
    	instance.start();
        return instance;
    }

    @Override
    public void run() {
    	
        while (!Thread.currentThread().isInterrupted()) {
        	
            try {
            	
                Runnable r;
                
                synchronized (this) {
                	
                    while ((r = queue.poll()) == null && !running.get())
                        wait();
                }
                
                if (r != null)
                    r.run();
                
            } catch (InterruptedException e) {
            	
                Thread.currentThread().interrupt();
            }
        }
    }

    public synchronized void post(Runnable r) {
    	
        queue.offer(r);
        notifyAll();
    }

    public synchronized void start() {
    	
        if (running.compareAndSet(false, true))
        	super.start();
    }

    public synchronized void requestStop() {
    	
        running.set(false);
        notifyAll();
    }

    public synchronized void interrupt() {
    	
        this.requestStop();
        super.interrupt();
    }
}