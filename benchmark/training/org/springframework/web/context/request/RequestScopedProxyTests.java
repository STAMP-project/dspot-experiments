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
package org.springframework.web.context.request;


import DummyFactory.SINGLETON_NAME;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.mock.web.test.MockHttpServletRequest;
import org.springframework.tests.sample.beans.DerivedTestBean;
import org.springframework.tests.sample.beans.ITestBean;
import org.springframework.tests.sample.beans.TestBean;
import org.springframework.tests.sample.beans.factory.DummyFactory;


/**
 *
 *
 * @author Juergen Hoeller
 */
public class RequestScopedProxyTests {
    private final DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();

    @Test
    public void testGetFromScope() throws Exception {
        String name = "requestScopedObject";
        TestBean bean = ((TestBean) (this.beanFactory.getBean(name)));
        Assert.assertTrue(AopUtils.isCglibProxy(bean));
        MockHttpServletRequest request = new MockHttpServletRequest();
        RequestAttributes requestAttributes = new ServletRequestAttributes(request);
        RequestContextHolder.setRequestAttributes(requestAttributes);
        try {
            Assert.assertNull(request.getAttribute(("scopedTarget." + name)));
            Assert.assertEquals("scoped", bean.getName());
            Assert.assertNotNull(request.getAttribute(("scopedTarget." + name)));
            TestBean target = ((TestBean) (request.getAttribute(("scopedTarget." + name))));
            Assert.assertEquals(TestBean.class, target.getClass());
            Assert.assertEquals("scoped", target.getName());
            Assert.assertSame(bean, this.beanFactory.getBean(name));
            Assert.assertEquals(bean.toString(), target.toString());
        } finally {
            RequestContextHolder.setRequestAttributes(null);
        }
    }

    @Test
    public void testGetFromScopeThroughDynamicProxy() throws Exception {
        String name = "requestScopedProxy";
        ITestBean bean = ((ITestBean) (this.beanFactory.getBean(name)));
        // assertTrue(AopUtils.isJdkDynamicProxy(bean));
        MockHttpServletRequest request = new MockHttpServletRequest();
        RequestAttributes requestAttributes = new ServletRequestAttributes(request);
        RequestContextHolder.setRequestAttributes(requestAttributes);
        try {
            Assert.assertNull(request.getAttribute(("scopedTarget." + name)));
            Assert.assertEquals("scoped", bean.getName());
            Assert.assertNotNull(request.getAttribute(("scopedTarget." + name)));
            TestBean target = ((TestBean) (request.getAttribute(("scopedTarget." + name))));
            Assert.assertEquals(TestBean.class, target.getClass());
            Assert.assertEquals("scoped", target.getName());
            Assert.assertSame(bean, this.beanFactory.getBean(name));
            Assert.assertEquals(bean.toString(), target.toString());
        } finally {
            RequestContextHolder.setRequestAttributes(null);
        }
    }

    @Test
    public void testDestructionAtRequestCompletion() throws Exception {
        String name = "requestScopedDisposableObject";
        DerivedTestBean bean = ((DerivedTestBean) (this.beanFactory.getBean(name)));
        Assert.assertTrue(AopUtils.isCglibProxy(bean));
        MockHttpServletRequest request = new MockHttpServletRequest();
        ServletRequestAttributes requestAttributes = new ServletRequestAttributes(request);
        RequestContextHolder.setRequestAttributes(requestAttributes);
        try {
            Assert.assertNull(request.getAttribute(("scopedTarget." + name)));
            Assert.assertEquals("scoped", bean.getName());
            Assert.assertNotNull(request.getAttribute(("scopedTarget." + name)));
            Assert.assertEquals(DerivedTestBean.class, request.getAttribute(("scopedTarget." + name)).getClass());
            Assert.assertEquals("scoped", getName());
            Assert.assertSame(bean, this.beanFactory.getBean(name));
            requestAttributes.requestCompleted();
            Assert.assertTrue(wasDestroyed());
        } finally {
            RequestContextHolder.setRequestAttributes(null);
        }
    }

    @Test
    public void testGetFromFactoryBeanInScope() throws Exception {
        String name = "requestScopedFactoryBean";
        TestBean bean = ((TestBean) (this.beanFactory.getBean(name)));
        Assert.assertTrue(AopUtils.isCglibProxy(bean));
        MockHttpServletRequest request = new MockHttpServletRequest();
        RequestAttributes requestAttributes = new ServletRequestAttributes(request);
        RequestContextHolder.setRequestAttributes(requestAttributes);
        try {
            Assert.assertNull(request.getAttribute(("scopedTarget." + name)));
            Assert.assertEquals(SINGLETON_NAME, bean.getName());
            Assert.assertNotNull(request.getAttribute(("scopedTarget." + name)));
            Assert.assertEquals(DummyFactory.class, request.getAttribute(("scopedTarget." + name)).getClass());
            Assert.assertSame(bean, this.beanFactory.getBean(name));
        } finally {
            RequestContextHolder.setRequestAttributes(null);
        }
    }

    @Test
    public void testGetInnerBeanFromScope() throws Exception {
        TestBean bean = ((TestBean) (this.beanFactory.getBean("outerBean")));
        Assert.assertFalse(AopUtils.isAopProxy(bean));
        Assert.assertTrue(AopUtils.isCglibProxy(bean.getSpouse()));
        String name = "scopedInnerBean";
        MockHttpServletRequest request = new MockHttpServletRequest();
        RequestAttributes requestAttributes = new ServletRequestAttributes(request);
        RequestContextHolder.setRequestAttributes(requestAttributes);
        try {
            Assert.assertNull(request.getAttribute(("scopedTarget." + name)));
            Assert.assertEquals("scoped", getName());
            Assert.assertNotNull(request.getAttribute(("scopedTarget." + name)));
            Assert.assertEquals(TestBean.class, request.getAttribute(("scopedTarget." + name)).getClass());
            Assert.assertEquals("scoped", getName());
        } finally {
            RequestContextHolder.setRequestAttributes(null);
        }
    }

    @Test
    public void testGetAnonymousInnerBeanFromScope() throws Exception {
        TestBean bean = ((TestBean) (this.beanFactory.getBean("outerBean")));
        Assert.assertFalse(AopUtils.isAopProxy(bean));
        Assert.assertTrue(AopUtils.isCglibProxy(bean.getSpouse()));
        BeanDefinition beanDef = this.beanFactory.getBeanDefinition("outerBean");
        BeanDefinitionHolder innerBeanDef = ((BeanDefinitionHolder) (beanDef.getPropertyValues().getPropertyValue("spouse").getValue()));
        String name = innerBeanDef.getBeanName();
        MockHttpServletRequest request = new MockHttpServletRequest();
        RequestAttributes requestAttributes = new ServletRequestAttributes(request);
        RequestContextHolder.setRequestAttributes(requestAttributes);
        try {
            Assert.assertNull(request.getAttribute(("scopedTarget." + name)));
            Assert.assertEquals("scoped", getName());
            Assert.assertNotNull(request.getAttribute(("scopedTarget." + name)));
            Assert.assertEquals(TestBean.class, request.getAttribute(("scopedTarget." + name)).getClass());
            Assert.assertEquals("scoped", getName());
        } finally {
            RequestContextHolder.setRequestAttributes(null);
        }
    }
}

