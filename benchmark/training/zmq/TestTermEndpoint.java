package zmq;


import ZError.ENOENT;
import ZMQ.ZMQ_DONTWAIT;
import ZMQ.ZMQ_LAST_ENDPOINT;
import ZMQ.ZMQ_PULL;
import ZMQ.ZMQ_PUSH;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;

import static ZError.EADDRINUSE;


public class TestTermEndpoint {
    @Test
    public void testUnbindWildcard() {
        String ep = endpointWildcard();
        Ctx ctx = ZMQ.init(1);
        Assert.assertThat(ctx, CoreMatchers.notNullValue());
        SocketBase push = ZMQ.socket(ctx, ZMQ_PUSH);
        Assert.assertThat(push, CoreMatchers.notNullValue());
        boolean rc = ZMQ.bind(push, ep);
        Assert.assertThat(rc, CoreMatchers.is(true));
        ep = ((String) (ZMQ.getSocketOptionExt(push, ZMQ_LAST_ENDPOINT)));
        SocketBase pull = ZMQ.socket(ctx, ZMQ_PULL);
        Assert.assertThat(pull, CoreMatchers.notNullValue());
        rc = ZMQ.connect(pull, ep);
        Assert.assertThat(rc, CoreMatchers.is(true));
        // Pass one message through to ensure the connection is established.
        int r = ZMQ.send(push, "ABC", 0);
        Assert.assertThat(r, CoreMatchers.is(3));
        Msg msg = ZMQ.recv(pull, 0);
        Assert.assertThat(msg.size(), CoreMatchers.is(3));
        // Unbind the lisnening endpoint
        rc = ZMQ.unbind(push, ep);
        Assert.assertThat(rc, CoreMatchers.is(true));
        // Let events some time
        ZMQ.sleep(1);
        // Check that sending would block (there's no outbound connection).
        r = ZMQ.send(push, "ABC", ZMQ_DONTWAIT);
        Assert.assertThat(r, CoreMatchers.is((-1)));
        // Clean up.
        ZMQ.close(pull);
        ZMQ.close(push);
        ZMQ.term(ctx);
    }

    @Test
    public void testDisconnectWildcard() {
        String ep = endpointWildcard();
        Ctx ctx = ZMQ.init(1);
        Assert.assertThat(ctx, CoreMatchers.notNullValue());
        SocketBase push = ZMQ.socket(ctx, ZMQ_PUSH);
        Assert.assertThat(push, CoreMatchers.notNullValue());
        boolean rc = ZMQ.bind(push, ep);
        Assert.assertThat(rc, CoreMatchers.is(true));
        ep = ((String) (ZMQ.getSocketOptionExt(push, ZMQ_LAST_ENDPOINT)));
        SocketBase pull = ZMQ.socket(ctx, ZMQ_PULL);
        Assert.assertThat(pull, CoreMatchers.notNullValue());
        rc = ZMQ.connect(pull, ep);
        Assert.assertThat(rc, CoreMatchers.is(true));
        // Pass one message through to ensure the connection is established.
        int r = ZMQ.send(push, "ABC", 0);
        Assert.assertThat(r, CoreMatchers.is(3));
        Msg msg = ZMQ.recv(pull, 0);
        Assert.assertThat(msg.size(), CoreMatchers.is(3));
        // Disconnect the bound endpoint
        rc = ZMQ.disconnect(push, ep);
        Assert.assertThat(rc, CoreMatchers.is(true));
        // Let events some time
        ZMQ.sleep(1);
        // Check that sending would block (there's no outbound connection).
        r = ZMQ.send(push, "ABC", ZMQ_DONTWAIT);
        Assert.assertThat(r, CoreMatchers.is((-1)));
        // Clean up.
        ZMQ.close(pull);
        ZMQ.close(push);
        ZMQ.term(ctx);
    }

    @Test
    public void testUnbindWildcardByWildcard() {
        String ep = endpointWildcard();
        Ctx ctx = ZMQ.init(1);
        Assert.assertThat(ctx, CoreMatchers.notNullValue());
        SocketBase push = ZMQ.socket(ctx, ZMQ_PUSH);
        Assert.assertThat(push, CoreMatchers.notNullValue());
        boolean rc = ZMQ.bind(push, ep);
        Assert.assertThat(rc, CoreMatchers.is(true));
        // Unbind the lisnening endpoint
        rc = ZMQ.unbind(push, ep);
        Assert.assertThat(rc, CoreMatchers.is(false));
        Assert.assertThat(push.errno.get(), CoreMatchers.is(ENOENT));
        // Clean up.
        ZMQ.close(push);
        ZMQ.term(ctx);
    }

    @Test
    public void testDisconnectWildcardByWildcard() {
        String ep = endpointWildcard();
        Ctx ctx = ZMQ.init(1);
        Assert.assertThat(ctx, CoreMatchers.notNullValue());
        SocketBase push = ZMQ.socket(ctx, ZMQ_PUSH);
        Assert.assertThat(push, CoreMatchers.notNullValue());
        boolean rc = ZMQ.bind(push, ep);
        Assert.assertThat(rc, CoreMatchers.is(true));
        // Disconnect the bound endpoint
        rc = ZMQ.disconnect(push, ep);
        Assert.assertThat(rc, CoreMatchers.is(false));
        Assert.assertThat(push.errno.get(), CoreMatchers.is(ENOENT));
        // Clean up.
        ZMQ.close(push);
        ZMQ.term(ctx);
    }

    @Test
    public void testUnbind() throws Exception {
        Ctx ctx = ZMQ.init(1);
        Assert.assertThat(ctx, CoreMatchers.notNullValue());
        SocketBase push = ZMQ.socket(ctx, ZMQ_PUSH);
        Assert.assertThat(push, CoreMatchers.notNullValue());
        String ep = null;
        boolean rc = false;
        do {
            ep = endpointNormal();
            // we might have to repeat until we find an open port
            rc = ZMQ.bind(push, ep);
        } while ((!rc) && ((push.errno()) == (EADDRINUSE)) );
        SocketBase pull = ZMQ.socket(ctx, ZMQ_PULL);
        Assert.assertThat(pull, CoreMatchers.notNullValue());
        rc = ZMQ.connect(pull, ep);
        Assert.assertThat(rc, CoreMatchers.is(true));
        // Pass one message through to ensure the connection is established.
        int r = ZMQ.send(push, "ABC", 0);
        Assert.assertThat(r, CoreMatchers.is(3));
        Msg msg = ZMQ.recv(pull, 0);
        Assert.assertThat(msg.size(), CoreMatchers.is(3));
        // Unbind the lisnening endpoint
        rc = ZMQ.unbind(push, ep);
        Assert.assertThat(rc, CoreMatchers.is(true));
        // Let events some time
        ZMQ.sleep(1);
        // Check that sending would block (there's no outbound connection).
        r = ZMQ.send(push, "ABC", ZMQ_DONTWAIT);
        Assert.assertThat(r, CoreMatchers.is((-1)));
        // Clean up.
        ZMQ.close(pull);
        ZMQ.close(push);
        ZMQ.term(ctx);
    }

    @Test
    public void testDisconnect() throws Exception {
        Ctx ctx = ZMQ.init(1);
        Assert.assertThat(ctx, CoreMatchers.notNullValue());
        SocketBase push = ZMQ.socket(ctx, ZMQ_PUSH);
        Assert.assertThat(push, CoreMatchers.notNullValue());
        String ep = null;
        boolean rc = false;
        do {
            ep = endpointNormal();
            // we might have to repeat until we find an open port
            rc = ZMQ.bind(push, ep);
        } while ((!rc) && ((push.errno()) == (EADDRINUSE)) );
        SocketBase pull = ZMQ.socket(ctx, ZMQ_PULL);
        Assert.assertThat(pull, CoreMatchers.notNullValue());
        rc = ZMQ.connect(pull, ep);
        Assert.assertThat(rc, CoreMatchers.is(true));
        // Pass one message through to ensure the connection is established.
        int r = ZMQ.send(push, "ABC", 0);
        Assert.assertThat(r, CoreMatchers.is(3));
        Msg msg = ZMQ.recv(pull, 0);
        Assert.assertThat(msg.size(), CoreMatchers.is(3));
        // Disconnect the bound endpoint
        rc = ZMQ.disconnect(push, ep);
        Assert.assertThat(rc, CoreMatchers.is(true));
        // Let events some time
        ZMQ.sleep(1);
        // Check that sending would block (there's no outbound connection).
        r = ZMQ.send(push, "ABC", ZMQ_DONTWAIT);
        Assert.assertThat(r, CoreMatchers.is((-1)));
        // Clean up.
        ZMQ.close(pull);
        ZMQ.close(push);
        ZMQ.term(ctx);
    }
}

