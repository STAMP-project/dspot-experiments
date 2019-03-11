package org.zeromq;


import java.util.concurrent.CountDownLatch;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.zeromq.ZMQ.Socket;


public class TestZThread {
    @Test
    public void testDetached() {
        final CountDownLatch stopped = new CountDownLatch(1);
        ZThread.start(( args) -> {
            try (ZContext ctx = new ZContext()) {
                Socket push = ctx.createSocket(SocketType.PUSH);
                assertThat(push, notNullValue());
            }
            stopped.countDown();
        });
        try {
            stopped.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            e.printStackTrace();
        }
    }

    @Test
    public void testFork() {
        final ZContext ctx = new ZContext();
        Socket pipe = ZThread.fork(ctx, ( args, ctx1, pipe1) -> {
            // Create a socket to check it'll be automatically deleted
            ctx1.createSocket(SocketType.PUSH);
            pipe1.recvStr();
            pipe1.send("pong");
        });
        Assert.assertThat(pipe, CoreMatchers.notNullValue());
        pipe.send("ping");
        String pong = pipe.recvStr();
        Assert.assertThat(pong, CoreMatchers.is("pong"));
        // Everything should be cleanly closed now
        ctx.close();
    }
}

