package com.vaadin.tests.components.grid;


import Keys.ESCAPE;
import com.vaadin.testbench.By;
import com.vaadin.testbench.elements.GridElement;
import com.vaadin.testbench.elements.TabSheetElement;
import com.vaadin.testbench.parallel.TestCategory;
import com.vaadin.tests.tb3.MultiBrowserTest;
import java.util.function.Consumer;
import org.junit.Assert;
import org.junit.Test;


@TestCategory("grid")
public class GridInTabSheetTest extends MultiBrowserTest {
    @Test
    public void testRemoveAllRowsAndAddThreeNewOnes() {
        setDebug(true);
        openTestURL();
        for (int i = 0; i < 3; ++i) {
            removeGridRow();
        }
        for (int i = 0; i < 3; ++i) {
            addGridRow();
            Assert.assertEquals(("" + (100 + i)), getGridElement().getCell(i, 1).getText());
        }
        assertNoNotification();
    }

    @Test
    public void testAddManyRowsWhenGridIsHidden() {
        setDebug(true);
        openTestURL();
        TabSheetElement tabsheet = $(TabSheetElement.class).first();
        tabsheet.openTab("Label");
        for (int i = 0; i < 50; ++i) {
            addGridRow();
        }
        tabsheet.openTab("Grid");
        assertNoNotification();
    }

    @Test
    public void testAddCellStyleGeneratorWhenGridIsHidden() {
        setDebug(true);
        openTestURL();
        TabSheetElement tabsheet = $(TabSheetElement.class).first();
        tabsheet.openTab("Label");
        addCellStyleGenerator();
        tabsheet.openTab("Grid");
        assertNoNotification();
    }

    @Test
    public void testNoDataRequestFromClientWhenSwitchingTab() {
        setDebug(true);
        openTestURL();
        TabSheetElement tabsheet = $(TabSheetElement.class).first();
        tabsheet.openTab("Label");
        tabsheet.openTab("Grid");
        getLogs().forEach(( logText) -> Assert.assertTrue(("There should be no logged requests, was: " + logText), logText.trim().isEmpty()));
        assertNoNotification();
    }

    @Test
    public void testEditorOpenWhenSwitchingTab() {
        setDebug(true);
        openTestURL();
        GridElement grid = $(GridElement.class).first();
        editRow(grid, 0);
        Assert.assertEquals("Editor should be open", "0", grid.getEditor().getField(1).getAttribute("value"));
        TabSheetElement tabsheet = $(TabSheetElement.class).first();
        tabsheet.openTab("Label");
        tabsheet.openTab("Grid");
        grid = $(GridElement.class).first();
        Assert.assertFalse("Editor should be closed.", grid.isElementPresent(By.vaadin("#editor")));
        editRow(grid, 1);
        Assert.assertEquals("Editor should open after tab switch", "1", grid.getEditor().getField(1).getAttribute("value"));
        // Close the current editor and reopen on a different row
        grid.sendKeys(ESCAPE);
        editRow(grid, 0);
        Assert.assertEquals("Editor should move", "0", grid.getEditor().getField(1).getAttribute("value"));
        assertNoErrorNotifications();
    }
}

