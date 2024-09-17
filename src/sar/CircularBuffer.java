package sar;

public class CircularBuffer {
    int m_capacity;
    int m_start, m_end;
    byte m_elements[];

    public CircularBuffer(int capacity) {
        m_capacity = capacity;
        m_elements = new byte[capacity];
        m_start = m_end = 0;
    }

    public boolean full() {
        int next = (m_end + 1) % m_capacity;
        return (next == m_start);
    }

    public boolean empty() {
        return (m_start == m_end);
    }

    /**
     * Pushes a byte in the buffer, if the buffer is not full,
     * throws an IllegalStateException otherwise.
     *
     * @return true if the push succeeded.
     */
    public void push(byte elem) {
        int next = (m_end + 1) % m_capacity;
        if (next == m_start)
            throw new IllegalStateException("Full");
        m_elements[m_end] = elem;
        m_end = next;
    }

    /**
     * @return the next available byte, if the buffer is not empty,
     *         throws an IllegalStateException otherwise. 
     */
    public byte pull() {
        if (m_start != m_end) {
            int next = (m_start + 1) % m_capacity;
            byte elem = m_elements[m_start];
            m_start = next;
            return elem;
        }
        throw new IllegalStateException("Empty");
    }
}
