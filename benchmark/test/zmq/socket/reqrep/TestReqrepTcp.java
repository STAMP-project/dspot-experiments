package zmq.socket.reqrep;


import ZMQ.ZMQ_LAST_ENDPOINT;
import ZMQ.ZMQ_REP;
import ZMQ.ZMQ_REQ;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import zmq.Ctx;
import zmq.Helper;
import zmq.SocketBase;
import zmq.ZMQ;


public class TestReqrepTcp {
    @Test
    public void testReqrepTcp() throws Exception {
        Ctx ctx = ZMQ.init(1);
        Assert.assertThat(ctx, CoreMatchers.notNullValue());
        SocketBase repBind = ZMQ.socket(ctx, ZMQ_REP);
        Assert.assertThat(repBind, CoreMatchers.notNullValue());
        boolean rc = ZMQ.bind(repBind, "tcp://127.0.0.1:*");
        Assert.assertThat(rc, CoreMatchers.is(true));
        String host = ((String) (ZMQ.getSocketOptionExt(repBind, ZMQ_LAST_ENDPOINT)));
        Assert.assertThat(host, CoreMatchers.notNullValue());
        SocketBase reqConnect = ZMQ.socket(ctx, ZMQ_REQ);
        Assert.assertThat(reqConnect, CoreMatchers.notNullValue());
        rc = ZMQ.connect(reqConnect, host);
        Assert.assertThat(rc, CoreMatchers.is(true));
        Helper.bounce(repBind, reqConnect);
        ZMQ.close(reqConnect);
        ZMQ.close(repBind);
        ZMQ.term(ctx);
    }
}

