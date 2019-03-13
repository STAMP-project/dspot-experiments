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


import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import org.junit.Test;


/**
 *
 *
 * @author jaehong.kim
 */
public class ASMMethodNodeAdapterAddDelegatorTest {
    private ASMClassNodeLoader.TestClassLoader classLoader;

    @Test
    public void addDelegatorMethod() throws Exception {
        Class<?> clazz = addDelegatorMethod("com.navercorp.pinpoint.profiler.instrument.mock.ExtendedClass", "com.navercorp.pinpoint.profiler.instrument.mock.BaseClass", "base");
        Method method = clazz.getDeclaredMethod("base");
        method.invoke(clazz.newInstance());
    }

    @Test
    public void addDelegatorStaticMethod() throws Exception {
        Class<?> clazz = addDelegatorMethod("com.navercorp.pinpoint.profiler.instrument.mock.ExtendedClass", "com.navercorp.pinpoint.profiler.instrument.mock.BaseClass", "getInstance");
        Method method = clazz.getDeclaredMethod("getInstance");
        method.invoke(clazz.newInstance());
    }

    @Test
    public void addDelegatorPublicArgByteReturnVoidMethod() throws Exception {
        Class<?> clazz = addDelegatorMethod("com.navercorp.pinpoint.profiler.instrument.mock.DelegatorClass", "com.navercorp.pinpoint.profiler.instrument.mock.DelegatorSuperClass", "publicArgByteReturnVoid");
        Method method = clazz.getDeclaredMethod("publicArgByteReturnVoid", byte.class);
        byte args = 1;
        method.invoke(clazz.newInstance(), args);
    }

    @Test
    public void addDelegatorPublicArgStringReturnStringMethod() throws Exception {
        Class<?> clazz = addDelegatorMethod("com.navercorp.pinpoint.profiler.instrument.mock.DelegatorClass", "com.navercorp.pinpoint.profiler.instrument.mock.DelegatorSuperClass", "publicArgStringReturnString");
        Method method = clazz.getDeclaredMethod("publicArgStringReturnString", String.class);
        String args = "";
        Object result = method.invoke(clazz.newInstance(), args);
    }

    @Test
    public void addDelegatorPublicArgStringReturnStringArrayMethod() throws Exception {
        Class<?> clazz = addDelegatorMethod("com.navercorp.pinpoint.profiler.instrument.mock.DelegatorClass", "com.navercorp.pinpoint.profiler.instrument.mock.DelegatorSuperClass", "publicArgStringReturnStringArray");
        Method method = clazz.getDeclaredMethod("publicArgStringReturnStringArray", String.class, String.class);
        Object result = method.invoke(clazz.newInstance(), "foo", "bar");
    }

    @Test
    public void addDelegatorPublicArgStringReturnStringArraysMethod() throws Exception {
        Class<?> clazz = addDelegatorMethod("com.navercorp.pinpoint.profiler.instrument.mock.DelegatorClass", "com.navercorp.pinpoint.profiler.instrument.mock.DelegatorSuperClass", "publicArgStringReturnStringArrays");
        Method method = clazz.getDeclaredMethod("publicArgStringReturnStringArrays", String.class, String.class, String.class);
        Object result = method.invoke(clazz.newInstance(), "foo", "bar", "zoo");
        if (result instanceof String[][]) {
            String[][] array = ((String[][]) (result));
        }
    }

    @Test
    public void addDelegatorPublicArgInterfaceReturnVoidMethod() throws Exception {
        Class<?> clazz = addDelegatorMethod("com.navercorp.pinpoint.profiler.instrument.mock.DelegatorClass", "com.navercorp.pinpoint.profiler.instrument.mock.DelegatorSuperClass", "publicArgInterfaceReturnVoid");
        Method method = clazz.getDeclaredMethod("publicArgInterfaceReturnVoid", Map.class, Map.class, Map.class);
        Map map = new HashMap();
        method.invoke(clazz.newInstance(), map, map, map);
    }

    @Test
    public void addDelegatorPublicArgsReturnVoidMethod() throws Exception {
        Class<?> clazz = addDelegatorMethod("com.navercorp.pinpoint.profiler.instrument.mock.DelegatorClass", "com.navercorp.pinpoint.profiler.instrument.mock.DelegatorSuperClass", "publicArgsReturnVoid");
        Method method = clazz.getDeclaredMethod("publicArgsReturnVoid", Object[].class);
        Object[] args = new Object[1];
        method.invoke(clazz.newInstance(), args);
    }
}

