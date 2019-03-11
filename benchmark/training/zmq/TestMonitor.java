package zmq;


import ZError.EPROTONOSUPPORT;
import ZMQ.Event;
import ZMQ.ZMQ_EVENT_ALL;
import ZMQ.ZMQ_EVENT_CONNECTED;
import ZMQ.ZMQ_LAST_ENDPOINT;
import ZMQ.ZMQ_LINGER;
import ZMQ.ZMQ_PAIR;
import ZMQ.ZMQ_REP;
import ZMQ.ZMQ_REQ;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;

import static ZError.ETERM;
import static ZMQ.ZMQ_EVENT_ACCEPTED;
import static ZMQ.ZMQ_EVENT_CLOSED;
import static ZMQ.ZMQ_EVENT_CLOSE_FAILED;
import static ZMQ.ZMQ_EVENT_CONNECTED;
import static ZMQ.ZMQ_EVENT_CONNECT_DELAYED;
import static ZMQ.ZMQ_EVENT_DISCONNECTED;
import static ZMQ.ZMQ_EVENT_LISTENING;
import static ZMQ.ZMQ_EVENT_MONITOR_STOPPED;


public class TestMonitor {
    static class SocketMonitor extends Thread {
        private Ctx ctx;

        private int events;

        private String monitorAddr;

        public SocketMonitor(Ctx ctx, String monitorAddr) {
            this.ctx = ctx;
            this.monitorAddr = monitorAddr;
            events = 0;
        }

        @Override
        public void run() {
            SocketBase s = ZMQ.socket(ctx, ZMQ_PAIR);
            boolean rc = s.connect(monitorAddr);
            Assert.assertThat(rc, CoreMatchers.is(true));
            // Only some of the exceptional events could fire
            while (true) {
                ZMQ.Event event = Event.read(s);
                if ((event == null) && ((s.errno()) == (ETERM))) {
                    break;
                }
                Assert.assertThat(event, CoreMatchers.notNullValue());
                switch (event.event) {
                    // listener specific
                    case ZMQ_EVENT_LISTENING :
                    case ZMQ_EVENT_ACCEPTED :
                        // connecter specific
                    case ZMQ_EVENT_CONNECTED :
                    case ZMQ_EVENT_CONNECT_DELAYED :
                        // generic - either end of the socket
                    case ZMQ_EVENT_CLOSE_FAILED :
                    case ZMQ_EVENT_CLOSED :
                    case ZMQ_EVENT_DISCONNECTED :
                    case ZMQ_EVENT_MONITOR_STOPPED :
                        events |= event.event;
                        break;
                    default :
                        // out of band / unexpected event
                        Assert.assertTrue(("Unkown Event " + (event.event)), true);
                }
            } 
            s.close();
        }
    }

    @Test
    public void testMonitor() throws Exception {
        String addr = "tcp://127.0.0.1:*";
        TestMonitor.SocketMonitor[] threads = new TestMonitor.SocketMonitor[3];
        // Create the infrastructure
        Ctx ctx = ZMQ.init(1);
        Assert.assertThat(ctx, CoreMatchers.notNullValue());
        // set socket monitor
        SocketBase rep = ZMQ.socket(ctx, ZMQ_REP);
        Assert.assertThat(rep, CoreMatchers.notNullValue());
        boolean rc = ZMQ.monitorSocket(rep, addr, 0);
        Assert.assertThat(rc, CoreMatchers.is(false));
        Assert.assertThat(rep.errno.get(), CoreMatchers.is(EPROTONOSUPPORT));
        // REP socket monitor, all events
        rc = ZMQ.monitorSocket(rep, "inproc://monitor.rep", ZMQ_EVENT_ALL);
        Assert.assertThat(rc, CoreMatchers.is(true));
        threads[0] = new TestMonitor.SocketMonitor(ctx, "inproc://monitor.rep");
        threads[0].start();
        threads[1] = new TestMonitor.SocketMonitor(ctx, "inproc://monitor.req");
        threads[1].start();
        threads[2] = new TestMonitor.SocketMonitor(ctx, "inproc://monitor.req2");
        threads[2].start();
        ZMQ.sleep(1);
        rc = ZMQ.bind(rep, addr);
        Assert.assertThat(rc, CoreMatchers.is(true));
        addr = ((String) (ZMQ.getSocketOptionExt(rep, ZMQ_LAST_ENDPOINT)));
        Assert.assertThat(addr, CoreMatchers.notNullValue());
        SocketBase req = ZMQ.socket(ctx, ZMQ_REQ);
        Assert.assertThat(req, CoreMatchers.notNullValue());
        // REQ socket monitor, all events
        rc = ZMQ.monitorSocket(req, "inproc://monitor.req", ZMQ_EVENT_ALL);
        Assert.assertThat(rc, CoreMatchers.is(true));
        rc = ZMQ.connect(req, addr);
        Assert.assertThat(rc, CoreMatchers.is(true));
        // 2nd REQ socket
        SocketBase req2 = ZMQ.socket(ctx, ZMQ_REQ);
        Assert.assertThat(req2, CoreMatchers.notNullValue());
        // 2nd REQ socket monitor, connected event only
        rc = ZMQ.monitorSocket(req2, "inproc://monitor.req2", ZMQ_EVENT_CONNECTED);
        Assert.assertThat(rc, CoreMatchers.is(true));
        rc = ZMQ.connect(req2, addr);
        Assert.assertThat(rc, CoreMatchers.is(true));
        Helper.bounce(rep, req);
        // Allow a window for socket events as connect can be async
        ZMQ.sleep(1);
        ZMQ.setSocketOption(rep, ZMQ_LINGER, 0);
        // Close the REP socket
        ZMQ.close(rep);
        // Allow some time for detecting error states
        ZMQ.sleep(1);
        ZMQ.setSocketOption(req, ZMQ_LINGER, 0);
        // Close the REQ socket
        ZMQ.close(req);
        ZMQ.setSocketOption(req2, ZMQ_LINGER, 0);
        // Close the 2nd REQ socket
        ZMQ.close(req2);
        // Allow for closed or disconnected events to bubble up
        ZMQ.sleep(1);
        ZMQ.term(ctx);
        // Expected REP socket events
        // We expect to at least observe these events
        Assert.assertTrue((((threads[0].events) & (ZMQ.ZMQ_EVENT_LISTENING)) > 0));
        Assert.assertTrue((((threads[0].events) & (ZMQ.ZMQ_EVENT_ACCEPTED)) > 0));
        Assert.assertTrue((((threads[0].events) & (ZMQ.ZMQ_EVENT_CLOSED)) > 0));
        Assert.assertTrue((((threads[0].events) & (ZMQ.ZMQ_EVENT_MONITOR_STOPPED)) > 0));
        // Expected REQ socket events
        Assert.assertTrue((((threads[1].events) & (ZMQ.ZMQ_EVENT_CONNECT_DELAYED)) > 0));
        Assert.assertTrue((((threads[1].events) & (ZMQ.ZMQ_EVENT_CONNECTED)) > 0));
        Assert.assertTrue((((threads[1].events) & (ZMQ.ZMQ_EVENT_MONITOR_STOPPED)) > 0));
        // Expected 2nd REQ socket events
        Assert.assertTrue(((threads[2].events) == (ZMQ.ZMQ_EVENT_CONNECTED)));
        threads[0].join();
        threads[1].join();
        threads[2].join();
    }
}

