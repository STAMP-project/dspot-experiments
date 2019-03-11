package zmq.socket.pipeline;


import ZMQ.ZMQ_PULL;
import ZMQ.ZMQ_PUSH;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import org.junit.Test;
import zmq.Ctx;
import zmq.ZMQ;
import zmq.socket.AbstractSpecTest;


public class PushPullSpecTest extends AbstractSpecTest {
    @Test
    public void testSpecPullFairQueueIn() throws IOException, InterruptedException {
        Ctx ctx = ZMQ.createContext();
        List<String> binds = Arrays.asList("inproc://a", "tcp://127.0.0.1:*");
        for (String bindAddress : binds) {
            // PULL: SHALL receive incoming messages from its peers using a fair-queuing
            // strategy.
            fairQueueIn(ctx, bindAddress, ZMQ_PULL, ZMQ_PUSH);
        }
        ZMQ.term(ctx);
    }

    @Test
    public void testSpecPushRoundRobinOut() throws IOException, InterruptedException {
        Ctx ctx = ZMQ.createContext();
        List<String> binds = Arrays.asList("inproc://a", "tcp://127.0.0.1:*");
        for (String bindAddress : binds) {
            // PUSH: SHALL route outgoing messages to connected peers using a
            // round-robin strategy.
            roundRobinOut(ctx, bindAddress, ZMQ_PUSH, ZMQ_PULL);
        }
        ZMQ.term(ctx);
    }

    @Test
    public void testSpecPushBlockOnSendNoPeers() throws IOException, InterruptedException {
        Ctx ctx = ZMQ.createContext();
        List<String> binds = Arrays.asList("inproc://a", "tcp://127.0.0.1:*");
        for (String bindAddress : binds) {
            // PUSH: SHALL block on sending, or return a suitable error, when it has no
            // available peers.
            blockOnSendNoPeers(ctx, bindAddress, ZMQ_PUSH);
        }
        ZMQ.term(ctx);
    }
}

