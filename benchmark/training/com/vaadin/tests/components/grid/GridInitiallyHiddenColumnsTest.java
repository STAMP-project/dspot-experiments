package com.vaadin.tests.components.grid;


import com.vaadin.testbench.elements.GridElement;
import com.vaadin.testbench.parallel.TestCategory;
import com.vaadin.tests.tb3.SingleBrowserTest;
import org.junit.Assert;
import org.junit.Test;


@TestCategory("grid")
public class GridInitiallyHiddenColumnsTest extends SingleBrowserTest {
    @Test
    public void ensureCorrectlyRendered() {
        openTestURL("debug");
        GridElement grid = $(GridElement.class).first();
        Assert.assertEquals("Rowling", grid.getCell(0, 0).getText());
        Assert.assertEquals("Barks", grid.getCell(1, 0).getText());
        getSidebarOpenButton(grid).click();
        getColumnHidingToggle(grid, "First Name").click();
        getColumnHidingToggle(grid, "Age").click();
        getSidebarOpenButton(grid).click();
        Assert.assertEquals("Umberto", grid.getCell(0, 0).getText());
        Assert.assertEquals("Rowling", grid.getCell(0, 1).getText());
        Assert.assertEquals("40", grid.getCell(0, 2).getText());
        Assert.assertEquals("Alex", grid.getCell(1, 0).getText());
        Assert.assertEquals("Barks", grid.getCell(1, 1).getText());
        Assert.assertEquals("25", grid.getCell(1, 2).getText());
    }

    @Test
    public void ensureCorrectlyRenderedAllInitiallyHidden() {
        openTestURL("debug&allHidden");
        GridElement grid = $(GridElement.class).first();
        getSidebarOpenButton(grid).click();
        getColumnHidingToggle(grid, "First Name").click();
        getColumnHidingToggle(grid, "Last Name").click();
        getColumnHidingToggle(grid, "Age").click();
        getSidebarOpenButton(grid).click();
        Assert.assertEquals("Umberto", grid.getCell(0, 0).getText());
        Assert.assertEquals("Rowling", grid.getCell(0, 1).getText());
        Assert.assertEquals("40", grid.getCell(0, 2).getText());
        Assert.assertEquals("Alex", grid.getCell(1, 0).getText());
        Assert.assertEquals("Barks", grid.getCell(1, 1).getText());
        Assert.assertEquals("25", grid.getCell(1, 2).getText());
    }
}

