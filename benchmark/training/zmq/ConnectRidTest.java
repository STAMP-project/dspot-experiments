package zmq;


import ZMQ.CHARSET;
import ZMQ.ZMQ_CONNECT_RID;
import ZMQ.ZMQ_DONTWAIT;
import ZMQ.ZMQ_IDENTITY;
import ZMQ.ZMQ_LAST_ENDPOINT;
import ZMQ.ZMQ_LINGER;
import ZMQ.ZMQ_ROUTER;
import ZMQ.ZMQ_SNDMORE;
import ZMQ.ZMQ_STREAM;
import java.io.IOException;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class ConnectRidTest {
    @Test
    public void testStream2stream() throws IOException, InterruptedException {
        System.out.println("Test Stream 2 stream");
        String host = "tcp://localhost:*";
        Msg msg = new Msg("hi 1".getBytes(CHARSET));
        Ctx ctx = ZMQ.init(1);
        assert ctx != null;
        // Set up listener STREAM.
        SocketBase bind = ZMQ.socket(ctx, ZMQ_STREAM);
        assert bind != null;
        ZMQ.setSocketOption(bind, ZMQ_CONNECT_RID, "connectRid");
        ZMQ.setSocketOption(bind, ZMQ_LINGER, 0);
        boolean rc = ZMQ.bind(bind, host);
        assert rc;
        host = ((String) (ZMQ.getSocketOptionExt(bind, ZMQ_LAST_ENDPOINT)));
        Assert.assertThat(host, CoreMatchers.notNullValue());
        // Set up connection stream.
        SocketBase connect = ZMQ.socket(ctx, ZMQ_STREAM);
        assert connect != null;
        ZMQ.setSocketOption(connect, ZMQ_LINGER, 0);
        // Do the connection.
        ZMQ.setSocketOption(connect, ZMQ_CONNECT_RID, "connectRid");
        rc = ZMQ.connect(connect, host);
        assert rc;
        ZMQ.sleep(1);
        /* Uncomment to test assert on duplicate rid.
        //  Test duplicate connect attempt.
        ZMQ.setSocketOption(connect, ZMQ_CONNECT_RID, "conn1", 6);
        rc = ZMQ.connect(connect, host);
        assert (rc);
         */
        // Send data to the bound stream.
        int ret = ZMQ.send(connect, "connectRid", ZMQ_SNDMORE);
        assert 10 == ret;
        ret = ZMQ.send(connect, msg, 0);
        assert 4 == ret;
        // Accept data on the bound stream.
        Msg recv = ZMQ.recv(bind, 0);
        assert recv != null;
        recv = ZMQ.recv(bind, 0);
        assert recv != null;
        ZMQ.close(bind);
        ZMQ.close(connect);
        ZMQ.term(ctx);
    }

    @Test
    public void testRouter2routerNamed() throws IOException, InterruptedException {
        System.out.println("Test Router 2 Router named");
        testRouter2router(true);
    }

    @Test
    public void testRouter2routerUnnamed() throws IOException, InterruptedException {
        System.out.println("Test Router 2 Router unnamed");
        testRouter2router(false);
    }

    @Test
    public void testRouter2routerWhileReceiving() throws IOException, InterruptedException {
        System.out.println("Test Router 2 router while receiving");
        String wildcardAddress = "tcp://localhost:*";
        Msg msg = new Msg("hi 1".getBytes(CHARSET));
        Ctx ctx = ZMQ.init(1);
        assert ctx != null;
        // Set up the router which both binds and connects
        SocketBase x = ZMQ.socket(ctx, ZMQ_ROUTER);
        assert x != null;
        ZMQ.setSocketOption(x, ZMQ_LINGER, 0);
        ZMQ.setSocketOption(x, ZMQ_IDENTITY, "X");
        boolean rc = ZMQ.bind(x, wildcardAddress);
        assert rc;
        String xaddress = ((String) (ZMQ.getSocketOptionExt(x, ZMQ_LAST_ENDPOINT)));
        Assert.assertThat(xaddress, CoreMatchers.notNullValue());
        // Set up the router which binds
        SocketBase z = ZMQ.socket(ctx, ZMQ_ROUTER);
        assert z != null;
        ZMQ.setSocketOption(z, ZMQ_LINGER, 0);
        ZMQ.setSocketOption(z, ZMQ_IDENTITY, "Z");
        rc = ZMQ.bind(z, wildcardAddress);
        assert rc;
        String zaddress = ((String) (ZMQ.getSocketOptionExt(z, ZMQ_LAST_ENDPOINT)));
        Assert.assertThat(zaddress, CoreMatchers.notNullValue());
        // Set up connect-only router.
        SocketBase y = ZMQ.socket(ctx, ZMQ_ROUTER);
        assert y != null;
        ZMQ.setSocketOption(y, ZMQ_LINGER, 0);
        ZMQ.setSocketOption(y, ZMQ_IDENTITY, "Y");
        // Do the connection.
        rc = ZMQ.connect(y, xaddress);
        assert rc;
        // Send data from Y to X
        int ret = ZMQ.send(y, "X", ZMQ_SNDMORE);
        Assert.assertThat(ret, CoreMatchers.is(1));
        ret = ZMQ.send(y, msg, 0);
        Assert.assertThat(ret, CoreMatchers.is(4));
        // wait for the messages to be processed on the io thread
        ZMQ.msleep(100);
        // try to connect X to Z
        ZMQ.setSocketOption(x, ZMQ_CONNECT_RID, "Z");
        rc = ZMQ.connect(x, zaddress);
        assert rc;
        // Send data from X to Z
        ret = ZMQ.send(x, "Z", ZMQ_SNDMORE);
        Assert.assertThat(ret, CoreMatchers.is(1));
        ret = ZMQ.send(x, msg, 0);
        Assert.assertThat(ret, CoreMatchers.is(4));
        // wait for the messages to be delivered
        ZMQ.msleep(100);
        // Make sure that Y has not received any messages
        Msg name = ZMQ.recv(y, ZMQ_DONTWAIT);
        Assert.assertThat(name, CoreMatchers.nullValue());
        // Receive the message from Z
        name = ZMQ.recv(z, 0);
        Assert.assertThat(name, CoreMatchers.notNullValue());
        Assert.assertThat(name.data()[0], CoreMatchers.is(((byte) ('X'))));
        Msg recv = null;
        recv = ZMQ.recv(z, 0);
        Assert.assertThat(recv, CoreMatchers.notNullValue());
        Assert.assertThat(recv.data()[0], CoreMatchers.is(((byte) ('h'))));
        ZMQ.close(x);
        ZMQ.close(y);
        ZMQ.close(z);
        ZMQ.term(ctx);
    }
}

