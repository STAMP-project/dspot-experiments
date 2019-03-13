package com.vaadin.tests.components.window;


import Keys.ESCAPE;
import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.WindowElement;
import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Assert;
import org.junit.Test;


public class CloseWindowWithFocusedTextFieldTest extends MultiBrowserTest {
    @Test
    public void OpenWindow_CloseWithEscapeKey_WindowClosed() {
        openTestURL();
        $(ButtonElement.class).first().click();
        Assert.assertTrue("Window should be opened", $(WindowElement.class).exists());
        new org.openqa.selenium.interactions.Actions(getDriver()).sendKeys(ESCAPE).build().perform();
        Assert.assertFalse("Window found when there should be none.", $(WindowElement.class).exists());
    }
}

