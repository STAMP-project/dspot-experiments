package com.netflix.conductor.service;


import com.netflix.conductor.core.events.EventProcessor;
import com.netflix.conductor.core.events.EventQueues;
import com.netflix.conductor.utility.TestUtils;
import java.util.Set;
import javax.validation.ConstraintViolationException;
import org.junit.Assert;
import org.junit.Test;


public class EventServiceTest {
    private MetadataService metadataService;

    private EventProcessor eventProcessor;

    private EventQueues eventQueues;

    private EventService eventService;

    @Test(expected = ConstraintViolationException.class)
    public void testAddEventHandler() {
        try {
            eventService.addEventHandler(null);
        } catch (ConstraintViolationException ex) {
            Assert.assertEquals(1, ex.getConstraintViolations().size());
            Set<String> messages = TestUtils.getConstraintViolationMessages(ex.getConstraintViolations());
            Assert.assertTrue(messages.contains("EventHandler cannot be null."));
            throw ex;
        }
        Assert.fail("eventService.addEventHandler did not throw ConstraintViolationException !");
    }

    @Test(expected = ConstraintViolationException.class)
    public void testUpdateEventHandler() {
        try {
            eventService.updateEventHandler(null);
        } catch (ConstraintViolationException ex) {
            Assert.assertEquals(1, ex.getConstraintViolations().size());
            Set<String> messages = TestUtils.getConstraintViolationMessages(ex.getConstraintViolations());
            Assert.assertTrue(messages.contains("EventHandler cannot be null."));
            throw ex;
        }
        Assert.fail("eventService.updateEventHandler did not throw ConstraintViolationException !");
    }

    @Test(expected = ConstraintViolationException.class)
    public void testRemoveEventHandlerStatus() {
        try {
            eventService.removeEventHandlerStatus(null);
        } catch (ConstraintViolationException ex) {
            Assert.assertEquals(1, ex.getConstraintViolations().size());
            Set<String> messages = TestUtils.getConstraintViolationMessages(ex.getConstraintViolations());
            Assert.assertTrue(messages.contains("EventHandler name cannot be null or empty."));
            throw ex;
        }
        Assert.fail("eventService.removeEventHandlerStatus did not throw ConstraintViolationException !");
    }

    @Test(expected = ConstraintViolationException.class)
    public void testGetEventHandlersForEvent() {
        try {
            eventService.getEventHandlersForEvent(null, false);
        } catch (ConstraintViolationException ex) {
            Assert.assertEquals(1, ex.getConstraintViolations().size());
            Set<String> messages = TestUtils.getConstraintViolationMessages(ex.getConstraintViolations());
            Assert.assertTrue(messages.contains("Event cannot be null or empty."));
            throw ex;
        }
        Assert.fail("eventService.getEventHandlersForEvent did not throw ConstraintViolationException !");
    }
}

