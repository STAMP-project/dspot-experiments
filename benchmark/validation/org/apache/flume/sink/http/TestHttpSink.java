/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.flume.sink.http;


import Status.BACKOFF;
import Status.READY;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import org.apache.flume.Channel;
import org.apache.flume.Context;
import org.apache.flume.Event;
import org.apache.flume.Transaction;
import org.apache.flume.instrumentation.SinkCounter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class TestHttpSink {
    private static final Integer DEFAULT_REQUEST_TIMEOUT = 5000;

    private static final Integer DEFAULT_CONNECT_TIMEOUT = 5000;

    private static final String DEFAULT_ACCEPT_HEADER = "text/plain";

    private static final String DEFAULT_CONTENT_TYPE_HEADER = "text/plain";

    @Mock
    private SinkCounter sinkCounter;

    @Mock
    private Context configContext;

    @Mock
    private Channel channel;

    @Mock
    private Transaction transaction;

    @Mock
    private Event event;

    @Mock
    private HttpURLConnection httpURLConnection;

    @Mock
    private OutputStream outputStream;

    @Mock
    private InputStream inputStream;

    @Test
    public void ensureAllConfigurationOptionsRead() {
        whenDefaultStringConfig();
        whenDefaultBooleanConfig();
        Mockito.when(configContext.getInteger(ArgumentMatchers.eq("connectTimeout"), Mockito.anyInt())).thenReturn(1000);
        Mockito.when(configContext.getInteger(ArgumentMatchers.eq("requestTimeout"), Mockito.anyInt())).thenReturn(1000);
        new HttpSink().configure(configContext);
        Mockito.verify(configContext).getString("endpoint", "");
        Mockito.verify(configContext).getInteger(ArgumentMatchers.eq("connectTimeout"), Mockito.anyInt());
        Mockito.verify(configContext).getInteger(ArgumentMatchers.eq("requestTimeout"), Mockito.anyInt());
        Mockito.verify(configContext).getString(ArgumentMatchers.eq("acceptHeader"), Mockito.anyString());
        Mockito.verify(configContext).getString(ArgumentMatchers.eq("contentTypeHeader"), Mockito.anyString());
        Mockito.verify(configContext).getBoolean("defaultBackoff", true);
        Mockito.verify(configContext).getBoolean("defaultRollback", true);
        Mockito.verify(configContext).getBoolean("defaultIncrementMetrics", false);
    }

    @Test(expected = IllegalArgumentException.class)
    public void ensureExceptionIfEndpointUrlEmpty() {
        Mockito.when(configContext.getString("endpoint", "")).thenReturn("");
        new HttpSink().configure(configContext);
    }

    @Test(expected = IllegalArgumentException.class)
    public void ensureExceptionIfEndpointUrlInvalid() {
        Mockito.when(configContext.getString("endpoint", "")).thenReturn("invalid url");
        new HttpSink().configure(configContext);
    }

    @Test(expected = IllegalArgumentException.class)
    public void ensureExceptionIfConnectTimeoutNegative() {
        whenDefaultStringConfig();
        Mockito.when(configContext.getInteger("connectTimeout", 1000)).thenReturn((-1000));
        Mockito.when(configContext.getInteger(ArgumentMatchers.eq("requestTimeout"), Mockito.anyInt())).thenReturn(1000);
        new HttpSink().configure(configContext);
    }

    @Test
    public void ensureDefaultConnectTimeoutCorrect() {
        whenDefaultStringConfig();
        Mockito.when(configContext.getInteger("connectTimeout", TestHttpSink.DEFAULT_CONNECT_TIMEOUT)).thenReturn(1000);
        Mockito.when(configContext.getInteger(ArgumentMatchers.eq("requestTimeout"), Mockito.anyInt())).thenReturn(1000);
        new HttpSink().configure(configContext);
        Mockito.verify(configContext).getInteger("connectTimeout", TestHttpSink.DEFAULT_CONNECT_TIMEOUT);
    }

    @Test(expected = IllegalArgumentException.class)
    public void ensureExceptionIfRequestTimeoutNegative() {
        whenDefaultStringConfig();
        Mockito.when(configContext.getInteger("requestTimeout", 1000)).thenReturn((-1000));
        Mockito.when(configContext.getInteger(ArgumentMatchers.eq("connectTimeout"), Mockito.anyInt())).thenReturn(1000);
        new HttpSink().configure(configContext);
    }

    @Test
    public void ensureDefaultRequestTimeoutCorrect() {
        whenDefaultStringConfig();
        Mockito.when(configContext.getInteger("requestTimeout", TestHttpSink.DEFAULT_REQUEST_TIMEOUT)).thenReturn(1000);
        Mockito.when(configContext.getInteger(ArgumentMatchers.eq("connectTimeout"), Mockito.anyInt())).thenReturn(1000);
        new HttpSink().configure(configContext);
        Mockito.verify(configContext).getInteger("requestTimeout", TestHttpSink.DEFAULT_REQUEST_TIMEOUT);
    }

    @Test
    public void ensureDefaultAcceptHeaderCorrect() {
        whenDefaultTimeouts();
        whenDefaultStringConfig();
        new HttpSink().configure(configContext);
        Mockito.verify(configContext).getString("acceptHeader", TestHttpSink.DEFAULT_ACCEPT_HEADER);
    }

    @Test
    public void ensureDefaultContentTypeHeaderCorrect() {
        whenDefaultTimeouts();
        whenDefaultStringConfig();
        new HttpSink().configure(configContext);
        Mockito.verify(configContext).getString("contentTypeHeader", TestHttpSink.DEFAULT_CONTENT_TYPE_HEADER);
    }

    @Test
    public void ensureBackoffOnNullEvent() throws Exception {
        Mockito.when(channel.take()).thenReturn(null);
        executeWithMocks(true);
    }

    @Test
    public void ensureBackoffOnNullEventBody() throws Exception {
        Mockito.when(channel.take()).thenReturn(event);
        Mockito.when(event.getBody()).thenReturn(null);
        executeWithMocks(true);
    }

    @Test
    public void ensureBackoffOnEmptyEvent() throws Exception {
        Mockito.when(channel.take()).thenReturn(event);
        Mockito.when(event.getBody()).thenReturn(new byte[]{  });
        executeWithMocks(true);
    }

    @Test
    public void ensureRollbackBackoffAndIncrementMetricsIfConfigured() throws Exception {
        Mockito.when(channel.take()).thenReturn(event);
        Mockito.when(event.getBody()).thenReturn("something".getBytes());
        Context context = new Context();
        context.put("defaultRollback", "true");
        context.put("defaultBackoff", "true");
        context.put("defaultIncrementMetrics", "true");
        executeWithMocks(false, BACKOFF, true, true, context, HttpURLConnection.HTTP_OK);
    }

    @Test
    public void ensureCommitReadyAndNoIncrementMetricsIfConfigured() throws Exception {
        Mockito.when(channel.take()).thenReturn(event);
        Mockito.when(event.getBody()).thenReturn("something".getBytes());
        Context context = new Context();
        context.put("defaultRollback", "false");
        context.put("defaultBackoff", "false");
        context.put("defaultIncrementMetrics", "false");
        executeWithMocks(true, READY, false, false, context, HttpURLConnection.HTTP_OK);
    }

    @Test
    public void ensureSingleStatusConfigurationCorrectlyUsed() throws Exception {
        Mockito.when(channel.take()).thenReturn(event);
        Mockito.when(event.getBody()).thenReturn("something".getBytes());
        Context context = new Context();
        context.put("defaultRollback", "true");
        context.put("defaultBackoff", "true");
        context.put("defaultIncrementMetrics", "false");
        context.put("rollback.200", "false");
        context.put("backoff.200", "false");
        context.put("incrementMetrics.200", "true");
        executeWithMocks(true, READY, true, true, context, HttpURLConnection.HTTP_OK);
    }

    @Test
    public void testErrorCounter() throws Exception {
        RuntimeException exception = new RuntimeException("dummy");
        Mockito.when(channel.take()).thenThrow(exception);
        Context context = new Context();
        context.put("defaultRollback", "false");
        context.put("defaultBackoff", "false");
        context.put("defaultIncrementMetrics", "false");
        executeWithMocks(false, BACKOFF, false, false, context, HttpURLConnection.HTTP_OK);
        Mockito.inOrder(sinkCounter).verify(sinkCounter).incrementEventWriteOrChannelFail(exception);
    }

    @Test
    public void ensureSingleErrorStatusConfigurationCorrectlyUsed() throws Exception {
        Mockito.when(channel.take()).thenReturn(event);
        Mockito.when(event.getBody()).thenReturn("something".getBytes());
        Context context = new Context();
        context.put("defaultRollback", "true");
        context.put("defaultBackoff", "true");
        context.put("defaultIncrementMetrics", "false");
        context.put("rollback.401", "false");
        context.put("backoff.401", "false");
        context.put("incrementMetrics.401", "false");
        executeWithMocks(true, READY, false, true, context, HttpURLConnection.HTTP_UNAUTHORIZED);
    }

    @Test
    public void ensureGroupConfigurationCorrectlyUsed() throws Exception {
        Mockito.when(channel.take()).thenReturn(event);
        Mockito.when(event.getBody()).thenReturn("something".getBytes());
        Context context = new Context();
        context.put("defaultRollback", "true");
        context.put("defaultBackoff", "true");
        context.put("defaultIncrementMetrics", "false");
        context.put("rollback.2XX", "false");
        context.put("backoff.2XX", "false");
        context.put("incrementMetrics.2XX", "true");
        executeWithMocks(true, READY, true, true, context, HttpURLConnection.HTTP_OK);
        executeWithMocks(true, READY, true, true, context, HttpURLConnection.HTTP_NO_CONTENT);
    }

    @Test
    public void ensureSingleStatusConfigurationOverridesGroupConfigurationCorrectly() throws Exception {
        Mockito.when(channel.take()).thenReturn(event);
        Mockito.when(event.getBody()).thenReturn("something".getBytes());
        Context context = new Context();
        context.put("rollback.2XX", "false");
        context.put("backoff.2XX", "false");
        context.put("incrementMetrics.2XX", "true");
        context.put("rollback.200", "true");
        context.put("backoff.200", "true");
        context.put("incrementMetrics.200", "false");
        executeWithMocks(true, READY, true, true, context, HttpURLConnection.HTTP_NO_CONTENT);
        executeWithMocks(false, BACKOFF, false, true, context, HttpURLConnection.HTTP_OK);
    }
}

