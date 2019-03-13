package com.vaadin.tests.components.datefield;


import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;


public class CustomDateFormatEEETest extends MultiBrowserTest {
    @Test
    public void verifyDatePattern() {
        openTestURL();
        String dateValue = driver.findElement(By.className("v-datefield-textfield")).getAttribute("value");
        Assert.assertEquals("14/03/2014 Fri", dateValue);
    }
}

