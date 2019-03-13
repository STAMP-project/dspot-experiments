package com.hwangjr.rxbus;


import com.hwangjr.rxbus.annotation.Subscribe;
import com.hwangjr.rxbus.thread.EventThread;
import com.hwangjr.rxbus.thread.ThreadEnforcer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import junit.framework.Assert;
import org.junit.Test;


/**
 * Validate that {@link Bus} behaves carefully when listeners publish
 * their own events.
 */
public class ReentrantEventsTest {
    static final String FIRST = "one";

    static final Double SECOND = 2.0;

    final Bus bus = new Bus(ThreadEnforcer.ANY);

    @Test
    public void noReentrantEvents() {
        ReentrantEventsTest.ReentrantEventsHater hater = new ReentrantEventsTest.ReentrantEventsHater();
        bus.register(hater);
        bus.post(ReentrantEventsTest.FIRST);
        Assert.assertEquals("ReentrantEventHater expected 2 events", Arrays.<Object>asList(ReentrantEventsTest.FIRST, ReentrantEventsTest.SECOND), hater.eventsReceived);
    }

    public class ReentrantEventsHater {
        boolean ready = true;

        List<Object> eventsReceived = new ArrayList<Object>();

        @Subscribe(thread = EventThread.IMMEDIATE)
        public void listenForStrings(String event) {
            eventsReceived.add(event);
            ready = false;
            try {
                bus.post(ReentrantEventsTest.SECOND);
            } finally {
                ready = true;
            }
        }

        @Subscribe(thread = EventThread.IMMEDIATE)
        public void listenForDoubles(Double event) {
            Assert.assertTrue("I received an event when I wasn't ready!", ready);
            eventsReceived.add(event);
        }
    }

    @Test
    public void eventOrderingIsPredictable() {
        ReentrantEventsTest.EventProcessor processor = new ReentrantEventsTest.EventProcessor();
        bus.register(processor);
        ReentrantEventsTest.EventRecorder recorder = new ReentrantEventsTest.EventRecorder();
        bus.register(recorder);
        bus.post(ReentrantEventsTest.FIRST);
        Assert.assertEquals("EventRecorder expected events in order", Arrays.<Object>asList(ReentrantEventsTest.FIRST, ReentrantEventsTest.SECOND), recorder.eventsReceived);
    }

    public class EventProcessor {
        @Subscribe(thread = EventThread.IMMEDIATE)
        public void listenForStrings(String event) {
            bus.post(ReentrantEventsTest.SECOND);
        }
    }

    public class EventRecorder {
        List<Object> eventsReceived = new ArrayList<Object>();

        @Subscribe(thread = EventThread.IMMEDIATE)
        public void listenForEverything(Object event) {
            eventsReceived.add(event);
        }
    }
}

