package zmq.io;


import ZMQ.Event;
import ZMQ.ZMQ_EVENT_DISCONNECTED;
import ZMQ.ZMQ_HANDSHAKE_IVL;
import ZMQ.ZMQ_LAST_ENDPOINT;
import ZMQ.ZMQ_PAIR;
import ZMQ.ZMQ_REP;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicReference;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import zmq.Ctx;
import zmq.SocketBase;
import zmq.ZError;
import zmq.ZMQ;
import zmq.util.TestUtils;


public class TimerEventTest {
    static class SocketMonitor extends Thread {
        private final Ctx ctx;

        private final String monitorAddr;

        private final AtomicReference<ZMQ.Event> event = new AtomicReference<>();

        public SocketMonitor(Ctx ctx, String monitorAddr) {
            this.ctx = ctx;
            this.monitorAddr = monitorAddr;
        }

        @Override
        public void run() {
            SocketBase monitor = ZMQ.socket(ctx, ZMQ_PAIR);
            Assert.assertThat(monitor, CoreMatchers.notNullValue());
            boolean rc = monitor.connect(monitorAddr);
            Assert.assertThat(rc, CoreMatchers.is(true));
            ZMQ.Event event = Event.read(monitor);
            if ((event == null) && ((monitor.errno()) == (ZError.ETERM))) {
                monitor.close();
                return;
            }
            Assert.assertThat(event, CoreMatchers.notNullValue());
            this.event.set(event);
            monitor.close();
        }
    }

    @Test
    public void testHandshakeTimeout() throws IOException, InterruptedException {
        int handshakeInterval = 10;
        Ctx ctx = ZMQ.createContext();
        Assert.assertThat(ctx, CoreMatchers.notNullValue());
        SocketBase socket = ctx.createSocket(ZMQ_REP);
        Assert.assertThat(socket, CoreMatchers.notNullValue());
        boolean rc = ZMQ.setSocketOption(socket, ZMQ_HANDSHAKE_IVL, handshakeInterval);
        Assert.assertThat(rc, CoreMatchers.is(true));
        rc = ZMQ.monitorSocket(socket, "inproc://monitor", ZMQ_EVENT_DISCONNECTED);
        Assert.assertThat(rc, CoreMatchers.is(true));
        TimerEventTest.SocketMonitor monitor = new TimerEventTest.SocketMonitor(ctx, "inproc://monitor");
        monitor.start();
        rc = ZMQ.bind(socket, "tcp://127.0.0.1:*");
        Assert.assertThat(rc, CoreMatchers.is(true));
        String endpoint = ((String) (ZMQ.getSocketOptionExt(socket, ZMQ_LAST_ENDPOINT)));
        Assert.assertThat(endpoint, CoreMatchers.notNullValue());
        Socket sender = new Socket("127.0.0.1", TestUtils.port(endpoint));
        OutputStream out = sender.getOutputStream();
        out.write(incompleteHandshake());
        out.flush();
        monitor.join();
        // there shall be a disconnected event because of the handshake timeout
        final ZMQ.Event event = monitor.event.get();
        Assert.assertThat(event, CoreMatchers.notNullValue());
        Assert.assertThat(event.event, CoreMatchers.is(ZMQ_EVENT_DISCONNECTED));
        out.close();
        sender.close();
        ZMQ.close(socket);
        ZMQ.term(ctx);
    }
}

