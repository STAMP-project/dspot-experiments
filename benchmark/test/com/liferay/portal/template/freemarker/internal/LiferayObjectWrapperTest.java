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
package com.liferay.portal.template.freemarker.internal;


import NewEnv.Type;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.test.CaptureHandler;
import com.liferay.portal.kernel.test.JDKLoggerTestUtil;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.CodeCoverageAssertor;
import com.liferay.portal.kernel.test.rule.NewEnv;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.test.aspects.ReflectionUtilAdvice;
import com.liferay.portal.test.rule.AdviseWith;
import com.liferay.portal.test.rule.AspectJNewEnvTestRule;
import freemarker.ext.beans.EnumerationModel;
import freemarker.ext.beans.ResourceBundleModel;
import freemarker.ext.beans.StringModel;
import freemarker.ext.dom.NodeModel;
import freemarker.template.TemplateModel;
import freemarker.template.Version;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.w3c.dom.Element;
import org.w3c.dom.Node;


/**
 *
 *
 * @author Xiangyue Cai
 */
public class LiferayObjectWrapperTest {
    @ClassRule
    @Rule
    public static final AggregateTestRule aggregateTestRule = new AggregateTestRule(AspectJNewEnvTestRule.INSTANCE, CodeCoverageAssertor.INSTANCE);

    @Test
    public void testCheckClassIsRestricted() {
        _testCheckClassIsRestricted(new LiferayObjectWrapper(null, null), LiferayObjectWrapperTest.TestLiferayObject.class, null);
        _testCheckClassIsRestricted(new LiferayObjectWrapper(new String[]{ LiferayObjectWrapperTest.TestLiferayObject.class.getName() }, new String[]{ LiferayObjectWrapperTest.TestLiferayObject.class.getName() }), LiferayObjectWrapperTest.TestLiferayObject.class, null);
        _testCheckClassIsRestricted(new LiferayObjectWrapper(null, new String[]{ "java.lang.String" }), LiferayObjectWrapperTest.TestLiferayObject.class, null);
        _testCheckClassIsRestricted(new LiferayObjectWrapper(null, new String[]{ "com.liferay.portal.cache" }), LiferayObjectWrapperTest.TestLiferayObject.class, null);
        _testCheckClassIsRestricted(new LiferayObjectWrapper(null, new String[]{ LiferayObjectWrapperTest.TestLiferayObject.class.getName() }), LiferayObjectWrapperTest.TestLiferayObject.class, StringBundler.concat("Denied resolving class ", LiferayObjectWrapperTest.TestLiferayObject.class.getName(), " by ", LiferayObjectWrapperTest.TestLiferayObject.class.getName()));
        _testCheckClassIsRestricted(new LiferayObjectWrapper(null, new String[]{ "com.liferay.portal.template.freemarker" }), LiferayObjectWrapperTest.TestLiferayObject.class, StringBundler.concat("Denied resolving class ", LiferayObjectWrapperTest.TestLiferayObject.class.getName(), " by com.liferay.portal.template.freemarker"));
        _testCheckClassIsRestricted(new LiferayObjectWrapper(null, new String[]{ "com.liferay.portal.template.freemarker" }), byte.class, null);
    }

    @Test
    public void testCheckClassIsRestrictedWithNoContextClassloader() {
        Thread thread = Thread.currentThread();
        ClassLoader contextClassLoader = thread.getContextClassLoader();
        thread.setContextClassLoader(null);
        try {
            _testCheckClassIsRestricted(new LiferayObjectWrapper(new String[]{ LiferayObjectWrapperTest.TestLiferayObject.class.getName() }, new String[]{ LiferayObjectWrapperTest.TestLiferayObject.class.getName() }), LiferayObjectWrapperTest.TestLiferayObject.class, null);
        } finally {
            thread.setContextClassLoader(contextClassLoader);
        }
    }

    @Test
    public void testConstructor() {
        try (CaptureHandler captureHandler = JDKLoggerTestUtil.configureJDKLogger(LiferayObjectWrapper.class.getName(), Level.INFO)) {
            Assert.assertEquals(Collections.singletonList("com.liferay.package.name"), ReflectionTestUtil.getFieldValue(new LiferayObjectWrapper(null, new String[]{ "com.liferay.package.name" }), "_restrictedPackageNames"));
            List<LogRecord> logRecords = captureHandler.getLogRecords();
            Assert.assertEquals(logRecords.toString(), 1, logRecords.size());
            LogRecord logRecord = logRecords.get(0);
            Assert.assertEquals(("Unable to find restricted class com.liferay.package.name. " + "Registering as a package."), logRecord.getMessage());
        }
        try (CaptureHandler captureHandler = JDKLoggerTestUtil.configureJDKLogger(LiferayObjectWrapper.class.getName(), Level.OFF)) {
            Assert.assertEquals(Collections.singletonList("com.liferay.package.name"), ReflectionTestUtil.getFieldValue(new LiferayObjectWrapper(null, new String[]{ "com.liferay.package.name" }), "_restrictedPackageNames"));
            List<LogRecord> logRecords = captureHandler.getLogRecords();
            Assert.assertEquals(logRecords.toString(), 0, logRecords.size());
        }
        Field cacheClassNamesField = ReflectionTestUtil.getAndSetFieldValue(LiferayObjectWrapper.class, "_cacheClassNamesField", null);
        try {
            new LiferayObjectWrapper(null, null);
            Assert.fail("NullPointerException was not thrown");
        } catch (Exception e) {
            Assert.assertSame(NullPointerException.class, e.getClass());
        } finally {
            ReflectionTestUtil.setFieldValue(LiferayObjectWrapper.class, "_cacheClassNamesField", cacheClassNamesField);
        }
    }

    @Test
    public void testHandleUnknownType() throws Exception {
        LiferayObjectWrapper liferayObjectWrapper = new LiferayObjectWrapper(null, null);
        // 1. Handle Enumeration
        Enumeration<String> enumeration = Collections.enumeration(Collections.singletonList("testElement"));
        _assertTemplateModel("testElement", ( enumerationModel) -> enumerationModel.next(), EnumerationModel.class.cast(liferayObjectWrapper.handleUnknownType(enumeration)));
        _assertModelFactoryCache("_ENUMERATION_MODEL_FACTORY", enumeration.getClass());
        // 2. Handle Node
        Node node = ((Node) (ProxyUtil.newProxyInstance(LiferayObjectWrapper.class.getClassLoader(), new Class<?>[]{ Node.class, Element.class }, ( proxy, method, args) -> {
            String methodName = method.getName();
            if (methodName.equals("getNodeType")) {
                return Node.ELEMENT_NODE;
            }
            return null;
        })));
        TemplateModel templateModel = liferayObjectWrapper.handleUnknownType(node);
        Assert.assertTrue("org.w3c.dom.Node should be handled as NodeModel", (templateModel instanceof NodeModel));
        NodeModel nodeModel = ((NodeModel) (templateModel));
        Assert.assertSame(node, nodeModel.getNode());
        Assert.assertEquals("element", nodeModel.getNodeType());
        _assertModelFactoryCache("_NODE_MODEL_FACTORY", node.getClass());
        // 3. Handle ResourceBundle
        ResourceBundle resourceBundle = new ResourceBundle() {
            @Override
            public Enumeration<String> getKeys() {
                return null;
            }

            @Override
            protected Object handleGetObject(String key) {
                return key;
            }
        };
        _assertTemplateModel(resourceBundle.toString(), ( resourceBundleModel) -> resourceBundleModel.getBundle(), ResourceBundleModel.class.cast(liferayObjectWrapper.handleUnknownType(resourceBundle)));
        _assertModelFactoryCache("_RESOURCE_BUNDLE_MODEL_FACTORY", resourceBundle.getClass());
        // 4. Handle Version
        _assertTemplateModel("1.0", ( stringModel) -> stringModel.getAsString(), StringModel.class.cast(liferayObjectWrapper.handleUnknownType(liferayObjectWrapper.handleUnknownType(new Version("1.0")))));
        _assertModelFactoryCache("_STRING_MODEL_FACTORY", Version.class);
    }

    @AdviseWith(adviceClasses = ReflectionUtilAdvice.class)
    @NewEnv(type = Type.CLASSLOADER)
    @Test
    public void testInitializationFailure() throws Exception {
        Exception exception = new Exception();
        ReflectionUtilAdvice.setDeclaredFieldThrowable(exception);
        try {
            Class.forName(("com.liferay.portal.template.freemarker.internal." + "LiferayObjectWrapper"));
            Assert.fail("ExceptionInInitializerError was not thrown");
        } catch (ExceptionInInitializerError eiie) {
            Assert.assertSame(exception, eiie.getCause());
        }
    }

    @Test
    public void testWrap() throws Exception {
        _testWrap(new LiferayObjectWrapper(null, null));
        _testWrap(new LiferayObjectWrapper(new String[]{ StringPool.STAR }, null));
        _testWrap(new LiferayObjectWrapper(new String[]{ StringPool.STAR }, new String[]{ LiferayObjectWrapper.class.getName() }));
        _testWrap(new LiferayObjectWrapper(new String[]{ StringPool.BLANK }, null));
        _testWrap(new LiferayObjectWrapper(null, new String[]{ StringPool.BLANK }));
        _testWrap(new LiferayObjectWrapper(new String[]{ StringPool.BLANK }, new String[]{ StringPool.BLANK }));
    }

    private class TestLiferayCollection extends ArrayList<String> {
        private TestLiferayCollection(String element) {
            add(element);
        }

        private static final long serialVersionUID = 1L;
    }

    private class TestLiferayMap extends HashMap<String, String> {
        private TestLiferayMap(String key, String value) {
            put(key, value);
        }

        private static final long serialVersionUID = 1L;
    }

    private class TestLiferayObject {
        @Override
        public String toString() {
            return _name;
        }

        private TestLiferayObject(String name) {
            _name = name;
        }

        private final String _name;
    }
}

