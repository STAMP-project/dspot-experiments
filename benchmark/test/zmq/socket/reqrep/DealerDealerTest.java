package zmq.socket.reqrep;


import ZMQ.ZMQ_DEALER;
import java.io.IOException;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import zmq.Ctx;
import zmq.Msg;
import zmq.SocketBase;
import zmq.ZMQ;
import zmq.util.Utils;


public class DealerDealerTest {
    @Test
    public void testIssue131() throws IOException {
        Ctx ctx = ZMQ.createContext();
        Assert.assertThat(ctx, CoreMatchers.notNullValue());
        SocketBase sender = ZMQ.socket(ctx, ZMQ_DEALER);
        Assert.assertThat(sender, CoreMatchers.notNullValue());
        final int port = Utils.findOpenPort();
        final String addr = "tcp://localhost:" + port;
        boolean rc = ZMQ.connect(sender, addr);
        Assert.assertThat(rc, CoreMatchers.is(true));
        byte[] sbuf = msg(255);
        int sent = ZMQ.send(sender, sbuf, 0);
        Assert.assertThat(sent, CoreMatchers.is(255));
        byte[] quit = new byte[]{ 'q' };
        sent = ZMQ.send(sender, quit, 0);
        Assert.assertThat(sent, CoreMatchers.is(1));
        ZMQ.close(sender);
        SocketBase receiver = ZMQ.socket(ctx, ZMQ_DEALER);
        Assert.assertThat(receiver, CoreMatchers.notNullValue());
        rc = ZMQ.bind(receiver, addr);
        Assert.assertThat(rc, CoreMatchers.is(true));
        int nbytes = 0;
        do {
            Msg msg = ZMQ.recv(receiver, 0);
            nbytes = msg.size();
            System.out.println(msg);
        } while (nbytes != 1 );
        ZMQ.close(receiver);
        ZMQ.term(ctx);
    }
}

