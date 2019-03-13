package com.prolificinteractive.materialcalendarview;


import MonthPagerAdapter.Monthly;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;


public class MonthlyRangeIndexTest {
    @Test
    public void test400Years() {
        final CalendarDay january1816 = CalendarDay.from(1816, JANUARY.getValue(), 15);
        final CalendarDay january2216 = CalendarDay.from(2216, JANUARY.getValue(), 15);
        final CalendarDay april2018 = CalendarDay.from(2018, APRIL.getValue(), 1);
        final CalendarDay february1816 = CalendarDay.from(1816, FEBRUARY.getValue(), 1);
        final MonthPagerAdapter.Monthly monthly = new MonthPagerAdapter.Monthly(january1816, january2216);
        MatcherAssert.assertThat(monthly.getCount(), Matchers.equalTo((((2216 - 1816) * 12) + 1)));
        MatcherAssert.assertThat(monthly.getItem((((2018 - 1816) * 12) + 3)), Matchers.equalTo(april2018));
        MatcherAssert.assertThat(monthly.getItem(1), Matchers.equalTo(february1816));
        MatcherAssert.assertThat(monthly.indexOf(january1816), Matchers.equalTo(0));
        MatcherAssert.assertThat(monthly.indexOf(february1816), Matchers.equalTo(1));
        MatcherAssert.assertThat(monthly.indexOf(april2018), Matchers.equalTo((((2018 - 1816) * 12) + 3)));
        MatcherAssert.assertThat(monthly.indexOf(january2216), Matchers.equalTo(((2216 - 1816) * 12)));
    }

    @Test
    public void test3Years() {
        final CalendarDay may2016 = CalendarDay.from(2016, MAY.getValue(), 6);
        final CalendarDay april2017 = CalendarDay.from(2017, MAY.getValue(), 1);
        final CalendarDay june2019 = CalendarDay.from(2019, JUNE.getValue(), 1);
        final CalendarDay july2019 = CalendarDay.from(2019, JULY.getValue(), 21);
        final MonthPagerAdapter.Monthly monthly = new MonthPagerAdapter.Monthly(may2016, july2019);
        MatcherAssert.assertThat(monthly.getCount(), Matchers.equalTo((((2019 - 2016) * 12) + 3)));
        MatcherAssert.assertThat(monthly.getItem(12), Matchers.equalTo(april2017));
        MatcherAssert.assertThat(monthly.getItem((((2019 - 2016) * 12) + 1)), Matchers.equalTo(june2019));
        MatcherAssert.assertThat(monthly.indexOf(may2016), Matchers.equalTo(0));
        MatcherAssert.assertThat(monthly.indexOf(april2017), Matchers.equalTo(12));
        MatcherAssert.assertThat(monthly.indexOf(june2019), Matchers.equalTo((((2019 - 2016) * 12) + 1)));
        MatcherAssert.assertThat(monthly.indexOf(july2019), Matchers.equalTo((((2019 - 2016) * 12) + 2)));
    }

    @Test
    public void test2Years() {
        final CalendarDay may2016 = CalendarDay.from(2016, MAY.getValue(), 31);
        final CalendarDay may2018 = CalendarDay.from(2018, MAY.getValue(), 1);
        final MonthPagerAdapter.Monthly monthly = new MonthPagerAdapter.Monthly(may2016, may2018);
        MatcherAssert.assertThat(monthly.getCount(), Matchers.equalTo(25));
        MatcherAssert.assertThat(monthly.getItem(25), Matchers.equalTo(CalendarDay.from(2018, JUNE.getValue(), 1)));
        MatcherAssert.assertThat(monthly.indexOf(may2016), Matchers.equalTo(0));
        MatcherAssert.assertThat(monthly.indexOf(may2018), Matchers.equalTo(((2018 - 2016) * 12)));
    }

    @Test
    public void test1Year() {
        final CalendarDay january2018 = CalendarDay.from(2018, JANUARY.getValue(), 1);
        final CalendarDay december2018 = CalendarDay.from(2018, DECEMBER.getValue(), 31);
        final CalendarDay last = CalendarDay.from(2018, DECEMBER.getValue(), 1);
        final MonthPagerAdapter.Monthly monthly = new MonthPagerAdapter.Monthly(january2018, december2018);
        MatcherAssert.assertThat(monthly.getCount(), Matchers.equalTo(12));
        MatcherAssert.assertThat(monthly.getItem(0), Matchers.equalTo(january2018));
        MatcherAssert.assertThat(monthly.getItem(11), Matchers.equalTo(last));
        MatcherAssert.assertThat(monthly.indexOf(january2018), Matchers.equalTo(0));
        MatcherAssert.assertThat(monthly.indexOf(last), Matchers.equalTo(11));
        MatcherAssert.assertThat(monthly.indexOf(december2018), Matchers.equalTo(11));
    }

    @Test
    public void test3Months() {
        final CalendarDay may2018 = CalendarDay.from(2018, MAY.getValue(), 25);
        final CalendarDay june2018 = CalendarDay.from(2018, JUNE.getValue(), 1);
        final CalendarDay july2018 = CalendarDay.from(2018, JULY.getValue(), 5);
        final MonthPagerAdapter.Monthly monthly = new MonthPagerAdapter.Monthly(may2018, july2018);
        MatcherAssert.assertThat(monthly.getCount(), Matchers.equalTo(3));
        MatcherAssert.assertThat(monthly.getItem(1), Matchers.equalTo(june2018));
        MatcherAssert.assertThat(monthly.indexOf(may2018), Matchers.equalTo(0));
        MatcherAssert.assertThat(monthly.indexOf(june2018), Matchers.equalTo(1));
        MatcherAssert.assertThat(monthly.indexOf(july2018), Matchers.equalTo(2));
    }

    @Test
    public void test2Months() {
        final CalendarDay october2018 = CalendarDay.from(2018, OCTOBER.getValue(), 31);
        final CalendarDay november2018 = CalendarDay.from(2018, NOVEMBER.getValue(), 1);
        final MonthPagerAdapter.Monthly monthly = new MonthPagerAdapter.Monthly(october2018, november2018);
        MatcherAssert.assertThat(monthly.getCount(), Matchers.equalTo(2));
        MatcherAssert.assertThat(monthly.getItem(0), Matchers.equalTo(CalendarDay.from(2018, OCTOBER.getValue(), 1)));
        MatcherAssert.assertThat(monthly.getItem(1), Matchers.equalTo(CalendarDay.from(2018, NOVEMBER.getValue(), 1)));
        MatcherAssert.assertThat(monthly.indexOf(october2018), Matchers.equalTo(0));
        MatcherAssert.assertThat(monthly.indexOf(november2018), Matchers.equalTo(1));
    }

    @Test
    public void test1Month() {
        final CalendarDay startOctober2018 = CalendarDay.from(2018, OCTOBER.getValue(), 5);
        final CalendarDay endOctober2018 = CalendarDay.from(2018, OCTOBER.getValue(), 25);
        final MonthPagerAdapter.Monthly monthly = new MonthPagerAdapter.Monthly(endOctober2018, endOctober2018);
        MatcherAssert.assertThat(monthly.getCount(), Matchers.equalTo(1));
        MatcherAssert.assertThat(monthly.getItem(0), Matchers.equalTo(CalendarDay.from(2018, OCTOBER.getValue(), 1)));
        MatcherAssert.assertThat(monthly.indexOf(startOctober2018), Matchers.equalTo(0));
        MatcherAssert.assertThat(monthly.indexOf(endOctober2018), Matchers.equalTo(0));
    }
}

