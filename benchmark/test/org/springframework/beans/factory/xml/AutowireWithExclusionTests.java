/**
 * Copyright 2002-2015 the original author or authors.
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


import RootBeanDefinition.AUTOWIRE_BY_TYPE;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.tests.sample.beans.TestBean;


/**
 *
 *
 * @author Rob Harrop
 * @author Juergen Hoeller
 */
public class AutowireWithExclusionTests {
    @Test
    public void byTypeAutowireWithAutoSelfExclusion() throws Exception {
        CountingFactory.reset();
        DefaultListableBeanFactory beanFactory = getBeanFactory("autowire-with-exclusion.xml");
        beanFactory.preInstantiateSingletons();
        TestBean rob = ((TestBean) (beanFactory.getBean("rob")));
        TestBean sally = ((TestBean) (beanFactory.getBean("sally")));
        Assert.assertEquals(sally, rob.getSpouse());
        Assert.assertEquals(1, CountingFactory.getFactoryBeanInstanceCount());
    }

    @Test
    public void byTypeAutowireWithExclusion() throws Exception {
        CountingFactory.reset();
        DefaultListableBeanFactory beanFactory = getBeanFactory("autowire-with-exclusion.xml");
        beanFactory.preInstantiateSingletons();
        TestBean rob = ((TestBean) (beanFactory.getBean("rob")));
        Assert.assertEquals("props1", rob.getSomeProperties().getProperty("name"));
        Assert.assertEquals(1, CountingFactory.getFactoryBeanInstanceCount());
    }

    @Test
    public void byTypeAutowireWithExclusionInParentFactory() throws Exception {
        CountingFactory.reset();
        DefaultListableBeanFactory parent = getBeanFactory("autowire-with-exclusion.xml");
        parent.preInstantiateSingletons();
        DefaultListableBeanFactory child = new DefaultListableBeanFactory(parent);
        RootBeanDefinition robDef = new RootBeanDefinition(TestBean.class);
        robDef.setAutowireMode(AUTOWIRE_BY_TYPE);
        robDef.getPropertyValues().add("spouse", new RuntimeBeanReference("sally"));
        child.registerBeanDefinition("rob2", robDef);
        TestBean rob = ((TestBean) (child.getBean("rob2")));
        Assert.assertEquals("props1", rob.getSomeProperties().getProperty("name"));
        Assert.assertEquals(1, CountingFactory.getFactoryBeanInstanceCount());
    }

    @Test
    public void byTypeAutowireWithPrimaryInParentFactory() throws Exception {
        CountingFactory.reset();
        DefaultListableBeanFactory parent = getBeanFactory("autowire-with-exclusion.xml");
        parent.getBeanDefinition("props1").setPrimary(true);
        parent.preInstantiateSingletons();
        DefaultListableBeanFactory child = new DefaultListableBeanFactory(parent);
        RootBeanDefinition robDef = new RootBeanDefinition(TestBean.class);
        robDef.setAutowireMode(AUTOWIRE_BY_TYPE);
        robDef.getPropertyValues().add("spouse", new RuntimeBeanReference("sally"));
        child.registerBeanDefinition("rob2", robDef);
        RootBeanDefinition propsDef = new RootBeanDefinition(PropertiesFactoryBean.class);
        propsDef.getPropertyValues().add("properties", "name=props3");
        child.registerBeanDefinition("props3", propsDef);
        TestBean rob = ((TestBean) (child.getBean("rob2")));
        Assert.assertEquals("props1", rob.getSomeProperties().getProperty("name"));
        Assert.assertEquals(1, CountingFactory.getFactoryBeanInstanceCount());
    }

    @Test
    public void byTypeAutowireWithPrimaryOverridingParentFactory() throws Exception {
        CountingFactory.reset();
        DefaultListableBeanFactory parent = getBeanFactory("autowire-with-exclusion.xml");
        parent.preInstantiateSingletons();
        DefaultListableBeanFactory child = new DefaultListableBeanFactory(parent);
        RootBeanDefinition robDef = new RootBeanDefinition(TestBean.class);
        robDef.setAutowireMode(AUTOWIRE_BY_TYPE);
        robDef.getPropertyValues().add("spouse", new RuntimeBeanReference("sally"));
        child.registerBeanDefinition("rob2", robDef);
        RootBeanDefinition propsDef = new RootBeanDefinition(PropertiesFactoryBean.class);
        propsDef.getPropertyValues().add("properties", "name=props3");
        propsDef.setPrimary(true);
        child.registerBeanDefinition("props3", propsDef);
        TestBean rob = ((TestBean) (child.getBean("rob2")));
        Assert.assertEquals("props3", rob.getSomeProperties().getProperty("name"));
        Assert.assertEquals(1, CountingFactory.getFactoryBeanInstanceCount());
    }

    @Test
    public void byTypeAutowireWithPrimaryInParentAndChild() throws Exception {
        CountingFactory.reset();
        DefaultListableBeanFactory parent = getBeanFactory("autowire-with-exclusion.xml");
        parent.getBeanDefinition("props1").setPrimary(true);
        parent.preInstantiateSingletons();
        DefaultListableBeanFactory child = new DefaultListableBeanFactory(parent);
        RootBeanDefinition robDef = new RootBeanDefinition(TestBean.class);
        robDef.setAutowireMode(AUTOWIRE_BY_TYPE);
        robDef.getPropertyValues().add("spouse", new RuntimeBeanReference("sally"));
        child.registerBeanDefinition("rob2", robDef);
        RootBeanDefinition propsDef = new RootBeanDefinition(PropertiesFactoryBean.class);
        propsDef.getPropertyValues().add("properties", "name=props3");
        propsDef.setPrimary(true);
        child.registerBeanDefinition("props3", propsDef);
        TestBean rob = ((TestBean) (child.getBean("rob2")));
        Assert.assertEquals("props3", rob.getSomeProperties().getProperty("name"));
        Assert.assertEquals(1, CountingFactory.getFactoryBeanInstanceCount());
    }

    @Test
    public void byTypeAutowireWithInclusion() throws Exception {
        CountingFactory.reset();
        DefaultListableBeanFactory beanFactory = getBeanFactory("autowire-with-inclusion.xml");
        beanFactory.preInstantiateSingletons();
        TestBean rob = ((TestBean) (beanFactory.getBean("rob")));
        Assert.assertEquals("props1", rob.getSomeProperties().getProperty("name"));
        Assert.assertEquals(1, CountingFactory.getFactoryBeanInstanceCount());
    }

    @Test
    public void byTypeAutowireWithSelectiveInclusion() throws Exception {
        CountingFactory.reset();
        DefaultListableBeanFactory beanFactory = getBeanFactory("autowire-with-selective-inclusion.xml");
        beanFactory.preInstantiateSingletons();
        TestBean rob = ((TestBean) (beanFactory.getBean("rob")));
        Assert.assertEquals("props1", rob.getSomeProperties().getProperty("name"));
        Assert.assertEquals(1, CountingFactory.getFactoryBeanInstanceCount());
    }

    @Test
    public void constructorAutowireWithAutoSelfExclusion() throws Exception {
        DefaultListableBeanFactory beanFactory = getBeanFactory("autowire-constructor-with-exclusion.xml");
        TestBean rob = ((TestBean) (beanFactory.getBean("rob")));
        TestBean sally = ((TestBean) (beanFactory.getBean("sally")));
        Assert.assertEquals(sally, rob.getSpouse());
        TestBean rob2 = ((TestBean) (beanFactory.getBean("rob")));
        Assert.assertEquals(rob, rob2);
        Assert.assertNotSame(rob, rob2);
        Assert.assertEquals(rob.getSpouse(), rob2.getSpouse());
        Assert.assertNotSame(rob.getSpouse(), rob2.getSpouse());
    }

    @Test
    public void constructorAutowireWithExclusion() throws Exception {
        DefaultListableBeanFactory beanFactory = getBeanFactory("autowire-constructor-with-exclusion.xml");
        TestBean rob = ((TestBean) (beanFactory.getBean("rob")));
        Assert.assertEquals("props1", rob.getSomeProperties().getProperty("name"));
    }
}

