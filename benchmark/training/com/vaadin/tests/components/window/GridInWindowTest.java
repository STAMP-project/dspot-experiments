package com.vaadin.tests.components.window;


import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.WindowElement;
import com.vaadin.tests.tb3.SingleBrowserTest;
import org.junit.Test;


public class GridInWindowTest extends SingleBrowserTest {
    @Test
    public void ensureAttachInHierachyChange() {
        openTestURL("debug");
        $(ButtonElement.class).first().click();
        assertNoErrorNotifications();
        $(WindowElement.class).first().close();
        assertNoErrorNotifications();
        $(ButtonElement.class).first().click();
        assertNoErrorNotifications();
    }
}

