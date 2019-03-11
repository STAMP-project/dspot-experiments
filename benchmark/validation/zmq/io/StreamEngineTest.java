package zmq.io;


import Config.OUT_BATCH_SIZE;
import ZMQ.ZMQ_IMMEDIATE;
import ZMQ.ZMQ_LAST_ENDPOINT;
import ZMQ.ZMQ_PULL;
import ZMQ.ZMQ_PUSH;
import java.io.IOException;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import zmq.Ctx;
import zmq.SocketBase;
import zmq.ZMQ;


public class StreamEngineTest {
    @Test
    public void testEncoderFlipIssue520() throws IOException {
        Ctx ctx = ZMQ.createContext();
        Assert.assertThat(ctx, CoreMatchers.notNullValue());
        SocketBase sender = ZMQ.socket(ctx, ZMQ_PUSH);
        Assert.assertThat(sender, CoreMatchers.notNullValue());
        boolean rc = ZMQ.setSocketOption(sender, ZMQ_IMMEDIATE, false);
        Assert.assertThat(rc, CoreMatchers.is(true));
        SocketBase receiver = ZMQ.socket(ctx, ZMQ_PULL);
        Assert.assertThat(receiver, CoreMatchers.notNullValue());
        String addr = "tcp://localhost:*";
        rc = ZMQ.bind(receiver, addr);
        Assert.assertThat(rc, CoreMatchers.is(true));
        addr = ((String) (ZMQ.getSocketOptionExt(receiver, ZMQ_LAST_ENDPOINT)));
        Assert.assertThat(addr, CoreMatchers.notNullValue());
        rc = ZMQ.connect(sender, addr);
        Assert.assertThat(rc, CoreMatchers.is(true));
        final int headerSize = 8 + 1;
        // first message + second message header fill the buffer
        byte[] msg1 = msg(((OUT_BATCH_SIZE.getValue()) - (2 * headerSize)));
        // second message will go in zero-copy mode
        byte[] msg2 = msg(OUT_BATCH_SIZE.getValue());
        exchange(sender, receiver, msg1, msg2, msg1, msg2);
        ZMQ.close(receiver);
        ZMQ.close(sender);
        ZMQ.term(ctx);
    }
}

