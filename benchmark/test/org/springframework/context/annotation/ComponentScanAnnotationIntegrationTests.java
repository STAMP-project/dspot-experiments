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


import example.scannable.DefaultNamedComponent;
import example.scannable.FooService;
import example.scannable.MessageBean;
import example.scannable._package;
import example.scannable_implicitbasepackage.ComponentScanAnnotatedConfigWithImplicitBasePackage;
import example.scannable_implicitbasepackage.ConfigurableComponent;
import example.scannable_scoped.CustomScopeAnnotationBean;
import java.io.IOException;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.componentscan.simple.ClassWithNestedComponents;
import org.springframework.context.annotation.componentscan.simple.SimpleComponent;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.filter.TypeFilter;
import org.springframework.tests.context.SimpleMapScope;
import org.springframework.util.SerializationTestUtils;


/**
 * Integration tests for processing ComponentScan-annotated Configuration classes.
 *
 * @author Chris Beams
 * @author Juergen Hoeller
 * @author Sam Brannen
 * @since 3.1
 */
@SuppressWarnings("resource")
public class ComponentScanAnnotationIntegrationTests {
    @Test
    public void controlScan() {
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
        ctx.scan(_package.class.getPackage().getName());
        ctx.refresh();
        Assert.assertThat("control scan for example.scannable package failed to register FooServiceImpl bean", ctx.containsBean("fooServiceImpl"), CoreMatchers.is(true));
    }

    @Test
    public void viaContextRegistration() {
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
        ctx.register(ComponentScanAnnotatedConfig.class);
        ctx.refresh();
        ctx.getBean(ComponentScanAnnotatedConfig.class);
        ctx.getBean(TestBean.class);
        Assert.assertThat("config class bean not found", ctx.containsBeanDefinition("componentScanAnnotatedConfig"), CoreMatchers.is(true));
        Assert.assertThat(("@ComponentScan annotated @Configuration class registered directly against " + "AnnotationConfigApplicationContext did not trigger component scanning as expected"), ctx.containsBean("fooServiceImpl"), CoreMatchers.is(true));
    }

    @Test
    public void viaContextRegistration_WithValueAttribute() {
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
        ctx.register(ComponentScanAnnotatedConfig_WithValueAttribute.class);
        ctx.refresh();
        ctx.getBean(ComponentScanAnnotatedConfig_WithValueAttribute.class);
        ctx.getBean(TestBean.class);
        Assert.assertThat("config class bean not found", ctx.containsBeanDefinition("componentScanAnnotatedConfig_WithValueAttribute"), CoreMatchers.is(true));
        Assert.assertThat(("@ComponentScan annotated @Configuration class registered directly against " + "AnnotationConfigApplicationContext did not trigger component scanning as expected"), ctx.containsBean("fooServiceImpl"), CoreMatchers.is(true));
    }

    @Test
    public void viaContextRegistration_FromPackageOfConfigClass() {
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
        ctx.register(ComponentScanAnnotatedConfigWithImplicitBasePackage.class);
        ctx.refresh();
        ctx.getBean(ComponentScanAnnotatedConfigWithImplicitBasePackage.class);
        Assert.assertThat("config class bean not found", ctx.containsBeanDefinition("componentScanAnnotatedConfigWithImplicitBasePackage"), CoreMatchers.is(true));
        Assert.assertThat(("@ComponentScan annotated @Configuration class registered directly against " + "AnnotationConfigApplicationContext did not trigger component scanning as expected"), ctx.containsBean("scannedComponent"), CoreMatchers.is(true));
        Assert.assertThat("@Bean method overrides scanned class", ctx.getBean(ConfigurableComponent.class).isFlag(), CoreMatchers.is(true));
    }

    @Test
    public void viaContextRegistration_WithComposedAnnotation() {
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
        ctx.register(ComponentScanAnnotationIntegrationTests.ComposedAnnotationConfig.class);
        ctx.refresh();
        ctx.getBean(ComponentScanAnnotationIntegrationTests.ComposedAnnotationConfig.class);
        ctx.getBean(SimpleComponent.class);
        ctx.getBean(ClassWithNestedComponents.NestedComponent.class);
        ctx.getBean(ClassWithNestedComponents.OtherNestedComponent.class);
        Assert.assertThat("config class bean not found", ctx.containsBeanDefinition("componentScanAnnotationIntegrationTests.ComposedAnnotationConfig"), CoreMatchers.is(true));
        Assert.assertThat(("@ComponentScan annotated @Configuration class registered directly against " + "AnnotationConfigApplicationContext did not trigger component scanning as expected"), ctx.containsBean("simpleComponent"), CoreMatchers.is(true));
    }

    @Test
    public void viaBeanRegistration() {
        DefaultListableBeanFactory bf = new DefaultListableBeanFactory();
        bf.registerBeanDefinition("componentScanAnnotatedConfig", genericBeanDefinition(ComponentScanAnnotatedConfig.class).getBeanDefinition());
        bf.registerBeanDefinition("configurationClassPostProcessor", genericBeanDefinition(ConfigurationClassPostProcessor.class).getBeanDefinition());
        GenericApplicationContext ctx = new GenericApplicationContext(bf);
        ctx.refresh();
        ctx.getBean(ComponentScanAnnotatedConfig.class);
        ctx.getBean(TestBean.class);
        Assert.assertThat("config class bean not found", ctx.containsBeanDefinition("componentScanAnnotatedConfig"), CoreMatchers.is(true));
        Assert.assertThat(("@ComponentScan annotated @Configuration class registered " + "as bean definition did not trigger component scanning as expected"), ctx.containsBean("fooServiceImpl"), CoreMatchers.is(true));
    }

    @Test
    public void withCustomBeanNameGenerator() {
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
        ctx.register(ComponentScanWithBeanNameGenerator.class);
        ctx.refresh();
        Assert.assertThat(ctx.containsBean("custom_fooServiceImpl"), CoreMatchers.is(true));
        Assert.assertThat(ctx.containsBean("fooServiceImpl"), CoreMatchers.is(false));
    }

    @Test
    public void withScopeResolver() {
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(ComponentScanWithScopeResolver.class);
        // custom scope annotation makes the bean prototype scoped. subsequent calls
        // to getBean should return distinct instances.
        Assert.assertThat(ctx.getBean(CustomScopeAnnotationBean.class), CoreMatchers.not(CoreMatchers.sameInstance(ctx.getBean(CustomScopeAnnotationBean.class))));
        Assert.assertThat(ctx.containsBean("scannedComponent"), CoreMatchers.is(false));
    }

    @Test
    public void multiComponentScan() {
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(MultiComponentScan.class);
        Assert.assertThat(ctx.getBean(CustomScopeAnnotationBean.class), CoreMatchers.not(CoreMatchers.sameInstance(ctx.getBean(CustomScopeAnnotationBean.class))));
        Assert.assertThat(ctx.containsBean("scannedComponent"), CoreMatchers.is(true));
    }

    @Test
    public void withCustomTypeFilter() {
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(ComponentScanWithCustomTypeFilter.class);
        Assert.assertFalse(ctx.getDefaultListableBeanFactory().containsSingleton("componentScanParserTests.KustomAnnotationAutowiredBean"));
        ComponentScanParserTests.KustomAnnotationAutowiredBean testBean = ctx.getBean("componentScanParserTests.KustomAnnotationAutowiredBean", ComponentScanParserTests.KustomAnnotationAutowiredBean.class);
        Assert.assertThat(testBean.getDependency(), CoreMatchers.notNullValue());
    }

    @Test
    public void withAwareTypeFilter() {
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(ComponentScanWithAwareTypeFilter.class);
        Assert.assertTrue(ctx.getEnvironment().acceptsProfiles(Profiles.of("the-filter-ran")));
    }

    @Test
    public void withScopedProxy() throws IOException, ClassNotFoundException {
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
        ctx.register(ComponentScanWithScopedProxy.class);
        ctx.getBeanFactory().registerScope("myScope", new SimpleMapScope());
        ctx.refresh();
        // should cast to the interface
        FooService bean = ((FooService) (ctx.getBean("scopedProxyTestBean")));
        // should be dynamic proxy
        Assert.assertThat(AopUtils.isJdkDynamicProxy(bean), CoreMatchers.is(true));
        // test serializability
        Assert.assertThat(bean.foo(1), CoreMatchers.equalTo("bar"));
        FooService deserialized = ((FooService) (SerializationTestUtils.serializeAndDeserialize(bean)));
        Assert.assertThat(deserialized, CoreMatchers.notNullValue());
        Assert.assertThat(deserialized.foo(1), CoreMatchers.equalTo("bar"));
    }

    @Test
    public void withScopedProxyThroughRegex() throws IOException, ClassNotFoundException {
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
        ctx.register(ComponentScanWithScopedProxyThroughRegex.class);
        ctx.getBeanFactory().registerScope("myScope", new SimpleMapScope());
        ctx.refresh();
        // should cast to the interface
        FooService bean = ((FooService) (ctx.getBean("scopedProxyTestBean")));
        // should be dynamic proxy
        Assert.assertThat(AopUtils.isJdkDynamicProxy(bean), CoreMatchers.is(true));
    }

    @Test
    public void withScopedProxyThroughAspectJPattern() throws IOException, ClassNotFoundException {
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
        ctx.register(ComponentScanWithScopedProxyThroughAspectJPattern.class);
        ctx.getBeanFactory().registerScope("myScope", new SimpleMapScope());
        ctx.refresh();
        // should cast to the interface
        FooService bean = ((FooService) (ctx.getBean("scopedProxyTestBean")));
        // should be dynamic proxy
        Assert.assertThat(AopUtils.isJdkDynamicProxy(bean), CoreMatchers.is(true));
    }

    @Test
    public void withMultipleAnnotationIncludeFilters1() throws IOException, ClassNotFoundException {
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
        ctx.register(ComponentScanWithMultipleAnnotationIncludeFilters1.class);
        ctx.refresh();
        ctx.getBean(DefaultNamedComponent.class);// @CustomStereotype-annotated

        ctx.getBean(MessageBean.class);// @CustomComponent-annotated

    }

    @Test
    public void withMultipleAnnotationIncludeFilters2() throws IOException, ClassNotFoundException {
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
        ctx.register(ComponentScanWithMultipleAnnotationIncludeFilters2.class);
        ctx.refresh();
        ctx.getBean(DefaultNamedComponent.class);// @CustomStereotype-annotated

        ctx.getBean(MessageBean.class);// @CustomComponent-annotated

    }

    @Test
    public void withBasePackagesAndValueAlias() {
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
        ctx.register(ComponentScanWithBasePackagesAndValueAlias.class);
        ctx.refresh();
        Assert.assertThat(ctx.containsBean("fooServiceImpl"), CoreMatchers.is(true));
    }

    @Configuration
    @ComponentScan
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    public @interface ComposedConfiguration {
        String[] basePackages() default {  };
    }

    @ComponentScanAnnotationIntegrationTests.ComposedConfiguration(basePackages = "org.springframework.context.annotation.componentscan.simple")
    public static class ComposedAnnotationConfig {}

    public static class AwareTypeFilter implements BeanClassLoaderAware , BeanFactoryAware , EnvironmentAware , ResourceLoaderAware , TypeFilter {
        private BeanFactory beanFactory;

        private ClassLoader classLoader;

        private ResourceLoader resourceLoader;

        private Environment environment;

        @Override
        public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
            this.beanFactory = beanFactory;
        }

        @Override
        public void setBeanClassLoader(ClassLoader classLoader) {
            this.classLoader = classLoader;
        }

        @Override
        public void setResourceLoader(ResourceLoader resourceLoader) {
            this.resourceLoader = resourceLoader;
        }

        @Override
        public void setEnvironment(Environment environment) {
            this.environment = environment;
        }

        @Override
        public boolean match(MetadataReader metadataReader, MetadataReaderFactory metadataReaderFactory) {
            addActiveProfile("the-filter-ran");
            Assert.assertNotNull(this.beanFactory);
            Assert.assertNotNull(this.classLoader);
            Assert.assertNotNull(this.resourceLoader);
            Assert.assertNotNull(this.environment);
            return false;
        }
    }
}

