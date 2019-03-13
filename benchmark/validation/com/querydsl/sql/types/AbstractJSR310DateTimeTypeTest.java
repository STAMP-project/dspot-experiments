package com.querydsl.sql.types;


import java.sql.SQLException;
import java.time.temporal.Temporal;
import java.util.Calendar;
import java.util.TimeZone;
import org.junit.Test;


public abstract class AbstractJSR310DateTimeTypeTest<T extends Temporal> {
    protected static final Calendar UTC = Calendar.getInstance(TimeZone.getTimeZone("UTC"));

    static {
        AbstractJSR310DateTimeTypeTest.UTC.setTimeInMillis(0);
    }

    protected final AbstractJSR310DateTimeType<T> type;

    public AbstractJSR310DateTimeTypeTest(AbstractJSR310DateTimeType<T> type) {
        this.type = type;
    }

    private TimeZone tz;

    @Test
    public void set_cST() throws SQLException {
        TimeZone.setDefault(TimeZone.getTimeZone("CST"));// -6:00

        set();
    }

    @Test
    public void set_iOT() throws SQLException {
        TimeZone.setDefault(TimeZone.getTimeZone("IOT"));// +6:00

        set();
    }

    @Test
    public void get_cST() throws SQLException {
        TimeZone.setDefault(TimeZone.getTimeZone("CST"));// -6:00

        get();
    }

    @Test
    public void get_iOT() throws SQLException {
        TimeZone.setDefault(TimeZone.getTimeZone("IOT"));// +6:00

        get();
    }
}

