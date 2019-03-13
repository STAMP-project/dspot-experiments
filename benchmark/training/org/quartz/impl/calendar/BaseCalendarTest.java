package org.quartz.impl.calendar;


import junit.framework.TestCase;


public class BaseCalendarTest extends TestCase {
    public void testClone() {
        BaseCalendar base = new BaseCalendar();
        BaseCalendar clone = ((BaseCalendar) (base.clone()));
        TestCase.assertEquals(base.getDescription(), clone.getDescription());
        TestCase.assertEquals(base.getBaseCalendar(), clone.getBaseCalendar());
        TestCase.assertEquals(base.getTimeZone(), clone.getTimeZone());
    }
}

