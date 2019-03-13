package com.vaadin.tests.components.grid;


import com.vaadin.testbench.elements.GridElement;
import com.vaadin.testbench.elements.GridElement.GridCellElement;
import com.vaadin.testbench.elements.GridElement.GridRowElement;
import com.vaadin.tests.tb3.MultiBrowserTest;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;


public class GridInDetailsRowTest extends MultiBrowserTest {
    @Test
    public void testNestedGridMultiRowHeaderPositions() {
        openTestURL();
        GridElement grid = $(GridElement.class).first();
        GridRowElement row = grid.getRow(2);
        row.doubleClick();
        waitForElementPresent(By.className("v-grid-spacer"));
        GridElement nestedGrid = $(GridElement.class).id("grid1");
        GridCellElement headerCell00 = nestedGrid.getHeaderCell(0, 0);
        GridCellElement headerCell11 = nestedGrid.getHeaderCell(1, 1);
        Assert.assertThat("Incorrect X-position.", headerCell11.getLocation().getX(), Matchers.greaterThan(headerCell00.getLocation().getX()));
        Assert.assertThat("Incorrect Y-position.", headerCell11.getLocation().getY(), Matchers.greaterThan(headerCell00.getLocation().getY()));
    }

    @Test
    public void testNestedGridRowHeights() {
        openTestURL();
        GridElement grid = $(GridElement.class).first();
        GridRowElement row = grid.getRow(2);
        row.doubleClick();
        waitForElementPresent(By.className("v-grid-spacer"));
        GridElement nestedGrid = $(GridElement.class).id("grid1");
        GridCellElement cell = nestedGrid.getCell(0, 0);
        Assert.assertThat("Incorrect row height.", cell.getSize().height, Matchers.greaterThan(30));
    }
}

