package org.zeromq;


import SocketType.PAIR;
import SocketType.PUB;
import SocketType.PULL;
import SocketType.PUSH;
import SocketType.REP;
import SocketType.REQ;
import SocketType.SUB;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.zeromq.ZMQ.Socket;


public class TestZContext {
    @Test(timeout = 5000)
    public void testZContext() {
        ZContext ctx = new ZContext();
        ctx.createSocket(PAIR);
        ctx.createSocket(REQ);
        ctx.createSocket(REP);
        ctx.createSocket(PUB);
        ctx.createSocket(SUB);
        ctx.close();
        Assert.assertThat(ctx.getSockets().isEmpty(), CoreMatchers.is(true));
    }

    @Test(timeout = 5000)
    public void testZContextSocketCloseBeforeContextClose() {
        ZContext ctx = new ZContext();
        Socket s1 = ctx.createSocket(PUSH);
        Socket s2 = ctx.createSocket(PULL);
        s1.close();
        s2.close();
        ctx.close();
    }

    @Test(timeout = 5000)
    public void testZContextLinger() {
        ZContext ctx = new ZContext();
        int linger = ctx.getLinger();
        Assert.assertThat(linger, CoreMatchers.is(0));
        final int newLinger = 1000;
        ctx.setLinger(newLinger);
        linger = ctx.getLinger();
        Assert.assertThat(linger, CoreMatchers.is(newLinger));
        ctx.close();
    }

    @Test(timeout = 5000)
    public void testConstruction() {
        ZContext ctx = new ZContext();
        Assert.assertThat(ctx, CoreMatchers.notNullValue());
        Assert.assertThat(ctx.getContext(), CoreMatchers.notNullValue());
        Assert.assertThat(ctx.isClosed(), CoreMatchers.is(false));
        Assert.assertThat(ctx.getIoThreads(), CoreMatchers.is(1));
        Assert.assertThat(ctx.getLinger(), CoreMatchers.is(0));
        Assert.assertThat(ctx.isMain(), CoreMatchers.is(true));
    }

    @SuppressWarnings("deprecation")
    @Test(timeout = 5000)
    public void testDestruction() {
        ZContext ctx = new ZContext();
        ctx.close();
        Assert.assertThat(ctx.isClosed(), CoreMatchers.is(true));
        Assert.assertThat(ctx.getSockets().isEmpty(), CoreMatchers.is(true));
        // Ensure context is not destroyed if not in main thread
        ZContext ctx1 = new ZContext();
        ctx1.setMain(false);
        @SuppressWarnings("unused")
        Socket s = ctx1.createSocket(PAIR);
        ctx1.close();
        Assert.assertThat(ctx1.getSockets().isEmpty(), CoreMatchers.is(true));
        Assert.assertThat(ctx1.getContext(), CoreMatchers.notNullValue());
    }

    @Test(timeout = 5000)
    public void testAddingSockets() throws ZMQException {
        ZContext ctx = new ZContext();
        try {
            Socket s = ctx.createSocket(PUB);
            Assert.assertThat(s, CoreMatchers.notNullValue());
            Assert.assertThat(s.getSocketType(), CoreMatchers.is(PUB));
            Socket s1 = ctx.createSocket(REQ);
            Assert.assertThat(s1, CoreMatchers.notNullValue());
            Assert.assertThat(s1.getSocketType(), CoreMatchers.is(REQ));
            Assert.assertThat(ctx.getSockets().size(), CoreMatchers.is(2));
        } finally {
            ctx.close();
        }
    }

    @Test(timeout = 5000)
    public void testRemovingSockets() throws ZMQException {
        ZContext ctx = new ZContext();
        try {
            Socket s = ctx.createSocket(PUB);
            Assert.assertThat(s, CoreMatchers.notNullValue());
            Assert.assertThat(ctx.getSockets().size(), CoreMatchers.is(1));
            ctx.destroySocket(s);
            Assert.assertThat(ctx.getSockets().size(), CoreMatchers.is(0));
        } finally {
            ctx.close();
        }
    }

    @SuppressWarnings("deprecation")
    @Test(timeout = 5000)
    public void testShadow() {
        ZContext ctx = new ZContext();
        Socket s = ctx.createSocket(PUB);
        Assert.assertThat(s, CoreMatchers.notNullValue());
        Assert.assertThat(ctx.getSockets().size(), CoreMatchers.is(1));
        ZContext shadowCtx = ZContext.shadow(ctx);
        shadowCtx.setMain(false);
        Assert.assertThat(shadowCtx.getSockets().size(), CoreMatchers.is(0));
        @SuppressWarnings("unused")
        Socket s1 = shadowCtx.createSocket(SUB);
        Assert.assertThat(shadowCtx.getSockets().size(), CoreMatchers.is(1));
        Assert.assertThat(ctx.getSockets().size(), CoreMatchers.is(1));
        shadowCtx.close();
        ctx.close();
    }

    @Test(timeout = 5000)
    public void testSeveralPendingInprocSocketsAreClosedIssue595() {
        ZContext ctx = new ZContext();
        for (SocketType type : SocketType.values()) {
            for (int idx = 0; idx < 3; ++idx) {
                Socket socket = ctx.createSocket(type);
                Assert.assertThat(socket, CoreMatchers.notNullValue());
                boolean rc = socket.connect(("inproc://" + (type.name())));
                Assert.assertThat(rc, CoreMatchers.is(true));
            }
        }
        ctx.close();
        Assert.assertThat(ctx.isClosed(), CoreMatchers.is(true));
    }
}

