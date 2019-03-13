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
package com.navercorp.pinpoint.bootstrap.plugin.proxy;


import com.navercorp.pinpoint.bootstrap.config.ProfilerConfig;
import com.navercorp.pinpoint.bootstrap.context.SpanRecorder;
import com.navercorp.pinpoint.bootstrap.context.TraceContext;
import com.navercorp.pinpoint.bootstrap.plugin.request.RequestAdaptor;
import com.navercorp.pinpoint.bootstrap.plugin.request.ServerRequestWrapper;
import com.navercorp.pinpoint.bootstrap.plugin.request.ServerRequestWrapperAdaptor;
import java.util.Arrays;
import java.util.List;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


/**
 *
 *
 * @author jaehong.kim
 */
public class ProxyHttpHeaderRecorderTest {
    @Test
    public void record() throws Exception {
        List<String> list = Arrays.asList("PINPOINT-PROXY");
        // TraceContext
        ProfilerConfig config = Mockito.mock(ProfilerConfig.class);
        Mockito.when(config.isProxyHttpHeaderEnable()).thenReturn(true);
        TraceContext traceContext = Mockito.mock(TraceContext.class);
        Mockito.when(traceContext.getProfilerConfig()).thenReturn(config);
        Mockito.when(traceContext.cacheString(ArgumentMatchers.any(String.class))).thenReturn(100);
        // SpanRecorder
        SpanRecorder spanRecorder = Mockito.mock(SpanRecorder.class);
        RequestAdaptor<ServerRequestWrapper> requestWrapperAdaptor = new ServerRequestWrapperAdaptor();
        ProxyHttpHeaderRecorder<ServerRequestWrapper> recorder = new ProxyHttpHeaderRecorder(true, requestWrapperAdaptor);
        recorder.record(spanRecorder, new ServerRequestWrapper() {
            @Override
            public String getHeader(String name) {
                return name;
            }

            @Override
            public String getRpcName() {
                return null;
            }

            @Override
            public String getEndPoint() {
                return null;
            }

            @Override
            public String getRemoteAddress() {
                return null;
            }

            @Override
            public String getAcceptorHost() {
                return null;
            }
        });
        recorder.record(spanRecorder, new ServerRequestWrapper() {
            @Override
            public String getHeader(String name) {
                throw new NullPointerException();
            }

            @Override
            public String getRpcName() {
                return null;
            }

            @Override
            public String getEndPoint() {
                return null;
            }

            @Override
            public String getRemoteAddress() {
                return null;
            }

            @Override
            public String getAcceptorHost() {
                return null;
            }
        });
    }
}

