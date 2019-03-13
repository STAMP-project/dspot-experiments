/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.support;


import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.apache.camel.ContextTestSupport;
import org.apache.camel.support.jndi.ExampleBean;
import org.apache.camel.util.AnotherExampleBean;
import org.apache.camel.util.OtherExampleBean;
import org.junit.Assert;
import org.junit.Test;


/**
 * Unit test for IntrospectionSupport
 */
public class IntrospectionSupportTest extends ContextTestSupport {
    @Test
    public void testOverloadSetterChooseStringSetter() throws Exception {
        IntrospectionSupportTest.MyOverloadedBean overloadedBean = new IntrospectionSupportTest.MyOverloadedBean();
        IntrospectionSupport.setProperty(context.getTypeConverter(), overloadedBean, "bean", "James");
        Assert.assertEquals("James", overloadedBean.getName());
    }

    @Test
    public void testOverloadSetterChooseBeanSetter() throws Exception {
        IntrospectionSupportTest.MyOverloadedBean overloadedBean = new IntrospectionSupportTest.MyOverloadedBean();
        ExampleBean bean = new ExampleBean();
        bean.setName("Claus");
        IntrospectionSupport.setProperty(context.getTypeConverter(), overloadedBean, "bean", bean);
        Assert.assertEquals("Claus", overloadedBean.getName());
    }

    @Test
    public void testOverloadSetterChooseUsingTypeConverter() throws Exception {
        IntrospectionSupportTest.MyOverloadedBean overloadedBean = new IntrospectionSupportTest.MyOverloadedBean();
        Object value = "Willem".getBytes();
        // should use byte[] -> String type converter and call the setBean(String) setter method
        IntrospectionSupport.setProperty(context.getTypeConverter(), overloadedBean, "bean", value);
        Assert.assertEquals("Willem", overloadedBean.getName());
    }

    @Test
    public void testPassword() throws Exception {
        IntrospectionSupportTest.MyPasswordBean passwordBean = new IntrospectionSupportTest.MyPasswordBean();
        IntrospectionSupport.setProperty(context.getTypeConverter(), passwordBean, "oldPassword", "Donald");
        IntrospectionSupport.setProperty(context.getTypeConverter(), passwordBean, "newPassword", "Duck");
        Assert.assertEquals("Donald", passwordBean.getOldPassword());
        Assert.assertEquals("Duck", passwordBean.getNewPassword());
    }

    public class MyPasswordBean {
        private String oldPassword;

        private String newPassword;

        public String getOldPassword() {
            return oldPassword;
        }

        public void setOldPassword(String oldPassword) {
            this.oldPassword = oldPassword;
        }

        public String getNewPassword() {
            return newPassword;
        }

        public void setNewPassword(String newPassword) {
            this.newPassword = newPassword;
        }
    }

    public class MyOverloadedBean {
        private ExampleBean bean;

        public void setBean(ExampleBean bean) {
            this.bean = bean;
        }

        public void setBean(String name) {
            bean = new ExampleBean();
            bean.setName(name);
        }

        public String getName() {
            return bean.getName();
        }
    }

    public class MyBuilderBean {
        private String name;

        public String getName() {
            return name;
        }

        public IntrospectionSupportTest.MyBuilderBean setName(String name) {
            this.name = name;
            return this;
        }
    }

    public class MyOtherBuilderBean extends IntrospectionSupportTest.MyBuilderBean {}

    public class MyOtherOtherBuilderBean extends IntrospectionSupportTest.MyOtherBuilderBean {
        public IntrospectionSupportTest.MyOtherOtherBuilderBean setName(String name) {
            super.setName(name);
            return this;
        }
    }

    @Test
    public void testIsSetterBuilderPatternSupport() throws Exception {
        Method setter = IntrospectionSupportTest.MyBuilderBean.class.getMethod("setName", String.class);
        Method setter2 = IntrospectionSupportTest.MyOtherBuilderBean.class.getMethod("setName", String.class);
        Method setter3 = IntrospectionSupportTest.MyOtherOtherBuilderBean.class.getMethod("setName", String.class);
        Assert.assertFalse(IntrospectionSupport.isSetter(setter, false));
        Assert.assertTrue(IntrospectionSupport.isSetter(setter, true));
        Assert.assertFalse(IntrospectionSupport.isSetter(setter2, false));
        Assert.assertTrue(IntrospectionSupport.isSetter(setter2, true));
        Assert.assertFalse(IntrospectionSupport.isSetter(setter3, false));
        Assert.assertTrue(IntrospectionSupport.isSetter(setter3, true));
    }

    @Test
    public void testHasProperties() throws Exception {
        Map<String, Object> empty = Collections.emptyMap();
        Assert.assertFalse(IntrospectionSupport.hasProperties(empty, null));
        Assert.assertFalse(IntrospectionSupport.hasProperties(empty, ""));
        Assert.assertFalse(IntrospectionSupport.hasProperties(empty, "foo."));
        Map<String, Object> param = new HashMap<>();
        Assert.assertFalse(IntrospectionSupport.hasProperties(param, null));
        Assert.assertFalse(IntrospectionSupport.hasProperties(param, ""));
        Assert.assertFalse(IntrospectionSupport.hasProperties(param, "foo."));
        param.put("name", "Claus");
        Assert.assertTrue(IntrospectionSupport.hasProperties(param, null));
        Assert.assertTrue(IntrospectionSupport.hasProperties(param, ""));
        Assert.assertFalse(IntrospectionSupport.hasProperties(param, "foo."));
        param.put("foo.name", "Hadrian");
        Assert.assertTrue(IntrospectionSupport.hasProperties(param, null));
        Assert.assertTrue(IntrospectionSupport.hasProperties(param, ""));
        Assert.assertTrue(IntrospectionSupport.hasProperties(param, "foo."));
    }

    @Test
    public void testGetProperties() throws Exception {
        ExampleBean bean = new ExampleBean();
        bean.setName("Claus");
        bean.setPrice(10.0);
        Map<String, Object> map = new HashMap<>();
        IntrospectionSupport.getProperties(bean, map, null);
        Assert.assertEquals(3, map.size());
        Assert.assertEquals("Claus", map.get("name"));
        String price = map.get("price").toString();
        Assert.assertTrue(price.startsWith("10"));
        Assert.assertEquals(null, map.get("id"));
    }

    @Test
    public void testAnotherGetProperties() throws Exception {
        AnotherExampleBean bean = new AnotherExampleBean();
        bean.setId("123");
        bean.setName("Claus");
        bean.setPrice(10.0);
        Date date = new Date(0);
        bean.setDate(date);
        bean.setGoldCustomer(true);
        bean.setLittle(true);
        Collection<?> children = new ArrayList<>();
        bean.setChildren(children);
        Map<String, Object> map = new HashMap<>();
        IntrospectionSupport.getProperties(bean, map, null);
        Assert.assertEquals(7, map.size());
        Assert.assertEquals("Claus", map.get("name"));
        String price = map.get("price").toString();
        Assert.assertTrue(price.startsWith("10"));
        Assert.assertSame(date, map.get("date"));
        Assert.assertSame(children, map.get("children"));
        Assert.assertEquals(Boolean.TRUE, map.get("goldCustomer"));
        Assert.assertEquals(Boolean.TRUE, map.get("little"));
        Assert.assertEquals("123", map.get("id"));
    }

    @Test
    public void testGetPropertiesOptionPrefix() throws Exception {
        ExampleBean bean = new ExampleBean();
        bean.setName("Claus");
        bean.setPrice(10.0);
        bean.setId("123");
        Map<String, Object> map = new HashMap<>();
        IntrospectionSupport.getProperties(bean, map, "bean.");
        Assert.assertEquals(3, map.size());
        Assert.assertEquals("Claus", map.get("bean.name"));
        String price = map.get("bean.price").toString();
        Assert.assertTrue(price.startsWith("10"));
        Assert.assertEquals("123", map.get("bean.id"));
    }

    @Test
    public void testGetPropertiesSkipNull() throws Exception {
        ExampleBean bean = new ExampleBean();
        bean.setName("Claus");
        bean.setPrice(10.0);
        bean.setId(null);
        Map<String, Object> map = new HashMap<>();
        IntrospectionSupport.getProperties(bean, map, null, false);
        Assert.assertEquals(2, map.size());
        Assert.assertEquals("Claus", map.get("name"));
        String price = map.get("price").toString();
        Assert.assertTrue(price.startsWith("10"));
    }

    @Test
    public void testGetProperty() throws Exception {
        ExampleBean bean = new ExampleBean();
        bean.setId("123");
        bean.setName("Claus");
        bean.setPrice(10.0);
        Object name = IntrospectionSupport.getProperty(bean, "name");
        Assert.assertEquals("Claus", name);
    }

    @Test
    public void testSetProperty() throws Exception {
        ExampleBean bean = new ExampleBean();
        bean.setId("123");
        bean.setName("Claus");
        bean.setPrice(10.0);
        IntrospectionSupport.setProperty(bean, "name", "James");
        Assert.assertEquals("James", bean.getName());
    }

    @Test
    public void testSetPropertyDash() throws Exception {
        AnotherExampleBean bean = new AnotherExampleBean();
        bean.setName("Claus");
        bean.setPrice(10.0);
        Date date = new Date(0);
        bean.setDate(date);
        bean.setGoldCustomer(true);
        bean.setLittle(true);
        IntrospectionSupport.setProperty(bean, "name", "James");
        IntrospectionSupport.setProperty(bean, "gold-customer", "false");
        Assert.assertEquals("James", bean.getName());
        Assert.assertEquals(false, bean.isGoldCustomer());
    }

    @Test
    public void testAnotherGetProperty() throws Exception {
        AnotherExampleBean bean = new AnotherExampleBean();
        bean.setName("Claus");
        bean.setPrice(10.0);
        Date date = new Date(0);
        bean.setDate(date);
        bean.setGoldCustomer(true);
        bean.setLittle(true);
        Collection<?> children = new ArrayList<>();
        bean.setChildren(children);
        Object name = IntrospectionSupport.getProperty(bean, "name");
        Assert.assertEquals("Claus", name);
        Assert.assertSame(date, IntrospectionSupport.getProperty(bean, "date"));
        Assert.assertSame(children, IntrospectionSupport.getProperty(bean, "children"));
        Assert.assertEquals(Boolean.TRUE, IntrospectionSupport.getProperty(bean, "goldCustomer"));
        Assert.assertEquals(Boolean.TRUE, IntrospectionSupport.getProperty(bean, "gold-customer"));
        Assert.assertEquals(Boolean.TRUE, IntrospectionSupport.getProperty(bean, "little"));
    }

    @Test
    public void testGetPropertyLocaleIndependent() throws Exception {
        Locale oldLocale = Locale.getDefault();
        Locale.setDefault(new Locale("tr", "TR"));
        try {
            ExampleBean bean = new ExampleBean();
            bean.setName("Claus");
            bean.setPrice(10.0);
            bean.setId("1");
            Object name = IntrospectionSupport.getProperty(bean, "name");
            Object id = IntrospectionSupport.getProperty(bean, "id");
            Object price = IntrospectionSupport.getProperty(bean, "price");
            Assert.assertEquals("Claus", name);
            Assert.assertEquals(10.0, price);
            Assert.assertEquals("1", id);
        } finally {
            Locale.setDefault(oldLocale);
        }
    }

    @Test
    public void testGetPropertyGetter() throws Exception {
        ExampleBean bean = new ExampleBean();
        bean.setName("Claus");
        bean.setPrice(10.0);
        Method name = IntrospectionSupport.getPropertyGetter(ExampleBean.class, "name");
        Assert.assertEquals("getName", name.getName());
        try {
            IntrospectionSupport.getPropertyGetter(ExampleBean.class, "xxx");
            Assert.fail("Should have thrown exception");
        } catch (NoSuchMethodException e) {
            Assert.assertEquals("org.apache.camel.support.jndi.ExampleBean.getXxx()", e.getMessage());
        }
    }

    @Test
    public void testGetPropertySetter() throws Exception {
        ExampleBean bean = new ExampleBean();
        bean.setName("Claus");
        bean.setPrice(10.0);
        Method name = IntrospectionSupport.getPropertySetter(ExampleBean.class, "name");
        Assert.assertEquals("setName", name.getName());
        try {
            IntrospectionSupport.getPropertySetter(ExampleBean.class, "xxx");
            Assert.fail("Should have thrown exception");
        } catch (NoSuchMethodException e) {
            Assert.assertEquals("org.apache.camel.support.jndi.ExampleBean.setXxx", e.getMessage());
        }
    }

    @Test
    public void testIsGetter() throws Exception {
        ExampleBean bean = new ExampleBean();
        Method name = bean.getClass().getMethod("getName", ((Class<?>[]) (null)));
        Assert.assertEquals(true, IntrospectionSupport.isGetter(name));
        Assert.assertEquals(false, IntrospectionSupport.isSetter(name));
        Method price = bean.getClass().getMethod("getPrice", ((Class<?>[]) (null)));
        Assert.assertEquals(true, IntrospectionSupport.isGetter(price));
        Assert.assertEquals(false, IntrospectionSupport.isSetter(price));
    }

    @Test
    public void testIsSetter() throws Exception {
        ExampleBean bean = new ExampleBean();
        Method name = bean.getClass().getMethod("setName", String.class);
        Assert.assertEquals(false, IntrospectionSupport.isGetter(name));
        Assert.assertEquals(true, IntrospectionSupport.isSetter(name));
        Method price = bean.getClass().getMethod("setPrice", double.class);
        Assert.assertEquals(false, IntrospectionSupport.isGetter(price));
        Assert.assertEquals(true, IntrospectionSupport.isSetter(price));
    }

    @Test
    public void testOtherIsGetter() throws Exception {
        OtherExampleBean bean = new OtherExampleBean();
        Method customerId = bean.getClass().getMethod("getCustomerId", ((Class<?>[]) (null)));
        Assert.assertEquals(true, IntrospectionSupport.isGetter(customerId));
        Assert.assertEquals(false, IntrospectionSupport.isSetter(customerId));
        Method goldCustomer = bean.getClass().getMethod("isGoldCustomer", ((Class<?>[]) (null)));
        Assert.assertEquals(true, IntrospectionSupport.isGetter(goldCustomer));
        Assert.assertEquals(false, IntrospectionSupport.isSetter(goldCustomer));
        Method silverCustomer = bean.getClass().getMethod("isSilverCustomer", ((Class<?>[]) (null)));
        Assert.assertEquals(true, IntrospectionSupport.isGetter(silverCustomer));
        Assert.assertEquals(false, IntrospectionSupport.isSetter(silverCustomer));
        Method company = bean.getClass().getMethod("getCompany", ((Class<?>[]) (null)));
        Assert.assertEquals(true, IntrospectionSupport.isGetter(company));
        Assert.assertEquals(false, IntrospectionSupport.isSetter(company));
        Method setupSomething = bean.getClass().getMethod("setupSomething", Object.class);
        Assert.assertEquals(false, IntrospectionSupport.isGetter(setupSomething));
        Assert.assertEquals(false, IntrospectionSupport.isSetter(setupSomething));
    }

    @Test
    public void testOtherIsSetter() throws Exception {
        OtherExampleBean bean = new OtherExampleBean();
        Method customerId = bean.getClass().getMethod("setCustomerId", int.class);
        Assert.assertEquals(false, IntrospectionSupport.isGetter(customerId));
        Assert.assertEquals(true, IntrospectionSupport.isSetter(customerId));
        Method goldCustomer = bean.getClass().getMethod("setGoldCustomer", boolean.class);
        Assert.assertEquals(false, IntrospectionSupport.isGetter(goldCustomer));
        Assert.assertEquals(true, IntrospectionSupport.isSetter(goldCustomer));
        Method silverCustomer = bean.getClass().getMethod("setSilverCustomer", Boolean.class);
        Assert.assertEquals(false, IntrospectionSupport.isGetter(silverCustomer));
        Assert.assertEquals(true, IntrospectionSupport.isSetter(silverCustomer));
        Method company = bean.getClass().getMethod("setCompany", String.class);
        Assert.assertEquals(false, IntrospectionSupport.isGetter(company));
        Assert.assertEquals(true, IntrospectionSupport.isSetter(company));
        Method setupSomething = bean.getClass().getMethod("setupSomething", Object.class);
        Assert.assertEquals(false, IntrospectionSupport.isGetter(setupSomething));
        Assert.assertEquals(false, IntrospectionSupport.isSetter(setupSomething));
    }

    @Test
    public void testExtractProperties() throws Exception {
        Map<String, Object> params = new LinkedHashMap<>();
        params.put("foo.name", "Camel");
        params.put("foo.age", 5);
        params.put("bar", "yes");
        // extract all "foo." properties
        // and their keys should have the prefix removed
        Map<String, Object> foo = IntrospectionSupport.extractProperties(params, "foo.");
        Assert.assertEquals(2, foo.size());
        Assert.assertEquals("Camel", foo.get("name"));
        Assert.assertEquals(5, foo.get("age"));
        // the extracted properties should be removed from original
        Assert.assertEquals(1, params.size());
        Assert.assertEquals("yes", params.get("bar"));
    }

    @Test
    public void testFindSetterMethodsOrderedByParameterType() throws Exception {
        List<Method> setters = IntrospectionSupport.findSetterMethodsOrderedByParameterType(IntrospectionSupportTest.MyOverloadedBean.class, "bean", false);
        Assert.assertNotNull(setters);
        Assert.assertEquals(2, setters.size());
        Assert.assertEquals(ExampleBean.class, setters.get(0).getParameterTypes()[0]);
        Assert.assertEquals(String.class, setters.get(1).getParameterTypes()[0]);
    }
}

