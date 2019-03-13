package com.vaadin.v7.tests.components.grid;


import com.vaadin.testbench.elements.GridElement;
import com.vaadin.tests.tb3.SingleBrowserTest;
import org.junit.Assert;
import org.junit.Test;


public class NullHeadersTest extends SingleBrowserTest {
    @Test
    public void gridWithNullHeadersShouldBeRendered() {
        openTestURL();
        GridElement grid = $(GridElement.class).first();
        Assert.assertEquals(1, grid.getHeaderCount());
        Assert.assertEquals(3, grid.getHeaderCells(0).size());
        for (int i = 0; i < 3; i++) {
            Assert.assertEquals("", grid.getHeaderCell(0, 0).getText());
        }
        assertRow(grid, 0, "Finland", "foo", "1");
        assertRow(grid, 1, "Swaziland", "bar", "2");
        assertRow(grid, 2, "Japan", "baz", "3");
    }
}

