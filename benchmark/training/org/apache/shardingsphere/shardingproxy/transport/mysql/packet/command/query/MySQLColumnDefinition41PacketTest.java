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
package org.apache.shardingsphere.shardingproxy.transport.mysql.packet.command.query;


import MySQLColumnType.MYSQL_TYPE_LONG;
import MySQLServerInfo.CHARSET;
import ShardingConstant.LOGIC_SCHEMA_NAME;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import org.apache.shardingsphere.shardingproxy.transport.mysql.payload.MySQLPacketPayload;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public final class MySQLColumnDefinition41PacketTest {
    @Mock
    private ResultSetMetaData resultSetMetaData;

    @Mock
    private MySQLPacketPayload payload;

    @Test
    public void assertWriteWithResultSetMetaData() throws SQLException {
        Mockito.when(resultSetMetaData.getSchemaName(1)).thenReturn(LOGIC_SCHEMA_NAME);
        Mockito.when(resultSetMetaData.getTableName(1)).thenReturn("tbl");
        Mockito.when(resultSetMetaData.getColumnLabel(1)).thenReturn("id");
        Mockito.when(resultSetMetaData.getColumnName(1)).thenReturn("id");
        Mockito.when(resultSetMetaData.getColumnDisplaySize(1)).thenReturn(10);
        Mockito.when(resultSetMetaData.getColumnType(1)).thenReturn(Types.INTEGER);
        MySQLColumnDefinition41Packet actual = new MySQLColumnDefinition41Packet(1, resultSetMetaData, 1);
        Assert.assertThat(actual.getSequenceId(), CoreMatchers.is(1));
        actual.write(payload);
        verifyWrite();
    }

    @Test
    public void assertWriteWithPayload() {
        Mockito.when(payload.readInt1()).thenReturn(1, MYSQL_TYPE_LONG.getValue(), 0);
        Mockito.when(payload.readInt2()).thenReturn(CHARSET, 0);
        Mockito.when(payload.readInt4()).thenReturn(10);
        Mockito.when(payload.readIntLenenc()).thenReturn(((long) (12)));
        Mockito.when(payload.readStringLenenc()).thenReturn("def", LOGIC_SCHEMA_NAME, "tbl", "tbl", "id", "id");
        MySQLColumnDefinition41Packet actual = new MySQLColumnDefinition41Packet(payload);
        Assert.assertThat(actual.getSequenceId(), CoreMatchers.is(1));
        actual.write(payload);
        verifyWrite();
    }
}

