package com.vaadin.v7.tests.components.grid;


import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.GridElement;
import com.vaadin.testbench.parallel.TestCategory;
import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Test;


@TestCategory("grid")
public class GridSubPixelProblemWrappingTest extends MultiBrowserTest {
    @Test
    public void addedRowShouldNotWrap() {
        openTestURL();
        GridElement grid = $(GridElement.class).first();
        // Cells in first row should be at the same y coordinate as the row
        assertRowAndCellTops(grid, 0);
        // Add a row
        $(ButtonElement.class).first().click();
        // Cells in the first row should be at the same y coordinate as the row
        assertRowAndCellTops(grid, 0);
        // Cells in the second row should be at the same y coordinate as the row
        assertRowAndCellTops(grid, 1);
    }
}

