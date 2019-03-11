package org.mockserver.client;


import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockserver.client.MockServerEventBus.SubscriberHandler;


/**
 *
 *
 * @author albans
 */
public class MockServerEventBusTest {
    @Mock
    private SubscriberHandler subscriber;

    @Mock
    private SubscriberHandler secondSubscriber;

    private MockServerEventBus bus;

    @Test
    public void shouldPublishStopEventWhenNoRegisterSubscriber() {
        // given no subscriber registered yet
        // when
        bus.publish(EventType.STOP);
        // then nothing, no exception
    }

    @Test
    public void shouldPublishStopEventToOneRegisteredSubscriber() {
        // given
        bus.subscribe(subscriber, EventType.STOP);
        // when
        bus.publish(EventType.STOP);
        // then
        Mockito.verify(subscriber).handle();
    }

    @Test
    public void shouldPublishResetEventToTwoSubscribers() {
        // given
        bus.subscribe(subscriber, EventType.RESET, EventType.STOP);
        bus.subscribe(subscriber, EventType.RESET, EventType.STOP);
        // when
        bus.publish(EventType.RESET);
        // then
        Mockito.verify(subscriber, Mockito.times(2)).handle();
    }

    @Test
    public void shouldPublishEventToCorrectConsumer() {
        // given
        bus.subscribe(subscriber, EventType.RESET);
        bus.subscribe(secondSubscriber, EventType.STOP);
        // when
        bus.publish(EventType.STOP);
        // then
        Mockito.verify(subscriber, Mockito.never()).handle();
        Mockito.verify(secondSubscriber).handle();
    }
}

