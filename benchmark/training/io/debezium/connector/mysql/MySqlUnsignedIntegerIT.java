/**
 * Copyright Debezium Authors.
 *
 * Licensed under the Apache Software License version 2.0, available at http://www.apache.org/licenses/LICENSE-2.0
 */
package io.debezium.connector.mysql;


import MySqlConnectorConfig.BIGINT_UNSIGNED_HANDLING_MODE;
import MySqlConnectorConfig.BigIntUnsignedHandlingMode.LONG;
import MySqlConnectorConfig.BigIntUnsignedHandlingMode.PRECISE;
import MySqlConnectorConfig.SNAPSHOT_MODE;
import MySqlConnectorConfig.SnapshotMode.NEVER;
import Testing.Debug;
import Testing.Files;
import io.debezium.config.Configuration;
import io.debezium.doc.FixFor;
import io.debezium.embedded.AbstractConnectorTest;
import java.nio.file.Path;
import java.sql.SQLException;
import org.apache.kafka.connect.data.Struct;
import org.junit.Test;


/**
 *
 *
 * @author Omar Al-Safi
 */
public class MySqlUnsignedIntegerIT extends AbstractConnectorTest {
    private static final Path DB_HISTORY_PATH = Files.createTestingPath("file-db-history-json.txt").toAbsolutePath();

    private Configuration config;

    private final UniqueDatabase DATABASE = new UniqueDatabase("unsignednumericit", "unsigned_integer_test").withDbHistoryPath(MySqlUnsignedIntegerIT.DB_HISTORY_PATH);

    @Test
    public void shouldConsumeAllEventsFromDatabaseUsingBinlogAndNoSnapshot() throws InterruptedException, SQLException {
        // Use the DB configuration to define the connector's configuration ...
        config = DATABASE.defaultConfig().with(SNAPSHOT_MODE, NEVER).with(BIGINT_UNSIGNED_HANDLING_MODE, PRECISE).build();
        // Start the connector ...
        start(MySqlConnector.class, config);
        // ---------------------------------------------------------------------------------------------------------------
        // Consume all of the events due to startup and initialization of the database
        // ---------------------------------------------------------------------------------------------------------------
        Debug.enable();
        int numCreateDatabase = 1;
        int numCreateTables = 5;
        int numDataRecords = numCreateTables * 3;// Total data records

        SourceRecords records = consumeRecordsByTopic(((numCreateDatabase + numCreateTables) + numDataRecords));
        stopConnector();
        assertThat(records).isNotNull();
        assertThat(records.recordsForTopic("unsignednumericit").size()).isEqualTo((numCreateDatabase + numCreateTables));
        assertThat(records.recordsForTopic(DATABASE.topicForTable("dbz_228_tinyint_unsigned")).size()).isEqualTo(3);
        assertThat(records.recordsForTopic(DATABASE.topicForTable("dbz_228_smallint_unsigned")).size()).isEqualTo(3);
        assertThat(records.recordsForTopic(DATABASE.topicForTable("dbz_228_mediumint_unsigned")).size()).isEqualTo(3);
        assertThat(records.recordsForTopic(DATABASE.topicForTable("dbz_228_int_unsigned")).size()).isEqualTo(3);
        assertThat(records.recordsForTopic(DATABASE.topicForTable("dbz_228_bigint_unsigned")).size()).isEqualTo(3);
        assertThat(records.topics().size()).isEqualTo((1 + numCreateTables));
        assertThat(records.databaseNames().size()).isEqualTo(1);
        assertThat(records.ddlRecordsForDatabase(DATABASE.getDatabaseName()).size()).isEqualTo((numCreateDatabase + numCreateTables));
        assertThat(records.ddlRecordsForDatabase("regression_test")).isNull();
        assertThat(records.ddlRecordsForDatabase("connector_test")).isNull();
        assertThat(records.ddlRecordsForDatabase("readbinlog_test")).isNull();
        assertThat(records.ddlRecordsForDatabase("json_test")).isNull();
        assertThat(records.ddlRecordsForDatabase("geometry_test")).isNull();
        records.ddlRecordsForDatabase(DATABASE.getDatabaseName()).forEach(this::print);
        // Check that all records are valid, can be serialized and deserialized ...
        records.forEach(this::validate);
        records.forEach(( record) -> {
            Struct value = ((Struct) (record.value()));
            if (record.topic().endsWith("dbz_228_int_unsigned")) {
                assertIntUnsigned(value);
            } else
                if (record.topic().endsWith("dbz_228_tinyint_unsigned")) {
                    assertTinyintUnsigned(value);
                } else
                    if (record.topic().endsWith("dbz_228_smallint_unsigned")) {
                        assertSmallUnsigned(value);
                    } else
                        if (record.topic().endsWith("dbz_228_mediumint_unsigned")) {
                            assertMediumUnsigned(value);
                        } else
                            if (record.topic().endsWith("dbz_228_bigint_unsigned")) {
                                assertBigintUnsignedPrecise(value);
                            }




        });
    }

    @Test
    @FixFor("DBZ-363")
    public void shouldConsumeAllEventsFromBigIntTableInDatabaseUsingBinlogAndNoSnapshotUsingLong() throws InterruptedException, SQLException {
        // Use the DB configuration to define the connector's configuration ...
        config = DATABASE.defaultConfig().with(SNAPSHOT_MODE, NEVER.toString()).with(BIGINT_UNSIGNED_HANDLING_MODE, LONG).build();
        // Start the connector ...
        start(MySqlConnector.class, config);
        // ---------------------------------------------------------------------------------------------------------------
        // Consume all of the events due to startup and initialization of the database
        // ---------------------------------------------------------------------------------------------------------------
        Debug.enable();
        int numCreateDatabase = 1;
        int numCreateTables = 5;
        int numDataRecords = numCreateTables * 3;// Total data records

        SourceRecords records = consumeRecordsByTopic(((numCreateDatabase + numCreateTables) + numDataRecords));
        stopConnector();
        assertThat(records).isNotNull();
        assertThat(records.recordsForTopic(DATABASE.getServerName()).size()).isEqualTo((numCreateDatabase + numCreateTables));
        assertThat(records.recordsForTopic(DATABASE.topicForTable("dbz_228_bigint_unsigned")).size()).isEqualTo(3);
        assertThat(records.topics().size()).isEqualTo((1 + numCreateTables));
        // Check that all records are valid, can be serialized and deserialized ...
        records.forEach(this::validate);
        records.forEach(( record) -> {
            Struct value = ((Struct) (record.value()));
            if (record.topic().endsWith("dbz_228_bigint_unsigned")) {
                assertBigintUnsignedLong(value);
            }
        });
    }

    @Test
    public void shouldConsumeAllEventsFromDatabaseUsingSnapshot() throws InterruptedException, SQLException {
        // Use the DB configuration to define the connector's configuration ...
        config = DATABASE.defaultConfig().build();
        // Start the connector ...
        start(MySqlConnector.class, config);
        // ---------------------------------------------------------------------------------------------------------------
        // Consume all of the events due to startup and initialization of the database
        // ---------------------------------------------------------------------------------------------------------------
        // Testing.Debug.enable();
        int numTables = 5;
        int numDataRecords = numTables * 3;
        int numDdlRecords = (numTables * 2) + 3;// for each table (1 drop + 1 create) + for each db (1 create + 1 drop + 1 use)

        int numSetVariables = 1;
        SourceRecords records = consumeRecordsByTopic(((numDdlRecords + numSetVariables) + numDataRecords));
        stopConnector();
        assertThat(records).isNotNull();
        assertThat(records.recordsForTopic("unsignednumericit").size()).isEqualTo((numDdlRecords + numSetVariables));
        assertThat(records.recordsForTopic(DATABASE.topicForTable("dbz_228_tinyint_unsigned")).size()).isEqualTo(3);
        assertThat(records.recordsForTopic(DATABASE.topicForTable("dbz_228_smallint_unsigned")).size()).isEqualTo(3);
        assertThat(records.recordsForTopic(DATABASE.topicForTable("dbz_228_mediumint_unsigned")).size()).isEqualTo(3);
        assertThat(records.recordsForTopic(DATABASE.topicForTable("dbz_228_int_unsigned")).size()).isEqualTo(3);
        assertThat(records.recordsForTopic(DATABASE.topicForTable("dbz_228_bigint_unsigned")).size()).isEqualTo(3);
        assertThat(records.topics().size()).isEqualTo((numTables + 1));
        assertThat(records.databaseNames()).containsOnly(DATABASE.getDatabaseName(), "");
        assertThat(records.ddlRecordsForDatabase(DATABASE.getDatabaseName()).size()).isEqualTo(numDdlRecords);
        assertThat(records.ddlRecordsForDatabase("regression_test")).isNull();
        assertThat(records.ddlRecordsForDatabase("connector_test")).isNull();
        assertThat(records.ddlRecordsForDatabase("readbinlog_test")).isNull();
        assertThat(records.ddlRecordsForDatabase("json_test")).isNull();
        assertThat(records.ddlRecordsForDatabase("geometry_test")).isNull();
        assertThat(records.ddlRecordsForDatabase("").size()).isEqualTo(1);// SET statement

        records.ddlRecordsForDatabase(DATABASE.getDatabaseName()).forEach(this::print);
        // Check that all records are valid, can be serialized and deserialized ...
        records.forEach(this::validate);
        records.forEach(( record) -> {
            Struct value = ((Struct) (record.value()));
            if (record.topic().endsWith("dbz_228_int_unsigned")) {
                assertIntUnsigned(value);
            } else
                if (record.topic().endsWith("dbz_228_tinyint_unsigned")) {
                    assertTinyintUnsigned(value);
                } else
                    if (record.topic().endsWith("dbz_228_smallint_unsigned")) {
                        assertSmallUnsigned(value);
                    } else
                        if (record.topic().endsWith("dbz_228_mediumint_unsigned")) {
                            assertMediumUnsigned(value);
                        } else
                            if (record.topic().endsWith("dbz_228_bigint_unsigned")) {
                                assertBigintUnsignedLong(value);
                            }




        });
    }
}

