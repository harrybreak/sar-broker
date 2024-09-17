package sar;

public class Broker {
    String name;
    static int count = 0;

    Broker(String name) {
        this.name = name;
        Broker.count += 1;
    }

    Channel connect(String name, int port) {
        return null;
    }

    Channel accept(int port) {
        return null;
    }
}
