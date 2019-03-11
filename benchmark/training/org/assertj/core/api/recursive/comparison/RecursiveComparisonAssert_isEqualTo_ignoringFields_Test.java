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
import org.assertj.core.api.RecursiveComparisonAssert_isEqualTo_BaseTest;
import org.assertj.core.internal.objects.data.Address;
import org.assertj.core.internal.objects.data.Home;
import org.assertj.core.internal.objects.data.Person;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Test;


public class RecursiveComparisonAssert_isEqualTo_ignoringFields_Test extends RecursiveComparisonAssert_isEqualTo_BaseTest {
    @Test
    public void should_fail_when_actual_differs_from_expected_even_when_all_null_fields_are_ignored() {
        // GIVEN
        Person actual = new Person(null);
        actual.home.address.number = 1;
        actual.dateOfBirth = null;
        actual.neighbour = null;
        Person expected = new Person("John");
        expected.home.address.number = 2;
        recursiveComparisonConfiguration.setIgnoreAllActualNullFields(true);
        // WHEN
        compareRecursivelyFailsAsExpected(actual, expected);
        // THEN
        ComparisonDifference comparisonDifference = new ComparisonDifference(Lists.list("home.address.number"), 1, 2);
        verifyShouldBeEqualByComparingFieldByFieldRecursivelyCall(actual, expected, comparisonDifference);
    }

    @Test
    public void should_fail_when_actual_differs_from_expected_even_when_some_fields_are_ignored() {
        // GIVEN
        Person actual = new Person("John");
        actual.home.address.number = 1;
        actual.dateOfBirth = new Date(123);
        actual.neighbour = new Person("Jack");
        actual.neighbour.home.address.number = 123;
        actual.neighbour.neighbour = new Person("James");
        actual.neighbour.neighbour.home.address.number = 124;
        Person expected = new Person("Jack");
        expected.home.address.number = 2;
        expected.dateOfBirth = new Date(456);
        expected.neighbour = new Person("Jim");
        expected.neighbour.home.address.number = 123;
        expected.neighbour.neighbour = new Person("James");
        expected.neighbour.neighbour.home.address.number = 457;
        recursiveComparisonConfiguration.ignoreFields("name", "home.address.number");
        // WHEN
        compareRecursivelyFailsAsExpected(actual, expected);
        // THEN
        ComparisonDifference dateOfBirthDifference = RecursiveComparisonAssert_isEqualTo_BaseTest.diff("dateOfBirth", actual.dateOfBirth, expected.dateOfBirth);
        ComparisonDifference neighbourNameDifference = RecursiveComparisonAssert_isEqualTo_BaseTest.diff("neighbour.name", actual.neighbour.name, expected.neighbour.name);
        ComparisonDifference numberDifference = RecursiveComparisonAssert_isEqualTo_BaseTest.diff("neighbour.neighbour.home.address.number", actual.neighbour.neighbour.home.address.number, expected.neighbour.neighbour.home.address.number);
        verifyShouldBeEqualByComparingFieldByFieldRecursivelyCall(actual, expected, dateOfBirthDifference, neighbourNameDifference, numberDifference);
    }

    @Test
    public void should_fail_when_actual_differs_from_expected_even_when_some_fields_are_ignored_by_regexes() {
        // GIVEN
        Person actual = new Person("John");
        actual.home.address.number = 1;
        actual.dateOfBirth = new Date(123);
        actual.neighbour = new Person("Jack");
        actual.neighbour.dateOfBirth = new Date(123);
        actual.neighbour.home.address.number = 123;
        actual.neighbour.neighbour = new Person("James");
        actual.neighbour.neighbour.home.address.number = 124;
        Person expected = new Person("Jack");
        expected.home.address.number = 2;
        expected.dateOfBirth = new Date(456);
        expected.neighbour = new Person("Jim");
        expected.neighbour.dateOfBirth = new Date(456);
        expected.neighbour.home.address.number = 234;
        expected.neighbour.neighbour = new Person("James");
        expected.neighbour.neighbour.home.address.number = 457;
        recursiveComparisonConfiguration.ignoreFieldsMatchingRegexes(".*name", ".*home.*number");
        // WHEN
        compareRecursivelyFailsAsExpected(actual, expected);
        // THEN
        ComparisonDifference dateOfBirthDifference = RecursiveComparisonAssert_isEqualTo_BaseTest.diff("dateOfBirth", actual.dateOfBirth, expected.dateOfBirth);
        ComparisonDifference neighbourdateOfBirthDifference = RecursiveComparisonAssert_isEqualTo_BaseTest.diff("neighbour.dateOfBirth", actual.neighbour.dateOfBirth, expected.neighbour.dateOfBirth);
        verifyShouldBeEqualByComparingFieldByFieldRecursivelyCall(actual, expected, dateOfBirthDifference, neighbourdateOfBirthDifference);
    }
}

