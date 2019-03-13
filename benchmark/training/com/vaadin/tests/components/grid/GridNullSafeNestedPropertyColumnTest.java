package com.vaadin.tests.components.grid;


import com.vaadin.testbench.By;
import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.org.openqa.selenium.By;
import com.vaadin.testbench.parallel.TestCategory;
import com.vaadin.tests.tb3.MultiBrowserTest;
import java.util.List;
import junit.framework.TestCase;
import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.WebElement;


/**
 * Tests that using a nested property name with a null bean child property won't
 * cause an exception.
 */
@TestCategory("grid")
public class GridNullSafeNestedPropertyColumnTest extends MultiBrowserTest {
    @Test
    public void testNullNestedPropertyInSafeGridColumn() {
        openTestURL();
        waitForElementPresent(org.openqa.selenium.By.className("v-grid"));
        $(ButtonElement.class).id("safe").click();
        $(ButtonElement.class).id("add").click();
        List<WebElement> errorIndicator = findElements(By.className("v-errorindicator"));
        TestCase.assertTrue(errorIndicator.isEmpty());
    }

    @Test
    public void testNullNestedPropertyInUnsafeGridColumn() {
        openTestURL();
        waitForElementPresent(org.openqa.selenium.By.className("v-grid"));
        $(ButtonElement.class).id("unsafe").click();
        $(ButtonElement.class).id("add").click();
        List<WebElement> errorIndicator = findElements(By.className("v-errorindicator"));
        Assert.assertFalse("There should be an error indicator when adding nested null values to Grid", errorIndicator.isEmpty());
    }
}

