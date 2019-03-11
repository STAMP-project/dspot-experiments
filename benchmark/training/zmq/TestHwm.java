package zmq;


import ZMQ.ZMQ_DONTWAIT;
import ZMQ.ZMQ_PULL;
import ZMQ.ZMQ_PUSH;
import ZMQ.ZMQ_RCVHWM;
import ZMQ.ZMQ_SNDHWM;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class TestHwm {
    @Test
    public void testHwm() {
        Ctx ctx = ZMQ.init(1);
        Assert.assertThat(ctx, CoreMatchers.notNullValue());
        int rc = 0;
        boolean brc = false;
        // Create pair of socket, each with high watermark of 2. Thus the total
        // buffer space should be 4 messages.
        SocketBase sb = ZMQ.socket(ctx, ZMQ_PULL);
        Assert.assertThat(sb, CoreMatchers.notNullValue());
        int hwm = 2;
        ZMQ.setSocketOption(sb, ZMQ_RCVHWM, hwm);
        brc = ZMQ.bind(sb, "inproc://a");
        Assert.assertThat(brc, CoreMatchers.is(true));
        SocketBase sc = ZMQ.socket(ctx, ZMQ_PUSH);
        Assert.assertThat(sc, CoreMatchers.notNullValue());
        ZMQ.setSocketOption(sc, ZMQ_SNDHWM, hwm);
        brc = ZMQ.connect(sc, "inproc://a");
        Assert.assertThat(brc, CoreMatchers.is(true));
        // Try to send 10 messages. Only 4 should succeed.
        for (int i = 0; i < 10; i++) {
            rc = ZMQ.send(sc, null, 0, ZMQ_DONTWAIT);
            if (i < 4) {
                Assert.assertThat(rc, CoreMatchers.is(0));
            } else {
                Assert.assertThat(rc, CoreMatchers.is((-1)));
            }
        }
        Msg m;
        // There should be now 4 messages pending, consume them.
        for (int i = 0; i != 4; i++) {
            m = ZMQ.recv(sb, 0);
            Assert.assertThat(m, CoreMatchers.notNullValue());
            Assert.assertThat(m.size(), CoreMatchers.is(0));
        }
        // Now it should be possible to send one more.
        rc = ZMQ.send(sc, null, 0, 0);
        Assert.assertThat(rc, CoreMatchers.is(0));
        // Consume the remaining message.
        m = ZMQ.recv(sb, 0);
        Assert.assertThat(rc, CoreMatchers.notNullValue());
        Assert.assertThat(m.size(), CoreMatchers.is(0));
        ZMQ.close(sc);
        ZMQ.close(sb);
        ZMQ.term(ctx);
    }
}

