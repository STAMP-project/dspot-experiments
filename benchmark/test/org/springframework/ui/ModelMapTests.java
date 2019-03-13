/**
 * Copyright 2002-2016 the original author or authors.
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
package org.springframework.ui;


import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.tests.sample.beans.TestBean;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;


/**
 *
 *
 * @author Rick Evans
 * @author Juergen Hoeller
 * @author Chris Beams
 */
public class ModelMapTests {
    @Test
    public void testNoArgCtorYieldsEmptyModel() throws Exception {
        Assert.assertEquals(0, new ModelMap().size());
    }

    /* SPR-2185 - Null model assertion causes backwards compatibility issue */
    @Test
    public void testAddNullObjectWithExplicitKey() throws Exception {
        ModelMap model = new ModelMap();
        model.addAttribute("foo", null);
        Assert.assertTrue(model.containsKey("foo"));
        Assert.assertNull(model.get("foo"));
    }

    /* SPR-2185 - Null model assertion causes backwards compatibility issue */
    @Test
    public void testAddNullObjectViaCtorWithExplicitKey() throws Exception {
        ModelMap model = new ModelMap("foo", null);
        Assert.assertTrue(model.containsKey("foo"));
        Assert.assertNull(model.get("foo"));
    }

    @Test
    public void testNamedObjectCtor() throws Exception {
        ModelMap model = new ModelMap("foo", "bing");
        Assert.assertEquals(1, model.size());
        String bing = ((String) (model.get("foo")));
        Assert.assertNotNull(bing);
        Assert.assertEquals("bing", bing);
    }

    @Test
    public void testUnnamedCtorScalar() throws Exception {
        ModelMap model = new ModelMap("foo", "bing");
        Assert.assertEquals(1, model.size());
        String bing = ((String) (model.get("foo")));
        Assert.assertNotNull(bing);
        Assert.assertEquals("bing", bing);
    }

    @Test
    public void testOneArgCtorWithScalar() throws Exception {
        ModelMap model = new ModelMap("bing");
        Assert.assertEquals(1, model.size());
        String string = ((String) (model.get("string")));
        Assert.assertNotNull(string);
        Assert.assertEquals("bing", string);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testOneArgCtorWithNull() {
        // Null model arguments added without a name being explicitly supplied are not allowed
        new ModelMap(null);
    }

    @Test
    public void testOneArgCtorWithCollection() throws Exception {
        ModelMap model = new ModelMap(new String[]{ "foo", "boing" });
        Assert.assertEquals(1, model.size());
        String[] strings = ((String[]) (model.get("stringList")));
        Assert.assertNotNull(strings);
        Assert.assertEquals(2, strings.length);
        Assert.assertEquals("foo", strings[0]);
        Assert.assertEquals("boing", strings[1]);
    }

    @Test
    public void testOneArgCtorWithEmptyCollection() throws Exception {
        ModelMap model = new ModelMap(new HashSet());
        // must not add if collection is empty...
        Assert.assertEquals(0, model.size());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddObjectWithNull() throws Exception {
        // Null model arguments added without a name being explicitly supplied are not allowed
        ModelMap model = new ModelMap();
        model.addAttribute(null);
    }

    @Test
    public void testAddObjectWithEmptyArray() throws Exception {
        ModelMap model = new ModelMap(new int[]{  });
        Assert.assertEquals(1, model.size());
        int[] ints = ((int[]) (model.get("intList")));
        Assert.assertNotNull(ints);
        Assert.assertEquals(0, ints.length);
    }

    @Test
    public void testAddAllObjectsWithNullMap() throws Exception {
        ModelMap model = new ModelMap();
        model.addAllAttributes(((Map<String, ?>) (null)));
        Assert.assertEquals(0, model.size());
    }

    @Test
    public void testAddAllObjectsWithNullCollection() throws Exception {
        ModelMap model = new ModelMap();
        model.addAllAttributes(((Collection<Object>) (null)));
        Assert.assertEquals(0, model.size());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddAllObjectsWithSparseArrayList() throws Exception {
        // Null model arguments added without a name being explicitly supplied are not allowed
        ModelMap model = new ModelMap();
        ArrayList<String> list = new ArrayList<>();
        list.add("bing");
        list.add(null);
        model.addAllAttributes(list);
    }

    @Test
    public void testAddMap() throws Exception {
        Map<String, String> map = new HashMap<>();
        map.put("one", "one-value");
        map.put("two", "two-value");
        ModelMap model = new ModelMap();
        model.addAttribute(map);
        Assert.assertEquals(1, model.size());
        String key = StringUtils.uncapitalize(ClassUtils.getShortName(map.getClass()));
        Assert.assertTrue(model.containsKey(key));
    }

    @Test
    public void testAddObjectNoKeyOfSameTypeOverrides() throws Exception {
        ModelMap model = new ModelMap();
        model.addAttribute("foo");
        model.addAttribute("bar");
        Assert.assertEquals(1, model.size());
        String bar = ((String) (model.get("string")));
        Assert.assertEquals("bar", bar);
    }

    @Test
    public void testAddListOfTheSameObjects() throws Exception {
        List<TestBean> beans = new ArrayList<>();
        beans.add(new TestBean("one"));
        beans.add(new TestBean("two"));
        beans.add(new TestBean("three"));
        ModelMap model = new ModelMap();
        model.addAllAttributes(beans);
        Assert.assertEquals(1, model.size());
    }

    @Test
    public void testMergeMapWithOverriding() throws Exception {
        Map<String, TestBean> beans = new HashMap<>();
        beans.put("one", new TestBean("one"));
        beans.put("two", new TestBean("two"));
        beans.put("three", new TestBean("three"));
        ModelMap model = new ModelMap();
        model.put("one", new TestBean("oneOld"));
        model.mergeAttributes(beans);
        Assert.assertEquals(3, model.size());
        Assert.assertEquals("oneOld", getName());
    }

    @Test
    public void testInnerClass() throws Exception {
        ModelMap map = new ModelMap();
        ModelMapTests.SomeInnerClass inner = new ModelMapTests.SomeInnerClass();
        map.addAttribute(inner);
        Assert.assertSame(inner, map.get("someInnerClass"));
    }

    @Test
    public void testInnerClassWithTwoUpperCaseLetters() throws Exception {
        ModelMap map = new ModelMap();
        ModelMapTests.UKInnerClass inner = new ModelMapTests.UKInnerClass();
        map.addAttribute(inner);
        Assert.assertSame(inner, map.get("UKInnerClass"));
    }

    @Test
    public void testAopCglibProxy() throws Exception {
        ModelMap map = new ModelMap();
        ProxyFactory factory = new ProxyFactory();
        ModelMapTests.SomeInnerClass val = new ModelMapTests.SomeInnerClass();
        factory.setTarget(val);
        factory.setProxyTargetClass(true);
        map.addAttribute(factory.getProxy());
        Assert.assertTrue(map.containsKey("someInnerClass"));
        Assert.assertEquals(val, map.get("someInnerClass"));
    }

    @Test
    public void testAopJdkProxy() throws Exception {
        ModelMap map = new ModelMap();
        ProxyFactory factory = new ProxyFactory();
        Map<?, ?> target = new HashMap<>();
        factory.setTarget(target);
        factory.addInterface(Map.class);
        Object proxy = factory.getProxy();
        map.addAttribute(proxy);
        Assert.assertSame(proxy, map.get("map"));
    }

    @Test
    public void testAopJdkProxyWithMultipleInterfaces() throws Exception {
        ModelMap map = new ModelMap();
        Map<?, ?> target = new HashMap<>();
        ProxyFactory factory = new ProxyFactory();
        factory.setTarget(target);
        factory.addInterface(Serializable.class);
        factory.addInterface(Cloneable.class);
        factory.addInterface(Comparable.class);
        factory.addInterface(Map.class);
        Object proxy = factory.getProxy();
        map.addAttribute(proxy);
        Assert.assertSame(proxy, map.get("map"));
    }

    @Test
    public void testAopJdkProxyWithDetectedInterfaces() throws Exception {
        ModelMap map = new ModelMap();
        Map<?, ?> target = new HashMap<>();
        ProxyFactory factory = new ProxyFactory(target);
        Object proxy = factory.getProxy();
        map.addAttribute(proxy);
        Assert.assertSame(proxy, map.get("map"));
    }

    @Test
    public void testRawJdkProxy() throws Exception {
        ModelMap map = new ModelMap();
        Object proxy = Proxy.newProxyInstance(getClass().getClassLoader(), new Class<?>[]{ Map.class }, new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) {
                return "proxy";
            }
        });
        map.addAttribute(proxy);
        Assert.assertSame(proxy, map.get("map"));
    }

    public static class SomeInnerClass {
        @Override
        public boolean equals(Object obj) {
            return obj instanceof ModelMapTests.SomeInnerClass;
        }

        @Override
        public int hashCode() {
            return ModelMapTests.SomeInnerClass.class.hashCode();
        }
    }

    public static class UKInnerClass {}
}

