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


import java.beans.IndexedPropertyDescriptor;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import junit.framework.TestCase;
import org.apache.harmony.beans.tests.support.mock.MockJavaBean;


/**
 * Unit test for IndexedPropertyDescriptor.
 */
public class IndexedPropertyDescriptorTest extends TestCase {
    public void testEquals() throws IntrospectionException, NoSuchMethodException, SecurityException {
        String propertyName = "PropertyFour";
        Class<MockJavaBean> beanClass = MockJavaBean.class;
        Method readMethod = beanClass.getMethod(("get" + propertyName), ((Class[]) (null)));
        Method writeMethod = beanClass.getMethod(("set" + propertyName), new Class[]{ String[].class });
        Method indexedReadMethod = beanClass.getMethod(("get" + propertyName), new Class[]{ Integer.TYPE });
        Method indexedWriteMethod = beanClass.getMethod(("set" + propertyName), new Class[]{ Integer.TYPE, String.class });
        IndexedPropertyDescriptor ipd = new IndexedPropertyDescriptor(propertyName, readMethod, writeMethod, indexedReadMethod, indexedWriteMethod);
        IndexedPropertyDescriptor ipd2 = new IndexedPropertyDescriptor(propertyName, beanClass);
        TestCase.assertTrue(ipd.equals(ipd2));
        TestCase.assertTrue(ipd.equals(ipd));
        TestCase.assertTrue(ipd2.equals(ipd));
        TestCase.assertFalse(ipd.equals(null));
    }

    /* Read method */
    public void testEquals_ReadMethod() throws IntrospectionException, NoSuchMethodException, SecurityException {
        String propertyName = "PropertyFour";
        Class<MockJavaBean> beanClass = MockJavaBean.class;
        Method readMethod = beanClass.getMethod("getPropertyFive", ((Class[]) (null)));
        Method writeMethod = beanClass.getMethod(("set" + propertyName), new Class[]{ String[].class });
        Method indexedReadMethod = beanClass.getMethod(("get" + propertyName), new Class[]{ Integer.TYPE });
        Method indexedWriteMethod = beanClass.getMethod(("set" + propertyName), new Class[]{ Integer.TYPE, String.class });
        IndexedPropertyDescriptor ipd = new IndexedPropertyDescriptor(propertyName, readMethod, writeMethod, indexedReadMethod, indexedWriteMethod);
        IndexedPropertyDescriptor ipd2 = new IndexedPropertyDescriptor(propertyName, beanClass);
        TestCase.assertFalse(ipd.equals(ipd2));
    }

    /* read method null. */
    public void testEquals_ReadMethodNull() throws IntrospectionException, NoSuchMethodException, SecurityException {
        String propertyName = "PropertyFour";
        Class<MockJavaBean> beanClass = MockJavaBean.class;
        Method readMethod = null;
        Method writeMethod = beanClass.getMethod(("set" + propertyName), new Class[]{ String[].class });
        Method indexedReadMethod = beanClass.getMethod(("get" + propertyName), new Class[]{ Integer.TYPE });
        Method indexedWriteMethod = beanClass.getMethod(("set" + propertyName), new Class[]{ Integer.TYPE, String.class });
        IndexedPropertyDescriptor ipd = new IndexedPropertyDescriptor(propertyName, readMethod, writeMethod, indexedReadMethod, indexedWriteMethod);
        IndexedPropertyDescriptor ipd2 = new IndexedPropertyDescriptor(propertyName, beanClass);
        TestCase.assertFalse(ipd.equals(ipd2));
    }

    public void testEquals_WriteMethod() throws IntrospectionException, NoSuchMethodException, SecurityException {
        String propertyName = "PropertyFour";
        Class<MockJavaBean> beanClass = MockJavaBean.class;
        Method readMethod = beanClass.getMethod(("get" + propertyName), ((Class[]) (null)));
        Method writeMethod = beanClass.getMethod("setPropertyFive", new Class[]{ String[].class });
        Method indexedReadMethod = beanClass.getMethod(("get" + propertyName), new Class[]{ Integer.TYPE });
        Method indexedWriteMethod = beanClass.getMethod(("set" + propertyName), new Class[]{ Integer.TYPE, String.class });
        IndexedPropertyDescriptor ipd = new IndexedPropertyDescriptor(propertyName, readMethod, writeMethod, indexedReadMethod, indexedWriteMethod);
        IndexedPropertyDescriptor ipd2 = new IndexedPropertyDescriptor(propertyName, beanClass);
        TestCase.assertFalse(ipd.equals(ipd2));
    }

    /* write method null. */
    public void testEquals_WriteMethodNull() throws IntrospectionException, NoSuchMethodException, SecurityException {
        String propertyName = "PropertyFour";
        Class<MockJavaBean> beanClass = MockJavaBean.class;
        Method readMethod = beanClass.getMethod(("get" + propertyName), ((Class[]) (null)));
        Method writeMethod = null;
        Method indexedReadMethod = beanClass.getMethod(("get" + propertyName), new Class[]{ Integer.TYPE });
        Method indexedWriteMethod = beanClass.getMethod(("set" + propertyName), new Class[]{ Integer.TYPE, String.class });
        IndexedPropertyDescriptor ipd = new IndexedPropertyDescriptor(propertyName, readMethod, writeMethod, indexedReadMethod, indexedWriteMethod);
        IndexedPropertyDescriptor ipd2 = new IndexedPropertyDescriptor(propertyName, beanClass);
        TestCase.assertFalse(ipd.equals(ipd2));
    }

    /* Indexed read method. */
    public void testEquals_IndexedR() throws IntrospectionException, NoSuchMethodException, SecurityException {
        String propertyName = "PropertyFour";
        Class<MockJavaBean> beanClass = MockJavaBean.class;
        Method readMethod = beanClass.getMethod(("get" + propertyName), ((Class[]) (null)));
        Method writeMethod = beanClass.getMethod(("set" + propertyName), new Class[]{ String[].class });
        Method indexedReadMethod = beanClass.getMethod("getPropertyFive", new Class[]{ Integer.TYPE });
        Method indexedWriteMethod = beanClass.getMethod(("set" + propertyName), new Class[]{ Integer.TYPE, String.class });
        IndexedPropertyDescriptor ipd = new IndexedPropertyDescriptor(propertyName, readMethod, writeMethod, indexedReadMethod, indexedWriteMethod);
        IndexedPropertyDescriptor ipd2 = new IndexedPropertyDescriptor(propertyName, beanClass);
        TestCase.assertFalse(ipd.equals(ipd2));
    }

    /* Indexed read method null. */
    public void testEquals_IndexedRNull() throws IntrospectionException, NoSuchMethodException, SecurityException {
        String propertyName = "PropertyFour";
        Class<MockJavaBean> beanClass = MockJavaBean.class;
        Method readMethod = beanClass.getMethod(("get" + propertyName), ((Class[]) (null)));
        Method writeMethod = beanClass.getMethod(("set" + propertyName), new Class[]{ String[].class });
        Method indexedReadMethod = null;
        Method indexedWriteMethod = beanClass.getMethod(("set" + propertyName), new Class[]{ Integer.TYPE, String.class });
        IndexedPropertyDescriptor ipd = new IndexedPropertyDescriptor(propertyName, readMethod, writeMethod, indexedReadMethod, indexedWriteMethod);
        IndexedPropertyDescriptor ipd2 = new IndexedPropertyDescriptor(propertyName, beanClass);
        TestCase.assertFalse(ipd.equals(ipd2));
    }

    /* indexed write method. */
    public void testEquals_IndexedW() throws IntrospectionException, NoSuchMethodException, SecurityException {
        String propertyName = "PropertyFour";
        Class<MockJavaBean> beanClass = MockJavaBean.class;
        Method readMethod = beanClass.getMethod(("get" + propertyName), ((Class[]) (null)));
        Method writeMethod = beanClass.getMethod(("set" + propertyName), new Class[]{ String[].class });
        Method indexedReadMethod = beanClass.getMethod(("get" + propertyName), new Class[]{ Integer.TYPE });
        Method indexedWriteMethod = beanClass.getMethod("setPropertyFive", new Class[]{ Integer.TYPE, String.class });
        IndexedPropertyDescriptor ipd = new IndexedPropertyDescriptor(propertyName, readMethod, writeMethod, indexedReadMethod, indexedWriteMethod);
        IndexedPropertyDescriptor ipd2 = new IndexedPropertyDescriptor(propertyName, beanClass);
        TestCase.assertFalse(ipd.equals(ipd2));
    }

    /* Indexed write method null. */
    public void testEquals_IndexWNull() throws IntrospectionException, NoSuchMethodException, SecurityException {
        String propertyName = "PropertyFour";
        Class<MockJavaBean> beanClass = MockJavaBean.class;
        Method readMethod = beanClass.getMethod(("get" + propertyName), ((Class[]) (null)));
        Method writeMethod = beanClass.getMethod(("set" + propertyName), new Class[]{ String[].class });
        Method indexedReadMethod = beanClass.getMethod(("get" + propertyName), new Class[]{ Integer.TYPE });
        Method indexedWriteMethod = null;
        IndexedPropertyDescriptor ipd = new IndexedPropertyDescriptor(propertyName, readMethod, writeMethod, indexedReadMethod, indexedWriteMethod);
        IndexedPropertyDescriptor ipd2 = new IndexedPropertyDescriptor(propertyName, beanClass);
        TestCase.assertFalse(ipd.equals(ipd2));
    }

    /* Property Type. */
    public void testEquals_PropertyType() throws IntrospectionException, NoSuchMethodException, SecurityException {
        String propertyName = "PropertyFour";
        Class<MockJavaBean> beanClass = MockJavaBean.class;
        Method readMethod = beanClass.getMethod(("get" + propertyName), ((Class[]) (null)));
        Method writeMethod = beanClass.getMethod(("set" + propertyName), new Class[]{ String[].class });
        Method indexedReadMethod = beanClass.getMethod(("get" + propertyName), new Class[]{ Integer.TYPE });
        Method indexedWriteMethod = beanClass.getMethod(("set" + propertyName), new Class[]{ Integer.TYPE, String.class });
        IndexedPropertyDescriptor ipd = new IndexedPropertyDescriptor(propertyName, readMethod, writeMethod, indexedReadMethod, indexedWriteMethod);
        IndexedPropertyDescriptor ipd2 = new IndexedPropertyDescriptor("PropertySix", beanClass);
        TestCase.assertFalse(ipd.getPropertyType().equals(ipd2.getPropertyType()));
        TestCase.assertFalse(ipd.equals(ipd2));
    }

    /* Class under test for void IndexedPropertyDescriptor(String, Class) */
    public void testIndexedPropertyDescriptorStringClass() throws IntrospectionException, NoSuchMethodException, SecurityException {
        String propertyName = "propertyFour";
        Class<MockJavaBean> beanClass = MockJavaBean.class;
        IndexedPropertyDescriptor ipd = new IndexedPropertyDescriptor(propertyName, beanClass);
        String capitalName = (propertyName.substring(0, 1).toUpperCase()) + (propertyName.substring(1));
        Method readMethod = beanClass.getMethod(("get" + capitalName), ((Class[]) (null)));
        Method writeMethod = beanClass.getMethod(("set" + capitalName), new Class[]{ String[].class });
        Method indexedReadMethod = beanClass.getMethod(("get" + capitalName), new Class[]{ Integer.TYPE });
        Method indexedWriteMethod = beanClass.getMethod(("set" + capitalName), new Class[]{ Integer.TYPE, String.class });
        TestCase.assertEquals(readMethod, ipd.getReadMethod());
        TestCase.assertEquals(writeMethod, ipd.getWriteMethod());
        TestCase.assertEquals(indexedReadMethod, ipd.getIndexedReadMethod());
        TestCase.assertEquals(indexedWriteMethod, ipd.getIndexedWriteMethod());
        TestCase.assertEquals(String[].class, ipd.getPropertyType());
        TestCase.assertEquals(String.class, ipd.getIndexedPropertyType());
        TestCase.assertFalse(ipd.isBound());
        TestCase.assertFalse(ipd.isConstrained());
        TestCase.assertEquals(propertyName, ipd.getDisplayName());
        TestCase.assertEquals(propertyName, ipd.getName());
        TestCase.assertEquals(propertyName, ipd.getShortDescription());
        TestCase.assertNotNull(ipd.attributeNames());
        TestCase.assertFalse(ipd.isExpert());
        TestCase.assertFalse(ipd.isHidden());
        TestCase.assertFalse(ipd.isPreferred());
        // Regression for HARMONY-1236
        try {
            new IndexedPropertyDescriptor("0xDFRF", Float.TYPE);
            TestCase.fail("IntrospectionException expected");
        } catch (IntrospectionException e) {
            // expected
        }
    }

    public void testIndexedPropertyDescriptorStringClass_PropertyNameNull() throws IntrospectionException {
        String propertyName = null;
        Class<MockJavaBean> beanClass = MockJavaBean.class;
        try {
            new IndexedPropertyDescriptor(propertyName, beanClass);
            TestCase.fail("Should throw IntrospectionException");
        } catch (IntrospectionException e) {
        }
    }

    public void testIndexedPropertyDescriptorStringClass_PropertyNameEmpty() throws IntrospectionException {
        String propertyName = "";
        Class<MockJavaBean> beanClass = MockJavaBean.class;
        try {
            new IndexedPropertyDescriptor(propertyName, beanClass);
            TestCase.fail("Should throw IntrospectionException");
        } catch (IntrospectionException e) {
        }
    }

    public void testIndexedPropertyDescriptorStringClass_PropertyNameInvalid() throws IntrospectionException {
        String propertyName = "Not a property";
        Class<MockJavaBean> beanClass = MockJavaBean.class;
        try {
            new IndexedPropertyDescriptor(propertyName, beanClass);
            TestCase.fail("Should throw IntrospectionException");
        } catch (IntrospectionException e) {
        }
    }

    public void testIndexedPropertyDescriptorStringClass_NotIndexedProperty() throws IntrospectionException {
        String propertyName = "propertyOne";
        Class<MockJavaBean> beanClass = MockJavaBean.class;
        try {
            new IndexedPropertyDescriptor(propertyName, beanClass);
            TestCase.fail("Should throw IntrospectionException");
        } catch (IntrospectionException e) {
        }
    }

    public void testIndexedPropertyDescriptorStringClass_ClassNull() throws IntrospectionException {
        String propertyName = "propertyFour";
        Class<?> beanClass = null;
        try {
            new IndexedPropertyDescriptor(propertyName, beanClass);
            TestCase.fail("Should throw IntrospectionException");
        } catch (IntrospectionException e) {
        }
    }

    /* bean class does not implements java.io.Serializable */
    public void testIndexedPropertyDescriptorStringClass_NotBeanClass() throws IntrospectionException {
        String propertyName = "propertyOne";
        Class<IndexedPropertyDescriptorTest.NotJavaBean> beanClass = IndexedPropertyDescriptorTest.NotJavaBean.class;
        IndexedPropertyDescriptor ipd = new IndexedPropertyDescriptor(propertyName, beanClass);
        TestCase.assertEquals(String.class, ipd.getIndexedPropertyType());
    }

    private class MyClass {
        private int[] a;

        public void setA(int v, int i) {
            a[i] = v;
        }

        public void setA(int[] a) {
            this.a = a;
        }

        public int[] getA() {
            return a;
        }
    }

    /* Class under test for void IndexedPropertyDescriptor(String, Class,
    String, String, String, String)
     */
    public void testIndexedPropertyDescriptorStringClassStringStringStringString() throws IntrospectionException, NoSuchMethodException, SecurityException {
        String propertyName = "PropertyFour";
        Class<MockJavaBean> beanClass = MockJavaBean.class;
        IndexedPropertyDescriptor ipd = new IndexedPropertyDescriptor(propertyName, beanClass, ("get" + propertyName), ("set" + propertyName), ("get" + propertyName), ("set" + propertyName));
        Method readMethod = beanClass.getMethod(("get" + propertyName), ((Class[]) (null)));
        Method writeMethod = beanClass.getMethod(("set" + propertyName), new Class[]{ String[].class });
        Method indexedReadMethod = beanClass.getMethod(("get" + propertyName), new Class[]{ Integer.TYPE });
        Method indexedWriteMethod = beanClass.getMethod(("set" + propertyName), new Class[]{ Integer.TYPE, String.class });
        TestCase.assertEquals(readMethod, ipd.getReadMethod());
        TestCase.assertEquals(writeMethod, ipd.getWriteMethod());
        TestCase.assertEquals(indexedReadMethod, ipd.getIndexedReadMethod());
        TestCase.assertEquals(indexedWriteMethod, ipd.getIndexedWriteMethod());
        TestCase.assertEquals(String[].class, ipd.getPropertyType());
        TestCase.assertEquals(String.class, ipd.getIndexedPropertyType());
        TestCase.assertFalse(ipd.isBound());
        TestCase.assertFalse(ipd.isConstrained());
        TestCase.assertEquals(propertyName, ipd.getDisplayName());
        TestCase.assertEquals(propertyName, ipd.getName());
        TestCase.assertEquals(propertyName, ipd.getShortDescription());
        TestCase.assertNotNull(ipd.attributeNames());
        TestCase.assertFalse(ipd.isExpert());
        TestCase.assertFalse(ipd.isHidden());
        TestCase.assertFalse(ipd.isPreferred());
        // empty method name
        new IndexedPropertyDescriptor(propertyName, beanClass, ("get" + propertyName), ("set" + propertyName), "", ("set" + propertyName));
        try {
            new IndexedPropertyDescriptor("a", IndexedPropertyDescriptorTest.MyClass.class, "getA", "setA", "", "setA");
            TestCase.fail("Shoule throw exception");
        } catch (IntrospectionException e) {
            // expected
        }
        try {
            new IndexedPropertyDescriptor(propertyName, beanClass, "", ("set" + propertyName), ("get" + propertyName), ("set" + propertyName));
            TestCase.fail("Shoule throw exception");
        } catch (IntrospectionException e) {
            // expected
        }
        try {
            new IndexedPropertyDescriptor(propertyName, beanClass, ("get" + propertyName), "", ("get" + propertyName), ("set" + propertyName));
            TestCase.fail("Shoule throw exception");
        } catch (IntrospectionException e) {
            // expected
        }
        try {
            new IndexedPropertyDescriptor(propertyName, beanClass, ("get" + propertyName), ("set" + propertyName), ("get" + propertyName), "");
            TestCase.fail("Shoule throw exception");
        } catch (IntrospectionException e) {
            // expected
        }
        // null method name
        new IndexedPropertyDescriptor(propertyName, beanClass, ("get" + propertyName), ("set" + propertyName), null, ("set" + propertyName));
        new IndexedPropertyDescriptor(propertyName, beanClass, null, ("set" + propertyName), ("get" + propertyName), ("set" + propertyName));
        new IndexedPropertyDescriptor(propertyName, beanClass, ("get" + propertyName), null, ("get" + propertyName), ("set" + propertyName));
        new IndexedPropertyDescriptor(propertyName, beanClass, ("get" + propertyName), ("set" + propertyName), ("get" + propertyName), null);
    }

    public void testIndexedPropertyDescriptorStringClassStringStringStringString_propNull() throws IntrospectionException {
        String propertyName = "PropertyFour";
        Class<MockJavaBean> beanClass = MockJavaBean.class;
        try {
            new IndexedPropertyDescriptor(null, beanClass, ("get" + propertyName), ("set" + propertyName), ("get" + propertyName), ("set" + propertyName));
            TestCase.fail("Should throw IntrospectionException.");
        } catch (IntrospectionException e) {
        }
    }

    public void testIndexedPropertyDescriptorStringClassStringStringStringString_propEmpty() {
        String propertyName = "PropertyFour";
        Class<MockJavaBean> beanClass = MockJavaBean.class;
        try {
            new IndexedPropertyDescriptor("", beanClass, ("get" + propertyName), ("set" + propertyName), ("get" + propertyName), ("set" + propertyName));
            TestCase.fail("Should throw IntrospectionException.");
        } catch (IntrospectionException e) {
        }
    }

    public void testIndexedPropertyDescriptorStringClassStringStringStringString_propInvalid() throws IntrospectionException {
        String propertyName = "PropertyFour";
        String invalidProp = "Not a prop";
        Class<MockJavaBean> beanClass = MockJavaBean.class;
        IndexedPropertyDescriptor ipd = new IndexedPropertyDescriptor(invalidProp, beanClass, ("get" + propertyName), ("set" + propertyName), ("get" + propertyName), ("set" + propertyName));
        TestCase.assertEquals(String[].class, ipd.getPropertyType());
        TestCase.assertEquals(String.class, ipd.getIndexedPropertyType());
        TestCase.assertEquals(invalidProp, ipd.getName());
    }

    public void testIndexedPropertyDescriptorStringClassStringStringStringString_BeanClassNull() throws IntrospectionException {
        String propertyName = "PropertyFour";
        Class<?> beanClass = null;
        try {
            new IndexedPropertyDescriptor(propertyName, beanClass, ("get" + propertyName), ("set" + propertyName), ("get" + propertyName), ("set" + propertyName));
            TestCase.fail("Should throw IntrospectionException.");
        } catch (IntrospectionException e) {
        }
    }

    public void testIndexedPropertyDescriptorStringClassStringStringStringString_ReadMethodNull() throws IntrospectionException {
        String propertyName = "PropertyFour";
        Class<MockJavaBean> beanClass = MockJavaBean.class;
        IndexedPropertyDescriptor ipd = new IndexedPropertyDescriptor(propertyName, beanClass, null, ("set" + propertyName), ("get" + propertyName), ("set" + propertyName));
        TestCase.assertNull(ipd.getReadMethod());
        TestCase.assertNotNull(ipd.getWriteMethod());
        TestCase.assertEquals(String.class, ipd.getIndexedPropertyType());
    }

    public void testIndexedPropertyDescriptorStringClassStringStringStringString_WriteMethodNull() throws IntrospectionException {
        String propertyName = "PropertyFour";
        Class<MockJavaBean> beanClass = MockJavaBean.class;
        IndexedPropertyDescriptor ipd = new IndexedPropertyDescriptor(propertyName, beanClass, ("get" + propertyName), null, ("get" + propertyName), ("set" + propertyName));
        TestCase.assertNotNull(ipd.getReadMethod());
        TestCase.assertNull(ipd.getWriteMethod());
        TestCase.assertEquals(String.class, ipd.getIndexedPropertyType());
        new IndexedPropertyDescriptor(propertyName, beanClass, ("get" + propertyName), ("set" + propertyName), "", ("set" + propertyName));
        try {
            new IndexedPropertyDescriptor(propertyName, beanClass, ("get" + propertyName), ("set" + propertyName), ("get" + propertyName), "");
            TestCase.fail();
        } catch (Exception e) {
        }
    }

    public void testIndexedPropertyDescriptorStringClassStringStringStringString_IndexedReadMethodNull() throws IntrospectionException {
        String propertyName = "PropertyFour";
        Class<MockJavaBean> beanClass = MockJavaBean.class;
        IndexedPropertyDescriptor ipd = new IndexedPropertyDescriptor(propertyName, beanClass, ("get" + propertyName), ("set" + propertyName), null, ("set" + propertyName));
        TestCase.assertNull(ipd.getIndexedReadMethod());
        TestCase.assertNotNull(ipd.getIndexedWriteMethod());
        TestCase.assertEquals(String.class, ipd.getIndexedPropertyType());
    }

    public void testIndexedPropertyDescriptorStringClassStringStringStringString_IndexedWriteMethodNull() throws IntrospectionException {
        String propertyName = "PropertyFour";
        Class<MockJavaBean> beanClass = MockJavaBean.class;
        IndexedPropertyDescriptor ipd = new IndexedPropertyDescriptor(propertyName, beanClass, ("get" + propertyName), ("set" + propertyName), ("get" + propertyName), null);
        TestCase.assertNotNull(ipd.getIndexedReadMethod());
        TestCase.assertNull(ipd.getIndexedWriteMethod());
        TestCase.assertEquals(String.class, ipd.getIndexedPropertyType());
    }

    /**
     * indexed read/write null
     */
    public void testIndexedPropertyDescriptorStringClassStringStringStringString_RWNull() throws IntrospectionException {
        String propertyName = "PropertyFour";
        Class<MockJavaBean> beanClass = MockJavaBean.class;
        IndexedPropertyDescriptor ipd = new IndexedPropertyDescriptor(propertyName, beanClass, null, null, ("get" + propertyName), ("set" + propertyName));
        TestCase.assertNull(ipd.getReadMethod());
        TestCase.assertNull(ipd.getWriteMethod());
        TestCase.assertEquals(String.class, ipd.getIndexedPropertyType());
        TestCase.assertNull(ipd.getPropertyType());
    }

    /**
     * indexed read/write null
     */
    public void testIndexedPropertyDescriptorStringClassStringStringStringString_IndexedRWNull() throws IntrospectionException {
        String propertyName = "PropertyFour";
        Class<MockJavaBean> beanClass = MockJavaBean.class;
        try {
            new IndexedPropertyDescriptor(propertyName, beanClass, ("get" + propertyName), ("set" + propertyName), null, null);
            TestCase.fail("Should throw IntrospectionException.");
        } catch (IntrospectionException e) {
        }
    }

    /**
     * index read /read null
     */
    public void testIndexedPropertyDescriptorStringClassStringStringStringString_RNull() throws IntrospectionException {
        String propertyName = "PropertyFour";
        Class<MockJavaBean> beanClass = MockJavaBean.class;
        IndexedPropertyDescriptor ipd = new IndexedPropertyDescriptor(propertyName, beanClass, null, ("set" + propertyName), null, ("set" + propertyName));
        TestCase.assertEquals(String.class, ipd.getIndexedPropertyType());
        TestCase.assertEquals(String[].class, ipd.getPropertyType());
        TestCase.assertNotNull(ipd.getWriteMethod());
        TestCase.assertNotNull(ipd.getIndexedWriteMethod());
    }

    /**
     * index write /write null
     */
    public void testIndexedPropertyDescriptorStringClassStringStringStringString_WNull() throws IntrospectionException {
        String propertyName = "PropertyFour";
        Class<MockJavaBean> beanClass = MockJavaBean.class;
        IndexedPropertyDescriptor ipd = new IndexedPropertyDescriptor(propertyName, beanClass, ("get" + propertyName), null, ("get" + propertyName), null);
        TestCase.assertEquals(String.class, ipd.getIndexedPropertyType());
        TestCase.assertEquals(String[].class, ipd.getPropertyType());
        TestCase.assertNotNull(ipd.getReadMethod());
        TestCase.assertNotNull(ipd.getIndexedReadMethod());
    }

    public void testIndexedPropertyDescriptorStringClassStringStringStringString_allNull() throws IntrospectionException {
        String propertyName = "PropertyFour";
        Class<MockJavaBean> beanClass = MockJavaBean.class;
        IndexedPropertyDescriptor ipd = new IndexedPropertyDescriptor(propertyName, beanClass, null, null, null, null);
        TestCase.assertNull(ipd.getIndexedPropertyType());
        TestCase.assertNull(ipd.getPropertyType());
        TestCase.assertNull(ipd.getReadMethod());
        TestCase.assertNull(ipd.getIndexedReadMethod());
    }

    /* read/write incompatible */
    public void testIndexedPropertyDescriptorStringClassStringStringStringString_RWIncompatible() throws IntrospectionException {
        String propertyName = "PropertyFour";
        String anotherProp = "PropertyFive";
        Class<MockJavaBean> beanClass = MockJavaBean.class;
        IndexedPropertyDescriptor ipd = new IndexedPropertyDescriptor(propertyName, beanClass, ("get" + propertyName), ("set" + anotherProp), ("get" + propertyName), ("set" + propertyName));
        TestCase.assertEquals(String.class, ipd.getIndexedPropertyType());
        TestCase.assertEquals(String[].class, ipd.getPropertyType());
        TestCase.assertEquals(("set" + anotherProp), ipd.getWriteMethod().getName());
    }

    /**
     * IndexedRead/IndexedWrite incompatible
     *
     * @throws IntrospectionException
     * 		
     */
    public void testIndexedPropertyDescriptorStringClassStringStringStringString_IndexedRWIncompatible() throws IntrospectionException {
        String propertyName = "PropertyFour";
        String anotherProp = "PropertyFive";
        Class<MockJavaBean> beanClass = MockJavaBean.class;
        IndexedPropertyDescriptor ipd = new IndexedPropertyDescriptor(propertyName, beanClass, ("get" + propertyName), ("set" + propertyName), ("get" + propertyName), ("set" + anotherProp));
        TestCase.assertEquals(String.class, ipd.getIndexedPropertyType());
        TestCase.assertEquals(String[].class, ipd.getPropertyType());
        TestCase.assertEquals(("set" + anotherProp), ipd.getIndexedWriteMethod().getName());
    }

    /* ReadMethod/IndexedReadMethod incompatible */
    public void testIndexedPropertyDescriptorStringClassStringStringStringString_RIndexedRcompatible() throws IntrospectionException {
        String propertyName = "PropertyFour";
        String anotherProp = "PropertyFive";
        Class<MockJavaBean> beanClass = MockJavaBean.class;
        IndexedPropertyDescriptor ipd = new IndexedPropertyDescriptor(propertyName, beanClass, ("get" + propertyName), ("set" + propertyName), ("get" + anotherProp), ("set" + anotherProp));
        TestCase.assertEquals(String.class, ipd.getIndexedPropertyType());
        TestCase.assertEquals(String[].class, ipd.getPropertyType());
        TestCase.assertEquals(("set" + anotherProp), ipd.getIndexedWriteMethod().getName());
    }

    public void testIndexedPropertyDescriptorStringClassStringStringStringString_WrongArgumentNumber() throws IntrospectionException {
        IndexedPropertyDescriptor ipd = new IndexedPropertyDescriptor("a", IndexedPropertyDescriptorTest.DummyClass.class, null, "setAI", "getAI", "setAI");
        TestCase.assertNotNull(ipd);
    }

    private class DummyClass {
        private int[] a;

        public void setAI(int v, int i) {
            a[i] = v;
        }

        public void setAI(int[] a) {
            this.a = a;
        }

        public int[] getA() {
            return a;
        }

        public int getAI(int i) {
            return a[i];
        }
    }

    /* Class under test for void IndexedPropertyDescriptor(String, Method,
    Method, Method, Method)
     */
    public void testIndexedPropertyDescriptorStringMethodMethodMethodMethod() throws IntrospectionException, NoSuchMethodException, SecurityException {
        String propertyName = "PropertyFour";
        Class<MockJavaBean> beanClass = MockJavaBean.class;
        Method readMethod = beanClass.getMethod(("get" + propertyName), ((Class[]) (null)));
        Method writeMethod = beanClass.getMethod(("set" + propertyName), new Class[]{ String[].class });
        Method indexedReadMethod = beanClass.getMethod(("get" + propertyName), new Class[]{ Integer.TYPE });
        Method indexedWriteMethod = beanClass.getMethod(("set" + propertyName), new Class[]{ Integer.TYPE, String.class });
        IndexedPropertyDescriptor ipd = new IndexedPropertyDescriptor(propertyName, readMethod, writeMethod, indexedReadMethod, indexedWriteMethod);
        TestCase.assertEquals(readMethod, ipd.getReadMethod());
        TestCase.assertEquals(writeMethod, ipd.getWriteMethod());
        TestCase.assertEquals(indexedReadMethod, ipd.getIndexedReadMethod());
        TestCase.assertEquals(indexedWriteMethod, ipd.getIndexedWriteMethod());
        TestCase.assertEquals(String[].class, ipd.getPropertyType());
        TestCase.assertEquals(String.class, ipd.getIndexedPropertyType());
        TestCase.assertFalse(ipd.isBound());
        TestCase.assertFalse(ipd.isConstrained());
        TestCase.assertEquals(propertyName, ipd.getDisplayName());
        TestCase.assertEquals(propertyName, ipd.getName());
        TestCase.assertEquals(propertyName, ipd.getShortDescription());
        TestCase.assertNotNull(ipd.attributeNames());
        TestCase.assertFalse(ipd.isExpert());
        TestCase.assertFalse(ipd.isHidden());
        TestCase.assertFalse(ipd.isPreferred());
    }

    /* propertyName=null */
    public void testIndexedPropertyDescriptorStringMethodMethodMethodMethod_propNull() throws IntrospectionException, NoSuchMethodException, SecurityException {
        String propertyName = "PropertyFour";
        Class<MockJavaBean> beanClass = MockJavaBean.class;
        Method readMethod = beanClass.getMethod(("get" + propertyName), ((Class[]) (null)));
        Method writeMethod = beanClass.getMethod(("set" + propertyName), new Class[]{ String[].class });
        Method indexedReadMethod = beanClass.getMethod(("get" + propertyName), new Class[]{ Integer.TYPE });
        Method indexedWriteMethod = beanClass.getMethod(("set" + propertyName), new Class[]{ Integer.TYPE, String.class });
        try {
            new IndexedPropertyDescriptor(null, readMethod, writeMethod, indexedReadMethod, indexedWriteMethod);
            TestCase.fail("Should throw IntrospectionException.");
        } catch (IntrospectionException e) {
        }
    }

    /* propertyname=""; */
    public void testIndexedPropertyDescriptorStringMethodMethodMethodMethod_propEmpty() throws NoSuchMethodException, SecurityException {
        String propertyName = "PropertyFour";
        Class<MockJavaBean> beanClass = MockJavaBean.class;
        Method readMethod = beanClass.getMethod(("get" + propertyName), ((Class[]) (null)));
        Method writeMethod = beanClass.getMethod(("set" + propertyName), new Class[]{ String[].class });
        Method indexedReadMethod = beanClass.getMethod(("get" + propertyName), new Class[]{ Integer.TYPE });
        Method indexedWriteMethod = beanClass.getMethod(("set" + propertyName), new Class[]{ Integer.TYPE, String.class });
        try {
            new IndexedPropertyDescriptor("", readMethod, writeMethod, indexedReadMethod, indexedWriteMethod);
            TestCase.fail("Should throw IntrospectionException.");
        } catch (IntrospectionException e) {
        }
    }

    public void testIndexedPropertyDescriptorStringMethodMethodMethodMethod_propInvalid() throws IntrospectionException, NoSuchMethodException, SecurityException {
        String propertyName = "PropertyFour";
        String invalidName = "An Invalid Property name";
        Class<MockJavaBean> beanClass = MockJavaBean.class;
        Method readMethod = beanClass.getMethod(("get" + propertyName), ((Class[]) (null)));
        Method writeMethod = beanClass.getMethod(("set" + propertyName), new Class[]{ String[].class });
        Method indexedReadMethod = beanClass.getMethod(("get" + propertyName), new Class[]{ Integer.TYPE });
        Method indexedWriteMethod = beanClass.getMethod(("set" + propertyName), new Class[]{ Integer.TYPE, String.class });
        IndexedPropertyDescriptor ipd = new IndexedPropertyDescriptor(invalidName, readMethod, writeMethod, indexedReadMethod, indexedWriteMethod);
        TestCase.assertEquals(invalidName, ipd.getName());
        TestCase.assertEquals(String.class, ipd.getIndexedPropertyType());
    }

    public void testIndexedPropertyDescriptorStringMethodMethodMethodMethod_ReadMethodNull() throws IntrospectionException, NoSuchMethodException, SecurityException {
        String propertyName = "PropertyFour";
        Class<MockJavaBean> beanClass = MockJavaBean.class;
        Method writeMethod = beanClass.getMethod(("set" + propertyName), new Class[]{ String[].class });
        Method indexedReadMethod = beanClass.getMethod(("get" + propertyName), new Class[]{ Integer.TYPE });
        Method indexedWriteMethod = beanClass.getMethod(("set" + propertyName), new Class[]{ Integer.TYPE, String.class });
        IndexedPropertyDescriptor ipd = new IndexedPropertyDescriptor(propertyName, null, writeMethod, indexedReadMethod, indexedWriteMethod);
        TestCase.assertNull(ipd.getReadMethod());
        TestCase.assertEquals(String[].class, ipd.getPropertyType());
        TestCase.assertEquals(String.class, ipd.getIndexedPropertyType());
    }

    public void testIndexedPropertyDescriptorStringMethodMethodMethodMethod_WriteMethodNull() throws IntrospectionException, NoSuchMethodException, SecurityException {
        String propertyName = "PropertyFour";
        Class<MockJavaBean> beanClass = MockJavaBean.class;
        Method readMethod = beanClass.getMethod(("get" + propertyName), ((Class[]) (null)));
        Method indexedReadMethod = beanClass.getMethod(("get" + propertyName), new Class[]{ Integer.TYPE });
        Method indexedWriteMethod = beanClass.getMethod(("set" + propertyName), new Class[]{ Integer.TYPE, String.class });
        IndexedPropertyDescriptor ipd = new IndexedPropertyDescriptor(propertyName, readMethod, null, indexedReadMethod, indexedWriteMethod);
        TestCase.assertNull(ipd.getWriteMethod());
        TestCase.assertEquals(String[].class, ipd.getPropertyType());
        TestCase.assertEquals(String.class, ipd.getIndexedPropertyType());
    }

    public void testIndexedPropertyDescriptorStringMethodMethodMethodMethod_IndexedReadMethodNull() throws IntrospectionException, NoSuchMethodException, SecurityException {
        String propertyName = "PropertyFour";
        Class<MockJavaBean> beanClass = MockJavaBean.class;
        Method readMethod = beanClass.getMethod(("get" + propertyName), ((Class[]) (null)));
        Method writeMethod = beanClass.getMethod(("set" + propertyName), new Class[]{ String[].class });
        Method indexedWriteMethod = beanClass.getMethod(("set" + propertyName), new Class[]{ Integer.TYPE, String.class });
        IndexedPropertyDescriptor ipd = new IndexedPropertyDescriptor(propertyName, readMethod, writeMethod, null, indexedWriteMethod);
        TestCase.assertNull(ipd.getIndexedReadMethod());
        TestCase.assertEquals(String[].class, ipd.getPropertyType());
        TestCase.assertEquals(String.class, ipd.getIndexedPropertyType());
    }

    public void testIndexedPropertyDescriptorStringMethodMethodMethodMethod_IndexedWriteMethodNull() throws IntrospectionException, NoSuchMethodException, SecurityException {
        String propertyName = "PropertyFour";
        Class<MockJavaBean> beanClass = MockJavaBean.class;
        Method readMethod = beanClass.getMethod(("get" + propertyName), ((Class[]) (null)));
        Method writeMethod = beanClass.getMethod(("set" + propertyName), new Class[]{ String[].class });
        Method indexedReadMethod = beanClass.getMethod(("get" + propertyName), new Class[]{ Integer.TYPE });
        IndexedPropertyDescriptor ipd = new IndexedPropertyDescriptor(propertyName, readMethod, writeMethod, indexedReadMethod, null);
        TestCase.assertNull(ipd.getIndexedWriteMethod());
        TestCase.assertEquals(String[].class, ipd.getPropertyType());
        TestCase.assertEquals(String.class, ipd.getIndexedPropertyType());
    }

    public void testIndexedPropertyDescriptorStringMethodMethodMethodMethod_IndexedRWNull() throws IntrospectionException, NoSuchMethodException, SecurityException {
        String propertyName = "PropertyFour";
        Class<MockJavaBean> beanClass = MockJavaBean.class;
        Method readMethod = beanClass.getMethod(("get" + propertyName), ((Class[]) (null)));
        Method writeMethod = beanClass.getMethod(("set" + propertyName), new Class[]{ String[].class });
        try {
            new IndexedPropertyDescriptor(propertyName, readMethod, writeMethod, null, null);
            TestCase.fail("Should throw IntrospectionException.");
        } catch (IntrospectionException e) {
        }
    }

    public void testIndexedPropertyDescriptorStringMethodMethodMethodMethod_RWNull() throws IntrospectionException, NoSuchMethodException, SecurityException {
        String propertyName = "PropertyFour";
        Class<MockJavaBean> beanClass = MockJavaBean.class;
        Method indexedReadMethod = beanClass.getMethod(("get" + propertyName), new Class[]{ Integer.TYPE });
        Method indexedWriteMethod = beanClass.getMethod(("set" + propertyName), new Class[]{ Integer.TYPE, String.class });
        IndexedPropertyDescriptor ipd = new IndexedPropertyDescriptor(propertyName, null, null, indexedReadMethod, indexedWriteMethod);
        TestCase.assertNull(ipd.getPropertyType());
        TestCase.assertEquals(String.class, ipd.getIndexedPropertyType());
    }

    /* read/write incompatible */
    public void testIndexedPropertyDescriptorStringMethodMethodMethodMethod_RWIncompatible() throws IntrospectionException, NoSuchMethodException, SecurityException {
        String propertyName = "PropertyFour";
        String anotherProp = "PropertyFive";
        Class<MockJavaBean> beanClass = MockJavaBean.class;
        Method readMethod = beanClass.getMethod(("get" + propertyName), ((Class[]) (null)));
        Method writeMethod = beanClass.getMethod(("set" + anotherProp), new Class[]{ String[].class });
        Method indexedReadMethod = beanClass.getMethod(("get" + propertyName), new Class[]{ Integer.TYPE });
        Method indexedWriteMethod = beanClass.getMethod(("set" + propertyName), new Class[]{ Integer.TYPE, String.class });
        IndexedPropertyDescriptor ipd = new IndexedPropertyDescriptor(propertyName, readMethod, writeMethod, indexedReadMethod, indexedWriteMethod);
        TestCase.assertEquals(propertyName, ipd.getName());
        TestCase.assertEquals(String[].class, ipd.getPropertyType());
        TestCase.assertEquals(String.class, ipd.getIndexedPropertyType());
    }

    /* IndexedRead/IndexedWrite incompatible */
    public void testIndexedPropertyDescriptorStringMethodMethodMethodMethod_IndexedRWIncompatible() throws IntrospectionException, NoSuchMethodException, SecurityException {
        String propertyName = "PropertyFour";
        String anotherProp = "PropertyFive";
        Class<MockJavaBean> beanClass = MockJavaBean.class;
        Method readMethod = beanClass.getMethod(("get" + propertyName), ((Class[]) (null)));
        Method writeMethod = beanClass.getMethod(("set" + propertyName), new Class[]{ String[].class });
        Method indexedReadMethod = beanClass.getMethod(("get" + anotherProp), new Class[]{ Integer.TYPE });
        Method indexedWriteMethod = beanClass.getMethod(("set" + propertyName), new Class[]{ Integer.TYPE, String.class });
        IndexedPropertyDescriptor ipd = new IndexedPropertyDescriptor(propertyName, readMethod, writeMethod, indexedReadMethod, indexedWriteMethod);
        TestCase.assertEquals(propertyName, ipd.getName());
        TestCase.assertEquals(String[].class, ipd.getPropertyType());
        TestCase.assertEquals(String.class, ipd.getIndexedPropertyType());
        indexedReadMethod = beanClass.getMethod(("get" + anotherProp), new Class[]{ Integer.TYPE, Integer.TYPE });
        try {
            new IndexedPropertyDescriptor(propertyName, readMethod, writeMethod, indexedReadMethod, indexedWriteMethod);
            TestCase.fail("should throw IntrosecptionException");
        } catch (IntrospectionException e) {
            // expected
        }
    }

    public void testSetIndexedReadMethod() throws IntrospectionException, NoSuchMethodException, SecurityException {
        String propertyName = "PropertyFour";
        Class<MockJavaBean> beanClass = MockJavaBean.class;
        Method readMethod = beanClass.getMethod(("get" + propertyName), ((Class[]) (null)));
        Method writeMethod = beanClass.getMethod(("set" + propertyName), new Class[]{ String[].class });
        Method indexedReadMethod = beanClass.getMethod(("get" + propertyName), new Class[]{ Integer.TYPE });
        Method indexedWriteMethod = beanClass.getMethod(("set" + propertyName), new Class[]{ Integer.TYPE, String.class });
        IndexedPropertyDescriptor ipd = new IndexedPropertyDescriptor(propertyName, readMethod, writeMethod, null, indexedWriteMethod);
        TestCase.assertNull(ipd.getIndexedReadMethod());
        ipd.setIndexedReadMethod(indexedReadMethod);
        TestCase.assertSame(indexedReadMethod, ipd.getIndexedReadMethod());
    }

    public void testSetIndexedReadMethod_invalid() throws IntrospectionException, NoSuchMethodException, SecurityException {
        String propertyName = "PropertyFour";
        Class<MockJavaBean> beanClass = MockJavaBean.class;
        Method indexedReadMethod = beanClass.getMethod(("get" + propertyName), new Class[]{ Integer.TYPE });
        Method indexedWriteMethod = beanClass.getMethod(("set" + propertyName), new Class[]{ Integer.TYPE, String.class });
        IndexedPropertyDescriptor ipd = new IndexedPropertyDescriptor(propertyName, null, null, indexedReadMethod, indexedWriteMethod);
        Method indexedReadMethod2 = beanClass.getMethod("getPropertySix", new Class[]{ Integer.TYPE });
        try {
            ipd.setIndexedReadMethod(indexedReadMethod2);
            TestCase.fail("Should throw IntrospectionException.");
        } catch (IntrospectionException e) {
        }
    }

    public void testSetIndexedReadMethod_null() throws IntrospectionException, NoSuchMethodException, SecurityException {
        String propertyName = "PropertyFour";
        Class<MockJavaBean> beanClass = MockJavaBean.class;
        Method readMethod = beanClass.getMethod(("get" + propertyName), ((Class[]) (null)));
        Method writeMethod = beanClass.getMethod(("set" + propertyName), new Class[]{ String[].class });
        Method indexedReadMethod = beanClass.getMethod(("get" + propertyName), new Class[]{ Integer.TYPE });
        Method indexedWriteMethod = beanClass.getMethod(("set" + propertyName), new Class[]{ Integer.TYPE, String.class });
        IndexedPropertyDescriptor ipd = new IndexedPropertyDescriptor(propertyName, readMethod, writeMethod, indexedReadMethod, indexedWriteMethod);
        TestCase.assertSame(indexedReadMethod, ipd.getIndexedReadMethod());
        ipd.setIndexedReadMethod(null);
        TestCase.assertNull(ipd.getIndexedReadMethod());
    }

    /* indexed read method without args */
    public void testSetIndexedReadMethod_RInvalidArgs() throws IntrospectionException, NoSuchMethodException, SecurityException {
        String propertyName = "PropertyFour";
        Class<MockJavaBean> beanClass = MockJavaBean.class;
        Method readMethod = beanClass.getMethod(("get" + propertyName), ((Class[]) (null)));
        Method writeMethod = beanClass.getMethod(("set" + propertyName), new Class[]{ String[].class });
        Method indexedReadMethod = beanClass.getMethod(("get" + propertyName), new Class[]{ Integer.TYPE });
        Method indexedWriteMethod = beanClass.getMethod(("set" + propertyName), new Class[]{ Integer.TYPE, String.class });
        IndexedPropertyDescriptor ipd = new IndexedPropertyDescriptor(propertyName, readMethod, writeMethod, indexedReadMethod, indexedWriteMethod);
        TestCase.assertSame(indexedReadMethod, ipd.getIndexedReadMethod());
        try {
            ipd.setIndexedReadMethod(readMethod);
            TestCase.fail("Should throw IntrospectionException.");
        } catch (IntrospectionException e) {
        }
    }

    /* indexed read method with invalid arg type (!Integer.TYPE) */
    public void testSetIndexedReadMethod_RInvalidArgType() throws IntrospectionException, NoSuchMethodException, SecurityException {
        String propertyName = "PropertyFour";
        Class<MockJavaBean> beanClass = MockJavaBean.class;
        Method readMethod = beanClass.getMethod(("get" + propertyName), ((Class[]) (null)));
        Method writeMethod = beanClass.getMethod(("set" + propertyName), new Class[]{ String[].class });
        Method indexedReadMethod = beanClass.getMethod(("get" + propertyName), new Class[]{ Integer.TYPE });
        Method indexedWriteMethod = beanClass.getMethod(("set" + propertyName), new Class[]{ Integer.TYPE, String.class });
        IndexedPropertyDescriptor ipd = new IndexedPropertyDescriptor(propertyName, readMethod, writeMethod, indexedReadMethod, indexedWriteMethod);
        TestCase.assertSame(indexedReadMethod, ipd.getIndexedReadMethod());
        try {
            ipd.setIndexedReadMethod(writeMethod);
            TestCase.fail("Should throw IntrospectionException.");
        } catch (IntrospectionException e) {
        }
    }

    /* indexed read method with void return. */
    public void testSetIndexedReadMethod_RInvalidReturn() throws IntrospectionException, NoSuchMethodException, SecurityException {
        String propertyName = "PropertyFour";
        Class<MockJavaBean> beanClass = MockJavaBean.class;
        Method readMethod = beanClass.getMethod(("get" + propertyName), ((Class[]) (null)));
        Method writeMethod = beanClass.getMethod(("set" + propertyName), new Class[]{ String[].class });
        Method indexedReadMethod = beanClass.getMethod(("get" + propertyName), new Class[]{ Integer.TYPE });
        Method indexedWriteMethod = beanClass.getMethod(("set" + propertyName), new Class[]{ Integer.TYPE, String.class });
        IndexedPropertyDescriptor ipd = new IndexedPropertyDescriptor(propertyName, readMethod, writeMethod, indexedReadMethod, indexedWriteMethod);
        TestCase.assertSame(indexedReadMethod, ipd.getIndexedReadMethod());
        Method voidMethod = beanClass.getMethod("getPropertyFourInvalid", new Class[]{ Integer.TYPE });
        try {
            ipd.setIndexedReadMethod(voidMethod);
            TestCase.fail("Should throw IntrospectionException.");
        } catch (IntrospectionException e) {
        }
    }

    public void testSetIndexedWriteMethod_null() throws IntrospectionException, NoSuchMethodException {
        String propertyName = "PropertyFour";
        Class<MockJavaBean> beanClass = MockJavaBean.class;
        Method readMethod = beanClass.getMethod(("get" + propertyName), ((Class[]) (null)));
        Method writeMethod = beanClass.getMethod(("set" + propertyName), new Class[]{ String[].class });
        Method indexedReadMethod = beanClass.getMethod(("get" + propertyName), new Class[]{ Integer.TYPE });
        Method indexedWriteMethod = beanClass.getMethod(("set" + propertyName), new Class[]{ Integer.TYPE, String.class });
        IndexedPropertyDescriptor ipd = new IndexedPropertyDescriptor(propertyName, readMethod, writeMethod, indexedReadMethod, indexedWriteMethod);
        TestCase.assertSame(indexedWriteMethod, ipd.getIndexedWriteMethod());
        ipd.setIndexedWriteMethod(null);
        TestCase.assertNull(ipd.getIndexedWriteMethod());
    }

    public void testSetIndexedWriteMethod() throws IntrospectionException, NoSuchMethodException {
        String propertyName = "PropertyFour";
        Class<MockJavaBean> beanClass = MockJavaBean.class;
        Method readMethod = beanClass.getMethod(("get" + propertyName), ((Class[]) (null)));
        Method writeMethod = beanClass.getMethod(("set" + propertyName), new Class[]{ String[].class });
        Method indexedReadMethod = beanClass.getMethod(("get" + propertyName), new Class[]{ Integer.TYPE });
        Method indexedWriteMethod = beanClass.getMethod(("set" + propertyName), new Class[]{ Integer.TYPE, String.class });
        IndexedPropertyDescriptor ipd = new IndexedPropertyDescriptor(propertyName, readMethod, writeMethod, indexedReadMethod, null);
        TestCase.assertNull(ipd.getIndexedWriteMethod());
        ipd.setIndexedWriteMethod(indexedWriteMethod);
        TestCase.assertSame(indexedWriteMethod, ipd.getIndexedWriteMethod());
    }

    /* bad arg count */
    public void testSetIndexedWriteMethod_noargs() throws IntrospectionException, NoSuchMethodException {
        String propertyName = "PropertyFour";
        Class<MockJavaBean> beanClass = MockJavaBean.class;
        Method readMethod = beanClass.getMethod(("get" + propertyName), ((Class[]) (null)));
        Method writeMethod = beanClass.getMethod(("set" + propertyName), new Class[]{ String[].class });
        Method indexedReadMethod = beanClass.getMethod(("get" + propertyName), new Class[]{ Integer.TYPE });
        IndexedPropertyDescriptor ipd = new IndexedPropertyDescriptor(propertyName, readMethod, writeMethod, indexedReadMethod, null);
        TestCase.assertNull(ipd.getIndexedWriteMethod());
        try {
            ipd.setIndexedWriteMethod(indexedReadMethod);
            TestCase.fail("Should throw IntrospectionException.");
        } catch (IntrospectionException e) {
        }
    }

    /* bad arg type */
    public void testSetIndexedWriteMethod_badargtype() throws IntrospectionException, NoSuchMethodException {
        String propertyName = "PropertyFour";
        Class<MockJavaBean> beanClass = MockJavaBean.class;
        Method readMethod = beanClass.getMethod(("get" + propertyName), ((Class[]) (null)));
        Method writeMethod = beanClass.getMethod(("set" + propertyName), new Class[]{ String[].class });
        Method indexedReadMethod = beanClass.getMethod(("get" + propertyName), new Class[]{ Integer.TYPE });
        IndexedPropertyDescriptor ipd = new IndexedPropertyDescriptor(propertyName, readMethod, writeMethod, indexedReadMethod, null);
        TestCase.assertNull(ipd.getIndexedWriteMethod());
        Method badArgType = beanClass.getMethod(("set" + propertyName), new Class[]{ Integer.TYPE, Integer.TYPE });
        try {
            ipd.setIndexedWriteMethod(badArgType);
            TestCase.fail("Should throw IntrospectionException");
        } catch (IntrospectionException e) {
        }
    }

    public void testSetIndexedWriteMethod_return() throws IntrospectionException, NoSuchMethodException {
        String propertyName = "PropertyFour";
        Class<MockJavaBean> beanClass = MockJavaBean.class;
        Method readMethod = beanClass.getMethod(("get" + propertyName), ((Class[]) (null)));
        Method writeMethod = beanClass.getMethod(("set" + propertyName), new Class[]{ String[].class });
        Method indexedReadMethod = beanClass.getMethod(("get" + propertyName), new Class[]{ Integer.TYPE });
        IndexedPropertyDescriptor ipd = new IndexedPropertyDescriptor(propertyName, readMethod, writeMethod, indexedReadMethod, null);
        TestCase.assertNull(ipd.getIndexedWriteMethod());
        Method badArgType = beanClass.getMethod("setPropertyFourInvalid", new Class[]{ Integer.TYPE, String.class });
        ipd.setIndexedWriteMethod(badArgType);
        TestCase.assertEquals(String.class, ipd.getIndexedPropertyType());
        TestCase.assertEquals(String[].class, ipd.getPropertyType());
        TestCase.assertEquals(Integer.TYPE, ipd.getIndexedWriteMethod().getReturnType());
    }

    public void testSetIndexedWriteMethod_InvalidIndexType() throws IntrospectionException, NoSuchMethodException {
        String propertyName = "PropertyFour";
        Class<MockJavaBean> beanClass = MockJavaBean.class;
        Method readMethod = beanClass.getMethod(("get" + propertyName), ((Class[]) (null)));
        Method writeMethod = beanClass.getMethod(("set" + propertyName), new Class[]{ String[].class });
        Method indexedReadMethod = beanClass.getMethod(("get" + propertyName), new Class[]{ Integer.TYPE });
        IndexedPropertyDescriptor ipd = new IndexedPropertyDescriptor(propertyName, readMethod, writeMethod, indexedReadMethod, null);
        TestCase.assertNull(ipd.getIndexedWriteMethod());
        Method badArgType = beanClass.getMethod("setPropertyFourInvalid2", new Class[]{ String.class, String.class });
        try {
            ipd.setIndexedWriteMethod(badArgType);
            TestCase.fail("Should throw IntrospectionException");
        } catch (IntrospectionException e) {
        }
        ipd = new IndexedPropertyDescriptor("data", IndexedPropertyDescriptorTest.NormalBean.class);
        ipd.setIndexedReadMethod(null);
        try {
            ipd.setIndexedWriteMethod(IndexedPropertyDescriptorTest.NormalBean.class.getMethod("setData", Integer.TYPE, Integer.TYPE));
            TestCase.fail("should throw IntrospectionException");
        } catch (IntrospectionException e) {
            // expected
        }
    }

    public void testSetIndexedMethodNullNull() throws Exception {
        try {
            IndexedPropertyDescriptor i = new IndexedPropertyDescriptor("a", IndexedPropertyDescriptorTest.NormalBean.class, "getData", "setData", null, "setData");
            i.setIndexedWriteMethod(null);
            TestCase.fail("should throw IntrospectionException.");
        } catch (IntrospectionException e) {
            // expected
        }
        try {
            IndexedPropertyDescriptor i = new IndexedPropertyDescriptor("a", IndexedPropertyDescriptorTest.NormalBean.class, "getData", "setData", "getData", null);
            i.setIndexedReadMethod(null);
            TestCase.fail("should throw IntrospectionException.");
        } catch (IntrospectionException e) {
            // expected
        }
    }

    public void testSetIndexedReadMethodFollowANullValue() throws Exception {
        try {
            IndexedPropertyDescriptor i = new IndexedPropertyDescriptor("a", IndexedPropertyDescriptorTest.DummyBean.class, "readMethod", "writeMethod", null, "indexedReadMethod");
            Method irm = IndexedPropertyDescriptorTest.DummyBean.class.getDeclaredMethod("indexedReadMethod", Integer.TYPE);
            i.setIndexedReadMethod(irm);
            TestCase.fail("should throw IntrospectionException.");
        } catch (IntrospectionException e) {
            // expected
        }
    }

    static class DummyBean {
        public int[] readMehtod() {
            return null;
        }

        public void writeMethod(int[] a) {
        }

        public double indexedReadMethod(int i) {
            return 0;
        }

        public void indexedWriteMethod(int i, int j) {
        }
    }

    class NotJavaBean {
        private String[] propertyOne;

        /**
         *
         *
         * @return Returns the propertyOne.
         */
        public String[] getPropertyOne() {
            return propertyOne;
        }

        /**
         *
         *
         * @param propertyOne
         * 		The propertyOne to set.
         */
        public void setPropertyOne(String[] propertyOne) {
            this.propertyOne = propertyOne;
        }

        public String getPropertyOne(int i) {
            return getPropertyOne()[i];
        }

        public void setPropertyOne(int i, String value) {
            this.propertyOne[i] = value;
        }
    }

    // Regression Test
    class InCompatibleGetterSetterBean {
        private Object[] data = new Object[10];

        public void setData(Object[] data) {
            this.data = data;
        }

        public Object[] getDate() {
            return data;
        }

        public void setData(int index, Object o) {
            this.data[index] = o;
        }
    }

    public void testInCompatibleGetterSetterBean() {
        try {
            new IndexedPropertyDescriptor("data", IndexedPropertyDescriptorTest.InCompatibleGetterSetterBean.class);
            TestCase.fail("should throw IntrospectionException");
        } catch (IntrospectionException e) {
            // expected
        }
    }

    class NormalBean {
        private Object[] data = new Object[10];

        public Object[] getData() {
            return data;
        }

        public void setData(Object[] data) {
            this.data = data;
        }

        public void setData(int index, Object o) {
            data[index] = o;
        }

        public void setData(int index, int value) {
            // do nothing
        }

        public Object getData(int index) {
            return data[index];
        }
    }

    public void testEquals_superClass() throws Exception {
        PropertyDescriptor propertyDescriptor = new PropertyDescriptor("data", IndexedPropertyDescriptorTest.NormalBean.class);
        IndexedPropertyDescriptor indexedPropertyDescriptor = new IndexedPropertyDescriptor("data", IndexedPropertyDescriptorTest.NormalBean.class);
        TestCase.assertFalse(indexedPropertyDescriptor.equals(propertyDescriptor));
        TestCase.assertTrue(propertyDescriptor.equals(indexedPropertyDescriptor));
    }

    public void testHashCode() throws Exception {
        String propertyName = "PropertyFour";
        Class<MockJavaBean> beanClass = MockJavaBean.class;
        Method readMethod = beanClass.getMethod(("get" + propertyName), ((Class[]) (null)));
        Method writeMethod = beanClass.getMethod(("set" + propertyName), new Class[]{ String[].class });
        Method indexedReadMethod = beanClass.getMethod(("get" + propertyName), new Class[]{ Integer.TYPE });
        Method indexedWriteMethod = beanClass.getMethod(("set" + propertyName), new Class[]{ Integer.TYPE, String.class });
        IndexedPropertyDescriptor ipd = new IndexedPropertyDescriptor(propertyName, readMethod, writeMethod, indexedReadMethod, indexedWriteMethod);
        IndexedPropertyDescriptor ipd2 = new IndexedPropertyDescriptor(propertyName, beanClass);
        TestCase.assertEquals(ipd, ipd2);
        TestCase.assertEquals(ipd.hashCode(), ipd2.hashCode());
    }

    public void testIncompatibleGetterAndIndexedGetterBean() {
        try {
            new IndexedPropertyDescriptor("data", IndexedPropertyDescriptorTest.IncompatibleGetterAndIndexedGetterBean.class);
            TestCase.fail("should throw IntrospectionException");
        } catch (IntrospectionException e) {
            // expected
        }
    }

    private class IncompatibleGetterAndIndexedGetterBean {
        private int[] data;

        public int getData() {
            return data[0];
        }

        public int getData(int index) {
            return data[index];
        }

        public void setData(int index, int data) {
            this.data[index] = data;
        }
    }
}

