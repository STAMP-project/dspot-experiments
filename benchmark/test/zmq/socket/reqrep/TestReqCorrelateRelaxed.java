package zmq.socket.reqrep;


import ZMQ.ZMQ_DEALER;
import ZMQ.ZMQ_RCVTIMEO;
import ZMQ.ZMQ_REQ;
import ZMQ.ZMQ_REQ_CORRELATE;
import ZMQ.ZMQ_REQ_RELAXED;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import zmq.Ctx;
import zmq.SocketBase;
import zmq.ZMQ;


/**
 * This test does one setup, and runs a few tests on that setup.
 */
public class TestReqCorrelateRelaxed {
    static final int REQUEST_ID_LENGTH = 4;

    private static final String PAYLOAD = "Payload";

    /**
     * Prepares sockets and runs actual tests.
     *
     * Doing it this way so order is guaranteed.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void overallSetup() throws Exception {
        Ctx ctx = ZMQ.init(1);
        Assert.assertThat(ctx, CoreMatchers.notNullValue());
        SocketBase dealer = ZMQ.socket(ctx, ZMQ_DEALER);
        Assert.assertThat(dealer, CoreMatchers.notNullValue());
        boolean brc = ZMQ.bind(dealer, "inproc://a");
        Assert.assertThat(brc, CoreMatchers.is(true));
        SocketBase reqClient = ZMQ.socket(ctx, ZMQ_REQ);
        Assert.assertThat(reqClient, CoreMatchers.notNullValue());
        reqClient.setSocketOpt(ZMQ_REQ_CORRELATE, 1);
        reqClient.setSocketOpt(ZMQ_REQ_RELAXED, 1);
        reqClient.setSocketOpt(ZMQ_RCVTIMEO, 100);
        brc = ZMQ.connect(reqClient, "inproc://a");
        Assert.assertThat(brc, CoreMatchers.is(true));
        // Test good path
        byte[] origRequestId = testReqSentFrames(dealer, reqClient);
        testReqRecvGoodRequestId(dealer, reqClient, origRequestId);
        // Test what happens when a bad request ID is sent back.
        origRequestId = testReqSentFrames(dealer, reqClient);
        testReqRecvBadRequestId(dealer, reqClient, origRequestId);
        ZMQ.close(reqClient);
        ZMQ.close(dealer);
        ZMQ.term(ctx);
    }
}

