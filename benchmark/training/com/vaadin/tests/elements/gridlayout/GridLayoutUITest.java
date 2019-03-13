package com.vaadin.tests.elements.gridlayout;


import GridLayoutUI.ONE_ROW_ONE_COL;
import GridLayoutUI.TEN_ROWS_TEN_COLS;
import com.vaadin.testbench.elements.GridLayoutElement;
import com.vaadin.tests.tb3.SingleBrowserTest;
import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;


public class GridLayoutUITest extends SingleBrowserTest {
    @Test
    public void getRows() {
        openTestURL();
        Assert.assertEquals(1, $(GridLayoutElement.class).id(ONE_ROW_ONE_COL).getRowCount());
        Assert.assertEquals(10, $(GridLayoutElement.class).id(TEN_ROWS_TEN_COLS).getRowCount());
    }

    @Test
    public void getColumns() {
        openTestURL();
        Assert.assertEquals(1, $(GridLayoutElement.class).id(ONE_ROW_ONE_COL).getColumnCount());
        Assert.assertEquals(10, $(GridLayoutElement.class).id(TEN_ROWS_TEN_COLS).getColumnCount());
    }

    @Test
    public void getCell() {
        openTestURL();
        GridLayoutElement grid = $(GridLayoutElement.class).id(TEN_ROWS_TEN_COLS);
        WebElement cell55 = grid.getCell(5, 5);
        Assert.assertEquals("v-gridlayout-slot", cell55.getAttribute("class"));
        Assert.assertEquals("5-5", cell55.getText());
        try {
            grid.getCell(4, 4);
            Assert.fail("Should throw for empty cell");
        } catch (NoSuchElementException e) {
        }
        WebElement cell77 = grid.getCell(7, 7);
        Assert.assertEquals("v-gridlayout-slot", cell77.getAttribute("class"));
        Assert.assertEquals("7-7 8-8", cell77.getText());
        try {
            grid.getCell(7, 8);
            Assert.fail("Should throw for merged cell");
        } catch (NoSuchElementException e) {
        }
    }
}

