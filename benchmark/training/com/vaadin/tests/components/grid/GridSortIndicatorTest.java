package com.vaadin.tests.components.grid;


import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.GridElement;
import com.vaadin.testbench.parallel.TestCategory;
import com.vaadin.tests.tb3.SingleBrowserTest;
import org.junit.Assert;
import org.junit.Test;


@TestCategory("grid")
public class GridSortIndicatorTest extends SingleBrowserTest {
    @Test
    public void testIndicators() throws InterruptedException {
        openTestURL();
        GridElement grid = $(GridElement.class).first();
        $(ButtonElement.class).caption("Sort both").first().click();
        Assert.assertTrue("First column should be sorted ascending", grid.getHeaderCell(0, 0).getAttribute("class").contains("sort-asc"));
        Assert.assertEquals("First column should have aria-sort other", "other", grid.getHeaderCell(0, 0).getAttribute("aria-sort"));
        Assert.assertEquals("First column should be first in sort order", "1", grid.getHeaderCell(0, 0).getAttribute("sort-order"));
        Assert.assertTrue("Second column should be sorted ascending", grid.getHeaderCell(0, 1).getAttribute("class").contains("sort-asc"));
        Assert.assertEquals("Second column should have aria-sort other", "other", grid.getHeaderCell(0, 1).getAttribute("aria-sort"));
        Assert.assertEquals("Second column should be also sorted", "2", grid.getHeaderCell(0, 1).getAttribute("sort-order"));
        $(ButtonElement.class).caption("Sort first").first().click();
        Assert.assertEquals("First column should have aria-sort ascending", "ascending", grid.getHeaderCell(0, 0).getAttribute("aria-sort"));
        Assert.assertTrue("First column should be sorted ascending", grid.getHeaderCell(0, 0).getAttribute("class").contains("sort-asc"));
        Assert.assertEquals("Second column should have aria-sort none", "none", grid.getHeaderCell(0, 1).getAttribute("aria-sort"));
        Assert.assertFalse("Second column should not be sorted", grid.getHeaderCell(0, 1).getAttribute("class").contains("sort-asc"));
    }
}

