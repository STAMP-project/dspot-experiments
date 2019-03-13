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
package org.springframework.beans.factory.xml;


import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.core.io.ClassPathResource;


/**
 * Tests for propagating enclosing beans element defaults to nested beans elements.
 *
 * @author Chris Beams
 */
public class NestedBeansElementAttributeRecursionTests {
    @Test
    public void defaultLazyInit() {
        DefaultListableBeanFactory bf = new DefaultListableBeanFactory();
        new XmlBeanDefinitionReader(bf).loadBeanDefinitions(new ClassPathResource("NestedBeansElementAttributeRecursionTests-lazy-context.xml", this.getClass()));
        assertLazyInits(bf);
    }

    @Test
    public void defaultLazyInitWithNonValidatingParser() {
        DefaultListableBeanFactory bf = new DefaultListableBeanFactory();
        XmlBeanDefinitionReader xmlBeanDefinitionReader = new XmlBeanDefinitionReader(bf);
        xmlBeanDefinitionReader.setValidating(false);
        xmlBeanDefinitionReader.loadBeanDefinitions(new ClassPathResource("NestedBeansElementAttributeRecursionTests-lazy-context.xml", this.getClass()));
        assertLazyInits(bf);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void defaultMerge() {
        DefaultListableBeanFactory bf = new DefaultListableBeanFactory();
        new XmlBeanDefinitionReader(bf).loadBeanDefinitions(new ClassPathResource("NestedBeansElementAttributeRecursionTests-merge-context.xml", this.getClass()));
        assertMerge(bf);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void defaultMergeWithNonValidatingParser() {
        DefaultListableBeanFactory bf = new DefaultListableBeanFactory();
        XmlBeanDefinitionReader xmlBeanDefinitionReader = new XmlBeanDefinitionReader(bf);
        xmlBeanDefinitionReader.setValidating(false);
        xmlBeanDefinitionReader.loadBeanDefinitions(new ClassPathResource("NestedBeansElementAttributeRecursionTests-merge-context.xml", this.getClass()));
        assertMerge(bf);
    }

    @Test
    public void defaultAutowireCandidates() {
        DefaultListableBeanFactory bf = new DefaultListableBeanFactory();
        new XmlBeanDefinitionReader(bf).loadBeanDefinitions(new ClassPathResource("NestedBeansElementAttributeRecursionTests-autowire-candidates-context.xml", this.getClass()));
        assertAutowireCandidates(bf);
    }

    @Test
    public void defaultAutowireCandidatesWithNonValidatingParser() {
        DefaultListableBeanFactory bf = new DefaultListableBeanFactory();
        XmlBeanDefinitionReader xmlBeanDefinitionReader = new XmlBeanDefinitionReader(bf);
        xmlBeanDefinitionReader.setValidating(false);
        xmlBeanDefinitionReader.loadBeanDefinitions(new ClassPathResource("NestedBeansElementAttributeRecursionTests-autowire-candidates-context.xml", this.getClass()));
        assertAutowireCandidates(bf);
    }

    @Test
    public void initMethod() {
        DefaultListableBeanFactory bf = new DefaultListableBeanFactory();
        new XmlBeanDefinitionReader(bf).loadBeanDefinitions(new ClassPathResource("NestedBeansElementAttributeRecursionTests-init-destroy-context.xml", this.getClass()));
        InitDestroyBean beanA = bf.getBean("beanA", InitDestroyBean.class);
        InitDestroyBean beanB = bf.getBean("beanB", InitDestroyBean.class);
        InitDestroyBean beanC = bf.getBean("beanC", InitDestroyBean.class);
        InitDestroyBean beanD = bf.getBean("beanD", InitDestroyBean.class);
        Assert.assertThat(beanA.initMethod1Called, CoreMatchers.is(true));
        Assert.assertThat(beanB.initMethod2Called, CoreMatchers.is(true));
        Assert.assertThat(beanC.initMethod3Called, CoreMatchers.is(true));
        Assert.assertThat(beanD.initMethod2Called, CoreMatchers.is(true));
        bf.destroySingletons();
        Assert.assertThat(beanA.destroyMethod1Called, CoreMatchers.is(true));
        Assert.assertThat(beanB.destroyMethod2Called, CoreMatchers.is(true));
        Assert.assertThat(beanC.destroyMethod3Called, CoreMatchers.is(true));
        Assert.assertThat(beanD.destroyMethod2Called, CoreMatchers.is(true));
    }
}

