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
package com.liferay.portal.cluster.multiple.internal.io;


import StringPool.DASH;
import StringPool.NULL;
import com.liferay.petra.lang.ClassLoaderPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.test.CaptureHandler;
import com.liferay.portal.kernel.test.JDKLoggerTestUtil;
import com.liferay.portal.kernel.test.rule.CodeCoverageAssertor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentNavigableMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.FutureTask;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.osgi.framework.Version;


/**
 *
 *
 * @author Lance Ji
 */
public class ClusterClassLoaderPoolTest {
    @ClassRule
    public static final CodeCoverageAssertor codeCoverageAssertor = CodeCoverageAssertor.INSTANCE;

    @Test
    public void testConcurrentRegister() throws Exception {
        ClassLoader classLoader1 = new URLClassLoader(new URL[0]);
        ClassLoader classLoader2 = new URLClassLoader(new URL[0]);
        ClusterClassLoaderPool.registerFallback(ClusterClassLoaderPoolTest._SYMBOLIC_NAME, new Version("1.0.0"), classLoader1);
        Assert.assertSame(classLoader1, ClusterClassLoaderPool.getClassLoader(ClusterClassLoaderPoolTest._CONTEXT_NAME_1));
        Assert.assertSame(classLoader1, ClusterClassLoaderPool.getClassLoader(ClusterClassLoaderPoolTest._CONTEXT_NAME_2));
        ClusterClassLoaderPoolTest.BlockingInvocationHandler blockingInvocationHandler = _block();
        FutureTask<Void> futureTask = new FutureTask<>(() -> {
            Assert.assertSame(classLoader2, ClusterClassLoaderPool.getClassLoader(ClusterClassLoaderPoolTest._CONTEXT_NAME_1));
            Assert.assertSame(classLoader2, ClusterClassLoaderPool.getClassLoader(ClusterClassLoaderPoolTest._CONTEXT_NAME_2));
            return null;
        });
        Thread thread = new Thread(futureTask, StringBundler.concat(ClusterClassLoaderPoolTest.class.getName(), DASH, "testConcurrentRegister"));
        thread.start();
        blockingInvocationHandler.waitUntilBlock();
        ClusterClassLoaderPool.registerFallback(ClusterClassLoaderPoolTest._SYMBOLIC_NAME, new Version("2.0.0"), classLoader2);
        blockingInvocationHandler.unblock();
        futureTask.get();
    }

    @Test
    public void testConcurrentUnregister() throws Exception {
        ClassLoader classLoader = new URLClassLoader(new URL[0]);
        ClusterClassLoaderPool.registerFallback(ClusterClassLoaderPoolTest._SYMBOLIC_NAME, new Version("1.0.0"), classLoader);
        Assert.assertSame(classLoader, ClusterClassLoaderPool.getClassLoader(ClusterClassLoaderPoolTest._CONTEXT_NAME_1));
        Assert.assertSame(classLoader, ClusterClassLoaderPool.getClassLoader(ClusterClassLoaderPoolTest._CONTEXT_NAME_2));
        ClusterClassLoaderPoolTest.BlockingInvocationHandler blockingInvocationHandler = _block();
        FutureTask<Void> futureTask = new FutureTask<>(() -> {
            Assert.assertSame(_contextClassLoader, ClusterClassLoaderPool.getClassLoader(ClusterClassLoaderPoolTest._CONTEXT_NAME_1));
            Assert.assertSame(_contextClassLoader, ClusterClassLoaderPool.getClassLoader(ClusterClassLoaderPoolTest._CONTEXT_NAME_2));
            return null;
        });
        Thread thread = new Thread(futureTask, StringBundler.concat(ClusterClassLoaderPoolTest.class.getName(), DASH, "testConcurrentUnregister"));
        thread.start();
        blockingInvocationHandler.waitUntilBlock();
        ClusterClassLoaderPool.unregisterFallback(ClusterClassLoaderPoolTest._SYMBOLIC_NAME, new Version("1.0.0"));
        blockingInvocationHandler.unblock();
        futureTask.get();
    }

    @Test
    public void testConstructor() {
        new ClusterClassLoaderPool();
    }

    @Test
    public void testGetClassLoader() {
        ClassLoader classLoader1 = new URLClassLoader(new URL[0]);
        ClassLoader classLoader2 = new URLClassLoader(new URL[0]);
        ClassLoaderPool.register(ClusterClassLoaderPoolTest._CONTEXT_NAME_1, classLoader1);
        ClassLoaderPool.register(ClusterClassLoaderPoolTest._CONTEXT_NAME_2, classLoader2);
        ClusterClassLoaderPool.registerFallback(ClusterClassLoaderPoolTest._SYMBOLIC_NAME, new Version("1.0.0"), classLoader1);
        ClusterClassLoaderPool.registerFallback(ClusterClassLoaderPoolTest._SYMBOLIC_NAME, new Version("2.0.0"), classLoader2);
        Assert.assertSame(classLoader1, ClusterClassLoaderPool.getClassLoader(ClusterClassLoaderPoolTest._CONTEXT_NAME_1));
        Assert.assertSame(classLoader2, ClusterClassLoaderPool.getClassLoader(ClusterClassLoaderPoolTest._CONTEXT_NAME_2));
        Assert.assertSame(classLoader2, ClusterClassLoaderPool.getClassLoader(ClusterClassLoaderPoolTest._CONTEXT_NAME_3));
        Assert.assertSame(_contextClassLoader, ClusterClassLoaderPool.getClassLoader(ClusterClassLoaderPoolTest._SYMBOLIC_NAME));
        Assert.assertSame(_contextClassLoader, ClusterClassLoaderPool.getClassLoader(null));
        Assert.assertSame(_contextClassLoader, ClusterClassLoaderPool.getClassLoader(NULL));
        try (CaptureHandler captureHandler = JDKLoggerTestUtil.configureJDKLogger(ClusterClassLoaderPool.class.getName(), Level.WARNING)) {
            // Test 1, log level is WARNING
            List<LogRecord> logRecords = captureHandler.getLogRecords();
            Assert.assertSame(classLoader2, ClusterClassLoaderPool.getClassLoader(ClusterClassLoaderPoolTest._CONTEXT_NAME_3));
            Assert.assertEquals(logRecords.toString(), 1, logRecords.size());
            LogRecord logRecord = logRecords.get(0);
            Assert.assertEquals((((("Unable to find class loader for " + (ClusterClassLoaderPoolTest._CONTEXT_NAME_3)) + ", class loader ") + (ClusterClassLoaderPoolTest._CONTEXT_NAME_2)) + " is provided instead"), logRecord.getMessage());
            // Test 2, log level is OFF
            logRecords = captureHandler.resetLogLevel(Level.OFF);
            Assert.assertSame(classLoader2, ClusterClassLoaderPool.getClassLoader(ClusterClassLoaderPoolTest._CONTEXT_NAME_3));
            Assert.assertTrue(logRecords.toString(), logRecords.isEmpty());
        }
    }

    @Test
    public void testGetClassLoaderDebug() {
        try (CaptureHandler captureHandler = JDKLoggerTestUtil.configureJDKLogger(ClusterClassLoaderPool.class.getName(), Level.INFO)) {
            // Test 1, log level is INFO
            List<LogRecord> logRecords = captureHandler.getLogRecords();
            Assert.assertSame(_contextClassLoader, ClusterClassLoaderPool.getClassLoader(ClusterClassLoaderPoolTest._CONTEXT_NAME_1));
            Assert.assertTrue(logRecords.toString(), logRecords.isEmpty());
            // Test 2, log level is FINE
            logRecords = captureHandler.resetLogLevel(Level.FINE);
            Assert.assertSame(_contextClassLoader, ClusterClassLoaderPool.getClassLoader(ClusterClassLoaderPoolTest._CONTEXT_NAME_1));
            Assert.assertEquals(logRecords.toString(), 1, logRecords.size());
            LogRecord logRecord = logRecords.get(0);
            Assert.assertEquals((("Unable to find class loader for " + (ClusterClassLoaderPoolTest._CONTEXT_NAME_1)) + ", fall back to current thread's context class loader"), logRecord.getMessage());
        }
    }

    @Test
    public void testGetContextName() {
        ClassLoader classLoader = new URLClassLoader(new URL[0]);
        ClassLoaderPool.register(ClusterClassLoaderPoolTest._CONTEXT_NAME_1, classLoader);
        ClusterClassLoaderPool.registerFallback(ClusterClassLoaderPoolTest._SYMBOLIC_NAME, new Version("1.0.0"), classLoader);
        Assert.assertEquals(NULL, ClusterClassLoaderPool.getContextName(null));
        Assert.assertEquals(ClusterClassLoaderPoolTest._CONTEXT_NAME_1, ClusterClassLoaderPool.getContextName(classLoader));
        Assert.assertEquals(NULL, ClusterClassLoaderPool.getContextName(new URLClassLoader(new URL[0])));
    }

    @Test
    public void testGetContextNameDebug() {
        ClassLoader classLoader = new URLClassLoader(new URL[0]);
        try (CaptureHandler captureHandler = JDKLoggerTestUtil.configureJDKLogger(ClusterClassLoaderPool.class.getName(), Level.INFO)) {
            // Test 1, log level is INFO
            List<LogRecord> logRecords = captureHandler.getLogRecords();
            Assert.assertEquals(NULL, ClusterClassLoaderPool.getContextName(classLoader));
            Assert.assertTrue(logRecords.toString(), logRecords.isEmpty());
            // Test 2, log level is FINE
            logRecords = captureHandler.resetLogLevel(Level.FINE);
            Assert.assertEquals(NULL, ClusterClassLoaderPool.getContextName(classLoader));
            Assert.assertEquals(logRecords.toString(), 1, logRecords.size());
            LogRecord logRecord = logRecords.get(0);
            Assert.assertEquals((("Unable to find context name for " + classLoader) + ", send 'null' as context name instead"), logRecord.getMessage());
        }
    }

    @Test
    public void testUnregisterFallbackClassLoader() {
        ClusterClassLoaderPool.registerFallback(ClusterClassLoaderPoolTest._SYMBOLIC_NAME, new Version("1.0.0"), new URLClassLoader(new URL[0]));
        ClusterClassLoaderPool.registerFallback(ClusterClassLoaderPoolTest._SYMBOLIC_NAME, new Version("2.0.0"), new URLClassLoader(new URL[0]));
        ClusterClassLoaderPool.registerFallback(ClusterClassLoaderPoolTest._SYMBOLIC_NAME, new Version("3.0.0"), new URLClassLoader(new URL[0]));
        ClusterClassLoaderPool.unregisterFallback("WRONG_SYMBOLIC_NAME", new Version("1.0.0"));
        Assert.assertEquals(_fallbackClassLoaders.toString(), 1, _fallbackClassLoaders.size());
        ClusterClassLoaderPool.unregisterFallback(ClusterClassLoaderPoolTest._SYMBOLIC_NAME, new Version("4.0.0"));
        Assert.assertEquals(_fallbackClassLoaders.toString(), 1, _fallbackClassLoaders.size());
        ClusterClassLoaderPool.unregisterFallback(ClusterClassLoaderPoolTest._SYMBOLIC_NAME, new Version("2.0.0"));
        Assert.assertEquals(_fallbackClassLoaders.toString(), 1, _fallbackClassLoaders.size());
        ClusterClassLoaderPool.unregisterFallback(ClusterClassLoaderPoolTest._SYMBOLIC_NAME, new Version("1.0.0"));
        Assert.assertEquals(_fallbackClassLoaders.toString(), 1, _fallbackClassLoaders.size());
        ClusterClassLoaderPool.unregisterFallback(ClusterClassLoaderPoolTest._SYMBOLIC_NAME, new Version("3.0.0"));
        Assert.assertTrue(_fallbackClassLoaders.toString(), _fallbackClassLoaders.isEmpty());
    }

    private static final String _CONTEXT_NAME_1;

    private static final String _CONTEXT_NAME_2;

    private static final String _CONTEXT_NAME_3;

    private static final String _SYMBOLIC_NAME = "symbolic.name";

    static {
        _CONTEXT_NAME_1 = (ClusterClassLoaderPoolTest._SYMBOLIC_NAME) + "_1.0.0";
        _CONTEXT_NAME_2 = (ClusterClassLoaderPoolTest._SYMBOLIC_NAME) + "_2.0.0";
        _CONTEXT_NAME_3 = (ClusterClassLoaderPoolTest._SYMBOLIC_NAME) + "_3.0.0";
    }

    private Map<String, ClassLoader> _classLoaders;

    private ClassLoader _contextClassLoader;

    private Map<ClassLoader, String> _contextNames;

    private Map<String, ConcurrentNavigableMap<Version, ClassLoader>> _fallbackClassLoaders;

    private class BlockingInvocationHandler implements InvocationHandler {
        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            String methodName = method.getName();
            if (methodName.equals("lastEntry")) {
                _waitCountDownLatch.countDown();
                _blockCountDownLatch.await();
            }
            return method.invoke(_concurrentNavigableMap, args);
        }

        public void unblock() {
            _blockCountDownLatch.countDown();
        }

        public void waitUntilBlock() throws InterruptedException {
            _waitCountDownLatch.await();
        }

        private BlockingInvocationHandler(ConcurrentNavigableMap<Version, ClassLoader> concurrentNavigableMap) {
            _concurrentNavigableMap = concurrentNavigableMap;
        }

        private final CountDownLatch _blockCountDownLatch = new CountDownLatch(1);

        private final ConcurrentNavigableMap<Version, ClassLoader> _concurrentNavigableMap;

        private final CountDownLatch _waitCountDownLatch = new CountDownLatch(1);
    }
}

