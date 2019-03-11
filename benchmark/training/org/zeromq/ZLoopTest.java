/**
 * import org.junit.Assert;
 */
package org.zeromq;


import ZLoop.IZLoopHandler;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.zeromq.ZMQ.PollItem;
import org.zeromq.ZMQ.Poller;
import org.zeromq.ZMQ.Socket;


public class ZLoopTest {
    private String received;

    private ZContext ctx;

    private Socket input;

    private Socket output;

    @Test
    public void testZLoop() {
        int rc = 0;
        ZLoop loop = new ZLoop(ctx);
        Assert.assertThat(loop, CoreMatchers.notNullValue());
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
        Assert.assertThat(rc, CoreMatchers.is(0));
        loop.start();
        loop.removePoller(pollInput);
        Assert.assertThat(received, CoreMatchers.is("PING"));
    }

    @Test
    public void testZLoopAddTimerFromTimer() {
        int rc = 0;
        ZLoop loop = new ZLoop(ctx);
        Assert.assertThat(loop, CoreMatchers.notNullValue());
        ZLoop.IZLoopHandler timerEvent = new ZLoop.IZLoopHandler() {
            @Override
            public int handle(ZLoop loop, PollItem item, Object arg) {
                final long now = System.currentTimeMillis();
                ZLoop.IZLoopHandler timerEvent2 = new ZLoop.IZLoopHandler() {
                    @Override
                    public int handle(ZLoop loop, PollItem item, Object arg) {
                        final long now2 = System.currentTimeMillis();
                        Assert.assertThat((now2 >= (now + 10)), CoreMatchers.is(true));
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
        Assert.assertThat(rc, CoreMatchers.is(0));
        loop.start();
        loop.removePoller(pollInput);
        Assert.assertThat(received, CoreMatchers.is("PING"));
    }

    @Test(timeout = 1000)
    public void testZLoopAddTimerFromSocketHandler() {
        int rc = 0;
        ZLoop loop = new ZLoop(ctx);
        Assert.assertThat(loop, CoreMatchers.notNullValue());
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
                        Assert.assertThat((now2 >= (now + 10)), CoreMatchers.is(true));
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
        Assert.assertThat(rc, CoreMatchers.is(0));
        loop.start();
        loop.removePoller(pollInput);
        Assert.assertThat(received, CoreMatchers.is("PING"));
    }

    @Test(timeout = 1000)
    public void testZLoopEndReactorFromTimer() {
        int rc = 0;
        ZLoop loop = new ZLoop(ctx);
        Assert.assertThat(loop, CoreMatchers.notNullValue());
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
        Assert.assertThat(rc, CoreMatchers.is(0));
        loop.start();
        loop.removePoller(pollInput);
        Assert.assertThat(received, CoreMatchers.is("PING"));
    }
}

