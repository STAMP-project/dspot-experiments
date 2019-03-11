package zmq.socket.pubsub;


import ZError.EAGAIN;
import ZMQ.ZMQ_DONTWAIT;
import ZMQ.ZMQ_PUB;
import ZMQ.ZMQ_SNDHWM;
import ZMQ.ZMQ_SNDTIMEO;
import ZMQ.ZMQ_SUB;
import ZMQ.ZMQ_SUBSCRIBE;
import ZMQ.ZMQ_XPUB_NODROP;
import java.io.IOException;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import zmq.Ctx;
import zmq.Msg;
import zmq.SocketBase;
import zmq.ZMQ;


public class XPubNodropTest {
    // Create REQ/ROUTER wiring.
    @Test
    public void testXpubNoDrop() throws IOException {
        Ctx ctx = ZMQ.init(1);
        Assert.assertThat(ctx, CoreMatchers.notNullValue());
        // Create a publisher
        SocketBase pubBind = ZMQ.socket(ctx, ZMQ_PUB);
        Assert.assertThat(pubBind, CoreMatchers.notNullValue());
        boolean rc = ZMQ.bind(pubBind, "inproc://soname");
        Assert.assertThat(rc, CoreMatchers.is(true));
        // set pub socket options
        ZMQ.setSocketOption(pubBind, ZMQ_XPUB_NODROP, 1);
        int hwm = 2000;
        ZMQ.setSocketOption(pubBind, ZMQ_SNDHWM, hwm);
        // Create a subscriber
        SocketBase subConnect = ZMQ.socket(ctx, ZMQ_SUB);
        Assert.assertThat(subConnect, CoreMatchers.notNullValue());
        rc = ZMQ.connect(subConnect, "inproc://soname");
        Assert.assertThat(rc, CoreMatchers.is(true));
        // Subscribe for all messages.
        ZMQ.setSocketOption(subConnect, ZMQ_SUBSCRIBE, "");
        int hwmlimit = hwm - 1;
        int sendCount = 0;
        // Send an empty message
        for (int i = 0; i < hwmlimit; i++) {
            int ret = ZMQ.send(pubBind, "", 0);
            assert ret == 0;
            sendCount++;
        }
        int recvCount = 0;
        Msg msg = null;
        do {
            // Receive the message in the subscriber
            msg = ZMQ.recv(subConnect, ZMQ_DONTWAIT);
            if (msg != null) {
                recvCount++;
            }
        } while (msg != null );
        Assert.assertThat(sendCount, CoreMatchers.is(recvCount));
        // Now test real blocking behavior
        // Set a timeout, default is infinite
        int timeout = 0;
        ZMQ.setSocketOption(pubBind, ZMQ_SNDTIMEO, timeout);
        recvCount = 0;
        sendCount = 0;
        hwmlimit = hwm;
        while ((ZMQ.send(pubBind, "", 0)) == 0) {
            sendCount++;
        } 
        Assert.assertThat(pubBind.errno(), CoreMatchers.is(EAGAIN));
        while ((ZMQ.recv(subConnect, ZMQ_DONTWAIT)) != null) {
            recvCount++;
        } 
        Assert.assertThat(sendCount, CoreMatchers.is(recvCount));
        // Tear down the wiring.
        ZMQ.close(pubBind);
        ZMQ.close(subConnect);
        ZMQ.term(ctx);
    }
}

