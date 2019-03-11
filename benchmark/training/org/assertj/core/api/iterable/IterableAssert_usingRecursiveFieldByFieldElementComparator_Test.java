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
import java.util.List;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.IterableAssertBaseTest;
import org.assertj.core.internal.Iterables;
import org.assertj.core.test.AlwaysEqualComparator;
import org.junit.jupiter.api.Test;


public class IterableAssert_usingRecursiveFieldByFieldElementComparator_Test extends IterableAssertBaseTest {
    private Iterables iterablesBefore;

    @Test
    public void successful_isEqualTo_assertion_using_recursive_field_by_field_element_comparator() {
        List<IterableAssert_usingRecursiveFieldByFieldElementComparator_Test.Foo> list1 = Collections.singletonList(new IterableAssert_usingRecursiveFieldByFieldElementComparator_Test.Foo("id", new IterableAssert_usingRecursiveFieldByFieldElementComparator_Test.Bar(1)));
        List<IterableAssert_usingRecursiveFieldByFieldElementComparator_Test.Foo> list2 = Collections.singletonList(new IterableAssert_usingRecursiveFieldByFieldElementComparator_Test.Foo("id", new IterableAssert_usingRecursiveFieldByFieldElementComparator_Test.Bar(1)));
        Assertions.assertThat(list1).usingRecursiveFieldByFieldElementComparator().isEqualTo(list2);
    }

    @Test
    public void successful_isIn_assertion_using_recursive_field_by_field_element_comparator() {
        List<IterableAssert_usingRecursiveFieldByFieldElementComparator_Test.Foo> list1 = Collections.singletonList(new IterableAssert_usingRecursiveFieldByFieldElementComparator_Test.Foo("id", new IterableAssert_usingRecursiveFieldByFieldElementComparator_Test.Bar(1)));
        List<IterableAssert_usingRecursiveFieldByFieldElementComparator_Test.Foo> list2 = Collections.singletonList(new IterableAssert_usingRecursiveFieldByFieldElementComparator_Test.Foo("id", new IterableAssert_usingRecursiveFieldByFieldElementComparator_Test.Bar(1)));
        Assertions.assertThat(list1).usingRecursiveFieldByFieldElementComparator().isIn(Collections.singletonList(list2));
    }

    @Test
    public void failed_isEqualTo_assertion_using_recursive_field_by_field_element_comparator() {
        List<IterableAssert_usingRecursiveFieldByFieldElementComparator_Test.Foo> list1 = Collections.singletonList(new IterableAssert_usingRecursiveFieldByFieldElementComparator_Test.Foo("id", new IterableAssert_usingRecursiveFieldByFieldElementComparator_Test.Bar(1)));
        List<IterableAssert_usingRecursiveFieldByFieldElementComparator_Test.Foo> list2 = Collections.singletonList(new IterableAssert_usingRecursiveFieldByFieldElementComparator_Test.Foo("id", new IterableAssert_usingRecursiveFieldByFieldElementComparator_Test.Bar(2)));
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> assertThat(list1).usingRecursiveFieldByFieldElementComparator().isEqualTo(list2)).withMessage(String.format(("%nExpecting:%n" + (((((((" <[Foo(id=id, bar=Bar(id=1))]>%n" + "to be equal to:%n") + " <[Foo(id=id, bar=Bar(id=2))]>%n") + "when comparing elements using recursive field/property by field/property comparator on all fields/properties%n") + "Comparators used:%n") + "- for elements fields (by type): {Double -> DoubleComparator[precision=1.0E-15], Float -> FloatComparator[precision=1.0E-6]}%n") + "- for elements (by type): {Double -> DoubleComparator[precision=1.0E-15], Float -> FloatComparator[precision=1.0E-6]}%n") + "but was not."))));
    }

    @Test
    public void failed_isIn_assertion_using_recursive_field_by_field_element_comparator() {
        List<IterableAssert_usingRecursiveFieldByFieldElementComparator_Test.Foo> list1 = Collections.singletonList(new IterableAssert_usingRecursiveFieldByFieldElementComparator_Test.Foo("id", new IterableAssert_usingRecursiveFieldByFieldElementComparator_Test.Bar(1)));
        List<IterableAssert_usingRecursiveFieldByFieldElementComparator_Test.Foo> list2 = Collections.singletonList(new IterableAssert_usingRecursiveFieldByFieldElementComparator_Test.Foo("id", new IterableAssert_usingRecursiveFieldByFieldElementComparator_Test.Bar(2)));
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> assertThat(list1).usingRecursiveFieldByFieldElementComparator().isIn(singletonList(list2))).withMessage(String.format(("%nExpecting:%n" + ((((((" <[Foo(id=id, bar=Bar(id=1))]>%n" + "to be in:%n") + " <[[Foo(id=id, bar=Bar(id=2))]]>%n") + "when comparing elements using recursive field/property by field/property comparator on all fields/properties%n") + "Comparators used:%n") + "- for elements fields (by type): {Double -> DoubleComparator[precision=1.0E-15], Float -> FloatComparator[precision=1.0E-6]}%n") + "- for elements (by type): {Double -> DoubleComparator[precision=1.0E-15], Float -> FloatComparator[precision=1.0E-6]}"))));
    }

    @Test
    public void should_be_able_to_use_a_comparator_for_specified_fields_of_elements_when_using_recursive_field_by_field_element_comparator() {
        IterableAssert_usingRecursiveFieldByFieldElementComparator_Test.Foo actual = new IterableAssert_usingRecursiveFieldByFieldElementComparator_Test.Foo("1", new IterableAssert_usingRecursiveFieldByFieldElementComparator_Test.Bar(1));
        IterableAssert_usingRecursiveFieldByFieldElementComparator_Test.Foo other = new IterableAssert_usingRecursiveFieldByFieldElementComparator_Test.Foo("1", new IterableAssert_usingRecursiveFieldByFieldElementComparator_Test.Bar(2));
        final class AlwaysEqualIntegerComparator implements Comparator<Integer> {
            @Override
            public int compare(Integer o1, Integer o2) {
                return 0;
            }
        }
        Assertions.assertThat(Collections.singletonList(actual)).usingComparatorForElementFieldsWithNames(new AlwaysEqualIntegerComparator(), "bar.id").usingRecursiveFieldByFieldElementComparator().contains(other);
    }

    @Test
    public void comparators_for_element_field_names_should_have_precedence_over_comparators_for_element_field_types_when_using_recursive_field_by_field_element_comparator() {
        Comparator<String> comparator = ( o1, o2) -> o1.compareTo(o2);
        IterableAssert_usingRecursiveFieldByFieldElementComparator_Test.Foo actual = new IterableAssert_usingRecursiveFieldByFieldElementComparator_Test.Foo("1", new IterableAssert_usingRecursiveFieldByFieldElementComparator_Test.Bar(1));
        IterableAssert_usingRecursiveFieldByFieldElementComparator_Test.Foo other = new IterableAssert_usingRecursiveFieldByFieldElementComparator_Test.Foo("2", new IterableAssert_usingRecursiveFieldByFieldElementComparator_Test.Bar(1));
        Assertions.assertThat(Collections.singletonList(actual)).usingComparatorForElementFieldsWithNames(AlwaysEqualComparator.ALWAY_EQUALS_STRING, "id").usingComparatorForElementFieldsWithType(comparator, String.class).usingRecursiveFieldByFieldElementComparator().contains(other);
    }

    @Test
    public void should_be_able_to_use_a_comparator_for_element_fields_with_specified_type_when_using_recursive_field_by_field_element_comparator() {
        IterableAssert_usingRecursiveFieldByFieldElementComparator_Test.Foo actual = new IterableAssert_usingRecursiveFieldByFieldElementComparator_Test.Foo("1", new IterableAssert_usingRecursiveFieldByFieldElementComparator_Test.Bar(1));
        IterableAssert_usingRecursiveFieldByFieldElementComparator_Test.Foo other = new IterableAssert_usingRecursiveFieldByFieldElementComparator_Test.Foo("2", new IterableAssert_usingRecursiveFieldByFieldElementComparator_Test.Bar(1));
        Assertions.assertThat(Collections.singletonList(actual)).usingComparatorForElementFieldsWithType(AlwaysEqualComparator.ALWAY_EQUALS_STRING, String.class).usingRecursiveFieldByFieldElementComparator().contains(other);
    }

    public static class Foo {
        public String id;

        public IterableAssert_usingRecursiveFieldByFieldElementComparator_Test.Bar bar;

        public Foo(String id, IterableAssert_usingRecursiveFieldByFieldElementComparator_Test.Bar bar) {
            this.id = id;
            this.bar = bar;
        }

        @Override
        public String toString() {
            return ((("Foo(id=" + (id)) + ", bar=") + (bar)) + ")";
        }
    }

    public static class Bar {
        public int id;

        public Bar(int id) {
            this.id = id;
        }

        @Override
        public String toString() {
            return ("Bar(id=" + (id)) + ")";
        }
    }
}

