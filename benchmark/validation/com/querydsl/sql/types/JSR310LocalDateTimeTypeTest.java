package com.querydsl.sql.types;


import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Test;


public class JSR310LocalDateTimeTypeTest extends AbstractJSR310DateTimeTypeTest<LocalDateTime> {
    public JSR310LocalDateTimeTypeTest() {
        super(new JSR310LocalDateTimeType());
    }

    @Test
    public void set() throws SQLException {
        LocalDateTime value = LocalDateTime.now();
        Timestamp ts = new Timestamp(value.toInstant(ZoneOffset.UTC).toEpochMilli());
        PreparedStatement stmt = EasyMock.createNiceMock(PreparedStatement.class);
        stmt.setTimestamp(1, ts, AbstractJSR310DateTimeTypeTest.UTC);
        EasyMock.replay(stmt);
        type.setValue(stmt, 1, value);
        EasyMock.verify(stmt);
    }

    @Test
    public void jodaSet() throws SQLException {
        LocalDateTime value = LocalDateTime.now();
        Timestamp ts = new Timestamp(value.toInstant(ZoneOffset.UTC).toEpochMilli());
        PreparedStatement stmt = EasyMock.createNiceMock(PreparedStatement.class);
        stmt.setTimestamp(1, ts, AbstractJSR310DateTimeTypeTest.UTC);
        EasyMock.replay(stmt);
        new LocalDateTimeType().setValue(stmt, 1, AbstractJSR310DateTimeTypeTest.toJoda(value));
        EasyMock.verify(stmt);
    }

    @Test
    public void get() throws SQLException {
        ResultSet resultSet = EasyMock.createNiceMock(ResultSet.class);
        EasyMock.expect(resultSet.getTimestamp(1, AbstractJSR310DateTimeTypeTest.UTC)).andReturn(new Timestamp(AbstractJSR310DateTimeTypeTest.UTC.getTimeInMillis()));
        EasyMock.replay(resultSet);
        LocalDateTime result = type.getValue(resultSet, 1);
        EasyMock.verify(resultSet);
        Assert.assertNotNull(result);
        Assert.assertTrue(((result.getDayOfYear()) == 1));
    }
}

