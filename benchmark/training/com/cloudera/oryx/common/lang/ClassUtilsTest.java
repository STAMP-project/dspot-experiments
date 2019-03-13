/**
 * Copyright (c) 2014, Cloudera, Inc. All Rights Reserved.
 *
 * Cloudera, Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"). You may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * This software is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied. See the License for
 * the specific language governing permissions and limitations under the
 * License.
 */
package com.cloudera.oryx.common.lang;


import com.cloudera.oryx.common.OryxTest;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests {@link ClassUtils}.
 */
public final class ClassUtilsTest extends OryxTest {
    @Test
    public void testLoadClass() {
        Assert.assertSame(ArrayList.class, ClassUtils.loadClass(ArrayList.class.getName()));
    }

    @Test
    public void testLoadClass2() {
        Assert.assertSame(ArrayList.class, ClassUtils.loadClass(ArrayList.class.getName(), List.class));
    }

    @Test(expected = IllegalStateException.class)
    public void testLoadNonExistentClass() {
        ClassUtils.loadClass("foobar");
    }

    @Test(expected = IllegalStateException.class)
    public void testLoadNonExistentClass2() {
        ClassUtils.loadClass("foobar", Number.class);
    }

    @Test
    public void testLoadInstanceOf() {
        OryxTest.assertInstanceOf(ClassUtils.loadInstanceOf(HashSet.class), HashSet.class);
    }

    @Test
    public void testLoadInstanceOf2() {
        OryxTest.assertInstanceOf(ClassUtils.loadInstanceOf(HashSet.class.getName(), Set.class), HashSet.class);
    }

    @Test
    public void testInstantiateWithArgs() {
        Number n = ClassUtils.loadInstanceOf(Integer.class.getName(), Number.class, new Class<?>[]{ int.class }, new Object[]{ 3 });
        Assert.assertEquals(3, n.intValue());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNoSuchMethod() {
        ClassUtils.loadInstanceOf(Long.class.getName(), Long.class);
    }

    @Test(expected = IllegalStateException.class)
    public void tesInvocationException() {
        ClassUtils.loadInstanceOf(String.class.getName(), String.class, new Class<?>[]{ char[].class }, new Object[]{ null });
    }

    @Test
    public void testExists() {
        Assert.assertTrue(ClassUtils.classExists("java.lang.String"));
        Assert.assertTrue(ClassUtils.classExists("com.cloudera.oryx.common.lang.ClassUtils"));
        Assert.assertFalse(ClassUtils.classExists("java.Foo"));
    }

    @Test
    public void testNoContextCL() {
        Thread current = Thread.currentThread();
        ClassLoader savedCL = current.getContextClassLoader();
        current.setContextClassLoader(null);
        try {
            Assert.assertTrue(ClassUtils.classExists(ClassUtilsTest.class.getName()));
        } finally {
            current.setContextClassLoader(savedCL);
        }
    }
}

