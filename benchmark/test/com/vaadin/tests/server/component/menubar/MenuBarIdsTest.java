package com.vaadin.tests.server.component.menubar;


import com.vaadin.ui.MenuBar;
import com.vaadin.ui.MenuBar.Command;
import com.vaadin.ui.MenuBar.MenuItem;
import java.util.HashSet;
import java.util.Set;
import org.junit.Test;


public class MenuBarIdsTest implements Command {
    private MenuItem lastSelectedItem;

    private MenuItem menuFile;

    private MenuItem menuEdit;

    private MenuItem menuEditCopy;

    private MenuItem menuEditCut;

    private MenuItem menuEditPaste;

    private MenuItem menuEditFind;

    private MenuItem menuFileOpen;

    private MenuItem menuFileSave;

    private MenuItem menuFileExit;

    private final Set<MenuItem> menuItems = new HashSet<>();

    private MenuBar menuBar;

    @Test
    public void testMenubarIdUniqueness() {
        // Ids within a menubar must be unique
        MenuBarIdsTest.assertUniqueIds(menuBar);
        menuBar.removeItem(menuFile);
        MenuItem file2 = menuBar.addItem("File2", this);
        MenuItem file3 = menuBar.addItem("File3", this);
        MenuItem file2sub = file2.addItem("File2 sub menu", this);
        menuItems.add(file2);
        menuItems.add(file2sub);
        menuItems.add(file3);
        MenuBarIdsTest.assertUniqueIds(menuBar);
    }
}

