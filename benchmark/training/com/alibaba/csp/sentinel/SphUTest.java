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


import Constants.CONTEXT_DEFAULT_NAME;
import EntryType.IN;
import EntryType.OUT;
import com.alibaba.csp.sentinel.context.ContextUtil;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.csp.sentinel.util.StringUtil;
import java.lang.reflect.Method;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test cases for {@link SphU}.
 *
 * @author jialiang.linjl
 */
public class SphUTest {
    @Test
    public void testStringEntryNormal() throws BlockException {
        Entry e = SphU.entry("resourceName");
        Assert.assertNotNull(e);
        Assert.assertEquals(e.resourceWrapper.getName(), "resourceName");
        Assert.assertEquals(e.resourceWrapper.getType(), OUT);
        Assert.assertEquals(ContextUtil.getContext().getName(), CONTEXT_DEFAULT_NAME);
        e.exit();
    }

    @Test
    public void testMethodEntryNormal() throws BlockException, NoSuchMethodException, SecurityException {
        Method method = SphUTest.class.getMethod("testMethodEntryNormal");
        Entry e = SphU.entry(method);
        Assert.assertNotNull(e);
        Assert.assertTrue(StringUtil.equalsIgnoreCase(e.resourceWrapper.getName(), "com.alibaba.csp.sentinel.SphUTest:testMethodEntryNormal()"));
        Assert.assertEquals(e.resourceWrapper.getType(), OUT);
        Assert.assertEquals(ContextUtil.getContext().getName(), CONTEXT_DEFAULT_NAME);
        e.exit();
    }

    @Test(expected = ErrorEntryFreeException.class)
    public void testStringEntryNotPairedException() throws BlockException {
        Entry e = SphU.entry("resourceName");
        Entry e1 = SphU.entry("resourceName");
        if (e != null) {
            e.exit();
        }
        if (e1 != null) {
            e1.exit();
        }
    }

    @Test
    public void testStringEntryCount() throws BlockException {
        Entry e = SphU.entry("resourceName", 2);
        Assert.assertNotNull(e);
        Assert.assertEquals("resourceName", e.resourceWrapper.getName());
        Assert.assertEquals(e.resourceWrapper.getType(), OUT);
        Assert.assertEquals(ContextUtil.getContext().getName(), CONTEXT_DEFAULT_NAME);
        e.exit(2);
    }

    @Test
    public void testMethodEntryCount() throws BlockException, NoSuchMethodException, SecurityException {
        Method method = SphUTest.class.getMethod("testMethodEntryNormal");
        Entry e = SphU.entry(method, 2);
        Assert.assertNotNull(e);
        Assert.assertTrue(StringUtil.equalsIgnoreCase(e.resourceWrapper.getName(), "com.alibaba.csp.sentinel.SphUTest:testMethodEntryNormal()"));
        Assert.assertEquals(e.resourceWrapper.getType(), OUT);
        e.exit(2);
    }

    @Test
    public void testStringEntryType() throws BlockException {
        Entry e = SphU.entry("resourceName", IN);
        Assert.assertSame(e.resourceWrapper.getType(), IN);
        e.exit();
    }

    @Test
    public void testMethodEntryType() throws BlockException, NoSuchMethodException, SecurityException {
        Method method = SphUTest.class.getMethod("testMethodEntryNormal");
        Entry e = SphU.entry(method, IN);
        Assert.assertSame(e.resourceWrapper.getType(), IN);
        e.exit();
    }

    @Test
    public void testStringEntryCountType() throws BlockException {
        Entry e = SphU.entry("resourceName", IN, 2);
        Assert.assertSame(e.resourceWrapper.getType(), IN);
        e.exit(2);
    }

    @Test
    public void testMethodEntryCountType() throws BlockException, NoSuchMethodException, SecurityException {
        Method method = SphUTest.class.getMethod("testMethodEntryNormal");
        Entry e = SphU.entry(method, IN, 2);
        Assert.assertSame(e.resourceWrapper.getType(), IN);
        e.exit();
    }

    @Test
    public void testStringEntryAll() throws BlockException {
        final String arg0 = "foo";
        final String arg1 = "baz";
        Entry e = SphU.entry("resourceName", IN, 2, arg0, arg1);
        Assert.assertSame(e.resourceWrapper.getType(), IN);
        e.exit(2, arg0, arg1);
    }

    @Test
    public void testMethodEntryAll() throws BlockException, NoSuchMethodException, SecurityException {
        final String arg0 = "foo";
        final String arg1 = "baz";
        Method method = SphUTest.class.getMethod("testMethodEntryNormal");
        Entry e = SphU.entry(method, IN, 2, arg0, arg1);
        Assert.assertSame(e.resourceWrapper.getType(), IN);
        e.exit(2, arg0, arg1);
    }
}

