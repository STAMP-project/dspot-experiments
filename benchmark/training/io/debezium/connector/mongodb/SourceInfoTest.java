/**
 * Copyright Debezium Authors.
 *
 * Licensed under the Apache Software License version 2.0, available at http://www.apache.org/licenses/LICENSE-2.0
 */
package io.debezium.connector.mongodb;


import Schema.INT32_SCHEMA;
import Schema.OPTIONAL_INT64_SCHEMA;
import Schema.STRING_SCHEMA;
import SourceInfo.DEBEZIUM_CONNECTOR_KEY;
import SourceInfo.DEBEZIUM_VERSION_KEY;
import SourceInfo.INITIAL_SYNC;
import SourceInfo.NAMESPACE;
import SourceInfo.OPERATION_ID;
import SourceInfo.ORDER;
import SourceInfo.REPLICA_SET_NAME;
import SourceInfo.SERVER_ID_KEY;
import SourceInfo.SERVER_NAME;
import SourceInfo.TIMESTAMP;
import java.util.Map;
import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.data.SchemaBuilder;
import org.apache.kafka.connect.data.Struct;
import org.bson.BsonTimestamp;
import org.bson.Document;
import org.junit.Test;


/**
 *
 *
 * @author Randall Hauch
 */
public class SourceInfoTest {
    private static String REPLICA_SET_NAME = "myReplicaSet";

    private SourceInfo source;

    private Map<String, String> partition;

    @Test
    public void shouldHaveSchemaForSource() {
        Schema schema = source.schema();
        assertThat(schema.name()).isNotEmpty();
        assertThat(schema.version()).isNotNull();
        assertThat(schema.field(SERVER_NAME).schema()).isEqualTo(STRING_SCHEMA);
        assertThat(schema.field(SourceInfo.REPLICA_SET_NAME).schema()).isEqualTo(STRING_SCHEMA);
        assertThat(schema.field(NAMESPACE).schema()).isEqualTo(STRING_SCHEMA);
        assertThat(schema.field(TIMESTAMP).schema()).isEqualTo(INT32_SCHEMA);
        assertThat(schema.field(ORDER).schema()).isEqualTo(INT32_SCHEMA);
        assertThat(schema.field(OPERATION_ID).schema()).isEqualTo(OPTIONAL_INT64_SCHEMA);
        assertThat(schema.field(INITIAL_SYNC).schema()).isEqualTo(SchemaBuilder.bool().optional().defaultValue(false).build());
    }

    @Test
    public void shouldProducePartitionMap() {
        partition = source.partition(SourceInfoTest.REPLICA_SET_NAME);
        assertThat(partition.get(SourceInfo.REPLICA_SET_NAME)).isEqualTo(SourceInfoTest.REPLICA_SET_NAME);
        assertThat(partition.get(SERVER_ID_KEY)).isEqualTo("serverX");
        assertThat(partition.size()).isEqualTo(2);
    }

    @Test
    public void shouldReturnSamePartitionMapForSameReplicaName() {
        partition = source.partition(SourceInfoTest.REPLICA_SET_NAME);
        assertThat(partition).isSameAs(source.partition(SourceInfoTest.REPLICA_SET_NAME));
    }

    @Test
    public void shouldSetAndReturnRecordedOffset() {
        Document event = new Document().append("ts", new BsonTimestamp(100, 2)).append("h", Long.valueOf(1987654321)).append("ns", "dbA.collectA");
        assertThat(source.hasOffset(SourceInfoTest.REPLICA_SET_NAME)).isEqualTo(false);
        source.offsetStructForEvent(SourceInfoTest.REPLICA_SET_NAME, event);
        assertThat(source.hasOffset(SourceInfoTest.REPLICA_SET_NAME)).isEqualTo(true);
        Map<String, ?> offset = source.lastOffset(SourceInfoTest.REPLICA_SET_NAME);
        assertThat(offset.get(TIMESTAMP)).isEqualTo(100);
        assertThat(offset.get(ORDER)).isEqualTo(2);
        assertThat(offset.get(OPERATION_ID)).isEqualTo(1987654321L);
        // Create a new source info and set the offset ...
        Map<String, String> partition = source.partition(SourceInfoTest.REPLICA_SET_NAME);
        source = new SourceInfo("serverX");
        source.setOffsetFor(partition, offset);
        offset = source.lastOffset(SourceInfoTest.REPLICA_SET_NAME);
        assertThat(offset.get(TIMESTAMP)).isEqualTo(100);
        assertThat(offset.get(ORDER)).isEqualTo(2);
        assertThat(offset.get(OPERATION_ID)).isEqualTo(1987654321L);
        BsonTimestamp ts = source.lastOffsetTimestamp(SourceInfoTest.REPLICA_SET_NAME);
        assertThat(ts.getTime()).isEqualTo(100);
        assertThat(ts.getInc()).isEqualTo(2);
        Struct struct = source.lastOffsetStruct(SourceInfoTest.REPLICA_SET_NAME, new CollectionId(SourceInfoTest.REPLICA_SET_NAME, "dbA", "collectA"));
        assertThat(struct.getInt32(TIMESTAMP)).isEqualTo(100);
        assertThat(struct.getInt32(ORDER)).isEqualTo(2);
        assertThat(struct.getInt64(OPERATION_ID)).isEqualTo(1987654321L);
        assertThat(struct.getString(NAMESPACE)).isEqualTo("dbA.collectA");
        assertThat(struct.getString(SourceInfo.REPLICA_SET_NAME)).isEqualTo(SourceInfoTest.REPLICA_SET_NAME);
        assertThat(struct.getString(SERVER_NAME)).isEqualTo("serverX");
        assertThat(struct.getBoolean(INITIAL_SYNC)).isNull();
    }

    @Test
    public void shouldReturnOffsetForUnusedReplicaName() {
        assertThat(source.hasOffset(SourceInfoTest.REPLICA_SET_NAME)).isEqualTo(false);
        Map<String, ?> offset = source.lastOffset(SourceInfoTest.REPLICA_SET_NAME);
        assertThat(offset.get(TIMESTAMP)).isEqualTo(0);
        assertThat(offset.get(ORDER)).isEqualTo(0);
        assertThat(offset.get(OPERATION_ID)).isNull();
        BsonTimestamp ts = source.lastOffsetTimestamp(SourceInfoTest.REPLICA_SET_NAME);
        assertThat(ts.getTime()).isEqualTo(0);
        assertThat(ts.getInc()).isEqualTo(0);
        Struct struct = source.lastOffsetStruct(SourceInfoTest.REPLICA_SET_NAME, new CollectionId(SourceInfoTest.REPLICA_SET_NAME, "dbA", "collectA"));
        assertThat(struct.getInt32(TIMESTAMP)).isEqualTo(0);
        assertThat(struct.getInt32(ORDER)).isEqualTo(0);
        assertThat(struct.getInt64(OPERATION_ID)).isNull();
        assertThat(struct.getString(NAMESPACE)).isEqualTo("dbA.collectA");
        assertThat(struct.getString(SourceInfo.REPLICA_SET_NAME)).isEqualTo(SourceInfoTest.REPLICA_SET_NAME);
        assertThat(struct.getString(SERVER_NAME)).isEqualTo("serverX");
        assertThat(struct.getBoolean(INITIAL_SYNC)).isNull();
        assertThat(source.hasOffset(SourceInfoTest.REPLICA_SET_NAME)).isEqualTo(false);
    }

    @Test
    public void shouldReturnRecordedOffsetForUsedReplicaName() {
        Document event = new Document().append("ts", new BsonTimestamp(100, 2)).append("h", Long.valueOf(1987654321)).append("ns", "dbA.collectA");
        assertThat(source.hasOffset(SourceInfoTest.REPLICA_SET_NAME)).isEqualTo(false);
        source.offsetStructForEvent(SourceInfoTest.REPLICA_SET_NAME, event);
        assertThat(source.hasOffset(SourceInfoTest.REPLICA_SET_NAME)).isEqualTo(true);
        Map<String, ?> offset = source.lastOffset(SourceInfoTest.REPLICA_SET_NAME);
        assertThat(offset.get(TIMESTAMP)).isEqualTo(100);
        assertThat(offset.get(ORDER)).isEqualTo(2);
        assertThat(offset.get(OPERATION_ID)).isEqualTo(1987654321L);
        BsonTimestamp ts = source.lastOffsetTimestamp(SourceInfoTest.REPLICA_SET_NAME);
        assertThat(ts.getTime()).isEqualTo(100);
        assertThat(ts.getInc()).isEqualTo(2);
        Struct struct = source.lastOffsetStruct(SourceInfoTest.REPLICA_SET_NAME, new CollectionId(SourceInfoTest.REPLICA_SET_NAME, "dbA", "collectA"));
        assertThat(struct.getInt32(TIMESTAMP)).isEqualTo(100);
        assertThat(struct.getInt32(ORDER)).isEqualTo(2);
        assertThat(struct.getInt64(OPERATION_ID)).isEqualTo(1987654321L);
        assertThat(struct.getString(NAMESPACE)).isEqualTo("dbA.collectA");
        assertThat(struct.getString(SourceInfo.REPLICA_SET_NAME)).isEqualTo(SourceInfoTest.REPLICA_SET_NAME);
        assertThat(struct.getString(SERVER_NAME)).isEqualTo("serverX");
        assertThat(struct.getBoolean(INITIAL_SYNC)).isNull();
    }

    @Test
    public void shouldReturnOffsetForUnusedReplicaNameDuringInitialSync() {
        source.startInitialSync(SourceInfoTest.REPLICA_SET_NAME);
        assertThat(source.hasOffset(SourceInfoTest.REPLICA_SET_NAME)).isEqualTo(false);
        Map<String, ?> offset = source.lastOffset(SourceInfoTest.REPLICA_SET_NAME);
        assertThat(offset.get(TIMESTAMP)).isEqualTo(0);
        assertThat(offset.get(ORDER)).isEqualTo(0);
        assertThat(offset.get(OPERATION_ID)).isNull();
        BsonTimestamp ts = source.lastOffsetTimestamp(SourceInfoTest.REPLICA_SET_NAME);
        assertThat(ts.getTime()).isEqualTo(0);
        assertThat(ts.getInc()).isEqualTo(0);
        Struct struct = source.lastOffsetStruct(SourceInfoTest.REPLICA_SET_NAME, new CollectionId(SourceInfoTest.REPLICA_SET_NAME, "dbA", "collectA"));
        assertThat(struct.getInt32(TIMESTAMP)).isEqualTo(0);
        assertThat(struct.getInt32(ORDER)).isEqualTo(0);
        assertThat(struct.getInt64(OPERATION_ID)).isNull();
        assertThat(struct.getString(NAMESPACE)).isEqualTo("dbA.collectA");
        assertThat(struct.getString(SourceInfo.REPLICA_SET_NAME)).isEqualTo(SourceInfoTest.REPLICA_SET_NAME);
        assertThat(struct.getString(SERVER_NAME)).isEqualTo("serverX");
        assertThat(struct.getBoolean(INITIAL_SYNC)).isEqualTo(true);
        assertThat(source.hasOffset(SourceInfoTest.REPLICA_SET_NAME)).isEqualTo(false);
    }

    @Test
    public void shouldReturnRecordedOffsetForUsedReplicaNameDuringInitialSync() {
        source.startInitialSync(SourceInfoTest.REPLICA_SET_NAME);
        Document event = new Document().append("ts", new BsonTimestamp(100, 2)).append("h", Long.valueOf(1987654321)).append("ns", "dbA.collectA");
        assertThat(source.hasOffset(SourceInfoTest.REPLICA_SET_NAME)).isEqualTo(false);
        source.offsetStructForEvent(SourceInfoTest.REPLICA_SET_NAME, event);
        assertThat(source.hasOffset(SourceInfoTest.REPLICA_SET_NAME)).isEqualTo(true);
        Map<String, ?> offset = source.lastOffset(SourceInfoTest.REPLICA_SET_NAME);
        assertThat(offset.get(TIMESTAMP)).isEqualTo(100);
        assertThat(offset.get(ORDER)).isEqualTo(2);
        assertThat(offset.get(OPERATION_ID)).isEqualTo(1987654321L);
        BsonTimestamp ts = source.lastOffsetTimestamp(SourceInfoTest.REPLICA_SET_NAME);
        assertThat(ts.getTime()).isEqualTo(100);
        assertThat(ts.getInc()).isEqualTo(2);
        Struct struct = source.lastOffsetStruct(SourceInfoTest.REPLICA_SET_NAME, new CollectionId(SourceInfoTest.REPLICA_SET_NAME, "dbA", "collectA"));
        assertThat(struct.getInt32(TIMESTAMP)).isEqualTo(100);
        assertThat(struct.getInt32(ORDER)).isEqualTo(2);
        assertThat(struct.getInt64(OPERATION_ID)).isEqualTo(1987654321L);
        assertThat(struct.getString(NAMESPACE)).isEqualTo("dbA.collectA");
        assertThat(struct.getString(SourceInfo.REPLICA_SET_NAME)).isEqualTo(SourceInfoTest.REPLICA_SET_NAME);
        assertThat(struct.getString(SERVER_NAME)).isEqualTo("serverX");
        assertThat(struct.getBoolean(INITIAL_SYNC)).isEqualTo(true);
    }

    @Test
    public void versionIsPresent() {
        assertThat(source.offsetStructForEvent("rs", null).getString(DEBEZIUM_VERSION_KEY)).isEqualTo(Module.version());
    }

    @Test
    public void connectorIsPresent() {
        assertThat(source.offsetStructForEvent("rs", null).getString(DEBEZIUM_CONNECTOR_KEY)).isEqualTo(Module.name());
    }
}

