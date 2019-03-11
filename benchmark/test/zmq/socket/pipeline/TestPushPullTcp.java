package zmq.socket.pipeline;


import ZMQ.CHARSET;
import ZMQ.ZMQ_LAST_ENDPOINT;
import ZMQ.ZMQ_PULL;
import ZMQ.ZMQ_PUSH;
import ZMQ.ZMQ_RCVMORE;
import ZMQ.ZMQ_SNDMORE;
import java.io.IOException;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import zmq.Ctx;
import zmq.Msg;
import zmq.SocketBase;
import zmq.ZMQ;


public class TestPushPullTcp {
    @Test
    public void testPushPullTcp() throws IOException {
        Ctx ctx = ZMQ.init(1);
        Assert.assertThat(ctx, CoreMatchers.notNullValue());
        SocketBase push = ZMQ.socket(ctx, ZMQ_PUSH);
        Assert.assertThat(push, CoreMatchers.notNullValue());
        boolean brc = ZMQ.bind(push, "tcp://127.0.0.1:*");
        Assert.assertThat(brc, CoreMatchers.is(true));
        String host = ((String) (ZMQ.getSocketOptionExt(push, ZMQ_LAST_ENDPOINT)));
        Assert.assertThat(host, CoreMatchers.notNullValue());
        SocketBase pull = ZMQ.socket(ctx, ZMQ_PULL);
        Assert.assertThat(pull, CoreMatchers.notNullValue());
        brc = ZMQ.connect(pull, host);
        Assert.assertThat(brc, CoreMatchers.is(true));
        byte[] content = "12345678ABCDEFGH12345678abcdefgh".getBytes(CHARSET);
        // Send the message.
        int rc = ZMQ.send(push, content, 32, ZMQ_SNDMORE);
        assert rc == 32;
        rc = ZMQ.send(push, content, 32, 0);
        Assert.assertThat(rc, CoreMatchers.is(32));
        // Bounce the message back.
        Msg msg;
        msg = ZMQ.recv(pull, 0);
        assert (msg.size()) == 32;
        int rcvmore = ZMQ.getSocketOption(pull, ZMQ_RCVMORE);
        Assert.assertThat(rcvmore, CoreMatchers.is(1));
        msg = ZMQ.recv(pull, 0);
        assert rc == 32;
        rcvmore = ZMQ.getSocketOption(pull, ZMQ_RCVMORE);
        Assert.assertThat(rcvmore, CoreMatchers.is(0));
        // Tear down the wiring.
        ZMQ.close(push);
        ZMQ.close(pull);
        ZMQ.term(ctx);
    }
}

