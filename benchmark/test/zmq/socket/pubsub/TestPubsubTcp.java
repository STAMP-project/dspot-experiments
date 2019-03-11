package zmq.socket.pubsub;


import ZMQ.CHARSET;
import ZMQ.ZMQ_LAST_ENDPOINT;
import ZMQ.ZMQ_PUB;
import ZMQ.ZMQ_SUB;
import ZMQ.ZMQ_SUBSCRIBE;
import ZMQ.ZMQ_XPUB_NODROP;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import zmq.Ctx;
import zmq.Msg;
import zmq.SocketBase;
import zmq.ZMQ;


public class TestPubsubTcp {
    @Test
    public void testPubsubTcp() throws Exception {
        Ctx ctx = ZMQ.createContext();
        Assert.assertThat(ctx, CoreMatchers.notNullValue());
        SocketBase pubBind = ZMQ.socket(ctx, ZMQ_PUB);
        Assert.assertThat(pubBind, CoreMatchers.notNullValue());
        ZMQ.setSocketOption(pubBind, ZMQ_XPUB_NODROP, true);
        boolean rc = ZMQ.bind(pubBind, "tcp://127.0.0.1:*");
        Assert.assertThat(rc, CoreMatchers.is(true));
        String host = ((String) (ZMQ.getSocketOptionExt(pubBind, ZMQ_LAST_ENDPOINT)));
        Assert.assertThat(host, CoreMatchers.notNullValue());
        SocketBase subConnect = ZMQ.socket(ctx, ZMQ_SUB);
        Assert.assertThat(subConnect, CoreMatchers.notNullValue());
        rc = subConnect.setSocketOpt(ZMQ_SUBSCRIBE, "topic");
        Assert.assertThat(rc, CoreMatchers.is(true));
        rc = ZMQ.connect(subConnect, host);
        Assert.assertThat(rc, CoreMatchers.is(true));
        ZMQ.sleep(1);
        System.out.print("Send");
        rc = pubBind.send(new Msg("topic abc".getBytes(CHARSET)), 0);
        Assert.assertThat(rc, CoreMatchers.is(true));
        rc = pubBind.send(new Msg("topix defg".getBytes(CHARSET)), 0);
        Assert.assertThat(rc, CoreMatchers.is(true));
        rc = pubBind.send(new Msg("topic defgh".getBytes(CHARSET)), 0);
        Assert.assertThat(rc, CoreMatchers.is(true));
        System.out.print(".Recv.");
        Msg msg = subConnect.recv(0);
        System.out.print("1.");
        Assert.assertThat(msg.size(), CoreMatchers.is(9));
        msg = subConnect.recv(0);
        System.out.print("2.");
        Assert.assertThat(msg.size(), CoreMatchers.is(11));
        System.out.print(".End.");
        ZMQ.close(subConnect);
        ZMQ.close(pubBind);
        ZMQ.term(ctx);
        System.out.println("Done.");
    }
}

