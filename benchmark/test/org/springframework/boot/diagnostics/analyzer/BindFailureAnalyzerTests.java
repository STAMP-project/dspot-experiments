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


import java.util.List;
import java.util.Set;
import javax.validation.constraints.Min;
import org.junit.Test;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.diagnostics.FailureAnalysis;
import org.springframework.validation.annotation.Validated;


/**
 * Tests for {@link BindFailureAnalyzer}.
 *
 * @author Andy Wilkinson
 * @author Madhura Bhave
 */
public class BindFailureAnalyzerTests {
    @Test
    public void analysisForUnboundElementsIsNull() {
        FailureAnalysis analysis = performAnalysis(BindFailureAnalyzerTests.UnboundElementsFailureConfiguration.class, "test.foo.listValue[0]=hello", "test.foo.listValue[2]=world");
        assertThat(analysis).isNull();
    }

    @Test
    public void analysisForValidationExceptionIsNull() {
        FailureAnalysis analysis = performAnalysis(BindFailureAnalyzerTests.FieldValidationFailureConfiguration.class, "test.foo.value=1");
        assertThat(analysis).isNull();
    }

    @Test
    public void bindExceptionDueToOtherFailure() {
        FailureAnalysis analysis = performAnalysis(BindFailureAnalyzerTests.GenericFailureConfiguration.class, "test.foo.value=alpha");
        assertThat(analysis.getDescription()).contains(BindFailureAnalyzerTests.failure("test.foo.value", "alpha", "\"test.foo.value\" from property source \"test\"", "failed to convert java.lang.String to int"));
    }

    @Test
    public void bindExceptionForUnknownValueInEnumListsValidValuesInAction() {
        FailureAnalysis analysis = performAnalysis(BindFailureAnalyzerTests.EnumFailureConfiguration.class, "test.foo.fruit=apple,strawberry");
        for (BindFailureAnalyzerTests.Fruit fruit : BindFailureAnalyzerTests.Fruit.values()) {
            assertThat(analysis.getAction()).contains(fruit.name());
        }
    }

    @Test
    public void bindExceptionWithNestedFailureShouldDisplayNestedMessage() {
        FailureAnalysis analysis = performAnalysis(BindFailureAnalyzerTests.NestedFailureConfiguration.class, "test.foo.value=hello");
        assertThat(analysis.getDescription()).contains(BindFailureAnalyzerTests.failure("test.foo.value", "hello", "\"test.foo.value\" from property source \"test\"", "This is a failure"));
    }

    @EnableConfigurationProperties(BindValidationFailureAnalyzerTests.FieldValidationFailureProperties.class)
    static class FieldValidationFailureConfiguration {}

    @EnableConfigurationProperties(BindFailureAnalyzerTests.UnboundElementsFailureProperties.class)
    static class UnboundElementsFailureConfiguration {}

    @EnableConfigurationProperties(BindFailureAnalyzerTests.GenericFailureProperties.class)
    static class GenericFailureConfiguration {}

    @EnableConfigurationProperties(BindFailureAnalyzerTests.EnumFailureProperties.class)
    static class EnumFailureConfiguration {}

    @EnableConfigurationProperties(BindFailureAnalyzerTests.NestedFailureProperties.class)
    static class NestedFailureConfiguration {}

    @ConfigurationProperties("test.foo")
    @Validated
    static class FieldValidationFailureProperties {
        @Min(value = 5, message = "at least five")
        private int value;

        public int getValue() {
            return this.value;
        }

        public void setValue(int value) {
            this.value = value;
        }
    }

    @ConfigurationProperties("test.foo")
    static class UnboundElementsFailureProperties {
        private List<String> listValue;

        public List<String> getListValue() {
            return this.listValue;
        }

        public void setListValue(List<String> listValue) {
            this.listValue = listValue;
        }
    }

    @ConfigurationProperties("test.foo")
    static class GenericFailureProperties {
        private int value;

        public int getValue() {
            return this.value;
        }

        public void setValue(int value) {
            this.value = value;
        }
    }

    @ConfigurationProperties("test.foo")
    static class EnumFailureProperties {
        private Set<BindFailureAnalyzerTests.Fruit> fruit;

        public Set<BindFailureAnalyzerTests.Fruit> getFruit() {
            return this.fruit;
        }

        public void setFruit(Set<BindFailureAnalyzerTests.Fruit> fruit) {
            this.fruit = fruit;
        }
    }

    @ConfigurationProperties("test.foo")
    static class NestedFailureProperties {
        private String value;

        public String getValue() {
            return this.value;
        }

        public void setValue(String value) {
            throw new RuntimeException("This is a failure");
        }
    }

    enum Fruit {

        APPLE,
        BANANA,
        ORANGE;}
}

