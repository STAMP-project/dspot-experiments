package com.vaadin.tests.components.table;


import com.vaadin.testbench.elements.TableElement;
import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Assert;
import org.junit.Test;


public class SelectableEditableTest extends MultiBrowserTest {
    @Test
    public void testSelectFromCellWith() throws Exception {
        openTestURL();
        TableElement table = $(TableElement.class).first();
        table.getCell(0, 1).click(70, 12);
        Assert.assertTrue("Element does not have the 'v-selected' css class", hasCssClass(table.getRow(0), "v-selected"));
    }
}

