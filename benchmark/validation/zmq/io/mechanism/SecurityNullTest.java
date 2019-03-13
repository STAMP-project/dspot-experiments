package zmq.io.mechanism;


import ZMQ.CHARSET;
import ZMQ.ZMQ_DEALER;
import ZMQ.ZMQ_LAST_ENDPOINT;
import ZMQ.ZMQ_RCVTIMEO;
import ZMQ.ZMQ_REP;
import ZMQ.ZMQ_SNDMORE;
import ZMQ.ZMQ_ZAP_DOMAIN;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import zmq.Ctx;
import zmq.Helper;
import zmq.Msg;
import zmq.SocketBase;
import zmq.ZMQ;
import zmq.util.TestUtils;


public class SecurityNullTest {
    private static class ZapHandler implements Runnable {
        private final SocketBase handler;

        public ZapHandler(SocketBase handler) {
            this.handler = handler;
        }

        @Override
        @SuppressWarnings("unused")
        public void run() {
            // Process ZAP requests forever
            while (true) {
                Msg version = ZMQ.recv(handler, 0);
                if (version == null) {
                    break;// Terminating

                }
                Msg sequence = ZMQ.recv(handler, 0);
                Msg domain = ZMQ.recv(handler, 0);
                Msg address = ZMQ.recv(handler, 0);
                Msg identity = ZMQ.recv(handler, 0);
                Msg mechanism = ZMQ.recv(handler, 0);
                Assert.assertThat(new String(version.data(), ZMQ.CHARSET), CoreMatchers.is("1.0"));
                Assert.assertThat(new String(mechanism.data(), ZMQ.CHARSET), CoreMatchers.is("NULL"));
                int ret = ZMQ.send(handler, version, ZMQ_SNDMORE);
                Assert.assertThat(ret, CoreMatchers.is(3));
                ret = ZMQ.send(handler, sequence, ZMQ_SNDMORE);
                Assert.assertThat(ret, CoreMatchers.is(1));
                System.out.println("Sending ZAP NULL reply");
                if ("TEST".equals(new String(domain.data(), ZMQ.CHARSET))) {
                    ret = ZMQ.send(handler, "200", ZMQ_SNDMORE);
                    Assert.assertThat(ret, CoreMatchers.is(3));
                    ret = ZMQ.send(handler, "OK", ZMQ_SNDMORE);
                    Assert.assertThat(ret, CoreMatchers.is(2));
                    ret = ZMQ.send(handler, "anonymous", ZMQ_SNDMORE);
                    Assert.assertThat(ret, CoreMatchers.is(9));
                    ret = ZMQ.send(handler, "", 0);
                    Assert.assertThat(ret, CoreMatchers.is(0));
                } else {
                    ret = ZMQ.send(handler, "400", ZMQ_SNDMORE);
                    Assert.assertThat(ret, CoreMatchers.is(3));
                    ret = ZMQ.send(handler, "BAD DOMAIN", ZMQ_SNDMORE);
                    Assert.assertThat(ret, CoreMatchers.is(10));
                    ret = ZMQ.send(handler, "", ZMQ_SNDMORE);
                    Assert.assertThat(ret, CoreMatchers.is(0));
                    ret = ZMQ.send(handler, "", 0);
                    Assert.assertThat(ret, CoreMatchers.is(0));
                }
            } 
            ZMQ.closeZeroLinger(handler);
        }
    }

    @Test
    public void testNullMechanismSecurity() throws IOException, InterruptedException {
        String host = "tcp://127.0.0.1:*";
        Ctx ctx = ZMQ.createContext();
        // Spawn ZAP handler
        // We create and bind ZAP socket in main thread to avoid case
        // where child thread does not start up fast enough.
        SocketBase handler = ZMQ.socket(ctx, ZMQ_REP);
        Assert.assertThat(handler, CoreMatchers.notNullValue());
        boolean rc = ZMQ.bind(handler, "inproc://zeromq.zap.01");
        Assert.assertThat(rc, CoreMatchers.is(true));
        Thread thread = new Thread(new SecurityNullTest.ZapHandler(handler));
        thread.start();
        // We bounce between a binding server and a connecting client
        // We first test client/server with no ZAP domain
        // Libzmq does not call our ZAP handler, the connect must succeed
        System.out.println("Test NO ZAP domain");
        SocketBase server = ZMQ.socket(ctx, ZMQ_DEALER);
        Assert.assertThat(server, CoreMatchers.notNullValue());
        SocketBase client = ZMQ.socket(ctx, ZMQ_DEALER);
        Assert.assertThat(client, CoreMatchers.notNullValue());
        rc = ZMQ.bind(server, host);
        Assert.assertThat(rc, CoreMatchers.is(true));
        String endpoint = ((String) (ZMQ.getSocketOptionExt(server, ZMQ_LAST_ENDPOINT)));
        Assert.assertThat(endpoint, CoreMatchers.notNullValue());
        rc = ZMQ.connect(client, endpoint);
        Assert.assertThat(rc, CoreMatchers.is(true));
        Helper.bounce(server, client);
        ZMQ.closeZeroLinger(server);
        ZMQ.closeZeroLinger(client);
        // Now define a ZAP domain for the server; this enables
        // authentication. We're using the wrong domain so this test
        // must fail.
        System.out.println("Test WRONG ZAP domain");
        server = ZMQ.socket(ctx, ZMQ_DEALER);
        Assert.assertThat(server, CoreMatchers.notNullValue());
        client = ZMQ.socket(ctx, ZMQ_DEALER);
        Assert.assertThat(client, CoreMatchers.notNullValue());
        ZMQ.setSocketOption(server, ZMQ_ZAP_DOMAIN, "WRONG");
        rc = ZMQ.bind(server, host);
        Assert.assertThat(rc, CoreMatchers.is(true));
        endpoint = ((String) (ZMQ.getSocketOptionExt(server, ZMQ_LAST_ENDPOINT)));
        Assert.assertThat(endpoint, CoreMatchers.notNullValue());
        rc = ZMQ.connect(client, endpoint);
        Assert.assertThat(rc, CoreMatchers.is(true));
        Helper.expectBounceFail(server, client);
        ZMQ.closeZeroLinger(server);
        ZMQ.closeZeroLinger(client);
        // Now use the right domain, the test must pass
        System.out.println("Test RIGHT ZAP domain");
        server = ZMQ.socket(ctx, ZMQ_DEALER);
        Assert.assertThat(server, CoreMatchers.notNullValue());
        client = ZMQ.socket(ctx, ZMQ_DEALER);
        Assert.assertThat(client, CoreMatchers.notNullValue());
        ZMQ.setSocketOption(server, ZMQ_ZAP_DOMAIN, "TEST");
        rc = ZMQ.bind(server, host);
        Assert.assertThat(rc, CoreMatchers.is(true));
        endpoint = ((String) (ZMQ.getSocketOptionExt(server, ZMQ_LAST_ENDPOINT)));
        Assert.assertThat(endpoint, CoreMatchers.notNullValue());
        rc = ZMQ.connect(client, endpoint);
        Assert.assertThat(rc, CoreMatchers.is(true));
        Helper.bounce(server, client);
        ZMQ.closeZeroLinger(server);
        ZMQ.closeZeroLinger(client);
        // Unauthenticated messages from a vanilla socket shouldn't be received
        server = ZMQ.socket(ctx, ZMQ_DEALER);
        Assert.assertThat(server, CoreMatchers.notNullValue());
        ZMQ.setSocketOption(server, ZMQ_ZAP_DOMAIN, "WRONG");
        rc = ZMQ.bind(server, host);
        Assert.assertThat(rc, CoreMatchers.is(true));
        endpoint = ((String) (ZMQ.getSocketOptionExt(server, ZMQ_LAST_ENDPOINT)));
        Assert.assertThat(endpoint, CoreMatchers.notNullValue());
        Socket sock = new Socket("127.0.0.1", TestUtils.port(endpoint));
        // send anonymous ZMTP/1.0 greeting
        OutputStream out = sock.getOutputStream();
        out.write(new StringBuilder().append(1).append(0).toString().getBytes(CHARSET));
        // send sneaky message that shouldn't be received
        out.write(new StringBuilder().append(8).append(0).append("sneaky").append(0).toString().getBytes(CHARSET));
        int timeout = 250;
        ZMQ.setSocketOption(server, ZMQ_RCVTIMEO, timeout);
        Msg msg = ZMQ.recv(server, 0);
        Assert.assertThat(msg, CoreMatchers.nullValue());
        sock.close();
        ZMQ.closeZeroLinger(server);
        // Shutdown
        ZMQ.term(ctx);
        // Wait until ZAP handler terminates
        thread.join();
    }
}

