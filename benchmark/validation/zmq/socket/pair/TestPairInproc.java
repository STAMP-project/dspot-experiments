package zmq.socket.pair;


import ZMQ.ZMQ_PAIR;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import zmq.Ctx;
import zmq.Helper;
import zmq.SocketBase;
import zmq.ZMQ;


public class TestPairInproc {
    // Create REQ/ROUTER wiring.
    @Test
    public void testPairInproc() {
        Ctx ctx = ZMQ.init(1);
        Assert.assertThat(ctx, CoreMatchers.notNullValue());
        SocketBase sb = ZMQ.socket(ctx, ZMQ_PAIR);
        Assert.assertThat(sb, CoreMatchers.notNullValue());
        boolean brc = ZMQ.bind(sb, "inproc://a");
        Assert.assertThat(brc, CoreMatchers.is(true));
        SocketBase sc = ZMQ.socket(ctx, ZMQ_PAIR);
        Assert.assertThat(sc, CoreMatchers.notNullValue());
        brc = ZMQ.connect(sc, "inproc://a");
        Assert.assertThat(brc, CoreMatchers.is(true));
        Helper.bounce(sb, sc);
        // Tear down the wiring.
        ZMQ.close(sb);
        ZMQ.close(sc);
        ZMQ.term(ctx);
    }
}

