package com.vaadin.tests.components.grid;


import com.vaadin.testbench.elements.GridElement;
import com.vaadin.testbench.elements.GridElement.GridCellElement;
import com.vaadin.testbench.elements.GridElement.GridRowElement;
import com.vaadin.tests.tb3.MultiBrowserTest;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;


public class CompatibilityGridInDetailsRowTest extends MultiBrowserTest {
    @Test
    public void testNestedGridMultiRowHeaderPositions() {
        openTestURL();
        GridElement grid = $(GridElement.class).first();
        GridRowElement row = grid.getRow(1);
        row.doubleClick();
        waitForElementPresent(By.className("v-grid-spacer"));
        GridElement nestedGrid = $(GridElement.class).id("grid2");
        Assert.assertEquals("Incorrect header row count.", 2, nestedGrid.getHeaderCount());
        GridCellElement headerCell00 = nestedGrid.getHeaderCell(0, 0);
        GridCellElement headerCell11 = nestedGrid.getHeaderCell(1, 1);
        Assert.assertThat("Incorrect X-position.", headerCell11.getLocation().getX(), Matchers.greaterThan(headerCell00.getLocation().getX()));
        Assert.assertThat("Incorrect Y-position.", headerCell11.getLocation().getY(), Matchers.greaterThan(headerCell00.getLocation().getY()));
    }

    @Test
    public void testNestedGridRowHeights() {
        openTestURL();
        GridElement grid = $(GridElement.class).first();
        GridRowElement row = grid.getRow(1);
        row.doubleClick();
        waitForElementPresent(By.className("v-grid-spacer"));
        GridElement nestedGrid = $(GridElement.class).id("grid2");
        grid.findElement(By.className("v-grid-sidebar-button")).click();
        Assert.assertNotNull("There are no options for toggling column visibility but there should be.", getColumnHidingToggle(nestedGrid));
    }
}

