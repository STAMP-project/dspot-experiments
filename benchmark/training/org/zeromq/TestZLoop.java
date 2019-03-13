package org.zeromq;


import ZMQ.CHARSET;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.zeromq.ZLoop.IZLoopHandler;
import org.zeromq.ZMQ.PollItem;
import org.zeromq.ZMQ.Poller;
import org.zeromq.ZMQ.Socket;

import static ZMQ.CHARSET;


public class TestZLoop {
    private String received;

    private ZContext ctx;

    private Socket input;

    private Socket output;

    @Test
    public void testZLoopWithUDP() throws IOException {
        final int port = Utils.findOpenPort();
        final InetSocketAddress addr = new InetSocketAddress("127.0.0.1", port);
        ZLoop loop = new ZLoop(ctx);
        DatagramChannel udpIn = DatagramChannel.open();
        Assert.assertThat(udpIn, CoreMatchers.notNullValue());
        udpIn.configureBlocking(false);
        udpIn.bind(new InetSocketAddress(port));
        DatagramChannel udpOut = DatagramChannel.open();
        Assert.assertThat(udpOut, CoreMatchers.notNullValue());
        udpOut.configureBlocking(false);
        udpOut.connect(addr);
        final AtomicInteger counter = new AtomicInteger();
        final AtomicBoolean done = new AtomicBoolean();
        loop.addPoller(new PollItem(udpIn, Poller.POLLIN), new IZLoopHandler() {
            @Override
            public int handle(ZLoop loop, PollItem item, Object arg) {
                DatagramChannel udpIn = ((DatagramChannel) (arg));
                ByteBuffer bb = ByteBuffer.allocate(3);
                try {
                    udpIn.receive(bb);
                    String read = new String(bb.array(), 0, bb.limit(), CHARSET);
                    Assert.assertThat(read, CoreMatchers.is("udp"));
                    done.set(true);
                    counter.incrementAndGet();
                } catch (IOException e) {
                    e.printStackTrace();
                    Assert.fail();
                }
                return -1;
            }
        }, udpIn);
        loop.addPoller(new PollItem(udpOut, Poller.POLLOUT), new IZLoopHandler() {
            @Override
            public int handle(ZLoop loop, PollItem item, Object arg) {
                DatagramChannel udpOut = ((DatagramChannel) (arg));
                try {
                    ByteBuffer bb = ByteBuffer.allocate(3);
                    bb.put("udp".getBytes(CHARSET));
                    bb.flip();
                    int written = udpOut.send(bb, addr);
                    Assert.assertThat(written, CoreMatchers.is(3));
                    counter.incrementAndGet();
                } catch (IOException e) {
                    e.printStackTrace();
                    Assert.fail();
                }
                return 0;
            }
        }, udpOut);
        loop.start();
        Assert.assertThat(done.get(), CoreMatchers.is(true));
        Assert.assertThat(counter.get(), CoreMatchers.is(2));
        udpIn.close();
        udpOut.close();
    }

    @Test
    public void testZLoop() {
        int rc = 0;
        // setUp() should create the context
        assert (ctx) != null;
        ZLoop loop = new ZLoop(ctx);
        assert loop != null;
        ZLoop.IZLoopHandler timerEvent = new ZLoop.IZLoopHandler() {
            @Override
            public int handle(ZLoop loop, PollItem item, Object arg) {
                send("PING", 0);
                return 0;
            }
        };
        ZLoop.IZLoopHandler socketEvent = new ZLoop.IZLoopHandler() {
            @Override
            public int handle(ZLoop loop, PollItem item, Object arg) {
                received = recvStr(0);
                // Just end the reactor
                return -1;
            }
        };
        // After 10 msecs, send a ping message to output
        loop.addTimer(10, 1, timerEvent, input);
        // When we get the ping message, end the reactor
        PollItem pollInput = new PollItem(output, Poller.POLLIN);
        rc = loop.addPoller(pollInput, socketEvent, output);
        Assert.assertEquals(0, rc);
        loop.start();
        loop.removePoller(pollInput);
        Assert.assertEquals("PING", received);
    }

    @Test
    public void testZLoopAddTimerFromTimer() {
        int rc = 0;
        ZLoop loop = new ZLoop(ctx);
        assert loop != null;
        ZLoop.IZLoopHandler timerEvent = new ZLoop.IZLoopHandler() {
            @Override
            public int handle(ZLoop loop, PollItem item, Object arg) {
                final long now = System.currentTimeMillis();
                ZLoop.IZLoopHandler timerEvent2 = new ZLoop.IZLoopHandler() {
                    @Override
                    public int handle(ZLoop loop, PollItem item, Object arg) {
                        final long now2 = System.currentTimeMillis();
                        assert now2 >= (now + 10);
                        send("PING", 0);
                        return 0;
                    }
                };
                loop.addTimer(10, 1, timerEvent2, arg);
                return 0;
            }
        };
        ZLoop.IZLoopHandler socketEvent = new ZLoop.IZLoopHandler() {
            @Override
            public int handle(ZLoop loop, PollItem item, Object arg) {
                received = recvStr(0);
                // Just end the reactor
                return -1;
            }
        };
        // After 10 msecs, fire a timer that registers
        // another timer that sends the ping message
        loop.addTimer(10, 1, timerEvent, input);
        // When we get the ping message, end the reactor
        PollItem pollInput = new PollItem(output, Poller.POLLIN);
        rc = loop.addPoller(pollInput, socketEvent, output);
        Assert.assertEquals(0, rc);
        loop.start();
        loop.removePoller(pollInput);
        Assert.assertEquals("PING", received);
    }

    @Test(timeout = 1000)
    public void testZLoopAddTimerFromSocketHandler() {
        int rc = 0;
        ZLoop loop = new ZLoop(ctx);
        assert loop != null;
        ZLoop.IZLoopHandler timerEvent = new ZLoop.IZLoopHandler() {
            @Override
            public int handle(ZLoop loop, PollItem item, Object arg) {
                send("PING", 0);
                return 0;
            }
        };
        ZLoop.IZLoopHandler socketEvent = new ZLoop.IZLoopHandler() {
            @Override
            public int handle(ZLoop loop, PollItem item, Object arg) {
                final long now = System.currentTimeMillis();
                ZLoop.IZLoopHandler timerEvent2 = new ZLoop.IZLoopHandler() {
                    @Override
                    public int handle(ZLoop loop, PollItem item, Object arg) {
                        final long now2 = System.currentTimeMillis();
                        Assert.assertTrue((now2 >= (now + 10)));
                        received = recvStr(0);
                        // Just end the reactor
                        return -1;
                    }
                };
                // After 10 msec fire a timer that ends the reactor
                loop.addTimer(10, 1, timerEvent2, arg);
                return 0;
            }
        };
        // Fire a timer that sends the ping message
        loop.addTimer(0, 1, timerEvent, input);
        // When we get the ping message, end the reactor
        PollItem pollInput = new PollItem(output, Poller.POLLIN);
        rc = loop.addPoller(pollInput, socketEvent, output);
        Assert.assertEquals(0, rc);
        loop.start();
        loop.removePoller(pollInput);
        Assert.assertEquals("PING", received);
    }

    @Test(timeout = 1000)
    public void testZLoopEndReactorFromTimer() {
        int rc = 0;
        ZLoop loop = new ZLoop(ctx);
        assert loop != null;
        ZLoop.IZLoopHandler timerEvent = new ZLoop.IZLoopHandler() {
            @Override
            public int handle(ZLoop loop, PollItem item, Object arg) {
                send("PING", 0);
                return 0;
            }
        };
        ZLoop.IZLoopHandler socketEvent = new ZLoop.IZLoopHandler() {
            @Override
            public int handle(ZLoop loop, PollItem item, Object arg) {
                // After 10 msecs, fire an event that ends the reactor
                ZLoop.IZLoopHandler shutdownEvent = new ZLoop.IZLoopHandler() {
                    @Override
                    public int handle(ZLoop loop, PollItem item, Object arg) {
                        received = recvStr(0);
                        // Just end the reactor
                        return -1;
                    }
                };
                loop.addTimer(10, 1, shutdownEvent, arg);
                return 0;
            }
        };
        // Fire event that sends a ping message to output
        loop.addTimer(0, 1, timerEvent, input);
        // When we get the ping message, end the reactor
        PollItem pollInput = new PollItem(output, Poller.POLLIN);
        rc = loop.addPoller(pollInput, socketEvent, output);
        Assert.assertEquals(0, rc);
        loop.start();
        loop.removePoller(pollInput);
        Assert.assertEquals("PING", received);
    }
}

