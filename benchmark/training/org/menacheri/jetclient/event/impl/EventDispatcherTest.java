package org.menacheri.jetclient.event.impl;


import Events.CONNECT;
import Events.EXCEPTION;
import Events.LOG_IN;
import Events.NETWORK_MESSAGE;
import Events.SESSION_MESSAGE;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.junit.Assert;
import org.junit.Test;
import org.menacheri.jetclient.event.Event;
import org.menacheri.jetclient.event.EventDispatcher;
import org.menacheri.jetclient.event.EventHandler;
import org.menacheri.jetclient.event.Events;


public class EventDispatcherTest {
    @Test
    public void specificEventReceiptOnSpecificEventHandler() throws InterruptedException {
        EventDispatcher dispatcher = new DefaultEventDispatcher();
        final CountDownLatch latch = new CountDownLatch(1);
        dispatcher.addHandler(new EventHandler() {
            @Override
            public void onEvent(Event event) {
                latch.countDown();
            }

            @Override
            public int getEventType() {
                return Events.SESSION_MESSAGE;
            }
        });
        Event event = Events.event(null, SESSION_MESSAGE);
        dispatcher.fireEvent(event);
        Assert.assertTrue(latch.await(1, TimeUnit.SECONDS));
    }

    @Test
    public void eventReceiptOnANYTypeEventHandler() throws InterruptedException {
        EventDispatcher dispatcher = new DefaultEventDispatcher();
        final CountDownLatch latch = new CountDownLatch(5);
        dispatcher.addHandler(new EventHandler() {
            @Override
            public void onEvent(Event event) {
                latch.countDown();
            }

            @Override
            public int getEventType() {
                return Events.ANY;
            }
        });
        Event event = Events.event(null, SESSION_MESSAGE);
        dispatcher.fireEvent(event);
        event = Events.event(null, NETWORK_MESSAGE);
        dispatcher.fireEvent(event);
        event = Events.event(null, EXCEPTION);
        dispatcher.fireEvent(event);
        event = Events.event(null, LOG_IN);
        dispatcher.fireEvent(event);
        event = Events.event(null, CONNECT);
        dispatcher.fireEvent(event);
        Assert.assertTrue(latch.await(1, TimeUnit.SECONDS));
    }

    @Test
    public void nonReceiptOfWrongEventOnSpecificEventHandler() throws InterruptedException {
        EventDispatcher dispatcher = new DefaultEventDispatcher();
        final CountDownLatch latch = new CountDownLatch(1);
        dispatcher.addHandler(new EventHandler() {
            @Override
            public void onEvent(Event event) {
                latch.countDown();
            }

            @Override
            public int getEventType() {
                return Events.SESSION_MESSAGE;
            }
        });
        Event event = Events.event(null, NETWORK_MESSAGE);
        dispatcher.fireEvent(event);
        Assert.assertFalse(latch.await(1, TimeUnit.SECONDS));
    }

    @Test
    public void eventPublishingPerformance() throws InterruptedException {
        EventDispatcher dispatcher = new DefaultEventDispatcher();
        int countOfEvents = 5000000;
        final CountDownLatch latch = new CountDownLatch(countOfEvents);
        dispatcher.addHandler(new EventHandler() {
            @Override
            public void onEvent(Event event) {
                latch.countDown();
            }

            @Override
            public int getEventType() {
                return 0;
            }
        });
        long startTime = System.nanoTime();
        for (int i = 1; i <= countOfEvents; i++) {
            Event event = Events.event(null, SESSION_MESSAGE);
            dispatcher.fireEvent(event);
        }
        long time = (System.nanoTime()) - startTime;
        latch.await(10, TimeUnit.SECONDS);
        System.out.printf("Took  %.3f seconds to send %d int events", (time / 1.0E9), countOfEvents);
    }
}

