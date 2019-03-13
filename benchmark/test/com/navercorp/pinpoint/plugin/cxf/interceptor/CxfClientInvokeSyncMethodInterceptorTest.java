/**
 * Copyright 2014 NAVER Corp.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.navercorp.pinpoint.plugin.cxf.interceptor;


import CxfPluginConstants.CXF_ARGS;
import CxfPluginConstants.CXF_CLIENT_SERVICE_TYPE;
import CxfPluginConstants.CXF_OPERATION;
import com.navercorp.pinpoint.bootstrap.config.ProfilerConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;


/**
 *
 *
 * @author barney
 */
@RunWith(MockitoJUnitRunner.class)
public class CxfClientInvokeSyncMethodInterceptorTest {
    @Mock
    private TraceContext traceContext;

    @Mock
    private MethodDescriptor descriptor;

    @Mock
    private ProfilerConfig profilerConfig;

    @Mock
    private Trace trace;

    @Mock
    private TraceId traceId;

    @Mock
    private TraceId nextId;

    @Mock
    private SpanEventRecorder recorder;

    @Test
    public void before() throws Exception {
        Mockito.doReturn(profilerConfig).when(traceContext).getProfilerConfig();
        Mockito.doReturn(trace).when(traceContext).currentRawTraceObject();
        Mockito.doReturn(true).when(trace).canSampled();
        Mockito.doReturn(traceId).when(trace).getTraceId();
        Mockito.doReturn(nextId).when(traceId).getNextTraceId();
        Mockito.doReturn(recorder).when(trace).traceBlockBegin();
        Object target = new Object();
        Object operInfo = "[BindingOperationInfo: {http://foo.com/}getFoo]";
        Object[] arg = new Object[]{ "foo", "bar" };
        Object[] args = new Object[]{ "", operInfo, arg };
        CxfClientInvokeSyncMethodInterceptor interceptor = new CxfClientInvokeSyncMethodInterceptor(traceContext, descriptor);
        interceptor.before(target, args);
        Mockito.verify(recorder).recordServiceType(CXF_CLIENT_SERVICE_TYPE);
        Mockito.verify(recorder).recordDestinationId("http://foo.com/");
        Mockito.verify(recorder).recordAttribute(CXF_OPERATION, "{http://foo.com/}getFoo");
        Mockito.verify(recorder).recordAttribute(CXF_ARGS, "[foo, bar]");
    }

    @Test
    public void sampled_false() throws Exception {
        Mockito.doReturn(profilerConfig).when(traceContext).getProfilerConfig();
        Mockito.doReturn(trace).when(traceContext).currentRawTraceObject();
        Mockito.doReturn(false).when(trace).canSampled();
        Object target = new Object();
        Object[] args = new Object[]{  };
        CxfClientInvokeSyncMethodInterceptor interceptor = new CxfClientInvokeSyncMethodInterceptor(traceContext, descriptor);
        interceptor.before(target, args);
        Mockito.verify(trace, Mockito.never()).traceBlockBegin();
    }

    @Test
    public void hidden_all_params() throws Exception {
        Mockito.doReturn(profilerConfig).when(traceContext).getProfilerConfig();
        String hiddenParams = "{http://foo.com/}getFoo";
        Mockito.doReturn(hiddenParams).when(profilerConfig).readString("profiler.cxf.client.hiddenParams", "");
        Mockito.doReturn(trace).when(traceContext).currentRawTraceObject();
        Mockito.doReturn(true).when(trace).canSampled();
        Mockito.doReturn(traceId).when(trace).getTraceId();
        Mockito.doReturn(nextId).when(traceId).getNextTraceId();
        Mockito.doReturn(recorder).when(trace).traceBlockBegin();
        Object target = new Object();
        Object operInfo = "[BindingOperationInfo: {http://foo.com/}getFoo]";
        Object[] arg = new Object[]{ "foo", "bar" };
        Object[] args = new Object[]{ "", operInfo, arg };
        CxfClientInvokeSyncMethodInterceptor interceptor = new CxfClientInvokeSyncMethodInterceptor(traceContext, descriptor);
        interceptor.before(target, args);
        Mockito.verify(recorder).recordServiceType(CXF_CLIENT_SERVICE_TYPE);
        Mockito.verify(recorder).recordDestinationId("http://foo.com/");
        Mockito.verify(recorder).recordAttribute(CXF_OPERATION, "{http://foo.com/}getFoo");
        Mockito.verify(recorder).recordAttribute(CXF_ARGS, "[HIDDEN 2 PARAM]");
    }

    @Test
    public void hidden_param_index() throws Exception {
        Mockito.doReturn(profilerConfig).when(traceContext).getProfilerConfig();
        String hiddenParams = "{http://foo.com/}getFoo:1";
        Mockito.doReturn(hiddenParams).when(profilerConfig).readString("profiler.cxf.client.hiddenParams", "");
        Mockito.doReturn(trace).when(traceContext).currentRawTraceObject();
        Mockito.doReturn(true).when(trace).canSampled();
        Mockito.doReturn(traceId).when(trace).getTraceId();
        Mockito.doReturn(nextId).when(traceId).getNextTraceId();
        Mockito.doReturn(recorder).when(trace).traceBlockBegin();
        Object target = new Object();
        Object operInfo = "[BindingOperationInfo: {http://foo.com/}getFoo]";
        Object[] arg = new Object[]{ "foo", "bar" };
        Object[] args = new Object[]{ "", operInfo, arg };
        CxfClientInvokeSyncMethodInterceptor interceptor = new CxfClientInvokeSyncMethodInterceptor(traceContext, descriptor);
        interceptor.before(target, args);
        Mockito.verify(recorder).recordServiceType(CXF_CLIENT_SERVICE_TYPE);
        Mockito.verify(recorder).recordDestinationId("http://foo.com/");
        Mockito.verify(recorder).recordAttribute(CXF_OPERATION, "{http://foo.com/}getFoo");
        Mockito.verify(recorder).recordAttribute(CXF_ARGS, "[foo, [HIDDEN PARAM]]");
    }

    @Test
    public void hidden_param_incorrect_index() throws Exception {
        Mockito.doReturn(profilerConfig).when(traceContext).getProfilerConfig();
        String hiddenParams = "{http://foo.com/}getFoo:2";
        Mockito.doReturn(hiddenParams).when(profilerConfig).readString("profiler.cxf.client.hiddenParams", "");
        Mockito.doReturn(trace).when(traceContext).currentRawTraceObject();
        Mockito.doReturn(true).when(trace).canSampled();
        Mockito.doReturn(traceId).when(trace).getTraceId();
        Mockito.doReturn(nextId).when(traceId).getNextTraceId();
        Mockito.doReturn(recorder).when(trace).traceBlockBegin();
        Object target = new Object();
        Object operInfo = "[BindingOperationInfo: {http://foo.com/}getFoo]";
        Object[] arg = new Object[]{ "foo", "bar" };
        Object[] args = new Object[]{ "", operInfo, arg };
        CxfClientInvokeSyncMethodInterceptor interceptor = new CxfClientInvokeSyncMethodInterceptor(traceContext, descriptor);
        interceptor.before(target, args);
        Mockito.verify(recorder).recordServiceType(CXF_CLIENT_SERVICE_TYPE);
        Mockito.verify(recorder).recordDestinationId("http://foo.com/");
        Mockito.verify(recorder).recordAttribute(CXF_OPERATION, "{http://foo.com/}getFoo");
        Mockito.verify(recorder).recordAttribute(CXF_ARGS, "[foo, bar]");
    }
}

