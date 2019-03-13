package com.querydsl.sql.types;


import DateTimeZone.UTC;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import org.easymock.EasyMock;
import org.joda.time.DateTime;
import org.joda.time.LocalDateTime;
import org.junit.Test;


public class LocalDateTimeTest {
    private static final Calendar UTC = Calendar.getInstance(TimeZone.getTimeZone("UTC"));

    private LocalDateTimeType type = new LocalDateTimeType();

    @Test
    public void set() throws SQLException {
        LocalDateTime value = new LocalDateTime();
        DateTime dt = value.toDateTime(DateTimeZone.UTC);
        Timestamp ts = new Timestamp(dt.getMillis());
        PreparedStatement stmt = EasyMock.createNiceMock(PreparedStatement.class);
        stmt.setTimestamp(0, ts, LocalDateTimeTest.UTC);
        EasyMock.replay(stmt);
        type.setValue(stmt, 0, value);
        EasyMock.verify(stmt);
    }
}

