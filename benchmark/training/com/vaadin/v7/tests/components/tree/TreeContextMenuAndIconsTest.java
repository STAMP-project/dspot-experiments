package com.vaadin.v7.tests.components.tree;


import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebElement;


public class TreeContextMenuAndIconsTest extends MultiBrowserTest {
    @Test
    public void testSimpleContextMenu() throws Exception {
        openTestURL();
        selectMenuPath("Settings", "Show event log");
        selectMenuPath("Component", "Features", "Context menu", "Item without icon");
        openContextMenu(getTreeNodeByCaption("Item 1"));
        compareScreen("contextmenu-noicon");
        closeContextMenu();
    }

    @Test
    public void testContextMenuWithAndWithoutIcon() throws Exception {
        openTestURL();
        selectMenuPath("Settings", "Show event log");
        selectMenuPath("Component", "Features", "Context menu", "With and without icon");
        openContextMenu(getTreeNodeByCaption("Item 1"));
        compareScreen("caption-only-and-has-icon");
        closeContextMenu();
    }

    @Test
    public void testContextLargeIcon() throws Exception {
        openTestURL();
        selectMenuPath("Settings", "Show event log");
        selectMenuPath("Component", "Features", "Context menu", "Only one large icon");
        WebElement menu = openContextMenu(getTreeNodeByCaption("Item 1"));
        // reindeer doesn't support menu with larger row height, so the
        // background image contains parts of other sprites =>
        // just check that the menu is of correct size
        Dimension size = menu.getSize();
        Assert.assertEquals("Menu height with large icons", 74, size.height);
        closeContextMenu();
    }

    @Test
    public void testContextRemoveIcon() throws Exception {
        openTestURL();
        selectMenuPath("Settings", "Show event log");
        selectMenuPath("Component", "Features", "Context menu", "Only one large icon");
        openContextMenu(getTreeNodeByCaption("Item 1"));
        closeContextMenu();
        selectMenuPath("Component", "Features", "Context menu", "Item without icon");
        openContextMenu(getTreeNodeByCaption("Item 1"));
        compareScreen("contextmenu-noicon");
        closeContextMenu();
    }
}

