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


public class RecursiveComparisonAssert_isEqualTo_withFieldComparators_Test extends RecursiveComparisonAssert_isEqualTo_BaseTest {
    @Test
    public void should_fail_when_actual_differs_from_expected_when_using_field_comparators() {
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
        // register comparators for some fields that will fail the comparison
        recursiveComparisonConfiguration.registerComparatorForField(AlwaysDifferentComparator.alwaysDifferent(), FieldLocation.fielLocation("dateOfBirth"));
        recursiveComparisonConfiguration.registerComparatorForField(AlwaysDifferentComparator.alwaysDifferent(), FieldLocation.fielLocation("neighbour.home.address"));
        // WHEN
        compareRecursivelyFailsAsExpected(actual, expected);
        // THEN
        ComparisonDifference dateOfBirthDifference = RecursiveComparisonAssert_isEqualTo_BaseTest.diff("dateOfBirth", actual.dateOfBirth, expected.dateOfBirth);
        ComparisonDifference neighbourAddressDifference = RecursiveComparisonAssert_isEqualTo_BaseTest.diff("neighbour.home.address", actual.neighbour.home.address, expected.neighbour.home.address);
        verifyShouldBeEqualByComparingFieldByFieldRecursivelyCall(actual, expected, dateOfBirthDifference, neighbourAddressDifference);
    }

    @Test
    public void should_be_able_to_compare_objects_recursively_using_some_precision_for_numerical_fields() {
        // GIVEN
        Giant goliath = new Giant();
        goliath.name = "Goliath";
        goliath.height = 3.0;
        Giant goliathTwin = new Giant();
        goliathTwin.name = "Goliath";
        goliathTwin.height = 3.1;
        // THEN
        Assertions.assertThat(goliath).usingRecursiveComparison().withComparatorForFields(new AtPrecisionComparator(0.2), "height").isEqualTo(goliathTwin);
    }

    @Test
    public void should_be_able_to_compare_objects_recursively_using_given_comparator_for_specified_nested_field() {
        // GIVEN
        Giant goliath = new Giant();
        goliath.name = "Goliath";
        goliath.height = 3.0;
        goliath.home.address.number = 1;
        Giant goliathTwin = new Giant();
        goliathTwin.name = "Goliath";
        goliathTwin.height = 3.1;
        goliathTwin.home.address.number = 5;
        // THEN
        Assertions.assertThat(goliath).usingRecursiveComparison().withComparatorForFields(new AtPrecisionComparator(0.2), "height").withComparatorForFields(new AtPrecisionComparator(10), "home.address.number").isEqualTo(goliathTwin);
    }

    @Test
    public void should_handle_null_field_with_field_comparator() {
        // GIVEN
        Patient actual = new Patient(null);
        Patient expected = new Patient(new Timestamp(3L));
        // THEN
        Assertions.assertThat(actual).usingRecursiveComparison().withComparatorForFields(AlwaysEqualComparator.ALWAY_EQUALS_TIMESTAMP, "dateOfBirth").isEqualTo(expected);
    }

    @Test
    public void should_ignore_comparators_when_fields_are_the_same() {
        // GIVEN
        Timestamp dateOfBirth = new Timestamp(3L);
        Patient actual = new Patient(dateOfBirth);
        Patient expected = new Patient(dateOfBirth);
        // WHEN
        Assertions.assertThat(actual).usingRecursiveComparison().withComparatorForFields(NeverEqualComparator.NEVER_EQUALS, "dateOfBirth").isEqualTo(expected);
    }

    @Test
    public void should_treat_timestamp_as_equal_to_date_when_registering_a_date_symmetric_comparator() {
        // GIVEN
        Person actual = new Person("Fred");
        actual.dateOfBirth = new Timestamp(1000L);
        Person other = new Person(actual.name);
        other.dateOfBirth = new Date(1000L);
        // THEN
        Assertions.assertThat(actual).usingRecursiveComparison().withComparatorForFields(SymmetricDateComparator.SYMMETRIC_DATE_COMPARATOR, "dateOfBirth").isEqualTo(other);
        Assertions.assertThat(other).usingRecursiveComparison().withComparatorForFields(SymmetricDateComparator.SYMMETRIC_DATE_COMPARATOR, "dateOfBirth").isEqualTo(actual);
    }

    @Test
    public void field_comparator_should_take_precedence_over_type_comparator_whatever_their_order_of_registration() {
        // GIVEN
        Patient actual = new Patient(new Timestamp(1L));
        Patient expected = new Patient(new Timestamp(3L));
        // THEN
        Assertions.assertThat(actual).usingRecursiveComparison().withComparatorForType(NeverEqualComparator.NEVER_EQUALS, Timestamp.class).withComparatorForFields(AlwaysEqualComparator.ALWAY_EQUALS_TIMESTAMP, "dateOfBirth").isEqualTo(expected);
        Assertions.assertThat(actual).usingRecursiveComparison().withComparatorForFields(AlwaysEqualComparator.ALWAY_EQUALS_TIMESTAMP, "dateOfBirth").withComparatorForType(NeverEqualComparator.NEVER_EQUALS, Timestamp.class).isEqualTo(expected);
    }

    @Test
    public void ignoringOverriddenEquals_should_not_interfere_with_field_comparators() {
        // GIVEN
        Person actual = new Person("Fred");
        actual.neighbour = new AlwaysEqualPerson();
        actual.neighbour.name = "Omar";
        Person expected = new Person("Fred");
        expected.neighbour = new AlwaysEqualPerson();
        expected.neighbour.name = "Omar2";
        // THEN
        // fails if commented
        Assertions.assertThat(actual).usingRecursiveComparison().withComparatorForFields(AlwaysEqualComparator.ALWAY_EQUALS, "neighbour").ignoringOverriddenEqualsForFields("neighbour").isEqualTo(expected);
    }
}

