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
package org.apache.hadoop.util;


import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.List;
import org.apache.hadoop.test.GenericTestUtils;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.apache.hadoop.test.GenericTestUtils.LogCapturer.captureLogs;


public class TestReflectionUtils {
    private static Class[] toConstruct = new Class[]{ String.class, TestReflectionUtils.class, HashMap.class };

    private Throwable failure = null;

    @Test
    public void testCache() throws Exception {
        Assert.assertEquals(0, cacheSize());
        doTestCache();
        Assert.assertEquals(TestReflectionUtils.toConstruct.length, cacheSize());
        ReflectionUtils.clearCache();
        Assert.assertEquals(0, cacheSize());
    }

    @Test
    public void testThreadSafe() throws Exception {
        Thread[] th = new Thread[32];
        for (int i = 0; i < (th.length); i++) {
            th[i] = new Thread() {
                @Override
                public void run() {
                    try {
                        doTestCache();
                    } catch (Throwable t) {
                        failure = t;
                    }
                }
            };
            th[i].start();
        }
        for (int i = 0; i < (th.length); i++) {
            th[i].join();
        }
        if ((failure) != null) {
            failure.printStackTrace();
            Assert.fail(failure.getMessage());
        }
    }

    @Test
    public void testCantCreate() {
        try {
            ReflectionUtils.newInstance(TestReflectionUtils.NoDefaultCtor.class, null);
            Assert.fail("invalid call should fail");
        } catch (RuntimeException rte) {
            Assert.assertEquals(NoSuchMethodException.class, rte.getCause().getClass());
        }
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testCacheDoesntLeak() throws Exception {
        int iterations = 9999;// very fast, but a bit less reliable - bigger numbers force GC

        for (int i = 0; i < iterations; i++) {
            URLClassLoader loader = new URLClassLoader(new URL[0], getClass().getClassLoader());
            Class cl = Class.forName("org.apache.hadoop.util.TestReflectionUtils$LoadedInChild", false, loader);
            Object o = ReflectionUtils.newInstance(cl, null);
            Assert.assertEquals(cl, o.getClass());
        }
        System.gc();
        Assert.assertTrue(((cacheSize()) + " too big"), ((cacheSize()) < iterations));
    }

    @Test
    public void testGetDeclaredFieldsIncludingInherited() {
        TestReflectionUtils.Parent child = new TestReflectionUtils.Parent() {
            private int childField;

            @SuppressWarnings("unused")
            public int getChildField() {
                return childField;
            }
        };
        List<Field> fields = ReflectionUtils.getDeclaredFieldsIncludingInherited(child.getClass());
        boolean containsParentField = false;
        boolean containsChildField = false;
        for (Field field : fields) {
            if (field.getName().equals("parentField")) {
                containsParentField = true;
            } else
                if (field.getName().equals("childField")) {
                    containsChildField = true;
                }

        }
        List<Method> methods = ReflectionUtils.getDeclaredMethodsIncludingInherited(child.getClass());
        boolean containsParentMethod = false;
        boolean containsChildMethod = false;
        for (Method method : methods) {
            if (method.getName().equals("getParentField")) {
                containsParentMethod = true;
            } else
                if (method.getName().equals("getChildField")) {
                    containsChildMethod = true;
                }

        }
        Assert.assertTrue("Missing parent field", containsParentField);
        Assert.assertTrue("Missing child field", containsChildField);
        Assert.assertTrue("Missing parent method", containsParentMethod);
        Assert.assertTrue("Missing child method", containsChildMethod);
    }

    @Test
    public void testLogThreadInfo() throws Exception {
        Logger logger = LoggerFactory.getLogger(TestReflectionUtils.class);
        GenericTestUtils.LogCapturer logCapturer = captureLogs(logger);
        final String title = "title";
        ReflectionUtils.logThreadInfo(logger, title, 0L);
        Assert.assertThat(logCapturer.getOutput(), CoreMatchers.containsString(("Process Thread Dump: " + title)));
    }

    // Used for testGetDeclaredFieldsIncludingInherited
    private class Parent {
        private int parentField;

        @SuppressWarnings("unused")
        public int getParentField() {
            return parentField;
        }
    }

    private static class LoadedInChild {}

    public static class NoDefaultCtor {
        public NoDefaultCtor(int x) {
        }
    }
}

