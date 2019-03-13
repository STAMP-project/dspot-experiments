package org.whispersystems.dispatch.redis;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.security.SecureRandom;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;


public class PubSubConnectionTest {
    private static final String REPLY = "*3\r\n" + (((((((((((((((((((((((((((((("$9\r\n" + "subscribe\r\n") + "$5\r\n") + "abcde\r\n") + ":1\r\n") + "*3\r\n") + "$9\r\n") + "subscribe\r\n") + "$5\r\n") + "fghij\r\n") + ":2\r\n") + "*3\r\n") + "$9\r\n") + "subscribe\r\n") + "$5\r\n") + "klmno\r\n") + ":2\r\n") + "*3\r\n") + "$7\r\n") + "message\r\n") + "$5\r\n") + "abcde\r\n") + "$10\r\n") + "1234567890\r\n") + "*3\r\n") + "$7\r\n") + "message\r\n") + "$5\r\n") + "klmno\r\n") + "$10\r\n") + "0987654321\r\n");

    @Test
    public void testSubscribe() throws IOException {
        // ByteChannel      byteChannel = mock(ByteChannel.class);
        OutputStream outputStream = Mockito.mock(OutputStream.class);
        Socket socket = Mockito.mock(Socket.class);
        Mockito.when(socket.getOutputStream()).thenReturn(outputStream);
        PubSubConnection connection = new PubSubConnection(socket);
        connection.subscribe("foobar");
        ArgumentCaptor<byte[]> captor = ArgumentCaptor.forClass(byte[].class);
        Mockito.verify(outputStream).write(captor.capture());
        Assert.assertArrayEquals(captor.getValue(), "SUBSCRIBE foobar\r\n".getBytes());
    }

    @Test
    public void testUnsubscribe() throws IOException {
        OutputStream outputStream = Mockito.mock(OutputStream.class);
        Socket socket = Mockito.mock(Socket.class);
        Mockito.when(socket.getOutputStream()).thenReturn(outputStream);
        PubSubConnection connection = new PubSubConnection(socket);
        connection.unsubscribe("bazbar");
        ArgumentCaptor<byte[]> captor = ArgumentCaptor.forClass(byte[].class);
        Mockito.verify(outputStream).write(captor.capture());
        Assert.assertArrayEquals(captor.getValue(), "UNSUBSCRIBE bazbar\r\n".getBytes());
    }

    @Test
    public void testTricklyResponse() throws Exception {
        InputStream inputStream = mockInputStreamFor(new PubSubConnectionTest.TrickleInputStream(PubSubConnectionTest.REPLY.getBytes()));
        OutputStream outputStream = Mockito.mock(OutputStream.class);
        Socket socket = Mockito.mock(Socket.class);
        Mockito.when(socket.getOutputStream()).thenReturn(outputStream);
        Mockito.when(socket.getInputStream()).thenReturn(inputStream);
        PubSubConnection pubSubConnection = new PubSubConnection(socket);
        readResponses(pubSubConnection);
    }

    @Test
    public void testFullResponse() throws Exception {
        InputStream inputStream = mockInputStreamFor(new PubSubConnectionTest.FullInputStream(PubSubConnectionTest.REPLY.getBytes()));
        OutputStream outputStream = Mockito.mock(OutputStream.class);
        Socket socket = Mockito.mock(Socket.class);
        Mockito.when(socket.getOutputStream()).thenReturn(outputStream);
        Mockito.when(socket.getInputStream()).thenReturn(inputStream);
        PubSubConnection pubSubConnection = new PubSubConnection(socket);
        readResponses(pubSubConnection);
    }

    @Test
    public void testRandomLengthResponse() throws Exception {
        InputStream inputStream = mockInputStreamFor(new PubSubConnectionTest.RandomInputStream(PubSubConnectionTest.REPLY.getBytes()));
        OutputStream outputStream = Mockito.mock(OutputStream.class);
        Socket socket = Mockito.mock(Socket.class);
        Mockito.when(socket.getOutputStream()).thenReturn(outputStream);
        Mockito.when(socket.getInputStream()).thenReturn(inputStream);
        PubSubConnection pubSubConnection = new PubSubConnection(socket);
        readResponses(pubSubConnection);
    }

    private interface MockInputStream {
        public int read();

        public int read(byte[] input, int offset, int length);
    }

    private static class TrickleInputStream implements PubSubConnectionTest.MockInputStream {
        private final byte[] data;

        private int index = 0;

        private TrickleInputStream(byte[] data) {
            this.data = data;
        }

        public int read() {
            return data[((index)++)];
        }

        public int read(byte[] input, int offset, int length) {
            input[offset] = data[((index)++)];
            return 1;
        }
    }

    private static class FullInputStream implements PubSubConnectionTest.MockInputStream {
        private final byte[] data;

        private int index = 0;

        private FullInputStream(byte[] data) {
            this.data = data;
        }

        public int read() {
            return data[((index)++)];
        }

        public int read(byte[] input, int offset, int length) {
            int amount = Math.min(((data.length) - (index)), length);
            System.arraycopy(data, index, input, offset, amount);
            index += length;
            return amount;
        }
    }

    private static class RandomInputStream implements PubSubConnectionTest.MockInputStream {
        private final byte[] data;

        private int index = 0;

        private RandomInputStream(byte[] data) {
            this.data = data;
        }

        public int read() {
            return data[((index)++)];
        }

        public int read(byte[] input, int offset, int length) {
            int maxCopy = Math.min(((data.length) - (index)), length);
            int randomCopy = (new SecureRandom().nextInt(maxCopy)) + 1;
            int copyAmount = Math.min(maxCopy, randomCopy);
            System.arraycopy(data, index, input, offset, copyAmount);
            index += copyAmount;
            return copyAmount;
        }
    }
}

