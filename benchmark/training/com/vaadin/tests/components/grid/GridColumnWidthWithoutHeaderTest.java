package com.vaadin.tests.components.grid;


import com.vaadin.testbench.elements.GridElement;
import com.vaadin.tests.tb3.SingleBrowserTest;
import org.junit.Assert;
import org.junit.Test;


public class GridColumnWidthWithoutHeaderTest extends SingleBrowserTest {
    public static final int THRESHOLD = 3;

    @Test
    public void testWidthWithoutHeader() {
        openTestURL();
        GridElement grid = $(GridElement.class).first();
        int columnsWidth = getColWidthsRounded(grid);
        Assert.assertTrue(((Math.abs((columnsWidth - (grid.getSize().getWidth())))) <= (GridColumnWidthWithoutHeaderTest.THRESHOLD)));
    }
}

