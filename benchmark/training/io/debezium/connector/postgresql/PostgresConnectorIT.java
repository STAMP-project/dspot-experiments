/**
 * Copyright Debezium Authors.
 *
 * Licensed under the Apache Software License version 2.0, available at http://www.apache.org/licenses/LICENSE-2.0
 */
package io.debezium.connector.postgresql;


import Configuration.Builder;
import EmbeddedEngine.CompletionCallback;
import LogicalDecoder.DECODERBUFS;
import PostgresConnectorConfig.ALL_FIELDS;
import PostgresConnectorConfig.COLUMN_BLACKLIST;
import PostgresConnectorConfig.DECIMAL_HANDLING_MODE;
import PostgresConnectorConfig.DEFAULT_MAX_BATCH_SIZE;
import PostgresConnectorConfig.DEFAULT_MAX_QUEUE_SIZE;
import PostgresConnectorConfig.DEFAULT_POLL_INTERVAL_MILLIS;
import PostgresConnectorConfig.DEFAULT_PORT;
import PostgresConnectorConfig.DEFAULT_ROWS_FETCH_SIZE;
import PostgresConnectorConfig.DEFAULT_SNAPSHOT_LOCK_TIMEOUT_MILLIS;
import PostgresConnectorConfig.DROP_SLOT_ON_STOP;
import PostgresConnectorConfig.DecimalHandlingMode.PRECISE;
import PostgresConnectorConfig.INCLUDE_UNKNOWN_DATATYPES;
import PostgresConnectorConfig.MAX_BATCH_SIZE;
import PostgresConnectorConfig.MAX_QUEUE_SIZE;
import PostgresConnectorConfig.ON_CONNECT_STATEMENTS;
import PostgresConnectorConfig.PLUGIN_NAME;
import PostgresConnectorConfig.POLL_INTERVAL_MS;
import PostgresConnectorConfig.PORT;
import PostgresConnectorConfig.ROWS_FETCH_SIZE;
import PostgresConnectorConfig.SCHEMA_BLACKLIST;
import PostgresConnectorConfig.SCHEMA_WHITELIST;
import PostgresConnectorConfig.SERVER_NAME;
import PostgresConnectorConfig.SLOT_NAME;
import PostgresConnectorConfig.SNAPSHOT_LOCK_TIMEOUT_MS;
import PostgresConnectorConfig.SNAPSHOT_MODE;
import PostgresConnectorConfig.SSL_CLIENT_CERT;
import PostgresConnectorConfig.SSL_CLIENT_KEY;
import PostgresConnectorConfig.SSL_CLIENT_KEY_PASSWORD;
import PostgresConnectorConfig.SSL_MODE;
import PostgresConnectorConfig.SSL_ROOT_CERT;
import PostgresConnectorConfig.SSL_SOCKET_FACTORY;
import PostgresConnectorConfig.SecureConnectionMode.DISABLED;
import PostgresConnectorConfig.SecureConnectionMode.REQUIRED;
import PostgresConnectorConfig.TABLE_BLACKLIST;
import PostgresConnectorConfig.TABLE_WHITELIST;
import PostgresConnectorConfig.TCP_KEEPALIVE;
import PostgresConnectorConfig.TIME_PRECISION_MODE;
import PostgresConnectorConfig.TOPIC_SELECTION_STRATEGY;
import PostgresConnectorConfig.TopicSelectionStrategy.TOPIC_PER_TABLE;
import ReplicationConnection.Builder.DEFAULT_SLOT_NAME;
import TemporalPrecisionMode.ADAPTIVE;
import io.debezium.config.Configuration;
import io.debezium.connector.postgresql.connection.PostgresConnection;
import io.debezium.data.VerifyRecord;
import io.debezium.doc.FixFor;
import io.debezium.embedded.AbstractConnectorTest;
import io.debezium.embedded.EmbeddedEngine;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import junit.framework.TestCase;
import org.apache.kafka.common.config.Config;
import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.connect.data.Struct;
import org.apache.kafka.connect.source.SourceRecord;
import org.fest.assertions.Assertions;
import org.junit.Assert;
import org.junit.Test;


/**
 * Integration test for {@link PostgresConnector} using an {@link io.debezium.embedded.EmbeddedEngine}
 *
 * @author Horia Chiorean (hchiorea@redhat.com)
 */
public class PostgresConnectorIT extends AbstractConnectorTest {
    /* Specific tests that need to extend the initial DDL set should do it in a form of
    TestHelper.execute(SETUP_TABLES_STMT + ADDITIONAL_STATEMENTS)
     */
    private static final String INSERT_STMT = "INSERT INTO s1.a (aa) VALUES (1);" + "INSERT INTO s2.a (aa) VALUES (1);";

    private static final String SETUP_TABLES_STMT = ("DROP SCHEMA IF EXISTS s1 CASCADE;" + (((("DROP SCHEMA IF EXISTS s2 CASCADE;" + "CREATE SCHEMA s1; ") + "CREATE SCHEMA s2; ") + "CREATE TABLE s1.a (pk SERIAL, aa integer, PRIMARY KEY(pk));") + "CREATE TABLE s2.a (pk SERIAL, aa integer, bb varchar(20), PRIMARY KEY(pk));")) + (PostgresConnectorIT.INSERT_STMT);

    private PostgresConnector connector;

    @Test
    public void shouldValidateConnectorConfigDef() {
        connector = new PostgresConnector();
        ConfigDef configDef = connector.config();
        assertThat(configDef).isNotNull();
        ALL_FIELDS.forEach(this::validateFieldDef);
    }

    @Test
    public void shouldNotStartWithInvalidConfiguration() throws Exception {
        // use an empty configuration which should be invalid because of the lack of DB connection details
        Configuration config = Configuration.create().build();
        // we expect the engine will log at least one error, so preface it ...
        logger.info("Attempting to start the connector with an INVALID configuration, so MULTIPLE error messages & one exceptions will appear in the log");
        start(PostgresConnector.class, config, ( success, msg, error) -> {
            assertThat(success).isFalse();
            assertThat(error).isNotNull();
        });
        assertConnectorNotRunning();
    }

    @Test
    public void shouldValidateMinimalConfiguration() throws Exception {
        Configuration config = TestHelper.defaultConfig().build();
        Config validateConfig = new PostgresConnector().validate(config.asMap());
        validateConfig.configValues().forEach(( configValue) -> assertTrue(("Unexpected error for: " + (configValue.name())), configValue.errorMessages().isEmpty()));
    }

    @Test
    public void shouldValidateConfiguration() throws Exception {
        // use an empty configuration which should be invalid because of the lack of DB connection details
        Configuration config = Configuration.create().build();
        PostgresConnector connector = new PostgresConnector();
        Config validatedConfig = connector.validate(config.asMap());
        // validate that the required fields have errors
        assertConfigurationErrors(validatedConfig, PostgresConnectorConfig.HOSTNAME, 1);
        assertConfigurationErrors(validatedConfig, PostgresConnectorConfig.USER, 1);
        assertConfigurationErrors(validatedConfig, PostgresConnectorConfig.DATABASE_NAME, 1);
        // validate the non required fields
        validateField(validatedConfig, PLUGIN_NAME, DECODERBUFS.getValue());
        validateField(validatedConfig, SLOT_NAME, DEFAULT_SLOT_NAME);
        validateField(validatedConfig, DROP_SLOT_ON_STOP, Boolean.FALSE);
        validateField(validatedConfig, PORT, DEFAULT_PORT);
        validateField(validatedConfig, SERVER_NAME, null);
        validateField(validatedConfig, TOPIC_SELECTION_STRATEGY, TOPIC_PER_TABLE);
        validateField(validatedConfig, MAX_QUEUE_SIZE, DEFAULT_MAX_QUEUE_SIZE);
        validateField(validatedConfig, MAX_BATCH_SIZE, DEFAULT_MAX_BATCH_SIZE);
        validateField(validatedConfig, ROWS_FETCH_SIZE, DEFAULT_ROWS_FETCH_SIZE);
        validateField(validatedConfig, POLL_INTERVAL_MS, DEFAULT_POLL_INTERVAL_MILLIS);
        validateField(validatedConfig, SSL_MODE, DISABLED);
        validateField(validatedConfig, SSL_CLIENT_CERT, null);
        validateField(validatedConfig, SSL_CLIENT_KEY, null);
        validateField(validatedConfig, SSL_CLIENT_KEY_PASSWORD, null);
        validateField(validatedConfig, SSL_ROOT_CERT, null);
        validateField(validatedConfig, SCHEMA_WHITELIST, null);
        validateField(validatedConfig, SCHEMA_BLACKLIST, null);
        validateField(validatedConfig, TABLE_WHITELIST, null);
        validateField(validatedConfig, TABLE_BLACKLIST, null);
        validateField(validatedConfig, COLUMN_BLACKLIST, null);
        validateField(validatedConfig, SNAPSHOT_MODE, SnapshotMode.INITIAL);
        validateField(validatedConfig, SNAPSHOT_LOCK_TIMEOUT_MS, DEFAULT_SNAPSHOT_LOCK_TIMEOUT_MILLIS);
        validateField(validatedConfig, TIME_PRECISION_MODE, ADAPTIVE);
        validateField(validatedConfig, DECIMAL_HANDLING_MODE, PRECISE);
        validateField(validatedConfig, SSL_SOCKET_FACTORY, null);
        validateField(validatedConfig, TCP_KEEPALIVE, true);
    }

    @Test
    public void shouldSupportSSLParameters() throws Exception {
        // the default docker image we're testing against doesn't use SSL, so check that the connector fails to start when
        // SSL is enabled
        Configuration config = TestHelper.defaultConfig().with(SSL_MODE, REQUIRED).build();
        start(PostgresConnector.class, config, ( success, msg, error) -> {
            if (TestHelper.shouldSSLConnectionFail()) {
                // we expect the task to fail at startup when we're printing the server info
                assertThat(success).isFalse();
                assertThat(error).isInstanceOf(.class);
                Throwable cause = error.getCause();
                assertThat(cause).isInstanceOf(.class);
                assertThat(PSQLState.CONNECTION_REJECTED.getState().equals(((SQLException) (cause)).getSQLState()));
            }
        });
        if (TestHelper.shouldSSLConnectionFail()) {
            assertConnectorNotRunning();
        } else {
            assertConnectorIsRunning();
            Thread.sleep(10000);
            stopConnector();
        }
    }

    @Test
    public void shouldProduceEventsWithInitialSnapshot() throws Exception {
        TestHelper.execute(PostgresConnectorIT.SETUP_TABLES_STMT);
        Configuration.Builder configBuilder = TestHelper.defaultConfig().with(SNAPSHOT_MODE, SnapshotMode.INITIAL.getValue()).with(DROP_SLOT_ON_STOP, Boolean.FALSE);
        start(PostgresConnector.class, configBuilder.build());
        assertConnectorIsRunning();
        // check the records from the snapshot
        assertRecordsFromSnapshot(2, 1, 1);
        // insert 2 new records
        TestHelper.execute(PostgresConnectorIT.INSERT_STMT);
        assertRecordsAfterInsert(2, 2, 2);
        // now stop the connector
        stopConnector();
        assertNoRecordsToConsume();
        // insert some more records
        TestHelper.execute(PostgresConnectorIT.INSERT_STMT);
        // start the connector back up and check that a new snapshot has not been performed (we're running initial only mode)
        // but the 2 records that we were inserted while we were down will be retrieved
        start(PostgresConnector.class, configBuilder.with(DROP_SLOT_ON_STOP, Boolean.TRUE).build());
        assertConnectorIsRunning();
        assertRecordsAfterInsert(2, 3, 3);
    }

    @Test
    @FixFor("DBZ-997")
    public void shouldReceiveChangesForChangePKColumnDefinition() throws Exception {
        final String slotName = "pkcolumndef" + (new Random().nextInt(100));
        TestHelper.create().dropReplicationSlot(slotName);
        final PostgresConnectorConfig config = new PostgresConnectorConfig(TestHelper.defaultConfig().with(INCLUDE_UNKNOWN_DATATYPES, Boolean.FALSE).with(SCHEMA_WHITELIST, "changepk").with(DROP_SLOT_ON_STOP, Boolean.FALSE).with(SLOT_NAME, slotName).build());
        final String newPkField = "newpk";
        final String topicName = TestHelper.topicName("changepk.test_table");
        TestHelper.execute("CREATE SCHEMA IF NOT EXISTS changepk;", "DROP TABLE IF EXISTS changepk.test_table;", "CREATE TABLE changepk.test_table (pk SERIAL, text TEXT, PRIMARY KEY(pk));", "INSERT INTO changepk.test_table(text) VALUES ('insert');");
        start(PostgresConnector.class, config.getConfig());
        assertConnectorIsRunning();
        // wait for snapshot completion
        SourceRecords records = consumeRecordsByTopic(1);
        TestHelper.execute(("ALTER TABLE changepk.test_table DROP CONSTRAINT test_table_pkey;" + (("ALTER TABLE changepk.test_table RENAME COLUMN pk TO newpk;" + "ALTER TABLE changepk.test_table ADD PRIMARY KEY(newpk);") + "INSERT INTO changepk.test_table VALUES(2, 'newpkcol')")));
        records = consumeRecordsByTopic(1);
        SourceRecord insertRecord = records.recordsForTopic(topicName).get(0);
        TestCase.assertEquals(topicName, insertRecord.topic());
        VerifyRecord.isValidInsert(insertRecord, "newpk", 2);
        TestHelper.execute(("ALTER TABLE changepk.test_table ADD COLUMN pk2 SERIAL;" + (("ALTER TABLE changepk.test_table DROP CONSTRAINT test_table_pkey;" + "ALTER TABLE changepk.test_table ADD PRIMARY KEY(newpk,pk2);") + "INSERT INTO changepk.test_table VALUES(3, 'newpkcol', 8)")));
        records = consumeRecordsByTopic(1);
        insertRecord = records.recordsForTopic(topicName).get(0);
        TestCase.assertEquals(topicName, insertRecord.topic());
        VerifyRecord.isValidInsert(insertRecord, newPkField, 3);
        VerifyRecord.isValidInsert(insertRecord, "pk2", 8);
        stopConnector();
        // De-synchronize JDBC PK info and decoded event schema
        TestHelper.execute("INSERT INTO changepk.test_table VALUES(4, 'newpkcol', 20)");
        TestHelper.execute(("ALTER TABLE changepk.test_table DROP CONSTRAINT test_table_pkey;" + ((("ALTER TABLE changepk.test_table DROP COLUMN pk2;" + "ALTER TABLE changepk.test_table ADD COLUMN pk3 SERIAL;") + "ALTER TABLE changepk.test_table ADD PRIMARY KEY(newpk,pk3);") + "INSERT INTO changepk.test_table VALUES(5, 'dropandaddpkcol',10)")));
        start(PostgresConnector.class, config.getConfig());
        records = consumeRecordsByTopic(2);
        insertRecord = records.recordsForTopic(topicName).get(0);
        TestCase.assertEquals(topicName, insertRecord.topic());
        VerifyRecord.isValidInsert(insertRecord, newPkField, 4);
        Struct key = ((Struct) (insertRecord.key()));
        // The problematic record PK info is temporarily desynced
        assertThat(key.schema().field("pk2")).isNull();
        assertThat(key.schema().field("pk3")).isNull();
        insertRecord = records.recordsForTopic(topicName).get(1);
        TestCase.assertEquals(topicName, insertRecord.topic());
        VerifyRecord.isValidInsert(insertRecord, newPkField, 5);
        VerifyRecord.isValidInsert(insertRecord, "pk3", 10);
        key = ((Struct) (insertRecord.key()));
        assertThat(key.schema().field("pk2")).isNull();
        stopConnector();
        TestHelper.create().dropReplicationSlot(slotName);
        TestHelper.execute("DROP SCHEMA IF EXISTS changepk CASCADE;");
    }

    @Test
    @FixFor("DBZ-1021")
    public void shouldIgnoreEventsForDeletedTable() throws Exception {
        TestHelper.execute(PostgresConnectorIT.SETUP_TABLES_STMT);
        Configuration.Builder configBuilder = TestHelper.defaultConfig().with(SNAPSHOT_MODE, SnapshotMode.INITIAL.getValue()).with(DROP_SLOT_ON_STOP, Boolean.FALSE);
        start(PostgresConnector.class, configBuilder.build());
        assertConnectorIsRunning();
        // check the records from the snapshot
        assertRecordsFromSnapshot(2, 1, 1);
        // insert 2 new records
        TestHelper.execute(PostgresConnectorIT.INSERT_STMT);
        assertRecordsAfterInsert(2, 2, 2);
        // now stop the connector
        stopConnector();
        assertNoRecordsToConsume();
        // insert some more records and deleted the table
        TestHelper.execute(PostgresConnectorIT.INSERT_STMT);
        TestHelper.execute("DROP TABLE s1.a");
        start(PostgresConnector.class, configBuilder.with(DROP_SLOT_ON_STOP, Boolean.TRUE).build());
        assertConnectorIsRunning();
        SourceRecords actualRecords = consumeRecordsByTopic(1);
        assertThat(actualRecords.topics()).hasSize(1);
        assertThat(actualRecords.recordsForTopic(TestHelper.topicName("s2.a"))).hasSize(1);
    }

    @Test
    public void shouldIgnoreViews() throws Exception {
        TestHelper.execute(((PostgresConnectorIT.SETUP_TABLES_STMT) + "CREATE VIEW s1.myview AS SELECT * from s1.a;"));
        Configuration.Builder configBuilder = TestHelper.defaultConfig().with(SNAPSHOT_MODE, SnapshotMode.INITIAL.getValue()).with(DROP_SLOT_ON_STOP, Boolean.FALSE);
        start(PostgresConnector.class, configBuilder.build());
        assertConnectorIsRunning();
        // check the records from the snapshot
        assertRecordsFromSnapshot(2, 1, 1);
        // insert 2 new records
        TestHelper.execute(PostgresConnectorIT.INSERT_STMT);
        assertRecordsAfterInsert(2, 2, 2);
        // now stop the connector
        stopConnector();
        assertNoRecordsToConsume();
        // insert some more records
        TestHelper.execute(PostgresConnectorIT.INSERT_STMT);
        // start the connector back up and check that a new snapshot has not been performed (we're running initial only mode)
        // but the 2 records that we were inserted while we were down will be retrieved
        start(PostgresConnector.class, configBuilder.with(DROP_SLOT_ON_STOP, Boolean.TRUE).build());
        assertConnectorIsRunning();
        assertRecordsAfterInsert(2, 3, 3);
    }

    @Test
    @FixFor("DBZ-693")
    public void shouldExecuteOnConnectStatements() throws Exception {
        TestHelper.execute(PostgresConnectorIT.SETUP_TABLES_STMT);
        Configuration.Builder configBuilder = TestHelper.defaultConfig().with(SNAPSHOT_MODE, SnapshotMode.INITIAL.getValue()).with(ON_CONNECT_STATEMENTS, "INSERT INTO s1.a (aa) VALUES (2); INSERT INTO s2.a (aa, bb) VALUES (2, 'hello;; world');").with(DROP_SLOT_ON_STOP, Boolean.FALSE);
        start(PostgresConnector.class, configBuilder.build());
        assertConnectorIsRunning();
        SourceRecords actualRecords = consumeRecordsByTopic(7);
        assertKey(actualRecords.allRecordsInOrder().get(0), "pk", 1);
        assertKey(actualRecords.allRecordsInOrder().get(1), "pk", 2);
        // JdbcConnection#connection() is called multiple times during connector start-up,
        // so the given statements will be executed multiple times, resulting in multiple
        // records; here we're interested just in the first insert for s2.a
        assertValueField(actualRecords.allRecordsInOrder().get(6), "after/bb", "hello; world");
    }

    @Test
    public void shouldProduceEventsWhenSnapshotsAreNeverAllowed() throws InterruptedException {
        TestHelper.execute(PostgresConnectorIT.SETUP_TABLES_STMT);
        Configuration config = TestHelper.defaultConfig().with(SNAPSHOT_MODE, SnapshotMode.NEVER.getValue()).with(DROP_SLOT_ON_STOP, Boolean.TRUE).build();
        start(PostgresConnector.class, config);
        assertConnectorIsRunning();
        waitForAvailableRecords(100, TimeUnit.MILLISECONDS);
        // there shouldn't be any snapshot records
        assertNoRecordsToConsume();
        // insert and verify 2 new records
        TestHelper.execute(PostgresConnectorIT.INSERT_STMT);
        assertRecordsAfterInsert(2, 2, 2);
    }

    @Test
    public void shouldNotProduceEventsWithInitialOnlySnapshot() throws InterruptedException {
        TestHelper.execute(PostgresConnectorIT.SETUP_TABLES_STMT);
        Configuration config = TestHelper.defaultConfig().with(SNAPSHOT_MODE, SnapshotMode.INITIAL_ONLY.getValue()).with(DROP_SLOT_ON_STOP, Boolean.TRUE).build();
        start(PostgresConnector.class, config);
        assertConnectorIsRunning();
        // check the records from the snapshot
        assertRecordsFromSnapshot(2, 1, 1);
        // insert and verify that no events were received since the connector should not be streaming changes
        TestHelper.execute(PostgresConnectorIT.INSERT_STMT);
        waitForAvailableRecords(100, TimeUnit.MILLISECONDS);
        // there shouldn't be any  records
        assertNoRecordsToConsume();
    }

    @Test
    public void shouldProduceEventsWhenAlwaysTakingSnapshots() throws InterruptedException {
        TestHelper.execute(PostgresConnectorIT.SETUP_TABLES_STMT);
        Configuration.Builder configBuilder = TestHelper.defaultConfig().with(SNAPSHOT_MODE, SnapshotMode.ALWAYS.getValue()).with(DROP_SLOT_ON_STOP, Boolean.FALSE);
        start(PostgresConnector.class, configBuilder.build());
        assertConnectorIsRunning();
        // check the records from the snapshot
        assertRecordsFromSnapshot(2, 1, 1);
        // insert and verify 2 new records
        TestHelper.execute(PostgresConnectorIT.INSERT_STMT);
        assertRecordsAfterInsert(2, 2, 2);
        // now stop the connector
        stopConnector();
        assertNoRecordsToConsume();
        // start the connector back up and check that a new snapshot has been performed
        start(PostgresConnector.class, configBuilder.with(DROP_SLOT_ON_STOP, Boolean.TRUE).build());
        assertConnectorIsRunning();
        assertRecordsFromSnapshot(4, 1, 2, 1, 2);
    }

    @Test
    public void shouldResumeSnapshotIfFailingMidstream() throws Exception {
        // insert another set of rows so we can stop at certain point
        CountDownLatch latch = new CountDownLatch(1);
        String setupStmt = (PostgresConnectorIT.SETUP_TABLES_STMT) + (PostgresConnectorIT.INSERT_STMT);
        TestHelper.execute(setupStmt);
        Configuration.Builder configBuilder = TestHelper.defaultConfig().with(SNAPSHOT_MODE, SnapshotMode.INITIAL.getValue()).with(DROP_SLOT_ON_STOP, Boolean.FALSE);
        EmbeddedEngine.CompletionCallback completionCallback = ( success, message, error) -> {
            if (error != null) {
                latch.countDown();
            } else {
                fail("A controlled exception was expected....");
            }
        };
        start(PostgresConnector.class, configBuilder.build(), completionCallback, stopOnPKPredicate(2));
        // wait until we know we've raised the exception at startup AND the engine has been shutdown
        if (!(latch.await(10, TimeUnit.SECONDS))) {
            Assert.fail("did not reach stop condition in time");
        }
        // wait until we know we've raised the exception at startup AND the engine has been shutdown
        assertConnectorNotRunning();
        // just drain all the records
        consumeAvailableRecords(( record) -> {
        });
        // stop the engine altogether
        stopConnector();
        // make sure there are no records to consume
        assertNoRecordsToConsume();
        // start the connector back up and check that it took another full snapshot since previously it was stopped midstream
        start(PostgresConnector.class, configBuilder.with(DROP_SLOT_ON_STOP, Boolean.TRUE).build());
        assertConnectorIsRunning();
        // check that the snapshot was recreated
        assertRecordsFromSnapshot(4, 1, 2, 1, 2);
        // and we can stream records
        // insert and verify 2 new records
        TestHelper.execute(PostgresConnectorIT.INSERT_STMT);
        assertRecordsAfterInsert(2, 3, 3);
    }

    @Test
    public void shouldTakeBlacklistFiltersIntoAccount() throws Exception {
        String setupStmt = ((((((PostgresConnectorIT.SETUP_TABLES_STMT) + "CREATE TABLE s1.b (pk SERIAL, aa integer, bb integer, PRIMARY KEY(pk));") + "ALTER TABLE s1.a ADD COLUMN bb integer;") + "INSERT INTO s1.a (aa, bb) VALUES (2, 2);") + "INSERT INTO s1.a (aa, bb) VALUES (3, 3);") + "INSERT INTO s1.b (aa, bb) VALUES (4, 4);") + "INSERT INTO s2.a (aa) VALUES (5);";
        TestHelper.execute(setupStmt);
        Configuration.Builder configBuilder = TestHelper.defaultConfig().with(SNAPSHOT_MODE, SnapshotMode.INITIAL.getValue()).with(DROP_SLOT_ON_STOP, Boolean.TRUE).with(SCHEMA_BLACKLIST, "s2").with(TABLE_BLACKLIST, ".+b").with(COLUMN_BLACKLIST, ".+bb");
        start(PostgresConnector.class, configBuilder.build());
        assertConnectorIsRunning();
        // check the records from the snapshot take the filters into account
        SourceRecords actualRecords = consumeRecordsByTopic(4);// 3 records in s1.a and 1 in s1.b

        assertThat(actualRecords.recordsForTopic(TestHelper.topicName("s2.a"))).isNullOrEmpty();
        assertThat(actualRecords.recordsForTopic(TestHelper.topicName("s1.b"))).isNullOrEmpty();
        List<SourceRecord> recordsForS1a = actualRecords.recordsForTopic(TestHelper.topicName("s1.a"));
        assertThat(recordsForS1a.size()).isEqualTo(3);
        AtomicInteger pkValue = new AtomicInteger(1);
        recordsForS1a.forEach(( record) -> {
            VerifyRecord.isValidRead(record, PK_FIELD, pkValue.getAndIncrement());
            assertFieldAbsent(record, "bb");
        });
        // insert some more records and verify the filtering behavior
        String insertStmt = "INSERT INTO s1.b (aa, bb) VALUES (6, 6);" + "INSERT INTO s2.a (aa) VALUES (7);";
        TestHelper.execute(insertStmt);
        assertNoRecordsToConsume();
    }

    @Test
    @FixFor("DBZ-878")
    public void shouldReplaceInvalidTopicNameCharacters() throws Exception {
        String setupStmt = ((PostgresConnectorIT.SETUP_TABLES_STMT) + "CREATE TABLE s1.\"dbz_878_some|test@data\" (pk SERIAL, aa integer, PRIMARY KEY(pk));") + "INSERT INTO s1.\"dbz_878_some|test@data\" (aa) VALUES (123);";
        TestHelper.execute(setupStmt);
        Configuration.Builder configBuilder = TestHelper.defaultConfig().with(SNAPSHOT_MODE, SnapshotMode.INITIAL.getValue()).with(DROP_SLOT_ON_STOP, Boolean.TRUE).with(SCHEMA_WHITELIST, "s1").with(TABLE_WHITELIST, "s1\\.dbz_878_some\\|test@data");
        start(PostgresConnector.class, configBuilder.build());
        assertConnectorIsRunning();
        SourceRecords actualRecords = consumeRecordsByTopic(1);
        List<SourceRecord> records = actualRecords.recordsForTopic(TestHelper.topicName("s1.dbz_878_some_test_data"));
        assertThat(records.size()).isEqualTo(1);
        SourceRecord record = records.get(0);
        VerifyRecord.isValidRead(record, TestHelper.PK_FIELD, 1);
        String sourceTable = getStruct("source").getString("table");
        assertThat(sourceTable).isEqualTo("dbz_878_some|test@data");
    }

    @Test
    @FixFor("DBZ-965")
    public void shouldRegularlyFlushLsn() throws InterruptedException, SQLException {
        final int recordCount = 10;
        TestHelper.execute(PostgresConnectorIT.SETUP_TABLES_STMT);
        Configuration config = TestHelper.defaultConfig().with(SNAPSHOT_MODE, SnapshotMode.NEVER.getValue()).with(DROP_SLOT_ON_STOP, Boolean.TRUE).with(TABLE_WHITELIST, "s1.a").build();
        start(PostgresConnector.class, config);
        assertConnectorIsRunning();
        waitForAvailableRecords(100, TimeUnit.MILLISECONDS);
        // there shouldn't be any snapshot records
        assertNoRecordsToConsume();
        final Set<String> flushLsn = new HashSet<>();
        try (final PostgresConnection connection = TestHelper.create()) {
            flushLsn.add(getConfirmedFlushLsn(connection));
            for (int i = 2; i <= (recordCount + 2); i++) {
                TestHelper.execute(PostgresConnectorIT.INSERT_STMT);
                final SourceRecords actualRecords = consumeRecordsByTopic(1);
                assertThat(actualRecords.topics().size()).isEqualTo(1);
                assertThat(actualRecords.recordsForTopic(TestHelper.topicName("s1.a")).size()).isEqualTo(1);
                TimeUnit.MILLISECONDS.sleep(20);
                flushLsn.add(getConfirmedFlushLsn(connection));
            }
        }
        // Theoretically the LSN should change for each record but in reality there can be
        // unfortunate timings so let's suppose the chane will happen in 75 % of cases
        Assertions.assertThat(flushLsn.size()).isGreaterThanOrEqualTo(((recordCount * 3) / 4));
    }
}

