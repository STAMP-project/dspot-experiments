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


import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Date;
import org.junit.Test;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.scheduling.annotation.EnableScheduling;


/**
 * Tests for {@link ConditionalOnBean}.
 *
 * @author Dave Syer
 * @author Stephane Nicoll
 */
public class ConditionalOnBeanTests {
    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner();

    @Test
    public void testNameOnBeanCondition() {
        this.contextRunner.withUserConfiguration(ConditionalOnBeanTests.FooConfiguration.class, ConditionalOnBeanTests.OnBeanNameConfiguration.class).run(this::hasBarBean);
    }

    @Test
    public void testNameAndTypeOnBeanCondition() {
        this.contextRunner.withUserConfiguration(ConditionalOnBeanTests.FooConfiguration.class, ConditionalOnBeanTests.OnBeanNameAndTypeConfiguration.class).run(( context) -> assertThat(context).doesNotHaveBean("bar"));
    }

    @Test
    public void testNameOnBeanConditionReverseOrder() {
        // Ideally this should be true
        this.contextRunner.withUserConfiguration(ConditionalOnBeanTests.OnBeanNameConfiguration.class, ConditionalOnBeanTests.FooConfiguration.class).run(( context) -> assertThat(context).doesNotHaveBean("bar"));
    }

    @Test
    public void testClassOnBeanCondition() {
        this.contextRunner.withUserConfiguration(ConditionalOnBeanTests.FooConfiguration.class, ConditionalOnBeanTests.OnBeanClassConfiguration.class).run(this::hasBarBean);
    }

    @Test
    public void testClassOnBeanClassNameCondition() {
        this.contextRunner.withUserConfiguration(ConditionalOnBeanTests.FooConfiguration.class, ConditionalOnBeanTests.OnBeanClassNameConfiguration.class).run(this::hasBarBean);
    }

    @Test
    public void testOnBeanConditionWithXml() {
        this.contextRunner.withUserConfiguration(ConditionalOnBeanTests.XmlConfiguration.class, ConditionalOnBeanTests.OnBeanNameConfiguration.class).run(this::hasBarBean);
    }

    @Test
    public void testOnBeanConditionWithCombinedXml() {
        // Ideally this should be true
        this.contextRunner.withUserConfiguration(ConditionalOnBeanTests.CombinedXmlConfiguration.class).run(( context) -> assertThat(context).doesNotHaveBean("bar"));
    }

    @Test
    public void testAnnotationOnBeanCondition() {
        this.contextRunner.withUserConfiguration(ConditionalOnBeanTests.FooConfiguration.class, ConditionalOnBeanTests.OnAnnotationConfiguration.class).run(this::hasBarBean);
    }

    @Test
    public void testOnMissingBeanType() {
        this.contextRunner.withUserConfiguration(ConditionalOnBeanTests.FooConfiguration.class, ConditionalOnBeanTests.OnBeanMissingClassConfiguration.class).run(( context) -> assertThat(context).doesNotHaveBean("bar"));
    }

    @Test
    public void withPropertyPlaceholderClassName() {
        this.contextRunner.withUserConfiguration(PropertySourcesPlaceholderConfigurer.class, ConditionalOnBeanTests.WithPropertyPlaceholderClassName.class, ConditionalOnBeanTests.OnBeanClassConfiguration.class).withPropertyValues("mybeanclass=java.lang.String").run(( context) -> assertThat(context).hasNotFailed());
    }

    @Test
    public void beanProducedByFactoryBeanIsConsideredWhenMatchingOnAnnotation() {
        this.contextRunner.withUserConfiguration(ConditionalOnBeanTests.FactoryBeanConfiguration.class, ConditionalOnBeanTests.OnAnnotationWithFactoryBeanConfiguration.class).run(( context) -> {
            assertThat(context).hasBean("bar");
            assertThat(context).hasSingleBean(.class);
        });
    }

    @Test
    public void conditionEvaluationConsidersChangeInTypeWhenBeanIsOverridden() {
        this.contextRunner.withUserConfiguration(ConditionalOnBeanTests.OriginalDefinition.class, ConditionalOnBeanTests.OverridingDefinition.class, ConditionalOnBeanTests.ConsumingConfiguration.class).run(( context) -> {
            assertThat(context).hasBean("testBean");
            assertThat(context).hasSingleBean(.class);
            assertThat(context).doesNotHaveBean(.class);
        });
    }

    @Test
    public void parameterizedContainerWhenValueIsOfMissingBeanDoesNotMatch() {
        this.contextRunner.withUserConfiguration(ConditionalOnBeanTests.ParameterizedWithoutCustomConfig.class, ConditionalOnBeanTests.ParameterizedConditionWithValueConfig.class).run(( context) -> assertThat(context).satisfies(exampleBeanRequirement("otherExampleBean")));
    }

    @Test
    public void parameterizedContainerWhenValueIsOfExistingBeanMatches() {
        this.contextRunner.withUserConfiguration(ConditionalOnBeanTests.ParameterizedWithCustomConfig.class, ConditionalOnBeanTests.ParameterizedConditionWithValueConfig.class).run(( context) -> assertThat(context).satisfies(exampleBeanRequirement("customExampleBean", "conditionalCustomExampleBean")));
    }

    @Test
    public void parameterizedContainerWhenValueIsOfMissingBeanRegistrationDoesNotMatch() {
        this.contextRunner.withUserConfiguration(ConditionalOnBeanTests.ParameterizedWithoutCustomContainerConfig.class, ConditionalOnBeanTests.ParameterizedConditionWithValueConfig.class).run(( context) -> assertThat(context).satisfies(exampleBeanRequirement("otherExampleBean")));
    }

    @Test
    public void parameterizedContainerWhenValueIsOfExistingBeanRegistrationMatches() {
        this.contextRunner.withUserConfiguration(ConditionalOnBeanTests.ParameterizedWithCustomContainerConfig.class, ConditionalOnBeanTests.ParameterizedConditionWithValueConfig.class).run(( context) -> assertThat(context).satisfies(exampleBeanRequirement("customExampleBean", "conditionalCustomExampleBean")));
    }

    @Test
    public void parameterizedContainerWhenReturnTypeIsOfExistingBeanMatches() {
        this.contextRunner.withUserConfiguration(ConditionalOnBeanTests.ParameterizedWithCustomConfig.class, ConditionalOnBeanTests.ParameterizedConditionWithReturnTypeConfig.class).run(( context) -> assertThat(context).satisfies(exampleBeanRequirement("customExampleBean", "conditionalCustomExampleBean")));
    }

    @Test
    public void parameterizedContainerWhenReturnTypeIsOfExistingBeanRegistrationMatches() {
        this.contextRunner.withUserConfiguration(ConditionalOnBeanTests.ParameterizedWithCustomContainerConfig.class, ConditionalOnBeanTests.ParameterizedConditionWithReturnTypeConfig.class).run(( context) -> assertThat(context).satisfies(exampleBeanRequirement("customExampleBean", "conditionalCustomExampleBean")));
    }

    @Test
    public void parameterizedContainerWhenReturnRegistrationTypeIsOfExistingBeanMatches() {
        this.contextRunner.withUserConfiguration(ConditionalOnBeanTests.ParameterizedWithCustomConfig.class, ConditionalOnBeanTests.ParameterizedConditionWithReturnRegistrationTypeConfig.class).run(( context) -> assertThat(context).satisfies(exampleBeanRequirement("customExampleBean", "conditionalCustomExampleBean")));
    }

    @Test
    public void parameterizedContainerWhenReturnRegistrationTypeIsOfExistingBeanRegistrationMatches() {
        this.contextRunner.withUserConfiguration(ConditionalOnBeanTests.ParameterizedWithCustomContainerConfig.class, ConditionalOnBeanTests.ParameterizedConditionWithReturnRegistrationTypeConfig.class).run(( context) -> assertThat(context).satisfies(exampleBeanRequirement("customExampleBean", "conditionalCustomExampleBean")));
    }

    @Configuration
    @ConditionalOnBean(name = "foo")
    protected static class OnBeanNameConfiguration {
        @Bean
        public String bar() {
            return "bar";
        }
    }

    @Configuration
    @ConditionalOnBean(name = "foo", value = Date.class)
    protected static class OnBeanNameAndTypeConfiguration {
        @Bean
        public String bar() {
            return "bar";
        }
    }

    @Configuration
    @ConditionalOnBean(annotation = EnableScheduling.class)
    protected static class OnAnnotationConfiguration {
        @Bean
        public String bar() {
            return "bar";
        }
    }

    @Configuration
    @ConditionalOnBean(String.class)
    protected static class OnBeanClassConfiguration {
        @Bean
        public String bar() {
            return "bar";
        }
    }

    @Configuration
    @ConditionalOnBean(type = "java.lang.String")
    protected static class OnBeanClassNameConfiguration {
        @Bean
        public String bar() {
            return "bar";
        }
    }

    @Configuration
    @ConditionalOnBean(type = "some.type.Missing")
    protected static class OnBeanMissingClassConfiguration {
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
    @ImportResource("org/springframework/boot/autoconfigure/condition/foo.xml")
    protected static class XmlConfiguration {}

    @Configuration
    @ImportResource("org/springframework/boot/autoconfigure/condition/foo.xml")
    @Import(ConditionalOnBeanTests.OnBeanNameConfiguration.class)
    protected static class CombinedXmlConfiguration {}

    @Configuration
    @Import(ConditionalOnBeanTests.WithPropertyPlaceholderClassNameRegistrar.class)
    protected static class WithPropertyPlaceholderClassName {}

    @Configuration
    static class FactoryBeanConfiguration {
        @Bean
        public ConditionalOnBeanTests.ExampleFactoryBean exampleBeanFactoryBean() {
            return new ConditionalOnBeanTests.ExampleFactoryBean();
        }
    }

    @Configuration
    @ConditionalOnBean(annotation = ConditionalOnBeanTests.TestAnnotation.class)
    static class OnAnnotationWithFactoryBeanConfiguration {
        @Bean
        public String bar() {
            return "bar";
        }
    }

    protected static class WithPropertyPlaceholderClassNameRegistrar implements ImportBeanDefinitionRegistrar {
        @Override
        public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
            RootBeanDefinition bd = new RootBeanDefinition();
            bd.setBeanClassName("${mybeanclass}");
            registry.registerBeanDefinition("mybean", bd);
        }
    }

    public static class ExampleFactoryBean implements FactoryBean<ConditionalOnBeanTests.ExampleBean> {
        @Override
        public ConditionalOnBeanTests.ExampleBean getObject() {
            return new ConditionalOnBeanTests.ExampleBean("fromFactory");
        }

        @Override
        public Class<?> getObjectType() {
            return ConditionalOnBeanTests.ExampleBean.class;
        }

        @Override
        public boolean isSingleton() {
            return false;
        }
    }

    @Configuration
    public static class OriginalDefinition {
        @Bean
        public String testBean() {
            return "test";
        }
    }

    @Configuration
    @ConditionalOnBean(String.class)
    public static class OverridingDefinition {
        @Bean
        public Integer testBean() {
            return 1;
        }
    }

    @Configuration
    @ConditionalOnBean(String.class)
    public static class ConsumingConfiguration {
        ConsumingConfiguration(String testBean) {
        }
    }

    @Configuration
    static class ParameterizedWithCustomConfig {
        @Bean
        public ConditionalOnBeanTests.CustomExampleBean customExampleBean() {
            return new ConditionalOnBeanTests.CustomExampleBean();
        }
    }

    @Configuration
    static class ParameterizedWithoutCustomConfig {
        @Bean
        public ConditionalOnBeanTests.OtherExampleBean otherExampleBean() {
            return new ConditionalOnBeanTests.OtherExampleBean();
        }
    }

    @Configuration
    static class ParameterizedWithoutCustomContainerConfig {
        @Bean
        public TestParameterizedContainer<ConditionalOnBeanTests.OtherExampleBean> otherExampleBean() {
            return new TestParameterizedContainer<>();
        }
    }

    @Configuration
    static class ParameterizedWithCustomContainerConfig {
        @Bean
        public TestParameterizedContainer<ConditionalOnBeanTests.CustomExampleBean> customExampleBean() {
            return new TestParameterizedContainer<>();
        }
    }

    @Configuration
    static class ParameterizedConditionWithValueConfig {
        @Bean
        @ConditionalOnBean(value = ConditionalOnBeanTests.CustomExampleBean.class, parameterizedContainer = TestParameterizedContainer.class)
        public ConditionalOnBeanTests.CustomExampleBean conditionalCustomExampleBean() {
            return new ConditionalOnBeanTests.CustomExampleBean();
        }
    }

    @Configuration
    static class ParameterizedConditionWithReturnTypeConfig {
        @Bean
        @ConditionalOnBean(parameterizedContainer = TestParameterizedContainer.class)
        public ConditionalOnBeanTests.CustomExampleBean conditionalCustomExampleBean() {
            return new ConditionalOnBeanTests.CustomExampleBean();
        }
    }

    @Configuration
    static class ParameterizedConditionWithReturnRegistrationTypeConfig {
        @Bean
        @ConditionalOnBean(parameterizedContainer = TestParameterizedContainer.class)
        public TestParameterizedContainer<ConditionalOnBeanTests.CustomExampleBean> conditionalCustomExampleBean() {
            return new TestParameterizedContainer<>();
        }
    }

    @ConditionalOnBeanTests.TestAnnotation
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

    public static class CustomExampleBean extends ConditionalOnBeanTests.ExampleBean {
        public CustomExampleBean() {
            super("custom subclass");
        }
    }

    public static class OtherExampleBean extends ConditionalOnBeanTests.ExampleBean {
        public OtherExampleBean() {
            super("other subclass");
        }
    }

    @Target(ElementType.TYPE)
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    public @interface TestAnnotation {}
}

