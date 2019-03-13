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


import java.beans.MethodDescriptor;
import java.beans.ParameterDescriptor;
import java.lang.reflect.Method;
import junit.framework.TestCase;
import org.apache.harmony.beans.tests.support.mock.MockJavaBean;


/**
 * Unit test for MethodDescriptor
 */
public class MethodDescriptorTest extends TestCase {
    /* Class under test for void MethodDescriptor(Method) */
    public void testMethodDescriptorMethod() throws NoSuchMethodException, SecurityException {
        String beanName = "MethodDescriptorTest.bean";
        MockJavaBean bean = new MockJavaBean(beanName);
        Method method = bean.getClass().getMethod("getBeanName", ((Class[]) (null)));
        MethodDescriptor md = new MethodDescriptor(method);
        TestCase.assertSame(method, md.getMethod());
        TestCase.assertNull(md.getParameterDescriptors());
        TestCase.assertEquals(method.getName(), md.getDisplayName());
        TestCase.assertEquals(method.getName(), md.getName());
        TestCase.assertEquals(method.getName(), md.getShortDescription());
        TestCase.assertNotNull(md.attributeNames());
        TestCase.assertFalse(md.isExpert());
        TestCase.assertFalse(md.isHidden());
        TestCase.assertFalse(md.isPreferred());
    }

    public void testMethodDescriptorMethod_Null() {
        Method method = null;
        try {
            new MethodDescriptor(method);
            TestCase.fail("Should throw NullPointerException.");
        } catch (NullPointerException e) {
        }
    }

    /* Class under test for void MethodDescriptor(Method, ParameterDescriptor[]) */
    public void testMethodDescriptorMethodParameterDescriptorArray() throws NoSuchMethodException, SecurityException {
        String beanName = "MethodDescriptorTest.bean";
        MockJavaBean bean = new MockJavaBean(beanName);
        Method method = bean.getClass().getMethod("setPropertyOne", new Class[]{ String.class });
        ParameterDescriptor[] pds = new ParameterDescriptor[1];
        pds[0] = new ParameterDescriptor();
        pds[0].setValue(method.getName(), method.getReturnType());
        MethodDescriptor md = new MethodDescriptor(method, pds);
        TestCase.assertSame(method, md.getMethod());
        TestCase.assertSame(pds, md.getParameterDescriptors());
        TestCase.assertEquals(pds[0].getValue(method.getName()), md.getParameterDescriptors()[0].getValue(method.getName()));
        TestCase.assertEquals(method.getName(), md.getDisplayName());
        TestCase.assertEquals(method.getName(), md.getName());
        TestCase.assertEquals(method.getName(), md.getShortDescription());
        TestCase.assertNotNull(md.attributeNames());
        TestCase.assertFalse(md.isExpert());
        TestCase.assertFalse(md.isHidden());
        TestCase.assertFalse(md.isPreferred());
    }

    public void testMethodDescriptorMethodParameterDescriptorArray_MethodNull() {
        Method method = null;
        ParameterDescriptor[] pds = new ParameterDescriptor[1];
        pds[0] = new ParameterDescriptor();
        try {
            new MethodDescriptor(method, pds);
            TestCase.fail("Should throw NullPointerException.");
        } catch (NullPointerException e) {
        }
    }

    public void testMethodDescriptorMethodParameterDescriptorArray_PDNull() throws NoSuchMethodException, SecurityException {
        String beanName = "MethodDescriptorTest.bean";
        MockJavaBean bean = new MockJavaBean(beanName);
        Method method = bean.getClass().getMethod("setPropertyOne", new Class[]{ String.class });
        MethodDescriptor md = new MethodDescriptor(method, null);
        TestCase.assertSame(method, md.getMethod());
        TestCase.assertNull(md.getParameterDescriptors());
        TestCase.assertEquals(method.getName(), md.getDisplayName());
        TestCase.assertEquals(method.getName(), md.getName());
        TestCase.assertEquals(method.getName(), md.getShortDescription());
        TestCase.assertNotNull(md.attributeNames());
        TestCase.assertFalse(md.isExpert());
        TestCase.assertFalse(md.isHidden());
        TestCase.assertFalse(md.isPreferred());
    }

    /**
     *
     *
     * @unknown java.beans.MethodDescriptor#MethodDescriptor(
    java.lang.reflect.Method)
     */
    public void test_Ctor1_NullPointerException() {
        try {
            // Regression for HARMONY-226
            new MethodDescriptor(null);
            TestCase.fail("No expected NullPointerException");
        } catch (NullPointerException e) {
        }
    }

    /**
     *
     *
     * @unknown java.beans.MethodDescriptor#MethodDescriptor(
    java.lang.reflect.Method, java.beans.ParameterDescriptor[])
     */
    public void test_Ctor2_NullPointerException() {
        try {
            // Regression for HARMONY-226
            new MethodDescriptor(null, new ParameterDescriptor[0]);
            TestCase.fail("No expected NullPointerException");
        } catch (NullPointerException e) {
        }
    }
}

