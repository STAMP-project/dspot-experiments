package com.vaadin.tests.components.grid;


import com.vaadin.testbench.elements.GridElement;
import com.vaadin.testbench.parallel.TestCategory;
import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Assert;
import org.junit.Test;


@TestCategory("grid")
public class GridEditingWithNoScrollBarsTest extends MultiBrowserTest {
    @Test
    public void testEditorWideEnough() {
        openTestURL();
        GridElement grid = $(GridElement.class).first();
        grid.getCell(1, 1).doubleClick();
        Assert.assertEquals(grid.getEditor().getSize().width, grid.getTableWrapper().getSize().width);
    }
}

