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
package org.apache.shardingsphere.shardingproxy.transport.mysql.packet.command.query.binary.execute.protocol;


import MySQLColumnType.MYSQL_TYPE_BIT;
import MySQLColumnType.MYSQL_TYPE_BLOB;
import MySQLColumnType.MYSQL_TYPE_DATE;
import MySQLColumnType.MYSQL_TYPE_DATETIME;
import MySQLColumnType.MYSQL_TYPE_DECIMAL;
import MySQLColumnType.MYSQL_TYPE_DOUBLE;
import MySQLColumnType.MYSQL_TYPE_ENUM;
import MySQLColumnType.MYSQL_TYPE_FLOAT;
import MySQLColumnType.MYSQL_TYPE_GEOMETRY;
import MySQLColumnType.MYSQL_TYPE_INT24;
import MySQLColumnType.MYSQL_TYPE_LONG;
import MySQLColumnType.MYSQL_TYPE_LONGLONG;
import MySQLColumnType.MYSQL_TYPE_LONG_BLOB;
import MySQLColumnType.MYSQL_TYPE_MEDIUM_BLOB;
import MySQLColumnType.MYSQL_TYPE_NEWDECIMAL;
import MySQLColumnType.MYSQL_TYPE_NULL;
import MySQLColumnType.MYSQL_TYPE_SET;
import MySQLColumnType.MYSQL_TYPE_SHORT;
import MySQLColumnType.MYSQL_TYPE_STRING;
import MySQLColumnType.MYSQL_TYPE_TIME;
import MySQLColumnType.MYSQL_TYPE_TIMESTAMP;
import MySQLColumnType.MYSQL_TYPE_TINY;
import MySQLColumnType.MYSQL_TYPE_TINY_BLOB;
import MySQLColumnType.MYSQL_TYPE_VARCHAR;
import MySQLColumnType.MYSQL_TYPE_VAR_STRING;
import MySQLColumnType.MYSQL_TYPE_YEAR;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public final class MySQLMySQLBinaryProtocolValueFactoryTest {
    @Test
    public void assertGetBinaryProtocolValueWithMySQLTypeString() {
        Assert.assertThat(MySQLBinaryProtocolValueFactory.getBinaryProtocolValue(MYSQL_TYPE_STRING), CoreMatchers.instanceOf(MySQLStringLenencBinaryProtocolValue.class));
    }

    @Test
    public void assertGetBinaryProtocolValueWithMySQLTypeVarchar() {
        Assert.assertThat(MySQLBinaryProtocolValueFactory.getBinaryProtocolValue(MYSQL_TYPE_VARCHAR), CoreMatchers.instanceOf(MySQLStringLenencBinaryProtocolValue.class));
    }

    @Test
    public void assertGetBinaryProtocolValueWithMySQLTypeVarString() {
        Assert.assertThat(MySQLBinaryProtocolValueFactory.getBinaryProtocolValue(MYSQL_TYPE_VAR_STRING), CoreMatchers.instanceOf(MySQLStringLenencBinaryProtocolValue.class));
    }

    @Test
    public void assertGetBinaryProtocolValueWithMySQLTypeEnum() {
        Assert.assertThat(MySQLBinaryProtocolValueFactory.getBinaryProtocolValue(MYSQL_TYPE_ENUM), CoreMatchers.instanceOf(MySQLStringLenencBinaryProtocolValue.class));
    }

    @Test
    public void assertGetBinaryProtocolValueWithMySQLTypeSet() {
        Assert.assertThat(MySQLBinaryProtocolValueFactory.getBinaryProtocolValue(MYSQL_TYPE_SET), CoreMatchers.instanceOf(MySQLStringLenencBinaryProtocolValue.class));
    }

    @Test
    public void assertGetBinaryProtocolValueWithMySQLTypeLongBlob() {
        Assert.assertThat(MySQLBinaryProtocolValueFactory.getBinaryProtocolValue(MYSQL_TYPE_LONG_BLOB), CoreMatchers.instanceOf(MySQLStringLenencBinaryProtocolValue.class));
    }

    @Test
    public void assertGetBinaryProtocolValueWithMySQLTypeMediumBlob() {
        Assert.assertThat(MySQLBinaryProtocolValueFactory.getBinaryProtocolValue(MYSQL_TYPE_MEDIUM_BLOB), CoreMatchers.instanceOf(MySQLStringLenencBinaryProtocolValue.class));
    }

    @Test
    public void assertGetBinaryProtocolValueWithMySQLTypeBlob() {
        Assert.assertThat(MySQLBinaryProtocolValueFactory.getBinaryProtocolValue(MYSQL_TYPE_BLOB), CoreMatchers.instanceOf(MySQLStringLenencBinaryProtocolValue.class));
    }

    @Test
    public void assertGetBinaryProtocolValueWithMySQLTypeTinyBlob() {
        Assert.assertThat(MySQLBinaryProtocolValueFactory.getBinaryProtocolValue(MYSQL_TYPE_TINY_BLOB), CoreMatchers.instanceOf(MySQLStringLenencBinaryProtocolValue.class));
    }

    @Test
    public void assertGetBinaryProtocolValueWithMySQLTypeGeometry() {
        Assert.assertThat(MySQLBinaryProtocolValueFactory.getBinaryProtocolValue(MYSQL_TYPE_GEOMETRY), CoreMatchers.instanceOf(MySQLStringLenencBinaryProtocolValue.class));
    }

    @Test
    public void assertGetBinaryProtocolValueWithMySQLTypeBit() {
        Assert.assertThat(MySQLBinaryProtocolValueFactory.getBinaryProtocolValue(MYSQL_TYPE_BIT), CoreMatchers.instanceOf(MySQLStringLenencBinaryProtocolValue.class));
    }

    @Test
    public void assertGetBinaryProtocolValueWithMySQLTypeDecimal() {
        Assert.assertThat(MySQLBinaryProtocolValueFactory.getBinaryProtocolValue(MYSQL_TYPE_DECIMAL), CoreMatchers.instanceOf(MySQLStringLenencBinaryProtocolValue.class));
    }

    @Test
    public void assertGetBinaryProtocolValueWithMySQLTypeNewDecimal() {
        Assert.assertThat(MySQLBinaryProtocolValueFactory.getBinaryProtocolValue(MYSQL_TYPE_NEWDECIMAL), CoreMatchers.instanceOf(MySQLStringLenencBinaryProtocolValue.class));
    }

    @Test
    public void assertGetBinaryProtocolValueWithMySQLTypeLongLong() {
        Assert.assertThat(MySQLBinaryProtocolValueFactory.getBinaryProtocolValue(MYSQL_TYPE_LONGLONG), CoreMatchers.instanceOf(MySQLInt8BinaryProtocolValue.class));
    }

    @Test
    public void assertGetBinaryProtocolValueWithMySQLTypeLong() {
        Assert.assertThat(MySQLBinaryProtocolValueFactory.getBinaryProtocolValue(MYSQL_TYPE_LONG), CoreMatchers.instanceOf(MySQLInt4BinaryProtocolValue.class));
    }

    @Test
    public void assertGetBinaryProtocolValueWithMySQLTypeInt24() {
        Assert.assertThat(MySQLBinaryProtocolValueFactory.getBinaryProtocolValue(MYSQL_TYPE_INT24), CoreMatchers.instanceOf(MySQLInt4BinaryProtocolValue.class));
    }

    @Test
    public void assertGetBinaryProtocolValueWithMySQLTypeShort() {
        Assert.assertThat(MySQLBinaryProtocolValueFactory.getBinaryProtocolValue(MYSQL_TYPE_SHORT), CoreMatchers.instanceOf(MySQLInt2BinaryProtocolValue.class));
    }

    @Test
    public void assertGetBinaryProtocolValueWithMySQLTypeYear() {
        Assert.assertThat(MySQLBinaryProtocolValueFactory.getBinaryProtocolValue(MYSQL_TYPE_YEAR), CoreMatchers.instanceOf(MySQLInt2BinaryProtocolValue.class));
    }

    @Test
    public void assertGetBinaryProtocolValueWithMySQLTypeTiny() {
        Assert.assertThat(MySQLBinaryProtocolValueFactory.getBinaryProtocolValue(MYSQL_TYPE_TINY), CoreMatchers.instanceOf(MySQLInt1BinaryProtocolValue.class));
    }

    @Test
    public void assertGetBinaryProtocolValueWithMySQLTypeDouble() {
        Assert.assertThat(MySQLBinaryProtocolValueFactory.getBinaryProtocolValue(MYSQL_TYPE_DOUBLE), CoreMatchers.instanceOf(MySQLDoubleBinaryProtocolValue.class));
    }

    @Test
    public void assertGetBinaryProtocolValueWithMySQLTypeFloat() {
        Assert.assertThat(MySQLBinaryProtocolValueFactory.getBinaryProtocolValue(MYSQL_TYPE_FLOAT), CoreMatchers.instanceOf(MySQLFloatBinaryProtocolValue.class));
    }

    @Test
    public void assertGetBinaryProtocolValueWithMySQLTypeDate() {
        Assert.assertThat(MySQLBinaryProtocolValueFactory.getBinaryProtocolValue(MYSQL_TYPE_DATE), CoreMatchers.instanceOf(MySQLDateBinaryProtocolValue.class));
    }

    @Test
    public void assertGetBinaryProtocolValueWithMySQLTypeDatetime() {
        Assert.assertThat(MySQLBinaryProtocolValueFactory.getBinaryProtocolValue(MYSQL_TYPE_DATETIME), CoreMatchers.instanceOf(MySQLDateBinaryProtocolValue.class));
    }

    @Test
    public void assertGetBinaryProtocolValueWithMySQLTypeTimestamp() {
        Assert.assertThat(MySQLBinaryProtocolValueFactory.getBinaryProtocolValue(MYSQL_TYPE_TIMESTAMP), CoreMatchers.instanceOf(MySQLDateBinaryProtocolValue.class));
    }

    @Test
    public void assertGetBinaryProtocolValueWithMySQLTypeTime() {
        Assert.assertThat(MySQLBinaryProtocolValueFactory.getBinaryProtocolValue(MYSQL_TYPE_TIME), CoreMatchers.instanceOf(MySQLTimeBinaryProtocolValue.class));
    }

    @Test(expected = IllegalArgumentException.class)
    public void assertGetBinaryProtocolValueWithUnsupportedType() {
        MySQLBinaryProtocolValueFactory.getBinaryProtocolValue(MYSQL_TYPE_NULL);
    }
}

