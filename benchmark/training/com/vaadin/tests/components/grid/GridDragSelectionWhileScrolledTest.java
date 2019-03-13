package com.vaadin.tests.components.grid;


import com.vaadin.testbench.elements.GridElement;
import com.vaadin.tests.tb3.MultiBrowserTest;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.JavascriptExecutor;


public class GridDragSelectionWhileScrolledTest extends MultiBrowserTest {
    @Test
    public void testDragSelect() throws IOException {
        openTestURL();
        // Scroll grid to view
        GridElement grid = $(GridElement.class).first();
        ((JavascriptExecutor) (getDriver())).executeScript("arguments[0].scrollIntoView(true);", grid);
        // Drag select 2 rows
        new org.openqa.selenium.interactions.Actions(getDriver()).moveToElement(grid.getCell(3, 0), 5, 5).clickAndHold().moveToElement(grid.getCell(2, 0), 5, 5).release().perform();
        // Assert only those are selected.
        Assert.assertTrue("Row 3 should be selected", grid.getRow(3).isSelected());
        Assert.assertTrue("Row 2 should be selected", grid.getRow(2).isSelected());
        Assert.assertFalse("Row 4 should not be selected", grid.getRow(4).isSelected());
        Assert.assertFalse("Row 1 should not be selected", grid.getRow(1).isSelected());
    }
}

