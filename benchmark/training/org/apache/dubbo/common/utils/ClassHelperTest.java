/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.dubbo.common.utils;


import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


public class ClassHelperTest {
    @Test
    public void testForNameWithThreadContextClassLoader() throws Exception {
        ClassLoader oldClassLoader = Thread.currentThread().getContextClassLoader();
        try {
            ClassLoader classLoader = Mockito.mock(ClassLoader.class);
            Thread.currentThread().setContextClassLoader(classLoader);
            ClassHelper.forNameWithThreadContextClassLoader("a.b.c.D");
            Mockito.verify(classLoader).loadClass("a.b.c.D");
        } finally {
            Thread.currentThread().setContextClassLoader(oldClassLoader);
        }
    }

    @Test
    public void tetForNameWithCallerClassLoader() throws Exception {
        Class c = ClassHelper.forNameWithCallerClassLoader(ClassHelper.class.getName(), ClassHelperTest.class);
        MatcherAssert.assertThat((c == (ClassHelper.class)), Matchers.is(true));
    }

    @Test
    public void testGetCallerClassLoader() throws Exception {
        MatcherAssert.assertThat(ClassHelper.getCallerClassLoader(ClassHelperTest.class), Matchers.sameInstance(ClassHelperTest.class.getClassLoader()));
    }

    @Test
    public void testGetClassLoader1() throws Exception {
        ClassLoader oldClassLoader = Thread.currentThread().getContextClassLoader();
        try {
            MatcherAssert.assertThat(ClassHelper.getClassLoader(ClassHelperTest.class), Matchers.sameInstance(oldClassLoader));
            Thread.currentThread().setContextClassLoader(null);
            MatcherAssert.assertThat(ClassHelper.getClassLoader(ClassHelperTest.class), Matchers.sameInstance(ClassHelperTest.class.getClassLoader()));
        } finally {
            Thread.currentThread().setContextClassLoader(oldClassLoader);
        }
    }

    @Test
    public void testGetClassLoader2() throws Exception {
        MatcherAssert.assertThat(ClassHelper.getClassLoader(), Matchers.sameInstance(ClassHelper.class.getClassLoader()));
    }

    @Test
    public void testForName1() throws Exception {
        MatcherAssert.assertThat(((ClassHelper.forName(ClassHelperTest.class.getName())) == (ClassHelperTest.class)), Matchers.is(true));
    }

    @Test
    public void testForName2() throws Exception {
        MatcherAssert.assertThat(((ClassHelper.forName("byte")) == (byte.class)), Matchers.is(true));
        MatcherAssert.assertThat(((ClassHelper.forName("java.lang.String[]")) == (String[].class)), Matchers.is(true));
        MatcherAssert.assertThat(((ClassHelper.forName("[Ljava.lang.String;")) == (String[].class)), Matchers.is(true));
    }

    @Test
    public void testForName3() throws Exception {
        ClassLoader classLoader = Mockito.mock(ClassLoader.class);
        ClassHelper.forName("a.b.c.D", classLoader);
        Mockito.verify(classLoader).loadClass("a.b.c.D");
    }

    @Test
    public void testResolvePrimitiveClassName() throws Exception {
        MatcherAssert.assertThat(((ClassHelper.resolvePrimitiveClassName("boolean")) == (boolean.class)), Matchers.is(true));
        MatcherAssert.assertThat(((ClassHelper.resolvePrimitiveClassName("byte")) == (byte.class)), Matchers.is(true));
        MatcherAssert.assertThat(((ClassHelper.resolvePrimitiveClassName("char")) == (char.class)), Matchers.is(true));
        MatcherAssert.assertThat(((ClassHelper.resolvePrimitiveClassName("double")) == (double.class)), Matchers.is(true));
        MatcherAssert.assertThat(((ClassHelper.resolvePrimitiveClassName("float")) == (float.class)), Matchers.is(true));
        MatcherAssert.assertThat(((ClassHelper.resolvePrimitiveClassName("int")) == (int.class)), Matchers.is(true));
        MatcherAssert.assertThat(((ClassHelper.resolvePrimitiveClassName("long")) == (long.class)), Matchers.is(true));
        MatcherAssert.assertThat(((ClassHelper.resolvePrimitiveClassName("short")) == (short.class)), Matchers.is(true));
        MatcherAssert.assertThat(((ClassHelper.resolvePrimitiveClassName("[Z")) == (boolean[].class)), Matchers.is(true));
        MatcherAssert.assertThat(((ClassHelper.resolvePrimitiveClassName("[B")) == (byte[].class)), Matchers.is(true));
        MatcherAssert.assertThat(((ClassHelper.resolvePrimitiveClassName("[C")) == (char[].class)), Matchers.is(true));
        MatcherAssert.assertThat(((ClassHelper.resolvePrimitiveClassName("[D")) == (double[].class)), Matchers.is(true));
        MatcherAssert.assertThat(((ClassHelper.resolvePrimitiveClassName("[F")) == (float[].class)), Matchers.is(true));
        MatcherAssert.assertThat(((ClassHelper.resolvePrimitiveClassName("[I")) == (int[].class)), Matchers.is(true));
        MatcherAssert.assertThat(((ClassHelper.resolvePrimitiveClassName("[J")) == (long[].class)), Matchers.is(true));
        MatcherAssert.assertThat(((ClassHelper.resolvePrimitiveClassName("[S")) == (short[].class)), Matchers.is(true));
    }

    @Test
    public void testToShortString() throws Exception {
        MatcherAssert.assertThat(ClassHelper.toShortString(null), Matchers.equalTo("null"));
        MatcherAssert.assertThat(ClassHelper.toShortString(new ClassHelperTest()), Matchers.startsWith("ClassHelperTest@"));
    }

    @Test
    public void testConvertPrimitive() throws Exception {
        MatcherAssert.assertThat(ClassHelper.convertPrimitive(char.class, ""), Matchers.equalTo('\u0000'));
        MatcherAssert.assertThat(ClassHelper.convertPrimitive(char.class, null), Matchers.equalTo(null));
        MatcherAssert.assertThat(ClassHelper.convertPrimitive(char.class, "6"), Matchers.equalTo('6'));
        MatcherAssert.assertThat(ClassHelper.convertPrimitive(boolean.class, ""), Matchers.equalTo(Boolean.FALSE));
        MatcherAssert.assertThat(ClassHelper.convertPrimitive(boolean.class, null), Matchers.equalTo(null));
        MatcherAssert.assertThat(ClassHelper.convertPrimitive(boolean.class, "true"), Matchers.equalTo(Boolean.TRUE));
        MatcherAssert.assertThat(ClassHelper.convertPrimitive(byte.class, ""), Matchers.equalTo(null));
        MatcherAssert.assertThat(ClassHelper.convertPrimitive(byte.class, null), Matchers.equalTo(null));
        MatcherAssert.assertThat(ClassHelper.convertPrimitive(byte.class, "127"), Matchers.equalTo(Byte.MAX_VALUE));
        MatcherAssert.assertThat(ClassHelper.convertPrimitive(short.class, ""), Matchers.equalTo(null));
        MatcherAssert.assertThat(ClassHelper.convertPrimitive(short.class, null), Matchers.equalTo(null));
        MatcherAssert.assertThat(ClassHelper.convertPrimitive(short.class, "32767"), Matchers.equalTo(Short.MAX_VALUE));
        MatcherAssert.assertThat(ClassHelper.convertPrimitive(int.class, ""), Matchers.equalTo(null));
        MatcherAssert.assertThat(ClassHelper.convertPrimitive(int.class, null), Matchers.equalTo(null));
        MatcherAssert.assertThat(ClassHelper.convertPrimitive(int.class, "6"), Matchers.equalTo(6));
        MatcherAssert.assertThat(ClassHelper.convertPrimitive(long.class, ""), Matchers.equalTo(null));
        MatcherAssert.assertThat(ClassHelper.convertPrimitive(long.class, null), Matchers.equalTo(null));
        MatcherAssert.assertThat(ClassHelper.convertPrimitive(long.class, "6"), Matchers.equalTo(new Long(6)));
        MatcherAssert.assertThat(ClassHelper.convertPrimitive(float.class, ""), Matchers.equalTo(null));
        MatcherAssert.assertThat(ClassHelper.convertPrimitive(float.class, null), Matchers.equalTo(null));
        MatcherAssert.assertThat(ClassHelper.convertPrimitive(float.class, "1.1"), Matchers.equalTo(new Float(1.1)));
        MatcherAssert.assertThat(ClassHelper.convertPrimitive(double.class, ""), Matchers.equalTo(null));
        MatcherAssert.assertThat(ClassHelper.convertPrimitive(double.class, null), Matchers.equalTo(null));
        MatcherAssert.assertThat(ClassHelper.convertPrimitive(double.class, "10.1"), Matchers.equalTo(new Double(10.1)));
    }
}

