package mixed;

public class Rendevous {
    Broker ac;
    Broker cc;
    CircularBuffer CB1;
    CircularBuffer CB2;
    Channel chAccept;
    Channel chConnect;


    Rendevous(Broker a, Broker c){
        ac = null;
        cc = null;
        CB1 = new CircularBuffer(Channel.CAPACITY);
        CB2 = new CircularBuffer(Channel.CAPACITY);
        chAccept = new Channel(CB2, CB1);
        chConnect = new Channel(CB1, CB2);
        chAccept.addRemote(chConnect);
        chConnect.addRemote(chAccept);
    };

    synchronized void waitForBothBrokers() throws InterruptedException {
        while (ac == null || cc == null) {
            try {wait();} catch (InterruptedException e) {};
        }
        notifyAll();
    }

    Channel accept(Broker b) throws InterruptedException {
        synchronized (this) {
            ac = b;
            notifyAll();
        }
        waitForBothBrokers();
        return chAccept;
    }

    Channel connect(Broker b) throws InterruptedException {
        synchronized (this) {
            cc = b;
            notifyAll();
        }
        waitForBothBrokers();
        
        return chConnect;
    }

}
