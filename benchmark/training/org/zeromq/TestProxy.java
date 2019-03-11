package org.zeromq;


import SocketType.DEALER;
import SocketType.PAIR;
import SocketType.REQ;
import SocketType.ROUTER;
import ZMQ.PROXY_TERMINATE;
import ZMQ.SNDMORE;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.zeromq.ZMQ.Context;
import org.zeromq.ZMQ.Socket;
import org.zeromq.util.ZData;

import static ZMQ.CHARSET;


public class TestProxy {
    static class Client implements Runnable {
        private final String frontend;

        private final String name;

        private final AtomicBoolean result = new AtomicBoolean();

        public Client(String name, String frontend) {
            this.name = name;
            this.frontend = frontend;
        }

        @Override
        public void run() {
            Context ctx = ZMQ.context(1);
            Assert.assertThat(ctx, CoreMatchers.notNullValue());
            Socket socket = ctx.socket(REQ);
            boolean rc;
            rc = socket.setIdentity(TestProxy.id(name));
            Assert.assertThat(rc, CoreMatchers.is(true));
            System.out.println(("Start " + (name)));
            Thread.currentThread().setName(name);
            rc = socket.connect(frontend);
            Assert.assertThat(rc, CoreMatchers.is(true));
            result.set(process(socket));
            socket.close();
            ctx.close();
            System.out.println(("Stop " + (name)));
        }

        private boolean process(Socket socket) {
            boolean rc = socket.send("hello");
            if (!rc) {
                System.out.println(((name) + " unable to send first message"));
                return false;
            }
            System.out.println(((name) + " sent 1st message"));
            String msg = socket.recvStr(0);
            System.out.println((((name) + " received ") + msg));
            if ((msg == null) || (!(msg.startsWith("OK hello")))) {
                return false;
            }
            rc = socket.send("world");
            if (!rc) {
                System.out.println(((name) + " unable to send second message"));
                return false;
            }
            msg = socket.recvStr(0);
            System.out.println((((name) + " received ") + msg));
            if ((msg == null) || (!(msg.startsWith("OK world")))) {
                return false;
            }
            return true;
        }
    }

    static class Dealer implements Runnable {
        private final String backend;

        private final String name;

        private final AtomicBoolean result = new AtomicBoolean();

        public Dealer(String name, String backend) {
            this.name = name;
            this.backend = backend;
        }

        @Override
        public void run() {
            Context ctx = ZMQ.context(1);
            Assert.assertThat(ctx, CoreMatchers.notNullValue());
            Thread.currentThread().setName(name);
            System.out.println(("Start " + (name)));
            Socket socket = ctx.socket(DEALER);
            boolean rc;
            rc = socket.setIdentity(TestProxy.id(name));
            Assert.assertThat(rc, CoreMatchers.is(true));
            rc = socket.connect(backend);
            Assert.assertThat(rc, CoreMatchers.is(true));
            result.set(process(socket));
            socket.close();
            ctx.close();
            System.out.println(("Stop " + (name)));
        }

        private boolean process(Socket socket) {
            int count = 0;
            while (count < 2) {
                byte[] msg = socket.recv(0);
                String msgAsString = new String(msg, CHARSET);
                if ((msg == null) || (!(msgAsString.startsWith("Client-")))) {
                    System.out.println((((name) + " Wrong identity ") + msgAsString));
                    return false;
                }
                final byte[] identity = msg;
                System.out.println((((name) + " received client identity ") + (ZData.strhex(identity))));
                msg = socket.recv(0);
                msgAsString = new String(msg, CHARSET);
                if ((msg == null) || ((msg.length) != 0)) {
                    System.out.println(("Not bottom " + (Arrays.toString(msg))));
                    return false;
                }
                System.out.println((((name) + " received bottom ") + msgAsString));
                msg = socket.recv(0);
                if (msg == null) {
                    System.out.println((((name) + " Not data ") + msg));
                    return false;
                }
                msgAsString = new String(msg, CHARSET);
                System.out.println((((name) + " received data ") + msgAsString));
                socket.send(identity, SNDMORE);
                socket.send(((byte[]) (null)), SNDMORE);
                String response = (("OK " + msgAsString) + " ") + (name);
                socket.send(response, 0);
                count++;
            } 
            return true;
        }
    }

    static class Proxy extends Thread {
        private final String frontend;

        private final String backend;

        private final String control;

        private final AtomicBoolean result = new AtomicBoolean();

        Proxy(String frontend, String backend, String control) {
            this.frontend = frontend;
            this.backend = backend;
            this.control = control;
        }

        @Override
        public void run() {
            Context ctx = ZMQ.context(1);
            assert ctx != null;
            setName("Proxy");
            Socket frontend = ctx.socket(ROUTER);
            Assert.assertThat(frontend, CoreMatchers.notNullValue());
            frontend.bind(this.frontend);
            Socket backend = ctx.socket(DEALER);
            Assert.assertThat(backend, CoreMatchers.notNullValue());
            backend.bind(this.backend);
            Socket control = ctx.socket(PAIR);
            Assert.assertThat(control, CoreMatchers.notNullValue());
            control.bind(this.control);
            ZMQ.proxy(frontend, backend, null, control);
            frontend.close();
            backend.close();
            control.close();
            ctx.close();
            result.set(true);
        }
    }

    @Test
    public void testProxy() throws Exception {
        String frontend = "tcp://localhost:" + (Utils.findOpenPort());
        String backend = "tcp://localhost:" + (Utils.findOpenPort());
        String controlEndpoint = "tcp://localhost:" + (Utils.findOpenPort());
        TestProxy.Proxy proxy = new TestProxy.Proxy(frontend, backend, controlEndpoint);
        proxy.start();
        ExecutorService executor = Executors.newFixedThreadPool(4);
        TestProxy.Dealer d1 = new TestProxy.Dealer("Dealer-A", backend);
        TestProxy.Dealer d2 = new TestProxy.Dealer("Dealer-B", backend);
        executor.submit(d1);
        executor.submit(d2);
        Thread.sleep(1000);
        TestProxy.Client c1 = new TestProxy.Client("Client-X", frontend);
        TestProxy.Client c2 = new TestProxy.Client("Client-Y", frontend);
        executor.submit(c1);
        executor.submit(c2);
        executor.shutdown();
        executor.awaitTermination(40, TimeUnit.SECONDS);
        Context ctx = ZMQ.context(1);
        Socket control = ctx.socket(PAIR);
        control.connect(controlEndpoint);
        control.send(PROXY_TERMINATE);
        proxy.join();
        control.close();
        ctx.close();
        Assert.assertThat(c1.result.get(), CoreMatchers.is(true));
        Assert.assertThat(c2.result.get(), CoreMatchers.is(true));
        Assert.assertThat(d1.result.get(), CoreMatchers.is(true));
        Assert.assertThat(d2.result.get(), CoreMatchers.is(true));
        Assert.assertThat(proxy.result.get(), CoreMatchers.is(true));
    }
}

