package zmq.proxy;


import ZMQ.PROXY_TERMINATE;
import ZMQ.ZMQ_PUB;
import ZMQ.ZMQ_REP;
import ZMQ.ZMQ_REQ;
import ZMQ.ZMQ_SUB;
import ZMQ.ZMQ_SUBSCRIBE;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import zmq.Ctx;
import zmq.Msg;
import zmq.SocketBase;
import zmq.ZMQ;
import zmq.util.Utils;


public class ProxySingleSocketTest {
    private static class ServerTask implements Runnable {
        private final Ctx ctx;

        private final String host;

        public ServerTask(Ctx ctx, String host) {
            this.ctx = ctx;
            this.host = host;
        }

        @Override
        public void run() {
            SocketBase rep = ZMQ.socket(ctx, ZMQ_REP);
            Assert.assertThat(rep, CoreMatchers.notNullValue());
            boolean rc = ZMQ.bind(rep, host);
            Assert.assertThat(rc, CoreMatchers.is(true));
            // Control socket receives terminate command from main over inproc
            SocketBase control = ZMQ.socket(ctx, ZMQ_SUB);
            ZMQ.setSocketOption(control, ZMQ_SUBSCRIBE, "");
            rc = ZMQ.connect(control, "inproc://control");
            Assert.assertThat(rc, CoreMatchers.is(true));
            // Use rep as both frontend and backend
            ZMQ.proxy(rep, rep, null, control);
            ZMQ.close(rep);
            ZMQ.close(control);
        }
    }

    @Test
    public void testProxySingleSocket() throws IOException, InterruptedException {
        int port = Utils.findOpenPort();
        String host = "tcp://127.0.0.1:" + port;
        // The main thread simply starts several clients and a server, and then
        // waits for the server to finish.
        Ctx ctx = ZMQ.createContext();
        SocketBase req = ZMQ.socket(ctx, ZMQ_REQ);
        Assert.assertThat(req, CoreMatchers.notNullValue());
        boolean rc = ZMQ.connect(req, host);
        Assert.assertThat(rc, CoreMatchers.is(true));
        // Control socket receives terminate command from main over inproc
        SocketBase control = ZMQ.socket(ctx, ZMQ_PUB);
        rc = ZMQ.bind(control, "inproc://control");
        Assert.assertThat(rc, CoreMatchers.is(true));
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.submit(new ProxySingleSocketTest.ServerTask(ctx, host));
        int ret = ZMQ.send(req, "msg1", 0);
        Assert.assertThat(ret, CoreMatchers.is(4));
        System.out.print(".");
        Msg msg = ZMQ.recv(req, 0);
        System.out.print(".");
        Assert.assertThat(msg, CoreMatchers.notNullValue());
        Assert.assertThat(new String(msg.data(), ZMQ.CHARSET), CoreMatchers.is("msg1"));
        ret = ZMQ.send(req, "msg22", 0);
        Assert.assertThat(ret, CoreMatchers.is(5));
        System.out.print(".");
        msg = ZMQ.recv(req, 0);
        System.out.print(".");
        Assert.assertThat(msg, CoreMatchers.notNullValue());
        Assert.assertThat(new String(msg.data(), ZMQ.CHARSET), CoreMatchers.is("msg22"));
        ret = ZMQ.send(control, PROXY_TERMINATE, 0);
        Assert.assertThat(ret, CoreMatchers.is(9));
        System.out.println(".");
        ZMQ.close(control);
        ZMQ.close(req);
        executor.shutdown();
        executor.awaitTermination(30, TimeUnit.SECONDS);
        ZMQ.term(ctx);
    }
}

