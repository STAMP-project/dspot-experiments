package com.vaadin.v7.tests.components.grid;


import com.vaadin.testbench.elements.GridElement;
import com.vaadin.testbench.parallel.TestCategory;
import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Assert;
import org.junit.Test;


@TestCategory("grid")
public class GridThemeChangeTest extends MultiBrowserTest {
    @Test
    public void testThemeChange() {
        openTestURL("debug");
        GridElement grid = $(GridElement.class).first();
        int reindeerHeight = grid.getRow(0).getSize().getHeight();
        grid.getCell(0, 0).click();
        grid = $(GridElement.class).first();
        int valoHeight = grid.getRow(0).getSize().getHeight();
        Assert.assertTrue("Row height should increase when changing from Reindeer to Valo", (valoHeight > reindeerHeight));
    }
}

