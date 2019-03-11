/**
 * Copyright 1999-2018 Alibaba Group Holding Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alibaba.csp.sentinel;


import EntryType.IN;
import EntryType.OUT;
import com.alibaba.csp.sentinel.context.ContextUtil;
import com.alibaba.csp.sentinel.util.StringUtil;
import java.lang.reflect.Method;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test cases for {@link SphO}.
 *
 * @author jialiang.linjl
 */
public class SphOTest {
    @Test
    public void testStringEntryNormal() {
        if (SphO.entry("resourceName")) {
            try {
                Assert.assertTrue(StringUtil.equalsIgnoreCase(ContextUtil.getContext().getCurEntry().getResourceWrapper().getName(), "resourceName"));
            } finally {
                SphO.exit();
            }
        }
    }

    @Test
    public void testMethodEntryNormal() throws NoSuchMethodException, SecurityException {
        Method method = SphOTest.class.getMethod("testMethodEntryNormal");
        if (SphO.entry(method)) {
            try {
                Assert.assertTrue(StringUtil.equalsIgnoreCase(ContextUtil.getContext().getCurEntry().getResourceWrapper().getName(), "com.alibaba.csp.sentinel.SphOTest:testMethodEntryNormal()"));
            } finally {
                SphO.exit();
            }
        }
    }

    @Test
    public void testStringEntryCount() {
        if (SphO.entry("resourceName", 2)) {
            try {
                Assert.assertTrue(StringUtil.equalsIgnoreCase(ContextUtil.getContext().getCurEntry().getResourceWrapper().getName(), "resourceName"));
                Assert.assertSame(ContextUtil.getContext().getCurEntry().getResourceWrapper().getType(), OUT);
            } finally {
                SphO.exit(2);
            }
        }
    }

    @Test
    public void testMethodEntryCount() throws NoSuchMethodException, SecurityException {
        Method method = SphOTest.class.getMethod("testMethodEntryCount");
        if (SphO.entry(method, 2)) {
            try {
                Assert.assertTrue(StringUtil.equalsIgnoreCase(ContextUtil.getContext().getCurEntry().getResourceWrapper().getName(), "com.alibaba.csp.sentinel.SphOTest:testMethodEntryCount()"));
                Assert.assertSame(ContextUtil.getContext().getCurEntry().getResourceWrapper().getType(), OUT);
            } finally {
                SphO.exit(2);
            }
        }
    }

    @Test
    public void testStringEntryType() {
        if (SphO.entry("resourceName", IN)) {
            try {
                Assert.assertTrue(StringUtil.equalsIgnoreCase(ContextUtil.getContext().getCurEntry().getResourceWrapper().getName(), "resourceName"));
                Assert.assertSame(ContextUtil.getContext().getCurEntry().getResourceWrapper().getType(), IN);
            } finally {
                SphO.exit();
            }
        }
    }

    @Test
    public void testMethodEntryType() throws NoSuchMethodException, SecurityException {
        Method method = SphOTest.class.getMethod("testMethodEntryType");
        if (SphO.entry(method, IN)) {
            try {
                Assert.assertTrue(StringUtil.equalsIgnoreCase(ContextUtil.getContext().getCurEntry().getResourceWrapper().getName(), "com.alibaba.csp.sentinel.SphOTest:testMethodEntryType()"));
                Assert.assertSame(ContextUtil.getContext().getCurEntry().getResourceWrapper().getType(), IN);
            } finally {
                SphO.exit();
            }
        }
    }

    @Test
    public void testStringEntryTypeCount() {
        if (SphO.entry("resourceName", IN, 2)) {
            try {
                Assert.assertTrue(StringUtil.equalsIgnoreCase(ContextUtil.getContext().getCurEntry().getResourceWrapper().getName(), "resourceName"));
                Assert.assertSame(ContextUtil.getContext().getCurEntry().getResourceWrapper().getType(), IN);
            } finally {
                SphO.exit(2);
            }
        }
    }

    @Test
    public void testMethodEntryTypeCount() throws NoSuchMethodException, SecurityException {
        Method method = SphOTest.class.getMethod("testMethodEntryTypeCount");
        if (SphO.entry(method, IN, 2)) {
            try {
                Assert.assertTrue(StringUtil.equalsIgnoreCase(ContextUtil.getContext().getCurEntry().getResourceWrapper().getName(), "com.alibaba.csp.sentinel.SphOTest:testMethodEntryTypeCount()"));
                Assert.assertSame(ContextUtil.getContext().getCurEntry().getResourceWrapper().getType(), IN);
            } finally {
                SphO.exit(2);
            }
        }
    }

    @Test
    public void testStringEntryAll() {
        if (SphO.entry("resourceName", IN, 2, "hello1", "hello2")) {
            try {
                Assert.assertTrue(StringUtil.equalsIgnoreCase(ContextUtil.getContext().getCurEntry().getResourceWrapper().getName(), "resourceName"));
                Assert.assertSame(ContextUtil.getContext().getCurEntry().getResourceWrapper().getType(), IN);
            } finally {
                SphO.exit(2, "hello1", "hello2");
            }
        }
    }

    @Test
    public void testMethodEntryAll() throws NoSuchMethodException, SecurityException {
        Method method = SphOTest.class.getMethod("testMethodEntryAll");
        if (SphO.entry(method, IN, 2, "hello1", "hello2")) {
            try {
                Assert.assertTrue(StringUtil.equalsIgnoreCase(ContextUtil.getContext().getCurEntry().getResourceWrapper().getName(), "com.alibaba.csp.sentinel.SphOTest:testMethodEntryAll()"));
                Assert.assertSame(ContextUtil.getContext().getCurEntry().getResourceWrapper().getType(), IN);
            } finally {
                SphO.exit(2, "hello1", "hello2");
            }
        }
    }
}

