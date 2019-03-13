package com.github.dockerjava.cmd;


import com.github.dockerjava.api.model.Event;
import com.github.dockerjava.core.command.EventsResultCallback;
import com.github.dockerjava.junit.DockerAssume;
import com.github.dockerjava.utils.TestUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/* NOTE: These tests may fail if there is a difference between local and daemon time
(this is especially a problem when using boot2docker as time may not in sync
with the virtualbox host system)
 */
public class EventsCmdIT extends CmdIT {
    private static final Logger LOG = LoggerFactory.getLogger(EventsCmdIT.class);

    @Test
    public void testEventStreamTimeBound() throws Exception {
        // since until and filtering events is broken in swarm
        // https://github.com/docker/swarm/issues/1203
        DockerAssume.assumeNotSwarm("", dockerRule);
        String startTime = EventsCmdIT.getEpochTime();
        int expectedEvents = generateEvents();
        String endTime = EventsCmdIT.getEpochTime();
        EventsCmdIT.EventsTestCallback eventCallback = new EventsCmdIT.EventsTestCallback(expectedEvents);
        dockerRule.getClient().eventsCmd().withSince(startTime).withUntil(endTime).exec(eventCallback);
        List<Event> events = eventCallback.awaitExpectedEvents(30, TimeUnit.SECONDS);
        // we may receive more events as expected
        Assert.assertTrue(("Received events: " + events), ((events.size()) >= expectedEvents));
    }

    @Test
    public void testEventStreaming() throws Exception {
        String startTime = EventsCmdIT.getEpochTime();
        int expectedEvents = generateEvents();
        EventsCmdIT.EventsTestCallback eventCallback = new EventsCmdIT.EventsTestCallback(expectedEvents);
        dockerRule.getClient().eventsCmd().withSince(startTime).exec(eventCallback);
        generateEvents();
        List<Event> events = eventCallback.awaitExpectedEvents(30, TimeUnit.SECONDS);
        // we may receive more events as expected
        Assert.assertTrue(("Received events: " + events), ((events.size()) >= expectedEvents));
        for (Event event : events) {
            if (TestUtils.isSwarm(dockerRule.getClient())) {
                MatcherAssert.assertThat(event.getNode(), CoreMatchers.is(CoreMatchers.notNullValue()));
                MatcherAssert.assertThat(event.getNode().getAddr(), CoreMatchers.is(CoreMatchers.notNullValue()));
                MatcherAssert.assertThat(event.getNode().getId(), CoreMatchers.is(CoreMatchers.notNullValue()));
                MatcherAssert.assertThat(event.getNode().getIp(), CoreMatchers.is(CoreMatchers.notNullValue()));
                MatcherAssert.assertThat(event.getNode().getName(), CoreMatchers.is(CoreMatchers.notNullValue()));
            } else {
                MatcherAssert.assertThat(event.getNode(), CoreMatchers.is(CoreMatchers.nullValue()));
            }
        }
    }

    @Test
    public void testEventStreamingWithFilter() throws Exception {
        // since until and filtering events is broken in swarm
        // https://github.com/docker/swarm/issues/1203
        DockerAssume.assumeNotSwarm("", dockerRule);
        String startTime = EventsCmdIT.getEpochTime();
        int expectedEvents = 1;
        EventsCmdIT.EventsTestCallback eventCallback = new EventsCmdIT.EventsTestCallback(expectedEvents);
        dockerRule.getClient().eventsCmd().withSince(startTime).withEventFilter("start").exec(eventCallback);
        generateEvents();
        List<Event> events = eventCallback.awaitExpectedEvents(30, TimeUnit.SECONDS);
        // we should only get "start" events here
        for (Event event : events) {
            MatcherAssert.assertThat(("Received event: " + event), event.getAction(), CoreMatchers.is("start"));
        }
    }

    private class EventsTestCallback extends EventsResultCallback {
        private final CountDownLatch countDownLatch;

        private final List<Event> events = new ArrayList<Event>();

        public EventsTestCallback(int expextedEvents) {
            this.countDownLatch = new CountDownLatch(expextedEvents);
        }

        public void onNext(Event event) {
            EventsCmdIT.LOG.info("Received event #{}: {}", countDownLatch.getCount(), event);
            events.add(event);
            countDownLatch.countDown();
        }

        public List<Event> awaitExpectedEvents(long timeout, TimeUnit unit) {
            try {
                countDownLatch.await(timeout, unit);
                close();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            return new ArrayList<Event>(events);
        }
    }
}

