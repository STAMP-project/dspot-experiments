/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.beam.sdk.util.common;


import ReflectHelpers.ANNOTATION_FORMATTER;
import ReflectHelpers.CLASS_AND_METHOD_FORMATTER;
import ReflectHelpers.CLASS_NAME;
import ReflectHelpers.CLASS_SIMPLE_NAME;
import ReflectHelpers.METHOD_FORMATTER;
import ReflectHelpers.TYPE_SIMPLE_DESCRIPTION;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Map;
import org.apache.beam.sdk.options.Default;
import org.apache.beam.sdk.options.PipelineOptions;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests for {@link ReflectHelpers}.
 */
@RunWith(JUnit4.class)
public class ReflectHelpersTest {
    @Test
    public void testClassName() {
        Assert.assertEquals(getClass().getName(), CLASS_NAME.apply(getClass()));
    }

    @Test
    public void testClassSimpleName() {
        Assert.assertEquals(getClass().getSimpleName(), CLASS_SIMPLE_NAME.apply(getClass()));
    }

    @Test
    public void testMethodFormatter() throws Exception {
        Assert.assertEquals("testMethodFormatter()", METHOD_FORMATTER.apply(getClass().getMethod("testMethodFormatter")));
        Assert.assertEquals("oneArg(int)", METHOD_FORMATTER.apply(getClass().getDeclaredMethod("oneArg", int.class)));
        Assert.assertEquals("twoArg(String, List)", METHOD_FORMATTER.apply(getClass().getDeclaredMethod("twoArg", String.class, List.class)));
    }

    @Test
    public void testClassMethodFormatter() throws Exception {
        Assert.assertEquals(((getClass().getName()) + "#testMethodFormatter()"), CLASS_AND_METHOD_FORMATTER.apply(getClass().getMethod("testMethodFormatter")));
        Assert.assertEquals(((getClass().getName()) + "#oneArg(int)"), CLASS_AND_METHOD_FORMATTER.apply(getClass().getDeclaredMethod("oneArg", int.class)));
        Assert.assertEquals(((getClass().getName()) + "#twoArg(String, List)"), CLASS_AND_METHOD_FORMATTER.apply(getClass().getDeclaredMethod("twoArg", String.class, List.class)));
    }

    @Test
    public void testTypeFormatterOnClasses() throws Exception {
        Assert.assertEquals("Integer", TYPE_SIMPLE_DESCRIPTION.apply(Integer.class));
        Assert.assertEquals("int", TYPE_SIMPLE_DESCRIPTION.apply(int.class));
        Assert.assertEquals("Map", TYPE_SIMPLE_DESCRIPTION.apply(Map.class));
        Assert.assertEquals(getClass().getSimpleName(), TYPE_SIMPLE_DESCRIPTION.apply(getClass()));
    }

    @Test
    public void testTypeFormatterOnArrays() throws Exception {
        Assert.assertEquals("Integer[]", TYPE_SIMPLE_DESCRIPTION.apply(Integer[].class));
        Assert.assertEquals("int[]", TYPE_SIMPLE_DESCRIPTION.apply(int[].class));
    }

    @Test
    public void testTypeFormatterWithGenerics() throws Exception {
        Assert.assertEquals("Map<Integer, String>", TYPE_SIMPLE_DESCRIPTION.apply(getType()));
        Assert.assertEquals("Map<?, String>", TYPE_SIMPLE_DESCRIPTION.apply(getType()));
        Assert.assertEquals("Map<? extends Integer, String>", TYPE_SIMPLE_DESCRIPTION.apply(getType()));
    }

    @Test
    public <T> void testTypeFormatterWithWildcards() throws Exception {
        Assert.assertEquals("Map<T, T>", TYPE_SIMPLE_DESCRIPTION.apply(getType()));
    }

    @Test
    public <InputT, OutputT> void testTypeFormatterWithMultipleWildcards() throws Exception {
        Assert.assertEquals("Map<? super InputT, ? extends OutputT>", TYPE_SIMPLE_DESCRIPTION.apply(getType()));
    }

    /**
     * Test interface.
     */
    public interface Options extends PipelineOptions {
        @Default.String("package.OuterClass$InnerClass#method()")
        String getString();

        @JsonIgnore
        Object getObject();
    }

    @Test
    public void testAnnotationFormatter() throws Exception {
        Assert.assertEquals("Default.String(value=package.OuterClass$InnerClass#method())", ANNOTATION_FORMATTER.apply(ReflectHelpersTest.Options.class.getMethod("getString").getAnnotations()[0]));
        Assert.assertEquals("JsonIgnore(value=true)", ANNOTATION_FORMATTER.apply(ReflectHelpersTest.Options.class.getMethod("getObject").getAnnotations()[0]));
    }

    @Test
    public void testFindProperClassLoaderIfContextClassLoaderIsNull() throws InterruptedException {
        final ClassLoader[] classLoader = new ClassLoader[1];
        Thread thread = new Thread(() -> classLoader[0] = ReflectHelpers.findClassLoader());
        thread.setContextClassLoader(null);
        thread.start();
        thread.join();
        Assert.assertEquals(ReflectHelpers.class.getClassLoader(), classLoader[0]);
    }

    @Test
    public void testFindProperClassLoaderIfContextClassLoaderIsAvailable() throws InterruptedException {
        final ClassLoader[] classLoader = new ClassLoader[1];
        Thread thread = new Thread(() -> classLoader[0] = ReflectHelpers.findClassLoader());
        ClassLoader cl = new ClassLoader() {};
        thread.setContextClassLoader(cl);
        thread.start();
        thread.join();
        Assert.assertEquals(cl, classLoader[0]);
    }
}

