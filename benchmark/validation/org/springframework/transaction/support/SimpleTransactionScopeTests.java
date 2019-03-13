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
package org.springframework.transaction.support;


import TransactionSynchronization.STATUS_COMMITTED;
import java.util.HashSet;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.tests.sample.beans.DerivedTestBean;
import org.springframework.tests.sample.beans.TestBean;
import org.springframework.tests.transaction.CallCountingTransactionManager;


/**
 *
 *
 * @author Juergen Hoeller
 */
public class SimpleTransactionScopeTests {
    @Test
    @SuppressWarnings("resource")
    public void getFromScope() throws Exception {
        GenericApplicationContext context = new GenericApplicationContext();
        context.getBeanFactory().registerScope("tx", new SimpleTransactionScope());
        GenericBeanDefinition bd1 = new GenericBeanDefinition();
        bd1.setBeanClass(TestBean.class);
        bd1.setScope("tx");
        bd1.setPrimary(true);
        context.registerBeanDefinition("txScopedObject1", bd1);
        GenericBeanDefinition bd2 = new GenericBeanDefinition();
        bd2.setBeanClass(DerivedTestBean.class);
        bd2.setScope("tx");
        context.registerBeanDefinition("txScopedObject2", bd2);
        context.refresh();
        try {
            context.getBean(TestBean.class);
            Assert.fail("Should have thrown BeanCreationException");
        } catch (BeanCreationException ex) {
            // expected - no synchronization active
            Assert.assertTrue(((ex.getCause()) instanceof IllegalStateException));
        }
        try {
            context.getBean(DerivedTestBean.class);
            Assert.fail("Should have thrown BeanCreationException");
        } catch (BeanCreationException ex) {
            // expected - no synchronization active
            Assert.assertTrue(((ex.getCause()) instanceof IllegalStateException));
        }
        TestBean bean1 = null;
        DerivedTestBean bean2 = null;
        DerivedTestBean bean2a = null;
        DerivedTestBean bean2b = null;
        TransactionSynchronizationManager.initSynchronization();
        try {
            bean1 = context.getBean(TestBean.class);
            Assert.assertSame(bean1, context.getBean(TestBean.class));
            bean2 = context.getBean(DerivedTestBean.class);
            Assert.assertSame(bean2, context.getBean(DerivedTestBean.class));
            context.getBeanFactory().destroyScopedBean("txScopedObject2");
            Assert.assertFalse(TransactionSynchronizationManager.hasResource("txScopedObject2"));
            Assert.assertTrue(bean2.wasDestroyed());
            bean2a = context.getBean(DerivedTestBean.class);
            Assert.assertSame(bean2a, context.getBean(DerivedTestBean.class));
            Assert.assertNotSame(bean2, bean2a);
            context.getBeanFactory().getRegisteredScope("tx").remove("txScopedObject2");
            Assert.assertFalse(TransactionSynchronizationManager.hasResource("txScopedObject2"));
            Assert.assertFalse(bean2a.wasDestroyed());
            bean2b = context.getBean(DerivedTestBean.class);
            Assert.assertSame(bean2b, context.getBean(DerivedTestBean.class));
            Assert.assertNotSame(bean2, bean2b);
            Assert.assertNotSame(bean2a, bean2b);
        } finally {
            TransactionSynchronizationUtils.triggerAfterCompletion(STATUS_COMMITTED);
            TransactionSynchronizationManager.clearSynchronization();
        }
        Assert.assertFalse(bean2a.wasDestroyed());
        Assert.assertTrue(bean2b.wasDestroyed());
        Assert.assertTrue(TransactionSynchronizationManager.getResourceMap().isEmpty());
        try {
            context.getBean(TestBean.class);
            Assert.fail("Should have thrown IllegalStateException");
        } catch (BeanCreationException ex) {
            // expected - no synchronization active
            Assert.assertTrue(((ex.getCause()) instanceof IllegalStateException));
        }
        try {
            context.getBean(DerivedTestBean.class);
            Assert.fail("Should have thrown IllegalStateException");
        } catch (BeanCreationException ex) {
            // expected - no synchronization active
            Assert.assertTrue(((ex.getCause()) instanceof IllegalStateException));
        }
    }

    @Test
    public void getWithTransactionManager() throws Exception {
        try (GenericApplicationContext context = new GenericApplicationContext()) {
            context.getBeanFactory().registerScope("tx", new SimpleTransactionScope());
            GenericBeanDefinition bd1 = new GenericBeanDefinition();
            bd1.setBeanClass(TestBean.class);
            bd1.setScope("tx");
            bd1.setPrimary(true);
            context.registerBeanDefinition("txScopedObject1", bd1);
            GenericBeanDefinition bd2 = new GenericBeanDefinition();
            bd2.setBeanClass(DerivedTestBean.class);
            bd2.setScope("tx");
            context.registerBeanDefinition("txScopedObject2", bd2);
            context.refresh();
            CallCountingTransactionManager tm = new CallCountingTransactionManager();
            TransactionTemplate tt = new TransactionTemplate(tm);
            Set<DerivedTestBean> finallyDestroy = new HashSet<>();
            tt.execute(( status) -> {
                TestBean bean1 = context.getBean(.class);
                assertSame(bean1, context.getBean(.class));
                DerivedTestBean bean2 = context.getBean(.class);
                assertSame(bean2, context.getBean(.class));
                context.getBeanFactory().destroyScopedBean("txScopedObject2");
                assertFalse(TransactionSynchronizationManager.hasResource("txScopedObject2"));
                assertTrue(bean2.wasDestroyed());
                DerivedTestBean bean2a = context.getBean(.class);
                assertSame(bean2a, context.getBean(.class));
                assertNotSame(bean2, bean2a);
                context.getBeanFactory().getRegisteredScope("tx").remove("txScopedObject2");
                assertFalse(TransactionSynchronizationManager.hasResource("txScopedObject2"));
                assertFalse(bean2a.wasDestroyed());
                DerivedTestBean bean2b = context.getBean(.class);
                finallyDestroy.add(bean2b);
                assertSame(bean2b, context.getBean(.class));
                assertNotSame(bean2, bean2b);
                assertNotSame(bean2a, bean2b);
                Set<DerivedTestBean> immediatelyDestroy = new HashSet<>();
                TransactionTemplate tt2 = new TransactionTemplate(tm);
                tt2.setPropagationBehavior(TransactionTemplate.PROPAGATION_REQUIRED);
                tt2.execute(( status2) -> {
                    DerivedTestBean bean2c = context.getBean(.class);
                    immediatelyDestroy.add(bean2c);
                    assertSame(bean2c, context.getBean(.class));
                    assertNotSame(bean2, bean2c);
                    assertNotSame(bean2a, bean2c);
                    assertNotSame(bean2b, bean2c);
                    return null;
                });
                assertTrue(immediatelyDestroy.iterator().next().wasDestroyed());
                assertFalse(bean2b.wasDestroyed());
                return null;
            });
            Assert.assertTrue(finallyDestroy.iterator().next().wasDestroyed());
        }
    }
}

