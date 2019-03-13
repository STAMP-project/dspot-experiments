package com.vaadin.tests.elements.calendar;


import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.CalendarElement;
import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Assert;
import org.junit.Test;


public class CalendarNavigationTest extends MultiBrowserTest {
    private CalendarElement calendarElement;

    @Test
    public void calendarNavigation_backAndForwardInWeekView_navigationWorks() {
        Assert.assertTrue(calendarElement.hasWeekView());
        String originalFirstDay = calendarElement.getDayHeaders().get(0).getText();
        calendarElement.back();
        calendarElement.waitForVaadin();
        Assert.assertNotEquals(originalFirstDay, calendarElement.getDayHeaders().get(0).getText());
        calendarElement.next();
        calendarElement.waitForVaadin();
        Assert.assertEquals(originalFirstDay, calendarElement.getDayHeaders().get(0).getText());
    }

    @Test(expected = IllegalStateException.class)
    public void calendarNavigation_navigationInMonthView_exceptionThrown() {
        $(ButtonElement.class).get(0).click();
        calendarElement.waitForVaadin();
        Assert.assertTrue(calendarElement.hasMonthView());
        calendarElement.next();
    }
}

