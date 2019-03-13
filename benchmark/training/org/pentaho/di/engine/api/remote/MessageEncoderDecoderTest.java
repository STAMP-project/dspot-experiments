/**
 * ! ******************************************************************************
 *
 *  Pentaho Data Integration
 *
 *  Copyright (C) 2002-2017 by Hitachi Vantara : http://www.pentaho.com
 *
 * ******************************************************************************
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with
 *  the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 * *****************************************************************************
 */
package org.pentaho.di.engine.api.remote;


import LogLevel.DEBUG;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.pentaho.di.engine.api.events.ErrorEvent;
import org.pentaho.di.engine.api.events.MetricsEvent;
import org.pentaho.di.engine.api.model.ModelType;
import org.pentaho.di.engine.api.reporting.LogEntry;
import org.pentaho.di.engine.api.reporting.Metrics;
import org.pentaho.di.engine.api.reporting.Status;


/**
 * Tests MessageEncoder & MessageDecoder classes
 * <p>
 * Created by ccaspanello on 8/14/17.
 */
public class MessageEncoderDecoderTest {
    private MessageEncoder encoder = new MessageEncoder();

    private MessageDecoder decoder = new MessageDecoder();

    @Test
    public void testExecutionRequest() throws Exception {
        Message expected = executionRequest();
        String sMessage = encoder.encode(expected);
        Message actual = decoder.decode(sMessage);
        assertExecutionRequest(((ExecutionRequest) (expected)), ((ExecutionRequest) (actual)));
    }

    @Test
    public void testMetricEvent() throws Exception {
        Message expected = metricsEvent();
        String sMessage = encoder.encode(expected);
        Message actual = decoder.decode(sMessage);
        assertMetricsEvent(((MetricsEvent) (expected)), ((MetricsEvent) (actual)));
    }

    @Test
    public void testExecutionFetchRequest() throws Exception {
        Message expected = new ExecutionFetchRequest(UUID.randomUUID().toString());
        String sMessage = encoder.encode(expected);
        Message actual = decoder.decode(sMessage);
        ExecutionFetchRequest oExpected = ((ExecutionFetchRequest) (expected));
        ExecutionFetchRequest oActual = ((ExecutionFetchRequest) (actual));
        Assert.assertThat(oExpected.getRequestId(), CoreMatchers.equalTo(oActual.getRequestId()));
    }

    @Test
    public void testStopMessage() throws Exception {
        Message expected = new StopMessage("MyStop Reason");
        String sMessage = encoder.encode(expected);
        Message actual = decoder.decode(sMessage);
        StopMessage oExpected = ((StopMessage) (expected));
        StopMessage oActual = ((StopMessage) (actual));
        Assert.assertThat(oExpected.getReasonPhrase(), CoreMatchers.equalTo(oActual.getReasonPhrase()));
    }

    @Test
    public void testOperationRemoteSource() throws Exception {
        RemoteSource step = new RemoteSource(ModelType.OPERATION, "step");
        Metrics metrics = new Metrics(1, 2, 3, 4);
        Message metricEvent = new MetricsEvent(step, metrics);
        String sMessage = encoder.encode(metricEvent);
        Message decodeMessage = decoder.decode(sMessage);
        Assert.assertTrue(((getModelType()) == (getModelType())));
        Assert.assertTrue(getId().equals(getId()));
    }

    @Test
    public void testTransformationRemoteSource() throws Exception {
        RemoteSource step = new RemoteSource(ModelType.TRANSFORMATION);
        Metrics metrics = new Metrics(1, 2, 3, 4);
        Message statusEvent = new org.pentaho.di.engine.api.events.StatusEvent(step, Status.FAILED);
        String sMessage = encoder.encode(statusEvent);
        Message decodeMessage = decoder.decode(sMessage);
        Assert.assertTrue(((getModelType()) == (getModelType())));
        Assert.assertNull(getId());
    }

    @Test
    public void testRemoteSource() throws Exception {
        RemoteSource remoteSource = new RemoteSource("remoteId");
        Assert.assertNull(remoteSource.getModelType());
        Assert.assertTrue("remoteId".equals(remoteSource.getId()));
    }

    @Test
    public void testErrorEvent() throws Exception {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("key", "value");
        LogEntry logEntry = new LogEntry.LogEntryBuilder().withMessage("log message").withLogLevel(DEBUG).withTimestamp(new Date()).withExtras(hashMap).build();
        ErrorEvent errorEvent = new ErrorEvent(new RemoteSource(ModelType.TRANSFORMATION), logEntry);
        String sMessage = encoder.encode(errorEvent);
        Message decodeMessage = decoder.decode(sMessage);
        Assert.assertTrue(((getModelType()) == (getModelType())));
        LogEntry decodeLogEntry = ((LogEntry) (getData()));
        Assert.assertTrue(logEntry.getMessage().equals(decodeLogEntry.getMessage()));
        Assert.assertTrue(logEntry.getLogLogLevel().equals(decodeLogEntry.getLogLogLevel()));
        Assert.assertTrue(((logEntry.getTimestamp().getTime()) == (decodeLogEntry.getTimestamp().getTime())));
        Assert.assertTrue(((logEntry.getExtras().hashCode()) == (decodeLogEntry.getExtras().hashCode())));
    }
}

