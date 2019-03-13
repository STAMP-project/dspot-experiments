package com.vaadin.tests.components.datefield;


import Keys.ARROW_DOWN;
import com.vaadin.tests.tb3.MultiBrowserTest;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;


public class DisabledDateFieldPopupTest extends MultiBrowserTest {
    @Test
    public void testPopup() throws IOException {
        openTestURL();
        WebElement button = driver.findElement(By.className("v-datefield-button"));
        new org.openqa.selenium.interactions.Actions(driver).moveToElement(button).click().sendKeys(ARROW_DOWN).perform();
        Assert.assertFalse("Calendar popup should not be opened for disabled date field", isElementPresent(By.className("v-datefield-popup")));
    }
}

