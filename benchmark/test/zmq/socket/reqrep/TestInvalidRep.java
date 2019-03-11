package zmq.socket.reqrep;


import ZMQ.ZMQ_LINGER;
import ZMQ.ZMQ_REQ;
import ZMQ.ZMQ_ROUTER;
import ZMQ.ZMQ_SNDMORE;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import zmq.Ctx;
import zmq.Msg;
import zmq.SocketBase;
import zmq.ZMQ;


public class TestInvalidRep {
    // Create REQ/ROUTER wiring.
    @Test
    public void testInvalidRep() {
        Ctx ctx = ZMQ.init(1);
        Assert.assertThat(ctx, CoreMatchers.notNullValue());
        SocketBase routerSocket = ZMQ.socket(ctx, ZMQ_ROUTER);
        Assert.assertThat(routerSocket, CoreMatchers.notNullValue());
        SocketBase reqSocket = ZMQ.socket(ctx, ZMQ_REQ);
        Assert.assertThat(reqSocket, CoreMatchers.notNullValue());
        int linger = 0;
        int rc;
        ZMQ.setSocketOption(routerSocket, ZMQ_LINGER, linger);
        ZMQ.setSocketOption(reqSocket, ZMQ_LINGER, linger);
        boolean brc = ZMQ.bind(routerSocket, "inproc://hi");
        Assert.assertThat(brc, CoreMatchers.is(true));
        brc = ZMQ.connect(reqSocket, "inproc://hi");
        Assert.assertThat(brc, CoreMatchers.is(true));
        // Initial request.
        rc = ZMQ.send(reqSocket, "r", 0);
        Assert.assertThat(rc, CoreMatchers.is(1));
        // Receive the request.
        Msg addr;
        Msg bottom;
        Msg body;
        addr = ZMQ.recv(routerSocket, 0);
        int addrSize = addr.size();
        System.out.println(("addrSize: " + (addr.size())));
        Assert.assertThat(((addr.size()) > 0), CoreMatchers.is(true));
        bottom = ZMQ.recv(routerSocket, 0);
        Assert.assertThat(bottom.size(), CoreMatchers.is(0));
        body = ZMQ.recv(routerSocket, 0);
        Assert.assertThat(body.size(), CoreMatchers.is(1));
        Assert.assertThat(body.data()[0], CoreMatchers.is(((byte) ('r'))));
        // Send invalid reply.
        rc = ZMQ.send(routerSocket, addr, 0);
        Assert.assertThat(rc, CoreMatchers.is(addrSize));
        // Send valid reply.
        rc = ZMQ.send(routerSocket, addr, ZMQ_SNDMORE);
        Assert.assertThat(rc, CoreMatchers.is(addrSize));
        rc = ZMQ.send(routerSocket, bottom, ZMQ_SNDMORE);
        Assert.assertThat(rc, CoreMatchers.is(0));
        rc = ZMQ.send(routerSocket, "b", 0);
        Assert.assertThat(rc, CoreMatchers.is(1));
        // Check whether we've got the valid reply.
        body = ZMQ.recv(reqSocket, 0);
        Assert.assertThat(body.size(), CoreMatchers.is(1));
        Assert.assertThat(body.data()[0], CoreMatchers.is(((byte) ('b'))));
        // Tear down the wiring.
        ZMQ.close(routerSocket);
        ZMQ.close(reqSocket);
        ZMQ.term(ctx);
    }
}

