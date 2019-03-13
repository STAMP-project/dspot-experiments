package com.vaadin.tests.components.treetable;


import com.vaadin.tests.tb3.SingleBrowserTest;
import org.junit.Test;


public class TreeTableRowGeneratorTest extends SingleBrowserTest {
    @Test
    public void testNoExceptionOnRender() {
        setDebug(true);
        openTestURL();
        assertNoErrorNotifications();
    }
}

