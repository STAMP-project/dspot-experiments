package com.vaadin.tests.components.datefield;


import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Test;


public class DateFieldWeekShortNameTest extends MultiBrowserTest {
    @Test
    public void ar() {
        // Sat, Sun, Mon, Tue, Wed, Thu, Fri
        String[] shortWeekDays = new String[]{ "?", "?", "?", "?", "?", "?", "?" };
        test(0, 30, shortWeekDays);
    }

    @Test
    public void de() {
        String[] shortWeekDays = new String[]{ "Mo", "Di", "Mi", "Do", "Fr", "Sa", "So" };
        test(1, 25, shortWeekDays);
    }

    @Test
    public void en() {
        String[] shortWeekDays = new String[]{ "Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat" };
        test(2, 1, shortWeekDays);
    }
}

