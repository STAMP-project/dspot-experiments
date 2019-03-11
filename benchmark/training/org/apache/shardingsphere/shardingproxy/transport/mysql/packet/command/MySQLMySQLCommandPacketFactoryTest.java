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
package org.apache.shardingsphere.shardingproxy.transport.mysql.packet.command;


import MySQLCommandPacketType.COM_BINLOG_DUMP;
import MySQLCommandPacketType.COM_BINLOG_DUMP_GTID;
import MySQLCommandPacketType.COM_CHANGE_USER;
import MySQLCommandPacketType.COM_CONNECT;
import MySQLCommandPacketType.COM_CONNECT_OUT;
import MySQLCommandPacketType.COM_CREATE_DB;
import MySQLCommandPacketType.COM_DAEMON;
import MySQLCommandPacketType.COM_DEBUG;
import MySQLCommandPacketType.COM_DELAYED_INSERT;
import MySQLCommandPacketType.COM_DROP_DB;
import MySQLCommandPacketType.COM_FIELD_LIST;
import MySQLCommandPacketType.COM_INIT_DB;
import MySQLCommandPacketType.COM_PING;
import MySQLCommandPacketType.COM_PROCESS_INFO;
import MySQLCommandPacketType.COM_PROCESS_KILL;
import MySQLCommandPacketType.COM_QUERY;
import MySQLCommandPacketType.COM_QUIT;
import MySQLCommandPacketType.COM_REFRESH;
import MySQLCommandPacketType.COM_REGISTER_SLAVE;
import MySQLCommandPacketType.COM_RESET_CONNECTION;
import MySQLCommandPacketType.COM_SET_OPTION;
import MySQLCommandPacketType.COM_SHUTDOWN;
import MySQLCommandPacketType.COM_SLEEP;
import MySQLCommandPacketType.COM_STATISTICS;
import MySQLCommandPacketType.COM_STMT_CLOSE;
import MySQLCommandPacketType.COM_STMT_EXECUTE;
import MySQLCommandPacketType.COM_STMT_FETCH;
import MySQLCommandPacketType.COM_STMT_PREPARE;
import MySQLCommandPacketType.COM_STMT_RESET;
import MySQLCommandPacketType.COM_STMT_SEND_LONG_DATA;
import MySQLCommandPacketType.COM_TABLE_DUMP;
import MySQLCommandPacketType.COM_TIME;
import MySQLNewParametersBoundFlag.PARAMETER_TYPE_EXIST;
import java.sql.SQLException;
import org.apache.shardingsphere.shardingproxy.transport.mysql.packet.command.admin.MySQLUnsupportedCommandPacket;
import org.apache.shardingsphere.shardingproxy.transport.mysql.packet.command.admin.initdb.MySQLComInitDbPacket;
import org.apache.shardingsphere.shardingproxy.transport.mysql.packet.command.admin.ping.MySQLComPingPacket;
import org.apache.shardingsphere.shardingproxy.transport.mysql.packet.command.admin.quit.MySQLComQuitPacket;
import org.apache.shardingsphere.shardingproxy.transport.mysql.packet.command.query.binary.MySQLBinaryStatementRegistry;
import org.apache.shardingsphere.shardingproxy.transport.mysql.packet.command.query.binary.close.MySQLComStmtClosePacket;
import org.apache.shardingsphere.shardingproxy.transport.mysql.packet.command.query.binary.execute.MySQLQueryComStmtExecutePacket;
import org.apache.shardingsphere.shardingproxy.transport.mysql.packet.command.query.binary.prepare.MySQLComStmtPreparePacket;
import org.apache.shardingsphere.shardingproxy.transport.mysql.packet.command.query.text.fieldlist.MySQLComFieldListPacket;
import org.apache.shardingsphere.shardingproxy.transport.mysql.packet.command.query.text.query.MySQLComQueryPacket;
import org.apache.shardingsphere.shardingproxy.transport.mysql.payload.MySQLPacketPayload;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public final class MySQLMySQLCommandPacketFactoryTest {
    @Mock
    private MySQLPacketPayload payload;

    @Test
    public void assertNewInstanceWithComQuitPacket() throws SQLException {
        Assert.assertThat(MySQLCommandPacketFactory.newInstance(COM_QUIT, payload), CoreMatchers.instanceOf(MySQLComQuitPacket.class));
    }

    @Test
    public void assertNewInstanceWithComInitDbPacket() throws SQLException {
        Assert.assertThat(MySQLCommandPacketFactory.newInstance(COM_INIT_DB, payload), CoreMatchers.instanceOf(MySQLComInitDbPacket.class));
    }

    @Test
    public void assertNewInstanceWithComFieldListPacket() throws SQLException {
        Assert.assertThat(MySQLCommandPacketFactory.newInstance(COM_FIELD_LIST, payload), CoreMatchers.instanceOf(MySQLComFieldListPacket.class));
    }

    @Test
    public void assertNewInstanceWithComQueryPacket() throws SQLException {
        Mockito.when(payload.readStringEOF()).thenReturn("SHOW TABLES");
        Assert.assertThat(MySQLCommandPacketFactory.newInstance(COM_QUERY, payload), CoreMatchers.instanceOf(MySQLComQueryPacket.class));
    }

    @Test
    public void assertNewInstanceWithComStmtPreparePacket() throws SQLException {
        Assert.assertThat(MySQLCommandPacketFactory.newInstance(COM_STMT_PREPARE, payload), CoreMatchers.instanceOf(MySQLComStmtPreparePacket.class));
    }

    @Test
    public void assertNewInstanceWithComStmtExecutePacket() throws SQLException {
        Mockito.when(payload.readInt1()).thenReturn(PARAMETER_TYPE_EXIST.getValue());
        Mockito.when(payload.readInt4()).thenReturn(1);
        MySQLBinaryStatementRegistry.getInstance().register("SELECT * FROM t_order", 1);
        Assert.assertThat(MySQLCommandPacketFactory.newInstance(COM_STMT_EXECUTE, payload), CoreMatchers.instanceOf(MySQLQueryComStmtExecutePacket.class));
    }

    @Test
    public void assertNewInstanceWithComStmtClosePacket() throws SQLException {
        Assert.assertThat(MySQLCommandPacketFactory.newInstance(COM_STMT_CLOSE, payload), CoreMatchers.instanceOf(MySQLComStmtClosePacket.class));
    }

    @Test
    public void assertNewInstanceWithComPingPacket() throws SQLException {
        Assert.assertThat(MySQLCommandPacketFactory.newInstance(COM_PING, payload), CoreMatchers.instanceOf(MySQLComPingPacket.class));
    }

    @Test
    public void assertNewInstanceWithComSleepPacket() throws SQLException {
        Assert.assertThat(MySQLCommandPacketFactory.newInstance(COM_SLEEP, payload), CoreMatchers.instanceOf(MySQLUnsupportedCommandPacket.class));
    }

    @Test
    public void assertNewInstanceWithComCreateDbPacket() throws SQLException {
        Assert.assertThat(MySQLCommandPacketFactory.newInstance(COM_CREATE_DB, payload), CoreMatchers.instanceOf(MySQLUnsupportedCommandPacket.class));
    }

    @Test
    public void assertNewInstanceWithComDropDbPacket() throws SQLException {
        Assert.assertThat(MySQLCommandPacketFactory.newInstance(COM_DROP_DB, payload), CoreMatchers.instanceOf(MySQLUnsupportedCommandPacket.class));
    }

    @Test
    public void assertNewInstanceWithComRefreshPacket() throws SQLException {
        Assert.assertThat(MySQLCommandPacketFactory.newInstance(COM_REFRESH, payload), CoreMatchers.instanceOf(MySQLUnsupportedCommandPacket.class));
    }

    @Test
    public void assertNewInstanceWithComShutDownPacket() throws SQLException {
        Assert.assertThat(MySQLCommandPacketFactory.newInstance(COM_SHUTDOWN, payload), CoreMatchers.instanceOf(MySQLUnsupportedCommandPacket.class));
    }

    @Test
    public void assertNewInstanceWithComStatisticsPacket() throws SQLException {
        Assert.assertThat(MySQLCommandPacketFactory.newInstance(COM_STATISTICS, payload), CoreMatchers.instanceOf(MySQLUnsupportedCommandPacket.class));
    }

    @Test
    public void assertNewInstanceWithComProcessInfoPacket() throws SQLException {
        Assert.assertThat(MySQLCommandPacketFactory.newInstance(COM_PROCESS_INFO, payload), CoreMatchers.instanceOf(MySQLUnsupportedCommandPacket.class));
    }

    @Test
    public void assertNewInstanceWithComConnectPacket() throws SQLException {
        Assert.assertThat(MySQLCommandPacketFactory.newInstance(COM_CONNECT, payload), CoreMatchers.instanceOf(MySQLUnsupportedCommandPacket.class));
    }

    @Test
    public void assertNewInstanceWithComProcessKillPacket() throws SQLException {
        Assert.assertThat(MySQLCommandPacketFactory.newInstance(COM_PROCESS_KILL, payload), CoreMatchers.instanceOf(MySQLUnsupportedCommandPacket.class));
    }

    @Test
    public void assertNewInstanceWithComDebugPacket() throws SQLException {
        Assert.assertThat(MySQLCommandPacketFactory.newInstance(COM_DEBUG, payload), CoreMatchers.instanceOf(MySQLUnsupportedCommandPacket.class));
    }

    @Test
    public void assertNewInstanceWithComTimePacket() throws SQLException {
        Assert.assertThat(MySQLCommandPacketFactory.newInstance(COM_TIME, payload), CoreMatchers.instanceOf(MySQLUnsupportedCommandPacket.class));
    }

    @Test
    public void assertNewInstanceWithComDelayedInsertPacket() throws SQLException {
        Assert.assertThat(MySQLCommandPacketFactory.newInstance(COM_DELAYED_INSERT, payload), CoreMatchers.instanceOf(MySQLUnsupportedCommandPacket.class));
    }

    @Test
    public void assertNewInstanceWithComChangeUserPacket() throws SQLException {
        Assert.assertThat(MySQLCommandPacketFactory.newInstance(COM_CHANGE_USER, payload), CoreMatchers.instanceOf(MySQLUnsupportedCommandPacket.class));
    }

    @Test
    public void assertNewInstanceWithComBinlogDumpPacket() throws SQLException {
        Assert.assertThat(MySQLCommandPacketFactory.newInstance(COM_BINLOG_DUMP, payload), CoreMatchers.instanceOf(MySQLUnsupportedCommandPacket.class));
    }

    @Test
    public void assertNewInstanceWithComTableDumpPacket() throws SQLException {
        Assert.assertThat(MySQLCommandPacketFactory.newInstance(COM_TABLE_DUMP, payload), CoreMatchers.instanceOf(MySQLUnsupportedCommandPacket.class));
    }

    @Test
    public void assertNewInstanceWithComConnectOutPacket() throws SQLException {
        Assert.assertThat(MySQLCommandPacketFactory.newInstance(COM_CONNECT_OUT, payload), CoreMatchers.instanceOf(MySQLUnsupportedCommandPacket.class));
    }

    @Test
    public void assertNewInstanceWithComRegisterSlavePacket() throws SQLException {
        Assert.assertThat(MySQLCommandPacketFactory.newInstance(COM_REGISTER_SLAVE, payload), CoreMatchers.instanceOf(MySQLUnsupportedCommandPacket.class));
    }

    @Test
    public void assertNewInstanceWithComStmtSendLongDataPacket() throws SQLException {
        Assert.assertThat(MySQLCommandPacketFactory.newInstance(COM_STMT_SEND_LONG_DATA, payload), CoreMatchers.instanceOf(MySQLUnsupportedCommandPacket.class));
    }

    @Test
    public void assertNewInstanceWithComStmtResetPacket() throws SQLException {
        Assert.assertThat(MySQLCommandPacketFactory.newInstance(COM_STMT_RESET, payload), CoreMatchers.instanceOf(MySQLUnsupportedCommandPacket.class));
    }

    @Test
    public void assertNewInstanceWithComSetOptionPacket() throws SQLException {
        Assert.assertThat(MySQLCommandPacketFactory.newInstance(COM_SET_OPTION, payload), CoreMatchers.instanceOf(MySQLUnsupportedCommandPacket.class));
    }

    @Test
    public void assertNewInstanceWithComStmtFetchPacket() throws SQLException {
        Assert.assertThat(MySQLCommandPacketFactory.newInstance(COM_STMT_FETCH, payload), CoreMatchers.instanceOf(MySQLUnsupportedCommandPacket.class));
    }

    @Test
    public void assertNewInstanceWithComDaemonPacket() throws SQLException {
        Assert.assertThat(MySQLCommandPacketFactory.newInstance(COM_DAEMON, payload), CoreMatchers.instanceOf(MySQLUnsupportedCommandPacket.class));
    }

    @Test
    public void assertNewInstanceWithComBinlogDumpGTIDPacket() throws SQLException {
        Assert.assertThat(MySQLCommandPacketFactory.newInstance(COM_BINLOG_DUMP_GTID, payload), CoreMatchers.instanceOf(MySQLUnsupportedCommandPacket.class));
    }

    @Test
    public void assertNewInstanceWithComResetConnectionPacket() throws SQLException {
        Assert.assertThat(MySQLCommandPacketFactory.newInstance(COM_RESET_CONNECTION, payload), CoreMatchers.instanceOf(MySQLUnsupportedCommandPacket.class));
    }
}

