package com.vaadin.tests.components.tabsheet;


import Keys.ARROW_RIGHT;
import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Assert;
import org.junit.Test;


public class TabSheetFocusedTabTest extends MultiBrowserTest {
    @Test
    public void clickingChangesFocusedTab() throws Exception {
        openTestURL();
        getTab(1).click();
        Assert.assertTrue(isFocused(getTab(1)));
        new org.openqa.selenium.interactions.Actions(getDriver()).sendKeys(ARROW_RIGHT).perform();
        Assert.assertFalse(isFocused(getTab(1)));
        Assert.assertTrue(isFocused(getTab(3)));
        getTab(5).click();
        Assert.assertFalse(isFocused(getTab(3)));
        Assert.assertTrue(isFocused(getTab(5)));
        getTab(1).click();
        Assert.assertFalse(isFocused(getTab(5)));
        Assert.assertTrue(isFocused(getTab(1)));
    }
}

