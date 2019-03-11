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
package org.assertj.core.internal.iterables;


import org.assertj.core.api.AssertionInfo;
import org.assertj.core.api.Assertions;
import org.assertj.core.error.ShouldContainAnyOf;
import org.assertj.core.internal.ErrorMessages;
import org.assertj.core.internal.IterablesBaseTest;
import org.assertj.core.test.Name;
import org.assertj.core.test.TestData;
import org.assertj.core.test.TestFailures;
import org.assertj.core.util.Arrays;
import org.assertj.core.util.FailureMessages;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static java.util.Arrays.asList;


/**
 * Tests for <code>{@link Iterables#assertContainsAnyOf(AssertionInfo, Iterable, Object[])} </code>.
 *
 * @author Marko Bekhta
 */
public class Iterables_assertContainsAnyOf_Test extends IterablesBaseTest {
    @Test
    public void should_pass_if_actual_contains_given_values() {
        iterables.assertContainsAnyOf(TestData.someInfo(), actual, Arrays.array("Luke"));
    }

    @Test
    public void should_pass_with_non_comparable_values() {
        // GIVEN
        Iterable<Name> names = asList(Name.name("John", "Doe"));
        // THEN
        iterables.assertContainsAnyOf(TestData.someInfo(), names, Arrays.array(Name.name("John", "Doe"), Name.name("Jane", "Doe")));
    }

    @Test
    public void should_pass_if_actual_contains_given_values_in_different_order() {
        iterables.assertContainsAnyOf(TestData.someInfo(), actual, Arrays.array("Leia", "Yoda"));
    }

    @Test
    public void should_pass_if_actual_contains_all_given_values() {
        iterables.assertContainsAnyOf(TestData.someInfo(), actual, Arrays.array("Luke", "Yoda", "Leia"));
    }

    @Test
    public void should_pass_if_actual_contains_given_values_more_than_once() {
        actual.addAll(Lists.newArrayList("Luke", "Luke"));
        iterables.assertContainsAnyOf(TestData.someInfo(), actual, Arrays.array("Luke"));
    }

    @Test
    public void should_pass_if_actual_contains_just_one_of_given_values() {
        iterables.assertContainsAnyOf(TestData.someInfo(), actual, Arrays.array("Luke", "John", "Tom"));
    }

    @Test
    public void should_pass_if_actual_contains_given_values_even_if_duplicated() {
        iterables.assertContainsAnyOf(TestData.someInfo(), actual, Arrays.array("Luke", "Luke"));
    }

    @Test
    public void should_pass_if_actual_and_given_values_are_empty() {
        actual.clear();
        iterables.assertContainsAnyOf(TestData.someInfo(), actual, Arrays.array());
    }

    @Test
    public void should_fail_if_array_of_values_to_look_for_is_empty_and_actual_is_not() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> iterables.assertContainsAnyOf(someInfo(), actual, emptyArray()));
    }

    @Test
    public void should_throw_error_if_array_of_values_to_look_for_is_null() {
        Assertions.assertThatNullPointerException().isThrownBy(() -> iterables.assertContainsAnyOf(someInfo(), actual, null)).withMessage(ErrorMessages.valuesToLookForIsNull());
    }

    @Test
    public void should_fail_if_actual_is_null() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> iterables.assertContainsAnyOf(someInfo(), null, array("Yoda"))).withMessage(FailureMessages.actualIsNull());
    }

    @Test
    public void should_fail_if_actual_does_not_contain_any_of_the_given_values() {
        AssertionInfo info = TestData.someInfo();
        Object[] expected = new Object[]{ "Han", "John" };
        try {
            iterables.assertContainsAnyOf(info, actual, expected);
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, ShouldContainAnyOf.shouldContainAnyOf(actual, expected));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }

    // ------------------------------------------------------------------------------------------------------------------
    // tests using a custom comparison strategy
    // ------------------------------------------------------------------------------------------------------------------
    @Test
    public void should_pass_if_actual_contains_given_values_according_to_custom_comparison_strategy() {
        iterablesWithCaseInsensitiveComparisonStrategy.assertContainsAnyOf(TestData.someInfo(), actual, Arrays.array("LUKE"));
    }

    @Test
    public void should_pass_if_actual_contains_given_values_in_different_order_according_to_custom_comparison_strategy() {
        iterablesWithCaseInsensitiveComparisonStrategy.assertContainsAnyOf(TestData.someInfo(), actual, Arrays.array("LEIA", "yODa"));
    }

    @Test
    public void should_pass_if_actual_contains_all_given_values_according_to_custom_comparison_strategy() {
        iterablesWithCaseInsensitiveComparisonStrategy.assertContainsAnyOf(TestData.someInfo(), actual, Arrays.array("luke", "YODA", "leia"));
    }

    @Test
    public void should_pass_if_actual_contains_given_values_more_than_once_according_to_custom_comparison_strategy() {
        actual.addAll(Lists.newArrayList("Luke", "Luke"));
        iterablesWithCaseInsensitiveComparisonStrategy.assertContainsAnyOf(TestData.someInfo(), actual, Arrays.array("LUke"));
    }

    @Test
    public void should_pass_if_actual_contains_given_values_even_if_duplicated_according_to_custom_comparison_strategy() {
        iterablesWithCaseInsensitiveComparisonStrategy.assertContainsAnyOf(TestData.someInfo(), actual, Arrays.array("LUke", "LuKe"));
    }

    @Test
    public void should_pass_if_actual_contains_just_one_of_given_values_according_to_custom_comparison_strategy() {
        iterablesWithCaseInsensitiveComparisonStrategy.assertContainsAnyOf(TestData.someInfo(), actual, Arrays.array("LuKe", "JoHn", "ToM"));
    }

    @Test
    public void should_fail_if_actual_does_not_contain_values_according_to_custom_comparison_strategy() {
        AssertionInfo info = TestData.someInfo();
        Object[] expected = new Object[]{ "Han", "John" };
        try {
            iterablesWithCaseInsensitiveComparisonStrategy.assertContainsAnyOf(info, actual, expected);
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, ShouldContainAnyOf.shouldContainAnyOf(actual, expected, comparisonStrategy));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }
}

