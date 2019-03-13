package com.querydsl.sql.types;


import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.OffsetDateTime;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Test;


public class JSR310OffsetDateTimeTypeTest extends AbstractJSR310DateTimeTypeTest<OffsetDateTime> {
    public JSR310OffsetDateTimeTypeTest() {
        super(new JSR310OffsetDateTimeType());
    }

    @Test
    public void set() throws SQLException {
        OffsetDateTime value = OffsetDateTime.now();
        Timestamp ts = new Timestamp(value.toInstant().toEpochMilli());
        PreparedStatement stmt = EasyMock.createNiceMock(PreparedStatement.class);
        stmt.setTimestamp(1, ts, AbstractJSR310DateTimeTypeTest.UTC);
        EasyMock.replay(stmt);
        type.setValue(stmt, 1, value);
        EasyMock.verify(stmt);
    }

    @Test
    public void jodaSet() throws SQLException {
        OffsetDateTime value = OffsetDateTime.now();
        Timestamp ts = new Timestamp(value.toInstant().toEpochMilli());
        PreparedStatement stmt = EasyMock.createNiceMock(PreparedStatement.class);
        stmt.setTimestamp(1, ts);
        EasyMock.replay(stmt);
        new DateTimeType().setValue(stmt, 1, AbstractJSR310DateTimeTypeTest.toJoda(value));
        EasyMock.verify(stmt);
    }

    @Test
    public void get() throws SQLException {
        ResultSet resultSet = EasyMock.createNiceMock(ResultSet.class);
        EasyMock.expect(resultSet.getTimestamp(1, AbstractJSR310DateTimeTypeTest.UTC)).andReturn(new Timestamp(AbstractJSR310DateTimeTypeTest.UTC.getTimeInMillis()));
        EasyMock.replay(resultSet);
        OffsetDateTime result = type.getValue(resultSet, 1);
        EasyMock.verify(resultSet);
        Assert.assertNotNull(result);
        Assert.assertTrue(((result.toEpochSecond()) == 0));
    }
}

