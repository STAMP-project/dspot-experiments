package zmq;


import java.util.ArrayList;
import java.util.List;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import zmq.socket.Sockets;


public class CtxTest {
    @Test
    public void testSeveralPendingInprocSocketsAreClosedIssue595() {
        Ctx ctx = ZMQ.init(0);
        Assert.assertThat(ctx, CoreMatchers.notNullValue());
        List<SocketBase> sockets = new ArrayList<>();
        for (Sockets type : Sockets.values()) {
            for (int idx = 0; idx < 3; ++idx) {
                SocketBase socket = ZMQ.socket(ctx, type.ordinal());
                Assert.assertThat(socket, CoreMatchers.notNullValue());
                boolean rc = socket.connect(("inproc://" + (type.name())));
                Assert.assertThat(rc, CoreMatchers.is(true));
                sockets.add(socket);
            }
        }
        sockets.stream().forEach(ZMQ::close);
        ZMQ.term(ctx);
        Assert.assertThat(ctx.checkTag(), CoreMatchers.is(false));
    }
}

