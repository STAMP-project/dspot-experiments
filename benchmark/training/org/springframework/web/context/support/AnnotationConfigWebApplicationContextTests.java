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
package org.springframework.web.context.support;


import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.AnnotationBeanNameGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;


/**
 *
 *
 * @author Chris Beams
 * @author Juergen Hoeller
 */
public class AnnotationConfigWebApplicationContextTests {
    @Test
    @SuppressWarnings("resource")
    public void registerSingleClass() {
        AnnotationConfigWebApplicationContext ctx = new AnnotationConfigWebApplicationContext();
        ctx.register(AnnotationConfigWebApplicationContextTests.Config.class);
        ctx.refresh();
        AnnotationConfigWebApplicationContextTests.TestBean bean = ctx.getBean(AnnotationConfigWebApplicationContextTests.TestBean.class);
        Assert.assertNotNull(bean);
    }

    @Test
    @SuppressWarnings("resource")
    public void configLocationWithSingleClass() {
        AnnotationConfigWebApplicationContext ctx = new AnnotationConfigWebApplicationContext();
        ctx.setConfigLocation(AnnotationConfigWebApplicationContextTests.Config.class.getName());
        ctx.refresh();
        AnnotationConfigWebApplicationContextTests.TestBean bean = ctx.getBean(AnnotationConfigWebApplicationContextTests.TestBean.class);
        Assert.assertNotNull(bean);
    }

    @Test
    @SuppressWarnings("resource")
    public void configLocationWithBasePackage() {
        AnnotationConfigWebApplicationContext ctx = new AnnotationConfigWebApplicationContext();
        ctx.setConfigLocation("org.springframework.web.context.support");
        ctx.refresh();
        AnnotationConfigWebApplicationContextTests.TestBean bean = ctx.getBean(AnnotationConfigWebApplicationContextTests.TestBean.class);
        Assert.assertNotNull(bean);
    }

    @Test
    @SuppressWarnings("resource")
    public void withBeanNameGenerator() {
        AnnotationConfigWebApplicationContext ctx = new AnnotationConfigWebApplicationContext();
        ctx.setBeanNameGenerator(new AnnotationBeanNameGenerator() {
            @Override
            public String generateBeanName(BeanDefinition definition, BeanDefinitionRegistry registry) {
                return "custom-" + (super.generateBeanName(definition, registry));
            }
        });
        ctx.setConfigLocation(AnnotationConfigWebApplicationContextTests.Config.class.getName());
        ctx.refresh();
        Assert.assertThat(ctx.containsBean("custom-myConfig"), CoreMatchers.is(true));
    }

    @Test
    @SuppressWarnings("resource")
    public void registerBean() {
        AnnotationConfigWebApplicationContext ctx = new AnnotationConfigWebApplicationContext();
        ctx.registerBean(AnnotationConfigWebApplicationContextTests.TestBean.class);
        ctx.refresh();
        Assert.assertTrue(ctx.getBeanFactory().containsSingleton("annotationConfigWebApplicationContextTests.TestBean"));
        AnnotationConfigWebApplicationContextTests.TestBean bean = ctx.getBean(AnnotationConfigWebApplicationContextTests.TestBean.class);
        Assert.assertNotNull(bean);
    }

    @Test
    @SuppressWarnings("resource")
    public void registerBeanWithLazy() {
        AnnotationConfigWebApplicationContext ctx = new AnnotationConfigWebApplicationContext();
        ctx.registerBean(AnnotationConfigWebApplicationContextTests.TestBean.class, Lazy.class);
        ctx.refresh();
        Assert.assertFalse(ctx.getBeanFactory().containsSingleton("annotationConfigWebApplicationContextTests.TestBean"));
        AnnotationConfigWebApplicationContextTests.TestBean bean = ctx.getBean(AnnotationConfigWebApplicationContextTests.TestBean.class);
        Assert.assertNotNull(bean);
    }

    @Test
    @SuppressWarnings("resource")
    public void registerBeanWithSupplier() {
        AnnotationConfigWebApplicationContext ctx = new AnnotationConfigWebApplicationContext();
        ctx.registerBean(AnnotationConfigWebApplicationContextTests.TestBean.class, AnnotationConfigWebApplicationContextTests.TestBean::new);
        ctx.refresh();
        Assert.assertTrue(ctx.getBeanFactory().containsSingleton("annotationConfigWebApplicationContextTests.TestBean"));
        AnnotationConfigWebApplicationContextTests.TestBean bean = ctx.getBean(AnnotationConfigWebApplicationContextTests.TestBean.class);
        Assert.assertNotNull(bean);
    }

    @Test
    @SuppressWarnings("resource")
    public void registerBeanWithSupplierAndLazy() {
        AnnotationConfigWebApplicationContext ctx = new AnnotationConfigWebApplicationContext();
        ctx.registerBean(AnnotationConfigWebApplicationContextTests.TestBean.class, AnnotationConfigWebApplicationContextTests.TestBean::new, Lazy.class);
        ctx.refresh();
        Assert.assertFalse(ctx.getBeanFactory().containsSingleton("annotationConfigWebApplicationContextTests.TestBean"));
        AnnotationConfigWebApplicationContextTests.TestBean bean = ctx.getBean(AnnotationConfigWebApplicationContextTests.TestBean.class);
        Assert.assertNotNull(bean);
    }

    @Configuration("myConfig")
    static class Config {
        @Bean
        public AnnotationConfigWebApplicationContextTests.TestBean myTestBean() {
            return new AnnotationConfigWebApplicationContextTests.TestBean();
        }
    }

    static class TestBean {}
}

