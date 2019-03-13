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
package org.springframework.context.annotation.configuration;


import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;
import javax.annotation.Resource;
import javax.inject.Provider;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InjectionPoint;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.DependencyDescriptor;
import org.springframework.beans.factory.config.ListFactoryBean;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.beans.factory.parsing.BeanDefinitionParsingException;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.AnnotationConfigUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.tests.sample.beans.ITestBean;
import org.springframework.tests.sample.beans.NestedTestBean;
import org.springframework.tests.sample.beans.TestBean;


/**
 * Miscellaneous system tests covering {@link Bean} naming, aliases, scoping and
 * error handling within {@link Configuration} class definitions.
 *
 * @author Chris Beams
 * @author Juergen Hoeller
 * @author Sam Brannen
 */
public class ConfigurationClassProcessingTests {
    @Rule
    public final ExpectedException exception = ExpectedException.none();

    @Test
    public void customBeanNameIsRespectedWhenConfiguredViaNameAttribute() {
        customBeanNameIsRespected(ConfigurationClassProcessingTests.ConfigWithBeanWithCustomName.class, () -> ConfigurationClassProcessingTests.ConfigWithBeanWithCustomName.testBean, "customName");
    }

    @Test
    public void customBeanNameIsRespectedWhenConfiguredViaValueAttribute() {
        customBeanNameIsRespected(ConfigurationClassProcessingTests.ConfigWithBeanWithCustomNameConfiguredViaValueAttribute.class, () -> ConfigurationClassProcessingTests.ConfigWithBeanWithCustomNameConfiguredViaValueAttribute.testBean, "enigma");
    }

    @Test
    public void aliasesAreRespectedWhenConfiguredViaNameAttribute() {
        aliasesAreRespected(ConfigurationClassProcessingTests.ConfigWithBeanWithAliases.class, () -> ConfigurationClassProcessingTests.ConfigWithBeanWithAliases.testBean, "name1");
    }

    @Test
    public void aliasesAreRespectedWhenConfiguredViaValueAttribute() {
        aliasesAreRespected(ConfigurationClassProcessingTests.ConfigWithBeanWithAliasesConfiguredViaValueAttribute.class, () -> ConfigurationClassProcessingTests.ConfigWithBeanWithAliasesConfiguredViaValueAttribute.testBean, "enigma");
    }

    // SPR-11830
    @Test
    public void configWithBeanWithProviderImplementation() {
        GenericApplicationContext ac = new GenericApplicationContext();
        AnnotationConfigUtils.registerAnnotationConfigProcessors(ac);
        ac.registerBeanDefinition("config", new RootBeanDefinition(ConfigurationClassProcessingTests.ConfigWithBeanWithProviderImplementation.class));
        ac.refresh();
        Assert.assertSame(ac.getBean("customName"), ConfigurationClassProcessingTests.ConfigWithBeanWithProviderImplementation.testBean);
    }

    // SPR-11830
    @Test
    public void configWithSetWithProviderImplementation() {
        GenericApplicationContext ac = new GenericApplicationContext();
        AnnotationConfigUtils.registerAnnotationConfigProcessors(ac);
        ac.registerBeanDefinition("config", new RootBeanDefinition(ConfigurationClassProcessingTests.ConfigWithSetWithProviderImplementation.class));
        ac.refresh();
        Assert.assertSame(ac.getBean("customName"), ConfigurationClassProcessingTests.ConfigWithSetWithProviderImplementation.set);
    }

    @Test
    public void testFinalBeanMethod() {
        exception.expect(BeanDefinitionParsingException.class);
        initBeanFactory(ConfigurationClassProcessingTests.ConfigWithFinalBean.class);
    }

    @Test
    public void simplestPossibleConfig() {
        BeanFactory factory = initBeanFactory(ConfigurationClassProcessingTests.SimplestPossibleConfig.class);
        String stringBean = factory.getBean("stringBean", String.class);
        Assert.assertEquals("foo", stringBean);
    }

    @Test
    public void configWithObjectReturnType() {
        BeanFactory factory = initBeanFactory(ConfigurationClassProcessingTests.ConfigWithNonSpecificReturnTypes.class);
        Assert.assertEquals(Object.class, factory.getType("stringBean"));
        Assert.assertFalse(factory.isTypeMatch("stringBean", String.class));
        String stringBean = factory.getBean("stringBean", String.class);
        Assert.assertEquals("foo", stringBean);
    }

    @Test
    public void configWithFactoryBeanReturnType() {
        ListableBeanFactory factory = initBeanFactory(ConfigurationClassProcessingTests.ConfigWithNonSpecificReturnTypes.class);
        Assert.assertEquals(List.class, factory.getType("factoryBean"));
        Assert.assertTrue(factory.isTypeMatch("factoryBean", List.class));
        Assert.assertEquals(FactoryBean.class, factory.getType("&factoryBean"));
        Assert.assertTrue(factory.isTypeMatch("&factoryBean", FactoryBean.class));
        Assert.assertFalse(factory.isTypeMatch("&factoryBean", BeanClassLoaderAware.class));
        Assert.assertFalse(factory.isTypeMatch("&factoryBean", ListFactoryBean.class));
        Assert.assertTrue(((factory.getBean("factoryBean")) instanceof List));
        String[] beanNames = factory.getBeanNamesForType(FactoryBean.class);
        Assert.assertEquals(1, beanNames.length);
        Assert.assertEquals("&factoryBean", beanNames[0]);
        beanNames = factory.getBeanNamesForType(BeanClassLoaderAware.class);
        Assert.assertEquals(1, beanNames.length);
        Assert.assertEquals("&factoryBean", beanNames[0]);
        beanNames = factory.getBeanNamesForType(ListFactoryBean.class);
        Assert.assertEquals(1, beanNames.length);
        Assert.assertEquals("&factoryBean", beanNames[0]);
        beanNames = factory.getBeanNamesForType(List.class);
        Assert.assertEquals("factoryBean", beanNames[0]);
    }

    @Test
    public void configurationWithPrototypeScopedBeans() {
        BeanFactory factory = initBeanFactory(ConfigurationClassProcessingTests.ConfigWithPrototypeBean.class);
        TestBean foo = factory.getBean("foo", TestBean.class);
        ITestBean bar = factory.getBean("bar", ITestBean.class);
        ITestBean baz = factory.getBean("baz", ITestBean.class);
        Assert.assertSame(foo.getSpouse(), bar);
        Assert.assertNotSame(bar.getSpouse(), baz);
    }

    @Test
    public void configurationWithNullReference() {
        BeanFactory factory = initBeanFactory(ConfigurationClassProcessingTests.ConfigWithNullReference.class);
        TestBean foo = factory.getBean("foo", TestBean.class);
        Assert.assertTrue(factory.getBean("bar").equals(null));
        Assert.assertNull(foo.getSpouse());
    }

    @Test
    public void configurationWithAdaptivePrototypes() {
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
        ctx.register(ConfigurationClassProcessingTests.ConfigWithPrototypeBean.class, ConfigurationClassProcessingTests.AdaptiveInjectionPoints.class);
        ctx.refresh();
        ConfigurationClassProcessingTests.AdaptiveInjectionPoints adaptive = ctx.getBean(ConfigurationClassProcessingTests.AdaptiveInjectionPoints.class);
        Assert.assertEquals("adaptiveInjectionPoint1", adaptive.adaptiveInjectionPoint1.getName());
        Assert.assertEquals("setAdaptiveInjectionPoint2", adaptive.adaptiveInjectionPoint2.getName());
        adaptive = ctx.getBean(ConfigurationClassProcessingTests.AdaptiveInjectionPoints.class);
        Assert.assertEquals("adaptiveInjectionPoint1", adaptive.adaptiveInjectionPoint1.getName());
        Assert.assertEquals("setAdaptiveInjectionPoint2", adaptive.adaptiveInjectionPoint2.getName());
        ctx.close();
    }

    @Test
    public void configurationWithAdaptiveResourcePrototypes() {
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
        ctx.register(ConfigurationClassProcessingTests.ConfigWithPrototypeBean.class, ConfigurationClassProcessingTests.AdaptiveResourceInjectionPoints.class);
        ctx.refresh();
        ConfigurationClassProcessingTests.AdaptiveResourceInjectionPoints adaptive = ctx.getBean(ConfigurationClassProcessingTests.AdaptiveResourceInjectionPoints.class);
        Assert.assertEquals("adaptiveInjectionPoint1", adaptive.adaptiveInjectionPoint1.getName());
        Assert.assertEquals("setAdaptiveInjectionPoint2", adaptive.adaptiveInjectionPoint2.getName());
        adaptive = ctx.getBean(ConfigurationClassProcessingTests.AdaptiveResourceInjectionPoints.class);
        Assert.assertEquals("adaptiveInjectionPoint1", adaptive.adaptiveInjectionPoint1.getName());
        Assert.assertEquals("setAdaptiveInjectionPoint2", adaptive.adaptiveInjectionPoint2.getName());
        ctx.close();
    }

    @Test
    public void configurationWithPostProcessor() {
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
        ctx.register(ConfigurationClassProcessingTests.ConfigWithPostProcessor.class);
        RootBeanDefinition placeholderConfigurer = new RootBeanDefinition(PropertyPlaceholderConfigurer.class);
        placeholderConfigurer.getPropertyValues().add("properties", "myProp=myValue");
        ctx.registerBeanDefinition("placeholderConfigurer", placeholderConfigurer);
        ctx.refresh();
        TestBean foo = ctx.getBean("foo", TestBean.class);
        ITestBean bar = ctx.getBean("bar", ITestBean.class);
        ITestBean baz = ctx.getBean("baz", ITestBean.class);
        Assert.assertEquals("foo-processed-myValue", foo.getName());
        Assert.assertEquals("bar-processed-myValue", bar.getName());
        Assert.assertEquals("baz-processed-myValue", baz.getName());
        ConfigurationClassProcessingTests.SpousyTestBean listener = ctx.getBean("listenerTestBean", ConfigurationClassProcessingTests.SpousyTestBean.class);
        Assert.assertTrue(listener.refreshed);
        ctx.close();
    }

    @Test
    public void configurationWithFunctionalRegistration() {
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
        ctx.register(ConfigurationClassProcessingTests.ConfigWithFunctionalRegistration.class);
        ctx.refresh();
        Assert.assertSame(ctx.getBean("spouse"), ctx.getBean(TestBean.class).getSpouse());
        Assert.assertEquals("functional", ctx.getBean(NestedTestBean.class).getCompany());
    }

    @Configuration
    static class ConfigWithBeanWithCustomName {
        static TestBean testBean = new TestBean(ConfigurationClassProcessingTests.ConfigWithBeanWithCustomName.class.getSimpleName());

        @Bean(name = "customName")
        public TestBean methodName() {
            return ConfigurationClassProcessingTests.ConfigWithBeanWithCustomName.testBean;
        }
    }

    @Configuration
    static class ConfigWithBeanWithCustomNameConfiguredViaValueAttribute {
        static TestBean testBean = new TestBean(ConfigurationClassProcessingTests.ConfigWithBeanWithCustomNameConfiguredViaValueAttribute.class.getSimpleName());

        @Bean("enigma")
        public TestBean methodName() {
            return ConfigurationClassProcessingTests.ConfigWithBeanWithCustomNameConfiguredViaValueAttribute.testBean;
        }
    }

    @Configuration
    static class ConfigWithBeanWithAliases {
        static TestBean testBean = new TestBean(ConfigurationClassProcessingTests.ConfigWithBeanWithAliases.class.getSimpleName());

        @Bean(name = { "name1", "alias1", "alias2", "alias3" })
        public TestBean methodName() {
            return ConfigurationClassProcessingTests.ConfigWithBeanWithAliases.testBean;
        }
    }

    @Configuration
    static class ConfigWithBeanWithAliasesConfiguredViaValueAttribute {
        static TestBean testBean = new TestBean(ConfigurationClassProcessingTests.ConfigWithBeanWithAliasesConfiguredViaValueAttribute.class.getSimpleName());

        @Bean({ "enigma", "alias1", "alias2", "alias3" })
        public TestBean methodName() {
            return ConfigurationClassProcessingTests.ConfigWithBeanWithAliasesConfiguredViaValueAttribute.testBean;
        }
    }

    @Configuration
    static class ConfigWithBeanWithProviderImplementation implements Provider<TestBean> {
        static TestBean testBean = new TestBean(ConfigurationClassProcessingTests.ConfigWithBeanWithProviderImplementation.class.getSimpleName());

        @Bean(name = "customName")
        public TestBean get() {
            return ConfigurationClassProcessingTests.ConfigWithBeanWithProviderImplementation.testBean;
        }
    }

    @Configuration
    static class ConfigWithSetWithProviderImplementation implements Provider<Set<String>> {
        static Set<String> set = Collections.singleton("value");

        @Bean(name = "customName")
        public Set<String> get() {
            return ConfigurationClassProcessingTests.ConfigWithSetWithProviderImplementation.set;
        }
    }

    @Configuration
    static class ConfigWithFinalBean {
        @Bean
        public final TestBean testBean() {
            return new TestBean();
        }
    }

    @Configuration
    static class SimplestPossibleConfig {
        @Bean
        public String stringBean() {
            return "foo";
        }
    }

    @Configuration
    static class ConfigWithNonSpecificReturnTypes {
        @Bean
        public Object stringBean() {
            return "foo";
        }

        @Bean
        public FactoryBean<?> factoryBean() {
            ListFactoryBean fb = new ListFactoryBean();
            fb.setSourceList(Arrays.asList("element1", "element2"));
            return fb;
        }
    }

    @Configuration
    static class ConfigWithPrototypeBean {
        @Bean
        public TestBean foo() {
            TestBean foo = new ConfigurationClassProcessingTests.SpousyTestBean("foo");
            foo.setSpouse(bar());
            return foo;
        }

        @Bean
        public TestBean bar() {
            TestBean bar = new ConfigurationClassProcessingTests.SpousyTestBean("bar");
            bar.setSpouse(baz());
            return bar;
        }

        @Bean
        @Scope("prototype")
        public TestBean baz() {
            return new TestBean("baz");
        }

        @Bean
        @Scope("prototype")
        public TestBean adaptive1(InjectionPoint ip) {
            return new TestBean(getName());
        }

        @Bean
        @Scope("prototype")
        public TestBean adaptive2(DependencyDescriptor dd) {
            return new TestBean(getName());
        }
    }

    @Configuration
    static class ConfigWithNullReference extends ConfigurationClassProcessingTests.ConfigWithPrototypeBean {
        @Override
        public TestBean bar() {
            return null;
        }
    }

    @Scope("prototype")
    static class AdaptiveInjectionPoints {
        @Autowired
        @Qualifier("adaptive1")
        public TestBean adaptiveInjectionPoint1;

        public TestBean adaptiveInjectionPoint2;

        @Autowired
        @Qualifier("adaptive2")
        public void setAdaptiveInjectionPoint2(TestBean adaptiveInjectionPoint2) {
            this.adaptiveInjectionPoint2 = adaptiveInjectionPoint2;
        }
    }

    @Scope("prototype")
    static class AdaptiveResourceInjectionPoints {
        @Resource(name = "adaptive1")
        public TestBean adaptiveInjectionPoint1;

        public TestBean adaptiveInjectionPoint2;

        @Resource(name = "adaptive2")
        public void setAdaptiveInjectionPoint2(TestBean adaptiveInjectionPoint2) {
            this.adaptiveInjectionPoint2 = adaptiveInjectionPoint2;
        }
    }

    static class ConfigWithPostProcessor extends ConfigurationClassProcessingTests.ConfigWithPrototypeBean {
        @Value("${myProp}")
        private String myProp;

        @Bean
        public ConfigurationClassProcessingTests.POBPP beanPostProcessor() {
            return new ConfigurationClassProcessingTests.POBPP() {
                String nameSuffix = "-processed-" + (myProp);

                public void setNameSuffix(String nameSuffix) {
                    this.nameSuffix = nameSuffix;
                }

                @Override
                public Object postProcessBeforeInitialization(Object bean, String beanName) {
                    if (bean instanceof ITestBean) {
                        ((ITestBean) (bean)).setName(((getName()) + (nameSuffix)));
                    }
                    return bean;
                }

                @Override
                public Object postProcessAfterInitialization(Object bean, String beanName) {
                    return bean;
                }

                public int getOrder() {
                    return 0;
                }
            };
        }

        // @Bean
        public BeanFactoryPostProcessor beanFactoryPostProcessor() {
            return new BeanFactoryPostProcessor() {
                @Override
                public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) {
                    BeanDefinition bd = beanFactory.getBeanDefinition("beanPostProcessor");
                    bd.getPropertyValues().addPropertyValue("nameSuffix", ("-processed-" + (myProp)));
                }
            };
        }

        @Bean
        public ITestBean listenerTestBean() {
            return new ConfigurationClassProcessingTests.SpousyTestBean("listener");
        }
    }

    public interface POBPP extends BeanPostProcessor {}

    private static class SpousyTestBean extends TestBean implements ApplicationListener<ContextRefreshedEvent> {
        public boolean refreshed = false;

        public SpousyTestBean(String name) {
            super(name);
        }

        @Override
        public void setSpouse(ITestBean spouse) {
            super.setSpouse(spouse);
        }

        @Override
        public void onApplicationEvent(ContextRefreshedEvent event) {
            this.refreshed = true;
        }
    }

    @Configuration
    static class ConfigWithFunctionalRegistration {
        @Autowired
        void register(GenericApplicationContext ctx) {
            ctx.registerBean("spouse", TestBean.class, () -> new TestBean("functional"));
            Supplier<TestBean> testBeanSupplier = () -> new TestBean(ctx.getBean("spouse", TestBean.class));
            ctx.registerBean(TestBean.class, testBeanSupplier, ( bd) -> bd.setPrimary(true));
        }

        @Bean
        public NestedTestBean nestedTestBean(TestBean testBean) {
            return new NestedTestBean(getName());
        }
    }
}

