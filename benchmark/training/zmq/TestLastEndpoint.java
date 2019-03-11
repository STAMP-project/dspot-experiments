package zmq;


import ZMQ.ZMQ_LAST_ENDPOINT;
import ZMQ.ZMQ_ROUTER;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import zmq.util.Utils;


public class TestLastEndpoint {
    @Test
    public void testLastEndpoint() throws IOException {
        int port1 = Utils.findOpenPort();
        int port2 = Utils.findOpenPort();
        // Create the infrastructure
        Ctx ctx = ZMQ.init(1);
        Assert.assertThat(ctx, CoreMatchers.notNullValue());
        SocketBase sb = ZMQ.socket(ctx, ZMQ_ROUTER);
        Assert.assertThat(sb, CoreMatchers.notNullValue());
        TestLastEndpoint.bindAndVerify(sb, ("tcp://127.0.0.1:" + port1));
        TestLastEndpoint.bindAndVerify(sb, ("tcp://127.0.0.1:" + port2));
        sb.close();
        ctx.terminate();
    }

    @Test
    public void testLastEndpointWildcardIpc() throws IOException {
        // Create the infrastructure
        Ctx ctx = ZMQ.init(1);
        Assert.assertThat(ctx, CoreMatchers.notNullValue());
        SocketBase socket = ZMQ.socket(ctx, ZMQ_ROUTER);
        Assert.assertThat(socket, CoreMatchers.notNullValue());
        boolean brc = ZMQ.bind(socket, "ipc://*");
        Assert.assertThat(brc, CoreMatchers.is(true));
        String stest = ((String) (ZMQ.getSocketOptionExt(socket, ZMQ_LAST_ENDPOINT)));
        Assert.assertThat(stest, CoreMatchers.is(CoreMatchers.not("ipc://*")));
        Assert.assertThat(stest, CoreMatchers.is(CoreMatchers.not("ipc://localhost:0")));
        Pattern pattern = Pattern.compile("ipc://.*", Pattern.DOTALL);
        Matcher matcher = pattern.matcher(stest);
        Assert.assertThat(stest, matcher.matches(), CoreMatchers.is(true));
        socket.close();
        ctx.terminate();
    }

    @Test
    public void testLastEndpointWildcardTcp() throws IOException {
        // Create the infrastructure
        Ctx ctx = ZMQ.init(1);
        Assert.assertThat(ctx, CoreMatchers.notNullValue());
        SocketBase socket = ZMQ.socket(ctx, ZMQ_ROUTER);
        Assert.assertThat(socket, CoreMatchers.notNullValue());
        boolean brc = ZMQ.bind(socket, "tcp://127.0.0.1:*");
        Assert.assertThat(brc, CoreMatchers.is(true));
        String stest = ((String) (ZMQ.getSocketOptionExt(socket, ZMQ_LAST_ENDPOINT)));
        Assert.assertThat(stest, CoreMatchers.is(CoreMatchers.not("tcp://127.0.0.1:*")));
        Assert.assertThat(stest, CoreMatchers.is(CoreMatchers.not("tcp://127.0.0.1:0")));
        Assert.assertThat(stest.matches("tcp://127.0.0.1:\\d+"), CoreMatchers.is(true));
        socket.close();
        ctx.terminate();
    }

    @Test
    public void testLastEndpointAllWildcards() throws IOException {
        // Create the infrastructure
        Ctx ctx = ZMQ.init(1);
        Assert.assertThat(ctx, CoreMatchers.notNullValue());
        SocketBase socket = ZMQ.socket(ctx, ZMQ_ROUTER);
        Assert.assertThat(socket, CoreMatchers.notNullValue());
        boolean brc = ZMQ.bind(socket, "tcp://*:*");
        Assert.assertThat(brc, CoreMatchers.is(true));
        String stest = ((String) (ZMQ.getSocketOptionExt(socket, ZMQ_LAST_ENDPOINT)));
        Assert.assertThat(stest, CoreMatchers.is(CoreMatchers.not("tcp://0.0.0.0:0")));
        Assert.assertThat(stest.matches("tcp://0.0.0.0:\\d+"), CoreMatchers.is(true));
        socket.close();
        ctx.terminate();
    }
}

