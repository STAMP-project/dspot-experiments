package com.vaadin.tests.components.table;


import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;


public class ColumnWidthsAfterChangeTableColumnsCountOrOrderTest extends MultiBrowserTest {
    @Test
    public void testColumnWidthAfterChangeTableColumnsOrder() {
        openTestURL();
        getButtonChangeOrderAndWidth().click();
        waitForElementPresent(By.className("v-table"));
        Assert.assertEquals(("The width of descr column should be " + (ColumnWidthsAfterChangeTableColumnsCountOrOrder.NEW_COLUMN_WIDTH)), ColumnWidthsAfterChangeTableColumnsCountOrOrder.NEW_COLUMN_WIDTH, getDescriptionColumnWidth());
    }

    @Test
    public void testColumnWidthAfterChangeTableColumnsCount() {
        openTestURL();
        getButtonChangeColumnCountAndWidth().click();
        waitForElementPresent(By.className("v-table"));
        Assert.assertEquals(("The width of descr column should be " + (ColumnWidthsAfterChangeTableColumnsCountOrOrder.NEW_COLUMN_WIDTH)), ColumnWidthsAfterChangeTableColumnsCountOrOrder.NEW_COLUMN_WIDTH, getDescriptionColumnWidth());
    }
}

