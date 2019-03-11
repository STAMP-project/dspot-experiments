package zmq;


import ZMQ.CHARSET;
import ZMQ.Event;
import ZMQ.ZMQ_DEALER;
import ZMQ.ZMQ_EVENT_ACCEPTED;
import ZMQ.ZMQ_EVENT_DISCONNECTED;
import ZMQ.ZMQ_HEARTBEAT_IVL;
import ZMQ.ZMQ_HEARTBEAT_TTL;
import ZMQ.ZMQ_LAST_ENDPOINT;
import ZMQ.ZMQ_PAIR;
import ZMQ.ZMQ_PULL;
import ZMQ.ZMQ_PUSH;
import ZMQ.ZMQ_REP;
import ZMQ.ZMQ_REQ;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;

import static ZMQ.CHARSET;


public class HeartbeatsTest {
    private static final long MAX = 100000;

    // Tests sequentiality of received messages while heartbeating
    @Test
    public void testSequentialityReceivedMessagesMultiThreadedPushBindPullConnect() throws IOException, InterruptedException {
        final int port = Utils.findOpenPort();
        final String host = "localhost";
        Ctx ctx = ZMQ.init(1);
        Assert.assertThat(ctx, CoreMatchers.notNullValue());
        final SocketBase push = ctx.createSocket(ZMQ_PUSH);
        Assert.assertThat(push, CoreMatchers.notNullValue());
        boolean rc = ZMQ.setSocketOption(push, ZMQ_HEARTBEAT_IVL, 200);
        Assert.assertThat(rc, CoreMatchers.is(true));
        rc = push.bind(String.format("tcp://%s:%s", host, port));
        Assert.assertThat(rc, CoreMatchers.is(true));
        final SocketBase pull = ctx.createSocket(ZMQ_PULL);
        rc = pull.connect(String.format("tcp://%s:%s", host, port));
        Assert.assertThat(rc, CoreMatchers.is(true));
        ExecutorService service = Executors.newFixedThreadPool(2);
        service.submit(new Runnable() {
            @Override
            public void run() {
                Thread.currentThread().setName("Push");
                long counter = 0;
                while ((++counter) < (HeartbeatsTest.MAX)) {
                    String data = Long.toString(counter);
                    int sent = ZMQ.send(push, Long.toString(counter), 0);
                    Assert.assertThat(sent, CoreMatchers.is(data.length()));
                } 
                push.close();
            }
        });
        service.submit(new Runnable() {
            @Override
            public void run() {
                Thread.currentThread().setName("Pull");
                long counter = 0;
                while ((++counter) < (HeartbeatsTest.MAX)) {
                    Msg msg = ZMQ.recv(pull, 0);
                    Assert.assertThat(msg, CoreMatchers.notNullValue());
                    long received = Long.parseLong(new String(msg.data(), CHARSET));
                    Assert.assertThat(received, CoreMatchers.is(counter));
                    if ((counter % ((HeartbeatsTest.MAX) / 10)) == 0) {
                        System.out.print((counter + " "));
                    }
                } 
                System.out.println();
                pull.close();
            }
        });
        service.shutdown();
        service.awaitTermination(100, TimeUnit.SECONDS);
        ctx.terminate();
    }

    // Tests sequentiality of received messages while heartbeating
    @Test
    public void testSequentialityReceivedMessagesMultiThreadedPushConnectPullBind() throws IOException, InterruptedException {
        final int port = Utils.findOpenPort();
        final String host = "localhost";
        Ctx ctx = ZMQ.init(1);
        Assert.assertThat(ctx, CoreMatchers.notNullValue());
        final SocketBase push = ctx.createSocket(ZMQ_PUSH);
        Assert.assertThat(push, CoreMatchers.notNullValue());
        boolean rc = ZMQ.setSocketOption(push, ZMQ_HEARTBEAT_IVL, 200);
        Assert.assertThat(rc, CoreMatchers.is(true));
        rc = push.connect(String.format("tcp://%s:%s", host, port));
        Assert.assertThat(rc, CoreMatchers.is(true));
        final SocketBase pull = ctx.createSocket(ZMQ_PULL);
        rc = pull.bind(String.format("tcp://%s:%s", host, port));
        Assert.assertThat(rc, CoreMatchers.is(true));
        ExecutorService service = Executors.newFixedThreadPool(2);
        service.submit(new Runnable() {
            @Override
            public void run() {
                Thread.currentThread().setName("Push");
                long counter = 0;
                while ((++counter) < (HeartbeatsTest.MAX)) {
                    String data = Long.toString(counter);
                    int sent = ZMQ.send(push, Long.toString(counter), 0);
                    Assert.assertThat(sent, CoreMatchers.is(data.length()));
                } 
                push.close();
            }
        });
        service.submit(new Runnable() {
            @Override
            public void run() {
                Thread.currentThread().setName("Pull");
                long counter = 0;
                while ((++counter) < (HeartbeatsTest.MAX)) {
                    Msg msg = ZMQ.recv(pull, 0);
                    Assert.assertThat(msg, CoreMatchers.notNullValue());
                    long received = Long.parseLong(new String(msg.data(), ZMQ.CHARSET));
                    Assert.assertThat(received, CoreMatchers.is(counter));
                    if ((counter % ((HeartbeatsTest.MAX) / 10)) == 0) {
                        System.out.print((counter + " "));
                    }
                } 
                System.out.println();
                pull.close();
            }
        });
        service.shutdown();
        service.awaitTermination(100, TimeUnit.SECONDS);
        ctx.terminate();
    }

    // Tests sequentiality of received messages while heartbeating
    @Test
    public void testSequentialityReceivedMessagesSingleThread() throws IOException, InterruptedException {
        final int port = Utils.findOpenPort();
        final String host = "localhost";
        Ctx ctx = ZMQ.init(1);
        Assert.assertThat(ctx, CoreMatchers.notNullValue());
        SocketBase push = ctx.createSocket(ZMQ_PUSH);
        Assert.assertThat(push, CoreMatchers.notNullValue());
        boolean rc = ZMQ.setSocketOption(push, ZMQ_HEARTBEAT_IVL, 200);
        Assert.assertThat(rc, CoreMatchers.is(true));
        rc = push.connect(String.format("tcp://%s:%s", host, port));
        Assert.assertThat(rc, CoreMatchers.is(true));
        SocketBase pull = ctx.createSocket(ZMQ_PULL);
        Assert.assertThat(pull, CoreMatchers.notNullValue());
        rc = pull.bind(String.format("tcp://%s:%s", host, port));
        Assert.assertThat(rc, CoreMatchers.is(true));
        long counter = 0;
        while ((++counter) < ((HeartbeatsTest.MAX) / 10)) {
            String data = Long.toString(counter);
            int sent = ZMQ.send(push, Long.toString(counter), 0);
            Assert.assertThat(sent, CoreMatchers.is(data.length()));
            Msg msg = ZMQ.recv(pull, 0);
            Assert.assertThat(msg, CoreMatchers.notNullValue());
            long received = Long.parseLong(new String(msg.data(), ZMQ.CHARSET));
            Assert.assertThat(received, CoreMatchers.is(counter));
            if ((counter % ((HeartbeatsTest.MAX) / 100)) == 0) {
                System.out.print((counter + " "));
            }
        } 
        System.out.println();
        push.close();
        pull.close();
        ctx.terminate();
    }

    // this checks for heartbeat in REQ socket.
    @Test
    public void testHeartbeatReq() throws IOException {
        final int heartbeatInterval = 100;
        String addr = "tcp://localhost:*";
        Ctx ctx = ZMQ.init(1);
        Assert.assertThat(ctx, CoreMatchers.notNullValue());
        SocketBase req = ctx.createSocket(ZMQ_REQ);
        Assert.assertThat(req, CoreMatchers.notNullValue());
        boolean rc = req.setSocketOpt(ZMQ_HEARTBEAT_IVL, heartbeatInterval);
        Assert.assertThat(rc, CoreMatchers.is(true));
        SocketBase rep = ctx.createSocket(ZMQ_REP);
        Assert.assertThat(rep, CoreMatchers.notNullValue());
        rc = rep.bind(addr);
        Assert.assertThat(rc, CoreMatchers.is(true));
        addr = ((String) (ZMQ.getSocketOptionExt(rep, ZMQ_LAST_ENDPOINT)));
        Assert.assertThat(addr, CoreMatchers.notNullValue());
        rc = req.connect(addr);
        Assert.assertThat(rc, CoreMatchers.is(true));
        final long start = System.currentTimeMillis();
        do {
            // request
            int sent = ZMQ.send(req, "hello", 0);
            Assert.assertThat(sent, CoreMatchers.is("hello".length()));
            Msg msg = ZMQ.recv(rep, 0);
            Assert.assertThat(msg, CoreMatchers.notNullValue());
            // reply
            sent = ZMQ.send(rep, "world", 0);
            Assert.assertThat(sent, CoreMatchers.is("world".length()));
            msg = ZMQ.recv(req, 0);
            Assert.assertThat(msg, CoreMatchers.notNullValue());
            // let some time pass so several heartbeats are sent
        } while (((System.currentTimeMillis()) - start) < (5 * heartbeatInterval) );
        rep.close();
        req.close();
        ctx.terminate();
    }

    // This checks for a broken TCP connection (or, in this case a stuck one
    // where the peer never responds to PINGS). There should be an accepted event
    // then a disconnect event.
    @Test
    public void testHeartbeatTimeout() throws IOException, InterruptedException {
        testHeartbeatTimeout(false);
    }

    @Test
    public void testHeartbeatTimeoutWithContext() throws IOException, InterruptedException {
        testHeartbeatTimeout(true);
    }

    // This checks that peers respect the TTL value in ping messages
    // We set up a mock ZMTP 3 client and send a ping message with a TTL
    // to a server that is not doing any heartbeating. Then we sleep,
    // if the server disconnects the client, then we know the TTL did
    // its thing correctly.
    @Test
    public void testHeartbeatTtl() throws IOException, InterruptedException {
        Ctx ctx = ZMQ.createContext();
        Assert.assertThat(ctx, CoreMatchers.notNullValue());
        SocketBase server = prepServerSocket(ctx, false, false);
        Assert.assertThat(server, CoreMatchers.notNullValue());
        SocketBase monitor = ZMQ.socket(ctx, ZMQ_PAIR);
        boolean rc = monitor.connect("inproc://monitor");
        Assert.assertThat(rc, CoreMatchers.is(true));
        SocketBase client = ZMQ.socket(ctx, ZMQ_DEALER);
        Assert.assertThat(client, CoreMatchers.notNullValue());
        // Set the heartbeat TTL to 0.1 seconds
        rc = ZMQ.setSocketOption(client, ZMQ_HEARTBEAT_TTL, 100);
        Assert.assertThat(rc, CoreMatchers.is(true));
        // Set the heartbeat interval to much longer than the TTL so that
        // the socket times out on the remote side.
        rc = ZMQ.setSocketOption(client, ZMQ_HEARTBEAT_IVL, 250);
        Assert.assertThat(rc, CoreMatchers.is(true));
        String endpoint = ((String) (ZMQ.getSocketOptionExt(server, ZMQ_LAST_ENDPOINT)));
        Assert.assertThat(endpoint, CoreMatchers.notNullValue());
        rc = ZMQ.connect(client, endpoint);
        Assert.assertThat(rc, CoreMatchers.is(true));
        // By now everything should report as connected
        ZMQ.Event event = Event.read(monitor);
        Assert.assertThat(event.event, CoreMatchers.is(ZMQ_EVENT_ACCEPTED));
        ZMQ.msleep(100);
        // We should have been disconnected
        event = Event.read(monitor);
        Assert.assertThat(event.event, CoreMatchers.is(ZMQ_EVENT_DISCONNECTED));
        ZMQ.close(monitor);
        ZMQ.close(server);
        ZMQ.close(client);
        ZMQ.term(ctx);
    }

    // This checks for normal operation - that is pings and pongs being
    // exchanged normally. There should be an accepted event on the server,
    // and then no event afterwards.
    @Test
    public void testHeartbeatNoTimeoutWithCurve() throws IOException, InterruptedException {
        testHeartbeatNoTimeout(true, new byte[0]);
    }

    @Test
    public void testHeartbeatNoTimeoutWithoutCurve() throws IOException, InterruptedException {
        testHeartbeatNoTimeout(false, new byte[0]);
    }

    @Test
    public void testHeartbeatNoTimeoutWithoutCurveWithPingContext() throws IOException, InterruptedException {
        testHeartbeatNoTimeout(false, "context".getBytes(CHARSET));
    }
}

