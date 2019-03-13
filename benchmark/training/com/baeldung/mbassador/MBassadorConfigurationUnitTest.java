package com.baeldung.mbassador;


import java.util.LinkedList;
import junit.framework.TestCase;
import net.engio.mbassy.bus.MBassador;
import net.engio.mbassy.bus.error.IPublicationErrorHandler;
import org.junit.Assert;
import org.junit.Test;


public class MBassadorConfigurationUnitTest implements IPublicationErrorHandler {
    private MBassador dispatcher;

    private String messageString;

    private Throwable errorCause;

    private LinkedList<Integer> list = new LinkedList<>();

    @Test
    public void whenErrorOccurs_thenErrorHandler() {
        dispatcher.post("Error").now();
        Assert.assertNull(messageString);
        Assert.assertNotNull(errorCause);
    }

    @Test
    public void whenNoErrorOccurs_thenStringHandler() {
        dispatcher.post("Errol").now();
        Assert.assertNull(errorCause);
        Assert.assertNotNull(messageString);
    }

    @Test
    public void whenRejectDispatched_thenPriorityHandled() {
        dispatcher.post(new RejectMessage()).now();
        // Items should pop() off in reverse priority order
        TestCase.assertTrue((1 == (list.pop())));
        TestCase.assertTrue((3 == (list.pop())));
        TestCase.assertTrue((5 == (list.pop())));
    }
}

