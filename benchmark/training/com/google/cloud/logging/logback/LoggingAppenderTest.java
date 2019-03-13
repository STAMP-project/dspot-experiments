/**
 * Copyright 2017 Google LLC
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
package com.google.cloud.logging.logback;


import Level.INFO;
import Level.WARN;
import Severity.ERROR;
import Severity.WARNING;
import ch.qos.logback.classic.filter.ThresholdFilter;
import ch.qos.logback.classic.spi.LoggingEvent;
import com.google.cloud.MonitoredResource;
import com.google.cloud.Timestamp;
import com.google.cloud.logging.LogEntry;
import com.google.cloud.logging.Logging;
import com.google.cloud.logging.Logging.WriteOption;
import com.google.cloud.logging.Payload.StringPayload;
import com.google.common.collect.ImmutableMap;
import org.easymock.Capture;
import org.easymock.EasyMock;
import org.easymock.EasyMockRunner;
import org.junit.Test;
import org.junit.runner.RunWith;


@RunWith(EasyMockRunner.class)
public class LoggingAppenderTest {
    private final String projectId = "test-project";

    private final Logging logging = EasyMock.createStrictMock(Logging.class);

    private LoggingAppender loggingAppender = new LoggingAppenderTest.TestLoggingAppender();

    class TestLoggingAppender extends LoggingAppender {
        @Override
        String getProjectId() {
            return projectId;
        }

        @Override
        Logging getLogging() {
            return logging;
        }
    }

    private final WriteOption[] defaultWriteOptions = new WriteOption[]{ WriteOption.logName("java.log"), WriteOption.resource(MonitoredResource.newBuilder("global").setLabels(new ImmutableMap.Builder<String, String>().put("project_id", projectId).build()).build()) };

    @Test
    public void testFlushLevelConfigUpdatesLoggingFlushSeverity() {
        LogEntry logEntry = LogEntry.newBuilder(StringPayload.of("this is a test")).setTimestamp(100000L).setSeverity(WARNING).setLabels(new ImmutableMap.Builder<String, String>().put("levelName", "WARN").put("levelValue", String.valueOf(30000L)).build()).build();
        logging.setFlushSeverity(WARNING);
        Capture<Iterable<LogEntry>> capturedArgument = Capture.newInstance();
        logging.write(capture(capturedArgument), ((WriteOption) (anyObject())), ((WriteOption) (anyObject())));
        replay(logging);
        Timestamp timestamp = Timestamp.ofTimeSecondsAndNanos(100000, 0);
        LoggingEvent loggingEvent = createLoggingEvent(WARN, timestamp.getSeconds());
        // error is the default, updating to warn for test
        loggingAppender.setFlushLevel(WARN);
        loggingAppender.start();
        loggingAppender.doAppend(loggingEvent);
        verify(logging);
        assertThat(capturedArgument.getValue().iterator().hasNext()).isTrue();
        assertThat(capturedArgument.getValue().iterator().next()).isEqualTo(logEntry);
    }

    @Test
    public void testFilterLogsOnlyLogsAtOrAboveLogLevel() {
        LogEntry logEntry = LogEntry.newBuilder(StringPayload.of("this is a test")).setTimestamp(100000L).setSeverity(ERROR).setLabels(new ImmutableMap.Builder<String, String>().put("levelName", "ERROR").put("levelValue", String.valueOf(40000L)).build()).build();
        logging.setFlushSeverity(ERROR);
        Capture<Iterable<LogEntry>> capturedArgument = Capture.newInstance();
        logging.write(capture(capturedArgument), ((WriteOption) (anyObject())), ((WriteOption) (anyObject())));
        expectLastCall().once();
        replay(logging);
        Timestamp timestamp = Timestamp.ofTimeSecondsAndNanos(100000, 0);
        LoggingEvent loggingEvent1 = createLoggingEvent(INFO, timestamp.getSeconds());
        ThresholdFilter thresholdFilter = new ThresholdFilter();
        thresholdFilter.setLevel("ERROR");
        thresholdFilter.start();
        loggingAppender.addFilter(thresholdFilter);
        loggingAppender.start();
        // info event does not get logged
        loggingAppender.doAppend(loggingEvent1);
        LoggingEvent loggingEvent2 = createLoggingEvent(Level.ERROR, timestamp.getSeconds());
        // error event gets logged
        loggingAppender.doAppend(loggingEvent2);
        verify(logging);
        assertThat(capturedArgument.getValue().iterator().hasNext()).isTrue();
        assertThat(capturedArgument.getValue().iterator().next()).isEqualTo(logEntry);
    }

    @Test
    public void testEnhancersAddCorrectLabelsToLogEntries() {
        LogEntry logEntry = LogEntry.newBuilder(StringPayload.of("this is a test")).setTimestamp(100000L).setSeverity(WARNING).setLabels(new ImmutableMap.Builder<String, String>().put("levelName", "WARN").put("levelValue", String.valueOf(30000L)).put("test-label-1", "test-value-1").put("test-label-2", "test-value-2").build()).build();
        logging.setFlushSeverity(ERROR);
        Capture<Iterable<LogEntry>> capturedArgument = Capture.newInstance();
        logging.write(capture(capturedArgument), ((WriteOption) (anyObject())), ((WriteOption) (anyObject())));
        expectLastCall().once();
        replay(logging);
        loggingAppender.addEnhancer("com.example.enhancers.TestLoggingEnhancer");
        loggingAppender.addEnhancer("com.example.enhancers.AnotherTestLoggingEnhancer");
        loggingAppender.start();
        Timestamp timestamp = Timestamp.ofTimeSecondsAndNanos(100000, 0);
        LoggingEvent loggingEvent = createLoggingEvent(WARN, timestamp.getSeconds());
        loggingAppender.doAppend(loggingEvent);
        verify(logging);
        assertThat(capturedArgument.getValue().iterator().hasNext()).isTrue();
        assertThat(capturedArgument.getValue().iterator().next()).isEqualTo(logEntry);
    }

    @Test
    public void testDefaultWriteOptionsHasExpectedDefaults() {
        logging.setFlushSeverity(ERROR);
        Capture<WriteOption> logNameArg = Capture.newInstance();
        Capture<WriteOption> resourceArg = Capture.newInstance();
        logging.write(((Iterable<LogEntry>) (anyObject())), capture(logNameArg), capture(resourceArg));
        expectLastCall().once();
        replay(logging);
        loggingAppender.start();
        Timestamp timestamp = Timestamp.ofTimeSecondsAndNanos(100000, 0);
        LoggingEvent loggingEvent = createLoggingEvent(Level.ERROR, timestamp.getSeconds());
        loggingAppender.doAppend(loggingEvent);
        assertThat(logNameArg.getValue()).isEqualTo(defaultWriteOptions[0]);
        // TODO(chingor): Fix this test to work on GCE and locally
        // assertThat(resourceArg.getValue()).isEqualTo(defaultWriteOptions[1]);
    }
}

