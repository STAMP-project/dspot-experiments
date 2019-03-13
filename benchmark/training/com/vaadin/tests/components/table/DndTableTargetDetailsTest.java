package com.vaadin.tests.components.table;


import com.vaadin.testbench.By;
import com.vaadin.tests.tb3.DndActionsTest;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.WebElement;


/**
 * Test for mouse details in AbstractSelectTargetDetails class when DnD target
 * is a table.
 *
 * @author Vaadin Ltd
 */
public class DndTableTargetDetailsTest extends DndActionsTest {
    @Test
    public void testMouseDetails() throws IOException, InterruptedException {
        openTestURL();
        WebElement row = findElement(By.className("v-table-cell-wrapper"));
        dragAndDrop(row, getTarget());
        WebElement label = findElement(By.className("dnd-button-name"));
        Assert.assertEquals("Button name=left", label.getText());
    }
}

