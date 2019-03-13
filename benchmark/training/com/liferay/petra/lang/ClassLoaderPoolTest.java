/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */
package com.liferay.petra.lang;


import com.liferay.portal.kernel.test.rule.CodeCoverageAssertor;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Map;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;


/**
 *
 *
 * @author Shuyang Zhou
 */
public class ClassLoaderPoolTest {
    @ClassRule
    public static final CodeCoverageAssertor codeCoverageAssertor = CodeCoverageAssertor.INSTANCE;

    @Test
    public void testConstructor() {
        new ClassLoaderPool();
    }

    @Test
    public void testGetClassLoaderWithInvalidContextName() {
        ClassLoader classLoader = new URLClassLoader(new URL[0]);
        ClassLoaderPool.register(ClassLoaderPoolTest._CONTEXT_NAME, classLoader);
        Thread currentThread = Thread.currentThread();
        ClassLoader contextClassLoader = currentThread.getContextClassLoader();
        Assert.assertSame(contextClassLoader, ClassLoaderPool.getClassLoader("null"));
        Assert.assertSame(contextClassLoader, ClassLoaderPool.getClassLoader(null));
    }

    @Test
    public void testGetClassLoaderWithValidContextName() {
        ClassLoader classLoader = new URLClassLoader(new URL[0]);
        ClassLoaderPool.register(ClassLoaderPoolTest._CONTEXT_NAME, classLoader);
        Assert.assertSame(classLoader, ClassLoaderPool.getClassLoader(ClassLoaderPoolTest._CONTEXT_NAME));
    }

    @Test
    public void testGetContextNameWithInvalidClassLoader() {
        ClassLoader classLoader = new URLClassLoader(new URL[0]);
        ClassLoaderPool.register(ClassLoaderPoolTest._CONTEXT_NAME, classLoader);
        Assert.assertEquals("null", ClassLoaderPool.getContextName(new URLClassLoader(new URL[0])));
        Assert.assertEquals("null", ClassLoaderPool.getContextName(null));
    }

    @Test
    public void testGetContextNameWithValidClassLoader() {
        ClassLoader classLoader = new URLClassLoader(new URL[0]);
        ClassLoaderPool.register(ClassLoaderPoolTest._CONTEXT_NAME, classLoader);
        Assert.assertEquals(ClassLoaderPoolTest._CONTEXT_NAME, ClassLoaderPool.getContextName(classLoader));
    }

    @Test
    public void testRegister() {
        ClassLoader classLoader = new URLClassLoader(new URL[0]);
        ClassLoaderPool.register(ClassLoaderPoolTest._CONTEXT_NAME, classLoader);
        Assert.assertEquals(ClassLoaderPoolTest._contextNames.toString(), 1, ClassLoaderPoolTest._contextNames.size());
        Assert.assertEquals(ClassLoaderPoolTest._classLoaders.toString(), 1, ClassLoaderPoolTest._classLoaders.size());
        Assert.assertSame(classLoader, ClassLoaderPoolTest._classLoaders.get(ClassLoaderPoolTest._CONTEXT_NAME));
        Assert.assertEquals(ClassLoaderPoolTest._CONTEXT_NAME, ClassLoaderPoolTest._contextNames.get(classLoader));
    }

    @Test(expected = NullPointerException.class)
    public void testRegisterWithNullClassLoader() {
        ClassLoaderPool.register("null", null);
    }

    @Test(expected = NullPointerException.class)
    public void testRegisterWithNullContextName() {
        ClassLoaderPool.register(null, null);
    }

    @Test
    public void testUnregisterWithInvalidClassLoader() {
        ClassLoaderPool.unregister(new URLClassLoader(new URL[0]));
        _assertEmptyMaps();
    }

    @Test
    public void testUnregisterWithInvalidContextName() {
        ClassLoaderPool.unregister(ClassLoaderPoolTest._CONTEXT_NAME);
        _assertEmptyMaps();
    }

    @Test
    public void testUnregisterWithValidClassLoader() {
        ClassLoader classLoader = new URLClassLoader(new URL[0]);
        ClassLoaderPool.register(ClassLoaderPoolTest._CONTEXT_NAME, classLoader);
        ClassLoaderPool.unregister(classLoader);
        _assertEmptyMaps();
    }

    @Test
    public void testUnregisterWithValidContextName() {
        ClassLoader classLoader = new URLClassLoader(new URL[0]);
        ClassLoaderPool.register(ClassLoaderPoolTest._CONTEXT_NAME, classLoader);
        ClassLoaderPool.unregister(ClassLoaderPoolTest._CONTEXT_NAME);
        _assertEmptyMaps();
    }

    private static final String _CONTEXT_NAME = "contextName";

    private static Map<String, ClassLoader> _classLoaders;

    private static Map<ClassLoader, String> _contextNames;
}

