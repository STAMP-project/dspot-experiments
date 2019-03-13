/**
 * Copyright 2002-2016 the original author or authors.
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
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.stereotype.Component;

import static ConfigurationPhase.REGISTER_BEAN;


/**
 * Test for {@link Conditional} beans.
 *
 * @author Phillip Webb
 * @author Juergen Hoeller
 */
@SuppressWarnings("resource")
public class ConfigurationClassWithConditionTests {
    @Test
    public void conditionalOnMissingBeanMatch() throws Exception {
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
        ctx.register(ConfigurationClassWithConditionTests.BeanOneConfiguration.class, ConfigurationClassWithConditionTests.BeanTwoConfiguration.class);
        ctx.refresh();
        Assert.assertTrue(ctx.containsBean("bean1"));
        Assert.assertFalse(ctx.containsBean("bean2"));
        Assert.assertFalse(ctx.containsBean("configurationClassWithConditionTests.BeanTwoConfiguration"));
    }

    @Test
    public void conditionalOnMissingBeanNoMatch() throws Exception {
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
        ctx.register(ConfigurationClassWithConditionTests.BeanTwoConfiguration.class);
        ctx.refresh();
        Assert.assertFalse(ctx.containsBean("bean1"));
        Assert.assertTrue(ctx.containsBean("bean2"));
        Assert.assertTrue(ctx.containsBean("configurationClassWithConditionTests.BeanTwoConfiguration"));
    }

    @Test
    public void conditionalOnBeanMatch() throws Exception {
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
        ctx.register(ConfigurationClassWithConditionTests.BeanOneConfiguration.class, ConfigurationClassWithConditionTests.BeanThreeConfiguration.class);
        ctx.refresh();
        Assert.assertTrue(ctx.containsBean("bean1"));
        Assert.assertTrue(ctx.containsBean("bean3"));
    }

    @Test
    public void conditionalOnBeanNoMatch() throws Exception {
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
        ctx.register(ConfigurationClassWithConditionTests.BeanThreeConfiguration.class);
        ctx.refresh();
        Assert.assertFalse(ctx.containsBean("bean1"));
        Assert.assertFalse(ctx.containsBean("bean3"));
    }

    @Test
    public void metaConditional() throws Exception {
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
        ctx.register(ConfigurationClassWithConditionTests.ConfigurationWithMetaCondition.class);
        ctx.refresh();
        Assert.assertTrue(ctx.containsBean("bean"));
    }

    @Test
    public void metaConditionalWithAsm() throws Exception {
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
        ctx.registerBeanDefinition("config", new RootBeanDefinition(ConfigurationClassWithConditionTests.ConfigurationWithMetaCondition.class.getName()));
        ctx.refresh();
        Assert.assertTrue(ctx.containsBean("bean"));
    }

    @Test
    public void nonConfigurationClass() throws Exception {
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
        ctx.register(ConfigurationClassWithConditionTests.NonConfigurationClass.class);
        ctx.refresh();
        Assert.assertFalse(ctx.containsBean("bean1"));
    }

    @Test
    public void nonConfigurationClassWithAsm() throws Exception {
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
        ctx.registerBeanDefinition("config", new RootBeanDefinition(ConfigurationClassWithConditionTests.NonConfigurationClass.class.getName()));
        ctx.refresh();
        Assert.assertFalse(ctx.containsBean("bean1"));
    }

    @Test
    public void methodConditional() throws Exception {
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
        ctx.register(ConfigurationClassWithConditionTests.ConditionOnMethodConfiguration.class);
        ctx.refresh();
        Assert.assertFalse(ctx.containsBean("bean1"));
    }

    @Test
    public void methodConditionalWithAsm() throws Exception {
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
        ctx.registerBeanDefinition("config", new RootBeanDefinition(ConfigurationClassWithConditionTests.ConditionOnMethodConfiguration.class.getName()));
        ctx.refresh();
        Assert.assertFalse(ctx.containsBean("bean1"));
    }

    @Test
    public void importsNotCreated() throws Exception {
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
        ctx.register(ConfigurationClassWithConditionTests.ImportsNotCreated.class);
        ctx.refresh();
    }

    @Test
    public void conditionOnOverriddenMethodHonored() {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(ConfigurationClassWithConditionTests.ConfigWithBeanSkipped.class);
        Assert.assertEquals(0, context.getBeansOfType(ConfigurationClassWithConditionTests.ExampleBean.class).size());
    }

    @Test
    public void noConditionOnOverriddenMethodHonored() {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(ConfigurationClassWithConditionTests.ConfigWithBeanReactivated.class);
        Map<String, ConfigurationClassWithConditionTests.ExampleBean> beans = context.getBeansOfType(ConfigurationClassWithConditionTests.ExampleBean.class);
        Assert.assertEquals(1, beans.size());
        Assert.assertEquals("baz", beans.keySet().iterator().next());
    }

    @Test
    public void configWithAlternativeBeans() {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(ConfigurationClassWithConditionTests.ConfigWithAlternativeBeans.class);
        Map<String, ConfigurationClassWithConditionTests.ExampleBean> beans = context.getBeansOfType(ConfigurationClassWithConditionTests.ExampleBean.class);
        Assert.assertEquals(1, beans.size());
        Assert.assertEquals("baz", beans.keySet().iterator().next());
    }

    @Configuration
    static class BeanOneConfiguration {
        @Bean
        public ConfigurationClassWithConditionTests.ExampleBean bean1() {
            return new ConfigurationClassWithConditionTests.ExampleBean();
        }
    }

    @Configuration
    @Conditional(ConfigurationClassWithConditionTests.NoBeanOneCondition.class)
    static class BeanTwoConfiguration {
        @Bean
        public ConfigurationClassWithConditionTests.ExampleBean bean2() {
            return new ConfigurationClassWithConditionTests.ExampleBean();
        }
    }

    @Configuration
    @Conditional(ConfigurationClassWithConditionTests.HasBeanOneCondition.class)
    static class BeanThreeConfiguration {
        @Bean
        public ConfigurationClassWithConditionTests.ExampleBean bean3() {
            return new ConfigurationClassWithConditionTests.ExampleBean();
        }
    }

    @Configuration
    @ConfigurationClassWithConditionTests.MetaConditional("test")
    static class ConfigurationWithMetaCondition {
        @Bean
        public ConfigurationClassWithConditionTests.ExampleBean bean() {
            return new ConfigurationClassWithConditionTests.ExampleBean();
        }
    }

    @Conditional(ConfigurationClassWithConditionTests.MetaConditionalFilter.class)
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    public @interface MetaConditional {
        String value();
    }

    @Conditional(ConfigurationClassWithConditionTests.NeverCondition.class)
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ ElementType.TYPE, ElementType.METHOD })
    public @interface Never {}

    @Conditional(ConfigurationClassWithConditionTests.AlwaysCondition.class)
    @ConfigurationClassWithConditionTests.Never
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ ElementType.TYPE, ElementType.METHOD })
    public @interface MetaNever {}

    static class NoBeanOneCondition implements Condition {
        @Override
        public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
            return !(context.getBeanFactory().containsBeanDefinition("bean1"));
        }
    }

    static class HasBeanOneCondition implements ConfigurationCondition {
        @Override
        public ConfigurationPhase getConfigurationPhase() {
            return REGISTER_BEAN;
        }

        @Override
        public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
            return context.getBeanFactory().containsBeanDefinition("bean1");
        }
    }

    static class MetaConditionalFilter implements Condition {
        @Override
        public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
            AnnotationAttributes attributes = AnnotationAttributes.fromMap(metadata.getAnnotationAttributes(ConfigurationClassWithConditionTests.MetaConditional.class.getName()));
            Assert.assertThat(attributes.getString("value"), equalTo("test"));
            return true;
        }
    }

    static class NeverCondition implements Condition {
        @Override
        public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
            return false;
        }
    }

    static class AlwaysCondition implements Condition {
        @Override
        public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
            return true;
        }
    }

    @Component
    @ConfigurationClassWithConditionTests.MetaNever
    static class NonConfigurationClass {
        @Bean
        public ConfigurationClassWithConditionTests.ExampleBean bean1() {
            return new ConfigurationClassWithConditionTests.ExampleBean();
        }
    }

    @Configuration
    static class ConditionOnMethodConfiguration {
        @Bean
        @ConfigurationClassWithConditionTests.Never
        public ConfigurationClassWithConditionTests.ExampleBean bean1() {
            return new ConfigurationClassWithConditionTests.ExampleBean();
        }
    }

    @Configuration
    @ConfigurationClassWithConditionTests.Never
    @Import({ ConfigurationClassWithConditionTests.ConfigurationNotCreated.class, ConfigurationClassWithConditionTests.RegistrarNotCreated.class, ConfigurationClassWithConditionTests.ImportSelectorNotCreated.class })
    static class ImportsNotCreated {
        static {
            if (true)
                throw new RuntimeException();

        }
    }

    @Configuration
    static class ConfigurationNotCreated {
        static {
            if (true)
                throw new RuntimeException();

        }
    }

    static class RegistrarNotCreated implements ImportBeanDefinitionRegistrar {
        static {
            if (true)
                throw new RuntimeException();

        }

        @Override
        public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        }
    }

    static class ImportSelectorNotCreated implements ImportSelector {
        static {
            if (true)
                throw new RuntimeException();

        }

        @Override
        public String[] selectImports(AnnotationMetadata importingClassMetadata) {
            return new String[]{  };
        }
    }

    static class ExampleBean {}

    @Configuration
    static class ConfigWithBeanActive {
        @Bean
        public ConfigurationClassWithConditionTests.ExampleBean baz() {
            return new ConfigurationClassWithConditionTests.ExampleBean();
        }
    }

    static class ConfigWithBeanSkipped extends ConfigurationClassWithConditionTests.ConfigWithBeanActive {
        @Override
        @Bean
        @Conditional(ConfigurationClassWithConditionTests.NeverCondition.class)
        public ConfigurationClassWithConditionTests.ExampleBean baz() {
            return new ConfigurationClassWithConditionTests.ExampleBean();
        }
    }

    static class ConfigWithBeanReactivated extends ConfigurationClassWithConditionTests.ConfigWithBeanSkipped {
        @Override
        @Bean
        public ConfigurationClassWithConditionTests.ExampleBean baz() {
            return new ConfigurationClassWithConditionTests.ExampleBean();
        }
    }

    @Configuration
    static class ConfigWithAlternativeBeans {
        @Bean(name = "baz")
        @Conditional(ConfigurationClassWithConditionTests.AlwaysCondition.class)
        public ConfigurationClassWithConditionTests.ExampleBean baz1() {
            return new ConfigurationClassWithConditionTests.ExampleBean();
        }

        @Bean(name = "baz")
        @Conditional(ConfigurationClassWithConditionTests.NeverCondition.class)
        public ConfigurationClassWithConditionTests.ExampleBean baz2() {
            return new ConfigurationClassWithConditionTests.ExampleBean();
        }
    }
}

