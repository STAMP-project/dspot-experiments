/**
 * Copyright Debezium Authors.
 *
 * Licensed under the Apache Software License version 2.0, available at http://www.apache.org/licenses/LICENSE-2.0
 */
package io.debezium.connector.sqlserver;


import Heartbeat.HEARTBEAT_INTERVAL;
import Schema.INT32_SCHEMA;
import Schema.OPTIONAL_INT32_SCHEMA;
import Schema.OPTIONAL_STRING_SCHEMA;
import SnapshotIsolationMode.EXCLUSIVE;
import SnapshotIsolationMode.READ_UNCOMMITTED;
import SnapshotIsolationMode.REPEATABLE_READ;
import SnapshotIsolationMode.SNAPSHOT;
import SnapshotMode.INITIAL;
import SnapshotMode.INITIAL_SCHEMA_ONLY;
import SqlServerConnectorConfig.COLUMN_BLACKLIST;
import SqlServerConnectorConfig.SNAPSHOT_MODE;
import SqlServerConnectorConfig.TABLE_WHITELIST;
import Testing.Files;
import io.debezium.config.Configuration;
import io.debezium.connector.sqlserver.util.TestHelper;
import io.debezium.data.SchemaAndValueField;
import io.debezium.data.SourceRecordAssert;
import io.debezium.doc.FixFor;
import io.debezium.embedded.AbstractConnectorTest;
import java.util.Arrays;
import java.util.List;
import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.data.SchemaBuilder;
import org.apache.kafka.connect.data.Struct;
import org.apache.kafka.connect.source.SourceRecord;
import org.fest.assertions.Assertions;
import org.fest.assertions.MapAssert;
import org.junit.Assert;
import org.junit.Test;


/**
 * Integration test for the Debezium SQL Server connector.
 *
 * @author Jiri Pechanec
 */
public class SnapshotIT extends AbstractConnectorTest {
    private static final int INITIAL_RECORDS_PER_TABLE = 500;

    private static final int STREAMING_RECORDS_PER_TABLE = 500;

    private SqlServerConnection connection;

    @Test
    public void takeSnapshotInExclusiveMode() throws Exception {
        takeSnapshot(EXCLUSIVE);
    }

    @Test
    public void takeSnapshotInSnapshotMode() throws Exception {
        takeSnapshot(SNAPSHOT);
    }

    @Test
    public void takeSnapshotInRepeatableReadMode() throws Exception {
        takeSnapshot(REPEATABLE_READ);
    }

    @Test
    public void takeSnapshotInReadUncommittedMode() throws Exception {
        takeSnapshot(READ_UNCOMMITTED);
    }

    @Test
    public void takeSnapshotAndStartStreaming() throws Exception {
        final Configuration config = TestHelper.defaultConfig().build();
        start(SqlServerConnector.class, config);
        assertConnectorIsRunning();
        // Ignore initial records
        consumeRecordsByTopic(SnapshotIT.INITIAL_RECORDS_PER_TABLE);
        testStreaming();
    }

    @Test
    public void takeSchemaOnlySnapshotAndStartStreaming() throws Exception {
        final Configuration config = TestHelper.defaultConfig().with(SNAPSHOT_MODE, INITIAL_SCHEMA_ONLY).build();
        start(SqlServerConnector.class, config);
        assertConnectorIsRunning();
        TestHelper.waitForSnapshotToBeCompleted();
        testStreaming();
    }

    @Test
    @FixFor("DBZ-1031")
    public void takeSnapshotFromTableWithReservedName() throws Exception {
        connection.execute("CREATE TABLE [User] (id int, name varchar(30), primary key(id))");
        for (int i = 0; i < (SnapshotIT.INITIAL_RECORDS_PER_TABLE); i++) {
            connection.execute(String.format("INSERT INTO [User] VALUES(%s, '%s')", i, ("name" + i)));
        }
        TestHelper.enableTableCdc(connection, "User");
        initializeConnectorTestFramework();
        Files.delete(TestHelper.DB_HISTORY_PATH);
        final Configuration config = TestHelper.defaultConfig().with("table.whitelist", "dbo.User").build();
        start(SqlServerConnector.class, config);
        assertConnectorIsRunning();
        final SourceRecords records = consumeRecordsByTopic(SnapshotIT.INITIAL_RECORDS_PER_TABLE);
        final List<SourceRecord> user = records.recordsForTopic("server1.dbo.User");
        assertThat(user).hasSize(SnapshotIT.INITIAL_RECORDS_PER_TABLE);
        for (int i = 0; i < (SnapshotIT.INITIAL_RECORDS_PER_TABLE); i++) {
            final SourceRecord record1 = user.get(i);
            final List<SchemaAndValueField> expectedKey1 = Arrays.asList(new SchemaAndValueField("id", Schema.INT32_SCHEMA, i));
            final List<SchemaAndValueField> expectedRow1 = Arrays.asList(new SchemaAndValueField("id", Schema.INT32_SCHEMA, i), new SchemaAndValueField("name", Schema.OPTIONAL_STRING_SCHEMA, ("name" + i)));
            final Struct key1 = ((Struct) (record1.key()));
            final Struct value1 = ((Struct) (record1.value()));
            assertRecord(key1, expectedKey1);
            assertRecord(((Struct) (value1.get("after"))), expectedRow1);
            assertThat(record1.sourceOffset()).includes(MapAssert.entry("snapshot", true), MapAssert.entry("snapshot_completed", (i == ((SnapshotIT.INITIAL_RECORDS_PER_TABLE) - 1))));
            Assert.assertNull(value1.get("before"));
        }
    }

    @Test
    public void takeSchemaOnlySnapshotAndSendHeartbeat() throws Exception {
        final Configuration config = TestHelper.defaultConfig().with(SNAPSHOT_MODE, INITIAL_SCHEMA_ONLY).with(HEARTBEAT_INTERVAL, 300000).build();
        start(SqlServerConnector.class, config);
        assertConnectorIsRunning();
        TestHelper.waitForSnapshotToBeCompleted();
        final SourceRecord record = consumeRecord();
        Assertions.assertThat(record).isNotNull();
        Assertions.assertThat(record.topic()).startsWith("__debezium-heartbeat");
    }

    @Test
    @FixFor("DBZ-1067")
    public void blacklistColumn() throws Exception {
        connection.execute("CREATE TABLE blacklist_column_table_a (id int, name varchar(30), amount integer primary key(id))", "CREATE TABLE blacklist_column_table_b (id int, name varchar(30), amount integer primary key(id))");
        connection.execute("INSERT INTO blacklist_column_table_a VALUES(10, 'some_name', 120)");
        connection.execute("INSERT INTO blacklist_column_table_b VALUES(11, 'some_name', 447)");
        TestHelper.enableTableCdc(connection, "blacklist_column_table_a");
        TestHelper.enableTableCdc(connection, "blacklist_column_table_b");
        final Configuration config = TestHelper.defaultConfig().with(SNAPSHOT_MODE, INITIAL).with(COLUMN_BLACKLIST, "dbo.blacklist_column_table_a.amount").with(TABLE_WHITELIST, "dbo.blacklist_column_table_a,dbo.blacklist_column_table_b").build();
        start(SqlServerConnector.class, config);
        assertConnectorIsRunning();
        final SourceRecords records = consumeRecordsByTopic(2);
        final List<SourceRecord> tableA = records.recordsForTopic("server1.dbo.blacklist_column_table_a");
        final List<SourceRecord> tableB = records.recordsForTopic("server1.dbo.blacklist_column_table_b");
        Schema expectedSchemaA = SchemaBuilder.struct().optional().name("server1.dbo.blacklist_column_table_a.Value").field("id", INT32_SCHEMA).field("name", OPTIONAL_STRING_SCHEMA).build();
        Struct expectedValueA = put("id", 10).put("name", "some_name");
        Schema expectedSchemaB = SchemaBuilder.struct().optional().name("server1.dbo.blacklist_column_table_b.Value").field("id", INT32_SCHEMA).field("name", OPTIONAL_STRING_SCHEMA).field("amount", OPTIONAL_INT32_SCHEMA).build();
        Struct expectedValueB = put("id", 11).put("name", "some_name").put("amount", 447);
        Assertions.assertThat(tableA).hasSize(1);
        SourceRecordAssert.assertThat(tableA.get(0)).valueAfterFieldIsEqualTo(expectedValueA).valueAfterFieldSchemaIsEqualTo(expectedSchemaA);
        Assertions.assertThat(tableB).hasSize(1);
        SourceRecordAssert.assertThat(tableB.get(0)).valueAfterFieldIsEqualTo(expectedValueB).valueAfterFieldSchemaIsEqualTo(expectedSchemaB);
        stopConnector();
    }
}

