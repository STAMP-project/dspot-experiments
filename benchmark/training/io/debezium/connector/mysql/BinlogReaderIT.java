/**
 * Copyright Debezium Authors.
 *
 * Licensed under the Apache Software License version 2.0, available at http://www.apache.org/licenses/LICENSE-2.0
 */
package io.debezium.connector.mysql;


import Envelope.FieldName.AFTER;
import EventProcessingFailureHandlingMode.IGNORE;
import EventProcessingFailureHandlingMode.WARN;
import Testing.Files;
import ZonedTimestamp.FORMATTER;
import io.debezium.config.Configuration;
import io.debezium.connector.mysql.AbstractReader.AcceptAllPredicate;
import io.debezium.data.KeyValueStore;
import io.debezium.data.KeyValueStore.Collection;
import io.debezium.data.SchemaChangeHistory;
import io.debezium.doc.FixFor;
import java.nio.file.Path;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.apache.kafka.connect.data.Struct;
import org.apache.kafka.connect.errors.ConnectException;
import org.apache.kafka.connect.source.SourceRecord;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Randall Hauch
 */
public class BinlogReaderIT {
    private static final Path DB_HISTORY_PATH = Files.createTestingPath("file-db-history-binlog.txt").toAbsolutePath();

    private final UniqueDatabase DATABASE = new UniqueDatabase("logical_server_name", "connector_test_ro").withDbHistoryPath(BinlogReaderIT.DB_HISTORY_PATH);

    private Configuration config;

    private MySqlTaskContext context;

    private BinlogReader reader;

    private KeyValueStore store;

    private SchemaChangeHistory schemaChanges;

    @Test
    public void shouldCreateSnapshotOfSingleDatabase() throws Exception {
        config = simpleConfig().build();
        Filters filters = build();
        context = new MySqlTaskContext(config, filters);
        context.start();
        context.source().setBinlogStartPoint("", 0L);// start from beginning

        context.initializeHistory();
        reader = new BinlogReader("binlog", context, new AcceptAllPredicate());
        // Start reading the binlog ...
        reader.start();
        // Poll for records ...
        // Testing.Print.enable();
        int expected = (((9 + 9) + 4) + 5) + 1;// only the inserts for our 4 tables in this database and 1 create table

        int consumed = consumeAtLeast(expected);
        assertThat(consumed).isGreaterThanOrEqualTo(expected);
        store.sourceRecords().forEach(System.out::println);
        // There should be no schema changes ...
        assertThat(schemaChanges.recordCount()).isEqualTo(0);
        // Check the records via the store ...
        assertThat(store.collectionCount()).isEqualTo(5);
        Collection products = store.collection(DATABASE.getDatabaseName(), productsTableName());
        assertThat(products.numberOfCreates()).isEqualTo(9);
        assertThat(products.numberOfUpdates()).isEqualTo(0);
        assertThat(products.numberOfDeletes()).isEqualTo(0);
        assertThat(products.numberOfReads()).isEqualTo(0);
        assertThat(products.numberOfTombstones()).isEqualTo(0);
        assertThat(products.numberOfKeySchemaChanges()).isEqualTo(1);
        assertThat(products.numberOfValueSchemaChanges()).isEqualTo(1);
        Collection products_on_hand = store.collection(DATABASE.getDatabaseName(), "products_on_hand");
        assertThat(products_on_hand.numberOfCreates()).isEqualTo(9);
        assertThat(products_on_hand.numberOfUpdates()).isEqualTo(0);
        assertThat(products_on_hand.numberOfDeletes()).isEqualTo(0);
        assertThat(products_on_hand.numberOfReads()).isEqualTo(0);
        assertThat(products_on_hand.numberOfTombstones()).isEqualTo(0);
        assertThat(products_on_hand.numberOfKeySchemaChanges()).isEqualTo(1);
        assertThat(products_on_hand.numberOfValueSchemaChanges()).isEqualTo(1);
        Collection customers = store.collection(DATABASE.getDatabaseName(), "customers");
        assertThat(customers.numberOfCreates()).isEqualTo(4);
        assertThat(customers.numberOfUpdates()).isEqualTo(0);
        assertThat(customers.numberOfDeletes()).isEqualTo(0);
        assertThat(customers.numberOfReads()).isEqualTo(0);
        assertThat(customers.numberOfTombstones()).isEqualTo(0);
        assertThat(customers.numberOfKeySchemaChanges()).isEqualTo(1);
        assertThat(customers.numberOfValueSchemaChanges()).isEqualTo(1);
        Collection orders = store.collection(DATABASE.getDatabaseName(), "orders");
        assertThat(orders.numberOfCreates()).isEqualTo(5);
        assertThat(orders.numberOfUpdates()).isEqualTo(0);
        assertThat(orders.numberOfDeletes()).isEqualTo(0);
        assertThat(orders.numberOfReads()).isEqualTo(0);
        assertThat(orders.numberOfTombstones()).isEqualTo(0);
        assertThat(orders.numberOfKeySchemaChanges()).isEqualTo(1);
        assertThat(orders.numberOfValueSchemaChanges()).isEqualTo(1);
    }

    @Test
    public void shouldCreateSnapshotOfSingleDatabaseWithSchemaChanges() throws Exception {
        config = simpleConfig().with(MySqlConnectorConfig.INCLUDE_SCHEMA_CHANGES, true).build();
        Filters filters = build();
        context = new MySqlTaskContext(config, filters);
        context.start();
        context.source().setBinlogStartPoint("", 0L);// start from beginning

        context.initializeHistory();
        reader = new BinlogReader("binlog", context, new AcceptAllPredicate());
        // Start reading the binlog ...
        reader.start();
        // Poll for records ...
        // Testing.Print.enable();
        int expectedSchemaChangeCount = 5 + 2;// 5 tables plus 2 alters

        int expected = ((((9 + 9) + 4) + 5) + 1) + expectedSchemaChangeCount;// only the inserts for our 4 tables in this database, plus

        // schema changes
        int consumed = consumeAtLeast(expected);
        assertThat(consumed).isGreaterThanOrEqualTo(expected);
        // There should be no schema changes ...
        assertThat(schemaChanges.recordCount()).isEqualTo(expectedSchemaChangeCount);
        // Check the records via the store ...
        assertThat(store.collectionCount()).isEqualTo(5);
        Collection products = store.collection(DATABASE.getDatabaseName(), productsTableName());
        assertThat(products.numberOfCreates()).isEqualTo(9);
        assertThat(products.numberOfUpdates()).isEqualTo(0);
        assertThat(products.numberOfDeletes()).isEqualTo(0);
        assertThat(products.numberOfReads()).isEqualTo(0);
        assertThat(products.numberOfTombstones()).isEqualTo(0);
        assertThat(products.numberOfKeySchemaChanges()).isEqualTo(1);
        assertThat(products.numberOfValueSchemaChanges()).isEqualTo(1);
        Collection products_on_hand = store.collection(DATABASE.getDatabaseName(), "products_on_hand");
        assertThat(products_on_hand.numberOfCreates()).isEqualTo(9);
        assertThat(products_on_hand.numberOfUpdates()).isEqualTo(0);
        assertThat(products_on_hand.numberOfDeletes()).isEqualTo(0);
        assertThat(products_on_hand.numberOfReads()).isEqualTo(0);
        assertThat(products_on_hand.numberOfTombstones()).isEqualTo(0);
        assertThat(products_on_hand.numberOfKeySchemaChanges()).isEqualTo(1);
        assertThat(products_on_hand.numberOfValueSchemaChanges()).isEqualTo(1);
        Collection customers = store.collection(DATABASE.getDatabaseName(), "customers");
        assertThat(customers.numberOfCreates()).isEqualTo(4);
        assertThat(customers.numberOfUpdates()).isEqualTo(0);
        assertThat(customers.numberOfDeletes()).isEqualTo(0);
        assertThat(customers.numberOfReads()).isEqualTo(0);
        assertThat(customers.numberOfTombstones()).isEqualTo(0);
        assertThat(customers.numberOfKeySchemaChanges()).isEqualTo(1);
        assertThat(customers.numberOfValueSchemaChanges()).isEqualTo(1);
        Collection orders = store.collection(DATABASE.getDatabaseName(), "orders");
        assertThat(orders.numberOfCreates()).isEqualTo(5);
        assertThat(orders.numberOfUpdates()).isEqualTo(0);
        assertThat(orders.numberOfDeletes()).isEqualTo(0);
        assertThat(orders.numberOfReads()).isEqualTo(0);
        assertThat(orders.numberOfTombstones()).isEqualTo(0);
        assertThat(orders.numberOfKeySchemaChanges()).isEqualTo(1);
        assertThat(orders.numberOfValueSchemaChanges()).isEqualTo(1);
    }

    @Test
    @FixFor("DBZ-183")
    public void shouldHandleTimestampTimezones() throws Exception {
        final UniqueDatabase REGRESSION_DATABASE = new UniqueDatabase("logical_server_name", "regression_test").withDbHistoryPath(BinlogReaderIT.DB_HISTORY_PATH);
        REGRESSION_DATABASE.createAndInitialize();
        String tableName = "dbz_85_fractest";
        config = simpleConfig().with(MySqlConnectorConfig.INCLUDE_SCHEMA_CHANGES, false).with(MySqlConnectorConfig.DATABASE_WHITELIST, REGRESSION_DATABASE.getDatabaseName()).with(MySqlConnectorConfig.TABLE_WHITELIST, REGRESSION_DATABASE.qualifiedTableName(tableName)).build();
        Filters filters = build();
        context = new MySqlTaskContext(config, filters);
        context.start();
        context.source().setBinlogStartPoint("", 0L);// start from beginning

        context.initializeHistory();
        reader = new BinlogReader("binlog", context, new AcceptAllPredicate());
        // Start reading the binlog ...
        reader.start();
        int expectedChanges = 1;// only 1 insert

        consumeAtLeast(expectedChanges);
        // Check the records via the store ...
        List<SourceRecord> sourceRecords = store.sourceRecords();
        assertThat(sourceRecords.size()).isEqualTo(1);
        // TIMESTAMP should be converted to UTC, using the DB's (or connection's) time zone
        ZonedDateTime expectedTimestamp = ZonedDateTime.of(LocalDateTime.parse("2014-09-08T17:51:04.780"), UniqueDatabase.TIMEZONE).withZoneSameInstant(ZoneOffset.UTC);
        String expectedTimestampString = expectedTimestamp.format(FORMATTER);
        SourceRecord sourceRecord = sourceRecords.get(0);
        Struct value = ((Struct) (sourceRecord.value()));
        Struct after = value.getStruct(AFTER);
        String actualTimestampString = after.getString("c4");
        assertThat(actualTimestampString).isEqualTo(expectedTimestampString);
    }

    @Test
    @FixFor("DBZ-342")
    public void shouldHandleMySQLTimeCorrectly() throws Exception {
        final UniqueDatabase REGRESSION_DATABASE = new UniqueDatabase("logical_server_name", "regression_test").withDbHistoryPath(BinlogReaderIT.DB_HISTORY_PATH);
        REGRESSION_DATABASE.createAndInitialize();
        String tableName = "dbz_342_timetest";
        config = simpleConfig().with(MySqlConnectorConfig.INCLUDE_SCHEMA_CHANGES, false).with(MySqlConnectorConfig.DATABASE_WHITELIST, REGRESSION_DATABASE.getDatabaseName()).with(MySqlConnectorConfig.TABLE_WHITELIST, REGRESSION_DATABASE.qualifiedTableName(tableName)).build();
        Filters filters = build();
        context = new MySqlTaskContext(config, filters);
        context.start();
        context.source().setBinlogStartPoint("", 0L);// start from beginning

        context.initializeHistory();
        reader = new BinlogReader("binlog", context, null);
        // Start reading the binlog ...
        reader.start();
        int expectedChanges = 1;// only 1 insert

        consumeAtLeast(expectedChanges);
        // Check the records via the store ...
        List<SourceRecord> sourceRecords = store.sourceRecords();
        assertThat(sourceRecords.size()).isEqualTo(1);
        SourceRecord sourceRecord = sourceRecords.get(0);
        Struct value = ((Struct) (sourceRecord.value()));
        Struct after = value.getStruct(AFTER);
        // '517:51:04.777'
        long c1 = after.getInt64("c1");
        Duration c1Time = Duration.ofNanos((c1 * 1000));
        Duration c1ExpectedTime = toDuration("PT517H51M4.78S");
        Assert.assertEquals(c1ExpectedTime, c1Time);
        Assert.assertEquals(c1ExpectedTime.toNanos(), c1Time.toNanos());
        assertThat(c1Time.toNanos()).isEqualTo(1864264780000000L);
        assertThat(c1Time).isEqualTo(Duration.ofHours(517).plusMinutes(51).plusSeconds(4).plusMillis(780));
        // '-13:14:50'
        long c2 = after.getInt64("c2");
        Duration c2Time = Duration.ofNanos((c2 * 1000));
        Duration c2ExpectedTime = toDuration("-PT13H14M50S");
        Assert.assertEquals(c2ExpectedTime, c2Time);
        Assert.assertEquals(c2ExpectedTime.toNanos(), c2Time.toNanos());
        assertThat(c2Time.toNanos()).isEqualTo((-47690000000000L));
        Assert.assertTrue(c2Time.isNegative());
        assertThat(c2Time).isEqualTo(Duration.ofHours((-13)).minusMinutes(14).minusSeconds(50));
        // '-733:00:00.0011'
        long c3 = after.getInt64("c3");
        Duration c3Time = Duration.ofNanos((c3 * 1000));
        Duration c3ExpectedTime = toDuration("-PT733H0M0.001S");
        Assert.assertEquals(c3ExpectedTime, c3Time);
        Assert.assertEquals(c3ExpectedTime.toNanos(), c3Time.toNanos());
        assertThat(c3Time.toNanos()).isEqualTo((-2638800001000000L));
        Assert.assertTrue(c3Time.isNegative());
        assertThat(c3Time).isEqualTo(Duration.ofHours((-733)).minusMillis(1));
        // '-1:59:59.0011'
        long c4 = after.getInt64("c4");
        Duration c4Time = Duration.ofNanos((c4 * 1000));
        Duration c4ExpectedTime = toDuration("-PT1H59M59.001S");
        Assert.assertEquals(c4ExpectedTime, c4Time);
        Assert.assertEquals(c4ExpectedTime.toNanos(), c4Time.toNanos());
        assertThat(c4Time.toNanos()).isEqualTo((-7199001000000L));
        Assert.assertTrue(c4Time.isNegative());
        assertThat(c4Time).isEqualTo(Duration.ofHours((-1)).minusMinutes(59).minusSeconds(59).minusMillis(1));
        // '-838:59:58.999999'
        long c5 = after.getInt64("c5");
        Duration c5Time = Duration.ofNanos((c5 * 1000));
        Duration c5ExpectedTime = toDuration("-PT838H59M58.999999S");
        Assert.assertEquals(c5ExpectedTime, c5Time);
        Assert.assertEquals(c5ExpectedTime.toNanos(), c5Time.toNanos());
        assertThat(c5Time.toNanos()).isEqualTo((-3020398999999000L));
        Assert.assertTrue(c5Time.isNegative());
        assertThat(c5Time).isEqualTo(Duration.ofHours((-838)).minusMinutes(59).minusSeconds(58).minusNanos(999999000));
    }

    @Test(expected = ConnectException.class)
    public void shouldFailOnSchemaInconsistency() throws Exception {
        inconsistentSchema(null);
        consumeAtLeast(2);
    }

    @Test
    public void shouldWarnOnSchemaInconsistency() throws Exception {
        inconsistentSchema(WARN);
        int consumed = consumeAtLeast(2, 2, TimeUnit.SECONDS);
        assertThat(consumed).isZero();
    }

    @Test
    public void shouldIgnoreOnSchemaInconsistency() throws Exception {
        inconsistentSchema(IGNORE);
        int consumed = consumeAtLeast(2, 2, TimeUnit.SECONDS);
        assertThat(consumed).isZero();
    }
}

