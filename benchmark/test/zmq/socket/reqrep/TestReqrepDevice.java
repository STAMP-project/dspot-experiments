package zmq.socket.reqrep;


import ZMQ.ZMQ_DEALER;
import ZMQ.ZMQ_RCVMORE;
import ZMQ.ZMQ_REP;
import ZMQ.ZMQ_REQ;
import ZMQ.ZMQ_ROUTER;
import ZMQ.ZMQ_SNDMORE;
import java.io.IOException;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import zmq.Ctx;
import zmq.Msg;
import zmq.SocketBase;
import zmq.ZMQ;
import zmq.util.Utils;


public class TestReqrepDevice {
    // Create REQ/ROUTER wiring.
    @Test
    public void testReprepDevice() throws IOException {
        int routerPort = Utils.findOpenPort();
        int dealerPort = Utils.findOpenPort();
        boolean brc;
        Ctx ctx = ZMQ.init(1);
        Assert.assertThat(ctx, CoreMatchers.notNullValue());
        // Create a req/rep device.
        SocketBase dealer = ZMQ.socket(ctx, ZMQ_DEALER);
        Assert.assertThat(dealer, CoreMatchers.notNullValue());
        brc = ZMQ.bind(dealer, ("tcp://127.0.0.1:" + dealerPort));
        Assert.assertThat(brc, CoreMatchers.is(true));
        SocketBase router = ZMQ.socket(ctx, ZMQ_ROUTER);
        Assert.assertThat(router, CoreMatchers.notNullValue());
        brc = ZMQ.bind(router, ("tcp://127.0.0.1:" + routerPort));
        Assert.assertThat(brc, CoreMatchers.is(true));
        // Create a worker.
        SocketBase rep = ZMQ.socket(ctx, ZMQ_REP);
        Assert.assertThat(rep, CoreMatchers.notNullValue());
        brc = ZMQ.connect(rep, ("tcp://127.0.0.1:" + dealerPort));
        Assert.assertThat(brc, CoreMatchers.is(true));
        SocketBase req = ZMQ.socket(ctx, ZMQ_REQ);
        Assert.assertThat(req, CoreMatchers.notNullValue());
        brc = ZMQ.connect(req, ("tcp://127.0.0.1:" + routerPort));
        Assert.assertThat(brc, CoreMatchers.is(true));
        // Send a request.
        int rc;
        Msg msg;
        String buff;
        long rcvmore;
        rc = ZMQ.send(req, "ABC", ZMQ_SNDMORE);
        Assert.assertThat(rc, CoreMatchers.is(3));
        rc = ZMQ.send(req, "DEFG", 0);
        Assert.assertThat(rc, CoreMatchers.is(4));
        // Pass the request through the device.
        for (int i = 0; i != 4; i++) {
            msg = ZMQ.recvMsg(router, 0);
            Assert.assertThat(msg, CoreMatchers.notNullValue());
            rcvmore = ZMQ.getSocketOption(router, ZMQ_RCVMORE);
            rc = ZMQ.sendMsg(dealer, msg, (rcvmore > 0 ? ZMQ.ZMQ_SNDMORE : 0));
            Assert.assertThat((rc >= 0), CoreMatchers.is(true));
        }
        // Receive the request.
        msg = ZMQ.recv(rep, 0);
        Assert.assertThat(msg.size(), CoreMatchers.is(3));
        buff = new String(msg.data(), ZMQ.CHARSET);
        Assert.assertThat(buff, CoreMatchers.is("ABC"));
        rcvmore = ZMQ.getSocketOption(rep, ZMQ_RCVMORE);
        Assert.assertThat((rcvmore > 0), CoreMatchers.is(true));
        msg = ZMQ.recv(rep, 0);
        Assert.assertThat(msg.size(), CoreMatchers.is(4));
        buff = new String(msg.data(), ZMQ.CHARSET);
        Assert.assertThat(buff, CoreMatchers.is("DEFG"));
        rcvmore = ZMQ.getSocketOption(rep, ZMQ_RCVMORE);
        Assert.assertThat(rcvmore, CoreMatchers.is(0L));
        // Send the reply.
        rc = ZMQ.send(rep, "GHIJKL", ZMQ_SNDMORE);
        Assert.assertThat(rc, CoreMatchers.is(6));
        rc = ZMQ.send(rep, "MN", 0);
        Assert.assertThat(rc, CoreMatchers.is(2));
        // Pass the reply through the device.
        for (int i = 0; i != 4; i++) {
            msg = ZMQ.recvMsg(dealer, 0);
            Assert.assertThat(msg, CoreMatchers.notNullValue());
            rcvmore = ZMQ.getSocketOption(dealer, ZMQ_RCVMORE);
            rc = ZMQ.sendMsg(router, msg, (rcvmore > 0 ? ZMQ.ZMQ_SNDMORE : 0));
            Assert.assertThat((rc >= 0), CoreMatchers.is(true));
        }
        // Receive the reply.
        msg = ZMQ.recv(req, 0);
        Assert.assertThat(msg.size(), CoreMatchers.is(6));
        buff = new String(msg.data(), ZMQ.CHARSET);
        Assert.assertThat(buff, CoreMatchers.is("GHIJKL"));
        rcvmore = ZMQ.getSocketOption(req, ZMQ_RCVMORE);
        Assert.assertThat((rcvmore > 0), CoreMatchers.is(true));
        msg = ZMQ.recv(req, 0);
        Assert.assertThat(msg.size(), CoreMatchers.is(2));
        buff = new String(msg.data(), ZMQ.CHARSET);
        Assert.assertThat(buff, CoreMatchers.is("MN"));
        rcvmore = ZMQ.getSocketOption(req, ZMQ_RCVMORE);
        Assert.assertThat(rcvmore, CoreMatchers.is(0L));
        // Clean up.
        ZMQ.close(req);
        ZMQ.close(rep);
        ZMQ.close(router);
        ZMQ.close(dealer);
        ZMQ.term(ctx);
    }
}

