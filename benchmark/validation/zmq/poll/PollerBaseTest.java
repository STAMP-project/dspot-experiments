package zmq.poll;


import java.util.concurrent.atomic.AtomicInteger;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class PollerBaseTest {
    private IPollEvents sink = new PollEvents();

    @Test
    public void testNoTimer() {
        PollerBase poller = new PollerBaseTested();
        long timeout = poller.executeTimers();
        Assert.assertThat(timeout, CoreMatchers.is(0L));
        poller.addTimer(1000, sink, 1);
    }

    @Test
    public void testOneTimer() {
        final PollerBaseTested poller = new PollerBaseTested();
        poller.addTimer(1000, sink, 1);
        long timeout = executeTimers();
        Assert.assertThat(timeout, CoreMatchers.is(1000L));
        Assert.assertThat(isEmpty(), CoreMatchers.is(false));
        poller.clock(200);
        timeout = executeTimers();
        Assert.assertThat(timeout, CoreMatchers.is(800L));
        Assert.assertThat(isEmpty(), CoreMatchers.is(false));
        poller.clock(1000);
        timeout = executeTimers();
        Assert.assertThat(isEmpty(), CoreMatchers.is(true));
        Assert.assertThat(timeout, CoreMatchers.is(0L));
    }

    @Test
    public void testCancelTimer() {
        final PollerBaseTested poller = new PollerBaseTested();
        poller.addTimer(1000, sink, 1);
        long timeout = executeTimers();
        Assert.assertThat(timeout, CoreMatchers.is(1000L));
        Assert.assertThat(isEmpty(), CoreMatchers.is(false));
        poller.cancelTimer(sink, 1);
        timeout = executeTimers();
        Assert.assertThat(timeout, CoreMatchers.is(0L));
        Assert.assertThat(isEmpty(), CoreMatchers.is(true));
    }

    @Test
    public void testCancelTimerInTimerEvent() {
        final PollerBaseTested poller = new PollerBaseTested();
        PollEvents sink = new PollEvents() {
            @Override
            public void timerEvent(int id) {
                // cancelTimer() is never called in timerEvent()
                poller.cancelTimer(this, id);
            }
        };
        poller.addTimer(1000, sink, 1);
        poller.clock(1000);
        long rc = executeTimers();
        Assert.assertThat(rc, CoreMatchers.is(0L));
        Assert.assertThat(isEmpty(), CoreMatchers.is(true));
    }

    @Test
    public void testAddTimerInTimerEvent() {
        final int id = 1;
        final PollerBaseTested poller = new PollerBaseTested();
        final AtomicInteger counter = new AtomicInteger();
        PollEvents sink = new PollEvents() {
            @Override
            public void timerEvent(int id) {
                counter.incrementAndGet();
                poller.addTimer(100, this, id);
            }
        };
        poller.addTimer(1000, sink, id);
        Assert.assertThat(isEmpty(), CoreMatchers.is(false));
        poller.clock(1000);
        long timeout = executeTimers();
        Assert.assertThat(counter.get(), CoreMatchers.is(1));
        Assert.assertThat(isEmpty(), CoreMatchers.is(false));
        Assert.assertThat(timeout, CoreMatchers.is(100L));
        cancelTimer(sink, id);
        poller.clock(3000);
        timeout = executeTimers();
        Assert.assertThat(counter.get(), CoreMatchers.is(1));
        Assert.assertThat(timeout, CoreMatchers.is(0L));
        Assert.assertThat(isEmpty(), CoreMatchers.is(true));
    }

    @Test
    public void testAddTimer() {
        final PollerBaseTested poller = new PollerBaseTested();
        addTimer(1000, new PollEvents() {
            private boolean first = true;

            @Override
            public void timerEvent(int id) {
                if (first) {
                    // expires at 2000 + 1000
                    addTimer(2000, this, id);
                }
                first = false;
            }
        }, 1);
        poller.clock(1000);
        long timeout = executeTimers();
        Assert.assertThat(isEmpty(), CoreMatchers.is(false));
        Assert.assertThat(timeout, CoreMatchers.is(2000L));
        poller.clock(3000);
        timeout = executeTimers();
        Assert.assertThat(timeout, CoreMatchers.is(0L));
        Assert.assertThat(isEmpty(), CoreMatchers.is(true));
    }
}

