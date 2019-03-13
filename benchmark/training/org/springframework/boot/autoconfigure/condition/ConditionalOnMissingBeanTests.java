/**
 * Copyright 2012-2018 the original author or authors.
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
package org.springframework.boot.autoconfigure.condition;


import OnBeanCondition.FACTORY_BEAN_OBJECT_TYPE;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Date;
import org.junit.Test;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.boot.autoconfigure.condition.scan.ScannedFactoryBeanConfiguration;
import org.springframework.boot.autoconfigure.condition.scan.ScannedFactoryBeanWithBeanMethodArgumentsConfiguration;
import org.springframework.boot.autoconfigure.context.PropertyPlaceholderAutoConfiguration;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.context.annotation.ImportResource;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.util.Assert;

import static SearchStrategy.ANCESTORS;
import static SearchStrategy.CURRENT;


/**
 * Tests for {@link ConditionalOnMissingBean}.
 *
 * @author Dave Syer
 * @author Phillip Webb
 * @author Jakub Kubrynski
 * @author Andy Wilkinson
 */
@SuppressWarnings("resource")
public class ConditionalOnMissingBeanTests {
    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner();

    @Test
    public void testNameOnMissingBeanCondition() {
        this.contextRunner.withUserConfiguration(ConditionalOnMissingBeanTests.FooConfiguration.class, ConditionalOnMissingBeanTests.OnBeanNameConfiguration.class).run(( context) -> {
            assertThat(context).doesNotHaveBean("bar");
            assertThat(context.getBean("foo")).isEqualTo("foo");
        });
    }

    @Test
    public void testNameOnMissingBeanConditionReverseOrder() {
        this.contextRunner.withUserConfiguration(ConditionalOnMissingBeanTests.OnBeanNameConfiguration.class, ConditionalOnMissingBeanTests.FooConfiguration.class).run(( context) -> {
            // Ideally this would be doesNotHaveBean, but the ordering is a
            // problem
            assertThat(context).hasBean("bar");
            assertThat(context.getBean("foo")).isEqualTo("foo");
        });
    }

    @Test
    public void testNameAndTypeOnMissingBeanCondition() {
        // Arguably this should be hasBean, but as things are implemented the conditions
        // specified in the different attributes of @ConditionalOnBean are combined with
        // logical OR (not AND) so if any of them match the condition is true.
        this.contextRunner.withUserConfiguration(ConditionalOnMissingBeanTests.FooConfiguration.class, ConditionalOnMissingBeanTests.OnBeanNameAndTypeConfiguration.class).run(( context) -> assertThat(context).doesNotHaveBean("bar"));
    }

    @Test
    public void hierarchyConsidered() {
        this.contextRunner.withUserConfiguration(ConditionalOnMissingBeanTests.FooConfiguration.class).run(( parent) -> new ApplicationContextRunner().withParent(parent).withUserConfiguration(.class).run(( context) -> assertThat(context.containsLocalBean("bar")).isFalse()));
    }

    @Test
    public void hierarchyNotConsidered() {
        this.contextRunner.withUserConfiguration(ConditionalOnMissingBeanTests.FooConfiguration.class).run(( parent) -> new ApplicationContextRunner().withParent(parent).withUserConfiguration(.class).run(( context) -> assertThat(context.containsLocalBean("bar")).isTrue()));
    }

    @Test
    public void impliedOnBeanMethod() {
        this.contextRunner.withUserConfiguration(ConditionalOnMissingBeanTests.ExampleBeanConfiguration.class, ConditionalOnMissingBeanTests.ImpliedOnBeanMethod.class).run(( context) -> assertThat(context).hasSingleBean(.class));
    }

    @Test
    public void testAnnotationOnMissingBeanCondition() {
        this.contextRunner.withUserConfiguration(ConditionalOnMissingBeanTests.FooConfiguration.class, ConditionalOnMissingBeanTests.OnAnnotationConfiguration.class).run(( context) -> {
            assertThat(context).doesNotHaveBean("bar");
            assertThat(context.getBean("foo")).isEqualTo("foo");
        });
    }

    @Test
    public void testAnnotationOnMissingBeanConditionWithEagerFactoryBean() {
        // Rigorous test for SPR-11069
        this.contextRunner.withUserConfiguration(ConditionalOnMissingBeanTests.FooConfiguration.class, ConditionalOnMissingBeanTests.OnAnnotationConfiguration.class, ConditionalOnMissingBeanTests.FactoryBeanXmlConfiguration.class, PropertyPlaceholderAutoConfiguration.class).run(( context) -> {
            assertThat(context).doesNotHaveBean("bar");
            assertThat(context).hasBean("example");
            assertThat(context.getBean("foo")).isEqualTo("foo");
        });
    }

    @Test
    public void testOnMissingBeanConditionWithFactoryBean() {
        this.contextRunner.withUserConfiguration(ConditionalOnMissingBeanTests.FactoryBeanConfiguration.class, ConditionalOnMissingBeanTests.ConditionalOnFactoryBean.class, PropertyPlaceholderAutoConfiguration.class).run(( context) -> assertThat(context.getBean(.class).toString()).isEqualTo("fromFactory"));
    }

    @Test
    public void testOnMissingBeanConditionWithComponentScannedFactoryBean() {
        this.contextRunner.withUserConfiguration(ConditionalOnMissingBeanTests.ComponentScannedFactoryBeanBeanMethodConfiguration.class, ConditionalOnMissingBeanTests.ConditionalOnFactoryBean.class, PropertyPlaceholderAutoConfiguration.class).run(( context) -> assertThat(context.getBean(.class).toString()).isEqualTo("fromFactory"));
    }

    @Test
    public void testOnMissingBeanConditionWithComponentScannedFactoryBeanWithBeanMethodArguments() {
        this.contextRunner.withUserConfiguration(ConditionalOnMissingBeanTests.ComponentScannedFactoryBeanBeanMethodWithArgumentsConfiguration.class, ConditionalOnMissingBeanTests.ConditionalOnFactoryBean.class, PropertyPlaceholderAutoConfiguration.class).run(( context) -> assertThat(context.getBean(.class).toString()).isEqualTo("fromFactory"));
    }

    @Test
    public void testOnMissingBeanConditionWithFactoryBeanWithBeanMethodArguments() {
        this.contextRunner.withUserConfiguration(ConditionalOnMissingBeanTests.FactoryBeanWithBeanMethodArgumentsConfiguration.class, ConditionalOnMissingBeanTests.ConditionalOnFactoryBean.class, PropertyPlaceholderAutoConfiguration.class).withPropertyValues("theValue=foo").run(( context) -> assertThat(context.getBean(.class).toString()).isEqualTo("fromFactory"));
    }

    @Test
    public void testOnMissingBeanConditionWithConcreteFactoryBean() {
        this.contextRunner.withUserConfiguration(ConditionalOnMissingBeanTests.ConcreteFactoryBeanConfiguration.class, ConditionalOnMissingBeanTests.ConditionalOnFactoryBean.class, PropertyPlaceholderAutoConfiguration.class).run(( context) -> assertThat(context.getBean(.class).toString()).isEqualTo("fromFactory"));
    }

    @Test
    public void testOnMissingBeanConditionWithUnhelpfulFactoryBean() {
        // We could not tell that the FactoryBean would ultimately create an ExampleBean
        this.contextRunner.withUserConfiguration(ConditionalOnMissingBeanTests.UnhelpfulFactoryBeanConfiguration.class, ConditionalOnMissingBeanTests.ConditionalOnFactoryBean.class, PropertyPlaceholderAutoConfiguration.class).run(( context) -> assertThat(context).getBeans(.class).hasSize(2));
    }

    @Test
    public void testOnMissingBeanConditionWithRegisteredFactoryBean() {
        this.contextRunner.withUserConfiguration(ConditionalOnMissingBeanTests.RegisteredFactoryBeanConfiguration.class, ConditionalOnMissingBeanTests.ConditionalOnFactoryBean.class, PropertyPlaceholderAutoConfiguration.class).run(( context) -> assertThat(context.getBean(.class).toString()).isEqualTo("fromFactory"));
    }

    @Test
    public void testOnMissingBeanConditionWithNonspecificFactoryBeanWithClassAttribute() {
        this.contextRunner.withUserConfiguration(ConditionalOnMissingBeanTests.NonspecificFactoryBeanClassAttributeConfiguration.class, ConditionalOnMissingBeanTests.ConditionalOnFactoryBean.class, PropertyPlaceholderAutoConfiguration.class).run(( context) -> assertThat(context.getBean(.class).toString()).isEqualTo("fromFactory"));
    }

    @Test
    public void testOnMissingBeanConditionWithNonspecificFactoryBeanWithStringAttribute() {
        this.contextRunner.withUserConfiguration(ConditionalOnMissingBeanTests.NonspecificFactoryBeanStringAttributeConfiguration.class, ConditionalOnMissingBeanTests.ConditionalOnFactoryBean.class, PropertyPlaceholderAutoConfiguration.class).run(( context) -> assertThat(context.getBean(.class).toString()).isEqualTo("fromFactory"));
    }

    @Test
    public void testOnMissingBeanConditionWithFactoryBeanInXml() {
        this.contextRunner.withUserConfiguration(ConditionalOnMissingBeanTests.FactoryBeanXmlConfiguration.class, ConditionalOnMissingBeanTests.ConditionalOnFactoryBean.class, PropertyPlaceholderAutoConfiguration.class).run(( context) -> assertThat(context.getBean(.class).toString()).isEqualTo("fromFactory"));
    }

    @Test
    public void testOnMissingBeanConditionWithIgnoredSubclass() {
        this.contextRunner.withUserConfiguration(ConditionalOnMissingBeanTests.CustomExampleBeanConfiguration.class, ConditionalOnMissingBeanTests.ConditionalOnIgnoredSubclass.class, PropertyPlaceholderAutoConfiguration.class).run(( context) -> {
            assertThat(context).getBeans(.class).hasSize(2);
            assertThat(context).getBeans(.class).hasSize(1);
        });
    }

    @Test
    public void testOnMissingBeanConditionWithIgnoredSubclassByName() {
        this.contextRunner.withUserConfiguration(ConditionalOnMissingBeanTests.CustomExampleBeanConfiguration.class, ConditionalOnMissingBeanTests.ConditionalOnIgnoredSubclassByName.class, PropertyPlaceholderAutoConfiguration.class).run(( context) -> {
            assertThat(context).getBeans(.class).hasSize(2);
            assertThat(context).getBeans(.class).hasSize(1);
        });
    }

    @Test
    public void grandparentIsConsideredWhenUsingAncestorsStrategy() {
        this.contextRunner.withUserConfiguration(ConditionalOnMissingBeanTests.ExampleBeanConfiguration.class).run(( grandparent) -> new ApplicationContextRunner().withParent(grandparent).run(( parent) -> new ApplicationContextRunner().withParent(parent).withUserConfiguration(.class, .class).run(( context) -> assertThat(context).getBeans(.class).hasSize(1))));
    }

    @Test
    public void currentContextIsIgnoredWhenUsingAncestorsStrategy() {
        this.contextRunner.run(( parent) -> new ApplicationContextRunner().withParent(parent).withUserConfiguration(.class, .class).run(( context) -> assertThat(context).getBeans(.class).hasSize(2)));
    }

    @Test
    public void beanProducedByFactoryBeanIsConsideredWhenMatchingOnAnnotation() {
        this.contextRunner.withUserConfiguration(ConditionalOnMissingBeanTests.ConcreteFactoryBeanConfiguration.class, ConditionalOnMissingBeanTests.OnAnnotationWithFactoryBeanConfiguration.class).run(( context) -> {
            assertThat(context).doesNotHaveBean("bar");
            assertThat(context).hasSingleBean(.class);
        });
    }

    @Test
    public void parameterizedContainerWhenValueIsOfMissingBeanMatches() {
        this.contextRunner.withUserConfiguration(ConditionalOnMissingBeanTests.ParameterizedWithoutCustomConfig.class, ConditionalOnMissingBeanTests.ParameterizedConditionWithValueConfig.class).run(( context) -> assertThat(context).satisfies(exampleBeanRequirement("otherExampleBean", "conditionalCustomExampleBean")));
    }

    @Test
    public void parameterizedContainerWhenValueIsOfExistingBeanDoesNotMatch() {
        this.contextRunner.withUserConfiguration(ConditionalOnMissingBeanTests.ParameterizedWithCustomConfig.class, ConditionalOnMissingBeanTests.ParameterizedConditionWithValueConfig.class).run(( context) -> assertThat(context).satisfies(exampleBeanRequirement("customExampleBean")));
    }

    @Test
    public void parameterizedContainerWhenValueIsOfMissingBeanRegistrationMatches() {
        this.contextRunner.withUserConfiguration(ConditionalOnMissingBeanTests.ParameterizedWithoutCustomContainerConfig.class, ConditionalOnMissingBeanTests.ParameterizedConditionWithValueConfig.class).run(( context) -> assertThat(context).satisfies(exampleBeanRequirement("otherExampleBean", "conditionalCustomExampleBean")));
    }

    @Test
    public void parameterizedContainerWhenValueIsOfExistingBeanRegistrationDoesNotMatch() {
        this.contextRunner.withUserConfiguration(ConditionalOnMissingBeanTests.ParameterizedWithCustomContainerConfig.class, ConditionalOnMissingBeanTests.ParameterizedConditionWithValueConfig.class).run(( context) -> assertThat(context).satisfies(exampleBeanRequirement("customExampleBean")));
    }

    @Test
    public void parameterizedContainerWhenReturnTypeIsOfExistingBeanDoesNotMatch() {
        this.contextRunner.withUserConfiguration(ConditionalOnMissingBeanTests.ParameterizedWithCustomConfig.class, ConditionalOnMissingBeanTests.ParameterizedConditionWithReturnTypeConfig.class).run(( context) -> assertThat(context).satisfies(exampleBeanRequirement("customExampleBean")));
    }

    @Test
    public void parameterizedContainerWhenReturnTypeIsOfExistingBeanRegistrationDoesNotMatch() {
        this.contextRunner.withUserConfiguration(ConditionalOnMissingBeanTests.ParameterizedWithCustomContainerConfig.class, ConditionalOnMissingBeanTests.ParameterizedConditionWithReturnTypeConfig.class).run(( context) -> assertThat(context).satisfies(exampleBeanRequirement("customExampleBean")));
    }

    @Test
    public void parameterizedContainerWhenReturnRegistrationTypeIsOfExistingBeanDoesNotMatch() {
        this.contextRunner.withUserConfiguration(ConditionalOnMissingBeanTests.ParameterizedWithCustomConfig.class, ConditionalOnMissingBeanTests.ParameterizedConditionWithReturnRegistrationTypeConfig.class).run(( context) -> assertThat(context).satisfies(exampleBeanRequirement("customExampleBean")));
    }

    @Test
    public void parameterizedContainerWhenReturnRegistrationTypeIsOfExistingBeanRegistrationDoesNotMatch() {
        this.contextRunner.withUserConfiguration(ConditionalOnMissingBeanTests.ParameterizedWithCustomContainerConfig.class, ConditionalOnMissingBeanTests.ParameterizedConditionWithReturnRegistrationTypeConfig.class).run(( context) -> assertThat(context).satisfies(exampleBeanRequirement("customExampleBean")));
    }

    @Configuration
    protected static class OnBeanInAncestorsConfiguration {
        @Bean
        @ConditionalOnMissingBean(search = ANCESTORS)
        public ConditionalOnMissingBeanTests.ExampleBean exampleBean2() {
            return new ConditionalOnMissingBeanTests.ExampleBean("test");
        }
    }

    @Configuration
    @ConditionalOnMissingBean(name = "foo")
    protected static class OnBeanNameConfiguration {
        @Bean
        public String bar() {
            return "bar";
        }
    }

    @Configuration
    @ConditionalOnMissingBean(name = "foo", value = Date.class)
    @ConditionalOnBean(name = "foo", value = Date.class)
    protected static class OnBeanNameAndTypeConfiguration {
        @Bean
        public String bar() {
            return "bar";
        }
    }

    @Configuration
    protected static class FactoryBeanConfiguration {
        @Bean
        public FactoryBean<ConditionalOnMissingBeanTests.ExampleBean> exampleBeanFactoryBean() {
            return new ConditionalOnMissingBeanTests.ExampleFactoryBean("foo");
        }
    }

    @Configuration
    @ComponentScan(basePackages = "org.springframework.boot.autoconfigure.condition.scan", includeFilters = @Filter(type = FilterType.ASSIGNABLE_TYPE, classes = ScannedFactoryBeanConfiguration.class))
    protected static class ComponentScannedFactoryBeanBeanMethodConfiguration {}

    @Configuration
    @ComponentScan(basePackages = "org.springframework.boot.autoconfigure.condition.scan", includeFilters = @Filter(type = FilterType.ASSIGNABLE_TYPE, classes = ScannedFactoryBeanWithBeanMethodArgumentsConfiguration.class))
    protected static class ComponentScannedFactoryBeanBeanMethodWithArgumentsConfiguration {}

    @Configuration
    protected static class FactoryBeanWithBeanMethodArgumentsConfiguration {
        @Bean
        public FactoryBean<ConditionalOnMissingBeanTests.ExampleBean> exampleBeanFactoryBean(@Value("${theValue}")
        String value) {
            return new ConditionalOnMissingBeanTests.ExampleFactoryBean(value);
        }
    }

    @Configuration
    protected static class ConcreteFactoryBeanConfiguration {
        @Bean
        public ConditionalOnMissingBeanTests.ExampleFactoryBean exampleBeanFactoryBean() {
            return new ConditionalOnMissingBeanTests.ExampleFactoryBean("foo");
        }
    }

    @Configuration
    protected static class UnhelpfulFactoryBeanConfiguration {
        @Bean
        @SuppressWarnings("rawtypes")
        public FactoryBean exampleBeanFactoryBean() {
            return new ConditionalOnMissingBeanTests.ExampleFactoryBean("foo");
        }
    }

    @Configuration
    @Import(ConditionalOnMissingBeanTests.NonspecificFactoryBeanClassAttributeRegistrar.class)
    protected static class NonspecificFactoryBeanClassAttributeConfiguration {}

    protected static class NonspecificFactoryBeanClassAttributeRegistrar implements ImportBeanDefinitionRegistrar {
        @Override
        public void registerBeanDefinitions(AnnotationMetadata meta, BeanDefinitionRegistry registry) {
            BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(ConditionalOnMissingBeanTests.NonspecificFactoryBean.class);
            builder.addConstructorArgValue("foo");
            builder.getBeanDefinition().setAttribute(FACTORY_BEAN_OBJECT_TYPE, ConditionalOnMissingBeanTests.ExampleBean.class);
            registry.registerBeanDefinition("exampleBeanFactoryBean", builder.getBeanDefinition());
        }
    }

    @Configuration
    @Import(ConditionalOnMissingBeanTests.NonspecificFactoryBeanClassAttributeRegistrar.class)
    protected static class NonspecificFactoryBeanStringAttributeConfiguration {}

    protected static class NonspecificFactoryBeanStringAttributeRegistrar implements ImportBeanDefinitionRegistrar {
        @Override
        public void registerBeanDefinitions(AnnotationMetadata meta, BeanDefinitionRegistry registry) {
            BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(ConditionalOnMissingBeanTests.NonspecificFactoryBean.class);
            builder.addConstructorArgValue("foo");
            builder.getBeanDefinition().setAttribute(FACTORY_BEAN_OBJECT_TYPE, ConditionalOnMissingBeanTests.ExampleBean.class.getName());
            registry.registerBeanDefinition("exampleBeanFactoryBean", builder.getBeanDefinition());
        }
    }

    @Configuration
    @Import(ConditionalOnMissingBeanTests.FactoryBeanRegistrar.class)
    protected static class RegisteredFactoryBeanConfiguration {}

    protected static class FactoryBeanRegistrar implements ImportBeanDefinitionRegistrar {
        @Override
        public void registerBeanDefinitions(AnnotationMetadata meta, BeanDefinitionRegistry registry) {
            BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(ConditionalOnMissingBeanTests.ExampleFactoryBean.class);
            builder.addConstructorArgValue("foo");
            registry.registerBeanDefinition("exampleBeanFactoryBean", builder.getBeanDefinition());
        }
    }

    @Configuration
    @ImportResource("org/springframework/boot/autoconfigure/condition/factorybean.xml")
    protected static class FactoryBeanXmlConfiguration {}

    @Configuration
    protected static class ConditionalOnFactoryBean {
        @Bean
        @ConditionalOnMissingBean(ConditionalOnMissingBeanTests.ExampleBean.class)
        public ConditionalOnMissingBeanTests.ExampleBean createExampleBean() {
            return new ConditionalOnMissingBeanTests.ExampleBean("direct");
        }
    }

    @Configuration
    protected static class ConditionalOnIgnoredSubclass {
        @Bean
        @ConditionalOnMissingBean(value = ConditionalOnMissingBeanTests.ExampleBean.class, ignored = ConditionalOnMissingBeanTests.CustomExampleBean.class)
        public ConditionalOnMissingBeanTests.ExampleBean exampleBean() {
            return new ConditionalOnMissingBeanTests.ExampleBean("test");
        }
    }

    @Configuration
    protected static class ConditionalOnIgnoredSubclassByName {
        @Bean
        @ConditionalOnMissingBean(value = ConditionalOnMissingBeanTests.ExampleBean.class, ignoredType = "org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBeanTests.CustomExampleBean")
        public ConditionalOnMissingBeanTests.ExampleBean exampleBean() {
            return new ConditionalOnMissingBeanTests.ExampleBean("test");
        }
    }

    @Configuration
    protected static class CustomExampleBeanConfiguration {
        @Bean
        public ConditionalOnMissingBeanTests.CustomExampleBean customExampleBean() {
            return new ConditionalOnMissingBeanTests.CustomExampleBean();
        }
    }

    @Configuration
    @ConditionalOnMissingBean(annotation = EnableScheduling.class)
    protected static class OnAnnotationConfiguration {
        @Bean
        public String bar() {
            return "bar";
        }
    }

    @Configuration
    @ConditionalOnMissingBean(annotation = ConditionalOnMissingBeanTests.TestAnnotation.class)
    protected static class OnAnnotationWithFactoryBeanConfiguration {
        @Bean
        public String bar() {
            return "bar";
        }
    }

    @Configuration
    @EnableScheduling
    protected static class FooConfiguration {
        @Bean
        public String foo() {
            return "foo";
        }
    }

    @Configuration
    @ConditionalOnMissingBean(name = "foo")
    protected static class HierarchyConsidered {
        @Bean
        public String bar() {
            return "bar";
        }
    }

    @Configuration
    @ConditionalOnMissingBean(name = "foo", search = CURRENT)
    protected static class HierarchyNotConsidered {
        @Bean
        public String bar() {
            return "bar";
        }
    }

    @Configuration
    protected static class ExampleBeanConfiguration {
        @Bean
        public ConditionalOnMissingBeanTests.ExampleBean exampleBean() {
            return new ConditionalOnMissingBeanTests.ExampleBean("test");
        }
    }

    @Configuration
    protected static class ImpliedOnBeanMethod {
        @Bean
        @ConditionalOnMissingBean
        public ConditionalOnMissingBeanTests.ExampleBean exampleBean2() {
            return new ConditionalOnMissingBeanTests.ExampleBean("test");
        }
    }

    public static class ExampleFactoryBean implements FactoryBean<ConditionalOnMissingBeanTests.ExampleBean> {
        public ExampleFactoryBean(String value) {
            Assert.state((!(value.contains("$"))), "value should not contain '$'");
        }

        @Override
        public ConditionalOnMissingBeanTests.ExampleBean getObject() {
            return new ConditionalOnMissingBeanTests.ExampleBean("fromFactory");
        }

        @Override
        public Class<?> getObjectType() {
            return ConditionalOnMissingBeanTests.ExampleBean.class;
        }

        @Override
        public boolean isSingleton() {
            return false;
        }
    }

    public static class NonspecificFactoryBean implements FactoryBean<Object> {
        public NonspecificFactoryBean(String value) {
            Assert.state((!(value.contains("$"))), "value should not contain '$'");
        }

        @Override
        public ConditionalOnMissingBeanTests.ExampleBean getObject() {
            return new ConditionalOnMissingBeanTests.ExampleBean("fromFactory");
        }

        @Override
        public Class<?> getObjectType() {
            return ConditionalOnMissingBeanTests.ExampleBean.class;
        }

        @Override
        public boolean isSingleton() {
            return false;
        }
    }

    @Configuration
    static class ParameterizedWithCustomConfig {
        @Bean
        public ConditionalOnMissingBeanTests.CustomExampleBean customExampleBean() {
            return new ConditionalOnMissingBeanTests.CustomExampleBean();
        }
    }

    @Configuration
    static class ParameterizedWithoutCustomConfig {
        @Bean
        public ConditionalOnMissingBeanTests.OtherExampleBean otherExampleBean() {
            return new ConditionalOnMissingBeanTests.OtherExampleBean();
        }
    }

    @Configuration
    static class ParameterizedWithoutCustomContainerConfig {
        @Bean
        public TestParameterizedContainer<ConditionalOnMissingBeanTests.OtherExampleBean> otherExampleBean() {
            return new TestParameterizedContainer<>();
        }
    }

    @Configuration
    static class ParameterizedWithCustomContainerConfig {
        @Bean
        public TestParameterizedContainer<ConditionalOnMissingBeanTests.CustomExampleBean> customExampleBean() {
            return new TestParameterizedContainer<>();
        }
    }

    @Configuration
    static class ParameterizedConditionWithValueConfig {
        @Bean
        @ConditionalOnMissingBean(value = ConditionalOnMissingBeanTests.CustomExampleBean.class, parameterizedContainer = TestParameterizedContainer.class)
        public ConditionalOnMissingBeanTests.CustomExampleBean conditionalCustomExampleBean() {
            return new ConditionalOnMissingBeanTests.CustomExampleBean();
        }
    }

    @Configuration
    static class ParameterizedConditionWithReturnTypeConfig {
        @Bean
        @ConditionalOnMissingBean(parameterizedContainer = TestParameterizedContainer.class)
        public ConditionalOnMissingBeanTests.CustomExampleBean conditionalCustomExampleBean() {
            return new ConditionalOnMissingBeanTests.CustomExampleBean();
        }
    }

    @Configuration
    static class ParameterizedConditionWithReturnRegistrationTypeConfig {
        @Bean
        @ConditionalOnMissingBean(parameterizedContainer = TestParameterizedContainer.class)
        public TestParameterizedContainer<ConditionalOnMissingBeanTests.CustomExampleBean> conditionalCustomExampleBean() {
            return new TestParameterizedContainer<>();
        }
    }

    @ConditionalOnMissingBeanTests.TestAnnotation
    public static class ExampleBean {
        private String value;

        public ExampleBean(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return this.value;
        }
    }

    public static class CustomExampleBean extends ConditionalOnMissingBeanTests.ExampleBean {
        public CustomExampleBean() {
            super("custom subclass");
        }
    }

    public static class OtherExampleBean extends ConditionalOnMissingBeanTests.ExampleBean {
        public OtherExampleBean() {
            super("other subclass");
        }
    }

    @Target(ElementType.TYPE)
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    public @interface TestAnnotation {}
}

