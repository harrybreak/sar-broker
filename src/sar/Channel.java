package sar;

public class Channel {

    CircularBuffer buffer;

    int read(byte bytes[], int offset, int length) {
        // MAKE THIS EXCLUSIVE
        int total_read = 0;

        try {
            for (; total_read < length; total_read++) {
                bytes[offset + total_read] = buffer.pull();
            }
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
        return total_read;
    }

    int write(byte bytes[], int offset, int length) {
        // MAKE THIS EXCLUSIVE
        int total_sent = 0;

        try {
            for (; total_sent < length; total_sent++) {
                buffer.push(bytes[offset + total_sent]);
            }
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
        return total_sent;
    }

    // METHODS TO IMPLEMENT
    void disconnect() {}
    boolean disconnected() { return false; }
}
