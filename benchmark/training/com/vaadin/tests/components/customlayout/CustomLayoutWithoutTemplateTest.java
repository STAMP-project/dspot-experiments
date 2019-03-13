package com.vaadin.tests.components.customlayout;


import com.vaadin.testbench.ElementQuery;
import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.CustomLayoutElement;
import com.vaadin.testbench.elements.LabelElement;
import com.vaadin.tests.tb3.SingleBrowserTest;
import org.junit.Assert;
import org.junit.Test;


public class CustomLayoutWithoutTemplateTest extends SingleBrowserTest {
    @Test
    public void testChildComponents() {
        openTestURL();
        ElementQuery<CustomLayoutElement> customLayout = $(CustomLayoutElement.class);
        // Verify the Button and Label are rendered inside the CustomLayout.
        Assert.assertTrue("Button was not rendered.", customLayout.$(ButtonElement.class).exists());
        Assert.assertTrue("Label was not rendered.", customLayout.$(LabelElement.class).exists());
    }
}

