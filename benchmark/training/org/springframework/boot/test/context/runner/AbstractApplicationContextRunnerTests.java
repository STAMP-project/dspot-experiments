/**
 * Copyright 2012-2019 the original author or authors.
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
package org.springframework.boot.test.context.runner;


import com.google.gson.Gson;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import org.junit.Test;
import org.springframework.boot.context.annotation.UserConfigurations;
import org.springframework.boot.test.context.FilteredClassLoader;
import org.springframework.boot.test.context.assertj.ApplicationContextAssertProvider;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.util.ClassUtils;


/**
 * Abstract tests for {@link AbstractApplicationContextRunner} implementations.
 *
 * @param <T>
 * 		The runner type
 * @param <C>
 * 		the context type
 * @param <A>
 * 		the assertable context type
 * @author Stephane Nicoll
 * @author Phillip Webb
 */
public abstract class AbstractApplicationContextRunnerTests<T extends AbstractApplicationContextRunner<T, C, A>, C extends ConfigurableApplicationContext, A extends ApplicationContextAssertProvider<C>> {
    @Test
    public void runWithInitializerShouldInitialize() {
        AtomicBoolean called = new AtomicBoolean();
        withInitializer(( context) -> called.set(true)).run(( context) -> {
        });
        AbstractApplicationContextRunnerTests.assertThat(called).isTrue();
    }

    @Test
    public void runWithSystemPropertiesShouldSetAndRemoveProperties() {
        String key = "test." + (UUID.randomUUID());
        AbstractApplicationContextRunnerTests.assertThat(System.getProperties().containsKey(key)).isFalse();
        withSystemProperties((key + "=value")).run(( context) -> assertThat(System.getProperties()).containsEntry(key, "value"));
        AbstractApplicationContextRunnerTests.assertThat(System.getProperties().containsKey(key)).isFalse();
    }

    @Test
    public void runWithSystemPropertiesWhenContextFailsShouldRemoveProperties() {
        String key = "test." + (UUID.randomUUID());
        AbstractApplicationContextRunnerTests.assertThat(System.getProperties().containsKey(key)).isFalse();
        withUserConfiguration(AbstractApplicationContextRunnerTests.FailingConfig.class).run(( context) -> assertThat(context).hasFailed());
        AbstractApplicationContextRunnerTests.assertThat(System.getProperties().containsKey(key)).isFalse();
    }

    @Test
    public void runWithSystemPropertiesShouldRestoreOriginalProperties() {
        String key = "test." + (UUID.randomUUID());
        System.setProperty(key, "value");
        try {
            AbstractApplicationContextRunnerTests.assertThat(System.getProperties().getProperty(key)).isEqualTo("value");
            withSystemProperties((key + "=newValue")).run(( context) -> assertThat(System.getProperties()).containsEntry(key, "newValue"));
            AbstractApplicationContextRunnerTests.assertThat(System.getProperties().getProperty(key)).isEqualTo("value");
        } finally {
            System.clearProperty(key);
        }
    }

    @Test
    public void runWithSystemPropertiesWhenValueIsNullShouldRemoveProperty() {
        String key = "test." + (UUID.randomUUID());
        System.setProperty(key, "value");
        try {
            AbstractApplicationContextRunnerTests.assertThat(System.getProperties().getProperty(key)).isEqualTo("value");
            withSystemProperties((key + "=")).run(( context) -> assertThat(System.getProperties()).doesNotContainKey(key));
            AbstractApplicationContextRunnerTests.assertThat(System.getProperties().getProperty(key)).isEqualTo("value");
        } finally {
            System.clearProperty(key);
        }
    }

    @Test
    public void runWithMultiplePropertyValuesShouldAllAllValues() {
        withPropertyValues("test.bar=2").run(( context) -> {
            Environment environment = context.getEnvironment();
            assertThat(environment.getProperty("test.foo")).isEqualTo("1");
            assertThat(environment.getProperty("test.bar")).isEqualTo("2");
        });
    }

    @Test
    public void runWithPropertyValuesWhenHasExistingShouldReplaceValue() {
        withPropertyValues("test.foo=2").run(( context) -> {
            Environment environment = context.getEnvironment();
            assertThat(environment.getProperty("test.foo")).isEqualTo("2");
        });
    }

    @Test
    public void runWithConfigurationsShouldRegisterConfigurations() {
        withUserConfiguration(AbstractApplicationContextRunnerTests.FooConfig.class).run(( context) -> assertThat(context).hasBean("foo"));
    }

    @Test
    public void runWithMultipleConfigurationsShouldRegisterAllConfigurations() {
        withUserConfiguration(AbstractApplicationContextRunnerTests.FooConfig.class).withConfiguration(UserConfigurations.of(AbstractApplicationContextRunnerTests.BarConfig.class)).run(( context) -> assertThat(context).hasBean("foo").hasBean("bar"));
    }

    @Test
    public void runWithFailedContextShouldReturnFailedAssertableContext() {
        withUserConfiguration(AbstractApplicationContextRunnerTests.FailingConfig.class).run(( context) -> assertThat(context).hasFailed());
    }

    @Test
    public void runWithClassLoaderShouldSetClassLoaderOnContext() {
        get().withClassLoader(new FilteredClassLoader(Gson.class.getPackage().getName())).run(( context) -> assertThatExceptionOfType(.class).isThrownBy(() -> ClassUtils.forName(.class.getName(), context.getClassLoader())));
    }

    @Test
    public void runWithClassLoaderShouldSetClassLoaderOnConditionContext() {
        withUserConfiguration(AbstractApplicationContextRunnerTests.ConditionalConfig.class).run(( context) -> assertThat(context).hasSingleBean(.class));
    }

    @Test
    public void thrownRuleWorksWithCheckedException() {
        run(( context) -> assertThatIOException().isThrownBy(() -> throwCheckedException("Expected message")).withMessageContaining("Expected message"));
    }

    @Configuration
    static class FailingConfig {
        @Bean
        public String foo() {
            throw new IllegalStateException("Failed");
        }
    }

    @Configuration
    static class FooConfig {
        @Bean
        public String foo() {
            return "foo";
        }
    }

    @Configuration
    static class BarConfig {
        @Bean
        public String bar() {
            return "bar";
        }
    }

    @Configuration
    @Conditional(AbstractApplicationContextRunnerTests.FilteredClassLoaderCondition.class)
    static class ConditionalConfig {}

    static class FilteredClassLoaderCondition implements Condition {
        @Override
        public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
            return (context.getClassLoader()) instanceof FilteredClassLoader;
        }
    }
}

