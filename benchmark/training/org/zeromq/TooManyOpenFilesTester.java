package org.zeromq;


import SocketType.REQ;
import SocketType.ROUTER;
import ZMQ.SNDMORE;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.zeromq.ZMQ.Socket;

import static ZMQ.CHARSET;


/**
 * Tests exhaustion of java file pipes,
 * each component being on a separate thread.
 */
@Ignore
public class TooManyOpenFilesTester {
    private static final long REQUEST_TIMEOUT = 2000;// msecs


    /**
     * A simple server for one reply only.
     */
    private class Server extends Thread {
        private final int port;

        private final int idx;

        /**
         * Creates a new server.
         *
         * @param port
         * 		the port to which to connect.
         * @param idx
         * 		the index of the server
         */
        public Server(int port, int idx) {
            this.port = port;
            this.idx = idx;
            setName(("Server-" + idx));
        }

        @Override
        public void run() {
            ZContext ctx = new ZContext(1);
            Socket server = ctx.createSocket(ROUTER);
            server.bind(("tcp://localhost:" + (port)));
            byte[] msg = server.recv(0);
            byte[] address = msg;
            poll(ctx, server);
            msg = server.recv(0);
            byte[] delimiter = msg;
            poll(ctx, server);
            msg = server.recv(0);
            // only one echo message for this server
            server.send(address, SNDMORE);
            server.send(delimiter, SNDMORE);
            server.send(msg, 0);
            // Clean up.
            ctx.destroy();
        }
    }

    /**
     * Simple client.
     */
    private class Client extends Thread {
        private final int port;

        final AtomicBoolean finished = new AtomicBoolean();

        private final int idx;

        /**
         * Creates a new client.
         *
         * @param port
         * 		the port to which to connect.
         */
        public Client(int port, int idx) {
            this.port = port;
            this.idx = idx;
            setName(("Client-" + idx));
        }

        @Override
        public void run() {
            ZContext ctx = new ZContext(1);
            Socket client = ctx.createSocket(REQ);
            client.setIdentity("ID".getBytes());
            client.connect(("tcp://localhost:" + (port)));
            client.send("DATA", 0);
            inBetween(ctx, client);
            byte[] reply = client.recv(0);
            Assert.assertThat(reply, CoreMatchers.notNullValue());
            Assert.assertThat(new String(reply, CHARSET), CoreMatchers.is("DATA"));
            // Clean up.
            ctx.destroy();
            finished.set(true);
        }

        /**
         * Called between the request-reply cycle.
         *
         * @param client
         * 		the socket participating to the cycle of request-reply
         * @param selector
         * 		the selector used for polling
         */
        protected void inBetween(ZContext ctx, Socket client) {
            poll(ctx, client);
        }
    }

    /**
     * Test exhaustion of java pipes.
     * Exhaustion can currently come from {@link zmq.Signaler} that are not closed
     * or from {@link java.nio.channels.Selector} that are not closed.
     *
     * @throws Exception
     * 		if something bad occurs.
     */
    @Test
    public void testReqRouterTcpPoll() throws Exception {
        // we have no direct way to test this, except by running a bunch of tests and waiting for the failure to happen...
        // crashed on iteration 3000-ish in my machine for poll selectors; on iteration 16-ish for sockets
        for (int index = 0; index < 10000; ++index) {
            long start = System.currentTimeMillis();
            List<TooManyOpenFilesTester.Pair> pairs = new ArrayList<>();
            for (int idx = 0; idx < 20; ++idx) {
                TooManyOpenFilesTester.Pair pair = testWithPoll(idx);
                pairs.add(pair);
            }
            for (TooManyOpenFilesTester.Pair p : pairs) {
                p.server.join();
                p.client.join();
            }
            boolean finished = true;
            for (TooManyOpenFilesTester.Pair p : pairs) {
                finished &= p.client.finished.get();
            }
            long end = System.currentTimeMillis();
            Assert.assertThat(finished, CoreMatchers.is(true));
            System.out.printf("Test %s finished in %s millis.\n", index, (end - start));
        }
    }

    /**
     * Dummy class to help keep relation between client and server.
     */
    private class Pair {
        private TooManyOpenFilesTester.Client client;

        private TooManyOpenFilesTester.Server server;
    }
}

