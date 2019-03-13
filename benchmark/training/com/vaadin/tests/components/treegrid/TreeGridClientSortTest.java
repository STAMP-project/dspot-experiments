package com.vaadin.tests.components.treegrid;


import com.vaadin.testbench.elements.TreeGridElement;
import com.vaadin.tests.tb3.SingleBrowserTest;
import org.junit.Assert;
import org.junit.Test;


public class TreeGridClientSortTest extends SingleBrowserTest {
    @Test
    public void client_sorting_with_collapse_and_expand() {
        openTestURL();
        TreeGridElement grid = $(TreeGridElement.class).first();
        selectMenuPath("Component", "Features", "Set data provider", "TreeDataProvider");
        grid.getHeaderCell(0, 0).click();
        grid.getHeaderCell(0, 0).click();
        grid.expandWithClick(0);
        grid.expandWithClick(1);
        grid.collapseWithClick(0);
        grid.expandWithClick(0);
        Assert.assertEquals("0 | 2", grid.getCell(0, 0).getText());
        Assert.assertEquals("1 | 2", grid.getCell(1, 0).getText());
        Assert.assertEquals("2 | 2", grid.getCell(2, 0).getText());
    }
}

