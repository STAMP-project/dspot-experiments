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
import org.junit.Test;


/**
 *
 *
 * @author Gunnar Morling
 */
public class MySqlTableMaintenanceStatementsIT extends AbstractConnectorTest {
    private static final Path DB_HISTORY_PATH = Files.createTestingPath("file-db-history-table-maintenance.txt").toAbsolutePath();

    private final UniqueDatabase DATABASE = new UniqueDatabase("tablemaintenanceit", "table_maintenance_test").withDbHistoryPath(MySqlTableMaintenanceStatementsIT.DB_HISTORY_PATH);

    private Configuration config;

    @Test
    @FixFor("DBZ-253")
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
        int numTableMaintenanceStatements = 3;
        SourceRecords records = consumeRecordsByTopic(((numCreateDatabase + numCreateTables) + numTableMaintenanceStatements));
        System.out.println(records.allRecordsInOrder());
        stopConnector();
        assertThat(records).isNotNull();
        assertThat(records.recordsForTopic(DATABASE.getServerName()).size()).isEqualTo(((numCreateDatabase + numCreateTables) + numTableMaintenanceStatements));
        assertThat(records.databaseNames()).containsOnly(DATABASE.getDatabaseName());
        assertThat(records.ddlRecordsForDatabase(DATABASE.getDatabaseName()).size()).isEqualTo(((numCreateDatabase + numCreateTables) + numTableMaintenanceStatements));
        // Check that all records are valid, can be serialized and deserialized ...
        records.forEach(this::validate);
    }
}

