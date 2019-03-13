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
package org.springframework.beans.factory;


import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;


/**
 *
 *
 * @author Rob Harrop
 * @author Juergen Hoeller
 * @author Chris Beams
 */
public class FactoryBeanTests {
    private static final Class<?> CLASS = FactoryBeanTests.class;

    private static final Resource RETURNS_NULL_CONTEXT = qualifiedResource(FactoryBeanTests.CLASS, "returnsNull.xml");

    private static final Resource WITH_AUTOWIRING_CONTEXT = qualifiedResource(FactoryBeanTests.CLASS, "withAutowiring.xml");

    private static final Resource ABSTRACT_CONTEXT = qualifiedResource(FactoryBeanTests.CLASS, "abstract.xml");

    private static final Resource CIRCULAR_CONTEXT = qualifiedResource(FactoryBeanTests.CLASS, "circular.xml");

    @Test
    public void testFactoryBeanReturnsNull() throws Exception {
        DefaultListableBeanFactory factory = new DefaultListableBeanFactory();
        new org.springframework.beans.factory.xml.XmlBeanDefinitionReader(factory).loadBeanDefinitions(FactoryBeanTests.RETURNS_NULL_CONTEXT);
        Assert.assertEquals("null", factory.getBean("factoryBean").toString());
    }

    @Test
    public void testFactoryBeansWithAutowiring() throws Exception {
        DefaultListableBeanFactory factory = new DefaultListableBeanFactory();
        new org.springframework.beans.factory.xml.XmlBeanDefinitionReader(factory).loadBeanDefinitions(FactoryBeanTests.WITH_AUTOWIRING_CONTEXT);
        BeanFactoryPostProcessor ppc = ((BeanFactoryPostProcessor) (factory.getBean("propertyPlaceholderConfigurer")));
        ppc.postProcessBeanFactory(factory);
        Assert.assertNull(factory.getType("betaFactory"));
        FactoryBeanTests.Alpha alpha = ((FactoryBeanTests.Alpha) (factory.getBean("alpha")));
        FactoryBeanTests.Beta beta = ((FactoryBeanTests.Beta) (factory.getBean("beta")));
        FactoryBeanTests.Gamma gamma = ((FactoryBeanTests.Gamma) (factory.getBean("gamma")));
        FactoryBeanTests.Gamma gamma2 = ((FactoryBeanTests.Gamma) (factory.getBean("gammaFactory")));
        Assert.assertSame(beta, alpha.getBeta());
        Assert.assertSame(gamma, beta.getGamma());
        Assert.assertSame(gamma2, beta.getGamma());
        Assert.assertEquals("yourName", beta.getName());
    }

    @Test
    public void testFactoryBeansWithIntermediateFactoryBeanAutowiringFailure() throws Exception {
        DefaultListableBeanFactory factory = new DefaultListableBeanFactory();
        new org.springframework.beans.factory.xml.XmlBeanDefinitionReader(factory).loadBeanDefinitions(FactoryBeanTests.WITH_AUTOWIRING_CONTEXT);
        BeanFactoryPostProcessor ppc = ((BeanFactoryPostProcessor) (factory.getBean("propertyPlaceholderConfigurer")));
        ppc.postProcessBeanFactory(factory);
        FactoryBeanTests.Beta beta = ((FactoryBeanTests.Beta) (factory.getBean("beta")));
        FactoryBeanTests.Alpha alpha = ((FactoryBeanTests.Alpha) (factory.getBean("alpha")));
        FactoryBeanTests.Gamma gamma = ((FactoryBeanTests.Gamma) (factory.getBean("gamma")));
        Assert.assertSame(beta, alpha.getBeta());
        Assert.assertSame(gamma, beta.getGamma());
    }

    @Test
    public void testAbstractFactoryBeanViaAnnotation() throws Exception {
        DefaultListableBeanFactory factory = new DefaultListableBeanFactory();
        new org.springframework.beans.factory.xml.XmlBeanDefinitionReader(factory).loadBeanDefinitions(FactoryBeanTests.ABSTRACT_CONTEXT);
        factory.getBeansWithAnnotation(Component.class);
    }

    @Test
    public void testAbstractFactoryBeanViaType() throws Exception {
        DefaultListableBeanFactory factory = new DefaultListableBeanFactory();
        new org.springframework.beans.factory.xml.XmlBeanDefinitionReader(factory).loadBeanDefinitions(FactoryBeanTests.ABSTRACT_CONTEXT);
        factory.getBeansOfType(FactoryBeanTests.AbstractFactoryBean.class);
    }

    @Test
    public void testCircularReferenceWithPostProcessor() {
        DefaultListableBeanFactory factory = new DefaultListableBeanFactory();
        new org.springframework.beans.factory.xml.XmlBeanDefinitionReader(factory).loadBeanDefinitions(FactoryBeanTests.CIRCULAR_CONTEXT);
        FactoryBeanTests.CountingPostProcessor counter = new FactoryBeanTests.CountingPostProcessor();
        factory.addBeanPostProcessor(counter);
        FactoryBeanTests.BeanImpl1 impl1 = factory.getBean(FactoryBeanTests.BeanImpl1.class);
        Assert.assertNotNull(impl1);
        Assert.assertNotNull(impl1.getImpl2());
        Assert.assertNotNull(impl1.getImpl2());
        Assert.assertSame(impl1, impl1.getImpl2().getImpl1());
        Assert.assertEquals(1, counter.getCount("bean1"));
        Assert.assertEquals(1, counter.getCount("bean2"));
    }

    public static class NullReturningFactoryBean implements FactoryBean<Object> {
        @Override
        public Object getObject() {
            return null;
        }

        @Override
        public Class<?> getObjectType() {
            return null;
        }

        @Override
        public boolean isSingleton() {
            return true;
        }
    }

    public static class Alpha implements InitializingBean {
        private FactoryBeanTests.Beta beta;

        public void setBeta(FactoryBeanTests.Beta beta) {
            this.beta = beta;
        }

        public FactoryBeanTests.Beta getBeta() {
            return beta;
        }

        @Override
        public void afterPropertiesSet() {
            org.springframework.util.Assert.notNull(beta, "'beta' property is required");
        }
    }

    public static class Beta implements InitializingBean {
        private FactoryBeanTests.Gamma gamma;

        private String name;

        public void setGamma(FactoryBeanTests.Gamma gamma) {
            this.gamma = gamma;
        }

        public FactoryBeanTests.Gamma getGamma() {
            return gamma;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        @Override
        public void afterPropertiesSet() {
            org.springframework.util.Assert.notNull(gamma, "'gamma' property is required");
        }
    }

    public static class Gamma {}

    @Component
    public static class BetaFactoryBean implements FactoryBean<Object> {
        public BetaFactoryBean(FactoryBeanTests.Alpha alpha) {
        }

        private FactoryBeanTests.Beta beta;

        public void setBeta(FactoryBeanTests.Beta beta) {
            this.beta = beta;
        }

        @Override
        public Object getObject() {
            return this.beta;
        }

        @Override
        public Class<?> getObjectType() {
            return null;
        }

        @Override
        public boolean isSingleton() {
            return true;
        }
    }

    public abstract static class AbstractFactoryBean implements FactoryBean<Object> {}

    public static class PassThroughFactoryBean<T> implements BeanFactoryAware , FactoryBean<T> {
        private Class<T> type;

        private String instanceName;

        private BeanFactory beanFactory;

        private T instance;

        public PassThroughFactoryBean(Class<T> type) {
            this.type = type;
        }

        public void setInstanceName(String instanceName) {
            this.instanceName = instanceName;
        }

        @Override
        public void setBeanFactory(BeanFactory beanFactory) {
            this.beanFactory = beanFactory;
        }

        @Override
        public T getObject() {
            if ((instance) == null) {
                instance = beanFactory.getBean(instanceName, type);
            }
            return instance;
        }

        @Override
        public Class<?> getObjectType() {
            return type;
        }

        @Override
        public boolean isSingleton() {
            return true;
        }
    }

    public static class CountingPostProcessor implements BeanPostProcessor {
        private final Map<String, AtomicInteger> count = new HashMap<>();

        @Override
        public Object postProcessBeforeInitialization(Object bean, String beanName) {
            return bean;
        }

        @Override
        public Object postProcessAfterInitialization(Object bean, String beanName) {
            if (bean instanceof FactoryBean) {
                return bean;
            }
            AtomicInteger c = count.get(beanName);
            if (c == null) {
                c = new AtomicInteger(0);
                count.put(beanName, c);
            }
            c.incrementAndGet();
            return bean;
        }

        public int getCount(String beanName) {
            AtomicInteger c = count.get(beanName);
            if (c != null) {
                return c.intValue();
            } else {
                return 0;
            }
        }
    }

    public static class BeanImpl1 {
        private FactoryBeanTests.BeanImpl2 impl2;

        public FactoryBeanTests.BeanImpl2 getImpl2() {
            return impl2;
        }

        public void setImpl2(FactoryBeanTests.BeanImpl2 impl2) {
            this.impl2 = impl2;
        }
    }

    public static class BeanImpl2 {
        private FactoryBeanTests.BeanImpl1 impl1;

        public FactoryBeanTests.BeanImpl1 getImpl1() {
            return impl1;
        }

        public void setImpl1(FactoryBeanTests.BeanImpl1 impl1) {
            this.impl1 = impl1;
        }
    }
}

