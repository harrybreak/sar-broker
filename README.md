# Broker API : Overview

SAR is a Java package geared to allow tasks communicating each others locally. A ``Broker``, instantiated from the main thread, connects and accepts connection requests from other brokers through a dedicated connection port (80 by default). This object establishes connections between tasks. A ``Channel`` is a stream that stores the messages sent by tasks into a circular FIFO lossless buffer. This object is created whenever two brokers are connected each others. A ``Task`` is a runnable object that can be connected to an other one via an instiated ``Broker``. A task can send messages to multiple other tasks because it can read from the channel as many bytes as the program wills to, from whomever send them.

## How to use it ?

Create one instance of ``Broker`` for each peer connection you want to have. This broker must have an unique name to be identified by tasks, and, most important, **an unique port number!**.

Create as many ``Task``s as you want with the broker.

For instance, in order to read 5 bytes from the broker ``Broker:80``, in your program, write the following code:

```java
Broker b = new Broker("Broker");

Task t2 = new Task(b, new Runnable(){
    @Override
    public void run() {
        byte data[] = {0,0,0,0,0,0};
        b.accept(80).read(data, 1, 5);
    }
});

Task t1 = new Task(b, new Runnable(){
    @Override
    public void run() {
        byte data[] = {5,4,3,2,1,0};
        b.connect("Broker", 80).write(data, 1, 5);
    }
});
```

The above program uses the same broker to both connect and accept a connection from itself, which is allowed since a broker is not identified by its only name. You can also create two brokers, one which connects to the other one. The most important object to consider is the channel instance returned by the connect/accept broker's method because the channel instance must be unique in the establishment of a link between 2 or more tasks. In the above code, there is no need to save the returned channel instance in a dedicated pointer variable since it is only used to read/write 5 pure bytes from the buffer.

## How to *not* use it ?

The channel methods are exclusive to prevent messages from being mixed each others such that it cannot be restored when reading bytes. Therefore, mind yourself while overloading channels with huge amounts of data because it might block future tasks trying to read next bytes.

Working locally on a single machine implies one unique port number for one broker. You must not allow both "Toto:80" and "Titi:80" brokers on the same machine.

### Channel methods

``int read(byte[] bytes, int offset, int length);``

Get first ``length`` bytes from the channel's buffer and store them into array ``bytes``. Shift the offset from the beginning of the given array to ``offset`` if this argument is given and greater than $0$. **WARNING**: this method is exclusive, which means that a task might be blocking when it requests to write/read a huge amount of bytes into the channel.

``int write(byte[] bytes, int offset, int length);``

Write bytes ``bytes`` into the channel's buffer until the channel's buffer is full. Shift ``offset`` unused memory cases in the given array before writing bytes if this argument is given and greater than $0$. **WARNING**: this method is exclusive, which means that bytes from different tasks can be mixed each others and read in an unpredictable order by another task.

``void disconnect();``

Shutdown the connection.

``boolean disconnected();``

Check whether the connection is shut down or not.

### Broker methods

``Broker(String name);``

Instantiate a new broker with any given name.

``Channel accept(int port);``

Return the active channel corresponding to the given port. Return ***null*** when no corresponding channel is found.

``Channel connect(String name, int port);``

Return the active channel corresponding to the given name and port. Return ***null*** when the broker did not succeed to connect to such a corresponding channels after several attempts.

### Task methods

``Task(Broker b, Runnable r);``

Instantiate a new task with given broker ``b`` and given runnable ``r``. The broker ``b`` must be instantied before!

``static Broker getBroker();``

Return the broker used by the given task. This function is exclusive to prevent from data duplication between threads.
