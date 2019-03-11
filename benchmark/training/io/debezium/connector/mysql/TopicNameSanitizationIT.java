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
import io.debezium.data.VerifyRecord;
import io.debezium.doc.FixFor;
import io.debezium.embedded.AbstractConnectorTest;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.List;
import org.apache.kafka.connect.source.SourceRecord;
import org.junit.Test;


/**
 * Tests around {@code NUMERIC} columns. Keep in sync with {@link MySqlDecimalColumnIT}.
 *
 * @author Gunnar Morling
 */
public class TopicNameSanitizationIT extends AbstractConnectorTest {
    private static final Path DB_HISTORY_PATH = Files.createTestingPath("file-db-history-topic-name-sanitization.txt").toAbsolutePath();

    private final UniqueDatabase DATABASE = new UniqueDatabase("topic-name-sanitization-it", "topic_name_sanitization_test").withDbHistoryPath(TopicNameSanitizationIT.DB_HISTORY_PATH);

    private Configuration config;

    @Test
    @FixFor("DBZ-878")
    public void shouldReplaceInvalidTopicNameCharacters() throws InterruptedException, SQLException {
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
        List<SourceRecord> dmls = records.recordsForTopic(DATABASE.topicForTable("dbz_878_some_test_data"));
        assertThat(dmls).hasSize(1);
        SourceRecord insert = dmls.get(0);
        assertThat(insert.valueSchema().name()).endsWith("dbz_878_some_test_data.Envelope");
        VerifyRecord.isValidInsert(insert, "id", 1);
        String sourceTable = getStruct("source").getString("table");
        assertThat(sourceTable).isEqualTo("dbz_878_some|test@data");
    }
}

