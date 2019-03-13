package zmq.socket.reqrep;


import ZMQ.ZMQ_RCVTIMEO;
import ZMQ.ZMQ_REP;
import ZMQ.ZMQ_REQ;
import ZMQ.ZMQ_REQ_CORRELATE;
import ZMQ.ZMQ_REQ_RELAXED;
import ZMQ.ZMQ_ROUTER;
import java.util.ArrayList;
import java.util.List;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import zmq.Ctx;
import zmq.Helper;
import zmq.SocketBase;
import zmq.ZMQ;


public class TestReqRelaxed {
    @Test
    public void testReqRelaxed() throws Exception {
        Ctx ctx = ZMQ.init(1);
        Assert.assertThat(ctx, CoreMatchers.notNullValue());
        SocketBase req = ZMQ.socket(ctx, ZMQ_REQ);
        Assert.assertThat(req, CoreMatchers.notNullValue());
        boolean rc;
        rc = req.setSocketOpt(ZMQ_REQ_CORRELATE, true);
        Assert.assertThat(rc, CoreMatchers.is(true));
        rc = req.setSocketOpt(ZMQ_REQ_RELAXED, true);
        Assert.assertThat(rc, CoreMatchers.is(true));
        rc = ZMQ.bind(req, "inproc://a");
        Assert.assertThat(rc, CoreMatchers.is(true));
        int services = 5;
        List<SocketBase> reps = new ArrayList<>();
        for (int idx = 0; idx < services; ++idx) {
            SocketBase rep = ctx.createSocket(ZMQ_REP);
            Assert.assertThat(rep, CoreMatchers.notNullValue());
            reps.add(rep);
            rc = req.setSocketOpt(ZMQ_RCVTIMEO, 500);
            Assert.assertThat(rc, CoreMatchers.is(true));
            rc = rep.connect("inproc://a");
            Assert.assertThat(rc, CoreMatchers.is(true));
        }
        // We have to give the connects time to finish otherwise the requests
        // will not properly round-robin. We could alternatively connect the
        // REQ sockets to the REP sockets.
        ZMQ.msleep(100);
        // Case 1: Second send() before a reply arrives in a pipe.
        // Send a request, ensure it arrives, don't send a reply
        Helper.sendSeq(req, "A", "B");
        Helper.recvSeq(reps.get(0), "A", "B");
        // Send another request on the REQ socket
        Helper.sendSeq(req, "C", "D");
        Helper.recvSeq(reps.get(1), "C", "D");
        // Send a reply to the first request - that should be discarded by the REQ
        Helper.sendSeq(reps.get(0), "WRONG");
        // Send the expected reply
        Helper.sendSeq(reps.get(1), "OK");
        Helper.recvSeq(req, "OK");
        // Another standard req-rep cycle, just to check
        Helper.sendSeq(req, "E");
        Helper.recvSeq(reps.get(2), "E");
        Helper.sendSeq(reps.get(2), "F", "G");
        Helper.recvSeq(req, "F", "G");
        // Case 2: Second send() after a reply is already in a pipe on the REQ.
        // Send a request, ensure it arrives, send a reply
        Helper.sendSeq(req, "H");
        Helper.recvSeq(reps.get(3), "H");
        Helper.sendSeq(reps.get(3), "BAD");
        // Wait for message to be there.
        ZMQ.msleep(100);
        // Without receiving that reply, send another request on the REQ socket
        Helper.sendSeq(req, "I");
        Helper.recvSeq(reps.get(4), "I");
        // Send the expected reply
        Helper.sendSeq(reps.get(4), "GOOD");
        Helper.recvSeq(req, "GOOD");
        // Case 3: Check issue #1690. Two send() in a row should not close the
        // communication pipes. For example pipe from req to rep[0] should not be
        // closed after executing Case 1. So rep[0] should be the next to receive,
        // not rep[1].
        Helper.sendSeq(req, "J");
        Helper.recvSeq(reps.get(0), "J");
        ZMQ.closeZeroLinger(req);
        for (SocketBase rep : reps) {
            ZMQ.closeZeroLinger(rep);
        }
        // Wait for disconnects.
        ZMQ.msleep(100);
        ZMQ.term(ctx);
    }

    @Test
    public void testIssueLibzmq1965() {
        Ctx ctx = ZMQ.init(1);
        Assert.assertThat(ctx, CoreMatchers.notNullValue());
        SocketBase req;
        boolean rc;
        // Case 4: Check issue #1695. As messages may pile up before a responder
        // is available, we check that responses to messages other than the last
        // sent one are correctly discarded by the REQ pipe
        // Setup REQ socket as client
        req = ZMQ.socket(ctx, ZMQ_REQ);
        Assert.assertThat(req, CoreMatchers.notNullValue());
        rc = req.setSocketOpt(ZMQ_REQ_CORRELATE, true);
        Assert.assertThat(rc, CoreMatchers.is(true));
        rc = req.setSocketOpt(ZMQ_REQ_RELAXED, true);
        Assert.assertThat(rc, CoreMatchers.is(true));
        rc = ZMQ.connect(req, "inproc://b");
        Assert.assertThat(rc, CoreMatchers.is(true));
        // Setup ROUTER socket as server but do not bind it just yet
        SocketBase router = ZMQ.socket(ctx, ZMQ_ROUTER);
        Assert.assertThat(router, CoreMatchers.notNullValue());
        // Send two requests
        Helper.sendSeq(req, "TO_BE_DISCARDED");
        Helper.sendSeq(req, "TO_BE_ANSWERED");
        // Bind server allowing it to receive messages
        rc = ZMQ.bind(router, "inproc://b");
        Assert.assertThat(rc, CoreMatchers.is(true));
        // Read the two messages and send them back as is
        bounce(router);
        bounce(router);
        Helper.recvSeq(req, "TO_BE_ANSWERED");
        ZMQ.closeZeroLinger(req);
        ZMQ.closeZeroLinger(router);
        ctx.terminate();
    }
}

