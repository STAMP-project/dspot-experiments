package com.vaadin.tests.components.table;


import com.vaadin.testbench.elements.TableElement;
import com.vaadin.testbench.elements.TableHeaderElement;
import com.vaadin.tests.tb3.MultiBrowserTest;
import java.io.IOException;
import java.util.Locale;
import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;


public class ColumnReorderingWithManyColumnsTest extends MultiBrowserTest {
    @Test
    public void testReordering() throws IOException {
        openTestURL();
        TableElement table = $(TableElement.class).first();
        TableHeaderElement sourceCell = table.getHeaderCell(0);
        TableHeaderElement targetCell = table.getHeaderCell(10);
        drag(sourceCell, targetCell);
        WebElement markedElement = table.findElement(By.className("v-table-focus-slot-right"));
        String markedColumnName = markedElement.findElement(By.xpath("..")).getText();
        Assert.assertEquals("col-9", markedColumnName.toLowerCase(Locale.ROOT));
    }
}

