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
package org.apache.shardingsphere.shardingproxy.transport.mysql.constant;


import MySQLColumnType.MYSQL_TYPE_BIT;
import MySQLColumnType.MYSQL_TYPE_BLOB;
import MySQLColumnType.MYSQL_TYPE_DATE;
import MySQLColumnType.MYSQL_TYPE_DECIMAL;
import MySQLColumnType.MYSQL_TYPE_DOUBLE;
import MySQLColumnType.MYSQL_TYPE_FLOAT;
import MySQLColumnType.MYSQL_TYPE_LONG;
import MySQLColumnType.MYSQL_TYPE_LONGLONG;
import MySQLColumnType.MYSQL_TYPE_NEWDECIMAL;
import MySQLColumnType.MYSQL_TYPE_NULL;
import MySQLColumnType.MYSQL_TYPE_SHORT;
import MySQLColumnType.MYSQL_TYPE_TIME;
import MySQLColumnType.MYSQL_TYPE_TIMESTAMP;
import MySQLColumnType.MYSQL_TYPE_TINY;
import MySQLColumnType.MYSQL_TYPE_VARCHAR;
import java.sql.Types;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public final class MySQLColumnTypeTest {
    @Test
    public void assertValueOfJDBC() {
        Assert.assertThat(MySQLColumnType.valueOfJDBCType(Types.BIT), CoreMatchers.is(MYSQL_TYPE_BIT));
        Assert.assertThat(MySQLColumnType.valueOfJDBCType(Types.TINYINT), CoreMatchers.is(MYSQL_TYPE_TINY));
        Assert.assertThat(MySQLColumnType.valueOfJDBCType(Types.SMALLINT), CoreMatchers.is(MYSQL_TYPE_SHORT));
        Assert.assertThat(MySQLColumnType.valueOfJDBCType(Types.INTEGER), CoreMatchers.is(MYSQL_TYPE_LONG));
        Assert.assertThat(MySQLColumnType.valueOfJDBCType(Types.BIGINT), CoreMatchers.is(MYSQL_TYPE_LONGLONG));
        Assert.assertThat(MySQLColumnType.valueOfJDBCType(Types.FLOAT), CoreMatchers.is(MYSQL_TYPE_FLOAT));
        Assert.assertThat(MySQLColumnType.valueOfJDBCType(Types.REAL), CoreMatchers.is(MYSQL_TYPE_FLOAT));
        Assert.assertThat(MySQLColumnType.valueOfJDBCType(Types.DOUBLE), CoreMatchers.is(MYSQL_TYPE_DOUBLE));
        Assert.assertThat(MySQLColumnType.valueOfJDBCType(Types.NUMERIC), CoreMatchers.is(MYSQL_TYPE_NEWDECIMAL));
        Assert.assertThat(MySQLColumnType.valueOfJDBCType(Types.DECIMAL), CoreMatchers.is(MYSQL_TYPE_NEWDECIMAL));
        Assert.assertThat(MySQLColumnType.valueOfJDBCType(Types.CHAR), CoreMatchers.is(MYSQL_TYPE_VARCHAR));
        Assert.assertThat(MySQLColumnType.valueOfJDBCType(Types.VARCHAR), CoreMatchers.is(MYSQL_TYPE_VARCHAR));
        Assert.assertThat(MySQLColumnType.valueOfJDBCType(Types.LONGVARCHAR), CoreMatchers.is(MYSQL_TYPE_VARCHAR));
        Assert.assertThat(MySQLColumnType.valueOfJDBCType(Types.DATE), CoreMatchers.is(MYSQL_TYPE_DATE));
        Assert.assertThat(MySQLColumnType.valueOfJDBCType(Types.TIME), CoreMatchers.is(MYSQL_TYPE_TIME));
        Assert.assertThat(MySQLColumnType.valueOfJDBCType(Types.TIMESTAMP), CoreMatchers.is(MYSQL_TYPE_TIMESTAMP));
        Assert.assertThat(MySQLColumnType.valueOfJDBCType(Types.BINARY), CoreMatchers.is(MYSQL_TYPE_VARCHAR));
        Assert.assertThat(MySQLColumnType.valueOfJDBCType(Types.VARBINARY), CoreMatchers.is(MYSQL_TYPE_VARCHAR));
        Assert.assertThat(MySQLColumnType.valueOfJDBCType(Types.LONGVARBINARY), CoreMatchers.is(MYSQL_TYPE_VARCHAR));
        Assert.assertThat(MySQLColumnType.valueOfJDBCType(Types.NULL), CoreMatchers.is(MYSQL_TYPE_NULL));
        Assert.assertThat(MySQLColumnType.valueOfJDBCType(Types.BLOB), CoreMatchers.is(MYSQL_TYPE_BLOB));
    }

    @Test(expected = IllegalArgumentException.class)
    public void assertValueOfJDBCIllegalArgument() {
        MySQLColumnType.valueOfJDBCType(9999);
    }

    @Test
    public void assertValueOf() {
        Assert.assertThat(MySQLColumnType.valueOf(MYSQL_TYPE_DECIMAL.getValue()), CoreMatchers.is(MYSQL_TYPE_DECIMAL));
    }

    @Test(expected = IllegalArgumentException.class)
    public void assertValueOfWithIllegalArgument() {
        MySQLColumnType.valueOf((-1));
    }
}

