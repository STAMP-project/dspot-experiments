/**
 * Copyright 2018 NAVER Corp.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.navercorp.pinpoint.bootstrap.plugin.request;


import Header.HTTP_FLAGS;
import Header.HTTP_PARENT_SPAN_ID;
import Header.HTTP_SAMPLED;
import Header.HTTP_SPAN_ID;
import Header.HTTP_TRACE_ID;
import com.navercorp.pinpoint.bootstrap.config.DefaultProfilerConfig;
import com.navercorp.pinpoint.bootstrap.context.Trace;
import com.navercorp.pinpoint.bootstrap.context.TraceContext;
import com.navercorp.pinpoint.bootstrap.context.TraceId;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


/**
 *
 *
 * @author jaehong.kim
 */
public class RequestWrapperReaderTest {
    @Test
    public void read() throws Exception {
        // disable sampling
        Trace disableTrace = Mockito.mock(Trace.class);
        Mockito.when(disableTrace.canSampled()).thenReturn(Boolean.FALSE);
        // continue trace
        Trace continueTrace = Mockito.mock(Trace.class);
        Mockito.when(continueTrace.canSampled()).thenReturn(Boolean.TRUE);
        // new trace
        Trace newTrace = Mockito.mock(Trace.class);
        Mockito.when(newTrace.canSampled()).thenReturn(Boolean.TRUE);
        TraceContext traceContext = Mockito.mock(TraceContext.class);
        Mockito.when(traceContext.disableSampling()).thenReturn(disableTrace);
        Mockito.when(traceContext.continueTraceObject(ArgumentMatchers.any(TraceId.class))).thenReturn(continueTrace);
        Mockito.when(traceContext.newTraceObject()).thenReturn(newTrace);
        Mockito.when(traceContext.getProfilerConfig()).thenReturn(new DefaultProfilerConfig());
        TraceId traceId = Mockito.mock(TraceId.class);
        Mockito.when(traceContext.createTraceId(ArgumentMatchers.anyString(), ArgumentMatchers.anyLong(), ArgumentMatchers.anyLong(), ArgumentMatchers.anyShort())).thenReturn(traceId);
        RequestAdaptor<ServerRequestWrapper> serverRequestWrapperAdaptor = new ServerRequestWrapperAdaptor();
        final RequestTraceReader<ServerRequestWrapper> reader = new RequestTraceReader<ServerRequestWrapper>(traceContext, serverRequestWrapperAdaptor);
        // sampling flag is true
        ServerRequestWrapper samplingFlagServerRequestWrapper = Mockito.mock(ServerRequestWrapper.class);
        Mockito.when(samplingFlagServerRequestWrapper.getHeader(HTTP_SAMPLED.toString())).thenReturn("s0");
        Assert.assertEquals(disableTrace, reader.read(samplingFlagServerRequestWrapper));
        // continue trace
        ServerRequestWrapper continueServerRequestWrapper = Mockito.mock(ServerRequestWrapper.class);
        Mockito.when(continueServerRequestWrapper.getHeader(HTTP_TRACE_ID.toString())).thenReturn("avcrawler01.ugedit^1517877953952^1035131");
        Mockito.when(continueServerRequestWrapper.getHeader(HTTP_PARENT_SPAN_ID.toString())).thenReturn("1");
        Mockito.when(continueServerRequestWrapper.getHeader(HTTP_SPAN_ID.toString())).thenReturn("1");
        Mockito.when(continueServerRequestWrapper.getHeader(HTTP_FLAGS.toString())).thenReturn("1");
        Assert.assertEquals(continueTrace, reader.read(continueServerRequestWrapper));
        // new trace
        ServerRequestWrapper newServerRequestWrapper = Mockito.mock(ServerRequestWrapper.class);
        Assert.assertEquals(newTrace, reader.read(newServerRequestWrapper));
    }
}

