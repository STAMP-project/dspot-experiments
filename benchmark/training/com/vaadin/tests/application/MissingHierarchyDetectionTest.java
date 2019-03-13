package com.vaadin.tests.application;


import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.LabelElement;
import com.vaadin.tests.tb3.SingleBrowserTest;
import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;


public class MissingHierarchyDetectionTest extends SingleBrowserTest {
    @Test
    public void testMissingHierarchyDetection() {
        openTestURL();
        Assert.assertTrue(isElementPresent(By.id("label")));
        ButtonElement toggleProperly = $(ButtonElement.class).caption("Toggle properly").first();
        toggleProperly.click();
        assertNoSystemNotifications();
        Assert.assertFalse(isElementPresent(By.id("label")));
        toggleProperly.click();
        assertNoSystemNotifications();
        Assert.assertTrue(isElementPresent(LabelElement.class));
        ButtonElement toggleImproperly = $(ButtonElement.class).caption("Toggle improperly").first();
        toggleImproperly.click();
        assertSystemNotification();
    }
}

