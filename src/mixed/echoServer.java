package mixed;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class echoServer {
    public static final int PORT = 800;

    public static byte[] convertIntToByteArray(int value) {
        return new byte[] {
                (byte)(value >> 24),
                (byte)(value >> 16),
                (byte)(value >> 8),
                (byte)value };
    }

    public static int convertByteArrayToInt(byte[] byteArray) {
        int value = 0;
        value += byteArray[3];
        value += byteArray[2] * Math.pow(2,8);
        value += byteArray[1] * Math.pow(2,16);
        value += byteArray[0] * Math.pow(2,24);
        return value;
        }

   
	public static void main(String[] args) throws IOException, InterruptedException {
        Broker brokerClient = new Broker( "brokerClient");
        Broker brokerServer = new Broker( "brokerServer");

        Task echoserver = new Task(brokerServer, new Runnable(){ 
            @Override
            public void run(){
                System.out.println("...echoServers broker: " + Task.getBroker().getName());

                Channel channel = null;
                while(channel == null){
                    try {
                        System.out.println("... echoServer: accepting brokerClient ...");
                        channel = brokerServer.accept(PORT);
                    } catch (InterruptedException e) {  };
                }
                if(channel != null){
                    System.out.println("... echoServer: connected ...");
                }

                //SERVER READ
                byte[] lengthMessage = new byte[1];  
                System.out.println("... echoServer: trying to read ...");
                int read = 0;
                while (read == 0){    
                    try{
                        read = channel.read(lengthMessage, 0, 1);
                        System.out.println("... echoServer: read ...");
                    }
                    catch (IOException e){
                        //nothing;
                    };
                }        
                

                System.out.println("... echoServer: received Message length: " + lengthMessage[0]);

                int lengthToRead = lengthMessage[0];
                int lengthRead = 0;
                int offsetRead = 0;

                byte[] message = new byte[lengthToRead];

                while(offsetRead < lengthMessage[0]){
                    System.out.println("... echoServer: reading ...");
                    try{
                        lengthRead = channel.read(message, offsetRead, lengthToRead);
                        lengthToRead -= lengthRead;
                        offsetRead += lengthRead;
                        System.out.println("... echoServer: read " + offsetRead + " of " + lengthMessage[0] + " ...");
                    } catch (IOException e) { 
                        // nothing
                    };
                    
                }
            
                // SERVER WRITE
                if(message != null){
                    System.out.println("... echoServer: received message: " + new String(message, StandardCharsets.UTF_8) + " ...");
                    int written = 0;
                    while (written ==0){
                        try {
                            System.out.println("... echoServer: writing lengthMessage ...");
                            written = channel.write(lengthMessage, 0, 1);
                            System.out.println("... echoServer: written" + written + " ...");
                        } catch (IOException e){
                            //nothing
                        }
                    }
                    int lengthWritten = 0;
                    int lengthToWrite = message.length;
                    int offset = 0;
                    while(offset < message.length){
                        try {
                            System.out.println("... echoServer: writing ...");
                            lengthWritten = channel.write(message, offset, lengthToWrite);
                            lengthToWrite -= lengthWritten;
                            offset+= lengthWritten;
                            System.out.println("... echoServer: written" + offset + " of " + message.length + " ...");
                        } catch (IOException e) {
                            //nothing
                        };
                    }
                } 
                /* else{
                    System.out.println("Failure.");
                    }
                */
            }
        });

        Task echoclient = new Task(brokerClient, new Runnable(){
            @Override
            public void run(){
                System.out.println("...echoClients broker: " + Task.getBroker().getName());
                Channel channel = null;
                while (channel == null){
                    try {
                        System.out.println("... echoCllient: connecting to brokerServer ...");
                        channel = brokerClient.connect("brokerServer", PORT);
                    } catch (InterruptedException e) {
                        System.out.println("... echoCllient: Interrupted: Return");
                        
                    }
                }
                if(channel != null){
                    System.out.println("... echoCllient: connected ...");
                }
               

                // final ByteArrayOutputStream baos = new ByteArrayOutputStream();
                // byte[] buffer = new byte[32 * 1024];
                // int bytesRead;
                // try {
                //     while ((bytesRead = System.in.read(buffer)) > 0) {
                //         baos.write(buffer, 0, bytesRead);
                //     }
                // } catch (IOException e) {
                //     return;
                // }
                
                String inputString = "Hello World!";
                byte[] message = inputString.getBytes(StandardCharsets.UTF_8);
                // byte[] message = buffer;
                
                // CLIENT WRITE
                byte[] lengthMessage = {(byte) message.length};
                int written = 0;
                while (written ==0){
                    try {
                        System.out.println("... echoClient: writing lengthMessage ...");
                        written = channel.write(lengthMessage, 0, 1);
                        System.out.println("... echoClient: written" + written + " ...");
                    } catch (IOException e){
                        //nothing
                    }
                }

                int lengthWritten = 0;
                int lengthToWrite = message.length;
                int offset = 0;
                while(offset < message.length){
                    try {
                        System.out.println("... echoClient: writing ...");
                        lengthWritten = channel.write(message, offset, lengthToWrite);
                        lengthToWrite -= lengthWritten;
                        offset+= lengthWritten;
                        System.out.println("... echoClient: written" + offset + " of " + message.length + " ...");
                    } catch (IOException e) {
                        //nothing
                    };
                }


                // CLIENT READ
                lengthMessage = new byte[1];
                int read = 0;
                while (read == 0){
                    try{
                        read = channel.read(lengthMessage, 0, 1);
                    } catch (IOException e) { 
                        // nothing
                    };
                }

                int lengthToRead = lengthMessage[0];
                int lengthRead = 0;
                int offsetRead = 0;
                while(offsetRead < lengthMessage[0]){
                    try{
                    lengthRead = channel.read(message, offsetRead, lengthToRead);
                    lengthToRead -= lengthRead;
                    offsetRead += lengthRead;
                    } catch (IOException e) { 
                        // nothing
                    };
                }      
                System.out.println(new String(message, StandardCharsets.UTF_8));  
            }
        });
        
        echoserver.start();
        System.out.println("...echoserver start succesfull...");
        echoclient.start();
        System.out.println("...echoclient start succesfull...");

        echoclient.join();
        echoserver.join();

    }
}
