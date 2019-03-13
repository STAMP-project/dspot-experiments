/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.harmony.beans.tests.java.beans;


import java.beans.BeanDescriptor;
import java.beans.BeanInfo;
import java.beans.EventSetDescriptor;
import java.beans.IndexedPropertyDescriptor;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.MethodDescriptor;
import java.beans.PropertyChangeListener;
import java.beans.PropertyDescriptor;
import java.beans.SimpleBeanInfo;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.security.Permission;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.PropertyPermission;
import java.util.TooManyListenersException;
import junit.framework.TestCase;
import org.apache.harmony.beans.tests.support.MisprintBean;
import org.apache.harmony.beans.tests.support.OtherBean;
import org.apache.harmony.beans.tests.support.SampleBean;
import org.apache.harmony.beans.tests.support.mock.FakeFox;
import org.apache.harmony.beans.tests.support.mock.FakeFox01;
import org.apache.harmony.beans.tests.support.mock.FakeFox011;
import org.apache.harmony.beans.tests.support.mock.FakeFox01BeanInfo;
import org.apache.harmony.beans.tests.support.mock.FakeFox02;
import org.apache.harmony.beans.tests.support.mock.FakeFox031;
import org.apache.harmony.beans.tests.support.mock.FakeFox041;
import org.apache.harmony.beans.tests.support.mock.FakeFox0411;
import org.apache.harmony.beans.tests.support.mock.MockButton;
import org.apache.harmony.beans.tests.support.mock.MockFoo;
import org.apache.harmony.beans.tests.support.mock.MockFooButton;
import org.apache.harmony.beans.tests.support.mock.MockFooLabel;
import org.apache.harmony.beans.tests.support.mock.MockFooStop;
import org.apache.harmony.beans.tests.support.mock.MockFooSub;
import org.apache.harmony.beans.tests.support.mock.MockFooSubSub;
import org.apache.harmony.beans.tests.support.mock.MockJavaBean;
import org.apache.harmony.beans.tests.support.mock.MockNullSubClass;
import org.apache.harmony.beans.tests.support.mock.homonymy.mocksubject1.MockHomonymySubject;


/**
 * Unit test for Introspector.
 */
/* Introspector Mixed Testing End */
public class IntrospectorTest extends TestCase {
    private String[] defaultPackage;

    public IntrospectorTest(String str) {
        super(str);
    }

    public IntrospectorTest() {
    }

    /**
     * The test checks the getBeanDescriptor method
     */
    public void testBeanDescriptor() throws Exception {
        String[] oldBeanInfoSearchPath = Introspector.getBeanInfoSearchPath();
        try {
            Introspector.setBeanInfoSearchPath(new String[]{ "java.beans.infos" });
            BeanInfo info = Introspector.getBeanInfo(SampleBean.class);
            TestCase.assertNotNull(info);
            BeanDescriptor descriptor = info.getBeanDescriptor();
            TestCase.assertNotNull(descriptor);
            TestCase.assertEquals(SampleBean.class, descriptor.getBeanClass());
        } finally {
            Introspector.setBeanInfoSearchPath(oldBeanInfoSearchPath);
        }
    }

    public void testBeanDescriptor_Same() throws Exception {
        BeanInfo beanInfo = Introspector.getBeanInfo(MockJavaBean.class);
        TestCase.assertSame(beanInfo.getBeanDescriptor(), beanInfo.getBeanDescriptor());
    }

    /**
     * The test checks the getEventSetDescriptors method
     *
     * @throws IntrospectionException
     * 		
     */
    public void testUnicastEventSetDescriptor() throws IntrospectionException {
        BeanInfo info = Introspector.getBeanInfo(SampleBean.class);
        TestCase.assertNotNull(info);
        EventSetDescriptor[] descriptors = info.getEventSetDescriptors();
        TestCase.assertNotNull(descriptors);
        for (EventSetDescriptor descriptor : descriptors) {
            Method m = descriptor.getAddListenerMethod();
            if (m != null) {
                Class<?>[] exceptionTypes = m.getExceptionTypes();
                boolean found = false;
                for (Class<?> et : exceptionTypes) {
                    if (et.equals(TooManyListenersException.class)) {
                        TestCase.assertTrue(descriptor.isUnicast());
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    TestCase.assertFalse(descriptor.isUnicast());
                }
            }
        }
    }

    /**
     * The test checks the getEventSetDescriptors method
     *
     * @throws IntrospectionException
     * 		
     */
    public void testEventSetDescriptorWithoutAddListenerMethod() throws IntrospectionException {
        BeanInfo info = Introspector.getBeanInfo(OtherBean.class);
        EventSetDescriptor[] descriptors;
        TestCase.assertNotNull(info);
        descriptors = info.getEventSetDescriptors();
        TestCase.assertNotNull(descriptors);
        TestCase.assertEquals(1, descriptors.length);
        TestCase.assertTrue(IntrospectorTest.contains("sample", descriptors));
    }

    /**
     * The test checks the getEventSetDescriptors method
     *
     * @throws IntrospectionException
     * 		
     */
    public void testIllegalEventSetDescriptor() throws IntrospectionException {
        BeanInfo info = Introspector.getBeanInfo(MisprintBean.class);
        TestCase.assertNotNull(info);
        EventSetDescriptor[] descriptors = info.getEventSetDescriptors();
        TestCase.assertNotNull(descriptors);
        TestCase.assertEquals(0, descriptors.length);
    }

    /**
     * The test checks the getPropertyDescriptors method
     *
     * @throws IntrospectionException
     * 		
     */
    public void testPropertyDescriptorWithSetMethod() throws IntrospectionException {
        BeanInfo info = Introspector.getBeanInfo(OtherBean.class);
        TestCase.assertNotNull(info);
        PropertyDescriptor[] descriptors = info.getPropertyDescriptors();
        TestCase.assertNotNull(descriptors);
        TestCase.assertEquals(2, descriptors.length);
        TestCase.assertEquals("class", descriptors[0].getName());
        TestCase.assertEquals("number", descriptors[1].getName());
    }

    public void testGetBeanInfo_NPE() throws IntrospectionException {
        // Regression for HARMONY-257
        try {
            Introspector.getBeanInfo(((Class<?>) (null)));
            TestCase.fail("getBeanInfo should throw NullPointerException");
        } catch (NullPointerException e) {
        }
        try {
            Introspector.getBeanInfo(((Class<?>) (null)), ((Class<?>) (null)));
            TestCase.fail("getBeanInfo should throw NullPointerException");
        } catch (NullPointerException e) {
        }
        try {
            Introspector.getBeanInfo(((Class<?>) (null)), 0);
            TestCase.fail("getBeanInfo should throw NullPointerException");
        } catch (NullPointerException e) {
        }
    }

    /* Common */
    public void testDecapitalize() {
        TestCase.assertEquals("fooBah", Introspector.decapitalize("FooBah"));
        TestCase.assertEquals("fooBah", Introspector.decapitalize("fooBah"));
        TestCase.assertEquals("x", Introspector.decapitalize("X"));
        TestCase.assertNull(Introspector.decapitalize(null));
        TestCase.assertEquals("", Introspector.decapitalize(""));
        TestCase.assertEquals("a1", Introspector.decapitalize("A1"));
    }

    public void testFlushCaches() throws IntrospectionException {
        BeanInfo info = Introspector.getBeanInfo(MockJavaBean.class);
        BeanDescriptor beanDesc = new BeanDescriptor(MockJavaBean.class);
        TestCase.assertEquals(beanDesc.getName(), info.getBeanDescriptor().getName());
        TestCase.assertEquals(beanDesc.isExpert(), info.getBeanDescriptor().isExpert());
        Introspector.flushCaches();
        BeanInfo cacheInfo = Introspector.getBeanInfo(MockJavaBean.class);
        TestCase.assertNotSame(info, cacheInfo);
        beanDesc = new BeanDescriptor(MockJavaBean.class);
        TestCase.assertEquals(beanDesc.getName(), info.getBeanDescriptor().getName());
        TestCase.assertEquals(beanDesc.isExpert(), info.getBeanDescriptor().isExpert());
    }

    public void testFlushFromCaches() throws IntrospectionException {
        BeanInfo info = Introspector.getBeanInfo(MockFooSubSub.class);
        BeanInfo info2 = Introspector.getBeanInfo(MockFooSubSub.class);
        TestCase.assertSame(info, info2);
        Introspector.flushFromCaches(MockFooSubSub.class);
        BeanInfo info3 = Introspector.getBeanInfo(MockFooSubSub.class);
        TestCase.assertNotSame(info, info3);
    }

    public void testFlushFromCaches_Null() throws IntrospectionException {
        BeanInfo info = Introspector.getBeanInfo(MockJavaBean.class);
        BeanDescriptor beanDesc = new BeanDescriptor(MockJavaBean.class);
        TestCase.assertEquals(beanDesc.getName(), info.getBeanDescriptor().getName());
        TestCase.assertEquals(beanDesc.isExpert(), info.getBeanDescriptor().isExpert());
        try {
            Introspector.flushFromCaches(null);
            TestCase.fail("Should throw NullPointerException.");
        } catch (NullPointerException e) {
        }
    }

    /* Class under test for BeanInfo getBeanInfo(Class) No XXXXBeanInfo + test
    cache info
     */
    public void testGetBeanInfoClass_no_BeanInfo() throws IntrospectionException {
        Class<FakeFox> beanClass = FakeFox.class;
        BeanInfo info = Introspector.getBeanInfo(beanClass);
        TestCase.assertNull(info.getAdditionalBeanInfo());
        BeanDescriptor beanDesc = info.getBeanDescriptor();
        TestCase.assertEquals("FakeFox", beanDesc.getName());
        TestCase.assertEquals(0, info.getEventSetDescriptors().length);
        TestCase.assertEquals((-1), info.getDefaultEventIndex());
        TestCase.assertEquals((-1), info.getDefaultPropertyIndex());
        MethodDescriptor[] methodDesc = info.getMethodDescriptors();
        Method[] methods = beanClass.getMethods();
        TestCase.assertEquals(methods.length, methodDesc.length);
        ArrayList<Method> methodList = new ArrayList<Method>();
        for (Method element : methods) {
            methodList.add(element);
        }
        for (MethodDescriptor element : methodDesc) {
            TestCase.assertTrue(methodList.contains(element.getMethod()));
        }
        PropertyDescriptor[] propertyDesc = info.getPropertyDescriptors();
        TestCase.assertEquals(1, propertyDesc.length);
        for (PropertyDescriptor element : propertyDesc) {
            if (element.getName().equals("class")) {
                TestCase.assertNull(element.getWriteMethod());
                TestCase.assertNotNull(element.getReadMethod());
            }
        }
        BeanInfo cacheInfo = Introspector.getBeanInfo(FakeFox.class);
        TestCase.assertSame(info, cacheInfo);
    }

    /* There is a BeanInfo class + test cache info */
    public void testGetBeanInfoClass_HaveBeanInfo() throws IntrospectionException {
        Class<FakeFox01> beanClass = FakeFox01.class;
        BeanInfo info = Introspector.getBeanInfo(beanClass);
        // printInfo(info);
        BeanInfo beanInfo = new FakeFox01BeanInfo();
        IntrospectorTest.assertBeanInfoEquals(beanInfo, info);
        TestCase.assertEquals((-1), info.getDefaultEventIndex());
        TestCase.assertEquals(0, info.getDefaultPropertyIndex());
        BeanInfo cacheInfo = Introspector.getBeanInfo(beanClass);
        TestCase.assertSame(info, cacheInfo);
    }

    public void testGetBeanInfoClass_ClassNull() throws IntrospectionException {
        try {
            Introspector.getBeanInfo(null);
            TestCase.fail("Should throw NullPointerException.");
        } catch (NullPointerException e) {
        }
    }

    /* Class under test for BeanInfo getBeanInfo(Class, Class) */
    public void testGetBeanInfoClassClass_Property() throws IntrospectionException {
        BeanInfo info = Introspector.getBeanInfo(MockFoo.class, MockFooStop.class);
        PropertyDescriptor[] pds = info.getPropertyDescriptors();
        TestCase.assertEquals(2, pds.length);
        TestCase.assertTrue(IntrospectorTest.contains("name", String.class, pds));
        TestCase.assertTrue(IntrospectorTest.contains("complexLabel", MockFooLabel.class, pds));
    }

    public void testGetBeanInfoClassClass_Method() throws IntrospectionException {
        BeanInfo info = Introspector.getBeanInfo(MockFoo.class, MockFooStop.class);
        MethodDescriptor[] mds = info.getMethodDescriptors();
        TestCase.assertEquals(4, mds.length);
        TestCase.assertTrue(IntrospectorTest.contains("getName", mds));
        TestCase.assertTrue(IntrospectorTest.contains("setName", mds));
        TestCase.assertTrue(IntrospectorTest.contains("getComplexLabel", mds));
        TestCase.assertTrue(IntrospectorTest.contains("setComplexLabel", mds));
        try {
            Introspector.getBeanInfo(MockFoo.class, Serializable.class);
            TestCase.fail("Shoule throw exception, stopclass must be superclass of given bean");
        } catch (IntrospectionException e) {
        }
    }

    /* BeanClass provide bean info about itself */
    // public Image getIcon(int iconKind) {
    // return null;
    // }
    public static class MockBeanInfo4BeanClassSelf implements BeanInfo {
        public void setValue(String v) throws Exception {
        }

        public int getValue() {
            return 0;
        }

        public BeanDescriptor getBeanDescriptor() {
            return null;
        }

        public EventSetDescriptor[] getEventSetDescriptors() {
            return new EventSetDescriptor[0];
        }

        public int getDefaultEventIndex() {
            return -1;
        }

        public int getDefaultPropertyIndex() {
            return -1;
        }

        public PropertyDescriptor[] getPropertyDescriptors() {
            return new PropertyDescriptor[0];
        }

        public MethodDescriptor[] getMethodDescriptors() {
            return new MethodDescriptor[0];
        }

        public BeanInfo[] getAdditionalBeanInfo() {
            return null;
        }
    }

    public void test_BeanInfo_Self() throws Exception {
        BeanInfo info = Introspector.getBeanInfo(IntrospectorTest.MockBeanInfo4BeanClassSelf.class);
        TestCase.assertEquals(0, info.getMethodDescriptors().length);
        TestCase.assertEquals(0, info.getPropertyDescriptors().length);
        TestCase.assertEquals(0, info.getEventSetDescriptors().length);
    }

    /* Introspect static methods */
    public void testGetBeanInfo_StaticMethods() throws Exception {
        BeanInfo beanInfo = Introspector.getBeanInfo(IntrospectorTest.StaticClazz.class);
        PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
        TestCase.assertEquals(1, propertyDescriptors.length);
        TestCase.assertTrue(IntrospectorTest.contains("class", Class.class, propertyDescriptors));
        MethodDescriptor[] methodDescriptors = beanInfo.getMethodDescriptors();
        TestCase.assertTrue(IntrospectorTest.contains("getStaticMethod", methodDescriptors));
        TestCase.assertTrue(IntrospectorTest.contains("setStaticMethod", methodDescriptors));
        beanInfo = Introspector.getBeanInfo(IntrospectorTest.StaticClazzWithProperty.class);
        propertyDescriptors = beanInfo.getPropertyDescriptors();
        TestCase.assertEquals(1, propertyDescriptors.length);
        methodDescriptors = beanInfo.getMethodDescriptors();
        TestCase.assertTrue(IntrospectorTest.contains("getStaticName", methodDescriptors));
        TestCase.assertTrue(IntrospectorTest.contains("setStaticName", methodDescriptors));
    }

    public void testMockIncompatibleGetterAndIndexedGetterBean() throws Exception {
        Class<?> beanClass = IntrospectorTest.MockIncompatibleGetterAndIndexedGetterBean.class;
        BeanInfo beanInfo = Introspector.getBeanInfo(beanClass);
        PropertyDescriptor pd = null;
        PropertyDescriptor[] pds = beanInfo.getPropertyDescriptors();
        for (int i = 0; i < (pds.length); i++) {
            pd = pds[i];
            if (pd.getName().equals("data")) {
                break;
            }
        }
        TestCase.assertNotNull(pd);
        TestCase.assertTrue((pd instanceof IndexedPropertyDescriptor));
        IndexedPropertyDescriptor ipd = ((IndexedPropertyDescriptor) (pd));
        TestCase.assertNull(ipd.getReadMethod());
        TestCase.assertNull(ipd.getWriteMethod());
        Method indexedReadMethod = beanClass.getMethod("getData", new Class[]{ int.class });
        Method indexedWriteMethod = beanClass.getMethod("setData", new Class[]{ int.class, int.class });
        TestCase.assertEquals(indexedReadMethod, ipd.getIndexedReadMethod());
        TestCase.assertEquals(indexedWriteMethod, ipd.getIndexedWriteMethod());
    }

    public void testMockIncompatibleSetterAndIndexedSetterBean() throws Exception {
        Class<?> beanClass = IntrospectorTest.MockIncompatibleSetterAndIndexedSetterBean.class;
        BeanInfo beanInfo = Introspector.getBeanInfo(beanClass);
        PropertyDescriptor pd = null;
        PropertyDescriptor[] pds = beanInfo.getPropertyDescriptors();
        for (int i = 0; i < (pds.length); i++) {
            pd = pds[i];
            if (pd.getName().equals("data")) {
                break;
            }
        }
        TestCase.assertNotNull(pd);
        TestCase.assertTrue((pd instanceof IndexedPropertyDescriptor));
        IndexedPropertyDescriptor ipd = ((IndexedPropertyDescriptor) (pd));
        TestCase.assertNull(ipd.getReadMethod());
        TestCase.assertNull(ipd.getWriteMethod());
        Method indexedReadMethod = beanClass.getMethod("getData", new Class[]{ int.class });
        Method indexedWriteMethod = beanClass.getMethod("setData", new Class[]{ int.class, int.class });
        TestCase.assertEquals(indexedReadMethod, ipd.getIndexedReadMethod());
        TestCase.assertEquals(indexedWriteMethod, ipd.getIndexedWriteMethod());
    }

    public void testMockIncompatibleAllSetterAndGetterBean() throws Exception {
        Class<?> beanClass = IntrospectorTest.MockIncompatibleAllSetterAndGetterBean.class;
        BeanInfo beanInfo = Introspector.getBeanInfo(beanClass);
        PropertyDescriptor pd = null;
        PropertyDescriptor[] pds = beanInfo.getPropertyDescriptors();
        for (int i = 0; i < (pds.length); i++) {
            pd = pds[i];
            if (pd.getName().equals("data")) {
                break;
            }
        }
        TestCase.assertNotNull(pd);
        TestCase.assertTrue((pd instanceof IndexedPropertyDescriptor));
        IndexedPropertyDescriptor ipd = ((IndexedPropertyDescriptor) (pd));
        TestCase.assertNull(ipd.getReadMethod());
        TestCase.assertNull(ipd.getWriteMethod());
        Method indexedReadMethod = beanClass.getMethod("getData", new Class[]{ int.class });
        Method indexedWriteMethod = beanClass.getMethod("setData", new Class[]{ int.class, int.class });
        TestCase.assertEquals(indexedReadMethod, ipd.getIndexedReadMethod());
        TestCase.assertEquals(indexedWriteMethod, ipd.getIndexedWriteMethod());
    }

    public class MockIncompatibleGetterAndIndexedGetterBean {
        private int[] datas;

        public int getData() {
            return datas[0];
        }

        public int getData(int index) {
            return datas[index];
        }

        public void setData(int index, int data) {
            this.datas[index] = data;
        }
    }

    public class MockIncompatibleSetterAndIndexedSetterBean {
        private int[] datas;

        public int getData(int index) {
            return datas[index];
        }

        public void setData(int index, int data) {
            this.datas[index] = data;
        }

        public void setData(int data) {
            this.datas[0] = data;
        }
    }

    public class MockIncompatibleAllSetterAndGetterBean {
        private int[] datas;

        public int getData() {
            return datas[0];
        }

        public int getData(int index) {
            return datas[index];
        }

        public void setData(int index, int data) {
            this.datas[index] = data;
        }

        public void setData(int data) {
            this.datas[0] = data;
        }

        public void setData() {
            this.datas[0] = 0;
        }
    }

    public static class StaticClazz {
        /* public static get method */
        public static String getStaticMethod() {
            return "static class";
        }

        /* public static set method */
        public static void setStaticMethod(String content) {
            // do nothing
        }
    }

    public static class StaticClazzWithProperty {
        private static String staticName = "Static Clazz";

        /* public static get method */
        public static String getStaticName() {
            return IntrospectorTest.StaticClazzWithProperty.staticName;
        }

        /* public static set method */
        public static void setStaticName(String name) {
            IntrospectorTest.StaticClazzWithProperty.staticName = name;
        }
    }

    public void testGetBeanInfoClassClass_StopNull() throws IntrospectionException {
        BeanInfo info = Introspector.getBeanInfo(MockFoo.class);// , null);

        PropertyDescriptor[] pds = info.getPropertyDescriptors();
        boolean name = false;
        boolean label = false;
        for (PropertyDescriptor element : pds) {
            if (element.getName().equals("name")) {
                name = true;
            }
            if (element.getName().equals("label")) {
                label = true;
            }
        }
        TestCase.assertTrue(name);
        TestCase.assertTrue(label);
    }

    public void testGetBeanInfoClassClass_ClassNull() throws IntrospectionException {
        try {
            Introspector.getBeanInfo(null, MockFooStop.class);
            TestCase.fail("Should throw NullPointerException.");
        } catch (NullPointerException e) {
        }
    }

    /* StopClass is not a supper class of the bean. */
    public void testGetBeanInfoClassClass_ClassInvalid() throws IntrospectionException {
        try {
            Introspector.getBeanInfo(MockButton.class, MockFooStop.class);
            TestCase.fail("Should throw IntrospectionException.");
        } catch (IntrospectionException e) {
        }
    }

    /* FLAG=IGNORE_ALL_BEANINFO; */
    public void testGetBeanInfoClassint_IGNORE_ALL_Property() throws IntrospectionException {
        BeanInfo info = Introspector.getBeanInfo(MockFooSub.class, Introspector.IGNORE_ALL_BEANINFO);
        PropertyDescriptor[] pds = info.getPropertyDescriptors();
        int text = 0;
        for (PropertyDescriptor element : pds) {
            String name = element.getName();
            if (name.startsWith("text")) {
                text++;
                TestCase.assertEquals("text", name);
            }
        }
        TestCase.assertEquals(1, text);
    }

    /* FLAG=IGNORE_ALL_BEANINFO; */
    public void testGetBeanInfoClassint_IGNORE_ALL_Method() throws IntrospectionException {
        BeanInfo info = Introspector.getBeanInfo(MockFooSub.class, Introspector.IGNORE_ALL_BEANINFO);
        MethodDescriptor[] mds = info.getMethodDescriptors();
        int getMethod = 0;
        int setMethod = 0;
        for (MethodDescriptor element : mds) {
            String name = element.getName();
            if (name.startsWith("getText")) {
                getMethod++;
                TestCase.assertEquals("getText", name);
            }
            if (name.startsWith("setText")) {
                setMethod++;
                TestCase.assertEquals("setText", name);
            }
        }
        TestCase.assertEquals(1, getMethod);
        TestCase.assertEquals(1, setMethod);
    }

    /* FLAG=IGNORE_ALL_BEANINFO; */
    public void testGetBeanInfoClassint_IGNORE_ALL_Event() throws IntrospectionException {
        BeanInfo info = Introspector.getBeanInfo(MockFooSub.class, Introspector.IGNORE_ALL_BEANINFO);
        EventSetDescriptor[] esds = info.getEventSetDescriptors();
        TestCase.assertEquals(1, esds.length);
        TestCase.assertTrue(IntrospectorTest.contains("mockPropertyChange", esds));
    }

    /* FLAG invalid; */
    public void testGetBeanInfoClassint_FLAG_Invalid() throws IntrospectionException {
        BeanInfo info = Introspector.getBeanInfo(MockFooSub.class, (-1));
        PropertyDescriptor[] pds = info.getPropertyDescriptors();
        Introspector.getBeanInfo(MockFooSub.class, Introspector.IGNORE_ALL_BEANINFO);
        PropertyDescriptor[] pds2 = info.getPropertyDescriptors();
        TestCase.assertEquals(pds.length, pds2.length);
        for (int i = 0; i < (pds.length); i++) {
            TestCase.assertEquals(pds[i], pds2[i]);
        }
    }

    public void testGetBeanInfoSearchPath() {
        String[] path = Introspector.getBeanInfoSearchPath();
        TestCase.assertEquals(1, path.length);
        TestCase.assertTrue(path[0].endsWith("beans.infos"));
    }

    public void testGetBeanInfoSearchPath_Default() throws IntrospectionException, ClassNotFoundException {
        BeanInfo info = Introspector.getBeanInfo(MockFooButton.class);
        PropertyDescriptor[] pds = info.getPropertyDescriptors();
        BeanDescriptor beanDesc;
        TestCase.assertEquals(2, pds.length);
        TestCase.assertEquals("class", pds[0].getName());
        beanDesc = info.getBeanDescriptor();
        TestCase.assertEquals("MockFooButton", beanDesc.getName());
    }

    /* Test Introspection with BeanInfo No immediate BeanInfo Have super
    BeanInfo
     */
    public void testBeanInfo_1() throws IntrospectionException {
        Class<FakeFox011> beanClass = FakeFox011.class;
        BeanInfo info = Introspector.getBeanInfo(beanClass);
        TestCase.assertNull(info.getAdditionalBeanInfo());
        BeanDescriptor beanDesc = info.getBeanDescriptor();
        TestCase.assertEquals("FakeFox011", beanDesc.getName());
        TestCase.assertEquals(0, info.getEventSetDescriptors().length);
        TestCase.assertEquals((-1), info.getDefaultEventIndex());
        TestCase.assertEquals(0, info.getDefaultPropertyIndex());
        MethodDescriptor[] methodDesc = info.getMethodDescriptors();
        TestCase.assertEquals(4, methodDesc.length);
        PropertyDescriptor[] propertyDesc = info.getPropertyDescriptors();
        TestCase.assertEquals(2, propertyDesc.length);
        for (PropertyDescriptor element : propertyDesc) {
            if (element.getName().equals("class")) {
                TestCase.assertNull(element.getWriteMethod());
                TestCase.assertNotNull(element.getReadMethod());
            }
        }
    }

    public void testBeanInfo_2() throws IntrospectionException {
        Class<FakeFox02> beanClass = FakeFox02.class;
        BeanInfo info = Introspector.getBeanInfo(beanClass);
        TestCase.assertNull(info.getAdditionalBeanInfo());
        BeanDescriptor beanDesc = info.getBeanDescriptor();
        TestCase.assertEquals("FakeFox02", beanDesc.getName());
        TestCase.assertEquals(0, info.getEventSetDescriptors().length);
        TestCase.assertEquals((-1), info.getDefaultEventIndex());
        TestCase.assertEquals((-1), info.getDefaultPropertyIndex());
        PropertyDescriptor[] propertyDesc = info.getPropertyDescriptors();
        for (PropertyDescriptor element : propertyDesc) {
            if (element.getName().equals("fox02")) {
                TestCase.assertEquals("fox02.beaninfo", element.getDisplayName());
            }
        }
    }

    public void testPropertySort() throws IntrospectionException {
        Class<IntrospectorTest.FakeFox70> beanClass = IntrospectorTest.FakeFox70.class;
        BeanInfo info = Introspector.getBeanInfo(beanClass);
        PropertyDescriptor[] descs = info.getPropertyDescriptors();
        String[] names = new String[]{ "a", "aaa", "bb", "bbb", "bc", "class", "ddd", "ff" };
        for (int i = 0; i < (descs.length); i++) {
            TestCase.assertEquals(names[i], descs[i].getName());
        }
    }

    public void testIntrospectProperties() throws IntrospectionException {
        Class<IntrospectorTest.FakeFox80> beanClass = IntrospectorTest.FakeFox80.class;
        BeanInfo info = Introspector.getBeanInfo(beanClass);
        TestCase.assertEquals(2, info.getPropertyDescriptors().length);
    }

    public void testIntrospectProperties2() throws IntrospectionException {
        Class<IntrospectorTest.FakeFox90> beanClass = IntrospectorTest.FakeFox90.class;
        BeanInfo info = Introspector.getBeanInfo(beanClass);
        // printInfo(info);
        PropertyDescriptor[] pds = info.getPropertyDescriptors();
        TestCase.assertEquals(2, pds.length);
        TestCase.assertNull(pds[1].getReadMethod());
    }

    /* If Bean1 has wrong getter method: public int getProp6(boolean i), then
    Introspector.getBeanInfo(Bean1) throws java.beans.IntrospectionException.
     */
    public void testIntrospectorGetBeanInfo() throws IntrospectionException {
        Class<IntrospectorTest.FakeFoxInfo> clazz = IntrospectorTest.FakeFoxInfo.class;
        BeanInfo info = Introspector.getBeanInfo(clazz);
        // printInfo(info);
        PropertyDescriptor[] pds = info.getPropertyDescriptors();
        TestCase.assertEquals("prop6", pds[1].getName());
        TestCase.assertNull(pds[1].getReadMethod());
        TestCase.assertNotNull(pds[1].getWriteMethod());
    }

    public void testGetBeanInfoExplicitNull() throws Exception {
        Introspector.flushCaches();
        BeanInfo subinfo = Introspector.getBeanInfo(MockNullSubClass.class);
        TestCase.assertNotNull(subinfo.getPropertyDescriptors());
        TestCase.assertNotNull(subinfo.getEventSetDescriptors());
        TestCase.assertNotNull(subinfo.getMethodDescriptors());
        TestCase.assertEquals((-1), subinfo.getDefaultEventIndex());
        TestCase.assertEquals((-1), subinfo.getDefaultPropertyIndex());
    }

    static class FakeFoxInfo {
        public int getProp6(boolean i) {
            return 0;
        }

        public void setProp6(boolean i) {
        }
    }

    /* setBeanInfoSearchPath method of Introspector doesn't invoke
    checkPropertiesAccess method of SecurityManager class
     */
    public void testSetBeanInfoSearchPath2() {
        try {
            // test here
            {
                String[] newPath = new String[]{ "a", "b" };
                Introspector.setBeanInfoSearchPath(newPath);
                String[] path = Introspector.getBeanInfoSearchPath();
                TestCase.assertTrue(Arrays.equals(newPath, path));
                TestCase.assertNotSame(newPath, path);
                path[0] = "c";
                newPath[0] = "d";
                String[] path2 = Introspector.getBeanInfoSearchPath();
                TestCase.assertEquals("d", path2[0]);
            }
            {
                String[] newPath = new String[]{  };
                Introspector.setBeanInfoSearchPath(newPath);
                String[] path = Introspector.getBeanInfoSearchPath();
                TestCase.assertNotSame(newPath, path);
                TestCase.assertTrue(Arrays.equals(newPath, path));
            }
            {
                String[] newPath = null;
                Introspector.setBeanInfoSearchPath(newPath);
                try {
                    Introspector.getBeanInfoSearchPath();
                    TestCase.fail("Should throw NullPointerException.");
                } catch (NullPointerException e) {
                }
            }
        } catch (SecurityException e) {
        }
    }

    /* @test setBeanInfoSearchPath

    Change the sequence of the paths in Introspector.searchpaths, check
    whether the BeanInfo is consistent with the bean class
     */
    public void testSetBeanInfoSearchPath_SameClassesInDifferentPackage() throws IntrospectionException {
        // set the search path in the correct sequence
        Introspector.setBeanInfoSearchPath(new String[]{ "org.apache.harmony.beans.tests.support.mock.homonymy.mocksubject1.info", "org.apache.harmony.beans.tests.support.mock.homonymy.mocksubject2.info" });
        BeanInfo beanInfo = Introspector.getBeanInfo(MockHomonymySubject.class);
        BeanDescriptor beanDesc = beanInfo.getBeanDescriptor();
        TestCase.assertEquals(beanDesc.getName(), "mocksubject1");
        TestCase.assertEquals(beanDesc.getBeanClass(), MockHomonymySubject.class);
        // set the search path in the reverse sequence
        Introspector.setBeanInfoSearchPath(new String[]{ "org.apache.harmony.beans.tests.support.mock.homonymy.mocksubject2.info", "org.apache.harmony.beans.tests.support.mock.homonymy.mocksubject1.info" });
        beanInfo = Introspector.getBeanInfo(MockHomonymySubject.class);
        beanDesc = beanInfo.getBeanDescriptor();
        TestCase.assertEquals(beanDesc.getName(), "mocksubject1");
        TestCase.assertEquals(beanDesc.getBeanClass(), MockHomonymySubject.class);
    }

    static class MockSecurity2 extends SecurityManager {
        @Override
        public void checkPermission(Permission p) {
            if (p instanceof PropertyPermission) {
                throw new SecurityException("Expected exception.");
            }
        }
    }

    /* If Bean3 has empty BeanInfo, then
    Introspector.getBeanInfo(Bean3.class).getBeanDescriptor() returns null.
     */
    public void testNullBeanDescriptor() throws IntrospectionException {
        Class<IntrospectorTest.Bean3> clazz = IntrospectorTest.Bean3.class;
        BeanInfo info = Introspector.getBeanInfo(clazz);
        // printInfo(info);
        TestCase.assertNotNull(info.getBeanDescriptor());
    }

    public static class Bean3 {
        private String prop1;

        public String getProp1() {
            return prop1;
        }

        public void setProp1(String prop1) {
            this.prop1 = prop1;
        }
    }

    public static class Bean3BeanInfo extends SimpleBeanInfo {}

    public void testGetPropertyDescriptors_H1838() throws IntrospectionException {
        // Regression for HARMONY-1838
        PropertyDescriptor[] propertyDescriptors = Introspector.getBeanInfo(IntrospectorTest.Bean.class).getPropertyDescriptors();
        TestCase.assertEquals("class", propertyDescriptors[0].getName());
        TestCase.assertEquals("prop1", propertyDescriptors[1].getName());
        TestCase.assertEquals("prop2", propertyDescriptors[2].getName());
        TestCase.assertEquals(3, propertyDescriptors.length);
    }

    public static class Bean {
        public String getProp1(int i) {
            return null;
        }

        public void setProp2(int i, String str) {
        }
    }

    /*  */
    public void testGetPropertyDescriptors() throws IntrospectionException {
        Class<IntrospectorTest.Bean2> clazz = IntrospectorTest.Bean2.class;
        BeanInfo info = Introspector.getBeanInfo(clazz);
        PropertyDescriptor[] pds = info.getPropertyDescriptors();
        TestCase.assertEquals(2, pds.length);
        TestCase.assertEquals("property1", pds[0].getName());
        TestCase.assertEquals("property8", pds[1].getName());
    }

    public void testHarmony4861() throws IntrospectionException {
        final PropertyDescriptor[] propertyDescriptors = Introspector.getBeanInfo(IntrospectorTest.TestBean.class).getPropertyDescriptors();
        for (PropertyDescriptor d : propertyDescriptors) {
            if (d.getName().equals("prop1")) {
                // $NON-NLS-1$
                TestCase.assertEquals("isProp1", d.getReadMethod().getName());// $NON-NLS-1$

                return;
            }
        }
    }

    public static class TestBean {
        boolean prop1;

        public void setProp1(boolean prop1) {
            this.prop1 = prop1;
        }

        public boolean isProp1() {
            return prop1;
        }

        public boolean getProp1() {
            return prop1;
        }
    }

    public static class Bean1 {
        private int i;

        public int ggetI() {
            return i;
        }

        public void ssetI(int i) {
            this.i = i;
        }
    }

    public static class Bean1BeanInfo extends SimpleBeanInfo {
        @Override
        public PropertyDescriptor[] getPropertyDescriptors() {
            try {
                PropertyDescriptor _property1 = new PropertyDescriptor("property1", IntrospectorTest.Bean1.class, "ggetI", "ssetI");
                PropertyDescriptor[] pds = new PropertyDescriptor[]{ _property1 };
                return pds;
            } catch (IntrospectionException exception) {
                return null;
            }
        }
    }

    public static class Bean2 extends IntrospectorTest.Bean1 {
        private int property8;

        public int getProperty8() {
            return property8;
        }

        public void setProperty8(int property8) {
            this.property8 = property8;
        }
    }

    /* The following classes are used to test introspect Event */
    static class FakeFox70 {
        int ddd;

        int bbb;

        int bc;

        Integer ff;

        String a;

        String bb;

        String aaa;

        public String getA() {
            return a;
        }

        public void setA(String a) {
            this.a = a;
        }

        public String getAaa() {
            return aaa;
        }

        public void setAaa(String aaa) {
            this.aaa = aaa;
        }

        public String getBb() {
            return bb;
        }

        public void setBb(String bb) {
            this.bb = bb;
        }

        public int getBbb() {
            return bbb;
        }

        public void setBbb(int bbb) {
            this.bbb = bbb;
        }

        public int getBc() {
            return bc;
        }

        public void setBc(int bc) {
            this.bc = bc;
        }

        public int getDdd() {
            return ddd;
        }

        public void setDdd(int ddd) {
            this.ddd = ddd;
        }

        public Integer getFf() {
            return ff;
        }

        public void setFf(Integer ff) {
            this.ff = ff;
        }
    }

    static class FakeFox80 {
        public String get() {
            return null;
        }

        public String get123() {
            return null;
        }
    }

    static class FakeFox90 {
        public String getFox(String value) {
            return null;
        }

        public void setFox(String value) {
        }
    }

    public void testProperty() throws IntrospectionException {
        Class<IntrospectorTest.MockSubClassForPorpertiesStandard> beanClass = IntrospectorTest.MockSubClassForPorpertiesStandard.class;
        BeanInfo info = Introspector.getBeanInfo(beanClass);
        TestCase.assertEquals((-1), info.getDefaultEventIndex());
        TestCase.assertEquals((-1), info.getDefaultPropertyIndex());
        PropertyDescriptor[] pds = info.getPropertyDescriptors();
        for (PropertyDescriptor pd : pds) {
            TestCase.assertFalse(pd.isBound());
            TestCase.assertFalse(pd.isConstrained());
            TestCase.assertFalse(pd.isExpert());
            TestCase.assertFalse(pd.isHidden());
            TestCase.assertFalse(pd.isPreferred());
        }
        TestCase.assertEquals(2, info.getPropertyDescriptors().length);
        BeanInfo dummyInfo = Introspector.getBeanInfo(FakeFox041.class);
        PropertyDescriptor[] p = dummyInfo.getPropertyDescriptors();
        TestCase.assertFalse(p[0].isBound());
        TestCase.assertFalse(p[0].isConstrained());
        TestCase.assertFalse(p[1].isBound());
        TestCase.assertFalse(p[1].isConstrained());
        TestCase.assertTrue(p[2].isBound());
        TestCase.assertTrue(p[2].isConstrained());
        dummyInfo = Introspector.getBeanInfo(FakeFox0411.class);
        p = dummyInfo.getPropertyDescriptors();
        TestCase.assertFalse(p[0].isBound());
        TestCase.assertFalse(p[0].isConstrained());
        TestCase.assertFalse(p[1].isBound());
        TestCase.assertFalse(p[1].isConstrained());
        TestCase.assertTrue(p[2].isBound());
        TestCase.assertFalse(p[2].isConstrained());
        TestCase.assertTrue(p[3].isBound());
        TestCase.assertTrue(p[3].isConstrained());
        dummyInfo = Introspector.getBeanInfo(FakeFox0411.class, FakeFox041.class);
        p = dummyInfo.getPropertyDescriptors();
        TestCase.assertFalse(p[0].isBound());
        TestCase.assertFalse(p[0].isConstrained());
    }

    public void testDefaultEvent() throws IntrospectionException {
        Class<?> beanClass = IntrospectorTest.MockClassForDefaultEvent.class;
        BeanInfo info = Introspector.getBeanInfo(beanClass);
        TestCase.assertEquals((-1), info.getDefaultEventIndex());
        TestCase.assertEquals((-1), info.getDefaultPropertyIndex());
        EventSetDescriptor[] events = info.getEventSetDescriptors();
        for (EventSetDescriptor event : events) {
            TestCase.assertFalse(event.isUnicast());
            TestCase.assertTrue(event.isInDefaultEventSet());
            TestCase.assertFalse(event.isExpert());
            TestCase.assertFalse(event.isHidden());
            TestCase.assertFalse(event.isPreferred());
        }
    }

    public void testDefaultIndex() throws IntrospectionException {
        Introspector.setBeanInfoSearchPath(new String[]{ "org.apache.harmony.beans.tests.support" });
        BeanInfo dummyInfo = Introspector.getBeanInfo(FakeFox031.class);
        TestCase.assertEquals((-1), dummyInfo.getDefaultPropertyIndex());
        TestCase.assertEquals((-1), dummyInfo.getDefaultEventIndex());
    }

    static class MockBaseClassForPorpertiesStandard {
        int a = 0;

        int b = 1;
    }

    static class MockSubClassForPorpertiesStandard extends IntrospectorTest.MockBaseClassForPorpertiesStandard {
        int a = 2;

        int b = 3;

        public int getName() {
            return a;
        }

        public void setName(int i) {
            a = i;
        }
    }

    static class MockClassForDefaultEvent {
        public void addPropertyChangeListener(PropertyChangeListener a) {
        }

        public void removePropertyChangeListener(PropertyChangeListener a) {
        }
    }

    static class MockBaseClassForPorperties {
        int a = 0;

        int b = 1;
    }

    static class MockSubClassForPorperties extends IntrospectorTest.MockBaseClassForPorperties {
        int a = 2;

        int b = 3;

        int c = 3;

        public int getName() {
            return a;
        }

        public void setName(int i) {
            a = i;
        }
    }

    // public void testGetIcon() throws IntrospectionException {
    // Class<MockSubClassForPorperties> beanClass = MockSubClassForPorperties.class;
    // BeanInfo info = Introspector.getBeanInfo(beanClass);
    // assertNotNull(info.getIcon(BeanInfo.ICON_COLOR_16x16));
    // }
    // public Image getIcon(int iconKind) {
    // return null;
    // }
    public static class MockBaseClassForPorpertiesBeanInfo extends SimpleBeanInfo {
        @Override
        public MethodDescriptor[] getMethodDescriptors() {
            MethodDescriptor md = null;
            try {
                Class<IntrospectorTest.MockSubClassForPorperties> clz = IntrospectorTest.MockSubClassForPorperties.class;
                Method m = clz.getMethod("getName", new Class[]{  });
                md = new MethodDescriptor(m);
            } catch (Exception e) {
            }
            return new MethodDescriptor[]{ md };
        }

        @Override
        public PropertyDescriptor[] getPropertyDescriptors() {
            PropertyDescriptor[] pds = new PropertyDescriptor[2];
            Class<IntrospectorTest.MockSubClassForPorperties> clazz = IntrospectorTest.MockSubClassForPorperties.class;
            try {
                Method getter = clazz.getMethod("getName");
                Method setter = clazz.getMethod("setName", Integer.TYPE);
                pds[0] = new PropertyDescriptor("a", getter, setter);
                pds[0].setConstrained(true);
                pds[0].setBound(true);
                pds[0].setExpert(true);
                pds[0].setHidden(true);
                pds[1] = new PropertyDescriptor("b", getter, setter);
            } catch (IntrospectionException e) {
                e.printStackTrace();
            } catch (SecurityException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
            return pds;
        }
    }

    // public Image getIcon(int iconKind) {
    // return new BufferedImage(16, 16, 1);
    // }
    public static class MockSubClassForPorpertiesBeanInfo extends SimpleBeanInfo {
        @Override
        public MethodDescriptor[] getMethodDescriptors() {
            MethodDescriptor md = null;
            try {
                Class<IntrospectorTest.MockSubClassForPorperties> clz = IntrospectorTest.MockSubClassForPorperties.class;
                Method m = clz.getMethod("getName", new Class[]{  });
                md = new MethodDescriptor(m);
            } catch (Exception e) {
            }
            return new MethodDescriptor[]{ md };
        }

        @Override
        public PropertyDescriptor[] getPropertyDescriptors() {
            PropertyDescriptor[] pds = new PropertyDescriptor[2];
            Class<IntrospectorTest.MockSubClassForPorperties> clazz = IntrospectorTest.MockSubClassForPorperties.class;
            try {
                Method getter = clazz.getMethod("getName");
                Method setter = clazz.getMethod("setName", Integer.TYPE);
                pds[0] = new PropertyDescriptor("a", getter, setter);
                pds[1] = new PropertyDescriptor("b", getter, setter);
            } catch (IntrospectionException e) {
                e.printStackTrace();
            } catch (SecurityException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
            return pds;
        }
    }

    /* Regression test for HARMONY-4892 */
    public static class MyBean {
        public static String invisble;

        public static String getInvisible() {
            return IntrospectorTest.MyBean.invisble;
        }

        public String visible;

        public String getVisible() {
            return visible;
        }

        public void setVisible(String a) {
            this.visible = a;
        }
    }

    public void testPropertyDescriptors() throws IntrospectionException {
        BeanInfo info = Introspector.getBeanInfo(IntrospectorTest.MyBean.class);
        for (PropertyDescriptor pd : info.getPropertyDescriptors()) {
            TestCase.assertFalse(pd.getName().equals("invisible"));
        }
    }

    /* Introspector Mixed Testing Begin */
    private String propertyName = "list";

    public class MixedSimpleClass1 {
        public Object isList(int index) {
            return null;
        }

        public Object isList() {
            return null;
        }
    }

    public void test_MixedSimpleClass1() throws Exception {
        BeanInfo info = Introspector.getBeanInfo(IntrospectorTest.MixedSimpleClass1.class);
        Method getter = IntrospectorTest.MixedSimpleClass1.class.getDeclaredMethod("isList");
        for (PropertyDescriptor pd : info.getPropertyDescriptors()) {
            if (propertyName.equals(pd.getName())) {
                TestCase.assertFalse((pd instanceof IndexedPropertyDescriptor));
                TestCase.assertEquals(getter, pd.getReadMethod());
                TestCase.assertNull(pd.getWriteMethod());
            }
        }
    }

    public class MixedSimpleClass2 {
        public Object isList(int index) {
            return null;
        }

        public Object getList() {
            return null;
        }
    }

    public void test_MixedSimpleClass2() throws Exception {
        BeanInfo info = Introspector.getBeanInfo(IntrospectorTest.MixedSimpleClass2.class);
        Method getter = IntrospectorTest.MixedSimpleClass2.class.getDeclaredMethod("getList");
        for (PropertyDescriptor pd : info.getPropertyDescriptors()) {
            if (propertyName.equals(pd.getName())) {
                TestCase.assertFalse((pd instanceof IndexedPropertyDescriptor));
                TestCase.assertEquals(getter, pd.getReadMethod());
                TestCase.assertNull(pd.getWriteMethod());
            }
        }
    }

    public class MixedSimpleClass3 {
        public Object getList(int index) {
            return null;
        }

        public Object isList() {
            return null;
        }
    }

    public void test_MixedSimpleClass3() throws Exception {
        BeanInfo info = Introspector.getBeanInfo(IntrospectorTest.MixedSimpleClass3.class);
        Method getter = IntrospectorTest.MixedSimpleClass3.class.getDeclaredMethod("getList", int.class);
        for (PropertyDescriptor pd : info.getPropertyDescriptors()) {
            if (propertyName.equals(pd.getName())) {
                TestCase.assertTrue((pd instanceof IndexedPropertyDescriptor));
                TestCase.assertNull(pd.getReadMethod());
                TestCase.assertNull(pd.getWriteMethod());
                TestCase.assertEquals(getter, ((IndexedPropertyDescriptor) (pd)).getIndexedReadMethod());
                TestCase.assertNull(((IndexedPropertyDescriptor) (pd)).getIndexedWriteMethod());
            }
        }
    }

    public class MixedSimpleClass4 {
        public Object getList(int index) {
            return null;
        }

        public Object getList() {
            return null;
        }
    }

    public void test_MixedSimpleClass4() throws Exception {
        BeanInfo info = Introspector.getBeanInfo(IntrospectorTest.MixedSimpleClass4.class);
        Method getter = IntrospectorTest.MixedSimpleClass4.class.getDeclaredMethod("getList", int.class);
        for (PropertyDescriptor pd : info.getPropertyDescriptors()) {
            if (propertyName.equals(pd.getName())) {
                TestCase.assertTrue((pd instanceof IndexedPropertyDescriptor));
                TestCase.assertNull(pd.getReadMethod());
                TestCase.assertNull(pd.getWriteMethod());
                TestCase.assertEquals(getter, ((IndexedPropertyDescriptor) (pd)).getIndexedReadMethod());
                TestCase.assertNull(((IndexedPropertyDescriptor) (pd)).getIndexedWriteMethod());
            }
        }
    }

    public class MixedSimpleClass5 {
        public Object getList(int index) {
            return null;
        }

        public Object getList() {
            return null;
        }

        public void setList(Object obj) {
        }
    }

    public void test_MixedSimpleClass5() throws Exception {
        BeanInfo info = Introspector.getBeanInfo(IntrospectorTest.MixedSimpleClass5.class);
        Method getter = IntrospectorTest.MixedSimpleClass5.class.getDeclaredMethod("getList");
        Method setter = IntrospectorTest.MixedSimpleClass5.class.getDeclaredMethod("setList", Object.class);
        for (PropertyDescriptor pd : info.getPropertyDescriptors()) {
            if (propertyName.equals(pd.getName())) {
                TestCase.assertFalse((pd instanceof IndexedPropertyDescriptor));
                TestCase.assertEquals(getter, pd.getReadMethod());
                TestCase.assertEquals(setter, pd.getWriteMethod());
            }
        }
    }

    public class MixedSimpleClass6 {
        public Object getList(int index) {
            return null;
        }

        public Object isList() {
            return null;
        }

        public void setList(Object obj) {
        }
    }

    public void test_MixedSimpleClass6() throws Exception {
        BeanInfo info = Introspector.getBeanInfo(IntrospectorTest.MixedSimpleClass6.class);
        Method getter = IntrospectorTest.MixedSimpleClass6.class.getDeclaredMethod("getList", int.class);
        for (PropertyDescriptor pd : info.getPropertyDescriptors()) {
            if (propertyName.equals(pd.getName())) {
                TestCase.assertTrue((pd instanceof IndexedPropertyDescriptor));
                TestCase.assertNull(pd.getReadMethod());
                TestCase.assertNull(pd.getWriteMethod());
                TestCase.assertEquals(getter, ((IndexedPropertyDescriptor) (pd)).getIndexedReadMethod());
                TestCase.assertNull(((IndexedPropertyDescriptor) (pd)).getIndexedWriteMethod());
            }
        }
    }

    public class MixedSimpleClass7 {
        public Object isList(int index) {
            return null;
        }

        public Object getList() {
            return null;
        }

        public void setList(Object obj) {
        }
    }

    public void test_MixedSimpleClass7() throws Exception {
        BeanInfo info = Introspector.getBeanInfo(IntrospectorTest.MixedSimpleClass7.class);
        Method getter = IntrospectorTest.MixedSimpleClass7.class.getDeclaredMethod("getList");
        Method setter = IntrospectorTest.MixedSimpleClass7.class.getDeclaredMethod("setList", Object.class);
        for (PropertyDescriptor pd : info.getPropertyDescriptors()) {
            if (propertyName.equals(pd.getName())) {
                TestCase.assertFalse((pd instanceof IndexedPropertyDescriptor));
                TestCase.assertEquals(getter, pd.getReadMethod());
                TestCase.assertEquals(setter, pd.getWriteMethod());
            }
        }
    }

    public class MixedSimpleClass8 {
        public Object isList(int index) {
            return null;
        }

        public Object isList() {
            return null;
        }

        public void setList(Object obj) {
        }
    }

    public void test_MixedSimpleClass8() throws Exception {
        BeanInfo info = Introspector.getBeanInfo(IntrospectorTest.MixedSimpleClass8.class);
        Method setter = IntrospectorTest.MixedSimpleClass8.class.getDeclaredMethod("setList", Object.class);
        for (PropertyDescriptor pd : info.getPropertyDescriptors()) {
            if (propertyName.equals(pd.getName())) {
                TestCase.assertFalse((pd instanceof IndexedPropertyDescriptor));
                TestCase.assertNull(pd.getReadMethod());
                TestCase.assertEquals(setter, pd.getWriteMethod());
            }
        }
    }

    public class MixedSimpleClass9 {
        public Object isList(int index) {
            return null;
        }

        public Object isList() {
            return null;
        }

        public void setList(int index, Object obj) {
        }
    }

    public void test_MixedSimpleClass9() throws Exception {
        BeanInfo info = Introspector.getBeanInfo(IntrospectorTest.MixedSimpleClass9.class);
        Method setter = IntrospectorTest.MixedSimpleClass9.class.getDeclaredMethod("setList", int.class, Object.class);
        for (PropertyDescriptor pd : info.getPropertyDescriptors()) {
            if (propertyName.equals(pd.getName())) {
                TestCase.assertTrue((pd instanceof IndexedPropertyDescriptor));
                TestCase.assertNull(pd.getReadMethod());
                TestCase.assertNull(pd.getWriteMethod());
                TestCase.assertNull(((IndexedPropertyDescriptor) (pd)).getIndexedReadMethod());
                TestCase.assertEquals(setter, ((IndexedPropertyDescriptor) (pd)).getIndexedWriteMethod());
            }
        }
    }

    public class MixedSimpleClass10 {
        public Object isList(int index) {
            return null;
        }

        public Object getList() {
            return null;
        }

        public void setList(int index, Object obj) {
        }
    }

    public void test_MixedSimpleClass10() throws Exception {
        BeanInfo info = Introspector.getBeanInfo(IntrospectorTest.MixedSimpleClass10.class);
        Method setter = IntrospectorTest.MixedSimpleClass10.class.getDeclaredMethod("setList", int.class, Object.class);
        for (PropertyDescriptor pd : info.getPropertyDescriptors()) {
            if (propertyName.equals(pd.getName())) {
                TestCase.assertTrue((pd instanceof IndexedPropertyDescriptor));
                TestCase.assertNull(pd.getReadMethod());
                TestCase.assertNull(pd.getWriteMethod());
                TestCase.assertNull(((IndexedPropertyDescriptor) (pd)).getIndexedReadMethod());
                TestCase.assertEquals(setter, ((IndexedPropertyDescriptor) (pd)).getIndexedWriteMethod());
            }
        }
    }

    public class MixedSimpleClass11 {
        public Object getList(int index) {
            return null;
        }

        public Object isList() {
            return null;
        }

        public void setList(int index, Object obj) {
        }
    }

    public void test_MixedSimpleClass11() throws Exception {
        BeanInfo info = Introspector.getBeanInfo(IntrospectorTest.MixedSimpleClass11.class);
        Method getter = IntrospectorTest.MixedSimpleClass11.class.getDeclaredMethod("getList", int.class);
        Method setter = IntrospectorTest.MixedSimpleClass11.class.getDeclaredMethod("setList", int.class, Object.class);
        for (PropertyDescriptor pd : info.getPropertyDescriptors()) {
            if (propertyName.equals(pd.getName())) {
                TestCase.assertTrue((pd instanceof IndexedPropertyDescriptor));
                TestCase.assertNull(pd.getReadMethod());
                TestCase.assertNull(pd.getWriteMethod());
                TestCase.assertEquals(getter, ((IndexedPropertyDescriptor) (pd)).getIndexedReadMethod());
                TestCase.assertEquals(setter, ((IndexedPropertyDescriptor) (pd)).getIndexedWriteMethod());
            }
        }
    }

    public class MixedSimpleClass12 {
        public Object getList(int index) {
            return null;
        }

        public Object getList() {
            return null;
        }

        public void setList(int index, Object obj) {
        }
    }

    public void test_MixedSimpleClass12() throws Exception {
        BeanInfo info = Introspector.getBeanInfo(IntrospectorTest.MixedSimpleClass12.class);
        Method getter = IntrospectorTest.MixedSimpleClass12.class.getDeclaredMethod("getList", int.class);
        Method setter = IntrospectorTest.MixedSimpleClass12.class.getDeclaredMethod("setList", int.class, Object.class);
        for (PropertyDescriptor pd : info.getPropertyDescriptors()) {
            if (propertyName.equals(pd.getName())) {
                TestCase.assertTrue((pd instanceof IndexedPropertyDescriptor));
                TestCase.assertNull(pd.getReadMethod());
                TestCase.assertNull(pd.getWriteMethod());
                TestCase.assertEquals(getter, ((IndexedPropertyDescriptor) (pd)).getIndexedReadMethod());
                TestCase.assertEquals(setter, ((IndexedPropertyDescriptor) (pd)).getIndexedWriteMethod());
            }
        }
    }

    public class MixedSimpleClass13 {
        public Object getList(int index) {
            return null;
        }

        public Object getList() {
            return null;
        }

        public void setList(int index, Object obj) {
        }

        public void setList(Object obj) {
        }
    }

    public void test_MixedSimpleClass13() throws Exception {
        BeanInfo info = Introspector.getBeanInfo(IntrospectorTest.MixedSimpleClass13.class);
        Method getter = IntrospectorTest.MixedSimpleClass13.class.getDeclaredMethod("getList", int.class);
        Method setter = IntrospectorTest.MixedSimpleClass13.class.getDeclaredMethod("setList", int.class, Object.class);
        for (PropertyDescriptor pd : info.getPropertyDescriptors()) {
            if (propertyName.equals(pd.getName())) {
                TestCase.assertTrue((pd instanceof IndexedPropertyDescriptor));
                TestCase.assertNull(pd.getReadMethod());
                TestCase.assertNull(pd.getWriteMethod());
                TestCase.assertEquals(getter, ((IndexedPropertyDescriptor) (pd)).getIndexedReadMethod());
                TestCase.assertEquals(setter, ((IndexedPropertyDescriptor) (pd)).getIndexedWriteMethod());
            }
        }
    }

    public class MixedSimpleClass14 {
        public Object getList(int index) {
            return null;
        }

        public Object isList() {
            return null;
        }

        public void setList(int index, Object obj) {
        }

        public void setList(Object obj) {
        }
    }

    public void test_MixedSimpleClass14() throws Exception {
        BeanInfo info = Introspector.getBeanInfo(IntrospectorTest.MixedSimpleClass14.class);
        Method getter = IntrospectorTest.MixedSimpleClass14.class.getDeclaredMethod("getList", int.class);
        Method setter = IntrospectorTest.MixedSimpleClass14.class.getDeclaredMethod("setList", int.class, Object.class);
        for (PropertyDescriptor pd : info.getPropertyDescriptors()) {
            if (propertyName.equals(pd.getName())) {
                TestCase.assertTrue((pd instanceof IndexedPropertyDescriptor));
                TestCase.assertNull(pd.getReadMethod());
                TestCase.assertNull(pd.getWriteMethod());
                TestCase.assertEquals(getter, ((IndexedPropertyDescriptor) (pd)).getIndexedReadMethod());
                TestCase.assertEquals(setter, ((IndexedPropertyDescriptor) (pd)).getIndexedWriteMethod());
            }
        }
    }

    public class MixedSimpleClass15 {
        public Object isList(int index) {
            return null;
        }

        public Object getList() {
            return null;
        }

        public void setList(int index, Object obj) {
        }

        public void setList(Object obj) {
        }
    }

    public void test_MixedSimpleClass15() throws Exception {
        BeanInfo info = Introspector.getBeanInfo(IntrospectorTest.MixedSimpleClass15.class);
        Method getter = IntrospectorTest.MixedSimpleClass15.class.getDeclaredMethod("getList");
        Method setter = IntrospectorTest.MixedSimpleClass15.class.getDeclaredMethod("setList", Object.class);
        for (PropertyDescriptor pd : info.getPropertyDescriptors()) {
            if (propertyName.equals(pd.getName())) {
                TestCase.assertFalse((pd instanceof IndexedPropertyDescriptor));
                TestCase.assertEquals(getter, pd.getReadMethod());
                TestCase.assertEquals(setter, pd.getWriteMethod());
            }
        }
    }

    public class MixedSimpleClass16 {
        public Object isList(int index) {
            return null;
        }

        public Object isList() {
            return null;
        }

        public void setList(int index, Object obj) {
        }

        public void setList(Object obj) {
        }
    }

    public void test_MixedSimpleClass16() throws Exception {
        BeanInfo info = Introspector.getBeanInfo(IntrospectorTest.MixedSimpleClass16.class);
        Method setter = IntrospectorTest.MixedSimpleClass16.class.getDeclaredMethod("setList", int.class, Object.class);
        for (PropertyDescriptor pd : info.getPropertyDescriptors()) {
            if (propertyName.equals(pd.getName())) {
                TestCase.assertTrue((pd instanceof IndexedPropertyDescriptor));
                TestCase.assertNull(pd.getReadMethod());
                TestCase.assertNull(pd.getWriteMethod());
                TestCase.assertNull(((IndexedPropertyDescriptor) (pd)).getIndexedReadMethod());
                TestCase.assertEquals(setter, ((IndexedPropertyDescriptor) (pd)).getIndexedWriteMethod());
            }
        }
    }

    public class MixedSimpleClass17 {
        public Object getList() {
            return null;
        }

        public void setList(int index, Object obj) {
        }

        public void setList(Object obj) {
        }
    }

    public void test_MixedSimpleClass17() throws Exception {
        BeanInfo info = Introspector.getBeanInfo(IntrospectorTest.MixedSimpleClass17.class);
        Method getter = IntrospectorTest.MixedSimpleClass17.class.getDeclaredMethod("getList");
        Method setter = IntrospectorTest.MixedSimpleClass17.class.getDeclaredMethod("setList", Object.class);
        for (PropertyDescriptor pd : info.getPropertyDescriptors()) {
            if (propertyName.equals(pd.getName())) {
                TestCase.assertFalse((pd instanceof IndexedPropertyDescriptor));
                TestCase.assertEquals(getter, pd.getReadMethod());
                TestCase.assertEquals(setter, pd.getWriteMethod());
            }
        }
    }

    public class MixedSimpleClass18 {
        public Object isList() {
            return null;
        }

        public void setList(int index, Object obj) {
        }

        public void setList(Object obj) {
        }
    }

    public void test_MixedSimpleClass18() throws Exception {
        BeanInfo info = Introspector.getBeanInfo(IntrospectorTest.MixedSimpleClass18.class);
        Method setter = IntrospectorTest.MixedSimpleClass18.class.getDeclaredMethod("setList", int.class, Object.class);
        for (PropertyDescriptor pd : info.getPropertyDescriptors()) {
            if (propertyName.equals(pd.getName())) {
                TestCase.assertTrue((pd instanceof IndexedPropertyDescriptor));
                TestCase.assertNull(pd.getReadMethod());
                TestCase.assertNull(pd.getWriteMethod());
                TestCase.assertNull(((IndexedPropertyDescriptor) (pd)).getIndexedReadMethod());
                TestCase.assertEquals(setter, ((IndexedPropertyDescriptor) (pd)).getIndexedWriteMethod());
            }
        }
    }

    public class MixedSimpleClass19 {
        public Object getList(int index) {
            return null;
        }

        public void setList(Object obj) {
        }

        public void setList(int index, Object obj) {
        }
    }

    public void test_MixedSimpleClass19() throws Exception {
        BeanInfo info = Introspector.getBeanInfo(IntrospectorTest.MixedSimpleClass19.class);
        Method getter = IntrospectorTest.MixedSimpleClass19.class.getDeclaredMethod("getList", int.class);
        Method setter = IntrospectorTest.MixedSimpleClass19.class.getDeclaredMethod("setList", int.class, Object.class);
        for (PropertyDescriptor pd : info.getPropertyDescriptors()) {
            if (propertyName.equals(pd.getName())) {
                TestCase.assertTrue((pd instanceof IndexedPropertyDescriptor));
                TestCase.assertNull(pd.getReadMethod());
                TestCase.assertNull(pd.getWriteMethod());
                TestCase.assertEquals(getter, ((IndexedPropertyDescriptor) (pd)).getIndexedReadMethod());
                TestCase.assertEquals(setter, ((IndexedPropertyDescriptor) (pd)).getIndexedWriteMethod());
            }
        }
    }

    public class MixedSimpleClass20 {
        public Object isList(int index) {
            return null;
        }

        public void setList(Object obj) {
        }

        public void setList(int index, Object obj) {
        }
    }

    public void test_MixedSimpleClass20() throws Exception {
        BeanInfo info = Introspector.getBeanInfo(IntrospectorTest.MixedSimpleClass20.class);
        Method setter = IntrospectorTest.MixedSimpleClass20.class.getDeclaredMethod("setList", int.class, Object.class);
        for (PropertyDescriptor pd : info.getPropertyDescriptors()) {
            if (propertyName.equals(pd.getName())) {
                TestCase.assertTrue((pd instanceof IndexedPropertyDescriptor));
                TestCase.assertNull(pd.getReadMethod());
                TestCase.assertNull(pd.getWriteMethod());
                TestCase.assertNull(((IndexedPropertyDescriptor) (pd)).getIndexedReadMethod());
                TestCase.assertEquals(setter, ((IndexedPropertyDescriptor) (pd)).getIndexedWriteMethod());
            }
        }
    }

    public class MixedSimpleClass21 {
        public Object getList(int index) {
            return null;
        }

        public void setList(Object obj) {
        }
    }

    public void test_MixedSimpleClass21() throws Exception {
        BeanInfo info = Introspector.getBeanInfo(IntrospectorTest.MixedSimpleClass21.class);
        Method getter = IntrospectorTest.MixedSimpleClass21.class.getDeclaredMethod("getList", int.class);
        for (PropertyDescriptor pd : info.getPropertyDescriptors()) {
            if (propertyName.equals(pd.getName())) {
                TestCase.assertTrue((pd instanceof IndexedPropertyDescriptor));
                TestCase.assertNull(pd.getReadMethod());
                TestCase.assertNull(pd.getWriteMethod());
                TestCase.assertEquals(getter, ((IndexedPropertyDescriptor) (pd)).getIndexedReadMethod());
                TestCase.assertNull(((IndexedPropertyDescriptor) (pd)).getIndexedWriteMethod());
            }
        }
    }

    public class MixedSimpleClass22 {
        public Object isList(int index) {
            return null;
        }

        public void setList(Object obj) {
        }
    }

    public void test_MixedSimpleClass22() throws Exception {
        BeanInfo info = Introspector.getBeanInfo(IntrospectorTest.MixedSimpleClass22.class);
        Method setter = IntrospectorTest.MixedSimpleClass22.class.getDeclaredMethod("setList", Object.class);
        for (PropertyDescriptor pd : info.getPropertyDescriptors()) {
            if (propertyName.equals(pd.getName())) {
                TestCase.assertFalse((pd instanceof IndexedPropertyDescriptor));
                TestCase.assertNull(pd.getReadMethod());
                TestCase.assertEquals(setter, pd.getWriteMethod());
            }
        }
    }

    public class MixedSimpleClass23 {
        public Object getList() {
            return null;
        }

        public void setList(int index, Object obj) {
        }
    }

    public void test_MixedSimpleClass23() throws Exception {
        BeanInfo info = Introspector.getBeanInfo(IntrospectorTest.MixedSimpleClass23.class);
        Method setter = IntrospectorTest.MixedSimpleClass23.class.getDeclaredMethod("setList", int.class, Object.class);
        for (PropertyDescriptor pd : info.getPropertyDescriptors()) {
            if (propertyName.equals(pd.getName())) {
                TestCase.assertTrue((pd instanceof IndexedPropertyDescriptor));
                TestCase.assertNull(pd.getReadMethod());
                TestCase.assertNull(pd.getWriteMethod());
                TestCase.assertNull(((IndexedPropertyDescriptor) (pd)).getIndexedReadMethod());
                TestCase.assertEquals(setter, ((IndexedPropertyDescriptor) (pd)).getIndexedWriteMethod());
            }
        }
    }

    public class MixedSimpleClass24 {
        public Object isList() {
            return null;
        }

        public void setList(int index, Object obj) {
        }
    }

    public void test_MixedSimpleClass24() throws Exception {
        BeanInfo info = Introspector.getBeanInfo(IntrospectorTest.MixedSimpleClass24.class);
        Method setter = IntrospectorTest.MixedSimpleClass24.class.getDeclaredMethod("setList", int.class, Object.class);
        for (PropertyDescriptor pd : info.getPropertyDescriptors()) {
            if (propertyName.equals(pd.getName())) {
                TestCase.assertTrue((pd instanceof IndexedPropertyDescriptor));
                TestCase.assertNull(pd.getReadMethod());
                TestCase.assertNull(pd.getWriteMethod());
                TestCase.assertNull(((IndexedPropertyDescriptor) (pd)).getIndexedReadMethod());
                TestCase.assertEquals(setter, ((IndexedPropertyDescriptor) (pd)).getIndexedWriteMethod());
            }
        }
    }

    public class MixedSimpleClass25 {
        public void setList(Object obj) {
        }

        public void setList(int index, Object obj) {
        }
    }

    public void test_MixedSimpleClass25() throws Exception {
        BeanInfo info = Introspector.getBeanInfo(IntrospectorTest.MixedSimpleClass25.class);
        Method setter = IntrospectorTest.MixedSimpleClass25.class.getDeclaredMethod("setList", int.class, Object.class);
        for (PropertyDescriptor pd : info.getPropertyDescriptors()) {
            if (propertyName.equals(pd.getName())) {
                TestCase.assertTrue((pd instanceof IndexedPropertyDescriptor));
                TestCase.assertNull(pd.getReadMethod());
                TestCase.assertNull(pd.getWriteMethod());
                TestCase.assertNull(((IndexedPropertyDescriptor) (pd)).getIndexedReadMethod());
                TestCase.assertEquals(setter, ((IndexedPropertyDescriptor) (pd)).getIndexedWriteMethod());
            }
        }
    }

    public class MixedSimpleClass26 {
        public Object[] getList() {
            return null;
        }

        public Object getList(int i) {
            return null;
        }
    }

    public void test_MixedSimpleClass26() throws Exception {
        BeanInfo info = Introspector.getBeanInfo(IntrospectorTest.MixedSimpleClass26.class);
        Method normalGetter = IntrospectorTest.MixedSimpleClass26.class.getDeclaredMethod("getList");
        Method indexedGetter = IntrospectorTest.MixedSimpleClass26.class.getDeclaredMethod("getList", int.class);
        for (PropertyDescriptor pd : info.getPropertyDescriptors()) {
            if (propertyName.equals(pd.getName())) {
                TestCase.assertTrue((pd instanceof IndexedPropertyDescriptor));
                TestCase.assertEquals(normalGetter, pd.getReadMethod());
                TestCase.assertNull(pd.getWriteMethod());
                TestCase.assertEquals(indexedGetter, ((IndexedPropertyDescriptor) (pd)).getIndexedReadMethod());
                TestCase.assertNull(((IndexedPropertyDescriptor) (pd)).getIndexedWriteMethod());
            }
        }
    }

    public class MixedSimpleClass27 {
        public Object[] isList() {
            return null;
        }

        public Object getList(int i) {
            return null;
        }
    }

    public void test_MixedSimpleClass27() throws Exception {
        BeanInfo info = Introspector.getBeanInfo(IntrospectorTest.MixedSimpleClass27.class);
        Method indexedGetter = IntrospectorTest.MixedSimpleClass27.class.getDeclaredMethod("getList", int.class);
        for (PropertyDescriptor pd : info.getPropertyDescriptors()) {
            if (propertyName.equals(pd.getName())) {
                TestCase.assertTrue((pd instanceof IndexedPropertyDescriptor));
                TestCase.assertNull(pd.getReadMethod());
                TestCase.assertNull(pd.getWriteMethod());
                TestCase.assertEquals(indexedGetter, ((IndexedPropertyDescriptor) (pd)).getIndexedReadMethod());
                TestCase.assertNull(((IndexedPropertyDescriptor) (pd)).getIndexedWriteMethod());
            }
        }
    }

    public class MixedSimpleClass28 {
        public Object[] getList() {
            return null;
        }

        public Object isList(int i) {
            return null;
        }
    }

    public void test_MixedSimpleClass28() throws Exception {
        BeanInfo info = Introspector.getBeanInfo(IntrospectorTest.MixedSimpleClass28.class);
        Method getter = IntrospectorTest.MixedSimpleClass28.class.getDeclaredMethod("getList");
        for (PropertyDescriptor pd : info.getPropertyDescriptors()) {
            if (propertyName.equals(pd.getName())) {
                TestCase.assertFalse((pd instanceof IndexedPropertyDescriptor));
                TestCase.assertEquals(getter, pd.getReadMethod());
                TestCase.assertNull(pd.getWriteMethod());
            }
        }
    }

    public class MixedSimpleClass29 {
        public Object[] isList() {
            return null;
        }

        public Object isList(int i) {
            return null;
        }
    }

    public void test_MixedSimpleClass29() throws Exception {
        BeanInfo info = Introspector.getBeanInfo(IntrospectorTest.MixedSimpleClass29.class);
        for (PropertyDescriptor pd : info.getPropertyDescriptors()) {
            TestCase.assertFalse(propertyName.equals(pd.getName()));
        }
    }

    public class MixedSimpleClass30 {
        public Object getList() {
            return null;
        }

        public Object[] getList(int i) {
            return null;
        }
    }

    public void test_MixedSimpleClass30() throws Exception {
        BeanInfo info = Introspector.getBeanInfo(IntrospectorTest.MixedSimpleClass30.class);
        Method indexedGetter = IntrospectorTest.MixedSimpleClass30.class.getDeclaredMethod("getList", int.class);
        for (PropertyDescriptor pd : info.getPropertyDescriptors()) {
            if (propertyName.equals(pd.getName())) {
                TestCase.assertTrue((pd instanceof IndexedPropertyDescriptor));
                TestCase.assertNull(pd.getReadMethod());
                TestCase.assertNull(pd.getWriteMethod());
                TestCase.assertEquals(indexedGetter, ((IndexedPropertyDescriptor) (pd)).getIndexedReadMethod());
                TestCase.assertNull(((IndexedPropertyDescriptor) (pd)).getIndexedWriteMethod());
            }
        }
    }

    public class MixedSimpleClass31 {
        public Object isList() {
            return null;
        }

        public Object[] getList(int i) {
            return null;
        }
    }

    public void test_MixedSimpleClass31() throws Exception {
        BeanInfo info = Introspector.getBeanInfo(IntrospectorTest.MixedSimpleClass31.class);
        Method indexedGetter = IntrospectorTest.MixedSimpleClass31.class.getDeclaredMethod("getList", int.class);
        for (PropertyDescriptor pd : info.getPropertyDescriptors()) {
            if (propertyName.equals(pd.getName())) {
                TestCase.assertTrue((pd instanceof IndexedPropertyDescriptor));
                TestCase.assertNull(pd.getReadMethod());
                TestCase.assertNull(pd.getWriteMethod());
                TestCase.assertEquals(indexedGetter, ((IndexedPropertyDescriptor) (pd)).getIndexedReadMethod());
                TestCase.assertNull(((IndexedPropertyDescriptor) (pd)).getIndexedWriteMethod());
            }
        }
    }

    public class MixedSimpleClass32 {
        public Object getList() {
            return null;
        }

        public Object[] isList(int i) {
            return null;
        }
    }

    public void test_MixedSimpleClass32() throws Exception {
        BeanInfo info = Introspector.getBeanInfo(IntrospectorTest.MixedSimpleClass32.class);
        Method getter = IntrospectorTest.MixedSimpleClass32.class.getDeclaredMethod("getList");
        for (PropertyDescriptor pd : info.getPropertyDescriptors()) {
            if (propertyName.equals(pd.getName())) {
                TestCase.assertFalse((pd instanceof IndexedPropertyDescriptor));
                TestCase.assertEquals(getter, pd.getReadMethod());
                TestCase.assertNull(pd.getWriteMethod());
            }
        }
    }

    public class MixedSimpleClass33 {
        public Object isList() {
            return null;
        }

        public Object[] isList(int i) {
            return null;
        }
    }

    public void test_MixedSimpleClass33() throws Exception {
        BeanInfo info = Introspector.getBeanInfo(IntrospectorTest.MixedSimpleClass33.class);
        for (PropertyDescriptor pd : info.getPropertyDescriptors()) {
            TestCase.assertFalse(propertyName.equals(pd.getName()));
        }
    }

    public class MixedSimpleClass34 {
        public Object[] getList() {
            return null;
        }

        public Object[] getList(int index) {
            return null;
        }
    }

    public void test_MixedSimpleClass34() throws Exception {
        BeanInfo info = Introspector.getBeanInfo(IntrospectorTest.MixedSimpleClass34.class);
        Method indexedGetter = IntrospectorTest.MixedSimpleClass34.class.getDeclaredMethod("getList", int.class);
        for (PropertyDescriptor pd : info.getPropertyDescriptors()) {
            if (propertyName.equals(pd.getName())) {
                TestCase.assertTrue((pd instanceof IndexedPropertyDescriptor));
                TestCase.assertNull(pd.getReadMethod());
                TestCase.assertNull(pd.getWriteMethod());
                TestCase.assertEquals(indexedGetter, ((IndexedPropertyDescriptor) (pd)).getIndexedReadMethod());
                TestCase.assertNull(((IndexedPropertyDescriptor) (pd)).getIndexedWriteMethod());
            }
        }
    }

    public class MixedSimpleClass35 {
        public Object[] isList() {
            return null;
        }

        public Object[] getList(int index) {
            return null;
        }
    }

    public void test_MixedSimpleClass35() throws Exception {
        BeanInfo info = Introspector.getBeanInfo(IntrospectorTest.MixedSimpleClass35.class);
        Method indexedGetter = IntrospectorTest.MixedSimpleClass35.class.getDeclaredMethod("getList", int.class);
        for (PropertyDescriptor pd : info.getPropertyDescriptors()) {
            if (propertyName.equals(pd.getName())) {
                TestCase.assertTrue((pd instanceof IndexedPropertyDescriptor));
                TestCase.assertNull(pd.getReadMethod());
                TestCase.assertNull(pd.getWriteMethod());
                TestCase.assertEquals(indexedGetter, ((IndexedPropertyDescriptor) (pd)).getIndexedReadMethod());
                TestCase.assertNull(((IndexedPropertyDescriptor) (pd)).getIndexedWriteMethod());
            }
        }
    }

    public class MixedSimpleClass36 {
        public Object[] getList() {
            return null;
        }

        public Object[] isList(int index) {
            return null;
        }
    }

    public void test_MixedSimpleClass36() throws Exception {
        BeanInfo info = Introspector.getBeanInfo(IntrospectorTest.MixedSimpleClass36.class);
        Method normalGetter = IntrospectorTest.MixedSimpleClass36.class.getDeclaredMethod("getList");
        for (PropertyDescriptor pd : info.getPropertyDescriptors()) {
            if (propertyName.equals(pd.getName())) {
                TestCase.assertFalse((pd instanceof IndexedPropertyDescriptor));
                TestCase.assertEquals(normalGetter, pd.getReadMethod());
                TestCase.assertNull(pd.getWriteMethod());
            }
        }
    }

    public class MixedSimpleClass37 {
        public Object[] isList() {
            return null;
        }

        public Object[] isList(int index) {
            return null;
        }
    }

    public void test_MixedSimpleClass37() throws Exception {
        BeanInfo info = Introspector.getBeanInfo(IntrospectorTest.MixedSimpleClass37.class);
        for (PropertyDescriptor pd : info.getPropertyDescriptors()) {
            TestCase.assertFalse(propertyName.equals(pd.getName()));
        }
    }

    public class MixedSimpleClass38 {
        public Object[][] getList() {
            return null;
        }

        public Object[] getList(int index) {
            return null;
        }
    }

    public void test_MixedSimpleClass38() throws Exception {
        BeanInfo info = Introspector.getBeanInfo(IntrospectorTest.MixedSimpleClass38.class);
        Method normalGetter = IntrospectorTest.MixedSimpleClass38.class.getDeclaredMethod("getList");
        Method indexedGetter = IntrospectorTest.MixedSimpleClass38.class.getDeclaredMethod("getList", int.class);
        for (PropertyDescriptor pd : info.getPropertyDescriptors()) {
            if (propertyName.equals(pd.getName())) {
                TestCase.assertTrue((pd instanceof IndexedPropertyDescriptor));
                TestCase.assertEquals(normalGetter, pd.getReadMethod());
                TestCase.assertNull(pd.getWriteMethod());
                TestCase.assertEquals(indexedGetter, ((IndexedPropertyDescriptor) (pd)).getIndexedReadMethod());
                TestCase.assertNull(((IndexedPropertyDescriptor) (pd)).getIndexedWriteMethod());
            }
        }
    }

    public class MixedSimpleClass39 {
        public boolean isList(int index) {
            return false;
        }
    }

    public void test_MixedSimpleClass39() throws Exception {
        BeanInfo info = Introspector.getBeanInfo(IntrospectorTest.MixedSimpleClass39.class);
        for (PropertyDescriptor pd : info.getPropertyDescriptors()) {
            TestCase.assertFalse(propertyName.equals(pd.getName()));
        }
    }

    public class MixedSimpleClass40 {
        public Object isList() {
            return null;
        }
    }

    public void test_MixedSimpleClass40() throws Exception {
        BeanInfo info = Introspector.getBeanInfo(IntrospectorTest.MixedSimpleClass40.class);
        for (PropertyDescriptor pd : info.getPropertyDescriptors()) {
            TestCase.assertFalse(propertyName.equals(pd.getName()));
        }
    }

    public class MixedSimpleClass41 {
        public Object getList() {
            return null;
        }

        public void setList(Object obj) {
        }
    }

    public void test_MixedSimpleClass41() throws Exception {
        BeanInfo info = Introspector.getBeanInfo(IntrospectorTest.MixedSimpleClass41.class);
        Method getter = IntrospectorTest.MixedSimpleClass41.class.getDeclaredMethod("getList");
        Method setter = IntrospectorTest.MixedSimpleClass41.class.getDeclaredMethod("setList", Object.class);
        for (PropertyDescriptor pd : info.getPropertyDescriptors()) {
            if (propertyName.equals(pd.getName())) {
                TestCase.assertFalse((pd instanceof IndexedPropertyDescriptor));
                TestCase.assertEquals(getter, pd.getReadMethod());
                TestCase.assertEquals(setter, pd.getWriteMethod());
            }
        }
    }

    public class MixedSimpleClass42 {
        public Object isList() {
            return null;
        }

        public void setList(Object obj) {
        }
    }

    public void test_MixedSimpleClass42() throws Exception {
        BeanInfo info = Introspector.getBeanInfo(IntrospectorTest.MixedSimpleClass42.class);
        Method setter = IntrospectorTest.MixedSimpleClass42.class.getDeclaredMethod("setList", Object.class);
        for (PropertyDescriptor pd : info.getPropertyDescriptors()) {
            if (propertyName.equals(pd.getName())) {
                TestCase.assertFalse((pd instanceof IndexedPropertyDescriptor));
                TestCase.assertNull(pd.getReadMethod());
                TestCase.assertEquals(setter, pd.getWriteMethod());
            }
        }
    }

    public class MixedSimpleClass43 {
        public Object getList() {
            return null;
        }
    }

    public void test_MixedSimpleClass43() throws Exception {
        BeanInfo info = Introspector.getBeanInfo(IntrospectorTest.MixedSimpleClass43.class);
        Method getter = IntrospectorTest.MixedSimpleClass43.class.getDeclaredMethod("getList");
        for (PropertyDescriptor pd : info.getPropertyDescriptors()) {
            if (propertyName.equals(pd.getName())) {
                TestCase.assertFalse((pd instanceof IndexedPropertyDescriptor));
                TestCase.assertEquals(getter, pd.getReadMethod());
                TestCase.assertNull(pd.getWriteMethod());
            }
        }
    }

    public class MixedSimpleClass44 {
        public void setList(Object obj) {
        }
    }

    public void test_MixedSimpleClass44() throws Exception {
        BeanInfo info = Introspector.getBeanInfo(IntrospectorTest.MixedSimpleClass44.class);
        Method setter = IntrospectorTest.MixedSimpleClass44.class.getDeclaredMethod("setList", Object.class);
        for (PropertyDescriptor pd : info.getPropertyDescriptors()) {
            if (propertyName.equals(pd.getName())) {
                TestCase.assertFalse((pd instanceof IndexedPropertyDescriptor));
                TestCase.assertNull(pd.getReadMethod());
                TestCase.assertEquals(setter, pd.getWriteMethod());
            }
        }
    }

    public class MixedSimpleClass45 {
        public boolean isList(int index) {
            return true;
        }
    }

    public void test_MixedSimpleClass45() throws Exception {
        BeanInfo beanInfo = Introspector.getBeanInfo(IntrospectorTest.MixedSimpleClass45.class);
        TestCase.assertEquals(1, beanInfo.getPropertyDescriptors().length);
    }

    public class MixedSimpleClass46 {
        public boolean getList() {
            return true;
        }

        public boolean isList(int index) {
            return true;
        }
    }

    public void test_MixedSimpleClass46() throws Exception {
        BeanInfo beanInfo = Introspector.getBeanInfo(IntrospectorTest.MixedSimpleClass46.class);
        Method getter = IntrospectorTest.MixedSimpleClass46.class.getMethod("getList", new Class<?>[]{  });
        TestCase.assertEquals(2, beanInfo.getPropertyDescriptors().length);
        for (PropertyDescriptor pd : beanInfo.getPropertyDescriptors()) {
            if (propertyName.equals(pd.getName())) {
                TestCase.assertEquals(getter, pd.getReadMethod());
                TestCase.assertNull(pd.getWriteMethod());
                TestCase.assertFalse((pd instanceof IndexedPropertyDescriptor));
            }
        }
    }

    public class MixedSimpleClass47 {
        public boolean isList() {
            return true;
        }

        public boolean isList(int index) {
            return true;
        }
    }

    public void test_MixedSimpleClass47() throws Exception {
        BeanInfo beanInfo = Introspector.getBeanInfo(IntrospectorTest.MixedSimpleClass47.class);
        Method getter = IntrospectorTest.MixedSimpleClass47.class.getMethod("isList", new Class<?>[]{  });
        TestCase.assertEquals(2, beanInfo.getPropertyDescriptors().length);
        for (PropertyDescriptor pd : beanInfo.getPropertyDescriptors()) {
            if (propertyName.equals(pd.getName())) {
                TestCase.assertEquals(getter, pd.getReadMethod());
                TestCase.assertNull(pd.getWriteMethod());
                TestCase.assertFalse((pd instanceof IndexedPropertyDescriptor));
            }
        }
    }

    public class MixedSimpleClass48 {
        public boolean getList(int index) {
            return true;
        }

        public boolean isList(int index) {
            return true;
        }
    }

    public void test_MixedSimpleClass48() throws Exception {
        BeanInfo beanInfo = Introspector.getBeanInfo(IntrospectorTest.MixedSimpleClass48.class);
        Method getter = IntrospectorTest.MixedSimpleClass48.class.getMethod("getList", new Class<?>[]{ int.class });
        TestCase.assertEquals(2, beanInfo.getPropertyDescriptors().length);
        for (PropertyDescriptor pd : beanInfo.getPropertyDescriptors()) {
            if (propertyName.equals(pd.getName())) {
                TestCase.assertNull(pd.getReadMethod());
                TestCase.assertNull(pd.getWriteMethod());
                TestCase.assertTrue((pd instanceof IndexedPropertyDescriptor));
                TestCase.assertEquals(getter, ((IndexedPropertyDescriptor) (pd)).getIndexedReadMethod());
                TestCase.assertNull(((IndexedPropertyDescriptor) (pd)).getIndexedWriteMethod());
            }
        }
    }

    public class MixedSimpleClass49 {
        public boolean isList(int index) {
            return true;
        }

        public void setList(boolean bool) {
        }
    }

    public void test_MixedSimpleClass49() throws Exception {
        BeanInfo beanInfo = Introspector.getBeanInfo(IntrospectorTest.MixedSimpleClass49.class);
        Method setter = IntrospectorTest.MixedSimpleClass49.class.getMethod("setList", new Class<?>[]{ boolean.class });
        TestCase.assertEquals(2, beanInfo.getPropertyDescriptors().length);
        for (PropertyDescriptor pd : beanInfo.getPropertyDescriptors()) {
            if (propertyName.equals(pd.getName())) {
                TestCase.assertNull(pd.getReadMethod());
                TestCase.assertEquals(setter, pd.getWriteMethod());
                TestCase.assertFalse((pd instanceof IndexedPropertyDescriptor));
            }
        }
    }

    public class MixedSimpleClass50 {
        public boolean isList(int index) {
            return true;
        }

        public void setList(int index, boolean bool) {
        }
    }

    public void test_MixedSimpleClass50() throws Exception {
        BeanInfo beanInfo = Introspector.getBeanInfo(IntrospectorTest.MixedSimpleClass50.class);
        Method setter = IntrospectorTest.MixedSimpleClass50.class.getMethod("setList", new Class<?>[]{ int.class, boolean.class });
        TestCase.assertEquals(2, beanInfo.getPropertyDescriptors().length);
        for (PropertyDescriptor pd : beanInfo.getPropertyDescriptors()) {
            if (propertyName.equals(pd.getName())) {
                TestCase.assertNull(pd.getReadMethod());
                TestCase.assertNull(pd.getWriteMethod());
                TestCase.assertTrue((pd instanceof IndexedPropertyDescriptor));
                TestCase.assertNull(((IndexedPropertyDescriptor) (pd)).getIndexedReadMethod());
                TestCase.assertEquals(setter, ((IndexedPropertyDescriptor) (pd)).getIndexedWriteMethod());
            }
        }
    }

    public class MixedSimpleClass51 {
        public boolean getList() {
            return true;
        }

        public boolean isList(int index) {
            return true;
        }

        public void setList(boolean bool) {
        }
    }

    public void test_MixedSimpleClass51() throws Exception {
        BeanInfo beanInfo = Introspector.getBeanInfo(IntrospectorTest.MixedSimpleClass51.class);
        Method getter = IntrospectorTest.MixedSimpleClass51.class.getMethod("getList", new Class<?>[]{  });
        Method setter = IntrospectorTest.MixedSimpleClass51.class.getMethod("setList", new Class<?>[]{ boolean.class });
        TestCase.assertEquals(2, beanInfo.getPropertyDescriptors().length);
        for (PropertyDescriptor pd : beanInfo.getPropertyDescriptors()) {
            if (propertyName.equals(pd.getName())) {
                TestCase.assertEquals(getter, pd.getReadMethod());
                TestCase.assertEquals(setter, pd.getWriteMethod());
                TestCase.assertFalse((pd instanceof IndexedPropertyDescriptor));
            }
        }
    }

    public class MixedSimpleClass52 {
        public boolean getList() {
            return true;
        }

        public boolean isList(int index) {
            return true;
        }

        public void setList(int index, boolean bool) {
        }
    }

    public void test_MixedSimpleClass52() throws Exception {
        BeanInfo beanInfo = Introspector.getBeanInfo(IntrospectorTest.MixedSimpleClass52.class);
        Method setter = IntrospectorTest.MixedSimpleClass52.class.getMethod("setList", new Class<?>[]{ int.class, boolean.class });
        TestCase.assertEquals(2, beanInfo.getPropertyDescriptors().length);
        for (PropertyDescriptor pd : beanInfo.getPropertyDescriptors()) {
            if (propertyName.equals(pd.getName())) {
                TestCase.assertNull(pd.getReadMethod());
                TestCase.assertNull(pd.getWriteMethod());
                TestCase.assertTrue((pd instanceof IndexedPropertyDescriptor));
                TestCase.assertNull(((IndexedPropertyDescriptor) (pd)).getIndexedReadMethod());
                TestCase.assertEquals(setter, ((IndexedPropertyDescriptor) (pd)).getIndexedWriteMethod());
            }
        }
    }

    public class MixedSimpleClass53 {
        public boolean isList() {
            return true;
        }

        public boolean isList(int index) {
            return true;
        }

        public void setList(boolean bool) {
        }
    }

    public void test_MixedSimpleClass53() throws Exception {
        BeanInfo beanInfo = Introspector.getBeanInfo(IntrospectorTest.MixedSimpleClass53.class);
        Method getter = IntrospectorTest.MixedSimpleClass53.class.getMethod("isList", new Class<?>[]{  });
        Method setter = IntrospectorTest.MixedSimpleClass53.class.getMethod("setList", new Class<?>[]{ boolean.class });
        TestCase.assertEquals(2, beanInfo.getPropertyDescriptors().length);
        for (PropertyDescriptor pd : beanInfo.getPropertyDescriptors()) {
            if (propertyName.equals(pd.getName())) {
                TestCase.assertEquals(getter, pd.getReadMethod());
                TestCase.assertEquals(setter, pd.getWriteMethod());
                TestCase.assertFalse((pd instanceof IndexedPropertyDescriptor));
            }
        }
    }

    public class MixedSimpleClass54 {
        public boolean isList() {
            return true;
        }

        public boolean isList(int index) {
            return true;
        }

        public void setList(int index, boolean bool) {
        }
    }

    public void test_MixedSimpleClass54() throws Exception {
        BeanInfo beanInfo = Introspector.getBeanInfo(IntrospectorTest.MixedSimpleClass54.class);
        Method setter = IntrospectorTest.MixedSimpleClass54.class.getMethod("setList", new Class<?>[]{ int.class, boolean.class });
        TestCase.assertEquals(2, beanInfo.getPropertyDescriptors().length);
        for (PropertyDescriptor pd : beanInfo.getPropertyDescriptors()) {
            if (propertyName.equals(pd.getName())) {
                TestCase.assertNull(pd.getReadMethod());
                TestCase.assertNull(pd.getWriteMethod());
                TestCase.assertTrue((pd instanceof IndexedPropertyDescriptor));
                TestCase.assertNull(((IndexedPropertyDescriptor) (pd)).getIndexedReadMethod());
                TestCase.assertEquals(setter, ((IndexedPropertyDescriptor) (pd)).getIndexedWriteMethod());
            }
        }
    }

    public class MixedSimpleClass55 {
        public boolean getList(int index) {
            return true;
        }

        public boolean isList(int index) {
            return true;
        }

        public void setList(boolean bool) {
        }
    }

    public void test_MixedSimpleClass55() throws Exception {
        BeanInfo beanInfo = Introspector.getBeanInfo(IntrospectorTest.MixedSimpleClass55.class);
        Method getter = IntrospectorTest.MixedSimpleClass55.class.getMethod("getList", new Class<?>[]{ int.class });
        for (PropertyDescriptor pd : beanInfo.getPropertyDescriptors()) {
            if (propertyName.equals(pd.getName())) {
                TestCase.assertNull(pd.getReadMethod());
                TestCase.assertNull(pd.getWriteMethod());
                TestCase.assertTrue((pd instanceof IndexedPropertyDescriptor));
                TestCase.assertEquals(getter, ((IndexedPropertyDescriptor) (pd)).getIndexedReadMethod());
                TestCase.assertNull(((IndexedPropertyDescriptor) (pd)).getIndexedWriteMethod());
            }
        }
    }

    public class MixedSimpleClass56 {
        public boolean getList(int index) {
            return true;
        }

        public boolean isList(int index) {
            return true;
        }

        public void setList(int index, boolean bool) {
        }
    }

    public void test_MixedSimpleClass56() throws Exception {
        BeanInfo beanInfo = Introspector.getBeanInfo(IntrospectorTest.MixedSimpleClass56.class);
        Method getter = IntrospectorTest.MixedSimpleClass56.class.getMethod("getList", new Class<?>[]{ int.class });
        Method setter = IntrospectorTest.MixedSimpleClass56.class.getMethod("setList", new Class<?>[]{ int.class, boolean.class });
        TestCase.assertEquals(2, beanInfo.getPropertyDescriptors().length);
        for (PropertyDescriptor pd : beanInfo.getPropertyDescriptors()) {
            if (propertyName.equals(pd.getName())) {
                TestCase.assertNull(pd.getReadMethod());
                TestCase.assertNull(pd.getWriteMethod());
                TestCase.assertTrue((pd instanceof IndexedPropertyDescriptor));
                TestCase.assertEquals(getter, ((IndexedPropertyDescriptor) (pd)).getIndexedReadMethod());
                TestCase.assertEquals(setter, ((IndexedPropertyDescriptor) (pd)).getIndexedWriteMethod());
            }
        }
    }

    public class MixedSimpleClass57 {
        public boolean isList(int index) {
            return true;
        }

        public void setList(boolean bool) {
        }

        public void setList(int index, boolean bool) {
        }
    }

    public void test_MixedSimpleClass57() throws Exception {
        BeanInfo beanInfo = Introspector.getBeanInfo(IntrospectorTest.MixedSimpleClass57.class);
        Method setter = IntrospectorTest.MixedSimpleClass57.class.getMethod("setList", new Class<?>[]{ int.class, boolean.class });
        TestCase.assertEquals(2, beanInfo.getPropertyDescriptors().length);
        for (PropertyDescriptor pd : beanInfo.getPropertyDescriptors()) {
            if (propertyName.equals(pd.getName())) {
                TestCase.assertNull(pd.getReadMethod());
                TestCase.assertNull(pd.getWriteMethod());
                TestCase.assertTrue((pd instanceof IndexedPropertyDescriptor));
                TestCase.assertNull(((IndexedPropertyDescriptor) (pd)).getIndexedReadMethod());
                TestCase.assertEquals(setter, ((IndexedPropertyDescriptor) (pd)).getIndexedWriteMethod());
            }
        }
    }

    public class MixedSimpleClass58 {
        public boolean getList() {
            return true;
        }

        public boolean isList(int index) {
            return true;
        }

        public void setList(boolean bool) {
        }

        public void setList(int index, boolean bool) {
        }
    }

    public void test_MixedSimpleClass58() throws Exception {
        BeanInfo beanInfo = Introspector.getBeanInfo(IntrospectorTest.MixedSimpleClass58.class);
        Method getter = IntrospectorTest.MixedSimpleClass58.class.getMethod("getList", new Class<?>[]{  });
        Method setter = IntrospectorTest.MixedSimpleClass58.class.getMethod("setList", new Class<?>[]{ boolean.class });
        TestCase.assertEquals(2, beanInfo.getPropertyDescriptors().length);
        for (PropertyDescriptor pd : beanInfo.getPropertyDescriptors()) {
            if (propertyName.equals(pd.getName())) {
                TestCase.assertEquals(getter, pd.getReadMethod());
                TestCase.assertEquals(setter, pd.getWriteMethod());
                TestCase.assertFalse((pd instanceof IndexedPropertyDescriptor));
            }
        }
    }

    public class MixedSimpleClass59 {
        public boolean isList() {
            return true;
        }

        public boolean isList(int index) {
            return true;
        }

        public void setList(boolean bool) {
        }

        public void setList(int index, boolean bool) {
        }
    }

    public void test_MixedSimpleClass59() throws Exception {
        BeanInfo beanInfo = Introspector.getBeanInfo(IntrospectorTest.MixedSimpleClass59.class);
        Method getter = IntrospectorTest.MixedSimpleClass59.class.getMethod("isList", new Class<?>[]{  });
        Method setter = IntrospectorTest.MixedSimpleClass59.class.getMethod("setList", new Class<?>[]{ boolean.class });
        TestCase.assertEquals(2, beanInfo.getPropertyDescriptors().length);
        for (PropertyDescriptor pd : beanInfo.getPropertyDescriptors()) {
            if (propertyName.equals(pd.getName())) {
                TestCase.assertEquals(getter, pd.getReadMethod());
                TestCase.assertEquals(setter, pd.getWriteMethod());
                TestCase.assertFalse((pd instanceof IndexedPropertyDescriptor));
            }
        }
    }

    public class MixedSimpleClass60 {
        public boolean getList(int index) {
            return true;
        }

        public boolean isList(int index) {
            return true;
        }

        public void setList(boolean bool) {
        }

        public void setList(int index, boolean bool) {
        }
    }

    public void test_MixedSimpleClass60() throws Exception {
        BeanInfo beanInfo = Introspector.getBeanInfo(IntrospectorTest.MixedSimpleClass60.class);
        Method getter = IntrospectorTest.MixedSimpleClass60.class.getMethod("getList", new Class<?>[]{ int.class });
        Method setter = IntrospectorTest.MixedSimpleClass60.class.getMethod("setList", new Class<?>[]{ int.class, boolean.class });
        TestCase.assertEquals(2, beanInfo.getPropertyDescriptors().length);
        for (PropertyDescriptor pd : beanInfo.getPropertyDescriptors()) {
            if (propertyName.equals(pd.getName())) {
                TestCase.assertNull(pd.getReadMethod());
                TestCase.assertNull(pd.getWriteMethod());
                TestCase.assertTrue((pd instanceof IndexedPropertyDescriptor));
                TestCase.assertEquals(getter, ((IndexedPropertyDescriptor) (pd)).getIndexedReadMethod());
                TestCase.assertEquals(setter, ((IndexedPropertyDescriptor) (pd)).getIndexedWriteMethod());
            }
        }
    }

    public class MixedSimpleClass61 {
        public boolean getList() {
            return true;
        }

        public boolean getList(int index) {
            return true;
        }

        public boolean isList() {
            return true;
        }

        public boolean isList(int index) {
            return true;
        }

        public void setList(boolean bool) {
        }

        public void setList(int index, boolean bool) {
        }
    }

    public void test_MixedSimpleClass61() throws Exception {
        BeanInfo beanInfo = Introspector.getBeanInfo(IntrospectorTest.MixedSimpleClass61.class);
        Method getter = IntrospectorTest.MixedSimpleClass61.class.getMethod("getList", new Class<?>[]{ int.class });
        Method setter = IntrospectorTest.MixedSimpleClass61.class.getMethod("setList", new Class<?>[]{ int.class, boolean.class });
        TestCase.assertEquals(2, beanInfo.getPropertyDescriptors().length);
        for (PropertyDescriptor pd : beanInfo.getPropertyDescriptors()) {
            if (propertyName.equals(pd.getName())) {
                TestCase.assertNull(pd.getReadMethod());
                TestCase.assertNull(pd.getWriteMethod());
                TestCase.assertTrue((pd instanceof IndexedPropertyDescriptor));
                TestCase.assertEquals(getter, ((IndexedPropertyDescriptor) (pd)).getIndexedReadMethod());
                TestCase.assertEquals(setter, ((IndexedPropertyDescriptor) (pd)).getIndexedWriteMethod());
            }
        }
    }

    public class MixedExtendClass1 extends IntrospectorTest.MixedSimpleClass4 {
        public void setList(Object a) {
        }
    }

    public void test_MixedExtendClass1() throws Exception {
        BeanInfo info = Introspector.getBeanInfo(IntrospectorTest.MixedExtendClass1.class);
        Method getter = IntrospectorTest.MixedSimpleClass4.class.getDeclaredMethod("getList");
        Method setter = IntrospectorTest.MixedExtendClass1.class.getDeclaredMethod("setList", Object.class);
        for (PropertyDescriptor pd : info.getPropertyDescriptors()) {
            if (propertyName.equals(pd.getName())) {
                TestCase.assertFalse((pd instanceof IndexedPropertyDescriptor));
                TestCase.assertEquals(getter, pd.getReadMethod());
                TestCase.assertEquals(setter, pd.getWriteMethod());
                break;
            }
        }
    }

    public class MixedExtendClass2 extends IntrospectorTest.MixedSimpleClass4 {
        public void setList(int index, Object a) {
        }
    }

    public void test_MixedExtendClass2() throws Exception {
        BeanInfo info = Introspector.getBeanInfo(IntrospectorTest.MixedExtendClass2.class);
        Method getter = IntrospectorTest.MixedSimpleClass4.class.getDeclaredMethod("getList", int.class);
        Method setter = IntrospectorTest.MixedExtendClass2.class.getDeclaredMethod("setList", int.class, Object.class);
        for (PropertyDescriptor pd : info.getPropertyDescriptors()) {
            if (propertyName.equals(pd.getName())) {
                TestCase.assertTrue((pd instanceof IndexedPropertyDescriptor));
                TestCase.assertNull(pd.getReadMethod());
                TestCase.assertNull(pd.getWriteMethod());
                TestCase.assertEquals(getter, ((IndexedPropertyDescriptor) (pd)).getIndexedReadMethod());
                TestCase.assertEquals(setter, ((IndexedPropertyDescriptor) (pd)).getIndexedWriteMethod());
                break;
            }
        }
    }

    public class MixedExtendClass3 extends IntrospectorTest.MixedSimpleClass4 {
        public void setList(Object a) {
        }

        public void setList(int index, Object a) {
        }
    }

    public void test_MixedExtendClass3() throws Exception {
        BeanInfo info = Introspector.getBeanInfo(IntrospectorTest.MixedExtendClass3.class);
        Method getter = IntrospectorTest.MixedSimpleClass4.class.getDeclaredMethod("getList", int.class);
        Method setter = IntrospectorTest.MixedExtendClass3.class.getDeclaredMethod("setList", int.class, Object.class);
        for (PropertyDescriptor pd : info.getPropertyDescriptors()) {
            if (propertyName.equals(pd.getName())) {
                TestCase.assertTrue((pd instanceof IndexedPropertyDescriptor));
                TestCase.assertNull(pd.getReadMethod());
                TestCase.assertNull(pd.getWriteMethod());
                TestCase.assertEquals(getter, ((IndexedPropertyDescriptor) (pd)).getIndexedReadMethod());
                TestCase.assertEquals(setter, ((IndexedPropertyDescriptor) (pd)).getIndexedWriteMethod());
                break;
            }
        }
    }

    public class MixedExtendClass4 extends IntrospectorTest.MixedSimpleClass4 {
        public Object getList() {
            return null;
        }
    }

    public void test_MixedExtendClass4() throws Exception {
        BeanInfo info = Introspector.getBeanInfo(IntrospectorTest.MixedExtendClass4.class);
        Method getter = IntrospectorTest.MixedExtendClass4.class.getDeclaredMethod("getList");
        for (PropertyDescriptor pd : info.getPropertyDescriptors()) {
            if (propertyName.equals(pd.getName())) {
                TestCase.assertFalse((pd instanceof IndexedPropertyDescriptor));
                TestCase.assertEquals(getter, pd.getReadMethod());
                TestCase.assertNull(pd.getWriteMethod());
                break;
            }
        }
    }

    public class MixedExtendClass5 extends IntrospectorTest.MixedSimpleClass4 {
        public Object getList(int index) {
            return null;
        }
    }

    public void test_MixedExtendClass5() throws Exception {
        BeanInfo info = Introspector.getBeanInfo(IntrospectorTest.MixedExtendClass5.class);
        Method getter = IntrospectorTest.MixedExtendClass5.class.getDeclaredMethod("getList", int.class);
        for (PropertyDescriptor pd : info.getPropertyDescriptors()) {
            if (propertyName.equals(pd.getName())) {
                TestCase.assertTrue((pd instanceof IndexedPropertyDescriptor));
                TestCase.assertNull(pd.getReadMethod());
                TestCase.assertNull(pd.getWriteMethod());
                TestCase.assertEquals(getter, ((IndexedPropertyDescriptor) (pd)).getIndexedReadMethod());
                TestCase.assertNull(((IndexedPropertyDescriptor) (pd)).getIndexedWriteMethod());
                break;
            }
        }
    }

    public class MixedExtendClass6 extends IntrospectorTest.MixedSimpleClass25 {
        public Object getList() {
            return null;
        }
    }

    public void test_MixedExtendClass6() throws Exception {
        BeanInfo info = Introspector.getBeanInfo(IntrospectorTest.MixedExtendClass6.class);
        Method getter = IntrospectorTest.MixedExtendClass6.class.getDeclaredMethod("getList");
        Method setter = IntrospectorTest.MixedSimpleClass25.class.getDeclaredMethod("setList", Object.class);
        for (PropertyDescriptor pd : info.getPropertyDescriptors()) {
            if (propertyName.equals(pd.getName())) {
                TestCase.assertFalse((pd instanceof IndexedPropertyDescriptor));
                TestCase.assertEquals(getter, pd.getReadMethod());
                TestCase.assertEquals(setter, pd.getWriteMethod());
                break;
            }
        }
    }

    public class MixedExtendClass7 extends IntrospectorTest.MixedSimpleClass25 {
        public Object getList(int index) {
            return null;
        }
    }

    public void test_MixedExtendClass7() throws Exception {
        BeanInfo info = Introspector.getBeanInfo(IntrospectorTest.MixedExtendClass7.class);
        Method getter = IntrospectorTest.MixedExtendClass7.class.getDeclaredMethod("getList", int.class);
        Method setter = IntrospectorTest.MixedSimpleClass25.class.getDeclaredMethod("setList", int.class, Object.class);
        for (PropertyDescriptor pd : info.getPropertyDescriptors()) {
            if (propertyName.equals(pd.getName())) {
                TestCase.assertTrue((pd instanceof IndexedPropertyDescriptor));
                TestCase.assertNull(pd.getReadMethod());
                TestCase.assertNull(pd.getWriteMethod());
                TestCase.assertEquals(getter, ((IndexedPropertyDescriptor) (pd)).getIndexedReadMethod());
                TestCase.assertEquals(setter, ((IndexedPropertyDescriptor) (pd)).getIndexedWriteMethod());
                break;
            }
        }
    }

    public class MixedExtendClass8 extends IntrospectorTest.MixedSimpleClass25 {
        public Object getList() {
            return null;
        }

        public Object getList(int index) {
            return null;
        }
    }

    public void test_MixedExtendClass8() throws Exception {
        BeanInfo info = Introspector.getBeanInfo(IntrospectorTest.MixedExtendClass8.class);
        Method getter = IntrospectorTest.MixedExtendClass8.class.getDeclaredMethod("getList", int.class);
        Method setter = IntrospectorTest.MixedSimpleClass25.class.getDeclaredMethod("setList", int.class, Object.class);
        for (PropertyDescriptor pd : info.getPropertyDescriptors()) {
            if (propertyName.equals(pd.getName())) {
                TestCase.assertTrue((pd instanceof IndexedPropertyDescriptor));
                TestCase.assertNull(pd.getReadMethod());
                TestCase.assertNull(pd.getWriteMethod());
                TestCase.assertEquals(getter, ((IndexedPropertyDescriptor) (pd)).getIndexedReadMethod());
                TestCase.assertEquals(setter, ((IndexedPropertyDescriptor) (pd)).getIndexedWriteMethod());
                break;
            }
        }
    }

    public class MixedExtendClass9 extends IntrospectorTest.MixedSimpleClass25 {
        public void setList(Object obj) {
        }
    }

    public void test_MixedExtendClass9() throws Exception {
        BeanInfo info = Introspector.getBeanInfo(IntrospectorTest.MixedExtendClass9.class);
        Method setter = IntrospectorTest.MixedExtendClass9.class.getDeclaredMethod("setList", Object.class);
        for (PropertyDescriptor pd : info.getPropertyDescriptors()) {
            if (propertyName.equals(pd.getName())) {
                TestCase.assertFalse((pd instanceof IndexedPropertyDescriptor));
                TestCase.assertNull(pd.getReadMethod());
                TestCase.assertEquals(setter, pd.getWriteMethod());
                break;
            }
        }
    }

    public class MixedExtendClass10 extends IntrospectorTest.MixedSimpleClass25 {
        public void setList(int index, Object obj) {
        }
    }

    public void test_MixedExtendClass10() throws Exception {
        BeanInfo info = Introspector.getBeanInfo(IntrospectorTest.MixedExtendClass10.class);
        Method setter = IntrospectorTest.MixedExtendClass10.class.getDeclaredMethod("setList", int.class, Object.class);
        for (PropertyDescriptor pd : info.getPropertyDescriptors()) {
            if (propertyName.equals(pd.getName())) {
                TestCase.assertTrue((pd instanceof IndexedPropertyDescriptor));
                TestCase.assertNull(pd.getReadMethod());
                TestCase.assertNull(pd.getWriteMethod());
                TestCase.assertNull(((IndexedPropertyDescriptor) (pd)).getIndexedReadMethod());
                TestCase.assertEquals(setter, ((IndexedPropertyDescriptor) (pd)).getIndexedWriteMethod());
                break;
            }
        }
    }

    public class MixedExtendClass11 extends IntrospectorTest.MixedSimpleClass41 {
        public void setList(String obj) {
        }
    }

    public void test_MixedExtendClass11() throws Exception {
        BeanInfo info = Introspector.getBeanInfo(IntrospectorTest.MixedExtendClass11.class);
        Method getter = IntrospectorTest.MixedSimpleClass41.class.getDeclaredMethod("getList");
        Method setter = IntrospectorTest.MixedSimpleClass41.class.getDeclaredMethod("setList", Object.class);
        for (PropertyDescriptor pd : info.getPropertyDescriptors()) {
            if (propertyName.equals(pd.getName())) {
                TestCase.assertFalse((pd instanceof IndexedPropertyDescriptor));
                TestCase.assertEquals(getter, pd.getReadMethod());
                TestCase.assertEquals(setter, pd.getWriteMethod());
            }
        }
    }

    public class MixedExtendClass13 extends IntrospectorTest.MixedSimpleClass42 {
        public void setList(String obj) {
        }
    }

    public void test_MixedExtendClass13() throws Exception {
        BeanInfo info = Introspector.getBeanInfo(IntrospectorTest.MixedExtendClass13.class);
        Method setter = IntrospectorTest.MixedExtendClass13.class.getDeclaredMethod("setList", String.class);
        for (PropertyDescriptor pd : info.getPropertyDescriptors()) {
            if (propertyName.equals(pd.getName())) {
                TestCase.assertFalse((pd instanceof IndexedPropertyDescriptor));
                TestCase.assertNull(pd.getReadMethod());
                TestCase.assertEquals(setter, pd.getWriteMethod());
            }
        }
    }

    public class MixedExtendClass15 extends IntrospectorTest.MixedSimpleClass44 {
        public void setList(String obj) {
        }
    }

    public void test_MixedExtendClass15() throws Exception {
        BeanInfo info = Introspector.getBeanInfo(IntrospectorTest.MixedExtendClass15.class);
        Method setter = IntrospectorTest.MixedExtendClass15.class.getDeclaredMethod("setList", String.class);
        for (PropertyDescriptor pd : info.getPropertyDescriptors()) {
            if (propertyName.equals(pd.getName())) {
                TestCase.assertFalse((pd instanceof IndexedPropertyDescriptor));
                TestCase.assertNull(pd.getReadMethod());
                TestCase.assertEquals(setter, pd.getWriteMethod());
            }
        }
    }

    public class MixedBooleanSimpleClass1 {
        public boolean isList(int index) {
            return false;
        }

        public boolean isList() {
            return false;
        }
    }

    public void test_MixedBooleanSimpleClass1() throws Exception {
        BeanInfo info = Introspector.getBeanInfo(IntrospectorTest.MixedBooleanSimpleClass1.class);
        Method getter = IntrospectorTest.MixedBooleanSimpleClass1.class.getDeclaredMethod("isList");
        for (PropertyDescriptor pd : info.getPropertyDescriptors()) {
            if (propertyName.equals(pd.getName())) {
                TestCase.assertFalse((pd instanceof IndexedPropertyDescriptor));
                TestCase.assertEquals(getter, pd.getReadMethod());
                TestCase.assertNull(pd.getWriteMethod());
            }
        }
    }

    public class MixedBooleanSimpleClass2 {
        public boolean isList(int index) {
            return false;
        }

        public boolean getList() {
            return false;
        }
    }

    public void test_MixedBooleanSimpleClass2() throws Exception {
        BeanInfo info = Introspector.getBeanInfo(IntrospectorTest.MixedBooleanSimpleClass2.class);
        Method getter = IntrospectorTest.MixedBooleanSimpleClass2.class.getDeclaredMethod("getList");
        for (PropertyDescriptor pd : info.getPropertyDescriptors()) {
            if (propertyName.equals(pd.getName())) {
                TestCase.assertFalse((pd instanceof IndexedPropertyDescriptor));
                TestCase.assertEquals(getter, pd.getReadMethod());
                TestCase.assertNull(pd.getWriteMethod());
            }
        }
    }

    public class MixedBooleanSimpleClass3 {
        public boolean getList(int index) {
            return false;
        }

        public boolean isList() {
            return false;
        }
    }

    public void test_MixedBooleanSimpleClass3() throws Exception {
        BeanInfo info = Introspector.getBeanInfo(IntrospectorTest.MixedBooleanSimpleClass3.class);
        Method getter = IntrospectorTest.MixedBooleanSimpleClass3.class.getDeclaredMethod("getList", int.class);
        for (PropertyDescriptor pd : info.getPropertyDescriptors()) {
            if (propertyName.equals(pd.getName())) {
                TestCase.assertTrue((pd instanceof IndexedPropertyDescriptor));
                TestCase.assertNull(pd.getReadMethod());
                TestCase.assertNull(pd.getWriteMethod());
                TestCase.assertEquals(getter, ((IndexedPropertyDescriptor) (pd)).getIndexedReadMethod());
                TestCase.assertNull(((IndexedPropertyDescriptor) (pd)).getIndexedWriteMethod());
            }
        }
    }

    public class MixedBooleanSimpleClass4 {
        public boolean getList(int index) {
            return false;
        }

        public boolean getList() {
            return false;
        }
    }

    public void test_MixedBooleanSimpleClass4() throws Exception {
        BeanInfo info = Introspector.getBeanInfo(IntrospectorTest.MixedBooleanSimpleClass4.class);
        Method getter = IntrospectorTest.MixedBooleanSimpleClass4.class.getDeclaredMethod("getList", int.class);
        for (PropertyDescriptor pd : info.getPropertyDescriptors()) {
            if (propertyName.equals(pd.getName())) {
                TestCase.assertTrue((pd instanceof IndexedPropertyDescriptor));
                TestCase.assertNull(pd.getReadMethod());
                TestCase.assertNull(pd.getWriteMethod());
                TestCase.assertEquals(getter, ((IndexedPropertyDescriptor) (pd)).getIndexedReadMethod());
                TestCase.assertNull(((IndexedPropertyDescriptor) (pd)).getIndexedWriteMethod());
            }
        }
    }

    public class MixedBooleanSimpleClass5 {
        public boolean isList(int index) {
            return false;
        }

        public boolean isList() {
            return false;
        }

        public void setList(boolean b) {
        }
    }

    public void test_MixedBooleanSimpleClass5() throws Exception {
        BeanInfo info = Introspector.getBeanInfo(IntrospectorTest.MixedBooleanSimpleClass5.class);
        Method getter = IntrospectorTest.MixedBooleanSimpleClass5.class.getDeclaredMethod("isList");
        Method setter = IntrospectorTest.MixedBooleanSimpleClass5.class.getDeclaredMethod("setList", boolean.class);
        for (PropertyDescriptor pd : info.getPropertyDescriptors()) {
            if (propertyName.equals(pd.getName())) {
                TestCase.assertFalse((pd instanceof IndexedPropertyDescriptor));
                TestCase.assertEquals(getter, pd.getReadMethod());
                TestCase.assertEquals(setter, pd.getWriteMethod());
            }
        }
    }

    public class MixedBooleanSimpleClass6 {
        public boolean isList(int index) {
            return false;
        }

        public boolean getList() {
            return false;
        }

        public void setList(boolean b) {
        }
    }

    public void test_MixedBooleanSimpleClass6() throws Exception {
        BeanInfo info = Introspector.getBeanInfo(IntrospectorTest.MixedBooleanSimpleClass6.class);
        Method getter = IntrospectorTest.MixedBooleanSimpleClass6.class.getDeclaredMethod("getList");
        Method setter = IntrospectorTest.MixedBooleanSimpleClass6.class.getDeclaredMethod("setList", boolean.class);
        for (PropertyDescriptor pd : info.getPropertyDescriptors()) {
            if (propertyName.equals(pd.getName())) {
                TestCase.assertFalse((pd instanceof IndexedPropertyDescriptor));
                TestCase.assertEquals(getter, pd.getReadMethod());
                TestCase.assertEquals(setter, pd.getWriteMethod());
            }
        }
    }

    public class MixedBooleanSimpleClass7 {
        public boolean getList(int index) {
            return false;
        }

        public boolean isList() {
            return false;
        }

        public void setList(boolean b) {
        }
    }

    public void test_MixedBooleanSimpleClass7() throws Exception {
        BeanInfo info = Introspector.getBeanInfo(IntrospectorTest.MixedBooleanSimpleClass7.class);
        Method getter = IntrospectorTest.MixedBooleanSimpleClass7.class.getDeclaredMethod("isList");
        Method setter = IntrospectorTest.MixedBooleanSimpleClass7.class.getDeclaredMethod("setList", boolean.class);
        for (PropertyDescriptor pd : info.getPropertyDescriptors()) {
            if (propertyName.equals(pd.getName())) {
                TestCase.assertFalse((pd instanceof IndexedPropertyDescriptor));
                TestCase.assertEquals(getter, pd.getReadMethod());
                TestCase.assertEquals(setter, pd.getWriteMethod());
            }
        }
    }

    public class MixedBooleanSimpleClass8 {
        public boolean getList(int index) {
            return false;
        }

        public boolean getList() {
            return false;
        }

        public void setList(boolean b) {
        }
    }

    public void test_MixedBooleanSimpleClass8() throws Exception {
        BeanInfo info = Introspector.getBeanInfo(IntrospectorTest.MixedBooleanSimpleClass8.class);
        Method getter = IntrospectorTest.MixedBooleanSimpleClass8.class.getDeclaredMethod("getList");
        Method setter = IntrospectorTest.MixedBooleanSimpleClass8.class.getDeclaredMethod("setList", boolean.class);
        for (PropertyDescriptor pd : info.getPropertyDescriptors()) {
            if (propertyName.equals(pd.getName())) {
                TestCase.assertFalse((pd instanceof IndexedPropertyDescriptor));
                TestCase.assertEquals(getter, pd.getReadMethod());
                TestCase.assertEquals(setter, pd.getWriteMethod());
            }
        }
    }

    public class MixedBooleanSimpleClass9 {
        public boolean getList(int index) {
            return false;
        }

        public boolean getList() {
            return false;
        }

        public void setList(int index, boolean b) {
        }
    }

    public void test_MixedBooleanSimpleClass9() throws Exception {
        BeanInfo info = Introspector.getBeanInfo(IntrospectorTest.MixedBooleanSimpleClass9.class);
        Method getter = IntrospectorTest.MixedBooleanSimpleClass9.class.getDeclaredMethod("getList", int.class);
        Method setter = IntrospectorTest.MixedBooleanSimpleClass9.class.getDeclaredMethod("setList", int.class, boolean.class);
        for (PropertyDescriptor pd : info.getPropertyDescriptors()) {
            if (propertyName.equals(pd.getName())) {
                TestCase.assertTrue((pd instanceof IndexedPropertyDescriptor));
                TestCase.assertNull(pd.getReadMethod());
                TestCase.assertNull(pd.getWriteMethod());
                TestCase.assertEquals(getter, ((IndexedPropertyDescriptor) (pd)).getIndexedReadMethod());
                TestCase.assertEquals(setter, ((IndexedPropertyDescriptor) (pd)).getIndexedWriteMethod());
            }
        }
    }

    public class MixedBooleanSimpleClass10 {
        public boolean getList(int index) {
            return false;
        }

        public boolean isList() {
            return false;
        }

        public void setList(int index, boolean b) {
        }
    }

    public void test_MixedBooleanSimpleClass10() throws Exception {
        BeanInfo info = Introspector.getBeanInfo(IntrospectorTest.MixedBooleanSimpleClass10.class);
        Method getter = IntrospectorTest.MixedBooleanSimpleClass10.class.getDeclaredMethod("getList", int.class);
        Method setter = IntrospectorTest.MixedBooleanSimpleClass10.class.getDeclaredMethod("setList", int.class, boolean.class);
        for (PropertyDescriptor pd : info.getPropertyDescriptors()) {
            if (propertyName.equals(pd.getName())) {
                TestCase.assertTrue((pd instanceof IndexedPropertyDescriptor));
                TestCase.assertNull(pd.getReadMethod());
                TestCase.assertNull(pd.getWriteMethod());
                TestCase.assertEquals(getter, ((IndexedPropertyDescriptor) (pd)).getIndexedReadMethod());
                TestCase.assertEquals(setter, ((IndexedPropertyDescriptor) (pd)).getIndexedWriteMethod());
            }
        }
    }

    public class MixedBooleanSimpleClass11 {
        public boolean isList(int index) {
            return false;
        }

        public boolean getList() {
            return false;
        }

        public void setList(int index, boolean b) {
        }
    }

    public void test_MixedBooleanSimpleClass11() throws Exception {
        BeanInfo info = Introspector.getBeanInfo(IntrospectorTest.MixedBooleanSimpleClass11.class);
        Method setter = IntrospectorTest.MixedBooleanSimpleClass11.class.getDeclaredMethod("setList", int.class, boolean.class);
        for (PropertyDescriptor pd : info.getPropertyDescriptors()) {
            if (propertyName.equals(pd.getName())) {
                TestCase.assertTrue((pd instanceof IndexedPropertyDescriptor));
                TestCase.assertNull(pd.getReadMethod());
                TestCase.assertNull(pd.getWriteMethod());
                TestCase.assertNull(((IndexedPropertyDescriptor) (pd)).getIndexedReadMethod());
                TestCase.assertEquals(setter, ((IndexedPropertyDescriptor) (pd)).getIndexedWriteMethod());
            }
        }
    }

    public class MixedBooleanSimpleClass12 {
        public boolean isList(int index) {
            return false;
        }

        public boolean isList() {
            return false;
        }

        public void setList(int index, boolean b) {
        }
    }

    public void test_MixedBooleanSimpleClass12() throws Exception {
        BeanInfo info = Introspector.getBeanInfo(IntrospectorTest.MixedBooleanSimpleClass12.class);
        Method setter = IntrospectorTest.MixedBooleanSimpleClass12.class.getDeclaredMethod("setList", int.class, boolean.class);
        for (PropertyDescriptor pd : info.getPropertyDescriptors()) {
            if (propertyName.equals(pd.getName())) {
                TestCase.assertTrue((pd instanceof IndexedPropertyDescriptor));
                TestCase.assertNull(pd.getReadMethod());
                TestCase.assertNull(pd.getWriteMethod());
                TestCase.assertNull(((IndexedPropertyDescriptor) (pd)).getIndexedReadMethod());
                TestCase.assertEquals(setter, ((IndexedPropertyDescriptor) (pd)).getIndexedWriteMethod());
            }
        }
    }

    public class MixedBooleanSimpleClass13 {
        public boolean isList(int index) {
            return false;
        }

        public boolean isList() {
            return false;
        }

        public void setList(boolean b) {
        }

        public void setList(int index, boolean b) {
        }
    }

    public void test_MixedBooleanSimpleClass13() throws Exception {
        BeanInfo info = Introspector.getBeanInfo(IntrospectorTest.MixedBooleanSimpleClass13.class);
        Method getter = IntrospectorTest.MixedBooleanSimpleClass13.class.getDeclaredMethod("isList");
        Method setter = IntrospectorTest.MixedBooleanSimpleClass13.class.getDeclaredMethod("setList", boolean.class);
        for (PropertyDescriptor pd : info.getPropertyDescriptors()) {
            if (propertyName.equals(pd.getName())) {
                TestCase.assertFalse((pd instanceof IndexedPropertyDescriptor));
                TestCase.assertEquals(getter, pd.getReadMethod());
                TestCase.assertEquals(setter, pd.getWriteMethod());
            }
        }
    }

    public class MixedBooleanSimpleClass14 {
        public boolean isList(int index) {
            return false;
        }

        public boolean getList() {
            return false;
        }

        public void setList(boolean b) {
        }

        public void setList(int index, boolean b) {
        }
    }

    public void test_MixedBooleanSimpleClass14() throws Exception {
        BeanInfo info = Introspector.getBeanInfo(IntrospectorTest.MixedBooleanSimpleClass14.class);
        Method getter = IntrospectorTest.MixedBooleanSimpleClass14.class.getDeclaredMethod("getList");
        Method setter = IntrospectorTest.MixedBooleanSimpleClass14.class.getDeclaredMethod("setList", boolean.class);
        for (PropertyDescriptor pd : info.getPropertyDescriptors()) {
            if (propertyName.equals(pd.getName())) {
                TestCase.assertFalse((pd instanceof IndexedPropertyDescriptor));
                TestCase.assertEquals(getter, pd.getReadMethod());
                TestCase.assertEquals(setter, pd.getWriteMethod());
            }
        }
    }

    public class MixedBooleanSimpleClass15 {
        public boolean getList(int index) {
            return false;
        }

        public boolean isList() {
            return false;
        }

        public void setList(boolean b) {
        }

        public void setList(int index, boolean b) {
        }
    }

    public void test_MixedBooleanSimpleClass15() throws Exception {
        BeanInfo info = Introspector.getBeanInfo(IntrospectorTest.MixedBooleanSimpleClass15.class);
        Method getter = IntrospectorTest.MixedBooleanSimpleClass15.class.getDeclaredMethod("getList", int.class);
        Method setter = IntrospectorTest.MixedBooleanSimpleClass15.class.getDeclaredMethod("setList", int.class, boolean.class);
        for (PropertyDescriptor pd : info.getPropertyDescriptors()) {
            if (propertyName.equals(pd.getName())) {
                TestCase.assertTrue((pd instanceof IndexedPropertyDescriptor));
                TestCase.assertNull(pd.getReadMethod());
                TestCase.assertNull(pd.getWriteMethod());
                TestCase.assertEquals(getter, ((IndexedPropertyDescriptor) (pd)).getIndexedReadMethod());
                TestCase.assertEquals(setter, ((IndexedPropertyDescriptor) (pd)).getIndexedWriteMethod());
            }
        }
    }

    public class MixedBooleanSimpleClass16 {
        public boolean getList(int index) {
            return false;
        }

        public boolean getList() {
            return false;
        }

        public void setList(boolean b) {
        }

        public void setList(int index, boolean b) {
        }
    }

    public void test_MixedBooleanSimpleClass16() throws Exception {
        BeanInfo info = Introspector.getBeanInfo(IntrospectorTest.MixedBooleanSimpleClass16.class);
        Method getter = IntrospectorTest.MixedBooleanSimpleClass16.class.getDeclaredMethod("getList", int.class);
        Method setter = IntrospectorTest.MixedBooleanSimpleClass16.class.getDeclaredMethod("setList", int.class, boolean.class);
        for (PropertyDescriptor pd : info.getPropertyDescriptors()) {
            if (propertyName.equals(pd.getName())) {
                TestCase.assertTrue((pd instanceof IndexedPropertyDescriptor));
                TestCase.assertNull(pd.getReadMethod());
                TestCase.assertNull(pd.getWriteMethod());
                TestCase.assertEquals(getter, ((IndexedPropertyDescriptor) (pd)).getIndexedReadMethod());
                TestCase.assertEquals(setter, ((IndexedPropertyDescriptor) (pd)).getIndexedWriteMethod());
            }
        }
    }

    public class MixedBooleanSimpleClass17 {
        public boolean getList() {
            return false;
        }

        public void setList(int index, boolean obj) {
        }

        public void setList(boolean obj) {
        }
    }

    public void test_MixedBooleanSimpleClass17() throws Exception {
        BeanInfo info = Introspector.getBeanInfo(IntrospectorTest.MixedBooleanSimpleClass17.class);
        Method getter = IntrospectorTest.MixedBooleanSimpleClass17.class.getDeclaredMethod("getList");
        Method setter = IntrospectorTest.MixedBooleanSimpleClass17.class.getDeclaredMethod("setList", boolean.class);
        for (PropertyDescriptor pd : info.getPropertyDescriptors()) {
            if (propertyName.equals(pd.getName())) {
                TestCase.assertFalse((pd instanceof IndexedPropertyDescriptor));
                TestCase.assertEquals(getter, pd.getReadMethod());
                TestCase.assertEquals(setter, pd.getWriteMethod());
            }
        }
    }

    public class MixedBooleanSimpleClass18 {
        public boolean isList() {
            return false;
        }

        public void setList(int index, boolean obj) {
        }

        public void setList(boolean obj) {
        }
    }

    public void test_MixedBooleanSimpleClass18() throws Exception {
        BeanInfo info = Introspector.getBeanInfo(IntrospectorTest.MixedBooleanSimpleClass18.class);
        Method getter = IntrospectorTest.MixedBooleanSimpleClass18.class.getDeclaredMethod("isList");
        Method setter = IntrospectorTest.MixedBooleanSimpleClass18.class.getDeclaredMethod("setList", boolean.class);
        for (PropertyDescriptor pd : info.getPropertyDescriptors()) {
            if (propertyName.equals(pd.getName())) {
                TestCase.assertFalse((pd instanceof IndexedPropertyDescriptor));
                TestCase.assertEquals(getter, pd.getReadMethod());
                TestCase.assertEquals(setter, pd.getWriteMethod());
            }
        }
    }

    public class MixedBooleanSimpleClass19 {
        public boolean getList(int index) {
            return false;
        }

        public void setList(boolean obj) {
        }

        public void setList(int index, boolean obj) {
        }
    }

    public void test_MixedBooleanSimpleClass19() throws Exception {
        BeanInfo info = Introspector.getBeanInfo(IntrospectorTest.MixedBooleanSimpleClass19.class);
        Method getter = IntrospectorTest.MixedBooleanSimpleClass19.class.getDeclaredMethod("getList", int.class);
        Method setter = IntrospectorTest.MixedBooleanSimpleClass19.class.getDeclaredMethod("setList", int.class, boolean.class);
        for (PropertyDescriptor pd : info.getPropertyDescriptors()) {
            if (propertyName.equals(pd.getName())) {
                TestCase.assertTrue((pd instanceof IndexedPropertyDescriptor));
                TestCase.assertNull(pd.getReadMethod());
                TestCase.assertNull(pd.getWriteMethod());
                TestCase.assertEquals(getter, ((IndexedPropertyDescriptor) (pd)).getIndexedReadMethod());
                TestCase.assertEquals(setter, ((IndexedPropertyDescriptor) (pd)).getIndexedWriteMethod());
            }
        }
    }

    public class MixedBooleanSimpleClass20 {
        public boolean isList(int index) {
            return false;
        }

        public void setList(boolean obj) {
        }

        public void setList(int index, boolean obj) {
        }
    }

    public void test_MixedBooleanSimpleClass20() throws Exception {
        BeanInfo info = Introspector.getBeanInfo(IntrospectorTest.MixedBooleanSimpleClass20.class);
        Method setter = IntrospectorTest.MixedBooleanSimpleClass20.class.getDeclaredMethod("setList", int.class, boolean.class);
        for (PropertyDescriptor pd : info.getPropertyDescriptors()) {
            if (propertyName.equals(pd.getName())) {
                TestCase.assertTrue((pd instanceof IndexedPropertyDescriptor));
                TestCase.assertNull(pd.getReadMethod());
                TestCase.assertNull(pd.getWriteMethod());
                TestCase.assertNull(((IndexedPropertyDescriptor) (pd)).getIndexedReadMethod());
                TestCase.assertEquals(setter, ((IndexedPropertyDescriptor) (pd)).getIndexedWriteMethod());
            }
        }
    }

    public class MixedBooleanSimpleClass21 {
        public boolean getList(int index) {
            return false;
        }

        public void setList(boolean obj) {
        }
    }

    public void test_MixedBooleanSimpleClass21() throws Exception {
        BeanInfo info = Introspector.getBeanInfo(IntrospectorTest.MixedBooleanSimpleClass21.class);
        Method getter = IntrospectorTest.MixedBooleanSimpleClass21.class.getDeclaredMethod("getList", int.class);
        for (PropertyDescriptor pd : info.getPropertyDescriptors()) {
            if (propertyName.equals(pd.getName())) {
                TestCase.assertTrue((pd instanceof IndexedPropertyDescriptor));
                TestCase.assertNull(pd.getReadMethod());
                TestCase.assertNull(pd.getWriteMethod());
                TestCase.assertEquals(getter, ((IndexedPropertyDescriptor) (pd)).getIndexedReadMethod());
                TestCase.assertNull(((IndexedPropertyDescriptor) (pd)).getIndexedWriteMethod());
            }
        }
    }

    public class MixedBooleanSimpleClass22 {
        public boolean isList(int index) {
            return false;
        }

        public void setList(boolean obj) {
        }
    }

    public void test_MixedBooleanSimpleClass22() throws Exception {
        BeanInfo info = Introspector.getBeanInfo(IntrospectorTest.MixedBooleanSimpleClass22.class);
        Method setter = IntrospectorTest.MixedBooleanSimpleClass22.class.getDeclaredMethod("setList", boolean.class);
        for (PropertyDescriptor pd : info.getPropertyDescriptors()) {
            if (propertyName.equals(pd.getName())) {
                TestCase.assertFalse((pd instanceof IndexedPropertyDescriptor));
                TestCase.assertNull(pd.getReadMethod());
                TestCase.assertEquals(setter, pd.getWriteMethod());
            }
        }
    }

    public class MixedBooleanSimpleClass23 {
        public boolean getList() {
            return false;
        }

        public void setList(int index, boolean obj) {
        }
    }

    public void test_MixedBooleanSimpleClass23() throws Exception {
        BeanInfo info = Introspector.getBeanInfo(IntrospectorTest.MixedBooleanSimpleClass23.class);
        Method setter = IntrospectorTest.MixedBooleanSimpleClass23.class.getDeclaredMethod("setList", int.class, boolean.class);
        for (PropertyDescriptor pd : info.getPropertyDescriptors()) {
            if (propertyName.equals(pd.getName())) {
                TestCase.assertTrue((pd instanceof IndexedPropertyDescriptor));
                TestCase.assertNull(pd.getReadMethod());
                TestCase.assertNull(pd.getWriteMethod());
                TestCase.assertNull(((IndexedPropertyDescriptor) (pd)).getIndexedReadMethod());
                TestCase.assertEquals(setter, ((IndexedPropertyDescriptor) (pd)).getIndexedWriteMethod());
            }
        }
    }

    public class MixedBooleanSimpleClass24 {
        public boolean isList() {
            return false;
        }

        public void setList(int index, boolean obj) {
        }
    }

    public void test_MixedBooleanSimpleClass24() throws Exception {
        BeanInfo info = Introspector.getBeanInfo(IntrospectorTest.MixedBooleanSimpleClass24.class);
        Method setter = IntrospectorTest.MixedBooleanSimpleClass24.class.getDeclaredMethod("setList", int.class, boolean.class);
        for (PropertyDescriptor pd : info.getPropertyDescriptors()) {
            if (propertyName.equals(pd.getName())) {
                TestCase.assertTrue((pd instanceof IndexedPropertyDescriptor));
                TestCase.assertNull(pd.getReadMethod());
                TestCase.assertNull(pd.getWriteMethod());
                TestCase.assertNull(((IndexedPropertyDescriptor) (pd)).getIndexedReadMethod());
                TestCase.assertEquals(setter, ((IndexedPropertyDescriptor) (pd)).getIndexedWriteMethod());
            }
        }
    }

    public class MixedBooleanSimpleClass25 {
        public void setList(boolean obj) {
        }

        public void setList(int index, boolean obj) {
        }
    }

    public void test_MixedBooleanSimpleClass25() throws Exception {
        BeanInfo info = Introspector.getBeanInfo(IntrospectorTest.MixedBooleanSimpleClass25.class);
        Method setter = IntrospectorTest.MixedBooleanSimpleClass25.class.getDeclaredMethod("setList", int.class, boolean.class);
        for (PropertyDescriptor pd : info.getPropertyDescriptors()) {
            if (propertyName.equals(pd.getName())) {
                TestCase.assertTrue((pd instanceof IndexedPropertyDescriptor));
                TestCase.assertNull(pd.getReadMethod());
                TestCase.assertNull(pd.getWriteMethod());
                TestCase.assertNull(((IndexedPropertyDescriptor) (pd)).getIndexedReadMethod());
                TestCase.assertEquals(setter, ((IndexedPropertyDescriptor) (pd)).getIndexedWriteMethod());
            }
        }
    }

    public class MixedBooleanSimpleClass26 {
        public boolean[] getList() {
            return null;
        }

        public boolean getList(int i) {
            return false;
        }
    }

    public void test_MixedBooleanSimpleClass26() throws Exception {
        BeanInfo info = Introspector.getBeanInfo(IntrospectorTest.MixedBooleanSimpleClass26.class);
        Method normalGetter = IntrospectorTest.MixedBooleanSimpleClass26.class.getDeclaredMethod("getList");
        Method indexedGetter = IntrospectorTest.MixedBooleanSimpleClass26.class.getDeclaredMethod("getList", int.class);
        for (PropertyDescriptor pd : info.getPropertyDescriptors()) {
            if (propertyName.equals(pd.getName())) {
                TestCase.assertTrue((pd instanceof IndexedPropertyDescriptor));
                TestCase.assertEquals(normalGetter, pd.getReadMethod());
                TestCase.assertNull(pd.getWriteMethod());
                TestCase.assertEquals(indexedGetter, ((IndexedPropertyDescriptor) (pd)).getIndexedReadMethod());
                TestCase.assertNull(((IndexedPropertyDescriptor) (pd)).getIndexedWriteMethod());
            }
        }
    }

    public class MixedBooleanSimpleClass27 {
        public boolean[] isList() {
            return null;
        }

        public boolean getList(int i) {
            return false;
        }
    }

    public void test_MixedBooleanSimpleClass27() throws Exception {
        BeanInfo info = Introspector.getBeanInfo(IntrospectorTest.MixedBooleanSimpleClass27.class);
        Method indexedGetter = IntrospectorTest.MixedBooleanSimpleClass27.class.getDeclaredMethod("getList", int.class);
        for (PropertyDescriptor pd : info.getPropertyDescriptors()) {
            if (propertyName.equals(pd.getName())) {
                TestCase.assertTrue((pd instanceof IndexedPropertyDescriptor));
                TestCase.assertNull(pd.getReadMethod());
                TestCase.assertNull(pd.getWriteMethod());
                TestCase.assertEquals(indexedGetter, ((IndexedPropertyDescriptor) (pd)).getIndexedReadMethod());
                TestCase.assertNull(((IndexedPropertyDescriptor) (pd)).getIndexedWriteMethod());
            }
        }
    }

    public class MixedBooleanSimpleClass28 {
        public boolean[] getList() {
            return null;
        }

        public boolean isList(int i) {
            return false;
        }
    }

    public void test_MixedBooleanSimpleClass28() throws Exception {
        BeanInfo info = Introspector.getBeanInfo(IntrospectorTest.MixedBooleanSimpleClass28.class);
        Method getter = IntrospectorTest.MixedBooleanSimpleClass28.class.getDeclaredMethod("getList");
        for (PropertyDescriptor pd : info.getPropertyDescriptors()) {
            if (propertyName.equals(pd.getName())) {
                TestCase.assertFalse((pd instanceof IndexedPropertyDescriptor));
                TestCase.assertEquals(getter, pd.getReadMethod());
                TestCase.assertNull(pd.getWriteMethod());
            }
        }
    }

    public class MixedBooleanSimpleClass29 {
        public boolean[] isList() {
            return null;
        }

        public boolean isList(int i) {
            return false;
        }
    }

    public void test_MixedBooleanSimpleClass29() throws Exception {
        BeanInfo info = Introspector.getBeanInfo(IntrospectorTest.MixedBooleanSimpleClass29.class);
        for (PropertyDescriptor pd : info.getPropertyDescriptors()) {
            TestCase.assertFalse(propertyName.equals(pd.getName()));
        }
    }

    public class MixedBooleanSimpleClass30 {
        public boolean getList() {
            return false;
        }

        public boolean[] getList(int i) {
            return null;
        }
    }

    public void test_MixedBooleanSimpleClass30() throws Exception {
        BeanInfo info = Introspector.getBeanInfo(IntrospectorTest.MixedBooleanSimpleClass30.class);
        Method indexedGetter = IntrospectorTest.MixedBooleanSimpleClass30.class.getDeclaredMethod("getList", int.class);
        for (PropertyDescriptor pd : info.getPropertyDescriptors()) {
            if (propertyName.equals(pd.getName())) {
                TestCase.assertTrue((pd instanceof IndexedPropertyDescriptor));
                TestCase.assertNull(pd.getReadMethod());
                TestCase.assertNull(pd.getWriteMethod());
                TestCase.assertEquals(indexedGetter, ((IndexedPropertyDescriptor) (pd)).getIndexedReadMethod());
                TestCase.assertNull(((IndexedPropertyDescriptor) (pd)).getIndexedWriteMethod());
            }
        }
    }

    public class MixedBooleanSimpleClass31 {
        public boolean isList() {
            return false;
        }

        public boolean[] getList(int i) {
            return null;
        }
    }

    public void test_MixedBooleanSimpleClass31() throws Exception {
        BeanInfo info = Introspector.getBeanInfo(IntrospectorTest.MixedBooleanSimpleClass31.class);
        Method indexedGetter = IntrospectorTest.MixedBooleanSimpleClass31.class.getDeclaredMethod("getList", int.class);
        for (PropertyDescriptor pd : info.getPropertyDescriptors()) {
            if (propertyName.equals(pd.getName())) {
                TestCase.assertTrue((pd instanceof IndexedPropertyDescriptor));
                TestCase.assertNull(pd.getReadMethod());
                TestCase.assertNull(pd.getWriteMethod());
                TestCase.assertEquals(indexedGetter, ((IndexedPropertyDescriptor) (pd)).getIndexedReadMethod());
                TestCase.assertNull(((IndexedPropertyDescriptor) (pd)).getIndexedWriteMethod());
            }
        }
    }

    public class MixedBooleanSimpleClass32 {
        public boolean getList() {
            return false;
        }

        public boolean[] isList(int i) {
            return null;
        }
    }

    public void test_MixedBooleanSimpleClass32() throws Exception {
        BeanInfo info = Introspector.getBeanInfo(IntrospectorTest.MixedBooleanSimpleClass32.class);
        Method getter = IntrospectorTest.MixedBooleanSimpleClass32.class.getDeclaredMethod("getList");
        for (PropertyDescriptor pd : info.getPropertyDescriptors()) {
            if (propertyName.equals(pd.getName())) {
                TestCase.assertFalse((pd instanceof IndexedPropertyDescriptor));
                TestCase.assertEquals(getter, pd.getReadMethod());
                TestCase.assertNull(pd.getWriteMethod());
            }
        }
    }

    public class MixedBooleanSimpleClass33 {
        public boolean isList() {
            return false;
        }

        public boolean[] isList(int i) {
            return null;
        }
    }

    public void test_MixedBooleanSimpleClass33() throws Exception {
        BeanInfo info = Introspector.getBeanInfo(IntrospectorTest.MixedBooleanSimpleClass33.class);
        Method getter = IntrospectorTest.MixedBooleanSimpleClass33.class.getDeclaredMethod("isList");
        for (PropertyDescriptor pd : info.getPropertyDescriptors()) {
            if (propertyName.equals(pd.getName())) {
                TestCase.assertFalse((pd instanceof IndexedPropertyDescriptor));
                TestCase.assertEquals(getter, pd.getReadMethod());
                TestCase.assertNull(pd.getWriteMethod());
            }
        }
    }

    public class MixedBooleanSimpleClass34 {
        public boolean[] getList() {
            return null;
        }

        public boolean[] getList(int index) {
            return null;
        }
    }

    public void test_MixedBooleanSimpleClass34() throws Exception {
        BeanInfo info = Introspector.getBeanInfo(IntrospectorTest.MixedBooleanSimpleClass34.class);
        Method indexedGetter = IntrospectorTest.MixedBooleanSimpleClass34.class.getDeclaredMethod("getList", int.class);
        for (PropertyDescriptor pd : info.getPropertyDescriptors()) {
            if (propertyName.equals(pd.getName())) {
                TestCase.assertTrue((pd instanceof IndexedPropertyDescriptor));
                TestCase.assertNull(pd.getReadMethod());
                TestCase.assertNull(pd.getWriteMethod());
                TestCase.assertEquals(indexedGetter, ((IndexedPropertyDescriptor) (pd)).getIndexedReadMethod());
                TestCase.assertNull(((IndexedPropertyDescriptor) (pd)).getIndexedWriteMethod());
            }
        }
    }

    public class MixedBooleanSimpleClass35 {
        public boolean[] isList() {
            return null;
        }

        public boolean[] getList(int index) {
            return null;
        }
    }

    public void test_MixedBooleanSimpleClass35() throws Exception {
        BeanInfo info = Introspector.getBeanInfo(IntrospectorTest.MixedBooleanSimpleClass35.class);
        Method indexedGetter = IntrospectorTest.MixedBooleanSimpleClass35.class.getDeclaredMethod("getList", int.class);
        for (PropertyDescriptor pd : info.getPropertyDescriptors()) {
            if (propertyName.equals(pd.getName())) {
                TestCase.assertTrue((pd instanceof IndexedPropertyDescriptor));
                TestCase.assertNull(pd.getReadMethod());
                TestCase.assertNull(pd.getWriteMethod());
                TestCase.assertEquals(indexedGetter, ((IndexedPropertyDescriptor) (pd)).getIndexedReadMethod());
                TestCase.assertNull(((IndexedPropertyDescriptor) (pd)).getIndexedWriteMethod());
            }
        }
    }

    public class MixedBooleanSimpleClass36 {
        public boolean[] getList() {
            return null;
        }

        public boolean[] isList(int index) {
            return null;
        }
    }

    public void test_MixedBooleanSimpleClass36() throws Exception {
        BeanInfo info = Introspector.getBeanInfo(IntrospectorTest.MixedBooleanSimpleClass36.class);
        Method normalGetter = IntrospectorTest.MixedBooleanSimpleClass36.class.getDeclaredMethod("getList");
        for (PropertyDescriptor pd : info.getPropertyDescriptors()) {
            if (propertyName.equals(pd.getName())) {
                TestCase.assertFalse((pd instanceof IndexedPropertyDescriptor));
                TestCase.assertEquals(normalGetter, pd.getReadMethod());
                TestCase.assertNull(pd.getWriteMethod());
            }
        }
    }

    public class MixedBooleanSimpleClass37 {
        public boolean[] isList() {
            return null;
        }

        public boolean[] isList(int index) {
            return null;
        }
    }

    public void test_MixedBooleanSimpleClass37() throws Exception {
        BeanInfo info = Introspector.getBeanInfo(IntrospectorTest.MixedBooleanSimpleClass37.class);
        for (PropertyDescriptor pd : info.getPropertyDescriptors()) {
            TestCase.assertFalse(propertyName.equals(pd.getName()));
        }
    }

    public class MixedBooleanSimpleClass38 {
        public boolean[][] getList() {
            return null;
        }

        public boolean[] getList(int index) {
            return null;
        }
    }

    public void test_MixedBooleanSimpleClass38() throws Exception {
        BeanInfo info = Introspector.getBeanInfo(IntrospectorTest.MixedBooleanSimpleClass38.class);
        Method normalGetter = IntrospectorTest.MixedBooleanSimpleClass38.class.getDeclaredMethod("getList");
        Method indexedGetter = IntrospectorTest.MixedBooleanSimpleClass38.class.getDeclaredMethod("getList", int.class);
        for (PropertyDescriptor pd : info.getPropertyDescriptors()) {
            if (propertyName.equals(pd.getName())) {
                TestCase.assertTrue((pd instanceof IndexedPropertyDescriptor));
                TestCase.assertEquals(normalGetter, pd.getReadMethod());
                TestCase.assertNull(pd.getWriteMethod());
                TestCase.assertEquals(indexedGetter, ((IndexedPropertyDescriptor) (pd)).getIndexedReadMethod());
                TestCase.assertNull(((IndexedPropertyDescriptor) (pd)).getIndexedWriteMethod());
            }
        }
    }

    public class MixedBooleanSimpleClass39 {
        public void setList(boolean a) {
        }
    }

    public void test_MixedBooleanSimpleClass39() throws Exception {
        BeanInfo info = Introspector.getBeanInfo(IntrospectorTest.MixedBooleanSimpleClass39.class);
        Method setter = IntrospectorTest.MixedBooleanSimpleClass39.class.getDeclaredMethod("setList", boolean.class);
        for (PropertyDescriptor pd : info.getPropertyDescriptors()) {
            if (propertyName.equals(pd.getName())) {
                TestCase.assertFalse((pd instanceof IndexedPropertyDescriptor));
                TestCase.assertNull(pd.getReadMethod());
                TestCase.assertEquals(setter, pd.getWriteMethod());
                break;
            }
        }
    }

    public class MixedBooleanSimpleClass40 {
        public void setList(int index, boolean a) {
        }
    }

    public void test_MixedBooleanSimpleClass40() throws Exception {
        BeanInfo info = Introspector.getBeanInfo(IntrospectorTest.MixedBooleanSimpleClass40.class);
        Method setter = IntrospectorTest.MixedBooleanSimpleClass40.class.getDeclaredMethod("setList", int.class, boolean.class);
        for (PropertyDescriptor pd : info.getPropertyDescriptors()) {
            if (propertyName.equals(pd.getName())) {
                TestCase.assertTrue((pd instanceof IndexedPropertyDescriptor));
                TestCase.assertNull(pd.getReadMethod());
                TestCase.assertNull(pd.getWriteMethod());
                TestCase.assertNull(((IndexedPropertyDescriptor) (pd)).getIndexedReadMethod());
                TestCase.assertEquals(setter, ((IndexedPropertyDescriptor) (pd)).getIndexedWriteMethod());
                break;
            }
        }
    }

    public class MixedBooleanSimpleClass41 {
        public boolean getList() {
            return false;
        }

        public void setList(boolean bool) {
        }
    }

    public void test_MixedBooleanSimpleClass41() throws Exception {
        BeanInfo info = Introspector.getBeanInfo(IntrospectorTest.MixedBooleanSimpleClass41.class);
        Method getter = IntrospectorTest.MixedBooleanSimpleClass41.class.getDeclaredMethod("getList");
        Method setter = IntrospectorTest.MixedBooleanSimpleClass41.class.getDeclaredMethod("setList", boolean.class);
        for (PropertyDescriptor pd : info.getPropertyDescriptors()) {
            if (propertyName.equals(pd.getName())) {
                TestCase.assertFalse((pd instanceof IndexedPropertyDescriptor));
                TestCase.assertEquals(getter, pd.getReadMethod());
                TestCase.assertEquals(setter, pd.getWriteMethod());
            }
        }
    }

    public class MixedBooleanSimpleClass42 {
        public boolean isList() {
            return false;
        }

        public void setList(boolean bool) {
        }
    }

    public void test_MixedBooleanSimpleClass42() throws Exception {
        BeanInfo info = Introspector.getBeanInfo(IntrospectorTest.MixedBooleanSimpleClass42.class);
        Method getter = IntrospectorTest.MixedBooleanSimpleClass42.class.getDeclaredMethod("isList");
        Method setter = IntrospectorTest.MixedBooleanSimpleClass42.class.getDeclaredMethod("setList", boolean.class);
        for (PropertyDescriptor pd : info.getPropertyDescriptors()) {
            if (propertyName.equals(pd.getName())) {
                TestCase.assertFalse((pd instanceof IndexedPropertyDescriptor));
                TestCase.assertEquals(getter, pd.getReadMethod());
                TestCase.assertEquals(setter, pd.getWriteMethod());
            }
        }
    }

    public class MixedBooleanSimpleClass43 {
        public boolean isList() {
            return false;
        }
    }

    public void test_MixedBooleanSimpleClass43() throws Exception {
        BeanInfo info = Introspector.getBeanInfo(IntrospectorTest.MixedBooleanSimpleClass43.class);
        Method getter = IntrospectorTest.MixedBooleanSimpleClass43.class.getDeclaredMethod("isList");
        for (PropertyDescriptor pd : info.getPropertyDescriptors()) {
            if (propertyName.equals(pd.getName())) {
                TestCase.assertFalse((pd instanceof IndexedPropertyDescriptor));
                TestCase.assertEquals(getter, pd.getReadMethod());
                TestCase.assertNull(pd.getWriteMethod());
            }
        }
    }

    public class MixedBooleanExtendClass1 extends IntrospectorTest.MixedBooleanSimpleClass1 {
        public void setList(boolean a) {
        }
    }

    public void test_MixedBooleanExtendClass1() throws Exception {
        BeanInfo info = Introspector.getBeanInfo(IntrospectorTest.MixedBooleanExtendClass1.class);
        Method getter = IntrospectorTest.MixedBooleanSimpleClass1.class.getDeclaredMethod("isList");
        Method setter = IntrospectorTest.MixedBooleanExtendClass1.class.getDeclaredMethod("setList", boolean.class);
        for (PropertyDescriptor pd : info.getPropertyDescriptors()) {
            if (propertyName.equals(pd.getName())) {
                TestCase.assertFalse((pd instanceof IndexedPropertyDescriptor));
                TestCase.assertEquals(getter, pd.getReadMethod());
                TestCase.assertEquals(setter, pd.getWriteMethod());
                break;
            }
        }
    }

    public class MixedBooleanExtendClass2 extends IntrospectorTest.MixedBooleanSimpleClass1 {
        public void setList(int index, boolean a) {
        }
    }

    public void test_MixedBooleanExtendClass2() throws Exception {
        BeanInfo info = Introspector.getBeanInfo(IntrospectorTest.MixedBooleanExtendClass2.class);
        Method setter = IntrospectorTest.MixedBooleanExtendClass2.class.getDeclaredMethod("setList", int.class, boolean.class);
        for (PropertyDescriptor pd : info.getPropertyDescriptors()) {
            if (propertyName.equals(pd.getName())) {
                TestCase.assertTrue((pd instanceof IndexedPropertyDescriptor));
                TestCase.assertNull(pd.getReadMethod());
                TestCase.assertNull(pd.getWriteMethod());
                TestCase.assertNull(((IndexedPropertyDescriptor) (pd)).getIndexedReadMethod());
                TestCase.assertEquals(setter, ((IndexedPropertyDescriptor) (pd)).getIndexedWriteMethod());
                break;
            }
        }
    }

    public class MixedBooleanExtendClass3 extends IntrospectorTest.MixedBooleanSimpleClass1 {
        public void setList(boolean a) {
        }

        public void setList(int index, boolean a) {
        }
    }

    public void test_MixedBooleanExtendClass3() throws Exception {
        BeanInfo info = Introspector.getBeanInfo(IntrospectorTest.MixedBooleanExtendClass3.class);
        Method getter = IntrospectorTest.MixedBooleanSimpleClass1.class.getDeclaredMethod("isList");
        Method setter = IntrospectorTest.MixedBooleanExtendClass3.class.getDeclaredMethod("setList", boolean.class);
        for (PropertyDescriptor pd : info.getPropertyDescriptors()) {
            if (propertyName.equals(pd.getName())) {
                TestCase.assertFalse((pd instanceof IndexedPropertyDescriptor));
                TestCase.assertEquals(getter, pd.getReadMethod());
                TestCase.assertEquals(setter, pd.getWriteMethod());
                break;
            }
        }
    }

    public class MixedBooleanExtendClass4 extends IntrospectorTest.MixedBooleanSimpleClass1 {
        public boolean isList() {
            return false;
        }
    }

    public void test_MixedBooleanExtendClass4() throws Exception {
        BeanInfo info = Introspector.getBeanInfo(IntrospectorTest.MixedBooleanExtendClass4.class);
        Method getter = IntrospectorTest.MixedBooleanSimpleClass1.class.getDeclaredMethod("isList");
        for (PropertyDescriptor pd : info.getPropertyDescriptors()) {
            if (propertyName.equals(pd.getName())) {
                TestCase.assertFalse((pd instanceof IndexedPropertyDescriptor));
                TestCase.assertEquals(getter, pd.getReadMethod());
                TestCase.assertNull(pd.getWriteMethod());
                break;
            }
        }
    }

    public class MixedBooleanExtendClass5 extends IntrospectorTest.MixedBooleanSimpleClass1 {
        public boolean isList(int index) {
            return false;
        }
    }

    public void test_MixedBooleanExtendClass5() throws Exception {
        BeanInfo info = Introspector.getBeanInfo(IntrospectorTest.MixedBooleanExtendClass5.class);
        Method getter = IntrospectorTest.MixedBooleanSimpleClass1.class.getDeclaredMethod("isList");
        for (PropertyDescriptor pd : info.getPropertyDescriptors()) {
            if (propertyName.equals(pd.getName())) {
                TestCase.assertFalse((pd instanceof IndexedPropertyDescriptor));
                TestCase.assertEquals(getter, pd.getReadMethod());
                TestCase.assertNull(pd.getWriteMethod());
                break;
            }
        }
    }

    public class MixedBooleanExtendClass6 extends IntrospectorTest.MixedBooleanSimpleClass25 {
        public boolean isList() {
            return false;
        }
    }

    public void test_MixedBooleanExtendClass6() throws Exception {
        BeanInfo info = Introspector.getBeanInfo(IntrospectorTest.MixedBooleanExtendClass6.class);
        Method getter = IntrospectorTest.MixedBooleanExtendClass6.class.getDeclaredMethod("isList");
        Method setter = IntrospectorTest.MixedBooleanSimpleClass25.class.getDeclaredMethod("setList", boolean.class);
        for (PropertyDescriptor pd : info.getPropertyDescriptors()) {
            if (propertyName.equals(pd.getName())) {
                TestCase.assertFalse((pd instanceof IndexedPropertyDescriptor));
                TestCase.assertEquals(getter, pd.getReadMethod());
                TestCase.assertEquals(setter, pd.getWriteMethod());
                break;
            }
        }
    }

    public class MixedBooleanExtendClass7 extends IntrospectorTest.MixedBooleanSimpleClass25 {
        public boolean isList(int index) {
            return false;
        }
    }

    public void test_MixedBooleanExtendClass7() throws Exception {
        BeanInfo info = Introspector.getBeanInfo(IntrospectorTest.MixedBooleanExtendClass7.class);
        Method setter = IntrospectorTest.MixedBooleanSimpleClass25.class.getDeclaredMethod("setList", int.class, boolean.class);
        for (PropertyDescriptor pd : info.getPropertyDescriptors()) {
            if (propertyName.equals(pd.getName())) {
                TestCase.assertTrue((pd instanceof IndexedPropertyDescriptor));
                TestCase.assertNull(pd.getReadMethod());
                TestCase.assertNull(pd.getWriteMethod());
                TestCase.assertNull(((IndexedPropertyDescriptor) (pd)).getIndexedReadMethod());
                TestCase.assertEquals(setter, ((IndexedPropertyDescriptor) (pd)).getIndexedWriteMethod());
                break;
            }
        }
    }

    public class MixedBooleanExtendClass8 extends IntrospectorTest.MixedBooleanSimpleClass25 {
        public boolean isList() {
            return false;
        }

        public boolean isList(int index) {
            return false;
        }
    }

    public void test_MixedBooleanExtendClass8() throws Exception {
        BeanInfo info = Introspector.getBeanInfo(IntrospectorTest.MixedBooleanExtendClass8.class);
        Method getter = IntrospectorTest.MixedBooleanExtendClass8.class.getDeclaredMethod("isList");
        Method setter = IntrospectorTest.MixedBooleanSimpleClass25.class.getDeclaredMethod("setList", boolean.class);
        for (PropertyDescriptor pd : info.getPropertyDescriptors()) {
            if (propertyName.equals(pd.getName())) {
                TestCase.assertFalse((pd instanceof IndexedPropertyDescriptor));
                TestCase.assertEquals(getter, pd.getReadMethod());
                TestCase.assertEquals(setter, pd.getWriteMethod());
                break;
            }
        }
    }

    public class MixedBooleanExtendClass9 extends IntrospectorTest.MixedBooleanSimpleClass25 {
        public void setList(boolean obj) {
        }
    }

    public void test_MixedBooleanExtendClass9() throws Exception {
        BeanInfo info = Introspector.getBeanInfo(IntrospectorTest.MixedBooleanExtendClass9.class);
        Method setter = IntrospectorTest.MixedBooleanExtendClass9.class.getDeclaredMethod("setList", boolean.class);
        for (PropertyDescriptor pd : info.getPropertyDescriptors()) {
            if (propertyName.equals(pd.getName())) {
                TestCase.assertFalse((pd instanceof IndexedPropertyDescriptor));
                TestCase.assertNull(pd.getReadMethod());
                TestCase.assertEquals(setter, pd.getWriteMethod());
                break;
            }
        }
    }

    public class MixedBooleanExtendClass10 extends IntrospectorTest.MixedBooleanSimpleClass25 {
        public void setList(int index, boolean obj) {
        }
    }

    public void test_MixedBooleanExtendClass10() throws Exception {
        BeanInfo info = Introspector.getBeanInfo(IntrospectorTest.MixedBooleanExtendClass10.class);
        Method setter = IntrospectorTest.MixedBooleanExtendClass10.class.getDeclaredMethod("setList", int.class, boolean.class);
        for (PropertyDescriptor pd : info.getPropertyDescriptors()) {
            if (propertyName.equals(pd.getName())) {
                TestCase.assertTrue((pd instanceof IndexedPropertyDescriptor));
                TestCase.assertNull(pd.getReadMethod());
                TestCase.assertNull(pd.getWriteMethod());
                TestCase.assertNull(((IndexedPropertyDescriptor) (pd)).getIndexedReadMethod());
                TestCase.assertEquals(setter, ((IndexedPropertyDescriptor) (pd)).getIndexedWriteMethod());
                break;
            }
        }
    }

    public class MixedBooleanExtendClass11 extends IntrospectorTest.MixedBooleanSimpleClass41 {
        public void setList(Object obj) {
        }
    }

    public void test_MixedBooleanExtendClass11() throws Exception {
        BeanInfo info = Introspector.getBeanInfo(IntrospectorTest.MixedBooleanExtendClass11.class);
        Method getter = IntrospectorTest.MixedBooleanSimpleClass41.class.getDeclaredMethod("getList");
        Method setter = IntrospectorTest.MixedBooleanSimpleClass41.class.getDeclaredMethod("setList", boolean.class);
        for (PropertyDescriptor pd : info.getPropertyDescriptors()) {
            if (propertyName.equals(pd.getName())) {
                TestCase.assertFalse((pd instanceof IndexedPropertyDescriptor));
                TestCase.assertEquals(getter, pd.getReadMethod());
                TestCase.assertEquals(setter, pd.getWriteMethod());
            }
        }
    }

    public class MixedBooleanExtendClass12 extends IntrospectorTest.MixedBooleanSimpleClass41 {
        public void setList(Boolean obj) {
        }
    }

    public void test_MixedBooleanExtendClass12() throws Exception {
        BeanInfo info = Introspector.getBeanInfo(IntrospectorTest.MixedBooleanExtendClass12.class);
        Method getter = IntrospectorTest.MixedBooleanSimpleClass41.class.getDeclaredMethod("getList");
        Method setter = IntrospectorTest.MixedBooleanSimpleClass41.class.getDeclaredMethod("setList", boolean.class);
        for (PropertyDescriptor pd : info.getPropertyDescriptors()) {
            if (propertyName.equals(pd.getName())) {
                TestCase.assertFalse((pd instanceof IndexedPropertyDescriptor));
                TestCase.assertEquals(getter, pd.getReadMethod());
                TestCase.assertEquals(setter, pd.getWriteMethod());
            }
        }
    }

    public class MixedBooleanExtendClass13 extends IntrospectorTest.MixedBooleanSimpleClass42 {
        public void setList(Object obj) {
        }
    }

    public void test_MixedBooleanExtendClass13() throws Exception {
        BeanInfo info = Introspector.getBeanInfo(IntrospectorTest.MixedBooleanExtendClass13.class);
        Method getter = IntrospectorTest.MixedBooleanSimpleClass42.class.getDeclaredMethod("isList");
        Method setter = IntrospectorTest.MixedBooleanSimpleClass42.class.getDeclaredMethod("setList", boolean.class);
        for (PropertyDescriptor pd : info.getPropertyDescriptors()) {
            if (propertyName.equals(pd.getName())) {
                TestCase.assertFalse((pd instanceof IndexedPropertyDescriptor));
                TestCase.assertEquals(getter, pd.getReadMethod());
                TestCase.assertEquals(setter, pd.getWriteMethod());
            }
        }
    }

    public class MixedBooleanExtendClass14 extends IntrospectorTest.MixedBooleanSimpleClass43 {
        public boolean isList() {
            return false;
        }
    }

    public void test_MixedBooleanExtendClass14() throws Exception {
        BeanInfo info = Introspector.getBeanInfo(IntrospectorTest.MixedBooleanExtendClass14.class);
        Method getter = IntrospectorTest.MixedBooleanSimpleClass43.class.getDeclaredMethod("isList");
        for (PropertyDescriptor pd : info.getPropertyDescriptors()) {
            if (propertyName.equals(pd.getName())) {
                TestCase.assertFalse((pd instanceof IndexedPropertyDescriptor));
                TestCase.assertEquals(getter, pd.getReadMethod());
                TestCase.assertNull(pd.getWriteMethod());
            }
        }
    }
}

