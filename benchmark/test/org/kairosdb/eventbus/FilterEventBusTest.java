package org.kairosdb.eventbus;


import org.junit.Assert;
import org.junit.Test;
import org.kairosdb.core.KairosRootConfig;


public class FilterEventBusTest {
    @Test(expected = NullPointerException.class)
    public void test_constructor_nullConfig_invalid() {
        new FilterEventBus(null);
    }

    @Test
    public void test_publishEvent() {
        FilterEventBusTest.Subscriber subscriber1 = new FilterEventBusTest.Subscriber();
        FilterEventBusTest.Subscriber subscriber2 = new FilterEventBusTest.Subscriber();
        FilterEventBusTest.Subscriber subscriber3 = new FilterEventBusTest.Subscriber();
        EventBusConfiguration config = new EventBusConfiguration(new KairosRootConfig());
        FilterEventBus eventBus = new FilterEventBus(config);
        eventBus.register(subscriber1);
        eventBus.register(subscriber2);
        eventBus.register(subscriber3);
        eventBus.createPublisher(String.class).post("Hi");
        Assert.assertEquals("Hi", subscriber1.what());
        Assert.assertEquals("Hi", subscriber2.what());
        Assert.assertEquals("Hi", subscriber3.what());
    }

    @Test
    public void test_modifyEvent() {
        FilterEventBusTest.Subscriber subscriber1 = new FilterEventBusTest.Subscriber();
        FilterEventBusTest.Subscriber subscriber2 = new FilterEventBusTest.Subscriber();
        FilterEventBusTest.Subscriber subscriber3 = new FilterEventBusTest.Subscriber();
        FilterEventBusTest.FilterSubscriber filter = new FilterEventBusTest.FilterSubscriber("Bye");
        EventBusConfiguration config = new EventBusConfiguration(new KairosRootConfig());
        FilterEventBus eventBus = new FilterEventBus(config);
        eventBus.register(subscriber1, 1);
        eventBus.register(subscriber2, 2);
        eventBus.register(subscriber3, 10);
        eventBus.register(filter, 5);
        eventBus.createPublisher(String.class).post("Hi");
        Assert.assertEquals("Hi", subscriber1.what());
        Assert.assertEquals("Hi", subscriber2.what());
        Assert.assertEquals("Hi", filter.what());
        Assert.assertEquals("Bye", subscriber3.what());
    }

    @Test
    public void test_filterEvent() {
        FilterEventBusTest.Subscriber subscriber1 = new FilterEventBusTest.Subscriber();
        FilterEventBusTest.Subscriber subscriber2 = new FilterEventBusTest.Subscriber();
        FilterEventBusTest.Subscriber subscriber3 = new FilterEventBusTest.Subscriber();
        FilterEventBusTest.FilterSubscriber filter = new FilterEventBusTest.FilterSubscriber(null);
        EventBusConfiguration config = new EventBusConfiguration(new KairosRootConfig());
        FilterEventBus eventBus = new FilterEventBus(config);
        eventBus.register(subscriber1, 1);
        eventBus.register(subscriber2, 2);
        eventBus.register(subscriber3, 10);
        eventBus.register(filter, 5);
        eventBus.createPublisher(String.class).post("Hi");
        Assert.assertEquals("Hi", subscriber1.what());
        Assert.assertEquals("Hi", subscriber2.what());
        Assert.assertEquals("Hi", filter.what());
        Assert.assertEquals(null, subscriber3.what());
        assertThat(subscriber3.isWasCalled()).isFalse();
    }

    public class Subscriber {
        private String m_what;

        private boolean m_wasCalled = false;

        @Subscribe
        public void consume(String data) {
            m_what = data;
            m_wasCalled = true;
        }

        public String what() {
            return m_what;
        }

        public boolean isWasCalled() {
            return m_wasCalled;
        }
    }

    public class FilterSubscriber {
        private String m_what;

        private final String m_change;

        public FilterSubscriber(String change) {
            m_change = change;
        }

        @Subscribe
        public String consume(String data) {
            m_what = data;
            return m_change;
        }

        public String what() {
            return m_what;
        }
    }
}

