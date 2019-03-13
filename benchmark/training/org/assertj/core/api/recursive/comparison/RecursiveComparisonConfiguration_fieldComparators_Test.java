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
package org.assertj.core.api.recursive.comparison;


import org.assertj.core.api.Assertions;
import org.assertj.core.test.AlwaysEqualComparator;
import org.assertj.core.util.AbsValueComparator;
import org.junit.jupiter.api.Test;


public class RecursiveComparisonConfiguration_fieldComparators_Test {
    private RecursiveComparisonConfiguration recursiveComparisonConfiguration;

    @Test
    public void should_register_given_field_comparators() {
        // GIVEN
        AbsValueComparator<Integer> integerComparator = new AbsValueComparator<>();
        recursiveComparisonConfiguration.registerComparatorForField(integerComparator, FieldLocation.fielLocation("height"));
        recursiveComparisonConfiguration.registerComparatorForField(AlwaysEqualComparator.ALWAY_EQUALS_TUPLE, FieldLocation.fielLocation("weight"));
        // THEN
        Assertions.assertThat(recursiveComparisonConfiguration.getComparatorForField("height")).isSameAs(integerComparator);
        Assertions.assertThat(recursiveComparisonConfiguration.getComparatorForField("weight")).isSameAs(AlwaysEqualComparator.ALWAY_EQUALS_TUPLE);
    }

    @Test
    public void should_replace_a_registered_field_comparator() {
        // GIVEN
        recursiveComparisonConfiguration.registerComparatorForField(new AbsValueComparator(), FieldLocation.fielLocation("height"));
        recursiveComparisonConfiguration.registerComparatorForField(AlwaysEqualComparator.ALWAY_EQUALS_TUPLE, FieldLocation.fielLocation("height"));
        // THEN
        Assertions.assertThat(recursiveComparisonConfiguration.getComparatorForField("height")).isSameAs(AlwaysEqualComparator.ALWAY_EQUALS_TUPLE);
    }
}

