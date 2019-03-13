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


import org.assertj.core.api.Assertions;
import org.assertj.core.util.introspection.IntrospectionError;
import org.junit.jupiter.api.Test;


public class AtomicReferenceArrayAssert_filteredOn_notIn_Test extends AtomicReferenceArrayAssert_filtered_baseTest {
    @Test
    public void should_apply_notIn_filter() {
        Assertions.assertThat(employees).filteredOn("age", Assertions.notIn(800, 10)).containsOnly(luke);
        Assertions.assertThat(employees).filteredOn("age", Assertions.notIn(800)).containsOnly(luke, noname);
    }

    @Test
    public void should_filter_object_array_under_test_on_property_not_backed_by_a_field_values() {
        Assertions.assertThat(employees).filteredOn("adult", Assertions.notIn(false)).containsOnly(yoda, obiwan, luke);
        Assertions.assertThat(employees).filteredOn("adult", Assertions.notIn(true)).containsOnly(noname);
        Assertions.assertThat(employees).filteredOn("adult", Assertions.notIn(true, false)).isEmpty();
    }

    @Test
    public void should_filter_object_array_under_test_on_public_field_values() {
        Assertions.assertThat(employees).filteredOn("id", Assertions.notIn(2L, 3L, 4L)).containsOnly(yoda);
    }

    @Test
    public void should_filter_object_array_under_test_on_private_field_values() {
        Assertions.assertThat(employees).filteredOn("city", Assertions.notIn("Paris")).containsOnly(yoda, obiwan, luke, noname);
        Assertions.assertThat(employees).filteredOn("city", Assertions.notIn("New York")).isEmpty();
        Assertions.assertThat(employees).filteredOn("city", Assertions.notIn("New York", "Paris")).isEmpty();
    }

    @Test
    public void should_fail_if_filter_is_on_private_field_and_reading_private_field_is_disabled() {
        Assertions.setAllowExtractingPrivateFields(false);
        try {
            Assertions.assertThatExceptionOfType(IntrospectionError.class).isThrownBy(() -> {
                assertThat(employees).filteredOn("city", notIn("New York")).isEmpty();
            });
        } finally {
            Assertions.setAllowExtractingPrivateFields(true);
        }
    }

    @Test
    public void should_filter_object_array_under_test_on_nested_property_values() {
        Assertions.assertThat(employees).filteredOn("name.first", Assertions.notIn("Luke")).containsOnly(yoda, obiwan, noname);
    }

    @Test
    public void should_filter_object_array_under_test_on_nested_mixed_property_and_field_values() {
        Assertions.assertThat(employees).filteredOn("name.last", Assertions.notIn("Skywalker")).containsOnly(yoda, obiwan, noname);
        Assertions.assertThat(employees).filteredOn("name.last", Assertions.notIn("Skywalker", null)).isEmpty();
        Assertions.assertThat(employees).filteredOn("name.last", Assertions.notIn("Vader")).containsOnly(yoda, obiwan, noname, luke);
    }

    @Test
    public void should_fail_if_given_property_or_field_name_is_null() {
        Assertions.assertThatIllegalArgumentException().isThrownBy(() -> assertThat(employees).filteredOn(null, notIn(800))).withMessage("The property/field name to filter on should not be null or empty");
    }

    @Test
    public void should_fail_if_given_property_or_field_name_is_empty() {
        Assertions.assertThatIllegalArgumentException().isThrownBy(() -> assertThat(employees).filteredOn("", notIn(800))).withMessage("The property/field name to filter on should not be null or empty");
    }

    @Test
    public void should_fail_if_on_of_the_object_array_element_does_not_have_given_property_or_field() {
        Assertions.assertThatExceptionOfType(IntrospectionError.class).isThrownBy(() -> assertThat(employees).filteredOn("secret", notIn("???"))).withMessageContaining("Can't find any field or property with name 'secret'");
    }
}

