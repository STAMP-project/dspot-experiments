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
import java.util.List;
import java.util.Map;
import org.apache.kafka.connect.data.Field;
import org.apache.kafka.connect.source.SourceRecord;
import org.junit.Test;


/**
 * Tests around {@code DECIMAL} columns. Keep in sync with {@link MySqlNumericColumnIT}.
 *
 * @author Gunnar Morling
 */
public class MySqlSourceTypeInSchemaIT extends AbstractConnectorTest {
    private static final String TYPE_NAME_PARAMETER_KEY = "__debezium.source.column.type";

    private static final String TYPE_LENGTH_PARAMETER_KEY = "__debezium.source.column.length";

    private static final String TYPE_SCALE_PARAMETER_KEY = "__debezium.source.column.scale";

    private static final Path DB_HISTORY_PATH = Files.createTestingPath("file-db-history-schema-parameter.txt").toAbsolutePath();

    private final UniqueDatabase DATABASE = new UniqueDatabase("schemaparameterit", "source_type_as_schema_parameter_test").withDbHistoryPath(MySqlSourceTypeInSchemaIT.DB_HISTORY_PATH);

    private Configuration config;

    @Test
    @FixFor("DBZ-644")
    public void shouldPropagateSourceTypeAsSchemaParameter() throws InterruptedException, SQLException {
        // Use the DB configuration to define the connector's configuration ...
        config = DATABASE.defaultConfig().with(SNAPSHOT_MODE, NEVER).with("column.propagate.source.type", ".*c1,.*c2,.*c3.*").build();
        // Start the connector ...
        start(MySqlConnector.class, config);
        // ---------------------------------------------------------------------------------------------------------------
        // Consume all of the events due to startup and initialization of the database
        // ---------------------------------------------------------------------------------------------------------------
        // Testing.Debug.enable();
        int numCreateDatabase = 1;
        int numCreateTables = 1;
        int numInserts = 1;
        SourceRecords records = consumeRecordsByTopic(((numCreateDatabase + numCreateTables) + numInserts));
        stopConnector();
        assertThat(records).isNotNull();
        records.forEach(this::validate);
        List<SourceRecord> dmls = records.recordsForTopic(DATABASE.topicForTable("dbz_644_source_type_mapped_as_schema_parameter_test"));
        assertThat(dmls).hasSize(1);
        SourceRecord insert = dmls.get(0);
        Field before = insert.valueSchema().field("before");
        // no type info requested as per given regexps
        Map<String, String> idSchemaParameters = before.schema().field("id").schema().parameters();
        assertThat(idSchemaParameters).isNull();
        // fixed width, name but no length info
        Map<String, String> c1SchemaParameters = before.schema().field("c1").schema().parameters();
        assertThat(c1SchemaParameters).includes(entry(MySqlSourceTypeInSchemaIT.TYPE_NAME_PARAMETER_KEY, "INT"));
        // fixed width, name but no length info
        Map<String, String> c2SchemaParameters = before.schema().field("c2").schema().parameters();
        assertThat(c2SchemaParameters).includes(entry(MySqlSourceTypeInSchemaIT.TYPE_NAME_PARAMETER_KEY, "MEDIUMINT"));
        // variable width, name and length info
        Map<String, String> c3aSchemaParameters = before.schema().field("c3a").schema().parameters();
        assertThat(c3aSchemaParameters).includes(entry(MySqlSourceTypeInSchemaIT.TYPE_NAME_PARAMETER_KEY, "NUMERIC"), entry(MySqlSourceTypeInSchemaIT.TYPE_LENGTH_PARAMETER_KEY, "5"), entry(MySqlSourceTypeInSchemaIT.TYPE_SCALE_PARAMETER_KEY, "2"));
        // variable width, name and length info
        Map<String, String> c3bSchemaParameters = before.schema().field("c3b").schema().parameters();
        assertThat(c3bSchemaParameters).includes(entry(MySqlSourceTypeInSchemaIT.TYPE_NAME_PARAMETER_KEY, "VARCHAR"), entry(MySqlSourceTypeInSchemaIT.TYPE_LENGTH_PARAMETER_KEY, "128"));
    }
}

