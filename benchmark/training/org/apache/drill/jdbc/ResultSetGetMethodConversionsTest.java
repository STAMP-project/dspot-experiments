/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.drill.jdbc;


import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.apache.drill.categories.JdbcTest;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;


// TODO:  Resolve requirements:
// - For SMALLINT retrieved with getObject(), is result required (by user
// expectations, from behavior of other JDBC drivers) to be a Short?  Can
// it be a smaller type (e.g., Byte, if the value fits)?  Can it be a larger
// type (e.g., Long)?
// - For INTEGER retrieved by getShort(...):
// - Is that always an error, or is it allowed as least sometimes?
// - For INTEGER, does getShort(...) return value if value fits in short?
// - For INTEGER, if value does not fit in short, does getShort(...) throw
// exception, or return something.  If it returns:
// - Does it return maximum short value or does it use modulus?
// - Does it set warning status (on ResultSet)?
// - Should getString(...) on BOOLEAN return "TRUE"/"FALSE" or "true"/"false"?
// (TODO:  Check SQL spec for general canonical form and results of
// CAST( TRUE AS VARCHAR ).)
/**
 * Integration-level unit test for ResultSet's <code>get<i>Type</i>(<i>column ID</i>)</code>
 * methods' type conversions.
 * <p>
 *   This test class is intended for higher-level type-vs.-type coverage tests.
 *   Detailed-case tests (e.g., boundary conditions) are intended to be in
 *   {@link org.apache.drill.jdbc.impl.TypeConvertingSqlAccessor}).
 * </p>
 */
// //////////////////////////////////////
// - getRowId:
// - ROWID;
// 
// //////////////////////////////////////
// - getSQLXML:
// - SQLXML SQLXML
@Category(JdbcTest.class)
public class ResultSetGetMethodConversionsTest extends JdbcTestBase {
    private static Connection connection;

    private static ResultSet testDataRow;

    private static ResultSet testDataRowWithNulls;

    @Test
    public void test_getByte_handles_INTEGER() throws SQLException {
        Assert.assertThat(ResultSetGetMethodConversionsTest.testDataRow.getByte("C_INTEGER_3"), CoreMatchers.equalTo(((byte) (3))));
    }

    @Test
    public void test_getByte_handles_BIGINT() throws SQLException {
        Assert.assertThat(ResultSetGetMethodConversionsTest.testDataRow.getByte("C_BIGINT_4"), CoreMatchers.equalTo(((byte) (4))));
    }

    @Test
    public void test_getByte_handles_DOUBLE() throws SQLException {
        Assert.assertThat(ResultSetGetMethodConversionsTest.testDataRow.getByte("C_DOUBLE_PREC._6.6"), CoreMatchers.equalTo(((byte) (6))));
    }

    @Test
    public void test_getByte_handles_FLOAT() throws SQLException {
        Assert.assertThat(ResultSetGetMethodConversionsTest.testDataRow.getByte("C_FLOAT_7.7"), CoreMatchers.equalTo(((byte) (7))));
    }

    @Test
    public void test_getByte_handles_DECIMAL() throws SQLException {
        Assert.assertThat(ResultSetGetMethodConversionsTest.testDataRow.getByte("C_DECIMAL_10.10"), CoreMatchers.equalTo(((byte) (10))));
    }

    @Test
    public void test_getShort_handles_INTEGER() throws SQLException {
        Assert.assertThat(ResultSetGetMethodConversionsTest.testDataRow.getShort("C_INTEGER_3"), CoreMatchers.equalTo(((short) (3))));
    }

    @Test
    public void test_getShort_handles_BIGINT() throws SQLException {
        Assert.assertThat(ResultSetGetMethodConversionsTest.testDataRow.getShort("C_BIGINT_4"), CoreMatchers.equalTo(((short) (4))));
    }

    @Test
    public void test_getShort_handles_DOUBLE() throws SQLException {
        Assert.assertThat(ResultSetGetMethodConversionsTest.testDataRow.getShort("C_DOUBLE_PREC._6.6"), CoreMatchers.equalTo(((short) (6))));
    }

    @Test
    public void test_getShort_handles_FLOAT() throws SQLException {
        Assert.assertThat(ResultSetGetMethodConversionsTest.testDataRow.getShort("C_FLOAT_7.7"), CoreMatchers.equalTo(((short) (7))));
    }

    @Test
    public void test_getShort_handles_DECIMAL() throws SQLException {
        Assert.assertThat(ResultSetGetMethodConversionsTest.testDataRow.getShort("C_DECIMAL_10.10"), CoreMatchers.equalTo(((short) (10))));
    }

    @Test
    public void test_getInt_handles_INTEGER() throws SQLException {
        Assert.assertThat(ResultSetGetMethodConversionsTest.testDataRow.getInt("C_INTEGER_3"), CoreMatchers.equalTo(3));
    }

    @Test
    public void test_getInt_handles_BIGINT() throws SQLException {
        Assert.assertThat(ResultSetGetMethodConversionsTest.testDataRow.getInt("C_BIGINT_4"), CoreMatchers.equalTo(4));
    }

    @Test
    public void test_getInt_handles_DOUBLE() throws SQLException {
        Assert.assertThat(ResultSetGetMethodConversionsTest.testDataRow.getInt("C_DOUBLE_PREC._6.6"), CoreMatchers.equalTo(6));
    }

    @Test
    public void test_getInt_handles_FLOAT() throws SQLException {
        Assert.assertThat(ResultSetGetMethodConversionsTest.testDataRow.getInt("C_FLOAT_7.7"), CoreMatchers.equalTo(7));
    }

    @Test
    public void test_getInt_handles_DECIMAL() throws SQLException {
        Assert.assertThat(ResultSetGetMethodConversionsTest.testDataRow.getInt("C_DECIMAL_10.10"), CoreMatchers.equalTo(10));
    }

    @Test
    public void test_getLong_handles_INTEGER() throws SQLException {
        Assert.assertThat(ResultSetGetMethodConversionsTest.testDataRow.getLong("C_INTEGER_3"), CoreMatchers.equalTo(3L));
    }

    @Test
    public void test_getLong_handles_BIGINT() throws SQLException {
        Assert.assertThat(ResultSetGetMethodConversionsTest.testDataRow.getLong("C_BIGINT_4"), CoreMatchers.equalTo(4L));
    }

    @Test
    public void test_getLong_handles_DOUBLE() throws SQLException {
        Assert.assertThat(ResultSetGetMethodConversionsTest.testDataRow.getLong("C_DOUBLE_PREC._6.6"), CoreMatchers.equalTo(6L));
    }

    @Test
    public void test_getLong_handles_FLOAT() throws SQLException {
        Assert.assertThat(ResultSetGetMethodConversionsTest.testDataRow.getLong("C_FLOAT_7.7"), CoreMatchers.equalTo(7L));
    }

    @Test
    public void test_getLong_handles_DECIMAL() throws SQLException {
        Assert.assertThat(ResultSetGetMethodConversionsTest.testDataRow.getLong("C_DECIMAL_10.10"), CoreMatchers.equalTo(10L));
    }

    @Test
    public void test_getFloat_handles_INTEGER() throws SQLException {
        Assert.assertThat(ResultSetGetMethodConversionsTest.testDataRow.getFloat("C_INTEGER_3"), CoreMatchers.equalTo(3.0F));
    }

    @Test
    public void test_getFloat_handles_BIGINT() throws SQLException {
        Assert.assertThat(ResultSetGetMethodConversionsTest.testDataRow.getFloat("C_BIGINT_4"), CoreMatchers.equalTo(4.0F));
    }

    @Test
    public void test_getFloat_handles_DOUBLE() throws SQLException {
        Assert.assertThat(ResultSetGetMethodConversionsTest.testDataRow.getFloat("C_DOUBLE_PREC._6.6"), CoreMatchers.equalTo(6.6F));
    }

    @Test
    public void test_getFloat_handles_FLOAT() throws SQLException {
        Assert.assertThat(ResultSetGetMethodConversionsTest.testDataRow.getFloat("C_FLOAT_7.7"), CoreMatchers.equalTo(7.7F));
    }

    @Test
    public void test_getFloat_handles_DECIMAL() throws SQLException {
        Assert.assertThat(ResultSetGetMethodConversionsTest.testDataRow.getFloat("C_DECIMAL_10.10"), CoreMatchers.equalTo(10.1F));
    }

    @Test
    public void test_getDouble_handles_INTEGER() throws SQLException {
        Assert.assertThat(ResultSetGetMethodConversionsTest.testDataRow.getDouble("C_INTEGER_3"), CoreMatchers.equalTo(3.0));
    }

    @Test
    public void test_getDouble_handles_BIGINT() throws SQLException {
        Assert.assertThat(ResultSetGetMethodConversionsTest.testDataRow.getDouble("C_BIGINT_4"), CoreMatchers.equalTo(4.0));
    }

    @Test
    public void test_getDouble_handles_DOUBLE() throws SQLException {
        Assert.assertThat(ResultSetGetMethodConversionsTest.testDataRow.getDouble("C_DOUBLE_PREC._6.6"), CoreMatchers.equalTo(6.6));
    }

    @Test
    public void test_getDouble_handles_FLOAT() throws SQLException {
        Assert.assertThat(ResultSetGetMethodConversionsTest.testDataRow.getDouble("C_FLOAT_7.7"), CoreMatchers.equalTo(((double) (7.7F))));
    }

    @Test
    public void test_getDouble_handles_DECIMAL() throws SQLException {
        Assert.assertThat(ResultSetGetMethodConversionsTest.testDataRow.getDouble("C_DECIMAL_10.10"), CoreMatchers.equalTo(10.1));
    }

    @Test
    public void test_getBigDecimal_handles_INTEGER() throws SQLException {
        Assert.assertThat(ResultSetGetMethodConversionsTest.testDataRow.getBigDecimal("C_INTEGER_3"), CoreMatchers.equalTo(new BigDecimal(3)));
    }

    @Test
    public void test_getBigDecimal_handles_BIGINT() throws SQLException {
        Assert.assertThat(ResultSetGetMethodConversionsTest.testDataRow.getBigDecimal("C_BIGINT_4"), CoreMatchers.equalTo(new BigDecimal(4)));
    }

    @Test
    public void test_getBigDecimal_handles_DOUBLE() throws SQLException {
        Assert.assertThat(ResultSetGetMethodConversionsTest.testDataRow.getBigDecimal("C_DOUBLE_PREC._6.6"), CoreMatchers.equalTo(new BigDecimal(6.6)));
    }

    @Test
    public void test_getBigDecimal_handles_FLOAT() throws SQLException {
        Assert.assertThat(ResultSetGetMethodConversionsTest.testDataRow.getBigDecimal("C_FLOAT_7.7"), CoreMatchers.equalTo(new BigDecimal(7.7F)));
    }

    @Test
    public void test_getBigDecimal_handles_DECIMAL_1() throws SQLException {
        Assert.assertThat(ResultSetGetMethodConversionsTest.testDataRow.getBigDecimal("C_DECIMAL_10.5"), CoreMatchers.equalTo(new BigDecimal("10.5")));
    }

    @Test
    public void test_getBigDecimal_handles_DECIMAL_2() throws SQLException {
        Assert.assertThat(ResultSetGetMethodConversionsTest.testDataRow.getBigDecimal("C_DECIMAL_10.10"), CoreMatchers.equalTo(new BigDecimal("10.10")));
    }

    @Test
    public void test_getString_handles_INTEGER() throws SQLException {
        Assert.assertThat(ResultSetGetMethodConversionsTest.testDataRow.getString("C_INTEGER_3"), CoreMatchers.equalTo("3"));
        Assert.assertThat(ResultSetGetMethodConversionsTest.testDataRowWithNulls.getString("C_INTEGER_3"), CoreMatchers.equalTo(null));
    }

    @Test
    public void test_getString_handles_BIGINT() throws SQLException {
        Assert.assertThat(ResultSetGetMethodConversionsTest.testDataRow.getString("C_BIGINT_4"), CoreMatchers.equalTo("4"));
        Assert.assertThat(ResultSetGetMethodConversionsTest.testDataRowWithNulls.getString("C_BIGINT_4"), CoreMatchers.equalTo(null));
    }

    @Test
    public void test_getString_handles_DOUBLE() throws SQLException {
        Assert.assertThat(ResultSetGetMethodConversionsTest.testDataRow.getString("C_DOUBLE_PREC._6.6"), CoreMatchers.equalTo("6.6"));
        Assert.assertThat(ResultSetGetMethodConversionsTest.testDataRowWithNulls.getString("C_DOUBLE_PREC._6.6"), CoreMatchers.equalTo(null));
    }

    @Test
    public void test_getString_handles_FLOAT() throws SQLException {
        Assert.assertThat(ResultSetGetMethodConversionsTest.testDataRow.getString("C_FLOAT_7.7"), CoreMatchers.equalTo("7.7"));
        Assert.assertThat(ResultSetGetMethodConversionsTest.testDataRowWithNulls.getString("C_FLOAT_7.7"), CoreMatchers.equalTo(null));
    }

    @Test
    public void test_getString_handles_DECIMAL() throws SQLException {
        Assert.assertThat(ResultSetGetMethodConversionsTest.testDataRow.getString("C_DECIMAL_10.10"), CoreMatchers.equalTo("10.10"));
        Assert.assertThat(ResultSetGetMethodConversionsTest.testDataRowWithNulls.getString("C_DECIMAL_10.10"), CoreMatchers.equalTo(null));
    }

    @Test
    public void test_getObject_handles_INTEGER() throws SQLException {
        Assert.assertThat(ResultSetGetMethodConversionsTest.testDataRow.getObject("C_INTEGER_3"), CoreMatchers.equalTo(((Object) (3))));
    }

    @Test
    public void test_getObject_handles_BIGINT() throws SQLException {
        Assert.assertThat(ResultSetGetMethodConversionsTest.testDataRow.getObject("C_BIGINT_4"), CoreMatchers.equalTo(((Object) (4L))));
    }

    @Test
    public void test_getObject_handles_DOUBLE() throws SQLException {
        Assert.assertThat(ResultSetGetMethodConversionsTest.testDataRow.getObject("C_DOUBLE_PREC._6.6"), CoreMatchers.equalTo(((Object) (6.6))));
    }

    @Test
    public void test_getObject_handles_FLOAT() throws SQLException {
        Assert.assertThat(ResultSetGetMethodConversionsTest.testDataRow.getObject("C_FLOAT_7.7"), CoreMatchers.equalTo(((Object) (7.7F))));
    }

    @Test
    public void test_getObject_handles_DECIMAL_1() throws SQLException {
        Assert.assertThat(ResultSetGetMethodConversionsTest.testDataRow.getObject("C_DECIMAL_10.5"), CoreMatchers.equalTo(((Object) (new BigDecimal("10.5")))));
    }

    @Test
    public void test_getObject_handles_DECIMAL_2() throws SQLException {
        Assert.assertThat(ResultSetGetMethodConversionsTest.testDataRow.getObject("C_DECIMAL_10.10"), CoreMatchers.equalTo(((Object) (new BigDecimal("10.10")))));
    }
}

