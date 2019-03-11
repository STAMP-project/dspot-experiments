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


import java.sql.Timestamp;
import java.util.Date;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.RecursiveComparisonAssert_isEqualTo_BaseTest;
import org.assertj.core.internal.AtPrecisionComparator;
import org.assertj.core.internal.objects.SymmetricDateComparator;
import org.assertj.core.internal.objects.data.Address;
import org.assertj.core.internal.objects.data.AlwaysEqualPerson;
import org.assertj.core.internal.objects.data.Giant;
import org.assertj.core.internal.objects.data.Home;
import org.assertj.core.internal.objects.data.Person;
import org.assertj.core.test.AlwaysDifferentComparator;
import org.assertj.core.test.AlwaysEqualComparator;
import org.assertj.core.test.NeverEqualComparator;
import org.assertj.core.test.Patient;
import org.junit.jupiter.api.Test;


public class RecursiveComparisonAssert_isEqualTo_withTypeComparators_Test extends RecursiveComparisonAssert_isEqualTo_BaseTest {
    @Test
    public void should_fail_when_actual_differs_from_expected_when_using_comparators_by_type() {
        // GIVEN
        Person actual = new Person("John");
        actual.home.address.number = 1;
        actual.dateOfBirth = new Date(123);
        actual.neighbour = new Person("Jack");
        actual.neighbour.home.address.number = 123;
        // actually a clone of actual
        Person expected = new Person("John");
        expected.home.address.number = 1;
        expected.dateOfBirth = new Date(123);
        expected.neighbour = new Person("Jack");
        expected.neighbour.home.address.number = 123;
        // register comparators for some type that will fail the comparison
        recursiveComparisonConfiguration.registerComparatorForType(new AlwaysDifferentComparator(), Person.class);
        recursiveComparisonConfiguration.registerComparatorForType(new AlwaysDifferentComparator(), Date.class);
        recursiveComparisonConfiguration.registerComparatorForType(new AlwaysDifferentComparator(), Address.class);
        // WHEN
        compareRecursivelyFailsAsExpected(actual, expected);
        // THEN
        ComparisonDifference dateOfBirthDifference = RecursiveComparisonAssert_isEqualTo_BaseTest.diff("dateOfBirth", actual.dateOfBirth, expected.dateOfBirth);
        ComparisonDifference addressDifference = RecursiveComparisonAssert_isEqualTo_BaseTest.diff("home.address", actual.home.address, expected.home.address);
        ComparisonDifference neighbourDifference = RecursiveComparisonAssert_isEqualTo_BaseTest.diff("neighbour", actual.neighbour, expected.neighbour);
        verifyShouldBeEqualByComparingFieldByFieldRecursivelyCall(actual, expected, dateOfBirthDifference, addressDifference, neighbourDifference);
    }

    @Test
    public void should_be_able_to_compare_objects_recursively_using_some_precision_for_numerical_types() {
        // GIVEN
        Giant goliath = new Giant();
        goliath.name = "Goliath";
        goliath.height = 3.0;
        Giant goliathTwin = new Giant();
        goliathTwin.name = "Goliath";
        goliathTwin.height = 3.1;
        // THEN
        Assertions.assertThat(goliath).usingRecursiveComparison().withComparatorForType(new AtPrecisionComparator(0.2), Double.class).isEqualTo(goliathTwin);
    }

    @Test
    public void should_handle_null_field_with_type_comparator() {
        // GIVEN
        Patient actual = new Patient(null);
        Patient expected = new Patient(new Timestamp(3L));
        // THEN
        Assertions.assertThat(actual).usingRecursiveComparison().withComparatorForType(AlwaysEqualComparator.ALWAY_EQUALS_TIMESTAMP, Timestamp.class).isEqualTo(expected);
    }

    @Test
    public void should_ignore_comparators_when_fields_are_the_same() {
        // GIVEN
        Timestamp dateOfBirth = new Timestamp(3L);
        Patient actual = new Patient(dateOfBirth);
        Patient expected = new Patient(dateOfBirth);
        // THEN
        Assertions.assertThat(actual).usingRecursiveComparison().withComparatorForType(NeverEqualComparator.NEVER_EQUALS, Timestamp.class).isEqualTo(expected);
    }

    @Test
    public void should_treat_timestamp_as_equal_to_date_when_registering_a_Date_symmetric_comparator() {
        // GIVEN
        Person actual = new Person("Fred");
        actual.dateOfBirth = new Timestamp(1000L);
        Person expected = new Person(actual.name);
        expected.dateOfBirth = new Date(1000L);
        // THEN
        Assertions.assertThat(actual).usingRecursiveComparison().withComparatorForType(SymmetricDateComparator.SYMMETRIC_DATE_COMPARATOR, Timestamp.class).isEqualTo(expected);
        Assertions.assertThat(expected).usingRecursiveComparison().withComparatorForType(SymmetricDateComparator.SYMMETRIC_DATE_COMPARATOR, Timestamp.class).isEqualTo(actual);
    }

    @Test
    public void ignoringOverriddenEquals_should_not_interfere_with_comparators_by_type() {
        // GIVEN
        Person actual = new Person("Fred");
        actual.neighbour = new AlwaysEqualPerson();
        actual.neighbour.name = "Omar";
        Person expected = new Person("Fred");
        expected.neighbour = new AlwaysEqualPerson();
        expected.neighbour.name = "Omar2";
        // THEN
        // fails if commented
        Assertions.assertThat(actual).usingRecursiveComparison().withComparatorForType(AlwaysEqualComparator.ALWAY_EQUALS, AlwaysEqualPerson.class).ignoringOverriddenEqualsForFields("neighbour").isEqualTo(expected);
    }
}

