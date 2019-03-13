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
package org.springframework.boot.test.context.assertj;


import Scope.NO_ANCESTORS;
import org.junit.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.support.StaticApplicationContext;


/**
 * Tests for {@link ApplicationContextAssert}.
 *
 * @author Phillip Webb
 * @author Andy Wilkinson
 */
public class ApplicationContextAssertTests {
    private StaticApplicationContext parent;

    private StaticApplicationContext context;

    private RuntimeException failure = new RuntimeException();

    @Test
    public void createWhenApplicationContextIsNullShouldThrowException() {
        assertThatIllegalArgumentException().isThrownBy(() -> new ApplicationContextAssert<>(null, null)).withMessageContaining("ApplicationContext must not be null");
    }

    @Test
    public void createWhenHasApplicationContextShouldSetActual() {
        assertThat(getAssert(this.context).getSourceApplicationContext()).isSameAs(this.context);
    }

    @Test
    public void createWhenHasExceptionShouldSetFailure() {
        assertThat(getAssert(this.failure)).getFailure().isSameAs(this.failure);
    }

    @Test
    public void hasBeanWhenHasBeanShouldPass() {
        this.context.registerSingleton("foo", ApplicationContextAssertTests.Foo.class);
        assertThat(getAssert(this.context)).hasBean("foo");
    }

    @Test
    public void hasBeanWhenHasNoBeanShouldFail() {
        assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> assertThat(getAssert(this.context)).hasBean("foo")).withMessageContaining("no such bean");
    }

    @Test
    public void hasBeanWhenNotStartedShouldFail() {
        assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> assertThat(getAssert(this.failure)).hasBean("foo")).withMessageContaining(String.format("but context failed to start:%n java.lang.RuntimeException"));
    }

    @Test
    public void hasSingleBeanWhenHasSingleBeanShouldPass() {
        this.context.registerSingleton("foo", ApplicationContextAssertTests.Foo.class);
        assertThat(getAssert(this.context)).hasSingleBean(ApplicationContextAssertTests.Foo.class);
    }

    @Test
    public void hasSingleBeanWhenHasNoBeansShouldFail() {
        assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> assertThat(getAssert(this.context)).hasSingleBean(.class)).withMessageContaining("to have a single bean of type");
    }

    @Test
    public void hasSingleBeanWhenHasMultipleShouldFail() {
        this.context.registerSingleton("foo", ApplicationContextAssertTests.Foo.class);
        this.context.registerSingleton("bar", ApplicationContextAssertTests.Foo.class);
        assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> assertThat(getAssert(this.context)).hasSingleBean(.class)).withMessageContaining("but found:");
    }

    @Test
    public void hasSingleBeanWhenFailedToStartShouldFail() {
        assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> assertThat(getAssert(this.failure)).hasSingleBean(.class)).withMessageContaining("to have a single bean of type").withMessageContaining(String.format("but context failed to start:%n java.lang.RuntimeException"));
    }

    @Test
    public void hasSingleBeanWhenInParentShouldFail() {
        this.parent.registerSingleton("foo", ApplicationContextAssertTests.Foo.class);
        this.context.registerSingleton("bar", ApplicationContextAssertTests.Foo.class);
        assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> assertThat(getAssert(this.context)).hasSingleBean(.class)).withMessageContaining("but found:");
    }

    @Test
    public void hasSingleBeanWithLimitedScopeWhenInParentShouldPass() {
        this.parent.registerSingleton("foo", ApplicationContextAssertTests.Foo.class);
        this.context.registerSingleton("bar", ApplicationContextAssertTests.Foo.class);
        assertThat(getAssert(this.context)).hasSingleBean(ApplicationContextAssertTests.Foo.class, NO_ANCESTORS);
    }

    @Test
    public void doesNotHaveBeanOfTypeWhenHasNoBeanOfTypeShouldPass() {
        assertThat(getAssert(this.context)).doesNotHaveBean(ApplicationContextAssertTests.Foo.class);
    }

    @Test
    public void doesNotHaveBeanOfTypeWhenHasBeanOfTypeShouldFail() {
        this.context.registerSingleton("foo", ApplicationContextAssertTests.Foo.class);
        assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> assertThat(getAssert(this.context)).doesNotHaveBean(.class)).withMessageContaining("but found");
    }

    @Test
    public void doesNotHaveBeanOfTypeWhenFailedToStartShouldFail() {
        assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> assertThat(getAssert(this.failure)).doesNotHaveBean(.class)).withMessageContaining("not to have any beans of type").withMessageContaining(String.format("but context failed to start:%n java.lang.RuntimeException"));
    }

    @Test
    public void doesNotHaveBeanOfTypeWhenInParentShouldFail() {
        this.parent.registerSingleton("foo", ApplicationContextAssertTests.Foo.class);
        assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> assertThat(getAssert(this.context)).doesNotHaveBean(.class)).withMessageContaining("but found");
    }

    @Test
    public void doesNotHaveBeanOfTypeWithLimitedScopeWhenInParentShouldPass() {
        this.parent.registerSingleton("foo", ApplicationContextAssertTests.Foo.class);
        assertThat(getAssert(this.context)).doesNotHaveBean(ApplicationContextAssertTests.Foo.class, NO_ANCESTORS);
    }

    @Test
    public void doesNotHaveBeanOfNameWhenHasNoBeanOfTypeShouldPass() {
        assertThat(getAssert(this.context)).doesNotHaveBean("foo");
    }

    @Test
    public void doesNotHaveBeanOfNameWhenHasBeanOfTypeShouldFail() {
        this.context.registerSingleton("foo", ApplicationContextAssertTests.Foo.class);
        assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> assertThat(getAssert(this.context)).doesNotHaveBean("foo")).withMessageContaining("but found");
    }

    @Test
    public void doesNotHaveBeanOfNameWhenFailedToStartShouldFail() {
        assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> assertThat(getAssert(this.failure)).doesNotHaveBean("foo")).withMessageContaining("not to have any beans of name").withMessageContaining("failed to start");
    }

    @Test
    public void getBeanNamesWhenHasNamesShouldReturnNamesAssert() {
        this.context.registerSingleton("foo", ApplicationContextAssertTests.Foo.class);
        this.context.registerSingleton("bar", ApplicationContextAssertTests.Foo.class);
        assertThat(getAssert(this.context)).getBeanNames(ApplicationContextAssertTests.Foo.class).containsOnly("foo", "bar");
    }

    @Test
    public void getBeanNamesWhenHasNoNamesShouldReturnEmptyAssert() {
        assertThat(getAssert(this.context)).getBeanNames(ApplicationContextAssertTests.Foo.class).isEmpty();
    }

    @Test
    public void getBeanNamesWhenFailedToStartShouldFail() {
        assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> assertThat(getAssert(this.failure)).doesNotHaveBean("foo")).withMessageContaining("not to have any beans of name").withMessageContaining(String.format("but context failed to start:%n java.lang.RuntimeException"));
    }

    @Test
    public void getBeanOfTypeWhenHasBeanShouldReturnBeanAssert() {
        this.context.registerSingleton("foo", ApplicationContextAssertTests.Foo.class);
        assertThat(getAssert(this.context)).getBean(ApplicationContextAssertTests.Foo.class).isNotNull();
    }

    @Test
    public void getBeanOfTypeWhenHasNoBeanShouldReturnNullAssert() {
        assertThat(getAssert(this.context)).getBean(ApplicationContextAssertTests.Foo.class).isNull();
    }

    @Test
    public void getBeanOfTypeWhenHasMultipleBeansShouldFail() {
        this.context.registerSingleton("foo", ApplicationContextAssertTests.Foo.class);
        this.context.registerSingleton("bar", ApplicationContextAssertTests.Foo.class);
        assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> assertThat(getAssert(this.context)).getBean(.class)).withMessageContaining("but found");
    }

    @Test
    public void getBeanOfTypeWhenHasPrimaryBeanShouldReturnPrimary() {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(ApplicationContextAssertTests.PrimaryFooConfig.class);
        assertThat(getAssert(context)).getBean(ApplicationContextAssertTests.Foo.class).isInstanceOf(ApplicationContextAssertTests.Bar.class);
        context.close();
    }

    @Test
    public void getBeanOfTypeWhenFailedToStartShouldFail() {
        assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> assertThat(getAssert(this.failure)).getBean(.class)).withMessageContaining("to contain bean of type").withMessageContaining(String.format("but context failed to start:%n java.lang.RuntimeException"));
    }

    @Test
    public void getBeanOfTypeWhenInParentShouldReturnBeanAssert() {
        this.parent.registerSingleton("foo", ApplicationContextAssertTests.Foo.class);
        assertThat(getAssert(this.context)).getBean(ApplicationContextAssertTests.Foo.class).isNotNull();
    }

    @Test
    public void getBeanOfTypeWhenInParentWithLimitedScopeShouldReturnNullAssert() {
        this.parent.registerSingleton("foo", ApplicationContextAssertTests.Foo.class);
        assertThat(getAssert(this.context)).getBean(ApplicationContextAssertTests.Foo.class, NO_ANCESTORS).isNull();
    }

    @Test
    public void getBeanOfTypeWhenHasMultipleBeansIncludingParentShouldFail() {
        this.parent.registerSingleton("foo", ApplicationContextAssertTests.Foo.class);
        this.context.registerSingleton("bar", ApplicationContextAssertTests.Foo.class);
        assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> assertThat(getAssert(this.context)).getBean(.class)).withMessageContaining("but found");
    }

    @Test
    public void getBeanOfTypeWithLimitedScopeWhenHasMultipleBeansIncludingParentShouldReturnBeanAssert() {
        this.parent.registerSingleton("foo", ApplicationContextAssertTests.Foo.class);
        this.context.registerSingleton("bar", ApplicationContextAssertTests.Foo.class);
        assertThat(getAssert(this.context)).getBean(ApplicationContextAssertTests.Foo.class, NO_ANCESTORS).isNotNull();
    }

    @Test
    public void getBeanOfNameWhenHasBeanShouldReturnBeanAssert() {
        this.context.registerSingleton("foo", ApplicationContextAssertTests.Foo.class);
        assertThat(getAssert(this.context)).getBean("foo").isNotNull();
    }

    @Test
    public void getBeanOfNameWhenHasNoBeanOfNameShouldReturnNullAssert() {
        assertThat(getAssert(this.context)).getBean("foo").isNull();
    }

    @Test
    public void getBeanOfNameWhenFailedToStartShouldFail() {
        assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> assertThat(getAssert(this.failure)).getBean("foo")).withMessageContaining("to contain a bean of name").withMessageContaining(String.format("but context failed to start:%n java.lang.RuntimeException"));
    }

    @Test
    public void getBeanOfNameAndTypeWhenHasBeanShouldReturnBeanAssert() {
        this.context.registerSingleton("foo", ApplicationContextAssertTests.Foo.class);
        assertThat(getAssert(this.context)).getBean("foo", ApplicationContextAssertTests.Foo.class).isNotNull();
    }

    @Test
    public void getBeanOfNameAndTypeWhenHasNoBeanOfNameShouldReturnNullAssert() {
        assertThat(getAssert(this.context)).getBean("foo", ApplicationContextAssertTests.Foo.class).isNull();
    }

    @Test
    public void getBeanOfNameAndTypeWhenHasNoBeanOfNameButDifferentTypeShouldFail() {
        this.context.registerSingleton("foo", ApplicationContextAssertTests.Foo.class);
        assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> assertThat(getAssert(this.context)).getBean("foo", .class)).withMessageContaining("of type");
    }

    @Test
    public void getBeanOfNameAndTypeWhenFailedToStartShouldFail() {
        assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> assertThat(getAssert(this.failure)).getBean("foo", .class)).withMessageContaining("to contain a bean of name").withMessageContaining(String.format("but context failed to start:%n java.lang.RuntimeException"));
    }

    @Test
    public void getBeansWhenHasBeansShouldReturnMapAssert() {
        this.context.registerSingleton("foo", ApplicationContextAssertTests.Foo.class);
        this.context.registerSingleton("bar", ApplicationContextAssertTests.Foo.class);
        assertThat(getAssert(this.context)).getBeans(ApplicationContextAssertTests.Foo.class).hasSize(2).containsKeys("foo", "bar");
    }

    @Test
    public void getBeansWhenHasNoBeansShouldReturnEmptyMapAssert() {
        assertThat(getAssert(this.context)).getBeans(ApplicationContextAssertTests.Foo.class).isEmpty();
    }

    @Test
    public void getBeansWhenFailedToStartShouldFail() {
        assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> assertThat(getAssert(this.failure)).getBeans(.class)).withMessageContaining("to get beans of type").withMessageContaining(String.format("but context failed to start:%n java.lang.RuntimeException"));
    }

    @Test
    public void getBeansShouldIncludeBeansFromParentScope() {
        this.parent.registerSingleton("foo", ApplicationContextAssertTests.Foo.class);
        this.context.registerSingleton("bar", ApplicationContextAssertTests.Foo.class);
        assertThat(getAssert(this.context)).getBeans(ApplicationContextAssertTests.Foo.class).hasSize(2).containsKeys("foo", "bar");
    }

    @Test
    public void getBeansWithLimitedScopeShouldNotIncludeBeansFromParentScope() {
        this.parent.registerSingleton("foo", ApplicationContextAssertTests.Foo.class);
        this.context.registerSingleton("bar", ApplicationContextAssertTests.Foo.class);
        assertThat(getAssert(this.context)).getBeans(ApplicationContextAssertTests.Foo.class, NO_ANCESTORS).hasSize(1).containsKeys("bar");
    }

    @Test
    public void getFailureWhenFailedShouldReturnFailure() {
        assertThat(getAssert(this.failure)).getFailure().isSameAs(this.failure);
    }

    @Test
    public void getFailureWhenDidNotFailShouldFail() {
        assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> assertThat(getAssert(this.context)).getFailure()).withMessageContaining("context started");
    }

    @Test
    public void hasFailedWhenFailedShouldPass() {
        assertThat(getAssert(this.failure)).hasFailed();
    }

    @Test
    public void hasFailedWhenNotFailedShouldFail() {
        assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> assertThat(getAssert(this.context)).hasFailed()).withMessageContaining("to have failed");
    }

    @Test
    public void hasNotFailedWhenFailedShouldFail() {
        assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> assertThat(getAssert(this.failure)).hasNotFailed()).withMessageContaining("to have not failed").withMessageContaining(String.format("but context failed to start:%n java.lang.RuntimeException"));
    }

    @Test
    public void hasNotFailedWhenNotFailedShouldPass() {
        assertThat(getAssert(this.context)).hasNotFailed();
    }

    private static class Foo {}

    private static class Bar extends ApplicationContextAssertTests.Foo {}

    @Configuration
    static class PrimaryFooConfig {
        @Bean
        public ApplicationContextAssertTests.Foo foo() {
            return new ApplicationContextAssertTests.Foo();
        }

        @Bean
        @Primary
        public ApplicationContextAssertTests.Bar bar() {
            return new ApplicationContextAssertTests.Bar();
        }
    }
}

