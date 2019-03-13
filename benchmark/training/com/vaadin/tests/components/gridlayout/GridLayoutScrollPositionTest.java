package com.vaadin.tests.components.gridlayout;


import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;


public class GridLayoutScrollPositionTest extends MultiBrowserTest {
    @Test
    public void testToggleChildComponents() throws Exception {
        final int SCROLLTOP = 100;
        openTestURL();
        WebDriver driver = getDriver();
        WebElement ui = driver.findElement(By.className("v-ui"));
        testBenchElement(ui).scroll(SCROLLTOP);
        driver.findElement(By.id("visibility-toggle")).findElement(By.tagName("input")).click();
        Assert.assertEquals("UI scroll position", String.valueOf(SCROLLTOP), ui.getAttribute("scrollTop"));
    }
}

