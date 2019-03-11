package com.baeldung.mbassador;


import junit.framework.TestCase;
import net.engio.mbassy.bus.MBassador;
import org.junit.Assert;
import org.junit.Test;


public class MBassadorBasicUnitTest {
    private MBassador dispatcher = new MBassador();

    private String messageString;

    private Integer messageInteger;

    private Object deadEvent;

    @Test
    public void whenStringDispatched_thenHandleString() {
        dispatcher.post("TestString").now();
        Assert.assertNotNull(messageString);
        Assert.assertEquals("TestString", messageString);
    }

    @Test
    public void whenIntegerDispatched_thenHandleInteger() {
        dispatcher.post(42).now();
        Assert.assertNull(messageString);
        Assert.assertNotNull(messageInteger);
        TestCase.assertTrue((42 == (messageInteger)));
    }

    @Test
    public void whenLongDispatched_thenDeadEvent() {
        dispatcher.post(42L).now();
        Assert.assertNull(messageString);
        Assert.assertNull(messageInteger);
        Assert.assertNotNull(deadEvent);
        TestCase.assertTrue(((deadEvent) instanceof Long));
        TestCase.assertTrue((42L == ((Long) (deadEvent))));
    }
}

