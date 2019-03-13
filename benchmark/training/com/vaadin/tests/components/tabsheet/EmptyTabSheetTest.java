package com.vaadin.tests.components.tabsheet;


import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;


public class EmptyTabSheetTest extends MultiBrowserTest {
    @Test
    public void emptyTabSheet() throws Exception {
        openTestURL();
        compareScreen("empty");
    }

    @Test
    public void emptyTabSheetValo() {
        openTestURL("theme=valo");
        WebElement deco = getDriver().findElement(By.className("v-tabsheet-deco"));
        Assert.assertEquals("none", deco.getCssValue("display"));
    }
}

