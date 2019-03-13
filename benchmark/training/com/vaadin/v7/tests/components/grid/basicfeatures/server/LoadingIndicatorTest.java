package com.vaadin.v7.tests.components.grid.basicfeatures.server;


import com.vaadin.testbench.elements.GridElement;
import com.vaadin.v7.tests.components.grid.basicfeatures.GridBasicFeaturesTest;
import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;


public class LoadingIndicatorTest extends GridBasicFeaturesTest {
    @Test
    public void testLoadingIndicator() throws InterruptedException {
        setDebug(true);
        openTestURL();
        selectMenuPath("Component", "State", "Container delay", "2000");
        GridElement gridElement = $(GridElement.class).first();
        Assert.assertFalse("Loading indicator should not be visible before disabling waitForVaadin", isLoadingIndicatorVisible());
        testBench().disableWaitForVaadin();
        // Scroll to a completely new location
        gridElement.getCell(200, 1);
        // Wait for loading indicator delay
        waitUntil(ExpectedConditions.visibilityOfElementLocated(By.className("v-loading-indicator")));
        waitUntilNot(ExpectedConditions.visibilityOfElementLocated(By.className("v-loading-indicator")));
        // Scroll so much that more data gets fetched, but not so much that
        // missing rows are shown
        gridElement.getCell(230, 1);
        // Wait for potentially triggered loading indicator to become visible
        Thread.sleep(500);
        Assert.assertFalse("Loading indicator should not be visible when fetching rows that are not visible", isLoadingIndicatorVisible());
        // Finally verify that there was actually a request going on
        waitUntilLogContains("Requested items");
    }
}

