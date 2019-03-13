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
package org.assertj.core.api.atomic.referencearray;


import java.util.concurrent.atomic.AtomicReferenceArray;
import org.assertj.core.api.Assertions;
import org.assertj.core.test.Employee;
import org.assertj.core.test.Name;
import org.assertj.core.util.introspection.IntrospectionError;
import org.junit.jupiter.api.Test;


@SuppressWarnings("deprecation")
public class AtomicReferenceArrayAssert_extracting_Test {
    private static Employee yoda;

    private static Employee luke;

    private static AtomicReferenceArray<Employee> employees;

    @Test
    public void should_allow_assertions_on_property_values_extracted_from_given_iterable() {
        Assertions.assertThat(AtomicReferenceArrayAssert_extracting_Test.employees).extracting("age").containsOnly(800, 26);
    }

    @Test
    public void should_allow_assertions_on_property_values_extracted_from_given_iterable_with_extracted_type_defined() {
        Assertions.assertThat(AtomicReferenceArrayAssert_extracting_Test.employees).extracting("name", Name.class).containsOnly(new Name("Yoda"), new Name("Luke", "Skywalker"));
    }

    @Test
    public void should_allow_assertions_on_field_values_extracted_from_given_iterable() {
        // basic types
        Assertions.assertThat(AtomicReferenceArrayAssert_extracting_Test.employees).extracting("id").containsOnly(1L, 2L);
        // object
        Assertions.assertThat(AtomicReferenceArrayAssert_extracting_Test.employees).extracting("name").containsOnly(new Name("Yoda"), new Name("Luke", "Skywalker"));
        // nested property
        Assertions.assertThat(AtomicReferenceArrayAssert_extracting_Test.employees).extracting("name.first").containsOnly("Yoda", "Luke");
    }

    @Test
    public void should_throw_error_if_no_property_nor_field_with_given_name_can_be_extracted() {
        Assertions.assertThatExceptionOfType(IntrospectionError.class).isThrownBy(() -> assertThat(AtomicReferenceArrayAssert_extracting_Test.employees).extracting("unknown"));
    }

    @Test
    public void should_allow_assertions_on_multiple_extracted_values_from_given_iterable() {
        Assertions.assertThat(AtomicReferenceArrayAssert_extracting_Test.employees).extracting("name.first", "age", "id").containsOnly(Assertions.tuple("Yoda", 800, 1L), Assertions.tuple("Luke", 26, 2L));
    }

    @Test
    public void should_throw_error_if_one_property_or_field_can_not_be_extracted() {
        Assertions.assertThatExceptionOfType(IntrospectionError.class).isThrownBy(() -> {
            assertThat(AtomicReferenceArrayAssert_extracting_Test.employees).extracting("unknown", "age", "id").containsOnly(tuple("Yoda", 800, 1L), tuple("Luke", 26, 2L));
        });
    }

    @Test
    public void should_allow_assertions_on_extractor_assertions_extracted_from_given_array() {
        Assertions.assertThat(AtomicReferenceArrayAssert_extracting_Test.employees).extracting(( input) -> input.getName().getFirst()).containsOnly("Yoda", "Luke");
    }

    @Test
    public void should_use_property_field_names_as_description_when_extracting_simple_value_list() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> assertThat(AtomicReferenceArrayAssert_extracting_Test.employees).extracting("name.first").isEmpty()).withMessageContaining("[Extracted: name.first]");
    }

    @Test
    public void should_use_property_field_names_as_description_when_extracting_typed_simple_value_list() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> assertThat(AtomicReferenceArrayAssert_extracting_Test.employees).extracting("name.first", .class).isEmpty()).withMessageContaining("[Extracted: name.first]");
    }

    @Test
    public void should_use_property_field_names_as_description_when_extracting_tuples_list() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> assertThat(AtomicReferenceArrayAssert_extracting_Test.employees).extracting("name.first", "name.last").isEmpty()).withMessageContaining("[Extracted: name.first, name.last]");
    }

    @Test
    public void should_keep_existing_description_if_set_when_extracting_typed_simple_value_list() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> assertThat(AtomicReferenceArrayAssert_extracting_Test.employees).as("check employees first name").extracting("name.first", .class).isEmpty()).withMessageContaining("[check employees first name]");
    }

    @Test
    public void should_keep_existing_description_if_set_when_extracting_tuples_list() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> assertThat(AtomicReferenceArrayAssert_extracting_Test.employees).as("check employees name").extracting("name.first", "name.last").isEmpty()).withMessageContaining("[check employees name]");
    }

    @Test
    public void should_keep_existing_description_if_set_when_extracting_simple_value_list() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> assertThat(AtomicReferenceArrayAssert_extracting_Test.employees).as("check employees first name").extracting("name.first").isEmpty()).withMessageContaining("[check employees first name]");
    }

    @Test
    public void should_let_anonymous_class_extractor_runtime_exception_bubble_up() {
        Assertions.assertThatExceptionOfType(RuntimeException.class).isThrownBy(() -> assertThat(AtomicReferenceArrayAssert_extracting_Test.employees).extracting(new Extractor<Employee, String>() {
            @Override
            public String extract(Employee employee) {
                if ((employee.getAge()) > 100)
                    throw new RuntimeException("age > 100");

                return employee.getName().getFirst();
            }
        })).withMessage("age > 100");
    }

    @Test
    public void should_let_anonymous_class_function_runtime_exception_bubble_up() {
        Assertions.assertThatExceptionOfType(RuntimeException.class).isThrownBy(() -> assertThat(AtomicReferenceArrayAssert_extracting_Test.employees).extracting(new Function<Employee, String>() {
            @Override
            public String apply(Employee employee) {
                if ((employee.getAge()) > 100)
                    throw new RuntimeException("age > 100");

                return employee.getName().getFirst();
            }
        })).withMessage("age > 100");
    }

    @Test
    public void should_rethrow_throwing_extractor_checked_exception_as_a_runtime_exception() {
        Assertions.assertThatExceptionOfType(RuntimeException.class).isThrownBy(() -> assertThat(AtomicReferenceArrayAssert_extracting_Test.employees).extracting(( employee) -> {
            if ((employee.getAge()) > 100)
                throw new Exception("age > 100");

            return employee.getName().getFirst();
        })).withMessage("java.lang.Exception: age > 100");
    }

    @Test
    public void should_let_throwing_extractor_runtime_exception_bubble_up() {
        Assertions.assertThatExceptionOfType(RuntimeException.class).isThrownBy(() -> assertThat(AtomicReferenceArrayAssert_extracting_Test.employees).extracting(( employee) -> {
            if ((employee.getAge()) > 100)
                throw new RuntimeException("age > 100");

            return employee.getName().getFirst();
        })).withMessage("age > 100");
    }

    @Test
    public void should_allow_extracting_with_throwing_extractor() {
        Assertions.assertThat(AtomicReferenceArrayAssert_extracting_Test.employees).extracting(( employee) -> {
            if ((employee.getAge()) < 20)
                throw new Exception("age < 20");

            return employee.getName().getFirst();
        }).containsOnly("Yoda", "Luke");
    }

    @Test
    public void should_allow_extracting_with_anonymous_class_throwing_extractor() {
        Assertions.assertThat(AtomicReferenceArrayAssert_extracting_Test.employees).extracting(new org.assertj.core.api.iterable.ThrowingExtractor<Employee, Object, Exception>() {
            @Override
            public Object extractThrows(Employee employee) throws Exception {
                if ((employee.getAge()) < 20)
                    throw new Exception("age < 20");

                return employee.getName().getFirst();
            }
        }).containsOnly("Yoda", "Luke");
    }
}

