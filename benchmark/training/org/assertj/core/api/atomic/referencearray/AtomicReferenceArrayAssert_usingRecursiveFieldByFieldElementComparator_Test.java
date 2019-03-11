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


import java.util.Comparator;
import java.util.concurrent.atomic.AtomicReferenceArray;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.AtomicReferenceArrayAssertBaseTest;
import org.assertj.core.internal.ObjectArrays;
import org.assertj.core.test.AlwaysEqualComparator;
import org.junit.jupiter.api.Test;


public class AtomicReferenceArrayAssert_usingRecursiveFieldByFieldElementComparator_Test extends AtomicReferenceArrayAssertBaseTest {
    private ObjectArrays arraysBefore;

    @Test
    public void successful_isEqualTo_assertion_using_recursive_field_by_field_element_comparator() {
        AtomicReferenceArray<AtomicReferenceArrayAssert_usingRecursiveFieldByFieldElementComparator_Test.Foo> array1 = AtomicReferenceArrayAssertBaseTest.atomicArrayOf(new AtomicReferenceArrayAssert_usingRecursiveFieldByFieldElementComparator_Test.Foo("id", new AtomicReferenceArrayAssert_usingRecursiveFieldByFieldElementComparator_Test.Bar(1)));
        AtomicReferenceArrayAssert_usingRecursiveFieldByFieldElementComparator_Test.Foo[] array2 = new AtomicReferenceArrayAssert_usingRecursiveFieldByFieldElementComparator_Test.Foo[]{ new AtomicReferenceArrayAssert_usingRecursiveFieldByFieldElementComparator_Test.Foo("id", new AtomicReferenceArrayAssert_usingRecursiveFieldByFieldElementComparator_Test.Bar(1)) };
        Assertions.assertThat(array1).usingRecursiveFieldByFieldElementComparator().isEqualTo(array2);
    }

    @Test
    public void successful_isIn_assertion_using_recursive_field_by_field_element_comparator() {
        AtomicReferenceArray<AtomicReferenceArrayAssert_usingRecursiveFieldByFieldElementComparator_Test.Foo> array1 = AtomicReferenceArrayAssertBaseTest.atomicArrayOf(new AtomicReferenceArrayAssert_usingRecursiveFieldByFieldElementComparator_Test.Foo("id", new AtomicReferenceArrayAssert_usingRecursiveFieldByFieldElementComparator_Test.Bar(1)));
        AtomicReferenceArrayAssert_usingRecursiveFieldByFieldElementComparator_Test.Foo[] array2 = new AtomicReferenceArrayAssert_usingRecursiveFieldByFieldElementComparator_Test.Foo[]{ new AtomicReferenceArrayAssert_usingRecursiveFieldByFieldElementComparator_Test.Foo("id", new AtomicReferenceArrayAssert_usingRecursiveFieldByFieldElementComparator_Test.Bar(1)) };
        Assertions.assertThat(array1).usingRecursiveFieldByFieldElementComparator().isIn(new Object[]{ array2 });
    }

    @Test
    public void failed_isEqualTo_assertion_using_recursive_field_by_field_element_comparator() {
        AtomicReferenceArray<AtomicReferenceArrayAssert_usingRecursiveFieldByFieldElementComparator_Test.Foo> array1 = AtomicReferenceArrayAssertBaseTest.atomicArrayOf(new AtomicReferenceArrayAssert_usingRecursiveFieldByFieldElementComparator_Test.Foo("id", new AtomicReferenceArrayAssert_usingRecursiveFieldByFieldElementComparator_Test.Bar(1)));
        AtomicReferenceArrayAssert_usingRecursiveFieldByFieldElementComparator_Test.Foo[] array2 = new AtomicReferenceArrayAssert_usingRecursiveFieldByFieldElementComparator_Test.Foo[]{ new AtomicReferenceArrayAssert_usingRecursiveFieldByFieldElementComparator_Test.Foo("id", new AtomicReferenceArrayAssert_usingRecursiveFieldByFieldElementComparator_Test.Bar(2)) };
        try {
            Assertions.assertThat(array1).usingRecursiveFieldByFieldElementComparator().isEqualTo(array2);
        } catch (AssertionError e) {
            Assertions.assertThat(e).hasMessage(String.format(("%nExpecting:%n" + (((((((" <[Foo(id=id, bar=Bar(id=1))]>%n" + "to be equal to:%n") + " <[Foo(id=id, bar=Bar(id=2))]>%n") + "when comparing elements using recursive field/property by field/property comparator on all fields/properties%n") + "Comparators used:%n") + "- for elements fields (by type): {Double -> DoubleComparator[precision=1.0E-15], Float -> FloatComparator[precision=1.0E-6]}%n") + "- for elements (by type): {Double -> DoubleComparator[precision=1.0E-15], Float -> FloatComparator[precision=1.0E-6]}%n") + "but was not."))));
            return;
        }
        failBecauseExpectedAssertionErrorWasNotThrown();
    }

    @Test
    public void failed_isIn_assertion_using_recursive_field_by_field_element_comparator() {
        AtomicReferenceArray<AtomicReferenceArrayAssert_usingRecursiveFieldByFieldElementComparator_Test.Foo> array1 = AtomicReferenceArrayAssertBaseTest.atomicArrayOf(new AtomicReferenceArrayAssert_usingRecursiveFieldByFieldElementComparator_Test.Foo("id", new AtomicReferenceArrayAssert_usingRecursiveFieldByFieldElementComparator_Test.Bar(1)));
        AtomicReferenceArrayAssert_usingRecursiveFieldByFieldElementComparator_Test.Foo[] array2 = new AtomicReferenceArrayAssert_usingRecursiveFieldByFieldElementComparator_Test.Foo[]{ new AtomicReferenceArrayAssert_usingRecursiveFieldByFieldElementComparator_Test.Foo("id", new AtomicReferenceArrayAssert_usingRecursiveFieldByFieldElementComparator_Test.Bar(2)) };
        try {
            Assertions.assertThat(array1).usingRecursiveFieldByFieldElementComparator().isIn(new Object[]{ array2 });
        } catch (AssertionError e) {
            Assertions.assertThat(e).hasMessage(String.format(("%nExpecting:%n" + ((((((" <[Foo(id=id, bar=Bar(id=1))]>%n" + "to be in:%n") + " <[[Foo(id=id, bar=Bar(id=2))]]>%n") + "when comparing elements using recursive field/property by field/property comparator on all fields/properties%n") + "Comparators used:%n") + "- for elements fields (by type): {Double -> DoubleComparator[precision=1.0E-15], Float -> FloatComparator[precision=1.0E-6]}%n") + "- for elements (by type): {Double -> DoubleComparator[precision=1.0E-15], Float -> FloatComparator[precision=1.0E-6]}"))));
            return;
        }
        failBecauseExpectedAssertionErrorWasNotThrown();
    }

    @Test
    public void should_be_able_to_use_a_comparator_for_specified_fields_of_elements_when_using_recursive_field_by_field_element_comparator() {
        AtomicReferenceArrayAssert_usingRecursiveFieldByFieldElementComparator_Test.Foo actual = new AtomicReferenceArrayAssert_usingRecursiveFieldByFieldElementComparator_Test.Foo("1", new AtomicReferenceArrayAssert_usingRecursiveFieldByFieldElementComparator_Test.Bar(1));
        AtomicReferenceArrayAssert_usingRecursiveFieldByFieldElementComparator_Test.Foo other = new AtomicReferenceArrayAssert_usingRecursiveFieldByFieldElementComparator_Test.Foo("1", new AtomicReferenceArrayAssert_usingRecursiveFieldByFieldElementComparator_Test.Bar(2));
        final class AlwaysEqualIntegerComparator implements Comparator<Integer> {
            @Override
            public int compare(Integer o1, Integer o2) {
                return 0;
            }
        }
        Assertions.assertThat(AtomicReferenceArrayAssertBaseTest.atomicArrayOf(actual)).usingComparatorForElementFieldsWithNames(new AlwaysEqualIntegerComparator(), "bar.id").usingRecursiveFieldByFieldElementComparator().contains(other);
    }

    @Test
    public void comparators_for_element_field_names_should_have_precedence_over_comparators_for_element_field_types_when_using_recursive_field_by_field_element_comparator() {
        Comparator<String> comparator = ( o1, o2) -> o1.compareTo(o2);
        AtomicReferenceArrayAssert_usingRecursiveFieldByFieldElementComparator_Test.Foo actual = new AtomicReferenceArrayAssert_usingRecursiveFieldByFieldElementComparator_Test.Foo("1", new AtomicReferenceArrayAssert_usingRecursiveFieldByFieldElementComparator_Test.Bar(1));
        AtomicReferenceArrayAssert_usingRecursiveFieldByFieldElementComparator_Test.Foo other = new AtomicReferenceArrayAssert_usingRecursiveFieldByFieldElementComparator_Test.Foo("2", new AtomicReferenceArrayAssert_usingRecursiveFieldByFieldElementComparator_Test.Bar(1));
        Assertions.assertThat(AtomicReferenceArrayAssertBaseTest.atomicArrayOf(actual)).usingComparatorForElementFieldsWithNames(AlwaysEqualComparator.ALWAY_EQUALS_STRING, "id").usingComparatorForElementFieldsWithType(comparator, String.class).usingRecursiveFieldByFieldElementComparator().contains(other);
    }

    @Test
    public void should_be_able_to_use_a_comparator_for_element_fields_with_specified_type_when_using_recursive_field_by_field_element_comparator() {
        AtomicReferenceArrayAssert_usingRecursiveFieldByFieldElementComparator_Test.Foo actual = new AtomicReferenceArrayAssert_usingRecursiveFieldByFieldElementComparator_Test.Foo("1", new AtomicReferenceArrayAssert_usingRecursiveFieldByFieldElementComparator_Test.Bar(1));
        AtomicReferenceArrayAssert_usingRecursiveFieldByFieldElementComparator_Test.Foo other = new AtomicReferenceArrayAssert_usingRecursiveFieldByFieldElementComparator_Test.Foo("2", new AtomicReferenceArrayAssert_usingRecursiveFieldByFieldElementComparator_Test.Bar(1));
        Assertions.assertThat(AtomicReferenceArrayAssertBaseTest.atomicArrayOf(actual)).usingComparatorForElementFieldsWithType(AlwaysEqualComparator.ALWAY_EQUALS_STRING, String.class).usingRecursiveFieldByFieldElementComparator().contains(other);
    }

    public static class Foo {
        public String id;

        public AtomicReferenceArrayAssert_usingRecursiveFieldByFieldElementComparator_Test.Bar bar;

        public Foo(String id, AtomicReferenceArrayAssert_usingRecursiveFieldByFieldElementComparator_Test.Bar bar) {
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

