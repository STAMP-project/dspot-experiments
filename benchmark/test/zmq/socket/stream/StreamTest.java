package zmq.socket.stream;


import ZMQ.CHARSET;
import ZMQ.ZMQ_DEALER;
import ZMQ.ZMQ_LAST_ENDPOINT;
import ZMQ.ZMQ_LINGER;
import ZMQ.ZMQ_SNDMORE;
import ZMQ.ZMQ_STREAM;
import java.io.IOException;
import java.nio.ByteBuffer;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import zmq.Ctx;
import zmq.Msg;
import zmq.SocketBase;
import zmq.ZMQ;


public class StreamTest {
    @Test
    public void testStream2dealer() throws IOException, InterruptedException {
        final byte[] standardGreeting = new byte[64];
        standardGreeting[0] = ((byte) (255));
        standardGreeting[8] = 1;
        standardGreeting[9] = 127;
        standardGreeting[10] = 3;
        standardGreeting[12] = 'N';
        standardGreeting[13] = 'U';
        standardGreeting[14] = 'L';
        standardGreeting[15] = 'L';
        String host = "tcp://localhost:*";
        Ctx ctx = ZMQ.init(1);
        Assert.assertThat(ctx, CoreMatchers.notNullValue());
        // We'll be using this socket in raw mode
        SocketBase stream = ZMQ.socket(ctx, ZMQ_STREAM);
        Assert.assertThat(stream, CoreMatchers.notNullValue());
        boolean rc = ZMQ.setSocketOption(stream, ZMQ_LINGER, 0);
        Assert.assertThat(rc, CoreMatchers.is(true));
        rc = ZMQ.bind(stream, host);
        Assert.assertThat(rc, CoreMatchers.is(true));
        host = ((String) (ZMQ.getSocketOptionExt(stream, ZMQ_LAST_ENDPOINT)));
        Assert.assertThat(host, CoreMatchers.notNullValue());
        // We'll be using this socket as the other peer
        SocketBase dealer = ZMQ.socket(ctx, ZMQ_DEALER);
        Assert.assertThat(dealer, CoreMatchers.notNullValue());
        rc = ZMQ.setSocketOption(dealer, ZMQ_LINGER, 0);
        Assert.assertThat(rc, CoreMatchers.is(true));
        rc = ZMQ.connect(dealer, host);
        Assert.assertThat(rc, CoreMatchers.is(true));
        // Send a message on the dealer socket
        int ret = ZMQ.send(dealer, "Hello", 0);
        Assert.assertThat(ret, CoreMatchers.is(5));
        // Connecting sends a zero message
        // First frame is identity
        Msg id = ZMQ.recv(stream, 0);
        Assert.assertThat(id, CoreMatchers.notNullValue());
        Assert.assertThat(id.hasMore(), CoreMatchers.is(true));
        // Verify the existence of Peer-Address metadata
        Assert.assertThat(id.getMetadata().get("Peer-Address").startsWith("127.0.0.1:"), CoreMatchers.is(true));
        // Second frame is zero
        Msg zero = ZMQ.recv(stream, 0);
        Assert.assertThat(zero, CoreMatchers.notNullValue());
        Assert.assertThat(zero.size(), CoreMatchers.is(0));
        // Real data follows
        // First frame is identity
        id = ZMQ.recv(stream, 0);
        Assert.assertThat(id, CoreMatchers.notNullValue());
        Assert.assertThat(id.hasMore(), CoreMatchers.is(true));
        // Verify the existence of Peer-Address metadata
        Assert.assertThat(id.getMetadata().get("Peer-Address").startsWith("127.0.0.1:"), CoreMatchers.is(true));
        // Second frame is greeting signature
        Msg greeting = ZMQ.recv(stream, 0);
        Assert.assertThat(greeting, CoreMatchers.notNullValue());
        Assert.assertThat(greeting.size(), CoreMatchers.is(10));
        Assert.assertThat(greeting.data(), CoreMatchers.is(new byte[]{ ((byte) (255)), 0, 0, 0, 0, 0, 0, 0, 1, 127 }));
        // Send our own protocol greeting
        ret = ZMQ.send(stream, id, ZMQ_SNDMORE);
        ret = ZMQ.send(stream, standardGreeting, standardGreeting.length, 0);
        Assert.assertThat(ret, CoreMatchers.is(standardGreeting.length));
        // Now we expect the data from the DEALER socket
        // We want the rest of greeting along with the Ready command
        int bytesRead = 0;
        ByteBuffer read = ByteBuffer.allocate(100);
        while (bytesRead < 97) {
            // First frame is the identity of the connection (each time)
            Msg msg = ZMQ.recv(stream, 0);
            Assert.assertThat(msg, CoreMatchers.notNullValue());
            Assert.assertThat(msg.hasMore(), CoreMatchers.is(true));
            // Second frame contains the next chunk of data
            msg = ZMQ.recv(stream, 0);
            Assert.assertThat(msg, CoreMatchers.notNullValue());
            bytesRead += msg.size();
            read.put(msg.buf());
        } 
        Assert.assertThat(read.get(0), CoreMatchers.is(((byte) (3))));
        Assert.assertThat(read.get(1), CoreMatchers.is(((byte) (0))));
        Assert.assertThat(read.get(2), CoreMatchers.is(((byte) ('N'))));
        Assert.assertThat(read.get(3), CoreMatchers.is(((byte) ('U'))));
        Assert.assertThat(read.get(4), CoreMatchers.is(((byte) ('L'))));
        Assert.assertThat(read.get(5), CoreMatchers.is(((byte) ('L'))));
        for (int idx = 0; idx < 16; ++idx) {
            Assert.assertThat(read.get(((2 + 4) + idx)), CoreMatchers.is(((byte) (0))));
        }
        Assert.assertThat(read.get(54), CoreMatchers.is(((byte) (4))));
        Assert.assertThat(read.get(55), CoreMatchers.is(((byte) (41))));// octal notation, yes

        Assert.assertThat(read.get(56), CoreMatchers.is(((byte) (5))));
        Assert.assertThat(read.get(57), CoreMatchers.is(((byte) ('R'))));
        Assert.assertThat(read.get(58), CoreMatchers.is(((byte) ('E'))));
        Assert.assertThat(read.get(59), CoreMatchers.is(((byte) ('A'))));
        Assert.assertThat(read.get(60), CoreMatchers.is(((byte) ('D'))));
        Assert.assertThat(read.get(61), CoreMatchers.is(((byte) ('Y'))));
        Assert.assertThat(read.get(62), CoreMatchers.is(((byte) (11))));
        Assert.assertThat(read.get(63), CoreMatchers.is(((byte) ('S'))));
        Assert.assertThat(read.get(64), CoreMatchers.is(((byte) ('o'))));
        Assert.assertThat(read.get(65), CoreMatchers.is(((byte) ('c'))));
        Assert.assertThat(read.get(66), CoreMatchers.is(((byte) ('k'))));
        Assert.assertThat(read.get(67), CoreMatchers.is(((byte) ('e'))));
        Assert.assertThat(read.get(68), CoreMatchers.is(((byte) ('t'))));
        Assert.assertThat(read.get(69), CoreMatchers.is(((byte) ('-'))));
        Assert.assertThat(read.get(70), CoreMatchers.is(((byte) ('T'))));
        Assert.assertThat(read.get(71), CoreMatchers.is(((byte) ('y'))));
        Assert.assertThat(read.get(72), CoreMatchers.is(((byte) ('p'))));
        Assert.assertThat(read.get(73), CoreMatchers.is(((byte) ('e'))));
        Assert.assertThat(read.get(74), CoreMatchers.is(((byte) (0))));
        Assert.assertThat(read.get(75), CoreMatchers.is(((byte) (0))));
        Assert.assertThat(read.get(76), CoreMatchers.is(((byte) (0))));
        Assert.assertThat(read.get(77), CoreMatchers.is(((byte) (6))));
        Assert.assertThat(read.get(78), CoreMatchers.is(((byte) ('D'))));
        Assert.assertThat(read.get(79), CoreMatchers.is(((byte) ('E'))));
        Assert.assertThat(read.get(80), CoreMatchers.is(((byte) ('A'))));
        Assert.assertThat(read.get(81), CoreMatchers.is(((byte) ('L'))));
        Assert.assertThat(read.get(82), CoreMatchers.is(((byte) ('E'))));
        Assert.assertThat(read.get(83), CoreMatchers.is(((byte) ('R'))));
        Assert.assertThat(read.get(84), CoreMatchers.is(((byte) (8))));
        Assert.assertThat(read.get(85), CoreMatchers.is(((byte) ('I'))));
        Assert.assertThat(read.get(86), CoreMatchers.is(((byte) ('d'))));
        Assert.assertThat(read.get(87), CoreMatchers.is(((byte) ('e'))));
        Assert.assertThat(read.get(88), CoreMatchers.is(((byte) ('n'))));
        Assert.assertThat(read.get(89), CoreMatchers.is(((byte) ('t'))));
        Assert.assertThat(read.get(90), CoreMatchers.is(((byte) ('i'))));
        Assert.assertThat(read.get(91), CoreMatchers.is(((byte) ('t'))));
        Assert.assertThat(read.get(92), CoreMatchers.is(((byte) ('y'))));
        Assert.assertThat(read.get(93), CoreMatchers.is(((byte) (0))));
        Assert.assertThat(read.get(94), CoreMatchers.is(((byte) (0))));
        Assert.assertThat(read.get(95), CoreMatchers.is(((byte) (0))));
        Assert.assertThat(read.get(96), CoreMatchers.is(((byte) (0))));
        // Send Ready command
        ZMQ.send(stream, id, ZMQ_SNDMORE);
        ZMQ.send(stream, new byte[]{ 4, 41, 5, 'R', 'E', 'A', 'D', 'Y', 11, 'S', 'o', 'c', 'k', 'e', 't', '-', 'T', 'y', 'p', 'e', 0, 0, 0, 6, 'R', 'O', 'U', 'T', 'E', 'R', 8, 'I', 'd', 'e', 'n', 't', 'i', 't', 'y', 0, 0, 0, 0 }, 0);
        // Now we expect the data from the DEALER socket
        // First frame is, again, the identity of the connection
        id = ZMQ.recv(stream, 0);
        Assert.assertThat(id, CoreMatchers.notNullValue());
        Assert.assertThat(id.hasMore(), CoreMatchers.is(true));
        // Third frame contains Hello message from DEALER
        Msg un = ZMQ.recv(stream, 0);
        Assert.assertThat(un, CoreMatchers.notNullValue());
        Assert.assertThat(un.size(), CoreMatchers.is(7));
        // Then we have a 5-byte message "Hello"
        Assert.assertThat(un.get(0), CoreMatchers.is(((byte) (0))));
        Assert.assertThat(un.get(1), CoreMatchers.is(((byte) (5))));
        Assert.assertThat(un.get(2), CoreMatchers.is(((byte) ('H'))));
        Assert.assertThat(un.get(3), CoreMatchers.is(((byte) ('e'))));
        Assert.assertThat(un.get(4), CoreMatchers.is(((byte) ('l'))));
        Assert.assertThat(un.get(5), CoreMatchers.is(((byte) ('l'))));
        Assert.assertThat(un.get(6), CoreMatchers.is(((byte) ('o'))));
        // Send "World" back to DEALER
        ZMQ.send(stream, id, ZMQ_SNDMORE);
        ret = ZMQ.send(stream, new byte[]{ 0, 5, 'W', 'o', 'r', 'l', 'd' }, 0);
        Assert.assertThat(ret, CoreMatchers.is(7));
        // Expect response on DEALER socket
        Msg recv = ZMQ.recv(dealer, 0);
        Assert.assertThat(recv.size(), CoreMatchers.is(5));
        Assert.assertThat(recv.data(), CoreMatchers.is("World".getBytes(CHARSET)));
        ZMQ.close(stream);
        ZMQ.close(dealer);
        ZMQ.term(ctx);
    }

    @Test
    public void testStream2stream() throws IOException, InterruptedException {
        String host = "tcp://localhost:*";
        Ctx ctx = ZMQ.init(1);
        Assert.assertThat(ctx, CoreMatchers.notNullValue());
        // Set up listener STREAM.
        SocketBase bind = ZMQ.socket(ctx, ZMQ_STREAM);
        Assert.assertThat(bind, CoreMatchers.notNullValue());
        boolean rc = ZMQ.bind(bind, host);
        Assert.assertThat(rc, CoreMatchers.is(true));
        host = ((String) (ZMQ.getSocketOptionExt(bind, ZMQ_LAST_ENDPOINT)));
        Assert.assertThat(host, CoreMatchers.notNullValue());
        // Set up connection stream.
        SocketBase connect = ZMQ.socket(ctx, ZMQ_STREAM);
        Assert.assertThat(connect, CoreMatchers.notNullValue());
        // Do the connection.
        rc = ZMQ.connect(connect, host);
        Assert.assertThat(rc, CoreMatchers.is(true));
        ZMQ.sleep(1);
        // Connecting sends a zero message
        // Server: First frame is identity, second frame is zero
        Msg msg = ZMQ.recv(bind, 0);
        Assert.assertThat(msg, CoreMatchers.notNullValue());
        Assert.assertThat(((msg.size()) > 0), CoreMatchers.is(true));
        msg = ZMQ.recv(bind, 0);
        Assert.assertThat(msg, CoreMatchers.notNullValue());
        Assert.assertThat(msg.size(), CoreMatchers.is(0));
        ZMQ.close(bind);
        ZMQ.close(connect);
        ZMQ.term(ctx);
    }
}

