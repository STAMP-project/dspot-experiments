package com.vaadin.tests.components.treegrid;


import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.TreeGridElement;
import com.vaadin.tests.tb3.SingleBrowserTest;
import org.junit.Assert;
import org.junit.Test;


public class TreeGridChangingHierarchyTest extends SingleBrowserTest {
    private TreeGridElement grid;

    private ButtonElement addItemsToABtn;

    private ButtonElement addItemsToAABtn;

    private ButtonElement removeAABtn;

    private ButtonElement removeChildrenOfAABtn;

    private ButtonElement removeABtn;

    private ButtonElement removeChildrenOfABtn;

    private ButtonElement removeChildrenOfAAABtn;

    @Test
    public void removing_items_from_hierarchy() {
        addItemsToABtn.click();
        addItemsToAABtn.click();
        grid.expandWithClick(0);
        grid.expandWithClick(1);
        grid.collapseWithClick(0);
        removeAABtn.click();
        // expand "a" after the reset:
        grid.expandWithClick(0);
        // "a/a" should be removed from a's children:
        Assert.assertEquals("a/b", grid.getCell(1, 0).getText());
    }

    @Test
    public void removing_all_children_from_item() {
        addItemsToABtn.click();
        Assert.assertTrue(grid.isRowCollapsed(0, 0));
        // drop added children from backing data source
        removeChildrenOfABtn.click();
        // changes are not refreshed, thus the row should still appear as
        // collapsed
        Assert.assertTrue(grid.isRowCollapsed(0, 0));
        // when encountering 0 children, will reset
        grid.expandWithClick(0);
        Assert.assertEquals(3, grid.getRowCount());
        Assert.assertFalse(grid.hasExpandToggle(0, 0));
        // verify other items still expand/collapse correctly:
        grid.expandWithClick(1);
        Assert.assertEquals("b/a", grid.getCell(2, 0).getText());
        Assert.assertEquals(4, grid.getRowCount());
        grid.collapseWithClick(1);
        Assert.assertEquals("c", grid.getCell(2, 0).getText());
        Assert.assertEquals(3, grid.getRowCount());
    }

    @Test
    public void removal_of_deeply_nested_items() {
        addItemsToABtn.click();
        addItemsToAABtn.click();
        grid.expandWithClick(0);
        grid.expandWithClick(1);
        grid.expandWithClick(2);
        removeChildrenOfAAABtn.click();
        grid.collapseWithClick(1);
        grid.expandWithClick(1);
        Assert.assertEquals("a/a/a", grid.getCell(2, 0).getText());
        Assert.assertFalse(grid.hasExpandToggle(2, 0));
    }

    @Test
    public void changing_selection_from_selected_removed_item() {
        addItemsToABtn.click();
        grid.expandWithClick(0);
        grid.getCell(1, 0).click();
        removeChildrenOfABtn.click();
        // HierarchyMapper will notice the removal of the children of a, and
        // mark it as collapsed.
        // grid.collapseWithClick(0);
        grid.getCell(1, 0).click();
        Assert.assertTrue(grid.getRow(1).isSelected());
    }

    @Test
    public void remove_item_from_root() {
        addItemsToABtn.click();
        removeABtn.click();
        grid.expandWithClick(0);
        Assert.assertEquals("b", grid.getCell(0, 0).getText());
    }
}

