package com.vaadin.tests.elements.menubar;


import com.vaadin.testbench.elements.MenuBarElement;
import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Assert;
import org.junit.Test;


public class MenuBarUITest extends MultiBrowserTest {
    // Tests against bug #14568
    @Test
    public void testClickTopLevelItemHavingSubmenuItemFocused() {
        MenuBarElement menuBar = $(MenuBarElement.class).first();
        menuBar.clickItem("File");
        Assert.assertTrue(isItemVisible("Export.."));
        menuBar.clickItem("Export..");
        Assert.assertTrue(isItemVisible("As PDF..."));
        menuBar.clickItem("File");
        Assert.assertFalse(isItemVisible("Export.."));
    }

    /**
     * Validates clickItem(String) of MenuBarElement.
     */
    @Test
    public void testMenuBarClick() {
        MenuBarElement menuBar = $(MenuBarElement.class).first();
        menuBar.clickItem("File");
        Assert.assertTrue(isItemVisible("Save As.."));
        menuBar.clickItem("Export..");
        Assert.assertTrue(isItemVisible("As PDF..."));
        // The Edit menu will be opened by moving the mouse over the item (done
        // by clickItem). The first click then actually closes the menu.
        menuBar.clickItem("Edit");
        menuBar.clickItem("Edit");
        Assert.assertFalse(isItemVisible("Save As.."));
        Assert.assertTrue(isItemVisible("Paste"));
        menuBar.clickItem("Edit");
        Assert.assertFalse(isItemVisible("Save As.."));
        Assert.assertFalse(isItemVisible("Paste"));
        menuBar.clickItem("Edit");
        Assert.assertFalse(isItemVisible("Save As.."));
        Assert.assertTrue(isItemVisible("Paste"));
        // Menu does not contain a submenu, no need to click twice.
        menuBar.clickItem("Help");
        Assert.assertFalse(isItemVisible("Save As.."));
        Assert.assertFalse(isItemVisible("Paste"));
        // No submenu is open, so click only once to open the File menu.
        menuBar.clickItem("File");
        Assert.assertTrue(isItemVisible("Save As.."));
    }

    /**
     * Validates menuBar.clickItem(String...) feature.
     */
    @Test
    public void testMenuBarClickPath() {
        MenuBarElement menuBar = $(MenuBarElement.class).first();
        menuBar.clickItem("File", "Export..");
        Assert.assertTrue(isItemVisible("As Doc..."));
    }

    /**
     * Tests whether the selected MenuBar and its items are the correct ones.
     */
    @Test
    public void testMenuBarSelector() {
        MenuBarElement menuBar = $(MenuBarElement.class).get(2);
        menuBar.clickItem("File");
        Assert.assertTrue(isItemVisible("Open2"));
        // Close the menu item
        menuBar.clickItem("File");
        menuBar = $(MenuBarElement.class).get(1);
        menuBar.clickItem("Edit2");
        Assert.assertTrue(isItemVisible("Cut"));
        menuBar.clickItem("Edit2");
        menuBar = $(MenuBarElement.class).first();
        menuBar.clickItem("File");
        Assert.assertTrue(isItemVisible("Open"));
    }

    @Test
    public void testMenuItemTooltips() {
        MenuBarElement first = $(MenuBarElement.class).first();
        first.clickItem("File");
        assertTooltip("Open", "<b>Preformatted</b>\ndescription");
        assertTooltip("Save", "plain description, <b>HTML</b> is visible");
        assertTooltip("Exit", "HTML\ndescription");
    }
}

