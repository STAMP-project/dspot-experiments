package com.vaadin.tests.components.treegrid;


import com.vaadin.testbench.elements.TreeGridElement;
import com.vaadin.tests.tb3.SingleBrowserTest;
import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;


public class TreeGridDoubleClickTest extends SingleBrowserTest {
    @Test
    public void double_click_on_hierarchy_renderer() {
        openTestURL();
        TreeGridElement grid = $(TreeGridElement.class).first();
        WebElement hierarchyCell = grid.findElement(By.className("v-treegrid-node"));
        new org.openqa.selenium.interactions.Actions(getDriver()).doubleClick(hierarchyCell).perform();
        Assert.assertTrue("Double click is not handled", isDoubleClickNotificationPresent());
    }
}

