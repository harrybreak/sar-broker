package mixed;

import java.io.IOException;

public class Channel {
  
  CircularBuffer in;
  CircularBuffer out;
  Channel rch;
  public Boolean disconnected;

  public static final int CAPACITY = 5;

  Channel (CircularBuffer circularBufferIN, CircularBuffer circularBufferOUT){
    in = circularBufferIN;
    out = circularBufferOUT;
  };

  public void addRemote(Channel remote){
    rch = remote;
    disconnected = false;
  }

  public int read(byte[] bytes, int offset, int length) throws IOException{
    if (disconnected){
      throw new IOException("channel is disconnected");
    }
    else if (rch.disconnected){
      throw new IOException("channel is dangling");
    }

    synchronized (this) {
      if (out.empty()){

        //System.out.println("reading, Waiting");
        try{wait(1000);}catch(InterruptedException e){};
      }
    }

    int i = offset;
    synchronized (this){
      while( i < length) { 
        try{
            bytes[i] = out.pull();
            notifyAll();
            // byte[] letter = {bytes[i]};
            // System.out.println("reading" + i + new String(letter, StandardCharsets.UTF_8));
            i += 1;
        } catch (IllegalStateException e) {
            notifyAll();
            // System.out.println("reading exception " + (i - offset) + " " + length + " " + Thread.currentThread().getName());
            return i - offset;
        }
      }
    }            
    // System.out.println("reading done " + (i - offset) + " " + length + " " + Thread.currentThread().getName());
    return i - offset;
  };

  public int write(byte[] bytes, int offset, int length) throws IOException {
    if (disconnected){
      throw new IOException("channel is disconnected");
    }
    else if (rch.disconnected){
      throw new IOException("channel is dangling");
    }
    synchronized (this) {
      if (in.full()){
        // System.out.println("writing, Waiting");
        try{wait(1000);}catch(InterruptedException e){};
      }
    }

    int i = offset;
    synchronized (this){
      while(i < length) { 
        try{
          in.push(bytes[i]);
          notifyAll();
          // byte[] letter = {bytes[offset + i]};
          // System.out.println("writing" + i + new String(letter, StandardCharsets.UTF_8));
          i += 1;
        } catch (IllegalStateException e) {
            // System.out.println("writing " + new String(bytes, StandardCharsets.UTF_8)+ " " + (i - offset) + " " + length + Thread.currentThread().getName());
            notifyAll();
            return i - offset;
        }
      }
    }
    // System.out.println("writing done " + (i - offset) + " " + length + " " + Thread.currentThread().getName());
    return i;


  };
  
  public void disconnect(){
    disconnected = true;
    rch = null;
  };

}
