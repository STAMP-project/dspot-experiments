package zmq;


import ZMQ.ZMQ_DEALER;
import ZMQ.ZMQ_MORE;
import ZMQ.ZMQ_ROUTER;
import ZMQ.ZMQ_SNDMORE;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class TestMsgFlags {
    // Create REQ/ROUTER wiring.
    @Test
    public void testMsgFlags() {
        Ctx ctx = ZMQ.init(1);
        Assert.assertThat(ctx, CoreMatchers.notNullValue());
        SocketBase sb = ZMQ.socket(ctx, ZMQ_ROUTER);
        Assert.assertThat(sb, CoreMatchers.notNullValue());
        boolean brc = ZMQ.bind(sb, "inproc://a");
        Assert.assertThat(brc, CoreMatchers.is(true));
        SocketBase sc = ZMQ.socket(ctx, ZMQ_DEALER);
        Assert.assertThat(sc, CoreMatchers.notNullValue());
        brc = ZMQ.connect(sc, "inproc://a");
        Assert.assertThat(brc, CoreMatchers.is(true));
        int rc;
        // Send 2-part message.
        rc = ZMQ.send(sc, "A", ZMQ_SNDMORE);
        Assert.assertThat(rc, CoreMatchers.is(1));
        rc = ZMQ.send(sc, "B", 0);
        Assert.assertThat(rc, CoreMatchers.is(1));
        // Identity comes first.
        Msg msg = ZMQ.recvMsg(sb, 0);
        int more = ZMQ.getMessageOption(msg, ZMQ_MORE);
        Assert.assertThat(more, CoreMatchers.is(1));
        // Then the first part of the message body.
        msg = ZMQ.recvMsg(sb, 0);
        Assert.assertThat(rc, CoreMatchers.is(1));
        more = ZMQ.getMessageOption(msg, ZMQ_MORE);
        Assert.assertThat(more, CoreMatchers.is(1));
        // And finally, the second part of the message body.
        msg = ZMQ.recvMsg(sb, 0);
        Assert.assertThat(rc, CoreMatchers.is(1));
        more = ZMQ.getMessageOption(msg, ZMQ_MORE);
        Assert.assertThat(more, CoreMatchers.is(0));
        // Tear down the wiring.
        ZMQ.close(sb);
        ZMQ.close(sc);
        ZMQ.term(ctx);
    }
}

