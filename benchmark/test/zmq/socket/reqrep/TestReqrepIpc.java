package zmq.socket.reqrep;


import ZMQ.ZMQ_REP;
import ZMQ.ZMQ_REQ;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import zmq.Ctx;
import zmq.Helper;
import zmq.SocketBase;
import zmq.ZMQ;


public class TestReqrepIpc {
    @Test
    public void testReqrepIpc() {
        Ctx ctx = ZMQ.init(1);
        Assert.assertThat(ctx, CoreMatchers.notNullValue());
        SocketBase sb = ZMQ.socket(ctx, ZMQ_REP);
        Assert.assertThat(sb, CoreMatchers.notNullValue());
        boolean brc = ZMQ.bind(sb, "ipc:///tmp/tester2");
        Assert.assertThat(brc, CoreMatchers.is(true));
        SocketBase sc = ZMQ.socket(ctx, ZMQ_REQ);
        Assert.assertThat(sc, CoreMatchers.notNullValue());
        brc = ZMQ.connect(sc, "ipc:///tmp/tester2");
        Assert.assertThat(brc, CoreMatchers.is(true));
        Helper.bounce(sb, sc);
        // Tear down the wiring.
        ZMQ.close(sb);
        ZMQ.close(sc);
        ZMQ.term(ctx);
    }
}

