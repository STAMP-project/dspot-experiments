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


import Severity.ALERT;
import Severity.CRITICAL;
import Severity.DEBUG;
import Severity.EMERGENCY;
import Severity.ERROR;
import Severity.INFO;
import Severity.NOTICE;
import Severity.WARNING;
import Synchronicity.ASYNC;
import Synchronicity.SYNC;
import com.google.cloud.MonitoredResource;
import com.google.cloud.logging.LogEntry.Builder;
import com.google.cloud.logging.Logging.WriteOption;
import com.google.cloud.logging.Payload.StringPayload;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import java.util.Collections;
import java.util.logging.ErrorManager;
import java.util.logging.Filter;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import org.easymock.EasyMock;
import org.junit.Test;


public class LoggingHandlerTest {
    private static final String LOG_NAME = "java.log";

    private static final String MESSAGE = "message";

    private static final String PROJECT = "project";

    private static final MonitoredResource DEFAULT_RESOURCE = MonitoredResource.of("global", ImmutableMap.of("project_id", LoggingHandlerTest.PROJECT));

    private static final LogEntry FINEST_ENTRY = LogEntry.newBuilder(StringPayload.of(LoggingHandlerTest.MESSAGE)).setSeverity(DEBUG).addLabel("levelName", "FINEST").addLabel("levelValue", String.valueOf(Level.FINEST.intValue())).setTimestamp(123456789L).build();

    private static final LogEntry FINEST_ENHANCED_ENTRY = LogEntry.newBuilder(StringPayload.of(LoggingHandlerTest.MESSAGE)).setSeverity(DEBUG).addLabel("levelName", "FINEST").addLabel("levelValue", String.valueOf(Level.FINEST.intValue())).addLabel("enhanced", "true").setTimestamp(123456789L).build();

    private static final LogEntry FINER_ENTRY = LogEntry.newBuilder(StringPayload.of(LoggingHandlerTest.MESSAGE)).setSeverity(DEBUG).addLabel("levelName", "FINER").addLabel("levelValue", String.valueOf(Level.FINER.intValue())).setTimestamp(123456789L).build();

    private static final LogEntry FINE_ENTRY = LogEntry.newBuilder(StringPayload.of(LoggingHandlerTest.MESSAGE)).setSeverity(DEBUG).addLabel("levelName", "FINE").addLabel("levelValue", String.valueOf(Level.FINE.intValue())).setTimestamp(123456789L).build();

    private static final LogEntry CONFIG_ENTRY = LogEntry.newBuilder(StringPayload.of(LoggingHandlerTest.MESSAGE)).setSeverity(INFO).addLabel("levelName", "CONFIG").addLabel("levelValue", String.valueOf(Level.CONFIG.intValue())).setTimestamp(123456789L).build();

    private static final LogEntry INFO_ENTRY = LogEntry.newBuilder(StringPayload.of(LoggingHandlerTest.MESSAGE)).setSeverity(INFO).setTimestamp(123456789L).build();

    private static final LogEntry WARNING_ENTRY = LogEntry.newBuilder(StringPayload.of(LoggingHandlerTest.MESSAGE)).setSeverity(WARNING).addLabel("levelName", "WARNING").addLabel("levelValue", String.valueOf(Level.WARNING.intValue())).setTimestamp(123456789L).build();

    private static final LogEntry SEVERE_ENTRY = LogEntry.newBuilder(StringPayload.of(LoggingHandlerTest.MESSAGE)).setSeverity(ERROR).addLabel("levelName", "SEVERE").addLabel("levelValue", String.valueOf(Level.SEVERE.intValue())).setTimestamp(123456789L).build();

    private static final LogEntry DEBUG_ENTRY = LogEntry.newBuilder(StringPayload.of(LoggingHandlerTest.MESSAGE)).setSeverity(DEBUG).addLabel("levelName", "DEBUG").addLabel("levelValue", String.valueOf(LoggingLevel.DEBUG.intValue())).setTimestamp(123456789L).build();

    private static final LogEntry NOTICE_ENTRY = LogEntry.newBuilder(StringPayload.of(LoggingHandlerTest.MESSAGE)).setSeverity(NOTICE).addLabel("levelName", "NOTICE").addLabel("levelValue", String.valueOf(LoggingLevel.NOTICE.intValue())).setTimestamp(123456789L).build();

    private static final LogEntry ERROR_ENTRY = LogEntry.newBuilder(StringPayload.of(LoggingHandlerTest.MESSAGE)).setSeverity(ERROR).addLabel("levelName", "ERROR").addLabel("levelValue", String.valueOf(LoggingLevel.ERROR.intValue())).setTimestamp(123456789L).build();

    private static final LogEntry CRITICAL_ENTRY = LogEntry.newBuilder(StringPayload.of(LoggingHandlerTest.MESSAGE)).setSeverity(CRITICAL).addLabel("levelName", "CRITICAL").addLabel("levelValue", String.valueOf(LoggingLevel.CRITICAL.intValue())).setTimestamp(123456789L).build();

    private static final LogEntry ALERT_ENTRY = LogEntry.newBuilder(StringPayload.of(LoggingHandlerTest.MESSAGE)).setSeverity(ALERT).addLabel("levelName", "ALERT").addLabel("levelValue", String.valueOf(LoggingLevel.ALERT.intValue())).setTimestamp(123456789L).build();

    private static final LogEntry EMERGENCY_ENTRY = LogEntry.newBuilder(StringPayload.of(LoggingHandlerTest.MESSAGE)).setSeverity(EMERGENCY).addLabel("levelName", "EMERGENCY").addLabel("levelValue", String.valueOf(LoggingLevel.EMERGENCY.intValue())).setTimestamp(123456789L).build();

    private static final LogEntry TRACE_ENTRY = LogEntry.newBuilder(StringPayload.of(LoggingHandlerTest.MESSAGE)).setSeverity(DEBUG).addLabel("levelName", "FINEST").addLabel("levelValue", String.valueOf(Level.FINEST.intValue())).setTrace("projects/projectId/traces/traceId").setTimestamp(123456789L).build();

    private static final String CONFIG_NAMESPACE = "com.google.cloud.logging.LoggingHandler";

    private static final ImmutableMap<String, String> CONFIG_MAP = ImmutableMap.<String, String>builder().put("log", "testLogName").put("level", "ALL").put("filter", "com.google.cloud.logging.LoggingHandlerTest$TestFilter").put("formatter", "com.google.cloud.logging.LoggingHandlerTest$TestFormatter").put("flushLevel", "CRITICAL").put("enhancers", "com.google.cloud.logging.LoggingHandlerTest$TestLoggingEnhancer").put("resourceType", "testResourceType").put("synchronicity", "SYNC").build();

    private static final ImmutableMap<String, String> BASE_SEVERITY_MAP = ImmutableMap.of("levelName", Level.INFO.getName(), "levelValue", String.valueOf(Level.INFO.intValue()));

    private static final WriteOption[] DEFAULT_OPTIONS = new WriteOption[]{ WriteOption.logName(LoggingHandlerTest.LOG_NAME), WriteOption.resource(LoggingHandlerTest.DEFAULT_RESOURCE), WriteOption.labels(LoggingHandlerTest.BASE_SEVERITY_MAP) };

    private Logging logging;

    private LoggingOptions options;

    static final class TestFormatter extends Formatter {
        @Override
        public String format(LogRecord record) {
            return record.getMessage();
        }
    }

    static final class TestFilter implements Filter {
        @Override
        public boolean isLoggable(LogRecord record) {
            return true;
        }
    }

    static final class TestLoggingEnhancer implements LoggingEnhancer {
        @Override
        public void enhanceLogEntry(LogEntry.Builder builder) {
            builder.addLabel("enhanced", "true");
        }
    }

    @Test
    public void testPublishLevels() {
        expect(options.getProjectId()).andReturn(LoggingHandlerTest.PROJECT).anyTimes();
        expect(options.getService()).andReturn(logging);
        logging.setFlushSeverity(ERROR);
        expectLastCall().once();
        logging.setWriteSynchronicity(ASYNC);
        expectLastCall().once();
        logging.write(ImmutableList.of(LoggingHandlerTest.FINEST_ENTRY), LoggingHandlerTest.DEFAULT_OPTIONS);
        expectLastCall().once();
        logging.write(ImmutableList.of(LoggingHandlerTest.FINER_ENTRY), LoggingHandlerTest.DEFAULT_OPTIONS);
        expectLastCall().once();
        logging.write(ImmutableList.of(LoggingHandlerTest.FINE_ENTRY), LoggingHandlerTest.DEFAULT_OPTIONS);
        expectLastCall().once();
        logging.write(ImmutableList.of(LoggingHandlerTest.CONFIG_ENTRY), LoggingHandlerTest.DEFAULT_OPTIONS);
        expectLastCall().once();
        logging.write(ImmutableList.of(LoggingHandlerTest.INFO_ENTRY), LoggingHandlerTest.DEFAULT_OPTIONS);
        expectLastCall().once();
        logging.write(ImmutableList.of(LoggingHandlerTest.WARNING_ENTRY), LoggingHandlerTest.DEFAULT_OPTIONS);
        expectLastCall().once();
        logging.write(ImmutableList.of(LoggingHandlerTest.SEVERE_ENTRY), LoggingHandlerTest.DEFAULT_OPTIONS);
        expectLastCall().once();
        logging.write(ImmutableList.of(LoggingHandlerTest.DEBUG_ENTRY), LoggingHandlerTest.DEFAULT_OPTIONS);
        expectLastCall().once();
        logging.write(ImmutableList.of(LoggingHandlerTest.NOTICE_ENTRY), LoggingHandlerTest.DEFAULT_OPTIONS);
        expectLastCall().once();
        logging.write(ImmutableList.of(LoggingHandlerTest.ERROR_ENTRY), LoggingHandlerTest.DEFAULT_OPTIONS);
        expectLastCall().once();
        logging.write(ImmutableList.of(LoggingHandlerTest.CRITICAL_ENTRY), LoggingHandlerTest.DEFAULT_OPTIONS);
        expectLastCall().once();
        logging.write(ImmutableList.of(LoggingHandlerTest.ALERT_ENTRY), LoggingHandlerTest.DEFAULT_OPTIONS);
        expectLastCall().once();
        logging.write(ImmutableList.of(LoggingHandlerTest.EMERGENCY_ENTRY), LoggingHandlerTest.DEFAULT_OPTIONS);
        expectLastCall().once();
        replay(options, logging);
        Handler handler = new LoggingHandler(LoggingHandlerTest.LOG_NAME, options, LoggingHandlerTest.DEFAULT_RESOURCE);
        handler.setLevel(Level.ALL);
        handler.setFormatter(new LoggingHandlerTest.TestFormatter());
        // default levels
        handler.publish(LoggingHandlerTest.newLogRecord(Level.FINEST, LoggingHandlerTest.MESSAGE));
        handler.publish(LoggingHandlerTest.newLogRecord(Level.FINER, LoggingHandlerTest.MESSAGE));
        handler.publish(LoggingHandlerTest.newLogRecord(Level.FINE, LoggingHandlerTest.MESSAGE));
        handler.publish(LoggingHandlerTest.newLogRecord(Level.CONFIG, LoggingHandlerTest.MESSAGE));
        handler.publish(LoggingHandlerTest.newLogRecord(Level.INFO, LoggingHandlerTest.MESSAGE));
        handler.publish(LoggingHandlerTest.newLogRecord(Level.WARNING, LoggingHandlerTest.MESSAGE));
        handler.publish(LoggingHandlerTest.newLogRecord(Level.SEVERE, LoggingHandlerTest.MESSAGE));
        // Logging levels
        handler.publish(LoggingHandlerTest.newLogRecord(LoggingLevel.DEBUG, LoggingHandlerTest.MESSAGE));
        handler.publish(LoggingHandlerTest.newLogRecord(LoggingLevel.NOTICE, LoggingHandlerTest.MESSAGE));
        handler.publish(LoggingHandlerTest.newLogRecord(LoggingLevel.ERROR, LoggingHandlerTest.MESSAGE));
        handler.publish(LoggingHandlerTest.newLogRecord(LoggingLevel.CRITICAL, LoggingHandlerTest.MESSAGE));
        handler.publish(LoggingHandlerTest.newLogRecord(LoggingLevel.ALERT, LoggingHandlerTest.MESSAGE));
        handler.publish(LoggingHandlerTest.newLogRecord(LoggingLevel.EMERGENCY, LoggingHandlerTest.MESSAGE));
    }

    @Test
    public void testPublishCustomResource() {
        expect(options.getProjectId()).andReturn(LoggingHandlerTest.PROJECT).anyTimes();
        expect(options.getService()).andReturn(logging);
        logging.setFlushSeverity(ERROR);
        expectLastCall().once();
        logging.setWriteSynchronicity(ASYNC);
        expectLastCall().once();
        MonitoredResource resource = MonitoredResource.of("custom", ImmutableMap.<String, String>of());
        logging.write(ImmutableList.of(LoggingHandlerTest.FINEST_ENTRY), WriteOption.logName(LoggingHandlerTest.LOG_NAME), WriteOption.resource(resource), WriteOption.labels(LoggingHandlerTest.BASE_SEVERITY_MAP));
        expectLastCall().once();
        replay(options, logging);
        Handler handler = new LoggingHandler(LoggingHandlerTest.LOG_NAME, options, resource);
        handler.setLevel(Level.ALL);
        handler.setFormatter(new LoggingHandlerTest.TestFormatter());
        handler.publish(LoggingHandlerTest.newLogRecord(Level.FINEST, LoggingHandlerTest.MESSAGE));
    }

    @Test
    public void testEnhancedLogEntry() {
        expect(options.getProjectId()).andReturn(LoggingHandlerTest.PROJECT).anyTimes();
        expect(options.getService()).andReturn(logging);
        MonitoredResource resource = MonitoredResource.of("custom", ImmutableMap.<String, String>of());
        logging.setFlushSeverity(ERROR);
        expectLastCall().once();
        logging.setWriteSynchronicity(ASYNC);
        expectLastCall().once();
        logging.write(ImmutableList.of(LoggingHandlerTest.FINEST_ENHANCED_ENTRY), WriteOption.logName(LoggingHandlerTest.LOG_NAME), WriteOption.resource(resource), WriteOption.labels(LoggingHandlerTest.BASE_SEVERITY_MAP));
        expectLastCall().once();
        replay(options, logging);
        LoggingEnhancer enhancer = new LoggingEnhancer() {
            @Override
            public void enhanceLogEntry(Builder builder) {
                builder.addLabel("enhanced", "true");
            }
        };
        Handler handler = new LoggingHandler(LoggingHandlerTest.LOG_NAME, options, resource, Collections.singletonList(enhancer));
        handler.setLevel(Level.ALL);
        handler.setFormatter(new LoggingHandlerTest.TestFormatter());
        handler.publish(LoggingHandlerTest.newLogRecord(Level.FINEST, LoggingHandlerTest.MESSAGE));
    }

    @Test
    public void testTraceEnhancedLogEntry() {
        expect(options.getProjectId()).andReturn(LoggingHandlerTest.PROJECT).anyTimes();
        expect(options.getService()).andReturn(logging);
        MonitoredResource resource = MonitoredResource.of("custom", ImmutableMap.<String, String>of());
        logging.setFlushSeverity(ERROR);
        expectLastCall().once();
        logging.setWriteSynchronicity(ASYNC);
        expectLastCall().once();
        logging.write(ImmutableList.of(LoggingHandlerTest.TRACE_ENTRY), WriteOption.logName(LoggingHandlerTest.LOG_NAME), WriteOption.resource(resource), WriteOption.labels(LoggingHandlerTest.BASE_SEVERITY_MAP));
        expectLastCall().once();
        replay(options, logging);
        LoggingEnhancer enhancer = new TraceLoggingEnhancer();
        TraceLoggingEnhancer.setCurrentTraceId("projects/projectId/traces/traceId");
        Handler handler = new LoggingHandler(LoggingHandlerTest.LOG_NAME, options, resource, Collections.singletonList(enhancer));
        handler.setLevel(Level.ALL);
        handler.setFormatter(new LoggingHandlerTest.TestFormatter());
        handler.publish(LoggingHandlerTest.newLogRecord(Level.FINEST, LoggingHandlerTest.MESSAGE));
    }

    @Test
    public void testReportWriteError() {
        expect(options.getProjectId()).andReturn(LoggingHandlerTest.PROJECT).anyTimes();
        expect(options.getService()).andReturn(logging);
        RuntimeException ex = new RuntimeException();
        logging.setFlushSeverity(ERROR);
        expectLastCall().once();
        logging.setWriteSynchronicity(ASYNC);
        expectLastCall().once();
        logging.write(ImmutableList.of(LoggingHandlerTest.FINEST_ENTRY), LoggingHandlerTest.DEFAULT_OPTIONS);
        expectLastCall().andStubThrow(ex);
        replay(options, logging);
        ErrorManager errorManager = EasyMock.createStrictMock(ErrorManager.class);
        errorManager.error(null, ex, ErrorManager.WRITE_FAILURE);
        expectLastCall().once();
        replay(errorManager);
        Handler handler = new LoggingHandler(LoggingHandlerTest.LOG_NAME, options, LoggingHandlerTest.DEFAULT_RESOURCE);
        handler.setLevel(Level.ALL);
        handler.setErrorManager(errorManager);
        handler.setFormatter(new LoggingHandlerTest.TestFormatter());
        handler.publish(LoggingHandlerTest.newLogRecord(Level.FINEST, LoggingHandlerTest.MESSAGE));
        verify(errorManager);
    }

    @Test
    public void testReportFlushError() {
        expect(options.getProjectId()).andReturn(LoggingHandlerTest.PROJECT).anyTimes();
        expect(options.getService()).andReturn(logging);
        RuntimeException ex = new RuntimeException();
        logging.setFlushSeverity(ERROR);
        expectLastCall().once();
        logging.setWriteSynchronicity(ASYNC);
        expectLastCall().once();
        logging.write(ImmutableList.of(LoggingHandlerTest.FINEST_ENTRY), LoggingHandlerTest.DEFAULT_OPTIONS);
        expectLastCall().once();
        logging.flush();
        expectLastCall().andThrow(ex);
        ErrorManager errorManager = EasyMock.createStrictMock(ErrorManager.class);
        errorManager.error(null, ex, ErrorManager.FLUSH_FAILURE);
        expectLastCall().once();
        replay(options, logging, errorManager);
        LogRecord record = LoggingHandlerTest.newLogRecord(Level.FINEST, LoggingHandlerTest.MESSAGE);
        Handler handler = new LoggingHandler(LoggingHandlerTest.LOG_NAME, options, LoggingHandlerTest.DEFAULT_RESOURCE);
        handler.setLevel(Level.ALL);
        handler.setErrorManager(errorManager);
        handler.setFormatter(new LoggingHandlerTest.TestFormatter());
        handler.publish(record);
        handler.flush();
        verify(errorManager);
    }

    @Test
    public void testReportFormatError() {
        expect(options.getProjectId()).andReturn(LoggingHandlerTest.PROJECT).anyTimes();
        expect(options.getService()).andReturn(logging);
        logging.setFlushSeverity(ERROR);
        expectLastCall().once();
        logging.setWriteSynchronicity(ASYNC);
        expectLastCall().once();
        replay(options, logging);
        Formatter formatter = EasyMock.createStrictMock(Formatter.class);
        RuntimeException ex = new RuntimeException();
        ErrorManager errorManager = EasyMock.createStrictMock(ErrorManager.class);
        errorManager.error(null, ex, ErrorManager.FORMAT_FAILURE);
        expectLastCall().once();
        LogRecord record = LoggingHandlerTest.newLogRecord(Level.FINEST, LoggingHandlerTest.MESSAGE);
        expect(formatter.format(record)).andThrow(ex);
        replay(errorManager, formatter);
        Handler handler = new LoggingHandler(LoggingHandlerTest.LOG_NAME, options, LoggingHandlerTest.DEFAULT_RESOURCE);
        handler.setLevel(Level.ALL);
        handler.setErrorManager(errorManager);
        handler.setFormatter(formatter);
        handler.publish(record);
        verify(errorManager, formatter);
    }

    @Test
    public void testSyncWrite() {
        expect(options.getProjectId()).andReturn(LoggingHandlerTest.PROJECT).anyTimes();
        expect(options.getService()).andReturn(logging);
        LogEntry entry = LogEntry.newBuilder(Payload.StringPayload.of(LoggingHandlerTest.MESSAGE)).setSeverity(DEBUG).addLabel("levelName", "FINEST").addLabel("levelValue", String.valueOf(Level.FINEST.intValue())).setTimestamp(123456789L).build();
        logging.setFlushSeverity(ERROR);
        expectLastCall().once();
        logging.setWriteSynchronicity(ASYNC);
        expectLastCall().once();
        logging.setWriteSynchronicity(SYNC);
        expectLastCall().once();
        logging.write(ImmutableList.of(entry), LoggingHandlerTest.DEFAULT_OPTIONS);
        expectLastCall().once();
        replay(options, logging);
        LoggingHandler handler = new LoggingHandler(LoggingHandlerTest.LOG_NAME, options, LoggingHandlerTest.DEFAULT_RESOURCE);
        handler.setLevel(Level.ALL);
        handler.setSynchronicity(SYNC);
        handler.setFormatter(new LoggingHandlerTest.TestFormatter());
        LogRecord record = new LogRecord(Level.FINEST, LoggingHandlerTest.MESSAGE);
        record.setMillis(123456789L);
        handler.publish(record);
    }

    @Test
    public void testAddHandler() {
        expect(options.getProjectId()).andReturn(LoggingHandlerTest.PROJECT).anyTimes();
        expect(options.getService()).andReturn(logging);
        logging.setFlushSeverity(ERROR);
        expectLastCall().once();
        logging.setWriteSynchronicity(ASYNC);
        expectLastCall().andVoid();
        logging.write(ImmutableList.of(LoggingHandlerTest.FINEST_ENTRY), LoggingHandlerTest.DEFAULT_OPTIONS);
        expectLastCall().once();
        replay(options, logging);
        LoggingHandler handler = new LoggingHandler(LoggingHandlerTest.LOG_NAME, options, LoggingHandlerTest.DEFAULT_RESOURCE) {
            @Override
            public void close() {
                // Make close NOOP to avoid mock close exception
            }
        };
        handler.setLevel(Level.ALL);
        handler.setFormatter(new LoggingHandlerTest.TestFormatter());
        Logger logger = Logger.getLogger(getClass().getName());
        logger.setLevel(Level.ALL);
        LoggingHandler.addHandler(logger, handler);
        logger.log(LoggingHandlerTest.newLogRecord(Level.FINEST, LoggingHandlerTest.MESSAGE));
    }

    @Test
    public void testClose() throws Exception {
        expect(options.getProjectId()).andReturn(LoggingHandlerTest.PROJECT).anyTimes();
        expect(options.getService()).andReturn(logging);
        logging.setFlushSeverity(ERROR);
        expectLastCall().once();
        logging.setWriteSynchronicity(ASYNC);
        expectLastCall().once();
        logging.write(ImmutableList.of(LoggingHandlerTest.FINEST_ENTRY), LoggingHandlerTest.DEFAULT_OPTIONS);
        expectLastCall().once();
        logging.close();
        expectLastCall().once();
        replay(options, logging);
        Handler handler = new LoggingHandler(LoggingHandlerTest.LOG_NAME, options, LoggingHandlerTest.DEFAULT_RESOURCE);
        handler.setLevel(Level.ALL);
        handler.setFormatter(new LoggingHandlerTest.TestFormatter());
        handler.publish(LoggingHandlerTest.newLogRecord(Level.FINEST, LoggingHandlerTest.MESSAGE));
        handler.close();
        handler.close();
    }
}

