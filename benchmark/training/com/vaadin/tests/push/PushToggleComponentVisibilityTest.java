package com.vaadin.tests.push;


import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.LabelElement;
import com.vaadin.tests.tb3.SingleBrowserTest;
import org.junit.Assert;
import org.junit.Test;


public class PushToggleComponentVisibilityTest extends SingleBrowserTest {
    private static final String HIDE = "hide";

    @Test
    public void ensureComponentVisible() {
        openTestURL();
        $(ButtonElement.class).id(PushToggleComponentVisibilityTest.HIDE).click();
        Assert.assertEquals("Please wait", $(LabelElement.class).first().getText());
        waitUntil(( driver) -> isElementPresent(.class));
        $(ButtonElement.class).id(PushToggleComponentVisibilityTest.HIDE).click();
        Assert.assertEquals("Please wait", $(LabelElement.class).first().getText());
    }
}

