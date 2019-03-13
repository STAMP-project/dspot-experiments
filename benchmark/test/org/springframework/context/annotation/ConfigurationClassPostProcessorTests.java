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
package org.springframework.context.annotation;


import ComponentScan.Filter;
import RootBeanDefinition.ROLE_SUPPORT;
import RootBeanDefinition.SCOPE_PROTOTYPE;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.aop.interceptor.SimpleTraceInterceptor;
import org.springframework.aop.scope.ScopedObject;
import org.springframework.aop.scope.ScopedProxyUtils;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor;
import org.springframework.beans.factory.annotation.Lookup;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.beans.factory.support.ChildBeanDefinition;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.core.ResolvableType;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.io.DescriptiveResource;
import org.springframework.stereotype.Component;
import org.springframework.tests.sample.beans.ITestBean;
import org.springframework.util.ObjectUtils;

import static ScopedProxyMode.INTERFACES;
import static ScopedProxyMode.TARGET_CLASS;


/**
 *
 *
 * @author Chris Beams
 * @author Juergen Hoeller
 * @author Sam Brannen
 */
public class ConfigurationClassPostProcessorTests {
    private final DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();

    /**
     * Enhanced {@link Configuration} classes are only necessary for respecting
     * certain bean semantics, like singleton-scoping, scoped proxies, etc.
     * <p>Technically, {@link ConfigurationClassPostProcessor} could fail to enhance the
     * registered Configuration classes and many use cases would still work.
     * Certain cases, however, like inter-bean singleton references would not.
     * We test for such a case below, and in doing so prove that enhancement is working.
     */
    @Test
    public void enhancementIsPresentBecauseSingletonSemanticsAreRespected() {
        beanFactory.registerBeanDefinition("config", new RootBeanDefinition(ConfigurationClassPostProcessorTests.SingletonBeanConfig.class));
        ConfigurationClassPostProcessor pp = new ConfigurationClassPostProcessor();
        pp.postProcessBeanFactory(beanFactory);
        ConfigurationClassPostProcessorTests.Foo foo = beanFactory.getBean("foo", ConfigurationClassPostProcessorTests.Foo.class);
        ConfigurationClassPostProcessorTests.Bar bar = beanFactory.getBean("bar", ConfigurationClassPostProcessorTests.Bar.class);
        Assert.assertSame(foo, bar.foo);
        Assert.assertTrue(Arrays.asList(beanFactory.getDependentBeans("foo")).contains("bar"));
    }

    @Test
    public void enhancementIsNotPresentForProxyBeanMethodsFlagSetToFalse() {
        beanFactory.registerBeanDefinition("config", new RootBeanDefinition(ConfigurationClassPostProcessorTests.NonEnhancedSingletonBeanConfig.class));
        ConfigurationClassPostProcessor pp = new ConfigurationClassPostProcessor();
        pp.postProcessBeanFactory(beanFactory);
        ConfigurationClassPostProcessorTests.Foo foo = beanFactory.getBean("foo", ConfigurationClassPostProcessorTests.Foo.class);
        ConfigurationClassPostProcessorTests.Bar bar = beanFactory.getBean("bar", ConfigurationClassPostProcessorTests.Bar.class);
        Assert.assertNotSame(foo, bar.foo);
    }

    @Test
    public void configurationIntrospectionOfInnerClassesWorksWithDotNameSyntax() {
        beanFactory.registerBeanDefinition("config", new RootBeanDefinition(((getClass().getName()) + ".SingletonBeanConfig")));
        ConfigurationClassPostProcessor pp = new ConfigurationClassPostProcessor();
        pp.postProcessBeanFactory(beanFactory);
        ConfigurationClassPostProcessorTests.Foo foo = beanFactory.getBean("foo", ConfigurationClassPostProcessorTests.Foo.class);
        ConfigurationClassPostProcessorTests.Bar bar = beanFactory.getBean("bar", ConfigurationClassPostProcessorTests.Bar.class);
        Assert.assertSame(foo, bar.foo);
    }

    /**
     * Tests the fix for SPR-5655, a special workaround that prefers reflection over ASM
     * if a bean class is already loaded.
     */
    @Test
    public void alreadyLoadedConfigurationClasses() {
        beanFactory.registerBeanDefinition("unloadedConfig", new RootBeanDefinition(ConfigurationClassPostProcessorTests.UnloadedConfig.class.getName()));
        beanFactory.registerBeanDefinition("loadedConfig", new RootBeanDefinition(ConfigurationClassPostProcessorTests.LoadedConfig.class));
        ConfigurationClassPostProcessor pp = new ConfigurationClassPostProcessor();
        pp.postProcessBeanFactory(beanFactory);
        beanFactory.getBean("foo");
        beanFactory.getBean("bar");
    }

    /**
     * Tests whether a bean definition without a specified bean class is handled correctly.
     */
    @Test
    public void postProcessorIntrospectsInheritedDefinitionsCorrectly() {
        beanFactory.registerBeanDefinition("config", new RootBeanDefinition(ConfigurationClassPostProcessorTests.SingletonBeanConfig.class));
        beanFactory.registerBeanDefinition("parent", new RootBeanDefinition(TestBean.class));
        beanFactory.registerBeanDefinition("child", new ChildBeanDefinition("parent"));
        ConfigurationClassPostProcessor pp = new ConfigurationClassPostProcessor();
        pp.postProcessBeanFactory(beanFactory);
        ConfigurationClassPostProcessorTests.Foo foo = beanFactory.getBean("foo", ConfigurationClassPostProcessorTests.Foo.class);
        ConfigurationClassPostProcessorTests.Bar bar = beanFactory.getBean("bar", ConfigurationClassPostProcessorTests.Bar.class);
        Assert.assertSame(foo, bar.foo);
    }

    @Test
    public void postProcessorWorksWithComposedConfigurationUsingReflection() {
        RootBeanDefinition beanDefinition = new RootBeanDefinition(ConfigurationClassPostProcessorTests.ComposedConfigurationClass.class);
        assertSupportForComposedAnnotation(beanDefinition);
    }

    @Test
    public void postProcessorWorksWithComposedConfigurationUsingAsm() {
        RootBeanDefinition beanDefinition = new RootBeanDefinition(ConfigurationClassPostProcessorTests.ComposedConfigurationClass.class.getName());
        assertSupportForComposedAnnotation(beanDefinition);
    }

    @Test
    public void postProcessorWorksWithComposedConfigurationWithAttributeOverrideForBasePackageUsingReflection() {
        RootBeanDefinition beanDefinition = new RootBeanDefinition(ConfigurationClassPostProcessorTests.ComposedConfigurationWithAttributeOverrideForBasePackage.class);
        assertSupportForComposedAnnotation(beanDefinition);
    }

    @Test
    public void postProcessorWorksWithComposedConfigurationWithAttributeOverrideForBasePackageUsingAsm() {
        RootBeanDefinition beanDefinition = new RootBeanDefinition(ConfigurationClassPostProcessorTests.ComposedConfigurationWithAttributeOverrideForBasePackage.class.getName());
        assertSupportForComposedAnnotation(beanDefinition);
    }

    @Test
    public void postProcessorWorksWithComposedConfigurationWithAttributeOverrideForExcludeFilterUsingReflection() {
        RootBeanDefinition beanDefinition = new RootBeanDefinition(ConfigurationClassPostProcessorTests.ComposedConfigurationWithAttributeOverrideForExcludeFilter.class);
        assertSupportForComposedAnnotationWithExclude(beanDefinition);
    }

    @Test
    public void postProcessorWorksWithComposedConfigurationWithAttributeOverrideForExcludeFilterUsingAsm() {
        RootBeanDefinition beanDefinition = new RootBeanDefinition(ConfigurationClassPostProcessorTests.ComposedConfigurationWithAttributeOverrideForExcludeFilter.class.getName());
        assertSupportForComposedAnnotationWithExclude(beanDefinition);
    }

    @Test
    public void postProcessorWorksWithExtendedConfigurationWithAttributeOverrideForExcludesFilterUsingReflection() {
        RootBeanDefinition beanDefinition = new RootBeanDefinition(ConfigurationClassPostProcessorTests.ExtendedConfigurationWithAttributeOverrideForExcludeFilter.class);
        assertSupportForComposedAnnotationWithExclude(beanDefinition);
    }

    @Test
    public void postProcessorWorksWithExtendedConfigurationWithAttributeOverrideForExcludesFilterUsingAsm() {
        RootBeanDefinition beanDefinition = new RootBeanDefinition(ConfigurationClassPostProcessorTests.ExtendedConfigurationWithAttributeOverrideForExcludeFilter.class.getName());
        assertSupportForComposedAnnotationWithExclude(beanDefinition);
    }

    @Test
    public void postProcessorWorksWithComposedComposedConfigurationWithAttributeOverridesUsingReflection() {
        RootBeanDefinition beanDefinition = new RootBeanDefinition(ConfigurationClassPostProcessorTests.ComposedComposedConfigurationWithAttributeOverridesClass.class);
        assertSupportForComposedAnnotation(beanDefinition);
    }

    @Test
    public void postProcessorWorksWithComposedComposedConfigurationWithAttributeOverridesUsingAsm() {
        RootBeanDefinition beanDefinition = new RootBeanDefinition(ConfigurationClassPostProcessorTests.ComposedComposedConfigurationWithAttributeOverridesClass.class.getName());
        assertSupportForComposedAnnotation(beanDefinition);
    }

    @Test
    public void postProcessorWorksWithMetaComponentScanConfigurationWithAttributeOverridesUsingReflection() {
        RootBeanDefinition beanDefinition = new RootBeanDefinition(ConfigurationClassPostProcessorTests.MetaComponentScanConfigurationWithAttributeOverridesClass.class);
        assertSupportForComposedAnnotation(beanDefinition);
    }

    @Test
    public void postProcessorWorksWithMetaComponentScanConfigurationWithAttributeOverridesUsingAsm() {
        RootBeanDefinition beanDefinition = new RootBeanDefinition(ConfigurationClassPostProcessorTests.MetaComponentScanConfigurationWithAttributeOverridesClass.class.getName());
        assertSupportForComposedAnnotation(beanDefinition);
    }

    @Test
    public void postProcessorWorksWithMetaComponentScanConfigurationWithAttributeOverridesSubclassUsingReflection() {
        RootBeanDefinition beanDefinition = new RootBeanDefinition(ConfigurationClassPostProcessorTests.SubMetaComponentScanConfigurationWithAttributeOverridesClass.class);
        assertSupportForComposedAnnotation(beanDefinition);
    }

    @Test
    public void postProcessorWorksWithMetaComponentScanConfigurationWithAttributeOverridesSubclassUsingAsm() {
        RootBeanDefinition beanDefinition = new RootBeanDefinition(ConfigurationClassPostProcessorTests.SubMetaComponentScanConfigurationWithAttributeOverridesClass.class.getName());
        assertSupportForComposedAnnotation(beanDefinition);
    }

    @Test
    public void postProcessorOverridesNonApplicationBeanDefinitions() {
        RootBeanDefinition rbd = new RootBeanDefinition(TestBean.class);
        rbd.setRole(ROLE_SUPPORT);
        beanFactory.registerBeanDefinition("bar", rbd);
        beanFactory.registerBeanDefinition("config", new RootBeanDefinition(ConfigurationClassPostProcessorTests.SingletonBeanConfig.class));
        ConfigurationClassPostProcessor pp = new ConfigurationClassPostProcessor();
        pp.postProcessBeanFactory(beanFactory);
        ConfigurationClassPostProcessorTests.Foo foo = beanFactory.getBean("foo", ConfigurationClassPostProcessorTests.Foo.class);
        ConfigurationClassPostProcessorTests.Bar bar = beanFactory.getBean("bar", ConfigurationClassPostProcessorTests.Bar.class);
        Assert.assertSame(foo, bar.foo);
    }

    @Test
    public void postProcessorDoesNotOverrideRegularBeanDefinitions() {
        RootBeanDefinition rbd = new RootBeanDefinition(TestBean.class);
        rbd.setResource(new DescriptiveResource("XML or something"));
        beanFactory.registerBeanDefinition("bar", rbd);
        beanFactory.registerBeanDefinition("config", new RootBeanDefinition(ConfigurationClassPostProcessorTests.SingletonBeanConfig.class));
        ConfigurationClassPostProcessor pp = new ConfigurationClassPostProcessor();
        pp.postProcessBeanFactory(beanFactory);
        beanFactory.getBean("foo", ConfigurationClassPostProcessorTests.Foo.class);
        beanFactory.getBean("bar", TestBean.class);
    }

    @Test
    public void postProcessorDoesNotOverrideRegularBeanDefinitionsEvenWithScopedProxy() {
        RootBeanDefinition rbd = new RootBeanDefinition(TestBean.class);
        rbd.setResource(new DescriptiveResource("XML or something"));
        BeanDefinitionHolder proxied = ScopedProxyUtils.createScopedProxy(new BeanDefinitionHolder(rbd, "bar"), beanFactory, true);
        beanFactory.registerBeanDefinition("bar", proxied.getBeanDefinition());
        beanFactory.registerBeanDefinition("config", new RootBeanDefinition(ConfigurationClassPostProcessorTests.SingletonBeanConfig.class));
        ConfigurationClassPostProcessor pp = new ConfigurationClassPostProcessor();
        pp.postProcessBeanFactory(beanFactory);
        beanFactory.getBean("foo", ConfigurationClassPostProcessorTests.Foo.class);
        beanFactory.getBean("bar", TestBean.class);
    }

    @Test
    public void postProcessorFailsOnImplicitOverrideIfOverridingIsNotAllowed() {
        RootBeanDefinition rbd = new RootBeanDefinition(TestBean.class);
        rbd.setResource(new DescriptiveResource("XML or something"));
        beanFactory.registerBeanDefinition("bar", rbd);
        beanFactory.registerBeanDefinition("config", new RootBeanDefinition(ConfigurationClassPostProcessorTests.SingletonBeanConfig.class));
        beanFactory.setAllowBeanDefinitionOverriding(false);
        ConfigurationClassPostProcessor pp = new ConfigurationClassPostProcessor();
        try {
            pp.postProcessBeanFactory(beanFactory);
            Assert.fail("Should have thrown BeanDefinitionStoreException");
        } catch (BeanDefinitionStoreException ex) {
            Assert.assertTrue(ex.getMessage().contains("bar"));
            Assert.assertTrue(ex.getMessage().contains("SingletonBeanConfig"));
            Assert.assertTrue(ex.getMessage().contains(TestBean.class.getName()));
        }
    }

    @Test
    public void configurationClassesProcessedInCorrectOrder() {
        beanFactory.registerBeanDefinition("config1", new RootBeanDefinition(ConfigurationClassPostProcessorTests.OverridingSingletonBeanConfig.class));
        beanFactory.registerBeanDefinition("config2", new RootBeanDefinition(ConfigurationClassPostProcessorTests.SingletonBeanConfig.class));
        ConfigurationClassPostProcessor pp = new ConfigurationClassPostProcessor();
        pp.postProcessBeanFactory(beanFactory);
        ConfigurationClassPostProcessorTests.Foo foo = beanFactory.getBean(ConfigurationClassPostProcessorTests.Foo.class);
        Assert.assertTrue((foo instanceof ConfigurationClassPostProcessorTests.ExtendedFoo));
        ConfigurationClassPostProcessorTests.Bar bar = beanFactory.getBean(ConfigurationClassPostProcessorTests.Bar.class);
        Assert.assertSame(foo, bar.foo);
    }

    @Test
    public void configurationClassesWithValidOverridingForProgrammaticCall() {
        beanFactory.registerBeanDefinition("config1", new RootBeanDefinition(ConfigurationClassPostProcessorTests.OverridingAgainSingletonBeanConfig.class));
        beanFactory.registerBeanDefinition("config2", new RootBeanDefinition(ConfigurationClassPostProcessorTests.OverridingSingletonBeanConfig.class));
        beanFactory.registerBeanDefinition("config3", new RootBeanDefinition(ConfigurationClassPostProcessorTests.SingletonBeanConfig.class));
        ConfigurationClassPostProcessor pp = new ConfigurationClassPostProcessor();
        pp.postProcessBeanFactory(beanFactory);
        ConfigurationClassPostProcessorTests.Foo foo = beanFactory.getBean(ConfigurationClassPostProcessorTests.Foo.class);
        Assert.assertTrue((foo instanceof ConfigurationClassPostProcessorTests.ExtendedAgainFoo));
        ConfigurationClassPostProcessorTests.Bar bar = beanFactory.getBean(ConfigurationClassPostProcessorTests.Bar.class);
        Assert.assertSame(foo, bar.foo);
    }

    @Test
    public void configurationClassesWithInvalidOverridingForProgrammaticCall() {
        beanFactory.registerBeanDefinition("config1", new RootBeanDefinition(ConfigurationClassPostProcessorTests.InvalidOverridingSingletonBeanConfig.class));
        beanFactory.registerBeanDefinition("config2", new RootBeanDefinition(ConfigurationClassPostProcessorTests.OverridingSingletonBeanConfig.class));
        beanFactory.registerBeanDefinition("config3", new RootBeanDefinition(ConfigurationClassPostProcessorTests.SingletonBeanConfig.class));
        ConfigurationClassPostProcessor pp = new ConfigurationClassPostProcessor();
        pp.postProcessBeanFactory(beanFactory);
        try {
            beanFactory.getBean(ConfigurationClassPostProcessorTests.Bar.class);
            Assert.fail("Should have thrown BeanCreationException");
        } catch (BeanCreationException ex) {
            Assert.assertTrue(ex.getMessage().contains("OverridingSingletonBeanConfig.foo"));
            Assert.assertTrue(ex.getMessage().contains(ConfigurationClassPostProcessorTests.ExtendedFoo.class.getName()));
            Assert.assertTrue(ex.getMessage().contains(ConfigurationClassPostProcessorTests.Foo.class.getName()));
            Assert.assertTrue(ex.getMessage().contains("InvalidOverridingSingletonBeanConfig"));
        }
    }

    // SPR-15384
    @Test
    public void nestedConfigurationClassesProcessedInCorrectOrder() {
        beanFactory.registerBeanDefinition("config", new RootBeanDefinition(ConfigurationClassPostProcessorTests.ConfigWithOrderedNestedClasses.class));
        ConfigurationClassPostProcessor pp = new ConfigurationClassPostProcessor();
        pp.postProcessBeanFactory(beanFactory);
        ConfigurationClassPostProcessorTests.Foo foo = beanFactory.getBean(ConfigurationClassPostProcessorTests.Foo.class);
        Assert.assertTrue((foo instanceof ConfigurationClassPostProcessorTests.ExtendedFoo));
        ConfigurationClassPostProcessorTests.Bar bar = beanFactory.getBean(ConfigurationClassPostProcessorTests.Bar.class);
        Assert.assertSame(foo, bar.foo);
    }

    // SPR-16734
    @Test
    public void innerConfigurationClassesProcessedInCorrectOrder() {
        beanFactory.registerBeanDefinition("config", new RootBeanDefinition(ConfigurationClassPostProcessorTests.ConfigWithOrderedInnerClasses.class));
        ConfigurationClassPostProcessor pp = new ConfigurationClassPostProcessor();
        pp.postProcessBeanFactory(beanFactory);
        beanFactory.addBeanPostProcessor(new AutowiredAnnotationBeanPostProcessor());
        ConfigurationClassPostProcessorTests.Foo foo = beanFactory.getBean(ConfigurationClassPostProcessorTests.Foo.class);
        Assert.assertTrue((foo instanceof ConfigurationClassPostProcessorTests.ExtendedFoo));
        ConfigurationClassPostProcessorTests.Bar bar = beanFactory.getBean(ConfigurationClassPostProcessorTests.Bar.class);
        Assert.assertSame(foo, bar.foo);
    }

    @Test
    public void scopedProxyTargetMarkedAsNonAutowireCandidate() {
        AutowiredAnnotationBeanPostProcessor bpp = new AutowiredAnnotationBeanPostProcessor();
        bpp.setBeanFactory(beanFactory);
        beanFactory.addBeanPostProcessor(bpp);
        beanFactory.registerBeanDefinition("config", new RootBeanDefinition(ConfigurationClassPostProcessorTests.ScopedProxyConfigurationClass.class));
        beanFactory.registerBeanDefinition("consumer", new RootBeanDefinition(ConfigurationClassPostProcessorTests.ScopedProxyConsumer.class));
        ConfigurationClassPostProcessor pp = new ConfigurationClassPostProcessor();
        pp.postProcessBeanFactory(beanFactory);
        ITestBean injected = beanFactory.getBean("consumer", ConfigurationClassPostProcessorTests.ScopedProxyConsumer.class).testBean;
        Assert.assertTrue((injected instanceof ScopedObject));
        Assert.assertSame(beanFactory.getBean("scopedClass"), injected);
        Assert.assertSame(beanFactory.getBean(ITestBean.class), injected);
    }

    @Test
    public void processingAllowedOnlyOncePerProcessorRegistryPair() {
        DefaultListableBeanFactory bf1 = new DefaultListableBeanFactory();
        DefaultListableBeanFactory bf2 = new DefaultListableBeanFactory();
        ConfigurationClassPostProcessor pp = new ConfigurationClassPostProcessor();
        pp.postProcessBeanFactory(bf1);// first invocation -- should succeed

        try {
            pp.postProcessBeanFactory(bf1);// second invocation for bf1 -- should throw

            Assert.fail("expected exception");
        } catch (IllegalStateException ex) {
        }
        pp.postProcessBeanFactory(bf2);// first invocation for bf2 -- should succeed

        try {
            pp.postProcessBeanFactory(bf2);// second invocation for bf2 -- should throw

            Assert.fail("expected exception");
        } catch (IllegalStateException ex) {
        }
    }

    @Test
    public void genericsBasedInjection() {
        AutowiredAnnotationBeanPostProcessor bpp = new AutowiredAnnotationBeanPostProcessor();
        bpp.setBeanFactory(beanFactory);
        beanFactory.addBeanPostProcessor(bpp);
        RootBeanDefinition bd = new RootBeanDefinition(ConfigurationClassPostProcessorTests.RepositoryInjectionBean.class);
        bd.setScope(SCOPE_PROTOTYPE);
        beanFactory.registerBeanDefinition("annotatedBean", bd);
        beanFactory.registerBeanDefinition("configClass", new RootBeanDefinition(ConfigurationClassPostProcessorTests.RepositoryConfiguration.class));
        ConfigurationClassPostProcessor pp = new ConfigurationClassPostProcessor();
        pp.postProcessBeanFactory(beanFactory);
        ConfigurationClassPostProcessorTests.RepositoryInjectionBean bean = ((ConfigurationClassPostProcessorTests.RepositoryInjectionBean) (beanFactory.getBean("annotatedBean")));
        Assert.assertEquals("Repository<String>", bean.stringRepository.toString());
        Assert.assertEquals("Repository<Integer>", bean.integerRepository.toString());
    }

    @Test
    public void genericsBasedInjectionWithScoped() {
        AutowiredAnnotationBeanPostProcessor bpp = new AutowiredAnnotationBeanPostProcessor();
        bpp.setBeanFactory(beanFactory);
        beanFactory.addBeanPostProcessor(bpp);
        RootBeanDefinition bd = new RootBeanDefinition(ConfigurationClassPostProcessorTests.RepositoryInjectionBean.class);
        bd.setScope(SCOPE_PROTOTYPE);
        beanFactory.registerBeanDefinition("annotatedBean", bd);
        beanFactory.registerBeanDefinition("configClass", new RootBeanDefinition(ConfigurationClassPostProcessorTests.ScopedRepositoryConfiguration.class));
        ConfigurationClassPostProcessor pp = new ConfigurationClassPostProcessor();
        pp.postProcessBeanFactory(beanFactory);
        ConfigurationClassPostProcessorTests.RepositoryInjectionBean bean = ((ConfigurationClassPostProcessorTests.RepositoryInjectionBean) (beanFactory.getBean("annotatedBean")));
        Assert.assertEquals("Repository<String>", bean.stringRepository.toString());
        Assert.assertEquals("Repository<Integer>", bean.integerRepository.toString());
    }

    @Test
    public void genericsBasedInjectionWithScopedProxy() {
        AutowiredAnnotationBeanPostProcessor bpp = new AutowiredAnnotationBeanPostProcessor();
        bpp.setBeanFactory(beanFactory);
        beanFactory.addBeanPostProcessor(bpp);
        RootBeanDefinition bd = new RootBeanDefinition(ConfigurationClassPostProcessorTests.RepositoryInjectionBean.class);
        bd.setScope(SCOPE_PROTOTYPE);
        beanFactory.registerBeanDefinition("annotatedBean", bd);
        beanFactory.registerBeanDefinition("configClass", new RootBeanDefinition(ConfigurationClassPostProcessorTests.ScopedProxyRepositoryConfiguration.class));
        ConfigurationClassPostProcessor pp = new ConfigurationClassPostProcessor();
        pp.postProcessBeanFactory(beanFactory);
        beanFactory.freezeConfiguration();
        ConfigurationClassPostProcessorTests.RepositoryInjectionBean bean = ((ConfigurationClassPostProcessorTests.RepositoryInjectionBean) (beanFactory.getBean("annotatedBean")));
        Assert.assertEquals("Repository<String>", bean.stringRepository.toString());
        Assert.assertEquals("Repository<Integer>", bean.integerRepository.toString());
        Assert.assertTrue(AopUtils.isCglibProxy(bean.stringRepository));
        Assert.assertTrue(AopUtils.isCglibProxy(bean.integerRepository));
    }

    @Test
    public void genericsBasedInjectionWithScopedProxyUsingAsm() {
        AutowiredAnnotationBeanPostProcessor bpp = new AutowiredAnnotationBeanPostProcessor();
        bpp.setBeanFactory(beanFactory);
        beanFactory.addBeanPostProcessor(bpp);
        RootBeanDefinition bd = new RootBeanDefinition(ConfigurationClassPostProcessorTests.RepositoryInjectionBean.class.getName());
        bd.setScope(SCOPE_PROTOTYPE);
        beanFactory.registerBeanDefinition("annotatedBean", bd);
        beanFactory.registerBeanDefinition("configClass", new RootBeanDefinition(ConfigurationClassPostProcessorTests.ScopedProxyRepositoryConfiguration.class.getName()));
        ConfigurationClassPostProcessor pp = new ConfigurationClassPostProcessor();
        pp.postProcessBeanFactory(beanFactory);
        beanFactory.freezeConfiguration();
        ConfigurationClassPostProcessorTests.RepositoryInjectionBean bean = ((ConfigurationClassPostProcessorTests.RepositoryInjectionBean) (beanFactory.getBean("annotatedBean")));
        Assert.assertEquals("Repository<String>", bean.stringRepository.toString());
        Assert.assertEquals("Repository<Integer>", bean.integerRepository.toString());
        Assert.assertTrue(AopUtils.isCglibProxy(bean.stringRepository));
        Assert.assertTrue(AopUtils.isCglibProxy(bean.integerRepository));
    }

    @Test
    public void genericsBasedInjectionWithImplTypeAtInjectionPoint() {
        AutowiredAnnotationBeanPostProcessor bpp = new AutowiredAnnotationBeanPostProcessor();
        bpp.setBeanFactory(beanFactory);
        beanFactory.addBeanPostProcessor(bpp);
        RootBeanDefinition bd = new RootBeanDefinition(ConfigurationClassPostProcessorTests.SpecificRepositoryInjectionBean.class);
        bd.setScope(SCOPE_PROTOTYPE);
        beanFactory.registerBeanDefinition("annotatedBean", bd);
        beanFactory.registerBeanDefinition("configClass", new RootBeanDefinition(ConfigurationClassPostProcessorTests.SpecificRepositoryConfiguration.class));
        ConfigurationClassPostProcessor pp = new ConfigurationClassPostProcessor();
        pp.postProcessBeanFactory(beanFactory);
        beanFactory.preInstantiateSingletons();
        ConfigurationClassPostProcessorTests.SpecificRepositoryInjectionBean bean = ((ConfigurationClassPostProcessorTests.SpecificRepositoryInjectionBean) (beanFactory.getBean("annotatedBean")));
        Assert.assertSame(beanFactory.getBean("genericRepo"), bean.genericRepository);
    }

    @Test
    public void genericsBasedInjectionWithFactoryBean() {
        AutowiredAnnotationBeanPostProcessor bpp = new AutowiredAnnotationBeanPostProcessor();
        bpp.setBeanFactory(beanFactory);
        beanFactory.addBeanPostProcessor(bpp);
        RootBeanDefinition bd = new RootBeanDefinition(ConfigurationClassPostProcessorTests.RepositoryFactoryBeanInjectionBean.class);
        bd.setScope(SCOPE_PROTOTYPE);
        beanFactory.registerBeanDefinition("annotatedBean", bd);
        beanFactory.registerBeanDefinition("configClass", new RootBeanDefinition(ConfigurationClassPostProcessorTests.RepositoryFactoryBeanConfiguration.class));
        ConfigurationClassPostProcessor pp = new ConfigurationClassPostProcessor();
        pp.postProcessBeanFactory(beanFactory);
        beanFactory.preInstantiateSingletons();
        ConfigurationClassPostProcessorTests.RepositoryFactoryBeanInjectionBean bean = ((ConfigurationClassPostProcessorTests.RepositoryFactoryBeanInjectionBean) (beanFactory.getBean("annotatedBean")));
        Assert.assertSame(beanFactory.getBean("&repoFactoryBean"), bean.repositoryFactoryBean);
        Assert.assertSame(beanFactory.getBean("&repoFactoryBean"), bean.qualifiedRepositoryFactoryBean);
        Assert.assertSame(beanFactory.getBean("&repoFactoryBean"), bean.prefixQualifiedRepositoryFactoryBean);
    }

    @Test
    public void genericsBasedInjectionWithRawMatch() {
        beanFactory.registerBeanDefinition("configClass", new RootBeanDefinition(ConfigurationClassPostProcessorTests.RawMatchingConfiguration.class));
        ConfigurationClassPostProcessor pp = new ConfigurationClassPostProcessor();
        pp.postProcessBeanFactory(beanFactory);
        Assert.assertSame(beanFactory.getBean("rawRepo"), beanFactory.getBean("repoConsumer"));
    }

    @Test
    public void genericsBasedInjectionWithWildcardMatch() {
        beanFactory.registerBeanDefinition("configClass", new RootBeanDefinition(ConfigurationClassPostProcessorTests.WildcardMatchingConfiguration.class));
        ConfigurationClassPostProcessor pp = new ConfigurationClassPostProcessor();
        pp.postProcessBeanFactory(beanFactory);
        Assert.assertSame(beanFactory.getBean("genericRepo"), beanFactory.getBean("repoConsumer"));
    }

    @Test
    public void genericsBasedInjectionWithWildcardWithExtendsMatch() {
        beanFactory.registerBeanDefinition("configClass", new RootBeanDefinition(ConfigurationClassPostProcessorTests.WildcardWithExtendsConfiguration.class));
        new ConfigurationClassPostProcessor().postProcessBeanFactory(beanFactory);
        Assert.assertSame(beanFactory.getBean("stringRepo"), beanFactory.getBean("repoConsumer"));
    }

    @Test
    public void genericsBasedInjectionWithWildcardWithGenericExtendsMatch() {
        beanFactory.registerBeanDefinition("configClass", new RootBeanDefinition(ConfigurationClassPostProcessorTests.WildcardWithGenericExtendsConfiguration.class));
        new ConfigurationClassPostProcessor().postProcessBeanFactory(beanFactory);
        Assert.assertSame(beanFactory.getBean("genericRepo"), beanFactory.getBean("repoConsumer"));
    }

    @Test
    public void genericsBasedInjectionWithEarlyGenericsMatching() {
        beanFactory.registerBeanDefinition("configClass", new RootBeanDefinition(ConfigurationClassPostProcessorTests.RepositoryConfiguration.class));
        new ConfigurationClassPostProcessor().postProcessBeanFactory(beanFactory);
        String[] beanNames = beanFactory.getBeanNamesForType(ConfigurationClassPostProcessorTests.Repository.class);
        Assert.assertTrue(ObjectUtils.containsElement(beanNames, "stringRepo"));
        beanNames = beanFactory.getBeanNamesForType(ResolvableType.forClassWithGenerics(ConfigurationClassPostProcessorTests.Repository.class, String.class));
        Assert.assertEquals(1, beanNames.length);
        Assert.assertEquals("stringRepo", beanNames[0]);
        beanNames = beanFactory.getBeanNamesForType(ResolvableType.forClassWithGenerics(ConfigurationClassPostProcessorTests.Repository.class, String.class));
        Assert.assertEquals(1, beanNames.length);
        Assert.assertEquals("stringRepo", beanNames[0]);
    }

    @Test
    public void genericsBasedInjectionWithLateGenericsMatching() {
        beanFactory.registerBeanDefinition("configClass", new RootBeanDefinition(ConfigurationClassPostProcessorTests.RepositoryConfiguration.class));
        new ConfigurationClassPostProcessor().postProcessBeanFactory(beanFactory);
        beanFactory.preInstantiateSingletons();
        String[] beanNames = beanFactory.getBeanNamesForType(ConfigurationClassPostProcessorTests.Repository.class);
        Assert.assertTrue(ObjectUtils.containsElement(beanNames, "stringRepo"));
        beanNames = beanFactory.getBeanNamesForType(ResolvableType.forClassWithGenerics(ConfigurationClassPostProcessorTests.Repository.class, String.class));
        Assert.assertEquals(1, beanNames.length);
        Assert.assertEquals("stringRepo", beanNames[0]);
        beanNames = beanFactory.getBeanNamesForType(ResolvableType.forClassWithGenerics(ConfigurationClassPostProcessorTests.Repository.class, String.class));
        Assert.assertEquals(1, beanNames.length);
        Assert.assertEquals("stringRepo", beanNames[0]);
    }

    @Test
    public void genericsBasedInjectionWithEarlyGenericsMatchingAndRawFactoryMethod() {
        beanFactory.registerBeanDefinition("configClass", new RootBeanDefinition(ConfigurationClassPostProcessorTests.RawFactoryMethodRepositoryConfiguration.class));
        new ConfigurationClassPostProcessor().postProcessBeanFactory(beanFactory);
        String[] beanNames = beanFactory.getBeanNamesForType(ConfigurationClassPostProcessorTests.Repository.class);
        Assert.assertTrue(ObjectUtils.containsElement(beanNames, "stringRepo"));
        beanNames = beanFactory.getBeanNamesForType(ResolvableType.forClassWithGenerics(ConfigurationClassPostProcessorTests.Repository.class, String.class));
        Assert.assertEquals(0, beanNames.length);
        beanNames = beanFactory.getBeanNamesForType(ResolvableType.forClassWithGenerics(ConfigurationClassPostProcessorTests.Repository.class, String.class));
        Assert.assertEquals(0, beanNames.length);
    }

    @Test
    public void genericsBasedInjectionWithLateGenericsMatchingAndRawFactoryMethod() {
        beanFactory.registerBeanDefinition("configClass", new RootBeanDefinition(ConfigurationClassPostProcessorTests.RawFactoryMethodRepositoryConfiguration.class));
        new ConfigurationClassPostProcessor().postProcessBeanFactory(beanFactory);
        beanFactory.preInstantiateSingletons();
        String[] beanNames = beanFactory.getBeanNamesForType(ConfigurationClassPostProcessorTests.Repository.class);
        Assert.assertTrue(ObjectUtils.containsElement(beanNames, "stringRepo"));
        beanNames = beanFactory.getBeanNamesForType(ResolvableType.forClassWithGenerics(ConfigurationClassPostProcessorTests.Repository.class, String.class));
        Assert.assertEquals(1, beanNames.length);
        Assert.assertEquals("stringRepo", beanNames[0]);
        beanNames = beanFactory.getBeanNamesForType(ResolvableType.forClassWithGenerics(ConfigurationClassPostProcessorTests.Repository.class, String.class));
        Assert.assertEquals(1, beanNames.length);
        Assert.assertEquals("stringRepo", beanNames[0]);
    }

    @Test
    public void genericsBasedInjectionWithEarlyGenericsMatchingAndRawInstance() {
        beanFactory.registerBeanDefinition("configClass", new RootBeanDefinition(ConfigurationClassPostProcessorTests.RawInstanceRepositoryConfiguration.class));
        new ConfigurationClassPostProcessor().postProcessBeanFactory(beanFactory);
        String[] beanNames = beanFactory.getBeanNamesForType(ConfigurationClassPostProcessorTests.Repository.class);
        Assert.assertTrue(ObjectUtils.containsElement(beanNames, "stringRepo"));
        beanNames = beanFactory.getBeanNamesForType(ResolvableType.forClassWithGenerics(ConfigurationClassPostProcessorTests.Repository.class, String.class));
        Assert.assertEquals(1, beanNames.length);
        Assert.assertEquals("stringRepo", beanNames[0]);
        beanNames = beanFactory.getBeanNamesForType(ResolvableType.forClassWithGenerics(ConfigurationClassPostProcessorTests.Repository.class, String.class));
        Assert.assertEquals(1, beanNames.length);
        Assert.assertEquals("stringRepo", beanNames[0]);
    }

    @Test
    public void genericsBasedInjectionWithLateGenericsMatchingAndRawInstance() {
        beanFactory.registerBeanDefinition("configClass", new RootBeanDefinition(ConfigurationClassPostProcessorTests.RawInstanceRepositoryConfiguration.class));
        new ConfigurationClassPostProcessor().postProcessBeanFactory(beanFactory);
        beanFactory.preInstantiateSingletons();
        String[] beanNames = beanFactory.getBeanNamesForType(ConfigurationClassPostProcessorTests.Repository.class);
        Assert.assertTrue(ObjectUtils.containsElement(beanNames, "stringRepo"));
        beanNames = beanFactory.getBeanNamesForType(ResolvableType.forClassWithGenerics(ConfigurationClassPostProcessorTests.Repository.class, String.class));
        Assert.assertEquals(1, beanNames.length);
        Assert.assertEquals("stringRepo", beanNames[0]);
        beanNames = beanFactory.getBeanNamesForType(ResolvableType.forClassWithGenerics(ConfigurationClassPostProcessorTests.Repository.class, String.class));
        Assert.assertEquals(1, beanNames.length);
        Assert.assertEquals("stringRepo", beanNames[0]);
    }

    @Test
    public void genericsBasedInjectionWithEarlyGenericsMatchingOnCglibProxy() {
        beanFactory.registerBeanDefinition("configClass", new RootBeanDefinition(ConfigurationClassPostProcessorTests.RepositoryConfiguration.class));
        new ConfigurationClassPostProcessor().postProcessBeanFactory(beanFactory);
        DefaultAdvisorAutoProxyCreator autoProxyCreator = new DefaultAdvisorAutoProxyCreator();
        autoProxyCreator.setProxyTargetClass(true);
        autoProxyCreator.setBeanFactory(beanFactory);
        beanFactory.addBeanPostProcessor(autoProxyCreator);
        beanFactory.registerSingleton("traceInterceptor", new org.springframework.aop.support.DefaultPointcutAdvisor(new SimpleTraceInterceptor()));
        String[] beanNames = beanFactory.getBeanNamesForType(ConfigurationClassPostProcessorTests.Repository.class);
        Assert.assertTrue(ObjectUtils.containsElement(beanNames, "stringRepo"));
        beanNames = beanFactory.getBeanNamesForType(ResolvableType.forClassWithGenerics(ConfigurationClassPostProcessorTests.Repository.class, String.class));
        Assert.assertEquals(1, beanNames.length);
        Assert.assertEquals("stringRepo", beanNames[0]);
        beanNames = beanFactory.getBeanNamesForType(ResolvableType.forClassWithGenerics(ConfigurationClassPostProcessorTests.Repository.class, String.class));
        Assert.assertEquals(1, beanNames.length);
        Assert.assertEquals("stringRepo", beanNames[0]);
        Assert.assertTrue(AopUtils.isCglibProxy(beanFactory.getBean("stringRepo")));
    }

    @Test
    public void genericsBasedInjectionWithLateGenericsMatchingOnCglibProxy() {
        beanFactory.registerBeanDefinition("configClass", new RootBeanDefinition(ConfigurationClassPostProcessorTests.RepositoryConfiguration.class));
        new ConfigurationClassPostProcessor().postProcessBeanFactory(beanFactory);
        DefaultAdvisorAutoProxyCreator autoProxyCreator = new DefaultAdvisorAutoProxyCreator();
        autoProxyCreator.setProxyTargetClass(true);
        autoProxyCreator.setBeanFactory(beanFactory);
        beanFactory.addBeanPostProcessor(autoProxyCreator);
        beanFactory.registerSingleton("traceInterceptor", new org.springframework.aop.support.DefaultPointcutAdvisor(new SimpleTraceInterceptor()));
        beanFactory.preInstantiateSingletons();
        String[] beanNames = beanFactory.getBeanNamesForType(ConfigurationClassPostProcessorTests.Repository.class);
        Assert.assertTrue(ObjectUtils.containsElement(beanNames, "stringRepo"));
        beanNames = beanFactory.getBeanNamesForType(ResolvableType.forClassWithGenerics(ConfigurationClassPostProcessorTests.Repository.class, String.class));
        Assert.assertEquals(1, beanNames.length);
        Assert.assertEquals("stringRepo", beanNames[0]);
        beanNames = beanFactory.getBeanNamesForType(ResolvableType.forClassWithGenerics(ConfigurationClassPostProcessorTests.Repository.class, String.class));
        Assert.assertEquals(1, beanNames.length);
        Assert.assertEquals("stringRepo", beanNames[0]);
        Assert.assertTrue(AopUtils.isCglibProxy(beanFactory.getBean("stringRepo")));
    }

    @Test
    public void genericsBasedInjectionWithLateGenericsMatchingOnCglibProxyAndRawFactoryMethod() {
        beanFactory.registerBeanDefinition("configClass", new RootBeanDefinition(ConfigurationClassPostProcessorTests.RawFactoryMethodRepositoryConfiguration.class));
        new ConfigurationClassPostProcessor().postProcessBeanFactory(beanFactory);
        DefaultAdvisorAutoProxyCreator autoProxyCreator = new DefaultAdvisorAutoProxyCreator();
        autoProxyCreator.setProxyTargetClass(true);
        autoProxyCreator.setBeanFactory(beanFactory);
        beanFactory.addBeanPostProcessor(autoProxyCreator);
        beanFactory.registerSingleton("traceInterceptor", new org.springframework.aop.support.DefaultPointcutAdvisor(new SimpleTraceInterceptor()));
        beanFactory.preInstantiateSingletons();
        String[] beanNames = beanFactory.getBeanNamesForType(ConfigurationClassPostProcessorTests.Repository.class);
        Assert.assertTrue(ObjectUtils.containsElement(beanNames, "stringRepo"));
        beanNames = beanFactory.getBeanNamesForType(ResolvableType.forClassWithGenerics(ConfigurationClassPostProcessorTests.Repository.class, String.class));
        Assert.assertEquals(1, beanNames.length);
        Assert.assertEquals("stringRepo", beanNames[0]);
        beanNames = beanFactory.getBeanNamesForType(ResolvableType.forClassWithGenerics(ConfigurationClassPostProcessorTests.Repository.class, String.class));
        Assert.assertEquals(1, beanNames.length);
        Assert.assertEquals("stringRepo", beanNames[0]);
        Assert.assertTrue(AopUtils.isCglibProxy(beanFactory.getBean("stringRepo")));
    }

    @Test
    public void genericsBasedInjectionWithLateGenericsMatchingOnCglibProxyAndRawInstance() {
        beanFactory.registerBeanDefinition("configClass", new RootBeanDefinition(ConfigurationClassPostProcessorTests.RawInstanceRepositoryConfiguration.class));
        new ConfigurationClassPostProcessor().postProcessBeanFactory(beanFactory);
        DefaultAdvisorAutoProxyCreator autoProxyCreator = new DefaultAdvisorAutoProxyCreator();
        autoProxyCreator.setProxyTargetClass(true);
        autoProxyCreator.setBeanFactory(beanFactory);
        beanFactory.addBeanPostProcessor(autoProxyCreator);
        beanFactory.registerSingleton("traceInterceptor", new org.springframework.aop.support.DefaultPointcutAdvisor(new SimpleTraceInterceptor()));
        beanFactory.preInstantiateSingletons();
        String[] beanNames = beanFactory.getBeanNamesForType(ConfigurationClassPostProcessorTests.Repository.class);
        Assert.assertTrue(ObjectUtils.containsElement(beanNames, "stringRepo"));
        beanNames = beanFactory.getBeanNamesForType(ResolvableType.forClassWithGenerics(ConfigurationClassPostProcessorTests.Repository.class, String.class));
        Assert.assertEquals(1, beanNames.length);
        Assert.assertEquals("stringRepo", beanNames[0]);
        beanNames = beanFactory.getBeanNamesForType(ResolvableType.forClassWithGenerics(ConfigurationClassPostProcessorTests.Repository.class, String.class));
        Assert.assertEquals(1, beanNames.length);
        Assert.assertEquals("stringRepo", beanNames[0]);
        Assert.assertTrue(AopUtils.isCglibProxy(beanFactory.getBean("stringRepo")));
    }

    @Test
    public void genericsBasedInjectionWithEarlyGenericsMatchingOnJdkProxy() {
        beanFactory.registerBeanDefinition("configClass", new RootBeanDefinition(ConfigurationClassPostProcessorTests.RepositoryConfiguration.class));
        new ConfigurationClassPostProcessor().postProcessBeanFactory(beanFactory);
        DefaultAdvisorAutoProxyCreator autoProxyCreator = new DefaultAdvisorAutoProxyCreator();
        autoProxyCreator.setBeanFactory(beanFactory);
        beanFactory.addBeanPostProcessor(autoProxyCreator);
        beanFactory.registerSingleton("traceInterceptor", new org.springframework.aop.support.DefaultPointcutAdvisor(new SimpleTraceInterceptor()));
        String[] beanNames = beanFactory.getBeanNamesForType(ConfigurationClassPostProcessorTests.RepositoryInterface.class);
        Assert.assertTrue(ObjectUtils.containsElement(beanNames, "stringRepo"));
        beanNames = beanFactory.getBeanNamesForType(ResolvableType.forClassWithGenerics(ConfigurationClassPostProcessorTests.RepositoryInterface.class, String.class));
        Assert.assertEquals(1, beanNames.length);
        Assert.assertEquals("stringRepo", beanNames[0]);
        beanNames = beanFactory.getBeanNamesForType(ResolvableType.forClassWithGenerics(ConfigurationClassPostProcessorTests.RepositoryInterface.class, String.class));
        Assert.assertEquals(1, beanNames.length);
        Assert.assertEquals("stringRepo", beanNames[0]);
        Assert.assertTrue(AopUtils.isJdkDynamicProxy(beanFactory.getBean("stringRepo")));
    }

    @Test
    public void genericsBasedInjectionWithLateGenericsMatchingOnJdkProxy() {
        beanFactory.registerBeanDefinition("configClass", new RootBeanDefinition(ConfigurationClassPostProcessorTests.RepositoryConfiguration.class));
        new ConfigurationClassPostProcessor().postProcessBeanFactory(beanFactory);
        DefaultAdvisorAutoProxyCreator autoProxyCreator = new DefaultAdvisorAutoProxyCreator();
        autoProxyCreator.setBeanFactory(beanFactory);
        beanFactory.addBeanPostProcessor(autoProxyCreator);
        beanFactory.registerSingleton("traceInterceptor", new org.springframework.aop.support.DefaultPointcutAdvisor(new SimpleTraceInterceptor()));
        beanFactory.preInstantiateSingletons();
        String[] beanNames = beanFactory.getBeanNamesForType(ConfigurationClassPostProcessorTests.RepositoryInterface.class);
        Assert.assertTrue(ObjectUtils.containsElement(beanNames, "stringRepo"));
        beanNames = beanFactory.getBeanNamesForType(ResolvableType.forClassWithGenerics(ConfigurationClassPostProcessorTests.RepositoryInterface.class, String.class));
        Assert.assertEquals(1, beanNames.length);
        Assert.assertEquals("stringRepo", beanNames[0]);
        beanNames = beanFactory.getBeanNamesForType(ResolvableType.forClassWithGenerics(ConfigurationClassPostProcessorTests.RepositoryInterface.class, String.class));
        Assert.assertEquals(1, beanNames.length);
        Assert.assertEquals("stringRepo", beanNames[0]);
        Assert.assertTrue(AopUtils.isJdkDynamicProxy(beanFactory.getBean("stringRepo")));
    }

    @Test
    public void genericsBasedInjectionWithLateGenericsMatchingOnJdkProxyAndRawFactoryMethod() {
        beanFactory.registerBeanDefinition("configClass", new RootBeanDefinition(ConfigurationClassPostProcessorTests.RawFactoryMethodRepositoryConfiguration.class));
        new ConfigurationClassPostProcessor().postProcessBeanFactory(beanFactory);
        DefaultAdvisorAutoProxyCreator autoProxyCreator = new DefaultAdvisorAutoProxyCreator();
        autoProxyCreator.setBeanFactory(beanFactory);
        beanFactory.addBeanPostProcessor(autoProxyCreator);
        beanFactory.registerSingleton("traceInterceptor", new org.springframework.aop.support.DefaultPointcutAdvisor(new SimpleTraceInterceptor()));
        beanFactory.preInstantiateSingletons();
        String[] beanNames = beanFactory.getBeanNamesForType(ConfigurationClassPostProcessorTests.RepositoryInterface.class);
        Assert.assertTrue(ObjectUtils.containsElement(beanNames, "stringRepo"));
        beanNames = beanFactory.getBeanNamesForType(ResolvableType.forClassWithGenerics(ConfigurationClassPostProcessorTests.RepositoryInterface.class, String.class));
        Assert.assertEquals(1, beanNames.length);
        Assert.assertEquals("stringRepo", beanNames[0]);
        beanNames = beanFactory.getBeanNamesForType(ResolvableType.forClassWithGenerics(ConfigurationClassPostProcessorTests.RepositoryInterface.class, String.class));
        Assert.assertEquals(1, beanNames.length);
        Assert.assertEquals("stringRepo", beanNames[0]);
        Assert.assertTrue(AopUtils.isJdkDynamicProxy(beanFactory.getBean("stringRepo")));
    }

    @Test
    public void genericsBasedInjectionWithLateGenericsMatchingOnJdkProxyAndRawInstance() {
        beanFactory.registerBeanDefinition("configClass", new RootBeanDefinition(ConfigurationClassPostProcessorTests.RawInstanceRepositoryConfiguration.class));
        new ConfigurationClassPostProcessor().postProcessBeanFactory(beanFactory);
        DefaultAdvisorAutoProxyCreator autoProxyCreator = new DefaultAdvisorAutoProxyCreator();
        autoProxyCreator.setBeanFactory(beanFactory);
        beanFactory.addBeanPostProcessor(autoProxyCreator);
        beanFactory.registerSingleton("traceInterceptor", new org.springframework.aop.support.DefaultPointcutAdvisor(new SimpleTraceInterceptor()));
        beanFactory.preInstantiateSingletons();
        String[] beanNames = beanFactory.getBeanNamesForType(ConfigurationClassPostProcessorTests.RepositoryInterface.class);
        Assert.assertTrue(ObjectUtils.containsElement(beanNames, "stringRepo"));
        beanNames = beanFactory.getBeanNamesForType(ResolvableType.forClassWithGenerics(ConfigurationClassPostProcessorTests.RepositoryInterface.class, String.class));
        Assert.assertEquals(1, beanNames.length);
        Assert.assertEquals("stringRepo", beanNames[0]);
        beanNames = beanFactory.getBeanNamesForType(ResolvableType.forClassWithGenerics(ConfigurationClassPostProcessorTests.RepositoryInterface.class, String.class));
        Assert.assertEquals(1, beanNames.length);
        Assert.assertEquals("stringRepo", beanNames[0]);
        Assert.assertTrue(AopUtils.isJdkDynamicProxy(beanFactory.getBean("stringRepo")));
    }

    @Test
    public void testSelfReferenceExclusionForFactoryMethodOnSameBean() {
        AutowiredAnnotationBeanPostProcessor bpp = new AutowiredAnnotationBeanPostProcessor();
        bpp.setBeanFactory(beanFactory);
        beanFactory.addBeanPostProcessor(bpp);
        beanFactory.addBeanPostProcessor(new CommonAnnotationBeanPostProcessor());
        beanFactory.registerBeanDefinition("configClass", new RootBeanDefinition(ConfigurationClassPostProcessorTests.ConcreteConfig.class));
        beanFactory.registerBeanDefinition("serviceBeanProvider", new RootBeanDefinition(ConfigurationClassPostProcessorTests.ServiceBeanProvider.class));
        new ConfigurationClassPostProcessor().postProcessBeanFactory(beanFactory);
        beanFactory.preInstantiateSingletons();
        beanFactory.getBean(ConfigurationClassPostProcessorTests.ServiceBean.class);
    }

    @Test
    public void testConfigWithDefaultMethods() {
        AutowiredAnnotationBeanPostProcessor bpp = new AutowiredAnnotationBeanPostProcessor();
        bpp.setBeanFactory(beanFactory);
        beanFactory.addBeanPostProcessor(bpp);
        beanFactory.addBeanPostProcessor(new CommonAnnotationBeanPostProcessor());
        beanFactory.registerBeanDefinition("configClass", new RootBeanDefinition(ConfigurationClassPostProcessorTests.ConcreteConfigWithDefaultMethods.class));
        beanFactory.registerBeanDefinition("serviceBeanProvider", new RootBeanDefinition(ConfigurationClassPostProcessorTests.ServiceBeanProvider.class));
        new ConfigurationClassPostProcessor().postProcessBeanFactory(beanFactory);
        beanFactory.preInstantiateSingletons();
        beanFactory.getBean(ConfigurationClassPostProcessorTests.ServiceBean.class);
    }

    @Test
    public void testConfigWithDefaultMethodsUsingAsm() {
        AutowiredAnnotationBeanPostProcessor bpp = new AutowiredAnnotationBeanPostProcessor();
        bpp.setBeanFactory(beanFactory);
        beanFactory.addBeanPostProcessor(bpp);
        beanFactory.addBeanPostProcessor(new CommonAnnotationBeanPostProcessor());
        beanFactory.registerBeanDefinition("configClass", new RootBeanDefinition(ConfigurationClassPostProcessorTests.ConcreteConfigWithDefaultMethods.class.getName()));
        beanFactory.registerBeanDefinition("serviceBeanProvider", new RootBeanDefinition(ConfigurationClassPostProcessorTests.ServiceBeanProvider.class.getName()));
        new ConfigurationClassPostProcessor().postProcessBeanFactory(beanFactory);
        beanFactory.preInstantiateSingletons();
        beanFactory.getBean(ConfigurationClassPostProcessorTests.ServiceBean.class);
    }

    @Test
    public void testCircularDependency() {
        AutowiredAnnotationBeanPostProcessor bpp = new AutowiredAnnotationBeanPostProcessor();
        bpp.setBeanFactory(beanFactory);
        beanFactory.addBeanPostProcessor(bpp);
        beanFactory.registerBeanDefinition("configClass1", new RootBeanDefinition(ConfigurationClassPostProcessorTests.A.class));
        beanFactory.registerBeanDefinition("configClass2", new RootBeanDefinition(ConfigurationClassPostProcessorTests.AStrich.class));
        new ConfigurationClassPostProcessor().postProcessBeanFactory(beanFactory);
        try {
            beanFactory.preInstantiateSingletons();
            Assert.fail("Should have thrown BeanCreationException");
        } catch (BeanCreationException ex) {
            Assert.assertTrue(ex.getMessage().contains("Circular reference"));
        }
    }

    @Test
    public void testCircularDependencyWithApplicationContext() {
        try {
            new AnnotationConfigApplicationContext(ConfigurationClassPostProcessorTests.A.class, ConfigurationClassPostProcessorTests.AStrich.class);
            Assert.fail("Should have thrown BeanCreationException");
        } catch (BeanCreationException ex) {
            Assert.assertTrue(ex.getMessage().contains("Circular reference"));
        }
    }

    @Test
    public void testPrototypeArgumentThroughBeanMethodCall() {
        ApplicationContext ctx = new AnnotationConfigApplicationContext(ConfigurationClassPostProcessorTests.BeanArgumentConfigWithPrototype.class);
        ctx.getBean(ConfigurationClassPostProcessorTests.FooFactory.class).createFoo(new ConfigurationClassPostProcessorTests.BarArgument());
    }

    @Test
    public void testSingletonArgumentThroughBeanMethodCall() {
        ApplicationContext ctx = new AnnotationConfigApplicationContext(ConfigurationClassPostProcessorTests.BeanArgumentConfigWithSingleton.class);
        ctx.getBean(ConfigurationClassPostProcessorTests.FooFactory.class).createFoo(new ConfigurationClassPostProcessorTests.BarArgument());
    }

    @Test
    public void testNullArgumentThroughBeanMethodCall() {
        ApplicationContext ctx = new AnnotationConfigApplicationContext(ConfigurationClassPostProcessorTests.BeanArgumentConfigWithNull.class);
        ctx.getBean("aFoo");
    }

    @Test
    public void testInjectionPointMatchForNarrowTargetReturnType() {
        ApplicationContext ctx = new AnnotationConfigApplicationContext(ConfigurationClassPostProcessorTests.FooBarConfiguration.class);
        Assert.assertSame(ctx.getBean(ConfigurationClassPostProcessorTests.BarImpl.class), ctx.getBean(ConfigurationClassPostProcessorTests.FooImpl.class).bar);
    }

    @Test
    public void testVarargOnBeanMethod() {
        ApplicationContext ctx = new AnnotationConfigApplicationContext(ConfigurationClassPostProcessorTests.VarargConfiguration.class, TestBean.class);
        ConfigurationClassPostProcessorTests.VarargConfiguration bean = ctx.getBean(ConfigurationClassPostProcessorTests.VarargConfiguration.class);
        Assert.assertNotNull(bean.testBeans);
        Assert.assertEquals(1, bean.testBeans.length);
        Assert.assertSame(ctx.getBean(TestBean.class), bean.testBeans[0]);
    }

    @Test
    public void testEmptyVarargOnBeanMethod() {
        ApplicationContext ctx = new AnnotationConfigApplicationContext(ConfigurationClassPostProcessorTests.VarargConfiguration.class);
        ConfigurationClassPostProcessorTests.VarargConfiguration bean = ctx.getBean(ConfigurationClassPostProcessorTests.VarargConfiguration.class);
        Assert.assertNotNull(bean.testBeans);
        Assert.assertEquals(0, bean.testBeans.length);
    }

    @Test
    public void testCollectionArgumentOnBeanMethod() {
        ApplicationContext ctx = new AnnotationConfigApplicationContext(ConfigurationClassPostProcessorTests.CollectionArgumentConfiguration.class, TestBean.class);
        ConfigurationClassPostProcessorTests.CollectionArgumentConfiguration bean = ctx.getBean(ConfigurationClassPostProcessorTests.CollectionArgumentConfiguration.class);
        Assert.assertNotNull(bean.testBeans);
        Assert.assertEquals(1, bean.testBeans.size());
        Assert.assertSame(ctx.getBean(TestBean.class), bean.testBeans.get(0));
    }

    @Test
    public void testEmptyCollectionArgumentOnBeanMethod() {
        ApplicationContext ctx = new AnnotationConfigApplicationContext(ConfigurationClassPostProcessorTests.CollectionArgumentConfiguration.class);
        ConfigurationClassPostProcessorTests.CollectionArgumentConfiguration bean = ctx.getBean(ConfigurationClassPostProcessorTests.CollectionArgumentConfiguration.class);
        Assert.assertNotNull(bean.testBeans);
        Assert.assertTrue(bean.testBeans.isEmpty());
    }

    @Test
    public void testMapArgumentOnBeanMethod() {
        ApplicationContext ctx = new AnnotationConfigApplicationContext(ConfigurationClassPostProcessorTests.MapArgumentConfiguration.class, ConfigurationClassPostProcessorTests.DummyRunnable.class);
        ConfigurationClassPostProcessorTests.MapArgumentConfiguration bean = ctx.getBean(ConfigurationClassPostProcessorTests.MapArgumentConfiguration.class);
        Assert.assertNotNull(bean.testBeans);
        Assert.assertEquals(1, bean.testBeans.size());
        Assert.assertSame(ctx.getBean(Runnable.class), bean.testBeans.values().iterator().next());
    }

    @Test
    public void testEmptyMapArgumentOnBeanMethod() {
        ApplicationContext ctx = new AnnotationConfigApplicationContext(ConfigurationClassPostProcessorTests.MapArgumentConfiguration.class);
        ConfigurationClassPostProcessorTests.MapArgumentConfiguration bean = ctx.getBean(ConfigurationClassPostProcessorTests.MapArgumentConfiguration.class);
        Assert.assertNotNull(bean.testBeans);
        Assert.assertTrue(bean.testBeans.isEmpty());
    }

    @Test
    public void testCollectionInjectionFromSameConfigurationClass() {
        ApplicationContext ctx = new AnnotationConfigApplicationContext(ConfigurationClassPostProcessorTests.CollectionInjectionConfiguration.class);
        ConfigurationClassPostProcessorTests.CollectionInjectionConfiguration bean = ctx.getBean(ConfigurationClassPostProcessorTests.CollectionInjectionConfiguration.class);
        Assert.assertNotNull(bean.testBeans);
        Assert.assertEquals(1, bean.testBeans.size());
        Assert.assertSame(ctx.getBean(TestBean.class), bean.testBeans.get(0));
    }

    @Test
    public void testMapInjectionFromSameConfigurationClass() {
        ApplicationContext ctx = new AnnotationConfigApplicationContext(ConfigurationClassPostProcessorTests.MapInjectionConfiguration.class);
        ConfigurationClassPostProcessorTests.MapInjectionConfiguration bean = ctx.getBean(ConfigurationClassPostProcessorTests.MapInjectionConfiguration.class);
        Assert.assertNotNull(bean.testBeans);
        Assert.assertEquals(1, bean.testBeans.size());
        Assert.assertSame(ctx.getBean(Runnable.class), bean.testBeans.get("testBean"));
    }

    @Test
    public void testBeanLookupFromSameConfigurationClass() {
        ApplicationContext ctx = new AnnotationConfigApplicationContext(ConfigurationClassPostProcessorTests.BeanLookupConfiguration.class);
        ConfigurationClassPostProcessorTests.BeanLookupConfiguration bean = ctx.getBean(ConfigurationClassPostProcessorTests.BeanLookupConfiguration.class);
        Assert.assertNotNull(bean.getTestBean());
        Assert.assertSame(ctx.getBean(TestBean.class), bean.getTestBean());
    }

    @Test(expected = BeanDefinitionStoreException.class)
    public void testNameClashBetweenConfigurationClassAndBean() {
        ApplicationContext ctx = new AnnotationConfigApplicationContext(MyTestBean.class);
        ctx.getBean("myTestBean", TestBean.class);
    }

    @Test
    public void testBeanDefinitionRegistryPostProcessorConfig() {
        ApplicationContext ctx = new AnnotationConfigApplicationContext(ConfigurationClassPostProcessorTests.BeanDefinitionRegistryPostProcessorConfig.class);
        Assert.assertTrue(((ctx.getBean("myTestBean")) instanceof TestBean));
    }

    // -------------------------------------------------------------------------
    @Configuration
    @Order(1)
    static class SingletonBeanConfig {
        @Bean
        public ConfigurationClassPostProcessorTests.Foo foo() {
            return new ConfigurationClassPostProcessorTests.Foo();
        }

        @Bean
        public ConfigurationClassPostProcessorTests.Bar bar() {
            return new ConfigurationClassPostProcessorTests.Bar(foo());
        }
    }

    @Configuration(proxyBeanMethods = false)
    static class NonEnhancedSingletonBeanConfig {
        @Bean
        public ConfigurationClassPostProcessorTests.Foo foo() {
            return new ConfigurationClassPostProcessorTests.Foo();
        }

        @Bean
        public ConfigurationClassPostProcessorTests.Bar bar() {
            return new ConfigurationClassPostProcessorTests.Bar(foo());
        }
    }

    @Configuration
    @Order(2)
    static class OverridingSingletonBeanConfig {
        @Bean
        public ConfigurationClassPostProcessorTests.ExtendedFoo foo() {
            return new ConfigurationClassPostProcessorTests.ExtendedFoo();
        }

        @Bean
        public ConfigurationClassPostProcessorTests.Bar bar() {
            return new ConfigurationClassPostProcessorTests.Bar(foo());
        }
    }

    @Configuration
    static class OverridingAgainSingletonBeanConfig {
        @Bean
        public ConfigurationClassPostProcessorTests.ExtendedAgainFoo foo() {
            return new ConfigurationClassPostProcessorTests.ExtendedAgainFoo();
        }
    }

    @Configuration
    static class InvalidOverridingSingletonBeanConfig {
        @Bean
        public ConfigurationClassPostProcessorTests.Foo foo() {
            return new ConfigurationClassPostProcessorTests.Foo();
        }
    }

    @Configuration
    static class ConfigWithOrderedNestedClasses {
        @Configuration
        @Order(1)
        static class SingletonBeanConfig {
            @Bean
            public ConfigurationClassPostProcessorTests.Foo foo() {
                return new ConfigurationClassPostProcessorTests.Foo();
            }

            @Bean
            public ConfigurationClassPostProcessorTests.Bar bar() {
                return new ConfigurationClassPostProcessorTests.Bar(foo());
            }
        }

        @Configuration
        @Order(2)
        static class OverridingSingletonBeanConfig {
            @Bean
            public ConfigurationClassPostProcessorTests.ExtendedFoo foo() {
                return new ConfigurationClassPostProcessorTests.ExtendedFoo();
            }

            @Bean
            public ConfigurationClassPostProcessorTests.Bar bar() {
                return new ConfigurationClassPostProcessorTests.Bar(foo());
            }
        }
    }

    @Configuration
    static class ConfigWithOrderedInnerClasses {
        @Configuration
        @Order(1)
        class SingletonBeanConfig {
            public SingletonBeanConfig(ConfigurationClassPostProcessorTests.ConfigWithOrderedInnerClasses other) {
            }

            @Bean
            public ConfigurationClassPostProcessorTests.Foo foo() {
                return new ConfigurationClassPostProcessorTests.Foo();
            }

            @Bean
            public ConfigurationClassPostProcessorTests.Bar bar() {
                return new ConfigurationClassPostProcessorTests.Bar(foo());
            }
        }

        @Configuration
        @Order(2)
        class OverridingSingletonBeanConfig {
            public OverridingSingletonBeanConfig(ObjectProvider<ConfigurationClassPostProcessorTests.ConfigWithOrderedInnerClasses.SingletonBeanConfig> other) {
                other.getObject();
            }

            @Bean
            public ConfigurationClassPostProcessorTests.ExtendedFoo foo() {
                return new ConfigurationClassPostProcessorTests.ExtendedFoo();
            }

            @Bean
            public ConfigurationClassPostProcessorTests.Bar bar() {
                return new ConfigurationClassPostProcessorTests.Bar(foo());
            }
        }
    }

    static class Foo {}

    static class ExtendedFoo extends ConfigurationClassPostProcessorTests.Foo {}

    static class ExtendedAgainFoo extends ConfigurationClassPostProcessorTests.ExtendedFoo {}

    static class Bar {
        final ConfigurationClassPostProcessorTests.Foo foo;

        public Bar(ConfigurationClassPostProcessorTests.Foo foo) {
            this.foo = foo;
        }
    }

    @Configuration
    static class UnloadedConfig {
        @Bean
        public ConfigurationClassPostProcessorTests.Foo foo() {
            return new ConfigurationClassPostProcessorTests.Foo();
        }
    }

    @Configuration
    static class LoadedConfig {
        @Bean
        public ConfigurationClassPostProcessorTests.Bar bar() {
            return new ConfigurationClassPostProcessorTests.Bar(new ConfigurationClassPostProcessorTests.Foo());
        }
    }

    public static class ScopedProxyConsumer {
        @Autowired
        public ITestBean testBean;
    }

    @Configuration
    public static class ScopedProxyConfigurationClass {
        @Bean
        @Lazy
        @Scope(proxyMode = INTERFACES)
        public ITestBean scopedClass() {
            return new TestBean();
        }
    }

    public interface RepositoryInterface<T> {
        String toString();
    }

    public static class Repository<T> implements ConfigurationClassPostProcessorTests.RepositoryInterface<T> {}

    public static class GenericRepository<T> extends ConfigurationClassPostProcessorTests.Repository<T> {}

    public static class RepositoryFactoryBean<T> implements FactoryBean<T> {
        @Override
        public T getObject() {
            throw new IllegalStateException();
        }

        @Override
        public Class<?> getObjectType() {
            return Object.class;
        }

        @Override
        public boolean isSingleton() {
            return false;
        }
    }

    public static class RepositoryInjectionBean {
        @Autowired
        public ConfigurationClassPostProcessorTests.Repository<String> stringRepository;

        @Autowired
        public ConfigurationClassPostProcessorTests.Repository<Integer> integerRepository;
    }

    @Configuration
    public static class RepositoryConfiguration {
        @Bean
        public ConfigurationClassPostProcessorTests.Repository<String> stringRepo() {
            return new ConfigurationClassPostProcessorTests.Repository<String>() {
                @Override
                public String toString() {
                    return "Repository<String>";
                }
            };
        }

        @Bean
        public ConfigurationClassPostProcessorTests.Repository<Integer> integerRepo() {
            return new ConfigurationClassPostProcessorTests.Repository<Integer>() {
                @Override
                public String toString() {
                    return "Repository<Integer>";
                }
            };
        }

        @Bean
        public ConfigurationClassPostProcessorTests.Repository<?> genericRepo() {
            return new ConfigurationClassPostProcessorTests.Repository<Object>() {
                @Override
                public String toString() {
                    return "Repository<Object>";
                }
            };
        }
    }

    @Configuration
    public static class RawFactoryMethodRepositoryConfiguration {
        @Bean
        public ConfigurationClassPostProcessorTests.Repository stringRepo() {
            return new ConfigurationClassPostProcessorTests.Repository<String>() {
                @Override
                public String toString() {
                    return "Repository<String>";
                }
            };
        }
    }

    @Configuration
    public static class RawInstanceRepositoryConfiguration {
        @SuppressWarnings({ "rawtypes", "unchecked" })
        @Bean
        public ConfigurationClassPostProcessorTests.Repository<String> stringRepo() {
            return new ConfigurationClassPostProcessorTests.Repository() {
                @Override
                public String toString() {
                    return "Repository<String>";
                }
            };
        }
    }

    @Configuration
    public static class ScopedRepositoryConfiguration {
        @Bean
        @Scope("prototype")
        public ConfigurationClassPostProcessorTests.Repository<String> stringRepo() {
            return new ConfigurationClassPostProcessorTests.Repository<String>() {
                @Override
                public String toString() {
                    return "Repository<String>";
                }
            };
        }

        @Bean
        @Scope("prototype")
        public ConfigurationClassPostProcessorTests.Repository<Integer> integerRepo() {
            return new ConfigurationClassPostProcessorTests.Repository<Integer>() {
                @Override
                public String toString() {
                    return "Repository<Integer>";
                }
            };
        }

        @Bean
        @Scope("prototype")
        @SuppressWarnings("rawtypes")
        public ConfigurationClassPostProcessorTests.Repository genericRepo() {
            return new ConfigurationClassPostProcessorTests.Repository<Object>() {
                @Override
                public String toString() {
                    return "Repository<Object>";
                }
            };
        }
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Scope(scopeName = "prototype")
    public @interface PrototypeScoped {
        ScopedProxyMode proxyMode() default TARGET_CLASS;
    }

    @Configuration
    public static class ScopedProxyRepositoryConfiguration {
        @Bean
        @Scope(scopeName = "prototype", proxyMode = ScopedProxyMode.TARGET_CLASS)
        public ConfigurationClassPostProcessorTests.Repository<String> stringRepo() {
            return new ConfigurationClassPostProcessorTests.Repository<String>() {
                @Override
                public String toString() {
                    return "Repository<String>";
                }
            };
        }

        @Bean
        @ConfigurationClassPostProcessorTests.PrototypeScoped
        public ConfigurationClassPostProcessorTests.Repository<Integer> integerRepo() {
            return new ConfigurationClassPostProcessorTests.Repository<Integer>() {
                @Override
                public String toString() {
                    return "Repository<Integer>";
                }
            };
        }
    }

    public static class SpecificRepositoryInjectionBean {
        @Autowired
        public ConfigurationClassPostProcessorTests.GenericRepository<?> genericRepository;
    }

    @Configuration
    public static class SpecificRepositoryConfiguration {
        @Bean
        public ConfigurationClassPostProcessorTests.Repository<Object> genericRepo() {
            return new ConfigurationClassPostProcessorTests.GenericRepository<>();
        }
    }

    public static class RepositoryFactoryBeanInjectionBean {
        @Autowired
        public ConfigurationClassPostProcessorTests.RepositoryFactoryBean<?> repositoryFactoryBean;

        @Autowired
        @Qualifier("repoFactoryBean")
        public ConfigurationClassPostProcessorTests.RepositoryFactoryBean<?> qualifiedRepositoryFactoryBean;

        @Autowired
        @Qualifier("&repoFactoryBean")
        public ConfigurationClassPostProcessorTests.RepositoryFactoryBean<?> prefixQualifiedRepositoryFactoryBean;
    }

    @Configuration
    public static class RepositoryFactoryBeanConfiguration {
        @Bean
        public ConfigurationClassPostProcessorTests.RepositoryFactoryBean<Object> repoFactoryBean() {
            return new ConfigurationClassPostProcessorTests.RepositoryFactoryBean<>();
        }

        @Bean
        public FactoryBean<Object> nullFactoryBean() {
            return null;
        }
    }

    @Configuration
    public static class RawMatchingConfiguration {
        @Bean
        @SuppressWarnings("rawtypes")
        public ConfigurationClassPostProcessorTests.Repository rawRepo() {
            return new ConfigurationClassPostProcessorTests.Repository();
        }

        @Bean
        public Object repoConsumer(ConfigurationClassPostProcessorTests.Repository<String> repo) {
            return repo;
        }
    }

    @Configuration
    public static class WildcardMatchingConfiguration {
        @Bean
        @SuppressWarnings("rawtypes")
        public ConfigurationClassPostProcessorTests.Repository<?> genericRepo() {
            return new ConfigurationClassPostProcessorTests.Repository();
        }

        @Bean
        public Object repoConsumer(ConfigurationClassPostProcessorTests.Repository<String> repo) {
            return repo;
        }
    }

    @Configuration
    public static class WildcardWithExtendsConfiguration {
        @Bean
        public ConfigurationClassPostProcessorTests.Repository<? extends String> stringRepo() {
            return new ConfigurationClassPostProcessorTests.Repository<>();
        }

        @Bean
        public ConfigurationClassPostProcessorTests.Repository<? extends Number> numberRepo() {
            return new ConfigurationClassPostProcessorTests.Repository<>();
        }

        @Bean
        public Object repoConsumer(ConfigurationClassPostProcessorTests.Repository<? extends String> repo) {
            return repo;
        }
    }

    @Configuration
    public static class WildcardWithGenericExtendsConfiguration {
        @Bean
        public ConfigurationClassPostProcessorTests.Repository<? extends Object> genericRepo() {
            return new ConfigurationClassPostProcessorTests.Repository<String>();
        }

        @Bean
        public ConfigurationClassPostProcessorTests.Repository<? extends Number> numberRepo() {
            return new ConfigurationClassPostProcessorTests.Repository<>();
        }

        @Bean
        public Object repoConsumer(ConfigurationClassPostProcessorTests.Repository<String> repo) {
            return repo;
        }
    }

    @Configuration
    @ComponentScan(basePackages = "org.springframework.context.annotation.componentscan.simple")
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    public @interface ComposedConfiguration {}

    @ConfigurationClassPostProcessorTests.ComposedConfiguration
    public static class ComposedConfigurationClass {}

    @Configuration
    @ComponentScan
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    public @interface ComposedConfigurationWithAttributeOverrides {
        String[] basePackages() default {  };

        Filter[] excludeFilters() default {  };
    }

    @ConfigurationClassPostProcessorTests.ComposedConfigurationWithAttributeOverrides(basePackages = "org.springframework.context.annotation.componentscan.simple")
    public static class ComposedConfigurationWithAttributeOverrideForBasePackage {}

    @ConfigurationClassPostProcessorTests.ComposedConfigurationWithAttributeOverrides(basePackages = "org.springframework.context.annotation.componentscan.simple", excludeFilters = @ComponentScan.Filter(Component.class))
    public static class ComposedConfigurationWithAttributeOverrideForExcludeFilter {}

    @ComponentScan(basePackages = "org.springframework.context.annotation.componentscan.base", excludeFilters = {  })
    public static class BaseConfigurationWithEmptyExcludeFilters {}

    @ComponentScan(basePackages = "org.springframework.context.annotation.componentscan.simple", excludeFilters = @ComponentScan.Filter(Component.class))
    public static class ExtendedConfigurationWithAttributeOverrideForExcludeFilter extends ConfigurationClassPostProcessorTests.BaseConfigurationWithEmptyExcludeFilters {}

    @ConfigurationClassPostProcessorTests.ComposedConfigurationWithAttributeOverrides
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    public @interface ComposedComposedConfigurationWithAttributeOverrides {
        String[] basePackages() default {  };
    }

    @ConfigurationClassPostProcessorTests.ComposedComposedConfigurationWithAttributeOverrides(basePackages = "org.springframework.context.annotation.componentscan.simple")
    public static class ComposedComposedConfigurationWithAttributeOverridesClass {}

    @ComponentScan
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    public @interface MetaComponentScan {}

    @ConfigurationClassPostProcessorTests.MetaComponentScan
    @Configuration
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    public @interface MetaComponentScanConfigurationWithAttributeOverrides {
        String[] basePackages() default {  };
    }

    @ConfigurationClassPostProcessorTests.MetaComponentScanConfigurationWithAttributeOverrides(basePackages = "org.springframework.context.annotation.componentscan.simple")
    public static class MetaComponentScanConfigurationWithAttributeOverridesClass {}

    @Configuration
    public static class SubMetaComponentScanConfigurationWithAttributeOverridesClass extends ConfigurationClassPostProcessorTests.MetaComponentScanConfigurationWithAttributeOverridesClass {}

    public static class ServiceBean {
        private final String parameter;

        public ServiceBean(String parameter) {
            this.parameter = parameter;
        }

        public String getParameter() {
            return parameter;
        }
    }

    @Configuration
    public abstract static class AbstractConfig {
        @Bean
        public ConfigurationClassPostProcessorTests.ServiceBean serviceBean() {
            return provider().getServiceBean();
        }

        @Bean
        public ConfigurationClassPostProcessorTests.ServiceBeanProvider provider() {
            return new ConfigurationClassPostProcessorTests.ServiceBeanProvider();
        }
    }

    @Configuration
    public static class ConcreteConfig extends ConfigurationClassPostProcessorTests.AbstractConfig {
        @Autowired
        private ConfigurationClassPostProcessorTests.ServiceBeanProvider provider;

        @Bean
        @Override
        public ConfigurationClassPostProcessorTests.ServiceBeanProvider provider() {
            return provider;
        }

        @PostConstruct
        public void validate() {
            org.springframework.util.Assert.notNull(provider, "No ServiceBeanProvider injected");
        }
    }

    public interface BaseInterface {
        ConfigurationClassPostProcessorTests.ServiceBean serviceBean();
    }

    public interface BaseDefaultMethods extends ConfigurationClassPostProcessorTests.BaseInterface {
        @Bean
        default ConfigurationClassPostProcessorTests.ServiceBeanProvider provider() {
            return new ConfigurationClassPostProcessorTests.ServiceBeanProvider();
        }

        @Bean
        @Override
        default ConfigurationClassPostProcessorTests.ServiceBean serviceBean() {
            return provider().getServiceBean();
        }
    }

    public interface DefaultMethodsConfig extends ConfigurationClassPostProcessorTests.BaseDefaultMethods {}

    @Configuration
    public static class ConcreteConfigWithDefaultMethods implements ConfigurationClassPostProcessorTests.DefaultMethodsConfig {
        @Autowired
        private ConfigurationClassPostProcessorTests.ServiceBeanProvider provider;

        @Bean
        @Override
        public ConfigurationClassPostProcessorTests.ServiceBeanProvider provider() {
            return provider;
        }

        @PostConstruct
        public void validate() {
            org.springframework.util.Assert.notNull(provider, "No ServiceBeanProvider injected");
        }
    }

    @Primary
    public static class ServiceBeanProvider {
        public ConfigurationClassPostProcessorTests.ServiceBean getServiceBean() {
            return new ConfigurationClassPostProcessorTests.ServiceBean("message");
        }
    }

    @Configuration
    public static class A {
        @Autowired(required = true)
        ConfigurationClassPostProcessorTests.Z z;

        @Bean
        public ConfigurationClassPostProcessorTests.B b() {
            if ((z) == null) {
                throw new NullPointerException("z is null");
            }
            return new ConfigurationClassPostProcessorTests.B(z);
        }
    }

    @Configuration
    public static class AStrich {
        @Autowired
        ConfigurationClassPostProcessorTests.B b;

        @Bean
        public ConfigurationClassPostProcessorTests.Z z() {
            return new ConfigurationClassPostProcessorTests.Z();
        }
    }

    public static class B {
        public B(ConfigurationClassPostProcessorTests.Z z) {
        }
    }

    public static class Z {}

    @Configuration
    static class BeanArgumentConfigWithPrototype {
        @Bean
        @Scope("prototype")
        public ConfigurationClassPostProcessorTests.DependingFoo foo(ConfigurationClassPostProcessorTests.BarArgument bar) {
            return new ConfigurationClassPostProcessorTests.DependingFoo(bar);
        }

        @Bean
        public ConfigurationClassPostProcessorTests.FooFactory fooFactory() {
            return new ConfigurationClassPostProcessorTests.FooFactory() {
                @Override
                public ConfigurationClassPostProcessorTests.DependingFoo createFoo(ConfigurationClassPostProcessorTests.BarArgument bar) {
                    return foo(bar);
                }
            };
        }
    }

    @Configuration
    static class BeanArgumentConfigWithSingleton {
        @Bean
        @Lazy
        public ConfigurationClassPostProcessorTests.DependingFoo foo(ConfigurationClassPostProcessorTests.BarArgument bar) {
            return new ConfigurationClassPostProcessorTests.DependingFoo(bar);
        }

        @Bean
        public ConfigurationClassPostProcessorTests.FooFactory fooFactory() {
            return new ConfigurationClassPostProcessorTests.FooFactory() {
                @Override
                public ConfigurationClassPostProcessorTests.DependingFoo createFoo(ConfigurationClassPostProcessorTests.BarArgument bar) {
                    return foo(bar);
                }
            };
        }
    }

    @Configuration
    static class BeanArgumentConfigWithNull {
        @Bean
        public ConfigurationClassPostProcessorTests.DependingFoo aFoo() {
            return foo(null);
        }

        @Bean
        @Lazy
        public ConfigurationClassPostProcessorTests.DependingFoo foo(ConfigurationClassPostProcessorTests.BarArgument bar) {
            return new ConfigurationClassPostProcessorTests.DependingFoo(bar);
        }

        @Bean
        public ConfigurationClassPostProcessorTests.BarArgument bar() {
            return new ConfigurationClassPostProcessorTests.BarArgument();
        }
    }

    static class BarArgument {}

    static class DependingFoo {
        DependingFoo(ConfigurationClassPostProcessorTests.BarArgument bar) {
            org.springframework.util.Assert.notNull(bar, "No BarArgument injected");
        }
    }

    abstract static class FooFactory {
        abstract ConfigurationClassPostProcessorTests.DependingFoo createFoo(ConfigurationClassPostProcessorTests.BarArgument bar);
    }

    interface BarInterface {}

    static class BarImpl implements ConfigurationClassPostProcessorTests.BarInterface {}

    static class FooImpl {
        @Autowired
        public ConfigurationClassPostProcessorTests.BarImpl bar;
    }

    @Configuration
    static class FooBarConfiguration {
        @Bean
        public ConfigurationClassPostProcessorTests.BarInterface bar() {
            return new ConfigurationClassPostProcessorTests.BarImpl();
        }

        @Bean
        public ConfigurationClassPostProcessorTests.FooImpl foo() {
            return new ConfigurationClassPostProcessorTests.FooImpl();
        }
    }

    public static class DummyRunnable implements Runnable {
        @Override
        public void run() {
            /* no-op */
        }
    }

    @Configuration
    static class VarargConfiguration {
        TestBean[] testBeans;

        @Bean(autowireCandidate = false)
        public TestBean thing(TestBean... testBeans) {
            this.testBeans = testBeans;
            return new TestBean();
        }
    }

    @Configuration
    static class CollectionArgumentConfiguration {
        List<TestBean> testBeans;

        @Bean(autowireCandidate = false)
        public TestBean thing(List<TestBean> testBeans) {
            this.testBeans = testBeans;
            return new TestBean();
        }
    }

    @Configuration
    public static class MapArgumentConfiguration {
        @Autowired
        ConfigurableEnvironment env;

        Map<String, Runnable> testBeans;

        @Bean(autowireCandidate = false)
        Runnable testBean(Map<String, Runnable> testBeans, @Qualifier("systemProperties")
        Map<String, String> sysprops, @Qualifier("systemEnvironment")
        Map<String, String> sysenv) {
            this.testBeans = testBeans;
            Assert.assertSame(env.getSystemProperties(), sysprops);
            Assert.assertSame(env.getSystemEnvironment(), sysenv);
            return () -> {
            };
        }

        // Unrelated, not to be considered as a factory method
        private boolean testBean(boolean param) {
            return param;
        }
    }

    @Configuration
    static class CollectionInjectionConfiguration {
        @Autowired(required = false)
        public List<TestBean> testBeans;

        @Bean
        public TestBean thing() {
            return new TestBean();
        }
    }

    @Configuration
    public static class MapInjectionConfiguration {
        @Autowired
        private Map<String, Runnable> testBeans;

        @Bean
        Runnable testBean() {
            return () -> {
            };
        }

        // Unrelated, not to be considered as a factory method
        private boolean testBean(boolean param) {
            return param;
        }
    }

    @Configuration
    abstract static class BeanLookupConfiguration {
        @Bean
        public TestBean thing() {
            return new TestBean();
        }

        @Lookup
        public abstract TestBean getTestBean();
    }

    @Configuration
    static class BeanDefinitionRegistryPostProcessorConfig {
        @Bean
        public static BeanDefinitionRegistryPostProcessor bdrpp() {
            return new BeanDefinitionRegistryPostProcessor() {
                @Override
                public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) {
                    registry.registerBeanDefinition("myTestBean", new RootBeanDefinition(TestBean.class));
                }

                @Override
                public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) {
                }
            };
        }
    }
}

