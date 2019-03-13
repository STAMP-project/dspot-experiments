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
import org.assertj.core.error.ShouldContainOnly;
import org.assertj.core.internal.ErrorMessages;
import org.assertj.core.internal.IterablesBaseTest;
import org.assertj.core.test.TestData;
import org.assertj.core.test.TestFailures;
import org.assertj.core.util.Arrays;
import org.assertj.core.util.FailureMessages;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


/**
 * Tests for <code>{@link Iterables#assertContainsOnly(AssertionInfo, Collection, Object[])}</code>.
 *
 * @author Alex Ruiz
 * @author Joel Costigliola
 */
public class Iterables_assertContainsOnly_Test extends IterablesBaseTest {
    @Test
    public void should_pass_if_actual_contains_given_values_only() {
        iterables.assertContainsOnly(TestData.someInfo(), actual, Arrays.array("Luke", "Yoda", "Leia"));
    }

    @Test
    public void should_pass_if_actual_contains_given_values_only_with_null_elements() {
        actual.add(null);
        actual.add(null);
        iterables.assertContainsOnly(TestData.someInfo(), actual, Arrays.array("Luke", null, "Yoda", "Leia", null));
    }

    @Test
    public void should_pass_if_actual_contains_given_values_only_in_different_order() {
        iterables.assertContainsOnly(TestData.someInfo(), actual, Arrays.array("Leia", "Yoda", "Luke"));
    }

    @Test
    public void should_pass_if_actual_contains_given_values_only_more_than_once() {
        actual.addAll(Lists.newArrayList("Luke", "Luke"));
        iterables.assertContainsOnly(TestData.someInfo(), actual, Arrays.array("Luke", "Yoda", "Leia"));
    }

    @Test
    public void should_pass_if_actual_contains_given_values_only_even_if_duplicated() {
        iterables.assertContainsOnly(TestData.someInfo(), actual, Arrays.array("Luke", "Luke", "Luke", "Yoda", "Leia"));
    }

    @Test
    public void should_pass_if_actual_and_given_values_are_empty() {
        actual.clear();
        iterables.assertContainsOnly(TestData.someInfo(), actual, Arrays.array());
    }

    @Test
    public void should_fail_if_array_of_values_to_look_for_is_empty_and_actual_is_not() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> iterables.assertContainsOnly(someInfo(), actual, emptyArray()));
    }

    @Test
    public void should_throw_error_if_array_of_values_to_look_for_is_null() {
        Assertions.assertThatNullPointerException().isThrownBy(() -> iterables.assertContainsOnly(someInfo(), emptyList(), null)).withMessage(ErrorMessages.valuesToLookForIsNull());
    }

    @Test
    public void should_fail_if_actual_is_null() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> iterables.assertContainsOnly(someInfo(), null, array("Yoda"))).withMessage(FailureMessages.actualIsNull());
    }

    @Test
    public void should_fail_if_actual_does_not_contain_all_given_values() {
        AssertionInfo info = TestData.someInfo();
        Object[] expected = new Object[]{ "Luke", "Yoda", "Han" };
        try {
            iterables.assertContainsOnly(info, actual, expected);
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, ShouldContainOnly.shouldContainOnly(actual, expected, Lists.newArrayList("Han"), Lists.newArrayList("Leia")));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }

    @Test
    public void should_fail_if_actual_contains_additional_elements() {
        AssertionInfo info = TestData.someInfo();
        Object[] expected = new Object[]{ "Luke", "Yoda" };
        try {
            iterables.assertContainsOnly(info, actual, expected);
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, ShouldContainOnly.shouldContainOnly(actual, expected, Lists.newArrayList(), Lists.newArrayList("Leia")));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }

    @Test
    public void should_fail_if_actual_contains_a_subset_of_expected_elements() {
        AssertionInfo info = TestData.someInfo();
        Object[] expected = new Object[]{ "Luke", "Yoda", "Obiwan", "Leia" };
        try {
            iterables.assertContainsOnly(info, actual, expected);
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, ShouldContainOnly.shouldContainOnly(actual, expected, Lists.newArrayList("Obiwan"), Lists.newArrayList()));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }

    // ------------------------------------------------------------------------------------------------------------------
    // tests using a custom comparison strategy
    // ------------------------------------------------------------------------------------------------------------------
    @Test
    public void should_pass_if_actual_contains_given_values_only_according_to_custom_comparison_strategy() {
        iterablesWithCaseInsensitiveComparisonStrategy.assertContainsOnly(TestData.someInfo(), actual, Arrays.array("LUKE", "YODA", "Leia"));
    }

    @Test
    public void should_pass_if_actual_contains_given_values_only_in_different_order_according_to_custom_comparison_strategy() {
        iterablesWithCaseInsensitiveComparisonStrategy.assertContainsOnly(TestData.someInfo(), actual, Arrays.array("LEIA", "yoda", "LukE"));
    }

    @Test
    public void should_pass_if_actual_contains_given_values_only_more_than_once_according_to_custom_comparison_strategy() {
        actual.addAll(Lists.newArrayList("Luke", "Luke"));
        iterablesWithCaseInsensitiveComparisonStrategy.assertContainsOnly(TestData.someInfo(), actual, Arrays.array("luke", "YOda", "LeIA"));
    }

    @Test
    public void should_pass_if_actual_contains_given_values_only_even_if_duplicated_according_to_custom_comparison_strategy() {
        actual.addAll(Lists.newArrayList("LUKE"));
        iterablesWithCaseInsensitiveComparisonStrategy.assertContainsOnly(TestData.someInfo(), actual, Arrays.array("LUke", "LUKE", "lukE", "YOda", "Leia"));
    }

    @Test
    public void should_fail_if_actual_does_not_contain_given_values_only_according_to_custom_comparison_strategy() {
        AssertionInfo info = TestData.someInfo();
        Object[] expected = new Object[]{ "Luke", "Yoda", "Han" };
        try {
            iterablesWithCaseInsensitiveComparisonStrategy.assertContainsOnly(info, actual, expected);
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, ShouldContainOnly.shouldContainOnly(actual, expected, Lists.newArrayList("Han"), Lists.newArrayList("Leia"), comparisonStrategy));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }
}

