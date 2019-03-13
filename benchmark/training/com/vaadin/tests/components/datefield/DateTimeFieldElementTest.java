package com.vaadin.tests.components.datefield;


import DateTimeFieldElementUI.ANOTHER_TEST_DATE_TIME;
import DateTimeFieldElementUI.TEST_DATE_TIME;
import com.vaadin.testbench.By;
import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.DateTimeFieldElement;
import com.vaadin.testbench.elements.InlineDateTimeFieldElement;
import com.vaadin.tests.tb3.SingleBrowserTest;
import java.time.LocalDateTime;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.WebElement;


public class DateTimeFieldElementTest extends SingleBrowserTest {
    @Test
    public void DateTimeFieldElementIsLocated() {
        openTestURL();
        MatcherAssert.assertThat($(DateTimeFieldElement.class).all().size(), Is.is(3));
        MatcherAssert.assertThat($(InlineDateTimeFieldElement.class).all().size(), Is.is(1));
    }

    @Test
    public void setGetValue() {
        openTestURL();
        // No date set
        DateTimeFieldElement defaultInitiallyEmpty = $(DateTimeFieldElement.class).first();
        Assert.assertNull(defaultInitiallyEmpty.getDateTime());
        defaultInitiallyEmpty.setDateTime(TEST_DATE_TIME);
        Assert.assertEquals(TEST_DATE_TIME, defaultInitiallyEmpty.getDateTime());
        assertServerValue("Default date field", TEST_DATE_TIME);
        DateTimeFieldElement fi = $(DateTimeFieldElement.class).id("fi");
        Assert.assertEquals(TEST_DATE_TIME, fi.getDateTime());
        fi.setDateTime(ANOTHER_TEST_DATE_TIME);
        Assert.assertEquals(ANOTHER_TEST_DATE_TIME, fi.getDateTime());
        assertServerValue("Finnish date field", ANOTHER_TEST_DATE_TIME);
        DateTimeFieldElement us = $(DateTimeFieldElement.class).id("us");
        Assert.assertEquals(TEST_DATE_TIME, us.getDateTime());
        us.setDateTime(ANOTHER_TEST_DATE_TIME);
        Assert.assertEquals(ANOTHER_TEST_DATE_TIME, us.getDateTime());
        assertServerValue("US date field", ANOTHER_TEST_DATE_TIME);
    }

    @Test
    public void testDateStyles() {
        openTestURL();
        Assert.assertTrue(findElements(By.className("teststyle")).isEmpty());
        // add styles
        $(ButtonElement.class).first().click();
        WebElement styledDateCell = $(InlineDateTimeFieldElement.class).first().findElement(By.className("teststyle"));
        Assert.assertEquals(String.valueOf(LocalDateTime.now().getDayOfMonth()), styledDateCell.getText());
        DateTimeFieldElement fi = $(DateTimeFieldElement.class).id("fi");
        fi.openPopup();
        waitForElementPresent(By.className("v-datefield-popup"));
        WebElement popup = findElement(By.className("v-datefield-popup"));
        styledDateCell = popup.findElement(By.className("teststyle"));
        Assert.assertEquals("1", styledDateCell.getText());
        styledDateCell.click();// close popup

        waitForElementNotPresent(By.className("v-datefield-popup"));
        DateTimeFieldElement us = $(DateTimeFieldElement.class).id("us");
        us.openPopup();
        waitForElementPresent(By.className("v-datefield-popup"));
        popup = findElement(By.className("v-datefield-popup"));
        styledDateCell = popup.findElement(By.className("teststyle"));
        Assert.assertEquals("1", styledDateCell.getText());
    }
}

