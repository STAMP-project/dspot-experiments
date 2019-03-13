package com.vaadin.tests.application;


import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.LabelElement;
import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Assert;
import org.junit.Test;


public class WebBrowserSizeTest extends MultiBrowserTest {
    @Test
    public void testBrowserSize() {
        openTestURL();
        $(ButtonElement.class).first().click();
        // Thanks to selenium the browser size should always be 1500 x 850
        Assert.assertEquals("Browser size is not correct.", "1500 x 850", $(LabelElement.class).get(2).getText());
    }
}

