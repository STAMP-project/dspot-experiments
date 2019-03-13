package com.vaadin.tests.components.ui;


import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.tests.tb3.SingleBrowserTest;
import org.junit.Test;


public class UIAccessExceptionHandlingTest extends SingleBrowserTest {
    @Test
    public void testExceptionHandlingOnUIAccess() throws Exception {
        openTestURL();
        $(ButtonElement.class).first().click();
        assertLogTexts("1. Exception caught on get: java.util.concurrent.ExecutionException", "0. Exception caught on execution with ConnectorErrorEvent : java.lang.RuntimeException");
        $(ButtonElement.class).get(1).click();
        assertLogTexts("1. Exception caught on get: java.util.concurrent.ExecutionException", "0. Exception caught on execution with ErrorEvent : java.lang.RuntimeException");
        $(ButtonElement.class).get(2).click();
        assertLogTexts("1. Exception caught on get: java.util.concurrent.ExecutionException", "0. Exception caught on execution with ConnectorErrorEvent : java.lang.RuntimeException");
        $(ButtonElement.class).get(3).click();
        assertLogText(0, "0. Exception caught on execution with ConnectorErrorEvent : java.lang.NullPointerException");
    }
}

