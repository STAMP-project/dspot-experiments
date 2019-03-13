package zmq.socket.reqrep;


import ZMQ.ZMQ_DEALER;
import ZMQ.ZMQ_REP;
import ZMQ.ZMQ_REQ;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import org.junit.Test;
import zmq.Ctx;
import zmq.ZMQ;
import zmq.socket.AbstractSpecTest;


public class RepSpecTest extends AbstractSpecTest {
    @Test
    public void testSpecFairQueueIn() throws IOException, InterruptedException {
        Ctx ctx = ZMQ.createContext();
        List<String> binds = Arrays.asList("inproc://a", "tcp://127.0.0.1:*");
        for (String bindAddress : binds) {
            // SHALL receive incoming messages from its peers using a fair-queuing
            // strategy.
            fairQueueIn(ctx, bindAddress, ZMQ_REP, ZMQ_REQ);
        }
        ZMQ.term(ctx);
    }

    @Test
    public void testSpecEnvelope() throws IOException, InterruptedException {
        Ctx ctx = ZMQ.createContext();
        List<String> binds = Arrays.asList("inproc://a", "tcp://127.0.0.1:*");
        for (String bindAddress : binds) {
            // For an incoming message:
            // SHALL remove and store the address envelope, including the delimiter.
            // SHALL pass the remaining data frames to its calling application.
            // SHALL wait for a single reply message from its calling application.
            // SHALL prepend the address envelope and delimiter.
            // SHALL deliver this message back to the originating peer.
            envelope(ctx, bindAddress, ZMQ_REP, ZMQ_DEALER);
        }
        ZMQ.term(ctx);
    }
}

