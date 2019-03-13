package zmq.proxy;


import ZMQ.PROXY_TERMINATE;
import ZMQ.ZMQ_PUB;
import ZMQ.ZMQ_PUSH;
import ZMQ.ZMQ_SUB;
import ZMQ.ZMQ_SUBSCRIBE;
import java.io.IOException;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import zmq.Ctx;
import zmq.SocketBase;
import zmq.ZMQ;
import zmq.util.Utils;


public class ProxyTerminateTest {
    private static class ServerTask implements Runnable {
        private final Ctx ctx;

        private final String hostFrontend;

        private String hostBackend;

        public ServerTask(Ctx ctx, String hostFrontend, String hostBackend) {
            this.ctx = ctx;
            this.hostFrontend = hostFrontend;
            this.hostBackend = hostBackend;
        }

        @Override
        public void run() {
            SocketBase frontend = ZMQ.socket(ctx, ZMQ_SUB);
            Assert.assertThat(frontend, CoreMatchers.notNullValue());
            ZMQ.setSocketOption(frontend, ZMQ_SUBSCRIBE, "");
            boolean rc = ZMQ.bind(frontend, hostFrontend);
            Assert.assertThat(rc, CoreMatchers.is(true));
            // Nice socket which is never read
            SocketBase backend = ZMQ.socket(ctx, ZMQ_PUSH);
            Assert.assertThat(backend, CoreMatchers.notNullValue());
            rc = ZMQ.bind(frontend, hostBackend);
            Assert.assertThat(rc, CoreMatchers.is(true));
            // Control socket receives terminate command from main over inproc
            SocketBase control = ZMQ.socket(ctx, ZMQ_SUB);
            ZMQ.setSocketOption(control, ZMQ_SUBSCRIBE, "");
            rc = ZMQ.connect(control, "inproc://control");
            Assert.assertThat(rc, CoreMatchers.is(true));
            // Connect backend to frontend via a proxy
            ZMQ.proxy(frontend, backend, null, control);
            ZMQ.close(frontend);
            ZMQ.close(backend);
            ZMQ.close(control);
        }
    }

    @Test
    public void testProxyTerminate() throws IOException, InterruptedException {
        int port = Utils.findOpenPort();
        String frontend = "tcp://127.0.0.1:" + port;
        port = Utils.findOpenPort();
        String backend = "tcp://127.0.0.1:" + port;
        // The main thread simply starts a basic steerable proxy server, publishes some messages, and then
        // waits for the server to terminate.
        Ctx ctx = ZMQ.createContext();
        // Control socket receives terminate command from main over inproc
        SocketBase control = ZMQ.socket(ctx, ZMQ_PUB);
        boolean rc = ZMQ.bind(control, "inproc://control");
        Assert.assertThat(rc, CoreMatchers.is(true));
        Thread thread = new Thread(new ProxyTerminateTest.ServerTask(ctx, frontend, backend));
        thread.start();
        Thread.sleep(500);
        // Start a secondary publisher which writes data to the SUB-PUSH server socket
        SocketBase publisher = ZMQ.socket(ctx, ZMQ_PUB);
        Assert.assertThat(publisher, CoreMatchers.notNullValue());
        rc = ZMQ.connect(publisher, frontend);
        Assert.assertThat(rc, CoreMatchers.is(true));
        Thread.sleep(50);
        int ret = ZMQ.send(publisher, "This is a test", 0);
        Assert.assertThat(ret, CoreMatchers.is(14));
        Thread.sleep(50);
        ret = ZMQ.send(publisher, "This is a test", 0);
        Assert.assertThat(ret, CoreMatchers.is(14));
        Thread.sleep(50);
        ret = ZMQ.send(publisher, "This is a test", 0);
        Assert.assertThat(ret, CoreMatchers.is(14));
        ret = ZMQ.send(control, PROXY_TERMINATE, 0);
        Assert.assertThat(ret, CoreMatchers.is(9));
        ZMQ.close(publisher);
        ZMQ.close(control);
        thread.join();
        ZMQ.term(ctx);
    }
}

