package com.vaadin.tests.components.grid;


import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.GridElement;
import com.vaadin.testbench.elements.NativeSelectElement;
import com.vaadin.testbench.elements.NotificationElement;
import com.vaadin.testbench.parallel.TestCategory;
import com.vaadin.tests.tb3.SingleBrowserTest;
import org.junit.Assert;
import org.junit.Test;


@TestCategory("grid")
public class GridColumnWidthsWithoutDataTest extends SingleBrowserTest {
    @Test
    public void testWidthsWhenAddingDataBack() {
        openTestURL();
        GridElement grid = $(GridElement.class).first();
        int[] baseWidths = getColWidths(grid);
        Assert.assertEquals("Sanity check", 2, baseWidths.length);
        Assert.assertTrue("Columns should not have equal width", ((Math.abs(((baseWidths[0]) - (baseWidths[1])))) > 2));
        removeData();
        GridColumnWidthsWithoutDataTest.assertSameWidths(baseWidths, getColWidths(grid));
        addData();
        GridColumnWidthsWithoutDataTest.assertSameWidths(baseWidths, getColWidths(grid));
    }

    @Test
    public void testWidthsWhenInitiallyEmpty() {
        setDebug(true);
        openTestURL();
        $(ButtonElement.class).caption("Recreate without data").first().click();
        GridElement grid = $(GridElement.class).first();
        int[] baseWidths = getColWidths(grid);
        Assert.assertEquals("Sanity check", 2, baseWidths.length);
        Assert.assertTrue("Columns should have roughly equal width", ((Math.abs(((baseWidths[0]) - (baseWidths[1])))) < 10));
        Assert.assertTrue("Columns should not have default widths", ((baseWidths[0]) > 140));
        Assert.assertTrue("Columns should not have default widths", ((baseWidths[1]) > 140));
        addData();
        GridColumnWidthsWithoutDataTest.assertSameWidths(baseWidths, getColWidths(grid));
        Assert.assertFalse("Notification was present", isElementPresent(NotificationElement.class));
    }

    @Test
    public void testMultiSelectWidths() {
        setDebug(true);
        openTestURL();
        $(NativeSelectElement.class).caption("Selection mode").first().selectByText("MULTI");
        GridElement grid = $(GridElement.class).first();
        int sum = sumUsedWidths(grid);
        // 295 instead of 300 to avoid rounding issues
        Assert.assertTrue((("Only " + sum) + " out of 300px was used"), (sum > 295));
        $(ButtonElement.class).caption("Recreate without data").first().click();
        grid = $(GridElement.class).first();
        sum = sumUsedWidths(grid);
        // 295 instead of 300 to avoid rounding issues
        Assert.assertTrue((("Only " + sum) + " out of 300px was used"), (sum > 295));
    }
}

