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
package org.springframework.boot.autoconfigure.validation;


import java.util.HashSet;
import java.util.Set;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.validation.beanvalidation.CustomValidatorBean;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;
import org.springframework.validation.beanvalidation.OptionalValidatorFactoryBean;


/**
 * Tests for {@link ValidationAutoConfiguration}.
 *
 * @author Stephane Nicoll
 * @author Phillip Webb
 */
public class ValidationAutoConfigurationTests {
    private AnnotationConfigApplicationContext context;

    @Test
    public void validationAutoConfigurationShouldConfigureDefaultValidator() {
        load(ValidationAutoConfigurationTests.Config.class);
        String[] jsrValidatorNames = this.context.getBeanNamesForType(Validator.class);
        String[] springValidatorNames = this.context.getBeanNamesForType(Validator.class);
        assertThat(jsrValidatorNames).containsExactly("defaultValidator");
        assertThat(springValidatorNames).containsExactly("defaultValidator");
        Validator jsrValidator = this.context.getBean(Validator.class);
        org.springframework.validation.Validator springValidator = this.context.getBean(Validator.class);
        assertThat(jsrValidator).isInstanceOf(LocalValidatorFactoryBean.class);
        assertThat(jsrValidator).isEqualTo(springValidator);
        assertThat(isPrimaryBean("defaultValidator")).isTrue();
    }

    @Test
    public void validationAutoConfigurationWhenUserProvidesValidatorShouldBackOff() {
        load(ValidationAutoConfigurationTests.UserDefinedValidatorConfig.class);
        String[] jsrValidatorNames = this.context.getBeanNamesForType(Validator.class);
        String[] springValidatorNames = this.context.getBeanNamesForType(Validator.class);
        assertThat(jsrValidatorNames).containsExactly("customValidator");
        assertThat(springValidatorNames).containsExactly("customValidator");
        org.springframework.validation.Validator springValidator = this.context.getBean(Validator.class);
        Validator jsrValidator = this.context.getBean(Validator.class);
        assertThat(jsrValidator).isInstanceOf(OptionalValidatorFactoryBean.class);
        assertThat(jsrValidator).isEqualTo(springValidator);
        assertThat(isPrimaryBean("customValidator")).isFalse();
    }

    @Test
    public void validationAutoConfigurationWhenUserProvidesDefaultValidatorShouldNotEnablePrimary() {
        load(ValidationAutoConfigurationTests.UserDefinedDefaultValidatorConfig.class);
        String[] jsrValidatorNames = this.context.getBeanNamesForType(Validator.class);
        String[] springValidatorNames = this.context.getBeanNamesForType(Validator.class);
        assertThat(jsrValidatorNames).containsExactly("defaultValidator");
        assertThat(springValidatorNames).containsExactly("defaultValidator");
        assertThat(isPrimaryBean("defaultValidator")).isFalse();
    }

    @Test
    public void validationAutoConfigurationWhenUserProvidesJsrValidatorShouldBackOff() {
        load(ValidationAutoConfigurationTests.UserDefinedJsrValidatorConfig.class);
        String[] jsrValidatorNames = this.context.getBeanNamesForType(Validator.class);
        String[] springValidatorNames = this.context.getBeanNamesForType(Validator.class);
        assertThat(jsrValidatorNames).containsExactly("customValidator");
        assertThat(springValidatorNames).isEmpty();
        assertThat(isPrimaryBean("customValidator")).isFalse();
    }

    @Test
    public void validationAutoConfigurationWhenUserProvidesSpringValidatorShouldCreateJsrValidator() {
        load(ValidationAutoConfigurationTests.UserDefinedSpringValidatorConfig.class);
        String[] jsrValidatorNames = this.context.getBeanNamesForType(Validator.class);
        String[] springValidatorNames = this.context.getBeanNamesForType(Validator.class);
        assertThat(jsrValidatorNames).containsExactly("defaultValidator");
        assertThat(springValidatorNames).containsExactly("customValidator", "anotherCustomValidator", "defaultValidator");
        Validator jsrValidator = this.context.getBean(Validator.class);
        org.springframework.validation.Validator springValidator = this.context.getBean(Validator.class);
        assertThat(jsrValidator).isInstanceOf(LocalValidatorFactoryBean.class);
        assertThat(jsrValidator).isEqualTo(springValidator);
        assertThat(isPrimaryBean("defaultValidator")).isTrue();
    }

    @Test
    public void validationAutoConfigurationWhenUserProvidesPrimarySpringValidatorShouldRemovePrimaryFlag() {
        load(ValidationAutoConfigurationTests.UserDefinedPrimarySpringValidatorConfig.class);
        String[] jsrValidatorNames = this.context.getBeanNamesForType(Validator.class);
        String[] springValidatorNames = this.context.getBeanNamesForType(Validator.class);
        assertThat(jsrValidatorNames).containsExactly("defaultValidator");
        assertThat(springValidatorNames).containsExactly("customValidator", "anotherCustomValidator", "defaultValidator");
        Validator jsrValidator = this.context.getBean(Validator.class);
        org.springframework.validation.Validator springValidator = this.context.getBean(Validator.class);
        assertThat(jsrValidator).isInstanceOf(LocalValidatorFactoryBean.class);
        assertThat(springValidator).isEqualTo(this.context.getBean("anotherCustomValidator"));
        assertThat(isPrimaryBean("defaultValidator")).isFalse();
    }

    @Test
    public void validationIsEnabled() {
        load(ValidationAutoConfigurationTests.SampleService.class);
        assertThat(this.context.getBeansOfType(Validator.class)).hasSize(1);
        ValidationAutoConfigurationTests.SampleService service = this.context.getBean(ValidationAutoConfigurationTests.SampleService.class);
        service.doSomething("Valid");
        assertThatExceptionOfType(ConstraintViolationException.class).isThrownBy(() -> service.doSomething("KO"));
    }

    @Test
    public void validationUsesCglibProxy() {
        load(ValidationAutoConfigurationTests.DefaultAnotherSampleService.class);
        assertThat(this.context.getBeansOfType(Validator.class)).hasSize(1);
        ValidationAutoConfigurationTests.DefaultAnotherSampleService service = this.context.getBean(ValidationAutoConfigurationTests.DefaultAnotherSampleService.class);
        service.doSomething(42);
        assertThatExceptionOfType(ConstraintViolationException.class).isThrownBy(() -> service.doSomething(2));
    }

    @Test
    public void validationCanBeConfiguredToUseJdkProxy() {
        load(ValidationAutoConfigurationTests.AnotherSampleServiceConfiguration.class, "spring.aop.proxy-target-class=false");
        assertThat(this.context.getBeansOfType(Validator.class)).hasSize(1);
        assertThat(this.context.getBeansOfType(ValidationAutoConfigurationTests.DefaultAnotherSampleService.class)).isEmpty();
        ValidationAutoConfigurationTests.AnotherSampleService service = this.context.getBean(ValidationAutoConfigurationTests.AnotherSampleService.class);
        service.doSomething(42);
        assertThatExceptionOfType(ConstraintViolationException.class).isThrownBy(() -> service.doSomething(2));
    }

    @Test
    public void userDefinedMethodValidationPostProcessorTakesPrecedence() {
        load(ValidationAutoConfigurationTests.SampleConfiguration.class);
        assertThat(this.context.getBeansOfType(Validator.class)).hasSize(1);
        Object userMethodValidationPostProcessor = this.context.getBean("testMethodValidationPostProcessor");
        assertThat(this.context.getBean(MethodValidationPostProcessor.class)).isSameAs(userMethodValidationPostProcessor);
        assertThat(this.context.getBeansOfType(MethodValidationPostProcessor.class)).hasSize(1);
        assertThat(this.context.getBean(Validator.class)).isNotSameAs(ReflectionTestUtils.getField(userMethodValidationPostProcessor, "validator"));
    }

    @Test
    public void methodValidationPostProcessorValidatorDependencyDoesNotTriggerEarlyInitialization() {
        load(ValidationAutoConfigurationTests.CustomValidatorConfiguration.class);
        assertThat(this.context.getBean(ValidationAutoConfigurationTests.CustomValidatorConfiguration.TestBeanPostProcessor.class).postProcessed).contains("someService");
    }

    @Configuration
    static class Config {}

    @Configuration
    static class UserDefinedValidatorConfig {
        @Bean
        public OptionalValidatorFactoryBean customValidator() {
            return new OptionalValidatorFactoryBean();
        }
    }

    @Configuration
    static class UserDefinedDefaultValidatorConfig {
        @Bean
        public OptionalValidatorFactoryBean defaultValidator() {
            return new OptionalValidatorFactoryBean();
        }
    }

    @Configuration
    static class UserDefinedJsrValidatorConfig {
        @Bean
        public Validator customValidator() {
            return Mockito.mock(Validator.class);
        }
    }

    @Configuration
    static class UserDefinedSpringValidatorConfig {
        @Bean
        public Validator customValidator() {
            return Mockito.mock(Validator.class);
        }

        @Bean
        public Validator anotherCustomValidator() {
            return Mockito.mock(Validator.class);
        }
    }

    @Configuration
    static class UserDefinedPrimarySpringValidatorConfig {
        @Bean
        public Validator customValidator() {
            return Mockito.mock(Validator.class);
        }

        @Bean
        @Primary
        public Validator anotherCustomValidator() {
            return Mockito.mock(Validator.class);
        }
    }

    @Validated
    static class SampleService {
        public void doSomething(@Size(min = 3, max = 10)
        String name) {
        }
    }

    interface AnotherSampleService {
        void doSomething(@Min(42)
        Integer counter);
    }

    @Validated
    static class DefaultAnotherSampleService implements ValidationAutoConfigurationTests.AnotherSampleService {
        @Override
        public void doSomething(Integer counter) {
        }
    }

    @Configuration
    static class AnotherSampleServiceConfiguration {
        @Bean
        public ValidationAutoConfigurationTests.AnotherSampleService anotherSampleService() {
            return new ValidationAutoConfigurationTests.DefaultAnotherSampleService();
        }
    }

    @Configuration
    static class SampleConfiguration {
        @Bean
        public MethodValidationPostProcessor testMethodValidationPostProcessor() {
            return new MethodValidationPostProcessor();
        }
    }

    @Configuration
    static class CustomValidatorConfiguration {
        CustomValidatorConfiguration(ValidationAutoConfigurationTests.CustomValidatorConfiguration.SomeService someService) {
        }

        @Bean
        Validator customValidator() {
            return new CustomValidatorBean();
        }

        @Bean
        static ValidationAutoConfigurationTests.CustomValidatorConfiguration.TestBeanPostProcessor testBeanPostProcessor() {
            return new ValidationAutoConfigurationTests.CustomValidatorConfiguration.TestBeanPostProcessor();
        }

        @Configuration
        static class SomeServiceConfiguration {
            @Bean
            public ValidationAutoConfigurationTests.CustomValidatorConfiguration.SomeService someService() {
                return new ValidationAutoConfigurationTests.CustomValidatorConfiguration.SomeService();
            }
        }

        static class SomeService {}

        static class TestBeanPostProcessor implements BeanPostProcessor {
            private Set<String> postProcessed = new HashSet<>();

            @Override
            public Object postProcessAfterInitialization(Object bean, String name) {
                this.postProcessed.add(name);
                return bean;
            }

            @Override
            public Object postProcessBeforeInitialization(Object bean, String name) {
                return bean;
            }
        }
    }
}

