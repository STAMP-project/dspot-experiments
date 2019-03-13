package com.vaadin.tests.components.abstractcomponent;


import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.UIElement;
import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Assert;
import org.junit.Test;


public class ContextClickUITest extends MultiBrowserTest {
    @Test
    public void testContextClick() {
        openTestURL();
        final UIElement uiElement = $(UIElement.class).first();
        new org.openqa.selenium.interactions.Actions(getDriver()).moveToElement(uiElement, getXOffset(uiElement, 10), getYOffset(uiElement, 10)).contextClick().perform();
        Assert.assertEquals("Context click not received correctly", "1. Received context click at (10, 10)", getLogRow(0));
    }

    @Test
    public void testRemoveListener() {
        openTestURL();
        $(ButtonElement.class).first().click();
        new org.openqa.selenium.interactions.Actions(getDriver()).moveToElement($(UIElement.class).first(), 50, 50).contextClick().perform();
        new org.openqa.selenium.interactions.Actions(getDriver()).moveToElement($(UIElement.class).first(), 10, 10).click().perform();
        Assert.assertTrue("Context click should not be handled.", getLogRow(0).trim().isEmpty());
    }
}

