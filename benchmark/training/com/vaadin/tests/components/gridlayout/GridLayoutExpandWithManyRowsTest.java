package com.vaadin.tests.components.gridlayout;


import GridLayoutExpandWithManyRows.POPULATED_ROWS;
import com.vaadin.testbench.elements.GridLayoutElement;
import com.vaadin.tests.tb3.SingleBrowserTest;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import static GridLayoutExpandWithManyRows.POPULATED_ROWS;


public class GridLayoutExpandWithManyRowsTest extends SingleBrowserTest {
    @Test
    public void equalRowHeights() {
        openTestURL();
        GridLayoutElement gridlayout = $(GridLayoutElement.class).first();
        // Rows are expanded using integer pixels and leftover pixels are added
        // to the first N rows.
        // The tests uses rowspan=2 so one row in the DOM should be max 2 pixels
        // lower than the first row
        List<WebElement> slots = gridlayout.findElements(By.className("v-gridlayout-slot"));
        Assert.assertEquals(POPULATED_ROWS, slots.size());
        int firstRowHeight = slots.get(0).getSize().height;
        int lastRowHeight = firstRowHeight;
        for (int i = 1; i < (POPULATED_ROWS); i++) {
            int rowHeight = slots.get(i).getSize().height;
            Assert.assertTrue((rowHeight <= firstRowHeight));
            Assert.assertTrue((rowHeight >= (firstRowHeight - 2)));
            Assert.assertTrue((rowHeight <= lastRowHeight));
            lastRowHeight = rowHeight;
        }
    }
}

