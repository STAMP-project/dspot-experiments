package com.vaadin.tests.components.window;


import com.vaadin.testbench.elements.WindowElement;
import com.vaadin.tests.tb3.SingleBrowserTest;
import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.Dimension;


public class WindowMaxHeightTest extends SingleBrowserTest {
    @Test
    public void ensureWindowNotFullHeight() {
        openTestURL();
        WindowElement window = $(WindowElement.class).first();
        Dimension size = window.getSize();
        Assert.assertTrue(("Window should be 200-250px high, was " + (size.getHeight())), ((size.getHeight()) < 250));
    }
}

