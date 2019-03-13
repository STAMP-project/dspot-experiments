/**
 * Copyright 2016 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.cloud.logging;


import HttpRequest.RequestMethod.GET;
import HttpRequest.RequestMethod.POST;
import Severity.DEBUG;
import Severity.DEFAULT;
import com.google.cloud.MonitoredResource;
import com.google.cloud.logging.Payload.JsonPayload;
import com.google.cloud.logging.Payload.ProtoPayload;
import com.google.cloud.logging.Payload.StringPayload;
import com.google.common.collect.ImmutableMap;
import com.google.protobuf.Any;
import com.google.protobuf.Empty;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;

import static Severity.ALERT;


public class LogEntryTest {
    private static final String LOG_NAME = "syslog";

    private static final MonitoredResource RESOURCE = MonitoredResource.newBuilder("cloudsql_database").setLabels(ImmutableMap.of("datasetId", "myDataset", "zone", "myZone")).build();

    private static final long TIMESTAMP = 42;

    private static final long RECEIVE_TIMESTAMP = 24;

    private static final Severity SEVERITY = ALERT;

    private static final String INSERT_ID = "insertId";

    private static final HttpRequest HTTP_REQUEST = HttpRequest.newBuilder().setRequestMethod(GET).setStatus(404).build();

    private static final Map<String, String> LABELS = ImmutableMap.of("key1", "value1", "key2", "value2");

    private static final Operation OPERATION = Operation.of("id", "producer");

    private static final String TRACE = "trace";

    private static final Object TRACE_FORMATTER = new Object() {
        @Override
        public String toString() {
            return LogEntryTest.TRACE;
        }
    };

    private static final String SPAN_ID = "spanId";

    private static final Object SPAN_ID_FORMATTER = new Object() {
        @Override
        public String toString() {
            return LogEntryTest.SPAN_ID;
        }
    };

    private static final boolean TRACE_SAMPLED = true;

    private static final SourceLocation SOURCE_LOCATION = new SourceLocation.Builder().setFile("file").setLine(42L).setFunction("function").build();

    private static final StringPayload STRING_PAYLOAD = StringPayload.of("payload");

    private static final JsonPayload JSON_PAYLOAD = JsonPayload.of(ImmutableMap.<String, Object>of("key", "val"));

    private static final ProtoPayload PROTO_PAYLOAD = ProtoPayload.of(Any.pack(Empty.getDefaultInstance()));

    private static final LogEntry STRING_ENTRY = LogEntry.newBuilder(LogEntryTest.STRING_PAYLOAD).setLogName(LogEntryTest.LOG_NAME).setResource(LogEntryTest.RESOURCE).setTimestamp(LogEntryTest.TIMESTAMP).setReceiveTimestamp(LogEntryTest.RECEIVE_TIMESTAMP).setSeverity(LogEntryTest.SEVERITY).setInsertId(LogEntryTest.INSERT_ID).setHttpRequest(LogEntryTest.HTTP_REQUEST).setLabels(LogEntryTest.LABELS).setOperation(LogEntryTest.OPERATION).setTrace(LogEntryTest.TRACE_FORMATTER).setSpanId(LogEntryTest.SPAN_ID_FORMATTER).setTraceSampled(LogEntryTest.TRACE_SAMPLED).setSourceLocation(LogEntryTest.SOURCE_LOCATION).build();

    private static final LogEntry JSON_ENTRY = LogEntry.newBuilder(LogEntryTest.JSON_PAYLOAD).setLogName(LogEntryTest.LOG_NAME).setResource(LogEntryTest.RESOURCE).setTimestamp(LogEntryTest.TIMESTAMP).setReceiveTimestamp(LogEntryTest.RECEIVE_TIMESTAMP).setSeverity(LogEntryTest.SEVERITY).setInsertId(LogEntryTest.INSERT_ID).setHttpRequest(LogEntryTest.HTTP_REQUEST).setLabels(LogEntryTest.LABELS).setOperation(LogEntryTest.OPERATION).setTrace(LogEntryTest.TRACE_FORMATTER).setSpanId(LogEntryTest.SPAN_ID_FORMATTER).setTraceSampled(LogEntryTest.TRACE_SAMPLED).setSourceLocation(LogEntryTest.SOURCE_LOCATION).build();

    private static final LogEntry PROTO_ENTRY = LogEntry.newBuilder(LogEntryTest.PROTO_PAYLOAD).setLogName(LogEntryTest.LOG_NAME).setResource(LogEntryTest.RESOURCE).setTimestamp(LogEntryTest.TIMESTAMP).setReceiveTimestamp(LogEntryTest.RECEIVE_TIMESTAMP).setSeverity(LogEntryTest.SEVERITY).setInsertId(LogEntryTest.INSERT_ID).setHttpRequest(LogEntryTest.HTTP_REQUEST).setLabels(LogEntryTest.LABELS).setOperation(LogEntryTest.OPERATION).setTrace(LogEntryTest.TRACE_FORMATTER).setSpanId(LogEntryTest.SPAN_ID_FORMATTER).setTraceSampled(LogEntryTest.TRACE_SAMPLED).setSourceLocation(LogEntryTest.SOURCE_LOCATION).build();

    @Test
    public void testOf() {
        LogEntry logEntry = LogEntry.of(LogEntryTest.STRING_PAYLOAD);
        Assert.assertEquals(LogEntryTest.STRING_PAYLOAD, logEntry.getPayload());
        Assert.assertEquals(DEFAULT, logEntry.getSeverity());
        Assert.assertEquals(ImmutableMap.of(), logEntry.getLabels());
        Assert.assertNull(logEntry.getLogName());
        Assert.assertNull(logEntry.getResource());
        Assert.assertNull(logEntry.getTimestamp());
        Assert.assertNull(logEntry.getReceiveTimestamp());
        Assert.assertNull(logEntry.getInsertId());
        Assert.assertNull(logEntry.getHttpRequest());
        Assert.assertNull(logEntry.getOperation());
        Assert.assertNull(logEntry.getTrace());
        Assert.assertNull(logEntry.getSpanId());
        Assert.assertFalse(logEntry.getTraceSampled());
        Assert.assertNull(logEntry.getSourceLocation());
        logEntry = LogEntry.of(LogEntryTest.LOG_NAME, LogEntryTest.RESOURCE, LogEntryTest.STRING_PAYLOAD);
        Assert.assertEquals(LogEntryTest.STRING_PAYLOAD, logEntry.getPayload());
        Assert.assertEquals(LogEntryTest.LOG_NAME, logEntry.getLogName());
        Assert.assertEquals(LogEntryTest.RESOURCE, logEntry.getResource());
        Assert.assertEquals(DEFAULT, logEntry.getSeverity());
        Assert.assertEquals(ImmutableMap.of(), logEntry.getLabels());
        Assert.assertEquals(ImmutableMap.of(), logEntry.getLabels());
        Assert.assertNull(logEntry.getTimestamp());
        Assert.assertNull(logEntry.getReceiveTimestamp());
        Assert.assertNull(logEntry.getInsertId());
        Assert.assertNull(logEntry.getHttpRequest());
        Assert.assertNull(logEntry.getOperation());
        Assert.assertNull(logEntry.getTrace());
        Assert.assertNull(logEntry.getSpanId());
        Assert.assertFalse(logEntry.getTraceSampled());
        Assert.assertNull(logEntry.getSourceLocation());
    }

    @Test
    public void testBuilder() {
        Assert.assertEquals(LogEntryTest.LOG_NAME, LogEntryTest.STRING_ENTRY.getLogName());
        Assert.assertEquals(LogEntryTest.RESOURCE, LogEntryTest.STRING_ENTRY.getResource());
        Assert.assertEquals(LogEntryTest.TIMESTAMP, ((long) (LogEntryTest.STRING_ENTRY.getTimestamp())));
        Assert.assertEquals(LogEntryTest.RECEIVE_TIMESTAMP, ((long) (LogEntryTest.STRING_ENTRY.getReceiveTimestamp())));
        Assert.assertEquals(LogEntryTest.SEVERITY, LogEntryTest.STRING_ENTRY.getSeverity());
        Assert.assertEquals(LogEntryTest.INSERT_ID, LogEntryTest.STRING_ENTRY.getInsertId());
        Assert.assertEquals(LogEntryTest.HTTP_REQUEST, LogEntryTest.STRING_ENTRY.getHttpRequest());
        Assert.assertEquals(LogEntryTest.LABELS, LogEntryTest.STRING_ENTRY.getLabels());
        Assert.assertEquals(LogEntryTest.OPERATION, LogEntryTest.STRING_ENTRY.getOperation());
        Assert.assertEquals(LogEntryTest.TRACE, LogEntryTest.STRING_ENTRY.getTrace());
        Assert.assertEquals(LogEntryTest.SPAN_ID, LogEntryTest.STRING_ENTRY.getSpanId());
        Assert.assertEquals(LogEntryTest.TRACE_SAMPLED, LogEntryTest.STRING_ENTRY.getTraceSampled());
        Assert.assertEquals(LogEntryTest.SOURCE_LOCATION, LogEntryTest.STRING_ENTRY.getSourceLocation());
        Assert.assertEquals(LogEntryTest.STRING_PAYLOAD, LogEntryTest.STRING_ENTRY.getPayload());
        Assert.assertEquals(LogEntryTest.LOG_NAME, LogEntryTest.JSON_ENTRY.getLogName());
        Assert.assertEquals(LogEntryTest.RESOURCE, LogEntryTest.JSON_ENTRY.getResource());
        Assert.assertEquals(LogEntryTest.TIMESTAMP, ((long) (LogEntryTest.JSON_ENTRY.getTimestamp())));
        Assert.assertEquals(LogEntryTest.RECEIVE_TIMESTAMP, ((long) (LogEntryTest.JSON_ENTRY.getReceiveTimestamp())));
        Assert.assertEquals(LogEntryTest.SEVERITY, LogEntryTest.JSON_ENTRY.getSeverity());
        Assert.assertEquals(LogEntryTest.INSERT_ID, LogEntryTest.JSON_ENTRY.getInsertId());
        Assert.assertEquals(LogEntryTest.HTTP_REQUEST, LogEntryTest.JSON_ENTRY.getHttpRequest());
        Assert.assertEquals(LogEntryTest.LABELS, LogEntryTest.JSON_ENTRY.getLabels());
        Assert.assertEquals(LogEntryTest.OPERATION, LogEntryTest.JSON_ENTRY.getOperation());
        Assert.assertEquals(LogEntryTest.TRACE, LogEntryTest.JSON_ENTRY.getTrace());
        Assert.assertEquals(LogEntryTest.SPAN_ID, LogEntryTest.JSON_ENTRY.getSpanId());
        Assert.assertEquals(LogEntryTest.TRACE_SAMPLED, LogEntryTest.JSON_ENTRY.getTraceSampled());
        Assert.assertEquals(LogEntryTest.SOURCE_LOCATION, LogEntryTest.JSON_ENTRY.getSourceLocation());
        Assert.assertEquals(LogEntryTest.JSON_PAYLOAD, LogEntryTest.JSON_ENTRY.getPayload());
        Assert.assertEquals(LogEntryTest.LOG_NAME, LogEntryTest.PROTO_ENTRY.getLogName());
        Assert.assertEquals(LogEntryTest.RESOURCE, LogEntryTest.PROTO_ENTRY.getResource());
        Assert.assertEquals(LogEntryTest.TIMESTAMP, ((long) (LogEntryTest.PROTO_ENTRY.getTimestamp())));
        Assert.assertEquals(LogEntryTest.RECEIVE_TIMESTAMP, ((long) (LogEntryTest.PROTO_ENTRY.getReceiveTimestamp())));
        Assert.assertEquals(LogEntryTest.SEVERITY, LogEntryTest.PROTO_ENTRY.getSeverity());
        Assert.assertEquals(LogEntryTest.INSERT_ID, LogEntryTest.PROTO_ENTRY.getInsertId());
        Assert.assertEquals(LogEntryTest.HTTP_REQUEST, LogEntryTest.PROTO_ENTRY.getHttpRequest());
        Assert.assertEquals(LogEntryTest.LABELS, LogEntryTest.PROTO_ENTRY.getLabels());
        Assert.assertEquals(LogEntryTest.OPERATION, LogEntryTest.PROTO_ENTRY.getOperation());
        Assert.assertEquals(LogEntryTest.TRACE, LogEntryTest.PROTO_ENTRY.getTrace());
        Assert.assertEquals(LogEntryTest.SPAN_ID, LogEntryTest.PROTO_ENTRY.getSpanId());
        Assert.assertEquals(LogEntryTest.TRACE_SAMPLED, LogEntryTest.PROTO_ENTRY.getTraceSampled());
        Assert.assertEquals(LogEntryTest.SOURCE_LOCATION, LogEntryTest.PROTO_ENTRY.getSourceLocation());
        Assert.assertEquals(LogEntryTest.PROTO_PAYLOAD, LogEntryTest.PROTO_ENTRY.getPayload());
        LogEntry logEntry = LogEntry.newBuilder(LogEntryTest.STRING_PAYLOAD).setPayload(StringPayload.of("otherPayload")).setLogName(LogEntryTest.LOG_NAME).setResource(LogEntryTest.RESOURCE).setTimestamp(LogEntryTest.TIMESTAMP).setReceiveTimestamp(LogEntryTest.RECEIVE_TIMESTAMP).setSeverity(LogEntryTest.SEVERITY).setInsertId(LogEntryTest.INSERT_ID).setHttpRequest(LogEntryTest.HTTP_REQUEST).addLabel("key1", "value1").addLabel("key2", "value2").setOperation(LogEntryTest.OPERATION).setTrace(LogEntryTest.TRACE).setSpanId(LogEntryTest.SPAN_ID).setTraceSampled(LogEntryTest.TRACE_SAMPLED).setSourceLocation(LogEntryTest.SOURCE_LOCATION).build();
        Assert.assertEquals(LogEntryTest.LOG_NAME, logEntry.getLogName());
        Assert.assertEquals(LogEntryTest.RESOURCE, logEntry.getResource());
        Assert.assertEquals(LogEntryTest.TIMESTAMP, ((long) (logEntry.getTimestamp())));
        Assert.assertEquals(LogEntryTest.RECEIVE_TIMESTAMP, ((long) (logEntry.getReceiveTimestamp())));
        Assert.assertEquals(LogEntryTest.SEVERITY, logEntry.getSeverity());
        Assert.assertEquals(LogEntryTest.INSERT_ID, logEntry.getInsertId());
        Assert.assertEquals(LogEntryTest.HTTP_REQUEST, logEntry.getHttpRequest());
        Assert.assertEquals(LogEntryTest.LABELS, logEntry.getLabels());
        Assert.assertEquals(LogEntryTest.OPERATION, logEntry.getOperation());
        Assert.assertEquals(LogEntryTest.TRACE, logEntry.getTrace());
        Assert.assertEquals(LogEntryTest.SPAN_ID, logEntry.getSpanId());
        Assert.assertEquals(LogEntryTest.TRACE_SAMPLED, logEntry.getTraceSampled());
        Assert.assertEquals(LogEntryTest.SOURCE_LOCATION, logEntry.getSourceLocation());
        Assert.assertEquals(StringPayload.of("otherPayload"), logEntry.getPayload());
    }

    @Test
    public void testToBuilder() {
        compareLogEntry(LogEntryTest.STRING_ENTRY, LogEntryTest.STRING_ENTRY.toBuilder().build());
        HttpRequest request = HttpRequest.newBuilder().setRequestMethod(POST).setStatus(500).build();
        LogEntry logEntry = LogEntryTest.STRING_ENTRY.toBuilder().setPayload(StringPayload.of("otherPayload")).setLogName("otherLogName").setResource(MonitoredResource.newBuilder("global").build()).setTimestamp(43).setReceiveTimestamp(34).setSeverity(DEBUG).setInsertId("otherInsertId").setHttpRequest(request).clearLabels().addLabel("key", "value").setOperation(Operation.of("otherId", "otherProducer")).setTrace("otherTrace").setSpanId("otherSpanId").setTraceSampled(false).setSourceLocation(new SourceLocation.Builder().setFile("hey.java").build()).build();
        Assert.assertEquals("otherLogName", logEntry.getLogName());
        Assert.assertEquals(MonitoredResource.newBuilder("global").build(), logEntry.getResource());
        Assert.assertEquals(43, ((long) (logEntry.getTimestamp())));
        Assert.assertEquals(34, ((long) (logEntry.getReceiveTimestamp())));
        Assert.assertEquals(DEBUG, logEntry.getSeverity());
        Assert.assertEquals("otherInsertId", logEntry.getInsertId());
        Assert.assertEquals(request, logEntry.getHttpRequest());
        Assert.assertEquals(ImmutableMap.of("key", "value"), logEntry.getLabels());
        Assert.assertEquals(Operation.of("otherId", "otherProducer"), logEntry.getOperation());
        Assert.assertEquals("otherTrace", logEntry.getTrace());
        Assert.assertEquals("otherSpanId", logEntry.getSpanId());
        Assert.assertFalse(logEntry.getTraceSampled());
        Assert.assertEquals(new SourceLocation.Builder().setFile("hey.java").build(), logEntry.getSourceLocation());
        Assert.assertEquals(StringPayload.of("otherPayload"), logEntry.getPayload());
        logEntry = logEntry.toBuilder().setPayload(LogEntryTest.STRING_PAYLOAD).setLogName(LogEntryTest.LOG_NAME).setResource(LogEntryTest.RESOURCE).setTimestamp(LogEntryTest.TIMESTAMP).setReceiveTimestamp(LogEntryTest.RECEIVE_TIMESTAMP).setSeverity(LogEntryTest.SEVERITY).setInsertId(LogEntryTest.INSERT_ID).setHttpRequest(LogEntryTest.HTTP_REQUEST).setLabels(LogEntryTest.LABELS).setOperation(LogEntryTest.OPERATION).setTrace(LogEntryTest.TRACE).setSpanId(LogEntryTest.SPAN_ID).setTraceSampled(LogEntryTest.TRACE_SAMPLED).setSourceLocation(LogEntryTest.SOURCE_LOCATION).build();
        compareLogEntry(LogEntryTest.STRING_ENTRY, logEntry);
    }

    @Test
    public void testToAndFromPb() {
        compareLogEntry(LogEntryTest.STRING_ENTRY, LogEntry.fromPb(LogEntryTest.STRING_ENTRY.toPb("project")));
        compareLogEntry(LogEntryTest.JSON_ENTRY, LogEntry.fromPb(LogEntryTest.JSON_ENTRY.toPb("project")));
        compareLogEntry(LogEntryTest.PROTO_ENTRY, LogEntry.fromPb(LogEntryTest.PROTO_ENTRY.toPb("project")));
        LogEntry logEntry = LogEntry.of(LogEntryTest.STRING_PAYLOAD);
        compareLogEntry(logEntry, LogEntry.fromPb(logEntry.toPb("project")));
        logEntry = LogEntry.of(LogEntryTest.LOG_NAME, LogEntryTest.RESOURCE, LogEntryTest.STRING_PAYLOAD);
        compareLogEntry(logEntry, LogEntry.fromPb(logEntry.toPb("project")));
    }
}

