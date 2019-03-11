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


import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.RecursiveComparisonAssert_isEqualTo_BaseTest;
import org.assertj.core.internal.objects.data.Address;
import org.assertj.core.internal.objects.data.AlwaysEqualAddress;
import org.assertj.core.internal.objects.data.AlwaysEqualPerson;
import org.assertj.core.internal.objects.data.Home;
import org.assertj.core.internal.objects.data.Person;
import org.junit.jupiter.api.Test;


public class RecursiveComparisonAssert_isEqualTo_ignoringOverriddenEquals_Test extends RecursiveComparisonAssert_isEqualTo_BaseTest {
    @Test
    public void should_fail_when_actual_differs_from_expected_as_some_overridden_equals_methods_are_ignored_by_regexes() {
        // GIVEN
        Person actual = new Person();
        actual.neighbour = new AlwaysEqualPerson();
        actual.neighbour.name = "Jack";
        actual.neighbour.home.address = new AlwaysEqualAddress();
        actual.neighbour.home.address.number = 123;
        Person expected = new Person();
        expected.neighbour = new AlwaysEqualPerson();
        expected.neighbour.name = "Jim";
        expected.neighbour.home.address = new AlwaysEqualAddress();
        expected.neighbour.home.address.number = 234;
        recursiveComparisonConfiguration.ignoreOverriddenEqualsForFieldsMatchingRegexes(".*AlwaysEqualPerson", ".*AlwaysEqualAddress");
        // WHEN
        compareRecursivelyFailsAsExpected(actual, expected);
        // THEN
        ComparisonDifference neighbourNameDifference = RecursiveComparisonAssert_isEqualTo_BaseTest.diff("neighbour.name", actual.neighbour.name, expected.neighbour.name);
        ComparisonDifference numberDifference = RecursiveComparisonAssert_isEqualTo_BaseTest.diff("neighbour.home.address.number", actual.neighbour.home.address.number, expected.neighbour.home.address.number);
        verifyShouldBeEqualByComparingFieldByFieldRecursivelyCall(actual, expected, numberDifference, neighbourNameDifference);
    }

    @Test
    public void ignoring_overriden_equals_with_regexes_does_not_replace_previous_regexes() {
        // WHEN
        recursiveComparisonConfiguration.ignoreOverriddenEqualsForFieldsMatchingRegexes("foo");
        recursiveComparisonConfiguration.ignoreOverriddenEqualsForFieldsMatchingRegexes("bar", "baz");
        // THEN
        List<Pattern> ignoredOverriddenEqualsRegexes = recursiveComparisonConfiguration.getIgnoredOverriddenEqualsRegexes();
        Assertions.assertThat(ignoredOverriddenEqualsRegexes).extracting(Pattern::pattern).containsExactlyInAnyOrder("foo", "bar", "baz");
    }

    @Test
    public void should_fail_when_actual_differs_from_expected_as_some_overridden_equals_methods_are_ignored_by_types() {
        // GIVEN
        Person actual = new Person();
        actual.neighbour = new AlwaysEqualPerson();
        actual.neighbour.name = "Jack";
        actual.neighbour.home.address = new AlwaysEqualAddress();
        actual.neighbour.home.address.number = 123;
        Person expected = new Person();
        expected.neighbour = new AlwaysEqualPerson();
        expected.neighbour.name = "Jim";
        expected.neighbour.home.address = new AlwaysEqualAddress();
        expected.neighbour.home.address.number = 234;
        recursiveComparisonConfiguration.ignoreOverriddenEqualsForTypes(AlwaysEqualPerson.class, AlwaysEqualAddress.class);
        // WHEN
        compareRecursivelyFailsAsExpected(actual, expected);
        // THEN
        ComparisonDifference neighbourNameDifference = RecursiveComparisonAssert_isEqualTo_BaseTest.diff("neighbour.name", actual.neighbour.name, expected.neighbour.name);
        ComparisonDifference numberDifference = RecursiveComparisonAssert_isEqualTo_BaseTest.diff("neighbour.home.address.number", actual.neighbour.home.address.number, expected.neighbour.home.address.number);
        verifyShouldBeEqualByComparingFieldByFieldRecursivelyCall(actual, expected, numberDifference, neighbourNameDifference);
    }

    @Test
    public void ignoring_overriden_equals_by_types_does_not_replace_previous_types() {
        // WHEN
        recursiveComparisonConfiguration.ignoreOverriddenEqualsForTypes(String.class);
        recursiveComparisonConfiguration.ignoreOverriddenEqualsForTypes(Date.class);
        // THEN
        Assertions.assertThat(recursiveComparisonConfiguration.getIgnoredOverriddenEqualsForTypes()).containsExactly(String.class, Date.class);
    }

    @Test
    public void should_fail_when_actual_differs_from_expected_as_some_overridden_equals_methods_are_ignored_by_fields() {
        // GIVEN
        Person actual = new Person();
        actual.neighbour = new AlwaysEqualPerson();
        actual.neighbour.name = "Jack";
        actual.neighbour.home.address = new AlwaysEqualAddress();
        actual.neighbour.home.address.number = 123;
        Person expected = new Person();
        expected.neighbour = new AlwaysEqualPerson();
        expected.neighbour.name = "Jim";
        expected.neighbour.home.address = new AlwaysEqualAddress();
        expected.neighbour.home.address.number = 234;
        recursiveComparisonConfiguration.ignoreOverriddenEqualsForFields("neighbour", "neighbour.home.address");
        // WHEN
        compareRecursivelyFailsAsExpected(actual, expected);
        // THEN
        ComparisonDifference neighbourNameDifference = RecursiveComparisonAssert_isEqualTo_BaseTest.diff("neighbour.name", actual.neighbour.name, expected.neighbour.name);
        ComparisonDifference numberDifference = RecursiveComparisonAssert_isEqualTo_BaseTest.diff("neighbour.home.address.number", actual.neighbour.home.address.number, expected.neighbour.home.address.number);
        verifyShouldBeEqualByComparingFieldByFieldRecursivelyCall(actual, expected, numberDifference, neighbourNameDifference);
    }

    @Test
    public void overridden_equals_is_not_used_on_the_object_under_test_itself() {
        // GIVEN
        AlwaysEqualPerson actual = new AlwaysEqualPerson();
        actual.name = "John";
        AlwaysEqualPerson expected = new AlwaysEqualPerson();
        expected.name = "Jack";
        // THEN
        // would have succeeded if we had used AlwaysEqualPerson equals method
        compareRecursivelyFailsAsExpected(actual, expected);
    }

    @Test
    public void ignoring_overriden_equals_for_fields_does_not_replace_previous_fields() {
        // WHEN
        recursiveComparisonConfiguration.ignoreOverriddenEqualsForFields("foo");
        recursiveComparisonConfiguration.ignoreOverriddenEqualsForFields("bar", "baz");
        // THEN
        List<FieldLocation> ignoredOverriddenEqualsFields = recursiveComparisonConfiguration.getIgnoredOverriddenEqualsForFields();
        Assertions.assertThat(ignoredOverriddenEqualsFields).containsExactly(FieldLocation.fielLocation("foo"), FieldLocation.fielLocation("bar"), FieldLocation.fielLocation("baz"));
    }
}

