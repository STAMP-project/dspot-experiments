package zmq;


import ZMQ.ZMQ_REP;
import java.io.IOException;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class InprocUnbindTest {
    @Test
    public void testUnbindInproc() throws IOException, InterruptedException {
        Ctx ctx = ZMQ.init(1);
        assert ctx != null;
        SocketBase bind = ZMQ.socket(ctx, ZMQ_REP);
        Assert.assertThat(bind, CoreMatchers.notNullValue());
        boolean rc = ZMQ.bind(bind, "inproc://a");
        Assert.assertThat(rc, CoreMatchers.is(true));
        rc = ZMQ.unbind(bind, "inproc://a");
        Assert.assertThat(rc, CoreMatchers.is(true));
        ZMQ.close(bind);
        ZMQ.term(ctx);
    }
}

