package com.vaadin.tests.serialization;


import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.WebElement;


public class GenericWidgetHandlingTest extends MultiBrowserTest {
    @Test
    public void testWidgetInit() {
        openTestURL();
        WebElement label = vaadinElementById("label");
        Assert.assertEquals("The generic text is strong in this one", label.getText());
    }
}

