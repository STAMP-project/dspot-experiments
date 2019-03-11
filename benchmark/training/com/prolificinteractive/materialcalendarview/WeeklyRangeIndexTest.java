package com.prolificinteractive.materialcalendarview;


import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;


public class WeeklyRangeIndexTest {
    private static final int _2018 = 2018;

    @Test
    public void test1week() {
        final CalendarDay startJanuary2018 = CalendarDay.from(WeeklyRangeIndexTest._2018, JANUARY.getValue(), 7);
        final CalendarDay endJanuary2018 = CalendarDay.from(WeeklyRangeIndexTest._2018, JANUARY.getValue(), 13);
        final DateRangeIndex weekly = new WeekPagerAdapter.Weekly(startJanuary2018, endJanuary2018, SUNDAY);
        MatcherAssert.assertThat(weekly.getCount(), Matchers.equalTo(1));
        MatcherAssert.assertThat(weekly.getItem(0), Matchers.equalTo(CalendarDay.from(WeeklyRangeIndexTest._2018, JANUARY.getValue(), 7)));
        MatcherAssert.assertThat(weekly.indexOf(startJanuary2018), Matchers.equalTo(0));
        MatcherAssert.assertThat(weekly.indexOf(endJanuary2018), Matchers.equalTo(0));
    }

    @Test
    public void test2weeksWith2Days() {
        final CalendarDay startJanuary2018 = CalendarDay.from(WeeklyRangeIndexTest._2018, JANUARY.getValue(), 13);
        final CalendarDay endJanuary2018 = CalendarDay.from(WeeklyRangeIndexTest._2018, JANUARY.getValue(), 14);
        final DateRangeIndex weekly = new WeekPagerAdapter.Weekly(startJanuary2018, endJanuary2018, SUNDAY);
        MatcherAssert.assertThat(weekly.getCount(), Matchers.equalTo(2));
        MatcherAssert.assertThat(weekly.getItem(0), Matchers.equalTo(CalendarDay.from(WeeklyRangeIndexTest._2018, JANUARY.getValue(), 7)));
        MatcherAssert.assertThat(weekly.indexOf(startJanuary2018), Matchers.equalTo(0));
        MatcherAssert.assertThat(weekly.indexOf(endJanuary2018), Matchers.equalTo(1));
    }

    @Test
    public void test2weeksWith2DaysWithDifferentFirstDay() {
        final CalendarDay startJanuary2018 = CalendarDay.from(WeeklyRangeIndexTest._2018, JANUARY.getValue(), 10);
        final CalendarDay endJanuary2018 = CalendarDay.from(WeeklyRangeIndexTest._2018, JANUARY.getValue(), 11);
        final DateRangeIndex weekly = new WeekPagerAdapter.Weekly(startJanuary2018, endJanuary2018, THURSDAY);
        MatcherAssert.assertThat(weekly.getCount(), Matchers.equalTo(2));
        MatcherAssert.assertThat(weekly.getItem(0), Matchers.equalTo(CalendarDay.from(WeeklyRangeIndexTest._2018, JANUARY.getValue(), 4)));
        MatcherAssert.assertThat(weekly.indexOf(startJanuary2018), Matchers.equalTo(0));
        MatcherAssert.assertThat(weekly.indexOf(endJanuary2018), Matchers.equalTo(1));
    }

    @Test
    public void test1weekStartingDifferentDays() {
        final CalendarDay startFebruary2018 = CalendarDay.from(WeeklyRangeIndexTest._2018, FEBRUARY.getValue(), 7);
        final CalendarDay endFebruary2018 = CalendarDay.from(WeeklyRangeIndexTest._2018, FEBRUARY.getValue(), 11);
        final DateRangeIndex monday = new WeekPagerAdapter.Weekly(startFebruary2018, endFebruary2018, MONDAY);
        final DateRangeIndex tuesday = new WeekPagerAdapter.Weekly(startFebruary2018, endFebruary2018, TUESDAY);
        final DateRangeIndex wednesday = new WeekPagerAdapter.Weekly(startFebruary2018, endFebruary2018, WEDNESDAY);
        final DateRangeIndex thursday = new WeekPagerAdapter.Weekly(startFebruary2018, endFebruary2018, THURSDAY);
        final DateRangeIndex friday = new WeekPagerAdapter.Weekly(startFebruary2018, endFebruary2018, FRIDAY);
        final DateRangeIndex saturday = new WeekPagerAdapter.Weekly(startFebruary2018, endFebruary2018, SATURDAY);
        final DateRangeIndex sunday = new WeekPagerAdapter.Weekly(startFebruary2018, endFebruary2018, SUNDAY);
        MatcherAssert.assertThat(monday.getItem(0), Matchers.equalTo(CalendarDay.from(WeeklyRangeIndexTest._2018, FEBRUARY.getValue(), 5)));
        MatcherAssert.assertThat(tuesday.getItem(0), Matchers.equalTo(CalendarDay.from(WeeklyRangeIndexTest._2018, FEBRUARY.getValue(), 6)));
        MatcherAssert.assertThat(wednesday.getItem(0), Matchers.equalTo(CalendarDay.from(WeeklyRangeIndexTest._2018, FEBRUARY.getValue(), 7)));
        MatcherAssert.assertThat(thursday.getItem(0), Matchers.equalTo(CalendarDay.from(WeeklyRangeIndexTest._2018, FEBRUARY.getValue(), 1)));
        MatcherAssert.assertThat(friday.getItem(0), Matchers.equalTo(CalendarDay.from(WeeklyRangeIndexTest._2018, FEBRUARY.getValue(), 2)));
        MatcherAssert.assertThat(saturday.getItem(0), Matchers.equalTo(CalendarDay.from(WeeklyRangeIndexTest._2018, FEBRUARY.getValue(), 3)));
        MatcherAssert.assertThat(sunday.getItem(0), Matchers.equalTo(CalendarDay.from(WeeklyRangeIndexTest._2018, FEBRUARY.getValue(), 4)));
    }

    @Test
    public void test2weeks() {
        final CalendarDay startJanuary2018 = CalendarDay.from(WeeklyRangeIndexTest._2018, JANUARY.getValue(), 9);
        final CalendarDay endJanuary2018 = CalendarDay.from(WeeklyRangeIndexTest._2018, JANUARY.getValue(), 18);
        final DateRangeIndex weekly = new WeekPagerAdapter.Weekly(startJanuary2018, endJanuary2018, SUNDAY);
        MatcherAssert.assertThat(weekly.getCount(), Matchers.equalTo(2));
        MatcherAssert.assertThat(weekly.getItem(0), Matchers.equalTo(CalendarDay.from(WeeklyRangeIndexTest._2018, JANUARY.getValue(), 7)));
        MatcherAssert.assertThat(weekly.getItem(1), Matchers.equalTo(CalendarDay.from(WeeklyRangeIndexTest._2018, JANUARY.getValue(), 14)));
        MatcherAssert.assertThat(weekly.indexOf(startJanuary2018), Matchers.equalTo(0));
        MatcherAssert.assertThat(weekly.indexOf(endJanuary2018), Matchers.equalTo(1));
    }

    @Test
    public void test10weeks() {
        final CalendarDay january2018 = CalendarDay.from(WeeklyRangeIndexTest._2018, JANUARY.getValue(), 9);
        final CalendarDay march2018 = CalendarDay.from(WeeklyRangeIndexTest._2018, MARCH.getValue(), 12);
        final DateRangeIndex weekly = new WeekPagerAdapter.Weekly(january2018, march2018, SUNDAY);
        MatcherAssert.assertThat(weekly.getCount(), Matchers.equalTo(10));
        MatcherAssert.assertThat(weekly.getItem(0), Matchers.equalTo(CalendarDay.from(WeeklyRangeIndexTest._2018, JANUARY.getValue(), 7)));
        MatcherAssert.assertThat(weekly.getItem(9), Matchers.equalTo(CalendarDay.from(WeeklyRangeIndexTest._2018, MARCH.getValue(), 11)));
        MatcherAssert.assertThat(weekly.indexOf(january2018), Matchers.equalTo(0));
        MatcherAssert.assertThat(weekly.indexOf(march2018), Matchers.equalTo(9));
    }

    @Test
    public void test52weeks() {
        final CalendarDay january2018 = CalendarDay.from(WeeklyRangeIndexTest._2018, JANUARY.getValue(), 9);
        final CalendarDay january2019 = CalendarDay.from(2019, JANUARY.getValue(), 3);
        final DateRangeIndex weekly = new WeekPagerAdapter.Weekly(january2018, january2019, SUNDAY);
        MatcherAssert.assertThat(weekly.getCount(), Matchers.equalTo(52));
        MatcherAssert.assertThat(weekly.getItem(0), Matchers.equalTo(CalendarDay.from(WeeklyRangeIndexTest._2018, JANUARY.getValue(), 7)));
        MatcherAssert.assertThat(weekly.getItem(51), Matchers.equalTo(CalendarDay.from(2018, DECEMBER.getValue(), 30)));
        MatcherAssert.assertThat(weekly.indexOf(january2018), Matchers.equalTo(0));
        MatcherAssert.assertThat(weekly.indexOf(january2019), Matchers.equalTo(51));
    }

    @Test
    public void test1000weeks() {
        final CalendarDay january2018 = CalendarDay.from(WeeklyRangeIndexTest._2018, JANUARY.getValue(), 11);
        final CalendarDay march2037 = CalendarDay.from(2037, MARCH.getValue(), 1);
        final DateRangeIndex weekly = new WeekPagerAdapter.Weekly(january2018, march2037, SUNDAY);
        MatcherAssert.assertThat(weekly.getCount(), Matchers.equalTo(1000));
        MatcherAssert.assertThat(weekly.getItem(0), Matchers.equalTo(CalendarDay.from(WeeklyRangeIndexTest._2018, JANUARY.getValue(), 7)));
        MatcherAssert.assertThat(weekly.getItem(999), Matchers.equalTo(CalendarDay.from(2037, MARCH.getValue(), 1)));
        MatcherAssert.assertThat(weekly.indexOf(january2018), Matchers.equalTo(0));
        MatcherAssert.assertThat(weekly.indexOf(march2037), Matchers.equalTo(999));
    }
}

