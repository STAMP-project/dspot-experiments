package zmq.io;


import ZMQ.ZMQ_DEALER;
import ZMQ.ZMQ_REP;
import ZMQ.ZMQ_SNDMORE;
import ZMQ.ZMQ_ZAP_DOMAIN;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.UUID;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import zmq.Ctx;
import zmq.Msg;
import zmq.SocketBase;
import zmq.ZMQ;
import zmq.util.Utils;


public class MetadataTest {
    private static class ZapHandler implements Runnable {
        private final SocketBase handler;

        public ZapHandler(SocketBase handler) {
            this.handler = handler;
        }

        @SuppressWarnings("unused")
        @Override
        public void run() {
            byte[] metadata = new byte[]{ 5, 'H', 'e', 'l', 'l', 'o', 0, 0, 0, 5, 'W', 'o', 'r', 'l', 'd' };
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
                System.out.println("Sending ZAP reply");
                if ("DOMAIN".equals(new String(domain.data(), ZMQ.CHARSET))) {
                    ret = ZMQ.send(handler, "200", ZMQ_SNDMORE);
                    Assert.assertThat(ret, CoreMatchers.is(3));
                    ret = ZMQ.send(handler, "OK", ZMQ_SNDMORE);
                    Assert.assertThat(ret, CoreMatchers.is(2));
                    ret = ZMQ.send(handler, "anonymous", ZMQ_SNDMORE);
                    Assert.assertThat(ret, CoreMatchers.is(9));
                    ret = ZMQ.send(handler, metadata, metadata.length, 0);
                    Assert.assertThat(ret, CoreMatchers.is(metadata.length));
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
    public void testMetadata() throws IOException, InterruptedException {
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
        Thread thread = new Thread(new MetadataTest.ZapHandler(handler));
        thread.start();
        // Server socket will accept connections
        SocketBase server = ZMQ.socket(ctx, ZMQ_DEALER);
        Assert.assertThat(server, CoreMatchers.notNullValue());
        SocketBase client = ZMQ.socket(ctx, ZMQ_DEALER);
        Assert.assertThat(client, CoreMatchers.notNullValue());
        ZMQ.setSocketOption(server, ZMQ_ZAP_DOMAIN, "DOMAIN");
        rc = ZMQ.bind(server, host);
        Assert.assertThat(rc, CoreMatchers.is(true));
        rc = ZMQ.connect(client, host);
        Assert.assertThat(rc, CoreMatchers.is(true));
        int ret = ZMQ.send(client, "This is a message", 0);
        Assert.assertThat(ret, CoreMatchers.is(17));
        Msg msg = ZMQ.recv(server, 0);
        Assert.assertThat(msg, CoreMatchers.notNullValue());
        String prop = ZMQ.getMessageMetadata(msg, "Socket-Type");
        Assert.assertThat(prop, CoreMatchers.is("DEALER"));
        prop = ZMQ.getMessageMetadata(msg, "User-Id");
        Assert.assertThat(prop, CoreMatchers.is("anonymous"));
        prop = ZMQ.getMessageMetadata(msg, "Peer-Address");
        Assert.assertThat(prop.startsWith("127.0.0.1:"), CoreMatchers.is(true));
        prop = ZMQ.getMessageMetadata(msg, "no such");
        Assert.assertThat(prop, CoreMatchers.nullValue());
        prop = ZMQ.getMessageMetadata(msg, "Hello");
        Assert.assertThat(prop, CoreMatchers.is("World"));
        ZMQ.closeZeroLinger(server);
        ZMQ.closeZeroLinger(client);
        // Shutdown
        ZMQ.term(ctx);
        // Wait until ZAP handler terminates
        thread.join();
    }

    @Test
    public void testWriteRead() throws IOException {
        Metadata src = new Metadata();
        src.set("key", "value");
        src.set(UUID.randomUUID().toString(), UUID.randomUUID().toString());
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        src.write(stream);
        byte[] array = stream.toByteArray();
        Metadata dst = new Metadata();
        dst.read(ByteBuffer.wrap(array), 0, null);
        Assert.assertThat(dst, CoreMatchers.is(src));
    }
}

