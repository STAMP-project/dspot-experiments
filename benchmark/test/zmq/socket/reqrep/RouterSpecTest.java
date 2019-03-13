package zmq.socket.reqrep;


import ZMQ.ZMQ_DEALER;
import ZMQ.ZMQ_ROUTER;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import org.junit.Test;
import zmq.Ctx;
import zmq.ZMQ;
import zmq.socket.AbstractSpecTest;


public class RouterSpecTest extends AbstractSpecTest {
    @Test
    public void testFairQueueIn() throws IOException, InterruptedException {
        Ctx ctx = ZMQ.createContext();
        List<String> binds = Arrays.asList("inproc://a", "tcp://127.0.0.1:*");
        for (String bindAddress : binds) {
            // SHALL receive incoming messages from its peers using a fair-queuing
            // strategy.
            fairQueueIn(ctx, bindAddress, ZMQ_ROUTER, ZMQ_DEALER);
        }
        ZMQ.term(ctx);
    }
}

