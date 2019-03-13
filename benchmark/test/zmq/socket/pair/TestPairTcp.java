package zmq.socket.pair;


import ZMQ.CHARSET;
import ZMQ.ZMQ_DONTWAIT;
import ZMQ.ZMQ_EVENT_ALL;
import ZMQ.ZMQ_LAST_ENDPOINT;
import ZMQ.ZMQ_PAIR;
import java.io.IOException;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import zmq.Ctx;
import zmq.Helper;
import zmq.Msg;
import zmq.SocketBase;
import zmq.ZMQ;
import zmq.util.Utils;


public class TestPairTcp {
    @Test
    public void testPairTcp() throws IOException {
        int port = Utils.findOpenPort();
        Ctx ctx = ZMQ.init(1);
        Assert.assertThat(ctx, CoreMatchers.notNullValue());
        SocketBase sb = ZMQ.socket(ctx, ZMQ_PAIR);
        Assert.assertThat(sb, CoreMatchers.notNullValue());
        boolean brc = ZMQ.bind(sb, ("tcp://127.0.0.1:" + port));
        Assert.assertThat(brc, CoreMatchers.is(true));
        SocketBase sc = ZMQ.socket(ctx, ZMQ_PAIR);
        Assert.assertThat(sc, CoreMatchers.notNullValue());
        brc = ZMQ.connect(sc, ("tcp://127.0.0.1:" + port));
        Assert.assertThat(brc, CoreMatchers.is(true));
        Helper.bounce(sb, sc);
        // Tear down the wiring.
        ZMQ.close(sb);
        ZMQ.close(sc);
        ZMQ.term(ctx);
    }

    @Test
    public void testPairConnectSecondClientIssue285() throws IOException {
        String host = "tcp://127.0.0.1:*";
        Ctx ctx = ZMQ.init(1);
        Assert.assertThat(ctx, CoreMatchers.notNullValue());
        SocketBase bind = ZMQ.socket(ctx, ZMQ_PAIR);
        Assert.assertThat(bind, CoreMatchers.notNullValue());
        boolean brc = ZMQ.bind(bind, host);
        Assert.assertThat(brc, CoreMatchers.is(true));
        host = ((String) (ZMQ.getSocketOptionExt(bind, ZMQ_LAST_ENDPOINT)));
        Assert.assertThat(host, CoreMatchers.notNullValue());
        SocketBase first = ZMQ.socket(ctx, ZMQ_PAIR);
        Assert.assertThat(first, CoreMatchers.notNullValue());
        brc = ZMQ.connect(first, host);
        Assert.assertThat(brc, CoreMatchers.is(true));
        Helper.bounce(bind, first);
        SocketBase second = ZMQ.socket(ctx, ZMQ_PAIR);
        Assert.assertThat(second, CoreMatchers.notNullValue());
        brc = ZMQ.connect(second, host);
        Assert.assertThat(brc, CoreMatchers.is(true));
        int ret = ZMQ.send(bind, "data", 0);
        Assert.assertThat(ret, CoreMatchers.is(4));
        ret = ZMQ.send(bind, "datb", 0);
        Assert.assertThat(ret, CoreMatchers.is(4));
        ret = ZMQ.send(bind, "datc", 0);
        Assert.assertThat(ret, CoreMatchers.is(4));
        ZMQ.msleep(100);
        // no receiving from second connected pair
        Msg msg = ZMQ.recv(second, ZMQ_DONTWAIT);
        Assert.assertThat(msg, CoreMatchers.nullValue());
        // receiving from first connected pair
        msg = ZMQ.recv(first, ZMQ_DONTWAIT);
        Assert.assertThat(msg, CoreMatchers.notNullValue());
        Assert.assertThat(msg.data(), CoreMatchers.is("data".getBytes(CHARSET)));
        msg = ZMQ.recv(first, ZMQ_DONTWAIT);
        Assert.assertThat(msg, CoreMatchers.notNullValue());
        Assert.assertThat(msg.data(), CoreMatchers.is("datb".getBytes(CHARSET)));
        msg = ZMQ.recv(first, ZMQ_DONTWAIT);
        Assert.assertThat(msg, CoreMatchers.notNullValue());
        Assert.assertThat(msg.data(), CoreMatchers.is("datc".getBytes(CHARSET)));
        // Tear down the wiring.
        ZMQ.close(bind);
        ZMQ.close(first);
        ZMQ.close(second);
        ZMQ.term(ctx);
    }

    @Test
    public void testPairMonitorBindConnect() throws IOException, InterruptedException {
        int port = Utils.findOpenPort();
        String host = "tcp://127.0.0.1:" + port;
        Ctx ctx = ZMQ.init(1);
        Assert.assertThat(ctx, CoreMatchers.notNullValue());
        SocketBase bind = ZMQ.socket(ctx, ZMQ_PAIR);
        Assert.assertThat(bind, CoreMatchers.notNullValue());
        boolean rc = ZMQ.bind(bind, host);
        Assert.assertThat(rc, CoreMatchers.is(true));
        SocketBase connect = ZMQ.socket(ctx, ZMQ_PAIR);
        Assert.assertThat(connect, CoreMatchers.notNullValue());
        rc = ZMQ.connect(connect, host);
        Assert.assertThat(rc, CoreMatchers.is(true));
        Helper.bounce(bind, connect);
        SocketBase monitor = ZMQ.socket(ctx, ZMQ_PAIR);
        Assert.assertThat(monitor, CoreMatchers.notNullValue());
        rc = ZMQ.monitorSocket(connect, "inproc://events", ZMQ_EVENT_ALL);
        Assert.assertThat(rc, CoreMatchers.is(true));
        rc = ZMQ.bind(monitor, "inproc://events");
        Assert.assertThat(rc, CoreMatchers.is(false));
        rc = ZMQ.connect(monitor, "inproc://events");
        Assert.assertThat(rc, CoreMatchers.is(true));
        // Tear down the wiring.
        ZMQ.close(bind);
        ZMQ.close(connect);
        ZMQ.close(monitor);
        ZMQ.term(ctx);
    }

    @Test
    public void testPairMonitorIssue291() throws IOException, InterruptedException {
        int port = Utils.findOpenPort();
        String host = "tcp://127.0.0.1:" + port;
        Ctx ctx = ZMQ.init(1);
        Assert.assertThat(ctx, CoreMatchers.notNullValue());
        // bind first to use the address
        SocketBase bind = ZMQ.socket(ctx, ZMQ_PAIR);
        Assert.assertThat(bind, CoreMatchers.notNullValue());
        boolean rc = ZMQ.bind(bind, host);
        Assert.assertThat(rc, CoreMatchers.is(true));
        // monitor new socket and connect another pair to send events to
        SocketBase monitored = ZMQ.socket(ctx, ZMQ_PAIR);
        Assert.assertThat(monitored, CoreMatchers.notNullValue());
        rc = ZMQ.monitorSocket(monitored, "inproc://events", ZMQ_EVENT_ALL);
        Assert.assertThat(rc, CoreMatchers.is(true));
        SocketBase monitor = ZMQ.socket(ctx, ZMQ_PAIR);
        Assert.assertThat(monitor, CoreMatchers.notNullValue());
        rc = ZMQ.connect(monitor, "inproc://events");
        Assert.assertThat(rc, CoreMatchers.is(true));
        // bind monitored socket with already used address
        rc = ZMQ.bind(monitored, host);
        Assert.assertThat(rc, CoreMatchers.is(false));
        // Tear down the wiring.
        ZMQ.close(bind);
        ZMQ.close(monitored);
        ZMQ.close(monitor);
        ZMQ.term(ctx);
    }
}

