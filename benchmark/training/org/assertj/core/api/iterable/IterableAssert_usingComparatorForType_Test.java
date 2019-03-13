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


import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.IterableAssertBaseTest;
import org.assertj.core.internal.Iterables;
import org.assertj.core.test.AlwaysEqualComparator;
import org.assertj.core.test.Jedi;
import org.assertj.core.test.NeverEqualComparator;
import org.assertj.core.util.BigDecimalComparator;
import org.junit.jupiter.api.Test;


public class IterableAssert_usingComparatorForType_Test extends IterableAssertBaseTest {
    private Jedi actual = new Jedi("Yoda", "green");

    private Jedi other = new Jedi("Luke", "blue");

    private Iterables iterablesBefore;

    @Test
    public void should_be_able_to_use_a_comparator_for_specified_types() {
        // GIVEN
        List<Object> list = Arrays.asList("some", "other", new BigDecimal(42));
        // THEN
        Assertions.assertThat(list).usingComparatorForType(AlwaysEqualComparator.ALWAY_EQUALS_STRING, String.class).usingComparatorForType(BigDecimalComparator.BIG_DECIMAL_COMPARATOR, BigDecimal.class).contains("other", "any", new BigDecimal("42.0")).containsOnly("other", "any", new BigDecimal("42.00")).containsExactly("other", "any", new BigDecimal("42.000"));
    }

    @Test
    public void should_use_comparator_for_type_when_using_element_comparator_ignoring_fields() {
        Assertions.assertThat(Arrays.asList(actual, "some")).usingComparatorForType(AlwaysEqualComparator.ALWAY_EQUALS_STRING, String.class).usingElementComparatorIgnoringFields("name").isNotEmpty().contains(other, "any").containsExactly(other, "any");
    }

    @Test
    public void should_use_comparator_for_type_when_using_element_comparator_on_fields() {
        Assertions.assertThat(Arrays.asList(actual, "some")).usingComparatorForType(AlwaysEqualComparator.ALWAY_EQUALS_STRING, String.class).usingElementComparatorOnFields("name", "lightSaberColor").contains(other, "any");
    }

    @Test
    public void should_use_comparator_for_type_when_using_field_by_field_element_comparator() {
        Assertions.assertThat(Arrays.asList(actual, "some")).usingComparatorForType(AlwaysEqualComparator.ALWAY_EQUALS_STRING, String.class).usingFieldByFieldElementComparator().contains(other, "any");
    }

    @Test
    public void should_use_comparator_for_type_when_using_recursive_field_by_field_element_comparator() {
        Assertions.assertThat(Arrays.asList(actual, "some")).usingComparatorForType(AlwaysEqualComparator.ALWAY_EQUALS_STRING, String.class).usingRecursiveFieldByFieldElementComparator().contains(other, "any");
    }

    @Test
    public void should_only_use_comparator_on_fields_element_but_not_the_element_itself() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> assertThat(asList(actual, "some")).usingComparatorForElementFieldsWithType(ALWAY_EQUALS_STRING, .class).usingElementComparatorIgnoringFields("name").contains(other, "any")).withMessage(String.format(("%nExpecting:%n" + ((((((((" <[Yoda the Jedi, \"some\"]>%n" + "to contain:%n") + " <[Luke the Jedi, \"any\"]>%n") + "but could not find:%n") + " <[\"any\"]>%n") + "when comparing values using field/property by field/property comparator on all fields/properties except [\"name\"]%n") + "Comparators used:%n") + "- for elements fields (by type): {Double -> DoubleComparator[precision=1.0E-15], Float -> FloatComparator[precision=1.0E-6], String -> AlwaysEqualComparator}%n") + "- for elements (by type): {Double -> DoubleComparator[precision=1.0E-15], Float -> FloatComparator[precision=1.0E-6]}"))));
    }

    @Test
    public void should_use_comparator_set_last_on_elements() {
        Assertions.assertThat(Arrays.asList(actual, actual)).usingComparatorForElementFieldsWithType(NeverEqualComparator.NEVER_EQUALS_STRING, String.class).usingComparatorForType(AlwaysEqualComparator.ALWAY_EQUALS_STRING, String.class).usingFieldByFieldElementComparator().contains(other, other);
    }

    @Test
    public void should_be_able_to_replace_a_registered_comparator_by_type() {
        Assertions.assertThat(Arrays.asList(actual, actual)).usingComparatorForType(NeverEqualComparator.NEVER_EQUALS_STRING, String.class).usingComparatorForType(AlwaysEqualComparator.ALWAY_EQUALS_STRING, String.class).usingFieldByFieldElementComparator().contains(other, other);
    }

    @Test
    public void should_be_able_to_replace_a_registered_comparator_by_field() {
        // @format:off
        Assertions.assertThat(Arrays.asList(actual, actual)).usingComparatorForElementFieldsWithNames(NeverEqualComparator.NEVER_EQUALS_STRING, "name", "lightSaberColor").usingComparatorForElementFieldsWithNames(AlwaysEqualComparator.ALWAY_EQUALS_STRING, "name", "lightSaberColor").usingFieldByFieldElementComparator().contains(other, other);
        // @format:on
    }

    @Test
    public void should_fail_because_of_comparator_set_last() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> {
            assertThat(asList(actual, actual)).usingComparatorForType(ALWAY_EQUALS_STRING, .class).usingComparatorForElementFieldsWithType(NEVER_EQUALS_STRING, .class).usingFieldByFieldElementComparator().contains(other, other);
        }).withMessage(String.format(("%nExpecting:%n" + ((((((((" <[Yoda the Jedi, Yoda the Jedi]>%n" + "to contain:%n") + " <[Luke the Jedi, Luke the Jedi]>%n") + "but could not find:%n") + " <[Luke the Jedi]>%n") + "when comparing values using field/property by field/property comparator on all fields/properties%n") + "Comparators used:%n") + "- for elements fields (by type): {Double -> DoubleComparator[precision=1.0E-15], Float -> FloatComparator[precision=1.0E-6], String -> org.assertj.core.test.NeverEqualComparator}%n") + "- for elements (by type): {Double -> DoubleComparator[precision=1.0E-15], Float -> FloatComparator[precision=1.0E-6], String -> AlwaysEqualComparator}"))));
    }
}

