package zmq;


import ZMQ.ZMQ_DEALER;
import ZMQ.ZMQ_DONTWAIT;
import ZMQ.ZMQ_IMMEDIATE;
import ZMQ.ZMQ_LINGER;
import ZMQ.ZMQ_PULL;
import ZMQ.ZMQ_PUSH;
import ZMQ.ZMQ_RCVTIMEO;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import zmq.util.Utils;


public class ImmediateTest {
    @Test
    public void testImmediateTrue() throws Exception {
        System.out.println("Immediate = true");
        // TEST 1.
        // First we're going to attempt to send messages to two
        // pipes, one connected, the other not. We should see
        // the PUSH load balancing to both pipes, and hence half
        // of the messages getting queued, as connect() creates a
        // pipe immediately.
        int pushPort1 = Utils.findOpenPort();
        int pushPort2 = Utils.findOpenPort();
        Ctx context = ZMQ.createContext();
        Assert.assertThat(context, CoreMatchers.notNullValue());
        SocketBase to = ZMQ.socket(context, ZMQ_PULL);
        Assert.assertThat(to, CoreMatchers.notNullValue());
        int val = 0;
        boolean rc = ZMQ.setSocketOption(to, ZMQ_LINGER, val);
        Assert.assertThat(rc, CoreMatchers.is(true));
        rc = ZMQ.bind(to, ("tcp://*:" + pushPort1));
        Assert.assertThat(rc, CoreMatchers.is(true));
        // Create a socket pushing to two endpoints - only 1 message should arrive.
        SocketBase from = ZMQ.socket(context, ZMQ_PUSH);
        Assert.assertThat(from, CoreMatchers.notNullValue());
        rc = ZMQ.setSocketOption(from, ZMQ_IMMEDIATE, true);
        Assert.assertThat(rc, CoreMatchers.is(true));
        val = 0;
        rc = ZMQ.setSocketOption(from, ZMQ_LINGER, val);
        Assert.assertThat(rc, CoreMatchers.is(true));
        // This pipe will not connect
        rc = ZMQ.connect(from, ("tcp://localhost:" + pushPort2));
        Assert.assertThat(rc, CoreMatchers.is(true));
        // This pipe will
        rc = ZMQ.connect(from, ("tcp://localhost:" + pushPort1));
        Assert.assertThat(rc, CoreMatchers.is(true));
        // We send 10 messages, 5 should just get stuck in the queue
        // for the not-yet-connected pipe
        for (int i = 0; i < 10; ++i) {
            String message = "message ";
            message += '0' + i;
            int sent = ZMQ.send(from, message, 0);
            Assert.assertThat((sent >= 0), CoreMatchers.is(true));
        }
        ZMQ.sleep(1);
        // We now consume from the connected pipe
        // - we should see just 5
        int timeout = 250;
        ZMQ.setSocketOption(to, ZMQ_RCVTIMEO, timeout);
        int seen = 0;
        for (int i = 0; i < 10; ++i) {
            Msg msg = ZMQ.recv(to, 0);
            if (msg == null) {
                break;// Break when we didn't get a message

            }
            seen++;
        }
        Assert.assertThat(seen, CoreMatchers.is(5));
        ZMQ.close(from);
        ZMQ.close(to);
        ZMQ.term(context);
    }

    @Test
    public void testImmediateFalse() throws Exception {
        System.out.println("Immediate = false");
        // TEST 2
        // This time we will do the same thing, connect two pipes,
        // one of which will succeed in connecting to a bound
        // receiver, the other of which will fail. However, we will
        // also set the delay attach on connect flag, which should
        // cause the pipe attachment to be delayed until the connection
        // succeeds.
        int validPort = Utils.findOpenPort();
        int invalidPort = Utils.findOpenPort();
        Ctx context = ZMQ.createContext();
        SocketBase to = ZMQ.socket(context, ZMQ_PULL);
        Assert.assertThat(to, CoreMatchers.notNullValue());
        boolean rc = ZMQ.bind(to, ("tcp://*:" + validPort));
        Assert.assertThat(rc, CoreMatchers.is(true));
        int val = 0;
        rc = ZMQ.setSocketOption(to, ZMQ_LINGER, val);
        Assert.assertThat(rc, CoreMatchers.is(true));
        // Create a socket pushing to two endpoints - all messages should arrive.
        SocketBase from = ZMQ.socket(context, ZMQ_PUSH);
        Assert.assertThat(from, CoreMatchers.notNullValue());
        val = 0;
        rc = ZMQ.setSocketOption(from, ZMQ_LINGER, val);
        Assert.assertThat(rc, CoreMatchers.is(true));
        // Set the key flag
        rc = ZMQ.setSocketOption(from, ZMQ_IMMEDIATE, false);
        Assert.assertThat(rc, CoreMatchers.is(true));
        // Connect to the invalid socket
        rc = ZMQ.connect(from, ("tcp://localhost:" + invalidPort));
        Assert.assertThat(rc, CoreMatchers.is(true));
        // Connect to the valid socket
        rc = ZMQ.connect(from, ("tcp://localhost:" + validPort));
        Assert.assertThat(rc, CoreMatchers.is(true));
        for (int i = 0; i < 10; ++i) {
            String message = "message ";
            message += '0' + i;
            int sent = ZMQ.send(from, message, 0);
            Assert.assertThat(sent, CoreMatchers.is(message.length()));
        }
        int timeout = 250;
        ZMQ.setSocketOption(to, ZMQ_RCVTIMEO, timeout);
        int seen = 0;
        for (int i = 0; i < 10; ++i) {
            Msg msg = ZMQ.recv(to, 0);
            if (msg == null) {
                break;
            }
            seen++;
        }
        Assert.assertThat(seen, CoreMatchers.is(10));
        ZMQ.close(from);
        ZMQ.close(to);
        ZMQ.term(context);
    }

    @Test
    public void testImmediateFalseWithBrokenConnection() throws Exception {
        System.out.print("Immediate = false with broken connection");
        // TEST 3
        // This time we want to validate that the same blocking behaviour
        // occurs with an existing connection that is broken. We will send
        // messages to a connected pipe, disconnect and verify the messages
        // block. Then we reconnect and verify messages flow again.
        int port = Utils.findOpenPort();
        Ctx context = ZMQ.createContext();
        SocketBase backend = ZMQ.socket(context, ZMQ_DEALER);
        Assert.assertThat(backend, CoreMatchers.notNullValue());
        SocketBase frontend = ZMQ.socket(context, ZMQ_DEALER);
        Assert.assertThat(frontend, CoreMatchers.notNullValue());
        final int linger = 0;
        ZMQ.setSocketOption(backend, ZMQ_LINGER, linger);
        ZMQ.setSocketOption(frontend, ZMQ_LINGER, linger);
        // Frontend connects to backend using IMMEDIATE
        ZMQ.setSocketOption(frontend, ZMQ_IMMEDIATE, false);
        boolean rc = ZMQ.bind(backend, ("tcp://*:" + port));
        Assert.assertThat(rc, CoreMatchers.is(true));
        rc = ZMQ.connect(frontend, ("tcp://localhost:" + port));
        Assert.assertThat(rc, CoreMatchers.is(true));
        System.out.print(".");
        // Ping backend to frontend so we know when the connection is up
        int sent = ZMQ.send(backend, "Hello", 0);
        Assert.assertThat(sent, CoreMatchers.is(5));
        System.out.print("Ping");
        Msg msg = ZMQ.recv(frontend, 0);
        System.out.print(".");
        Assert.assertThat(msg.size(), CoreMatchers.is(5));
        // Send message from frontend to backend
        sent = ZMQ.send(frontend, "Hello", ZMQ_DONTWAIT);
        Assert.assertThat(sent, CoreMatchers.is(5));
        System.out.print("Message sent");
        ZMQ.close(backend);
        System.out.print(".");
        // Give time to process disconnect
        // There's no way to do this except with a sleep
        ZMQ.sleep(2);
        System.out.print("Message send fail");
        // Send a message, should fail
        sent = ZMQ.send(frontend, "Hello", ZMQ_DONTWAIT);
        Assert.assertThat(sent, CoreMatchers.is((-1)));
        // Recreate backend socket
        backend = ZMQ.socket(context, ZMQ_DEALER);
        ZMQ.setSocketOption(backend, ZMQ_LINGER, linger);
        rc = ZMQ.bind(backend, ("tcp://*:" + port));
        Assert.assertThat(rc, CoreMatchers.is(true));
        System.out.print(".");
        // Ping backend to frontend so we know when the connection is up
        sent = ZMQ.send(backend, "Hello", 0);
        Assert.assertThat(sent, CoreMatchers.is(5));
        System.out.print("Ping");
        msg = ZMQ.recv(frontend, 0);
        System.out.print(".");
        Assert.assertThat(msg.size(), CoreMatchers.is(5));
        System.out.print("Message sent");
        // After the reconnect, should succeed
        sent = ZMQ.send(frontend, "Hello", ZMQ_DONTWAIT);
        Assert.assertThat(sent, CoreMatchers.is(5));
        System.out.print(".");
        ZMQ.close(backend);
        ZMQ.close(frontend);
        ZMQ.term(context);
        System.out.println("Done");
    }
}

