package com.vaadin.tests.components.grid;


import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.CheckBoxElement;
import com.vaadin.testbench.elements.GridElement;
import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Assert;
import org.junit.Test;


public class GridSortWhenPossibleTest extends MultiBrowserTest {
    @Test
    public void inMemory() throws InterruptedException {
        openTestURL();
        $(CheckBoxElement.class).first().click();
        $(ButtonElement.class).first().click();
        GridElement grid = $(GridElement.class).first();
        assertRow(grid, 0, "a", "4", true);
        assertRow(grid, 1, "b", "5", false);
        assertRow(grid, 2, "c", "3", false);
        assertRow(grid, 3, "a", "6", false);
        assertRow(grid, 4, "a", "2", true);
        assertRow(grid, 5, "c", "7", false);
        assertRow(grid, 6, "b", "1", true);
        grid.getHeaderCell(0, 0).click();
        Assert.assertTrue("First column should be sorted ascending", grid.getHeaderCell(0, 0).getAttribute("class").contains("sort-asc"));
        Assert.assertFalse("Second column should not be sorted", grid.getHeaderCell(0, 1).getAttribute("class").contains("sort-"));
        Assert.assertFalse("Third column should not be sorted", grid.getHeaderCell(0, 2).getAttribute("class").contains("sort-"));
        assertRow(grid, 0, "a", "4", true);
        assertRow(grid, 1, "a", "6", false);
        assertRow(grid, 2, "a", "2", true);
        assertRow(grid, 3, "b", "5", false);
        assertRow(grid, 4, "b", "1", true);
        assertRow(grid, 5, "c", "3", false);
        assertRow(grid, 6, "c", "7", false);
        grid.getHeaderCell(0, 1).click();
        Assert.assertFalse("First column should not be sorted", grid.getHeaderCell(0, 0).getAttribute("class").contains("sort-"));
        Assert.assertTrue("Second column should be sorted ascending", grid.getHeaderCell(0, 1).getAttribute("class").contains("sort-asc"));
        Assert.assertFalse("Third column should not be sorted", grid.getHeaderCell(0, 2).getAttribute("class").contains("sort-"));
        assertRow(grid, 0, "b", "1", true);
        assertRow(grid, 1, "a", "2", true);
        assertRow(grid, 2, "c", "3", false);
        assertRow(grid, 3, "a", "4", true);
        assertRow(grid, 4, "b", "5", false);
        assertRow(grid, 5, "a", "6", false);
        assertRow(grid, 6, "c", "7", false);
        grid.getHeaderCell(0, 2).click();
        Assert.assertFalse("First column should not be sorted", grid.getHeaderCell(0, 0).getAttribute("class").contains("sort-"));
        Assert.assertFalse("Second column should not be sorted", grid.getHeaderCell(0, 1).getAttribute("class").contains("sort-"));
        Assert.assertTrue("Third column should be sorted ascending", grid.getHeaderCell(0, 2).getAttribute("class").contains("sort-asc"));
        assertRow(grid, 0, "b", "5", false);
        assertRow(grid, 1, "c", "3", false);
        assertRow(grid, 2, "a", "6", false);
        assertRow(grid, 3, "c", "7", false);
        assertRow(grid, 4, "a", "4", true);
        assertRow(grid, 5, "a", "2", true);
        assertRow(grid, 6, "b", "1", true);
    }

    @Test
    public void lazyLoading() throws InterruptedException {
        openTestURL();
        $(ButtonElement.class).first().click();
        GridElement grid = $(GridElement.class).first();
        assertRow(grid, 0, "a", "4", true);
        assertRow(grid, 1, "b", "5", false);
        assertRow(grid, 2, "c", "3", false);
        assertRow(grid, 3, "a", "6", false);
        assertRow(grid, 4, "a", "2", true);
        assertRow(grid, 5, "c", "7", false);
        assertRow(grid, 6, "b", "1", true);
        grid.getHeaderCell(0, 0).click();
        Assert.assertTrue("First column should be sorted ascending", grid.getHeaderCell(0, 0).getAttribute("class").contains("sort-asc"));
        Assert.assertFalse("Second column should not be sorted", grid.getHeaderCell(0, 1).getAttribute("class").contains("sort-"));
        Assert.assertFalse("Third column should not be sorted", grid.getHeaderCell(0, 2).getAttribute("class").contains("sort-"));
        assertRow(grid, 0, "a", "4", true);
        assertRow(grid, 1, "a", "6", false);
        assertRow(grid, 2, "a", "2", true);
        assertRow(grid, 3, "b", "5", false);
        assertRow(grid, 4, "b", "1", true);
        assertRow(grid, 5, "c", "3", false);
        assertRow(grid, 6, "c", "7", false);
        grid.getHeaderCell(0, 1).click();
        Assert.assertFalse("First column should not be sorted", grid.getHeaderCell(0, 0).getAttribute("class").contains("sort-"));
        Assert.assertTrue("Second column should be sorted ascending", grid.getHeaderCell(0, 1).getAttribute("class").contains("sort-asc"));
        Assert.assertFalse("Third column should not be sorted", grid.getHeaderCell(0, 2).getAttribute("class").contains("sort-"));
        assertRow(grid, 0, "b", "1", true);
        assertRow(grid, 1, "a", "2", true);
        assertRow(grid, 2, "c", "3", false);
        assertRow(grid, 3, "a", "4", true);
        assertRow(grid, 4, "b", "5", false);
        assertRow(grid, 5, "a", "6", false);
        assertRow(grid, 6, "c", "7", false);
        grid.getHeaderCell(0, 2).click();
        Assert.assertFalse("First column should not be sorted", grid.getHeaderCell(0, 0).getAttribute("class").contains("sort-"));
        Assert.assertTrue("Second column should be sorted ascending", grid.getHeaderCell(0, 1).getAttribute("class").contains("sort-asc"));
        Assert.assertFalse("Third column should not be sorted", grid.getHeaderCell(0, 2).getAttribute("class").contains("sort-"));
        assertRow(grid, 0, "b", "1", true);
        assertRow(grid, 1, "a", "2", true);
        assertRow(grid, 2, "c", "3", false);
        assertRow(grid, 3, "a", "4", true);
        assertRow(grid, 4, "b", "5", false);
        assertRow(grid, 5, "a", "6", false);
        assertRow(grid, 6, "c", "7", false);
    }
}

