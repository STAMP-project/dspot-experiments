package com.vaadin.tests.applicationservlet;


import com.vaadin.tests.tb3.SingleBrowserTest;
import org.junit.Assert;
import org.junit.Test;


public class ServiceInitListenersTest extends SingleBrowserTest {
    @Test
    public void testServiceInitListenerTriggered() {
        openTestURL();
        Assert.assertNotEquals(getLogRow(0), 0, extractCount(getLogRow(0)));
        Assert.assertNotEquals(getLogRow(1), 0, extractCount(getLogRow(1)));
        Assert.assertNotEquals(getLogRow(2), 0, extractCount(getLogRow(2)));
    }
}

