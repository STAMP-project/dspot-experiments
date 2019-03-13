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
package org.apache.shardingsphere.shardingjdbc.jdbc.core.resultset;


import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.SQLXML;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import org.apache.shardingsphere.core.merger.MergedResult;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public final class ShardingResultSetTest {
    private MergedResult mergeResultSet;

    private ShardingResultSet shardingResultSet;

    @Test
    public void assertNext() throws SQLException {
        Mockito.when(mergeResultSet.next()).thenReturn(true);
        Assert.assertTrue(shardingResultSet.next());
    }

    @Test
    public void assertWasNull() throws SQLException {
        Assert.assertFalse(shardingResultSet.wasNull());
    }

    @Test
    public void assertGetBooleanWithColumnIndex() throws SQLException {
        Mockito.when(mergeResultSet.getValue(1, boolean.class)).thenReturn(true);
        Assert.assertTrue(shardingResultSet.getBoolean(1));
    }

    @Test
    public void assertGetBooleanWithColumnLabel() throws SQLException {
        Mockito.when(mergeResultSet.getValue("label", boolean.class)).thenReturn(true);
        Assert.assertTrue(shardingResultSet.getBoolean("label"));
    }

    @Test
    public void assertGetByteWithColumnIndex() throws SQLException {
        Mockito.when(mergeResultSet.getValue(1, byte.class)).thenReturn(((byte) (1)));
        Assert.assertThat(shardingResultSet.getByte(1), CoreMatchers.is(((byte) (1))));
    }

    @Test
    public void assertGetByteWithColumnLabel() throws SQLException {
        Mockito.when(mergeResultSet.getValue("label", byte.class)).thenReturn(((byte) (1)));
        Assert.assertThat(shardingResultSet.getByte("label"), CoreMatchers.is(((byte) (1))));
    }

    @Test
    public void assertGetShortWithColumnIndex() throws SQLException {
        Mockito.when(mergeResultSet.getValue(1, short.class)).thenReturn(((short) (1)));
        Assert.assertThat(shardingResultSet.getShort(1), CoreMatchers.is(((short) (1))));
    }

    @Test
    public void assertGetShortWithColumnLabel() throws SQLException {
        Mockito.when(mergeResultSet.getValue("label", short.class)).thenReturn(((short) (1)));
        Assert.assertThat(shardingResultSet.getShort("label"), CoreMatchers.is(((short) (1))));
    }

    @Test
    public void assertGetIntWithColumnIndex() throws SQLException {
        Mockito.when(mergeResultSet.getValue(1, int.class)).thenReturn(1);
        Assert.assertThat(shardingResultSet.getInt(1), CoreMatchers.is(1));
    }

    @Test
    public void assertGetIntWithColumnLabel() throws SQLException {
        Mockito.when(mergeResultSet.getValue("label", int.class)).thenReturn(((short) (1)));
        Assert.assertThat(shardingResultSet.getInt("label"), CoreMatchers.is(1));
    }

    @Test
    public void assertGetLongWithColumnIndex() throws SQLException {
        Mockito.when(mergeResultSet.getValue(1, long.class)).thenReturn(1L);
        Assert.assertThat(shardingResultSet.getLong(1), CoreMatchers.is(1L));
    }

    @Test
    public void assertGetLongWithColumnLabel() throws SQLException {
        Mockito.when(mergeResultSet.getValue("label", long.class)).thenReturn(1L);
        Assert.assertThat(shardingResultSet.getLong("label"), CoreMatchers.is(1L));
    }

    @Test
    public void assertGetFloatWithColumnIndex() throws SQLException {
        Mockito.when(mergeResultSet.getValue(1, float.class)).thenReturn(1.0F);
        Assert.assertThat(shardingResultSet.getFloat(1), CoreMatchers.is(1.0F));
    }

    @Test
    public void assertGetFloatWithColumnLabel() throws SQLException {
        Mockito.when(mergeResultSet.getValue("label", float.class)).thenReturn(1.0F);
        Assert.assertThat(shardingResultSet.getFloat("label"), CoreMatchers.is(1.0F));
    }

    @Test
    public void assertGetDoubleWithColumnIndex() throws SQLException {
        Mockito.when(mergeResultSet.getValue(1, double.class)).thenReturn(1.0);
        Assert.assertThat(shardingResultSet.getDouble(1), CoreMatchers.is(1.0));
    }

    @Test
    public void assertGetDoubleWithColumnLabel() throws SQLException {
        Mockito.when(mergeResultSet.getValue("label", double.class)).thenReturn(1.0);
        Assert.assertThat(shardingResultSet.getDouble("label"), CoreMatchers.is(1.0));
    }

    @Test
    public void assertGetStringWithColumnIndex() throws SQLException {
        Mockito.when(mergeResultSet.getValue(1, String.class)).thenReturn("value");
        Assert.assertThat(shardingResultSet.getString(1), CoreMatchers.is("value"));
    }

    @Test
    public void assertGetDoubleWithColumnLabelWithColumnLabelIndexMap() throws SQLException {
        Mockito.when(mergeResultSet.getValue("label", double.class)).thenReturn(1.0);
        Assert.assertThat(shardingResultSet.getDouble("label"), CoreMatchers.is(1.0));
    }

    @Test
    public void assertGetStringWithColumnLabel() throws SQLException {
        Mockito.when(mergeResultSet.getValue("label", String.class)).thenReturn("value");
        Assert.assertThat(shardingResultSet.getString("label"), CoreMatchers.is("value"));
    }

    @Test
    public void assertGetBigDecimalWithColumnIndex() throws SQLException {
        Mockito.when(mergeResultSet.getValue(1, BigDecimal.class)).thenReturn(new BigDecimal("1"));
        Assert.assertThat(shardingResultSet.getBigDecimal(1), CoreMatchers.is(new BigDecimal("1")));
    }

    @Test
    public void assertGetBigDecimalWithColumnLabel() throws SQLException {
        Mockito.when(mergeResultSet.getValue("label", BigDecimal.class)).thenReturn(new BigDecimal("1"));
        Assert.assertThat(shardingResultSet.getBigDecimal("label"), CoreMatchers.is(new BigDecimal("1")));
    }

    @Test
    public void assertGetBigDecimalAndScaleWithColumnIndex() throws SQLException {
        Mockito.when(mergeResultSet.getValue(1, BigDecimal.class)).thenReturn(new BigDecimal("1"));
        Assert.assertThat(shardingResultSet.getBigDecimal(1, 10), CoreMatchers.is(new BigDecimal("1")));
    }

    @Test
    public void assertGetBigDecimalAndScaleWithColumnLabel() throws SQLException {
        Mockito.when(mergeResultSet.getValue("label", BigDecimal.class)).thenReturn(new BigDecimal("1"));
        Assert.assertThat(shardingResultSet.getBigDecimal("label", 10), CoreMatchers.is(new BigDecimal("1")));
    }

    @Test
    public void assertGetBytesWithColumnIndex() throws SQLException {
        Mockito.when(mergeResultSet.getValue(1, byte[].class)).thenReturn(new byte[]{ ((byte) (1)) });
        Assert.assertThat(shardingResultSet.getBytes(1), CoreMatchers.is(new byte[]{ ((byte) (1)) }));
    }

    @Test
    public void assertGetBytesWithColumnLabel() throws SQLException {
        Mockito.when(mergeResultSet.getValue("label", byte[].class)).thenReturn(new byte[]{ ((byte) (1)) });
        Assert.assertThat(shardingResultSet.getBytes("label"), CoreMatchers.is(new byte[]{ ((byte) (1)) }));
    }

    @Test
    public void assertGetDateWithColumnIndex() throws SQLException {
        Mockito.when(mergeResultSet.getValue(1, Date.class)).thenReturn(new Date(0L));
        Assert.assertThat(shardingResultSet.getDate(1), CoreMatchers.is(new Date(0L)));
    }

    @Test
    public void assertGetDateWithColumnLabel() throws SQLException {
        Mockito.when(mergeResultSet.getValue("label", Date.class)).thenReturn(new Date(0L));
        Assert.assertThat(shardingResultSet.getDate("label"), CoreMatchers.is(new Date(0L)));
    }

    @Test
    public void assertGetDateAndCalendarWithColumnIndex() throws SQLException {
        Calendar calendar = Calendar.getInstance();
        Mockito.when(mergeResultSet.getCalendarValue(1, Date.class, calendar)).thenReturn(new Date(0L));
        Assert.assertThat(shardingResultSet.getDate(1, calendar), CoreMatchers.is(new Date(0L)));
    }

    @Test
    public void assertGetDateAndCalendarWithColumnLabel() throws SQLException {
        Calendar calendar = Calendar.getInstance();
        Mockito.when(mergeResultSet.getCalendarValue("label", Date.class, calendar)).thenReturn(new Date(0L));
        Assert.assertThat(shardingResultSet.getDate("label", calendar), CoreMatchers.is(new Date(0L)));
    }

    @Test
    public void assertGetTimeWithColumnIndex() throws SQLException {
        Mockito.when(mergeResultSet.getValue(1, Time.class)).thenReturn(new Time(0L));
        Assert.assertThat(shardingResultSet.getTime(1), CoreMatchers.is(new Time(0L)));
    }

    @Test
    public void assertGetTimeWithColumnLabel() throws SQLException {
        Mockito.when(mergeResultSet.getValue("label", Time.class)).thenReturn(new Time(0L));
        Assert.assertThat(shardingResultSet.getTime("label"), CoreMatchers.is(new Time(0L)));
    }

    @Test
    public void assertGetTimeAndCalendarWithColumnIndex() throws SQLException {
        Calendar calendar = Calendar.getInstance();
        Mockito.when(mergeResultSet.getCalendarValue(1, Time.class, calendar)).thenReturn(new Time(0L));
        Assert.assertThat(shardingResultSet.getTime(1, calendar), CoreMatchers.is(new Time(0L)));
    }

    @Test
    public void assertGetTimeAndCalendarWithColumnLabel() throws SQLException {
        Calendar calendar = Calendar.getInstance();
        Mockito.when(mergeResultSet.getCalendarValue("label", Time.class, calendar)).thenReturn(new Time(0L));
        Assert.assertThat(shardingResultSet.getTime("label", calendar), CoreMatchers.is(new Time(0L)));
    }

    @Test
    public void assertGetTimestampWithColumnIndex() throws SQLException {
        Mockito.when(mergeResultSet.getValue(1, Timestamp.class)).thenReturn(new Timestamp(0L));
        Assert.assertThat(shardingResultSet.getTimestamp(1), CoreMatchers.is(new Timestamp(0L)));
    }

    @Test
    public void assertGetTimestampWithColumnLabel() throws SQLException {
        Mockito.when(mergeResultSet.getValue("label", Timestamp.class)).thenReturn(new Timestamp(0L));
        Assert.assertThat(shardingResultSet.getTimestamp("label"), CoreMatchers.is(new Timestamp(0L)));
    }

    @Test
    public void assertGetTimestampAndCalendarWithColumnIndex() throws SQLException {
        Calendar calendar = Calendar.getInstance();
        Mockito.when(mergeResultSet.getCalendarValue(1, Timestamp.class, calendar)).thenReturn(new Timestamp(0L));
        Assert.assertThat(shardingResultSet.getTimestamp(1, calendar), CoreMatchers.is(new Timestamp(0L)));
    }

    @Test
    public void assertGetTimestampAndCalendarWithColumnLabel() throws SQLException {
        Calendar calendar = Calendar.getInstance();
        Mockito.when(mergeResultSet.getCalendarValue("label", Timestamp.class, calendar)).thenReturn(new Timestamp(0L));
        Assert.assertThat(shardingResultSet.getTimestamp("label", calendar), CoreMatchers.is(new Timestamp(0L)));
    }

    @Test
    public void assertGetAsciiStreamWithColumnIndex() throws SQLException {
        InputStream inputStream = Mockito.mock(InputStream.class);
        Mockito.when(mergeResultSet.getInputStream(1, "Ascii")).thenReturn(inputStream);
        Assert.assertThat(shardingResultSet.getAsciiStream(1), CoreMatchers.instanceOf(InputStream.class));
    }

    @Test
    public void assertGetAsciiStreamWithColumnLabel() throws SQLException {
        InputStream inputStream = Mockito.mock(InputStream.class);
        Mockito.when(mergeResultSet.getInputStream("label", "Ascii")).thenReturn(inputStream);
        Assert.assertThat(shardingResultSet.getAsciiStream("label"), CoreMatchers.instanceOf(InputStream.class));
    }

    @Test
    public void assertGetUnicodeStreamWithColumnIndex() throws SQLException {
        InputStream inputStream = Mockito.mock(InputStream.class);
        Mockito.when(mergeResultSet.getInputStream(1, "Unicode")).thenReturn(inputStream);
        Assert.assertThat(shardingResultSet.getUnicodeStream(1), CoreMatchers.instanceOf(InputStream.class));
    }

    @Test
    public void assertGetUnicodeStreamWithColumnLabel() throws SQLException {
        InputStream inputStream = Mockito.mock(InputStream.class);
        Mockito.when(mergeResultSet.getInputStream("label", "Unicode")).thenReturn(inputStream);
        Assert.assertThat(shardingResultSet.getUnicodeStream("label"), CoreMatchers.instanceOf(InputStream.class));
    }

    @Test
    public void assertGetBinaryStreamWithColumnIndex() throws SQLException {
        InputStream inputStream = Mockito.mock(InputStream.class);
        Mockito.when(mergeResultSet.getInputStream(1, "Binary")).thenReturn(inputStream);
        Assert.assertThat(shardingResultSet.getBinaryStream(1), CoreMatchers.instanceOf(InputStream.class));
    }

    @Test
    public void assertGetBinaryStreamWithColumnLabel() throws SQLException {
        InputStream inputStream = Mockito.mock(InputStream.class);
        Mockito.when(mergeResultSet.getInputStream("label", "Binary")).thenReturn(inputStream);
        Assert.assertThat(shardingResultSet.getBinaryStream("label"), CoreMatchers.instanceOf(InputStream.class));
    }

    @Test
    public void assertGetCharacterStreamWithColumnIndex() throws SQLException {
        Reader reader = Mockito.mock(Reader.class);
        Mockito.when(mergeResultSet.getValue(1, Reader.class)).thenReturn(reader);
        Assert.assertThat(shardingResultSet.getCharacterStream(1), CoreMatchers.is(reader));
    }

    @Test
    public void assertGetCharacterStreamWithColumnLabel() throws SQLException {
        Reader reader = Mockito.mock(Reader.class);
        Mockito.when(mergeResultSet.getValue("label", Reader.class)).thenReturn(reader);
        Assert.assertThat(shardingResultSet.getCharacterStream("label"), CoreMatchers.is(reader));
    }

    @Test
    public void assertGetBlobWithColumnIndex() throws SQLException {
        Blob blob = Mockito.mock(Blob.class);
        Mockito.when(mergeResultSet.getValue(1, Blob.class)).thenReturn(blob);
        Assert.assertThat(shardingResultSet.getBlob(1), CoreMatchers.is(blob));
    }

    @Test
    public void assertGetBlobWithColumnLabel() throws SQLException {
        Blob blob = Mockito.mock(Blob.class);
        Mockito.when(mergeResultSet.getValue("label", Blob.class)).thenReturn(blob);
        Assert.assertThat(shardingResultSet.getBlob("label"), CoreMatchers.is(blob));
    }

    @Test
    public void assertGetClobWithColumnIndex() throws SQLException {
        Clob clob = Mockito.mock(Clob.class);
        Mockito.when(mergeResultSet.getValue(1, Clob.class)).thenReturn(clob);
        Assert.assertThat(shardingResultSet.getClob(1), CoreMatchers.is(clob));
    }

    @Test
    public void assertGetClobWithColumnLabel() throws SQLException {
        Clob clob = Mockito.mock(Clob.class);
        Mockito.when(mergeResultSet.getValue("label", Clob.class)).thenReturn(clob);
        Assert.assertThat(shardingResultSet.getClob("label"), CoreMatchers.is(clob));
    }

    @Test
    public void assertGetURLWithColumnIndex() throws MalformedURLException, SQLException {
        Mockito.when(mergeResultSet.getValue(1, URL.class)).thenReturn(new URL("http://xxx.xxx"));
        Assert.assertThat(shardingResultSet.getURL(1), CoreMatchers.is(new URL("http://xxx.xxx")));
    }

    @Test
    public void assertGetURLWithColumnLabel() throws MalformedURLException, SQLException {
        Mockito.when(mergeResultSet.getValue("label", URL.class)).thenReturn(new URL("http://xxx.xxx"));
        Assert.assertThat(shardingResultSet.getURL("label"), CoreMatchers.is(new URL("http://xxx.xxx")));
    }

    @Test
    public void assertGetSQLXMLWithColumnIndex() throws SQLException {
        SQLXML sqlxml = Mockito.mock(SQLXML.class);
        Mockito.when(mergeResultSet.getValue(1, SQLXML.class)).thenReturn(sqlxml);
        Assert.assertThat(shardingResultSet.getSQLXML(1), CoreMatchers.is(sqlxml));
    }

    @Test
    public void assertGetSQLXMLWithColumnLabel() throws SQLException {
        SQLXML sqlxml = Mockito.mock(SQLXML.class);
        Mockito.when(mergeResultSet.getValue("label", SQLXML.class)).thenReturn(sqlxml);
        Assert.assertThat(shardingResultSet.getSQLXML("label"), CoreMatchers.is(sqlxml));
    }

    @Test
    public void assertGetObjectWithColumnIndex() throws SQLException {
        Mockito.when(mergeResultSet.getValue(1, Object.class)).thenReturn("object_value");
        Assert.assertThat(shardingResultSet.getObject(1), CoreMatchers.is(((Object) ("object_value"))));
    }

    @Test
    public void assertGetObjectWithColumnLabel() throws SQLException {
        Mockito.when(mergeResultSet.getValue("label", Object.class)).thenReturn("object_value");
        Assert.assertThat(shardingResultSet.getObject("label"), CoreMatchers.is(((Object) ("object_value"))));
    }
}

