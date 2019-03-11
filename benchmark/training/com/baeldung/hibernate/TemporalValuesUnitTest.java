package com.baeldung.hibernate;


import com.baeldung.hibernate.pojo.TemporalValues;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.TimeZone;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.junit.Test;


public class TemporalValuesUnitTest {
    private Session session;

    private Transaction transaction;

    @Test
    public void givenEntity_whenMappingSqlTypes_thenTemporalIsSelectedAutomatically() {
        TemporalValues temporalValues = new TemporalValues();
        temporalValues.setSqlDate(Date.valueOf("2017-11-15"));
        temporalValues.setSqlTime(Time.valueOf("15:30:14"));
        temporalValues.setSqlTimestamp(Timestamp.valueOf("2017-11-15 15:30:14.332"));
        session.save(temporalValues);
        session.flush();
        session.clear();
        temporalValues = session.get(TemporalValues.class, temporalValues.getId());
        assertThat(temporalValues.getSqlDate()).isEqualTo(Date.valueOf("2017-11-15"));
        assertThat(temporalValues.getSqlTime()).isEqualTo(Time.valueOf("15:30:14"));
        assertThat(temporalValues.getSqlTimestamp()).isEqualTo(Timestamp.valueOf("2017-11-15 15:30:14.332"));
    }

    @Test
    public void givenEntity_whenMappingUtilDateType_thenTemporalIsSpecifiedExplicitly() throws Exception {
        TemporalValues temporalValues = new TemporalValues();
        temporalValues.setUtilDate(new SimpleDateFormat("yyyy-MM-dd").parse("2017-11-15"));
        temporalValues.setUtilTime(new SimpleDateFormat("HH:mm:ss").parse("15:30:14"));
        temporalValues.setUtilTimestamp(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").parse("2017-11-15 15:30:14.332"));
        session.save(temporalValues);
        session.flush();
        session.clear();
        temporalValues = session.get(TemporalValues.class, temporalValues.getId());
        assertThat(temporalValues.getUtilDate()).isEqualTo(new SimpleDateFormat("yyyy-MM-dd").parse("2017-11-15"));
        assertThat(temporalValues.getUtilTime()).isEqualTo(new SimpleDateFormat("HH:mm:ss").parse("15:30:14"));
        assertThat(temporalValues.getUtilTimestamp()).isEqualTo(Timestamp.valueOf("2017-11-15 15:30:14.332"));
    }

    @Test
    public void givenEntity_whenMappingCalendarType_thenTemporalIsSpecifiedExplicitly() throws Exception {
        TemporalValues temporalValues = new TemporalValues();
        Calendar calendarDate = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        calendarDate.set(Calendar.YEAR, 2017);
        calendarDate.set(Calendar.MONTH, 10);
        calendarDate.set(Calendar.DAY_OF_MONTH, 15);
        temporalValues.setCalendarDate(calendarDate);
        Calendar calendarTimestamp = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        calendarTimestamp.setTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").parse("2017-11-15 15:30:14.322"));
        temporalValues.setCalendarTimestamp(calendarTimestamp);
        session.save(temporalValues);
        session.flush();
        session.clear();
        temporalValues = session.get(TemporalValues.class, temporalValues.getId());
        assertThat(temporalValues.getCalendarDate().getTime()).isEqualTo(new SimpleDateFormat("yyyy-MM-dd").parse("2017-11-15"));
        assertThat(temporalValues.getCalendarTimestamp().getTime()).isEqualTo(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").parse("2017-11-15 15:30:14.322"));
    }

    @Test
    public void givenEntity_whenMappingJavaTimeTypes_thenTemporalIsSelectedAutomatically() {
        TemporalValues temporalValues = new TemporalValues();
        temporalValues.setLocalDate(LocalDate.parse("2017-11-15"));
        temporalValues.setLocalTime(LocalTime.parse("15:30:18"));
        temporalValues.setOffsetTime(OffsetTime.parse("08:22:12+01:00"));
        System.out.println(("********" + (OffsetTime.parse("08:22:12+01:00").toString())));
        temporalValues.setInstant(Instant.parse("2017-11-15T08:22:12Z"));
        temporalValues.setLocalDateTime(LocalDateTime.parse("2017-11-15T08:22:12"));
        temporalValues.setOffsetDateTime(OffsetDateTime.parse("2017-11-15T08:22:12+01:00"));
        temporalValues.setZonedDateTime(ZonedDateTime.parse("2017-11-15T08:22:12+01:00[Europe/Paris]"));
        session.save(temporalValues);
        session.flush();
        session.clear();
        temporalValues = session.get(TemporalValues.class, temporalValues.getId());
        assertThat(temporalValues.getLocalDate()).isEqualTo(LocalDate.parse("2017-11-15"));
        assertThat(temporalValues.getLocalTime()).isEqualTo(LocalTime.parse("15:30:18"));
        // assertThat(temporalValues.getOffsetTime()).isEqualTo(OffsetTime.parse("08:22:12+01:00"));
        assertThat(temporalValues.getInstant()).isEqualTo(Instant.parse("2017-11-15T08:22:12Z"));
        assertThat(temporalValues.getLocalDateTime()).isEqualTo(LocalDateTime.parse("2017-11-15T08:22:12"));
        // assertThat(temporalValues.getOffsetDateTime()).isEqualTo(OffsetDateTime.parse("2017-11-15T08:22:12+01:00"));
        assertThat(temporalValues.getZonedDateTime()).isEqualTo(ZonedDateTime.parse("2017-11-15T08:22:12+01:00[Europe/Paris]"));
    }
}

