package zmq.socket.stream;


import ZMQ.ZMQ_DEALER;
import ZMQ.ZMQ_LAST_ENDPOINT;
import ZMQ.ZMQ_LINGER;
import ZMQ.ZMQ_SNDMORE;
import ZMQ.ZMQ_STREAM;
import java.io.IOException;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import zmq.Ctx;
import zmq.Msg;
import zmq.SocketBase;
import zmq.ZMQ;


public class StreamEmptyTest {
    @Test
    public void testStreamEmpty() throws IOException, InterruptedException {
        String host = "tcp://localhost:*";
        Ctx ctx = ZMQ.init(1);
        assert ctx != null;
        // Set up listener STREAM.
        SocketBase bind = ZMQ.socket(ctx, ZMQ_STREAM);
        assert bind != null;
        boolean rc = ZMQ.bind(bind, host);
        assert rc;
        host = ((String) (ZMQ.getSocketOptionExt(bind, ZMQ_LAST_ENDPOINT)));
        Assert.assertThat(host, CoreMatchers.notNullValue());
        // Set up connection stream.
        SocketBase connect = ZMQ.socket(ctx, ZMQ_DEALER);
        assert connect != null;
        // Do the connection.
        rc = ZMQ.connect(connect, host);
        assert rc;
        ZMQ.sleep(1);
        int ret = ZMQ.send(connect, "", 0);
        Assert.assertThat(ret, CoreMatchers.is(0));
        Msg msg = ZMQ.recv(bind, 0);
        Assert.assertThat(msg, CoreMatchers.notNullValue());
        Assert.assertThat(msg.size(), CoreMatchers.is(5));
        ret = ZMQ.send(bind, msg, ZMQ_SNDMORE);
        Assert.assertThat(ret, CoreMatchers.is(5));
        ret = ZMQ.send(bind, new Msg(), 0);
        Assert.assertThat(ret, CoreMatchers.is(0));
        ZMQ.setSocketOption(bind, ZMQ_LINGER, 0);
        ZMQ.setSocketOption(connect, ZMQ_LINGER, 0);
        ZMQ.close(bind);
        ZMQ.close(connect);
        ZMQ.term(ctx);
    }
}

