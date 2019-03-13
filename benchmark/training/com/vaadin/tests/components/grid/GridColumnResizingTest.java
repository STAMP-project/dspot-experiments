package com.vaadin.tests.components.grid;


import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.GridElement.GridCellElement;
import com.vaadin.testbench.elements.LabelElement;
import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;


public class GridColumnResizingTest extends MultiBrowserTest {
    @Test
    public void serverSetWidth() {
        openTestURL();
        serverSideSetWidth(50);
        assertColumnWidth(50, 0);
        serverSideSetWidth(500);
        assertColumnWidth(500, 0);
    }

    @Test
    public void setResizable() {
        openTestURL();
        ButtonElement toggleResizableButton = $(ButtonElement.class).get(4);
        GridCellElement cell = getGrid().getHeaderCell(0, 0);
        Assert.assertEquals(true, cell.isElementPresent(By.cssSelector("div.v-grid-column-resize-handle")));
        toggleResizableButton.click();
        Assert.assertEquals(false, cell.isElementPresent(By.cssSelector("div.v-grid-column-resize-handle")));
    }

    @Test
    public void setExpandRatio() {
        openTestURL();
        ButtonElement setExpandRatioButton = $(ButtonElement.class).get(1);
        setExpandRatioButton.click();
        assertColumnWidthWithThreshold(375, 0, 2);
        assertColumnWidthWithThreshold(125, 1, 2);
    }

    @Test
    public void setMinimumWidth() {
        openTestURL();
        setMinWidth(100);
        serverSideSetWidth(50);
        assertColumnWidth(100, 0);
        serverSideSetWidth(150);
        dragResizeColumn(0, 0, (-100));
        assertColumnWidth(100, 0);
    }

    @Test
    public void setMaximumWidth() {
        openTestURL();
        serverSideSetWidth(50);
        setMaxWidth(100);
        serverSideSetWidth(150);
        assertColumnWidth(100, 0);
        // TODO add the following when grid column width recalculation has been
        // fixed in the case where the sum of column widths exceeds the visible
        // area
        // serverSideSetWidth(50);
        // dragResizeColumn(0, 0, 200);
        // assertColumnWidth(100, 0);
    }

    @Test
    public void resizeEventListener() {
        openTestURL();
        Assert.assertEquals("not resized", $(LabelElement.class).get(1).getText());
        serverSideSetWidth(150);
        Assert.assertEquals("server resized", $(LabelElement.class).get(1).getText());
        dragResizeColumn(0, 0, 100);
        Assert.assertEquals("client resized", $(LabelElement.class).get(1).getText());
    }
}

