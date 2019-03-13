package com.vaadin.tests.components.abstractcomponent;


import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.tests.tb3.SingleBrowserTest;
import org.junit.Assert;
import org.junit.Test;


public class ResponseWritingErrorHandlingTest extends SingleBrowserTest {
    @Test
    public void testExceptionInBeforeClientResponse() {
        openTestURL();
        $(ButtonElement.class).first().click();
        Assert.assertEquals("Message should be logged by error handler", "1. Button.beforeClientResponse", getLogRow(0));
    }
}

