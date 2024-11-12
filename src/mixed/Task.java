package mixed;
public class Task extends Thread {

  Broker broker;
  Runnable runnable;

  public Task(Broker b, Runnable r){
    broker = b;
    runnable = r;
  };

  @Override
  public void run(){
    this.runnable.run();
  }

  public static Broker getBroker(){
    var currentThread = Thread.currentThread();
    return ((Task) currentThread).broker;
  }

}