package zmq.socket.reqrep;


import ZMQ.ZMQ_REP;
import ZMQ.ZMQ_REQ;
import ZMQ.ZMQ_ROUTER;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import org.junit.Test;
import zmq.Ctx;
import zmq.ZMQ;
import zmq.socket.AbstractSpecTest;


public class ReqSpecTest extends AbstractSpecTest {
    @Test
    public void testSpecMessageFormat() throws IOException, InterruptedException {
        Ctx ctx = ZMQ.createContext();
        List<String> binds = Arrays.asList("inproc://a", "tcp://127.0.0.1:*");
        for (String bindAddress : binds) {
            // The request and reply messages SHALL have this format on the wire:
            // * A delimiter, consisting of an empty frame, added by the REQ socket.
            // * One or more data frames, comprising the message visible to the
            // application.
            messageFormat(ctx, bindAddress, ZMQ_REQ, ZMQ_ROUTER);
        }
        ZMQ.term(ctx);
    }

    @Test
    public void testSpecRoundRobinOut() throws IOException, InterruptedException {
        Ctx ctx = ZMQ.createContext();
        List<String> binds = Arrays.asList("inproc://a", "tcp://127.0.0.1:*");
        for (String bindAddress : binds) {
            // SHALL route outgoing messages to connected peers using a round-robin
            // strategy.
            roundRobinOut(ctx, bindAddress, ZMQ_REQ, ZMQ_REP);
        }
        ZMQ.term(ctx);
    }

    @Test
    public void testSpecBlockOnSendNoPeers() throws IOException, InterruptedException {
        Ctx ctx = ZMQ.createContext();
        List<String> binds = Arrays.asList("inproc://a", "tcp://127.0.0.1:*");
        for (String bindAddress : binds) {
            // SHALL block on sending, or return a suitable error, when it has no
            // connected peers.
            blockOnSendNoPeers(ctx, bindAddress, ZMQ_REQ);
        }
        ZMQ.term(ctx);
    }
}

