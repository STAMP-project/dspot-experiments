package com.vaadin.tests.applicationservlet;


import com.vaadin.testbench.By;
import com.vaadin.tests.tb3.SingleBrowserTest;
import org.junit.Assert;
import org.junit.Test;


public class ContextProtocolTest extends SingleBrowserTest {
    @Test
    public void contextPathCorrect() {
        openTestURL();
        // Added by bootstrap
        Assert.assertEquals("said", executeScript("return window.hello"));
        // Added by client side
        Assert.assertEquals(((getBaseURL()) + "/statictestfiles/image.png"), findElement(By.id("image")).getAttribute("src"));
    }
}

