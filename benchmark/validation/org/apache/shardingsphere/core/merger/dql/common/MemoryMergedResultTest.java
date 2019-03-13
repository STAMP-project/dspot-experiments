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
import java.sql.Blob;
import java.sql.Clob;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.sql.SQLXML;
import java.util.Calendar;
import java.util.Date;
import org.apache.shardingsphere.core.merger.dql.common.fixture.TestMemoryMergedResult;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public final class MemoryMergedResultTest {
    @Mock
    private MemoryQueryResultRow memoryResultSetRow;

    private TestMemoryMergedResult memoryMergedResult;

    @Test
    public void assertGetValueWithColumnIndex() throws SQLException {
        Mockito.when(memoryResultSetRow.getCell(1)).thenReturn("1");
        Assert.assertThat(memoryMergedResult.getValue(1, Object.class).toString(), CoreMatchers.is("1"));
    }

    @Test(expected = SQLFeatureNotSupportedException.class)
    public void assertGetValueWithColumnIndexForBlob() throws SQLException {
        memoryMergedResult.getValue(1, Blob.class);
    }

    @Test(expected = SQLFeatureNotSupportedException.class)
    public void assertGetValueWithColumnIndexForClob() throws SQLException {
        memoryMergedResult.getValue(1, Clob.class);
    }

    @Test(expected = SQLFeatureNotSupportedException.class)
    public void assertGetValueWithColumnIndexForReader() throws SQLException {
        memoryMergedResult.getValue(1, Reader.class);
    }

    @Test(expected = SQLFeatureNotSupportedException.class)
    public void assertGetValueWithColumnIndexForInputStream() throws SQLException {
        memoryMergedResult.getValue(1, InputStream.class);
    }

    @Test(expected = SQLFeatureNotSupportedException.class)
    public void assertGetValueWithColumnIndexForSQLXML() throws SQLException {
        memoryMergedResult.getValue(1, SQLXML.class);
    }

    @Test
    public void assertGetValueWithColumnLabel() throws SQLException {
        Mockito.when(memoryResultSetRow.getCell(1)).thenReturn("1");
        Assert.assertThat(getValue("label", Object.class).toString(), CoreMatchers.is("1"));
    }

    @Test(expected = SQLFeatureNotSupportedException.class)
    public void assertGetValueWithColumnLabelForBlob() throws SQLException {
        getValue("label", Blob.class);
    }

    @Test(expected = SQLFeatureNotSupportedException.class)
    public void assertGetValueWithColumnLabelForClob() throws SQLException {
        getValue("label", Clob.class);
    }

    @Test(expected = SQLFeatureNotSupportedException.class)
    public void assertGetValueWithColumnLabelForReader() throws SQLException {
        getValue("label", Reader.class);
    }

    @Test(expected = SQLFeatureNotSupportedException.class)
    public void assertGetValueWithColumnLabelForInputStream() throws SQLException {
        getValue("label", InputStream.class);
    }

    @Test(expected = SQLFeatureNotSupportedException.class)
    public void assertGetValueWithColumnLabelForSQLXML() throws SQLException {
        getValue("label", SQLXML.class);
    }

    @Test
    public void assertGetCalendarValueWithColumnIndex() {
        Mockito.when(memoryResultSetRow.getCell(1)).thenReturn(new Date(0L));
        Assert.assertThat(((Date) (memoryMergedResult.getCalendarValue(1, Object.class, Calendar.getInstance()))), CoreMatchers.is(new Date(0L)));
    }

    @Test
    public void assertGetCalendarValueWithColumnLabel() {
        Mockito.when(memoryResultSetRow.getCell(1)).thenReturn(new Date(0L));
        Assert.assertThat(((Date) (getCalendarValue("label", Object.class, Calendar.getInstance()))), CoreMatchers.is(new Date(0L)));
    }

    @Test(expected = SQLFeatureNotSupportedException.class)
    public void assertGetInputStreamWithColumnIndex() throws SQLException {
        memoryMergedResult.getInputStream(1, "ascii");
    }

    @Test(expected = SQLFeatureNotSupportedException.class)
    public void assertGetInputStreamWithColumnLabel() throws SQLException {
        getInputStream("label", "ascii");
    }

    @Test
    public void assertWasNull() {
        Assert.assertFalse(wasNull());
    }
}

