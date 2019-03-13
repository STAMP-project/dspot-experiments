package com.vaadin.tests.components.ui;


import com.vaadin.testbench.elements.UIElement;
import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Assert;
import org.junit.Test;


public class VaadinFinderLocatorUISearchTest extends MultiBrowserTest {
    @Test
    public void getUIElementTest() {
        openTestURL();
        UIElement ui = $(UIElement.class).first();
        Assert.assertNotNull("Couldn't find the UI Element on the page", ui);
    }
}

