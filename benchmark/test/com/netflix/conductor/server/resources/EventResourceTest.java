package com.netflix.conductor.server.resources;


import com.netflix.conductor.common.metadata.events.EventHandler;
import com.netflix.conductor.service.EventService;
import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class EventResourceTest {
    private EventResource eventResource;

    @Mock
    private EventService mockEventService;

    @Test
    public void testAddEventHandler() {
        EventHandler eventHandler = new EventHandler();
        eventResource.addEventHandler(eventHandler);
        Mockito.verify(mockEventService, Mockito.times(1)).addEventHandler(ArgumentMatchers.any(EventHandler.class));
    }

    @Test
    public void testUpdateEventHandler() {
        EventHandler eventHandler = new EventHandler();
        eventResource.updateEventHandler(eventHandler);
        Mockito.verify(mockEventService, Mockito.times(1)).updateEventHandler(ArgumentMatchers.any(EventHandler.class));
    }

    @Test
    public void testRemoveEventHandlerStatus() {
        eventResource.removeEventHandlerStatus("testEvent");
        Mockito.verify(mockEventService, Mockito.times(1)).removeEventHandlerStatus(ArgumentMatchers.anyString());
    }

    @Test
    public void testGetEventHandlersForEvent() {
        EventHandler eventHandler = new EventHandler();
        eventResource.addEventHandler(eventHandler);
        List<EventHandler> listOfEventHandler = new ArrayList<>();
        listOfEventHandler.add(eventHandler);
        Mockito.when(mockEventService.getEventHandlersForEvent(ArgumentMatchers.anyString(), ArgumentMatchers.anyBoolean())).thenReturn(listOfEventHandler);
        Assert.assertEquals(listOfEventHandler, eventResource.getEventHandlersForEvent("testEvent", true));
    }

    @Test
    public void testGetEventHandlers() {
        EventHandler eventHandler = new EventHandler();
        eventResource.addEventHandler(eventHandler);
        List<EventHandler> listOfEventHandler = new ArrayList<>();
        listOfEventHandler.add(eventHandler);
        Mockito.when(mockEventService.getEventHandlers()).thenReturn(listOfEventHandler);
        Assert.assertEquals(listOfEventHandler, eventResource.getEventHandlers());
    }

    @Test
    public void testGetEventQueues() {
        eventResource.getEventQueues(false);
        Mockito.verify(mockEventService, Mockito.times(1)).getEventQueues(ArgumentMatchers.anyBoolean());
    }

    @Test
    public void getEventQueueProviders() {
        List<String> queuesList = new ArrayList<>();
        Mockito.when(mockEventService.getEventQueueProviders()).thenReturn(queuesList);
        Assert.assertEquals(queuesList, eventResource.getEventQueueProviders());
    }
}

