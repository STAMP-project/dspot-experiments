/**
 * Copyright Debezium Authors.
 *
 * Licensed under the Apache Software License version 2.0, available at http://www.apache.org/licenses/LICENSE-2.0
 */
package io.debezium.connector.mysql;


import org.junit.Test;


/**
 * Run the {@link MySqlConnector} in various configurations and against different MySQL server instances
 * and verify the output is as expected.
 *
 * @author Randall Hauch
 */
public class MySqlConnectorReplicaIT extends AbstractMySqlConnectorOutputTest {
    /**
     * Perform a snapshot of the entire MySQL server (with GTIDs) and ignore built-in tables.
     * This checks the approximately 160 change events against expected values.
     */
    @Test
    public void shouldCaptureChangesFromMySqlWithGtidsUsingSnapshot() {
        runConnector(usingSpec("mysql-replica-gtids", "src/test/expected/replica/snapshot/no-filter").withVariables(this::readSystemVariables));
    }

    /**
     * Perform a snapshot of the some of the MySQL server (with GTIDs) and ignore built-in tables.
     */
    @Test
    public void shouldCaptureChangesFromMySqlWithGtidsUsingSnapshotIncludingSpecificDatabases() {
        runConnector(usingSpec("mysql-replica-gtids-with-dbs", "src/test/expected/replica/snapshot/filter-db").withVariables(this::readSystemVariables));
    }

    /**
     * Perform a snapshot of the some of the MySQL server (with GTIDs) and ignore built-in tables.
     */
    @Test
    public void shouldCaptureChangesFromMySqlWithGtidsUsingSnapshotIncludingSpecificTables() {
        runConnector(usingSpec("mysql-replica-gtids-with-tables", "src/test/expected/replica/snapshot/filter-table").withVariables(this::readSystemVariables));
    }

    /**
     * Read binlog of the entire MySQL server (with GTIDs) and ignore built-in tables.
     */
    @Test
    public void shouldCaptureChangesFromMySqlWithGtidsUsingNoSnapshot() {
        runConnector(usingSpec("mysql-replica-gtids-nosnap", "src/test/expected/replica/no-snapshot/no-filter").withVariables(this::readSystemVariables));
    }

    /**
     * Read binlog of some of the MySQL server (with GTIDs) and ignore built-in tables.
     */
    @Test
    public void shouldCaptureChangesFromMySqlWithGtidsUsingNoSnapshotIncludingSpecificDatabases() {
        runConnector(usingSpec("mysql-replica-gtids-nosnap-with-dbs", "src/test/expected/replica/no-snapshot/filter-db").withVariables(this::readSystemVariables));
    }

    /**
     * Read binlog of some of the MySQL server (with GTIDs) and ignore built-in tables.
     */
    @Test
    public void shouldCaptureChangesFromMySqlWithGtidsUsingNoSnapshotIncludingSpecificTables() {
        runConnector(usingSpec("mysql-replica-gtids-nosnap-with-tables", "src/test/expected/replica/no-snapshot/filter-table").withVariables(this::readSystemVariables));
    }
}

