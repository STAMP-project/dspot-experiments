package com.vaadin.tests.components.table;


import com.vaadin.testbench.By;
import com.vaadin.testbench.elements.TableElement;
import com.vaadin.tests.tb3.MultiBrowserTest;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.WebElement;


/**
 * Test for a Table with a customised BeanItemContainer.
 *
 * @author Vaadin Ltd
 */
public class TableWithContainerRequiringEqualsForItemIdTest extends MultiBrowserTest {
    @Test
    public void testSorting() {
        openTestURL();
        TableElement table = $(TableElement.class).first();
        List<WebElement> rows = table.findElement(By.className("v-table-body")).findElements(By.tagName("tr"));
        Assert.assertEquals("unexpect amount of rows", 46, rows.size());
        // click the button on the first visible row
        clickButton(table, 0, 3, "1. Button Button999 clicked");
        // click the button on the last visible row
        clickButton(table, 14, 3, "2. Button Button985 clicked");
        clickTableHeaderToSort(table);
        // check the first cell of the new first visible row
        checkFirstCell(table, "0");
        // click the button on the first visible row
        clickButton(table, 0, 3, "3. Button Button0 clicked");
        // sort by the first column (descending)
        clickTableHeaderToSort(table);
        // check the first cell of the new first visible row
        checkFirstCell(table, "999");
        // click the button on the first visible row
        clickButton(table, 0, 3, "4. Button Button999 clicked");
    }
}

