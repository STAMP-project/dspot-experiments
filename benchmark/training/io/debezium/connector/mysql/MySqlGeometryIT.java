/**
 * Copyright Debezium Authors.
 *
 * Licensed under the Apache Software License version 2.0, available at http://www.apache.org/licenses/LICENSE-2.0
 */
package io.debezium.connector.mysql;


import MySqlConnectorConfig.SNAPSHOT_MODE;
import MySqlConnectorConfig.SnapshotMode.NEVER;
import Testing.Files;
import io.debezium.config.Configuration;
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
public class MySqlGeometryIT extends AbstractConnectorTest {
    private static final Path DB_HISTORY_PATH = Files.createTestingPath("file-db-history-json.txt").toAbsolutePath();

    private UniqueDatabase DATABASE;

    private MySqlGeometryIT.DatabaseGeoDifferences databaseDifferences;

    private Configuration config;

    @Test
    public void shouldConsumeAllEventsFromDatabaseUsingBinlogAndNoSnapshot() throws InterruptedException, SQLException {
        // Use the DB configuration to define the connector's configuration ...
        config = DATABASE.defaultConfig().with(SNAPSHOT_MODE, NEVER).build();
        // Start the connector ...
        start(MySqlConnector.class, config);
        // ---------------------------------------------------------------------------------------------------------------
        // Consume all of the events due to startup and initialization of the database
        // ---------------------------------------------------------------------------------------------------------------
        // Testing.Debug.enable();
        int numCreateDatabase = 1;
        int numCreateTables = 2;
        int numDataRecords = (databaseDifferences.geometryPointTableRecords()) + 2;
        SourceRecords records = consumeRecordsByTopic(((numCreateDatabase + numCreateTables) + numDataRecords));
        stopConnector();
        assertThat(records).isNotNull();
        assertThat(records.recordsForTopic(DATABASE.getServerName()).size()).isEqualTo((numCreateDatabase + numCreateTables));
        assertThat(records.recordsForTopic(DATABASE.topicForTable("dbz_222_point")).size()).isEqualTo(databaseDifferences.geometryPointTableRecords());
        assertThat(records.recordsForTopic(DATABASE.topicForTable("dbz_507_geometry")).size()).isEqualTo(2);
        assertThat(records.topics().size()).isEqualTo((1 + numCreateTables));
        assertThat(records.databaseNames().size()).isEqualTo(1);
        assertThat(records.ddlRecordsForDatabase(DATABASE.getDatabaseName()).size()).isEqualTo((numCreateDatabase + numCreateTables));
        assertThat(records.ddlRecordsForDatabase("regression_test")).isNull();
        assertThat(records.ddlRecordsForDatabase("connector_test")).isNull();
        assertThat(records.ddlRecordsForDatabase("readbinlog_test")).isNull();
        assertThat(records.ddlRecordsForDatabase("json_test")).isNull();
        records.ddlRecordsForDatabase(DATABASE.getDatabaseName()).forEach(this::print);
        // Check that all records are valid, can be serialized and deserialized ...
        records.forEach(this::validate);
        records.forEach(( record) -> {
            Struct value = ((Struct) (record.value()));
            if (record.topic().endsWith("dbz_222_point")) {
                assertPoint(value);
            } else
                if (record.topic().endsWith("dbz_507_geometry")) {
                    assertGeomRecord(value);
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
        int numTables = 2;
        int numDataRecords = (databaseDifferences.geometryPointTableRecords()) + 2;
        int numDdlRecords = (numTables * 2) + 3;// for each table (1 drop + 1 create) + for each db (1 create + 1 drop + 1 use)

        int numSetVariables = 1;
        SourceRecords records = consumeRecordsByTopic(((numDdlRecords + numSetVariables) + numDataRecords));
        stopConnector();
        assertThat(records).isNotNull();
        assertThat(records.recordsForTopic(DATABASE.getServerName()).size()).isEqualTo((numDdlRecords + numSetVariables));
        assertThat(records.recordsForTopic(DATABASE.topicForTable("dbz_222_point")).size()).isEqualTo(databaseDifferences.geometryPointTableRecords());
        assertThat(records.recordsForTopic(DATABASE.topicForTable("dbz_507_geometry")).size()).isEqualTo(2);
        assertThat(records.topics().size()).isEqualTo((numTables + 1));
        assertThat(records.databaseNames()).containsOnly(DATABASE.getDatabaseName(), "");
        assertThat(records.ddlRecordsForDatabase(DATABASE.getDatabaseName()).size()).isEqualTo(numDdlRecords);
        assertThat(records.ddlRecordsForDatabase("regression_test")).isNull();
        assertThat(records.ddlRecordsForDatabase("connector_test")).isNull();
        assertThat(records.ddlRecordsForDatabase("readbinlog_test")).isNull();
        assertThat(records.ddlRecordsForDatabase("json_test")).isNull();
        assertThat(records.ddlRecordsForDatabase("").size()).isEqualTo(1);// SET statement

        records.ddlRecordsForDatabase(DATABASE.getDatabaseName()).forEach(this::print);
        // Check that all records are valid, can be serialized and deserialized ...
        records.forEach(this::validate);
        records.forEach(( record) -> {
            Struct value = ((Struct) (record.value()));
            if (record.topic().endsWith("dbz_222_point")) {
                assertPoint(value);
            } else
                if (record.topic().endsWith("dbz_507_geometry")) {
                    assertGeomRecord(value);
                }

        });
    }

    private interface DatabaseGeoDifferences {
        String geometryDatabaseName();

        int geometryPointTableRecords();

        void geometryAssertPoints(Double expectedX, Double expectedY, Double actualX, Double actualY);
    }
}

