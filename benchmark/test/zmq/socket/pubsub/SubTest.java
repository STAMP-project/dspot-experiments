package zmq.socket.pubsub;


import ZError.EINVAL;
import ZError.ENOTSUP;
import ZMQ.ZMQ_DONTWAIT;
import ZMQ.ZMQ_EVENTS;
import ZMQ.ZMQ_SUB;
import ZMQ.ZMQ_SUBSCRIBE;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import zmq.Ctx;
import zmq.Msg;
import zmq.SocketBase;
import zmq.ZMQ;


public class SubTest {
    @Test
    public void testHasOut() {
        Ctx ctx = ZMQ.createContext();
        SocketBase pub = null;
        try {
            pub = ctx.createSocket(ZMQ_SUB);
            int events = pub.getSocketOpt(ZMQ_EVENTS);
            Assert.assertThat(events, CoreMatchers.is(0));
        } finally {
            ZMQ.close(pub);
            ZMQ.term(ctx);
        }
    }

    @Test
    public void testSetNullOption() {
        Ctx ctx = ZMQ.createContext();
        SocketBase pub = null;
        try {
            pub = ctx.createSocket(ZMQ_SUB);
            boolean rc = pub.setSocketOpt(ZMQ_SUBSCRIBE, null);
            Assert.assertThat(rc, CoreMatchers.is(false));
        } catch (IllegalArgumentException e) {
            Assert.assertThat(pub.errno.get(), CoreMatchers.is(EINVAL));
        } finally {
            ZMQ.close(pub);
            ZMQ.term(ctx);
        }
    }

    @Test
    public void testSend() {
        Ctx ctx = ZMQ.createContext();
        SocketBase pub = null;
        try {
            pub = ctx.createSocket(ZMQ_SUB);
            pub.send(new Msg(), ZMQ_DONTWAIT);
            Assert.fail("Sub cannot send message");
        } catch (UnsupportedOperationException e) {
            Assert.assertThat(ctx.errno().get(), CoreMatchers.is(ENOTSUP));
        } finally {
            ZMQ.close(pub);
            ZMQ.term(ctx);
        }
    }
}

