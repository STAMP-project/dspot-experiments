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
import java.nio.ByteBuffer;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.List;
import org.apache.kafka.connect.data.Struct;
import org.apache.kafka.connect.source.SourceRecord;
import org.junit.Test;


/**
 *
 *
 * @author Gunnar Morling
 */
public class MySqlFixedLengthBinaryColumnIT extends AbstractConnectorTest {
    private static final Path DB_HISTORY_PATH = Files.createTestingPath("file-db-history-binary-column.txt").toAbsolutePath();

    private final UniqueDatabase DATABASE = new UniqueDatabase("binarycolumnit", "binary_column_test").withDbHistoryPath(MySqlFixedLengthBinaryColumnIT.DB_HISTORY_PATH);

    private Configuration config;

    @Test
    @FixFor("DBZ-254")
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
        int numInserts = 4;
        SourceRecords records = consumeRecordsByTopic(((numCreateDatabase + numCreateTables) + numInserts));
        stopConnector();
        assertThat(records).isNotNull();
        List<SourceRecord> dmls = records.recordsForTopic(DATABASE.topicForTable("dbz_254_binary_column_test"));
        assertThat(dmls).hasSize(4);
        // source value has a trailing "00" which is not distinguishable from
        SourceRecord insert = dmls.get(0);
        Struct after = ((Struct) (get("after")));
        assertThat(encodeToBase64String(((ByteBuffer) (after.get("file_uuid"))))).isEqualTo("ZRrtCDkPSJOy8TaSPnt0AA==");
        insert = dmls.get(1);
        after = ((Struct) (get("after")));
        assertThat(encodeToBase64String(((ByteBuffer) (after.get("file_uuid"))))).isEqualTo("ZRrtCDkPSJOy8TaSPnt0qw==");
        // the value which isn't using the full length of the BINARY column is right-padded with 0x00 (zero bytes) - converted to AA in Base64
        insert = dmls.get(2);
        after = ((Struct) (get("after")));
        assertThat(encodeToBase64String(((ByteBuffer) (after.get("file_uuid"))))).isEqualTo("ZRrtCDkPSJOy8TaSPnt0AA==");
        // the value which isn't using the full length of the BINARY column is right-padded with 0x00 (zero bytes)
        insert = dmls.get(3);
        after = ((Struct) (get("after")));
        assertThat(encodeToBase64String(((ByteBuffer) (after.get("file_uuid"))))).isEqualTo("AAAAAAAAAAAAAAAAAAAAAA==");
        // Check that all records are valid, can be serialized and deserialized ...
        records.forEach(this::validate);
    }
}

