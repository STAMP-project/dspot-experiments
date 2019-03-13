package com.vaadin.tests.components.datefield;


import Keys.ENTER;
import com.vaadin.testbench.By;
import com.vaadin.testbench.elements.AbstractDateFieldElement;
import com.vaadin.tests.tb3.MultiBrowserTest;
import java.time.LocalDate;
import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.WebElement;


public class DateFieldUnparsableDateStringTest extends MultiBrowserTest {
    @Test
    public void testInvalidText() throws InterruptedException {
        openTestURL();
        waitForElementVisible(By.className("v-datefield"));
        WebElement dateTextbox = $(AbstractDateFieldElement.class).first().findElement(By.className("v-textfield"));
        dateTextbox.sendKeys("0304", ENTER);
        findElement(By.tagName("body")).click();
        Assert.assertEquals(("03.04." + (LocalDate.now().getYear())), dateTextbox.getAttribute("value"));
        dateTextbox.clear();
        dateTextbox.sendKeys("0304", ENTER);
        findElement(By.tagName("body")).click();
        Assert.assertEquals(("03.04." + (LocalDate.now().getYear())), dateTextbox.getAttribute("value"));
    }
}

