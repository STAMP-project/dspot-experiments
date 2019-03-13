package com.vaadin.tests.components.grid;


import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.GridElement;
import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Assert;
import org.junit.Test;


public class GridMultiSelectEmptyTest extends MultiBrowserTest {
    @Test
    public void testCheckBoxColumnCorrectSize() {
        openTestURL();
        GridElement grid = $(GridElement.class).first();
        int startingWidth = grid.getHeaderCell(0, 0).getSize().getWidth();
        $(ButtonElement.class).caption("Add Row").first().click();
        int currentWidth = grid.getHeaderCell(0, 0).getSize().getWidth();
        Assert.assertEquals("Checkbox column size should not change when data is added", startingWidth, currentWidth);
        $(ButtonElement.class).caption("Recalculate").first().click();
        currentWidth = grid.getHeaderCell(0, 0).getSize().getWidth();
        Assert.assertEquals("Checkbox column size should not change when columns are recalculated", startingWidth, currentWidth);
    }
}

