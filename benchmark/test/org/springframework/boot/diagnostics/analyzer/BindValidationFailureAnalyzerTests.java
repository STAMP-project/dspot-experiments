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
package org.springframework.boot.diagnostics.analyzer;


import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import org.junit.Test;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.diagnostics.FailureAnalysis;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.validation.Validator;
import org.springframework.validation.annotation.Validated;


/**
 * Tests for {@link BindValidationFailureAnalyzer}.
 *
 * @author Madhura Bhave
 */
public class BindValidationFailureAnalyzerTests {
    @Test
    public void bindExceptionWithFieldErrorsDueToValidationFailure() {
        FailureAnalysis analysis = performAnalysis(BindValidationFailureAnalyzerTests.FieldValidationFailureConfiguration.class);
        assertThat(analysis.getDescription()).contains(BindValidationFailureAnalyzerTests.failure("test.foo.foo", "null", "must not be null"));
        assertThat(analysis.getDescription()).contains(BindValidationFailureAnalyzerTests.failure("test.foo.value", "0", "at least five"));
        assertThat(analysis.getDescription()).contains(BindValidationFailureAnalyzerTests.failure("test.foo.nested.bar", "null", "must not be null"));
    }

    @Test
    public void bindExceptionWithOriginDueToValidationFailure() {
        FailureAnalysis analysis = performAnalysis(BindValidationFailureAnalyzerTests.FieldValidationFailureConfiguration.class, "test.foo.value=4");
        assertThat(analysis.getDescription()).contains("Origin: \"test.foo.value\" from property source \"test\"");
    }

    @Test
    public void bindExceptionWithObjectErrorsDueToValidationFailure() {
        FailureAnalysis analysis = performAnalysis(BindValidationFailureAnalyzerTests.ObjectValidationFailureConfiguration.class);
        assertThat(analysis.getDescription()).contains("Reason: This object could not be bound.");
    }

    @Test
    public void otherBindExceptionShouldReturnAnalysis() {
        BindException cause = new BindException(new BindValidationFailureAnalyzerTests.FieldValidationFailureProperties(), "fieldValidationFailureProperties");
        cause.addError(new FieldError("test", "value", "must not be null"));
        BeanCreationException rootFailure = new BeanCreationException("bean creation failure", cause);
        FailureAnalysis analysis = new BindValidationFailureAnalyzer().analyze(rootFailure, rootFailure);
        assertThat(analysis.getDescription()).contains(BindValidationFailureAnalyzerTests.failure("test.value", "null", "must not be null"));
    }

    @EnableConfigurationProperties(BindValidationFailureAnalyzerTests.FieldValidationFailureProperties.class)
    static class FieldValidationFailureConfiguration {}

    @EnableConfigurationProperties(BindValidationFailureAnalyzerTests.ObjectErrorFailureProperties.class)
    static class ObjectValidationFailureConfiguration {}

    @ConfigurationProperties("test.foo")
    @Validated
    static class FieldValidationFailureProperties {
        @NotNull
        private String foo;

        @Min(value = 5, message = "at least five")
        private int value;

        @Valid
        private BindValidationFailureAnalyzerTests.FieldValidationFailureProperties.Nested nested = new BindValidationFailureAnalyzerTests.FieldValidationFailureProperties.Nested();

        public String getFoo() {
            return this.foo;
        }

        public void setFoo(String foo) {
            this.foo = foo;
        }

        public int getValue() {
            return this.value;
        }

        public void setValue(int value) {
            this.value = value;
        }

        public BindValidationFailureAnalyzerTests.FieldValidationFailureProperties.Nested getNested() {
            return this.nested;
        }

        public void setNested(BindValidationFailureAnalyzerTests.FieldValidationFailureProperties.Nested nested) {
            this.nested = nested;
        }

        static class Nested {
            @NotNull
            private String bar;

            public String getBar() {
                return this.bar;
            }

            public void setBar(String bar) {
                this.bar = bar;
            }
        }
    }

    @ConfigurationProperties("foo.bar")
    @Validated
    static class ObjectErrorFailureProperties implements Validator {
        @Override
        public void validate(Object target, Errors errors) {
            errors.reject("my.objectError", "This object could not be bound.");
        }

        @Override
        public boolean supports(Class<?> clazz) {
            return true;
        }
    }
}

