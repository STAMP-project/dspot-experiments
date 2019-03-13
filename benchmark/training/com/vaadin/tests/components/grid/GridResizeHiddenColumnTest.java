package com.vaadin.tests.components.grid;


import com.vaadin.testbench.elements.GridElement;
import com.vaadin.testbench.elements.GridElement.GridCellElement;
import com.vaadin.testbench.parallel.BrowserUtil;
import com.vaadin.tests.tb3.MultiBrowserTest;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;


public class GridResizeHiddenColumnTest extends MultiBrowserTest {
    @Test
    public void testDragResizeHiddenColumnSize() {
        GridElement grid = $(GridElement.class).first();
        Actions action = new Actions(getDriver());
        // Check if column 'Last Name' hidden
        List<GridCellElement> headerCells = grid.getHeaderCells(0);
        Assert.assertEquals("There should be two visible columns", 2, headerCells.size());
        Assert.assertFalse("'Last Name' column should be hidden", containsText("Last Name", headerCells));
        // Resize first column
        int dragOffset = -100;
        int headerCellWidth = headerCells.get(0).getSize().getWidth();
        dragResizeColumn(headerCells.get(0), 1, dragOffset);
        // When dragging the resizer on IE8, the final offset will be smaller
        // (might be an issue with the feature that doesn't start resizing until
        // the cursor moved a few pixels)
        double delta = (BrowserUtil.isIE8(getDesiredCapabilities())) ? 5.0 : 0;
        Assert.assertEquals((("Column width should've changed by " + dragOffset) + "px"), (headerCellWidth + dragOffset), headerCells.get(0).getSize().getWidth(), delta);
        // Make column 'Last Name' visible
        WebElement menuButton = grid.findElement(By.className("v-contextmenu")).findElement(By.tagName("button"));
        action.click(menuButton).perform();// Click on menu button

        WebElement sidebarPopup = findElement(By.className("v-grid-sidebar-popup"));
        WebElement visibilityToggle = findElementByText("Last Name", sidebarPopup.findElements(By.className("gwt-MenuItem")));
        // Click on "Last Name" menu item
        action.click(visibilityToggle).perform();
        // Check if column "Last Name" is visible
        headerCells = grid.getHeaderCells(0);
        Assert.assertEquals("There should be three visible columns", 3, headerCells.size());
        Assert.assertTrue("'Last Name' column should be visible", containsText("Last Name", headerCells));
        // Check if column "Last Name" has expanded width
        int widthSum = 0;
        for (GridCellElement e : headerCells) {
            widthSum += e.getSize().getWidth();
        }
        Assert.assertEquals("'Last Name' column should take up the remaining space", grid.getHeader().getSize().getWidth(), widthSum, 1.0);
    }
}

