package zmq.socket.pubsub;


import ZMQ.CHARSET;
import ZMQ.ZMQ_DONTWAIT;
import ZMQ.ZMQ_LAST_ENDPOINT;
import ZMQ.ZMQ_SNDMORE;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import zmq.Ctx;
import zmq.Msg;
import zmq.SocketBase;
import zmq.ZMQ;
import zmq.ZMQ.ZMQ_PAIR;
import zmq.ZMQ.ZMQ_RCVTIMEO;
import zmq.ZMQ.ZMQ_SUB;
import zmq.ZMQ.ZMQ_SUBSCRIBE;
import zmq.ZMQ.ZMQ_UNSUBSCRIBE;
import zmq.ZMQ.ZMQ_XPUB;
import zmq.ZMQ.ZMQ_XSUB;
import zmq.util.Utils;
import zmq.zmq.ZMQ;


public class XpubXsubTest {
    @Test
    public void testXpubSub() throws IOException, InterruptedException, ExecutionException {
        final Ctx ctx = zmq.ZMQ.createContext();
        Assert.assertThat(ctx, CoreMatchers.notNullValue());
        boolean rc;
        SocketBase sub = zmq.ZMQ.socket(ctx, ZMQ_SUB);
        rc = zmq.ZMQ.setSocketOption(sub, ZMQ_SUBSCRIBE, "topic");
        Assert.assertThat(rc, CoreMatchers.is(true));
        rc = zmq.ZMQ.setSocketOption(sub, ZMQ_SUBSCRIBE, "topix");
        Assert.assertThat(rc, CoreMatchers.is(true));
        SocketBase pub = zmq.ZMQ.socket(ctx, ZMQ_XPUB);
        rc = zmq.ZMQ.bind(pub, "tcp://127.0.0.1:*");
        Assert.assertThat(rc, CoreMatchers.is(true));
        String endpoint = ((String) (ZMQ.getSocketOptionExt(pub, ZMQ_LAST_ENDPOINT)));
        Assert.assertThat(endpoint, CoreMatchers.notNullValue());
        rc = zmq.ZMQ.connect(sub, endpoint);
        Assert.assertThat(rc, CoreMatchers.is(true));
        zmq.ZMQ.msleep(1000);
        System.out.print("Send.");
        rc = pub.send(new Msg("topic".getBytes(CHARSET)), ZMQ_SNDMORE);
        Assert.assertThat(rc, CoreMatchers.is(true));
        rc = pub.send(new Msg("hop".getBytes(CHARSET)), 0);
        Assert.assertThat(rc, CoreMatchers.is(true));
        System.out.print("Recv.");
        Msg msg = sub.recv(0);
        Assert.assertThat(msg.size(), CoreMatchers.is(5));
        msg = sub.recv(0);
        Assert.assertThat(msg.size(), CoreMatchers.is(3));
        rc = zmq.ZMQ.setSocketOption(sub, ZMQ_UNSUBSCRIBE, "topix");
        Assert.assertThat(rc, CoreMatchers.is(true));
        rc = pub.send(new Msg("topix".getBytes(CHARSET)), ZMQ_SNDMORE);
        Assert.assertThat(rc, CoreMatchers.is(true));
        rc = pub.send(new Msg("hop".getBytes(CHARSET)), 0);
        Assert.assertThat(rc, CoreMatchers.is(true));
        rc = zmq.ZMQ.setSocketOption(sub, ZMQ_RCVTIMEO, 500);
        Assert.assertThat(rc, CoreMatchers.is(true));
        msg = sub.recv(0);
        Assert.assertThat(msg, CoreMatchers.nullValue());
        System.out.print("End.");
        zmq.ZMQ.close(sub);
        for (int idx = 0; idx < 2; ++idx) {
            rc = pub.send(new Msg("topic abc".getBytes(CHARSET)), 0);
            Assert.assertThat(rc, CoreMatchers.is(true));
            ZMQ.msleep(10);
        }
        zmq.ZMQ.close(pub);
        zmq.ZMQ.term(ctx);
        System.out.println("Done.");
    }

    @Test
    public void testXpubXSub() throws IOException, InterruptedException, ExecutionException {
        final Ctx ctx = zmq.ZMQ.createContext();
        Assert.assertThat(ctx, CoreMatchers.notNullValue());
        boolean rc;
        Msg msg;
        SocketBase pub = zmq.ZMQ.socket(ctx, ZMQ_XPUB);
        rc = zmq.ZMQ.bind(pub, "tcp://127.0.0.1:*");
        Assert.assertThat(rc, CoreMatchers.is(true));
        String endpoint = ((String) (ZMQ.getSocketOptionExt(pub, ZMQ_LAST_ENDPOINT)));
        Assert.assertThat(endpoint, CoreMatchers.notNullValue());
        SocketBase sub = zmq.ZMQ.socket(ctx, ZMQ_XSUB);
        rc = zmq.ZMQ.connect(sub, endpoint);
        Assert.assertThat(rc, CoreMatchers.is(true));
        zmq.ZMQ.msleep(300);
        System.out.print("Send.");
        rc = sub.send(new Msg("\u0001topic".getBytes(CHARSET)), 0);
        Assert.assertThat(rc, CoreMatchers.is(true));
        zmq.ZMQ.msleep(300);
        rc = pub.send(new Msg("topic".getBytes(CHARSET)), 0);
        Assert.assertThat(rc, CoreMatchers.is(true));
        System.out.print("Recv.");
        // msg = sub.recv(0);
        // assertThat(msg.size(), is(5));
        // 
        // msg = sub.recv(0);
        // assertThat(msg.size(), is(3));
        // 
        rc = sub.send(new Msg("\u0000topic".getBytes(CHARSET)), 0);
        Assert.assertThat(rc, CoreMatchers.is(true));
        // rc = pub.send(new Msg("topix".getBytes(ZMQ.CHARSET)), ZMQ.ZMQ_SNDMORE);
        // assertThat(rc, is(true));
        // 
        // rc = pub.send(new Msg("hop".getBytes(ZMQ.CHARSET)), 0);
        // assertThat(rc, is(true));
        // 
        // rc = zmq.ZMQ.setSocketOption(sub, zmq.ZMQ.ZMQ_RCVTIMEO, 500);
        // assertThat(rc, is(true));
        // 
        // msg = sub.recv(0);
        // assertThat(msg, nullValue());
        zmq.ZMQ.close(sub);
        zmq.ZMQ.close(pub);
        zmq.ZMQ.term(ctx);
    }

    @Test
    public void testIssue476() throws IOException, InterruptedException, ExecutionException {
        final int front = Utils.findOpenPort();
        final int back = Utils.findOpenPort();
        final Ctx ctx = zmq.ZMQ.createContext();
        Assert.assertThat(ctx, CoreMatchers.notNullValue());
        boolean rc;
        final SocketBase proxyPub = zmq.ZMQ.socket(ctx, ZMQ_XPUB);
        rc = proxyPub.bind(("tcp://127.0.0.1:" + back));
        Assert.assertThat(rc, CoreMatchers.is(true));
        final SocketBase proxySub = zmq.ZMQ.socket(ctx, ZMQ_XSUB);
        rc = proxySub.bind(("tcp://127.0.0.1:" + front));
        Assert.assertThat(rc, CoreMatchers.is(true));
        final SocketBase ctrl = zmq.ZMQ.socket(ctx, ZMQ_PAIR);
        rc = ctrl.bind("inproc://ctrl-proxy");
        Assert.assertThat(rc, CoreMatchers.is(true));
        ExecutorService service = Executors.newFixedThreadPool(1);
        Future<?> proxy = service.submit(new Runnable() {
            @Override
            public void run() {
                zmq.ZMQ.proxy(proxySub, proxyPub, null, ctrl);
            }
        });
        SocketBase sub = zmq.ZMQ.socket(ctx, ZMQ_SUB);
        rc = zmq.ZMQ.setSocketOption(sub, ZMQ_SUBSCRIBE, "topic");
        Assert.assertThat(rc, CoreMatchers.is(true));
        rc = zmq.ZMQ.connect(sub, ("tcp://127.0.0.1:" + back));
        Assert.assertThat(rc, CoreMatchers.is(true));
        SocketBase pub = zmq.ZMQ.socket(ctx, ZMQ_XPUB);
        rc = zmq.ZMQ.connect(pub, ("tcp://127.0.0.1:" + front));
        Assert.assertThat(rc, CoreMatchers.is(true));
        sub.recv(ZMQ_DONTWAIT);
        zmq.ZMQ.msleep(1000);
        System.out.print("Send.");
        rc = pub.send(new Msg("topic".getBytes(CHARSET)), ZMQ_SNDMORE);
        Assert.assertThat(rc, CoreMatchers.is(true));
        rc = pub.send(new Msg("hop".getBytes(CHARSET)), 0);
        Assert.assertThat(rc, CoreMatchers.is(true));
        System.out.print("Recv.");
        Msg msg = sub.recv(0);
        Assert.assertThat(msg.size(), CoreMatchers.is(5));
        msg = sub.recv(0);
        Assert.assertThat(msg.size(), CoreMatchers.is(3));
        System.out.print("End.");
        zmq.ZMQ.close(sub);
        for (int idx = 0; idx < 2; ++idx) {
            rc = pub.send(new Msg("topic abc".getBytes(CHARSET)), 0);
            Assert.assertThat(rc, CoreMatchers.is(true));
            ZMQ.msleep(10);
        }
        zmq.ZMQ.close(pub);
        final SocketBase command = zmq.ZMQ.socket(ctx, ZMQ_PAIR);
        rc = command.connect("inproc://ctrl-proxy");
        Assert.assertThat(rc, CoreMatchers.is(true));
        command.send(new Msg(ZMQ.PROXY_TERMINATE), 0);
        proxy.get();
        zmq.ZMQ.close(command);
        zmq.ZMQ.close(proxyPub);
        zmq.ZMQ.close(proxySub);
        zmq.ZMQ.close(ctrl);
        zmq.ZMQ.term(ctx);
        System.out.println("Done.");
    }
}

