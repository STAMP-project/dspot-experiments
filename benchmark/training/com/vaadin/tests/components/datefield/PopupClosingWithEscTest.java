package com.vaadin.tests.components.datefield;


import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Test;


public class PopupClosingWithEscTest extends MultiBrowserTest {
    @Test
    public void testPopupClosingDayResolution() {
        testPopupClosing("day");
    }

    @Test
    public void testPopupClosingMonthResolution() {
        testPopupClosing("month");
    }

    @Test
    public void testPopupClosingYearResolution() {
        testPopupClosing("year");
    }
}

