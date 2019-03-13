package com.vaadin.v7.tests.components.grid.basicfeatures.server;


import com.vaadin.testbench.elements.GridElement.GridCellElement;
import com.vaadin.testbench.parallel.TestCategory;
import com.vaadin.tests.tb3.AbstractTB3Test;
import com.vaadin.v7.tests.components.grid.basicfeatures.GridBasicFeatures;
import com.vaadin.v7.tests.components.grid.basicfeatures.GridBasicFeaturesTest;
import org.junit.Assert;
import org.junit.Test;


@TestCategory("grid")
public class GridColumnResizeTest extends GridBasicFeaturesTest {
    @Test
    public void testResizeHandlesPresentInDefaultHeader() {
        for (int i = 0; i < (GridBasicFeatures.COLUMNS); ++i) {
            assertResizable(i, true);
        }
    }

    @Test
    public void testResizeHandlesNotInNonDefaultHeader() {
        selectMenuPath("Component", "Header", "Prepend row");
        for (int i = 0; i < (GridBasicFeatures.COLUMNS); ++i) {
            assertResizable(getGridElement().getHeaderCell(0, i), false);
            assertResizable(getGridElement().getHeaderCell(1, i), true);
        }
    }

    @Test
    public void testResizeHandlesNotInFooter() {
        selectMenuPath("Component", "Footer", "Visible");
        for (int i = 0; i < (GridBasicFeatures.COLUMNS); ++i) {
            assertResizable(getGridElement().getFooterCell(0, i), false);
        }
    }

    @Test
    public void testToggleSetResizable() {
        selectMenuPath("Component", "Columns", "Column 1", "Resizable");
        for (int i = 0; i < (GridBasicFeatures.COLUMNS); ++i) {
            assertResizable(i, (i != 1));
        }
        selectMenuPath("Component", "Columns", "Column 1", "Resizable");
        for (int i = 0; i < (GridBasicFeatures.COLUMNS); ++i) {
            assertResizable(i, true);
        }
    }

    @Test
    public void testResizeFirstColumn() {
        dragResizeColumn(0, (-1), (-10));
        Assert.assertTrue("Log should contain a resize event", logContainsText("ColumnResizeEvent: isUserOriginated? true"));
    }

    @Test
    public void testDragHandleStraddlesColumns() {
        dragResizeColumn(0, 4, (-10));
        Assert.assertTrue("Log should contain a resize event", logContainsText("ColumnResizeEvent: isUserOriginated? true"));
    }

    @Test
    public void testColumnPixelSizesSetOnResize() {
        selectMenuPath("Component", "Columns", "All columns auto width");
        dragResizeColumn(0, (-1), (-10));
        for (String msg : getLogs()) {
            Assert.assertTrue("Log should contain a resize event", msg.contains("ColumnResizeEvent: isUserOriginated? true"));
        }
    }

    @Test
    public void testResizeWithWidgetHeader() {
        selectMenuPath("Component", "Columns", "Column 0", "Column 0 Width", "250px");
        selectMenuPath("Component", "Columns", "Column 0", "Header Type", "Widget Header");
        // IE9 and IE10 sometimes have a 1px gap between resize handle parts, so
        // using posX 1px
        dragResizeColumn(0, 1, 10);
        Assert.assertTrue("Log should contain a resize event", logContainsText("ColumnResizeEvent: isUserOriginated? true"));
    }

    @Test
    public void testShrinkColumnToZero() {
        openTestURL();
        GridCellElement cell = getGridElement().getCell(0, 1);
        dragResizeColumn(1, 0, cell.getSize().getWidth());
        AbstractTB3Test.assertGreaterOrEqual("Cell got too small.", cell.getSize().getWidth(), 10);
    }

    @Test
    public void testShrinkColumnToZeroWithHiddenColumn() {
        openTestURL();
        selectMenuPath("Component", "Columns", "Toggle all column hidden state");
        // Hides although already hidden
        toggleColumnHidden(0);
        // Shows
        toggleColumnHidden(0);
        // Hides although already hidden
        toggleColumnHidden(2);
        // Shows
        toggleColumnHidden(2);
        GridCellElement cell = getGridElement().getCell(0, 1);
        dragResizeColumn(1, 0, (-(cell.getSize().getWidth())));
        AbstractTB3Test.assertGreaterOrEqual("Cell got too small.", cell.getSize().getWidth(), 10);
        Assert.assertEquals(getGridElement().getCell(0, 0).getLocation().getY(), getGridElement().getCell(0, 1).getLocation().getY());
    }
}

