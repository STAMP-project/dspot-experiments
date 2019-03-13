/**
 * Copyright 2002-2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.beans;


import java.beans.IndexedPropertyDescriptor;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Chris Beams
 * @see ExtendedBeanInfoTests
 */
public class SimplePropertyDescriptorTests {
    @Test
    public void toStringOutput() throws IntrospectionException, NoSuchMethodException, SecurityException {
        {
            Object pd = new ExtendedBeanInfo.SimplePropertyDescriptor("foo", null, null);
            Assert.assertThat(pd.toString(), CoreMatchers.containsString("PropertyDescriptor[name=foo, propertyType=null, readMethod=null"));
        }
        {
            class C {
                @SuppressWarnings("unused")
                public Object setFoo(String foo) {
                    return null;
                }
            }
            Method m = C.class.getMethod("setFoo", String.class);
            Object pd = new ExtendedBeanInfo.SimplePropertyDescriptor("foo", null, m);
            Assert.assertThat(pd.toString(), CoreMatchers.allOf(CoreMatchers.containsString("PropertyDescriptor[name=foo"), CoreMatchers.containsString("propertyType=class java.lang.String"), CoreMatchers.containsString("readMethod=null, writeMethod=public java.lang.Object")));
        }
        {
            Object pd = new ExtendedBeanInfo.SimpleIndexedPropertyDescriptor("foo", null, null, null, null);
            Assert.assertThat(pd.toString(), CoreMatchers.containsString("PropertyDescriptor[name=foo, propertyType=null, indexedPropertyType=null"));
        }
        {
            class C {
                @SuppressWarnings("unused")
                public Object setFoo(int i, String foo) {
                    return null;
                }
            }
            Method m = C.class.getMethod("setFoo", int.class, String.class);
            Object pd = new ExtendedBeanInfo.SimpleIndexedPropertyDescriptor("foo", null, null, null, m);
            Assert.assertThat(pd.toString(), CoreMatchers.allOf(CoreMatchers.containsString("PropertyDescriptor[name=foo, propertyType=null"), CoreMatchers.containsString("indexedPropertyType=class java.lang.String"), CoreMatchers.containsString("indexedWriteMethod=public java.lang.Object")));
        }
    }

    @Test
    public void nonIndexedEquality() throws IntrospectionException, NoSuchMethodException, SecurityException {
        Object pd1 = new ExtendedBeanInfo.SimplePropertyDescriptor("foo", null, null);
        Assert.assertThat(pd1, CoreMatchers.equalTo(pd1));
        Object pd2 = new ExtendedBeanInfo.SimplePropertyDescriptor("foo", null, null);
        Assert.assertThat(pd1, CoreMatchers.equalTo(pd2));
        Assert.assertThat(pd2, CoreMatchers.equalTo(pd1));
        @SuppressWarnings("unused")
        class C {
            public Object setFoo(String foo) {
                return null;
            }

            public String getFoo() {
                return null;
            }
        }
        Method wm1 = C.class.getMethod("setFoo", String.class);
        Object pd3 = new ExtendedBeanInfo.SimplePropertyDescriptor("foo", null, wm1);
        Assert.assertThat(pd1, CoreMatchers.not(CoreMatchers.equalTo(pd3)));
        Assert.assertThat(pd3, CoreMatchers.not(CoreMatchers.equalTo(pd1)));
        Method rm1 = C.class.getMethod("getFoo");
        Object pd4 = new ExtendedBeanInfo.SimplePropertyDescriptor("foo", rm1, null);
        Assert.assertThat(pd1, CoreMatchers.not(CoreMatchers.equalTo(pd4)));
        Assert.assertThat(pd4, CoreMatchers.not(CoreMatchers.equalTo(pd1)));
        Object pd5 = new PropertyDescriptor("foo", null, null);
        Assert.assertThat(pd1, CoreMatchers.equalTo(pd5));
        Assert.assertThat(pd5, CoreMatchers.equalTo(pd1));
        Object pd6 = "not a PD";
        Assert.assertThat(pd1, CoreMatchers.not(CoreMatchers.equalTo(pd6)));
        Assert.assertThat(pd6, CoreMatchers.not(CoreMatchers.equalTo(pd1)));
        Object pd7 = null;
        Assert.assertThat(pd1, CoreMatchers.not(CoreMatchers.equalTo(pd7)));
        Assert.assertThat(pd7, CoreMatchers.not(CoreMatchers.equalTo(pd1)));
    }

    @Test
    public void indexedEquality() throws IntrospectionException, NoSuchMethodException, SecurityException {
        Object pd1 = new ExtendedBeanInfo.SimpleIndexedPropertyDescriptor("foo", null, null, null, null);
        Assert.assertThat(pd1, CoreMatchers.equalTo(pd1));
        Object pd2 = new ExtendedBeanInfo.SimpleIndexedPropertyDescriptor("foo", null, null, null, null);
        Assert.assertThat(pd1, CoreMatchers.equalTo(pd2));
        Assert.assertThat(pd2, CoreMatchers.equalTo(pd1));
        @SuppressWarnings("unused")
        class C {
            public Object setFoo(int i, String foo) {
                return null;
            }

            public String getFoo(int i) {
                return null;
            }
        }
        Method wm1 = C.class.getMethod("setFoo", int.class, String.class);
        Object pd3 = new ExtendedBeanInfo.SimpleIndexedPropertyDescriptor("foo", null, null, null, wm1);
        Assert.assertThat(pd1, CoreMatchers.not(CoreMatchers.equalTo(pd3)));
        Assert.assertThat(pd3, CoreMatchers.not(CoreMatchers.equalTo(pd1)));
        Method rm1 = C.class.getMethod("getFoo", int.class);
        Object pd4 = new ExtendedBeanInfo.SimpleIndexedPropertyDescriptor("foo", null, null, rm1, null);
        Assert.assertThat(pd1, CoreMatchers.not(CoreMatchers.equalTo(pd4)));
        Assert.assertThat(pd4, CoreMatchers.not(CoreMatchers.equalTo(pd1)));
        Object pd5 = new IndexedPropertyDescriptor("foo", null, null, null, null);
        Assert.assertThat(pd1, CoreMatchers.equalTo(pd5));
        Assert.assertThat(pd5, CoreMatchers.equalTo(pd1));
        Object pd6 = "not a PD";
        Assert.assertThat(pd1, CoreMatchers.not(CoreMatchers.equalTo(pd6)));
        Assert.assertThat(pd6, CoreMatchers.not(CoreMatchers.equalTo(pd1)));
        Object pd7 = null;
        Assert.assertThat(pd1, CoreMatchers.not(CoreMatchers.equalTo(pd7)));
        Assert.assertThat(pd7, CoreMatchers.not(CoreMatchers.equalTo(pd1)));
    }
}

