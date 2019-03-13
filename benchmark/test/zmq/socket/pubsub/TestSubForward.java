package zmq.socket.pubsub;


import ZMQ.ZMQ_PUB;
import ZMQ.ZMQ_SUB;
import ZMQ.ZMQ_SUBSCRIBE;
import ZMQ.ZMQ_XPUB;
import ZMQ.ZMQ_XSUB;
import java.io.IOException;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import zmq.Ctx;
import zmq.Msg;
import zmq.SocketBase;
import zmq.ZMQ;
import zmq.util.Utils;


public class TestSubForward {
    // Create REQ/ROUTER wiring.
    @Test
    public void testSubForward() throws IOException {
        int port1 = Utils.findOpenPort();
        int port2 = Utils.findOpenPort();
        Ctx ctx = ZMQ.init(1);
        Assert.assertThat(ctx, CoreMatchers.notNullValue());
        // First, create an intermediate device
        SocketBase xpubBind = ZMQ.socket(ctx, ZMQ_XPUB);
        Assert.assertThat(xpubBind, CoreMatchers.notNullValue());
        boolean rc = ZMQ.bind(xpubBind, ("tcp://127.0.0.1:" + port1));
        SocketBase xsubBind = ZMQ.socket(ctx, ZMQ_XSUB);
        Assert.assertThat(xsubBind, CoreMatchers.notNullValue());
        rc = ZMQ.bind(xsubBind, ("tcp://127.0.0.1:" + port2));
        Assert.assertThat(rc, CoreMatchers.is(true));
        // Create a publisher
        SocketBase pubConnect = ZMQ.socket(ctx, ZMQ_PUB);
        Assert.assertThat(pubConnect, CoreMatchers.notNullValue());
        rc = ZMQ.connect(pubConnect, ("tcp://127.0.0.1:" + port2));
        Assert.assertThat(rc, CoreMatchers.is(true));
        // Create a subscriber
        SocketBase subConnect = ZMQ.socket(ctx, ZMQ_SUB);
        Assert.assertThat(subConnect, CoreMatchers.notNullValue());
        rc = ZMQ.connect(subConnect, ("tcp://127.0.0.1:" + port1));
        Assert.assertThat(rc, CoreMatchers.is(true));
        // Subscribe for all messages.
        ZMQ.setSocketOption(subConnect, ZMQ_SUBSCRIBE, "");
        ZMQ.sleep(1);
        // Pass the subscription upstream through the device
        Msg msg = ZMQ.recv(xpubBind, 0);
        Assert.assertThat(msg, CoreMatchers.notNullValue());
        int n = ZMQ.send(xsubBind, msg, 0);
        Assert.assertThat(n, CoreMatchers.not(0));
        // Wait a bit till the subscription gets to the publisher
        ZMQ.sleep(1);
        // Send an empty message
        n = ZMQ.send(pubConnect, null, 0, 0);
        Assert.assertThat(n, CoreMatchers.is(0));
        // Pass the message downstream through the device
        msg = ZMQ.recv(xsubBind, 0);
        Assert.assertThat(msg, CoreMatchers.notNullValue());
        n = ZMQ.send(xpubBind, msg, 0);
        Assert.assertThat(n, CoreMatchers.is(0));
        // Receive the message in the subscriber
        msg = ZMQ.recv(subConnect, 0);
        Assert.assertThat(msg, CoreMatchers.notNullValue());
        // Tear down the wiring.
        ZMQ.close(xpubBind);
        ZMQ.close(xsubBind);
        ZMQ.close(pubConnect);
        ZMQ.close(subConnect);
        ZMQ.term(ctx);
    }
}

