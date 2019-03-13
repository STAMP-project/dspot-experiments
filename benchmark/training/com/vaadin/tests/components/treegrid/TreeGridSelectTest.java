package com.vaadin.tests.components.treegrid;


import com.vaadin.testbench.elements.TreeGridElement;
import com.vaadin.tests.tb3.SingleBrowserTest;
import org.junit.Test;


public class TreeGridSelectTest extends SingleBrowserTest {
    @Test
    public void select_and_deselect_all() {
        openTestURL();
        selectMenuPath("Component", "Features", "Set data provider", "TreeDataProvider");
        selectMenuPath("Component", "State", "Selection mode", "multi");
        TreeGridElement grid = $(TreeGridElement.class).first();
        assertAllRowsDeselected(grid);
        clickSelectAll(grid);
        assertAllRowsSelected(grid);
        grid.expandWithClick(1, 1);
        grid.expandWithClick(2, 1);
        assertAllRowsSelected(grid);
        clickSelectAll(grid);
        assertAllRowsDeselected(grid);
        clickSelectAll(grid);
        grid.collapseWithClick(2, 1);
        grid.expandWithClick(2, 1);
        assertAllRowsSelected(grid);
        grid.collapseWithClick(2, 1);
        clickSelectAll(grid);
        grid.expandWithClick(2, 1);
        assertAllRowsDeselected(grid);
    }
}

