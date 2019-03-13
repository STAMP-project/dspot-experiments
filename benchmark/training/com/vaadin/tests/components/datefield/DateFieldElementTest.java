package com.vaadin.tests.components.datefield;


import DateFieldElementUI.ANOTHER_TEST_DATE_TIME;
import DateFieldElementUI.TEST_DATE_TIME;
import com.vaadin.testbench.By;
import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.DateFieldElement;
import com.vaadin.testbench.elements.InlineDateFieldElement;
import com.vaadin.tests.tb3.SingleBrowserTest;
import java.time.LocalDate;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.WebElement;


public class DateFieldElementTest extends SingleBrowserTest {
    @Test
    public void dateFieldElementIsLocated() {
        openTestURL();
        MatcherAssert.assertThat($(DateFieldElement.class).all().size(), Is.is(3));
        MatcherAssert.assertThat($(InlineDateFieldElement.class).all().size(), Is.is(1));
    }

    @Test
    public void setGetValue() {
        openTestURL();
        // No date set
        DateFieldElement defaultInitiallyEmpty = $(DateFieldElement.class).first();
        Assert.assertNull(defaultInitiallyEmpty.getDate());
        defaultInitiallyEmpty.setDate(TEST_DATE_TIME);
        Assert.assertEquals(TEST_DATE_TIME, defaultInitiallyEmpty.getDate());
        assertServerValue("Default date field", TEST_DATE_TIME);
        DateFieldElement fi = $(DateFieldElement.class).id("fi");
        Assert.assertEquals(TEST_DATE_TIME, fi.getDate());
        fi.setDate(ANOTHER_TEST_DATE_TIME);
        Assert.assertEquals(ANOTHER_TEST_DATE_TIME, fi.getDate());
        assertServerValue("Finnish date field", ANOTHER_TEST_DATE_TIME);
        DateFieldElement us = $(DateFieldElement.class).id("us");
        Assert.assertEquals(TEST_DATE_TIME, us.getDate());
        us.setDate(ANOTHER_TEST_DATE_TIME);
        Assert.assertEquals(ANOTHER_TEST_DATE_TIME, us.getDate());
        assertServerValue("US date field", ANOTHER_TEST_DATE_TIME);
    }

    @Test
    public void testDateStyles() {
        openTestURL();
        Assert.assertTrue(findElements(By.className("teststyle")).isEmpty());
        $(ButtonElement.class).first().click();
        WebElement styledDateCell = $(InlineDateFieldElement.class).first().findElement(By.className("teststyle"));
        Assert.assertEquals(String.valueOf(LocalDate.now().getDayOfMonth()), styledDateCell.getText());
        DateFieldElement fi = $(DateFieldElement.class).id("fi");
        fi.openPopup();
        waitForElementPresent(By.className("v-datefield-popup"));
        WebElement popup = findElement(By.className("v-datefield-popup"));
        styledDateCell = popup.findElement(By.className("teststyle"));
        Assert.assertEquals("1", styledDateCell.getText());
        styledDateCell.click();// close popup

        waitForElementNotPresent(By.className("v-datefield-popup"));
        DateFieldElement us = $(DateFieldElement.class).id("us");
        us.openPopup();
        waitForElementPresent(By.className("v-datefield-popup"));
        popup = findElement(By.className("v-datefield-popup"));
        styledDateCell = popup.findElement(By.className("teststyle"));
        Assert.assertEquals("1", styledDateCell.getText());
    }
}

