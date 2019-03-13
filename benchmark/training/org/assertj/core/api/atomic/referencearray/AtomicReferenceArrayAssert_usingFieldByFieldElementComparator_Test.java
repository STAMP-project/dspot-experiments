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
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReferenceArray;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.AtomicReferenceArrayAssertBaseTest;
import org.assertj.core.internal.ObjectArrays;
import org.assertj.core.test.AlwaysEqualComparator;
import org.assertj.core.test.Jedi;
import org.assertj.core.util.Arrays;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Test;


public class AtomicReferenceArrayAssert_usingFieldByFieldElementComparator_Test extends AtomicReferenceArrayAssertBaseTest {
    private ObjectArrays arraysBefore;

    @Test
    public void successful_isEqualTo_assertion_using_field_by_field_element_comparator() {
        AtomicReferenceArray<AtomicReferenceArrayAssert_usingFieldByFieldElementComparator_Test.Foo> array1 = AtomicReferenceArrayAssertBaseTest.atomicArrayOf(new AtomicReferenceArrayAssert_usingFieldByFieldElementComparator_Test.Foo("id", 1));
        AtomicReferenceArrayAssert_usingFieldByFieldElementComparator_Test.Foo[] array2 = Arrays.array(new AtomicReferenceArrayAssert_usingFieldByFieldElementComparator_Test.Foo("id", 1));
        Assertions.assertThat(array1).usingFieldByFieldElementComparator().isEqualTo(array2);
    }

    @Test
    public void successful_isIn_assertion_using_field_by_field_element_comparator() {
        AtomicReferenceArray<AtomicReferenceArrayAssert_usingFieldByFieldElementComparator_Test.Foo> array1 = AtomicReferenceArrayAssertBaseTest.atomicArrayOf(new AtomicReferenceArrayAssert_usingFieldByFieldElementComparator_Test.Foo("id", 1));
        AtomicReferenceArrayAssert_usingFieldByFieldElementComparator_Test.Foo[] array2 = Arrays.array(new AtomicReferenceArrayAssert_usingFieldByFieldElementComparator_Test.Foo("id", 1));
        Assertions.assertThat(array1).usingFieldByFieldElementComparator().isIn(array2, array2);
    }

    @Test
    public void successful_isEqualTo_assertion_using_field_by_field_element_comparator_with_heterogeneous_array() {
        AtomicReferenceArray<AtomicReferenceArrayAssert_usingFieldByFieldElementComparator_Test.Animal> array1 = new AtomicReferenceArray(Arrays.array(new AtomicReferenceArrayAssert_usingFieldByFieldElementComparator_Test.Bird("White"), new AtomicReferenceArrayAssert_usingFieldByFieldElementComparator_Test.Snake(15)));
        AtomicReferenceArrayAssert_usingFieldByFieldElementComparator_Test.Animal[] array2 = Arrays.array(new AtomicReferenceArrayAssert_usingFieldByFieldElementComparator_Test.Bird("White"), new AtomicReferenceArrayAssert_usingFieldByFieldElementComparator_Test.Snake(15));
        Assertions.assertThat(array1).usingFieldByFieldElementComparator().isEqualTo(array2);
    }

    @Test
    public void successful_contains_assertion_using_field_by_field_element_comparator_with_heterogeneous_array() {
        AtomicReferenceArray<AtomicReferenceArrayAssert_usingFieldByFieldElementComparator_Test.Animal> array1 = new AtomicReferenceArray(Arrays.array(new AtomicReferenceArrayAssert_usingFieldByFieldElementComparator_Test.Bird("White"), new AtomicReferenceArrayAssert_usingFieldByFieldElementComparator_Test.Snake(15)));
        Assertions.assertThat(array1).usingFieldByFieldElementComparator().contains(new AtomicReferenceArrayAssert_usingFieldByFieldElementComparator_Test.Snake(15), new AtomicReferenceArrayAssert_usingFieldByFieldElementComparator_Test.Bird("White")).contains(new AtomicReferenceArrayAssert_usingFieldByFieldElementComparator_Test.Bird("White"), new AtomicReferenceArrayAssert_usingFieldByFieldElementComparator_Test.Snake(15));
        Assertions.assertThat(array1).usingFieldByFieldElementComparator().containsOnly(new AtomicReferenceArrayAssert_usingFieldByFieldElementComparator_Test.Snake(15), new AtomicReferenceArrayAssert_usingFieldByFieldElementComparator_Test.Bird("White")).containsOnly(new AtomicReferenceArrayAssert_usingFieldByFieldElementComparator_Test.Bird("White"), new AtomicReferenceArrayAssert_usingFieldByFieldElementComparator_Test.Snake(15));
    }

    @Test
    public void successful_isIn_assertion_using_field_by_field_element_comparator_with_heterogeneous_array() {
        AtomicReferenceArray<AtomicReferenceArrayAssert_usingFieldByFieldElementComparator_Test.Animal> array1 = new AtomicReferenceArray(Arrays.array(new AtomicReferenceArrayAssert_usingFieldByFieldElementComparator_Test.Bird("White"), new AtomicReferenceArrayAssert_usingFieldByFieldElementComparator_Test.Snake(15)));
        AtomicReferenceArrayAssert_usingFieldByFieldElementComparator_Test.Animal[] array2 = Arrays.array(new AtomicReferenceArrayAssert_usingFieldByFieldElementComparator_Test.Bird("White"), new AtomicReferenceArrayAssert_usingFieldByFieldElementComparator_Test.Snake(15));
        Assertions.assertThat(array1).usingFieldByFieldElementComparator().isIn(array2, array2);
    }

    @Test
    public void successful_containsExactly_assertion_using_field_by_field_element_comparator_with_heterogeneous_array() {
        AtomicReferenceArrayAssert_usingFieldByFieldElementComparator_Test.Animal[] array = Arrays.array(new AtomicReferenceArrayAssert_usingFieldByFieldElementComparator_Test.Bird("White"), new AtomicReferenceArrayAssert_usingFieldByFieldElementComparator_Test.Snake(15));
        AtomicReferenceArray<AtomicReferenceArrayAssert_usingFieldByFieldElementComparator_Test.Animal> array1 = new AtomicReferenceArray<>(array);
        Assertions.assertThat(array1).usingFieldByFieldElementComparator().containsExactly(new AtomicReferenceArrayAssert_usingFieldByFieldElementComparator_Test.Bird("White"), new AtomicReferenceArrayAssert_usingFieldByFieldElementComparator_Test.Snake(15));
    }

    @Test
    public void successful_containsExactlyInAnyOrder_assertion_using_field_by_field_element_comparator_with_heterogeneous_array() {
        AtomicReferenceArrayAssert_usingFieldByFieldElementComparator_Test.Snake snake = new AtomicReferenceArrayAssert_usingFieldByFieldElementComparator_Test.Snake(15);
        AtomicReferenceArray<AtomicReferenceArrayAssert_usingFieldByFieldElementComparator_Test.Animal> array1 = new AtomicReferenceArray(Arrays.array(new AtomicReferenceArrayAssert_usingFieldByFieldElementComparator_Test.Bird("White"), snake, snake));
        Assertions.assertThat(array1).usingFieldByFieldElementComparator().containsExactlyInAnyOrder(new AtomicReferenceArrayAssert_usingFieldByFieldElementComparator_Test.Snake(15), new AtomicReferenceArrayAssert_usingFieldByFieldElementComparator_Test.Bird("White"), new AtomicReferenceArrayAssert_usingFieldByFieldElementComparator_Test.Snake(15));
    }

    @Test
    public void successful_containsExactlyInAnyOrderElementsOf_assertion_using_field_by_field_element_comparator_with_heterogeneous_array() {
        AtomicReferenceArrayAssert_usingFieldByFieldElementComparator_Test.Snake snake = new AtomicReferenceArrayAssert_usingFieldByFieldElementComparator_Test.Snake(15);
        AtomicReferenceArray<AtomicReferenceArrayAssert_usingFieldByFieldElementComparator_Test.Animal> array1 = new AtomicReferenceArray(Arrays.array(new AtomicReferenceArrayAssert_usingFieldByFieldElementComparator_Test.Bird("White"), snake, snake));
        Assertions.assertThat(array1).usingFieldByFieldElementComparator().containsExactlyInAnyOrderElementsOf(Lists.newArrayList(new AtomicReferenceArrayAssert_usingFieldByFieldElementComparator_Test.Snake(15), new AtomicReferenceArrayAssert_usingFieldByFieldElementComparator_Test.Bird("White"), new AtomicReferenceArrayAssert_usingFieldByFieldElementComparator_Test.Snake(15)));
    }

    @Test
    public void successful_containsOnly_assertion_using_field_by_field_element_comparator_with_unordered_array() {
        AtomicReferenceArrayAssert_usingFieldByFieldElementComparator_Test.Person goodObiwan = new AtomicReferenceArrayAssert_usingFieldByFieldElementComparator_Test.Person("Obi-Wan", "Kenobi", "good man");
        AtomicReferenceArrayAssert_usingFieldByFieldElementComparator_Test.Person badObiwan = new AtomicReferenceArrayAssert_usingFieldByFieldElementComparator_Test.Person("Obi-Wan", "Kenobi", "bad man");
        AtomicReferenceArrayAssert_usingFieldByFieldElementComparator_Test.Person[] list = Arrays.array(goodObiwan, badObiwan);
        Assertions.assertThat(list).usingFieldByFieldElementComparator().containsOnly(badObiwan, goodObiwan);
    }

    private class Person {
        private String first;

        private String last;

        private String info;

        public Person(String first, String last, String info) {
            this.first = first;
            this.last = last;
            this.info = info;
        }

        @Override
        public boolean equals(Object o) {
            if ((this) == o)
                return true;

            if ((o == null) || ((getClass()) != (o.getClass())))
                return false;

            AtomicReferenceArrayAssert_usingFieldByFieldElementComparator_Test.Person person = ((AtomicReferenceArrayAssert_usingFieldByFieldElementComparator_Test.Person) (o));
            return (Objects.equals(first, person.first)) && (Objects.equals(last, person.last));
        }

        @Override
        public int hashCode() {
            return Objects.hash(first, last);
        }

        @Override
        public String toString() {
            return String.format("Person{first='%s', last='%s', info='%s'}", first, last, info);
        }
    }

    @Test
    public void failed_isEqualTo_assertion_using_field_by_field_element_comparator() {
        AtomicReferenceArrayAssert_usingFieldByFieldElementComparator_Test.Foo[] array1 = Arrays.array(new AtomicReferenceArrayAssert_usingFieldByFieldElementComparator_Test.Foo("id", 1));
        AtomicReferenceArrayAssert_usingFieldByFieldElementComparator_Test.Foo[] array2 = Arrays.array(new AtomicReferenceArrayAssert_usingFieldByFieldElementComparator_Test.Foo("id", 2));
        try {
            Assertions.assertThat(array1).usingFieldByFieldElementComparator().isEqualTo(array2);
        } catch (AssertionError e) {
            Assertions.assertThat(e).hasMessage(String.format(("%nExpecting:%n" + (((((((" <[Foo(id=id, bar=1)]>%n" + "to be equal to:%n") + " <[Foo(id=id, bar=2)]>%n") + "when comparing elements using field/property by field/property comparator on all fields/properties%n") + "Comparators used:%n") + "- for elements fields (by type): {Double -> DoubleComparator[precision=1.0E-15], Float -> FloatComparator[precision=1.0E-6]}%n") + "- for elements (by type): {Double -> DoubleComparator[precision=1.0E-15], Float -> FloatComparator[precision=1.0E-6]}%n") + "but was not."))));
            return;
        }
        failBecauseExpectedAssertionErrorWasNotThrown();
    }

    @Test
    public void failed_isIn_assertion_using_field_by_field_element_comparator() {
        AtomicReferenceArray<AtomicReferenceArrayAssert_usingFieldByFieldElementComparator_Test.Foo> array1 = AtomicReferenceArrayAssertBaseTest.atomicArrayOf(new AtomicReferenceArrayAssert_usingFieldByFieldElementComparator_Test.Foo("id", 1));
        AtomicReferenceArrayAssert_usingFieldByFieldElementComparator_Test.Foo[] array2 = Arrays.array(new AtomicReferenceArrayAssert_usingFieldByFieldElementComparator_Test.Foo("id", 2));
        try {
            Assertions.assertThat(array1).usingFieldByFieldElementComparator().isIn(array2, array2);
        } catch (AssertionError e) {
            Assertions.assertThat(e).hasMessage(String.format(("%nExpecting:%n" + ((((((" <[Foo(id=id, bar=1)]>%n" + "to be in:%n") + " <[[Foo(id=id, bar=2)], [Foo(id=id, bar=2)]]>%n") + "when comparing elements using field/property by field/property comparator on all fields/properties%n") + "Comparators used:%n") + "- for elements fields (by type): {Double -> DoubleComparator[precision=1.0E-15], Float -> FloatComparator[precision=1.0E-6]}%n") + "- for elements (by type): {Double -> DoubleComparator[precision=1.0E-15], Float -> FloatComparator[precision=1.0E-6]}"))));
            return;
        }
        failBecauseExpectedAssertionErrorWasNotThrown();
    }

    @Test
    public void should_be_able_to_use_a_comparator_for_specified_fields_of_elements_when_using_field_by_field_element_comparator() {
        Jedi actual = new Jedi("Yoda", "green");
        Jedi other = new Jedi("Luke", "green");
        Assertions.assertThat(AtomicReferenceArrayAssertBaseTest.atomicArrayOf(actual)).usingComparatorForElementFieldsWithNames(AlwaysEqualComparator.ALWAY_EQUALS_STRING, "name").usingFieldByFieldElementComparator().contains(other);
    }

    @Test
    public void comparators_for_element_field_names_should_have_precedence_over_comparators_for_element_field_types_when_using_field_by_field_element_comparator() {
        Comparator<String> comparator = ( o1, o2) -> o1.compareTo(o2);
        Jedi actual = new Jedi("Yoda", "green");
        Jedi other = new Jedi("Luke", "green");
        Assertions.assertThat(AtomicReferenceArrayAssertBaseTest.atomicArrayOf(actual)).usingComparatorForElementFieldsWithNames(AlwaysEqualComparator.ALWAY_EQUALS_STRING, "name").usingComparatorForElementFieldsWithType(comparator, String.class).usingFieldByFieldElementComparator().contains(other);
    }

    @Test
    public void should_be_able_to_use_a_comparator_for_element_fields_with_specified_type_when_using_field_by_field_element_comparator() {
        Jedi actual = new Jedi("Yoda", "green");
        Jedi other = new Jedi("Luke", "blue");
        Assertions.assertThat(AtomicReferenceArrayAssertBaseTest.atomicArrayOf(actual)).usingComparatorForElementFieldsWithType(AlwaysEqualComparator.ALWAY_EQUALS_STRING, String.class).usingFieldByFieldElementComparator().contains(other);
    }

    public static class Foo {
        public final String id;

        public final int bar;

        public Foo(final String id, final int bar) {
            this.id = id;
            this.bar = bar;
        }

        @Override
        public String toString() {
            return ((("Foo(id=" + (id)) + ", bar=") + (bar)) + ")";
        }
    }

    private static class Animal {
        private final String name;

        private Animal(String name) {
            this.name = name;
        }

        @SuppressWarnings("unused")
        public String getName() {
            return name;
        }
    }

    private static class Bird extends AtomicReferenceArrayAssert_usingFieldByFieldElementComparator_Test.Animal {
        private final String color;

        private Bird(String color) {
            super("Bird");
            this.color = color;
        }

        @SuppressWarnings("unused")
        public String getColor() {
            return color;
        }
    }

    private static class Snake extends AtomicReferenceArrayAssert_usingFieldByFieldElementComparator_Test.Animal {
        private final int length;

        private Snake(int length) {
            super("Snake");
            this.length = length;
        }

        @SuppressWarnings("unused")
        public int getLength() {
            return length;
        }
    }
}

