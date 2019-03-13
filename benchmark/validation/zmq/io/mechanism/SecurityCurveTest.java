package zmq.io.mechanism;


import Mechanisms.CURVE;
import ZError.EINVAL;
import ZMQ.CHARSET;
import ZMQ.ZMQ_CURVE_PUBLICKEY;
import ZMQ.ZMQ_CURVE_SECRETKEY;
import ZMQ.ZMQ_CURVE_SERVER;
import ZMQ.ZMQ_CURVE_SERVERKEY;
import ZMQ.ZMQ_DEALER;
import ZMQ.ZMQ_IDENTITY;
import ZMQ.ZMQ_LAST_ENDPOINT;
import ZMQ.ZMQ_PLAIN_PASSWORD;
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
import zmq.io.mechanism.curve.Curve;
import zmq.util.TestUtils;
import zmq.util.Z85;


public class SecurityCurveTest {
    private static class ZapHandler implements Runnable {
        private final SocketBase handler;

        private final String clientPublic;

        public ZapHandler(SocketBase handler, String clientPublic) {
            this.handler = handler;
            this.clientPublic = clientPublic;
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
                Msg clientKey = ZMQ.recv(handler, 0);
                final String clientKeyText = Z85.encode(clientKey.data(), clientKey.size());
                Assert.assertThat(new String(version.data(), ZMQ.CHARSET), CoreMatchers.is("1.0"));
                Assert.assertThat(new String(mechanism.data(), ZMQ.CHARSET), CoreMatchers.is("CURVE"));
                Assert.assertThat(new String(identity.data(), ZMQ.CHARSET), CoreMatchers.is("IDENT"));
                int ret = ZMQ.send(handler, version, ZMQ_SNDMORE);
                Assert.assertThat(ret, CoreMatchers.is(3));
                ret = ZMQ.send(handler, sequence, ZMQ_SNDMORE);
                Assert.assertThat(ret, CoreMatchers.is(1));
                System.out.println("Sending ZAP CURVE reply");
                if (clientKeyText.equals(clientPublic)) {
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
                    ret = ZMQ.send(handler, "Invalid client public key", ZMQ_SNDMORE);
                    Assert.assertThat(ret, CoreMatchers.is(25));
                    ret = ZMQ.send(handler, "", ZMQ_SNDMORE);
                    Assert.assertThat(ret, CoreMatchers.is(0));
                    ret = ZMQ.send(handler, "", 0);
                    Assert.assertThat(ret, CoreMatchers.is(0));
                }
            } 
            ZMQ.closeZeroLinger(handler);
        }
    }

    private String connectionString;

    @Test
    public void testPlainCurveKeys() throws Exception {
        byte[][] serverKeyPair = new Curve().keypair();
        byte[] serverPublicKey = serverKeyPair[0];
        byte[] serverSecretKey = serverKeyPair[1];
        byte[][] clientKeyPair = new Curve().keypair();
        byte[] clientPublicKey = clientKeyPair[0];
        byte[] clientSecretKey = clientKeyPair[1];
        Ctx context = ZMQ.createContext();
        SocketBase server = context.createSocket(ZMQ_DEALER);
        ZMQ.setSocketOption(server, ZMQ_CURVE_SERVER, true);
        ZMQ.setSocketOption(server, ZMQ_CURVE_SECRETKEY, serverSecretKey);
        server.bind(connectionString);
        SocketBase client = context.createSocket(ZMQ_DEALER);
        ZMQ.setSocketOption(client, ZMQ_CURVE_SERVERKEY, serverPublicKey);
        ZMQ.setSocketOption(client, ZMQ_CURVE_PUBLICKEY, clientPublicKey);
        ZMQ.setSocketOption(client, ZMQ_CURVE_SECRETKEY, clientSecretKey);
        client.connect(connectionString);
        byte[] testBytes = "hello-world".getBytes();
        ZMQ.send(client, testBytes, 0);
        byte[] recv = ZMQ.recv(server, 0).data();
        Assert.assertThat(recv, CoreMatchers.is(CoreMatchers.equalTo(testBytes)));
        server.close();
        client.close();
        context.terminate();
    }

    @Test
    public void testCurveMechanismSecurity() throws IOException, InterruptedException {
        Curve cryptoBox = new Curve();
        // Generate new keypairs for this test
        // We'll generate random test keys at startup
        String[] clientKeys = cryptoBox.keypairZ85();
        String clientPublic = clientKeys[0];
        String clientSecret = clientKeys[1];
        String[] serverKeys = cryptoBox.keypairZ85();
        String serverPublic = serverKeys[0];
        String serverSecret = serverKeys[1];
        String host = "tcp://127.0.0.1:*";
        Ctx ctx = ZMQ.createContext();
        // Spawn ZAP handler
        // We create and bind ZAP socket in main thread to avoid case
        // where child thread does not start up fast enough.
        SocketBase handler = ZMQ.socket(ctx, ZMQ_REP);
        Assert.assertThat(handler, CoreMatchers.notNullValue());
        boolean rc = ZMQ.bind(handler, "inproc://zeromq.zap.01");
        Assert.assertThat(rc, CoreMatchers.is(true));
        Thread thread = new Thread(new SecurityCurveTest.ZapHandler(handler, clientPublic));
        thread.start();
        // Server socket will accept connections
        SocketBase server = ZMQ.socket(ctx, ZMQ_DEALER);
        Assert.assertThat(server, CoreMatchers.notNullValue());
        ZMQ.setSocketOption(server, ZMQ_CURVE_SERVER, true);
        ZMQ.setSocketOption(server, ZMQ_CURVE_SECRETKEY, serverSecret);
        ZMQ.setSocketOption(server, ZMQ_IDENTITY, "IDENT");
        rc = ZMQ.bind(server, host);
        Assert.assertThat(rc, CoreMatchers.is(true));
        host = ((String) (ZMQ.getSocketOptionExt(server, ZMQ_LAST_ENDPOINT)));
        int port = TestUtils.port(host);
        // Check CURVE security with valid credentials
        System.out.println("Test Correct CURVE security");
        SocketBase client = ZMQ.socket(ctx, ZMQ_DEALER);
        Assert.assertThat(client, CoreMatchers.notNullValue());
        ZMQ.setSocketOption(client, ZMQ_CURVE_SERVERKEY, serverPublic);
        ZMQ.setSocketOption(client, ZMQ_CURVE_PUBLICKEY, clientPublic);
        ZMQ.setSocketOption(client, ZMQ_CURVE_SECRETKEY, clientSecret);
        rc = ZMQ.connect(client, host);
        Assert.assertThat(rc, CoreMatchers.is(true));
        Helper.bounce(server, client);
        ZMQ.close(client);
        // Check CURVE security with a garbage server key
        // This will be caught by the curve_server class, not passed to ZAP
        System.out.println("Test bad server key CURVE security");
        String garbageKey = "0000000000000000000000000000000000000000";
        client = ZMQ.socket(ctx, ZMQ_DEALER);
        Assert.assertThat(client, CoreMatchers.notNullValue());
        ZMQ.setSocketOption(client, ZMQ_CURVE_SERVERKEY, garbageKey);
        ZMQ.setSocketOption(client, ZMQ_CURVE_PUBLICKEY, clientPublic);
        ZMQ.setSocketOption(client, ZMQ_CURVE_SECRETKEY, clientSecret);
        rc = ZMQ.connect(client, host);
        Assert.assertThat(rc, CoreMatchers.is(true));
        Helper.expectBounceFail(server, client);
        ZMQ.closeZeroLinger(client);
        // Check CURVE security with a garbage client public key
        // This will be caught by the curve_server class, not passed to ZAP
        System.out.println("Test bad client public key CURVE security");
        client = ZMQ.socket(ctx, ZMQ_DEALER);
        Assert.assertThat(client, CoreMatchers.notNullValue());
        ZMQ.setSocketOption(client, ZMQ_CURVE_SERVERKEY, serverPublic);
        ZMQ.setSocketOption(client, ZMQ_CURVE_PUBLICKEY, garbageKey);
        ZMQ.setSocketOption(client, ZMQ_CURVE_SECRETKEY, clientSecret);
        rc = ZMQ.connect(client, host);
        Assert.assertThat(rc, CoreMatchers.is(true));
        Helper.expectBounceFail(server, client);
        ZMQ.closeZeroLinger(client);
        // Check CURVE security with a garbage client secret key
        // This will be caught by the curve_server class, not passed to ZAP
        System.out.println("Test bad client public key CURVE security");
        client = ZMQ.socket(ctx, ZMQ_DEALER);
        Assert.assertThat(client, CoreMatchers.notNullValue());
        ZMQ.setSocketOption(client, ZMQ_CURVE_SERVERKEY, serverPublic);
        ZMQ.setSocketOption(client, ZMQ_CURVE_PUBLICKEY, clientPublic);
        ZMQ.setSocketOption(client, ZMQ_CURVE_SECRETKEY, garbageKey);
        rc = ZMQ.connect(client, host);
        Assert.assertThat(rc, CoreMatchers.is(true));
        Helper.expectBounceFail(server, client);
        ZMQ.closeZeroLinger(client);
        // Check CURVE security with bogus client credentials
        // This must be caught by the ZAP handler
        String[] bogus = cryptoBox.keypairZ85();
        String bogusPublic = bogus[0];
        String bogusSecret = bogus[1];
        System.out.println("Test bad client credentials CURVE security");
        client = ZMQ.socket(ctx, ZMQ_DEALER);
        Assert.assertThat(client, CoreMatchers.notNullValue());
        ZMQ.setSocketOption(client, ZMQ_CURVE_SERVERKEY, serverPublic);
        ZMQ.setSocketOption(client, ZMQ_CURVE_PUBLICKEY, bogusPublic);
        ZMQ.setSocketOption(client, ZMQ_CURVE_SECRETKEY, bogusSecret);
        rc = ZMQ.connect(client, host);
        Assert.assertThat(rc, CoreMatchers.is(true));
        Helper.expectBounceFail(server, client);
        ZMQ.closeZeroLinger(client);
        // Check CURVE security with NULL client credentials
        // This must be caught by the curve_server class, not passed to ZAP
        System.out.println("Test NULL client with CURVE security");
        client = ZMQ.socket(ctx, ZMQ_DEALER);
        Assert.assertThat(client, CoreMatchers.notNullValue());
        rc = ZMQ.connect(client, host);
        Assert.assertThat(rc, CoreMatchers.is(true));
        Helper.expectBounceFail(server, client);
        ZMQ.closeZeroLinger(client);
        // Check CURVE security with PLAIN client credentials
        // This must be caught by the curve_server class, not passed to ZAP
        System.out.println("Test PLAIN client with CURVE security");
        client = ZMQ.socket(ctx, ZMQ_DEALER);
        Assert.assertThat(client, CoreMatchers.notNullValue());
        ZMQ.setSocketOption(client, ZMQ_PLAIN_USERNAME, "user");
        ZMQ.setSocketOption(client, ZMQ_PLAIN_PASSWORD, "pass");
        rc = ZMQ.connect(client, host);
        Assert.assertThat(rc, CoreMatchers.is(true));
        Helper.expectBounceFail(server, client);
        ZMQ.closeZeroLinger(client);
        // Unauthenticated messages from a vanilla socket shouldn't be received
        System.out.println("Test unauthenticated from vanilla socket CURVE security");
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
        // Check return codes for invalid buffer sizes
        // TODO
        System.out.println("Test return codes for invalid buffer sizes with CURVE security");
        client = ZMQ.socket(ctx, ZMQ_DEALER);
        Assert.assertThat(client, CoreMatchers.notNullValue());
        rc = ZMQ.setSocketOption(client, ZMQ_CURVE_SERVERKEY, new byte[123]);
        Assert.assertThat(rc, CoreMatchers.is(false));
        Assert.assertThat(client.errno.get(), CoreMatchers.is(EINVAL));
        rc = ZMQ.setSocketOption(client, ZMQ_CURVE_PUBLICKEY, new byte[123]);
        Assert.assertThat(rc, CoreMatchers.is(false));
        Assert.assertThat(client.errno.get(), CoreMatchers.is(EINVAL));
        rc = ZMQ.setSocketOption(client, ZMQ_CURVE_SECRETKEY, new byte[123]);
        Assert.assertThat(rc, CoreMatchers.is(false));
        Assert.assertThat(client.errno.get(), CoreMatchers.is(EINVAL));
        ZMQ.closeZeroLinger(client);
        ZMQ.closeZeroLinger(server);
        // Shutdown
        ZMQ.term(ctx);
        // Wait until ZAP handler terminates
        thread.join();
    }

    @Test(expected = IllegalStateException.class)
    public void inconsistent1() {
        Options opt = new Options();
        opt.setSocketOpt(ZMQ_CURVE_PUBLICKEY, new byte[32]);
        opt.setSocketOpt(ZMQ_CURVE_SECRETKEY, null);
        CURVE.check(opt);
    }

    @Test(expected = IllegalStateException.class)
    public void inconsistent2() {
        Options opt = new Options();
        opt.setSocketOpt(ZMQ_CURVE_PUBLICKEY, null);
        opt.setSocketOpt(ZMQ_CURVE_SECRETKEY, new byte[32]);
        CURVE.check(opt);
    }

    @Test(expected = IllegalStateException.class)
    public void inconsistent3() {
        Options opt = new Options();
        opt.setSocketOpt(ZMQ_CURVE_PUBLICKEY, new byte[32]);
        opt.setSocketOpt(ZMQ_CURVE_SECRETKEY, new byte[31]);
        CURVE.check(opt);
    }

    @Test(expected = IllegalStateException.class)
    public void inconsistent4() {
        Options opt = new Options();
        opt.setSocketOpt(ZMQ_CURVE_PUBLICKEY, new byte[31]);
        opt.setSocketOpt(ZMQ_CURVE_SECRETKEY, new byte[32]);
        CURVE.check(opt);
    }

    @Test(expected = IllegalStateException.class)
    public void inconsistent5() {
        Options opt = new Options();
        opt.setSocketOpt(ZMQ_CURVE_PUBLICKEY, new byte[32]);
        opt.setSocketOpt(ZMQ_CURVE_SECRETKEY, new byte[32]);
        opt.setSocketOpt(ZMQ_CURVE_SERVERKEY, new byte[31]);
        CURVE.check(opt);
    }

    @Test
    public void consistent1() {
        Options opt = new Options();
        opt.setSocketOpt(ZMQ_CURVE_PUBLICKEY, new byte[32]);
        opt.setSocketOpt(ZMQ_CURVE_SECRETKEY, new byte[32]);
        CURVE.check(opt);
    }

    @Test
    public void consistent2() {
        Options opt = new Options();
        opt.setSocketOpt(ZMQ_CURVE_PUBLICKEY, new byte[32]);
        opt.setSocketOpt(ZMQ_CURVE_SECRETKEY, new byte[32]);
        opt.setSocketOpt(ZMQ_CURVE_SERVERKEY, new byte[32]);
        CURVE.check(opt);
    }
}

