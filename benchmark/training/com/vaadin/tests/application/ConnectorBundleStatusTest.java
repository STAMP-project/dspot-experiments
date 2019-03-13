package com.vaadin.tests.application;


import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.tests.tb3.SingleBrowserTest;
import org.junit.Test;


public class ConnectorBundleStatusTest extends SingleBrowserTest {
    @Test
    public void testConnectorBundleLoading() {
        openTestURL();
        assertLoaded("__eager");
        $(ButtonElement.class).id("refresh").click();
        assertLoaded("__eager", "__deferred");
        $(ButtonElement.class).id("rta").click();
        $(ButtonElement.class).id("refresh").click();
        assertLoaded("__eager", "__deferred", "com.vaadin.client.ui.richtextarea.RichTextAreaConnector");
    }
}

