package com.hwangjr.rxbus;


import com.hwangjr.rxbus.annotation.Produce;
import com.hwangjr.rxbus.annotation.Subscribe;
import com.hwangjr.rxbus.thread.EventThread;
import com.hwangjr.rxbus.thread.ThreadEnforcer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import junit.framework.Assert;
import org.junit.Test;


/**
 * Test that Bus finds the correct producers.
 * <p/>
 * This test must be outside the c.g.c.rxbus package to test correctly.
 */
@SuppressWarnings("UnusedDeclaration")
public class AnnotatedProducerFinderTest {
    static class Subscriber {
        final List<Object> events = new ArrayList<Object>();

        @Subscribe(thread = EventThread.IMMEDIATE)
        public void subscribe(Object o) {
            events.add(o);
        }
    }

    static class SimpleProducer {
        static final Object VALUE = new Object();

        int produceCalled = 0;

        @Produce(thread = EventThread.IMMEDIATE)
        public Object produceIt() {
            produceCalled += 1;
            return AnnotatedProducerFinderTest.SimpleProducer.VALUE;
        }
    }

    @Test
    public void simpleProducer() {
        Bus bus = new Bus(ThreadEnforcer.ANY);
        AnnotatedProducerFinderTest.Subscriber subscriber = new AnnotatedProducerFinderTest.Subscriber();
        AnnotatedProducerFinderTest.SimpleProducer producer = new AnnotatedProducerFinderTest.SimpleProducer();
        bus.register(producer);
        assertThat(producer.produceCalled).isEqualTo(0);
        bus.register(subscriber);
        assertThat(producer.produceCalled).isEqualTo(1);
        Assert.assertEquals(Arrays.asList(AnnotatedProducerFinderTest.SimpleProducer.VALUE), subscriber.events);
    }

    @Test
    public void multipleSubscriptionsCallsProviderEachTime() {
        Bus bus = new Bus(ThreadEnforcer.ANY);
        AnnotatedProducerFinderTest.SimpleProducer producer = new AnnotatedProducerFinderTest.SimpleProducer();
        bus.register(producer);
        bus.register(new AnnotatedProducerFinderTest.Subscriber());
        assertThat(producer.produceCalled).isEqualTo(1);
        bus.register(new AnnotatedProducerFinderTest.Subscriber());
        assertThat(producer.produceCalled).isEqualTo(2);
    }
}

