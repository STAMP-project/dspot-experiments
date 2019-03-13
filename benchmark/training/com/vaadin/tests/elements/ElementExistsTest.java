package com.vaadin.tests.elements;


import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Assert;
import org.junit.Test;


public class ElementExistsTest extends MultiBrowserTest {
    @Test
    public void testExistsWithoutUI() {
        // Test that an exists query does not throw an exception even when the
        // initialization of the UI has not been done (#14808).
        boolean buttonExists = $(ButtonElement.class).exists();
        Assert.assertFalse("$(ButtonElement.class).exists() returned true, but there should be no buttons.", buttonExists);
        buttonExists = $(ButtonElement.class).caption("b").exists();
        Assert.assertFalse(("$(ButtonElement.class).caption(\"b\").exists() returned true, " + "but there should be no buttons."), buttonExists);
    }

    @Test
    public void testExistsWithUI() {
        // Test the expected case where the UI has been properly set up.
        openTestURL();
        boolean buttonExists = $(ButtonElement.class).exists();
        Assert.assertTrue("No button was found, although one should be present in the UI.", buttonExists);
        buttonExists = $(ButtonElement.class).caption("b").exists();
        Assert.assertTrue("No button with caption 'b' was found, although one should be present in the UI.", buttonExists);
        buttonExists = $(ButtonElement.class).caption("Button 2").exists();
        Assert.assertFalse(("$(ButtonElement.class).caption(\"Button 2\") returned true, but " + "there should be no button with that caption."), buttonExists);
    }
}

