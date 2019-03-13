package com.vaadin.tests.elements.table;


import TableElement.ContextMenuElement;
import com.vaadin.testbench.elements.TableElement;
import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.NoSuchElementException;


public class TableElementContextMenuTest extends MultiBrowserTest {
    private TableElement tableElement;

    @Test
    public void tableContextMenu_menuOpenFetchMenu_contextMenuFetchedCorrectly() {
        tableElement.contextClick();
        TableElement.ContextMenuElement contextMenu = tableElement.getContextMenu();
        Assert.assertNotNull("There is no context menu open by tableElement.contextClick()", contextMenu);
    }

    @Test(expected = NoSuchElementException.class)
    public void tableContextMenu_menuClosedfetchContextMenu_exceptionThrown() {
        tableElement.getContextMenu();
    }
}

