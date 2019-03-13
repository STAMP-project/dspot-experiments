package com.vaadin.tests.themes.base;


import com.vaadin.testbench.By;
import com.vaadin.tests.tb3.MultiBrowserTest;
import java.io.IOException;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.WebElement;


public class DisabledMenuBarItemTest extends MultiBrowserTest {
    @Test
    public void disabledMenuItemShouldHaveOpacity() throws IOException {
        openTestURL();
        WebElement element = driver.findElement(By.className("v-menubar-menuitem-disabled"));
        Assert.assertThat(element.getCssValue("opacity"), CoreMatchers.is("0.5"));
        compareScreen("transparent");
    }
}

