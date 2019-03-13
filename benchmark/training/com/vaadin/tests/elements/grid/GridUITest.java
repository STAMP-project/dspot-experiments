package com.vaadin.tests.elements.grid;


import com.vaadin.testbench.elements.GridElement;
import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.NoSuchElementException;


public class GridUITest extends MultiBrowserTest {
    @Test
    public void testRowCount() {
        openTestURL("rowCount=0");
        Assert.assertEquals(0, getRowCount());
        openTestURL("rowCount=1&restartApplication");
        Assert.assertEquals(1, getRowCount());
        openTestURL("rowCount=10&restartApplication");
        Assert.assertEquals(10, getRowCount());
        openTestURL("rowCount=1000&restartApplication");
        Assert.assertEquals(1000, getRowCount());
    }

    @Test
    public void testGetRows() {
        openTestURL("rowCount=0");
        Assert.assertEquals(0, checkRows());
        openTestURL("rowCount=1&restartApplication");
        Assert.assertEquals(1, checkRows());
        openTestURL("rowCount=10&restartApplication");
        Assert.assertEquals(10, checkRows());
        openTestURL("rowCount=100&restartApplication");
        Assert.assertEquals(100, checkRows());
    }

    @Test
    public void testGetHeadersByCaptionFirstRowFirstColumn() {
        openTestURL("rowCount=10&restartApplication");
        GridElement grid = $(GridElement.class).first();
        grid.getHeaderCellByCaption("foo");
    }

    @Test
    public void testGetHeadersByCaptionFirstRowNotFirstColumn() {
        openTestURL("rowCount=10&restartApplication");
        GridElement grid = $(GridElement.class).first();
        grid.getHeaderCellByCaption("bar");
    }

    @Test(expected = NoSuchElementException.class)
    public void testGetHeadersByCaptionNoHeader() {
        openTestURL("rowCount=10&restartApplication");
        GridElement grid = $(GridElement.class).first();
        grid.getHeaderCellByCaption("not existing caption");
    }

    @Test(expected = NoSuchElementException.class)
    public void testGetHeadersByCaptionByIndexNoHeader() {
        openTestURL("rowCount=10&restartApplication");
        GridElement grid = $(GridElement.class).first();
        grid.getHeaderCellByCaption(0, "not existing caption");
    }

    @Test
    public void testGetHeadersByCaptionNotFirstRow() {
        openTestURL("rowCount=10&restartApplication");
        GridElement grid = $(GridElement.class).first();
        grid.getHeaderCellByCaption("extra row");
    }

    @Test
    public void testGetHeadersByCaptionByIndexNotFirstRow() {
        openTestURL("rowCount=10&restartApplication");
        GridElement grid = $(GridElement.class).first();
        grid.getHeaderCellByCaption(1, "extra row");
    }
}

