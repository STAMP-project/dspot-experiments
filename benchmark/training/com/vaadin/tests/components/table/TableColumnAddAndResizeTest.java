package com.vaadin.tests.components.table;


import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.NotificationElement;
import com.vaadin.testbench.elements.TableElement;
import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Assert;
import org.junit.Test;


public class TableColumnAddAndResizeTest extends MultiBrowserTest {
    @Test
    public void testAddAndResizeColumn() {
        setDebug(true);
        openTestURL();
        $(ButtonElement.class).caption("Add and Resize").first().click();
        Assert.assertFalse("Error notification present.", $(NotificationElement.class).exists());
        Assert.assertEquals("Unexpected column width. ", 200, $(TableElement.class).first().getCell(0, 1).getSize().getWidth());
    }
}

