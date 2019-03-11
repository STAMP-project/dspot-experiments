package org.zeromq;


import SocketType.PULL;
import SocketType.PUSH;
import SocketType.ROUTER;
import SocketType.XPUB;
import ZPoller.ERR;
import ZPoller.IN;
import ZPoller.OUT;
import ZPoller.WRITABLE;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.Pipe;
import java.nio.channels.SelectableChannel;
import java.nio.channels.Selector;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.zeromq.ZMQ.Socket;
import org.zeromq.ZPoller.EventsHandler;
import org.zeromq.ZPoller.ItemCreator;
import org.zeromq.ZPoller.ItemHolder;

import static ZPoller.IN;


public class TestZPoller {
    private final class EventsHandlerCounter implements EventsHandler {
        private final AtomicInteger count;

        private EventsHandlerCounter(AtomicInteger count) {
            this.count = count;
        }

        @Override
        public boolean events(Socket socket, int events) {
            throw new UnsupportedOperationException("Not registred for Socket");
        }

        @Override
        public boolean events(SelectableChannel channel, int events) {
            Assert.assertThat(((events & (IN)) > 0), CoreMatchers.is(true));
            try {
                Pipe.SourceChannel sc = ((Pipe.SourceChannel) (channel));
                sc.read(ByteBuffer.allocate(5));
                count.incrementAndGet();
                return true;
            } catch (IOException e) {
                return false;
            }
        }
    }

    private final class EventsHandlerErrorCounter implements EventsHandler {
        private final AtomicInteger error;

        private EventsHandlerErrorCounter(AtomicInteger error) {
            this.error = error;
        }

        @Override
        public boolean events(Socket socket, int events) {
            throw new UnsupportedOperationException("Not registred for Socket");
        }

        @Override
        public boolean events(SelectableChannel channel, int events) {
            error.incrementAndGet();
            return false;
        }
    }

    private static class EventsHandlerAdapter implements EventsHandler {
        @Override
        public boolean events(SelectableChannel channel, int events) {
            return false;
        }

        @Override
        public boolean events(Socket socket, int events) {
            return false;
        }
    }

    static class Server extends Thread {
        private final int port;

        private final Socket socket;

        private final ZPoller poller;

        public Server(ZContext context, int port) {
            this.port = port;
            socket = context.createSocket(PUSH);
            poller = new ZPoller(context);
        }

        @Override
        public void run() {
            socket.bind(("tcp://127.0.0.1:" + (port)));
            poller.register(socket, WRITABLE);
            while (!(Thread.currentThread().isInterrupted())) {
                poller.poll((-1));
                if (poller.isWritable(socket)) {
                    ZMsg msg = new ZMsg();
                    msg.add("OK");
                    msg.send(socket);
                    break;
                } else {
                    Assert.fail("unable to get server socket in writable state");
                }
            } 
            socket.close();
            try {
                poller.close();
            } catch (IOException e) {
                e.printStackTrace();
                Assert.fail(("error while closing poller " + (e.getMessage())));
            }
        }
    }

    @Test
    public void testPollerPollout() throws IOException, InterruptedException {
        final int port = Utils.findOpenPort();
        final ZContext context = new ZContext();
        final ZPoller poller = new ZPoller(context);
        final ZMQ.Socket receiver = context.createSocket(PULL);
        final TestZPoller.Server client = new TestZPoller.Server(context, port);
        client.start();
        try {
            receiver.connect(("tcp://127.0.0.1:" + port));
            final AtomicReference<ZMsg> msg = new AtomicReference<>();
            poller.register(receiver, new TestZPoller.EventsHandlerAdapter() {
                @Override
                public boolean events(Socket s, int events) {
                    if (receiver.equals(s)) {
                        msg.set(ZMsg.recvMsg(receiver));
                        return false;
                    } else {
                        return true;
                    }
                }
            }, IN);
            int maxAttempts = 100;
            while ((!(Thread.currentThread().isInterrupted())) && ((maxAttempts--) > 0)) {
                int rc = poller.poll(1000);
                if (rc < 0) {
                    break;
                }
            } 
            client.join();
            Assert.assertThat("unable to receive msg after several cycles", msg, CoreMatchers.notNullValue());
        } finally {
            receiver.close();
            context.close();
            poller.close();
        }
    }

    @Test
    public void testUseNull() throws IOException {
        final ZContext context = new ZContext();
        ZPoller poller = new ZPoller(context);
        SelectableChannel channel = null;
        Socket socket = null;// ctx.createSocket(ZMQ.SUB);

        boolean rc = false;
        rc = poller.register(socket, IN);
        Assert.assertThat("Registering a null socket was successful", rc, CoreMatchers.is(false));
        rc = poller.register(channel, OUT);
        Assert.assertThat("Registering a null channel was successful", rc, CoreMatchers.is(false));
        int events = poller.poll(10);
        Assert.assertThat("reading event on without sockets", events, CoreMatchers.is(0));
        rc = poller.isReadable(socket);
        Assert.assertThat("checking read event on a null socket was successful", rc, CoreMatchers.is(false));
        rc = poller.writable(socket);
        Assert.assertThat("checking write event on a null socket was successful", rc, CoreMatchers.is(false));
        rc = poller.readable(channel);
        Assert.assertThat("checking read event on a null channel was successful", rc, CoreMatchers.is(false));
        rc = poller.isWritable(channel);
        Assert.assertThat("checking write event on a null channel was successful", rc, CoreMatchers.is(false));
        EventsHandler global = null;
        poller.setGlobalHandler(global);
        EventsHandler handler = null;
        rc = poller.register(socket, handler, ERR);
        Assert.assertThat("Register with handler on a null socket was successful", rc, CoreMatchers.is(false));
        rc = poller.register(channel, ERR);
        Assert.assertThat("Register with handler on a null channel was successful", rc, CoreMatchers.is(false));
        events = poller.poll(10);
        Assert.assertThat("reading event with events handlers without sockets", events, CoreMatchers.is(0));
        poller.close();
        context.close();
    }

    @Test
    public void testZPollerNew() throws IOException {
        ZContext ctx = new ZContext();
        ZPoller poller = new ZPoller(ctx);
        try {
            ZPoller other = new ZPoller(poller);
            other.close();
            ItemCreator itemCreator = new ZPoller.SimpleCreator();
            other = new ZPoller(itemCreator, poller);
            other.close();
        } finally {
            poller.close();
            ctx.close();
        }
    }

    @Test
    public void testGlobalHandler() throws IOException {
        ZContext ctx = new ZContext();
        ZPoller poller = new ZPoller(ctx);
        try {
            Assert.assertThat(poller.getGlobalHandler(), CoreMatchers.nullValue());
            EventsHandler handler = new TestZPoller.EventsHandlerAdapter();
            poller.setGlobalHandler(handler);
            Assert.assertThat(poller.getGlobalHandler(), CoreMatchers.is(handler));
        } finally {
            poller.close();
            ctx.close();
        }
    }

    @Test
    public void testItemEqualsBasic() throws IOException {
        ZContext ctx = new ZContext();
        ItemCreator itemCreator = new ZPoller.SimpleCreator();
        ZPoller poller = new ZPoller(itemCreator, ctx);
        try {
            Socket socket = ctx.createSocket(ROUTER);
            ItemHolder holder = itemCreator.create(socket, null, 0);
            Assert.assertThat(holder, CoreMatchers.is(holder));
            Assert.assertThat(holder, CoreMatchers.is(CoreMatchers.not(CoreMatchers.equalTo(null))));
            Assert.assertThat(holder.equals(""), CoreMatchers.is(false));
        } finally {
            poller.close();
            ctx.close();
        }
    }

    @Test
    public void testItemEquals() throws IOException {
        ZContext ctx = new ZContext();
        ItemCreator itemCreator = new ZPoller.SimpleCreator();
        ZPoller poller = new ZPoller(itemCreator, ctx);
        try {
            Socket socket = ctx.createSocket(ROUTER);
            ItemHolder holder = poller.create(socket, null, 0);
            ItemHolder other = new ZPoller.ZPollItem(socket, null, 0);
            Assert.assertThat(other, CoreMatchers.is(CoreMatchers.equalTo(holder)));
            other = new ZPoller.ZPollItem(socket, new TestZPoller.EventsHandlerAdapter(), 0);
            Assert.assertThat(other, CoreMatchers.is(CoreMatchers.not(CoreMatchers.equalTo(holder))));
            socket = ctx.createSocket(ROUTER);
            other = new ZPoller.ZPollItem(socket, null, 0);
            Assert.assertThat(other, CoreMatchers.is(CoreMatchers.not(CoreMatchers.equalTo(holder))));
            other = itemCreator.create(((SelectableChannel) (null)), null, 0);
            Assert.assertThat(other, CoreMatchers.is(CoreMatchers.not(CoreMatchers.equalTo(holder))));
            holder = poller.create(socket, new TestZPoller.EventsHandlerAdapter(), 0);
            other = new ZPoller.ZPollItem(socket, new TestZPoller.EventsHandlerAdapter(), 0);
            Assert.assertThat(other, CoreMatchers.is(CoreMatchers.not(CoreMatchers.equalTo(holder))));
        } finally {
            poller.close();
            ctx.close();
        }
    }

    @Test
    public void testReadable() throws IOException {
        ZContext ctx = new ZContext();
        ZPoller poller = new ZPoller(ctx);
        try {
            Socket socket = ctx.createSocket(XPUB);
            poller.register(socket, new TestZPoller.EventsHandlerAdapter());
            boolean rc = poller.readable(socket);
            Assert.assertThat(rc, CoreMatchers.is(false));
            rc = poller.isReadable(socket);
            Assert.assertThat(rc, CoreMatchers.is(false));
            rc = poller.pollin(socket);
            Assert.assertThat(rc, CoreMatchers.is(false));
        } finally {
            poller.close();
            ctx.close();
        }
    }

    @Test
    public void testWritable() throws IOException {
        ZContext ctx = new ZContext();
        ZPoller poller = new ZPoller(ctx);
        try {
            Socket socket = ctx.createSocket(XPUB);
            poller.register(socket, OUT);
            boolean rc = poller.writable(socket);
            Assert.assertThat(rc, CoreMatchers.is(false));
            rc = poller.isWritable(socket);
            Assert.assertThat(rc, CoreMatchers.is(false));
            rc = poller.pollout(socket);
            Assert.assertThat(rc, CoreMatchers.is(false));
        } finally {
            poller.close();
            ctx.close();
        }
    }

    @Test
    public void testError() throws IOException {
        ZContext ctx = new ZContext();
        ZPoller poller = new ZPoller(ctx);
        try {
            Socket socket = ctx.createSocket(XPUB);
            poller.register(socket, ERR);
            boolean rc = poller.error(socket);
            Assert.assertThat(rc, CoreMatchers.is(false));
            rc = poller.isError(socket);
            Assert.assertThat(rc, CoreMatchers.is(false));
            rc = poller.pollerr(socket);
            Assert.assertThat(rc, CoreMatchers.is(false));
        } finally {
            poller.close();
            ctx.close();
        }
    }

    @Test
    public void testRegister() throws IOException {
        ZContext ctx = new ZContext();
        ZPoller poller = new ZPoller(ctx);
        try {
            Socket socket = ctx.createSocket(XPUB);
            ItemHolder holder = poller.create(socket, null, 0);
            boolean rc = poller.register(holder);
            Assert.assertThat(rc, CoreMatchers.is(true));
        } finally {
            poller.close();
            ctx.close();
        }
    }

    @Test
    public void testItems() throws IOException {
        ZContext ctx = new ZContext();
        ZPoller poller = new ZPoller(ctx);
        try {
            Socket socket = ctx.createSocket(XPUB);
            Iterable<ItemHolder> items = poller.items(null);
            Assert.assertThat(items, CoreMatchers.notNullValue());
            ItemHolder holder = poller.create(socket, null, 0);
            poller.register(holder);
            items = poller.items(socket);
            Assert.assertThat(items, CoreMatchers.hasItem(holder));
        } finally {
            poller.close();
            ctx.close();
        }
    }

    @Test
    public void testMultipleRegistrations() throws IOException {
        Selector selector = Selector.open();
        AtomicInteger count = new AtomicInteger();
        AtomicInteger error = new AtomicInteger();
        EventsHandler cb1 = new TestZPoller.EventsHandlerCounter(count);
        EventsHandler cb2 = new TestZPoller.EventsHandlerCounter(count);
        EventsHandler cberr = new TestZPoller.EventsHandlerErrorCounter(error);
        Pipe[] pipes = new Pipe[2];
        try (ZPoller poller = new ZPoller(selector)) {
            pipes[0] = pipe(poller, cberr, cb1, cb2);
            pipes[1] = pipe(poller, cberr, cb1, cb2);
            int max = 10;
            do {
                poller.poll(100);
                --max;
            } while (((count.get()) != 4) && (max > 0) );
            Assert.assertThat("Not all events handlers checked", error.get(), CoreMatchers.is(CoreMatchers.equalTo(0)));
            if ((max == 0) && ((count.get()) != 4)) {
                Assert.fail("Unable to poll after 10 attempts");
            }
        } finally {
            selector.close();
            for (Pipe pipe : pipes) {
                pipe.sink().close();
                pipe.source().close();
            }
        }
    }
}

