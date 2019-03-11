package zmq;


import ZMQ.ZMQ_PUB;
import java.io.IOException;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import zmq.util.Utils;


public class TestConnectResolve {
    @Test
    public void testConnectResolve() throws IOException {
        int port = Utils.findOpenPort();
        System.out.println("test_connect_resolve running...\n");
        Ctx ctx = ZMQ.init(1);
        Assert.assertThat(ctx, CoreMatchers.notNullValue());
        // Create pair of socket, each with high watermark of 2. Thus the total
        // buffer space should be 4 messages.
        SocketBase sock = ZMQ.socket(ctx, ZMQ_PUB);
        Assert.assertThat(sock, CoreMatchers.notNullValue());
        boolean brc = ZMQ.connect(sock, ("tcp://localhost:" + port));
        Assert.assertThat(brc, CoreMatchers.is(true));
        /* try {
        brc = ZMQ.connect (sock, "tcp://foobar123xyz:" + port);
        assertTrue(false);
        } catch (IllegalArgumentException e) {
        }
         */
        ZMQ.close(sock);
        ZMQ.term(ctx);
    }
}

