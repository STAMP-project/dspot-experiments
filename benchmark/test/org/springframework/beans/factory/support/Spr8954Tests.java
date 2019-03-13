/**
 * Copyright 2002-2013 the original author or authors.
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
package org.springframework.beans.factory.support;


import java.util.Arrays;
import java.util.Map;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessorAdapter;


/**
 * Unit tests for SPR-8954, in which a custom {@link InstantiationAwareBeanPostProcessor}
 * forces the predicted type of a FactoryBean, effectively preventing retrieval of the
 * bean from calls to #getBeansOfType(FactoryBean.class). The implementation of
 * {@link AbstractBeanFactory#isFactoryBean(String, RootBeanDefinition)} now ensures that
 * not only the predicted bean type is considered, but also the original bean definition's
 * beanClass.
 *
 * @author Chris Beams
 * @author Oliver Gierke
 */
public class Spr8954Tests {
    private DefaultListableBeanFactory bf;

    @Test
    public void repro() {
        Assert.assertThat(bf.getBean("foo"), CoreMatchers.instanceOf(Spr8954Tests.Foo.class));
        Assert.assertThat(bf.getBean("&foo"), CoreMatchers.instanceOf(Spr8954Tests.FooFactoryBean.class));
        Assert.assertThat(bf.isTypeMatch("&foo", FactoryBean.class), CoreMatchers.is(true));
        @SuppressWarnings("rawtypes")
        Map<String, FactoryBean> fbBeans = bf.getBeansOfType(FactoryBean.class);
        Assert.assertThat(fbBeans.size(), CoreMatchers.is(1));
        Assert.assertThat(fbBeans.keySet(), CoreMatchers.hasItem("&foo"));
        Map<String, Spr8954Tests.AnInterface> aiBeans = bf.getBeansOfType(Spr8954Tests.AnInterface.class);
        Assert.assertThat(aiBeans.size(), CoreMatchers.is(1));
        Assert.assertThat(aiBeans.keySet(), CoreMatchers.hasItem("&foo"));
    }

    @Test
    public void findsBeansByTypeIfNotInstantiated() {
        Assert.assertThat(bf.isTypeMatch("&foo", FactoryBean.class), CoreMatchers.is(true));
        @SuppressWarnings("rawtypes")
        Map<String, FactoryBean> fbBeans = bf.getBeansOfType(FactoryBean.class);
        Assert.assertThat(1, CoreMatchers.equalTo(fbBeans.size()));
        Assert.assertThat("&foo", CoreMatchers.equalTo(fbBeans.keySet().iterator().next()));
        Map<String, Spr8954Tests.AnInterface> aiBeans = bf.getBeansOfType(Spr8954Tests.AnInterface.class);
        Assert.assertThat(aiBeans.size(), CoreMatchers.is(1));
        Assert.assertThat(aiBeans.keySet(), CoreMatchers.hasItem("&foo"));
    }

    /**
     * SPR-10517
     */
    @Test
    public void findsFactoryBeanNameByTypeWithoutInstantiation() {
        String[] names = bf.getBeanNamesForType(Spr8954Tests.AnInterface.class, false, false);
        Assert.assertThat(Arrays.asList(names), CoreMatchers.hasItem("&foo"));
        Map<String, Spr8954Tests.AnInterface> beans = bf.getBeansOfType(Spr8954Tests.AnInterface.class, false, false);
        Assert.assertThat(beans.size(), CoreMatchers.is(1));
        Assert.assertThat(beans.keySet(), CoreMatchers.hasItem("&foo"));
    }

    static class FooFactoryBean implements FactoryBean<Spr8954Tests.Foo> , Spr8954Tests.AnInterface {
        @Override
        public Spr8954Tests.Foo getObject() throws Exception {
            return new Spr8954Tests.Foo();
        }

        @Override
        public Class<?> getObjectType() {
            return Spr8954Tests.Foo.class;
        }

        @Override
        public boolean isSingleton() {
            return true;
        }
    }

    interface AnInterface {}

    static class Foo {}

    interface PredictedType {}

    static class PredictedTypeImpl implements Spr8954Tests.PredictedType {}

    static class PredictingBPP extends InstantiationAwareBeanPostProcessorAdapter {
        @Override
        public Class<?> predictBeanType(Class<?> beanClass, String beanName) {
            return FactoryBean.class.isAssignableFrom(beanClass) ? Spr8954Tests.PredictedType.class : null;
        }
    }
}

