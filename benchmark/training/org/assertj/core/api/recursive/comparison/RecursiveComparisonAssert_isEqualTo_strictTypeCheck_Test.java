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
import org.assertj.core.api.Assertions;
import org.assertj.core.api.RecursiveComparisonAssert_isEqualTo_BaseTest;
import org.assertj.core.internal.objects.data.Address;
import org.assertj.core.internal.objects.data.AddressDto;
import org.assertj.core.internal.objects.data.Giant;
import org.assertj.core.internal.objects.data.Home;
import org.assertj.core.internal.objects.data.HomeDto;
import org.assertj.core.internal.objects.data.Person;
import org.assertj.core.internal.objects.data.PersonDto;
import org.assertj.core.internal.objects.data.PersonDtoWithPersonNeighbour;
import org.junit.jupiter.api.Test;


public class RecursiveComparisonAssert_isEqualTo_strictTypeCheck_Test extends RecursiveComparisonAssert_isEqualTo_BaseTest {
    @Test
    public void should_pass_by_default_when_objects_data_are_equals_whatever_their_types_are() {
        // GIVEN
        Person actual = new Person("John");
        actual.home.address.number = 1;
        actual.dateOfBirth = new Date(123);
        actual.neighbour = new Person("Jack");
        actual.neighbour.home.address.number = 123;
        actual.neighbour.neighbour = new Person("James");
        actual.neighbour.neighbour.home.address.number = 124;
        PersonDto expected = new PersonDto("John");
        expected.home.address.number = 1;
        expected.dateOfBirth = new Date(123);
        expected.neighbour = new PersonDto("Jack");
        expected.neighbour.home.address.number = 123;
        expected.neighbour.neighbour = new PersonDto("James");
        expected.neighbour.neighbour.home.address.number = 124;
        // THEN
        Assertions.assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    public void should_pass_in_strict_type_check_mode_when_objects_data_are_equals_and_expected_type_is_compatible_with_actual_type() {
        // GIVEN
        Person actual = new Person("John");
        actual.home.address.number = 1;
        actual.dateOfBirth = new Date(123);
        actual.neighbour = new Person("Jack");
        actual.neighbour.home.address.number = 123;
        actual.neighbour.neighbour = new Person("James");
        actual.neighbour.neighbour.home.address.number = 124;
        Giant expected = new Giant();
        expected.name = "John";
        expected.home.address.number = 1;
        expected.dateOfBirth = new Date(123);
        expected.neighbour = new Giant();
        expected.neighbour.name = "Jack";
        expected.neighbour.home.address.number = 123;
        expected.neighbour.neighbour = new Person("James");
        expected.neighbour.neighbour.home.address.number = 124;
        Person expected2 = new Person("John");
        expected2.home.address.number = 1;
        expected2.dateOfBirth = new Date(123);
        expected2.neighbour = new Person("Jack");
        expected2.neighbour.home.address.number = 123;
        expected2.neighbour.neighbour = new Person("James");
        expected2.neighbour.neighbour.home.address.number = 124;
        // WHEN
        recursiveComparisonConfiguration.strictTypeChecking(true);
        // THEN
        Assertions.assertThat(actual).usingRecursiveComparison().withStrictTypeChecking().isEqualTo(expected).isEqualTo(expected2);
    }

    @Test
    public void should_fail_in_strict_type_checking_mode_when_actual_and_expected_have_the_same_data_but_incompatible_types() {
        // GIVEN
        Person actual = new Person("John");
        actual.home.address.number = 1;
        actual.dateOfBirth = new Date(123);
        actual.neighbour = new Person("Jack");
        actual.neighbour.home.address.number = 123;
        PersonDtoWithPersonNeighbour expected = new PersonDtoWithPersonNeighbour("John");
        expected.home.address.number = 1;
        expected.dateOfBirth = new Date(123);
        expected.neighbour = new Person("Jack");
        expected.neighbour.home.address.number = 123;
        recursiveComparisonConfiguration.strictTypeChecking(true);
        // WHEN
        compareRecursivelyFailsAsExpected(actual, expected);
        // THEN
        ComparisonDifference difference = RecursiveComparisonAssert_isEqualTo_BaseTest.diff("", actual, expected, "actual and expected are considered different since the comparison enforces strict type check and expected type org.assertj.core.internal.objects.data.PersonDtoWithPersonNeighbour is not a subtype of actual type org.assertj.core.internal.objects.data.Person");
        verifyShouldBeEqualByComparingFieldByFieldRecursivelyCall(actual, expected, difference);
    }

    @Test
    public void should_fail_in_strict_type_checking_mode_when_actual_and_expected_fields_have_the_same_data_but_incompatible_types() {
        // GIVEN
        RecursiveComparisonAssert_isEqualTo_strictTypeCheck_Test.Something withA = new RecursiveComparisonAssert_isEqualTo_strictTypeCheck_Test.Something(new RecursiveComparisonAssert_isEqualTo_strictTypeCheck_Test.A(10));
        RecursiveComparisonAssert_isEqualTo_strictTypeCheck_Test.Something withB = new RecursiveComparisonAssert_isEqualTo_strictTypeCheck_Test.Something(new RecursiveComparisonAssert_isEqualTo_strictTypeCheck_Test.B(10));
        recursiveComparisonConfiguration.strictTypeChecking(true);
        // WHEN
        compareRecursivelyFailsAsExpected(withA, withB);
        // THEN
        // inner comparison fails as the fields have different types
        ComparisonDifference valueDifference = RecursiveComparisonAssert_isEqualTo_BaseTest.diff("inner", withA.inner, withB.inner, "the fields are considered different since the comparison enforces strict type check and org.assertj.core.api.recursive.comparison.RecursiveComparisonAssert_isEqualTo_strictTypeCheck_Test$B is not a subtype of org.assertj.core.api.recursive.comparison.RecursiveComparisonAssert_isEqualTo_strictTypeCheck_Test$A");
        verifyShouldBeEqualByComparingFieldByFieldRecursivelyCall(withA, withB, valueDifference);
    }

    private static class Something {
        RecursiveComparisonAssert_isEqualTo_strictTypeCheck_Test.Inner inner;// can be A or B


        public Something(RecursiveComparisonAssert_isEqualTo_strictTypeCheck_Test.Inner inner) {
            this.inner = inner;
        }
    }

    private static class Inner {
        @SuppressWarnings("unused")
        int value;
    }

    private static class A extends RecursiveComparisonAssert_isEqualTo_strictTypeCheck_Test.Inner {
        public A(int value) {
            this.value = value;
        }
    }

    private static class B extends RecursiveComparisonAssert_isEqualTo_strictTypeCheck_Test.Inner {
        public B(int value) {
            this.value = value;
        }
    }
}

