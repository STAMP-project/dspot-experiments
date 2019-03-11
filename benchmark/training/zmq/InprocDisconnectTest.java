package zmq;


import java.nio.channels.Selector;
import org.junit.Test;


public class InprocDisconnectTest {
    @Test
    public void testDisconnectInproc() throws Exception {
        Ctx context = ZMQ.createContext();
        Selector selector = context.createSelector();
        try {
            testDisconnectInproc(context, selector);
        } finally {
            context.closeSelector(selector);
        }
        ZMQ.term(context);
    }
}

