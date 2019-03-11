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


import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Locale;
import junit.framework.TestCase;
import org.apache.harmony.beans.tests.support.mock.MockJavaBean;


/**
 * Unit test for PropertyDescriptor.
 */
public class PropertyDescriptorTest extends TestCase {
    public void testEquals() throws IntrospectionException, NoSuchMethodException, SecurityException {
        String propertyName = "PropertyOne";
        Class<MockJavaBean> beanClass = MockJavaBean.class;
        PropertyDescriptor pd = new PropertyDescriptor(propertyName, beanClass);
        Method readMethod = beanClass.getMethod(("get" + propertyName), ((Class[]) (null)));
        Method writeMethod = beanClass.getMethod(("set" + propertyName), new Class[]{ String.class });
        PropertyDescriptor pd2 = new PropertyDescriptor(propertyName, readMethod, writeMethod);
        pd.setName("different name");
        TestCase.assertTrue(pd.equals(pd2));
        TestCase.assertTrue(pd.equals(pd));
        TestCase.assertTrue(pd2.equals(pd));
        TestCase.assertFalse(pd.equals(null));
    }

    // Regression test for H-1763
    public void testEqualsRegression1763() throws IntrospectionException {
        String propertyName = "PropertyOne";
        Class<MockJavaBean> beanClass = MockJavaBean.class;
        PropertyDescriptor pd = new PropertyDescriptor(propertyName, beanClass);
        try {
            pd.equals(propertyName);
        } catch (ClassCastException e) {
            TestCase.fail("Equals throws ClassCastException");
        }
    }

    public void testEquals_ReadMethod() throws IntrospectionException, NoSuchMethodException, SecurityException {
        String propertyName = "PropertyOne";
        Class<MockJavaBean> beanClass = MockJavaBean.class;
        Method readMethod = beanClass.getMethod(("get" + propertyName), ((Class[]) (null)));
        PropertyDescriptor pd = new PropertyDescriptor(propertyName, readMethod, null);
        String propertyName2 = "PropertyThree";
        Method readMethod2 = beanClass.getMethod(("get" + propertyName2), ((Class[]) (null)));
        PropertyDescriptor pd2 = new PropertyDescriptor(propertyName2, readMethod2, null);
        TestCase.assertFalse(pd.equals(pd2));
    }

    public void testEquals_ReadMethod_Null() throws IntrospectionException, NoSuchMethodException, SecurityException {
        String propertyName = "PropertyOne";
        Class<MockJavaBean> beanClass = MockJavaBean.class;
        Method readMethod = null;
        PropertyDescriptor pd = new PropertyDescriptor(propertyName, readMethod, null);
        String propertyName2 = "PropertyThree";
        Method readMethod2 = beanClass.getMethod(("get" + propertyName2), ((Class[]) (null)));
        PropertyDescriptor pd2 = new PropertyDescriptor(propertyName2, readMethod2, null);
        TestCase.assertFalse(pd.equals(pd2));
    }

    public void testEquals_ReadMethod_Null_Null() throws IntrospectionException, NoSuchMethodException, SecurityException {
        String propertyName = "PropertyOne";
        Method readMethod = null;
        PropertyDescriptor pd = new PropertyDescriptor(propertyName, readMethod, null);
        String propertyName2 = "PropertyThree";
        Method readMethod2 = null;
        PropertyDescriptor pd2 = new PropertyDescriptor(propertyName2, readMethod2, null);
        TestCase.assertTrue(pd.equals(pd2));
    }

    public void testEquals_WriteMethod() throws IntrospectionException, NoSuchMethodException, SecurityException {
        String propertyName = "PropertyOne";
        Class<MockJavaBean> beanClass = MockJavaBean.class;
        Method writeMethod = beanClass.getMethod(("set" + propertyName), new Class[]{ String.class });
        PropertyDescriptor pd = new PropertyDescriptor(propertyName, null, writeMethod);
        String propertyName2 = "PropertyThree";
        Method writeMethod2 = beanClass.getMethod(("set" + propertyName2), new Class[]{ String.class });
        PropertyDescriptor pd2 = new PropertyDescriptor(propertyName2, null, writeMethod2);
        TestCase.assertFalse(pd.equals(pd2));
    }

    public void testEquals_WriteMethod_Null() throws IntrospectionException, NoSuchMethodException, SecurityException {
        String propertyName = "PropertyOne";
        Class<MockJavaBean> beanClass = MockJavaBean.class;
        Method writeMethod = null;
        PropertyDescriptor pd = new PropertyDescriptor(propertyName, null, writeMethod);
        String propertyName2 = "PropertyThree";
        Method writeMethod2 = beanClass.getMethod(("set" + propertyName2), new Class[]{ String.class });
        PropertyDescriptor pd2 = new PropertyDescriptor(propertyName2, null, writeMethod2);
        TestCase.assertFalse(pd.equals(pd2));
    }

    public void testEquals_Bound() throws IntrospectionException, NoSuchMethodException, SecurityException {
        String propertyName = "PropertyOne";
        PropertyDescriptor pd = new PropertyDescriptor(propertyName, null, null);
        String propertyName2 = "PropertyThree";
        PropertyDescriptor pd2 = new PropertyDescriptor(propertyName2, null, null);
        pd.setBound(true);
        TestCase.assertFalse(pd.equals(pd2));
    }

    public void testEquals_Contrained() throws IntrospectionException, NoSuchMethodException, SecurityException {
        String propertyName = "PropertyOne";
        PropertyDescriptor pd = new PropertyDescriptor(propertyName, null, null);
        String propertyName2 = "PropertyThree";
        PropertyDescriptor pd2 = new PropertyDescriptor(propertyName2, null, null);
        pd.setConstrained(true);
        TestCase.assertFalse(pd.equals(pd2));
    }

    public void testEquals_PropertyType() throws IntrospectionException {
        String propertyName = "PropertyOne";
        Class<MockJavaBean> beanClass = MockJavaBean.class;
        PropertyDescriptor pd = new PropertyDescriptor(propertyName, beanClass);
        Class<PropertyDescriptorTest.MockBeanPropertyDesc> beanClass2 = PropertyDescriptorTest.MockBeanPropertyDesc.class;
        PropertyDescriptor pd2 = new PropertyDescriptor(propertyName, beanClass2);
        TestCase.assertFalse(pd.equals(pd2));
    }

    /* Class under test for void PropertyDescriptor(String, Class) */
    public void testPropertyDescriptorStringClass() throws IntrospectionException {
        String propertyName = "propertyOne";
        Class<MockJavaBean> beanClass = MockJavaBean.class;
        PropertyDescriptor pd = new PropertyDescriptor(propertyName, beanClass);
        String capitalName = (propertyName.substring(0, 1).toUpperCase()) + (propertyName.substring(1));
        TestCase.assertEquals(String.class, pd.getPropertyType());
        TestCase.assertEquals(("get" + capitalName), pd.getReadMethod().getName());
        TestCase.assertEquals(("set" + capitalName), pd.getWriteMethod().getName());
        TestCase.assertFalse(pd.isBound());
        TestCase.assertFalse(pd.isConstrained());
        TestCase.assertEquals(propertyName, pd.getDisplayName());
        TestCase.assertEquals(propertyName, pd.getName());
        TestCase.assertEquals(propertyName, pd.getShortDescription());
        TestCase.assertNotNull(pd.attributeNames());
        TestCase.assertFalse(pd.isExpert());
        TestCase.assertFalse(pd.isHidden());
        TestCase.assertFalse(pd.isPreferred());
        propertyName = "propertyWithoutGet";
        try {
            new PropertyDescriptor(propertyName, beanClass);
            TestCase.fail("Should throw IntrospectionException");
        } catch (IntrospectionException e) {
        }
        try {
            new PropertyDescriptor(propertyName, beanClass, "getPropertyWithoutGet", "setPropertyWithoutGet");
            TestCase.fail("Should throw IntrospectionException");
        } catch (IntrospectionException e) {
        }
        propertyName = "propertyWithoutSet";
        beanClass = MockJavaBean.class;
        try {
            new PropertyDescriptor(propertyName, beanClass);
            TestCase.fail("Should throw IntrospectionException");
        } catch (IntrospectionException e) {
        }
        propertyName = "propertyWithDifferentGetSet";
        try {
            new PropertyDescriptor(propertyName, beanClass);
            TestCase.fail("Should throw IntrospectionException");
        } catch (IntrospectionException e) {
        }
        propertyName = "propertyWithInvalidGet";
        new PropertyDescriptor(propertyName, beanClass);
        propertyName = "propertyWithoutPublicGet";
        try {
            new PropertyDescriptor(propertyName, beanClass);
            TestCase.fail("Should throw IntrospectionException");
        } catch (IntrospectionException e) {
        }
        propertyName = "propertyWithGet1Param";
        try {
            new PropertyDescriptor(propertyName, beanClass);
            TestCase.fail("Should throw IntrospectionException");
        } catch (IntrospectionException e) {
        }
        propertyName = "propertyWithIs1Param";
        PropertyDescriptor pd2 = new PropertyDescriptor(propertyName, beanClass);
        TestCase.assertEquals("getPropertyWithIs1Param", pd2.getReadMethod().getName());
        propertyName = "propertyWithSet2Param";
        try {
            new PropertyDescriptor(propertyName, beanClass);
            TestCase.fail("Should throw IntrospectionException");
        } catch (IntrospectionException e) {
        }
        propertyName = "propertyWithIsGet";
        PropertyDescriptor pd3 = new PropertyDescriptor(propertyName, beanClass);
        TestCase.assertEquals("isPropertyWithIsGet", pd3.getReadMethod().getName());
        propertyName = "propertyWithVoidGet";
        try {
            new PropertyDescriptor(propertyName, beanClass);
            TestCase.fail("Should throw IntrospectionException");
        } catch (IntrospectionException e) {
        }
    }

    public void testPropertyDescriptorStringClass_PropertyNameCapital() throws IntrospectionException {
        String propertyName = "PropertyOne";
        Class<MockJavaBean> beanClass = MockJavaBean.class;
        PropertyDescriptor pd = new PropertyDescriptor(propertyName, beanClass);
        TestCase.assertEquals(propertyName, pd.getName());
    }

    public void testPropertyDescriptorStringClass_PropertyNameEmpty() throws IntrospectionException {
        String propertyName = "";
        Class<MockJavaBean> beanClass = MockJavaBean.class;
        try {
            new PropertyDescriptor(propertyName, beanClass);
            TestCase.fail("Should throw IntrospectionException.");
        } catch (IntrospectionException exception) {
        }
    }

    public void testPropertyDescriptorStringClass_PropertyNameNull() {
        Class<MockJavaBean> beanClass = MockJavaBean.class;
        try {
            new PropertyDescriptor(null, beanClass);
            TestCase.fail("Should throw IntrospectionException.");
        } catch (IntrospectionException exception) {
        }
    }

    public void testPropertyDescriptorStringClass_BeanClassNull() throws IntrospectionException {
        String propertyName = "propertyOne";
        try {
            new PropertyDescriptor(propertyName, null);
            TestCase.fail("Should throw IntrospectionException.");
        } catch (IntrospectionException exception) {
        }
    }

    public void testPropertyDescriptorStringClass_PropertyNameInvalid() {
        String propertyName = "not a property name";
        Class<MockJavaBean> beanClass = MockJavaBean.class;
        try {
            new PropertyDescriptor(propertyName, beanClass);
            TestCase.fail("Should throw IntrospectionException.");
        } catch (IntrospectionException exception) {
        }
    }

    public void testPropertyDescriptorStringClass_ProtectedGetter() {
        String propertyName = "protectedProp";
        Class<MockJavaBean> beanClass = MockJavaBean.class;
        try {
            new PropertyDescriptor(propertyName, beanClass);
            TestCase.fail("Should throw IntrospectionException.");
        } catch (IntrospectionException exception) {
        }
    }

    /* Class under test for void PropertyDescriptor(String, Class, String,
    String)
     */
    public void testPropertyDescriptorStringClassStringString() throws IntrospectionException {
        String propertyName = "PropertyTwo";
        Class<MockJavaBean> beanClass = MockJavaBean.class;
        PropertyDescriptor pd = new PropertyDescriptor(propertyName, beanClass, ("get" + propertyName), ("set" + propertyName));
        TestCase.assertEquals(Integer.class, pd.getPropertyType());
        TestCase.assertEquals(("get" + propertyName), pd.getReadMethod().getName());
        TestCase.assertEquals(("set" + propertyName), pd.getWriteMethod().getName());
        TestCase.assertFalse(pd.isBound());
        TestCase.assertFalse(pd.isConstrained());
        TestCase.assertEquals(propertyName, pd.getDisplayName());
        TestCase.assertEquals(propertyName, pd.getName());
        TestCase.assertEquals(propertyName, pd.getShortDescription());
        TestCase.assertNotNull(pd.attributeNames());
        TestCase.assertFalse(pd.isExpert());
        TestCase.assertFalse(pd.isHidden());
        TestCase.assertFalse(pd.isPreferred());
    }

    public void testPropertyDescriptorStringClassStringString_PropertyNameNull() {
        String propertyName = "PropertyTwo";
        Class<MockJavaBean> beanClass = MockJavaBean.class;
        try {
            new PropertyDescriptor(null, beanClass, ("get" + propertyName), ("set" + propertyName));
            TestCase.fail("Should throw IntrospectionException.");
        } catch (IntrospectionException e) {
        }
    }

    public void testPropertyDescriptorStringClassStringString_BeanClassNull() throws IntrospectionException {
        String propertyName = "PropertyTwo";
        Class<?> beanClass = null;
        try {
            new PropertyDescriptor(propertyName, beanClass, ("get" + propertyName), ("set" + propertyName));
            TestCase.fail("Should throw IntrospectionException.");
        } catch (IntrospectionException e) {
        }
    }

    public void testPropertyDescriptorStringClassStringString_ReadMethodNull() throws IntrospectionException {
        String propertyName = "PropertyTwo";
        Class<MockJavaBean> beanClass = MockJavaBean.class;
        PropertyDescriptor pd = new PropertyDescriptor(propertyName, beanClass, null, ("set" + propertyName));
        TestCase.assertEquals(Integer.class, pd.getPropertyType());
        TestCase.assertNull(pd.getReadMethod());
        TestCase.assertEquals(("set" + propertyName), pd.getWriteMethod().getName());
        TestCase.assertFalse(pd.isBound());
        TestCase.assertFalse(pd.isConstrained());
        TestCase.assertEquals(propertyName, pd.getDisplayName());
        TestCase.assertEquals(propertyName, pd.getName());
        TestCase.assertEquals(propertyName, pd.getShortDescription());
        TestCase.assertNotNull(pd.attributeNames());
        TestCase.assertFalse(pd.isExpert());
        TestCase.assertFalse(pd.isHidden());
        TestCase.assertFalse(pd.isPreferred());
        try {
            pd = new PropertyDescriptor(propertyName, beanClass, "", ("set" + propertyName));
            TestCase.fail("should throw exception");
        } catch (IntrospectionException e) {
        }
    }

    public void testPropertyDescriptorStringClassStringString_ReadMethodInvalid() throws IntrospectionException {
        String propertyName = "booleanProperty";
        Class<MockJavaBean> beanClass = MockJavaBean.class;
        PropertyDescriptor pd = new PropertyDescriptor(propertyName, beanClass, "getXX", ("set" + propertyName));
        TestCase.assertEquals("getBooleanProperty", pd.getReadMethod().getName());
        TestCase.assertEquals("setbooleanProperty", pd.getWriteMethod().getName());
    }

    public void testPropertyDescriptorStringClassStringString_WriteMethodNull() throws IntrospectionException {
        String propertyName = "PropertyTwo";
        Class<MockJavaBean> beanClass = MockJavaBean.class;
        PropertyDescriptor pd = new PropertyDescriptor(propertyName, beanClass, ("get" + propertyName), null);
        TestCase.assertEquals(Integer.class, pd.getPropertyType());
        TestCase.assertEquals(("get" + propertyName), pd.getReadMethod().getName());
        TestCase.assertNull(pd.getWriteMethod());
        TestCase.assertFalse(pd.isBound());
        TestCase.assertFalse(pd.isConstrained());
        TestCase.assertEquals(propertyName, pd.getDisplayName());
        TestCase.assertEquals(propertyName, pd.getName());
        TestCase.assertEquals(propertyName, pd.getShortDescription());
        TestCase.assertNotNull(pd.attributeNames());
        TestCase.assertFalse(pd.isExpert());
        TestCase.assertFalse(pd.isHidden());
        TestCase.assertFalse(pd.isPreferred());
    }

    public void testPropertyDescriptorStringClassStringString_WriteMethodEmpty() throws IntrospectionException {
        String propertyName = "PropertyTwo";
        Class<MockJavaBean> beanClass = MockJavaBean.class;
        try {
            new PropertyDescriptor(propertyName, beanClass, ("get" + propertyName), "");
            TestCase.fail("Should throw IntrospectionException.");
        } catch (IntrospectionException e) {
        }
    }

    public void testPropertyDescriptorStringClassStringString_WriteMethodInvalid() throws IntrospectionException {
        String propertyName = "PropertyTwo";
        Class<MockJavaBean> beanClass = MockJavaBean.class;
        try {
            new PropertyDescriptor(propertyName, beanClass, ("get" + propertyName), "setXXX");
            TestCase.fail("Should throw IntrospectionException.");
        } catch (IntrospectionException e) {
        }
    }

    /* Class under test for void PropertyDescriptor(String, Method, Method) */
    public void testPropertyDescriptorStringMethodMethod() throws IntrospectionException, NoSuchMethodException, SecurityException {
        String propertyName = "PropertyOne";
        Class<MockJavaBean> beanClass = MockJavaBean.class;
        Method readMethod = beanClass.getMethod(("get" + propertyName), ((Class[]) (null)));
        Method writeMethod = beanClass.getMethod(("set" + propertyName), new Class[]{ String.class });
        PropertyDescriptor pd = new PropertyDescriptor(propertyName, readMethod, writeMethod);
        TestCase.assertEquals(String.class, pd.getPropertyType());
        TestCase.assertEquals(("get" + propertyName), pd.getReadMethod().getName());
        TestCase.assertEquals(("set" + propertyName), pd.getWriteMethod().getName());
        TestCase.assertFalse(pd.isBound());
        TestCase.assertFalse(pd.isConstrained());
        TestCase.assertEquals(propertyName, pd.getDisplayName());
        TestCase.assertEquals(propertyName, pd.getName());
        TestCase.assertEquals(propertyName, pd.getShortDescription());
        TestCase.assertNotNull(pd.attributeNames());
        TestCase.assertFalse(pd.isExpert());
        TestCase.assertFalse(pd.isHidden());
        TestCase.assertFalse(pd.isPreferred());
    }

    public void testPropertyDescriptorStringMethodMethod_PropertyNameNull() throws IntrospectionException, NoSuchMethodException, SecurityException {
        String propertyName = "PropertyOne";
        Class<MockJavaBean> beanClass = MockJavaBean.class;
        Method readMethod = beanClass.getMethod(("get" + propertyName), ((Class[]) (null)));
        Method writeMethod = beanClass.getMethod(("set" + propertyName), new Class[]{ String.class });
        try {
            new PropertyDescriptor(null, readMethod, writeMethod);
            TestCase.fail("Should throw IntrospectionException.");
        } catch (IntrospectionException e) {
        }
    }

    public void testPropertyDescriptorStringMethodMethod_ReadMethodNull() throws IntrospectionException, NoSuchMethodException, SecurityException {
        String propertyName = "PropertyOne";
        Class<MockJavaBean> beanClass = MockJavaBean.class;
        Method readMethod = null;
        Method writeMethod = beanClass.getMethod(("set" + propertyName), new Class[]{ String.class });
        PropertyDescriptor pd = new PropertyDescriptor(propertyName, readMethod, writeMethod);
        TestCase.assertEquals(String.class, pd.getPropertyType());
        TestCase.assertNull(pd.getReadMethod());
        TestCase.assertEquals(("set" + propertyName), pd.getWriteMethod().getName());
        TestCase.assertFalse(pd.isBound());
        TestCase.assertFalse(pd.isConstrained());
        TestCase.assertEquals(propertyName, pd.getDisplayName());
        TestCase.assertEquals(propertyName, pd.getName());
        TestCase.assertEquals(propertyName, pd.getShortDescription());
        TestCase.assertNotNull(pd.attributeNames());
        TestCase.assertFalse(pd.isExpert());
        TestCase.assertFalse(pd.isHidden());
        TestCase.assertFalse(pd.isPreferred());
    }

    public void testPropertyDescriptorStringMethodMethod_ReadMethodInvalid() throws IntrospectionException, NoSuchMethodException, SecurityException {
        String propertyName = "PropertyOne";
        Class<MockJavaBean> beanClass = MockJavaBean.class;
        String anotherProp = "PropertyTwo";
        Method readMethod = beanClass.getMethod(("get" + anotherProp), ((Class[]) (null)));
        Method writeMethod = beanClass.getMethod(("set" + propertyName), new Class[]{ String.class });
        try {
            new PropertyDescriptor(propertyName, readMethod, writeMethod);
            TestCase.fail("Should throw IntrospectionException.");
        } catch (IntrospectionException e) {
        }
    }

    public void testPropertyDescriptorStringMethodMethod_WriteMethodNull() throws IntrospectionException, NoSuchMethodException, SecurityException {
        String propertyName = "PropertyOne";
        Class<MockJavaBean> beanClass = MockJavaBean.class;
        Method readMethod = beanClass.getMethod(("get" + propertyName), ((Class[]) (null)));
        Method writeMethod = null;
        PropertyDescriptor pd = new PropertyDescriptor(propertyName, readMethod, writeMethod);
        TestCase.assertEquals(String.class, pd.getPropertyType());
        TestCase.assertEquals(("get" + propertyName), pd.getReadMethod().getName());
        TestCase.assertNull(pd.getWriteMethod());
        TestCase.assertFalse(pd.isBound());
        TestCase.assertFalse(pd.isConstrained());
        TestCase.assertEquals(propertyName, pd.getDisplayName());
        TestCase.assertEquals(propertyName, pd.getName());
        TestCase.assertEquals(propertyName, pd.getShortDescription());
        TestCase.assertNotNull(pd.attributeNames());
        TestCase.assertFalse(pd.isExpert());
        TestCase.assertFalse(pd.isHidden());
        TestCase.assertFalse(pd.isPreferred());
    }

    public void testPropertyDescriptorStringMethodMethod_WriteMethodInvalid() throws IntrospectionException, NoSuchMethodException, SecurityException {
        String propertyName = "PropertyOne";
        Class<MockJavaBean> beanClass = MockJavaBean.class;
        String anotherProp = "PropertyTwo";
        Method readMethod = beanClass.getMethod(("get" + propertyName), ((Class[]) (null)));
        Method writeMethod = beanClass.getMethod(("set" + anotherProp), new Class[]{ Integer.class });
        try {
            new PropertyDescriptor(propertyName, readMethod, writeMethod);
            TestCase.fail("Should throw IntrospectionException.");
        } catch (IntrospectionException e) {
        }
    }

    /**
     *
     *
     * @throws Exception
     * 		
     */
    public void testPropertyDescriptorStringClassMethodMethod_SubClass() throws Exception {
        PropertyDescriptor pd = new PropertyDescriptor("prop1", PropertyDescriptorTest.SubMockJavaBean.class, null, "setPropertyOne");// $NON-NLS-1$ //$NON-NLS-2$

        TestCase.assertNull(pd.getReadMethod());
        TestCase.assertEquals("setPropertyOne", pd.getWriteMethod().getName());// $NON-NLS-1$

        pd = new PropertyDescriptor("prop1", PropertyDescriptorTest.SubMockJavaBean.class, "getPropertyOne", "setPropertyOne");// $NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

        TestCase.assertEquals("getPropertyOne", pd.getReadMethod().getName());// $NON-NLS-1$

        TestCase.assertEquals("setPropertyOne", pd.getWriteMethod().getName());// $NON-NLS-1$

        pd = new PropertyDescriptor("prop1", PropertyDescriptorTest.SubMockJavaBean.class, "getPropertyOne", null);// $NON-NLS-1$ //$NON-NLS-2$

        TestCase.assertEquals("getPropertyOne", pd.getReadMethod().getName());// $NON-NLS-1$

        TestCase.assertNull(pd.getWriteMethod());
    }

    public void testSetReadMethod() throws IntrospectionException, NoSuchMethodException, SecurityException {
        Class<MockJavaBean> beanClass = MockJavaBean.class;
        String propertyName = "PropertyOne";
        Method readMethod = beanClass.getMethod(("get" + propertyName), ((Class[]) (null)));
        PropertyDescriptor pd = new PropertyDescriptor(propertyName, null, null);
        TestCase.assertNull(pd.getReadMethod());
        pd.setReadMethod(readMethod);
        TestCase.assertSame(readMethod, pd.getReadMethod());
    }

    public void testSetReadMethod_Null() throws IntrospectionException, NoSuchMethodException, SecurityException {
        Class<MockJavaBean> beanClass = MockJavaBean.class;
        String propertyName = "PropertyOne";
        Method readMethod = beanClass.getMethod(("get" + propertyName), ((Class[]) (null)));
        PropertyDescriptor pd = new PropertyDescriptor(propertyName, readMethod, null);
        TestCase.assertSame(readMethod, pd.getReadMethod());
        pd.setReadMethod(null);
        TestCase.assertNull(pd.getReadMethod());
    }

    /**
     * Read method is incompatible with property name getPropertyTwo vs.
     * PropertyOne (writeMethod=null)
     */
    public void testSetReadMethod_Invalid() throws IntrospectionException, NoSuchMethodException, SecurityException {
        Class<MockJavaBean> beanClass = MockJavaBean.class;
        String propertyName = "PropertyOne";
        Method readMethod = beanClass.getMethod(("get" + "PropertyTwo"), ((Class[]) (null)));
        PropertyDescriptor pd = new PropertyDescriptor(propertyName, null, null);
        TestCase.assertNull(pd.getReadMethod());
        pd.setReadMethod(readMethod);
        TestCase.assertSame(readMethod, pd.getReadMethod());
    }

    /**
     * String invalidGetMethod(String arg)
     */
    public void testSetReadMethod_Invalid_withArg() throws IntrospectionException, NoSuchMethodException, SecurityException {
        Class<MockJavaBean> beanClass = MockJavaBean.class;
        String propertyName = "PropertyOne";
        Method readMethod = beanClass.getMethod("invalidGetMethod", new Class[]{ String.class });
        PropertyDescriptor pd = new PropertyDescriptor(propertyName, null, null);
        TestCase.assertNull(pd.getReadMethod());
        try {
            pd.setReadMethod(readMethod);
            TestCase.fail("Should throw IntrospectionException.");
        } catch (IntrospectionException e) {
        }
    }

    /**
     * String invalidGetMethod(String arg)
     */
    public void testSetReadMethod_Invalid_returnVoid() throws IntrospectionException, NoSuchMethodException, SecurityException {
        Class<MockJavaBean> beanClass = MockJavaBean.class;
        String propertyName = "PropertyOne";
        Method readMethod = beanClass.getMethod("invalidGetMethod", ((Class[]) (null)));
        PropertyDescriptor pd = new PropertyDescriptor(propertyName, null, null);
        TestCase.assertNull(pd.getReadMethod());
        try {
            pd.setReadMethod(readMethod);
            TestCase.fail("Should throw IntrospectionException.");
        } catch (IntrospectionException e) {
        }
    }

    /**
     * Read method is incompatible with write method getPropertyOn vs.
     * setPropertyTow
     */
    public void testSetReadMethod_ReadWriteIncompatible() throws IntrospectionException, NoSuchMethodException, SecurityException {
        Class<MockJavaBean> beanClass = MockJavaBean.class;
        String propertyName = "PropertyOne";
        Method readMethod = beanClass.getMethod(("get" + "PropertyOne"), ((Class[]) (null)));
        Method writeMethod = beanClass.getMethod(("set" + "PropertyTwo"), new Class[]{ Integer.class });
        PropertyDescriptor pd = new PropertyDescriptor(propertyName, null, writeMethod);
        TestCase.assertNull(pd.getReadMethod());
        try {
            pd.setReadMethod(readMethod);
            TestCase.fail("Should throw IntrospectionException.");
        } catch (IntrospectionException e) {
        }
    }

    /**
     * normal input
     */
    public void testSetWriteMethod() throws IntrospectionException, NoSuchMethodException, SecurityException {
        Class<MockJavaBean> beanClass = MockJavaBean.class;
        String propertyName = "PropertyOne";
        Method writeMethod = beanClass.getMethod(("set" + propertyName), new Class[]{ String.class });
        PropertyDescriptor pd = new PropertyDescriptor(propertyName, null, null);
        TestCase.assertNull(pd.getWriteMethod());
        pd.setWriteMethod(writeMethod);
        TestCase.assertSame(writeMethod, pd.getWriteMethod());
    }

    /**
     * setWriteMethod(null)
     */
    public void testSetWriteMethod_Null() throws IntrospectionException, NoSuchMethodException, SecurityException {
        Class<MockJavaBean> beanClass = MockJavaBean.class;
        String propertyName = "PropertyOne";
        Method writeMethod = beanClass.getMethod(("set" + propertyName), new Class[]{ String.class });
        PropertyDescriptor pd = new PropertyDescriptor(propertyName, null, writeMethod);
        TestCase.assertSame(writeMethod, pd.getWriteMethod());
        pd.setWriteMethod(null);
        TestCase.assertNull(pd.getWriteMethod());
    }

    /**
     * write method is incompatible with property name (read method is null)
     */
    public void testSetWriteMethod_Invalid() throws IntrospectionException, NoSuchMethodException, SecurityException {
        Class<MockJavaBean> beanClass = MockJavaBean.class;
        String propertyName = "PropertyOne";
        Method writeMethod = beanClass.getMethod(("set" + "PropertyTwo"), new Class[]{ Integer.class });
        PropertyDescriptor pd = new PropertyDescriptor(propertyName, null, null);
        TestCase.assertNull(pd.getWriteMethod());
        pd.setWriteMethod(writeMethod);
        TestCase.assertSame(writeMethod, pd.getWriteMethod());
    }

    /**
     * write method without argument
     */
    public void testSetWriteMethod_Invalid_NoArgs() throws IntrospectionException, NoSuchMethodException, SecurityException {
        Class<MockJavaBean> beanClass = MockJavaBean.class;
        String propertyName = "PropertyOne";
        Method writeMethod = beanClass.getMethod(("get" + "PropertyTwo"), ((Class[]) (null)));
        PropertyDescriptor pd = new PropertyDescriptor(propertyName, null, null);
        TestCase.assertNull(pd.getWriteMethod());
        try {
            pd.setWriteMethod(writeMethod);
            TestCase.fail("Should throw IntrospectionException.");
        } catch (IntrospectionException e) {
        }
    }

    /**
     * write method is incompatible with read method
     */
    public void testSetWriteMethod_WriteReadIncompatible() throws IntrospectionException, NoSuchMethodException, SecurityException {
        Class<MockJavaBean> beanClass = MockJavaBean.class;
        String propertyName = "PropertyOne";
        Method readMethod = beanClass.getMethod(("get" + "PropertyTwo"), ((Class[]) (null)));
        Method writeMethod = beanClass.getMethod(("set" + propertyName), new Class[]{ String.class });
        PropertyDescriptor pd = new PropertyDescriptor(propertyName, readMethod, null);
        TestCase.assertNull(pd.getWriteMethod());
        try {
            pd.setWriteMethod(writeMethod);
            TestCase.fail("Should throw IntrospectionException.");
        } catch (IntrospectionException e) {
        }
    }

    public void testSetBound_true() throws IntrospectionException, NoSuchMethodException, SecurityException {
        Class<MockJavaBean> beanClass = MockJavaBean.class;
        String propertyName = "PropertyOne";
        Method readMethod = beanClass.getMethod(("get" + propertyName), ((Class[]) (null)));
        Method writeMethod = beanClass.getMethod(("set" + propertyName), new Class[]{ String.class });
        PropertyDescriptor pd = new PropertyDescriptor(propertyName, readMethod, writeMethod);
        pd.setBound(true);
        TestCase.assertTrue(pd.isBound());
        TestCase.assertFalse(pd.isConstrained());
        TestCase.assertEquals(propertyName, pd.getDisplayName());
        TestCase.assertEquals(propertyName, pd.getName());
        TestCase.assertEquals(propertyName, pd.getShortDescription());
        TestCase.assertNotNull(pd.attributeNames());
        TestCase.assertFalse(pd.isExpert());
        TestCase.assertFalse(pd.isHidden());
        TestCase.assertFalse(pd.isPreferred());
    }

    public void testSetBound_false() throws IntrospectionException, NoSuchMethodException, SecurityException {
        Class<MockJavaBean> beanClass = MockJavaBean.class;
        String propertyName = "PropertyOne";
        Method readMethod = beanClass.getMethod(("get" + propertyName), ((Class[]) (null)));
        Method writeMethod = beanClass.getMethod(("set" + propertyName), new Class[]{ String.class });
        PropertyDescriptor pd = new PropertyDescriptor(propertyName, readMethod, writeMethod);
        pd.setBound(false);
        TestCase.assertFalse(pd.isBound());
        TestCase.assertFalse(pd.isConstrained());
        TestCase.assertEquals(propertyName, pd.getDisplayName());
        TestCase.assertEquals(propertyName, pd.getName());
        TestCase.assertEquals(propertyName, pd.getShortDescription());
        TestCase.assertNotNull(pd.attributeNames());
        TestCase.assertFalse(pd.isExpert());
        TestCase.assertFalse(pd.isHidden());
        TestCase.assertFalse(pd.isPreferred());
    }

    public void testSetConstrained_true() throws IntrospectionException, NoSuchMethodException, SecurityException {
        Class<MockJavaBean> beanClass = MockJavaBean.class;
        String propertyName = "PropertyOne";
        Method readMethod = beanClass.getMethod(("get" + propertyName), ((Class[]) (null)));
        Method writeMethod = beanClass.getMethod(("set" + propertyName), new Class[]{ String.class });
        PropertyDescriptor pd = new PropertyDescriptor(propertyName, readMethod, writeMethod);
        pd.setConstrained(true);
        TestCase.assertTrue(pd.isConstrained());
        TestCase.assertFalse(pd.isBound());
        TestCase.assertEquals(propertyName, pd.getDisplayName());
        TestCase.assertEquals(propertyName, pd.getName());
        TestCase.assertEquals(propertyName, pd.getShortDescription());
        TestCase.assertNotNull(pd.attributeNames());
        TestCase.assertFalse(pd.isExpert());
        TestCase.assertFalse(pd.isHidden());
        TestCase.assertFalse(pd.isPreferred());
    }

    public void testSetConstrained_false() throws IntrospectionException, NoSuchMethodException, SecurityException {
        Class<MockJavaBean> beanClass = MockJavaBean.class;
        String propertyName = "PropertyOne";
        Method readMethod = beanClass.getMethod(("get" + propertyName), ((Class[]) (null)));
        Method writeMethod = beanClass.getMethod(("set" + propertyName), new Class[]{ String.class });
        PropertyDescriptor pd = new PropertyDescriptor(propertyName, readMethod, writeMethod);
        pd.setConstrained(false);
        TestCase.assertFalse(pd.isConstrained());
        TestCase.assertFalse(pd.isBound());
        TestCase.assertEquals(propertyName, pd.getDisplayName());
        TestCase.assertEquals(propertyName, pd.getName());
        TestCase.assertEquals(propertyName, pd.getShortDescription());
        TestCase.assertNotNull(pd.attributeNames());
        TestCase.assertFalse(pd.isExpert());
        TestCase.assertFalse(pd.isHidden());
        TestCase.assertFalse(pd.isPreferred());
    }

    public void testConstructor_1() throws IntrospectionException {
        new PropertyDescriptor("fox01", PropertyDescriptorTest.FakeFox01.class);
    }

    // Regression test for HARMONY-237
    public void testIntrospectionExceptions() {
        try {
            new PropertyDescriptor(null, null);
            TestCase.fail(("Constructor PropertyDescriptor(null,null) should " + "throw IntrospectionException"));
        } catch (IntrospectionException e) {
            TestCase.assertEquals("Target Bean class is null", e.getMessage());
        }
        try {
            new PropertyDescriptor(null, String.class);
            TestCase.fail(("Constructor PropertyDescriptor(null,String.class) should " + "throw IntrospectionException"));
        } catch (IntrospectionException e) {
            TestCase.assertEquals("bad property name", e.getMessage());
        }
        try {
            new PropertyDescriptor(null, null, null, null);
            TestCase.fail(("Constructor PropertyDescriptor(null,null,null,null) should " + "throw IntrospectionException"));
        } catch (IntrospectionException e) {
            TestCase.assertEquals("Target Bean class is null", e.getMessage());
        }
        try {
            new PropertyDescriptor(null, String.class, null, null);
            TestCase.fail(("Constructor " + ("PropertyDescriptor(null,String.class,null,null) should " + "throw IntrospectionException")));
        } catch (IntrospectionException e) {
            TestCase.assertEquals("bad property name", e.getMessage());
        }
        try {
            new PropertyDescriptor(null, null, null);
            TestCase.fail(("Constructor PropertyDescriptor(null,null,null) should " + "throw IntrospectionException"));
        } catch (IntrospectionException e) {
            TestCase.assertEquals("bad property name", e.getMessage());
        }
        try {
            new PropertyDescriptor("", null, null);
            TestCase.fail(("Constructor PropertyDescriptor(\"\",null,null) should " + "throw IntrospectionException"));
        } catch (IntrospectionException e) {
            TestCase.assertEquals("bad property name", e.getMessage());
        }
    }

    static class FakeFox01 {
        public String getFox01() {
            return null;
        }

        public void setFox01(String value) {
        }
    }

    class MockBeanPropertyDesc implements Serializable {
        /**
         * Comment for <code>serialVersionUID</code>
         */
        private static final long serialVersionUID = 1L;

        Integer propertyOne;

        /**
         *
         *
         * @return Returns the propertyOne.
         */
        public Integer getPropertyOne() {
            return propertyOne;
        }

        /**
         *
         *
         * @param propertyOne
         * 		The propertyOne to set.
         */
        public void setPropertyOne(Integer propertyOne) {
            this.propertyOne = propertyOne;
        }
    }

    // 
    class SubMockJavaBean extends MockJavaBean {
        /**
         * Comment for <code>serialVersionUID</code>
         */
        private static final long serialVersionUID = 7423254295680570566L;
    }

    // Regression Test
    private class MockBean {
        int a;

        public int getA() {
            return a;
        }

        public void setA(int a) {
            this.a = a;
        }
    }

    public void testHashCode() throws IntrospectionException, NoSuchMethodException, SecurityException {
        PropertyDescriptor pd1 = new PropertyDescriptor("a", PropertyDescriptorTest.MockBean.class);
        PropertyDescriptor pd2 = new PropertyDescriptor("a", PropertyDescriptorTest.MockBean.class);
        TestCase.assertEquals(pd1, pd2);
        TestCase.assertEquals(pd1.hashCode(), pd2.hashCode());
    }

    private class MockBeanForTR {
        int i;

        public int getI() {
            return i;
        }

        public void setI(int i) {
            this.i = i;
        }
    }

    public void testByLocale() throws IntrospectionException {
        Locale backup = Locale.getDefault();
        Locale.setDefault(new Locale("TR"));
        try {
            TestCase.assertNotNull(new PropertyDescriptor("i", PropertyDescriptorTest.MockBeanForTR.class));
        } finally {
            Locale.setDefault(backup);
        }
    }
}

