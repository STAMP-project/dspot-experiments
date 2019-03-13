package com.vaadin.tests.push;


import com.vaadin.tests.tb3.SingleBrowserTest;
import org.junit.Test;


public class PushWithRequireJSTest extends SingleBrowserTest {
    @Test
    public void testPushWithRequireJS() {
        setDebug(true);
        openTestURL();
        assertNoErrorNotifications();
    }
}

