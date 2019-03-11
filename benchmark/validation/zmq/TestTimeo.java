package zmq;


import ZMQ.CHARSET;
import ZMQ.ZMQ_DONTWAIT;
import ZMQ.ZMQ_PULL;
import ZMQ.ZMQ_PUSH;
import ZMQ.ZMQ_RCVTIMEO;
import ZMQ.ZMQ_SNDTIMEO;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class TestTimeo {
    class Worker implements Runnable {
        Ctx ctx;

        Worker(Ctx ctx) {
            this.ctx = ctx;
        }

        @Override
        public void run() {
            ZMQ.sleep(1);
            SocketBase sc = ZMQ.socket(ctx, ZMQ_PUSH);
            Assert.assertThat(sc, CoreMatchers.notNullValue());
            boolean rc = ZMQ.connect(sc, "inproc://timeout_test");
            Assert.assertThat(rc, CoreMatchers.is(true));
            ZMQ.sleep(1);
            ZMQ.close(sc);
        }
    }

    @Test
    public void testTimeo() throws Exception {
        Ctx ctx = ZMQ.init(1);
        Assert.assertThat(ctx, CoreMatchers.notNullValue());
        SocketBase sb = ZMQ.socket(ctx, ZMQ_PULL);
        Assert.assertThat(sb, CoreMatchers.notNullValue());
        boolean rc = ZMQ.bind(sb, "inproc://timeout_test");
        Assert.assertThat(rc, CoreMatchers.is(true));
        // Check whether non-blocking recv returns immediately.
        Msg msg = ZMQ.recv(sb, ZMQ_DONTWAIT);
        Assert.assertThat(msg, CoreMatchers.nullValue());
        // Check whether recv timeout is honoured.
        int timeout = 500;
        ZMQ.setSocketOption(sb, ZMQ_RCVTIMEO, timeout);
        long watch = ZMQ.startStopwatch();
        msg = ZMQ.recv(sb, 0);
        Assert.assertThat(msg, CoreMatchers.nullValue());
        long elapsed = ZMQ.stopStopwatch(watch);
        Assert.assertThat(((elapsed > 440000) && (elapsed < 550000)), CoreMatchers.is(true));
        // Check whether connection during the wait doesn't distort the timeout.
        timeout = 2000;
        ZMQ.setSocketOption(sb, ZMQ_RCVTIMEO, timeout);
        Thread thread = new Thread(new TestTimeo.Worker(ctx));
        thread.start();
        watch = ZMQ.startStopwatch();
        msg = ZMQ.recv(sb, 0);
        Assert.assertThat(msg, CoreMatchers.nullValue());
        elapsed = ZMQ.stopStopwatch(watch);
        Assert.assertThat(((elapsed > 1900000) && (elapsed < 2100000)), CoreMatchers.is(true));
        thread.join();
        // Check that timeouts don't break normal message transfer.
        SocketBase sc = ZMQ.socket(ctx, ZMQ_PUSH);
        Assert.assertThat(sc, CoreMatchers.notNullValue());
        ZMQ.setSocketOption(sb, ZMQ_RCVTIMEO, timeout);
        ZMQ.setSocketOption(sb, ZMQ_SNDTIMEO, timeout);
        rc = ZMQ.connect(sc, "inproc://timeout_test");
        Assert.assertThat(rc, CoreMatchers.is(true));
        Msg smsg = new Msg("12345678ABCDEFGH12345678abcdefgh".getBytes(CHARSET));
        int r = ZMQ.send(sc, smsg, 0);
        Assert.assertThat(r, CoreMatchers.is(32));
        msg = ZMQ.recv(sb, 0);
        Assert.assertThat(msg.size(), CoreMatchers.is(32));
        ZMQ.close(sc);
        ZMQ.close(sb);
        ZMQ.term(ctx);
    }
}

