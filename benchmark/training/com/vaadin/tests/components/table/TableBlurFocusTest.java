package com.vaadin.tests.components.table;


import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.TableElement;
import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;


public class TableBlurFocusTest extends MultiBrowserTest {
    @Test
    public void testBlurAndFocus() throws InterruptedException {
        openTestURL();
        waitForElementPresent(By.className("v-button"));
        assertAnyLogText("1. variable change");
        Assert.assertEquals("Unexpected column header,", "COLUMN2", $(TableElement.class).first().getHeaderCell(1).getCaption());
        Assert.assertEquals("Unexpected button caption,", "click to focus", $(ButtonElement.class).first().getCaption());
        $(ButtonElement.class).first().click();
        assertAnyLogText("2. focus", "3. focus");
        $(TableElement.class).first().getHeaderCell(1).click();
        assertAnyLogText("3. blur", "4. blur");
    }
}

