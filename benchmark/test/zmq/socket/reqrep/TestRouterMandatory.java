package zmq.socket.reqrep;


import ZError.EAGAIN;
import ZError.EHOSTUNREACH;
import ZMQ.ZMQ_DEALER;
import ZMQ.ZMQ_IDENTITY;
import ZMQ.ZMQ_LAST_ENDPOINT;
import ZMQ.ZMQ_RCVHWM;
import ZMQ.ZMQ_ROUTER;
import ZMQ.ZMQ_ROUTER_MANDATORY;
import ZMQ.ZMQ_SNDHWM;
import ZMQ.ZMQ_SNDMORE;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import zmq.Ctx;
import zmq.SocketBase;
import zmq.ZMQ;


public class TestRouterMandatory {
    @Test
    public void testRouterMandatory() throws Exception {
        int rc;
        boolean brc;
        Ctx ctx = ZMQ.init(1);
        Assert.assertThat(ctx, CoreMatchers.notNullValue());
        SocketBase sa = ZMQ.socket(ctx, ZMQ_ROUTER);
        ZMQ.setSocketOption(sa, ZMQ_SNDHWM, 1);
        Assert.assertThat(sa, CoreMatchers.notNullValue());
        brc = ZMQ.bind(sa, "tcp://127.0.0.1:*");
        Assert.assertThat(brc, CoreMatchers.is(true));
        // Sending a message to an unknown peer with the default setting
        rc = ZMQ.send(sa, "UNKNOWN", ZMQ_SNDMORE);
        Assert.assertThat(rc, CoreMatchers.is(7));
        rc = ZMQ.send(sa, "DATA", 0);
        Assert.assertThat(rc, CoreMatchers.is(4));
        int mandatory = 1;
        // Set mandatory routing on socket
        ZMQ.setSocketOption(sa, ZMQ_ROUTER_MANDATORY, mandatory);
        // Send a message and check that it fails
        rc = ZMQ.send(sa, "UNKNOWN", ((ZMQ.ZMQ_SNDMORE) | (ZMQ.ZMQ_DONTWAIT)));
        Assert.assertThat(rc, CoreMatchers.is((-1)));
        Assert.assertThat(sa.errno(), CoreMatchers.is(EHOSTUNREACH));
        // Create a valid socket
        SocketBase sb = ZMQ.socket(ctx, ZMQ_DEALER);
        Assert.assertThat(sb, CoreMatchers.notNullValue());
        ZMQ.setSocketOption(sb, ZMQ_RCVHWM, 1);
        ZMQ.setSocketOption(sb, ZMQ_IDENTITY, "X");
        String host = ((String) (ZMQ.getSocketOptionExt(sa, ZMQ_LAST_ENDPOINT)));
        Assert.assertThat(host, CoreMatchers.notNullValue());
        brc = ZMQ.connect(sb, host);
        // wait until connect
        Thread.sleep(1000);
        // make it full and check that it fails
        rc = ZMQ.send(sa, "X", ZMQ_SNDMORE);
        Assert.assertThat(rc, CoreMatchers.is(1));
        rc = ZMQ.send(sa, "DATA1", 0);
        Assert.assertThat(rc, CoreMatchers.is(5));
        rc = ZMQ.send(sa, "X", ((ZMQ.ZMQ_SNDMORE) | (ZMQ.ZMQ_DONTWAIT)));
        if (rc == 1) {
            // the first frame has been sent
            rc = ZMQ.send(sa, "DATA2", 0);
            Assert.assertThat(rc, CoreMatchers.is(5));
            // send more
            rc = ZMQ.send(sa, "X", ((ZMQ.ZMQ_SNDMORE) | (ZMQ.ZMQ_DONTWAIT)));
        }
        Assert.assertThat(rc, CoreMatchers.is((-1)));
        Assert.assertThat(sa.errno(), CoreMatchers.is(EAGAIN));
        // Clean up.
        ZMQ.close(sa);
        ZMQ.close(sb);
        ZMQ.term(ctx);
    }
}

