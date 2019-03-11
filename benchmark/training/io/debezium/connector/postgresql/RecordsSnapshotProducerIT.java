/**
 * Copyright Debezium Authors.
 *
 * Licensed under the Apache Software License version 2.0, available at http://www.apache.org/licenses/LICENSE-2.0
 */
package io.debezium.connector.postgresql;


import DecimalHandlingMode.STRING;
import Heartbeat.HEARTBEAT_INTERVAL;
import PostgresConnectorConfig.DECIMAL_HANDLING_MODE;
import PostgresConnectorConfig.HSTORE_HANDLING_MODE;
import PostgresConnectorConfig.HStoreHandlingMode.JSON;
import PostgresConnectorConfig.INCLUDE_SCHEMA_CHANGES;
import PostgresConnectorConfig.INCLUDE_UNKNOWN_DATATYPES;
import PostgresConnectorConfig.SNAPSHOT_MODE;
import PostgresConnectorConfig.SnapshotMode.INITIAL;
import PostgresConnectorConfig.TIME_PRECISION_MODE;
import TemporalPrecisionMode.ADAPTIVE_TIME_MICROSECONDS;
import io.debezium.connector.postgresql.junit.SkipTestDependingOnDatabaseVersionRule;
import io.debezium.connector.postgresql.junit.SkipWhenDatabaseVersionLessThan;
import io.debezium.data.VerifyRecord;
import io.debezium.doc.FixFor;
import io.debezium.relational.TableId;
import io.debezium.schema.TopicSelector;
import io.debezium.util.Collect;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import org.apache.kafka.connect.data.Struct;
import org.apache.kafka.connect.source.SourceRecord;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;

import static io.debezium.connector.postgresql.junit.SkipWhenDatabaseVersionLessThan.PostgresVersion.POSTGRES_10;


/**
 * Integration test for {@link RecordsSnapshotProducerIT}
 *
 * @author Horia Chiorean (hchiorea@redhat.com)
 */
public class RecordsSnapshotProducerIT extends AbstractRecordsProducerTest {
    @Rule
    public final TestRule skip = new SkipTestDependingOnDatabaseVersionRule();

    private RecordsSnapshotProducer snapshotProducer;

    private PostgresTaskContext context;

    @Test
    public void shouldGenerateSnapshotsForDefaultDatatypes() throws Exception {
        snapshotProducer = new RecordsSnapshotProducer(context, new SourceInfo(TestHelper.TEST_SERVER, TestHelper.TEST_DATABASE), false);
        AbstractRecordsProducerTest.TestConsumer consumer = testConsumer(AbstractRecordsProducerTest.ALL_STMTS.size(), "public", "Quoted__");
        // insert data for each of different supported types
        String statementsBuilder = (AbstractRecordsProducerTest.ALL_STMTS.stream().collect(Collectors.joining((";" + (System.lineSeparator()))))) + ";";
        TestHelper.execute(statementsBuilder);
        // then start the producer and validate all records are there
        snapshotProducer.start(consumer, ( e) -> {
        });
        consumer.await(((TestHelper.waitTimeForRecords()) * 30), TimeUnit.SECONDS);
        Map<String, List<AbstractRecordsProducerTest.SchemaAndValueField>> expectedValuesByTopicName = super.schemaAndValuesByTopicName();
        consumer.process(( record) -> assertReadRecord(record, expectedValuesByTopicName));
        // check the offset information for each record
        while (!(consumer.isEmpty())) {
            SourceRecord record = consumer.remove();
            assertRecordOffsetAndSnapshotSource(record, true, consumer.isEmpty());
            assertSourceInfo(record);
        } 
    }

    @Test
    public void shouldGenerateSnapshotsForCustomDatatypes() throws Exception {
        final PostgresConnectorConfig config = new PostgresConnectorConfig(TestHelper.defaultConfig().with(SNAPSHOT_MODE, INITIAL).with(INCLUDE_UNKNOWN_DATATYPES, true).build());
        context = new PostgresTaskContext(config, TestHelper.getSchema(config), PostgresTopicSelector.create(config));
        snapshotProducer = new RecordsSnapshotProducer(context, new SourceInfo(TestHelper.TEST_SERVER, TestHelper.TEST_DATABASE), false);
        final AbstractRecordsProducerTest.TestConsumer consumer = testConsumer(1, "public");
        TestHelper.execute(AbstractRecordsProducerTest.INSERT_CUSTOM_TYPES_STMT);
        // then start the producer and validate all records are there
        snapshotProducer.start(consumer, ( e) -> {
        });
        consumer.await(((TestHelper.waitTimeForRecords()) * 30), TimeUnit.SECONDS);
        final Map<String, List<AbstractRecordsProducerTest.SchemaAndValueField>> expectedValuesByTopicName = Collect.hashMapOf("public.custom_table", schemasAndValuesForCustomTypes());
        consumer.process(( record) -> assertReadRecord(record, expectedValuesByTopicName));
    }

    @Test
    public void shouldGenerateSnapshotAndContinueStreaming() throws Exception {
        // PostGIS must not be used
        TestHelper.dropAllSchemas();
        TestHelper.executeDDL("postgres_create_tables.ddl");
        PostgresConnectorConfig config = new PostgresConnectorConfig(TestHelper.defaultConfig().build());
        TopicSelector<TableId> selector = PostgresTopicSelector.create(config);
        context = new PostgresTaskContext(config, TestHelper.getSchema(config), selector);
        String insertStmt = "INSERT INTO s1.a (aa) VALUES (1);" + "INSERT INTO s2.a (aa) VALUES (1);";
        String statements = ("CREATE SCHEMA s1; " + (("CREATE SCHEMA s2; " + "CREATE TABLE s1.a (pk SERIAL, aa integer, PRIMARY KEY(pk));") + "CREATE TABLE s2.a (pk SERIAL, aa integer, PRIMARY KEY(pk));")) + insertStmt;
        TestHelper.execute(statements);
        snapshotProducer = new RecordsSnapshotProducer(context, new SourceInfo(TestHelper.TEST_SERVER, TestHelper.TEST_DATABASE), true);
        AbstractRecordsProducerTest.TestConsumer consumer = testConsumer(2, "s1", "s2");
        snapshotProducer.start(consumer, ( e) -> {
        });
        // first make sure we get the initial records from both schemas...
        consumer.await(TestHelper.waitTimeForRecords(), TimeUnit.SECONDS);
        consumer.clear();
        // then insert some more data and check that we get it back
        TestHelper.execute(insertStmt);
        consumer.expects(2);
        consumer.await(TestHelper.waitTimeForRecords(), TimeUnit.SECONDS);
        SourceRecord first = consumer.remove();
        VerifyRecord.isValidInsert(first, TestHelper.PK_FIELD, 2);
        Assert.assertEquals(TestHelper.topicName("s1.a"), first.topic());
        assertRecordOffsetAndSnapshotSource(first, false, false);
        assertSourceInfo(first, "test_database", "s1", "a");
        SourceRecord second = consumer.remove();
        VerifyRecord.isValidInsert(second, TestHelper.PK_FIELD, 2);
        Assert.assertEquals(TestHelper.topicName("s2.a"), second.topic());
        assertRecordOffsetAndSnapshotSource(second, false, false);
        assertSourceInfo(second, "test_database", "s2", "a");
        // now shut down the producers and insert some more records
        snapshotProducer.stop();
        TestHelper.execute(insertStmt);
        // start a new producer back up, take a new snapshot (we expect all the records to be read back)
        int expectedRecordsCount = 6;
        consumer = testConsumer(expectedRecordsCount, "s1", "s2");
        snapshotProducer = new RecordsSnapshotProducer(context, new SourceInfo(TestHelper.TEST_SERVER, TestHelper.TEST_DATABASE), true);
        snapshotProducer.start(consumer, ( e) -> {
        });
        consumer.await(TestHelper.waitTimeForRecords(), TimeUnit.SECONDS);
        AtomicInteger counter = new AtomicInteger(0);
        consumer.process(( record) -> {
            int counterVal = counter.getAndIncrement();
            int expectedPk = (counterVal % 3) + 1;// each table has 3 entries keyed 1-3

            VerifyRecord.isValidRead(record, TestHelper.PK_FIELD, expectedPk);
            assertRecordOffsetAndSnapshotSource(record, true, (counterVal == (expectedRecordsCount - 1)));
            assertSourceInfo(record);
        });
        consumer.clear();
        // now insert two more records and check that we only get those back from the stream
        TestHelper.execute(insertStmt);
        consumer.expects(2);
        consumer.await(TestHelper.waitTimeForRecords(), TimeUnit.SECONDS);
        first = consumer.remove();
        VerifyRecord.isValidInsert(first, TestHelper.PK_FIELD, 4);
        assertRecordOffsetAndSnapshotSource(first, false, false);
        assertSourceInfo(first, "test_database", "s1", "a");
        second = consumer.remove();
        VerifyRecord.isValidInsert(second, TestHelper.PK_FIELD, 4);
        assertRecordOffsetAndSnapshotSource(second, false, false);
        assertSourceInfo(second, "test_database", "s2", "a");
    }

    @Test
    @FixFor("DBZ-859")
    public void shouldGenerateSnapshotAndSendHeartBeat() throws Exception {
        // PostGIS must not be used
        TestHelper.dropAllSchemas();
        TestHelper.execute("CREATE TABLE t1 (pk SERIAL, aa integer, PRIMARY KEY(pk)); INSERT INTO t1 VALUES (default, 11)");
        PostgresConnectorConfig config = new PostgresConnectorConfig(TestHelper.defaultConfig().with(SNAPSHOT_MODE, INITIAL).with(INCLUDE_SCHEMA_CHANGES, true).with(HEARTBEAT_INTERVAL, 300000).build());
        TopicSelector<TableId> selector = PostgresTopicSelector.create(config);
        context = new PostgresTaskContext(config, TestHelper.getSchema(config), selector);
        snapshotProducer = new RecordsSnapshotProducer(context, new SourceInfo(TestHelper.TEST_SERVER, TestHelper.TEST_DATABASE), true);
        AbstractRecordsProducerTest.TestConsumer consumer = testConsumer(2);
        snapshotProducer.start(consumer, ( e) -> {
        });
        // Make sure we get the table schema record and heartbeat record
        consumer.await(TestHelper.waitTimeForRecords(), TimeUnit.SECONDS);
        final SourceRecord first = consumer.remove();
        VerifyRecord.isValidRead(first, TestHelper.PK_FIELD, 1);
        assertRecordOffsetAndSnapshotSource(first, true, true);
        System.out.println(first);
        final SourceRecord second = consumer.remove();
        assertThat(second.topic()).startsWith("__debezium-heartbeat");
        assertRecordOffsetAndSnapshotSource(second, false, false);
        // now shut down the producers and insert some more records
        snapshotProducer.stop();
    }

    @Test
    @FixFor("DBZ-342")
    public void shouldGenerateSnapshotsForDefaultDatatypesAdaptiveMicroseconds() throws Exception {
        PostgresConnectorConfig config = new PostgresConnectorConfig(TestHelper.defaultConfig().with(TIME_PRECISION_MODE, ADAPTIVE_TIME_MICROSECONDS).build());
        TopicSelector<TableId> selector = PostgresTopicSelector.create(config);
        context = new PostgresTaskContext(config, TestHelper.getSchema(config), selector);
        snapshotProducer = new RecordsSnapshotProducer(context, new SourceInfo(TestHelper.TEST_SERVER, TestHelper.TEST_DATABASE), false);
        AbstractRecordsProducerTest.TestConsumer consumer = testConsumer(AbstractRecordsProducerTest.ALL_STMTS.size(), "public", "Quoted__");
        // insert data for each of different supported types
        String statementsBuilder = (AbstractRecordsProducerTest.ALL_STMTS.stream().collect(Collectors.joining((";" + (System.lineSeparator()))))) + ";";
        TestHelper.execute(statementsBuilder);
        // then start the producer and validate all records are there
        snapshotProducer.start(consumer, ( e) -> {
        });
        consumer.await(((TestHelper.waitTimeForRecords()) * 30), TimeUnit.SECONDS);
        Map<String, List<AbstractRecordsProducerTest.SchemaAndValueField>> expectedValuesByTopicName = super.schemaAndValuesByTopicNameAdaptiveTimeMicroseconds();
        consumer.process(( record) -> assertReadRecord(record, expectedValuesByTopicName));
        // check the offset information for each record
        while (!(consumer.isEmpty())) {
            SourceRecord record = consumer.remove();
            assertRecordOffsetAndSnapshotSource(record, true, consumer.isEmpty());
            assertSourceInfo(record);
        } 
    }

    @Test
    @FixFor("DBZ-606")
    public void shouldGenerateSnapshotsForDecimalDatatypesUsingStringEncoding() throws Exception {
        // PostGIS must not be used
        TestHelper.dropAllSchemas();
        TestHelper.executeDDL("postgres_create_tables.ddl");
        PostgresConnectorConfig config = new PostgresConnectorConfig(TestHelper.defaultConfig().with(DECIMAL_HANDLING_MODE, STRING).build());
        TopicSelector<TableId> selector = PostgresTopicSelector.create(config);
        context = new PostgresTaskContext(config, TestHelper.getSchema(config), selector);
        snapshotProducer = new RecordsSnapshotProducer(context, new SourceInfo(TestHelper.TEST_SERVER, TestHelper.TEST_DATABASE), false);
        AbstractRecordsProducerTest.TestConsumer consumer = testConsumer(1, "public", "Quoted_\"");
        // insert data for each of different supported types
        TestHelper.execute(AbstractRecordsProducerTest.INSERT_NUMERIC_DECIMAL_TYPES_STMT);
        // then start the producer and validate all records are there
        snapshotProducer.start(consumer, ( e) -> {
        });
        consumer.await(((TestHelper.waitTimeForRecords()) * 30), TimeUnit.SECONDS);
        Map<String, List<AbstractRecordsProducerTest.SchemaAndValueField>> expectedValuesByTopicName = super.schemaAndValuesByTopicNameStringEncodedDecimals();
        consumer.process(( record) -> assertReadRecord(record, expectedValuesByTopicName));
        // check the offset information for each record
        while (!(consumer.isEmpty())) {
            SourceRecord record = consumer.remove();
            assertRecordOffsetAndSnapshotSource(record, true, consumer.isEmpty());
            assertSourceInfo(record);
        } 
    }

    @Test
    @FixFor("DBZ-1118")
    @SkipWhenDatabaseVersionLessThan(POSTGRES_10)
    public void shouldGenerateSnapshotsForPartitionedTables() throws Exception {
        TestHelper.dropAllSchemas();
        String ddl = "CREATE TABLE first_table (pk integer, user_id integer, PRIMARY KEY(pk));" + (((((("CREATE TABLE partitioned (pk serial, user_id integer, aa integer) PARTITION BY RANGE (user_id);" + "CREATE TABLE partitioned_1_100 PARTITION OF partitioned ") + "(CONSTRAINT p_1_100_pk PRIMARY KEY (pk)) ") + "FOR VALUES FROM (1) TO (101);") + "CREATE TABLE partitioned_101_200 PARTITION OF partitioned ") + "(CONSTRAINT p_101_200_pk PRIMARY KEY (pk)) ") + "FOR VALUES FROM (101) TO (201);");
        TestHelper.execute(ddl);
        PostgresConnectorConfig config = new PostgresConnectorConfig(TestHelper.defaultConfig().build());
        TopicSelector<TableId> selector = PostgresTopicSelector.create(config);
        context = new PostgresTaskContext(config, TestHelper.getSchema(config), selector);
        snapshotProducer = new RecordsSnapshotProducer(context, new SourceInfo(TestHelper.TEST_SERVER, TestHelper.TEST_DATABASE), false);
        AbstractRecordsProducerTest.TestConsumer consumer = testConsumer(31);
        // add 1 record to `first_table`. To reproduce the bug we must process at
        // least one row before processing the partitioned table.
        TestHelper.execute("INSERT INTO first_table (pk, user_id) VALUES (1000, 1);");
        // add 10 random records to the first partition, 20 to the second
        TestHelper.execute(("INSERT INTO partitioned (user_id, aa) " + ("SELECT RANDOM() * 99 + 1, RANDOM() * 100000 " + "FROM generate_series(1, 10);")));
        TestHelper.execute(("INSERT INTO partitioned (user_id, aa) " + ("SELECT RANDOM() * 99 + 101, RANDOM() * 100000 " + "FROM generate_series(1, 20);")));
        // then start the producer and validate all records are there
        snapshotProducer.start(consumer, ( e) -> {
        });
        consumer.await(((TestHelper.waitTimeForRecords()) * 30), TimeUnit.SECONDS);
        Set<Integer> ids = new HashSet<>();
        Map<String, Integer> topicCounts = Collect.hashMapOf("test_server.public.first_table", 0, "test_server.public.partitioned", 0, "test_server.public.partitioned_1_100", 0, "test_server.public.partitioned_101_200", 0);
        consumer.process(( record) -> {
            Struct key = ((Struct) (record.key()));
            ids.add(key.getInt32("pk"));
            topicCounts.put(record.topic(), ((topicCounts.get(record.topic())) + 1));
        });
        // verify distinct records
        Assert.assertEquals(31, ids.size());
        // verify each topic contains exactly the number of input records
        Assert.assertEquals(1, topicCounts.get("test_server.public.first_table").intValue());
        Assert.assertEquals(0, topicCounts.get("test_server.public.partitioned").intValue());
        Assert.assertEquals(10, topicCounts.get("test_server.public.partitioned_1_100").intValue());
        Assert.assertEquals(20, topicCounts.get("test_server.public.partitioned_101_200").intValue());
        // check the offset information for each record
        while (!(consumer.isEmpty())) {
            SourceRecord record = consumer.remove();
            assertRecordOffsetAndSnapshotSource(record, true, consumer.isEmpty());
            assertSourceInfo(record);
        } 
    }

    @Test
    @FixFor("DBZ-1162")
    public void shouldGenerateSnapshotsForHstores() throws Exception {
        // PostGIS must not be used
        TestHelper.dropAllSchemas();
        TestHelper.executeDDL("postgres_create_tables.ddl");
        PostgresConnectorConfig config = new PostgresConnectorConfig(TestHelper.defaultConfig().with(HSTORE_HANDLING_MODE, JSON).build());
        TopicSelector<TableId> selector = PostgresTopicSelector.create(config);
        context = new PostgresTaskContext(config, TestHelper.getSchema(config), selector);
        snapshotProducer = new RecordsSnapshotProducer(context, new SourceInfo(TestHelper.TEST_SERVER, TestHelper.TEST_DATABASE), false);
        AbstractRecordsProducerTest.TestConsumer consumer = testConsumer(1, "public", "Quoted_\"");
        // insert data for each of different supported types
        TestHelper.execute(AbstractRecordsProducerTest.INSERT_HSTORE_TYPE_STMT);
        // then start the producer and validate all records are there
        snapshotProducer.start(consumer, ( e) -> {
        });
        consumer.await(((TestHelper.waitTimeForRecords()) * 30), TimeUnit.SECONDS);
        final Map<String, List<AbstractRecordsProducerTest.SchemaAndValueField>> expectedValuesByTopicName = Collect.hashMapOf("public.hstore_table", schemaAndValueFieldForJsonEncodedHStoreType());
        consumer.process(( record) -> assertReadRecord(record, expectedValuesByTopicName));
    }
}

