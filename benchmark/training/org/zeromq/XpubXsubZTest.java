package org.zeromq;


import SocketType.PAIR;
import SocketType.PUB;
import SocketType.XPUB;
import SocketType.XSUB;
import ZMQ.PROXY_TERMINATE;
import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.zeromq.ZMQ.Socket;
import org.zeromq.ZProxy.Plug;
import zmq.util.Utils;


public class XpubXsubZTest {
    @Test
    public void testIssue476() throws IOException, InterruptedException, ExecutionException {
        final int front = Utils.findOpenPort();
        final int back = Utils.findOpenPort();
        final int max = 10;
        ExecutorService service = Executors.newFixedThreadPool(3);
        final ZContext ctx = new ZContext();
        service.submit(new Runnable() {
            @Override
            public void run() {
                Thread.currentThread().setName("Proxy");
                ZMQ.Socket xpub = ctx.createSocket(XPUB);
                xpub.bind(("tcp://*:" + back));
                ZMQ.Socket xsub = ctx.createSocket(XSUB);
                xsub.bind(("tcp://*:" + front));
                ZMQ.Socket ctrl = ctx.createSocket(PAIR);
                ctrl.bind("inproc://ctrl-proxy");
                ZMQ.proxy(xpub, xsub, null, ctrl);
            }
        });
        final AtomicReference<Throwable> error = testIssue476(front, back, max, service, ctx);
        ZMQ.Socket ctrl = ctx.createSocket(PAIR);
        ctrl.connect("inproc://ctrl-proxy");
        ctrl.send(PROXY_TERMINATE);
        ctrl.close();
        service.shutdown();
        service.awaitTermination(2, TimeUnit.SECONDS);
        Assert.assertThat(error.get(), CoreMatchers.nullValue());
        ctx.close();
    }

    @SuppressWarnings("deprecation")
    @Test
    public void testIssue476WithZProxy() throws IOException, InterruptedException, ExecutionException {
        final int front = Utils.findOpenPort();
        final int back = Utils.findOpenPort();
        final int max = 10;
        ExecutorService service = Executors.newFixedThreadPool(3);
        final ZContext ctx = new ZContext();
        ZProxy.Proxy actor = new ZProxy.Proxy.SimpleProxy() {
            @Override
            public Socket create(ZContext ctx, Plug place, Object... args) {
                if (place == (Plug.FRONT)) {
                    return ctx.createSocket(XSUB);
                }
                if (place == (Plug.BACK)) {
                    return ctx.createSocket(XPUB);
                }
                if (place == (Plug.CAPTURE)) {
                    return ctx.createSocket(PUB);
                }
                return null;
            }

            @Override
            public boolean configure(Socket socket, Plug place, Object... args) throws IOException {
                if (place == (Plug.FRONT)) {
                    return socket.bind(("tcp://*:" + front));
                }
                if (place == (Plug.BACK)) {
                    return socket.bind(("tcp://*:" + back));
                }
                return true;
            }
        };
        ZProxy proxy = ZProxy.newZProxy(ctx, null, actor, UUID.randomUUID().toString());
        proxy.start(true);
        final AtomicReference<Throwable> error = testIssue476(front, back, max, service, ctx);
        proxy.exit(false);
        service.shutdown();
        service.awaitTermination(2, TimeUnit.SECONDS);
        Assert.assertThat(error.get(), CoreMatchers.nullValue());
        ctx.close();
    }

    @SuppressWarnings("deprecation")
    @Test
    public void testIssue476WithZProxyZmqPump() throws IOException, InterruptedException, ExecutionException {
        final int front = Utils.findOpenPort();
        final int back = Utils.findOpenPort();
        final int max = 10;
        ExecutorService service = Executors.newFixedThreadPool(3);
        final ZContext ctx = new ZContext();
        ZProxy.Proxy actor = new ZProxy.Proxy.SimpleProxy() {
            @Override
            public Socket create(ZContext ctx, Plug place, Object... args) {
                if (place == (Plug.FRONT)) {
                    return ctx.createSocket(XSUB);
                }
                if (place == (Plug.BACK)) {
                    return ctx.createSocket(XPUB);
                }
                if (place == (Plug.CAPTURE)) {
                    return ctx.createSocket(PUB);
                }
                return null;
            }

            @Override
            public boolean configure(Socket socket, Plug place, Object... args) throws IOException {
                if (place == (Plug.FRONT)) {
                    return socket.bind(("tcp://*:" + front));
                }
                if (place == (Plug.BACK)) {
                    return socket.bind(("tcp://*:" + back));
                }
                return true;
            }
        };
        ZProxy proxy = ZProxy.newProxy(ctx, "XPub-XSub", actor, UUID.randomUUID().toString());
        proxy.start(true);
        final AtomicReference<Throwable> error = testIssue476(front, back, max, service, ctx);
        proxy.exit(false);
        service.shutdown();
        service.awaitTermination(2, TimeUnit.SECONDS);
        Assert.assertThat(error.get(), CoreMatchers.nullValue());
        ctx.close();
    }
}

