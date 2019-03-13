package com.vaadin.tests.components.ui;


import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.tests.tb3.SingleBrowserTest;
import org.junit.Test;


public class DetachedAccessErrorHandlingTest extends SingleBrowserTest {
    @Test
    public void testDetachedErrorHandling_pageOpen_noErrors() {
        openTestURL();
        $(ButtonElement.class).id("simple").click();
        assertNoErrors();
        // The thing to really test here is that nothing is logged to stderr,
        // but that's not practical to detect
        $(ButtonElement.class).id("handling").click();
        assertNoErrors();
    }
}

