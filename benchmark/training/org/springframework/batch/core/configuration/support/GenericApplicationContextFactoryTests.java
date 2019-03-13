/**
 * Copyright 2006-2018 the original author or authors.
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
package org.springframework.batch.core.configuration.support;


import org.junit.Assert;
import org.junit.Test;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.job.JobSupport;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;
import org.springframework.util.ClassUtils;


/**
 *
 *
 * @author Dave Syer
 * @author Mahmoud Ben Hassine
 */
public class GenericApplicationContextFactoryTests {
    @Test
    public void testCreateJob() {
        GenericApplicationContextFactory factory = new GenericApplicationContextFactory(new org.springframework.core.io.ClassPathResource(ClassUtils.addResourcePathToPackagePath(getClass(), "trivial-context.xml")));
        @SuppressWarnings("resource")
        ConfigurableApplicationContext context = factory.createApplicationContext();
        Assert.assertNotNull(context);
        Assert.assertTrue(("Wrong id: " + context), context.getId().contains("trivial-context.xml"));
    }

    @Test
    public void testGetJobName() {
        GenericApplicationContextFactory factory = new GenericApplicationContextFactory(new org.springframework.core.io.ClassPathResource(ClassUtils.addResourcePathToPackagePath(getClass(), "trivial-context.xml")));
        Assert.assertEquals("test-job", factory.createApplicationContext().getBeanNamesForType(Job.class)[0]);
    }

    @SuppressWarnings("resource")
    @Test
    public void testParentConfigurationInherited() {
        GenericApplicationContextFactory factory = new GenericApplicationContextFactory(new org.springframework.core.io.ClassPathResource(ClassUtils.addResourcePathToPackagePath(getClass(), "child-context.xml")));
        factory.setApplicationContext(new ClassPathXmlApplicationContext(ClassUtils.addResourcePathToPackagePath(getClass(), "parent-context.xml")));
        ConfigurableApplicationContext context = factory.createApplicationContext();
        Assert.assertEquals("test-job", context.getBeanNamesForType(Job.class)[0]);
        Assert.assertEquals("bar", context.getBean("test-job", Job.class).getName());
        Assert.assertEquals(4, context.getBean("foo", GenericApplicationContextFactoryTests.Foo.class).values[1], 0.01);
    }

    @SuppressWarnings("resource")
    @Test
    public void testBeanFactoryPostProcessorOrderRespected() {
        GenericApplicationContextFactory factory = new GenericApplicationContextFactory(new org.springframework.core.io.ClassPathResource(ClassUtils.addResourcePathToPackagePath(getClass(), "placeholder-context.xml")));
        factory.setApplicationContext(new ClassPathXmlApplicationContext(ClassUtils.addResourcePathToPackagePath(getClass(), "parent-context.xml")));
        ConfigurableApplicationContext context = factory.createApplicationContext();
        Assert.assertEquals("test-job", context.getBeanNamesForType(Job.class)[0]);
        Assert.assertEquals("spam", context.getBean("test-job", Job.class).getName());
    }

    @Test
    public void testBeanFactoryProfileRespected() {
        GenericApplicationContextFactory factory = new GenericApplicationContextFactory(new org.springframework.core.io.ClassPathResource(ClassUtils.addResourcePathToPackagePath(getClass(), "profiles.xml")));
        @SuppressWarnings("resource")
        ClassPathXmlApplicationContext parentContext = new ClassPathXmlApplicationContext(ClassUtils.addResourcePathToPackagePath(getClass(), "parent-context.xml"));
        parentContext.getEnvironment().setActiveProfiles("preferred");
        factory.setApplicationContext(parentContext);
        @SuppressWarnings("resource")
        ConfigurableApplicationContext context = factory.createApplicationContext();
        Assert.assertEquals("test-job", context.getBeanNamesForType(Job.class)[0]);
        Assert.assertEquals("spam", context.getBean("test-job", Job.class).getName());
    }

    @SuppressWarnings("resource")
    @Test
    public void testBeanFactoryPostProcessorsNotCopied() {
        GenericApplicationContextFactory factory = new GenericApplicationContextFactory(new org.springframework.core.io.ClassPathResource(ClassUtils.addResourcePathToPackagePath(getClass(), "child-context.xml")));
        factory.setApplicationContext(new ClassPathXmlApplicationContext(ClassUtils.addResourcePathToPackagePath(getClass(), "parent-context.xml")));
        @SuppressWarnings("unchecked")
        Class<? extends BeanFactoryPostProcessor>[] classes = ((Class<? extends BeanFactoryPostProcessor>[]) (new Class<?>[0]));
        factory.setBeanFactoryPostProcessorClasses(classes);
        ConfigurableApplicationContext context = factory.createApplicationContext();
        Assert.assertEquals("test-job", context.getBeanNamesForType(Job.class)[0]);
        Assert.assertEquals("${foo}", context.getBean("test-job", Job.class).getName());
        Assert.assertEquals(4, context.getBean("foo", GenericApplicationContextFactoryTests.Foo.class).values[1], 0.01);
    }

    @SuppressWarnings("resource")
    @Test
    public void testBeanFactoryConfigurationNotCopied() {
        GenericApplicationContextFactory factory = new GenericApplicationContextFactory(new org.springframework.core.io.ClassPathResource(ClassUtils.addResourcePathToPackagePath(getClass(), "child-context.xml")));
        factory.setApplicationContext(new ClassPathXmlApplicationContext(ClassUtils.addResourcePathToPackagePath(getClass(), "parent-context.xml")));
        factory.setCopyConfiguration(false);
        ConfigurableApplicationContext context = factory.createApplicationContext();
        Assert.assertEquals("test-job", context.getBeanNamesForType(Job.class)[0]);
        Assert.assertEquals("bar", context.getBean("test-job", Job.class).getName());
        // The CustomEditorConfigurer is a BeanFactoryPostProcessor so the
        // editor gets copied anyway!
        Assert.assertEquals(4, context.getBean("foo", GenericApplicationContextFactoryTests.Foo.class).values[1], 0.01);
    }

    @Test
    public void testEquals() throws Exception {
        Resource resource = new org.springframework.core.io.ClassPathResource(ClassUtils.addResourcePathToPackagePath(getClass(), "child-context.xml"));
        GenericApplicationContextFactory factory = new GenericApplicationContextFactory(resource);
        GenericApplicationContextFactory other = new GenericApplicationContextFactory(resource);
        Assert.assertEquals(other, factory);
        Assert.assertEquals(other.hashCode(), factory.hashCode());
    }

    @Test
    public void testEqualsMultipleConfigs() throws Exception {
        Resource resource1 = new org.springframework.core.io.ClassPathResource(ClassUtils.addResourcePathToPackagePath(getClass(), "abstract-context.xml"));
        Resource resource2 = new org.springframework.core.io.ClassPathResource(ClassUtils.addResourcePathToPackagePath(getClass(), "child-context-with-abstract-job.xml"));
        GenericApplicationContextFactory factory = new GenericApplicationContextFactory(resource1, resource2);
        GenericApplicationContextFactory other = new GenericApplicationContextFactory(resource1, resource2);
        Assert.assertEquals(other, factory);
        Assert.assertEquals(other.hashCode(), factory.hashCode());
    }

    @Test
    public void testParentConfigurationInheritedMultipleConfigs() {
        Resource resource1 = new org.springframework.core.io.ClassPathResource(ClassUtils.addResourcePathToPackagePath(getClass(), "abstract-context.xml"));
        Resource resource2 = new org.springframework.core.io.ClassPathResource(ClassUtils.addResourcePathToPackagePath(getClass(), "child-context-with-abstract-job.xml"));
        GenericApplicationContextFactory factory = new GenericApplicationContextFactory(resource1, resource2);
        ConfigurableApplicationContext context = factory.createApplicationContext();
        Assert.assertEquals("concrete-job", context.getBeanNamesForType(Job.class)[0]);
        Assert.assertEquals("bar", context.getBean("concrete-job", Job.class).getName());
        Assert.assertEquals(4, context.getBean("foo", GenericApplicationContextFactoryTests.Foo.class).values[1], 0.01);
        Assert.assertNotNull(context.getBean("concrete-job", JobSupport.class).getStep("step31"));
        Assert.assertNotNull(context.getBean("concrete-job", JobSupport.class).getStep("step32"));
        boolean autowiredFound = false;
        for (BeanPostProcessor postProcessor : getBeanPostProcessors()) {
            if (postProcessor instanceof AutowiredAnnotationBeanPostProcessor) {
                autowiredFound = true;
            }
        }
        Assert.assertTrue(autowiredFound);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testDifferentResourceTypes() throws Exception {
        Resource resource1 = new org.springframework.core.io.ClassPathResource(ClassUtils.addResourcePathToPackagePath(getClass(), "abstract-context.xml"));
        GenericApplicationContextFactory factory = new GenericApplicationContextFactory(resource1, GenericApplicationContextFactoryTests.Configuration1.class);
        factory.createApplicationContext();
    }

    @Test
    public void testPackageScanning() throws Exception {
        GenericApplicationContextFactory factory = new GenericApplicationContextFactory("org.springframework.batch.core.configuration.support");
        ConfigurableApplicationContext context = factory.createApplicationContext();
        Assert.assertEquals(context.getBean("bean1"), "bean1");
        Assert.assertEquals(context.getBean("bean2"), "bean2");
        Assert.assertEquals(context.getBean("bean3"), "bean3");
        Assert.assertEquals(context.getBean("bean4"), "bean4");
    }

    @Test
    public void testMultipleConfigurationClasses() throws Exception {
        GenericApplicationContextFactory factory = new GenericApplicationContextFactory(GenericApplicationContextFactoryTests.Configuration1.class, GenericApplicationContextFactoryTests.Configuration2.class);
        ConfigurableApplicationContext context = factory.createApplicationContext();
        Assert.assertEquals(context.getBean("bean1"), "bean1");
        Assert.assertEquals(context.getBean("bean2"), "bean2");
        Assert.assertEquals(context.getBean("bean3"), "bean3");
        Assert.assertEquals(context.getBean("bean4"), "bean4");
    }

    @Test
    public void testParentChildLifecycleEvents() throws InterruptedException {
        AnnotationConfigApplicationContext parent = new AnnotationConfigApplicationContext(GenericApplicationContextFactoryTests.ParentContext.class);
        GenericApplicationContextFactory child = new GenericApplicationContextFactory(GenericApplicationContextFactoryTests.ChildContextConfiguration.class);
        child.setApplicationContext(parent);
        ApplicationContext context = child.createApplicationContext();
        GenericApplicationContextFactoryTests.ChildBean bean = context.getBean(GenericApplicationContextFactoryTests.ChildBean.class);
        Assert.assertEquals(1, bean.counter1);
        Assert.assertEquals(1, bean.counter2);
    }

    public static class Foo {
        private double[] values;

        public void setValues(double[] values) {
            this.values = values;
        }
    }

    @Configuration
    public static class Configuration1 {
        @Bean
        public String bean1() {
            return "bean1";
        }

        @Bean
        public String bean2() {
            return "bean2";
        }
    }

    @Configuration
    public static class Configuration2 {
        @Bean
        public String bean3() {
            return "bean3";
        }

        @Bean
        public String bean4() {
            return "bean4";
        }
    }

    @Configuration
    public static class ParentContext implements ApplicationContextAware {
        @Override
        public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        }
    }

    @Configuration
    public static class ChildContextConfiguration {
        @Bean
        public GenericApplicationContextFactoryTests.ChildBean childBean() {
            return new GenericApplicationContextFactoryTests.ChildBean();
        }
    }

    public static class ChildBean implements ApplicationContextAware , EnvironmentAware {
        private int counter1 = 0;

        private int counter2 = 0;

        @Override
        public void setEnvironment(Environment environment) {
            (counter2)++;
        }

        @Override
        public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
            (counter1)++;
        }
    }
}

