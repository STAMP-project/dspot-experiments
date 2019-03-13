/**
 * Copyright 2002-2018 the original author or authors.
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
package org.springframework.beans.factory.support;


import AnnotationAwareOrderComparator.INSTANCE;
import RootBeanDefinition.AUTOWIRE_BY_TYPE;
import RootBeanDefinition.AUTOWIRE_CONSTRUCTOR;
import TestGroup.LONG_RUNNING;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.beans.PropertyEditorRegistrar;
import org.springframework.beans.PropertyEditorRegistry;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.NoUniqueBeanDefinitionException;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.config.TypedStringValue;
import org.springframework.beans.propertyeditors.CustomNumberEditor;
import org.springframework.core.OverridingClassLoader;
import org.springframework.core.ResolvableType;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.UrlResource;
import org.springframework.tests.Assume;
import org.springframework.tests.sample.beans.GenericBean;
import org.springframework.tests.sample.beans.GenericIntegerBean;
import org.springframework.tests.sample.beans.GenericSetOfIntegerBean;
import org.springframework.tests.sample.beans.TestBean;

import static RootBeanDefinition.AUTOWIRE_CONSTRUCTOR;


/**
 *
 *
 * @author Juergen Hoeller
 * @author Chris Beams
 * @author Sam Brannen
 * @since 20.01.2006
 */
public class BeanFactoryGenericsTests {
    @Test
    public void testGenericSetProperty() {
        DefaultListableBeanFactory bf = new DefaultListableBeanFactory();
        RootBeanDefinition rbd = new RootBeanDefinition(GenericBean.class);
        Set<String> input = new HashSet<>();
        input.add("4");
        input.add("5");
        rbd.getPropertyValues().add("integerSet", input);
        bf.registerBeanDefinition("genericBean", rbd);
        GenericBean<?> gb = ((GenericBean<?>) (bf.getBean("genericBean")));
        Assert.assertTrue(gb.getIntegerSet().contains(new Integer(4)));
        Assert.assertTrue(gb.getIntegerSet().contains(new Integer(5)));
    }

    @Test
    public void testGenericListProperty() throws Exception {
        DefaultListableBeanFactory bf = new DefaultListableBeanFactory();
        RootBeanDefinition rbd = new RootBeanDefinition(GenericBean.class);
        List<String> input = new ArrayList<>();
        input.add("http://localhost:8080");
        input.add("http://localhost:9090");
        rbd.getPropertyValues().add("resourceList", input);
        bf.registerBeanDefinition("genericBean", rbd);
        GenericBean<?> gb = ((GenericBean<?>) (bf.getBean("genericBean")));
        Assert.assertEquals(new UrlResource("http://localhost:8080"), gb.getResourceList().get(0));
        Assert.assertEquals(new UrlResource("http://localhost:9090"), gb.getResourceList().get(1));
    }

    @Test
    public void testGenericListPropertyWithAutowiring() throws Exception {
        DefaultListableBeanFactory bf = new DefaultListableBeanFactory();
        bf.registerSingleton("resource1", new UrlResource("http://localhost:8080"));
        bf.registerSingleton("resource2", new UrlResource("http://localhost:9090"));
        RootBeanDefinition rbd = new RootBeanDefinition(GenericIntegerBean.class);
        rbd.setAutowireMode(AUTOWIRE_BY_TYPE);
        bf.registerBeanDefinition("genericBean", rbd);
        GenericIntegerBean gb = ((GenericIntegerBean) (bf.getBean("genericBean")));
        Assert.assertEquals(new UrlResource("http://localhost:8080"), gb.getResourceList().get(0));
        Assert.assertEquals(new UrlResource("http://localhost:9090"), gb.getResourceList().get(1));
    }

    @Test
    public void testGenericListPropertyWithInvalidElementType() {
        DefaultListableBeanFactory bf = new DefaultListableBeanFactory();
        RootBeanDefinition rbd = new RootBeanDefinition(GenericIntegerBean.class);
        List<Integer> input = new ArrayList<>();
        input.add(1);
        rbd.getPropertyValues().add("testBeanList", input);
        bf.registerBeanDefinition("genericBean", rbd);
        try {
            bf.getBean("genericBean");
            Assert.fail("Should have thrown BeanCreationException");
        } catch (BeanCreationException ex) {
            Assert.assertTrue(((ex.getMessage().contains("genericBean")) && (ex.getMessage().contains("testBeanList[0]"))));
            Assert.assertTrue(((ex.getMessage().contains(TestBean.class.getName())) && (ex.getMessage().contains("Integer"))));
        }
    }

    @Test
    public void testGenericListPropertyWithOptionalAutowiring() {
        DefaultListableBeanFactory bf = new DefaultListableBeanFactory();
        RootBeanDefinition rbd = new RootBeanDefinition(GenericBean.class);
        rbd.setAutowireMode(AUTOWIRE_BY_TYPE);
        bf.registerBeanDefinition("genericBean", rbd);
        GenericBean<?> gb = ((GenericBean<?>) (bf.getBean("genericBean")));
        Assert.assertNull(gb.getResourceList());
    }

    @Test
    public void testGenericMapProperty() {
        DefaultListableBeanFactory bf = new DefaultListableBeanFactory();
        RootBeanDefinition rbd = new RootBeanDefinition(GenericBean.class);
        Map<String, String> input = new HashMap<>();
        input.put("4", "5");
        input.put("6", "7");
        rbd.getPropertyValues().add("shortMap", input);
        bf.registerBeanDefinition("genericBean", rbd);
        GenericBean<?> gb = ((GenericBean<?>) (bf.getBean("genericBean")));
        Assert.assertEquals(new Integer(5), gb.getShortMap().get(new Short("4")));
        Assert.assertEquals(new Integer(7), gb.getShortMap().get(new Short("6")));
    }

    @Test
    public void testGenericListOfArraysProperty() {
        DefaultListableBeanFactory bf = new DefaultListableBeanFactory();
        new org.springframework.beans.factory.xml.XmlBeanDefinitionReader(bf).loadBeanDefinitions(new ClassPathResource("genericBeanTests.xml", getClass()));
        GenericBean<?> gb = ((GenericBean<?>) (bf.getBean("listOfArrays")));
        Assert.assertEquals(1, gb.getListOfArrays().size());
        String[] array = gb.getListOfArrays().get(0);
        Assert.assertEquals(2, array.length);
        Assert.assertEquals("value1", array[0]);
        Assert.assertEquals("value2", array[1]);
    }

    @Test
    public void testGenericSetConstructor() {
        DefaultListableBeanFactory bf = new DefaultListableBeanFactory();
        RootBeanDefinition rbd = new RootBeanDefinition(GenericBean.class);
        Set<String> input = new HashSet<>();
        input.add("4");
        input.add("5");
        rbd.getConstructorArgumentValues().addGenericArgumentValue(input);
        bf.registerBeanDefinition("genericBean", rbd);
        GenericBean<?> gb = ((GenericBean<?>) (bf.getBean("genericBean")));
        Assert.assertTrue(gb.getIntegerSet().contains(new Integer(4)));
        Assert.assertTrue(gb.getIntegerSet().contains(new Integer(5)));
    }

    @Test
    public void testGenericSetConstructorWithAutowiring() {
        DefaultListableBeanFactory bf = new DefaultListableBeanFactory();
        bf.registerSingleton("integer1", new Integer(4));
        bf.registerSingleton("integer2", new Integer(5));
        RootBeanDefinition rbd = new RootBeanDefinition(GenericBean.class);
        rbd.setAutowireMode(AUTOWIRE_CONSTRUCTOR);
        bf.registerBeanDefinition("genericBean", rbd);
        GenericBean<?> gb = ((GenericBean<?>) (bf.getBean("genericBean")));
        Assert.assertTrue(gb.getIntegerSet().contains(new Integer(4)));
        Assert.assertTrue(gb.getIntegerSet().contains(new Integer(5)));
    }

    @Test
    public void testGenericSetConstructorWithOptionalAutowiring() {
        DefaultListableBeanFactory bf = new DefaultListableBeanFactory();
        RootBeanDefinition rbd = new RootBeanDefinition(GenericBean.class);
        rbd.setAutowireMode(AUTOWIRE_CONSTRUCTOR);
        bf.registerBeanDefinition("genericBean", rbd);
        GenericBean<?> gb = ((GenericBean<?>) (bf.getBean("genericBean")));
        Assert.assertNull(gb.getIntegerSet());
    }

    @Test
    public void testGenericSetListConstructor() throws Exception {
        DefaultListableBeanFactory bf = new DefaultListableBeanFactory();
        RootBeanDefinition rbd = new RootBeanDefinition(GenericBean.class);
        Set<String> input = new HashSet<>();
        input.add("4");
        input.add("5");
        List<String> input2 = new ArrayList<>();
        input2.add("http://localhost:8080");
        input2.add("http://localhost:9090");
        rbd.getConstructorArgumentValues().addGenericArgumentValue(input);
        rbd.getConstructorArgumentValues().addGenericArgumentValue(input2);
        bf.registerBeanDefinition("genericBean", rbd);
        GenericBean<?> gb = ((GenericBean<?>) (bf.getBean("genericBean")));
        Assert.assertTrue(gb.getIntegerSet().contains(new Integer(4)));
        Assert.assertTrue(gb.getIntegerSet().contains(new Integer(5)));
        Assert.assertEquals(new UrlResource("http://localhost:8080"), gb.getResourceList().get(0));
        Assert.assertEquals(new UrlResource("http://localhost:9090"), gb.getResourceList().get(1));
    }

    @Test
    public void testGenericSetListConstructorWithAutowiring() throws Exception {
        DefaultListableBeanFactory bf = new DefaultListableBeanFactory();
        bf.registerSingleton("integer1", new Integer(4));
        bf.registerSingleton("integer2", new Integer(5));
        bf.registerSingleton("resource1", new UrlResource("http://localhost:8080"));
        bf.registerSingleton("resource2", new UrlResource("http://localhost:9090"));
        RootBeanDefinition rbd = new RootBeanDefinition(GenericBean.class);
        rbd.setAutowireMode(AUTOWIRE_CONSTRUCTOR);
        bf.registerBeanDefinition("genericBean", rbd);
        GenericBean<?> gb = ((GenericBean<?>) (bf.getBean("genericBean")));
        Assert.assertTrue(gb.getIntegerSet().contains(new Integer(4)));
        Assert.assertTrue(gb.getIntegerSet().contains(new Integer(5)));
        Assert.assertEquals(new UrlResource("http://localhost:8080"), gb.getResourceList().get(0));
        Assert.assertEquals(new UrlResource("http://localhost:9090"), gb.getResourceList().get(1));
    }

    @Test
    public void testGenericSetListConstructorWithOptionalAutowiring() throws Exception {
        DefaultListableBeanFactory bf = new DefaultListableBeanFactory();
        bf.registerSingleton("resource1", new UrlResource("http://localhost:8080"));
        bf.registerSingleton("resource2", new UrlResource("http://localhost:9090"));
        RootBeanDefinition rbd = new RootBeanDefinition(GenericBean.class);
        rbd.setAutowireMode(AUTOWIRE_CONSTRUCTOR);
        bf.registerBeanDefinition("genericBean", rbd);
        GenericBean<?> gb = ((GenericBean<?>) (bf.getBean("genericBean")));
        Assert.assertNull(gb.getIntegerSet());
        Assert.assertNull(gb.getResourceList());
    }

    @Test
    public void testGenericSetMapConstructor() {
        DefaultListableBeanFactory bf = new DefaultListableBeanFactory();
        RootBeanDefinition rbd = new RootBeanDefinition(GenericBean.class);
        Set<String> input = new HashSet<>();
        input.add("4");
        input.add("5");
        Map<String, String> input2 = new HashMap<>();
        input2.put("4", "5");
        input2.put("6", "7");
        rbd.getConstructorArgumentValues().addGenericArgumentValue(input);
        rbd.getConstructorArgumentValues().addGenericArgumentValue(input2);
        bf.registerBeanDefinition("genericBean", rbd);
        GenericBean<?> gb = ((GenericBean<?>) (bf.getBean("genericBean")));
        Assert.assertTrue(gb.getIntegerSet().contains(new Integer(4)));
        Assert.assertTrue(gb.getIntegerSet().contains(new Integer(5)));
        Assert.assertEquals(new Integer(5), gb.getShortMap().get(new Short("4")));
        Assert.assertEquals(new Integer(7), gb.getShortMap().get(new Short("6")));
    }

    @Test
    public void testGenericMapResourceConstructor() throws Exception {
        DefaultListableBeanFactory bf = new DefaultListableBeanFactory();
        RootBeanDefinition rbd = new RootBeanDefinition(GenericBean.class);
        Map<String, String> input = new HashMap<>();
        input.put("4", "5");
        input.put("6", "7");
        rbd.getConstructorArgumentValues().addGenericArgumentValue(input);
        rbd.getConstructorArgumentValues().addGenericArgumentValue("http://localhost:8080");
        bf.registerBeanDefinition("genericBean", rbd);
        GenericBean<?> gb = ((GenericBean<?>) (bf.getBean("genericBean")));
        Assert.assertEquals(new Integer(5), gb.getShortMap().get(new Short("4")));
        Assert.assertEquals(new Integer(7), gb.getShortMap().get(new Short("6")));
        Assert.assertEquals(new UrlResource("http://localhost:8080"), gb.getResourceList().get(0));
    }

    @Test
    public void testGenericMapMapConstructor() {
        DefaultListableBeanFactory bf = new DefaultListableBeanFactory();
        RootBeanDefinition rbd = new RootBeanDefinition(GenericBean.class);
        Map<String, String> input = new HashMap<>();
        input.put("1", "0");
        input.put("2", "3");
        Map<String, String> input2 = new HashMap<>();
        input2.put("4", "5");
        input2.put("6", "7");
        rbd.getConstructorArgumentValues().addGenericArgumentValue(input);
        rbd.getConstructorArgumentValues().addGenericArgumentValue(input2);
        bf.registerBeanDefinition("genericBean", rbd);
        GenericBean<?> gb = ((GenericBean<?>) (bf.getBean("genericBean")));
        Assert.assertNotSame(gb.getPlainMap(), gb.getShortMap());
        Assert.assertEquals(2, gb.getPlainMap().size());
        Assert.assertEquals("0", gb.getPlainMap().get("1"));
        Assert.assertEquals("3", gb.getPlainMap().get("2"));
        Assert.assertEquals(2, gb.getShortMap().size());
        Assert.assertEquals(new Integer(5), gb.getShortMap().get(new Short("4")));
        Assert.assertEquals(new Integer(7), gb.getShortMap().get(new Short("6")));
    }

    @Test
    public void testGenericMapMapConstructorWithSameRefAndConversion() {
        DefaultListableBeanFactory bf = new DefaultListableBeanFactory();
        RootBeanDefinition rbd = new RootBeanDefinition(GenericBean.class);
        Map<String, String> input = new HashMap<>();
        input.put("1", "0");
        input.put("2", "3");
        rbd.getConstructorArgumentValues().addGenericArgumentValue(input);
        rbd.getConstructorArgumentValues().addGenericArgumentValue(input);
        bf.registerBeanDefinition("genericBean", rbd);
        GenericBean<?> gb = ((GenericBean<?>) (bf.getBean("genericBean")));
        Assert.assertNotSame(gb.getPlainMap(), gb.getShortMap());
        Assert.assertEquals(2, gb.getPlainMap().size());
        Assert.assertEquals("0", gb.getPlainMap().get("1"));
        Assert.assertEquals("3", gb.getPlainMap().get("2"));
        Assert.assertEquals(2, gb.getShortMap().size());
        Assert.assertEquals(new Integer(0), gb.getShortMap().get(new Short("1")));
        Assert.assertEquals(new Integer(3), gb.getShortMap().get(new Short("2")));
    }

    @Test
    public void testGenericMapMapConstructorWithSameRefAndNoConversion() {
        DefaultListableBeanFactory bf = new DefaultListableBeanFactory();
        RootBeanDefinition rbd = new RootBeanDefinition(GenericBean.class);
        Map<Short, Integer> input = new HashMap<>();
        input.put(new Short(((short) (1))), new Integer(0));
        input.put(new Short(((short) (2))), new Integer(3));
        rbd.getConstructorArgumentValues().addGenericArgumentValue(input);
        rbd.getConstructorArgumentValues().addGenericArgumentValue(input);
        bf.registerBeanDefinition("genericBean", rbd);
        GenericBean<?> gb = ((GenericBean<?>) (bf.getBean("genericBean")));
        Assert.assertSame(gb.getPlainMap(), gb.getShortMap());
        Assert.assertEquals(2, gb.getShortMap().size());
        Assert.assertEquals(new Integer(0), gb.getShortMap().get(new Short("1")));
        Assert.assertEquals(new Integer(3), gb.getShortMap().get(new Short("2")));
    }

    @Test
    public void testGenericMapWithKeyTypeConstructor() {
        DefaultListableBeanFactory bf = new DefaultListableBeanFactory();
        RootBeanDefinition rbd = new RootBeanDefinition(GenericBean.class);
        Map<String, String> input = new HashMap<>();
        input.put("4", "5");
        input.put("6", "7");
        rbd.getConstructorArgumentValues().addGenericArgumentValue(input);
        bf.registerBeanDefinition("genericBean", rbd);
        GenericBean<?> gb = ((GenericBean<?>) (bf.getBean("genericBean")));
        Assert.assertEquals("5", gb.getLongMap().get(new Long("4")));
        Assert.assertEquals("7", gb.getLongMap().get(new Long("6")));
    }

    @Test
    public void testGenericMapWithCollectionValueConstructor() {
        DefaultListableBeanFactory bf = new DefaultListableBeanFactory();
        bf.addPropertyEditorRegistrar(new PropertyEditorRegistrar() {
            @Override
            public void registerCustomEditors(PropertyEditorRegistry registry) {
                registry.registerCustomEditor(Number.class, new CustomNumberEditor(Integer.class, false));
            }
        });
        RootBeanDefinition rbd = new RootBeanDefinition(GenericBean.class);
        Map<String, AbstractCollection<?>> input = new HashMap<>();
        HashSet<Integer> value1 = new HashSet<>();
        value1.add(new Integer(1));
        input.put("1", value1);
        ArrayList<Boolean> value2 = new ArrayList<>();
        value2.add(Boolean.TRUE);
        input.put("2", value2);
        rbd.getConstructorArgumentValues().addGenericArgumentValue(Boolean.TRUE);
        rbd.getConstructorArgumentValues().addGenericArgumentValue(input);
        bf.registerBeanDefinition("genericBean", rbd);
        GenericBean<?> gb = ((GenericBean<?>) (bf.getBean("genericBean")));
        Assert.assertTrue(((gb.getCollectionMap().get(new Integer(1))) instanceof HashSet));
        Assert.assertTrue(((gb.getCollectionMap().get(new Integer(2))) instanceof ArrayList));
    }

    @Test
    public void testGenericSetFactoryMethod() {
        DefaultListableBeanFactory bf = new DefaultListableBeanFactory();
        RootBeanDefinition rbd = new RootBeanDefinition(GenericBean.class);
        rbd.setFactoryMethodName("createInstance");
        Set<String> input = new HashSet<>();
        input.add("4");
        input.add("5");
        rbd.getConstructorArgumentValues().addGenericArgumentValue(input);
        bf.registerBeanDefinition("genericBean", rbd);
        GenericBean<?> gb = ((GenericBean<?>) (bf.getBean("genericBean")));
        Assert.assertTrue(gb.getIntegerSet().contains(new Integer(4)));
        Assert.assertTrue(gb.getIntegerSet().contains(new Integer(5)));
    }

    @Test
    public void testGenericSetListFactoryMethod() throws Exception {
        DefaultListableBeanFactory bf = new DefaultListableBeanFactory();
        RootBeanDefinition rbd = new RootBeanDefinition(GenericBean.class);
        rbd.setFactoryMethodName("createInstance");
        Set<String> input = new HashSet<>();
        input.add("4");
        input.add("5");
        List<String> input2 = new ArrayList<>();
        input2.add("http://localhost:8080");
        input2.add("http://localhost:9090");
        rbd.getConstructorArgumentValues().addGenericArgumentValue(input);
        rbd.getConstructorArgumentValues().addGenericArgumentValue(input2);
        bf.registerBeanDefinition("genericBean", rbd);
        GenericBean<?> gb = ((GenericBean<?>) (bf.getBean("genericBean")));
        Assert.assertTrue(gb.getIntegerSet().contains(new Integer(4)));
        Assert.assertTrue(gb.getIntegerSet().contains(new Integer(5)));
        Assert.assertEquals(new UrlResource("http://localhost:8080"), gb.getResourceList().get(0));
        Assert.assertEquals(new UrlResource("http://localhost:9090"), gb.getResourceList().get(1));
    }

    @Test
    public void testGenericSetMapFactoryMethod() {
        DefaultListableBeanFactory bf = new DefaultListableBeanFactory();
        RootBeanDefinition rbd = new RootBeanDefinition(GenericBean.class);
        rbd.setFactoryMethodName("createInstance");
        Set<String> input = new HashSet<>();
        input.add("4");
        input.add("5");
        Map<String, String> input2 = new HashMap<>();
        input2.put("4", "5");
        input2.put("6", "7");
        rbd.getConstructorArgumentValues().addGenericArgumentValue(input);
        rbd.getConstructorArgumentValues().addGenericArgumentValue(input2);
        bf.registerBeanDefinition("genericBean", rbd);
        GenericBean<?> gb = ((GenericBean<?>) (bf.getBean("genericBean")));
        Assert.assertTrue(gb.getIntegerSet().contains(new Integer(4)));
        Assert.assertTrue(gb.getIntegerSet().contains(new Integer(5)));
        Assert.assertEquals(new Integer(5), gb.getShortMap().get(new Short("4")));
        Assert.assertEquals(new Integer(7), gb.getShortMap().get(new Short("6")));
    }

    @Test
    public void testGenericMapResourceFactoryMethod() throws Exception {
        DefaultListableBeanFactory bf = new DefaultListableBeanFactory();
        RootBeanDefinition rbd = new RootBeanDefinition(GenericBean.class);
        rbd.setFactoryMethodName("createInstance");
        Map<String, String> input = new HashMap<>();
        input.put("4", "5");
        input.put("6", "7");
        rbd.getConstructorArgumentValues().addGenericArgumentValue(input);
        rbd.getConstructorArgumentValues().addGenericArgumentValue("http://localhost:8080");
        bf.registerBeanDefinition("genericBean", rbd);
        GenericBean<?> gb = ((GenericBean<?>) (bf.getBean("genericBean")));
        Assert.assertEquals(new Integer(5), gb.getShortMap().get(new Short("4")));
        Assert.assertEquals(new Integer(7), gb.getShortMap().get(new Short("6")));
        Assert.assertEquals(new UrlResource("http://localhost:8080"), gb.getResourceList().get(0));
    }

    @Test
    public void testGenericMapMapFactoryMethod() {
        DefaultListableBeanFactory bf = new DefaultListableBeanFactory();
        RootBeanDefinition rbd = new RootBeanDefinition(GenericBean.class);
        rbd.setFactoryMethodName("createInstance");
        Map<String, String> input = new HashMap<>();
        input.put("1", "0");
        input.put("2", "3");
        Map<String, String> input2 = new HashMap<>();
        input2.put("4", "5");
        input2.put("6", "7");
        rbd.getConstructorArgumentValues().addGenericArgumentValue(input);
        rbd.getConstructorArgumentValues().addGenericArgumentValue(input2);
        bf.registerBeanDefinition("genericBean", rbd);
        GenericBean<?> gb = ((GenericBean<?>) (bf.getBean("genericBean")));
        Assert.assertEquals("0", gb.getPlainMap().get("1"));
        Assert.assertEquals("3", gb.getPlainMap().get("2"));
        Assert.assertEquals(new Integer(5), gb.getShortMap().get(new Short("4")));
        Assert.assertEquals(new Integer(7), gb.getShortMap().get(new Short("6")));
    }

    @Test
    public void testGenericMapWithKeyTypeFactoryMethod() {
        DefaultListableBeanFactory bf = new DefaultListableBeanFactory();
        RootBeanDefinition rbd = new RootBeanDefinition(GenericBean.class);
        rbd.setFactoryMethodName("createInstance");
        Map<String, String> input = new HashMap<>();
        input.put("4", "5");
        input.put("6", "7");
        rbd.getConstructorArgumentValues().addGenericArgumentValue(input);
        bf.registerBeanDefinition("genericBean", rbd);
        GenericBean<?> gb = ((GenericBean<?>) (bf.getBean("genericBean")));
        Assert.assertEquals("5", gb.getLongMap().get(new Long("4")));
        Assert.assertEquals("7", gb.getLongMap().get(new Long("6")));
    }

    @Test
    public void testGenericMapWithCollectionValueFactoryMethod() {
        DefaultListableBeanFactory bf = new DefaultListableBeanFactory();
        bf.addPropertyEditorRegistrar(new PropertyEditorRegistrar() {
            @Override
            public void registerCustomEditors(PropertyEditorRegistry registry) {
                registry.registerCustomEditor(Number.class, new CustomNumberEditor(Integer.class, false));
            }
        });
        RootBeanDefinition rbd = new RootBeanDefinition(GenericBean.class);
        rbd.setFactoryMethodName("createInstance");
        Map<String, AbstractCollection<?>> input = new HashMap<>();
        HashSet<Integer> value1 = new HashSet<>();
        value1.add(new Integer(1));
        input.put("1", value1);
        ArrayList<Boolean> value2 = new ArrayList<>();
        value2.add(Boolean.TRUE);
        input.put("2", value2);
        rbd.getConstructorArgumentValues().addGenericArgumentValue(Boolean.TRUE);
        rbd.getConstructorArgumentValues().addGenericArgumentValue(input);
        bf.registerBeanDefinition("genericBean", rbd);
        GenericBean<?> gb = ((GenericBean<?>) (bf.getBean("genericBean")));
        Assert.assertTrue(((gb.getCollectionMap().get(new Integer(1))) instanceof HashSet));
        Assert.assertTrue(((gb.getCollectionMap().get(new Integer(2))) instanceof ArrayList));
    }

    @Test
    public void testGenericListBean() throws Exception {
        DefaultListableBeanFactory bf = new DefaultListableBeanFactory();
        new org.springframework.beans.factory.xml.XmlBeanDefinitionReader(bf).loadBeanDefinitions(new ClassPathResource("genericBeanTests.xml", getClass()));
        List<?> list = ((List<?>) (bf.getBean("list")));
        Assert.assertEquals(1, list.size());
        Assert.assertEquals(new URL("http://localhost:8080"), list.get(0));
    }

    @Test
    public void testGenericSetBean() throws Exception {
        DefaultListableBeanFactory bf = new DefaultListableBeanFactory();
        new org.springframework.beans.factory.xml.XmlBeanDefinitionReader(bf).loadBeanDefinitions(new ClassPathResource("genericBeanTests.xml", getClass()));
        Set<?> set = ((Set<?>) (bf.getBean("set")));
        Assert.assertEquals(1, set.size());
        Assert.assertEquals(new URL("http://localhost:8080"), set.iterator().next());
    }

    @Test
    public void testGenericMapBean() throws Exception {
        DefaultListableBeanFactory bf = new DefaultListableBeanFactory();
        new org.springframework.beans.factory.xml.XmlBeanDefinitionReader(bf).loadBeanDefinitions(new ClassPathResource("genericBeanTests.xml", getClass()));
        Map<?, ?> map = ((Map<?, ?>) (bf.getBean("map")));
        Assert.assertEquals(1, map.size());
        Assert.assertEquals(new Integer(10), map.keySet().iterator().next());
        Assert.assertEquals(new URL("http://localhost:8080"), map.values().iterator().next());
    }

    @Test
    public void testGenericallyTypedIntegerBean() {
        DefaultListableBeanFactory bf = new DefaultListableBeanFactory();
        new org.springframework.beans.factory.xml.XmlBeanDefinitionReader(bf).loadBeanDefinitions(new ClassPathResource("genericBeanTests.xml", getClass()));
        GenericIntegerBean gb = ((GenericIntegerBean) (bf.getBean("integerBean")));
        Assert.assertEquals(new Integer(10), gb.getGenericProperty());
        Assert.assertEquals(new Integer(20), gb.getGenericListProperty().get(0));
        Assert.assertEquals(new Integer(30), gb.getGenericListProperty().get(1));
    }

    @Test
    public void testGenericallyTypedSetOfIntegerBean() {
        DefaultListableBeanFactory bf = new DefaultListableBeanFactory();
        new org.springframework.beans.factory.xml.XmlBeanDefinitionReader(bf).loadBeanDefinitions(new ClassPathResource("genericBeanTests.xml", getClass()));
        GenericSetOfIntegerBean gb = ((GenericSetOfIntegerBean) (bf.getBean("setOfIntegerBean")));
        Assert.assertEquals(new Integer(10), gb.getGenericProperty().iterator().next());
        Assert.assertEquals(new Integer(20), gb.getGenericListProperty().get(0).iterator().next());
        Assert.assertEquals(new Integer(30), gb.getGenericListProperty().get(1).iterator().next());
    }

    @Test
    public void testSetBean() throws Exception {
        Assume.group(LONG_RUNNING);
        DefaultListableBeanFactory bf = new DefaultListableBeanFactory();
        new org.springframework.beans.factory.xml.XmlBeanDefinitionReader(bf).loadBeanDefinitions(new ClassPathResource("genericBeanTests.xml", getClass()));
        BeanFactoryGenericsTests.UrlSet us = ((BeanFactoryGenericsTests.UrlSet) (bf.getBean("setBean")));
        Assert.assertEquals(1, us.size());
        Assert.assertEquals(new URL("http://www.springframework.org"), us.iterator().next());
    }

    /**
     * Tests support for parameterized static {@code factory-method} declarations such as
     * Mockito's {@code mock()} method which has the following signature.
     * <pre>
     * {@code public static <T> T mock(Class<T> classToMock)}
     * </pre>
     * <p>See SPR-9493
     */
    @Test
    public void parameterizedStaticFactoryMethod() {
        RootBeanDefinition rbd = new RootBeanDefinition(Mockito.class);
        rbd.setFactoryMethodName("mock");
        rbd.getConstructorArgumentValues().addGenericArgumentValue(Runnable.class);
        DefaultListableBeanFactory bf = new DefaultListableBeanFactory();
        bf.registerBeanDefinition("mock", rbd);
        Assert.assertEquals(Runnable.class, bf.getType("mock"));
        Assert.assertEquals(Runnable.class, bf.getType("mock"));
        Map<String, Runnable> beans = bf.getBeansOfType(Runnable.class);
        Assert.assertEquals(1, beans.size());
    }

    /**
     * Tests support for parameterized instance {@code factory-method} declarations such
     * as EasyMock's {@code IMocksControl.createMock()} method which has the following
     * signature.
     * <pre>
     * {@code public <T> T createMock(Class<T> toMock)}
     * </pre>
     * <p>See SPR-10411
     */
    @Test
    public void parameterizedInstanceFactoryMethod() {
        DefaultListableBeanFactory bf = new DefaultListableBeanFactory();
        RootBeanDefinition rbd = new RootBeanDefinition(BeanFactoryGenericsTests.MocksControl.class);
        bf.registerBeanDefinition("mocksControl", rbd);
        rbd = new RootBeanDefinition();
        rbd.setFactoryBeanName("mocksControl");
        rbd.setFactoryMethodName("createMock");
        rbd.getConstructorArgumentValues().addGenericArgumentValue(Runnable.class);
        bf.registerBeanDefinition("mock", rbd);
        Assert.assertTrue(bf.isTypeMatch("mock", Runnable.class));
        Assert.assertTrue(bf.isTypeMatch("mock", Runnable.class));
        Assert.assertEquals(Runnable.class, bf.getType("mock"));
        Assert.assertEquals(Runnable.class, bf.getType("mock"));
        Map<String, Runnable> beans = bf.getBeansOfType(Runnable.class);
        Assert.assertEquals(1, beans.size());
    }

    @Test
    public void parameterizedInstanceFactoryMethodWithNonResolvedClassName() {
        DefaultListableBeanFactory bf = new DefaultListableBeanFactory();
        RootBeanDefinition rbd = new RootBeanDefinition(BeanFactoryGenericsTests.MocksControl.class);
        bf.registerBeanDefinition("mocksControl", rbd);
        rbd = new RootBeanDefinition();
        rbd.setFactoryBeanName("mocksControl");
        rbd.setFactoryMethodName("createMock");
        rbd.getConstructorArgumentValues().addGenericArgumentValue(Runnable.class.getName());
        bf.registerBeanDefinition("mock", rbd);
        Assert.assertTrue(bf.isTypeMatch("mock", Runnable.class));
        Assert.assertTrue(bf.isTypeMatch("mock", Runnable.class));
        Assert.assertEquals(Runnable.class, bf.getType("mock"));
        Assert.assertEquals(Runnable.class, bf.getType("mock"));
        Map<String, Runnable> beans = bf.getBeansOfType(Runnable.class);
        Assert.assertEquals(1, beans.size());
    }

    @Test
    public void parameterizedInstanceFactoryMethodWithWrappedClassName() {
        DefaultListableBeanFactory bf = new DefaultListableBeanFactory();
        RootBeanDefinition rbd = new RootBeanDefinition();
        rbd.setBeanClassName(Mockito.class.getName());
        rbd.setFactoryMethodName("mock");
        // TypedStringValue used to be equivalent to an XML-defined argument String
        rbd.getConstructorArgumentValues().addGenericArgumentValue(new TypedStringValue(Runnable.class.getName()));
        bf.registerBeanDefinition("mock", rbd);
        Assert.assertTrue(bf.isTypeMatch("mock", Runnable.class));
        Assert.assertTrue(bf.isTypeMatch("mock", Runnable.class));
        Assert.assertEquals(Runnable.class, bf.getType("mock"));
        Assert.assertEquals(Runnable.class, bf.getType("mock"));
        Map<String, Runnable> beans = bf.getBeansOfType(Runnable.class);
        Assert.assertEquals(1, beans.size());
    }

    @Test
    public void parameterizedInstanceFactoryMethodWithInvalidClassName() {
        DefaultListableBeanFactory bf = new DefaultListableBeanFactory();
        RootBeanDefinition rbd = new RootBeanDefinition(BeanFactoryGenericsTests.MocksControl.class);
        bf.registerBeanDefinition("mocksControl", rbd);
        rbd = new RootBeanDefinition();
        rbd.setFactoryBeanName("mocksControl");
        rbd.setFactoryMethodName("createMock");
        rbd.getConstructorArgumentValues().addGenericArgumentValue("x");
        bf.registerBeanDefinition("mock", rbd);
        Assert.assertFalse(bf.isTypeMatch("mock", Runnable.class));
        Assert.assertFalse(bf.isTypeMatch("mock", Runnable.class));
        Assert.assertNull(bf.getType("mock"));
        Assert.assertNull(bf.getType("mock"));
        Map<String, Runnable> beans = bf.getBeansOfType(Runnable.class);
        Assert.assertEquals(0, beans.size());
    }

    @Test
    public void parameterizedInstanceFactoryMethodWithIndexedArgument() {
        DefaultListableBeanFactory bf = new DefaultListableBeanFactory();
        RootBeanDefinition rbd = new RootBeanDefinition(BeanFactoryGenericsTests.MocksControl.class);
        bf.registerBeanDefinition("mocksControl", rbd);
        rbd = new RootBeanDefinition();
        rbd.setFactoryBeanName("mocksControl");
        rbd.setFactoryMethodName("createMock");
        rbd.getConstructorArgumentValues().addIndexedArgumentValue(0, Runnable.class);
        bf.registerBeanDefinition("mock", rbd);
        Assert.assertTrue(bf.isTypeMatch("mock", Runnable.class));
        Assert.assertTrue(bf.isTypeMatch("mock", Runnable.class));
        Assert.assertEquals(Runnable.class, bf.getType("mock"));
        Assert.assertEquals(Runnable.class, bf.getType("mock"));
        Map<String, Runnable> beans = bf.getBeansOfType(Runnable.class);
        Assert.assertEquals(1, beans.size());
    }

    // SPR-16720
    @Test
    public void parameterizedInstanceFactoryMethodWithTempClassLoader() {
        DefaultListableBeanFactory bf = new DefaultListableBeanFactory();
        bf.setTempClassLoader(new OverridingClassLoader(getClass().getClassLoader()));
        RootBeanDefinition rbd = new RootBeanDefinition(BeanFactoryGenericsTests.MocksControl.class);
        bf.registerBeanDefinition("mocksControl", rbd);
        rbd = new RootBeanDefinition();
        rbd.setFactoryBeanName("mocksControl");
        rbd.setFactoryMethodName("createMock");
        rbd.getConstructorArgumentValues().addGenericArgumentValue(Runnable.class);
        bf.registerBeanDefinition("mock", rbd);
        Assert.assertTrue(bf.isTypeMatch("mock", Runnable.class));
        Assert.assertTrue(bf.isTypeMatch("mock", Runnable.class));
        Assert.assertEquals(Runnable.class, bf.getType("mock"));
        Assert.assertEquals(Runnable.class, bf.getType("mock"));
        Map<String, Runnable> beans = bf.getBeansOfType(Runnable.class);
        Assert.assertEquals(1, beans.size());
    }

    @Test
    public void testGenericMatchingWithBeanNameDifferentiation() {
        DefaultListableBeanFactory bf = new DefaultListableBeanFactory();
        bf.setAutowireCandidateResolver(new GenericTypeAwareAutowireCandidateResolver());
        bf.registerBeanDefinition("doubleStore", new RootBeanDefinition(BeanFactoryGenericsTests.NumberStore.class));
        bf.registerBeanDefinition("floatStore", new RootBeanDefinition(BeanFactoryGenericsTests.NumberStore.class));
        bf.registerBeanDefinition("numberBean", new RootBeanDefinition(BeanFactoryGenericsTests.NumberBean.class, AUTOWIRE_CONSTRUCTOR, false));
        BeanFactoryGenericsTests.NumberBean nb = bf.getBean(BeanFactoryGenericsTests.NumberBean.class);
        Assert.assertSame(bf.getBean("doubleStore"), nb.getDoubleStore());
        Assert.assertSame(bf.getBean("floatStore"), nb.getFloatStore());
        String[] numberStoreNames = bf.getBeanNamesForType(ResolvableType.forClass(BeanFactoryGenericsTests.NumberStore.class));
        String[] doubleStoreNames = bf.getBeanNamesForType(ResolvableType.forClassWithGenerics(BeanFactoryGenericsTests.NumberStore.class, Double.class));
        String[] floatStoreNames = bf.getBeanNamesForType(ResolvableType.forClassWithGenerics(BeanFactoryGenericsTests.NumberStore.class, Float.class));
        Assert.assertEquals(2, numberStoreNames.length);
        Assert.assertEquals("doubleStore", numberStoreNames[0]);
        Assert.assertEquals("floatStore", numberStoreNames[1]);
        Assert.assertEquals(0, doubleStoreNames.length);
        Assert.assertEquals(0, floatStoreNames.length);
    }

    @Test
    public void testGenericMatchingWithFullTypeDifferentiation() {
        DefaultListableBeanFactory bf = new DefaultListableBeanFactory();
        bf.setDependencyComparator(INSTANCE);
        bf.setAutowireCandidateResolver(new GenericTypeAwareAutowireCandidateResolver());
        RootBeanDefinition bd1 = new RootBeanDefinition(BeanFactoryGenericsTests.NumberStoreFactory.class);
        bd1.setFactoryMethodName("newDoubleStore");
        bf.registerBeanDefinition("store1", bd1);
        RootBeanDefinition bd2 = new RootBeanDefinition(BeanFactoryGenericsTests.NumberStoreFactory.class);
        bd2.setFactoryMethodName("newFloatStore");
        bf.registerBeanDefinition("store2", bd2);
        bf.registerBeanDefinition("numberBean", new RootBeanDefinition(BeanFactoryGenericsTests.NumberBean.class, AUTOWIRE_CONSTRUCTOR, false));
        BeanFactoryGenericsTests.NumberBean nb = bf.getBean(BeanFactoryGenericsTests.NumberBean.class);
        Assert.assertSame(bf.getBean("store1"), nb.getDoubleStore());
        Assert.assertSame(bf.getBean("store2"), nb.getFloatStore());
        String[] numberStoreNames = bf.getBeanNamesForType(ResolvableType.forClass(BeanFactoryGenericsTests.NumberStore.class));
        String[] doubleStoreNames = bf.getBeanNamesForType(ResolvableType.forClassWithGenerics(BeanFactoryGenericsTests.NumberStore.class, Double.class));
        String[] floatStoreNames = bf.getBeanNamesForType(ResolvableType.forClassWithGenerics(BeanFactoryGenericsTests.NumberStore.class, Float.class));
        Assert.assertEquals(2, numberStoreNames.length);
        Assert.assertEquals("store1", numberStoreNames[0]);
        Assert.assertEquals("store2", numberStoreNames[1]);
        Assert.assertEquals(1, doubleStoreNames.length);
        Assert.assertEquals("store1", doubleStoreNames[0]);
        Assert.assertEquals(1, floatStoreNames.length);
        Assert.assertEquals("store2", floatStoreNames[0]);
        ObjectProvider<BeanFactoryGenericsTests.NumberStore<?>> numberStoreProvider = bf.getBeanProvider(ResolvableType.forClass(BeanFactoryGenericsTests.NumberStore.class));
        ObjectProvider<BeanFactoryGenericsTests.NumberStore<Double>> doubleStoreProvider = bf.getBeanProvider(ResolvableType.forClassWithGenerics(BeanFactoryGenericsTests.NumberStore.class, Double.class));
        ObjectProvider<BeanFactoryGenericsTests.NumberStore<Float>> floatStoreProvider = bf.getBeanProvider(ResolvableType.forClassWithGenerics(BeanFactoryGenericsTests.NumberStore.class, Float.class));
        try {
            numberStoreProvider.getObject();
            Assert.fail("Should have thrown NoUniqueBeanDefinitionException");
        } catch (NoUniqueBeanDefinitionException ex) {
            // expected
        }
        try {
            numberStoreProvider.getIfAvailable();
            Assert.fail("Should have thrown NoUniqueBeanDefinitionException");
        } catch (NoUniqueBeanDefinitionException ex) {
            // expected
        }
        Assert.assertNull(numberStoreProvider.getIfUnique());
        Assert.assertSame(bf.getBean("store1"), doubleStoreProvider.getObject());
        Assert.assertSame(bf.getBean("store1"), doubleStoreProvider.getIfAvailable());
        Assert.assertSame(bf.getBean("store1"), doubleStoreProvider.getIfUnique());
        Assert.assertSame(bf.getBean("store2"), floatStoreProvider.getObject());
        Assert.assertSame(bf.getBean("store2"), floatStoreProvider.getIfAvailable());
        Assert.assertSame(bf.getBean("store2"), floatStoreProvider.getIfUnique());
        List<BeanFactoryGenericsTests.NumberStore<?>> resolved = new ArrayList<>();
        for (BeanFactoryGenericsTests.NumberStore<?> instance : numberStoreProvider) {
            resolved.add(instance);
        }
        Assert.assertEquals(2, resolved.size());
        Assert.assertSame(bf.getBean("store1"), resolved.get(0));
        Assert.assertSame(bf.getBean("store2"), resolved.get(1));
        resolved = numberStoreProvider.stream().collect(Collectors.toList());
        Assert.assertEquals(2, resolved.size());
        Assert.assertSame(bf.getBean("store1"), resolved.get(0));
        Assert.assertSame(bf.getBean("store2"), resolved.get(1));
        resolved = numberStoreProvider.orderedStream().collect(Collectors.toList());
        Assert.assertEquals(2, resolved.size());
        Assert.assertSame(bf.getBean("store2"), resolved.get(0));
        Assert.assertSame(bf.getBean("store1"), resolved.get(1));
        resolved = new ArrayList<>();
        for (BeanFactoryGenericsTests.NumberStore<Double> instance : doubleStoreProvider) {
            resolved.add(instance);
        }
        Assert.assertEquals(1, resolved.size());
        Assert.assertTrue(resolved.contains(bf.getBean("store1")));
        resolved = doubleStoreProvider.stream().collect(Collectors.toList());
        Assert.assertEquals(1, resolved.size());
        Assert.assertTrue(resolved.contains(bf.getBean("store1")));
        resolved = doubleStoreProvider.orderedStream().collect(Collectors.toList());
        Assert.assertEquals(1, resolved.size());
        Assert.assertTrue(resolved.contains(bf.getBean("store1")));
        resolved = new ArrayList<>();
        for (BeanFactoryGenericsTests.NumberStore<Float> instance : floatStoreProvider) {
            resolved.add(instance);
        }
        Assert.assertEquals(1, resolved.size());
        Assert.assertTrue(resolved.contains(bf.getBean("store2")));
        resolved = floatStoreProvider.stream().collect(Collectors.toList());
        Assert.assertEquals(1, resolved.size());
        Assert.assertTrue(resolved.contains(bf.getBean("store2")));
        resolved = floatStoreProvider.orderedStream().collect(Collectors.toList());
        Assert.assertEquals(1, resolved.size());
        Assert.assertTrue(resolved.contains(bf.getBean("store2")));
    }

    @Test
    public void testGenericMatchingWithUnresolvedOrderedStream() {
        DefaultListableBeanFactory bf = new DefaultListableBeanFactory();
        bf.setDependencyComparator(INSTANCE);
        bf.setAutowireCandidateResolver(new GenericTypeAwareAutowireCandidateResolver());
        RootBeanDefinition bd1 = new RootBeanDefinition(BeanFactoryGenericsTests.NumberStoreFactory.class);
        bd1.setFactoryMethodName("newDoubleStore");
        bf.registerBeanDefinition("store1", bd1);
        RootBeanDefinition bd2 = new RootBeanDefinition(BeanFactoryGenericsTests.NumberStoreFactory.class);
        bd2.setFactoryMethodName("newFloatStore");
        bf.registerBeanDefinition("store2", bd2);
        ObjectProvider<BeanFactoryGenericsTests.NumberStore<?>> numberStoreProvider = bf.getBeanProvider(ResolvableType.forClass(BeanFactoryGenericsTests.NumberStore.class));
        List<BeanFactoryGenericsTests.NumberStore<?>> resolved = numberStoreProvider.orderedStream().collect(Collectors.toList());
        Assert.assertEquals(2, resolved.size());
        Assert.assertSame(bf.getBean("store2"), resolved.get(0));
        Assert.assertSame(bf.getBean("store1"), resolved.get(1));
    }

    @SuppressWarnings("serial")
    public static class NamedUrlList extends LinkedList<URL> {}

    @SuppressWarnings("serial")
    public static class NamedUrlSet extends HashSet<URL> {}

    @SuppressWarnings("serial")
    public static class NamedUrlMap extends HashMap<Integer, URL> {}

    public static class CollectionDependentBean {
        public CollectionDependentBean(BeanFactoryGenericsTests.NamedUrlList list, BeanFactoryGenericsTests.NamedUrlSet set, BeanFactoryGenericsTests.NamedUrlMap map) {
            Assert.assertEquals(1, list.size());
            Assert.assertEquals(1, set.size());
            Assert.assertEquals(1, map.size());
        }
    }

    @SuppressWarnings("serial")
    public static class UrlSet extends HashSet<URL> {
        public UrlSet() {
            super();
        }

        public UrlSet(Set<? extends URL> urls) {
            super();
        }

        public void setUrlNames(Set<URI> urlNames) throws MalformedURLException {
            for (URI urlName : urlNames) {
                add(urlName.toURL());
            }
        }
    }

    /**
     * Pseudo-implementation of EasyMock's {@code MocksControl} class.
     */
    public static class MocksControl {
        @SuppressWarnings("unchecked")
        public <T> T createMock(Class<T> toMock) {
            return ((T) (Proxy.newProxyInstance(BeanFactoryGenericsTests.class.getClassLoader(), new Class<?>[]{ toMock }, new InvocationHandler() {
                @Override
                public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                    throw new UnsupportedOperationException("mocked!");
                }
            })));
        }
    }

    public static class NumberStore<T extends Number> {}

    public static class DoubleStore extends BeanFactoryGenericsTests.NumberStore<Double> {}

    public static class FloatStore extends BeanFactoryGenericsTests.NumberStore<Float> {}

    public static class NumberBean {
        private final BeanFactoryGenericsTests.NumberStore<Double> doubleStore;

        private final BeanFactoryGenericsTests.NumberStore<Float> floatStore;

        public NumberBean(BeanFactoryGenericsTests.NumberStore<Double> doubleStore, BeanFactoryGenericsTests.NumberStore<Float> floatStore) {
            this.doubleStore = doubleStore;
            this.floatStore = floatStore;
        }

        public BeanFactoryGenericsTests.NumberStore<Double> getDoubleStore() {
            return this.doubleStore;
        }

        public BeanFactoryGenericsTests.NumberStore<Float> getFloatStore() {
            return this.floatStore;
        }
    }

    public static class NumberStoreFactory {
        @Order(1)
        public static BeanFactoryGenericsTests.NumberStore<Double> newDoubleStore() {
            return new BeanFactoryGenericsTests.DoubleStore();
        }

        @Order(0)
        public static BeanFactoryGenericsTests.NumberStore<Float> newFloatStore() {
            return new BeanFactoryGenericsTests.FloatStore();
        }
    }
}

