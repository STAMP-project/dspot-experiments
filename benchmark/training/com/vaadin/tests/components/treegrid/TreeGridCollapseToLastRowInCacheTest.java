package com.vaadin.tests.components.treegrid;


import com.vaadin.testbench.elements.TreeGridElement;
import com.vaadin.tests.tb3.SingleBrowserTest;
import org.junit.Assert;
import org.junit.Test;


public class TreeGridCollapseToLastRowInCacheTest extends SingleBrowserTest {
    // #8840
    @Test
    public void testCollapsingNode_removesLastRowFromGridCache_noInternalError() {
        TreeGridElement grid = $(TreeGridElement.class).first();
        grid.expandWithClick(0);
        grid.expandWithClick(1);
        assertNoErrorNotifications();
        Assert.assertEquals("0 | 0", grid.getCell(0, 0).getText());
        Assert.assertEquals("1 | 0", grid.getCell(1, 0).getText());
        Assert.assertEquals("2 | 0", grid.getCell(2, 0).getText());
        grid.collapseWithClick(0);
        Assert.assertEquals("0 | 0", grid.getCell(0, 0).getText());
        Assert.assertEquals("0 | 1", grid.getCell(1, 0).getText());
        assertNoErrorNotifications();
    }
}

