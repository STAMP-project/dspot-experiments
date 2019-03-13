/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.shardingsphere.core.merger.dql.common;


import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLXML;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Collection;
import org.apache.shardingsphere.core.merger.dql.common.fixture.TestStreamMergedResult;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public final class StreamMergedResultTest {
    @Mock
    private ResultSet resultSet;

    private TestStreamMergedResult streamMergedResult;

    @Test(expected = SQLException.class)
    public void assertGetCurrentResultSetIfNull() throws SQLException {
        setCurrentQueryResult(null);
        getCurrentQueryResult();
    }

    @Test
    public void assertGetValueWithColumnIndexWithObject() throws SQLException {
        Mockito.when(resultSet.getObject(1)).thenReturn("1");
        Assert.assertThat(streamMergedResult.getValue(1, Object.class).toString(), CoreMatchers.is("1"));
    }

    @Test
    public void assertGetValueWithColumnIndexWithBoolean() throws SQLException {
        Mockito.when(resultSet.getBoolean(1)).thenReturn(true);
        Assert.assertTrue(((Boolean) (streamMergedResult.getValue(1, boolean.class))));
    }

    @Test
    public void assertGetValueWithColumnIndexWithByte() throws SQLException {
        Mockito.when(resultSet.getByte(1)).thenReturn(((byte) (1)));
        Assert.assertThat(((byte) (streamMergedResult.getValue(1, byte.class))), CoreMatchers.is(((byte) (1))));
    }

    @Test
    public void assertGetValueWithColumnIndexWithShort() throws SQLException {
        Mockito.when(resultSet.getShort(1)).thenReturn(((short) (1)));
        Assert.assertThat(((short) (streamMergedResult.getValue(1, short.class))), CoreMatchers.is(((short) (1))));
    }

    @Test
    public void assertGetValueWithColumnIndexWithInt() throws SQLException {
        Mockito.when(resultSet.getInt(1)).thenReturn(1);
        Assert.assertThat(((int) (streamMergedResult.getValue(1, int.class))), CoreMatchers.is(1));
    }

    @Test
    public void assertGetValueWithColumnIndexWithLong() throws SQLException {
        Mockito.when(resultSet.getLong(1)).thenReturn(1L);
        Assert.assertThat(((long) (streamMergedResult.getValue(1, long.class))), CoreMatchers.is(1L));
    }

    @Test
    public void assertGetValueWithColumnIndexWithFloat() throws SQLException {
        Mockito.when(resultSet.getFloat(1)).thenReturn(1.0F);
        Assert.assertThat(((float) (streamMergedResult.getValue(1, float.class))), CoreMatchers.is(1.0F));
    }

    @Test
    public void assertGetValueWithColumnIndexWithDouble() throws SQLException {
        Mockito.when(resultSet.getDouble(1)).thenReturn(1.0);
        Assert.assertThat(((double) (streamMergedResult.getValue(1, double.class))), CoreMatchers.is(1.0));
    }

    @Test
    public void assertGetValueWithColumnIndexWithString() throws SQLException {
        Mockito.when(resultSet.getString(1)).thenReturn("1");
        Assert.assertThat(((String) (streamMergedResult.getValue(1, String.class))), CoreMatchers.is("1"));
    }

    @Test
    public void assertGetValueWithColumnIndexWithBigDecimal() throws SQLException {
        Mockito.when(resultSet.getBigDecimal(1)).thenReturn(new BigDecimal("1"));
        Assert.assertThat(((BigDecimal) (streamMergedResult.getValue(1, BigDecimal.class))), CoreMatchers.is(new BigDecimal("1")));
    }

    @Test
    public void assertGetValueWithColumnIndexWithByteArray() throws SQLException {
        Mockito.when(resultSet.getBytes(1)).thenReturn(new byte[]{ ((byte) (1)) });
        Assert.assertThat(((byte[]) (streamMergedResult.getValue(1, byte[].class))), CoreMatchers.is(new byte[]{ ((byte) (1)) }));
    }

    @Test
    public void assertGetValueWithColumnIndexWithDate() throws SQLException {
        Mockito.when(resultSet.getDate(1)).thenReturn(new Date(0L));
        Assert.assertThat(((Date) (streamMergedResult.getValue(1, Date.class))), CoreMatchers.is(new Date(0L)));
    }

    @Test
    public void assertGetValueWithColumnIndexWithTime() throws SQLException {
        Mockito.when(resultSet.getTime(1)).thenReturn(new Time(0L));
        Assert.assertThat(((Time) (streamMergedResult.getValue(1, Time.class))), CoreMatchers.is(new Time(0L)));
    }

    @Test
    public void assertGetValueWithColumnIndexWithTimestamp() throws SQLException {
        Mockito.when(resultSet.getTimestamp(1)).thenReturn(new Timestamp(0L));
        Assert.assertThat(((Timestamp) (streamMergedResult.getValue(1, Timestamp.class))), CoreMatchers.is(new Timestamp(0L)));
    }

    @Test
    public void assertGetValueWithColumnIndexWithURL() throws MalformedURLException, SQLException {
        Mockito.when(resultSet.getURL(1)).thenReturn(new URL("http://xxx.xxx"));
        Assert.assertThat(((URL) (streamMergedResult.getValue(1, URL.class))), CoreMatchers.is(new URL("http://xxx.xxx")));
    }

    @Test
    public void assertGetValueWithColumnIndexWithBlob() throws SQLException {
        Blob blob = Mockito.mock(Blob.class);
        Mockito.when(resultSet.getBlob(1)).thenReturn(blob);
        Assert.assertThat(((Blob) (streamMergedResult.getValue(1, Blob.class))), CoreMatchers.is(blob));
    }

    @Test
    public void assertGetValueWithColumnIndexWithClob() throws SQLException {
        Clob clob = Mockito.mock(Clob.class);
        Mockito.when(resultSet.getClob(1)).thenReturn(clob);
        Assert.assertThat(((Clob) (streamMergedResult.getValue(1, Clob.class))), CoreMatchers.is(clob));
    }

    @Test
    public void assertGetValueWithColumnIndexWithSQLXML() throws SQLException {
        SQLXML sqlxml = Mockito.mock(SQLXML.class);
        Mockito.when(resultSet.getSQLXML(1)).thenReturn(sqlxml);
        Assert.assertThat(((SQLXML) (streamMergedResult.getValue(1, SQLXML.class))), CoreMatchers.is(sqlxml));
    }

    @Test
    public void assertGetValueWithColumnIndexWithReader() throws SQLException {
        Reader reader = Mockito.mock(Reader.class);
        Mockito.when(resultSet.getCharacterStream(1)).thenReturn(reader);
        Assert.assertThat(((Reader) (streamMergedResult.getValue(1, Reader.class))), CoreMatchers.is(reader));
    }

    @Test
    public void assertGetValueWithColumnIndexWithOtherObject() throws SQLException {
        Mockito.when(resultSet.getObject(1)).thenReturn("1");
        Assert.assertThat(((String) (streamMergedResult.getValue(1, Collection.class))), CoreMatchers.is("1"));
    }

    @Test
    public void assertGetValueWithColumnLabelWithObject() throws SQLException {
        Mockito.when(resultSet.getObject("label")).thenReturn("1");
        Assert.assertThat(getValue("label", Object.class).toString(), CoreMatchers.is("1"));
    }

    @Test
    public void assertGetValueWithColumnLabelWithBoolean() throws SQLException {
        Mockito.when(resultSet.getBoolean("label")).thenReturn(true);
        Assert.assertTrue(((Boolean) (getValue("label", boolean.class))));
    }

    @Test
    public void assertGetValueWithColumnLabelWithByte() throws SQLException {
        Mockito.when(resultSet.getByte("label")).thenReturn(((byte) (1)));
        Assert.assertThat(((byte) (getValue("label", byte.class))), CoreMatchers.is(((byte) (1))));
    }

    @Test
    public void assertGetValueWithColumnLabelWithShort() throws SQLException {
        Mockito.when(resultSet.getShort("label")).thenReturn(((short) (1)));
        Assert.assertThat(((short) (getValue("label", short.class))), CoreMatchers.is(((short) (1))));
    }

    @Test
    public void assertGetValueWithColumnLabelWithInt() throws SQLException {
        Mockito.when(resultSet.getInt("label")).thenReturn(1);
        Assert.assertThat(((int) (getValue("label", int.class))), CoreMatchers.is(1));
    }

    @Test
    public void assertGetValueWithColumnLabelWithLong() throws SQLException {
        Mockito.when(resultSet.getLong("label")).thenReturn(1L);
        Assert.assertThat(((long) (getValue("label", long.class))), CoreMatchers.is(1L));
    }

    @Test
    public void assertGetValueWithColumnLabelWithFloat() throws SQLException {
        Mockito.when(resultSet.getFloat("label")).thenReturn(1.0F);
        Assert.assertThat(((float) (getValue("label", float.class))), CoreMatchers.is(1.0F));
    }

    @Test
    public void assertGetValueWithColumnLabelWithDouble() throws SQLException {
        Mockito.when(resultSet.getDouble("label")).thenReturn(1.0);
        Assert.assertThat(((double) (getValue("label", double.class))), CoreMatchers.is(1.0));
    }

    @Test
    public void assertGetValueWithColumnLabelWithString() throws SQLException {
        Mockito.when(resultSet.getString("label")).thenReturn("1");
        Assert.assertThat(((String) (getValue("label", String.class))), CoreMatchers.is("1"));
    }

    @Test
    public void assertGetValueWithColumnLabelWithBigDecimal() throws SQLException {
        Mockito.when(resultSet.getBigDecimal("label")).thenReturn(new BigDecimal("1"));
        Assert.assertThat(((BigDecimal) (getValue("label", BigDecimal.class))), CoreMatchers.is(new BigDecimal("1")));
    }

    @Test
    public void assertGetValueWithColumnLabelWithByteArray() throws SQLException {
        Mockito.when(resultSet.getBytes("label")).thenReturn(new byte[]{ ((byte) (1)) });
        Assert.assertThat(((byte[]) (getValue("label", byte[].class))), CoreMatchers.is(new byte[]{ ((byte) (1)) }));
    }

    @Test
    public void assertGetValueWithColumnLabelWithDate() throws SQLException {
        Mockito.when(resultSet.getDate("label")).thenReturn(new Date(0L));
        Assert.assertThat(((Date) (getValue("label", Date.class))), CoreMatchers.is(new Date(0L)));
    }

    @Test
    public void assertGetValueWithColumnLabelWithTime() throws SQLException {
        Mockito.when(resultSet.getTime("label")).thenReturn(new Time(0L));
        Assert.assertThat(((Time) (getValue("label", Time.class))), CoreMatchers.is(new Time(0L)));
    }

    @Test
    public void assertGetValueWithColumnLabelWithTimestamp() throws SQLException {
        Mockito.when(resultSet.getTimestamp("label")).thenReturn(new Timestamp(0L));
        Assert.assertThat(((Timestamp) (getValue("label", Timestamp.class))), CoreMatchers.is(new Timestamp(0L)));
    }

    @Test
    public void assertGetValueWithColumnLabelWithURL() throws MalformedURLException, SQLException {
        Mockito.when(resultSet.getURL("label")).thenReturn(new URL("http://xxx.xxx"));
        Assert.assertThat(((URL) (getValue("label", URL.class))), CoreMatchers.is(new URL("http://xxx.xxx")));
    }

    @Test
    public void assertGetValueWithColumnLabelWithBlob() throws SQLException {
        Blob blob = Mockito.mock(Blob.class);
        Mockito.when(resultSet.getBlob("label")).thenReturn(blob);
        Assert.assertThat(((Blob) (getValue("label", Blob.class))), CoreMatchers.is(blob));
    }

    @Test
    public void assertGetValueWithColumnLabelWithClob() throws SQLException {
        Clob clob = Mockito.mock(Clob.class);
        Mockito.when(resultSet.getClob("label")).thenReturn(clob);
        Assert.assertThat(((Clob) (getValue("label", Clob.class))), CoreMatchers.is(clob));
    }

    @Test
    public void assertGetValueWithColumnLabelWithSQLXML() throws SQLException {
        SQLXML sqlxml = Mockito.mock(SQLXML.class);
        Mockito.when(resultSet.getSQLXML("label")).thenReturn(sqlxml);
        Assert.assertThat(((SQLXML) (getValue("label", SQLXML.class))), CoreMatchers.is(sqlxml));
    }

    @Test
    public void assertGetValueWithColumnLabelWithReader() throws SQLException {
        Reader reader = Mockito.mock(Reader.class);
        Mockito.when(resultSet.getCharacterStream("label")).thenReturn(reader);
        Assert.assertThat(((Reader) (getValue("label", Reader.class))), CoreMatchers.is(reader));
    }

    @Test
    public void assertGetValueWithColumnLabelWithOtherObject() throws SQLException {
        Mockito.when(resultSet.getObject("label")).thenReturn("1");
        Assert.assertThat(((String) (getValue("label", Collection.class))), CoreMatchers.is("1"));
    }

    @Test
    public void assertGetCalendarValueWithColumnIndexWithDate() throws SQLException {
        Calendar calendar = Calendar.getInstance();
        Mockito.when(resultSet.getDate(1, calendar)).thenReturn(new Date(0L));
        Assert.assertThat(((Date) (streamMergedResult.getCalendarValue(1, Date.class, calendar))), CoreMatchers.is(new Date(0L)));
    }

    @Test
    public void assertGetCalendarValueWithColumnIndexWithTime() throws SQLException {
        Calendar calendar = Calendar.getInstance();
        Mockito.when(resultSet.getTime(1, calendar)).thenReturn(new Time(0L));
        Assert.assertThat(((Time) (streamMergedResult.getCalendarValue(1, Time.class, calendar))), CoreMatchers.is(new Time(0L)));
    }

    @Test
    public void assertGetCalendarValueWithColumnIndexWithTimestamp() throws SQLException {
        Calendar calendar = Calendar.getInstance();
        Mockito.when(resultSet.getTimestamp(1, calendar)).thenReturn(new Timestamp(0L));
        Assert.assertThat(((Timestamp) (streamMergedResult.getCalendarValue(1, Timestamp.class, calendar))), CoreMatchers.is(new Timestamp(0L)));
    }

    @Test(expected = SQLException.class)
    public void assertGetCalendarValueWithColumnIndexWithInvalidType() throws SQLException {
        streamMergedResult.getCalendarValue(1, Object.class, Calendar.getInstance());
    }

    @Test
    public void assertGetCalendarValueWithColumnLabelWithDate() throws SQLException {
        Calendar calendar = Calendar.getInstance();
        Mockito.when(resultSet.getDate("label", calendar)).thenReturn(new Date(0L));
        Assert.assertThat(((Date) (getCalendarValue("label", Date.class, calendar))), CoreMatchers.is(new Date(0L)));
    }

    @Test
    public void assertGetCalendarValueWithColumnLabelWithTime() throws SQLException {
        Calendar calendar = Calendar.getInstance();
        Mockito.when(resultSet.getTime("label", calendar)).thenReturn(new Time(0L));
        Assert.assertThat(((Time) (getCalendarValue("label", Time.class, calendar))), CoreMatchers.is(new Time(0L)));
    }

    @Test
    public void assertGetCalendarValueWithColumnLabelWithTimestamp() throws SQLException {
        Calendar calendar = Calendar.getInstance();
        Mockito.when(resultSet.getTimestamp("label", calendar)).thenReturn(new Timestamp(0L));
        Assert.assertThat(((Timestamp) (getCalendarValue("label", Timestamp.class, calendar))), CoreMatchers.is(new Timestamp(0L)));
    }

    @Test(expected = SQLException.class)
    public void assertGetCalendarValueWithColumnLabelWithInvalidType() throws SQLException {
        getCalendarValue("label", Object.class, Calendar.getInstance());
    }

    @Test
    public void assertGetInputStreamWithColumnIndexWithAscii() throws SQLException {
        InputStream inputStream = Mockito.mock(InputStream.class);
        Mockito.when(resultSet.getAsciiStream(1)).thenReturn(inputStream);
        Assert.assertThat(streamMergedResult.getInputStream(1, "Ascii"), CoreMatchers.is(inputStream));
    }

    @SuppressWarnings("deprecation")
    @Test
    public void assertGetInputStreamWithColumnIndexWithUnicode() throws SQLException {
        InputStream inputStream = Mockito.mock(InputStream.class);
        Mockito.when(resultSet.getUnicodeStream(1)).thenReturn(inputStream);
        Assert.assertThat(streamMergedResult.getInputStream(1, "Unicode"), CoreMatchers.is(inputStream));
    }

    @Test
    public void assertGetInputStreamWithColumnIndexWithBinary() throws SQLException {
        InputStream inputStream = Mockito.mock(InputStream.class);
        Mockito.when(resultSet.getBinaryStream(1)).thenReturn(inputStream);
        Assert.assertThat(streamMergedResult.getInputStream(1, "Binary"), CoreMatchers.is(inputStream));
    }

    @Test(expected = SQLException.class)
    public void assertGetInputStreamWithColumnIndexWithInvalidType() throws SQLException {
        streamMergedResult.getInputStream(1, "Invalid");
    }

    @Test
    public void assertGetInputStreamWithColumnLabelWithAscii() throws SQLException {
        InputStream inputStream = Mockito.mock(InputStream.class);
        Mockito.when(resultSet.getAsciiStream("label")).thenReturn(inputStream);
        Assert.assertThat(getInputStream("label", "Ascii"), CoreMatchers.is(inputStream));
    }

    @SuppressWarnings("deprecation")
    @Test
    public void assertGetInputStreamWithColumnLabelWithUnicode() throws SQLException {
        InputStream inputStream = Mockito.mock(InputStream.class);
        Mockito.when(resultSet.getUnicodeStream("label")).thenReturn(inputStream);
        Assert.assertThat(getInputStream("label", "Unicode"), CoreMatchers.is(inputStream));
    }

    @Test
    public void assertGetInputStreamWithColumnLabelWithBinary() throws SQLException {
        InputStream inputStream = Mockito.mock(InputStream.class);
        Mockito.when(resultSet.getBinaryStream("label")).thenReturn(inputStream);
        Assert.assertThat(getInputStream("label", "Binary"), CoreMatchers.is(inputStream));
    }

    @Test(expected = SQLException.class)
    public void assertGetInputStreamWithColumnLabelWithInvalidType() throws SQLException {
        getInputStream("label", "Invalid");
    }

    @Test
    public void assertWasNull() {
        Assert.assertFalse(wasNull());
    }
}

