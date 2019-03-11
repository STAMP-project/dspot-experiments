/**
 * Copyright Debezium Authors.
 *
 * Licensed under the Apache Software License version 2.0, available at http://www.apache.org/licenses/LICENSE-2.0
 */
package io.debezium.transforms;


import Envelope.Operation.CREATE;
import Envelope.Operation.DELETE;
import io.debezium.doc.FixFor;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.apache.kafka.connect.header.Header;
import org.apache.kafka.connect.source.SourceRecord;
import org.junit.Test;


/**
 *
 *
 * @author Jiri Pechanec
 */
public class UnwrapFromEnvelopeTest {
    private static final String DROP_DELETES = "drop.deletes";

    private static final String DROP_TOMBSTONES = "drop.tombstones";

    private static final String HANDLE_DELETES = "delete.handling.mode";

    private static final String OPERATION_HEADER = "operation.header";

    @Test
    public void testTombstoneDroppedByDefault() {
        try (final UnwrapFromEnvelope<SourceRecord> transform = new UnwrapFromEnvelope()) {
            final Map<String, String> props = new HashMap<>();
            transform.configure(props);
            final SourceRecord tombstone = new SourceRecord(new HashMap(), new HashMap(), "dummy", null, null);
            assertThat(transform.apply(tombstone)).isNull();
        }
    }

    @Test
    public void testTombstoneDroppedConfigured() {
        try (final UnwrapFromEnvelope<SourceRecord> transform = new UnwrapFromEnvelope()) {
            final Map<String, String> props = new HashMap<>();
            props.put(UnwrapFromEnvelopeTest.DROP_TOMBSTONES, "true");
            transform.configure(props);
            final SourceRecord tombstone = new SourceRecord(new HashMap(), new HashMap(), "dummy", null, null);
            assertThat(transform.apply(tombstone)).isNull();
        }
    }

    @Test
    public void testTombstoneForwardConfigured() {
        try (final UnwrapFromEnvelope<SourceRecord> transform = new UnwrapFromEnvelope()) {
            final Map<String, String> props = new HashMap<>();
            props.put(UnwrapFromEnvelopeTest.DROP_TOMBSTONES, "false");
            transform.configure(props);
            final SourceRecord tombstone = new SourceRecord(new HashMap(), new HashMap(), "dummy", null, null);
            assertThat(transform.apply(tombstone)).isEqualTo(tombstone);
        }
    }

    @Test
    public void testDeleteDroppedByDefault() {
        try (final UnwrapFromEnvelope<SourceRecord> transform = new UnwrapFromEnvelope()) {
            final Map<String, String> props = new HashMap<>();
            transform.configure(props);
            final SourceRecord deleteRecord = createDeleteRecord();
            assertThat(transform.apply(deleteRecord)).isNull();
        }
    }

    @Test
    public void testDeleteDroppedConfigured() {
        try (final UnwrapFromEnvelope<SourceRecord> transform = new UnwrapFromEnvelope()) {
            final Map<String, String> props = new HashMap<>();
            props.put(UnwrapFromEnvelopeTest.DROP_DELETES, "true");
            props.put(UnwrapFromEnvelopeTest.OPERATION_HEADER, "true");
            transform.configure(props);
            final SourceRecord deleteRecord = createDeleteRecord();
            assertThat(transform.apply(deleteRecord)).isNull();
        }
    }

    @Test
    public void testDeleteForwardConfigured() {
        try (final UnwrapFromEnvelope<SourceRecord> transform = new UnwrapFromEnvelope()) {
            final Map<String, String> props = new HashMap<>();
            props.put(UnwrapFromEnvelopeTest.DROP_DELETES, "false");
            props.put(UnwrapFromEnvelopeTest.OPERATION_HEADER, "true");
            transform.configure(props);
            final SourceRecord deleteRecord = createDeleteRecord();
            final SourceRecord tombstone = transform.apply(deleteRecord);
            assertThat(tombstone.value()).isNull();
            assertThat(tombstone.headers()).hasSize(1);
            String headerValue = getSourceRecordHeaderByKey(tombstone, transform.DEBEZIUM_OPERATION_HEADER_KEY);
            assertThat(headerValue).isEqualTo(DELETE.code());
        }
    }

    @Test
    public void testHandleDeleteDrop() {
        try (final UnwrapFromEnvelope<SourceRecord> transform = new UnwrapFromEnvelope()) {
            final Map<String, String> props = new HashMap<>();
            props.put(UnwrapFromEnvelopeTest.HANDLE_DELETES, "drop");
            transform.configure(props);
            final SourceRecord deleteRecord = createDeleteRecord();
            assertThat(transform.apply(deleteRecord)).isNull();
        }
    }

    @Test
    public void testHandleDeleteNone() {
        try (final UnwrapFromEnvelope<SourceRecord> transform = new UnwrapFromEnvelope()) {
            final Map<String, String> props = new HashMap<>();
            props.put(UnwrapFromEnvelopeTest.HANDLE_DELETES, "none");
            transform.configure(props);
            final SourceRecord deleteRecord = createDeleteRecord();
            final SourceRecord tombstone = transform.apply(deleteRecord);
            assertThat(tombstone.value()).isNull();
        }
    }

    @Test
    public void testHandleDeleteRewrite() {
        try (final UnwrapFromEnvelope<SourceRecord> transform = new UnwrapFromEnvelope()) {
            final Map<String, String> props = new HashMap<>();
            props.put(UnwrapFromEnvelopeTest.HANDLE_DELETES, "rewrite");
            transform.configure(props);
            final SourceRecord deleteRecord = createDeleteRecord();
            final SourceRecord unwrapped = transform.apply(deleteRecord);
            assertThat(getString("__deleted")).isEqualTo("true");
        }
    }

    @Test
    public void testHandleCreateRewrite() {
        try (final UnwrapFromEnvelope<SourceRecord> transform = new UnwrapFromEnvelope()) {
            final Map<String, String> props = new HashMap<>();
            props.put(UnwrapFromEnvelopeTest.HANDLE_DELETES, "rewrite");
            props.put(UnwrapFromEnvelopeTest.OPERATION_HEADER, "true");
            transform.configure(props);
            final SourceRecord createRecord = createCreateRecord();
            final SourceRecord unwrapped = transform.apply(createRecord);
            assertThat(getString("__deleted")).isEqualTo("false");
            assertThat(unwrapped.headers()).hasSize(1);
            String headerValue = getSourceRecordHeaderByKey(unwrapped, transform.DEBEZIUM_OPERATION_HEADER_KEY);
            assertThat(headerValue).isEqualTo(CREATE.code());
        }
    }

    @Test
    public void testUnwrapCreateRecord() {
        try (final UnwrapFromEnvelope<SourceRecord> transform = new UnwrapFromEnvelope()) {
            final Map<String, String> props = new HashMap<>();
            transform.configure(props);
            final SourceRecord createRecord = createCreateRecord();
            final SourceRecord unwrapped = transform.apply(createRecord);
            assertThat(getInt8("id")).isEqualTo(((byte) (1)));
        }
    }

    @Test
    public void testIgnoreUnknownRecord() {
        try (final UnwrapFromEnvelope<SourceRecord> transform = new UnwrapFromEnvelope()) {
            final Map<String, String> props = new HashMap<>();
            transform.configure(props);
            final SourceRecord unknownRecord = createUnknownRecord();
            assertThat(transform.apply(unknownRecord)).isEqualTo(unknownRecord);
            final SourceRecord unnamedSchemaRecord = createUnknownUnnamedSchemaRecord();
            assertThat(transform.apply(unnamedSchemaRecord)).isEqualTo(unnamedSchemaRecord);
        }
    }

    @Test
    @FixFor("DBZ-971")
    public void testUnwrapPropagatesRecordHeaders() {
        try (final UnwrapFromEnvelope<SourceRecord> transform = new UnwrapFromEnvelope()) {
            final Map<String, String> props = new HashMap<>();
            transform.configure(props);
            final SourceRecord createRecord = createCreateRecord();
            createRecord.headers().addString("application/debezium-test-header", "shouldPropagatePreviousRecordHeaders");
            final SourceRecord unwrapped = transform.apply(createRecord);
            assertThat(getInt8("id")).isEqualTo(((byte) (1)));
            assertThat(unwrapped.headers()).hasSize(1);
            Iterator<Header> headers = unwrapped.headers().allWithName("application/debezium-test-header");
            assertThat(headers.hasNext()).isTrue();
            assertThat(headers.next().value().toString()).isEqualTo("shouldPropagatePreviousRecordHeaders");
        }
    }
}

