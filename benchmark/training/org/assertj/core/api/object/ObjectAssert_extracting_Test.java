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
package org.assertj.core.api.object;


import java.math.BigDecimal;
import org.assertj.core.api.Assertions;
import org.assertj.core.test.Employee;
import org.assertj.core.test.Name;
import org.assertj.core.util.BigDecimalComparator;
import org.junit.jupiter.api.Test;


/**
 * Tests for <code>{@link ObjectAssert#extracting(String[])}</code>.
 */
public class ObjectAssert_extracting_Test {
    private Employee luke;

    @Test
    public void should_allow_assertions_on_array_of_properties_extracted_from_given_object_by_name() {
        Assertions.assertThat(luke).extracting("id", "name").hasSize(2).doesNotContainNull();
        Assertions.assertThat(luke).extracting("name.first", "name.last").hasSize(2).containsExactly("Luke", "Skywalker");
    }

    @Test
    public void should_allow_assertions_on_array_of_properties_extracted_from_given_object_with_lambdas() {
        Assertions.assertThat(luke).extracting(Employee::getName, Employee::getAge).hasSize(2).doesNotContainNull();
        Assertions.assertThat(luke).extracting(( employee) -> employee.getName().first, ( employee) -> employee.getName().getLast()).hasSize(2).containsExactly("Luke", "Skywalker");
    }

    @Test
    public void should_use_property_field_names_as_description_when_extracting_tuples_list() {
        Employee luke = new Employee(2L, new Name("Luke", "Skywalker"), 26);
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> assertThat(luke).extracting("name.first", "name.last").isEmpty()).withMessageContaining("[Extracted: name.first, name.last]");
    }

    @Test
    public void should_keep_existing_description_if_set_when_extracting_tuples_list() {
        Employee luke = new Employee(2L, new Name("Luke", "Skywalker"), 26);
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> assertThat(luke).as("check luke first name").extracting("name.first").isEmpty()).withMessageContaining("[check luke first name]");
    }

    @Test
    public void should_allow_to_specify_type_comparator_after_using_extracting_on_object() {
        ObjectAssert_extracting_Test.Person obiwan = new ObjectAssert_extracting_Test.Person("Obi-Wan");
        obiwan.setHeight(new BigDecimal("1.820"));
        Assertions.assertThat(obiwan).extracting("name", "height").usingComparatorForType(BigDecimalComparator.BIG_DECIMAL_COMPARATOR, BigDecimal.class).containsExactly("Obi-Wan", new BigDecimal("1.82"));
    }

    @SuppressWarnings("unused")
    private static class Person {
        private final String name;

        private BigDecimal height;

        public Person(String name) {
            this.name = name;
        }

        public void setHeight(BigDecimal height) {
            this.height = height;
        }
    }
}

