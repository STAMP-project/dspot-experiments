package zmq.socket.pubsub;


import ZError.ENOTSUP;
import ZMQ.ZMQ_DONTWAIT;
import ZMQ.ZMQ_EVENTS;
import ZMQ.ZMQ_PUB;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import zmq.Ctx;
import zmq.SocketBase;
import zmq.ZMQ;


public class PubTest {
    @Test
    public void testHasIn() {
        Ctx ctx = ZMQ.createContext();
        SocketBase pub = null;
        try {
            pub = ctx.createSocket(ZMQ_PUB);
            int events = pub.getSocketOpt(ZMQ_EVENTS);
            Assert.assertThat(events, CoreMatchers.is(2));
        } finally {
            ZMQ.close(pub);
            ZMQ.term(ctx);
        }
    }

    @Test
    public void testRecv() {
        Ctx ctx = ZMQ.createContext();
        SocketBase pub = null;
        try {
            pub = ctx.createSocket(ZMQ_PUB);
            pub.recv(ZMQ_DONTWAIT);
            Assert.fail("Pub cannot receive message");
        } catch (UnsupportedOperationException e) {
            Assert.assertThat(ctx.errno().get(), CoreMatchers.is(ENOTSUP));
        } finally {
            ZMQ.close(pub);
            ZMQ.term(ctx);
        }
    }
}

