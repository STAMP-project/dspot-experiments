package zmq.socket.reqrep;


import ZMQ.ZMQ_DEALER;
import ZMQ.ZMQ_DONTWAIT;
import ZMQ.ZMQ_IDENTITY;
import ZMQ.ZMQ_LAST_ENDPOINT;
import ZMQ.ZMQ_ROUTER;
import ZMQ.ZMQ_ROUTER_HANDOVER;
import ZMQ.ZMQ_SNDMORE;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import zmq.Ctx;
import zmq.Msg;
import zmq.SocketBase;
import zmq.ZMQ;


public class RouterHandoverTest {
    @Test
    public void testRouterHandover() throws Exception {
        int rc;
        boolean brc;
        Ctx ctx = ZMQ.init(1);
        Assert.assertThat(ctx, CoreMatchers.notNullValue());
        SocketBase router = ZMQ.socket(ctx, ZMQ_ROUTER);
        brc = ZMQ.bind(router, "tcp://127.0.0.1:*");
        Assert.assertThat(brc, CoreMatchers.is(true));
        // Enable the handover flag
        ZMQ.setSocketOption(router, ZMQ_ROUTER_HANDOVER, 1);
        Assert.assertThat(router, CoreMatchers.notNullValue());
        // Create dealer called "X" and connect it to our router
        SocketBase dealerOne = ZMQ.socket(ctx, ZMQ_DEALER);
        Assert.assertThat(dealerOne, CoreMatchers.notNullValue());
        ZMQ.setSocketOption(dealerOne, ZMQ_IDENTITY, "X");
        String host = ((String) (ZMQ.getSocketOptionExt(router, ZMQ_LAST_ENDPOINT)));
        Assert.assertThat(host, CoreMatchers.notNullValue());
        brc = ZMQ.connect(dealerOne, host);
        Assert.assertThat(brc, CoreMatchers.is(true));
        // Get message from dealer to know when connection is ready
        rc = ZMQ.send(dealerOne, "Hello", 0);
        Assert.assertThat(rc, CoreMatchers.is(5));
        Msg msg = ZMQ.recv(router, 0);
        Assert.assertThat(msg.size(), CoreMatchers.is(1));
        Assert.assertThat(new String(msg.data()), CoreMatchers.is("X"));
        msg = ZMQ.recv(router, 0);
        Assert.assertThat(msg.size(), CoreMatchers.is(5));
        // Now create a second dealer that uses the same identity
        SocketBase dealerTwo = ZMQ.socket(ctx, ZMQ_DEALER);
        Assert.assertThat(dealerTwo, CoreMatchers.notNullValue());
        ZMQ.setSocketOption(dealerTwo, ZMQ_IDENTITY, "X");
        brc = ZMQ.connect(dealerTwo, host);
        Assert.assertThat(brc, CoreMatchers.is(true));
        // Get message from dealer to know when connection is ready
        rc = ZMQ.send(dealerTwo, "Hello", 0);
        Assert.assertThat(rc, CoreMatchers.is(5));
        msg = ZMQ.recv(router, 0);
        Assert.assertThat(msg.size(), CoreMatchers.is(1));
        Assert.assertThat(new String(msg.data()), CoreMatchers.is("X"));
        msg = ZMQ.recv(router, 0);
        Assert.assertThat(msg.size(), CoreMatchers.is(5));
        // Send a message to 'X' identity. This should be delivered
        // to the second dealer, instead of the first because of the handover.
        rc = ZMQ.send(router, "X", ZMQ_SNDMORE);
        Assert.assertThat(rc, CoreMatchers.is(1));
        rc = ZMQ.send(router, "Hello", 0);
        Assert.assertThat(rc, CoreMatchers.is(5));
        // Ensure that the first dealer doesn't receive the message
        // but the second one does
        msg = ZMQ.recv(dealerOne, ZMQ_DONTWAIT);
        Assert.assertThat(msg, CoreMatchers.nullValue());
        msg = ZMQ.recv(dealerTwo, 0);
        Assert.assertThat(msg.size(), CoreMatchers.is(5));
        // Clean up.
        ZMQ.close(router);
        ZMQ.close(dealerOne);
        ZMQ.close(dealerTwo);
        ZMQ.term(ctx);
    }
}

