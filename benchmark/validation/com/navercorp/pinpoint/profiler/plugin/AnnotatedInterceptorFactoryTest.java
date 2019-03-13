/**
 * Copyright 2014 NAVER Corp.
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
package com.navercorp.pinpoint.profiler.plugin;


import com.navercorp.pinpoint.bootstrap.config.ProfilerConfig;
import com.navercorp.pinpoint.bootstrap.context.MethodDescriptor;
import com.navercorp.pinpoint.bootstrap.context.TraceContext;
import com.navercorp.pinpoint.bootstrap.instrument.InstrumentClass;
import com.navercorp.pinpoint.bootstrap.instrument.InstrumentContext;
import com.navercorp.pinpoint.bootstrap.instrument.InstrumentMethod;
import com.navercorp.pinpoint.bootstrap.interceptor.Interceptor;
import com.navercorp.pinpoint.bootstrap.plugin.ObjectFactory;
import com.navercorp.pinpoint.bootstrap.plugin.monitor.DataSourceMonitorRegistry;
import com.navercorp.pinpoint.exception.PinpointException;
import com.navercorp.pinpoint.profiler.instrument.ScopeInfo;
import com.navercorp.pinpoint.profiler.interceptor.factory.AnnotatedInterceptorFactory;
import com.navercorp.pinpoint.profiler.interceptor.factory.ExceptionHandlerFactory;
import com.navercorp.pinpoint.profiler.metadata.ApiMetaDataService;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class AnnotatedInterceptorFactoryTest {
    private final ProfilerConfig profilerConfig = Mockito.mock(ProfilerConfig.class);

    private final DataSourceMonitorRegistry dataSourceMonitorRegistry = Mockito.mock(DataSourceMonitorRegistry.class);

    private final ApiMetaDataService apiMetaDataService = Mockito.mock(ApiMetaDataService.class);

    private final InstrumentContext pluginContext = Mockito.mock(InstrumentContext.class);

    private final TraceContext traceContext = Mockito.mock(TraceContext.class);

    private final InstrumentClass instrumentClass = Mockito.mock(InstrumentClass.class);

    private final InstrumentMethod instrumentMethod = Mockito.mock(InstrumentMethod.class);

    private final MethodDescriptor descriptor = Mockito.mock(MethodDescriptor.class);

    private final ExceptionHandlerFactory exceptionHandlerFactory = new ExceptionHandlerFactory(false);

    @Test
    public void test0() throws Exception {
        AnnotatedInterceptorFactory factory = newAnnotatedInterceptorFactory();
        final ScopeInfo scopeInfo = newEmptyScopeInfo();
        Interceptor interceptor = factory.newInterceptor(TestInterceptors.TestInterceptor0.class, null, scopeInfo, instrumentClass, instrumentMethod);
        Assert.assertEquals(TestInterceptors.TestInterceptor0.class, interceptor.getClass());
    }

    @Test
    public void test1() throws Exception {
        Object[] args = new Object[]{ "arg0" };
        AnnotatedInterceptorFactory factory = newAnnotatedInterceptorFactory();
        final ScopeInfo scopeInfo = newEmptyScopeInfo();
        Interceptor interceptor = factory.newInterceptor(TestInterceptors.TestInterceptor0.class, args, scopeInfo, instrumentClass, instrumentMethod);
        Assert.assertEquals(TestInterceptors.TestInterceptor0.class, interceptor.getClass());
        Assert.assertEquals(args[0], getField(interceptor, "field0"));
    }

    @Test(expected = PinpointException.class)
    public void test2() throws Exception {
        Object[] args = new Object[]{ 1 };
        AnnotatedInterceptorFactory factory = newAnnotatedInterceptorFactory();
        final ScopeInfo scopeInfo = newEmptyScopeInfo();
        factory.newInterceptor(TestInterceptors.TestInterceptor0.class, args, scopeInfo, instrumentClass, instrumentMethod);
    }

    @Test
    public void test3() throws Exception {
        Object[] args = new Object[]{ "arg0", ((byte) (1)), ((short) (2)), ((float) (3.0)) };
        AnnotatedInterceptorFactory factory = newAnnotatedInterceptorFactory();
        final ScopeInfo scopeInfo = newEmptyScopeInfo();
        Interceptor interceptor = factory.newInterceptor(TestInterceptors.TestInterceptor1.class, args, scopeInfo, instrumentClass, instrumentMethod);
        Assert.assertEquals(TestInterceptors.TestInterceptor1.class, interceptor.getClass());
        Assert.assertEquals(args[0], getField(interceptor, "field0"));
        Assert.assertEquals(args[1], getField(interceptor, "field1"));
        Assert.assertEquals(args[2], getField(interceptor, "field2"));
        Assert.assertEquals(args[3], getField(interceptor, "field3"));
    }

    @Test
    public void test4() throws Exception {
        Object[] args = new Object[]{ ((byte) (1)), ((short) (2)), ((float) (3.0)), "arg0" };
        AnnotatedInterceptorFactory factory = newAnnotatedInterceptorFactory();
        final ScopeInfo scopeInfo = newEmptyScopeInfo();
        Interceptor interceptor = factory.newInterceptor(TestInterceptors.TestInterceptor1.class, args, scopeInfo, instrumentClass, instrumentMethod);
        Assert.assertEquals(TestInterceptors.TestInterceptor1.class, interceptor.getClass());
        Assert.assertEquals(args[3], getField(interceptor, "field0"));
        Assert.assertEquals(args[0], getField(interceptor, "field1"));
        Assert.assertEquals(args[1], getField(interceptor, "field2"));
        Assert.assertEquals(args[2], getField(interceptor, "field3"));
    }

    @Test
    public void test5() throws Exception {
        Object[] args = new Object[]{ ((short) (2)), ((float) (3.0)), "arg0", ((byte) (1)) };
        AnnotatedInterceptorFactory factory = newAnnotatedInterceptorFactory();
        final ScopeInfo scopeInfo = newEmptyScopeInfo();
        Interceptor interceptor = factory.newInterceptor(TestInterceptors.TestInterceptor1.class, args, scopeInfo, instrumentClass, instrumentMethod);
        Assert.assertEquals(TestInterceptors.TestInterceptor1.class, interceptor.getClass());
        Assert.assertEquals(args[2], getField(interceptor, "field0"));
        Assert.assertEquals(args[3], getField(interceptor, "field1"));
        Assert.assertEquals(args[0], getField(interceptor, "field2"));
        Assert.assertEquals(args[1], getField(interceptor, "field3"));
    }

    @Test
    public void test6() throws Exception {
        Object[] args = new Object[]{ ((float) (3.0)), ((short) (2)), ((byte) (1)), "arg0" };
        AnnotatedInterceptorFactory factory = newAnnotatedInterceptorFactory();
        final ScopeInfo scopeInfo = newEmptyScopeInfo();
        Interceptor interceptor = factory.newInterceptor(TestInterceptors.TestInterceptor1.class, args, scopeInfo, instrumentClass, instrumentMethod);
        Assert.assertEquals(TestInterceptors.TestInterceptor1.class, interceptor.getClass());
        Assert.assertEquals(args[3], getField(interceptor, "field0"));
        Assert.assertEquals(args[2], getField(interceptor, "field1"));
        Assert.assertEquals(args[1], getField(interceptor, "field2"));
        Assert.assertEquals(args[0], getField(interceptor, "field3"));
    }

    @Test(expected = PinpointException.class)
    public void test7() throws Exception {
        AnnotatedInterceptorFactory factory = newAnnotatedInterceptorFactory();
        final ScopeInfo scopeInfo = newEmptyScopeInfo();
        factory.newInterceptor(TestInterceptors.TestInterceptor1.class, null, scopeInfo, instrumentClass, instrumentMethod);
    }

    @Test(expected = PinpointException.class)
    public void test8() throws Exception {
        AnnotatedInterceptorFactory factory = newAnnotatedInterceptorFactory();
        final ScopeInfo scopeInfo = newEmptyScopeInfo();
        factory.newInterceptor(TestInterceptors.TestInterceptor1.class, null, scopeInfo, instrumentClass, instrumentMethod);
    }

    @Test
    public void test9() throws Exception {
        Object[] args = new Object[]{ "arg0", 1, 2.0, true, 3L };
        AnnotatedInterceptorFactory factory = newAnnotatedInterceptorFactory();
        final ScopeInfo scopeInfo = newEmptyScopeInfo();
        Interceptor interceptor = factory.newInterceptor(TestInterceptors.TestInterceptor2.class, args, scopeInfo, instrumentClass, instrumentMethod);
        Assert.assertEquals(TestInterceptors.TestInterceptor2.class, interceptor.getClass());
        Assert.assertEquals(args[0], getField(interceptor, "field0"));
        Assert.assertEquals(args[1], getField(interceptor, "field1"));
        Assert.assertEquals(args[2], getField(interceptor, "field2"));
        Assert.assertEquals(args[3], getField(interceptor, "field3"));
        Assert.assertEquals(args[4], getField(interceptor, "field4"));
        Assert.assertSame(descriptor, getField(interceptor, "descriptor"));
        Assert.assertSame(instrumentClass, getField(interceptor, "targetClass"));
        Assert.assertSame(instrumentMethod, getField(interceptor, "targetMethod"));
    }

    @Test
    public void test10() throws Exception {
        Object[] args = new Object[]{ "arg0", 1, 2.0 };
        AnnotatedInterceptorFactory factory = newAnnotatedInterceptorFactory();
        final ScopeInfo scopeInfo = newEmptyScopeInfo();
        Interceptor interceptor = factory.newInterceptor(TestInterceptors.TestInterceptor2.class, args, scopeInfo, instrumentClass, instrumentMethod);
        Assert.assertEquals(TestInterceptors.TestInterceptor2.class, interceptor.getClass());
        Assert.assertEquals(args[0], getField(interceptor, "field0"));
        Assert.assertEquals(args[1], getField(interceptor, "field1"));
        Assert.assertEquals(args[2], getField(interceptor, "field2"));
        Assert.assertEquals(false, getField(interceptor, "field3"));
        Assert.assertEquals(0L, getField(interceptor, "field4"));
        Assert.assertSame(descriptor, getField(interceptor, "descriptor"));
        Assert.assertSame(instrumentClass, getField(interceptor, "targetClass"));
        Assert.assertSame(instrumentMethod, getField(interceptor, "targetMethod"));
    }

    @Test
    public void test11() throws Exception {
        Object[] args = new Object[]{ "arg0", 1 };
        AnnotatedInterceptorFactory factory = newAnnotatedInterceptorFactory();
        final ScopeInfo scopeInfo = newEmptyScopeInfo();
        Interceptor interceptor = factory.newInterceptor(TestInterceptors.TestInterceptor2.class, args, scopeInfo, instrumentClass, instrumentMethod);
        Assert.assertEquals(TestInterceptors.TestInterceptor2.class, interceptor.getClass());
        Assert.assertEquals(args[0], getField(interceptor, "field0"));
        Assert.assertEquals(args[1], getField(interceptor, "field1"));
        Assert.assertEquals(0.0, getField(interceptor, "field2"));
        Assert.assertEquals(false, getField(interceptor, "field3"));
        Assert.assertEquals(0L, getField(interceptor, "field4"));
        Assert.assertSame(descriptor, getField(interceptor, "descriptor"));
        Assert.assertSame(instrumentClass, getField(interceptor, "targetClass"));
        Assert.assertSame(instrumentMethod, getField(interceptor, "targetMethod"));
    }

    @Test
    public void test12() throws Exception {
        AnnotatedInterceptorFactory factory = newAnnotatedInterceptorFactory();
        final ScopeInfo scopeInfo = newEmptyScopeInfo();
        Interceptor interceptor = factory.newInterceptor(TestInterceptors.TestInterceptor2.class, null, scopeInfo, instrumentClass, instrumentMethod);
        Assert.assertEquals(TestInterceptors.TestInterceptor2.class, interceptor.getClass());
        Assert.assertEquals(null, getField(interceptor, "field0"));
        Assert.assertEquals(0, getField(interceptor, "field1"));
        Assert.assertEquals(0.0, getField(interceptor, "field2"));
        Assert.assertEquals(false, getField(interceptor, "field3"));
        Assert.assertEquals(0L, getField(interceptor, "field4"));
        Assert.assertSame(descriptor, getField(interceptor, "descriptor"));
        Assert.assertSame(instrumentClass, getField(interceptor, "targetClass"));
        Assert.assertSame(instrumentMethod, getField(interceptor, "targetMethod"));
    }

    @Test
    public void test13() throws Exception {
        Object[] args = new Object[]{ "arg0" };
        AnnotatedInterceptorFactory factory = newAnnotatedInterceptorFactory();
        final ScopeInfo scopeInfo = newEmptyScopeInfo();
        Interceptor interceptor = factory.newInterceptor(TestInterceptors.TestInterceptor2.class, args, scopeInfo, instrumentClass, instrumentMethod);
        Assert.assertEquals(TestInterceptors.TestInterceptor2.class, interceptor.getClass());
        Assert.assertEquals(args[0], getField(interceptor, "field0"));
        Assert.assertEquals(0, getField(interceptor, "field1"));
        Assert.assertEquals(0.0, getField(interceptor, "field2"));
        Assert.assertEquals(false, getField(interceptor, "field3"));
        Assert.assertEquals(0L, getField(interceptor, "field4"));
        Assert.assertSame(descriptor, getField(interceptor, "descriptor"));
        Assert.assertSame(instrumentClass, getField(interceptor, "targetClass"));
        Assert.assertNull(getField(interceptor, "targetMethod"));
    }

    @Test
    public void test14() throws Exception {
        String arg0 = "arg0";
        Object[] args = new Object[]{ ObjectFactory.byConstructor("java.lang.String", arg0) };
        AnnotatedInterceptorFactory factory = newAnnotatedInterceptorFactory();
        final ScopeInfo scopeInfo = newEmptyScopeInfo();
        Interceptor interceptor = factory.newInterceptor(TestInterceptors.TestInterceptor0.class, args, scopeInfo, instrumentClass, instrumentMethod);
        Assert.assertEquals(TestInterceptors.TestInterceptor0.class, interceptor.getClass());
        Assert.assertEquals(arg0, getField(interceptor, "field0"));
    }
}

