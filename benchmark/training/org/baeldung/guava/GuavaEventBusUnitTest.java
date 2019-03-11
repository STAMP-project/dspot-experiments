package org.baeldung.guava;


import com.google.common.eventbus.EventBus;
import org.junit.Assert;
import org.junit.Test;


public class GuavaEventBusUnitTest {
    private EventListener listener;

    private EventBus eventBus;

    @Test
    public void givenStringEvent_whenEventHandled_thenSuccess() {
        listener.resetEventsHandled();
        eventBus.post("String Event");
        Assert.assertEquals(1, listener.getEventsHandled());
    }

    @Test
    public void givenCustomEvent_whenEventHandled_thenSuccess() {
        listener.resetEventsHandled();
        CustomEvent customEvent = new CustomEvent("Custom Event");
        eventBus.post(customEvent);
        Assert.assertEquals(1, listener.getEventsHandled());
    }

    @Test
    public void givenUnSubscribedEvent_whenEventHandledByDeadEvent_thenSuccess() {
        listener.resetEventsHandled();
        eventBus.post(12345);
        Assert.assertEquals(1, listener.getEventsHandled());
    }
}

