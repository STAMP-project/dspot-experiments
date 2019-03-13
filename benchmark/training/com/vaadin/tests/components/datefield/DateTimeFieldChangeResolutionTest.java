package com.vaadin.tests.components.datefield;


import DateTimeResolution.DAY;
import DateTimeResolution.HOUR;
import DateTimeResolution.MINUTE;
import DateTimeResolution.MONTH;
import DateTimeResolution.SECOND;
import DateTimeResolution.YEAR;
import com.vaadin.testbench.By;
import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.WebElement;


public class DateTimeFieldChangeResolutionTest extends MultiBrowserTest {
    private WebElement dateFieldButton;

    private WebElement textField;

    private WebElement resolutionSecond;

    private WebElement resolutionMinute;

    private WebElement resolutionHour;

    private WebElement resolutionDay;

    private WebElement resolutionMonth;

    private WebElement resolutionYear;

    @Test
    public void changeResolutionBetweenYearAndMonth() throws Exception {
        initialize();
        click(resolutionMonth);
        checkHeaderAndBody(MONTH, true);
        click(resolutionYear);
        checkHeaderAndBody(YEAR, true);
    }

    @Test
    public void changeResolutionBetweenYearAndSecond() throws Exception {
        initialize();
        click(resolutionSecond);
        checkHeaderAndBody(SECOND, true);
        click(resolutionYear);
        checkHeaderAndBody(YEAR, true);
    }

    @Test
    public void changeResolutionToDayThenMonth() throws Exception {
        initialize();
        // check the initial state
        checkHeaderAndBody(YEAR, true);
        click(resolutionDay);
        checkHeaderAndBody(DAY, true);
        click(resolutionMonth);
        checkHeaderAndBody(MONTH, true);
    }

    @Test
    public void setDateAndChangeResolution() throws Exception {
        initialize();
        // Set the date to previous month.
        click(resolutionMonth);
        openPopupDateField();
        click(driver.findElement(By.className("v-button-prevmonth")));
        closePopupDateField();
        Assert.assertFalse("The text field of the calendar should not be empty after selecting a date", textField.getAttribute("value").isEmpty());
        // Change resolutions and check that the selected date is not lost and
        // that the calendar has the correct resolution.
        click(resolutionHour);
        checkHeaderAndBody(HOUR, false);
        click(resolutionYear);
        checkHeaderAndBody(YEAR, false);
        click(resolutionMinute);
        checkHeaderAndBody(MINUTE, false);
    }
}

