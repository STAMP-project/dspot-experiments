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
package org.springframework.beans.factory.xml;


import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeMap;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.config.FieldRetrievingFactoryBean;
import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.beans.factory.parsing.ComponentDefinition;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.tests.beans.CollectingReaderEventListener;
import org.springframework.tests.sample.beans.CustomEnum;
import org.springframework.tests.sample.beans.TestBean;
import org.springframework.util.LinkedCaseInsensitiveMap;

import static java.lang.Thread.State.NEW;
import static java.lang.Thread.State.RUNNABLE;


/**
 *
 *
 * @author Rob Harrop
 * @author Juergen Hoeller
 * @author Mark Fisher
 */
public class UtilNamespaceHandlerTests {
    private DefaultListableBeanFactory beanFactory;

    private CollectingReaderEventListener listener = new CollectingReaderEventListener();

    @Test
    public void testConstant() {
        Integer min = ((Integer) (this.beanFactory.getBean("min")));
        Assert.assertEquals(Integer.MIN_VALUE, min.intValue());
    }

    @Test
    public void testConstantWithDefaultName() {
        Integer max = ((Integer) (this.beanFactory.getBean("java.lang.Integer.MAX_VALUE")));
        Assert.assertEquals(Integer.MAX_VALUE, max.intValue());
    }

    @Test
    public void testEvents() {
        ComponentDefinition propertiesComponent = this.listener.getComponentDefinition("myProperties");
        Assert.assertNotNull("Event for 'myProperties' not sent", propertiesComponent);
        AbstractBeanDefinition propertiesBean = ((AbstractBeanDefinition) (propertiesComponent.getBeanDefinitions()[0]));
        Assert.assertEquals("Incorrect BeanDefinition", PropertiesFactoryBean.class, propertiesBean.getBeanClass());
        ComponentDefinition constantComponent = this.listener.getComponentDefinition("min");
        Assert.assertNotNull("Event for 'min' not sent", propertiesComponent);
        AbstractBeanDefinition constantBean = ((AbstractBeanDefinition) (constantComponent.getBeanDefinitions()[0]));
        Assert.assertEquals("Incorrect BeanDefinition", FieldRetrievingFactoryBean.class, constantBean.getBeanClass());
    }

    @Test
    public void testNestedProperties() {
        TestBean bean = ((TestBean) (this.beanFactory.getBean("testBean")));
        Properties props = bean.getSomeProperties();
        Assert.assertEquals("Incorrect property value", "bar", props.get("foo"));
    }

    @Test
    public void testPropertyPath() {
        String name = ((String) (this.beanFactory.getBean("name")));
        Assert.assertEquals("Rob Harrop", name);
    }

    @Test
    public void testNestedPropertyPath() {
        TestBean bean = ((TestBean) (this.beanFactory.getBean("testBean")));
        Assert.assertEquals("Rob Harrop", bean.getName());
    }

    @Test
    public void testSimpleMap() {
        Map map = ((Map) (this.beanFactory.getBean("simpleMap")));
        Assert.assertEquals("bar", map.get("foo"));
        Map map2 = ((Map) (this.beanFactory.getBean("simpleMap")));
        Assert.assertTrue((map == map2));
    }

    @Test
    public void testScopedMap() {
        Map map = ((Map) (this.beanFactory.getBean("scopedMap")));
        Assert.assertEquals("bar", map.get("foo"));
        Map map2 = ((Map) (this.beanFactory.getBean("scopedMap")));
        Assert.assertEquals("bar", map2.get("foo"));
        Assert.assertTrue((map != map2));
    }

    @Test
    public void testSimpleList() {
        List list = ((List) (this.beanFactory.getBean("simpleList")));
        Assert.assertEquals("Rob Harrop", list.get(0));
        List list2 = ((List) (this.beanFactory.getBean("simpleList")));
        Assert.assertTrue((list == list2));
    }

    @Test
    public void testScopedList() {
        List list = ((List) (this.beanFactory.getBean("scopedList")));
        Assert.assertEquals("Rob Harrop", list.get(0));
        List list2 = ((List) (this.beanFactory.getBean("scopedList")));
        Assert.assertEquals("Rob Harrop", list2.get(0));
        Assert.assertTrue((list != list2));
    }

    @Test
    public void testSimpleSet() {
        Set set = ((Set) (this.beanFactory.getBean("simpleSet")));
        Assert.assertTrue(set.contains("Rob Harrop"));
        Set set2 = ((Set) (this.beanFactory.getBean("simpleSet")));
        Assert.assertTrue((set == set2));
    }

    @Test
    public void testScopedSet() {
        Set set = ((Set) (this.beanFactory.getBean("scopedSet")));
        Assert.assertTrue(set.contains("Rob Harrop"));
        Set set2 = ((Set) (this.beanFactory.getBean("scopedSet")));
        Assert.assertTrue(set2.contains("Rob Harrop"));
        Assert.assertTrue((set != set2));
    }

    @Test
    public void testMapWithRef() {
        Map map = ((Map) (this.beanFactory.getBean("mapWithRef")));
        Assert.assertTrue((map instanceof TreeMap));
        Assert.assertEquals(this.beanFactory.getBean("testBean"), map.get("bean"));
    }

    @Test
    public void testMapWithTypes() {
        Map map = ((Map) (this.beanFactory.getBean("mapWithTypes")));
        Assert.assertTrue((map instanceof LinkedCaseInsensitiveMap));
        Assert.assertEquals(this.beanFactory.getBean("testBean"), map.get("bean"));
    }

    @Test
    public void testNestedCollections() {
        TestBean bean = ((TestBean) (this.beanFactory.getBean("nestedCollectionsBean")));
        List list = bean.getSomeList();
        Assert.assertEquals(1, list.size());
        Assert.assertEquals("foo", list.get(0));
        Set set = bean.getSomeSet();
        Assert.assertEquals(1, set.size());
        Assert.assertTrue(set.contains("bar"));
        Map map = bean.getSomeMap();
        Assert.assertEquals(1, map.size());
        Assert.assertTrue(((map.get("foo")) instanceof Set));
        Set innerSet = ((Set) (map.get("foo")));
        Assert.assertEquals(1, innerSet.size());
        Assert.assertTrue(innerSet.contains("bar"));
        TestBean bean2 = ((TestBean) (this.beanFactory.getBean("nestedCollectionsBean")));
        Assert.assertEquals(list, bean2.getSomeList());
        Assert.assertEquals(set, bean2.getSomeSet());
        Assert.assertEquals(map, bean2.getSomeMap());
        Assert.assertFalse((list == (bean2.getSomeList())));
        Assert.assertFalse((set == (bean2.getSomeSet())));
        Assert.assertFalse((map == (bean2.getSomeMap())));
    }

    @Test
    public void testNestedShortcutCollections() {
        TestBean bean = ((TestBean) (this.beanFactory.getBean("nestedShortcutCollections")));
        Assert.assertEquals(1, bean.getStringArray().length);
        Assert.assertEquals("fooStr", bean.getStringArray()[0]);
        List list = bean.getSomeList();
        Assert.assertEquals(1, list.size());
        Assert.assertEquals("foo", list.get(0));
        Set set = bean.getSomeSet();
        Assert.assertEquals(1, set.size());
        Assert.assertTrue(set.contains("bar"));
        TestBean bean2 = ((TestBean) (this.beanFactory.getBean("nestedShortcutCollections")));
        Assert.assertTrue(Arrays.equals(bean.getStringArray(), bean2.getStringArray()));
        Assert.assertFalse(((bean.getStringArray()) == (bean2.getStringArray())));
        Assert.assertEquals(list, bean2.getSomeList());
        Assert.assertEquals(set, bean2.getSomeSet());
        Assert.assertFalse((list == (bean2.getSomeList())));
        Assert.assertFalse((set == (bean2.getSomeSet())));
    }

    @Test
    public void testNestedInCollections() {
        TestBean bean = ((TestBean) (this.beanFactory.getBean("nestedCustomTagBean")));
        List list = bean.getSomeList();
        Assert.assertEquals(1, list.size());
        Assert.assertEquals(Integer.MIN_VALUE, list.get(0));
        Set set = bean.getSomeSet();
        Assert.assertEquals(2, set.size());
        Assert.assertTrue(set.contains(NEW));
        Assert.assertTrue(set.contains(RUNNABLE));
        Map map = bean.getSomeMap();
        Assert.assertEquals(1, map.size());
        Assert.assertEquals(CustomEnum.VALUE_1, map.get("min"));
        TestBean bean2 = ((TestBean) (this.beanFactory.getBean("nestedCustomTagBean")));
        Assert.assertEquals(list, bean2.getSomeList());
        Assert.assertEquals(set, bean2.getSomeSet());
        Assert.assertEquals(map, bean2.getSomeMap());
        Assert.assertFalse((list == (bean2.getSomeList())));
        Assert.assertFalse((set == (bean2.getSomeSet())));
        Assert.assertFalse((map == (bean2.getSomeMap())));
    }

    @Test
    public void testCircularCollections() {
        TestBean bean = ((TestBean) (this.beanFactory.getBean("circularCollectionsBean")));
        List list = bean.getSomeList();
        Assert.assertEquals(1, list.size());
        Assert.assertEquals(bean, list.get(0));
        Set set = bean.getSomeSet();
        Assert.assertEquals(1, set.size());
        Assert.assertTrue(set.contains(bean));
        Map map = bean.getSomeMap();
        Assert.assertEquals(1, map.size());
        Assert.assertEquals(bean, map.get("foo"));
    }

    @Test
    public void testCircularCollectionBeansStartingWithList() {
        this.beanFactory.getBean("circularList");
        TestBean bean = ((TestBean) (this.beanFactory.getBean("circularCollectionBeansBean")));
        List list = bean.getSomeList();
        Assert.assertTrue(Proxy.isProxyClass(list.getClass()));
        Assert.assertEquals(1, list.size());
        Assert.assertEquals(bean, list.get(0));
        Set set = bean.getSomeSet();
        Assert.assertFalse(Proxy.isProxyClass(set.getClass()));
        Assert.assertEquals(1, set.size());
        Assert.assertTrue(set.contains(bean));
        Map map = bean.getSomeMap();
        Assert.assertFalse(Proxy.isProxyClass(map.getClass()));
        Assert.assertEquals(1, map.size());
        Assert.assertEquals(bean, map.get("foo"));
    }

    @Test
    public void testCircularCollectionBeansStartingWithSet() {
        this.beanFactory.getBean("circularSet");
        TestBean bean = ((TestBean) (this.beanFactory.getBean("circularCollectionBeansBean")));
        List list = bean.getSomeList();
        Assert.assertFalse(Proxy.isProxyClass(list.getClass()));
        Assert.assertEquals(1, list.size());
        Assert.assertEquals(bean, list.get(0));
        Set set = bean.getSomeSet();
        Assert.assertTrue(Proxy.isProxyClass(set.getClass()));
        Assert.assertEquals(1, set.size());
        Assert.assertTrue(set.contains(bean));
        Map map = bean.getSomeMap();
        Assert.assertFalse(Proxy.isProxyClass(map.getClass()));
        Assert.assertEquals(1, map.size());
        Assert.assertEquals(bean, map.get("foo"));
    }

    @Test
    public void testCircularCollectionBeansStartingWithMap() {
        this.beanFactory.getBean("circularMap");
        TestBean bean = ((TestBean) (this.beanFactory.getBean("circularCollectionBeansBean")));
        List list = bean.getSomeList();
        Assert.assertFalse(Proxy.isProxyClass(list.getClass()));
        Assert.assertEquals(1, list.size());
        Assert.assertEquals(bean, list.get(0));
        Set set = bean.getSomeSet();
        Assert.assertFalse(Proxy.isProxyClass(set.getClass()));
        Assert.assertEquals(1, set.size());
        Assert.assertTrue(set.contains(bean));
        Map map = bean.getSomeMap();
        Assert.assertTrue(Proxy.isProxyClass(map.getClass()));
        Assert.assertEquals(1, map.size());
        Assert.assertEquals(bean, map.get("foo"));
    }

    @Test
    public void testNestedInConstructor() {
        TestBean bean = ((TestBean) (this.beanFactory.getBean("constructedTestBean")));
        Assert.assertEquals("Rob Harrop", bean.getName());
    }

    @Test
    public void testLoadProperties() {
        Properties props = ((Properties) (this.beanFactory.getBean("myProperties")));
        Assert.assertEquals("Incorrect property value", "bar", props.get("foo"));
        Assert.assertEquals("Incorrect property value", null, props.get("foo2"));
        Properties props2 = ((Properties) (this.beanFactory.getBean("myProperties")));
        Assert.assertTrue((props == props2));
    }

    @Test
    public void testScopedProperties() {
        Properties props = ((Properties) (this.beanFactory.getBean("myScopedProperties")));
        Assert.assertEquals("Incorrect property value", "bar", props.get("foo"));
        Assert.assertEquals("Incorrect property value", null, props.get("foo2"));
        Properties props2 = ((Properties) (this.beanFactory.getBean("myScopedProperties")));
        Assert.assertEquals("Incorrect property value", "bar", props.get("foo"));
        Assert.assertEquals("Incorrect property value", null, props.get("foo2"));
        Assert.assertTrue((props != props2));
    }

    @Test
    public void testLocalProperties() {
        Properties props = ((Properties) (this.beanFactory.getBean("myLocalProperties")));
        Assert.assertEquals("Incorrect property value", null, props.get("foo"));
        Assert.assertEquals("Incorrect property value", "bar2", props.get("foo2"));
    }

    @Test
    public void testMergedProperties() {
        Properties props = ((Properties) (this.beanFactory.getBean("myMergedProperties")));
        Assert.assertEquals("Incorrect property value", "bar", props.get("foo"));
        Assert.assertEquals("Incorrect property value", "bar2", props.get("foo2"));
    }

    @Test
    public void testLocalOverrideDefault() {
        Properties props = ((Properties) (this.beanFactory.getBean("defaultLocalOverrideProperties")));
        Assert.assertEquals("Incorrect property value", "bar", props.get("foo"));
        Assert.assertEquals("Incorrect property value", "local2", props.get("foo2"));
    }

    @Test
    public void testLocalOverrideFalse() {
        Properties props = ((Properties) (this.beanFactory.getBean("falseLocalOverrideProperties")));
        Assert.assertEquals("Incorrect property value", "bar", props.get("foo"));
        Assert.assertEquals("Incorrect property value", "local2", props.get("foo2"));
    }

    @Test
    public void testLocalOverrideTrue() {
        Properties props = ((Properties) (this.beanFactory.getBean("trueLocalOverrideProperties")));
        Assert.assertEquals("Incorrect property value", "local", props.get("foo"));
        Assert.assertEquals("Incorrect property value", "local2", props.get("foo2"));
    }
}

