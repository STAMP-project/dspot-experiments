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


import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.Statement;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class GeneratedKeysResultSetTest {
    private static final Statement STATEMENT = Mockito.mock(Statement.class);

    private GeneratedKeysResultSet actualResultSet;

    @Test
    public void assertNext() {
        Assert.assertTrue(actualResultSet.next());
        Assert.assertTrue(actualResultSet.next());
        Assert.assertFalse(actualResultSet.next());
    }

    @Test
    public void assertNextForEmptyResultSet() {
        GeneratedKeysResultSet actual = new GeneratedKeysResultSet();
        Assert.assertFalse(actual.next());
    }

    @Test
    public void assertClose() {
        actualResultSet.close();
        Assert.assertTrue(actualResultSet.isClosed());
    }

    @Test(expected = IllegalStateException.class)
    public void assertThrowExceptionWhenInvokeClosedResultSet() {
        actualResultSet.close();
        actualResultSet.getType();
    }

    @Test
    public void assertWasNull() {
        Assert.assertFalse(actualResultSet.wasNull());
    }

    @Test
    public void assertGetString() {
        Assert.assertTrue(actualResultSet.next());
        Assert.assertThat(actualResultSet.getString(1), CoreMatchers.is("1"));
        Assert.assertTrue(actualResultSet.next());
        Assert.assertThat(actualResultSet.getString("order_id"), CoreMatchers.is("2"));
        Assert.assertFalse(actualResultSet.next());
    }

    @Test
    public void assertGetByte() {
        Assert.assertTrue(actualResultSet.next());
        Assert.assertThat(actualResultSet.getByte(1), CoreMatchers.is(((byte) (1L))));
        Assert.assertTrue(actualResultSet.next());
        Assert.assertThat(actualResultSet.getByte("order_id"), CoreMatchers.is(((byte) (2L))));
        Assert.assertFalse(actualResultSet.next());
    }

    @Test
    public void assertGetShort() {
        Assert.assertTrue(actualResultSet.next());
        Assert.assertThat(actualResultSet.getShort(1), CoreMatchers.is(((short) (1L))));
        Assert.assertTrue(actualResultSet.next());
        Assert.assertThat(actualResultSet.getShort("order_id"), CoreMatchers.is(((short) (2L))));
        Assert.assertFalse(actualResultSet.next());
    }

    @Test
    public void assertGetInt() {
        Assert.assertTrue(actualResultSet.next());
        Assert.assertThat(actualResultSet.getInt(1), CoreMatchers.is(1));
        Assert.assertTrue(actualResultSet.next());
        Assert.assertThat(actualResultSet.getInt("order_id"), CoreMatchers.is(2));
        Assert.assertFalse(actualResultSet.next());
    }

    @Test
    public void assertGetLong() {
        Assert.assertTrue(actualResultSet.next());
        Assert.assertThat(actualResultSet.getLong(1), CoreMatchers.is(1L));
        Assert.assertTrue(actualResultSet.next());
        Assert.assertThat(actualResultSet.getLong("order_id"), CoreMatchers.is(2L));
        Assert.assertFalse(actualResultSet.next());
    }

    @Test
    public void assertGetFloat() {
        Assert.assertTrue(actualResultSet.next());
        Assert.assertThat(actualResultSet.getFloat(1), CoreMatchers.is(1.0F));
        Assert.assertTrue(actualResultSet.next());
        Assert.assertThat(actualResultSet.getFloat("order_id"), CoreMatchers.is(2.0F));
        Assert.assertFalse(actualResultSet.next());
    }

    @Test
    public void assertGetDouble() {
        Assert.assertTrue(actualResultSet.next());
        Assert.assertThat(actualResultSet.getDouble(1), CoreMatchers.is(1.0));
        Assert.assertTrue(actualResultSet.next());
        Assert.assertThat(actualResultSet.getDouble("order_id"), CoreMatchers.is(2.0));
        Assert.assertFalse(actualResultSet.next());
    }

    @Test
    public void assertGetBigDecimal() {
        Assert.assertTrue(actualResultSet.next());
        Assert.assertThat(actualResultSet.getBigDecimal(1), CoreMatchers.is(new BigDecimal("1")));
        Assert.assertThat(actualResultSet.getBigDecimal(1, 2), CoreMatchers.is(new BigDecimal("1").setScale(BigDecimal.ROUND_CEILING, BigDecimal.ROUND_HALF_UP)));
        Assert.assertTrue(actualResultSet.next());
        Assert.assertThat(actualResultSet.getBigDecimal("order_id"), CoreMatchers.is(new BigDecimal("2")));
        Assert.assertThat(actualResultSet.getBigDecimal("order_id", 2), CoreMatchers.is(new BigDecimal("2").setScale(BigDecimal.ROUND_CEILING, BigDecimal.ROUND_HALF_UP)));
        Assert.assertFalse(actualResultSet.next());
    }

    @Test
    public void assertGetBytes() {
        Assert.assertTrue(actualResultSet.next());
        Assert.assertThat(actualResultSet.getBytes(1), CoreMatchers.is("1".getBytes()));
        Assert.assertTrue(actualResultSet.next());
        Assert.assertThat(actualResultSet.getBytes("order_id"), CoreMatchers.is("2".getBytes()));
        Assert.assertFalse(actualResultSet.next());
    }

    @Test
    public void assertGetObject() {
        Assert.assertTrue(actualResultSet.next());
        Assert.assertThat(actualResultSet.getObject(1), CoreMatchers.is(((Object) (1L))));
        Assert.assertTrue(actualResultSet.next());
        Assert.assertThat(actualResultSet.getObject("order_id"), CoreMatchers.is(((Object) (2L))));
        Assert.assertFalse(actualResultSet.next());
    }

    @Test
    public void assertFindColumn() {
        Assert.assertThat(actualResultSet.findColumn("any"), CoreMatchers.is(1));
    }

    @Test
    public void assertGetType() {
        Assert.assertThat(actualResultSet.getType(), CoreMatchers.is(ResultSet.TYPE_FORWARD_ONLY));
    }

    @Test
    public void assertGetConcurrency() {
        Assert.assertThat(actualResultSet.getConcurrency(), CoreMatchers.is(ResultSet.CONCUR_READ_ONLY));
    }

    @Test
    public void assertGetStatement() {
        Assert.assertThat(actualResultSet.getStatement(), CoreMatchers.is(GeneratedKeysResultSetTest.STATEMENT));
    }
}

