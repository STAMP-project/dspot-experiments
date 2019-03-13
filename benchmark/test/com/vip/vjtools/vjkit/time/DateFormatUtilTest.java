package com.vip.vjtools.vjkit.time;


import DateFormatUtil.DEFAULT_FORMAT;
import DateFormatUtil.DEFAULT_ON_SECOND_FORMAT;
import DateFormatUtil.ISO_FORMAT;
import DateFormatUtil.ISO_ON_DATE_FORMAT;
import DateFormatUtil.ISO_ON_SECOND_FORMAT;
import DateFormatUtil.PATTERN_DEFAULT;
import java.text.ParseException;
import java.util.Date;
import org.junit.Test;

import static DateUtil.MILLIS_PER_DAY;
import static DateUtil.MILLIS_PER_HOUR;


public class DateFormatUtilTest {
    @Test
    public void isoDateFormat() {
        Date date = new Date(116, 10, 1, 12, 23, 44);
        assertThat(ISO_FORMAT.format(date)).contains("2016-11-01T12:23:44.000");
        assertThat(ISO_ON_SECOND_FORMAT.format(date)).contains("2016-11-01T12:23:44");
        assertThat(ISO_ON_DATE_FORMAT.format(date)).isEqualTo("2016-11-01");
    }

    @Test
    public void defaultDateFormat() {
        Date date = new Date(116, 10, 1, 12, 23, 44);
        assertThat(DEFAULT_FORMAT.format(date)).isEqualTo("2016-11-01 12:23:44.000");
        assertThat(DEFAULT_ON_SECOND_FORMAT.format(date)).isEqualTo("2016-11-01 12:23:44");
    }

    @Test
    public void formatWithPattern() {
        Date date = new Date(116, 10, 1, 12, 23, 44);
        assertThat(DateFormatUtil.formatDate(PATTERN_DEFAULT, date)).isEqualTo("2016-11-01 12:23:44.000");
        assertThat(DateFormatUtil.formatDate(PATTERN_DEFAULT, date.getTime())).isEqualTo("2016-11-01 12:23:44.000");
    }

    @Test
    public void parseWithPattern() throws ParseException {
        Date date = new Date(116, 10, 1, 12, 23, 44);
        Date resultDate = DateFormatUtil.parseDate(PATTERN_DEFAULT, "2016-11-01 12:23:44.000");
        assertThat(((resultDate.getTime()) == (date.getTime()))).isTrue();
    }

    @Test
    public void formatDuration() {
        assertThat(DateFormatUtil.formatDuration(100)).isEqualTo("00:00:00.100");
        assertThat(DateFormatUtil.formatDuration(new Date(100), new Date(3000))).isEqualTo("00:00:02.900");
        assertThat(DateFormatUtil.formatDuration((((MILLIS_PER_DAY) * 2) + ((MILLIS_PER_HOUR) * 4)))).isEqualTo("52:00:00.000");
        assertThat(DateFormatUtil.formatDurationOnSecond(new Date(100), new Date(3000))).isEqualTo("00:00:02");
        assertThat(DateFormatUtil.formatDurationOnSecond(2000)).isEqualTo("00:00:02");
        assertThat(DateFormatUtil.formatDurationOnSecond((((MILLIS_PER_DAY) * 2) + ((MILLIS_PER_HOUR) * 4)))).isEqualTo("52:00:00");
    }

    @Test
    public void formatFriendlyTimeSpanByNow() throws ParseException {
        try {
            Date now = DEFAULT_ON_SECOND_FORMAT.parse("2016-12-11 23:30:00");
            ClockUtil.useDummyClock(now);
            Date lessOneSecond = DEFAULT_FORMAT.parse("2016-12-11 23:29:59.500");
            assertThat(DateFormatUtil.formatFriendlyTimeSpanByNow(lessOneSecond)).isEqualTo("??");
            Date lessOneMinute = DEFAULT_FORMAT.parse("2016-12-11 23:29:55.000");
            assertThat(DateFormatUtil.formatFriendlyTimeSpanByNow(lessOneMinute)).isEqualTo("5??");
            Date lessOneHour = DEFAULT_ON_SECOND_FORMAT.parse("2016-12-11 23:00:00");
            assertThat(DateFormatUtil.formatFriendlyTimeSpanByNow(lessOneHour)).isEqualTo("30???");
            Date today = DEFAULT_ON_SECOND_FORMAT.parse("2016-12-11 1:00:00");
            assertThat(DateFormatUtil.formatFriendlyTimeSpanByNow(today)).isEqualTo("??01:00");
            Date yesterday = DEFAULT_ON_SECOND_FORMAT.parse("2016-12-10 1:00:00");
            assertThat(DateFormatUtil.formatFriendlyTimeSpanByNow(yesterday)).isEqualTo("??01:00");
            Date threeDayBefore = DEFAULT_ON_SECOND_FORMAT.parse("2016-12-09 1:00:00");
            assertThat(DateFormatUtil.formatFriendlyTimeSpanByNow(threeDayBefore)).isEqualTo("2016-12-09");
        } finally {
            ClockUtil.useDefaultClock();
        }
    }
}

