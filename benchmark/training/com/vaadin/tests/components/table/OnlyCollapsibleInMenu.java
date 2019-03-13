package com.vaadin.tests.components.table;


import CollapseMenuContent.ALL_COLUMNS;
import CollapseMenuContent.COLLAPSIBLE_COLUMNS;
import com.vaadin.testbench.elements.TableElement;
import com.vaadin.tests.tb3.SingleBrowserTest;
import org.junit.Assert;
import org.junit.Test;


public class OnlyCollapsibleInMenu extends SingleBrowserTest {
    @Test
    public void testOnlyCollapsibleInMenu() {
        openTestURL();
        TableElement table = $(TableElement.class).first();
        selectMenuPath("Component", "Columns", "Property 3", "Collapsible");
        table.getCollapseMenuToggle().click();
        Assert.assertEquals("Property 3 should still be in the context menu", "Property 3", table.getContextMenu().getItem(2).getText());
        selectMenuPath("Component", "Features", "Collapsible menu content", COLLAPSIBLE_COLUMNS.toString());
        table.getCollapseMenuToggle().click();
        Assert.assertEquals("Property 3 should not be in the context menu", "Property 4", table.getContextMenu().getItem(2).getText());
        selectMenuPath("Component", "Features", "Collapsible menu content", ALL_COLUMNS.toString());
        table.getCollapseMenuToggle().click();
        Assert.assertEquals("Property 3 should again  be in the context menu", "Property 3", table.getContextMenu().getItem(2).getText());
    }
}

