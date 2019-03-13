package com.vaadin.tests.components.treegrid;


import com.vaadin.tests.tb3.SingleBrowserTest;
import org.junit.Test;


public class TreeGridNoHeaderOnInitTest extends SingleBrowserTest {
    @Test
    public void no_exception_thrown() {
        setDebug(true);
        openTestURL();
        assertNoErrorNotifications();
    }
}

