package zmq.socket.pubsub;


import ZMQ.ZMQ_XPUB;
import ZMQ.ZMQ_XPUB_NODROP;
import ZMQ.ZMQ_XPUB_VERBOSE;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import zmq.Ctx;
import zmq.SocketBase;
import zmq.ZMQ;


public class XPubTest {
    @Test
    public void testSetVerbose() {
        Ctx ctx = ZMQ.createContext();
        SocketBase pub = null;
        try {
            pub = ctx.createSocket(ZMQ_XPUB);
            boolean rc = pub.setSocketOpt(ZMQ_XPUB_VERBOSE, 0);
            Assert.assertThat(rc, CoreMatchers.is(true));
        } finally {
            ZMQ.close(pub);
            ZMQ.term(ctx);
        }
    }

    @Test
    public void testSetNoDrop() {
        Ctx ctx = ZMQ.createContext();
        SocketBase pub = null;
        try {
            pub = ctx.createSocket(ZMQ_XPUB);
            boolean rc = pub.setSocketOpt(ZMQ_XPUB_NODROP, 0);
            Assert.assertThat(rc, CoreMatchers.is(true));
        } finally {
            ZMQ.close(pub);
            ZMQ.term(ctx);
        }
    }
}

