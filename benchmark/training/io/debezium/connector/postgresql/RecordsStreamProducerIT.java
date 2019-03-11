/**
 * Copyright Debezium Authors.
 *
 * Licensed under the Apache Software License version 2.0, available at http://www.apache.org/licenses/LICENSE-2.0
 */
package io.debezium.connector.postgresql;


import CommonConnectorConfig.TOMBSTONES_ON_DELETE;
import Envelope.FieldName.AFTER;
import Envelope.FieldName.BEFORE;
import Heartbeat.HEARTBEAT_INTERVAL;
import PostgresConnectorConfig.DECIMAL_HANDLING_MODE;
import PostgresConnectorConfig.DecimalHandlingMode.DOUBLE;
import PostgresConnectorConfig.DecimalHandlingMode.STRING;
import PostgresConnectorConfig.HSTORE_HANDLING_MODE;
import PostgresConnectorConfig.HStoreHandlingMode.JSON;
import PostgresConnectorConfig.HStoreHandlingMode.MAP;
import PostgresConnectorConfig.INCLUDE_UNKNOWN_DATATYPES;
import PostgresConnectorConfig.SCHEMA_BLACKLIST;
import PostgresConnectorConfig.SCHEMA_REFRESH_MODE;
import PostgresConnectorConfig.STREAM_PARAMS;
import PostgresConnectorConfig.SchemaRefreshMode.COLUMNS_DIFF_EXCLUDE_UNCHANGED_TOAST;
import PostgresConnectorConfig.TABLE_WHITELIST;
import SchemaRefreshMode.COLUMNS_DIFF;
import TemporalPrecisionMode.ADAPTIVE;
import TemporalPrecisionMode.ADAPTIVE_TIME_MICROSECONDS;
import TemporalPrecisionMode.CONNECT;
import io.debezium.connector.postgresql.junit.SkipTestDependingOnDecoderPluginNameRule;
import io.debezium.connector.postgresql.junit.SkipWhenDecoderPluginNameIsNot;
import io.debezium.data.VariableScaleDecimal;
import io.debezium.data.VerifyRecord;
import io.debezium.doc.FixFor;
import io.debezium.junit.ConditionalFail;
import io.debezium.junit.ShouldFailWhen;
import io.debezium.relational.Table;
import io.debezium.relational.TableId;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import junit.framework.TestCase;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.kafka.connect.data.Decimal;
import org.apache.kafka.connect.data.SchemaBuilder;
import org.apache.kafka.connect.data.Struct;
import org.apache.kafka.connect.source.SourceRecord;
import org.fest.assertions.Assertions;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;

import static io.debezium.connector.postgresql.junit.SkipWhenDecoderPluginNameIsNot.DecoderPluginName.WAL2JSON;


/**
 * Integration test for the {@link RecordsStreamProducer} class. This also tests indirectly the PG plugin functionality for
 * different use cases.
 *
 * @author Horia Chiorean (hchiorea@redhat.com)
 */
public class RecordsStreamProducerIT extends AbstractRecordsProducerTest {
    private RecordsStreamProducer recordsProducer;

    private AbstractRecordsProducerTest.TestConsumer consumer;

    private final Consumer<Throwable> blackHole = ( t) -> {
    };

    @Rule
    public final TestRule skip = new SkipTestDependingOnDecoderPluginNameRule();

    @Rule
    public TestRule conditionalFail = new ConditionalFail();

    @Test
    public void shouldReceiveChangesForInsertsWithDifferentDataTypes() throws Exception {
        TestHelper.executeDDL("postgres_create_tables.ddl");
        consumer = testConsumer(1);
        recordsProducer.start(consumer, blackHole);
        // numerical types
        assertInsert(AbstractRecordsProducerTest.INSERT_NUMERIC_TYPES_STMT, 1, schemasAndValuesForNumericType());
        // numerical decimal types
        consumer.expects(1);
        assertInsert(AbstractRecordsProducerTest.INSERT_NUMERIC_DECIMAL_TYPES_STMT_NO_NAN, 1, schemasAndValuesForBigDecimalEncodedNumericTypes());
        // string types
        consumer.expects(1);
        assertInsert(AbstractRecordsProducerTest.INSERT_STRING_TYPES_STMT, 1, schemasAndValuesForStringTypes());
        // monetary types
        consumer.expects(1);
        assertInsert(AbstractRecordsProducerTest.INSERT_CASH_TYPES_STMT, 1, schemaAndValuesForMoneyTypes());
        // bits and bytes
        consumer.expects(1);
        assertInsert(AbstractRecordsProducerTest.INSERT_BIN_TYPES_STMT, 1, schemaAndValuesForBinTypes());
        // date and time
        consumer.expects(1);
        assertInsert(AbstractRecordsProducerTest.INSERT_DATE_TIME_TYPES_STMT, 1, schemaAndValuesForDateTimeTypes());
        // text
        consumer.expects(1);
        assertInsert(AbstractRecordsProducerTest.INSERT_TEXT_TYPES_STMT, 1, schemasAndValuesForTextTypes());
        // geom types
        consumer.expects(1);
        assertInsert(AbstractRecordsProducerTest.INSERT_GEOM_TYPES_STMT, 1, schemaAndValuesForGeomTypes());
        // timezone range types
        consumer.expects(1);
        assertInsert(AbstractRecordsProducerTest.INSERT_TSTZRANGE_TYPES_STMT, 1, schemaAndValuesForTstzRangeTypes());
    }

    @Test
    public void shouldReceiveChangesForInsertsCustomTypes() throws Exception {
        PostgresConnectorConfig config = new PostgresConnectorConfig(TestHelper.defaultConfig().with(INCLUDE_UNKNOWN_DATATYPES, true).with(SCHEMA_BLACKLIST, "postgis").build());
        setupRecordsProducer(config);
        TestHelper.executeDDL("postgres_create_tables.ddl");
        consumer = testConsumer(1);
        recordsProducer.start(consumer, blackHole);
        // custom types + null value
        assertInsert(AbstractRecordsProducerTest.INSERT_CUSTOM_TYPES_STMT, 1, schemasAndValuesForCustomTypes());
    }

    @Test
    @FixFor("DBZ-1141")
    public void shouldProcessNotNullColumnsConnectDateTypes() throws Exception {
        final Struct before = testProcessNotNullColumns(CONNECT);
        if (before != null) {
            Assertions.assertThat(before.get("created_at")).isEqualTo(new Date(0));
            Assertions.assertThat(before.get("created_at_tz")).isEqualTo("1970-01-01T00:00:00Z");
            Assertions.assertThat(before.get("ctime")).isEqualTo(new Date(0));
            Assertions.assertThat(before.get("ctime_tz")).isEqualTo("00:00:00Z");
            Assertions.assertThat(before.get("cdate")).isEqualTo(new Date(0));
            Assertions.assertThat(before.get("cmoney")).isEqualTo(new BigDecimal("0.00"));
            Assertions.assertThat(before.get("cbits")).isEqualTo(new byte[0]);
        }
    }

    @Test
    @FixFor("DBZ-1141")
    public void shouldProcessNotNullColumnsAdaptiveDateTypes() throws Exception {
        final Struct before = testProcessNotNullColumns(ADAPTIVE);
        if (before != null) {
            Assertions.assertThat(before.get("created_at")).isEqualTo(0L);
            Assertions.assertThat(before.get("created_at_tz")).isEqualTo("1970-01-01T00:00:00Z");
            Assertions.assertThat(before.get("ctime")).isEqualTo(0L);
            Assertions.assertThat(before.get("ctime_tz")).isEqualTo("00:00:00Z");
            Assertions.assertThat(before.get("cdate")).isEqualTo(0);
            Assertions.assertThat(before.get("cmoney")).isEqualTo(new BigDecimal("0.00"));
            Assertions.assertThat(before.get("cbits")).isEqualTo(new byte[0]);
        }
    }

    @Test
    @FixFor("DBZ-1141")
    public void shouldProcessNotNullColumnsAdaptiveMsDateTypes() throws Exception {
        final Struct before = testProcessNotNullColumns(ADAPTIVE_TIME_MICROSECONDS);
        if (before != null) {
            Assertions.assertThat(before.get("created_at")).isEqualTo(0L);
            Assertions.assertThat(before.get("created_at_tz")).isEqualTo("1970-01-01T00:00:00Z");
            Assertions.assertThat(before.get("ctime")).isEqualTo(0L);
            Assertions.assertThat(before.get("ctime_tz")).isEqualTo("00:00:00Z");
            Assertions.assertThat(before.get("cdate")).isEqualTo(0);
            Assertions.assertThat(before.get("cmoney")).isEqualTo(new BigDecimal("0.00"));
            Assertions.assertThat(before.get("cbits")).isEqualTo(new byte[0]);
        }
    }

    @Test(timeout = 30000)
    public void shouldReceiveChangesForInsertsWithPostgisTypes() throws Exception {
        TestHelper.executeDDL("postgis_create_tables.ddl");
        consumer = testConsumer(1, "public");// spatial_ref_sys produces a ton of records in the postgis schema

        consumer.setIgnoreExtraRecords(true);
        recordsProducer.start(consumer, blackHole);
        // need to wait for all the spatial_ref_sys to flow through and be ignored.
        // this exceeds the normal 2s timeout.
        TestHelper.execute("INSERT INTO public.dummy_table DEFAULT VALUES;");
        consumer.await(((TestHelper.waitTimeForRecords()) * 10), TimeUnit.SECONDS);
        while (true) {
            if (!(consumer.isEmpty())) {
                SourceRecord record = consumer.remove();
                if (record.topic().endsWith(".public.dummy_table")) {
                    break;
                }
            }
        } 
        // now do it for actual testing
        // postgis types
        consumer.expects(1);
        assertInsert(AbstractRecordsProducerTest.INSERT_POSTGIS_TYPES_STMT, 1, schemaAndValuesForPostgisTypes());
    }

    @Test(timeout = 30000)
    public void shouldReceiveChangesForInsertsWithPostgisArrayTypes() throws Exception {
        TestHelper.executeDDL("postgis_create_tables.ddl");
        consumer = testConsumer(1, "public");// spatial_ref_sys produces a ton of records in the postgis schema

        consumer.setIgnoreExtraRecords(true);
        recordsProducer.start(consumer, blackHole);
        // need to wait for all the spatial_ref_sys to flow through and be ignored.
        // this exceeds the normal 2s timeout.
        TestHelper.execute("INSERT INTO public.dummy_table DEFAULT VALUES;");
        consumer.await(((TestHelper.waitTimeForRecords()) * 10), TimeUnit.SECONDS);
        while (true) {
            if (!(consumer.isEmpty())) {
                SourceRecord record = consumer.remove();
                if (record.topic().endsWith(".public.dummy_table")) {
                    break;
                }
            }
        } 
        // now do it for actual testing
        // postgis types
        consumer.expects(1);
        assertInsert(AbstractRecordsProducerTest.INSERT_POSTGIS_ARRAY_TYPES_STMT, 1, schemaAndValuesForPostgisArrayTypes());
    }

    // TODO DBZ-493
    @Test
    @ShouldFailWhen(DecoderDifferences.AreQuotedIdentifiersUnsupported.class)
    public void shouldReceiveChangesForInsertsWithQuotedNames() throws Exception {
        TestHelper.executeDDL("postgres_create_tables.ddl");
        consumer = testConsumer(1);
        recordsProducer.start(consumer, blackHole);
        // Quoted column name
        assertInsert(AbstractRecordsProducerTest.INSERT_QUOTED_TYPES_STMT, 1, schemasAndValuesForQuotedTypes());
    }

    @Test
    public void shouldReceiveChangesForInsertsWithArrayTypes() throws Exception {
        TestHelper.executeDDL("postgres_create_tables.ddl");
        consumer = testConsumer(1);
        recordsProducer.start(consumer, blackHole);
        assertInsert(AbstractRecordsProducerTest.INSERT_ARRAY_TYPES_STMT, 1, schemasAndValuesForArrayTypes());
    }

    @Test
    @FixFor("DBZ-1029")
    public void shouldReceiveChangesForInsertsIndependentOfReplicaIdentity() {
        // insert statement should not be affected by replica identity settings in any way
        consumer = testConsumer(1);
        recordsProducer.start(consumer, blackHole);
        TestHelper.execute("ALTER TABLE test_table REPLICA IDENTITY DEFAULT;");
        String statement = "INSERT INTO test_table (text) VALUES ('pk_and_default');";
        assertInsert(statement, 2, Collections.singletonList(new AbstractRecordsProducerTest.SchemaAndValueField("text", SchemaBuilder.OPTIONAL_STRING_SCHEMA, "pk_and_default")));
        consumer.expects(1);
        TestHelper.execute("ALTER TABLE test_table REPLICA IDENTITY FULL;");
        statement = "INSERT INTO test_table (text) VALUES ('pk_and_full');";
        assertInsert(statement, 3, Collections.singletonList(new AbstractRecordsProducerTest.SchemaAndValueField("text", SchemaBuilder.OPTIONAL_STRING_SCHEMA, "pk_and_full")));
        consumer.expects(1);
        TestHelper.execute("ALTER TABLE test_table DROP CONSTRAINT test_table_pkey CASCADE;");
        statement = "INSERT INTO test_table (pk, text) VALUES (4, 'no_pk_and_full');";
        assertInsert(statement, 4, Collections.singletonList(new AbstractRecordsProducerTest.SchemaAndValueField("text", SchemaBuilder.OPTIONAL_STRING_SCHEMA, "no_pk_and_full")));
        consumer.expects(1);
        TestHelper.execute("ALTER TABLE test_table REPLICA IDENTITY DEFAULT;");
        statement = "INSERT INTO test_table (pk, text) VALUES (5, 'no_pk_and_default');";
        assertInsert(statement, 5, Collections.singletonList(new AbstractRecordsProducerTest.SchemaAndValueField("text", SchemaBuilder.OPTIONAL_STRING_SCHEMA, "no_pk_and_default")));
    }

    @Test
    @FixFor("DBZ-478")
    public void shouldReceiveChangesForNullInsertsWithArrayTypes() throws Exception {
        TestHelper.executeDDL("postgres_create_tables.ddl");
        consumer = testConsumer(1);
        recordsProducer.start(consumer, blackHole);
        assertInsert(AbstractRecordsProducerTest.INSERT_ARRAY_TYPES_WITH_NULL_VALUES_STMT, 1, schemasAndValuesForArrayTypesWithNullValues());
    }

    @Test
    public void shouldReceiveChangesForNewTable() throws Exception {
        String statement = "CREATE SCHEMA s1;" + ("CREATE TABLE s1.a (pk SERIAL, aa integer, PRIMARY KEY(pk));" + "INSERT INTO s1.a (aa) VALUES (11);");
        consumer = testConsumer(1);
        recordsProducer.start(consumer, blackHole);
        executeAndWait(statement);
        assertRecordInserted("s1.a", TestHelper.PK_FIELD, 1);
    }

    @Test
    public void shouldReceiveChangesForRenamedTable() throws Exception {
        String statement = "DROP TABLE IF EXISTS renamed_test_table;" + ("ALTER TABLE test_table RENAME TO renamed_test_table;" + "INSERT INTO renamed_test_table (text) VALUES ('new');");
        consumer = testConsumer(1);
        recordsProducer.start(consumer, blackHole);
        executeAndWait(statement);
        assertRecordInserted("public.renamed_test_table", TestHelper.PK_FIELD, 2);
    }

    @Test
    public void shouldReceiveChangesForUpdates() throws Exception {
        consumer = testConsumer(1);
        recordsProducer.start(consumer, blackHole);
        executeAndWait("UPDATE test_table set text='update' WHERE pk=1");
        // the update record should be the last record
        SourceRecord updatedRecord = consumer.remove();
        String topicName = TestHelper.topicName("public.test_table");
        TestCase.assertEquals(topicName, updatedRecord.topic());
        VerifyRecord.isValidUpdate(updatedRecord, TestHelper.PK_FIELD, 1);
        // default replica identity only fires previous values for PK changes
        List<AbstractRecordsProducerTest.SchemaAndValueField> expectedAfter = Collections.singletonList(new AbstractRecordsProducerTest.SchemaAndValueField("text", SchemaBuilder.OPTIONAL_STRING_SCHEMA, "update"));
        assertRecordSchemaAndValues(expectedAfter, updatedRecord, AFTER);
        // alter the table and set its replica identity to full the issue another update
        consumer.expects(1);
        TestHelper.execute("ALTER TABLE test_table REPLICA IDENTITY FULL");
        executeAndWait("UPDATE test_table set text='update2' WHERE pk=1");
        updatedRecord = consumer.remove();
        TestCase.assertEquals(topicName, updatedRecord.topic());
        VerifyRecord.isValidUpdate(updatedRecord, TestHelper.PK_FIELD, 1);
        // now we should get both old and new values
        List<AbstractRecordsProducerTest.SchemaAndValueField> expectedBefore = Collections.singletonList(new AbstractRecordsProducerTest.SchemaAndValueField("text", SchemaBuilder.OPTIONAL_STRING_SCHEMA, "update"));
        assertRecordSchemaAndValues(expectedBefore, updatedRecord, BEFORE);
        expectedAfter = Collections.singletonList(new AbstractRecordsProducerTest.SchemaAndValueField("text", SchemaBuilder.OPTIONAL_STRING_SCHEMA, "update2"));
        assertRecordSchemaAndValues(expectedAfter, updatedRecord, AFTER);
        // without PK and with REPLICA IDENTITY FULL we still getting all fields 'before' and all fields 'after'
        TestHelper.execute("ALTER TABLE test_table DROP CONSTRAINT test_table_pkey CASCADE;");
        consumer.expects(1);
        executeAndWait("UPDATE test_table SET text = 'update3' WHERE pk = 1;");
        updatedRecord = consumer.remove();
        TestCase.assertEquals(topicName, updatedRecord.topic());
        expectedBefore = Collections.singletonList(new AbstractRecordsProducerTest.SchemaAndValueField("text", SchemaBuilder.OPTIONAL_STRING_SCHEMA, "update2"));
        assertRecordSchemaAndValues(expectedBefore, updatedRecord, BEFORE);
        expectedAfter = Collections.singletonList(new AbstractRecordsProducerTest.SchemaAndValueField("text", SchemaBuilder.OPTIONAL_STRING_SCHEMA, "update3"));
        assertRecordSchemaAndValues(expectedAfter, updatedRecord, AFTER);
        // without PK and with REPLICA IDENTITY DEFAULT we will get nothing
        TestHelper.execute("ALTER TABLE test_table REPLICA IDENTITY DEFAULT;");
        consumer.expects(0);
        executeAndWait("UPDATE test_table SET text = 'no_pk_and_default' WHERE pk = 1;");
        assertThat(consumer.isEmpty()).isTrue();
    }

    @Test
    public void shouldReceiveChangesForUpdatesWithColumnChanges() throws Exception {
        // add a new column
        String statements = "ALTER TABLE test_table ADD COLUMN uvc VARCHAR(2);" + ("ALTER TABLE test_table REPLICA IDENTITY FULL;" + "UPDATE test_table SET uvc ='aa' WHERE pk = 1;");
        consumer = testConsumer(1);
        recordsProducer.start(consumer, blackHole);
        executeAndWait(statements);
        // the update should be the last record
        SourceRecord updatedRecord = consumer.remove();
        String topicName = TestHelper.topicName("public.test_table");
        TestCase.assertEquals(topicName, updatedRecord.topic());
        VerifyRecord.isValidUpdate(updatedRecord, TestHelper.PK_FIELD, 1);
        // now check we got the updated value (the old value should be null, the new one whatever we set)
        List<AbstractRecordsProducerTest.SchemaAndValueField> expectedBefore = Collections.singletonList(new AbstractRecordsProducerTest.SchemaAndValueField("uvc", null, null));
        assertRecordSchemaAndValues(expectedBefore, updatedRecord, BEFORE);
        List<AbstractRecordsProducerTest.SchemaAndValueField> expectedAfter = Collections.singletonList(new AbstractRecordsProducerTest.SchemaAndValueField("uvc", SchemaBuilder.OPTIONAL_STRING_SCHEMA, "aa"));
        assertRecordSchemaAndValues(expectedAfter, updatedRecord, AFTER);
        // rename a column
        statements = "ALTER TABLE test_table RENAME COLUMN uvc to xvc;" + "UPDATE test_table SET xvc ='bb' WHERE pk = 1;";
        consumer.expects(1);
        executeAndWait(statements);
        updatedRecord = consumer.remove();
        VerifyRecord.isValidUpdate(updatedRecord, TestHelper.PK_FIELD, 1);
        // now check we got the updated value (the old value should be null, the new one whatever we set)
        expectedBefore = Collections.singletonList(new AbstractRecordsProducerTest.SchemaAndValueField("xvc", SchemaBuilder.OPTIONAL_STRING_SCHEMA, "aa"));
        assertRecordSchemaAndValues(expectedBefore, updatedRecord, BEFORE);
        expectedAfter = Collections.singletonList(new AbstractRecordsProducerTest.SchemaAndValueField("xvc", SchemaBuilder.OPTIONAL_STRING_SCHEMA, "bb"));
        assertRecordSchemaAndValues(expectedAfter, updatedRecord, AFTER);
        // drop a column
        statements = "ALTER TABLE test_table DROP COLUMN xvc;" + "UPDATE test_table SET text ='update' WHERE pk = 1;";
        consumer.expects(1);
        executeAndWait(statements);
        updatedRecord = consumer.remove();
        VerifyRecord.isValidUpdate(updatedRecord, TestHelper.PK_FIELD, 1);
        // change a column type
        statements = "ALTER TABLE test_table ADD COLUMN modtype INTEGER;" + "INSERT INTO test_table (pk,modtype) VALUES (2,1);";
        consumer.expects(1);
        executeAndWait(statements);
        updatedRecord = consumer.remove();
        VerifyRecord.isValidInsert(updatedRecord, TestHelper.PK_FIELD, 2);
        assertRecordSchemaAndValues(Collections.singletonList(new AbstractRecordsProducerTest.SchemaAndValueField("modtype", SchemaBuilder.OPTIONAL_INT32_SCHEMA, 1)), updatedRecord, AFTER);
        statements = "ALTER TABLE test_table ALTER COLUMN modtype TYPE SMALLINT;" + "UPDATE test_table SET modtype = 2 WHERE pk = 2;";
        consumer.expects(1);
        executeAndWait(statements);
        updatedRecord = consumer.remove();
        VerifyRecord.isValidUpdate(updatedRecord, TestHelper.PK_FIELD, 2);
        assertRecordSchemaAndValues(Collections.singletonList(new AbstractRecordsProducerTest.SchemaAndValueField("modtype", SchemaBuilder.OPTIONAL_INT16_SCHEMA, ((short) (1)))), updatedRecord, BEFORE);
        assertRecordSchemaAndValues(Collections.singletonList(new AbstractRecordsProducerTest.SchemaAndValueField("modtype", SchemaBuilder.OPTIONAL_INT16_SCHEMA, ((short) (2)))), updatedRecord, AFTER);
    }

    @Test
    public void shouldReceiveChangesForUpdatesWithPKChanges() throws Exception {
        consumer = testConsumer(3);
        recordsProducer.start(consumer, blackHole);
        executeAndWait("UPDATE test_table SET text = 'update', pk = 2");
        String topicName = TestHelper.topicName("public.test_table");
        // first should be a delete of the old pk
        SourceRecord deleteRecord = consumer.remove();
        TestCase.assertEquals(topicName, deleteRecord.topic());
        VerifyRecord.isValidDelete(deleteRecord, TestHelper.PK_FIELD, 1);
        // followed by a tombstone of the old pk
        SourceRecord tombstoneRecord = consumer.remove();
        TestCase.assertEquals(topicName, tombstoneRecord.topic());
        VerifyRecord.isValidTombstone(tombstoneRecord, TestHelper.PK_FIELD, 1);
        // and finally insert of the new value
        SourceRecord insertRecord = consumer.remove();
        TestCase.assertEquals(topicName, insertRecord.topic());
        VerifyRecord.isValidInsert(insertRecord, TestHelper.PK_FIELD, 2);
    }

    @Test
    @FixFor("DBZ-582")
    public void shouldReceiveChangesForUpdatesWithPKChangesWithoutTombstone() throws Exception {
        PostgresConnectorConfig config = new PostgresConnectorConfig(TestHelper.defaultConfig().with(INCLUDE_UNKNOWN_DATATYPES, true).with(TOMBSTONES_ON_DELETE, false).build());
        setupRecordsProducer(config);
        consumer = testConsumer(2);
        recordsProducer.start(consumer, blackHole);
        executeAndWait("UPDATE test_table SET text = 'update', pk = 2");
        String topicName = TestHelper.topicName("public.test_table");
        // first should be a delete of the old pk
        SourceRecord deleteRecord = consumer.remove();
        TestCase.assertEquals(topicName, deleteRecord.topic());
        VerifyRecord.isValidDelete(deleteRecord, TestHelper.PK_FIELD, 1);
        // followed by insert of the new value
        SourceRecord insertRecord = consumer.remove();
        TestCase.assertEquals(topicName, insertRecord.topic());
        VerifyRecord.isValidInsert(insertRecord, TestHelper.PK_FIELD, 2);
    }

    @Test
    public void shouldReceiveChangesForDefaultValues() throws Exception {
        String statements = "ALTER TABLE test_table REPLICA IDENTITY FULL;" + ("ALTER TABLE test_table ADD COLUMN default_column TEXT DEFAULT 'default';" + "INSERT INTO test_table (text) VALUES ('update');");
        consumer = testConsumer(1);
        recordsProducer.start(consumer, blackHole);
        executeAndWait(statements);
        SourceRecord insertRecord = consumer.remove();
        TestCase.assertEquals(TestHelper.topicName("public.test_table"), insertRecord.topic());
        VerifyRecord.isValidInsert(insertRecord, TestHelper.PK_FIELD, 2);
        List<AbstractRecordsProducerTest.SchemaAndValueField> expectedSchemaAndValues = Arrays.asList(new AbstractRecordsProducerTest.SchemaAndValueField("text", SchemaBuilder.OPTIONAL_STRING_SCHEMA, "update"), new AbstractRecordsProducerTest.SchemaAndValueField("default_column", SchemaBuilder.OPTIONAL_STRING_SCHEMA, "default"));
        assertRecordSchemaAndValues(expectedSchemaAndValues, insertRecord, AFTER);
    }

    @Test
    public void shouldReceiveChangesForTypeConstraints() throws Exception {
        // add a new column
        String statements = "ALTER TABLE test_table ADD COLUMN num_val NUMERIC(5,2);" + ("ALTER TABLE test_table REPLICA IDENTITY FULL;" + "UPDATE test_table SET num_val = 123.45 WHERE pk = 1;");
        consumer = testConsumer(1);
        recordsProducer.start(consumer, blackHole);
        executeAndWait(statements);
        // the update should be the last record
        SourceRecord updatedRecord = consumer.remove();
        String topicName = TestHelper.topicName("public.test_table");
        TestCase.assertEquals(topicName, updatedRecord.topic());
        VerifyRecord.isValidUpdate(updatedRecord, TestHelper.PK_FIELD, 1);
        // now check we got the updated value (the old value should be null, the new one whatever we set)
        List<AbstractRecordsProducerTest.SchemaAndValueField> expectedBefore = Collections.singletonList(new AbstractRecordsProducerTest.SchemaAndValueField("num_val", null, null));
        assertRecordSchemaAndValues(expectedBefore, updatedRecord, BEFORE);
        List<AbstractRecordsProducerTest.SchemaAndValueField> expectedAfter = Collections.singletonList(new AbstractRecordsProducerTest.SchemaAndValueField("num_val", Decimal.builder(2).parameter(TestHelper.PRECISION_PARAMETER_KEY, "5").optional().build(), new BigDecimal("123.45")));
        assertRecordSchemaAndValues(expectedAfter, updatedRecord, AFTER);
        // change a constraint
        statements = "ALTER TABLE test_table ALTER COLUMN num_val TYPE NUMERIC(6,1);" + "INSERT INTO test_table (pk,num_val) VALUES (2,123.41);";
        consumer.expects(1);
        executeAndWait(statements);
        updatedRecord = consumer.remove();
        VerifyRecord.isValidInsert(updatedRecord, TestHelper.PK_FIELD, 2);
        assertRecordSchemaAndValues(Collections.singletonList(new AbstractRecordsProducerTest.SchemaAndValueField("num_val", Decimal.builder(1).parameter(TestHelper.PRECISION_PARAMETER_KEY, "6").optional().build(), new BigDecimal("123.4"))), updatedRecord, AFTER);
        statements = "ALTER TABLE test_table ALTER COLUMN num_val TYPE NUMERIC;" + "INSERT INTO test_table (pk,num_val) VALUES (3,123.4567);";
        consumer.expects(1);
        executeAndWait(statements);
        updatedRecord = consumer.remove();
        final Struct dvs = new Struct(VariableScaleDecimal.schema());
        dvs.put("scale", 4).put("value", new BigDecimal("123.4567").unscaledValue().toByteArray());
        VerifyRecord.isValidInsert(updatedRecord, TestHelper.PK_FIELD, 3);
        assertRecordSchemaAndValues(Collections.singletonList(new AbstractRecordsProducerTest.SchemaAndValueField("num_val", VariableScaleDecimal.builder().optional().build(), dvs)), updatedRecord, AFTER);
        statements = "ALTER TABLE test_table ALTER COLUMN num_val TYPE DECIMAL(12,4);" + "INSERT INTO test_table (pk,num_val) VALUES (4,2.48);";
        consumer.expects(1);
        executeAndWait(statements);
        updatedRecord = consumer.remove();
        VerifyRecord.isValidInsert(updatedRecord, TestHelper.PK_FIELD, 4);
        assertRecordSchemaAndValues(Collections.singletonList(new AbstractRecordsProducerTest.SchemaAndValueField("num_val", Decimal.builder(4).parameter(TestHelper.PRECISION_PARAMETER_KEY, "12").optional().build(), new BigDecimal("2.4800"))), updatedRecord, AFTER);
        statements = "ALTER TABLE test_table ALTER COLUMN num_val TYPE DECIMAL(12);" + "INSERT INTO test_table (pk,num_val) VALUES (5,1238);";
        consumer.expects(1);
        executeAndWait(statements);
        updatedRecord = consumer.remove();
        VerifyRecord.isValidInsert(updatedRecord, TestHelper.PK_FIELD, 5);
        assertRecordSchemaAndValues(Collections.singletonList(new AbstractRecordsProducerTest.SchemaAndValueField("num_val", Decimal.builder(0).parameter(TestHelper.PRECISION_PARAMETER_KEY, "12").optional().build(), new BigDecimal("1238"))), updatedRecord, AFTER);
        statements = "ALTER TABLE test_table ALTER COLUMN num_val TYPE DECIMAL;" + "INSERT INTO test_table (pk,num_val) VALUES (6,1225.1);";
        consumer.expects(1);
        executeAndWait(statements);
        updatedRecord = consumer.remove();
        final Struct dvs2 = new Struct(VariableScaleDecimal.schema());
        dvs2.put("scale", 1).put("value", new BigDecimal("1225.1").unscaledValue().toByteArray());
        VerifyRecord.isValidInsert(updatedRecord, TestHelper.PK_FIELD, 6);
        assertRecordSchemaAndValues(Collections.singletonList(new AbstractRecordsProducerTest.SchemaAndValueField("num_val", VariableScaleDecimal.builder().optional().build(), dvs2)), updatedRecord, AFTER);
        statements = "ALTER TABLE test_table ALTER COLUMN num_val SET NOT NULL;" + "INSERT INTO test_table (pk,num_val) VALUES (7,1976);";
        consumer.expects(1);
        executeAndWait(statements);
        updatedRecord = consumer.remove();
        dvs2.put("scale", 0).put("value", new BigDecimal("1976").unscaledValue().toByteArray());
        VerifyRecord.isValidInsert(updatedRecord, TestHelper.PK_FIELD, 7);
        assertRecordSchemaAndValues(Collections.singletonList(new AbstractRecordsProducerTest.SchemaAndValueField("num_val", VariableScaleDecimal.builder().build(), dvs2)), updatedRecord, AFTER);
    }

    @Test
    public void shouldReceiveChangesForDeletes() throws Exception {
        // add a new entry and remove both
        String statements = "INSERT INTO test_table (text) VALUES ('insert2');" + "DELETE FROM test_table WHERE pk > 0;";
        consumer = testConsumer(5);
        recordsProducer.start(consumer, blackHole);
        executeAndWait(statements);
        String topicPrefix = "public.test_table";
        String topicName = TestHelper.topicName(topicPrefix);
        assertRecordInserted(topicPrefix, TestHelper.PK_FIELD, 2);
        // first entry removed
        SourceRecord record = consumer.remove();
        TestCase.assertEquals(topicName, record.topic());
        VerifyRecord.isValidDelete(record, TestHelper.PK_FIELD, 1);
        // followed by a tombstone
        record = consumer.remove();
        TestCase.assertEquals(topicName, record.topic());
        VerifyRecord.isValidTombstone(record, TestHelper.PK_FIELD, 1);
        // second entry removed
        record = consumer.remove();
        TestCase.assertEquals(topicName, record.topic());
        VerifyRecord.isValidDelete(record, TestHelper.PK_FIELD, 2);
        // followed by a tombstone
        record = consumer.remove();
        TestCase.assertEquals(topicName, record.topic());
        VerifyRecord.isValidTombstone(record, TestHelper.PK_FIELD, 2);
    }

    @Test
    @FixFor("DBZ-582")
    public void shouldReceiveChangesForDeletesWithoutTombstone() throws Exception {
        PostgresConnectorConfig config = new PostgresConnectorConfig(TestHelper.defaultConfig().with(INCLUDE_UNKNOWN_DATATYPES, true).with(TOMBSTONES_ON_DELETE, false).build());
        setupRecordsProducer(config);
        // add a new entry and remove both
        String statements = "INSERT INTO test_table (text) VALUES ('insert2');" + "DELETE FROM test_table WHERE pk > 0;";
        consumer = testConsumer(3);
        recordsProducer.start(consumer, blackHole);
        executeAndWait(statements);
        String topicPrefix = "public.test_table";
        String topicName = TestHelper.topicName(topicPrefix);
        assertRecordInserted(topicPrefix, TestHelper.PK_FIELD, 2);
        // first entry removed
        SourceRecord record = consumer.remove();
        TestCase.assertEquals(topicName, record.topic());
        VerifyRecord.isValidDelete(record, TestHelper.PK_FIELD, 1);
        // second entry removed
        record = consumer.remove();
        TestCase.assertEquals(topicName, record.topic());
        VerifyRecord.isValidDelete(record, TestHelper.PK_FIELD, 2);
    }

    @Test
    public void shouldReceiveChangesForDeletesDependingOnReplicaIdentity() throws Exception {
        PostgresConnectorConfig config = new PostgresConnectorConfig(TestHelper.defaultConfig().with(INCLUDE_UNKNOWN_DATATYPES, true).with(TOMBSTONES_ON_DELETE, false).build());
        setupRecordsProducer(config);
        String topicName = TestHelper.topicName("public.test_table");
        // With PK we should get delete event with default level of replica identity
        String statement = "ALTER TABLE test_table REPLICA IDENTITY DEFAULT;" + "DELETE FROM test_table WHERE pk = 1;";
        consumer = testConsumer(1);
        recordsProducer.start(consumer, blackHole);
        executeAndWait(statement);
        SourceRecord record = consumer.remove();
        TestCase.assertEquals(topicName, record.topic());
        VerifyRecord.isValidDelete(record, TestHelper.PK_FIELD, 1);
        // Without PK we should get delete event with REPLICA IDENTITY FULL
        statement = "ALTER TABLE test_table REPLICA IDENTITY FULL;" + (("ALTER TABLE test_table DROP CONSTRAINT test_table_pkey CASCADE;" + "INSERT INTO test_table (pk, text) VALUES (2, 'insert2');") + "DELETE FROM test_table WHERE pk = 2;");
        consumer.expects(2);
        executeAndWait(statement);
        assertRecordInserted("public.test_table", TestHelper.PK_FIELD, 2);
        record = consumer.remove();
        TestCase.assertEquals(topicName, record.topic());
        VerifyRecord.isValidDelete(record, TestHelper.PK_FIELD, 2);
        // Without PK and without REPLICA IDENTITY FULL we will not get delete event
        statement = "ALTER TABLE test_table REPLICA IDENTITY DEFAULT;" + ("INSERT INTO test_table (pk, text) VALUES (3, 'insert3');" + "DELETE FROM test_table WHERE pk = 3;");
        consumer.expects(1);
        executeAndWait(statement);
        assertRecordInserted("public.test_table", TestHelper.PK_FIELD, 3);
        assertThat(consumer.isEmpty()).isTrue();
    }

    @Test
    public void shouldReceiveNumericTypeAsDouble() throws Exception {
        PostgresConnectorConfig config = new PostgresConnectorConfig(TestHelper.defaultConfig().with(DECIMAL_HANDLING_MODE, DOUBLE).build());
        setupRecordsProducer(config);
        TestHelper.executeDDL("postgres_create_tables.ddl");
        consumer = testConsumer(1);
        recordsProducer.start(consumer, blackHole);
        assertInsert(AbstractRecordsProducerTest.INSERT_NUMERIC_DECIMAL_TYPES_STMT, 1, schemasAndValuesForDoubleEncodedNumericTypes());
    }

    @Test
    @FixFor("DBZ-611")
    public void shouldReceiveNumericTypeAsString() throws Exception {
        PostgresConnectorConfig config = new PostgresConnectorConfig(TestHelper.defaultConfig().with(DECIMAL_HANDLING_MODE, STRING).build());
        setupRecordsProducer(config);
        TestHelper.executeDDL("postgres_create_tables.ddl");
        consumer = testConsumer(1);
        recordsProducer.start(consumer, blackHole);
        assertInsert(AbstractRecordsProducerTest.INSERT_NUMERIC_DECIMAL_TYPES_STMT, 1, schemasAndValuesForStringEncodedNumericTypes());
    }

    @Test
    @FixFor("DBZ-898")
    public void shouldReceiveHStoreTypeWithSingleValueAsMap() throws Exception {
        PostgresConnectorConfig config = new PostgresConnectorConfig(TestHelper.defaultConfig().with(HSTORE_HANDLING_MODE, MAP).build());
        setupRecordsProducer(config);
        TestHelper.executeDDL("postgres_create_tables.ddl");
        consumer = testConsumer(1);
        recordsProducer.start(consumer, blackHole);
        assertInsert(AbstractRecordsProducerTest.INSERT_HSTORE_TYPE_STMT, 1, schemaAndValueFieldForMapEncodedHStoreType());
    }

    @Test
    @FixFor("DBZ-898")
    public void shouldReceiveHStoreTypeWithMultipleValuesAsMap() throws Exception {
        PostgresConnectorConfig config = new PostgresConnectorConfig(TestHelper.defaultConfig().with(HSTORE_HANDLING_MODE, MAP).build());
        setupRecordsProducer(config);
        TestHelper.executeDDL("postgres_create_tables.ddl");
        consumer = testConsumer(1);
        recordsProducer.start(consumer, blackHole);
        assertInsert(AbstractRecordsProducerTest.INSERT_HSTORE_TYPE_WITH_MULTIPLE_VALUES_STMT, 1, schemaAndValueFieldForMapEncodedHStoreTypeWithMultipleValues());
    }

    @Test
    @FixFor("DBZ-898")
    public void shouldReceiveHStoreTypeWithNullValuesAsMap() throws Exception {
        PostgresConnectorConfig config = new PostgresConnectorConfig(TestHelper.defaultConfig().with(HSTORE_HANDLING_MODE, MAP).build());
        setupRecordsProducer(config);
        TestHelper.executeDDL("postgres_create_tables.ddl");
        consumer = testConsumer(1);
        recordsProducer.start(consumer, blackHole);
        assertInsert(AbstractRecordsProducerTest.INSERT_HSTORE_TYPE_WITH_NULL_VALUES_STMT, 1, schemaAndValueFieldForMapEncodedHStoreTypeWithNullValues());
    }

    @Test
    @FixFor("DBZ-898")
    public void shouldReceiveHStoreTypeWithSpecialCharactersInValuesAsMap() throws Exception {
        PostgresConnectorConfig config = new PostgresConnectorConfig(TestHelper.defaultConfig().with(HSTORE_HANDLING_MODE, MAP).build());
        setupRecordsProducer(config);
        TestHelper.executeDDL("postgres_create_tables.ddl");
        consumer = testConsumer(1);
        recordsProducer.start(consumer, blackHole);
        assertInsert(AbstractRecordsProducerTest.INSERT_HSTORE_TYPE_WITH_SPECIAL_CHAR_STMT, 1, schemaAndValueFieldForMapEncodedHStoreTypeWithSpecialCharacters());
    }

    @Test
    @FixFor("DBZ-898")
    public void shouldReceiveHStoreTypeAsJsonString() throws Exception {
        PostgresConnectorConfig config = new PostgresConnectorConfig(TestHelper.defaultConfig().with(HSTORE_HANDLING_MODE, JSON).build());
        setupRecordsProducer(config);
        TestHelper.executeDDL("postgres_create_tables.ddl");
        consumer = testConsumer(1);
        recordsProducer.start(consumer, blackHole);
        assertInsert(AbstractRecordsProducerTest.INSERT_HSTORE_TYPE_STMT, 1, schemaAndValueFieldForJsonEncodedHStoreType());
    }

    @Test
    @FixFor("DBZ-898")
    public void shouldReceiveHStoreTypeWithMultipleValuesAsJsonString() throws Exception {
        PostgresConnectorConfig config = new PostgresConnectorConfig(TestHelper.defaultConfig().with(HSTORE_HANDLING_MODE, JSON).build());
        setupRecordsProducer(config);
        TestHelper.executeDDL("postgres_create_tables.ddl");
        consumer = testConsumer(1);
        recordsProducer.start(consumer, blackHole);
        assertInsert(AbstractRecordsProducerTest.INSERT_HSTORE_TYPE_WITH_MULTIPLE_VALUES_STMT, 1, schemaAndValueFieldForJsonEncodedHStoreTypeWithMultipleValues());
    }

    @Test
    @FixFor("DBZ-898")
    public void shouldReceiveHStoreTypeWithSpecialValuesInJsonString() throws Exception {
        PostgresConnectorConfig config = new PostgresConnectorConfig(TestHelper.defaultConfig().with(HSTORE_HANDLING_MODE, JSON).build());
        setupRecordsProducer(config);
        TestHelper.executeDDL("postgres_create_tables.ddl");
        consumer = testConsumer(1);
        recordsProducer.start(consumer, blackHole);
        assertInsert(AbstractRecordsProducerTest.INSERT_HSTORE_TYPE_WITH_SPECIAL_CHAR_STMT, 1, schemaAndValueFieldForJsonEncodedHStoreTypeWithSpcialCharacters());
    }

    @Test
    @FixFor("DBZ-898")
    public void shouldReceiveHStoreTypeWithNullValuesAsJsonString() throws Exception {
        PostgresConnectorConfig config = new PostgresConnectorConfig(TestHelper.defaultConfig().with(HSTORE_HANDLING_MODE, JSON).build());
        setupRecordsProducer(config);
        TestHelper.executeDDL("postgres_create_tables.ddl");
        consumer = testConsumer(1);
        recordsProducer.start(consumer, blackHole);
        assertInsert(AbstractRecordsProducerTest.INSERT_HSTORE_TYPE_WITH_NULL_VALUES_STMT, 1, schemaAndValueFieldForJsonEncodedHStoreTypeWithNullValues());
    }

    @Test
    @FixFor("DBZ-259")
    public void shouldProcessIntervalDelete() throws Exception {
        final String statements = "INSERT INTO table_with_interval VALUES (default, 'Foo', default);" + ("INSERT INTO table_with_interval VALUES (default, 'Bar', default);" + "DELETE FROM table_with_interval WHERE id = 1;");
        consumer = testConsumer(4);
        recordsProducer.start(consumer, blackHole);
        executeAndWait(statements);
        final String topicPrefix = "public.table_with_interval";
        final String topicName = TestHelper.topicName(topicPrefix);
        final String pk = "id";
        assertRecordInserted(topicPrefix, pk, 1);
        assertRecordInserted(topicPrefix, pk, 2);
        // first entry removed
        SourceRecord record = consumer.remove();
        TestCase.assertEquals(topicName, record.topic());
        VerifyRecord.isValidDelete(record, pk, 1);
        // followed by a tombstone
        record = consumer.remove();
        TestCase.assertEquals(topicName, record.topic());
        VerifyRecord.isValidTombstone(record, pk, 1);
    }

    @Test
    @FixFor("DBZ-501")
    public void shouldNotStartAfterStop() throws Exception {
        recordsProducer.stop();
        recordsProducer.start(consumer, blackHole);
        // Need to remove record created in @Before
        PostgresConnectorConfig config = new PostgresConnectorConfig(TestHelper.defaultConfig().with(INCLUDE_UNKNOWN_DATATYPES, true).build());
        setupRecordsProducer(config);
        consumer = testConsumer(1);
        recordsProducer.start(consumer, blackHole);
    }

    @Test
    @FixFor("DBZ-644")
    public void shouldPropagateSourceColumnTypeToSchemaParameter() throws Exception {
        PostgresConnectorConfig config = new PostgresConnectorConfig(TestHelper.defaultConfig().with("column.propagate.source.type", ".*vc.*").build());
        setupRecordsProducer(config);
        TestHelper.executeDDL("postgres_create_tables.ddl");
        consumer = testConsumer(1);
        recordsProducer.start(consumer, blackHole);
        assertInsert(AbstractRecordsProducerTest.INSERT_STRING_TYPES_STMT, 1, schemasAndValuesForStringTypesWithSourceColumnTypeInfo());
    }

    @Test
    @FixFor("DBZ-1073")
    public void shouldPropagateSourceColumnTypeScaleToSchemaParameter() throws Exception {
        PostgresConnectorConfig config = new PostgresConnectorConfig(TestHelper.defaultConfig().with("column.propagate.source.type", ".*(d|dzs)").with(DECIMAL_HANDLING_MODE, DOUBLE).build());
        setupRecordsProducer(config);
        TestHelper.executeDDL("postgres_create_tables.ddl");
        consumer = testConsumer(1);
        recordsProducer.start(consumer, blackHole);
        assertInsert(AbstractRecordsProducerTest.INSERT_NUMERIC_DECIMAL_TYPES_STMT, 1, schemasAndValuesForNumericTypesWithSourceColumnTypeInfo());
    }

    @Test
    @FixFor("DBZ-800")
    public void shouldReceiveHeartbeatAlsoWhenChangingNonWhitelistedTable() throws Exception {
        // the low heartbeat interval should make sure that a heartbeat message is emitted after each change record
        // received from Postgres
        PostgresConnectorConfig config = new PostgresConnectorConfig(TestHelper.defaultConfig().with(HEARTBEAT_INTERVAL, "1").with(TABLE_WHITELIST, "s1\\.b").build());
        setupRecordsProducer(config);
        String statement = "CREATE SCHEMA s1;" + ((("CREATE TABLE s1.a (pk SERIAL, aa integer, PRIMARY KEY(pk));" + "CREATE TABLE s1.b (pk SERIAL, bb integer, PRIMARY KEY(pk));") + "INSERT INTO s1.a (aa) VALUES (11);") + "INSERT INTO s1.b (bb) VALUES (22);");
        // expecting two heartbeat records and one actual change record
        consumer = testConsumer((DecoderDifferences.singleHeartbeatPerTransaction() ? 2 : 3));
        recordsProducer.start(consumer, blackHole);
        executeAndWait(statement);
        if (!(DecoderDifferences.singleHeartbeatPerTransaction())) {
            // expecting no change record for s1.a but a heartbeat
            assertHeartBeatRecordInserted();
        }
        // and then a change record for s1.b and a heartbeat
        assertRecordInserted("s1.b", TestHelper.PK_FIELD, 1);
        assertHeartBeatRecordInserted();
        assertThat(consumer.isEmpty()).isTrue();
    }

    @Test
    @FixFor("DBZ-911")
    public void shouldNotRefreshSchemaOnUnchangedToastedData() throws Exception {
        PostgresConnectorConfig config = new PostgresConnectorConfig(TestHelper.defaultConfig().with(SCHEMA_REFRESH_MODE, COLUMNS_DIFF_EXCLUDE_UNCHANGED_TOAST).build());
        setupRecordsProducer(config);
        String toastedValue = RandomStringUtils.randomAlphanumeric(10000);
        // inserting a toasted value should /always/ produce a correct record
        String statement = ("ALTER TABLE test_table ADD COLUMN not_toast integer; INSERT INTO test_table (not_toast, text) values (10, '" + toastedValue) + "')";
        consumer = testConsumer(1);
        recordsProducer.start(consumer, blackHole);
        executeAndWait(statement);
        SourceRecord record = consumer.remove();
        // after record should contain the toasted value
        List<AbstractRecordsProducerTest.SchemaAndValueField> expectedAfter = Arrays.asList(new AbstractRecordsProducerTest.SchemaAndValueField("not_toast", SchemaBuilder.OPTIONAL_INT32_SCHEMA, 10), new AbstractRecordsProducerTest.SchemaAndValueField("text", SchemaBuilder.OPTIONAL_STRING_SCHEMA, toastedValue));
        assertRecordSchemaAndValues(expectedAfter, record, AFTER);
        // now we remove the toast column and update the not_toast column to see that our unchanged toast data
        // does not trigger a table schema refresh. the after schema should look the same as before.
        statement = "ALTER TABLE test_table DROP COLUMN text; update test_table set not_toast = 5 where not_toast = 10";
        consumer.expects(1);
        executeAndWait(statement);
        Table tbl = recordsProducer.schema().tableFor(TableId.parse("public.test_table"));
        TestCase.assertEquals(Arrays.asList("pk", "text", "not_toast"), tbl.retrieveColumnNames());
    }

    @Test
    @FixFor("DBZ-842")
    public void shouldNotPropagateUnchangedToastedData() throws Exception {
        PostgresConnectorConfig config = new PostgresConnectorConfig(TestHelper.defaultConfig().with(SCHEMA_REFRESH_MODE, COLUMNS_DIFF_EXCLUDE_UNCHANGED_TOAST).build());
        setupRecordsProducer(config);
        final String toastedValue1 = RandomStringUtils.randomAlphanumeric(10000);
        final String toastedValue2 = RandomStringUtils.randomAlphanumeric(10000);
        final String toastedValue3 = RandomStringUtils.randomAlphanumeric(10000);
        // inserting a toasted value should /always/ produce a correct record
        String statement = (((((((((((("ALTER TABLE test_table ADD COLUMN not_toast integer;" + "ALTER TABLE test_table ADD COLUMN mandatory_text TEXT NOT NULL DEFAULT '") + toastedValue3) + "';") + "INSERT INTO test_table (not_toast, text, mandatory_text) values (10, '") + toastedValue1) + "', '") + toastedValue1) + "');") + "INSERT INTO test_table (not_toast, text, mandatory_text) values (10, '") + toastedValue2) + "', '") + toastedValue2) + "');";
        consumer = testConsumer(2);
        recordsProducer.start(consumer, blackHole);
        executeAndWait(statement);
        // after record should contain the toasted value
        assertRecordSchemaAndValues(Arrays.asList(new AbstractRecordsProducerTest.SchemaAndValueField("not_toast", SchemaBuilder.OPTIONAL_INT32_SCHEMA, 10), new AbstractRecordsProducerTest.SchemaAndValueField("text", SchemaBuilder.OPTIONAL_STRING_SCHEMA, toastedValue1), new AbstractRecordsProducerTest.SchemaAndValueField("mandatory_text", SchemaBuilder.STRING_SCHEMA, toastedValue1)), consumer.remove(), AFTER);
        assertRecordSchemaAndValues(Arrays.asList(new AbstractRecordsProducerTest.SchemaAndValueField("not_toast", SchemaBuilder.OPTIONAL_INT32_SCHEMA, 10), new AbstractRecordsProducerTest.SchemaAndValueField("text", SchemaBuilder.OPTIONAL_STRING_SCHEMA, toastedValue2), new AbstractRecordsProducerTest.SchemaAndValueField("mandatory_text", SchemaBuilder.STRING_SCHEMA, toastedValue2)), consumer.remove(), AFTER);
        statement = "UPDATE test_table SET not_toast = 2;" + "UPDATE test_table SET not_toast = 3;";
        consumer.expects(6);
        executeAndWait(statement);
        consumer.process(( record) -> {
            Table tbl = recordsProducer.schema().tableFor(TableId.parse("public.test_table"));
            TestCase.assertEquals(Arrays.asList("pk", "text", "not_toast", "mandatory_text"), tbl.retrieveColumnNames());
        });
        assertRecordSchemaAndValues(Arrays.asList(new AbstractRecordsProducerTest.SchemaAndValueField("not_toast", SchemaBuilder.OPTIONAL_INT32_SCHEMA, 2), new AbstractRecordsProducerTest.SchemaAndValueField("text", SchemaBuilder.OPTIONAL_STRING_SCHEMA, "insert"), new AbstractRecordsProducerTest.SchemaAndValueField("mandatory_text", SchemaBuilder.STRING_SCHEMA, "")), consumer.remove(), AFTER);
        assertRecordSchemaAndValues(Arrays.asList(new AbstractRecordsProducerTest.SchemaAndValueField("not_toast", SchemaBuilder.OPTIONAL_INT32_SCHEMA, 2), new AbstractRecordsProducerTest.SchemaAndValueField("text", SchemaBuilder.OPTIONAL_STRING_SCHEMA, null), new AbstractRecordsProducerTest.SchemaAndValueField("mandatory_text", SchemaBuilder.STRING_SCHEMA, "")), consumer.remove(), AFTER);
        assertRecordSchemaAndValues(Arrays.asList(new AbstractRecordsProducerTest.SchemaAndValueField("not_toast", SchemaBuilder.OPTIONAL_INT32_SCHEMA, 2), new AbstractRecordsProducerTest.SchemaAndValueField("text", SchemaBuilder.OPTIONAL_STRING_SCHEMA, null), new AbstractRecordsProducerTest.SchemaAndValueField("mandatory_text", SchemaBuilder.STRING_SCHEMA, "")), consumer.remove(), AFTER);
        assertRecordSchemaAndValues(Arrays.asList(new AbstractRecordsProducerTest.SchemaAndValueField("not_toast", SchemaBuilder.OPTIONAL_INT32_SCHEMA, 3), new AbstractRecordsProducerTest.SchemaAndValueField("text", SchemaBuilder.OPTIONAL_STRING_SCHEMA, "insert"), new AbstractRecordsProducerTest.SchemaAndValueField("mandatory_text", SchemaBuilder.STRING_SCHEMA, "")), consumer.remove(), AFTER);
        assertRecordSchemaAndValues(Arrays.asList(new AbstractRecordsProducerTest.SchemaAndValueField("not_toast", SchemaBuilder.OPTIONAL_INT32_SCHEMA, 3), new AbstractRecordsProducerTest.SchemaAndValueField("text", SchemaBuilder.OPTIONAL_STRING_SCHEMA, null), new AbstractRecordsProducerTest.SchemaAndValueField("mandatory_text", SchemaBuilder.STRING_SCHEMA, "")), consumer.remove(), AFTER);
        assertRecordSchemaAndValues(Arrays.asList(new AbstractRecordsProducerTest.SchemaAndValueField("not_toast", SchemaBuilder.OPTIONAL_INT32_SCHEMA, 3), new AbstractRecordsProducerTest.SchemaAndValueField("text", SchemaBuilder.OPTIONAL_STRING_SCHEMA, null), new AbstractRecordsProducerTest.SchemaAndValueField("mandatory_text", SchemaBuilder.STRING_SCHEMA, "")), consumer.remove(), AFTER);
    }

    @Test
    @FixFor("DBZ-1029")
    public void shouldReceiveChangesForTableWithoutPrimaryKey() throws Exception {
        TestHelper.execute("DROP TABLE IF EXISTS test_table;", "CREATE TABLE test_table (id SERIAL, text TEXT);", "ALTER TABLE test_table REPLICA IDENTITY FULL");
        consumer = testConsumer(1);
        recordsProducer.start(consumer, blackHole);
        // INSERT
        String statement = "INSERT INTO test_table (text) VALUES ('a');";
        assertInsert(statement, // SERIAL is NOT NULL implicitly
        Arrays.asList(new AbstractRecordsProducerTest.SchemaAndValueField("id", SchemaBuilder.INT32_SCHEMA, 1), new AbstractRecordsProducerTest.SchemaAndValueField("text", SchemaBuilder.OPTIONAL_STRING_SCHEMA, "a")));
        // UPDATE
        consumer.expects(1);
        executeAndWait("UPDATE test_table set text='b' WHERE id=1");
        SourceRecord updatedRecord = consumer.remove();
        VerifyRecord.isValidUpdate(updatedRecord);
        List<AbstractRecordsProducerTest.SchemaAndValueField> expectedBefore = Arrays.asList(new AbstractRecordsProducerTest.SchemaAndValueField("id", SchemaBuilder.INT32_SCHEMA, 1), new AbstractRecordsProducerTest.SchemaAndValueField("text", SchemaBuilder.OPTIONAL_STRING_SCHEMA, "a"));
        assertRecordSchemaAndValues(expectedBefore, updatedRecord, BEFORE);
        List<AbstractRecordsProducerTest.SchemaAndValueField> expectedAfter = Arrays.asList(new AbstractRecordsProducerTest.SchemaAndValueField("id", SchemaBuilder.INT32_SCHEMA, 1), new AbstractRecordsProducerTest.SchemaAndValueField("text", SchemaBuilder.OPTIONAL_STRING_SCHEMA, "b"));
        assertRecordSchemaAndValues(expectedAfter, updatedRecord, AFTER);
        // DELETE
        consumer.expects(2);
        executeAndWait("DELETE FROM test_table WHERE id=1");
        SourceRecord deletedRecord = consumer.remove();
        VerifyRecord.isValidDelete(deletedRecord);
        expectedBefore = Arrays.asList(new AbstractRecordsProducerTest.SchemaAndValueField("id", SchemaBuilder.INT32_SCHEMA, 1), new AbstractRecordsProducerTest.SchemaAndValueField("text", SchemaBuilder.OPTIONAL_STRING_SCHEMA, "b"));
        assertRecordSchemaAndValues(expectedBefore, deletedRecord, BEFORE);
        expectedAfter = null;
        assertRecordSchemaAndValues(expectedAfter, deletedRecord, AFTER);
    }

    @Test
    @FixFor("DBZ-1130")
    @SkipWhenDecoderPluginNameIsNot(WAL2JSON)
    public void testPassingStreamParams() throws Exception {
        // Verify that passing stream parameters works by using the WAL2JSON add-tables parameter which acts as a
        // whitelist.
        PostgresConnectorConfig config = new PostgresConnectorConfig(TestHelper.defaultConfig().with(STREAM_PARAMS, "add-tables=s1.should_stream").build());
        setupRecordsProducer(config);
        String statement = "CREATE SCHEMA s1;" + ((("CREATE TABLE s1.should_stream (pk SERIAL, aa integer, PRIMARY KEY(pk));" + "CREATE TABLE s1.should_not_stream (pk SERIAL, aa integer, PRIMARY KEY(pk));") + "INSERT INTO s1.should_not_stream (aa) VALUES (456);") + "INSERT INTO s1.should_stream (aa) VALUES (123);");
        // Verify only one record made it
        consumer = testConsumer(1);
        recordsProducer.start(consumer, blackHole);
        executeAndWait(statement);
        // Verify the record that made it was from the whitelisted table
        assertRecordInserted("s1.should_stream", TestHelper.PK_FIELD, 1);
        assertThat(consumer.isEmpty()).isTrue();
    }

    @Test
    @FixFor("DBZ-1130")
    @SkipWhenDecoderPluginNameIsNot(WAL2JSON)
    public void testPassingStreamMultipleParams() throws Exception {
        // Verify that passing multiple stream parameters and multiple parameter values works.
        PostgresConnectorConfig config = new PostgresConnectorConfig(TestHelper.defaultConfig().with(STREAM_PARAMS, "add-tables=s1.should_stream,s2.*;filter-tables=s2.should_not_stream").build());
        setupRecordsProducer(config);
        String statement = "CREATE SCHEMA s1;" + (((((((("CREATE SCHEMA s2;" + "CREATE TABLE s1.should_stream (pk SERIAL, aa integer, PRIMARY KEY(pk));") + "CREATE TABLE s2.should_stream (pk SERIAL, aa integer, PRIMARY KEY(pk));") + "CREATE TABLE s1.should_not_stream (pk SERIAL, aa integer, PRIMARY KEY(pk));") + "CREATE TABLE s2.should_not_stream (pk SERIAL, aa integer, PRIMARY KEY(pk));") + "INSERT INTO s1.should_not_stream (aa) VALUES (456);") + "INSERT INTO s2.should_not_stream (aa) VALUES (111);") + "INSERT INTO s1.should_stream (aa) VALUES (123);") + "INSERT INTO s2.should_stream (aa) VALUES (999);");
        // Verify only the whitelisted record from s1 and s2 made it.
        consumer = testConsumer(2);
        recordsProducer.start(consumer, blackHole);
        executeAndWait(statement);
        // Verify the record that made it was from the whitelisted table
        assertRecordInserted("s1.should_stream", TestHelper.PK_FIELD, 1);
        assertRecordInserted("s2.should_stream", TestHelper.PK_FIELD, 1);
        assertThat(consumer.isEmpty()).isTrue();
    }

    @Test
    @FixFor("DBZ-1146")
    public void shouldReceiveChangesForReplicaIdentityFullTableWithToastedValueTableFromSnapshot() throws Exception {
        testReceiveChangesForReplicaIdentityFullTableWithToastedValue(SchemaRefreshMode.COLUMNS_DIFF_EXCLUDE_UNCHANGED_TOAST, true);
    }

    @Test
    @FixFor("DBZ-1146")
    public void shouldReceiveChangesForReplicaIdentityFullTableWithToastedValueTableFromStreaming() throws Exception {
        testReceiveChangesForReplicaIdentityFullTableWithToastedValue(SchemaRefreshMode.COLUMNS_DIFF_EXCLUDE_UNCHANGED_TOAST, false);
    }

    @Test
    @FixFor("DBZ-1146")
    public void shouldReceiveChangesForReplicaIdentityFullTableWithToastedValueTableFromSnapshotFullDiff() throws Exception {
        testReceiveChangesForReplicaIdentityFullTableWithToastedValue(COLUMNS_DIFF, true);
    }

    @Test
    @FixFor("DBZ-1146")
    public void shouldReceiveChangesForReplicaIdentityFullTableWithToastedValueTableFromStreamingFullDiff() throws Exception {
        testReceiveChangesForReplicaIdentityFullTableWithToastedValue(COLUMNS_DIFF, false);
    }
}

