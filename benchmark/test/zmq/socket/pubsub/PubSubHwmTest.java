package zmq.socket.pubsub;


import ZMQ.ZMQ_DONTWAIT;
import ZMQ.ZMQ_LAST_ENDPOINT;
import ZMQ.ZMQ_PUB;
import ZMQ.ZMQ_RCVHWM;
import ZMQ.ZMQ_SNDHWM;
import ZMQ.ZMQ_SUB;
import ZMQ.ZMQ_SUBSCRIBE;
import java.io.IOException;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import zmq.Ctx;
import zmq.SocketBase;
import zmq.ZMQ;


public class PubSubHwmTest {
    @Test
    public void testDefaults() {
        // send 1000 msg on hwm 1000, receive 1000
        int count = testDefaults(1000, 1000);
        Assert.assertThat(count, CoreMatchers.is(1000));
    }

    @Test
    public void testBlocking() {
        // send 6000 msg on hwm 2000, drops above hwm, only receive hwm
        int count = testBlocking(2000, 6000);
        Assert.assertThat(count, CoreMatchers.is(6000));
    }

    @Test
    public void testResetHwm() throws IOException {
        // hwm should apply to the messages that have already been received
        // with hwm 11024: send 9999 msg, receive 9999, send 1100, receive 1100
        int firstCount = 9999;
        int secondCount = 1100;
        int hwm = 11024;
        Ctx ctx = ZMQ.createContext();
        // Set up bind socket
        SocketBase pub = ctx.createSocket(ZMQ_PUB);
        boolean rc = ZMQ.setSocketOption(pub, ZMQ_SNDHWM, hwm);
        Assert.assertThat(rc, CoreMatchers.is(true));
        rc = ZMQ.bind(pub, "tcp://localhost:*");
        Assert.assertThat(rc, CoreMatchers.is(true));
        String host = ((String) (ZMQ.getSocketOptionExt(pub, ZMQ_LAST_ENDPOINT)));
        Assert.assertThat(host, CoreMatchers.notNullValue());
        // Set up connect socket
        SocketBase sub = ctx.createSocket(ZMQ_SUB);
        rc = ZMQ.setSocketOption(sub, ZMQ_RCVHWM, hwm);
        Assert.assertThat(rc, CoreMatchers.is(true));
        rc = ZMQ.connect(sub, host);
        Assert.assertThat(rc, CoreMatchers.is(true));
        rc = ZMQ.setSocketOption(sub, ZMQ_SUBSCRIBE, new byte[0]);
        Assert.assertThat(rc, CoreMatchers.is(true));
        ZMQ.sleep(1);
        // Send messages
        int sendCount = 0;
        while ((sendCount < firstCount) && ((ZMQ.send(pub, "1", ZMQ_DONTWAIT)) == 1)) {
            ++sendCount;
        } 
        Assert.assertThat(sendCount, CoreMatchers.is(firstCount));
        ZMQ.msleep(500);
        // Now receive all sent messages
        int recvCount = 0;
        while (null != (ZMQ.recv(sub, ZMQ_DONTWAIT))) {
            ++recvCount;
        } 
        Assert.assertThat(recvCount, CoreMatchers.is(firstCount));
        ZMQ.msleep(100);
        sendCount = 0;
        while ((sendCount < secondCount) && ((ZMQ.send(pub, "2", ZMQ_DONTWAIT)) == 1)) {
            ++sendCount;
        } 
        Assert.assertThat(sendCount, CoreMatchers.is(secondCount));
        ZMQ.msleep(200);
        // Now receive all sent messages
        recvCount = 0;
        while (null != (ZMQ.recv(sub, ZMQ_DONTWAIT))) {
            ++recvCount;
        } 
        Assert.assertThat(recvCount, CoreMatchers.is(secondCount));
        // Clean up
        ZMQ.close(sub);
        ZMQ.close(pub);
        ZMQ.term(ctx);
    }
}

