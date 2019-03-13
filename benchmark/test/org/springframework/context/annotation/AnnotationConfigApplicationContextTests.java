/**
 * Copyright 2002-2018 the original author or authors.
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
package org.springframework.context.annotation;


import java.util.Map;
import java.util.regex.Pattern;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation6.ComponentForScanning;
import org.springframework.context.annotation6.ConfigForScanning;
import org.springframework.context.annotation6.Jsr330NamedForScanning;
import org.springframework.core.ResolvableType;
import org.springframework.util.ObjectUtils;


/**
 *
 *
 * @author Chris Beams
 * @author Juergen Hoeller
 */
public class AnnotationConfigApplicationContextTests {
    @Test
    public void scanAndRefresh() {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        context.scan("org.springframework.context.annotation6");
        context.refresh();
        context.getBean(uncapitalize(ConfigForScanning.class.getSimpleName()));
        context.getBean("testBean");// contributed by ConfigForScanning

        context.getBean(uncapitalize(ComponentForScanning.class.getSimpleName()));
        context.getBean(uncapitalize(Jsr330NamedForScanning.class.getSimpleName()));
        Map<String, Object> beans = context.getBeansWithAnnotation(Configuration.class);
        Assert.assertEquals(1, beans.size());
    }

    @Test
    public void registerAndRefresh() {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        context.register(AnnotationConfigApplicationContextTests.Config.class, AnnotationConfigApplicationContextTests.NameConfig.class);
        context.refresh();
        context.getBean("testBean");
        context.getBean("name");
        Map<String, Object> beans = context.getBeansWithAnnotation(Configuration.class);
        Assert.assertEquals(2, beans.size());
    }

    @Test
    public void getBeansWithAnnotation() {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        context.register(AnnotationConfigApplicationContextTests.Config.class, AnnotationConfigApplicationContextTests.NameConfig.class, AnnotationConfigApplicationContextTests.UntypedFactoryBean.class);
        context.refresh();
        context.getBean("testBean");
        context.getBean("name");
        Map<String, Object> beans = context.getBeansWithAnnotation(Configuration.class);
        Assert.assertEquals(2, beans.size());
    }

    @Test
    public void getBeanByType() {
        ApplicationContext context = new AnnotationConfigApplicationContext(AnnotationConfigApplicationContextTests.Config.class);
        TestBean testBean = context.getBean(TestBean.class);
        Assert.assertNotNull(testBean);
        Assert.assertThat(testBean.name, equalTo("foo"));
    }

    @Test
    public void getBeanByTypeRaisesNoSuchBeanDefinitionException() {
        ApplicationContext context = new AnnotationConfigApplicationContext(AnnotationConfigApplicationContextTests.Config.class);
        // attempt to retrieve a bean that does not exist
        Class<?> targetType = Pattern.class;
        try {
            context.getBean(targetType);
            Assert.fail("Should have thrown NoSuchBeanDefinitionException");
        } catch (NoSuchBeanDefinitionException ex) {
            Assert.assertThat(ex.getMessage(), containsString(String.format("No qualifying bean of type '%s'", targetType.getName())));
        }
    }

    @Test
    public void getBeanByTypeAmbiguityRaisesException() {
        ApplicationContext context = new AnnotationConfigApplicationContext(AnnotationConfigApplicationContextTests.TwoTestBeanConfig.class);
        try {
            context.getBean(TestBean.class);
        } catch (NoSuchBeanDefinitionException ex) {
            Assert.assertThat(ex.getMessage(), allOf(containsString((("No qualifying bean of type '" + (TestBean.class.getName())) + "'")), containsString("tb1"), containsString("tb2")));
        }
    }

    /**
     * Tests that Configuration classes are registered according to convention
     *
     * @see org.springframework.beans.factory.support.DefaultBeanNameGenerator#generateBeanName
     */
    @Test
    public void defaultConfigClassBeanNameIsGeneratedProperly() {
        ApplicationContext context = new AnnotationConfigApplicationContext(AnnotationConfigApplicationContextTests.Config.class);
        // attempt to retrieve the instance by its generated bean name
        AnnotationConfigApplicationContextTests.Config configObject = ((AnnotationConfigApplicationContextTests.Config) (context.getBean("annotationConfigApplicationContextTests.Config")));
        Assert.assertNotNull(configObject);
    }

    /**
     * Tests that specifying @Configuration(value="foo") results in registering
     * the configuration class with bean name 'foo'.
     */
    @Test
    public void explicitConfigClassBeanNameIsRespected() {
        ApplicationContext context = new AnnotationConfigApplicationContext(AnnotationConfigApplicationContextTests.ConfigWithCustomName.class);
        // attempt to retrieve the instance by its specified name
        AnnotationConfigApplicationContextTests.ConfigWithCustomName configObject = ((AnnotationConfigApplicationContextTests.ConfigWithCustomName) (context.getBean("customConfigBeanName")));
        Assert.assertNotNull(configObject);
    }

    @Test
    public void autowiringIsEnabledByDefault() {
        ApplicationContext context = new AnnotationConfigApplicationContext(AnnotationConfigApplicationContextTests.AutowiredConfig.class);
        Assert.assertThat(context.getBean(TestBean.class).name, equalTo("foo"));
    }

    @Test
    public void nullReturningBeanPostProcessor() {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        context.register(AnnotationConfigApplicationContextTests.AutowiredConfig.class);
        context.getBeanFactory().addBeanPostProcessor(new BeanPostProcessor() {
            @Override
            public Object postProcessBeforeInitialization(Object bean, String beanName) {
                return bean instanceof TestBean ? null : bean;
            }

            @Override
            public Object postProcessAfterInitialization(Object bean, String beanName) {
                return bean;
            }
        });
        context.getBeanFactory().addBeanPostProcessor(new BeanPostProcessor() {
            @Override
            public Object postProcessBeforeInitialization(Object bean, String beanName) {
                bean.getClass().getName();
                return bean;
            }

            @Override
            public Object postProcessAfterInitialization(Object bean, String beanName) {
                bean.getClass().getName();
                return bean;
            }
        });
        context.refresh();
    }

    @Test
    public void individualBeans() {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        context.register(AnnotationConfigApplicationContextTests.BeanA.class, AnnotationConfigApplicationContextTests.BeanB.class, AnnotationConfigApplicationContextTests.BeanC.class);
        context.refresh();
        Assert.assertSame(context.getBean(AnnotationConfigApplicationContextTests.BeanB.class), context.getBean(AnnotationConfigApplicationContextTests.BeanA.class).b);
        Assert.assertSame(context.getBean(AnnotationConfigApplicationContextTests.BeanC.class), context.getBean(AnnotationConfigApplicationContextTests.BeanA.class).c);
        Assert.assertSame(context, context.getBean(AnnotationConfigApplicationContextTests.BeanB.class).applicationContext);
    }

    @Test
    public void individualNamedBeans() {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        context.registerBean("a", AnnotationConfigApplicationContextTests.BeanA.class);
        context.registerBean("b", AnnotationConfigApplicationContextTests.BeanB.class);
        context.registerBean("c", AnnotationConfigApplicationContextTests.BeanC.class);
        context.refresh();
        Assert.assertSame(context.getBean("b"), context.getBean("a", AnnotationConfigApplicationContextTests.BeanA.class).b);
        Assert.assertSame(context.getBean("c"), context.getBean("a", AnnotationConfigApplicationContextTests.BeanA.class).c);
        Assert.assertSame(context, context.getBean("b", AnnotationConfigApplicationContextTests.BeanB.class).applicationContext);
    }

    @Test
    public void individualBeanWithSupplier() {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        context.registerBean(AnnotationConfigApplicationContextTests.BeanA.class, () -> new org.springframework.context.annotation.BeanA(context.getBean(.class), context.getBean(.class)));
        context.registerBean(AnnotationConfigApplicationContextTests.BeanB.class, AnnotationConfigApplicationContextTests.BeanB::new);
        context.registerBean(AnnotationConfigApplicationContextTests.BeanC.class, AnnotationConfigApplicationContextTests.BeanC::new);
        context.refresh();
        Assert.assertTrue(context.getBeanFactory().containsSingleton("annotationConfigApplicationContextTests.BeanA"));
        Assert.assertSame(context.getBean(AnnotationConfigApplicationContextTests.BeanB.class), context.getBean(AnnotationConfigApplicationContextTests.BeanA.class).b);
        Assert.assertSame(context.getBean(AnnotationConfigApplicationContextTests.BeanC.class), context.getBean(AnnotationConfigApplicationContextTests.BeanA.class).c);
        Assert.assertSame(context, context.getBean(AnnotationConfigApplicationContextTests.BeanB.class).applicationContext);
        Assert.assertArrayEquals(new String[]{ "annotationConfigApplicationContextTests.BeanA" }, context.getDefaultListableBeanFactory().getDependentBeans("annotationConfigApplicationContextTests.BeanB"));
        Assert.assertArrayEquals(new String[]{ "annotationConfigApplicationContextTests.BeanA" }, context.getDefaultListableBeanFactory().getDependentBeans("annotationConfigApplicationContextTests.BeanC"));
    }

    @Test
    public void individualBeanWithSupplierAndCustomizer() {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        context.registerBean(AnnotationConfigApplicationContextTests.BeanA.class, () -> new org.springframework.context.annotation.BeanA(context.getBean(.class), context.getBean(.class)), ( bd) -> bd.setLazyInit(true));
        context.registerBean(AnnotationConfigApplicationContextTests.BeanB.class, AnnotationConfigApplicationContextTests.BeanB::new);
        context.registerBean(AnnotationConfigApplicationContextTests.BeanC.class, AnnotationConfigApplicationContextTests.BeanC::new);
        context.refresh();
        Assert.assertFalse(context.getBeanFactory().containsSingleton("annotationConfigApplicationContextTests.BeanA"));
        Assert.assertSame(context.getBean(AnnotationConfigApplicationContextTests.BeanB.class), context.getBean(AnnotationConfigApplicationContextTests.BeanA.class).b);
        Assert.assertSame(context.getBean(AnnotationConfigApplicationContextTests.BeanC.class), context.getBean(AnnotationConfigApplicationContextTests.BeanA.class).c);
        Assert.assertSame(context, context.getBean(AnnotationConfigApplicationContextTests.BeanB.class).applicationContext);
    }

    @Test
    public void individualNamedBeanWithSupplier() {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        context.registerBean("a", AnnotationConfigApplicationContextTests.BeanA.class, () -> new org.springframework.context.annotation.BeanA(context.getBean(.class), context.getBean(.class)));
        context.registerBean("b", AnnotationConfigApplicationContextTests.BeanB.class, AnnotationConfigApplicationContextTests.BeanB::new);
        context.registerBean("c", AnnotationConfigApplicationContextTests.BeanC.class, AnnotationConfigApplicationContextTests.BeanC::new);
        context.refresh();
        Assert.assertTrue(context.getBeanFactory().containsSingleton("a"));
        Assert.assertSame(context.getBean("b", AnnotationConfigApplicationContextTests.BeanB.class), context.getBean(AnnotationConfigApplicationContextTests.BeanA.class).b);
        Assert.assertSame(context.getBean("c"), context.getBean("a", AnnotationConfigApplicationContextTests.BeanA.class).c);
        Assert.assertSame(context, context.getBean("b", AnnotationConfigApplicationContextTests.BeanB.class).applicationContext);
    }

    @Test
    public void individualNamedBeanWithSupplierAndCustomizer() {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        context.registerBean("a", AnnotationConfigApplicationContextTests.BeanA.class, () -> new org.springframework.context.annotation.BeanA(context.getBean(.class), context.getBean(.class)), ( bd) -> bd.setLazyInit(true));
        context.registerBean("b", AnnotationConfigApplicationContextTests.BeanB.class, AnnotationConfigApplicationContextTests.BeanB::new);
        context.registerBean("c", AnnotationConfigApplicationContextTests.BeanC.class, AnnotationConfigApplicationContextTests.BeanC::new);
        context.refresh();
        Assert.assertFalse(context.getBeanFactory().containsSingleton("a"));
        Assert.assertSame(context.getBean("b", AnnotationConfigApplicationContextTests.BeanB.class), context.getBean(AnnotationConfigApplicationContextTests.BeanA.class).b);
        Assert.assertSame(context.getBean("c"), context.getBean("a", AnnotationConfigApplicationContextTests.BeanA.class).c);
        Assert.assertSame(context, context.getBean("b", AnnotationConfigApplicationContextTests.BeanB.class).applicationContext);
    }

    @Test
    public void individualBeanWithNullReturningSupplier() {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        context.registerBean("a", AnnotationConfigApplicationContextTests.BeanA.class, () -> null);
        context.registerBean("b", AnnotationConfigApplicationContextTests.BeanB.class, AnnotationConfigApplicationContextTests.BeanB::new);
        context.registerBean("c", AnnotationConfigApplicationContextTests.BeanC.class, AnnotationConfigApplicationContextTests.BeanC::new);
        context.refresh();
        Assert.assertTrue(ObjectUtils.containsElement(context.getBeanNamesForType(AnnotationConfigApplicationContextTests.BeanA.class), "a"));
        Assert.assertTrue(ObjectUtils.containsElement(context.getBeanNamesForType(AnnotationConfigApplicationContextTests.BeanB.class), "b"));
        Assert.assertTrue(ObjectUtils.containsElement(context.getBeanNamesForType(AnnotationConfigApplicationContextTests.BeanC.class), "c"));
        Assert.assertTrue(context.getBeansOfType(AnnotationConfigApplicationContextTests.BeanA.class).isEmpty());
        Assert.assertSame(context.getBean(AnnotationConfigApplicationContextTests.BeanB.class), context.getBeansOfType(AnnotationConfigApplicationContextTests.BeanB.class).values().iterator().next());
        Assert.assertSame(context.getBean(AnnotationConfigApplicationContextTests.BeanC.class), context.getBeansOfType(AnnotationConfigApplicationContextTests.BeanC.class).values().iterator().next());
    }

    @Test
    public void individualBeanWithSpecifiedConstructorArguments() {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        AnnotationConfigApplicationContextTests.BeanB b = new AnnotationConfigApplicationContextTests.BeanB();
        AnnotationConfigApplicationContextTests.BeanC c = new AnnotationConfigApplicationContextTests.BeanC();
        context.registerBean(AnnotationConfigApplicationContextTests.BeanA.class, b, c);
        context.refresh();
        Assert.assertSame(b, context.getBean(AnnotationConfigApplicationContextTests.BeanA.class).b);
        Assert.assertSame(c, context.getBean(AnnotationConfigApplicationContextTests.BeanA.class).c);
        Assert.assertNull(b.applicationContext);
    }

    @Test
    public void individualNamedBeanWithSpecifiedConstructorArguments() {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        AnnotationConfigApplicationContextTests.BeanB b = new AnnotationConfigApplicationContextTests.BeanB();
        AnnotationConfigApplicationContextTests.BeanC c = new AnnotationConfigApplicationContextTests.BeanC();
        context.registerBean("a", AnnotationConfigApplicationContextTests.BeanA.class, b, c);
        context.refresh();
        Assert.assertSame(b, context.getBean("a", AnnotationConfigApplicationContextTests.BeanA.class).b);
        Assert.assertSame(c, context.getBean("a", AnnotationConfigApplicationContextTests.BeanA.class).c);
        Assert.assertNull(b.applicationContext);
    }

    @Test
    public void individualBeanWithMixedConstructorArguments() {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        AnnotationConfigApplicationContextTests.BeanC c = new AnnotationConfigApplicationContextTests.BeanC();
        context.registerBean(AnnotationConfigApplicationContextTests.BeanA.class, c);
        context.registerBean(AnnotationConfigApplicationContextTests.BeanB.class);
        context.refresh();
        Assert.assertSame(context.getBean(AnnotationConfigApplicationContextTests.BeanB.class), context.getBean(AnnotationConfigApplicationContextTests.BeanA.class).b);
        Assert.assertSame(c, context.getBean(AnnotationConfigApplicationContextTests.BeanA.class).c);
        Assert.assertSame(context, context.getBean(AnnotationConfigApplicationContextTests.BeanB.class).applicationContext);
    }

    @Test
    public void individualNamedBeanWithMixedConstructorArguments() {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        AnnotationConfigApplicationContextTests.BeanC c = new AnnotationConfigApplicationContextTests.BeanC();
        context.registerBean("a", AnnotationConfigApplicationContextTests.BeanA.class, c);
        context.registerBean("b", AnnotationConfigApplicationContextTests.BeanB.class);
        context.refresh();
        Assert.assertSame(context.getBean("b", AnnotationConfigApplicationContextTests.BeanB.class), context.getBean("a", AnnotationConfigApplicationContextTests.BeanA.class).b);
        Assert.assertSame(c, context.getBean("a", AnnotationConfigApplicationContextTests.BeanA.class).c);
        Assert.assertSame(context, context.getBean("b", AnnotationConfigApplicationContextTests.BeanB.class).applicationContext);
    }

    @Test
    public void individualBeanWithFactoryBeanSupplier() {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        context.registerBean("fb", AnnotationConfigApplicationContextTests.TypedFactoryBean.class, AnnotationConfigApplicationContextTests.TypedFactoryBean::new, ( bd) -> bd.setLazyInit(true));
        context.refresh();
        Assert.assertEquals(String.class, context.getType("fb"));
        Assert.assertEquals(AnnotationConfigApplicationContextTests.TypedFactoryBean.class, context.getType("&fb"));
    }

    @Test
    public void individualBeanWithFactoryBeanSupplierAndTargetType() {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        RootBeanDefinition bd = new RootBeanDefinition();
        bd.setInstanceSupplier(AnnotationConfigApplicationContextTests.TypedFactoryBean::new);
        bd.setTargetType(ResolvableType.forClassWithGenerics(FactoryBean.class, String.class));
        bd.setLazyInit(true);
        context.registerBeanDefinition("fb", bd);
        context.refresh();
        Assert.assertEquals(String.class, context.getType("fb"));
        Assert.assertEquals(FactoryBean.class, context.getType("&fb"));
    }

    @Configuration
    static class Config {
        @Bean
        public TestBean testBean() {
            TestBean testBean = new TestBean();
            testBean.name = "foo";
            return testBean;
        }
    }

    @Configuration("customConfigBeanName")
    static class ConfigWithCustomName {
        @Bean
        public TestBean testBean() {
            return new TestBean();
        }
    }

    @Configuration
    static class TwoTestBeanConfig {
        @Bean
        TestBean tb1() {
            return new TestBean();
        }

        @Bean
        TestBean tb2() {
            return new TestBean();
        }
    }

    @Configuration
    static class NameConfig {
        @Bean
        String name() {
            return "foo";
        }
    }

    @Configuration
    @Import(AnnotationConfigApplicationContextTests.NameConfig.class)
    static class AutowiredConfig {
        @Autowired
        String autowiredName;

        @Bean
        TestBean testBean() {
            TestBean testBean = new TestBean();
            testBean.name = autowiredName;
            return testBean;
        }
    }

    static class BeanA {
        AnnotationConfigApplicationContextTests.BeanB b;

        AnnotationConfigApplicationContextTests.BeanC c;

        @Autowired
        public BeanA(AnnotationConfigApplicationContextTests.BeanB b, AnnotationConfigApplicationContextTests.BeanC c) {
            this.b = b;
            this.c = c;
        }
    }

    static class BeanB {
        @Autowired
        ApplicationContext applicationContext;

        public BeanB() {
        }
    }

    static class BeanC {}

    static class TypedFactoryBean implements FactoryBean<String> {
        public TypedFactoryBean() {
            throw new IllegalStateException();
        }

        @Override
        public String getObject() {
            return "";
        }

        @Override
        public Class<?> getObjectType() {
            return String.class;
        }

        @Override
        public boolean isSingleton() {
            return true;
        }
    }

    static class UntypedFactoryBean implements FactoryBean<Object> {
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
            return false;
        }
    }
}

