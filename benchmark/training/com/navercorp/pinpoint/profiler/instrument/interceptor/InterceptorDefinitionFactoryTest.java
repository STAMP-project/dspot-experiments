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
package com.navercorp.pinpoint.profiler.instrument.interceptor;


import CaptureType.AFTER;
import CaptureType.AROUND;
import CaptureType.BEFORE;
import CaptureType.NON;
import com.navercorp.pinpoint.bootstrap.interceptor.annotation.IgnoreMethod;
import java.lang.reflect.Method;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 *
 *
 * @author Woonduk Kang(emeroad)
 */
public class InterceptorDefinitionFactoryTest {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Test
    public void testGetInterceptorType_BasicType() throws Exception {
        InterceptorDefinitionFactory typeDetector = new InterceptorDefinitionFactory();
        Assert.assertSame(typeDetector.createInterceptorDefinition(AroundInterceptor.class).getCaptureType(), AROUND);
        Assert.assertSame(typeDetector.createInterceptorDefinition(AroundInterceptor0.class).getCaptureType(), AROUND);
        Assert.assertSame(typeDetector.createInterceptorDefinition(AroundInterceptor1.class).getCaptureType(), AROUND);
        Assert.assertSame(typeDetector.createInterceptorDefinition(AroundInterceptor2.class).getCaptureType(), AROUND);
        Assert.assertSame(typeDetector.createInterceptorDefinition(AroundInterceptor3.class).getCaptureType(), AROUND);
        Assert.assertSame(typeDetector.createInterceptorDefinition(AroundInterceptor4.class).getCaptureType(), AROUND);
        Assert.assertSame(typeDetector.createInterceptorDefinition(AroundInterceptor5.class).getCaptureType(), AROUND);
        Assert.assertSame(typeDetector.createInterceptorDefinition(StaticAroundInterceptor.class).getCaptureType(), AROUND);
        Assert.assertSame(typeDetector.createInterceptorDefinition(ApiIdAwareAroundInterceptor.class).getCaptureType(), AROUND);
    }

    @Test
    public void testGetInterceptorType_Inherited() throws Exception {
        InterceptorDefinitionFactory typeDetector = new InterceptorDefinitionFactory();
        Assert.assertSame(typeDetector.createInterceptorDefinition(InterceptorDefinitionFactoryTest.InheritedAroundInterceptor.class).getCaptureType(), AROUND);
    }

    @Test
    public void testDeclaredMethods() {
        Class<String> stringClass = String.class;
        for (Method method : stringClass.getDeclaredMethods()) {
            logger.debug("{}", method);
        }
    }

    @Test(expected = RuntimeException.class)
    public void testGetType_Error() throws Exception {
        InterceptorDefinitionFactory typeDetector = new InterceptorDefinitionFactory();
        typeDetector.createInterceptorDefinition(Interceptor.class);
    }

    @Test
    public void testGetInterceptorCaptureType() throws Exception {
        InterceptorDefinitionFactory interceptorDefinitionFactory = new InterceptorDefinitionFactory();
        final InterceptorDefinition before = interceptorDefinitionFactory.createInterceptorDefinition(InterceptorDefinitionFactoryTest.TestBeforeInterceptor.class);
        assertInterceptorType(before, BEFORE, "before", null);
        final InterceptorDefinition after = interceptorDefinitionFactory.createInterceptorDefinition(InterceptorDefinitionFactoryTest.TestAfterInterceptor.class);
        assertInterceptorType(after, AFTER, null, "after");
        final InterceptorDefinition around = interceptorDefinitionFactory.createInterceptorDefinition(InterceptorDefinitionFactoryTest.TestAroundInterceptor.class);
        assertInterceptorType(around, AROUND, "before", "after");
        final InterceptorDefinition ignore = interceptorDefinitionFactory.createInterceptorDefinition(InterceptorDefinitionFactoryTest.TestIgnoreInterceptor.class);
        assertInterceptorType(ignore, NON, null, null);
    }

    public static class TestBeforeInterceptor implements AroundInterceptor {
        @Override
        public void before(Object target, Object[] args) {
        }

        @IgnoreMethod
        @Override
        public void after(Object target, Object[] args, Object result, Throwable throwable) {
        }
    }

    public static class TestAfterInterceptor implements AroundInterceptor {
        @IgnoreMethod
        @Override
        public void before(Object target, Object[] args) {
        }

        @Override
        public void after(Object target, Object[] args, Object result, Throwable throwable) {
        }
    }

    public static class TestAroundInterceptor implements AroundInterceptor {
        @Override
        public void before(Object target, Object[] args) {
        }

        @Override
        public void after(Object target, Object[] args, Object result, Throwable throwable) {
        }
    }

    public static class TestIgnoreInterceptor implements AroundInterceptor {
        @IgnoreMethod
        @Override
        public void before(Object target, Object[] args) {
        }

        @IgnoreMethod
        @Override
        public void after(Object target, Object[] args, Object result, Throwable throwable) {
        }
    }

    public static class InheritedAroundInterceptor extends InterceptorDefinitionFactoryTest.TestAroundInterceptor {}
}

