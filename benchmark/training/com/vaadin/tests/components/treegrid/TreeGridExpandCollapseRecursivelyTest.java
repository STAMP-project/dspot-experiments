package com.vaadin.tests.components.treegrid;


import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.RadioButtonGroupElement;
import com.vaadin.testbench.elements.TreeGridElement;
import com.vaadin.tests.tb3.SingleBrowserTest;
import org.junit.Assert;
import org.junit.Test;


public class TreeGridExpandCollapseRecursivelyTest extends SingleBrowserTest {
    private static final int rowCount0 = 5;

    private static final int rowCount1 = (TreeGridExpandCollapseRecursivelyTest.rowCount0) + ((TreeGridExpandCollapseRecursivelyTest.rowCount0) * 5);

    private static final int rowCount2 = (TreeGridExpandCollapseRecursivelyTest.rowCount1) + (((TreeGridExpandCollapseRecursivelyTest.rowCount1) - (TreeGridExpandCollapseRecursivelyTest.rowCount0)) * 5);

    private static final int rowCount3 = (TreeGridExpandCollapseRecursivelyTest.rowCount2) + (((TreeGridExpandCollapseRecursivelyTest.rowCount2) - (TreeGridExpandCollapseRecursivelyTest.rowCount1)) * 5);

    private static final int rowCount4 = (TreeGridExpandCollapseRecursivelyTest.rowCount3) + (((TreeGridExpandCollapseRecursivelyTest.rowCount3) - (TreeGridExpandCollapseRecursivelyTest.rowCount2)) * 5);

    private TreeGridElement grid;

    private RadioButtonGroupElement depthSelector;

    private ButtonElement expandButton;

    private ButtonElement collapseButton;

    @Test
    public void expandVariousDepth() {
        Assert.assertEquals(TreeGridExpandCollapseRecursivelyTest.rowCount0, grid.getRowCount());
        selectDepth(0);
        expandButton.click();
        Assert.assertEquals(TreeGridExpandCollapseRecursivelyTest.rowCount1, grid.getRowCount());
        selectDepth(1);
        expandButton.click();
        Assert.assertEquals(TreeGridExpandCollapseRecursivelyTest.rowCount2, grid.getRowCount());
        selectDepth(2);
        expandButton.click();
        Assert.assertEquals(TreeGridExpandCollapseRecursivelyTest.rowCount3, grid.getRowCount());
        selectDepth(3);
        expandButton.click();
        Assert.assertEquals(TreeGridExpandCollapseRecursivelyTest.rowCount4, grid.getRowCount());
    }

    @Test(timeout = 5000)
    public void expandAndCollapseAllItems() {
        Assert.assertEquals(TreeGridExpandCollapseRecursivelyTest.rowCount0, grid.getRowCount());
        selectDepth(3);
        expandButton.click();
        Assert.assertEquals(TreeGridExpandCollapseRecursivelyTest.rowCount4, grid.getRowCount());
        collapseButton.click();
        Assert.assertEquals(TreeGridExpandCollapseRecursivelyTest.rowCount0, grid.getRowCount());
    }

    @Test
    public void partialCollapse() {
        Assert.assertEquals(TreeGridExpandCollapseRecursivelyTest.rowCount0, grid.getRowCount());
        selectDepth(3);
        expandButton.click();
        Assert.assertEquals(TreeGridExpandCollapseRecursivelyTest.rowCount4, grid.getRowCount());
        selectDepth(1);
        collapseButton.click();
        Assert.assertEquals(TreeGridExpandCollapseRecursivelyTest.rowCount0, grid.getRowCount());
        selectDepth(0);
        expandButton.click();
        Assert.assertEquals(TreeGridExpandCollapseRecursivelyTest.rowCount1, grid.getRowCount());
        // Open just one subtree to see if it is still fully expanded
        grid.getExpandElement(2, 0).click();
        Assert.assertEquals(((TreeGridExpandCollapseRecursivelyTest.rowCount1) + (TreeGridExpandCollapseRecursivelyTest.rowCount2)), grid.getRowCount());
    }
}

