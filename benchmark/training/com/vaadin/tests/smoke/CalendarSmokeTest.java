package com.vaadin.tests.smoke;


import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Test;


public class CalendarSmokeTest extends MultiBrowserTest {
    @Test
    public void calendarSmokeTest() {
        openTestURL();
        smokeTest();
    }

    @Test
    public void readOnlyCalendarSmokeTest() {
        openTestURL("restartApplication&readonly");
        smokeTest();
    }
}

