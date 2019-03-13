package com.vaadin.v7.tests.components.grid.basicfeatures.client;


import com.vaadin.v7.tests.components.grid.basicfeatures.GridBasicClientFeaturesTest;
import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;


public class GridClientContextMenuEventTest extends GridBasicClientFeaturesTest {
    @Test
    public void testContextMenuEventIsHandledCorrectly() {
        setDebug(true);
        openTestURL();
        selectMenuPath("Component", "Internals", "Listeners", "Add context menu listener");
        openDebugLogTab();
        clearDebugMessages();
        new org.openqa.selenium.interactions.Actions(getDriver()).moveToElement(getGridElement().getCell(0, 0), 5, 5).contextClick().perform();
        Assert.assertTrue("Debug log was not visible", isElementPresent(By.xpath("//span[text() = 'Prevented opening a context menu in grid body']")));
        new org.openqa.selenium.interactions.Actions(getDriver()).moveToElement(getGridElement().getHeaderCell(0, 0), 5, 5).contextClick().perform();
        Assert.assertTrue("Debug log was not visible", isElementPresent(By.xpath("//span[text() = 'Prevented opening a context menu in grid header']")));
    }
}

