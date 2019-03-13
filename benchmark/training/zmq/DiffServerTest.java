package zmq;


import ZMQ.ZMQ_PAIR;
import ZMQ.ZMQ_TOS;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;
import zmq.util.Utils;


public class DiffServerTest {
    @Test
    public void test() throws IOException, InterruptedException {
        int port = Utils.findOpenPort();
        String host = "tcp://localhost:" + port;
        int tos = 40;
        Ctx ctx = ZMQ.init(1);
        assert ctx != null;
        SocketBase bind = ZMQ.socket(ctx, ZMQ_PAIR);
        assert bind != null;
        ZMQ.setSocketOption(bind, ZMQ_TOS, tos);
        boolean rc = ZMQ.bind(bind, host);
        assert rc;
        int option = ZMQ.getSocketOption(bind, ZMQ_TOS);
        Assert.assertEquals(tos, option);
        SocketBase connect = ZMQ.socket(ctx, ZMQ_PAIR);
        assert connect != null;
        tos = 88;
        ZMQ.setSocketOption(connect, ZMQ_TOS, tos);
        rc = ZMQ.connect(connect, host);
        assert rc;
        option = ZMQ.getSocketOption(connect, ZMQ_TOS);
        Assert.assertEquals(tos, option);
        // Wireshark can be used to verify that the server socket is
        // using DSCP 0x28 in packets to the client while the client
        // is using 0x58 in packets to the server.
        Helper.bounce(bind, connect);
        ZMQ.close(bind);
        ZMQ.close(connect);
        ZMQ.term(ctx);
    }
}

