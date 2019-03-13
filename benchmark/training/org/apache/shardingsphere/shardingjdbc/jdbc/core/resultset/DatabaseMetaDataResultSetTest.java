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


import java.sql.Date;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class DatabaseMetaDataResultSetTest {
    private static final String TABLE_NAME_COLUMN_LABEL = "TABLE_NAME";

    private static final String ACTUAL_TABLE_NAME = "test_table_0";

    private static final String LOGIC_TABLE_NAME = "test_table";

    private static final String NON_TABLE_NAME_COLUMN_LABEL = "NON_TABLE_NAME";

    private static final boolean NON_TABLE_NAME = true;

    private static final String NUMBER_COLUMN_LABEL = "NUMBER";

    private static final int NUMBER = 100;

    private static final String BYTES_COLUMN_LABEL = "BYTES";

    private static final byte[] BYTES = DatabaseMetaDataResultSetTest.LOGIC_TABLE_NAME.getBytes();

    private static final String DATE_COLUMN_LABEL = "DATE";

    private static final Date DATE = new Date(System.currentTimeMillis());

    @Mock
    private ResultSetMetaData resultSetMetaData;

    private DatabaseMetaDataResultSet databaseMetaDataResultSet;

    @Test
    public void assertNext() throws Exception {
        Assert.assertTrue(databaseMetaDataResultSet.next());
        Assert.assertFalse(databaseMetaDataResultSet.next());
    }

    @Test
    public void assertClose() throws Exception {
        Assert.assertFalse(databaseMetaDataResultSet.isClosed());
        databaseMetaDataResultSet.close();
        Assert.assertTrue(databaseMetaDataResultSet.isClosed());
    }

    @Test
    public void assertWasNull() throws Exception {
        Assert.assertFalse(databaseMetaDataResultSet.wasNull());
    }

    @Test
    public void assertGetStringWithIndex() throws Exception {
        databaseMetaDataResultSet.next();
        Assert.assertThat(databaseMetaDataResultSet.getString(1), CoreMatchers.is(DatabaseMetaDataResultSetTest.LOGIC_TABLE_NAME));
        Assert.assertThat(databaseMetaDataResultSet.getString(2), CoreMatchers.is("true"));
        Assert.assertThat(databaseMetaDataResultSet.getString(3), CoreMatchers.is("100"));
    }

    @Test
    public void assertGetStringWithLabel() throws Exception {
        databaseMetaDataResultSet.next();
        Assert.assertThat(databaseMetaDataResultSet.getString(DatabaseMetaDataResultSetTest.TABLE_NAME_COLUMN_LABEL), CoreMatchers.is(DatabaseMetaDataResultSetTest.LOGIC_TABLE_NAME));
        Assert.assertThat(databaseMetaDataResultSet.getString(DatabaseMetaDataResultSetTest.NON_TABLE_NAME_COLUMN_LABEL), CoreMatchers.is("true"));
        Assert.assertThat(databaseMetaDataResultSet.getString(DatabaseMetaDataResultSetTest.NUMBER_COLUMN_LABEL), CoreMatchers.is("100"));
    }

    @Test
    public void assertGetBooleanWithIndex() throws Exception {
        databaseMetaDataResultSet.next();
        Assert.assertTrue(databaseMetaDataResultSet.getBoolean(2));
    }

    @Test
    public void assertGetBooleanWithLabel() throws Exception {
        databaseMetaDataResultSet.next();
        Assert.assertTrue(databaseMetaDataResultSet.getBoolean(DatabaseMetaDataResultSetTest.NON_TABLE_NAME_COLUMN_LABEL));
    }

    @Test
    public void assertGetByteWithIndex() throws Exception {
        databaseMetaDataResultSet.next();
        Assert.assertThat(databaseMetaDataResultSet.getByte(3), CoreMatchers.is(((byte) (DatabaseMetaDataResultSetTest.NUMBER))));
    }

    @Test
    public void assertGetByteWithLabel() throws Exception {
        databaseMetaDataResultSet.next();
        Assert.assertThat(databaseMetaDataResultSet.getByte(DatabaseMetaDataResultSetTest.NUMBER_COLUMN_LABEL), CoreMatchers.is(((byte) (DatabaseMetaDataResultSetTest.NUMBER))));
    }

    @Test
    public void assertGetShortWithIndex() throws Exception {
        databaseMetaDataResultSet.next();
        Assert.assertThat(databaseMetaDataResultSet.getShort(3), CoreMatchers.is(((short) (DatabaseMetaDataResultSetTest.NUMBER))));
    }

    @Test
    public void assertGetShortWithLabel() throws Exception {
        databaseMetaDataResultSet.next();
        Assert.assertThat(databaseMetaDataResultSet.getShort(DatabaseMetaDataResultSetTest.NUMBER_COLUMN_LABEL), CoreMatchers.is(((short) (DatabaseMetaDataResultSetTest.NUMBER))));
    }

    @Test
    public void assertGetIntWithIndex() throws Exception {
        databaseMetaDataResultSet.next();
        Assert.assertThat(databaseMetaDataResultSet.getInt(3), CoreMatchers.is(DatabaseMetaDataResultSetTest.NUMBER));
    }

    @Test
    public void assertGetIntWithLabel() throws Exception {
        databaseMetaDataResultSet.next();
        Assert.assertThat(databaseMetaDataResultSet.getInt(DatabaseMetaDataResultSetTest.NUMBER_COLUMN_LABEL), CoreMatchers.is(DatabaseMetaDataResultSetTest.NUMBER));
    }

    @Test
    public void assertGetLongWithIndex() throws Exception {
        databaseMetaDataResultSet.next();
        Assert.assertThat(databaseMetaDataResultSet.getLong(3), CoreMatchers.is(((long) (DatabaseMetaDataResultSetTest.NUMBER))));
    }

    @Test
    public void assertGetLongWithLabel() throws Exception {
        databaseMetaDataResultSet.next();
        Assert.assertThat(databaseMetaDataResultSet.getLong(DatabaseMetaDataResultSetTest.NUMBER_COLUMN_LABEL), CoreMatchers.is(((long) (DatabaseMetaDataResultSetTest.NUMBER))));
    }

    @Test
    public void assertGetFloatWithIndex() throws Exception {
        databaseMetaDataResultSet.next();
        Assert.assertThat(databaseMetaDataResultSet.getFloat(3), CoreMatchers.is(((float) (DatabaseMetaDataResultSetTest.NUMBER))));
    }

    @Test
    public void assertGetFloatWithLabel() throws Exception {
        databaseMetaDataResultSet.next();
        Assert.assertThat(databaseMetaDataResultSet.getFloat(DatabaseMetaDataResultSetTest.NUMBER_COLUMN_LABEL), CoreMatchers.is(((float) (DatabaseMetaDataResultSetTest.NUMBER))));
    }

    @Test
    public void assertGetDoubleWithIndex() throws Exception {
        databaseMetaDataResultSet.next();
        Assert.assertThat(databaseMetaDataResultSet.getDouble(3), CoreMatchers.is(((double) (DatabaseMetaDataResultSetTest.NUMBER))));
    }

    @Test
    public void assertGetDoubleWithLabel() throws Exception {
        databaseMetaDataResultSet.next();
        Assert.assertThat(databaseMetaDataResultSet.getDouble(DatabaseMetaDataResultSetTest.NUMBER_COLUMN_LABEL), CoreMatchers.is(((double) (DatabaseMetaDataResultSetTest.NUMBER))));
    }

    @Test
    public void assertGetBytesWithIndex() throws Exception {
        databaseMetaDataResultSet.next();
        Assert.assertThat(databaseMetaDataResultSet.getBytes(4), CoreMatchers.is(DatabaseMetaDataResultSetTest.BYTES));
    }

    @Test
    public void assertGetBytesWithLabel() throws Exception {
        databaseMetaDataResultSet.next();
        Assert.assertThat(databaseMetaDataResultSet.getBytes(DatabaseMetaDataResultSetTest.BYTES_COLUMN_LABEL), CoreMatchers.is(DatabaseMetaDataResultSetTest.BYTES));
    }

    @Test
    public void assertGetDateWithIndex() throws Exception {
        databaseMetaDataResultSet.next();
        Assert.assertThat(databaseMetaDataResultSet.getDate(5), CoreMatchers.is(DatabaseMetaDataResultSetTest.DATE));
    }

    @Test
    public void assertGetDateWithLabel() throws Exception {
        databaseMetaDataResultSet.next();
        Assert.assertThat(databaseMetaDataResultSet.getDate(DatabaseMetaDataResultSetTest.DATE_COLUMN_LABEL), CoreMatchers.is(DatabaseMetaDataResultSetTest.DATE));
    }

    @Test
    public void assertGetTimeWithIndex() throws Exception {
        databaseMetaDataResultSet.next();
        Assert.assertThat(databaseMetaDataResultSet.getTime(5), CoreMatchers.is(new Time(DatabaseMetaDataResultSetTest.DATE.getTime())));
    }

    @Test
    public void assertGetTimeWithLabel() throws Exception {
        databaseMetaDataResultSet.next();
        Assert.assertThat(databaseMetaDataResultSet.getTime(DatabaseMetaDataResultSetTest.DATE_COLUMN_LABEL), CoreMatchers.is(new Time(DatabaseMetaDataResultSetTest.DATE.getTime())));
    }

    @Test
    public void assertGetTimestampWithIndex() throws Exception {
        databaseMetaDataResultSet.next();
        Assert.assertThat(databaseMetaDataResultSet.getTimestamp(5), CoreMatchers.is(new Timestamp(DatabaseMetaDataResultSetTest.DATE.getTime())));
    }

    @Test
    public void assertGetTimestampWithLabel() throws Exception {
        databaseMetaDataResultSet.next();
        Assert.assertThat(databaseMetaDataResultSet.getTimestamp(DatabaseMetaDataResultSetTest.DATE_COLUMN_LABEL), CoreMatchers.is(new Timestamp(DatabaseMetaDataResultSetTest.DATE.getTime())));
    }

    @Test
    public void assertGetMetaData() throws Exception {
        Assert.assertThat(databaseMetaDataResultSet.getMetaData(), CoreMatchers.is(resultSetMetaData));
    }

    @Test
    public void assertGetObjectWithIndex() throws Exception {
        databaseMetaDataResultSet.next();
        Assert.assertTrue(databaseMetaDataResultSet.getObject(1).equals(DatabaseMetaDataResultSetTest.LOGIC_TABLE_NAME));
        Assert.assertTrue(databaseMetaDataResultSet.getObject(2).equals(DatabaseMetaDataResultSetTest.NON_TABLE_NAME));
        Assert.assertTrue(databaseMetaDataResultSet.getObject(3).equals(DatabaseMetaDataResultSetTest.NUMBER));
    }

    @Test
    public void assertGetObjectWithLabel() throws Exception {
        databaseMetaDataResultSet.next();
        Assert.assertTrue(databaseMetaDataResultSet.getObject(DatabaseMetaDataResultSetTest.TABLE_NAME_COLUMN_LABEL).equals(DatabaseMetaDataResultSetTest.LOGIC_TABLE_NAME));
        Assert.assertTrue(databaseMetaDataResultSet.getObject(DatabaseMetaDataResultSetTest.NON_TABLE_NAME_COLUMN_LABEL).equals(DatabaseMetaDataResultSetTest.NON_TABLE_NAME));
        Assert.assertTrue(databaseMetaDataResultSet.getObject(DatabaseMetaDataResultSetTest.NUMBER_COLUMN_LABEL).equals(DatabaseMetaDataResultSetTest.NUMBER));
    }

    @Test
    public void assertFindColumn() throws Exception {
        Assert.assertThat(databaseMetaDataResultSet.findColumn(DatabaseMetaDataResultSetTest.TABLE_NAME_COLUMN_LABEL), CoreMatchers.is(1));
        Assert.assertThat(databaseMetaDataResultSet.findColumn(DatabaseMetaDataResultSetTest.NON_TABLE_NAME_COLUMN_LABEL), CoreMatchers.is(2));
        Assert.assertThat(databaseMetaDataResultSet.findColumn(DatabaseMetaDataResultSetTest.NUMBER_COLUMN_LABEL), CoreMatchers.is(3));
    }

    @Test
    public void assertGetType() throws Exception {
        Assert.assertThat(databaseMetaDataResultSet.getType(), CoreMatchers.is(ResultSet.TYPE_FORWARD_ONLY));
    }

    @Test
    public void assertGetConcurrency() throws Exception {
        Assert.assertThat(databaseMetaDataResultSet.getConcurrency(), CoreMatchers.is(ResultSet.CONCUR_READ_ONLY));
    }

    @Test(expected = SQLException.class)
    public void assertGetObjectOutOfIndexRange() throws SQLException {
        databaseMetaDataResultSet.next();
        databaseMetaDataResultSet.getObject(6);
    }

    @Test(expected = SQLException.class)
    public void assertGetObjectInvalidLabel() throws SQLException {
        databaseMetaDataResultSet.next();
        databaseMetaDataResultSet.getObject("Invalid");
    }

    @Test(expected = SQLException.class)
    public void assertOperationWithClose() throws SQLException {
        databaseMetaDataResultSet.close();
        databaseMetaDataResultSet.next();
        databaseMetaDataResultSet.getObject(1);
    }
}

