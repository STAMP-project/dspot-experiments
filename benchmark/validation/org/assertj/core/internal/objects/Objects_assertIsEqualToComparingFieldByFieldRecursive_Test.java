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
package org.assertj.core.internal.objects;


import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;
import org.assertj.core.api.AssertionInfo;
import org.assertj.core.api.Assertions;
import org.assertj.core.error.ShouldBeEqualByComparingFieldByFieldRecursively;
import org.assertj.core.internal.AtPrecisionComparator;
import org.assertj.core.internal.DeepDifference.Difference;
import org.assertj.core.internal.ObjectsBaseTest;
import org.assertj.core.internal.TypeComparators;
import org.assertj.core.test.AlwaysEqualComparator;
import org.assertj.core.test.NeverEqualComparator;
import org.assertj.core.test.Patient;
import org.assertj.core.test.TestData;
import org.assertj.core.test.TestFailures;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


public class Objects_assertIsEqualToComparingFieldByFieldRecursive_Test extends ObjectsBaseTest {
    @Test
    public void should_be_able_to_compare_objects_recursively() {
        Objects_assertIsEqualToComparingFieldByFieldRecursive_Test.Person actual = new Objects_assertIsEqualToComparingFieldByFieldRecursive_Test.Person();
        actual.name = "John";
        actual.home.address.number = 1;
        Objects_assertIsEqualToComparingFieldByFieldRecursive_Test.Person other = new Objects_assertIsEqualToComparingFieldByFieldRecursive_Test.Person();
        other.name = "John";
        other.home.address.number = 1;
        objects.assertIsEqualToComparingFieldByFieldRecursively(TestData.someInfo(), actual, other, ObjectsBaseTest.noFieldComparators(), TypeComparators.defaultTypeComparators());
    }

    @Test
    public void should_be_able_to_compare_objects_of_different_types_recursively() {
        Objects_assertIsEqualToComparingFieldByFieldRecursive_Test.Person actual = new Objects_assertIsEqualToComparingFieldByFieldRecursive_Test.Person();
        actual.name = "John";
        actual.home.address.number = 1;
        Objects_assertIsEqualToComparingFieldByFieldRecursive_Test.Human other = new Objects_assertIsEqualToComparingFieldByFieldRecursive_Test.Human();
        other.name = "John";
        other.home.address.number = 1;
        objects.assertIsEqualToComparingFieldByFieldRecursively(TestData.someInfo(), actual, other, ObjectsBaseTest.noFieldComparators(), TypeComparators.defaultTypeComparators());
    }

    @Test
    public void should_be_able_to_compare_objects_recursively_using_some_precision_for_numerical_types() {
        Objects_assertIsEqualToComparingFieldByFieldRecursive_Test.Giant goliath = new Objects_assertIsEqualToComparingFieldByFieldRecursive_Test.Giant();
        goliath.name = "Goliath";
        goliath.height = 3.0;
        Objects_assertIsEqualToComparingFieldByFieldRecursive_Test.Giant goliathTwin = new Objects_assertIsEqualToComparingFieldByFieldRecursive_Test.Giant();
        goliathTwin.name = "Goliath";
        goliathTwin.height = 3.1;
        Assertions.assertThat(goliath).usingComparatorForType(new AtPrecisionComparator(0.2), Double.class).isEqualToComparingFieldByFieldRecursively(goliathTwin);
    }

    @Test
    public void should_be_able_to_compare_objects_recursively_using_given_comparator_for_specified_field() {
        Objects_assertIsEqualToComparingFieldByFieldRecursive_Test.Giant goliath = new Objects_assertIsEqualToComparingFieldByFieldRecursive_Test.Giant();
        goliath.name = "Goliath";
        goliath.height = 3.0;
        Objects_assertIsEqualToComparingFieldByFieldRecursive_Test.Giant goliathTwin = new Objects_assertIsEqualToComparingFieldByFieldRecursive_Test.Giant();
        goliathTwin.name = "Goliath";
        goliathTwin.height = 3.1;
        Assertions.assertThat(goliath).usingComparatorForFields(new AtPrecisionComparator(0.2), "height").isEqualToComparingFieldByFieldRecursively(goliathTwin);
    }

    @Test
    public void should_be_able_to_compare_objects_recursively_using_given_comparator_for_specified_nested_field() {
        Objects_assertIsEqualToComparingFieldByFieldRecursive_Test.Giant goliath = new Objects_assertIsEqualToComparingFieldByFieldRecursive_Test.Giant();
        goliath.name = "Goliath";
        goliath.height = 3.0;
        goliath.home.address.number = 1;
        Objects_assertIsEqualToComparingFieldByFieldRecursive_Test.Giant goliathTwin = new Objects_assertIsEqualToComparingFieldByFieldRecursive_Test.Giant();
        goliathTwin.name = "Goliath";
        goliathTwin.height = 3.1;
        goliathTwin.home.address.number = 5;
        Assertions.assertThat(goliath).usingComparatorForFields(new AtPrecisionComparator(0.2), "height").usingComparatorForFields(new AtPrecisionComparator(10), "home.address.number").isEqualToComparingFieldByFieldRecursively(goliathTwin);
    }

    @Test
    public void should_be_able_to_compare_objects_with_cycles_recursively() {
        Objects_assertIsEqualToComparingFieldByFieldRecursive_Test.FriendlyPerson actual = new Objects_assertIsEqualToComparingFieldByFieldRecursive_Test.FriendlyPerson();
        actual.name = "John";
        actual.home.address.number = 1;
        Objects_assertIsEqualToComparingFieldByFieldRecursive_Test.FriendlyPerson other = new Objects_assertIsEqualToComparingFieldByFieldRecursive_Test.FriendlyPerson();
        other.name = "John";
        other.home.address.number = 1;
        // neighbour
        other.neighbour = actual;
        actual.neighbour = other;
        // friends
        Objects_assertIsEqualToComparingFieldByFieldRecursive_Test.FriendlyPerson sherlock = new Objects_assertIsEqualToComparingFieldByFieldRecursive_Test.FriendlyPerson();
        sherlock.name = "Sherlock";
        sherlock.home.address.number = 221;
        actual.friends.add(sherlock);
        actual.friends.add(other);
        other.friends.add(sherlock);
        other.friends.add(actual);
        objects.assertIsEqualToComparingFieldByFieldRecursively(TestData.someInfo(), actual, other, ObjectsBaseTest.noFieldComparators(), TypeComparators.defaultTypeComparators());
    }

    @Test
    public void should_fail_when_fields_differ() {
        AssertionInfo info = TestData.someInfo();
        Objects_assertIsEqualToComparingFieldByFieldRecursive_Test.Person actual = new Objects_assertIsEqualToComparingFieldByFieldRecursive_Test.Person();
        actual.name = "John";
        Objects_assertIsEqualToComparingFieldByFieldRecursive_Test.Person other = new Objects_assertIsEqualToComparingFieldByFieldRecursive_Test.Person();
        other.name = "Jack";
        try {
            objects.assertIsEqualToComparingFieldByFieldRecursively(info, actual, other, ObjectsBaseTest.noFieldComparators(), TypeComparators.defaultTypeComparators());
        } catch (AssertionError err) {
            Mockito.verify(failures).failure(info, ShouldBeEqualByComparingFieldByFieldRecursively.shouldBeEqualByComparingFieldByFieldRecursive(actual, other, Arrays.asList(new Difference(Arrays.asList("name"), "John", "Jack")), CONFIGURATION_PROVIDER.representation()));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }

    @Test
    public void should_fail_when_fields_of_child_objects_differ() {
        AssertionInfo info = TestData.someInfo();
        Objects_assertIsEqualToComparingFieldByFieldRecursive_Test.Person actual = new Objects_assertIsEqualToComparingFieldByFieldRecursive_Test.Person();
        actual.name = "John";
        actual.home.address.number = 1;
        Objects_assertIsEqualToComparingFieldByFieldRecursive_Test.Person other = new Objects_assertIsEqualToComparingFieldByFieldRecursive_Test.Person();
        other.name = "John";
        other.home.address.number = 2;
        try {
            objects.assertIsEqualToComparingFieldByFieldRecursively(info, actual, other, ObjectsBaseTest.noFieldComparators(), TypeComparators.defaultTypeComparators());
        } catch (AssertionError err) {
            Mockito.verify(failures).failure(info, ShouldBeEqualByComparingFieldByFieldRecursively.shouldBeEqualByComparingFieldByFieldRecursive(actual, other, Arrays.asList(new Difference(Arrays.asList("home.address.number"), 1, 2)), CONFIGURATION_PROVIDER.representation()));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }

    @Test
    public void should_have_error_message_with_differences_and_path_to_differences() {
        Objects_assertIsEqualToComparingFieldByFieldRecursive_Test.Person actual = new Objects_assertIsEqualToComparingFieldByFieldRecursive_Test.Person();
        actual.name = "Jack";
        actual.home.address.number = 1;
        Objects_assertIsEqualToComparingFieldByFieldRecursive_Test.Person other = new Objects_assertIsEqualToComparingFieldByFieldRecursive_Test.Person();
        other.name = "John";
        other.home.address.number = 2;
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> assertThat(actual).isEqualToComparingFieldByFieldRecursively(other)).withMessage(String.format(("%nExpecting:%n  <%s>%nto be equal to:%n  <%s>%n" + (((((("when recursively comparing field by field, but found the following difference(s):%n%n" + "Path to difference: <home.address.number>%n") + "- actual  : <1>%n") + "- expected: <2>%n%n") + "Path to difference: <name>%n") + "- actual  : <\"Jack\">%n") + "- expected: <\"John\">")), actual, other));
    }

    @Test
    public void should_have_error_message_with_path_to_difference_when_difference_is_in_collection() {
        Objects_assertIsEqualToComparingFieldByFieldRecursive_Test.FriendlyPerson actual = new Objects_assertIsEqualToComparingFieldByFieldRecursive_Test.FriendlyPerson();
        Objects_assertIsEqualToComparingFieldByFieldRecursive_Test.FriendlyPerson friendOfActual = new Objects_assertIsEqualToComparingFieldByFieldRecursive_Test.FriendlyPerson();
        friendOfActual.home.address.number = 99;
        actual.friends = Arrays.asList(friendOfActual);
        Objects_assertIsEqualToComparingFieldByFieldRecursive_Test.FriendlyPerson other = new Objects_assertIsEqualToComparingFieldByFieldRecursive_Test.FriendlyPerson();
        Objects_assertIsEqualToComparingFieldByFieldRecursive_Test.FriendlyPerson friendOfOther = new Objects_assertIsEqualToComparingFieldByFieldRecursive_Test.FriendlyPerson();
        friendOfOther.home.address.number = 10;
        other.friends = Arrays.asList(friendOfOther);
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> assertThat(actual).isEqualToComparingFieldByFieldRecursively(other)).withMessage(String.format(("%nExpecting:%n  <%s>%nto be equal to:%n  <%s>%n" + ((("when recursively comparing field by field, but found the following difference(s):%n%n" + "Path to difference: <friends.home.address.number>%n") + "- actual  : <99>%n") + "- expected: <10>")), actual, other));
    }

    @Test
    public void should_not_use_equal_implementation_of_objects_to_compare() {
        AssertionInfo info = TestData.someInfo();
        Objects_assertIsEqualToComparingFieldByFieldRecursive_Test.EqualPerson actual = new Objects_assertIsEqualToComparingFieldByFieldRecursive_Test.EqualPerson();
        actual.name = "John";
        actual.home.address.number = 1;
        Objects_assertIsEqualToComparingFieldByFieldRecursive_Test.EqualPerson other = new Objects_assertIsEqualToComparingFieldByFieldRecursive_Test.EqualPerson();
        other.name = "John";
        other.home.address.number = 2;
        try {
            objects.assertIsEqualToComparingFieldByFieldRecursively(info, actual, other, ObjectsBaseTest.noFieldComparators(), TypeComparators.defaultTypeComparators());
        } catch (AssertionError err) {
            Mockito.verify(failures).failure(info, ShouldBeEqualByComparingFieldByFieldRecursively.shouldBeEqualByComparingFieldByFieldRecursive(actual, other, Arrays.asList(new Difference(Arrays.asList("home.address.number"), 1, 2)), CONFIGURATION_PROVIDER.representation()));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }

    @Test
    public void should_fail_when_comparing_unsorted_with_sorted_set() {
        Objects_assertIsEqualToComparingFieldByFieldRecursive_Test.WithCollection<String> actual = new Objects_assertIsEqualToComparingFieldByFieldRecursive_Test.WithCollection<>(new LinkedHashSet<String>());
        actual.collection.add("bar");
        actual.collection.add("foo");
        Objects_assertIsEqualToComparingFieldByFieldRecursive_Test.WithCollection<String> expected = new Objects_assertIsEqualToComparingFieldByFieldRecursive_Test.WithCollection<>(new TreeSet<String>());
        expected.collection.add("bar");
        expected.collection.add("foo");
        try {
            Assertions.assertThat(actual).isEqualToComparingFieldByFieldRecursively(expected);
        } catch (AssertionError err) {
            Assertions.assertThat(err).hasMessageContaining(String.format("Path to difference: <collection>%n"));
            Assertions.assertThat(err).hasMessageContaining(String.format("- actual  : <[\"bar\", \"foo\"] (LinkedHashSet@"));
            Assertions.assertThat(err).hasMessageContaining(String.format("- expected: <[\"bar\", \"foo\"] (TreeSet@"));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }

    @Test
    public void should_fail_when_comparing_sorted_with_unsorted_set() {
        Objects_assertIsEqualToComparingFieldByFieldRecursive_Test.WithCollection<String> actual = new Objects_assertIsEqualToComparingFieldByFieldRecursive_Test.WithCollection<>(new TreeSet<String>());
        actual.collection.add("bar");
        actual.collection.add("foo");
        Objects_assertIsEqualToComparingFieldByFieldRecursive_Test.WithCollection<String> expected = new Objects_assertIsEqualToComparingFieldByFieldRecursive_Test.WithCollection<>(new LinkedHashSet<String>());
        expected.collection.add("bar");
        expected.collection.add("foo");
        try {
            Assertions.assertThat(actual).isEqualToComparingFieldByFieldRecursively(expected);
        } catch (AssertionError err) {
            Assertions.assertThat(err).hasMessageContaining(String.format("Path to difference: <collection>%n"));
            Assertions.assertThat(err).hasMessageContaining(String.format("- actual  : <[\"bar\", \"foo\"] (TreeSet@"));
            Assertions.assertThat(err).hasMessageContaining(String.format("- expected: <[\"bar\", \"foo\"] (LinkedHashSet@"));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }

    @Test
    public void should_fail_when_comparing_unsorted_with_sorted_map() {
        Objects_assertIsEqualToComparingFieldByFieldRecursive_Test.WithMap<Long, Boolean> actual = new Objects_assertIsEqualToComparingFieldByFieldRecursive_Test.WithMap<>(new LinkedHashMap<>());
        actual.map.put(1L, true);
        actual.map.put(2L, false);
        Objects_assertIsEqualToComparingFieldByFieldRecursive_Test.WithMap<Long, Boolean> expected = new Objects_assertIsEqualToComparingFieldByFieldRecursive_Test.WithMap<>(new TreeMap<>());
        expected.map.put(2L, false);
        expected.map.put(1L, true);
        try {
            Assertions.assertThat(actual).isEqualToComparingFieldByFieldRecursively(expected);
        } catch (AssertionError err) {
            Assertions.assertThat(err).hasMessageContaining(String.format("Path to difference: <map>%n"));
            Assertions.assertThat(err).hasMessageContaining(String.format("- actual  : <{1L=true, 2L=false} (LinkedHashMap@"));
            Assertions.assertThat(err).hasMessageContaining(String.format("- expected: <{1L=true, 2L=false} (TreeMap@"));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }

    @Test
    public void should_fail_when_comparing_sorted_with_unsorted_map() {
        Objects_assertIsEqualToComparingFieldByFieldRecursive_Test.WithMap<Long, Boolean> actual = new Objects_assertIsEqualToComparingFieldByFieldRecursive_Test.WithMap<>(new TreeMap<Long, Boolean>());
        actual.map.put(1L, true);
        actual.map.put(2L, false);
        Objects_assertIsEqualToComparingFieldByFieldRecursive_Test.WithMap<Long, Boolean> expected = new Objects_assertIsEqualToComparingFieldByFieldRecursive_Test.WithMap<>(new LinkedHashMap<Long, Boolean>());
        expected.map.put(2L, false);
        expected.map.put(1L, true);
        try {
            Assertions.assertThat(actual).isEqualToComparingFieldByFieldRecursively(expected);
        } catch (AssertionError err) {
            Assertions.assertThat(err).hasMessageContaining(String.format("Path to difference: <map>%n"));
            Assertions.assertThat(err).hasMessageContaining(String.format("- actual  : <{1L=true, 2L=false} (TreeMap@"));
            Assertions.assertThat(err).hasMessageContaining(String.format("- expected: <{1L=true, 2L=false} (LinkedHashMap@"));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }

    @Test
    public void should_handle_null_field_with_field_comparator() {
        // GIVEN
        Patient adam = new Patient(null);
        Patient eve = new Patient(new Timestamp(3L));
        // THEN
        Assertions.assertThat(adam).usingComparatorForFields(AlwaysEqualComparator.ALWAY_EQUALS, "dateOfBirth", "health").isEqualToComparingFieldByFieldRecursively(eve);
    }

    @Test
    public void should_handle_null_field_with_type_comparator() {
        // GIVEN
        Patient adam = new Patient(null);
        Patient eve = new Patient(new Timestamp(3L));
        // THEN
        Assertions.assertThat(adam).usingComparatorForType(AlwaysEqualComparator.ALWAY_EQUALS_TIMESTAMP, Timestamp.class).isEqualToComparingFieldByFieldRecursively(eve);
    }

    @Test
    public void should_not_bother_with_comparators_when_fields_are_the_same() {
        // GIVEN
        Timestamp dateOfBirth = new Timestamp(3L);
        Patient adam = new Patient(dateOfBirth);
        Patient eve = new Patient(dateOfBirth);
        // THEN
        Assertions.assertThat(adam).usingComparatorForFields(NeverEqualComparator.NEVER_EQUALS, "dateOfBirth").isEqualToComparingFieldByFieldRecursively(eve);
    }

    @Test
    public void should_treat_date_as_equal_to_timestamp() {
        Objects_assertIsEqualToComparingFieldByFieldRecursive_Test.Person actual = new Objects_assertIsEqualToComparingFieldByFieldRecursive_Test.Person();
        actual.name = "Fred";
        actual.dateOfBirth = new Date(1000L);
        Objects_assertIsEqualToComparingFieldByFieldRecursive_Test.Person other = new Objects_assertIsEqualToComparingFieldByFieldRecursive_Test.Person();
        other.name = "Fred";
        other.dateOfBirth = new Timestamp(1000L);
        objects.assertIsEqualToComparingFieldByFieldRecursively(TestData.someInfo(), actual, other, ObjectsBaseTest.noFieldComparators(), TypeComparators.defaultTypeComparators());
    }

    @Test
    public void should_treat_timestamp_as_equal_to_date_when_registering_a_Date_symmetric_comparator() {
        Objects_assertIsEqualToComparingFieldByFieldRecursive_Test.Person actual = new Objects_assertIsEqualToComparingFieldByFieldRecursive_Test.Person();
        actual.name = "Fred";
        actual.dateOfBirth = new Timestamp(1000L);
        Objects_assertIsEqualToComparingFieldByFieldRecursive_Test.Person other = new Objects_assertIsEqualToComparingFieldByFieldRecursive_Test.Person();
        other.name = "Fred";
        other.dateOfBirth = new Date(1000L);
        TypeComparators typeComparators = new TypeComparators();
        typeComparators.put(Timestamp.class, SymmetricDateComparator.SYMMETRIC_DATE_COMPARATOR);
        objects.assertIsEqualToComparingFieldByFieldRecursively(TestData.someInfo(), actual, other, ObjectsBaseTest.noFieldComparators(), typeComparators);
        objects.assertIsEqualToComparingFieldByFieldRecursively(TestData.someInfo(), other, actual, ObjectsBaseTest.noFieldComparators(), typeComparators);
    }

    @Test
    public void should_treat_timestamp_as_equal_to_date_when_registering_a_Date_symmetric_comparator_for_field() {
        Objects_assertIsEqualToComparingFieldByFieldRecursive_Test.Person actual = new Objects_assertIsEqualToComparingFieldByFieldRecursive_Test.Person();
        actual.name = "Fred";
        actual.dateOfBirth = new Timestamp(1000L);
        Objects_assertIsEqualToComparingFieldByFieldRecursive_Test.Person other = new Objects_assertIsEqualToComparingFieldByFieldRecursive_Test.Person();
        other.name = "Fred";
        other.dateOfBirth = new Date(1000L);
        Map<String, Comparator<?>> fieldComparators = new HashMap<>();
        fieldComparators.put("dateOfBirth", SymmetricDateComparator.SYMMETRIC_DATE_COMPARATOR);
        objects.assertIsEqualToComparingFieldByFieldRecursively(TestData.someInfo(), actual, other, fieldComparators, TypeComparators.defaultTypeComparators());
    }

    @Test
    public void should_be_able_to_compare_objects_with_percentages() {
        Objects_assertIsEqualToComparingFieldByFieldRecursive_Test.Person actual = new Objects_assertIsEqualToComparingFieldByFieldRecursive_Test.Person();
        actual.name = "foo";
        Objects_assertIsEqualToComparingFieldByFieldRecursive_Test.Person other = new Objects_assertIsEqualToComparingFieldByFieldRecursive_Test.Person();
        other.name = "%foo";
        try {
            objects.assertIsEqualToComparingFieldByFieldRecursively(TestData.someInfo(), actual, other, ObjectsBaseTest.noFieldComparators(), TypeComparators.defaultTypeComparators());
        } catch (AssertionError err) {
            Assertions.assertThat(err).hasMessageContaining("Path to difference: <name>").hasMessageContaining("- expected: <\"%foo\">").hasMessageContaining("- actual  : <\"foo\">");
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }

    @Test
    public void should_report_missing_property() {
        // GIVEN
        Objects_assertIsEqualToComparingFieldByFieldRecursive_Test.Human joe = new Objects_assertIsEqualToComparingFieldByFieldRecursive_Test.Human();
        joe.name = "joe";
        Objects_assertIsEqualToComparingFieldByFieldRecursive_Test.Giant goliath = new Objects_assertIsEqualToComparingFieldByFieldRecursive_Test.Giant();
        goliath.name = "joe";
        goliath.height = 3.0;
        // WHEN
        Throwable error = Assertions.catchThrowable(() -> assertThat(goliath).isEqualToComparingFieldByFieldRecursively(joe));
        // THEN
        Assertions.assertThat(error).hasMessageContaining("Human does not declare all Giant fields").hasMessageContaining("[height]");
    }

    public static class WithMap<K, V> {
        public Map<K, V> map;

        public WithMap(Map<K, V> map) {
            this.map = map;
        }

        @Override
        public String toString() {
            return String.format("WithMap [map=%s]", map);
        }
    }

    public static class WithCollection<E> {
        public Collection<E> collection;

        public WithCollection(Collection<E> collection) {
            this.collection = collection;
        }

        @Override
        public String toString() {
            return String.format("WithCollection [collection=%s]", collection);
        }
    }

    public static class Person {
        public Date dateOfBirth;

        public String name;

        public Objects_assertIsEqualToComparingFieldByFieldRecursive_Test.Home home = new Objects_assertIsEqualToComparingFieldByFieldRecursive_Test.Home();

        public Objects_assertIsEqualToComparingFieldByFieldRecursive_Test.Person neighbour;

        @Override
        public String toString() {
            return ((("Person [name=" + (name)) + ", home=") + (home)) + "]";
        }
    }

    public static class Home {
        public Objects_assertIsEqualToComparingFieldByFieldRecursive_Test.Address address = new Objects_assertIsEqualToComparingFieldByFieldRecursive_Test.Address();

        @Override
        public String toString() {
            return ("Home [address=" + (address)) + "]";
        }
    }

    public static class Address {
        public int number = 1;

        @Override
        public String toString() {
            return ("Address [number=" + (number)) + "]";
        }
    }

    public static class Human extends Objects_assertIsEqualToComparingFieldByFieldRecursive_Test.Person {}

    public static class Giant extends Objects_assertIsEqualToComparingFieldByFieldRecursive_Test.Person {
        public double height = 3.0;

        @Override
        public String toString() {
            return (((((("Giant [name=" + (name)) + ", home=") + (home)) + ", ") + "height ") + (height)) + "]";
        }
    }

    public static class EqualPerson extends Objects_assertIsEqualToComparingFieldByFieldRecursive_Test.Person {
        @Override
        public boolean equals(Object o) {
            return true;
        }
    }

    public static class FriendlyPerson extends Objects_assertIsEqualToComparingFieldByFieldRecursive_Test.Person {
        public List<Objects_assertIsEqualToComparingFieldByFieldRecursive_Test.FriendlyPerson> friends = new ArrayList<>();
    }
}

