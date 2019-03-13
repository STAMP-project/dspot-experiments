/**
 * Copyright Debezium Authors.
 *
 * Licensed under the Apache Software License version 2.0, available at http://www.apache.org/licenses/LICENSE-2.0
 */
package io.debezium.connector.mysql;


import MySqlConnectorConfig.INCLUDE_SCHEMA_CHANGES;
import MySqlConnectorConfig.INCLUDE_SQL_QUERY;
import MySqlConnectorConfig.MAX_BATCH_SIZE;
import MySqlConnectorConfig.MAX_QUEUE_SIZE;
import MySqlConnectorConfig.POLL_INTERVAL_MS;
import MySqlConnectorConfig.SNAPSHOT_MODE;
import org.junit.Test;


/**
 *
 *
 * @author Randall Hauch
 */
public class MySqlTaskContextIT extends MySqlTaskContextTest {
    @Test
    public void shouldCreateTaskFromConfiguration() throws Exception {
        config = simpleConfig().build();
        context = new MySqlTaskContext(config, build());
        context.start();
        assertThat(context.config()).isSameAs(config);
        assertThat(context.getClock()).isNotNull();
        assertThat(context.dbSchema()).isNotNull();
        assertThat(context.getConnectionContext().jdbc()).isNotNull();
        assertThat(context.getConnectionContext().logger()).isNotNull();
        assertThat(context.makeRecord()).isNotNull();
        assertThat(context.source()).isNotNull();
        assertThat(context.topicSelector()).isNotNull();
        assertThat(context.getConnectionContext().hostname()).isEqualTo(hostname);
        assertThat(context.getConnectionContext().port()).isEqualTo(port);
        assertThat(context.getConnectionContext().username()).isEqualTo(username);
        assertThat(context.getConnectionContext().password()).isEqualTo(password);
        assertThat(context.serverId()).isEqualTo(serverId);
        assertThat(context.getConnectorConfig().getLogicalName()).isEqualTo(serverName);
        assertThat(("" + (context.includeSchemaChangeRecords()))).isEqualTo(INCLUDE_SCHEMA_CHANGES.defaultValueAsString());
        assertThat(("" + (context.includeSqlQuery()))).isEqualTo(INCLUDE_SQL_QUERY.defaultValueAsString());
        assertThat(("" + (context.getConnectorConfig().getMaxBatchSize()))).isEqualTo(MAX_BATCH_SIZE.defaultValueAsString());
        assertThat(("" + (context.getConnectorConfig().getMaxQueueSize()))).isEqualTo(MAX_QUEUE_SIZE.defaultValueAsString());
        assertThat(("" + (context.getConnectorConfig().getPollInterval().toMillis()))).isEqualTo(POLL_INTERVAL_MS.defaultValueAsString());
        assertThat(("" + (context.snapshotMode().getValue()))).isEqualTo(SNAPSHOT_MODE.defaultValueAsString());
        // Snapshot default is 'initial' ...
        assertThat(context.isSnapshotAllowedWhenNeeded()).isEqualTo(false);
        assertThat(context.isSnapshotNeverAllowed()).isEqualTo(false);
        // JDBC connection is automatically created by MySqlTaskContext when it reads database variables
        assertConnectedToJdbc();
    }

    @Test
    public void shouldCloseJdbcConnectionOnShutdown() throws Exception {
        config = simpleConfig().build();
        context = new MySqlTaskContext(config, build());
        context.start();
        // JDBC connection is automatically created by MySqlTaskContext when it reads database variables
        assertConnectedToJdbc();
        context.shutdown();
        assertNotConnectedToJdbc();
    }
}

