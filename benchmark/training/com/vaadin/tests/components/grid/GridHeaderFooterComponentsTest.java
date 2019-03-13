package com.vaadin.tests.components.grid;


import com.vaadin.testbench.By;
import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.GridElement;
import com.vaadin.testbench.elements.TextFieldElement;
import com.vaadin.tests.tb3.SingleBrowserTest;
import org.junit.Assert;
import org.junit.Test;


public class GridHeaderFooterComponentsTest extends SingleBrowserTest {
    @Test
    public void hideAndShowComponentsInHeader() {
        GridElement grid = $(GridElement.class).first();
        int filterRow = 2;
        Assert.assertNull(getHeaderElement(grid, filterRow, 1));
        Assert.assertNotNull(getHeaderElement(grid, filterRow, 2));
        Assert.assertNotNull(getHeaderElement(grid, filterRow, 3));
        // Show (1,2)
        $(ButtonElement.class).first().click();
        TextFieldElement textfield = getHeaderElement(grid, filterRow, 1);
        Assert.assertNotNull(textfield);
        Assert.assertEquals("Filter: string", textfield.getValue());
        textfield.setValue("foo");
        Assert.assertEquals("1. value change for field in string to foo", getLogRow(0));
        assertNoErrorNotifications();
    }

    @Test
    public void hideAndShowComponentsInFooter() {
        GridElement grid = $(GridElement.class).first();
        int filterRow = 0;
        Assert.assertNull(getFooterElement(grid, filterRow, 1));
        Assert.assertNotNull(getFooterElement(grid, filterRow, 2));
        Assert.assertNotNull(getFooterElement(grid, filterRow, 3));
        // Show (1,2)
        $(ButtonElement.class).first().click();
        TextFieldElement textfield = getFooterElement(grid, filterRow, 1);
        Assert.assertNotNull(textfield);
        Assert.assertEquals("Filter: string", textfield.getValue());
        textfield.setValue("foo");
        Assert.assertEquals("1. value change for field in string to foo", getLogRow(0));
        assertNoErrorNotifications();
    }

    @Test
    public void testRemoveAllHeadersAndFooters() {
        openTestURL();
        for (int i = 2; i >= 0; --i) {
            // Remove Header
            $(ButtonElement.class).first().click();
            Assert.assertFalse((("Header " + i) + " should not be present."), $(GridElement.class).first().isElementPresent(By.vaadin((("#header[" + i) + "]"))));
            // Remove Footer
            $(ButtonElement.class).first().click();
            Assert.assertFalse((("Footer " + i) + " should not be present."), $(GridElement.class).first().isElementPresent(By.vaadin((("#footer[" + i) + "]"))));
        }
        assertNoErrorNotifications();
    }
}

