/**
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 *
 * Copyright 2012-2019 the original author or authors.
 */
package org.assertj.core.groups;


import java.util.Arrays;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.assertj.core.extractor.Extractors;
import org.assertj.core.test.Employee;
import org.assertj.core.test.Name;
import org.assertj.core.util.introspection.IntrospectionError;
import org.junit.jupiter.api.Test;


public class FieldsOrPropertiesExtractor_extract_Test {
    private Employee yoda;

    private Employee luke;

    private List<Employee> employees;

    @Test
    public void should_extract_field_values_in_absence_of_properties() {
        List<Object> extractedValues = FieldsOrPropertiesExtractor.extract(employees, Extractors.byName("id"));
        Assertions.assertThat(extractedValues).containsOnly(1L, 2L);
    }

    @Test
    public void should_extract_null_valuesfor_null_property_values() {
        yoda.setName(null);
        List<Object> extractedValues = FieldsOrPropertiesExtractor.extract(employees, Extractors.byName("name"));
        Assertions.assertThat(extractedValues).containsOnly(null, new Name("Luke", "Skywalker"));
    }

    @Test
    public void should_extract_null_values_for_null_nested_property_values() {
        yoda.setName(null);
        List<Object> extractedValues = FieldsOrPropertiesExtractor.extract(employees, Extractors.byName("name.first"));
        Assertions.assertThat(extractedValues).containsOnly(null, "Luke");
    }

    @Test
    public void should_extract_null_valuesfor_null_field_values() {
        List<Object> extractedValues = FieldsOrPropertiesExtractor.extract(employees, Extractors.byName("surname"));
        Assertions.assertThat(extractedValues).containsOnly(new Name("Master", "Jedi"), null);
    }

    @Test
    public void should_extract_null_values_for_null_nested_field_values() {
        List<Object> extractedValues = FieldsOrPropertiesExtractor.extract(employees, Extractors.byName("surname.first"));
        Assertions.assertThat(extractedValues).containsOnly("Master", null);
    }

    @Test
    public void should_extract_property_values_when_no_public_field_match_given_name() {
        List<Object> extractedValues = FieldsOrPropertiesExtractor.extract(employees, Extractors.byName("age"));
        Assertions.assertThat(extractedValues).containsOnly(800, 26);
    }

    @Test
    public void should_extract_pure_property_values() {
        List<Object> extractedValues = FieldsOrPropertiesExtractor.extract(employees, Extractors.byName("adult"));
        Assertions.assertThat(extractedValues).containsOnly(true);
    }

    @Test
    public void should_throw_error_when_no_property_nor_public_field_match_given_name() {
        Assertions.assertThatExceptionOfType(IntrospectionError.class).isThrownBy(() -> extract(employees, byName("unknown")));
    }

    @Test
    public void should_throw_exception_when_given_name_is_null() {
        Assertions.assertThatIllegalArgumentException().isThrownBy(() -> extract(employees, byName(((String) (null))))).withMessage("The name of the field/property to read should not be null");
    }

    @Test
    public void should_throw_exception_when_given_name_is_empty() {
        Assertions.assertThatIllegalArgumentException().isThrownBy(() -> extract(employees, byName(""))).withMessage("The name of the field/property to read should not be empty");
    }

    @Test
    public void should_fallback_to_field_if_exception_has_been_thrown_on_property_access() {
        List<Employee> employees = Arrays.<Employee>asList(new FieldsOrPropertiesExtractor_extract_Test.EmployeeWithBrokenName("Name"));
        List<Object> extractedValues = FieldsOrPropertiesExtractor.extract(employees, Extractors.byName("name"));
        Assertions.assertThat(extractedValues).containsOnly(new Name("Name"));
    }

    @Test
    public void should_prefer_properties_over_fields() {
        List<Employee> employees = Arrays.<Employee>asList(new FieldsOrPropertiesExtractor_extract_Test.EmployeeWithOverriddenName("Overridden Name"));
        List<Object> extractedValues = FieldsOrPropertiesExtractor.extract(employees, Extractors.byName("name"));
        Assertions.assertThat(extractedValues).containsOnly(new Name("Overridden Name"));
    }

    @Test
    public void should_throw_exception_if_property_cannot_be_extracted_due_to_runtime_exception_during_property_access() {
        Assertions.assertThatExceptionOfType(IntrospectionError.class).isThrownBy(() -> {
            List<Employee> employees = Arrays.<Employee>asList(new org.assertj.core.groups.BrokenEmployee());
            extract(employees, byName("adult"));
        });
    }

    public static class EmployeeWithBrokenName extends Employee {
        public EmployeeWithBrokenName(String name) {
            super(1L, new Name(name), 0);
        }

        @Override
        public Name getName() {
            throw new IllegalStateException();
        }
    }

    public static class EmployeeWithOverriddenName extends Employee {
        private String overriddenName;

        public EmployeeWithOverriddenName(final String overriddenName) {
            super(1L, new Name("Name"), 0);
            this.overriddenName = overriddenName;
        }

        @Override
        public Name getName() {
            return new Name(overriddenName);
        }
    }

    public static class BrokenEmployee extends Employee {
        @Override
        public boolean isAdult() {
            throw new IllegalStateException();
        }
    }
}

