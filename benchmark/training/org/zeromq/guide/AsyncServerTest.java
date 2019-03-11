package org.zeromq.guide;


import SocketType.DEALER;
import SocketType.ROUTER;
import ZActor.SimpleActor;
import ZMQ.CHARSET;
import ZPoller.IN;
import ZPoller.POLLIN;
import ZProxy.EXITED;
import ZProxy.PAUSED;
import ZProxy.Proxy.SimpleProxy;
import ZProxy.STARTED;
import ZTimer.Timer;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.zeromq.Utils;
import org.zeromq.ZActor;
import org.zeromq.ZContext;
import org.zeromq.ZFrame;
import org.zeromq.ZMQ;
import org.zeromq.ZMQ.Socket;
import org.zeromq.ZMsg;
import org.zeromq.ZPoller;
import org.zeromq.ZProxy;
import org.zeromq.ZProxy.Plug;
import org.zeromq.ZTimer;


public class AsyncServerTest {
    private static final Random rand = new Random(System.nanoTime());

    private static final AtomicInteger counter = new AtomicInteger();

    // ---------------------------------------------------------------------
    // This is our client task
    // It connects to the server, and then sends a request once per second
    // It collects responses as they arrive, and it prints them out. We will
    // run several client tasks in parallel, each with a different random ID.
    private static class Client extends ZActor.SimpleActor implements ZTimer.Handler {
        private int requestNbr = 0;

        private final String identity = String.format("%04X-%04X", AsyncServerTest.counter.incrementAndGet(), AsyncServerTest.rand.nextInt());

        private final ZTimer timer = new ZTimer();

        private Timer handle;

        private Socket client;

        private final int port;

        public Client(int port) {
            this.port = port;
        }

        @Override
        public List<Socket> createSockets(ZContext ctx, Object... args) {
            client = ctx.createSocket(DEALER);
            Assert.assertThat(client, CoreMatchers.notNullValue());
            return Collections.singletonList(client);
        }

        @Override
        public void start(Socket pipe, List<Socket> sockets, ZPoller poller) {
            boolean rc = client.setIdentity(identity.getBytes(CHARSET));
            Assert.assertThat(rc, CoreMatchers.is(true));
            rc = client.connect(("tcp://localhost:" + (port)));
            Assert.assertThat(rc, CoreMatchers.is(true));
            rc = poller.register(client, POLLIN);
            Assert.assertThat(rc, CoreMatchers.is(true));
            handle = timer.add(100, this);
            Assert.assertThat(handle, CoreMatchers.notNullValue());
        }

        @Override
        public long looping(Socket pipe, ZPoller poller) {
            return timer.timeout();
        }

        @Override
        public boolean stage(Socket socket, Socket pipe, ZPoller poller, int events) {
            ZMsg msg = ZMsg.recvMsg(socket);
            Assert.assertThat(msg, CoreMatchers.notNullValue());
            ZFrame content = msg.pop();
            Assert.assertThat(content, CoreMatchers.notNullValue());
            System.out.println((((identity) + " ") + content));
            Assert.assertThat(content.getString(CHARSET), CoreMatchers.endsWith(identity));
            return true;
        }

        @Override
        public boolean looped(Socket pipe, ZPoller poller) {
            int rc = timer.sleepAndExecute();
            Assert.assertThat(rc, CoreMatchers.is(1));
            return super.looped(pipe, poller);
        }

        @Override
        public void time(Object... args) {
            boolean rc = client.send(String.format("request #%02d - %s", (++(requestNbr)), identity));
            Assert.assertThat(rc, CoreMatchers.is(true));
        }

        @Override
        public boolean destroyed(ZContext ctx, Socket pipe, ZPoller poller) {
            boolean rc = timer.cancel(handle);
            Assert.assertThat(rc, CoreMatchers.is(true));
            return super.destroyed(ctx, pipe, poller);
        }
    }

    // This is our server task.
    // It uses the multithreaded server model to deal requests out to a pool
    // of workers and route replies back to clients. One worker can handle
    // one request at a time but one client can talk to multiple workers at
    // once.
    private static class Proxy extends ZProxy.Proxy.SimpleProxy {
        private final int frontend;

        public Proxy(int frontend) {
            this.frontend = frontend;
        }

        @Override
        public Socket create(ZContext ctx, Plug place, Object... args) {
            switch (place) {
                case FRONT :
                    Socket front = ctx.createSocket(ROUTER);
                    Assert.assertThat(front, CoreMatchers.notNullValue());
                    return front;
                case BACK :
                    Socket back = ctx.createSocket(DEALER);
                    Assert.assertThat(back, CoreMatchers.notNullValue());
                    return back;
                default :
                    return null;
            }
        }

        @Override
        public boolean configure(Socket socket, Plug place, Object... args) throws IOException {
            switch (place) {
                case FRONT :
                    return socket.bind(("tcp://*:" + (frontend)));
                case BACK :
                    return socket.bind("inproc://backend");
                default :
                    return true;
            }
        }
    }

    // Each worker task works on one request at a time and sends a random number
    // of replies back, with random delays between replies:
    private static class Worker extends ZActor.SimpleActor {
        private int counter = 0;

        private final int id;

        public Worker(int id) {
            this.id = id;
        }

        @Override
        public List<Socket> createSockets(ZContext ctx, Object... args) {
            Socket socket = ctx.createSocket(DEALER);
            Assert.assertThat(socket, CoreMatchers.notNullValue());
            return Collections.singletonList(socket);
        }

        @Override
        public void start(Socket pipe, List<Socket> sockets, ZPoller poller) {
            Socket worker = sockets.get(0);
            boolean rc = worker.setLinger(0);
            Assert.assertThat(rc, CoreMatchers.is(true));
            rc = worker.setReceiveTimeOut(100);
            Assert.assertThat(rc, CoreMatchers.is(true));
            rc = worker.setSendTimeOut(100);
            Assert.assertThat(rc, CoreMatchers.is(true));
            rc = worker.connect("inproc://backend");
            Assert.assertThat(rc, CoreMatchers.is(true));
            rc = poller.register(worker, IN);
            Assert.assertThat(rc, CoreMatchers.is(true));
        }

        @Override
        public boolean stage(Socket worker, Socket pipe, ZPoller poller, int events) {
            ZMsg msg = ZMsg.recvMsg(worker);
            if (msg == null) {
                return false;
            }
            ZFrame address = msg.pop();
            Assert.assertThat(address, CoreMatchers.notNullValue());
            String request = msg.popString();
            Assert.assertThat(request, CoreMatchers.notNullValue());
            // Send 0..4 replies back
            final int replies = AsyncServerTest.rand.nextInt(5);
            for (int reply = 0; reply < replies; reply++) {
                // Sleep for some fraction of a second
                ZMQ.msleep(((AsyncServerTest.rand.nextInt(1000)) + 1));
                msg = new ZMsg();
                boolean rc = msg.add(address);
                Assert.assertThat(rc, CoreMatchers.is(true));
                msg.add(String.format("worker #%s reply #%02d : %s", id, (++(counter)), request));
                rc = msg.send(worker);
                if (!rc) {
                    return false;
                }
            }
            return true;
        }
    }

    // The main thread simply starts several clients, and a server, and then
    // waits for the server to finish.
    @Test
    public void testAsyncServer() throws Exception {
        try (final ZContext ctx = new ZContext()) {
            final int port = Utils.findOpenPort();
            List<ZActor> actors = new ArrayList<>();
            actors.add(new ZActor(new AsyncServerTest.Client(port), null));
            actors.add(new ZActor(new AsyncServerTest.Client(port), null));
            actors.add(new ZActor(new AsyncServerTest.Client(port), null));
            ZProxy proxy = ZProxy.newProxy(ctx, "proxy", new AsyncServerTest.Proxy(port), null);
            String status = proxy.start(true);
            Assert.assertThat(status, CoreMatchers.is(STARTED));
            // Launch pool of worker threads, precise number is not critical
            for (int threadNbr = 0; threadNbr < 5; threadNbr++) {
                actors.add(new ZActor(ctx, new AsyncServerTest.Worker(threadNbr), null));
            }
            ZMQ.sleep(5);
            status = proxy.pause(true);
            Assert.assertThat(status, CoreMatchers.is(PAUSED));
            for (ZActor actor : actors) {
                boolean rc = actor.send("fini");
                Assert.assertThat(rc, CoreMatchers.is(true));
                actor.exit().awaitSilent();
            }
            status = proxy.exit();
            Assert.assertThat(status, CoreMatchers.is(EXITED));
        }
    }
}

