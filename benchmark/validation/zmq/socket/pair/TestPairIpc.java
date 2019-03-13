package zmq.socket.pair;


import ZMQ.ZMQ_PAIR;
import java.util.UUID;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import zmq.Ctx;
import zmq.Helper;
import zmq.SocketBase;
import zmq.ZMQ;


public class TestPairIpc {
    // Create REQ/ROUTER wiring.
    @Test
    public void testPairIpc() {
        Ctx ctx = ZMQ.init(1);
        Assert.assertThat(ctx, CoreMatchers.notNullValue());
        SocketBase pairBind = ZMQ.socket(ctx, ZMQ_PAIR);
        Assert.assertThat(pairBind, CoreMatchers.notNullValue());
        UUID random;
        do {
            random = UUID.randomUUID();
        } while (!(ZMQ.bind(pairBind, ("ipc:///tmp/tester/" + (random.toString())))) );
        SocketBase pairConnect = ZMQ.socket(ctx, ZMQ_PAIR);
        Assert.assertThat(pairConnect, CoreMatchers.notNullValue());
        boolean brc = ZMQ.connect(pairConnect, ("ipc:///tmp/tester/" + (random.toString())));
        Assert.assertThat(brc, CoreMatchers.is(true));
        Helper.bounce(pairBind, pairConnect);
        // Tear down the wiring.
        ZMQ.close(pairBind);
        ZMQ.close(pairConnect);
        ZMQ.term(ctx);
    }
}

