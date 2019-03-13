package com.vaadin.tests.components;


import com.vaadin.tests.tb3.MultiBrowserTest;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;


public class SaneErrorsTest extends MultiBrowserTest {
    @Test
    public void test() {
        openTestURL();
        List<WebElement> elements = getDriver().findElements(By.xpath("//*[text() = 'Show me my NPE!']"));
        for (WebElement webElement : elements) {
            webElement.click();
        }
        getDriver().findElement(By.xpath("//*[text() = 'Collect exceptions']")).click();
        List<WebElement> errorMessages = getDriver().findElements(By.className("v-label"));
        for (WebElement webElement : errorMessages) {
            String text = webElement.getText();
            Assert.assertEquals("java.lang.NullPointerException", text);
        }
    }
}

