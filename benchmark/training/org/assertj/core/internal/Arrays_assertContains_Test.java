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
package org.assertj.core.internal;


import org.assertj.core.api.AssertionInfo;
import org.assertj.core.api.Assertions;
import org.assertj.core.error.ShouldContain;
import org.assertj.core.test.TestData;
import org.assertj.core.test.TestFailures;
import org.assertj.core.util.Arrays;
import org.assertj.core.util.FailureMessages;
import org.assertj.core.util.Sets;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


public class Arrays_assertContains_Test extends BaseArraysTest {
    @Test
    public void should_pass_if_actual_contains_given_values() {
        arrays.assertContains(TestData.someInfo(), failures, actual, Arrays.array("Luke"));
    }

    @Test
    public void should_pass_if_actual_contains_given_values_in_different_order() {
        arrays.assertContains(TestData.someInfo(), failures, actual, Arrays.array("Leia", "Yoda"));
    }

    @Test
    public void should_pass_if_actual_contains_all_given_values() {
        arrays.assertContains(TestData.someInfo(), failures, actual, Arrays.array("Luke", "Yoda", "Leia"));
    }

    @Test
    public void should_pass_if_actual_contains_given_values_more_than_once() {
        actual = Arrays.array("Luke", "Yoda", "Leia", "Luke", "Luke");
        arrays.assertContains(TestData.someInfo(), failures, actual, Arrays.array("Luke"));
    }

    @Test
    public void should_pass_if_actual_contains_given_values_even_if_duplicated() {
        arrays.assertContains(TestData.someInfo(), failures, actual, Arrays.array("Luke", "Luke"));
    }

    @Test
    public void should_pass_if_actual_and_given_values_are_empty() {
        actual = new String[0];
        arrays.assertContains(TestData.someInfo(), failures, actual, Arrays.array());
    }

    @Test
    public void should_fail_if_array_of_values_to_look_for_is_empty_and_actual_is_not() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> arrays.assertContains(someInfo(), failures, actual, emptyArray()));
    }

    @Test
    public void should_throw_error_if_array_of_values_to_look_for_is_null() {
        Assertions.assertThatNullPointerException().isThrownBy(() -> arrays.assertContains(someInfo(), failures, actual, null)).withMessage(ErrorMessages.valuesToLookForIsNull());
    }

    @Test
    public void should_fail_if_actual_is_null() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> arrays.assertContains(someInfo(), failures, null, array("Yoda"))).withMessage(FailureMessages.actualIsNull());
    }

    @Test
    public void should_fail_if_actual_does_not_contain_values() {
        AssertionInfo info = TestData.someInfo();
        Object[] expected = new Object[]{ "Leia", "Yoda", "John" };
        try {
            arrays.assertContains(info, failures, actual, expected);
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, ShouldContain.shouldContain(actual, expected, Sets.newLinkedHashSet("John")));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }

    // ------------------------------------------------------------------------------------------------------------------
    // tests using a custom comparison strategy
    // ------------------------------------------------------------------------------------------------------------------
    @Test
    public void should_pass_if_actual_contains_given_values_according_to_custom_comparison_strategy() {
        arraysWithCustomComparisonStrategy.assertContains(TestData.someInfo(), failures, actual, Arrays.array("LUKE"));
    }

    @Test
    public void should_pass_if_actual_contains_given_values_in_different_order_according_to_custom_comparison_strategy() {
        arraysWithCustomComparisonStrategy.assertContains(TestData.someInfo(), failures, actual, Arrays.array("LEIA", "yODa"));
    }

    @Test
    public void should_pass_if_actual_contains_all_given_values_according_to_custom_comparison_strategy() {
        arraysWithCustomComparisonStrategy.assertContains(TestData.someInfo(), failures, actual, Arrays.array("luke", "YODA", "leia"));
    }

    @Test
    public void should_pass_if_actual_contains_given_values_more_than_once_according_to_custom_comparison_strategy() {
        actual = Arrays.array("Luke", "Yoda", "Leia", "Luke", "Luke");
        arraysWithCustomComparisonStrategy.assertContains(TestData.someInfo(), failures, actual, Arrays.array("LUke"));
    }

    @Test
    public void should_pass_if_actual_contains_given_values_even_if_duplicated_according_to_custom_comparison_strategy() {
        arraysWithCustomComparisonStrategy.assertContains(TestData.someInfo(), failures, actual, Arrays.array("LUke", "LuKe"));
    }

    @Test
    public void should_fail_if_array_of_values_to_look_for_is_empty_and_actual_is_not_whatever_custom_comparison_strategy_is() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> arraysWithCustomComparisonStrategy.assertContains(someInfo(), failures, actual, emptyArray()));
    }

    @Test
    public void should_throw_error_if_array_of_values_to_look_for_is_null_whatever_custom_comparison_strategy_is() {
        Assertions.assertThatNullPointerException().isThrownBy(() -> arraysWithCustomComparisonStrategy.assertContains(someInfo(), failures, actual, null)).withMessage(ErrorMessages.valuesToLookForIsNull());
    }

    @Test
    public void should_fail_if_actual_is_null_whatever_custom_comparison_strategy_is() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> arraysWithCustomComparisonStrategy.assertContains(someInfo(), failures, null, array("LUke"))).withMessage(FailureMessages.actualIsNull());
    }

    @Test
    public void should_fail_if_actual_does_not_contain_values_according_to_custom_comparison_strategy() {
        AssertionInfo info = TestData.someInfo();
        Object[] expected = new Object[]{ "LeiA", "YODA", "JOhN" };
        try {
            arraysWithCustomComparisonStrategy.assertContains(info, failures, actual, expected);
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, ShouldContain.shouldContain(actual, expected, Sets.newLinkedHashSet("JOhN"), caseInsensitiveStringComparisonStrategy));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }
}

