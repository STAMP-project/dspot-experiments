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
package org.assertj.core.api.iterable;


import java.util.Collections;
import java.util.Comparator;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.IterableAssertBaseTest;
import org.assertj.core.internal.Iterables;
import org.assertj.core.test.AlwaysEqualComparator;
import org.assertj.core.test.Jedi;
import org.junit.jupiter.api.Test;


public class IterableAssert_usingElementComparatorIgnoringFields_Test extends IterableAssertBaseTest {
    private Iterables iterablesBefore;

    @Test
    public void should_be_able_to_use_a_comparator_for_specified_fields_of_elements_when_using_element_comparator_ignoring_fields() {
        Jedi actual = new Jedi("Yoda", "green");
        Jedi other = new Jedi("Luke", "green");
        Assertions.assertThat(Collections.singletonList(actual)).usingComparatorForElementFieldsWithNames(AlwaysEqualComparator.ALWAY_EQUALS_STRING, "name").usingElementComparatorIgnoringFields("lightSaberColor").contains(other);
    }

    @Test
    public void comparators_for_element_field_names_should_have_precedence_over_comparators_for_element_field_types_using_element_comparator_ignoring_fields() {
        Comparator<String> comparator = ( o1, o2) -> o1.compareTo(o2);
        Jedi actual = new Jedi("Yoda", "green");
        Jedi other = new Jedi("Luke", "green");
        Assertions.assertThat(Collections.singletonList(actual)).usingComparatorForElementFieldsWithNames(AlwaysEqualComparator.ALWAY_EQUALS_STRING, "name").usingComparatorForElementFieldsWithType(comparator, String.class).usingElementComparatorIgnoringFields("lightSaberColor").contains(other);
    }

    @Test
    public void should_be_able_to_use_a_comparator_for_element_fields_with_specified_type_using_element_comparator_ignoring_fields() {
        Jedi actual = new Jedi("Yoda", "green");
        Jedi other = new Jedi("Luke", "blue");
        Assertions.assertThat(Collections.singletonList(actual)).usingComparatorForElementFieldsWithType(AlwaysEqualComparator.ALWAY_EQUALS_STRING, String.class).usingElementComparatorIgnoringFields("name").contains(other);
    }
}

