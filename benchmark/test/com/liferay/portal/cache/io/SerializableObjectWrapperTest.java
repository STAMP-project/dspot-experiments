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
package com.liferay.portal.cache.io;


import com.liferay.petra.lang.ClassLoaderPool;
import com.liferay.portal.kernel.test.CaptureHandler;
import com.liferay.portal.kernel.test.JDKLoggerTestUtil;
import com.liferay.portal.kernel.test.rule.CodeCoverageAssertor;
import java.io.Serializable;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;


/**
 *
 *
 * @author Xiangyue Cai
 */
public class SerializableObjectWrapperTest {
    @ClassRule
    public static final CodeCoverageAssertor codeCoverageAssertor = CodeCoverageAssertor.INSTANCE;

    @Test
    public void testEquals() throws Exception {
        Assert.assertNotEquals(_testSerializableObjectWrapper, SerializableObjectWrapperTest._TEST_SERIALIZABLE);
        Assert.assertNotEquals(_testSerializableObjectWrapper, new SerializableObjectWrapper(SerializableObjectWrapperTest._ANOTHER_TEST_SERIALIZABLE));
        Assert.assertEquals(_testSerializableObjectWrapper, _testSerializableObjectWrapper);
        Assert.assertEquals(_testSerializableObjectWrapper, new SerializableObjectWrapper(SerializableObjectWrapperTest._TEST_SERIALIZABLE));
        Assert.assertEquals(_testSerializableObjectWrapper, _cloneBySerialization(_testSerializableObjectWrapper));
        Assert.assertEquals(_cloneBySerialization(_testSerializableObjectWrapper), _testSerializableObjectWrapper);
        Assert.assertEquals(_cloneBySerialization(_testSerializableObjectWrapper), _cloneBySerialization(_testSerializableObjectWrapper));
    }

    @Test
    public void testHashCode() throws Exception {
        SerializableObjectWrapper anotherTestSerializableObjectWrapper = new SerializableObjectWrapper(SerializableObjectWrapperTest._ANOTHER_TEST_SERIALIZABLE);
        Assert.assertNotEquals(_testSerializableObjectWrapper.hashCode(), anotherTestSerializableObjectWrapper.hashCode());
        SerializableObjectWrapper testSerializableObjectWrapper = new SerializableObjectWrapper(SerializableObjectWrapperTest._TEST_SERIALIZABLE);
        Assert.assertEquals(_testSerializableObjectWrapper.hashCode(), testSerializableObjectWrapper.hashCode());
        SerializableObjectWrapper cloneSerializableObjectWrapper = _cloneBySerialization(_testSerializableObjectWrapper);
        Assert.assertEquals(_testSerializableObjectWrapper.hashCode(), cloneSerializableObjectWrapper.hashCode());
    }

    @Test
    public void testUnwrap() throws Exception {
        Assert.assertEquals(SerializableObjectWrapperTest._TEST_SERIALIZABLE, SerializableObjectWrapper.unwrap(_testSerializableObjectWrapper));
        Assert.assertEquals(SerializableObjectWrapperTest._TEST_SERIALIZABLE, SerializableObjectWrapper.unwrap(_cloneBySerialization(_testSerializableObjectWrapper)));
        Assert.assertEquals(SerializableObjectWrapperTest._TEST_SERIALIZABLE, SerializableObjectWrapper.unwrap(SerializableObjectWrapperTest._TEST_SERIALIZABLE));
    }

    @Test
    public void testWithBrokenClassLoader() throws Exception {
        ClassLoaderPool.unregister(ClassLoaderPool.class.getClassLoader());
        Thread currentThread = Thread.currentThread();
        ClassLoader contextClassLoader = currentThread.getContextClassLoader();
        ClassNotFoundException cnfe = new ClassNotFoundException();
        currentThread.setContextClassLoader(new ClassLoader() {
            @Override
            public Class<?> loadClass(String name) throws ClassNotFoundException {
                if (name.equals(SerializableObjectWrapperTest.TestSerializable.class.getName())) {
                    throw cnfe;
                }
                return super.loadClass(name);
            }
        });
        try (CaptureHandler captureHandler = JDKLoggerTestUtil.configureJDKLogger(SerializableObjectWrapper.class.getName(), Level.ALL)) {
            // Test unwrap
            List<LogRecord> logRecords = captureHandler.getLogRecords();
            Assert.assertNull(SerializableObjectWrapper.unwrap(_cloneBySerialization(_testSerializableObjectWrapper)));
            Assert.assertEquals(logRecords.toString(), 1, logRecords.size());
            LogRecord logRecord = logRecords.get(0);
            Assert.assertEquals("Unable to deserialize object", logRecord.getMessage());
            Assert.assertSame(cnfe, logRecord.getThrown());
        } finally {
            currentThread.setContextClassLoader(contextClassLoader);
        }
    }

    private static final SerializableObjectWrapperTest.TestSerializable _ANOTHER_TEST_SERIALIZABLE = new SerializableObjectWrapperTest.TestSerializable("_ANOTHER_TEST_SERIALIZABLE");

    private static final SerializableObjectWrapperTest.TestSerializable _TEST_SERIALIZABLE = new SerializableObjectWrapperTest.TestSerializable("_TEST_SERIALIZABLE");

    private final SerializableObjectWrapper _testSerializableObjectWrapper = new SerializableObjectWrapper(SerializableObjectWrapperTest._TEST_SERIALIZABLE);

    private static class TestSerializable implements Serializable {
        @Override
        public boolean equals(Object object) {
            if ((this) == object) {
                return true;
            }
            if (!(object instanceof SerializableObjectWrapperTest.TestSerializable)) {
                return false;
            }
            SerializableObjectWrapperTest.TestSerializable testSerializable = ((SerializableObjectWrapperTest.TestSerializable) (object));
            return Objects.equals(_name, testSerializable._name);
        }

        @Override
        public int hashCode() {
            return _name.hashCode();
        }

        private TestSerializable(String name) {
            _name = name;
        }

        private final String _name;
    }
}

