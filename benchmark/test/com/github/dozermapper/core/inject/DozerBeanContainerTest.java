/**
 * Copyright 2005-2019 Dozer Project
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
package com.github.dozermapper.core.inject;


import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class DozerBeanContainerTest {
    DozerBeanContainer container;

    @Test(expected = IllegalStateException.class)
    public void notRegistered() {
        container.getBean(String.class);
    }

    @Test
    public void register() {
        container.register(String.class);
        String bean = container.getBean(String.class);
        Assert.assertThat(bean, CoreMatchers.notNullValue());
    }

    @Test
    public void viaInterface() {
        container.register(HashSet.class);
        container.register(ArrayList.class);
        Collection<Collection> beans = container.getBeans(Collection.class);
        Assert.assertThat(beans.size(), CoreMatchers.equalTo(2));
    }

    @Test(expected = IllegalStateException.class)
    public void moreThanOne() {
        container.register(HashSet.class);
        container.register(ArrayList.class);
        container.getBean(Collection.class);
    }

    @Test
    public void injection() {
        container.register(DozerBeanContainerTest.TestBean.class);
        DozerBeanContainerTest.TestBean bean = container.getBean(DozerBeanContainerTest.TestBean.class);
        Assert.assertThat(bean.string, CoreMatchers.notNullValue());
    }

    @Test
    public void deepInjection() {
        container.register(DozerBeanContainerTest.ContainerBean.class);
        DozerBeanContainerTest.ContainerBean bean = container.getBean(DozerBeanContainerTest.ContainerBean.class);
        Assert.assertThat(bean.testBean, CoreMatchers.notNullValue());
        Assert.assertThat(bean.testBean.string, CoreMatchers.notNullValue());
    }

    @Test
    public void caching() {
        container.register(DozerBeanContainerTest.ContainerBean.class);
        DozerBeanContainerTest.ContainerBean bean1 = container.getBean(DozerBeanContainerTest.ContainerBean.class);
        DozerBeanContainerTest.ContainerBean bean2 = container.getBean(DozerBeanContainerTest.ContainerBean.class);
        Assert.assertThat(bean1, CoreMatchers.sameInstance(bean2));
        Assert.assertThat(bean1.testBean, CoreMatchers.sameInstance(bean2.testBean));
        Assert.assertThat(bean1.testBean.string, CoreMatchers.sameInstance(bean2.testBean.string));
    }

    public static class ContainerBean {
        @Inject
        DozerBeanContainerTest.TestBean testBean;
    }

    public static class TestBean {
        @Inject
        String string;
    }
}

