/**
 * Copyright 2016 NAVER Corp.
 *
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
package com.navercorp.pinpoint.profiler.instrument;


import com.navercorp.pinpoint.profiler.instrument.mock.ApiIdAwareInterceptor;
import com.navercorp.pinpoint.profiler.instrument.mock.ArgsArrayInterceptor;
import com.navercorp.pinpoint.profiler.instrument.mock.BasicInterceptor;
import com.navercorp.pinpoint.profiler.instrument.mock.StaticInterceptor;
import com.navercorp.pinpoint.profiler.interceptor.factory.ExceptionHandlerFactory;
import com.navercorp.pinpoint.profiler.interceptor.registry.InterceptorRegistryBinder;
import com.navercorp.pinpoint.profiler.util.TestInterceptorRegistryBinder;
import org.junit.Test;


public class ASMMethodNodeAdapterAddInterceptorTest {
    private static final InterceptorRegistryBinder interceptorRegistryBinder = new TestInterceptorRegistryBinder();

    private ASMClassNodeLoader.TestClassLoader classLoader;

    private ExceptionHandlerFactory exceptionHandlerFactory = new ExceptionHandlerFactory(false);

    @Test
    public void addArgsArrayInterceptor() throws Exception {
        int interceptorId = ASMMethodNodeAdapterAddInterceptorTest.interceptorRegistryBinder.getInterceptorRegistryAdaptor().addInterceptor(new ArgsArrayInterceptor());
        addInterceptor(interceptorId, ArgsArrayInterceptor.class);
    }

    @Test
    public void addStaticInterceptor() throws Exception {
        int interceptorId = ASMMethodNodeAdapterAddInterceptorTest.interceptorRegistryBinder.getInterceptorRegistryAdaptor().addInterceptor(new StaticInterceptor());
        addInterceptor(interceptorId, StaticInterceptor.class);
    }

    @Test
    public void addApiIdAwareInterceptor() throws Exception {
        int interceptorId = ASMMethodNodeAdapterAddInterceptorTest.interceptorRegistryBinder.getInterceptorRegistryAdaptor().addInterceptor(new ApiIdAwareInterceptor());
        addInterceptor(interceptorId, ApiIdAwareInterceptor.class);
    }

    @Test
    public void addBasicInterceptor() throws Exception {
        int interceptorId = ASMMethodNodeAdapterAddInterceptorTest.interceptorRegistryBinder.getInterceptorRegistryAdaptor().addInterceptor(new BasicInterceptor());
        addInterceptor(interceptorId, BasicInterceptor.class);
    }
}

