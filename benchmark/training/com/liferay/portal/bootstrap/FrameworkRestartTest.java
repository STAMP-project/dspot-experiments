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
package com.liferay.portal.bootstrap;


import NewEnv.Type;
import com.liferay.portal.kernel.io.unsync.UnsyncByteArrayOutputStream;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.NewEnv;
import com.liferay.portal.kernel.test.rule.NewEnvTestRule;
import java.net.URLClassLoader;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.osgi.framework.launch.FrameworkFactory;


/**
 *
 *
 * @author Matthew Tambara
 */
public class FrameworkRestartTest {
    @NewEnv(type = Type.JVM)
    @Test
    public void testFrameworkRestart() throws Exception {
        ClassLoader classLoader = new URLClassLoader(_getURLS(Assert.class, FrameworkFactory.class, FrameworkRestartTest.class, UnsyncByteArrayOutputStream.class), null);
        Class<?> clazz = classLoader.loadClass(FrameworkRestartTest.class.getName());
        ReflectionTestUtil.invoke(clazz, "doTestFrameworkRestart", new Class<?>[0]);
    }

    @Rule
    public final NewEnvTestRule newEnvTestRule = NewEnvTestRule.INSTANCE;

    private static final String _TEST_EXPORT = "com.test.liferay.export";
}

