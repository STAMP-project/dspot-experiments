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


import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import javax.sql.rowset.RowSetMetaDataImpl;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class GeneratedKeysResultSetMetaDataTest {
    private ResultSetMetaData actualMetaData;

    @Test
    public void getColumnCount() throws SQLException {
        Assert.assertThat(actualMetaData.getColumnCount(), CoreMatchers.is(1));
    }

    @Test
    public void isAutoIncrement() throws SQLException {
        Assert.assertTrue(actualMetaData.isAutoIncrement(1));
    }

    @Test
    public void isCaseSensitive() throws SQLException {
        Assert.assertTrue(actualMetaData.isCaseSensitive(1));
    }

    @Test
    public void isSearchable() throws SQLException {
        Assert.assertFalse(actualMetaData.isSearchable(1));
    }

    @Test
    public void isCurrency() throws SQLException {
        Assert.assertFalse(actualMetaData.isCurrency(1));
    }

    @Test
    public void isNullable() throws SQLException {
        Assert.assertThat(actualMetaData.isNullable(1), CoreMatchers.is(ResultSetMetaData.columnNoNulls));
    }

    @Test
    public void isSigned() throws SQLException {
        Assert.assertTrue(actualMetaData.isSigned(1));
    }

    @Test
    public void getColumnDisplaySize() throws SQLException {
        Assert.assertThat(actualMetaData.getColumnDisplaySize(1), CoreMatchers.is(0));
    }

    @Test
    public void getColumnLabel() throws SQLException {
        Assert.assertThat(actualMetaData.getColumnLabel(1), CoreMatchers.is("order_id"));
    }

    @Test
    public void getColumnName() throws SQLException {
        Assert.assertThat(actualMetaData.getColumnName(1), CoreMatchers.is("order_id"));
    }

    @Test
    public void getSchemaName() throws SQLException {
        Assert.assertThat(actualMetaData.getSchemaName(1), CoreMatchers.is(""));
    }

    @Test
    public void getPrecision() throws SQLException {
        Assert.assertThat(actualMetaData.getPrecision(1), CoreMatchers.is(0));
    }

    @Test
    public void getScale() throws SQLException {
        Assert.assertThat(actualMetaData.getScale(1), CoreMatchers.is(0));
    }

    @Test
    public void getTableName() throws SQLException {
        Assert.assertThat(actualMetaData.getTableName(1), CoreMatchers.is(""));
    }

    @Test
    public void getCatalogName() throws SQLException {
        Assert.assertThat(actualMetaData.getCatalogName(1), CoreMatchers.is(""));
    }

    @Test
    public void getColumnType() throws SQLException {
        Assert.assertThat(actualMetaData.getColumnType(1), CoreMatchers.is(Types.BIGINT));
    }

    @Test
    public void getColumnTypeName() throws SQLException {
        Assert.assertThat(actualMetaData.getColumnTypeName(1), CoreMatchers.is(""));
    }

    @Test
    public void isReadOnly() throws SQLException {
        Assert.assertTrue(actualMetaData.isReadOnly(1));
    }

    @Test
    public void isWritable() throws SQLException {
        Assert.assertFalse(actualMetaData.isWritable(1));
    }

    @Test
    public void isDefinitelyWritable() throws SQLException {
        Assert.assertFalse(actualMetaData.isDefinitelyWritable(1));
    }

    @Test
    public void getColumnClassName() throws SQLException {
        Assert.assertThat(actualMetaData.getColumnClassName(1), CoreMatchers.is("java.lang.Number"));
    }

    @Test
    public void unwrap() throws SQLException {
        Assert.assertThat(actualMetaData.unwrap(GeneratedKeysResultSetMetaData.class), CoreMatchers.is(((GeneratedKeysResultSetMetaData) (actualMetaData))));
    }

    @Test(expected = SQLException.class)
    public void unwrapError() throws SQLException {
        actualMetaData.unwrap(RowSetMetaDataImpl.class);
    }

    @Test
    public void isWrapperFor() throws SQLException {
        Assert.assertTrue(actualMetaData.isWrapperFor(GeneratedKeysResultSetMetaData.class));
    }
}

