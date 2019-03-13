package org.zeromq;


import SocketType.REQ;
import SocketType.ROUTER;
import ZMQ.Poller;
import ZMQ.Poller.POLLIN;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.zeromq.ZMQ.Socket;


/**
 * Tests a REQ-ROUTER dialog with several methods,
 * each component being on a separate thread.
 */
public class TestReqRouterThreadedTcp {
    private static final long REQUEST_TIMEOUT = 1000;// msecs


    /**
     * A very simple server for one reply only.
     */
    private class Server implements Runnable {
        private final int port;

        /**
         * Creates a new server.
         *
         * @param port
         * 		the port to which to connect.
         */
        public Server(int port) {
            this.port = port;
        }

        @Override
        public void run() {
            ZContext ctx = new ZContext();
            ZMQ.Socket server = ctx.createSocket(ROUTER);
            server.bind(("tcp://localhost:" + (port)));
            System.out.println("Server started");
            ZMsg msg = ZMsg.recvMsg(server);
            // only one echo message for this server
            msg.send(server);
            System.out.println("Server sent reply");
            zmq.ZMQ.sleep(1);
            msg.destroy();
            // Clean up.
            ctx.destroySocket(server);
            ctx.close();
        }
    }

    private class Client implements Runnable {
        private final int port;

        final AtomicBoolean finished = new AtomicBoolean();

        /**
         * Creates a new client.
         *
         * @param port
         * 		the port to which to connect.
         */
        public Client(int port) {
            this.port = port;
        }

        @Override
        public void run() {
            ZContext ctx = new ZContext();
            ZMQ.Socket client = ctx.createSocket(REQ);
            client.connect(("tcp://localhost:" + (port)));
            System.out.println("Client started");
            client.send("DATA");
            System.out.println("Client sent message");
            inBetween(ctx, client);
            String reply = client.recvStr();
            System.out.println("Client received message");
            Assert.assertThat(reply, CoreMatchers.notNullValue());
            Assert.assertThat(reply, CoreMatchers.is("DATA"));
            finished.set(true);
            // Clean up.
            ctx.destroySocket(client);
            ctx.close();
        }

        /**
         * Called between the request-reply cycle.
         *
         * @param client
         * 		the socket participating to the cycle of request-reply
         */
        protected void inBetween(ZContext ctx, Socket client) {
            // to be overriden
        }
    }

    private class ClientPoll extends TestReqRouterThreadedTcp.Client {
        public ClientPoll(int port) {
            super(port);
        }

        // same results
        // @Override
        // protected void inBetween(ZContext ctx, Socket client)
        // {
        // // Poll socket for a reply, with timeout
        // PollItem items[] = { new PollItem(client, ZMQ.Poller.POLLIN) };
        // int rc = ZMQ.poll(items, 1, REQUEST_TIMEOUT);
        // assertThat(rc, is(1));
        // boolean readable = items[0].isReadable();
        // assertThat(readable, is(true));
        // }
        // 
        /**
         * Here we use a poller to check for readability of the message.
         * This should activate the prefetching mechanism.
         */
        @Override
        protected void inBetween(ZContext ctx, Socket client) {
            // Poll socket for a reply, with timeout
            ZMQ.Poller poller = ctx.createPoller(1);
            poller.register(client, POLLIN);
            int rc = poller.poll(TestReqRouterThreadedTcp.REQUEST_TIMEOUT);
            Assert.assertThat(rc, CoreMatchers.is(1));
            boolean readable = poller.pollin(0);
            Assert.assertThat(readable, CoreMatchers.is(true));
            // now a message should have been prefetched
            poller.close();
        }
    }

    /**
     * Test dialog directly.
     *
     * @throws Exception
     * 		if something bad occurs.
     */
    @Test
    public void testReqRouterTcp() throws Exception {
        System.out.println("test Req + Router");
        int port = Utils.findOpenPort();
        ExecutorService executor = Executors.newFixedThreadPool(2);
        TestReqRouterThreadedTcp.Server server = new TestReqRouterThreadedTcp.Server(port);
        TestReqRouterThreadedTcp.Client client = new TestReqRouterThreadedTcp.Client(port);
        executor.submit(server);
        executor.submit(client);
        executor.shutdown();
        executor.awaitTermination(30, TimeUnit.SECONDS);
        boolean finished = client.finished.get();
        Assert.assertThat(finished, CoreMatchers.is(true));
    }

    /**
     * Test dialog with a polling access in between request-reply.
     * This should activate the prefetching mechanism.
     *
     * @throws Exception
     * 		if something bad occurs.
     */
    @Test
    public void testReqRouterTcpPoll() throws Exception {
        System.out.println("test Req + Router with polling");
        int port = Utils.findOpenPort();
        ExecutorService executor = Executors.newFixedThreadPool(2);
        TestReqRouterThreadedTcp.Server server = new TestReqRouterThreadedTcp.Server(port);
        TestReqRouterThreadedTcp.ClientPoll client = new TestReqRouterThreadedTcp.ClientPoll(port);
        executor.submit(server);
        executor.submit(client);
        executor.shutdown();
        executor.awaitTermination(30, TimeUnit.SECONDS);
        boolean finished = client.finished.get();
        Assert.assertThat(finished, CoreMatchers.is(true));
    }
}

