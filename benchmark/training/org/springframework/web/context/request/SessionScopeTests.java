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


import java.io.Serializable;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.config.DestructionAwareBeanPostProcessor;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.mock.web.test.MockHttpServletRequest;
import org.springframework.mock.web.test.MockHttpSession;
import org.springframework.tests.sample.beans.DerivedTestBean;
import org.springframework.tests.sample.beans.TestBean;


/**
 *
 *
 * @author Rob Harrop
 * @author Juergen Hoeller
 * @author Sam Brannen
 * @see RequestScopeTests
 */
public class SessionScopeTests {
    private final DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();

    @Test
    public void getFromScope() throws Exception {
        AtomicInteger count = new AtomicInteger();
        MockHttpSession session = new MockHttpSession() {
            @Override
            public void setAttribute(String name, Object value) {
                super.setAttribute(name, value);
                count.incrementAndGet();
            }
        };
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setSession(session);
        ServletRequestAttributes requestAttributes = new ServletRequestAttributes(request);
        RequestContextHolder.setRequestAttributes(requestAttributes);
        String name = "sessionScopedObject";
        Assert.assertNull(session.getAttribute(name));
        TestBean bean = ((TestBean) (this.beanFactory.getBean(name)));
        Assert.assertEquals(1, count.intValue());
        Assert.assertEquals(session.getAttribute(name), bean);
        Assert.assertSame(bean, this.beanFactory.getBean(name));
        Assert.assertEquals(1, count.intValue());
        // should re-propagate updated attribute
        requestAttributes.requestCompleted();
        Assert.assertEquals(session.getAttribute(name), bean);
        Assert.assertEquals(2, count.intValue());
    }

    @Test
    public void getFromScopeWithSingleAccess() throws Exception {
        AtomicInteger count = new AtomicInteger();
        MockHttpSession session = new MockHttpSession() {
            @Override
            public void setAttribute(String name, Object value) {
                super.setAttribute(name, value);
                count.incrementAndGet();
            }
        };
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setSession(session);
        ServletRequestAttributes requestAttributes = new ServletRequestAttributes(request);
        RequestContextHolder.setRequestAttributes(requestAttributes);
        String name = "sessionScopedObject";
        Assert.assertNull(session.getAttribute(name));
        TestBean bean = ((TestBean) (this.beanFactory.getBean(name)));
        Assert.assertEquals(1, count.intValue());
        // should re-propagate updated attribute
        requestAttributes.requestCompleted();
        Assert.assertEquals(session.getAttribute(name), bean);
        Assert.assertEquals(2, count.intValue());
    }

    @Test
    public void destructionAtSessionTermination() throws Exception {
        MockHttpSession session = new MockHttpSession();
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setSession(session);
        ServletRequestAttributes requestAttributes = new ServletRequestAttributes(request);
        RequestContextHolder.setRequestAttributes(requestAttributes);
        String name = "sessionScopedDisposableObject";
        Assert.assertNull(session.getAttribute(name));
        DerivedTestBean bean = ((DerivedTestBean) (this.beanFactory.getBean(name)));
        Assert.assertEquals(session.getAttribute(name), bean);
        Assert.assertSame(bean, this.beanFactory.getBean(name));
        requestAttributes.requestCompleted();
        session.invalidate();
        Assert.assertTrue(bean.wasDestroyed());
    }

    @Test
    public void destructionWithSessionSerialization() throws Exception {
        doTestDestructionWithSessionSerialization(false);
    }

    @Test
    public void destructionWithSessionSerializationAndBeanPostProcessor() throws Exception {
        this.beanFactory.addBeanPostProcessor(new SessionScopeTests.CustomDestructionAwareBeanPostProcessor());
        doTestDestructionWithSessionSerialization(false);
    }

    @Test
    public void destructionWithSessionSerializationAndSerializableBeanPostProcessor() throws Exception {
        this.beanFactory.addBeanPostProcessor(new SessionScopeTests.CustomSerializableDestructionAwareBeanPostProcessor());
        doTestDestructionWithSessionSerialization(true);
    }

    private static class CustomDestructionAwareBeanPostProcessor implements DestructionAwareBeanPostProcessor {
        @Override
        public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
            return bean;
        }

        @Override
        public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
            return bean;
        }

        @Override
        public void postProcessBeforeDestruction(Object bean, String beanName) throws BeansException {
        }

        @Override
        public boolean requiresDestruction(Object bean) {
            return true;
        }
    }

    @SuppressWarnings("serial")
    private static class CustomSerializableDestructionAwareBeanPostProcessor implements Serializable , DestructionAwareBeanPostProcessor {
        @Override
        public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
            return bean;
        }

        @Override
        public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
            return bean;
        }

        @Override
        public void postProcessBeforeDestruction(Object bean, String beanName) throws BeansException {
            if (bean instanceof BeanNameAware) {
                setBeanName(null);
            }
        }

        @Override
        public boolean requiresDestruction(Object bean) {
            return true;
        }
    }
}

