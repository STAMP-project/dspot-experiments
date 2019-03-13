package com.vaadin.tests.components.grid;


import DropMode.BETWEEN;
import DropMode.ON_GRID;
import DropMode.ON_TOP;
import DropMode.ON_TOP_OR_BETWEEN;
import com.vaadin.ui.Grid;
import com.vaadin.ui.components.grid.GridDropTarget;
import org.junit.Assert;
import org.junit.Test;


public class GridDropTargetTest {
    private Grid<String> grid;

    private GridDropTarget<String> target;

    @Test
    public void dropAllowedOnSortedGridRows_defaultValue_isTrue() {
        Assert.assertTrue("Default drop allowed should be backwards compatible", target.isDropAllowedOnRowsWhenSorted());
    }

    @Test
    public void dropAllowedOnSortedGridRows_notAllowed_changesDropModeWhenSorted() {
        Assert.assertEquals(BETWEEN, target.getDropMode());
        target.setDropAllowedOnRowsWhenSorted(false);
        Assert.assertEquals(BETWEEN, target.getDropMode());
        grid.sort("1");
        Assert.assertEquals(ON_GRID, target.getDropMode());
        grid.sort("2");
        Assert.assertEquals(ON_GRID, target.getDropMode());
        grid.clearSortOrder();
        Assert.assertEquals(BETWEEN, target.getDropMode());
        grid.clearSortOrder();
        Assert.assertEquals(BETWEEN, target.getDropMode());
        grid.sort("2");
        Assert.assertEquals(ON_GRID, target.getDropMode());
    }

    @Test
    public void dropAllowedOnSortedGridRows_sortedGridIsDisallowed_modeChangesToOnGrid() {
        Assert.assertEquals(BETWEEN, target.getDropMode());
        grid.sort("1");
        Assert.assertEquals(BETWEEN, target.getDropMode());
        target.setDropAllowedOnRowsWhenSorted(false);
        Assert.assertEquals(ON_GRID, target.getDropMode());
        target.setDropAllowedOnRowsWhenSorted(true);
        Assert.assertEquals(BETWEEN, target.getDropMode());
    }

    @Test
    public void dropAllowedOnSortedGridRows_notAllowedBackToAllowed_changesBackToUserDefinedMode() {
        Assert.assertEquals(BETWEEN, target.getDropMode());
        target.setDropAllowedOnRowsWhenSorted(false);
        Assert.assertEquals(BETWEEN, target.getDropMode());
        grid.sort("1");
        Assert.assertEquals(ON_GRID, target.getDropMode());
        target.setDropAllowedOnRowsWhenSorted(true);
        Assert.assertEquals(BETWEEN, target.getDropMode());
        grid.clearSortOrder();
        Assert.assertEquals(BETWEEN, target.getDropMode());
    }

    @Test
    public void dropAllowedOnSortedGridRows_swappingAllowedDropOnSortedOffAndOn() {
        Assert.assertEquals(BETWEEN, target.getDropMode());
        target.setDropAllowedOnRowsWhenSorted(false);
        Assert.assertEquals(BETWEEN, target.getDropMode());
        target.setDropAllowedOnRowsWhenSorted(false);
        Assert.assertEquals(BETWEEN, target.getDropMode());
        target.setDropAllowedOnRowsWhenSorted(true);
        Assert.assertEquals(BETWEEN, target.getDropMode());
        target.setDropAllowedOnRowsWhenSorted(true);
        Assert.assertEquals(BETWEEN, target.getDropMode());
    }

    @Test
    public void dropAllowedOnSortedGridRows_changingDropModeWhileSorted_replacesPreviouslyCachedButDoesntOverride() {
        Assert.assertEquals(BETWEEN, target.getDropMode());
        target.setDropAllowedOnRowsWhenSorted(false);
        Assert.assertEquals(BETWEEN, target.getDropMode());
        grid.sort("1");
        Assert.assertEquals(ON_GRID, target.getDropMode());
        target.setDropMode(ON_TOP);
        Assert.assertEquals(ON_GRID, target.getDropMode());
        Assert.assertFalse("Changing drop mode should not have any effect here", target.isDropAllowedOnRowsWhenSorted());
        grid.clearSortOrder();
        Assert.assertEquals(ON_TOP, target.getDropMode());
        grid.sort("1");
        Assert.assertEquals(ON_GRID, target.getDropMode());
        target.setDropMode(ON_TOP_OR_BETWEEN);
        Assert.assertEquals(ON_GRID, target.getDropMode());
        target.setDropAllowedOnRowsWhenSorted(true);
        Assert.assertEquals(ON_TOP_OR_BETWEEN, target.getDropMode());
    }
}

