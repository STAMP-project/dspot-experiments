/**
 * Copyright 2002-2019 the original author or authors.
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
package org.springframework.orm.jpa.hibernate;


import org.hibernate.resource.beans.container.spi.BeanContainer;
import org.hibernate.resource.beans.container.spi.ContainedBean;
import org.hibernate.resource.beans.spi.BeanInstanceProducer;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.orm.jpa.AbstractEntityManagerFactoryIntegrationTests;
import org.springframework.orm.jpa.hibernate.beans.BeanSource;
import org.springframework.orm.jpa.hibernate.beans.MultiplePrototypesInSpringContextTestBean;
import org.springframework.orm.jpa.hibernate.beans.NoDefinitionInSpringContextTestBean;
import org.springframework.orm.jpa.hibernate.beans.SinglePrototypeInSpringContextTestBean;


/**
 * Hibernate-specific SpringBeanContainer integration tests.
 *
 * @author Yoann Rodiere
 */
public class HibernateNativeEntityManagerFactorySpringBeanContainerIntegrationTests extends AbstractEntityManagerFactoryIntegrationTests {
    @Autowired
    private ApplicationContext applicationContext;

    @Test
    public void testCanRetrieveBeanByTypeWithJpaCompliantOptions() {
        BeanContainer beanContainer = getBeanContainer();
        Assert.assertNotNull(beanContainer);
        ContainedBean<SinglePrototypeInSpringContextTestBean> bean = beanContainer.getBean(SinglePrototypeInSpringContextTestBean.class, HibernateNativeEntityManagerFactorySpringBeanContainerIntegrationTests.JpaLifecycleOptions.INSTANCE, HibernateNativeEntityManagerFactorySpringBeanContainerIntegrationTests.IneffectiveBeanInstanceProducer.INSTANCE);
        Assert.assertNotNull(bean);
        SinglePrototypeInSpringContextTestBean instance = bean.getBeanInstance();
        Assert.assertNotNull(instance);
        Assert.assertSame(applicationContext, instance.getApplicationContext());
    }

    @Test
    public void testCanRetrieveBeanByNameWithJpaCompliantOptions() {
        BeanContainer beanContainer = getBeanContainer();
        Assert.assertNotNull(beanContainer);
        ContainedBean<MultiplePrototypesInSpringContextTestBean> bean = beanContainer.getBean("multiple-1", MultiplePrototypesInSpringContextTestBean.class, HibernateNativeEntityManagerFactorySpringBeanContainerIntegrationTests.JpaLifecycleOptions.INSTANCE, HibernateNativeEntityManagerFactorySpringBeanContainerIntegrationTests.IneffectiveBeanInstanceProducer.INSTANCE);
        Assert.assertNotNull(bean);
        MultiplePrototypesInSpringContextTestBean instance = bean.getBeanInstance();
        Assert.assertNotNull(instance);
        Assert.assertEquals("multiple-1", instance.getName());
        Assert.assertSame(applicationContext, instance.getApplicationContext());
    }

    @Test
    public void testCanRetrieveBeanByTypeWithNativeOptions() {
        BeanContainer beanContainer = getBeanContainer();
        Assert.assertNotNull(beanContainer);
        ContainedBean<SinglePrototypeInSpringContextTestBean> bean = beanContainer.getBean(SinglePrototypeInSpringContextTestBean.class, HibernateNativeEntityManagerFactorySpringBeanContainerIntegrationTests.NativeLifecycleOptions.INSTANCE, HibernateNativeEntityManagerFactorySpringBeanContainerIntegrationTests.IneffectiveBeanInstanceProducer.INSTANCE);
        Assert.assertNotNull(bean);
        SinglePrototypeInSpringContextTestBean instance = bean.getBeanInstance();
        Assert.assertNotNull(instance);
        Assert.assertEquals("single", instance.getName());
        Assert.assertSame(applicationContext, instance.getApplicationContext());
        ContainedBean<SinglePrototypeInSpringContextTestBean> bean2 = beanContainer.getBean(SinglePrototypeInSpringContextTestBean.class, HibernateNativeEntityManagerFactorySpringBeanContainerIntegrationTests.NativeLifecycleOptions.INSTANCE, HibernateNativeEntityManagerFactorySpringBeanContainerIntegrationTests.IneffectiveBeanInstanceProducer.INSTANCE);
        Assert.assertNotNull(bean2);
        SinglePrototypeInSpringContextTestBean instance2 = bean2.getBeanInstance();
        Assert.assertNotNull(instance2);
        // Due to the lifecycle options, and because the bean has the "prototype" scope, we should not return the same instance
        Assert.assertNotSame(instance, instance2);
    }

    @Test
    public void testCanRetrieveBeanByNameWithNativeOptions() {
        BeanContainer beanContainer = getBeanContainer();
        Assert.assertNotNull(beanContainer);
        ContainedBean<MultiplePrototypesInSpringContextTestBean> bean = beanContainer.getBean("multiple-1", MultiplePrototypesInSpringContextTestBean.class, HibernateNativeEntityManagerFactorySpringBeanContainerIntegrationTests.NativeLifecycleOptions.INSTANCE, HibernateNativeEntityManagerFactorySpringBeanContainerIntegrationTests.IneffectiveBeanInstanceProducer.INSTANCE);
        Assert.assertNotNull(bean);
        MultiplePrototypesInSpringContextTestBean instance = bean.getBeanInstance();
        Assert.assertNotNull(instance);
        Assert.assertEquals("multiple-1", instance.getName());
        Assert.assertSame(applicationContext, instance.getApplicationContext());
        ContainedBean<MultiplePrototypesInSpringContextTestBean> bean2 = beanContainer.getBean("multiple-1", MultiplePrototypesInSpringContextTestBean.class, HibernateNativeEntityManagerFactorySpringBeanContainerIntegrationTests.NativeLifecycleOptions.INSTANCE, HibernateNativeEntityManagerFactorySpringBeanContainerIntegrationTests.IneffectiveBeanInstanceProducer.INSTANCE);
        Assert.assertNotNull(bean2);
        MultiplePrototypesInSpringContextTestBean instance2 = bean2.getBeanInstance();
        Assert.assertNotNull(instance2);
        // Due to the lifecycle options, and because the bean has the "prototype" scope, we should not return the same instance
        Assert.assertNotSame(instance, instance2);
    }

    @Test
    public void testCanRetrieveFallbackBeanByTypeWithJpaCompliantOptions() {
        BeanContainer beanContainer = getBeanContainer();
        Assert.assertNotNull(beanContainer);
        HibernateNativeEntityManagerFactorySpringBeanContainerIntegrationTests.NoDefinitionInSpringContextTestBeanInstanceProducer fallbackProducer = new HibernateNativeEntityManagerFactorySpringBeanContainerIntegrationTests.NoDefinitionInSpringContextTestBeanInstanceProducer();
        ContainedBean<NoDefinitionInSpringContextTestBean> bean = beanContainer.getBean(NoDefinitionInSpringContextTestBean.class, HibernateNativeEntityManagerFactorySpringBeanContainerIntegrationTests.JpaLifecycleOptions.INSTANCE, fallbackProducer);
        Assert.assertEquals(1, fallbackProducer.currentUnnamedInstantiationCount());
        Assert.assertEquals(0, fallbackProducer.currentNamedInstantiationCount());
        Assert.assertNotNull(bean);
        NoDefinitionInSpringContextTestBean instance = bean.getBeanInstance();
        Assert.assertNotNull(instance);
        Assert.assertEquals(BeanSource.FALLBACK, instance.getSource());
        Assert.assertNull(instance.getApplicationContext());
    }

    @Test
    public void testCanRetrieveFallbackBeanByNameWithJpaCompliantOptions() {
        BeanContainer beanContainer = getBeanContainer();
        Assert.assertNotNull(beanContainer);
        HibernateNativeEntityManagerFactorySpringBeanContainerIntegrationTests.NoDefinitionInSpringContextTestBeanInstanceProducer fallbackProducer = new HibernateNativeEntityManagerFactorySpringBeanContainerIntegrationTests.NoDefinitionInSpringContextTestBeanInstanceProducer();
        ContainedBean<NoDefinitionInSpringContextTestBean> bean = beanContainer.getBean("some name", NoDefinitionInSpringContextTestBean.class, HibernateNativeEntityManagerFactorySpringBeanContainerIntegrationTests.JpaLifecycleOptions.INSTANCE, fallbackProducer);
        Assert.assertEquals(0, fallbackProducer.currentUnnamedInstantiationCount());
        Assert.assertEquals(1, fallbackProducer.currentNamedInstantiationCount());
        Assert.assertNotNull(bean);
        NoDefinitionInSpringContextTestBean instance = bean.getBeanInstance();
        Assert.assertNotNull(instance);
        Assert.assertEquals(BeanSource.FALLBACK, instance.getSource());
        Assert.assertEquals("some name", instance.getName());
        Assert.assertNull(instance.getApplicationContext());
    }

    @Test
    public void testCanRetrieveFallbackBeanByTypeWithNativeOptions() {
        BeanContainer beanContainer = getBeanContainer();
        Assert.assertNotNull(beanContainer);
        HibernateNativeEntityManagerFactorySpringBeanContainerIntegrationTests.NoDefinitionInSpringContextTestBeanInstanceProducer fallbackProducer = new HibernateNativeEntityManagerFactorySpringBeanContainerIntegrationTests.NoDefinitionInSpringContextTestBeanInstanceProducer();
        ContainedBean<NoDefinitionInSpringContextTestBean> bean = beanContainer.getBean(NoDefinitionInSpringContextTestBean.class, HibernateNativeEntityManagerFactorySpringBeanContainerIntegrationTests.NativeLifecycleOptions.INSTANCE, fallbackProducer);
        Assert.assertEquals(1, fallbackProducer.currentUnnamedInstantiationCount());
        Assert.assertEquals(0, fallbackProducer.currentNamedInstantiationCount());
        Assert.assertNotNull(bean);
        NoDefinitionInSpringContextTestBean instance = bean.getBeanInstance();
        Assert.assertNotNull(instance);
        Assert.assertEquals(BeanSource.FALLBACK, instance.getSource());
        Assert.assertNull(instance.getApplicationContext());
    }

    @Test
    public void testCanRetrieveFallbackBeanByNameWithNativeOptions() {
        BeanContainer beanContainer = getBeanContainer();
        Assert.assertNotNull(beanContainer);
        HibernateNativeEntityManagerFactorySpringBeanContainerIntegrationTests.NoDefinitionInSpringContextTestBeanInstanceProducer fallbackProducer = new HibernateNativeEntityManagerFactorySpringBeanContainerIntegrationTests.NoDefinitionInSpringContextTestBeanInstanceProducer();
        ContainedBean<NoDefinitionInSpringContextTestBean> bean = beanContainer.getBean("some name", NoDefinitionInSpringContextTestBean.class, HibernateNativeEntityManagerFactorySpringBeanContainerIntegrationTests.NativeLifecycleOptions.INSTANCE, fallbackProducer);
        Assert.assertEquals(0, fallbackProducer.currentUnnamedInstantiationCount());
        Assert.assertEquals(1, fallbackProducer.currentNamedInstantiationCount());
        Assert.assertNotNull(bean);
        NoDefinitionInSpringContextTestBean instance = bean.getBeanInstance();
        Assert.assertNotNull(instance);
        Assert.assertEquals(BeanSource.FALLBACK, instance.getSource());
        Assert.assertEquals("some name", instance.getName());
        Assert.assertNull(instance.getApplicationContext());
    }

    /**
     * The lifecycle options mandated by the JPA spec and used as a default in Hibernate ORM.
     */
    private static class JpaLifecycleOptions implements BeanContainer.LifecycleOptions {
        public static final HibernateNativeEntityManagerFactorySpringBeanContainerIntegrationTests.JpaLifecycleOptions INSTANCE = new HibernateNativeEntityManagerFactorySpringBeanContainerIntegrationTests.JpaLifecycleOptions();

        @Override
        public boolean canUseCachedReferences() {
            return true;
        }

        @Override
        public boolean useJpaCompliantCreation() {
            return true;
        }
    }

    /**
     * The lifecycle options used by libraries integrating into Hibernate ORM
     * and that want a behavior closer to Spring's native behavior,
     * such as Hibernate Search.
     */
    private static class NativeLifecycleOptions implements BeanContainer.LifecycleOptions {
        public static final HibernateNativeEntityManagerFactorySpringBeanContainerIntegrationTests.NativeLifecycleOptions INSTANCE = new HibernateNativeEntityManagerFactorySpringBeanContainerIntegrationTests.NativeLifecycleOptions();

        @Override
        public boolean canUseCachedReferences() {
            return false;
        }

        @Override
        public boolean useJpaCompliantCreation() {
            return false;
        }
    }

    private static class IneffectiveBeanInstanceProducer implements BeanInstanceProducer {
        public static final HibernateNativeEntityManagerFactorySpringBeanContainerIntegrationTests.IneffectiveBeanInstanceProducer INSTANCE = new HibernateNativeEntityManagerFactorySpringBeanContainerIntegrationTests.IneffectiveBeanInstanceProducer();

        @Override
        public <B> B produceBeanInstance(Class<B> aClass) {
            throw new UnsupportedOperationException("should not be called");
        }

        @Override
        public <B> B produceBeanInstance(String s, Class<B> aClass) {
            throw new UnsupportedOperationException("should not be called");
        }
    }

    private static class NoDefinitionInSpringContextTestBeanInstanceProducer implements BeanInstanceProducer {
        private int unnamedInstantiationCount = 0;

        private int namedInstantiationCount = 0;

        @Override
        public <B> B produceBeanInstance(Class<B> beanType) {
            try {
                ++(unnamedInstantiationCount);
                /* We only expect to ever be asked to instantiate this class, so we just cut corners here.
                A real-world implementation would obviously be different.
                 */
                NoDefinitionInSpringContextTestBean instance = new NoDefinitionInSpringContextTestBean(null, BeanSource.FALLBACK);
                return beanType.cast(instance);
            } catch (RuntimeException e) {
                throw new AssertionError("Unexpected error instantiating a bean by type using reflection", e);
            }
        }

        @Override
        public <B> B produceBeanInstance(String name, Class<B> beanType) {
            try {
                ++(namedInstantiationCount);
                /* We only expect to ever be asked to instantiate this class, so we just cut corners here.
                A real-world implementation would obviously be different.
                 */
                NoDefinitionInSpringContextTestBean instance = new NoDefinitionInSpringContextTestBean(name, BeanSource.FALLBACK);
                return beanType.cast(instance);
            } catch (RuntimeException e) {
                throw new AssertionError("Unexpected error instantiating a bean by name using reflection", e);
            }
        }

        private int currentUnnamedInstantiationCount() {
            return unnamedInstantiationCount;
        }

        private int currentNamedInstantiationCount() {
            return namedInstantiationCount;
        }
    }
}

