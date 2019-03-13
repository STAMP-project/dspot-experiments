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
import org.apache.kafka.connect.source.SourceRecord;
import org.junit.Test;


/**
 * Tests around {@code DECIMAL} columns. Keep in sync with {@link MySqlNumericColumnIT}.
 *
 * @author Gunnar Morling
 */
public class MySqlDecimalColumnIT extends AbstractConnectorTest {
    private static final String PRECISION_PARAMETER_KEY = "connect.decimal.precision";

    private static final Path DB_HISTORY_PATH = Files.createTestingPath("file-db-history-decimal-column.txt").toAbsolutePath();

    private final UniqueDatabase DATABASE = new UniqueDatabase("decimalcolumnit", "decimal_column_test").withDbHistoryPath(MySqlDecimalColumnIT.DB_HISTORY_PATH);

    private Configuration config;

    @Test
    @FixFor("DBZ-751")
    public void shouldSetPrecisionSchemaParameter() throws InterruptedException, SQLException {
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
        int numInserts = 1;
        SourceRecords records = consumeRecordsByTopic(((numCreateDatabase + numCreateTables) + numInserts));
        stopConnector();
        assertThat(records).isNotNull();
        records.forEach(this::validate);
        List<SourceRecord> dmls = records.recordsForTopic(DATABASE.topicForTable("dbz_751_decimal_column_test"));
        assertThat(dmls).hasSize(1);
        SourceRecord insert = dmls.get(0);
        Map<String, String> rating1SchemaParameters = insert.valueSchema().field("before").schema().field("rating1").schema().parameters();
        assertThat(rating1SchemaParameters).includes(entry("scale", "0"), entry(MySqlDecimalColumnIT.PRECISION_PARAMETER_KEY, "10"));
        Map<String, String> rating2SchemaParameters = insert.valueSchema().field("before").schema().field("rating2").schema().parameters();
        assertThat(rating2SchemaParameters).includes(entry("scale", "4"), entry(MySqlDecimalColumnIT.PRECISION_PARAMETER_KEY, "8"));
        Map<String, String> rating3SchemaParameters = insert.valueSchema().field("before").schema().field("rating3").schema().parameters();
        assertThat(rating3SchemaParameters).includes(entry("scale", "0"), entry(MySqlDecimalColumnIT.PRECISION_PARAMETER_KEY, "7"));
        Map<String, String> rating4SchemaParameters = insert.valueSchema().field("before").schema().field("rating4").schema().parameters();
        assertThat(rating4SchemaParameters).includes(entry("scale", "0"), entry(MySqlDecimalColumnIT.PRECISION_PARAMETER_KEY, "6"));
    }
}

