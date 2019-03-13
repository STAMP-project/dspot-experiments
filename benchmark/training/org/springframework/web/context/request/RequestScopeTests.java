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


import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.BeanCurrentlyInCreationException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.mock.web.test.MockHttpServletRequest;
import org.springframework.tests.sample.beans.DerivedTestBean;
import org.springframework.tests.sample.beans.TestBean;


/**
 *
 *
 * @author Rob Harrop
 * @author Juergen Hoeller
 * @author Mark Fisher
 * @author Sam Brannen
 * @see SessionScopeTests
 */
public class RequestScopeTests {
    private final DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();

    @Test
    public void getFromScope() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setContextPath("/path");
        RequestAttributes requestAttributes = new ServletRequestAttributes(request);
        RequestContextHolder.setRequestAttributes(requestAttributes);
        String name = "requestScopedObject";
        Assert.assertNull(request.getAttribute(name));
        TestBean bean = ((TestBean) (this.beanFactory.getBean(name)));
        Assert.assertEquals("/path", bean.getName());
        Assert.assertSame(bean, request.getAttribute(name));
        Assert.assertSame(bean, this.beanFactory.getBean(name));
    }

    @Test
    public void destructionAtRequestCompletion() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        ServletRequestAttributes requestAttributes = new ServletRequestAttributes(request);
        RequestContextHolder.setRequestAttributes(requestAttributes);
        String name = "requestScopedDisposableObject";
        Assert.assertNull(request.getAttribute(name));
        DerivedTestBean bean = ((DerivedTestBean) (this.beanFactory.getBean(name)));
        Assert.assertSame(bean, request.getAttribute(name));
        Assert.assertSame(bean, this.beanFactory.getBean(name));
        requestAttributes.requestCompleted();
        Assert.assertTrue(bean.wasDestroyed());
    }

    @Test
    public void getFromFactoryBeanInScope() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        RequestAttributes requestAttributes = new ServletRequestAttributes(request);
        RequestContextHolder.setRequestAttributes(requestAttributes);
        String name = "requestScopedFactoryBean";
        Assert.assertNull(request.getAttribute(name));
        TestBean bean = ((TestBean) (this.beanFactory.getBean(name)));
        Assert.assertTrue(((request.getAttribute(name)) instanceof FactoryBean));
        Assert.assertSame(bean, this.beanFactory.getBean(name));
    }

    @Test
    public void circleLeadsToException() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        RequestAttributes requestAttributes = new ServletRequestAttributes(request);
        RequestContextHolder.setRequestAttributes(requestAttributes);
        try {
            String name = "requestScopedObjectCircle1";
            Assert.assertNull(request.getAttribute(name));
            this.beanFactory.getBean(name);
            Assert.fail("Should have thrown BeanCreationException");
        } catch (BeanCreationException ex) {
            Assert.assertTrue(ex.contains(BeanCurrentlyInCreationException.class));
        }
    }

    @Test
    public void innerBeanInheritsContainingBeanScopeByDefault() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        ServletRequestAttributes requestAttributes = new ServletRequestAttributes(request);
        RequestContextHolder.setRequestAttributes(requestAttributes);
        String outerBeanName = "requestScopedOuterBean";
        Assert.assertNull(request.getAttribute(outerBeanName));
        TestBean outer1 = ((TestBean) (this.beanFactory.getBean(outerBeanName)));
        Assert.assertNotNull(request.getAttribute(outerBeanName));
        TestBean inner1 = ((TestBean) (outer1.getSpouse()));
        Assert.assertSame(outer1, this.beanFactory.getBean(outerBeanName));
        requestAttributes.requestCompleted();
        Assert.assertTrue(outer1.wasDestroyed());
        Assert.assertTrue(inner1.wasDestroyed());
        request = new MockHttpServletRequest();
        requestAttributes = new ServletRequestAttributes(request);
        RequestContextHolder.setRequestAttributes(requestAttributes);
        TestBean outer2 = ((TestBean) (this.beanFactory.getBean(outerBeanName)));
        Assert.assertNotSame(outer1, outer2);
        Assert.assertNotSame(inner1, outer2.getSpouse());
    }

    @Test
    public void requestScopedInnerBeanDestroyedWhileContainedBySingleton() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        ServletRequestAttributes requestAttributes = new ServletRequestAttributes(request);
        RequestContextHolder.setRequestAttributes(requestAttributes);
        String outerBeanName = "singletonOuterBean";
        TestBean outer1 = ((TestBean) (this.beanFactory.getBean(outerBeanName)));
        Assert.assertNull(request.getAttribute(outerBeanName));
        TestBean inner1 = ((TestBean) (outer1.getSpouse()));
        TestBean outer2 = ((TestBean) (this.beanFactory.getBean(outerBeanName)));
        Assert.assertSame(outer1, outer2);
        Assert.assertSame(inner1, outer2.getSpouse());
        requestAttributes.requestCompleted();
        Assert.assertTrue(inner1.wasDestroyed());
        Assert.assertFalse(outer1.wasDestroyed());
    }
}

