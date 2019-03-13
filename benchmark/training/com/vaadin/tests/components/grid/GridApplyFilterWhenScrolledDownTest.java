package com.vaadin.tests.components.grid;


import com.vaadin.testbench.By;
import com.vaadin.testbench.TestBenchElement;
import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.GridElement;
import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.WebElement;


public class GridApplyFilterWhenScrolledDownTest extends MultiBrowserTest {
    @Test
    public void scrolledCorrectly() throws InterruptedException {
        openTestURL();
        final GridElement grid = $(GridElement.class).first();
        grid.scrollToRow(50);
        $(ButtonElement.class).first().click();
        final TestBenchElement gridBody = grid.getBody();
        // Can't use element API because it scrolls
        waitUntil(( input) -> (gridBody.findElements(By.className("v-grid-row")).size()) == 1);
        WebElement cell = gridBody.findElements(By.className("v-grid-cell")).get(0);
        Assert.assertEquals("Test", cell.getText());
        int gridHeight = grid.getSize().getHeight();
        int scrollerHeight = grid.getVerticalScroller().getSize().getHeight();
        Assert.assertTrue(((("Scroller height is " + scrollerHeight) + ", should be smaller than grid height: ") + gridHeight), (scrollerHeight < gridHeight));
    }
}

