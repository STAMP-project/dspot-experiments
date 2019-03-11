package zmq.socket.reqrep;


import ZError.EHOSTUNREACH;
import ZMQ.ZMQ_DEALER;
import ZMQ.ZMQ_DONTWAIT;
import ZMQ.ZMQ_IDENTITY;
import ZMQ.ZMQ_LAST_ENDPOINT;
import ZMQ.ZMQ_LINGER;
import ZMQ.ZMQ_RCVHWM;
import ZMQ.ZMQ_ROUTER;
import ZMQ.ZMQ_ROUTER_MANDATORY;
import ZMQ.ZMQ_SNDHWM;
import ZMQ.ZMQ_SNDMORE;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import zmq.Ctx;
import zmq.Msg;
import zmq.SocketBase;
import zmq.ZError;
import zmq.ZMQ;


public class RouterMandatoryTest {
    @Test
    public void testRouterMandatory() throws Exception {
        int sent;
        boolean rc;
        Ctx ctx = ZMQ.init(1);
        Assert.assertThat(ctx, CoreMatchers.notNullValue());
        SocketBase router = ZMQ.socket(ctx, ZMQ_ROUTER);
        Assert.assertThat(router, CoreMatchers.notNullValue());
        rc = ZMQ.bind(router, "tcp://127.0.0.1:*");
        Assert.assertThat(rc, CoreMatchers.is(true));
        // Sending a message to an unknown peer with the default setting
        // This will not report any error
        sent = ZMQ.send(router, "UNKNOWN", ZMQ_SNDMORE);
        Assert.assertThat(sent, CoreMatchers.is(7));
        sent = ZMQ.send(router, "DATA", 0);
        Assert.assertThat(sent, CoreMatchers.is(4));
        // Send a message to an unknown peer with mandatory routing
        // This will fail
        int mandatory = 1;
        ZMQ.setSocketOption(router, ZMQ_ROUTER_MANDATORY, mandatory);
        // Send a message and check that it fails
        sent = ZMQ.send(router, "UNKNOWN", ((ZMQ.ZMQ_SNDMORE) | (ZMQ.ZMQ_DONTWAIT)));
        Assert.assertThat(sent, CoreMatchers.is((-1)));
        Assert.assertThat(router.errno(), CoreMatchers.is(EHOSTUNREACH));
        // Create dealer called "X" and connect it to our router
        SocketBase dealer = ZMQ.socket(ctx, ZMQ_DEALER);
        Assert.assertThat(dealer, CoreMatchers.notNullValue());
        ZMQ.setSocketOption(dealer, ZMQ_IDENTITY, "X");
        String host = ((String) (ZMQ.getSocketOptionExt(router, ZMQ_LAST_ENDPOINT)));
        Assert.assertThat(host, CoreMatchers.notNullValue());
        rc = ZMQ.connect(dealer, host);
        Assert.assertThat(rc, CoreMatchers.is(true));
        // Get message from dealer to know when connection is ready
        int ret = ZMQ.send(dealer, "Hello", 0);
        Assert.assertThat(ret, CoreMatchers.is(5));
        Msg msg = ZMQ.recv(router, 0);
        Assert.assertThat(msg, CoreMatchers.notNullValue());
        Assert.assertThat(msg.data()[0], CoreMatchers.is(((byte) ('X'))));
        // Send a message to connected dealer now
        // It should work
        sent = ZMQ.send(router, "X", ZMQ_SNDMORE);
        Assert.assertThat(sent, CoreMatchers.is(1));
        sent = ZMQ.send(router, "Hello", 0);
        Assert.assertThat(sent, CoreMatchers.is(5));
        // Clean up.
        ZMQ.close(router);
        ZMQ.close(dealer);
        ZMQ.term(ctx);
    }

    private static final int BUF_SIZE = 65636;

    @Test
    public void testRouterMandatoryHwm() throws Exception {
        boolean rc;
        System.out.print("Starting router mandatory HWM test");
        Ctx ctx = ZMQ.init(1);
        Assert.assertThat(ctx, CoreMatchers.notNullValue());
        SocketBase router = ZMQ.socket(ctx, ZMQ_ROUTER);
        Assert.assertThat(router, CoreMatchers.notNullValue());
        ZMQ.setSocketOption(router, ZMQ_ROUTER_MANDATORY, true);
        ZMQ.setSocketOption(router, ZMQ_SNDHWM, 1);
        ZMQ.setSocketOption(router, ZMQ_LINGER, 1);
        rc = ZMQ.bind(router, "tcp://127.0.0.1:*");
        Assert.assertThat(rc, CoreMatchers.is(true));
        // Create dealer called "X" and connect it to our router, configure HWM
        SocketBase dealer = ZMQ.socket(ctx, ZMQ_DEALER);
        Assert.assertThat(dealer, CoreMatchers.notNullValue());
        ZMQ.setSocketOption(dealer, ZMQ_RCVHWM, 1);
        ZMQ.setSocketOption(dealer, ZMQ_IDENTITY, "X");
        String host = ((String) (ZMQ.getSocketOptionExt(router, ZMQ_LAST_ENDPOINT)));
        Assert.assertThat(host, CoreMatchers.notNullValue());
        rc = ZMQ.connect(dealer, host);
        Assert.assertThat(rc, CoreMatchers.is(true));
        System.out.print(".");
        // Get message from dealer to know when connection is ready
        int ret = ZMQ.send(dealer, "Hello", 0);
        Assert.assertThat(ret, CoreMatchers.is(5));
        System.out.print(".");
        Msg msg = ZMQ.recv(router, 0);
        System.out.print(".");
        Assert.assertThat(msg, CoreMatchers.notNullValue());
        Assert.assertThat(msg.data()[0], CoreMatchers.is(((byte) ('X'))));
        int i = 0;
        for (; i < 100000; ++i) {
            ret = ZMQ.send(router, "X", ((ZMQ.ZMQ_DONTWAIT) | (ZMQ.ZMQ_SNDMORE)));
            if ((ret == (-1)) && ((router.errno()) == (ZError.EAGAIN))) {
                break;
            }
            Assert.assertThat(ret, CoreMatchers.is(1));
            ret = ZMQ.send(router, new byte[RouterMandatoryTest.BUF_SIZE], RouterMandatoryTest.BUF_SIZE, ZMQ_DONTWAIT);
            Assert.assertThat(ret, CoreMatchers.is(RouterMandatoryTest.BUF_SIZE));
        }
        System.out.print(".");
        // This should fail after one message but kernel buffering could
        // skew results
        Assert.assertThat((i < 10), CoreMatchers.is(true));
        ZMQ.sleep(1);
        // Send second batch of messages
        for (; i < 100000; ++i) {
            ret = ZMQ.send(router, "X", ((ZMQ.ZMQ_DONTWAIT) | (ZMQ.ZMQ_SNDMORE)));
            if ((ret == (-1)) && ((router.errno()) == (ZError.EAGAIN))) {
                break;
            }
            Assert.assertThat(ret, CoreMatchers.is(1));
            ret = ZMQ.send(router, new byte[RouterMandatoryTest.BUF_SIZE], RouterMandatoryTest.BUF_SIZE, ZMQ_DONTWAIT);
            Assert.assertThat(ret, CoreMatchers.is(RouterMandatoryTest.BUF_SIZE));
        }
        System.out.print(".");
        // This should fail after two messages but kernel buffering could
        // skew results
        Assert.assertThat((i < 20), CoreMatchers.is(true));
        System.out.println("Done sending messages.");
        // Clean up.
        ZMQ.close(router);
        ZMQ.close(dealer);
        ZMQ.term(ctx);
    }
}

