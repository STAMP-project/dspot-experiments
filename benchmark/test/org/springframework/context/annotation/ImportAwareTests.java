/**
 * Copyright 2002-2014 the original author or authors.
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


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.scheduling.annotation.AsyncAnnotationBeanPostProcessor;

import static ConfigurationPhase.REGISTER_BEAN;


/**
 * Tests that an ImportAware @Configuration classes gets injected with the
 * annotation metadata of the @Configuration class that imported it.
 *
 * @author Chris Beams
 * @author Juergen Hoeller
 * @since 3.1
 */
public class ImportAwareTests {
    @Test
    public void directlyAnnotatedWithImport() {
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
        ctx.register(ImportAwareTests.ImportingConfig.class);
        ctx.refresh();
        Assert.assertNotNull(ctx.getBean("importedConfigBean"));
        ImportAwareTests.ImportedConfig importAwareConfig = ctx.getBean(ImportAwareTests.ImportedConfig.class);
        AnnotationMetadata importMetadata = importAwareConfig.importMetadata;
        Assert.assertThat("import metadata was not injected", importMetadata, CoreMatchers.notNullValue());
        Assert.assertThat(importMetadata.getClassName(), CoreMatchers.is(ImportAwareTests.ImportingConfig.class.getName()));
        AnnotationAttributes importAttribs = AnnotationConfigUtils.attributesFor(importMetadata, Import.class);
        Class<?>[] importedClasses = importAttribs.getClassArray("value");
        Assert.assertThat(importedClasses[0].getName(), CoreMatchers.is(ImportAwareTests.ImportedConfig.class.getName()));
    }

    @Test
    public void indirectlyAnnotatedWithImport() {
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
        ctx.register(ImportAwareTests.IndirectlyImportingConfig.class);
        ctx.refresh();
        Assert.assertNotNull(ctx.getBean("importedConfigBean"));
        ImportAwareTests.ImportedConfig importAwareConfig = ctx.getBean(ImportAwareTests.ImportedConfig.class);
        AnnotationMetadata importMetadata = importAwareConfig.importMetadata;
        Assert.assertThat("import metadata was not injected", importMetadata, CoreMatchers.notNullValue());
        Assert.assertThat(importMetadata.getClassName(), CoreMatchers.is(ImportAwareTests.IndirectlyImportingConfig.class.getName()));
        AnnotationAttributes enableAttribs = AnnotationConfigUtils.attributesFor(importMetadata, ImportAwareTests.EnableImportedConfig.class);
        String foo = enableAttribs.getString("foo");
        Assert.assertThat(foo, CoreMatchers.is("xyz"));
    }

    @Test
    public void importRegistrar() throws Exception {
        ImportAwareTests.ImportedRegistrar.called = false;
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
        ctx.register(ImportAwareTests.ImportingRegistrarConfig.class);
        ctx.refresh();
        Assert.assertNotNull(ctx.getBean("registrarImportedBean"));
        Assert.assertNotNull(ctx.getBean("otherImportedConfigBean"));
    }

    @Test
    public void importRegistrarWithImport() throws Exception {
        ImportAwareTests.ImportedRegistrar.called = false;
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
        ctx.register(ImportAwareTests.ImportingRegistrarConfigWithImport.class);
        ctx.refresh();
        Assert.assertNotNull(ctx.getBean("registrarImportedBean"));
        Assert.assertNotNull(ctx.getBean("otherImportedConfigBean"));
        Assert.assertNotNull(ctx.getBean("importedConfigBean"));
        Assert.assertNotNull(ctx.getBean(ImportAwareTests.ImportedConfig.class));
    }

    @Test
    public void metadataFromImportsOneThenTwo() {
        AnnotationMetadata importMetadata = new AnnotationConfigApplicationContext(ImportAwareTests.ConfigurationOne.class, ImportAwareTests.ConfigurationTwo.class).getBean(ImportAwareTests.MetadataHolder.class).importMetadata;
        Assert.assertEquals(ImportAwareTests.ConfigurationOne.class, getIntrospectedClass());
    }

    @Test
    public void metadataFromImportsTwoThenOne() {
        AnnotationMetadata importMetadata = new AnnotationConfigApplicationContext(ImportAwareTests.ConfigurationTwo.class, ImportAwareTests.ConfigurationOne.class).getBean(ImportAwareTests.MetadataHolder.class).importMetadata;
        Assert.assertEquals(ImportAwareTests.ConfigurationOne.class, getIntrospectedClass());
    }

    @Configuration
    @Import(ImportAwareTests.ImportedConfig.class)
    static class ImportingConfig {}

    @Configuration
    @ImportAwareTests.EnableImportedConfig(foo = "xyz")
    static class IndirectlyImportingConfig {}

    @Target(ElementType.TYPE)
    @Retention(RetentionPolicy.RUNTIME)
    @Import(ImportAwareTests.ImportedConfig.class)
    public @interface EnableImportedConfig {
        String foo() default "";
    }

    @Configuration
    static class ImportedConfig implements ImportAware {
        AnnotationMetadata importMetadata;

        @Override
        public void setImportMetadata(AnnotationMetadata importMetadata) {
            this.importMetadata = importMetadata;
        }

        @Bean
        public ImportAwareTests.BPP importedConfigBean() {
            return new ImportAwareTests.BPP();
        }

        @Bean
        public AsyncAnnotationBeanPostProcessor asyncBPP() {
            return new AsyncAnnotationBeanPostProcessor();
        }
    }

    @Configuration
    static class OtherImportedConfig {
        @Bean
        public String otherImportedConfigBean() {
            return "";
        }
    }

    static class BPP implements BeanFactoryAware , BeanPostProcessor {
        @Override
        public void setBeanFactory(BeanFactory beanFactory) {
        }

        @Override
        public Object postProcessBeforeInitialization(Object bean, String beanName) {
            return bean;
        }

        @Override
        public Object postProcessAfterInitialization(Object bean, String beanName) {
            return bean;
        }
    }

    @Configuration
    @ImportAwareTests.EnableImportRegistrar
    static class ImportingRegistrarConfig {}

    @Configuration
    @ImportAwareTests.EnableImportRegistrar
    @Import(ImportAwareTests.ImportedConfig.class)
    static class ImportingRegistrarConfigWithImport {}

    @Target(ElementType.TYPE)
    @Retention(RetentionPolicy.RUNTIME)
    @Import(ImportAwareTests.ImportedRegistrar.class)
    public @interface EnableImportRegistrar {}

    static class ImportedRegistrar implements ImportBeanDefinitionRegistrar {
        static boolean called;

        @Override
        public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
            GenericBeanDefinition beanDefinition = new GenericBeanDefinition();
            beanDefinition.setBeanClassName(String.class.getName());
            registry.registerBeanDefinition("registrarImportedBean", beanDefinition);
            GenericBeanDefinition beanDefinition2 = new GenericBeanDefinition();
            beanDefinition2.setBeanClass(ImportAwareTests.OtherImportedConfig.class);
            registry.registerBeanDefinition("registrarImportedConfig", beanDefinition2);
            org.springframework.util.Assert.state((!(ImportAwareTests.ImportedRegistrar.called)), "ImportedRegistrar called twice");
            ImportAwareTests.ImportedRegistrar.called = true;
        }
    }

    @ImportAwareTests.EnableSomeConfiguration("bar")
    @Configuration
    public static class ConfigurationOne {}

    @Conditional(ImportAwareTests.OnMissingBeanCondition.class)
    @ImportAwareTests.EnableSomeConfiguration("foo")
    @Configuration
    public static class ConfigurationTwo {}

    @Import(ImportAwareTests.SomeConfiguration.class)
    @Target(ElementType.TYPE)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface EnableSomeConfiguration {
        String value() default "";
    }

    @Configuration
    public static class SomeConfiguration implements ImportAware {
        private AnnotationMetadata importMetadata;

        @Override
        public void setImportMetadata(AnnotationMetadata importMetadata) {
            this.importMetadata = importMetadata;
        }

        @Bean
        public ImportAwareTests.MetadataHolder holder() {
            return new ImportAwareTests.MetadataHolder(this.importMetadata);
        }
    }

    public static class MetadataHolder {
        private final AnnotationMetadata importMetadata;

        public MetadataHolder(AnnotationMetadata importMetadata) {
            this.importMetadata = importMetadata;
        }
    }

    private static final class OnMissingBeanCondition implements ConfigurationCondition {
        @Override
        public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
            return (context.getBeanFactory().getBeanNamesForType(ImportAwareTests.MetadataHolder.class, true, false).length) == 0;
        }

        @Override
        public ConfigurationPhase getConfigurationPhase() {
            return REGISTER_BEAN;
        }
    }
}

