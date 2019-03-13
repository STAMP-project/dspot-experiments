/**
 * Copyright 2002-2017 the original author or authors.
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
package org.springframework.aop.scope;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.tests.context.SimpleMapScope;
import org.springframework.tests.sample.beans.ITestBean;
import org.springframework.tests.sample.beans.TestBean;
import org.springframework.util.SerializationTestUtils;


/**
 *
 *
 * @author Rob Harrop
 * @author Juergen Hoeller
 * @author Chris Beams
 */
public class ScopedProxyTests {
    private static final Class<?> CLASS = ScopedProxyTests.class;

    private static final String CLASSNAME = ScopedProxyTests.CLASS.getSimpleName();

    private static final ClassPathResource LIST_CONTEXT = new ClassPathResource(((ScopedProxyTests.CLASSNAME) + "-list.xml"), ScopedProxyTests.CLASS);

    private static final ClassPathResource MAP_CONTEXT = new ClassPathResource(((ScopedProxyTests.CLASSNAME) + "-map.xml"), ScopedProxyTests.CLASS);

    private static final ClassPathResource OVERRIDE_CONTEXT = new ClassPathResource(((ScopedProxyTests.CLASSNAME) + "-override.xml"), ScopedProxyTests.CLASS);

    private static final ClassPathResource TESTBEAN_CONTEXT = new ClassPathResource(((ScopedProxyTests.CLASSNAME) + "-testbean.xml"), ScopedProxyTests.CLASS);

    // SPR-2108
    @Test
    public void testProxyAssignable() throws Exception {
        DefaultListableBeanFactory bf = new DefaultListableBeanFactory();
        new org.springframework.beans.factory.xml.XmlBeanDefinitionReader(bf).loadBeanDefinitions(ScopedProxyTests.MAP_CONTEXT);
        Object baseMap = bf.getBean("singletonMap");
        Assert.assertTrue((baseMap instanceof Map));
    }

    @Test
    public void testSimpleProxy() throws Exception {
        DefaultListableBeanFactory bf = new DefaultListableBeanFactory();
        new org.springframework.beans.factory.xml.XmlBeanDefinitionReader(bf).loadBeanDefinitions(ScopedProxyTests.MAP_CONTEXT);
        Object simpleMap = bf.getBean("simpleMap");
        Assert.assertTrue((simpleMap instanceof Map));
        Assert.assertTrue((simpleMap instanceof HashMap));
    }

    @Test
    public void testScopedOverride() throws Exception {
        GenericApplicationContext ctx = new GenericApplicationContext();
        new org.springframework.beans.factory.xml.XmlBeanDefinitionReader(ctx).loadBeanDefinitions(ScopedProxyTests.OVERRIDE_CONTEXT);
        SimpleMapScope scope = new SimpleMapScope();
        ctx.getBeanFactory().registerScope("request", scope);
        ctx.refresh();
        ITestBean bean = ((ITestBean) (ctx.getBean("testBean")));
        Assert.assertEquals("male", bean.getName());
        Assert.assertEquals(99, bean.getAge());
        Assert.assertTrue(scope.getMap().containsKey("scopedTarget.testBean"));
        Assert.assertEquals(TestBean.class, scope.getMap().get("scopedTarget.testBean").getClass());
    }

    @Test
    public void testJdkScopedProxy() throws Exception {
        DefaultListableBeanFactory bf = new DefaultListableBeanFactory();
        new org.springframework.beans.factory.xml.XmlBeanDefinitionReader(bf).loadBeanDefinitions(ScopedProxyTests.TESTBEAN_CONTEXT);
        bf.setSerializationId("X");
        SimpleMapScope scope = new SimpleMapScope();
        bf.registerScope("request", scope);
        ITestBean bean = ((ITestBean) (bf.getBean("testBean")));
        Assert.assertNotNull(bean);
        Assert.assertTrue(AopUtils.isJdkDynamicProxy(bean));
        Assert.assertTrue((bean instanceof ScopedObject));
        ScopedObject scoped = ((ScopedObject) (bean));
        Assert.assertEquals(TestBean.class, scoped.getTargetObject().getClass());
        bean.setAge(101);
        Assert.assertTrue(scope.getMap().containsKey("testBeanTarget"));
        Assert.assertEquals(TestBean.class, scope.getMap().get("testBeanTarget").getClass());
        ITestBean deserialized = ((ITestBean) (SerializationTestUtils.serializeAndDeserialize(bean)));
        Assert.assertNotNull(deserialized);
        Assert.assertTrue(AopUtils.isJdkDynamicProxy(deserialized));
        Assert.assertEquals(101, bean.getAge());
        Assert.assertTrue((deserialized instanceof ScopedObject));
        ScopedObject scopedDeserialized = ((ScopedObject) (deserialized));
        Assert.assertEquals(TestBean.class, scopedDeserialized.getTargetObject().getClass());
        bf.setSerializationId(null);
    }

    @Test
    public void testCglibScopedProxy() throws Exception {
        DefaultListableBeanFactory bf = new DefaultListableBeanFactory();
        new org.springframework.beans.factory.xml.XmlBeanDefinitionReader(bf).loadBeanDefinitions(ScopedProxyTests.LIST_CONTEXT);
        bf.setSerializationId("Y");
        SimpleMapScope scope = new SimpleMapScope();
        bf.registerScope("request", scope);
        TestBean tb = ((TestBean) (bf.getBean("testBean")));
        Assert.assertTrue(AopUtils.isCglibProxy(tb.getFriends()));
        Assert.assertTrue(((tb.getFriends()) instanceof ScopedObject));
        ScopedObject scoped = ((ScopedObject) (tb.getFriends()));
        Assert.assertEquals(ArrayList.class, scoped.getTargetObject().getClass());
        tb.getFriends().add("myFriend");
        Assert.assertTrue(scope.getMap().containsKey("scopedTarget.scopedList"));
        Assert.assertEquals(ArrayList.class, scope.getMap().get("scopedTarget.scopedList").getClass());
        ArrayList<?> deserialized = ((ArrayList<?>) (SerializationTestUtils.serializeAndDeserialize(tb.getFriends())));
        Assert.assertNotNull(deserialized);
        Assert.assertTrue(AopUtils.isCglibProxy(deserialized));
        Assert.assertTrue(deserialized.contains("myFriend"));
        Assert.assertTrue((deserialized instanceof ScopedObject));
        ScopedObject scopedDeserialized = ((ScopedObject) (deserialized));
        Assert.assertEquals(ArrayList.class, scopedDeserialized.getTargetObject().getClass());
        bf.setSerializationId(null);
    }
}

