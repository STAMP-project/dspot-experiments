package com.baeldung.mbassador;


import junit.framework.TestCase;
import net.engio.mbassy.bus.MBassador;
import net.engio.mbassy.bus.common.DeadMessage;
import net.engio.mbassy.bus.common.FilteredMessage;
import org.junit.Assert;
import org.junit.Test;


public class MBassadorFilterUnitTest {
    private MBassador dispatcher = new MBassador();

    private Message baseMessage;

    private Message subMessage;

    private String testString;

    private FilteredMessage filteredMessage;

    private RejectMessage rejectMessage;

    private DeadMessage deadMessage;

    @Test
    public void whenMessageDispatched_thenMessageFiltered() {
        dispatcher.post(new Message()).now();
        Assert.assertNotNull(baseMessage);
        Assert.assertNull(subMessage);
    }

    @Test
    public void whenRejectDispatched_thenRejectFiltered() {
        dispatcher.post(new RejectMessage()).now();
        Assert.assertNotNull(subMessage);
        Assert.assertNull(baseMessage);
    }

    @Test
    public void whenShortStringDispatched_thenStringHandled() {
        dispatcher.post("foobar").now();
        Assert.assertNotNull(testString);
    }

    @Test
    public void whenLongStringDispatched_thenStringFiltered() {
        dispatcher.post("foobar!").now();
        Assert.assertNull(testString);
        // filtered only populated when messages does not pass any filters
        Assert.assertNotNull(filteredMessage);
        TestCase.assertTrue(((filteredMessage.getMessage()) instanceof String));
        Assert.assertNull(deadMessage);
    }

    @Test
    public void whenWrongRejectDispatched_thenRejectFiltered() {
        RejectMessage testReject = new RejectMessage();
        testReject.setCode((-1));
        dispatcher.post(testReject).now();
        Assert.assertNull(rejectMessage);
        Assert.assertNotNull(subMessage);
        Assert.assertEquals((-1), getCode());
    }
}

