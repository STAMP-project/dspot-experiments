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
import io.debezium.doc.FixFor;
import io.debezium.embedded.AbstractConnectorTest;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.apache.kafka.connect.data.Struct;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Randall Hauch
 */
public class MySqlConnectorJsonIT extends AbstractConnectorTest {
    private static final Path DB_HISTORY_PATH = Files.createTestingPath("file-db-history-json.txt").toAbsolutePath();

    private final UniqueDatabase DATABASE = new UniqueDatabase("jsonit", "json_test").withDbHistoryPath(MySqlConnectorJsonIT.DB_HISTORY_PATH);

    private Configuration config;

    @Test
    @FixFor("DBZ-126")
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
        int numCreateTables = 1;
        int numDataRecords = 1;
        SourceRecords records = consumeRecordsByTopic(((numCreateDatabase + numCreateTables) + numDataRecords));
        stopConnector();
        assertThat(records).isNotNull();
        assertThat(records.recordsForTopic(DATABASE.getServerName()).size()).isEqualTo((numCreateDatabase + numCreateTables));
        assertThat(records.recordsForTopic(DATABASE.topicForTable("dbz_126_jsontable")).size()).isEqualTo(1);
        assertThat(records.topics().size()).isEqualTo((1 + numCreateTables));
        assertThat(records.databaseNames().size()).isEqualTo(1);
        assertThat(records.ddlRecordsForDatabase(DATABASE.getDatabaseName()).size()).isEqualTo((numCreateDatabase + numCreateTables));
        assertThat(records.ddlRecordsForDatabase("regression_test")).isNull();
        assertThat(records.ddlRecordsForDatabase("connector_test")).isNull();
        assertThat(records.ddlRecordsForDatabase("readbinlog_test")).isNull();
        records.ddlRecordsForDatabase(DATABASE.getDatabaseName()).forEach(this::print);
        // Check that all records are valid, can be serialized and deserialized ...
        records.forEach(this::validate);
        List<String> errors = new ArrayList<>();
        records.forEach(( record) -> {
            Struct value = ((Struct) (record.value()));
            if (record.topic().endsWith("dbz_126_jsontable")) {
                Struct after = value.getStruct(Envelope.FieldName.AFTER);
                Integer i = after.getInt32("id");
                assertThat(i).isNotNull();
                String json = after.getString("json");
                String expectedBinlog = after.getString("expectedBinlogStr");
                check(json, expectedBinlog, errors::add);
            }
        });
        if (!(errors.isEmpty())) {
            Assert.fail((((("" + (errors.size())) + " errors with JSON records...") + (System.lineSeparator())) + (String.join(System.lineSeparator(), errors))));
        }
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
        int numTables = 1;
        int numDataRecords = 1;
        int numDdlRecords = (numTables * 2) + 3;// for each table (1 drop + 1 create) + for each db (1 create + 1 drop + 1 use)

        int numSetVariables = 1;
        SourceRecords records = consumeRecordsByTopic(((numDdlRecords + numSetVariables) + numDataRecords));
        stopConnector();
        assertThat(records).isNotNull();
        assertThat(records.recordsForTopic(DATABASE.getServerName()).size()).isEqualTo((numDdlRecords + numSetVariables));
        assertThat(records.recordsForTopic(DATABASE.topicForTable("dbz_126_jsontable")).size()).isEqualTo(1);
        assertThat(records.topics().size()).isEqualTo((numTables + 1));
        assertThat(records.databaseNames().size()).isEqualTo(2);
        assertThat(records.databaseNames()).containsOnly(DATABASE.getDatabaseName(), "");
        assertThat(records.ddlRecordsForDatabase(DATABASE.getDatabaseName()).size()).isEqualTo(numDdlRecords);
        assertThat(records.ddlRecordsForDatabase("regression_test")).isNull();
        assertThat(records.ddlRecordsForDatabase("connector_test")).isNull();
        assertThat(records.ddlRecordsForDatabase("readbinlog_test")).isNull();
        assertThat(records.ddlRecordsForDatabase("").size()).isEqualTo(1);// SET statement

        records.ddlRecordsForDatabase(DATABASE.getDatabaseName()).forEach(this::print);
        // Check that all records are valid, can be serialized and deserialized ...
        records.forEach(this::validate);
        List<String> errors = new ArrayList<>();
        records.forEach(( record) -> {
            Struct value = ((Struct) (record.value()));
            if (record.topic().endsWith("dbz_126_jsontable")) {
                Struct after = value.getStruct(Envelope.FieldName.AFTER);
                Integer i = after.getInt32("id");
                assertThat(i).isNotNull();
                String json = after.getString("json");
                String expectedJdbc = after.getString("expectedJdbcStr");
                check(json, expectedJdbc, errors::add);
            }
        });
        if (!(errors.isEmpty())) {
            Assert.fail((((("" + (errors.size())) + " errors with JSON records...") + (System.lineSeparator())) + (String.join(System.lineSeparator(), errors))));
        }
    }
}

