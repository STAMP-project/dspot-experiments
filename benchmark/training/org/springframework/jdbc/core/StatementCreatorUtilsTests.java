/**
 * Copyright 2002-2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.jdbc.core;


import SqlTypeValue.TYPE_UNKNOWN;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.Date;
import java.sql.ParameterMetaData;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.Calendar;
import java.util.GregorianCalendar;
import org.junit.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;

import static StatementCreatorUtils.shouldIgnoreGetParameterType;


/**
 *
 *
 * @author Juergen Hoeller
 * @since 31.08.2004
 */
public class StatementCreatorUtilsTests {
    private PreparedStatement preparedStatement;

    @Test
    public void testSetParameterValueWithNullAndType() throws SQLException {
        StatementCreatorUtils.setParameterValue(preparedStatement, 1, Types.VARCHAR, null, null);
        Mockito.verify(preparedStatement).setNull(1, Types.VARCHAR);
    }

    @Test
    public void testSetParameterValueWithNullAndTypeName() throws SQLException {
        StatementCreatorUtils.setParameterValue(preparedStatement, 1, Types.VARCHAR, "mytype", null);
        Mockito.verify(preparedStatement).setNull(1, Types.VARCHAR, "mytype");
    }

    @Test
    public void testSetParameterValueWithNullAndUnknownType() throws SQLException {
        shouldIgnoreGetParameterType = true;
        Connection con = Mockito.mock(Connection.class);
        DatabaseMetaData dbmd = Mockito.mock(DatabaseMetaData.class);
        BDDMockito.given(preparedStatement.getConnection()).willReturn(con);
        BDDMockito.given(dbmd.getDatabaseProductName()).willReturn("Oracle");
        BDDMockito.given(dbmd.getDriverName()).willReturn("Oracle Driver");
        BDDMockito.given(con.getMetaData()).willReturn(dbmd);
        StatementCreatorUtils.setParameterValue(preparedStatement, 1, TYPE_UNKNOWN, null, null);
        Mockito.verify(preparedStatement).setNull(1, Types.NULL);
        shouldIgnoreGetParameterType = false;
    }

    @Test
    public void testSetParameterValueWithNullAndUnknownTypeOnInformix() throws SQLException {
        shouldIgnoreGetParameterType = true;
        Connection con = Mockito.mock(Connection.class);
        DatabaseMetaData dbmd = Mockito.mock(DatabaseMetaData.class);
        BDDMockito.given(preparedStatement.getConnection()).willReturn(con);
        BDDMockito.given(con.getMetaData()).willReturn(dbmd);
        BDDMockito.given(dbmd.getDatabaseProductName()).willReturn("Informix Dynamic Server");
        BDDMockito.given(dbmd.getDriverName()).willReturn("Informix Driver");
        StatementCreatorUtils.setParameterValue(preparedStatement, 1, TYPE_UNKNOWN, null, null);
        Mockito.verify(dbmd).getDatabaseProductName();
        Mockito.verify(dbmd).getDriverName();
        Mockito.verify(preparedStatement).setObject(1, null);
        shouldIgnoreGetParameterType = false;
    }

    @Test
    public void testSetParameterValueWithNullAndUnknownTypeOnDerbyEmbedded() throws SQLException {
        shouldIgnoreGetParameterType = true;
        Connection con = Mockito.mock(Connection.class);
        DatabaseMetaData dbmd = Mockito.mock(DatabaseMetaData.class);
        BDDMockito.given(preparedStatement.getConnection()).willReturn(con);
        BDDMockito.given(con.getMetaData()).willReturn(dbmd);
        BDDMockito.given(dbmd.getDatabaseProductName()).willReturn("Apache Derby");
        BDDMockito.given(dbmd.getDriverName()).willReturn("Apache Derby Embedded Driver");
        StatementCreatorUtils.setParameterValue(preparedStatement, 1, TYPE_UNKNOWN, null, null);
        Mockito.verify(dbmd).getDatabaseProductName();
        Mockito.verify(dbmd).getDriverName();
        Mockito.verify(preparedStatement).setNull(1, Types.VARCHAR);
        shouldIgnoreGetParameterType = false;
    }

    @Test
    public void testSetParameterValueWithNullAndGetParameterTypeWorking() throws SQLException {
        ParameterMetaData pmd = Mockito.mock(ParameterMetaData.class);
        BDDMockito.given(preparedStatement.getParameterMetaData()).willReturn(pmd);
        BDDMockito.given(pmd.getParameterType(1)).willReturn(Types.SMALLINT);
        StatementCreatorUtils.setParameterValue(preparedStatement, 1, TYPE_UNKNOWN, null, null);
        Mockito.verify(pmd).getParameterType(1);
        Mockito.verify(preparedStatement, Mockito.never()).getConnection();
        Mockito.verify(preparedStatement).setNull(1, Types.SMALLINT);
    }

    @Test
    public void testSetParameterValueWithString() throws SQLException {
        StatementCreatorUtils.setParameterValue(preparedStatement, 1, Types.VARCHAR, null, "test");
        Mockito.verify(preparedStatement).setString(1, "test");
    }

    @Test
    public void testSetParameterValueWithStringAndSpecialType() throws SQLException {
        StatementCreatorUtils.setParameterValue(preparedStatement, 1, Types.CHAR, null, "test");
        Mockito.verify(preparedStatement).setObject(1, "test", Types.CHAR);
    }

    @Test
    public void testSetParameterValueWithStringAndUnknownType() throws SQLException {
        StatementCreatorUtils.setParameterValue(preparedStatement, 1, TYPE_UNKNOWN, null, "test");
        Mockito.verify(preparedStatement).setString(1, "test");
    }

    @Test
    public void testSetParameterValueWithSqlDate() throws SQLException {
        Date date = new Date(1000);
        StatementCreatorUtils.setParameterValue(preparedStatement, 1, Types.DATE, null, date);
        Mockito.verify(preparedStatement).setDate(1, date);
    }

    @Test
    public void testSetParameterValueWithDateAndUtilDate() throws SQLException {
        java.util.Date date = new java.util.Date(1000);
        StatementCreatorUtils.setParameterValue(preparedStatement, 1, Types.DATE, null, date);
        Mockito.verify(preparedStatement).setDate(1, new Date(1000));
    }

    @Test
    public void testSetParameterValueWithDateAndCalendar() throws SQLException {
        Calendar cal = new GregorianCalendar();
        StatementCreatorUtils.setParameterValue(preparedStatement, 1, Types.DATE, null, cal);
        Mockito.verify(preparedStatement).setDate(1, new Date(cal.getTime().getTime()), cal);
    }

    @Test
    public void testSetParameterValueWithSqlTime() throws SQLException {
        Time time = new Time(1000);
        StatementCreatorUtils.setParameterValue(preparedStatement, 1, Types.TIME, null, time);
        Mockito.verify(preparedStatement).setTime(1, time);
    }

    @Test
    public void testSetParameterValueWithTimeAndUtilDate() throws SQLException {
        java.util.Date date = new java.util.Date(1000);
        StatementCreatorUtils.setParameterValue(preparedStatement, 1, Types.TIME, null, date);
        Mockito.verify(preparedStatement).setTime(1, new Time(1000));
    }

    @Test
    public void testSetParameterValueWithTimeAndCalendar() throws SQLException {
        Calendar cal = new GregorianCalendar();
        StatementCreatorUtils.setParameterValue(preparedStatement, 1, Types.TIME, null, cal);
        Mockito.verify(preparedStatement).setTime(1, new Time(cal.getTime().getTime()), cal);
    }

    @Test
    public void testSetParameterValueWithSqlTimestamp() throws SQLException {
        Timestamp timestamp = new Timestamp(1000);
        StatementCreatorUtils.setParameterValue(preparedStatement, 1, Types.TIMESTAMP, null, timestamp);
        Mockito.verify(preparedStatement).setTimestamp(1, timestamp);
    }

    @Test
    public void testSetParameterValueWithTimestampAndUtilDate() throws SQLException {
        java.util.Date date = new java.util.Date(1000);
        StatementCreatorUtils.setParameterValue(preparedStatement, 1, Types.TIMESTAMP, null, date);
        Mockito.verify(preparedStatement).setTimestamp(1, new Timestamp(1000));
    }

    @Test
    public void testSetParameterValueWithTimestampAndCalendar() throws SQLException {
        Calendar cal = new GregorianCalendar();
        StatementCreatorUtils.setParameterValue(preparedStatement, 1, Types.TIMESTAMP, null, cal);
        Mockito.verify(preparedStatement).setTimestamp(1, new Timestamp(cal.getTime().getTime()), cal);
    }

    @Test
    public void testSetParameterValueWithDateAndUnknownType() throws SQLException {
        java.util.Date date = new java.util.Date(1000);
        StatementCreatorUtils.setParameterValue(preparedStatement, 1, TYPE_UNKNOWN, null, date);
        Mockito.verify(preparedStatement).setTimestamp(1, new Timestamp(1000));
    }

    @Test
    public void testSetParameterValueWithCalendarAndUnknownType() throws SQLException {
        Calendar cal = new GregorianCalendar();
        StatementCreatorUtils.setParameterValue(preparedStatement, 1, TYPE_UNKNOWN, null, cal);
        Mockito.verify(preparedStatement).setTimestamp(1, new Timestamp(cal.getTime().getTime()), cal);
    }

    // SPR-8571
    @Test
    public void testSetParameterValueWithStringAndVendorSpecificType() throws SQLException {
        Connection con = Mockito.mock(Connection.class);
        DatabaseMetaData dbmd = Mockito.mock(DatabaseMetaData.class);
        BDDMockito.given(preparedStatement.getConnection()).willReturn(con);
        BDDMockito.given(dbmd.getDatabaseProductName()).willReturn("Oracle");
        BDDMockito.given(con.getMetaData()).willReturn(dbmd);
        StatementCreatorUtils.setParameterValue(preparedStatement, 1, Types.OTHER, null, "test");
        Mockito.verify(preparedStatement).setString(1, "test");
    }

    // SPR-8571
    @Test
    public void testSetParameterValueWithNullAndVendorSpecificType() throws SQLException {
        shouldIgnoreGetParameterType = true;
        Connection con = Mockito.mock(Connection.class);
        DatabaseMetaData dbmd = Mockito.mock(DatabaseMetaData.class);
        BDDMockito.given(preparedStatement.getConnection()).willReturn(con);
        BDDMockito.given(dbmd.getDatabaseProductName()).willReturn("Oracle");
        BDDMockito.given(dbmd.getDriverName()).willReturn("Oracle Driver");
        BDDMockito.given(con.getMetaData()).willReturn(dbmd);
        StatementCreatorUtils.setParameterValue(preparedStatement, 1, Types.OTHER, null, null);
        Mockito.verify(preparedStatement).setNull(1, Types.NULL);
        shouldIgnoreGetParameterType = false;
    }
}

