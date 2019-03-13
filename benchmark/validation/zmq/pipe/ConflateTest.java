package zmq.pipe;


import ZMQ.ZMQ_CONFLATE;
import ZMQ.ZMQ_LAST_ENDPOINT;
import ZMQ.ZMQ_PULL;
import ZMQ.ZMQ_PUSH;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;
import zmq.Ctx;
import zmq.Helper;
import zmq.SocketBase;
import zmq.ZMQ;


public class ConflateTest {
    @Test
    public void test() throws IOException, InterruptedException {
        Ctx ctx = ZMQ.init(1);
        assert ctx != null;
        SocketBase in = ZMQ.socket(ctx, ZMQ_PULL);
        assert in != null;
        String host = "tcp://localhost:*";
        int conflate = 1;
        ZMQ.setSocketOption(in, ZMQ_CONFLATE, conflate);
        boolean rc = ZMQ.bind(in, host);
        assert rc;
        SocketBase out = ZMQ.socket(ctx, ZMQ_PUSH);
        assert out != null;
        String ep = ((String) (ZMQ.getSocketOptionExt(in, ZMQ_LAST_ENDPOINT)));
        rc = ZMQ.connect(out, ep);
        assert rc;
        int messageCount = 20;
        for (int j = 0; j < messageCount; ++j) {
            int count = Helper.send(out, Integer.toString(j));
            assert count > 0;
        }
        Thread.sleep(200);
        String recvd = Helper.recv(in);
        Assert.assertEquals("19", recvd);
        ZMQ.close(in);
        ZMQ.close(out);
        ZMQ.term(ctx);
    }
}

