package zmq.socket.reqrep;


import ZMQ.ZMQ_IDENTITY;
import ZMQ.ZMQ_PROBE_ROUTER;
import ZMQ.ZMQ_ROUTER;
import ZMQ.ZMQ_SNDMORE;
import java.io.IOException;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import zmq.Ctx;
import zmq.Msg;
import zmq.SocketBase;
import zmq.ZMQ;
import zmq.util.Utils;


public class RouterProbeTest {
    @Test
    public void testProbeRouter() throws IOException, InterruptedException {
        int port = Utils.findOpenPort();
        String host = "tcp://127.0.0.1:" + port;
        Ctx ctx = ZMQ.createContext();
        // Server socket will accept connections
        SocketBase server = ZMQ.socket(ctx, ZMQ_ROUTER);
        Assert.assertThat(server, CoreMatchers.notNullValue());
        boolean rc = ZMQ.bind(server, host);
        Assert.assertThat(rc, CoreMatchers.is(true));
        // Create client and connect to server, doing a probe
        SocketBase client = ZMQ.socket(ctx, ZMQ_ROUTER);
        Assert.assertThat(client, CoreMatchers.notNullValue());
        rc = ZMQ.setSocketOption(client, ZMQ_IDENTITY, "X");
        Assert.assertThat(rc, CoreMatchers.is(true));
        rc = ZMQ.setSocketOption(client, ZMQ_PROBE_ROUTER, true);
        Assert.assertThat(rc, CoreMatchers.is(true));
        rc = ZMQ.connect(client, host);
        Assert.assertThat(rc, CoreMatchers.is(true));
        // We expect an identity=X + empty message from client
        Msg msg = ZMQ.recv(server, 0);
        Assert.assertThat(msg, CoreMatchers.notNullValue());
        Assert.assertThat(msg.get(0), CoreMatchers.is(((byte) ('X'))));
        msg = ZMQ.recv(server, 0);
        Assert.assertThat(msg, CoreMatchers.notNullValue());
        Assert.assertThat(msg.size(), CoreMatchers.is(0));
        // Send a message to client now
        int ret = ZMQ.send(server, "X", ZMQ_SNDMORE);
        Assert.assertThat(ret, CoreMatchers.is(1));
        ret = ZMQ.send(server, "Hello", 0);
        Assert.assertThat(ret, CoreMatchers.is(5));
        msg = ZMQ.recv(client, 0);
        Assert.assertThat(msg, CoreMatchers.notNullValue());
        Assert.assertThat(msg.size(), CoreMatchers.is(5));
        // TODO DIFF V4 test should stop here, check the logic if we should receive payload in the previous message.
        msg = ZMQ.recv(client, 0);
        Assert.assertThat(msg, CoreMatchers.notNullValue());
        Assert.assertThat(new String(msg.data(), ZMQ.CHARSET), CoreMatchers.is("Hello"));
        ZMQ.closeZeroLinger(server);
        ZMQ.closeZeroLinger(client);
        // Shutdown
        ZMQ.term(ctx);
    }
}

