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
package org.apache.shardingsphere.shardingjdbc.jdbc.adapter;


import java.io.IOException;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.shardingsphere.core.constant.DatabaseType;
import org.apache.shardingsphere.shardingjdbc.common.base.AbstractShardingJDBCDatabaseAndTableTest;
import org.apache.shardingsphere.shardingjdbc.jdbc.core.connection.ShardingConnection;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public final class ResultSetGetterAdapterTest extends AbstractShardingJDBCDatabaseAndTableTest {
    private final List<ShardingConnection> shardingConnections = new ArrayList<>();

    private final List<Statement> statements = new ArrayList<>();

    private final Map<DatabaseType, ResultSet> resultSets = new HashMap<>();

    private final String columnName = "user_id";

    @Test
    public void assertGetBooleanForColumnIndex() {
        for (Map.Entry<DatabaseType, ResultSet> each : resultSets.entrySet()) {
            try {
                each.getValue().getBoolean(1);
                Assert.fail("Expected an SQLException to be thrown");
            } catch (final Exception ex) {
                Assert.assertFalse(ex.getMessage().isEmpty());
            }
        }
    }

    @Test
    public void assertGetBooleanForColumnLabel() {
        for (Map.Entry<DatabaseType, ResultSet> each : resultSets.entrySet()) {
            try {
                Assert.assertTrue(each.getValue().getBoolean(columnName));
                Assert.fail("Expected an SQLException to be thrown");
            } catch (final Exception ex) {
                Assert.assertFalse(ex.getMessage().isEmpty());
            }
        }
    }

    @Test
    public void assertGetByteForColumnIndex() throws SQLException {
        for (ResultSet each : resultSets.values()) {
            Assert.assertThat(each.getByte(1), CoreMatchers.is(((byte) (10))));
        }
    }

    @Test
    public void assertGetByteForColumnLabel() throws SQLException {
        for (ResultSet each : resultSets.values()) {
            Assert.assertThat(each.getByte(columnName), CoreMatchers.is(((byte) (10))));
        }
    }

    @Test
    public void assertGetShortForColumnIndex() throws SQLException {
        for (ResultSet each : resultSets.values()) {
            Assert.assertThat(each.getShort(1), CoreMatchers.is(((short) (10))));
        }
    }

    @Test
    public void assertGetShortForColumnLabel() throws SQLException {
        for (ResultSet each : resultSets.values()) {
            Assert.assertThat(each.getShort(columnName), CoreMatchers.is(((short) (10))));
        }
    }

    @Test
    public void assertGetIntForColumnIndex() throws SQLException {
        for (ResultSet each : resultSets.values()) {
            Assert.assertThat(each.getInt(1), CoreMatchers.is(10));
        }
    }

    @Test
    public void assertGetIntForColumnLabel() throws SQLException {
        for (ResultSet each : resultSets.values()) {
            Assert.assertThat(each.getInt(columnName), CoreMatchers.is(10));
        }
    }

    @Test
    public void assertGetLongForColumnIndex() throws SQLException {
        for (ResultSet each : resultSets.values()) {
            Assert.assertThat(each.getLong(1), CoreMatchers.is(10L));
        }
    }

    @Test
    public void assertGetLongForColumnLabel() throws SQLException {
        for (ResultSet each : resultSets.values()) {
            Assert.assertThat(each.getLong(columnName), CoreMatchers.is(10L));
        }
    }

    @Test
    public void assertGetFloatForColumnIndex() throws SQLException {
        for (ResultSet each : resultSets.values()) {
            Assert.assertThat(each.getFloat(1), CoreMatchers.is(10.0F));
        }
    }

    @Test
    public void assertGetFloatForColumnLabel() throws SQLException {
        for (ResultSet each : resultSets.values()) {
            Assert.assertThat(each.getFloat(columnName), CoreMatchers.is(10.0F));
        }
    }

    @Test
    public void assertGetDoubleForColumnIndex() throws SQLException {
        for (ResultSet each : resultSets.values()) {
            Assert.assertThat(each.getDouble(1), CoreMatchers.is(10.0));
        }
    }

    @Test
    public void assertGetDoubleForColumnLabel() throws SQLException {
        for (ResultSet each : resultSets.values()) {
            Assert.assertThat(each.getDouble(columnName), CoreMatchers.is(10.0));
        }
    }

    @Test
    public void assertGetStringForColumnIndex() throws SQLException {
        for (ResultSet each : resultSets.values()) {
            Assert.assertThat(each.getString(1), CoreMatchers.is("10"));
        }
    }

    @Test
    public void assertGetStringForColumnLabel() throws SQLException {
        for (ResultSet each : resultSets.values()) {
            Assert.assertThat(each.getString(columnName), CoreMatchers.is("10"));
        }
    }

    @Test
    public void assertGetBigDecimalForColumnIndex() throws SQLException {
        for (ResultSet each : resultSets.values()) {
            Assert.assertThat(each.getBigDecimal(1), CoreMatchers.is(new BigDecimal("10")));
        }
    }

    @Test
    public void assertGetBigDecimalForColumnLabel() throws SQLException {
        for (ResultSet each : resultSets.values()) {
            Assert.assertThat(each.getBigDecimal(columnName), CoreMatchers.is(new BigDecimal("10")));
        }
    }

    @SuppressWarnings("deprecation")
    @Test
    public void assertGetBigDecimalColumnIndexWithScale() throws SQLException {
        for (ResultSet each : resultSets.values()) {
            Assert.assertThat(each.getBigDecimal(1, 2), CoreMatchers.is(new BigDecimal("10")));
        }
    }

    @SuppressWarnings("deprecation")
    @Test
    public void assertGetBigDecimalColumnLabelWithScale() throws SQLException {
        for (ResultSet each : resultSets.values()) {
            Assert.assertThat(each.getBigDecimal(columnName, 2), CoreMatchers.is(new BigDecimal("10")));
        }
    }

    @Test
    public void assertGetBytesForColumnIndex() {
        for (ResultSet each : resultSets.values()) {
            try {
                Assert.assertTrue(((each.getBytes(1).length) > 0));
                Assert.fail("Expected an SQLException to be thrown");
            } catch (final Exception ex) {
                Assert.assertFalse(ex.getMessage().isEmpty());
            }
        }
    }

    @Test
    public void assertGetBytesForColumnLabel() {
        for (ResultSet each : resultSets.values()) {
            try {
                Assert.assertTrue(((each.getBytes(columnName).length) > 0));
                Assert.fail("Expected an SQLException to be thrown");
            } catch (final Exception ex) {
                Assert.assertFalse(ex.getMessage().isEmpty());
            }
        }
    }

    @Test
    public void assertGetDateForColumnIndex() {
        for (ResultSet each : resultSets.values()) {
            try {
                each.getDate(1);
                Assert.fail("Expected an SQLException to be thrown");
            } catch (final Exception ex) {
                Assert.assertFalse(ex.getMessage().isEmpty());
            }
        }
    }

    @Test
    public void assertGetDateForColumnLabel() {
        for (ResultSet each : resultSets.values()) {
            try {
                each.getDate(columnName);
                Assert.fail("Expected an SQLException to be thrown");
            } catch (final Exception ex) {
                Assert.assertFalse(ex.getMessage().isEmpty());
            }
        }
    }

    @Test
    public void assertGetDateColumnIndexWithCalendar() {
        for (ResultSet each : resultSets.values()) {
            try {
                each.getDate(1, Calendar.getInstance());
                Assert.fail("Expected an SQLException to be thrown");
            } catch (final Exception ex) {
                Assert.assertFalse(ex.getMessage().isEmpty());
            }
        }
    }

    @Test
    public void assertGetDateColumnLabelWithCalendar() {
        for (ResultSet each : resultSets.values()) {
            try {
                each.getDate(columnName, Calendar.getInstance());
                Assert.fail("Expected an SQLException to be thrown");
            } catch (final Exception ex) {
                Assert.assertFalse(ex.getMessage().isEmpty());
            }
        }
    }

    @Test
    public void assertGetTimeForColumnIndex() {
        for (ResultSet each : resultSets.values()) {
            try {
                each.getTime(1);
                Assert.fail("Expected an SQLException to be thrown");
            } catch (final Exception ex) {
                Assert.assertFalse(ex.getMessage().isEmpty());
            }
        }
    }

    @Test
    public void assertGetTimeForColumnLabel() {
        for (ResultSet each : resultSets.values()) {
            try {
                each.getTime(columnName);
                Assert.fail("Expected an SQLException to be thrown");
            } catch (final Exception ex) {
                Assert.assertFalse(ex.getMessage().isEmpty());
            }
        }
    }

    @Test
    public void assertGetTimeColumnIndexWithCalendar() {
        for (ResultSet each : resultSets.values()) {
            try {
                each.getTime(1, Calendar.getInstance());
                Assert.fail("Expected an SQLException to be thrown");
            } catch (final Exception ex) {
                Assert.assertFalse(ex.getMessage().isEmpty());
            }
        }
    }

    @Test
    public void assertGetTimeColumnLabelWithCalendar() {
        for (ResultSet each : resultSets.values()) {
            try {
                each.getTime(columnName, Calendar.getInstance());
                Assert.fail("Expected an SQLException to be thrown");
            } catch (final Exception ex) {
                Assert.assertFalse(ex.getMessage().isEmpty());
            }
        }
    }

    @Test
    public void assertGetTimestampForColumnIndex() {
        for (Map.Entry<DatabaseType, ResultSet> each : resultSets.entrySet()) {
            try {
                each.getValue().getTimestamp(1);
                if (((DatabaseType.MySQL) == (each.getKey())) || ((DatabaseType.PostgreSQL) == (each.getKey()))) {
                    continue;
                }
                Assert.fail("Expected an SQLException to be thrown");
            } catch (final Exception ex) {
                Assert.assertFalse(ex.getMessage().isEmpty());
            }
        }
    }

    @Test
    public void assertGetTimestampForColumnLabel() {
        for (Map.Entry<DatabaseType, ResultSet> each : resultSets.entrySet()) {
            try {
                each.getValue().getTimestamp(columnName);
                if (((DatabaseType.MySQL) == (each.getKey())) || ((DatabaseType.PostgreSQL) == (each.getKey()))) {
                    continue;
                }
                Assert.fail("Expected an SQLException to be thrown");
            } catch (final Exception ex) {
                Assert.assertFalse(ex.getMessage().isEmpty());
            }
        }
    }

    @Test
    public void assertGetTimestampColumnIndexWithCalendar() {
        for (Map.Entry<DatabaseType, ResultSet> each : resultSets.entrySet()) {
            try {
                each.getValue().getTimestamp(1, Calendar.getInstance());
                if (((DatabaseType.MySQL) == (each.getKey())) || ((DatabaseType.PostgreSQL) == (each.getKey()))) {
                    continue;
                }
                Assert.fail("Expected an SQLException to be thrown");
            } catch (final Exception ex) {
                Assert.assertFalse(ex.getMessage().isEmpty());
            }
        }
    }

    @Test
    public void assertGetTimestampColumnLabelWithCalendar() {
        for (Map.Entry<DatabaseType, ResultSet> each : resultSets.entrySet()) {
            try {
                each.getValue().getTimestamp(columnName, Calendar.getInstance());
                if (((DatabaseType.MySQL) == (each.getKey())) || ((DatabaseType.PostgreSQL) == (each.getKey()))) {
                    continue;
                }
                Assert.fail("Expected an SQLException to be thrown");
            } catch (final Exception ex) {
                Assert.assertFalse(ex.getMessage().isEmpty());
            }
        }
    }

    @Test
    public void assertGetAsciiStreamForColumnIndex() throws IOException, SQLException {
        for (Map.Entry<DatabaseType, ResultSet> each : resultSets.entrySet()) {
            if (((DatabaseType.MySQL) == (each.getKey())) || ((DatabaseType.PostgreSQL) == (each.getKey()))) {
                byte[] b = new byte[1];
                each.getValue().getAsciiStream(1).read(b);
                Assert.assertThat(new String(b), CoreMatchers.is("1"));
            }
        }
    }

    @Test
    public void assertGetAsciiStreamForColumnLabel() throws IOException, SQLException {
        for (Map.Entry<DatabaseType, ResultSet> each : resultSets.entrySet()) {
            if (((DatabaseType.MySQL) == (each.getKey())) || ((DatabaseType.PostgreSQL) == (each.getKey()))) {
                byte[] b = new byte[1];
                each.getValue().getAsciiStream(columnName).read(b);
                Assert.assertThat(new String(b), CoreMatchers.is("1"));
            }
        }
    }

    @SuppressWarnings("deprecation")
    @Test
    public void assertGetUnicodeStreamForColumnIndex() throws IOException, SQLException {
        for (Map.Entry<DatabaseType, ResultSet> each : resultSets.entrySet()) {
            if ((DatabaseType.Oracle) == (each.getKey())) {
                continue;
            }
            byte[] b = new byte[1];
            if (((DatabaseType.H2) == (each.getKey())) || ((DatabaseType.SQLServer) == (each.getKey()))) {
                try {
                    each.getValue().getUnicodeStream(1).read(b);
                } catch (final Exception ignored) {
                }
            } else {
                each.getValue().getUnicodeStream(1).read(b);
                Assert.assertThat(new String(b), CoreMatchers.is("1"));
            }
        }
    }

    @SuppressWarnings("deprecation")
    @Test
    public void assertGetUnicodeStreamForColumnLabel() throws IOException, SQLException {
        for (Map.Entry<DatabaseType, ResultSet> each : resultSets.entrySet()) {
            if ((DatabaseType.Oracle) == (each.getKey())) {
                continue;
            }
            byte[] b = new byte[1];
            if (((DatabaseType.H2) == (each.getKey())) || ((DatabaseType.SQLServer) == (each.getKey()))) {
                try {
                    each.getValue().getUnicodeStream(columnName).read(b);
                } catch (final Exception ignored) {
                }
            } else {
                each.getValue().getUnicodeStream(columnName).read(b);
                Assert.assertThat(new String(b), CoreMatchers.is("1"));
            }
        }
    }

    @Test
    public void assertGetBinaryStreamForColumnIndex() throws IOException, SQLException {
        for (Map.Entry<DatabaseType, ResultSet> each : resultSets.entrySet()) {
            if (((DatabaseType.MySQL) == (each.getKey())) || ((DatabaseType.PostgreSQL) == (each.getKey()))) {
                Assert.assertTrue(((each.getValue().getBinaryStream(1).read()) != (-1)));
            }
        }
    }

    @Test
    public void assertGetBinaryStreamForColumnLabel() throws IOException, SQLException {
        for (Map.Entry<DatabaseType, ResultSet> each : resultSets.entrySet()) {
            if (((DatabaseType.MySQL) == (each.getKey())) || ((DatabaseType.PostgreSQL) == (each.getKey()))) {
                Assert.assertTrue(((each.getValue().getBinaryStream(columnName).read()) != (-1)));
            }
        }
    }

    @Test
    public void assertGetCharacterStreamForColumnIndex() throws IOException, SQLException {
        for (Map.Entry<DatabaseType, ResultSet> each : resultSets.entrySet()) {
            if (((DatabaseType.MySQL) == (each.getKey())) || ((DatabaseType.PostgreSQL) == (each.getKey()))) {
                char[] c = new char[1];
                each.getValue().getCharacterStream(1).read(c);
                Assert.assertThat(c[0], CoreMatchers.is('1'));
            }
        }
    }

    @Test
    public void assertGetCharacterStreamForColumnLabel() throws IOException, SQLException {
        for (Map.Entry<DatabaseType, ResultSet> each : resultSets.entrySet()) {
            if (((DatabaseType.MySQL) == (each.getKey())) || ((DatabaseType.PostgreSQL) == (each.getKey()))) {
                char[] c = new char[1];
                each.getValue().getCharacterStream(columnName).read(c);
                Assert.assertThat(c[0], CoreMatchers.is('1'));
            }
        }
    }

    @Test
    public void assertGetBlobForColumnIndex() {
        for (Map.Entry<DatabaseType, ResultSet> each : resultSets.entrySet()) {
            if ((DatabaseType.H2) == (each.getKey())) {
                try {
                    Assert.assertTrue(((each.getValue().getBlob(1).length()) > 0));
                    Assert.fail("Expected an SQLException to be thrown");
                } catch (final Exception ex) {
                    Assert.assertFalse(ex.getMessage().isEmpty());
                }
            }
        }
    }

    @Test
    public void assertGetBlobForColumnLabel() {
        for (Map.Entry<DatabaseType, ResultSet> each : resultSets.entrySet()) {
            if ((DatabaseType.H2) == (each.getKey())) {
                try {
                    Assert.assertTrue(((each.getValue().getBlob(columnName).length()) > 0));
                    Assert.fail("Expected an SQLException to be thrown");
                } catch (final Exception ex) {
                    Assert.assertFalse(ex.getMessage().isEmpty());
                }
            }
        }
    }

    @Test
    public void assertGetClobForColumnIndex() {
        for (Map.Entry<DatabaseType, ResultSet> each : resultSets.entrySet()) {
            try {
                Assert.assertThat(each.getValue().getClob(1).getSubString(1, 2), CoreMatchers.is("10"));
                Assert.fail("Expected an SQLException to be thrown");
            } catch (final Exception ex) {
                Assert.assertFalse(ex.getMessage().isEmpty());
            }
        }
    }

    @Test
    public void assertGetClobForColumnLabel() {
        for (Map.Entry<DatabaseType, ResultSet> each : resultSets.entrySet()) {
            try {
                Assert.assertThat(each.getValue().getClob(columnName).getSubString(1, 2), CoreMatchers.is("10"));
                Assert.fail("Expected an SQLException to be thrown");
            } catch (final Exception ex) {
                Assert.assertFalse(ex.getMessage().isEmpty());
            }
        }
    }

    @Test
    public void assertGetURLForColumnIndex() {
        for (ResultSet each : resultSets.values()) {
            try {
                each.getURL(1);
                Assert.fail("Expected an SQLException to be thrown");
            } catch (final Exception ex) {
                Assert.assertFalse(ex.getMessage().isEmpty());
            }
        }
    }

    @Test
    public void assertGetURLForColumnLabel() {
        for (ResultSet each : resultSets.values()) {
            try {
                each.getURL(columnName);
                Assert.fail("Expected an SQLException to be thrown");
            } catch (final Exception ex) {
                Assert.assertFalse(ex.getMessage().isEmpty());
            }
        }
    }

    @Test
    public void assertGetSQLXMLForColumnIndex() throws SQLException {
        for (Map.Entry<DatabaseType, ResultSet> each : resultSets.entrySet()) {
            if ((DatabaseType.Oracle) == (each.getKey())) {
                continue;
            }
            if (((DatabaseType.H2) == (each.getKey())) || ((DatabaseType.SQLServer) == (each.getKey()))) {
                try {
                    each.getValue().getSQLXML(1);
                    Assert.fail("Expected an SQLException to be thrown");
                } catch (final Exception ex) {
                    Assert.assertFalse(ex.getMessage().isEmpty());
                }
            } else {
                Assert.assertThat(each.getValue().getSQLXML(1).getString(), CoreMatchers.is("10"));
            }
        }
    }

    @Test
    public void assertGetSQLXMLForColumnLabel() throws SQLException {
        for (Map.Entry<DatabaseType, ResultSet> each : resultSets.entrySet()) {
            if ((DatabaseType.Oracle) == (each.getKey())) {
                continue;
            }
            if (((DatabaseType.H2) == (each.getKey())) || ((DatabaseType.SQLServer) == (each.getKey()))) {
                try {
                    each.getValue().getSQLXML(columnName);
                    Assert.fail("Expected an SQLException to be thrown");
                } catch (final Exception ex) {
                    Assert.assertFalse(ex.getMessage().isEmpty());
                }
            } else {
                Assert.assertThat(each.getValue().getSQLXML(columnName).getString(), CoreMatchers.is("10"));
            }
        }
    }

    @Test
    public void assertGetObjectForColumnIndex() throws SQLException {
        for (ResultSet each : resultSets.values()) {
            Assert.assertThat(each.getObject(1).toString(), CoreMatchers.is("10"));
        }
    }

    @Test
    public void assertGetObjectForColumnLabel() throws SQLException {
        for (ResultSet each : resultSets.values()) {
            Assert.assertThat(each.getObject(columnName).toString(), CoreMatchers.is("10"));
        }
    }
}

