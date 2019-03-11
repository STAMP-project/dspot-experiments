package com.hwangjr.rxbus;


import Finder.ANNOTATED;
import com.hwangjr.rxbus.annotation.Produce;
import com.hwangjr.rxbus.annotation.Subscribe;
import com.hwangjr.rxbus.entity.EventType;
import com.hwangjr.rxbus.entity.ProducerEvent;
import com.hwangjr.rxbus.entity.SubscriberEvent;
import com.hwangjr.rxbus.finder.Finder;
import com.hwangjr.rxbus.thread.EventThread;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import junit.framework.Assert;
import org.junit.Test;


/**
 * Test case for subscribers which unregister while handling an event.
 */
public class UnregisteringSubscriberTest {
    private static final String EVENT = "Hello";

    private static final String BUS_IDENTIFIER = "test-bus";

    private Bus bus;

    @Test
    public void unregisterInSubscriber() {
        UnregisteringStringCatcher catcher = new UnregisteringStringCatcher(bus);
        bus.register(catcher);
        bus.post(UnregisteringSubscriberTest.EVENT);
        Assert.assertEquals("One correct event should be delivered.", Arrays.asList(UnregisteringSubscriberTest.EVENT), catcher.getEvents());
        bus.post(UnregisteringSubscriberTest.EVENT);
        bus.post(UnregisteringSubscriberTest.EVENT);
        Assert.assertEquals("Shouldn't catch any more events when unregistered.", Arrays.asList(UnregisteringSubscriberTest.EVENT), catcher.getEvents());
    }

    @Test
    public void unregisterInSubscriberWhenEventProduced() throws Exception {
        UnregisteringStringCatcher catcher = new UnregisteringStringCatcher(bus);
        bus.register(new StringProducer());
        bus.register(catcher);
        Assert.assertEquals(Arrays.asList(StringProducer.VALUE), catcher.getEvents());
        bus.post(UnregisteringSubscriberTest.EVENT);
        bus.post(UnregisteringSubscriberTest.EVENT);
        Assert.assertEquals("Shouldn't catch any more events when unregistered.", Arrays.asList(StringProducer.VALUE), catcher.getEvents());
    }

    @Test
    public void unregisterProducerInSubscriber() throws Exception {
        final Object producer = new Object() {
            private int calls = 0;

            @Produce(thread = EventThread.IMMEDIATE)
            public String produceString() {
                (calls)++;
                if ((calls) > 1) {
                    Assert.fail("Should only have been called once, then unregistered and never called again.");
                }
                return "Please enjoy this hand-crafted String.";
            }
        };
        bus.register(producer);
        bus.register(new Object() {
            @Subscribe(thread = EventThread.IMMEDIATE)
            public void firstUnsubscribeTheProducer(String produced) {
                bus.unregister(producer);
            }

            @Subscribe(thread = EventThread.IMMEDIATE)
            public void shouldNeverBeCalled(String uhoh) {
                Assert.fail("Shouldn't receive events from an unregistered producer.");
            }
        });
    }

    /**
     * Delegates to {@code Finder.ANNOTATED}, then sorts results by {@code SubscriberEvent#toString}
     */
    static class SortedSubscriberFinder implements Finder {
        static Comparator<SubscriberEvent> subscriberComparator = new Comparator<SubscriberEvent>() {
            @Override
            public int compare(SubscriberEvent eventSubscriber, SubscriberEvent eventSubscriber1) {
                return eventSubscriber.toString().compareTo(eventSubscriber1.toString());
            }
        };

        @Override
        public Map<EventType, ProducerEvent> findAllProducers(Object listener) {
            return ANNOTATED.findAllProducers(listener);
        }

        @Override
        public Map<EventType, Set<SubscriberEvent>> findAllSubscribers(Object listener) {
            Map<EventType, Set<SubscriberEvent>> found = ANNOTATED.findAllSubscribers(listener);
            Map<EventType, Set<SubscriberEvent>> sorted = new HashMap<>();
            for (Map.Entry<EventType, Set<SubscriberEvent>> entry : found.entrySet()) {
                SortedSet<SubscriberEvent> subscribers = new java.util.TreeSet(UnregisteringSubscriberTest.SortedSubscriberFinder.subscriberComparator);
                subscribers.addAll(entry.getValue());
                sorted.put(entry.getKey(), subscribers);
            }
            return sorted;
        }
    }
}

