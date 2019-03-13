package zmq.io.mechanism;


import Mechanisms.PLAIN;
import ZMQ.CHARSET;
import ZMQ.ZMQ_DEALER;
import ZMQ.ZMQ_IDENTITY;
import ZMQ.ZMQ_PLAIN_PASSWORD;
import ZMQ.ZMQ_PLAIN_SERVER;
import ZMQ.ZMQ_PLAIN_USERNAME;
import ZMQ.ZMQ_RCVTIMEO;
import ZMQ.ZMQ_REP;
import ZMQ.ZMQ_SNDMORE;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import zmq.Ctx;
import zmq.Helper;
import zmq.Msg;
import zmq.Options;
import zmq.SocketBase;
import zmq.ZMQ;
import zmq.util.Utils;


public class SecurityPlainTest {
    private static class ZapHandler implements Runnable {
        private final SocketBase handler;

        public ZapHandler(SocketBase handler) {
            this.handler = handler;
        }

        @SuppressWarnings("unused")
        @Override
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
                Msg username = ZMQ.recv(handler, 0);
                Msg password = ZMQ.recv(handler, 0);
                Assert.assertThat(new String(version.data(), ZMQ.CHARSET), CoreMatchers.is("1.0"));
                Assert.assertThat(new String(mechanism.data(), ZMQ.CHARSET), CoreMatchers.is("PLAIN"));
                Assert.assertThat(new String(identity.data(), ZMQ.CHARSET), CoreMatchers.is("IDENT"));
                int ret = ZMQ.send(handler, version, ZMQ_SNDMORE);
                Assert.assertThat(ret, CoreMatchers.is(3));
                ret = ZMQ.send(handler, sequence, ZMQ_SNDMORE);
                Assert.assertThat(ret, CoreMatchers.is(1));
                System.out.println("Sending ZAP PLAIN reply");
                if (("admin".equals(new String(username.data(), ZMQ.CHARSET))) && ("password".equals(new String(password.data(), ZMQ.CHARSET)))) {
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
                    ret = ZMQ.send(handler, "Invalid username or password", ZMQ_SNDMORE);
                    Assert.assertThat(ret, CoreMatchers.is(28));
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
    public void testPlainMechanismSecurity() throws IOException, InterruptedException {
        int port = Utils.findOpenPort();
        String host = "tcp://127.0.0.1:" + port;
        Ctx ctx = ZMQ.createContext();
        // Spawn ZAP handler
        // We create and bind ZAP socket in main thread to avoid case
        // where child thread does not start up fast enough.
        SocketBase handler = ZMQ.socket(ctx, ZMQ_REP);
        Assert.assertThat(handler, CoreMatchers.notNullValue());
        boolean rc = ZMQ.bind(handler, "inproc://zeromq.zap.01");
        Assert.assertThat(rc, CoreMatchers.is(true));
        Thread thread = new Thread(new SecurityPlainTest.ZapHandler(handler));
        thread.start();
        // Server socket will accept connections
        SocketBase server = ZMQ.socket(ctx, ZMQ_DEALER);
        Assert.assertThat(server, CoreMatchers.notNullValue());
        ZMQ.setSocketOption(server, ZMQ_IDENTITY, "IDENT");
        ZMQ.setSocketOption(server, ZMQ_PLAIN_SERVER, true);
        rc = ZMQ.bind(server, host);
        Assert.assertThat(rc, CoreMatchers.is(true));
        String username = "admin";
        String password = "password";
        // Check PLAIN security with correct username/password
        System.out.println("Test Correct PLAIN security");
        SocketBase client = ZMQ.socket(ctx, ZMQ_DEALER);
        Assert.assertThat(client, CoreMatchers.notNullValue());
        ZMQ.setSocketOption(client, ZMQ_PLAIN_USERNAME, username);
        ZMQ.setSocketOption(client, ZMQ_PLAIN_PASSWORD, password);
        rc = ZMQ.connect(client, host);
        Assert.assertThat(rc, CoreMatchers.is(true));
        Helper.bounce(server, client);
        ZMQ.close(client);
        // Check PLAIN security with badly configured client (as_server)
        // This will be caught by the plain_server class, not passed to ZAP
        System.out.println("Test badly configured PLAIN security");
        client = ZMQ.socket(ctx, ZMQ_DEALER);
        Assert.assertThat(client, CoreMatchers.notNullValue());
        ZMQ.setSocketOption(client, ZMQ_PLAIN_SERVER, true);
        rc = ZMQ.connect(client, host);
        Assert.assertThat(rc, CoreMatchers.is(true));
        Helper.expectBounceFail(server, client);
        ZMQ.closeZeroLinger(client);
        // Check PLAIN security -- failed authentication
        System.out.println("Test wrong authentication PLAIN security");
        client = ZMQ.socket(ctx, ZMQ_DEALER);
        Assert.assertThat(client, CoreMatchers.notNullValue());
        ZMQ.setSocketOption(client, ZMQ_PLAIN_USERNAME, "wronguser");
        ZMQ.setSocketOption(client, ZMQ_PLAIN_PASSWORD, "wrongpass");
        rc = ZMQ.connect(client, host);
        Assert.assertThat(rc, CoreMatchers.is(true));
        Helper.expectBounceFail(server, client);
        ZMQ.closeZeroLinger(client);
        // Unauthenticated messages from a vanilla socket shouldn't be received
        System.out.println("Test unauthenticated from vanilla socket PLAIN security");
        Socket sock = new Socket("127.0.0.1", port);
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

    @Test(expected = IllegalStateException.class)
    public void inconsistent1() {
        Options opt = new Options();
        opt.setSocketOpt(ZMQ_PLAIN_USERNAME, null);
        opt.setSocketOpt(ZMQ_PLAIN_PASSWORD, "plainPassword");
        PLAIN.check(opt);
    }

    @Test(expected = IllegalStateException.class)
    public void inconsistent2() {
        Options opt = new Options();
        opt.setSocketOpt(ZMQ_PLAIN_PASSWORD, null);
        opt.setSocketOpt(ZMQ_PLAIN_USERNAME, "plainUsername");
        PLAIN.check(opt);
    }

    @Test(expected = IllegalStateException.class)
    public void inconsistent3() {
        Options opt = new Options();
        opt.setSocketOpt(ZMQ_PLAIN_USERNAME, String.format("%256d", 1));
        opt.setSocketOpt(ZMQ_PLAIN_PASSWORD, "plainPassword");
        PLAIN.check(opt);
    }

    @Test(expected = IllegalStateException.class)
    public void inconsistent4() {
        Options opt = new Options();
        opt.setSocketOpt(ZMQ_PLAIN_USERNAME, "plainUsername");
        opt.setSocketOpt(ZMQ_PLAIN_PASSWORD, String.format("%256d", 1));
        PLAIN.check(opt);
    }

    @Test
    public void consistent() {
        Options opt = new Options();
        opt.setSocketOpt(ZMQ_PLAIN_USERNAME, "plainUsername");
        opt.setSocketOpt(ZMQ_PLAIN_PASSWORD, "plainPassword");
        PLAIN.check(opt);
    }
}

