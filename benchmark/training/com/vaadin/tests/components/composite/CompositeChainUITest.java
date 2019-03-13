package com.vaadin.tests.components.composite;


import com.vaadin.testbench.By;
import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.LabelElement;
import com.vaadin.tests.tb3.SingleBrowserTest;
import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.WebElement;


public class CompositeChainUITest extends SingleBrowserTest {
    @Test
    public void compositeRenderedAndUpdatedCorrectly() {
        openTestURL();
        LabelElement label = $(LabelElement.class).id("innermost");
        WebElement labelGrandParent = label.findElement(By.xpath("../.."));
        Assert.assertEquals("v-slot", labelGrandParent.getAttribute("class"));
        Assert.assertEquals("Label caption", label.getCaption());
        $(ButtonElement.class).caption("Update caption").first().click();
        Assert.assertEquals("Label caption - updated", label.getCaption());
    }

    @Test
    public void compositeRemovedCorrectly() {
        openTestURL("debug");
        LabelElement label = $(LabelElement.class).id("innermost");
        $(ButtonElement.class).caption("Update caption").first().click();
        Assert.assertEquals("Label caption - updated", label.getCaption());
        $(ButtonElement.class).caption("Replace with another Composite").first().click();
        label = $(LabelElement.class).id("innermost");
        Assert.assertEquals("Label caption", label.getCaption());
        assertNoErrorNotifications();
    }
}

