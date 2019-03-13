package com.vaadin.tests.components.table;


import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test to see if all items of the table can be selected by selecting first row,
 * press shift then select last (#13008)
 *
 * @author Vaadin Ltd
 */
public class SelectAllRowsTest extends MultiBrowserTest {
    @Test
    public void testAllRowsAreSelected() {
        openTestURL();
        clickFirstRow();
        scrollTableToBottom();
        clickLastRow();
        Assert.assertEquals(SelectAllRows.TOTAL_NUMBER_OF_ROWS, countSelectedItems());
    }
}

