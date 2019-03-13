package com.vaadin.tests.components.nativeselect;


import com.vaadin.testbench.By;
import com.vaadin.testbench.elements.NativeSelectElement;
import com.vaadin.tests.tb3.SingleBrowserTest;
import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.WebElement;


public class NativeSelectVisibleItemCountTest extends SingleBrowserTest {
    @Test
    public void changeItemCount() {
        openTestURL();
        WebElement select = $(NativeSelectElement.class).first().findElement(By.xpath("select"));
        Assert.assertEquals("1", select.getAttribute("size"));
        selectMenuPath("Component", "Size", "Visible item count", "5");
        Assert.assertEquals("5", select.getAttribute("size"));
    }
}

