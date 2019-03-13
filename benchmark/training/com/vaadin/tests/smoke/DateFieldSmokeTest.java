package com.vaadin.tests.smoke;


import com.vaadin.testbench.elements.InlineDateFieldElement;
import com.vaadin.testbench.elements.PopupDateFieldElement;
import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;


/**
 *
 *
 * @author Vaadin Ltd
 */
public class DateFieldSmokeTest extends MultiBrowserTest {
    @Test
    public void dateFieldsSmokeTest() {
        openTestURL();
        PopupDateFieldElement popup = $(PopupDateFieldElement.class).first();
        Assert.assertEquals("12/28/16", popup.getValue());
        InlineDateFieldElement inline = $(InlineDateFieldElement.class).first();
        Assert.assertEquals(String.valueOf(29), inline.findElement(By.className("v-inline-datefield-calendarpanel-day-selected")).getText());
        popup.findElement(By.tagName("button")).click();
        waitUntil(ExpectedConditions.visibilityOfElementLocated(By.className("v-datefield-popup")));
        selectDay(findElement(By.className("v-datefield-popup")), 14, "v-");
        waitUntil(( driver) -> "1. Popup value is : 2016.12.14".equals(getLogRow(0)));
        selectDay(inline, 13, "v-inline-");
        waitUntil(( driver) -> "2. Inline value is : 2016.12.13".equals(getLogRow(0)));
        inline.findElement(By.className("v-button-prevmonth")).click();
        WebElement monthTitle = inline.findElement(By.className("v-inline-datefield-calendarpanel-month"));
        Assert.assertEquals("November 2016", monthTitle.getText());
        inline.findElement(By.className("v-button-nextyear")).click();
        monthTitle = inline.findElement(By.className("v-inline-datefield-calendarpanel-month"));
        Assert.assertEquals("November 2017", monthTitle.getText());
    }
}

