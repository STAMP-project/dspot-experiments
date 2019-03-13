package com.vaadin.tests.application;


import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.NotificationElement;
import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Assert;
import org.junit.Test;


public class VaadinSessionAttributeTest extends MultiBrowserTest {
    @Test
    public void testSessionAttribute() {
        openTestURL();
        $(ButtonElement.class).first().click();
        Assert.assertEquals("notification does not contain suitable text", "42 & 84", $(NotificationElement.class).first().getCaption());
    }
}

