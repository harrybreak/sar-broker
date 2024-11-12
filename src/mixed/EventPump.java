/**
 * The EventPump class is a singleton that extends Thread and is responsible for running a list of Runnable objects, one at a time.
 * Only one instance of EventPump can exist at a time.
 * 
 * <p>This class provides methods to post new Runnable events to the list and to terminate the event pump.</p>
 * 
 * <p>Usage:</p>
 * <pre>
 * {@code
 * EventPump pump = EventPump.getInstance();
 * pump.post(() -> System.out.println("Hello, World!"));
 * pump.kill();
 * }
 * </pre>
 * 
 * <p>Thread Safety:</p>
 * <p>This class uses synchronization to ensure thread safety when accessing the list of Runnable objects and the singleton instance.</p>
 * 
 * <p>Methods:</p>
 * <ul>
 * <li>{@link #getInstance()} - Returns the singleton instance of EventPump, creating it if necessary.</li>
 * <li>{@link #run()} - Runs the list of Runnable objects, one at a time. Waits for new Runnable objects if the list is empty.</li>
 * <li>{@link #post(Runnable)} - Adds a new Runnable event to the list.</li>
 * <li>{@link #kill()} - Terminates the event pump.</li>
 * </ul>
 * 
 * <p>Fields:</p>
 * <ul>
 * <li>{@code pumpList} - The list of Runnable objects to be executed.</li>
 * <li>{@code lock} - The lock object used for synchronization.</li>
 * <li>{@code dead} - A flag indicating whether the event pump should terminate.</li>
 * <li>{@code instance} - The singleton instance of EventPump.</li>
 * </ul>
 */

package mixed;

import java.util.LinkedList;
import java.util.List;

// public class EventPump extends Thread, which is a thread that runs a list of Runnable objects, one at a time. Only one EventPump object can exist at a time.
public class EventPump extends Thread {
    List<Runnable> pumpList;
    Object lock = new Object();
    boolean dead = false;
    private static volatile EventPump instance;

    private EventPump() {
        this.pumpList = new LinkedList<>();
    }

    public static EventPump getInstance() {
        if (instance == null) {
            synchronized (EventPump.class) {
                if (instance == null) {
                    instance = new EventPump();
                    instance.start();
                }
            }
        }
        return instance;
    }

    // public void run(), which runs the list of Runnable objects, one at a time. The method waits for a new Runnable object to be added to the list if the list is empty.
    public synchronized void run() {
        Runnable nextEvent;
        while(!dead || !pumpList.isEmpty()) {
            synchronized (lock) {
                if(!pumpList.isEmpty()){
                    nextEvent = pumpList.removeFirst();
                    nextEvent.run();
                    lock.notify();
                } else {
                    try {lock.wait(2000);} catch (InterruptedException e) {}
                }
            }
        }
    }
    public void post(Runnable event) {
        if(!dead){
            synchronized (lock) {
                lock.notify();
                pumpList.add(event);
            }
        }
    }

    public void kill() {
        System.out.println("killing");;
        synchronized (lock) {
            dead = true;
            lock.notify();
        }
    }
}