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


import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.assertj.core.api.Assertions;
import org.assertj.core.extractor.Extractors;
import org.assertj.core.test.Employee;
import org.assertj.core.util.introspection.IntrospectionError;
import org.junit.jupiter.api.Test;


public class FieldsOrPropertiesExtractor_extract_tuples_Test {
    private Employee yoda;

    private Employee luke;

    private List<Employee> employees;

    @Test
    public void should_extract_tuples_from_fields_or_properties() {
        List<Tuple> extractedValues = FieldsOrPropertiesExtractor.extract(employees, Extractors.byName("id", "age"));
        Assertions.assertThat(extractedValues).containsOnly(Tuple.tuple(1L, 800), Tuple.tuple(2L, 26));
    }

    @Test
    public void should_extract_tuples_with_consistent_iteration_order() {
        Set<Employee> employeeSet = new HashSet<>(employees);
        List<Tuple> extractedValues = FieldsOrPropertiesExtractor.extract(employeeSet, Extractors.byName("id", "name.first", "age"));
        Assertions.assertThat(extractedValues).containsOnly(Tuple.tuple(1L, "Yoda", 800), Tuple.tuple(2L, "Luke", 26));
    }

    @Test
    public void should_extract_tuples_with_null_value_for_null_nested_field_or_property() {
        luke.setName(null);
        Assertions.assertThat(FieldsOrPropertiesExtractor.extract(employees, Extractors.byName("id", "name.first", "age"))).containsOnly(Tuple.tuple(1L, "Yoda", 800), Tuple.tuple(2L, null, 26));
        Assertions.assertThat(FieldsOrPropertiesExtractor.extract(employees, Extractors.byName("name.first"))).containsOnly("Yoda", null);
        Assertions.assertThat(FieldsOrPropertiesExtractor.extract(employees, Extractors.byName("id", "surname.first"))).containsOnly(Tuple.tuple(1L, "Master"), Tuple.tuple(2L, null));
    }

    @Test
    public void should_throw_error_when_no_property_nor_public_field_match_one_of_given_names() {
        Assertions.assertThatExceptionOfType(IntrospectionError.class).isThrownBy(() -> extract(employees, byName("id", "age", "unknown")));
    }

    @Test
    public void should_throw_exception_when_given_name_is_null() {
        Assertions.assertThatIllegalArgumentException().isThrownBy(() -> extract(employees, byName(((String[]) (null))))).withMessage("The names of the fields/properties to read should not be null");
    }

    @Test
    public void should_throw_exception_when_given_name_is_empty() {
        Assertions.assertThatIllegalArgumentException().isThrownBy(() -> extract(employees, byName(new String[0]))).withMessage("The names of the fields/properties to read should not be empty");
    }
}

