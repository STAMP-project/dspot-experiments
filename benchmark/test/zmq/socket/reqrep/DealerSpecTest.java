package zmq.socket.reqrep;


import ZMQ.ZMQ_DEALER;
import ZMQ.ZMQ_REP;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import org.junit.Test;
import zmq.Ctx;
import zmq.ZMQ;
import zmq.socket.AbstractSpecTest;


public class DealerSpecTest extends AbstractSpecTest {
    @Test
    public void testSpecFairQueueIn() throws IOException, InterruptedException {
        Ctx ctx = ZMQ.createContext();
        List<String> binds = Arrays.asList("inproc://a", "tcp://127.0.0.1:*");
        for (String bindAddress : binds) {
            // SHALL receive incoming messages from its peers using a fair-queuing
            // strategy.
            fairQueueIn(ctx, bindAddress, ZMQ_DEALER, ZMQ_DEALER);
        }
        ZMQ.term(ctx);
    }

    @Test
    public void testSpecRoundRobinOut() throws IOException, InterruptedException {
        Ctx ctx = ZMQ.createContext();
        List<String> binds = Arrays.asList("inproc://a", "tcp://127.0.0.1:*");
        for (String bindAddress : binds) {
            // SHALL route outgoing messages to available peers using a round-robin
            // strategy.
            roundRobinOut(ctx, bindAddress, ZMQ_DEALER, ZMQ_REP);
        }
        ZMQ.term(ctx);
    }

    @Test
    public void testSpecBlockOnSendNoPeers() throws IOException, InterruptedException {
        Ctx ctx = ZMQ.createContext();
        List<String> binds = Arrays.asList("inproc://a", "tcp://127.0.0.1:*");
        for (String bindAddress : binds) {
            // SHALL block on sending, or return a suitable error, when it has no connected peers.
            blockOnSendNoPeers(ctx, bindAddress, ZMQ_DEALER);
        }
        ZMQ.term(ctx);
    }
}

