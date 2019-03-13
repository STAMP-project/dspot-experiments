package com.vaadin.tests.components.table;


import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.tests.tb3.MultiBrowserTest;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;


/**
 * Tests that components within table cells get resized when their column gets
 * resized.
 *
 * @author Vaadin Ltd
 */
public class TableColumnResizeContentsWidthTest extends MultiBrowserTest {
    @Test
    public void testResizing() throws InterruptedException {
        openTestURL();
        List<ButtonElement> buttons = $(ButtonElement.class).all();
        WebElement resizer = getTable().findElement(By.className("v-table-resizer"));
        Assert.assertEquals(100, getTextFieldWidth());
        moveResizer(resizer, (-20));
        Assert.assertEquals(80, getTextFieldWidth());
        moveResizer(resizer, 40);
        Assert.assertEquals(120, getTextFieldWidth());
        // click the button for decreasing size
        buttons.get(1).click();
        waitUntilTextFieldWidthIs(80);
        // click the button for increasing size
        buttons.get(0).click();
        waitUntilTextFieldWidthIs(100);
    }
}

