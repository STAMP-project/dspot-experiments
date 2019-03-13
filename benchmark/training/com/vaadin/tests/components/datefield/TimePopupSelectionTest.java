package com.vaadin.tests.components.datefield;


import com.vaadin.testbench.By;
import com.vaadin.testbench.elements.DateTimeFieldElement;
import com.vaadin.tests.tb3.MultiBrowserTest;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.WebElement;


/**
 *
 *
 * @author Vaadin Ltd
 */
public class TimePopupSelectionTest extends MultiBrowserTest {
    @Test
    public void selectDateAndTimeFromPopup() {
        openTestURL();
        DateTimeFieldElement field = $(DateTimeFieldElement.class).first();
        Assert.assertEquals("1/13/17 01:00:00 AM", field.getValue());
        field.openPopup();
        List<WebElement> timeSelects = findElement(By.className("v-datefield-calendarpanel-time")).findElements(By.tagName("select"));
        selectByValue("09");
        Assert.assertEquals("1/13/17 09:00:00 AM", field.getValue());
        selectByValue("35");
        Assert.assertEquals("1/13/17 09:35:00 AM", field.getValue());
        selectByValue("41");
        Assert.assertEquals("1/13/17 09:35:41 AM", field.getValue());
        closePopup();
        waitUntil(( driver) -> getLogRow(0).equals("1. 13/01/2017 09:35:41"));
    }
}

