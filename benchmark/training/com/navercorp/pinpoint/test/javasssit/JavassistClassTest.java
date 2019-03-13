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
package com.navercorp.pinpoint.test.javasssit;


import com.navercorp.pinpoint.bootstrap.instrument.InstrumentClass;
import com.navercorp.pinpoint.bootstrap.instrument.InstrumentException;
import com.navercorp.pinpoint.bootstrap.instrument.InstrumentMethod;
import com.navercorp.pinpoint.bootstrap.instrument.Instrumentor;
import com.navercorp.pinpoint.bootstrap.instrument.transformer.TransformCallback;
import com.navercorp.pinpoint.bootstrap.interceptor.Interceptor;
import com.navercorp.pinpoint.profiler.context.module.DefaultApplicationContext;
import com.navercorp.pinpoint.profiler.logging.Slf4jLoggerBinder;
import com.navercorp.pinpoint.test.classloader.TestClassLoader;
import java.lang.reflect.Method;
import java.security.ProtectionDomain;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 *
 *
 * @author emeroad
 */
// @Deprecated
public class JavassistClassTest {
    private Logger logger = LoggerFactory.getLogger(JavassistClassTest.class.getName());

    private DefaultApplicationContext applicationContext;

    Slf4jLoggerBinder loggerBinder = new Slf4jLoggerBinder();

    @Test
    public void testBeforeAddInterceptor() throws Exception {
        final TestClassLoader loader = getTestClassLoader();
        final String javassistClassName = "com.navercorp.pinpoint.test.javasssit.mock.TestObject";
        loader.addTransformer(javassistClassName, new TransformCallback() {
            @Override
            public byte[] doInTransform(Instrumentor instrumentor, ClassLoader classLoader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws InstrumentException {
                try {
                    logger.info("modify className:{} cl:{}", className, classLoader);
                    InstrumentClass aClass = instrumentor.getInstrumentClass(classLoader, javassistClassName, classfileBuffer);
                    String methodName = "callA";
                    InstrumentMethod callaMethod = aClass.getDeclaredMethod(methodName);
                    callaMethod.addInterceptor("com.navercorp.pinpoint.test.javasssit.TestBeforeInterceptor");
                    byte[] bytes = aClass.toBytecode();
                    return bytes;
                } catch (Throwable e) {
                    e.printStackTrace();
                    throw new RuntimeException(e.getMessage(), e);
                }
            }
        });
        Class<?> testObjectClazz = loader.loadClass(javassistClassName);
        final String methodName = "callA";
        logger.info("class:{}", testObjectClazz.toString());
        logger.info("class cl:{}", testObjectClazz.getClassLoader());
        final Object testObject = testObjectClazz.newInstance();
        Method callA = testObjectClazz.getMethod(methodName);
        callA.invoke(testObject);
        Interceptor interceptor = getInterceptor(loader, 0);
        assertEqualsIntField(interceptor, "call", 1);
        assertEqualsObjectField(interceptor, "className", "com.navercorp.pinpoint.test.javasssit.mock.TestObject");
        assertEqualsObjectField(interceptor, "methodName", methodName);
        assertEqualsObjectField(interceptor, "args", null);
        assertEqualsObjectField(interceptor, "target", testObject);
    }
}

