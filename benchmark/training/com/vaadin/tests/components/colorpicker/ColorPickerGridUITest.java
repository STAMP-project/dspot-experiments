package com.vaadin.tests.components.colorpicker;


import com.vaadin.tests.tb3.SingleBrowserTest;
import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;


public class ColorPickerGridUITest extends SingleBrowserTest {
    @Test
    public void testNoError() throws Exception {
        openTestURL();
        // find the color picker grid and click on the second color
        WebElement grid = getDriver().findElement(By.className("v-colorpicker-grid"));
        // click on the second color
        grid.findElements(By.tagName("td")).get(1).click();
        // check that the color picker does not have component error set
        if (hasCssClass(grid, "v-colorpicker-grid-error")) {
            Assert.fail("ColorPickerGrid should not have an active component error");
        }
    }
}

