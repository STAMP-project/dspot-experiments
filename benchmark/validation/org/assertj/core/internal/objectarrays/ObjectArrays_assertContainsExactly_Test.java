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
package org.assertj.core.internal.objectarrays;


import org.assertj.core.api.AssertionInfo;
import org.assertj.core.api.Assertions;
import org.assertj.core.error.ShouldContainExactly;
import org.assertj.core.internal.ErrorMessages;
import org.assertj.core.internal.ObjectArraysBaseTest;
import org.assertj.core.internal.StandardComparisonStrategy;
import org.assertj.core.test.TestData;
import org.assertj.core.test.TestFailures;
import org.assertj.core.util.Arrays;
import org.assertj.core.util.FailureMessages;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


/**
 * Tests for <code>{@link Iterables#assertContainsExactly(AssertionInfo, Iterable, Object[])}</code>.
 *
 * @author Joel Costigliola
 */
public class ObjectArrays_assertContainsExactly_Test extends ObjectArraysBaseTest {
    @Test
    public void should_pass_if_actual_contains_exactly_given_values() {
        arrays.assertContainsExactly(TestData.someInfo(), actual, Arrays.array("Luke", "Yoda", "Leia"));
    }

    @Test
    public void should_pass_if_actual_contains_given_values_exactly_with_null_elements() {
        actual = Arrays.array("Luke", "Yoda", "Leia", null);
        arrays.assertContainsExactly(TestData.someInfo(), actual, Arrays.array("Luke", "Yoda", "Leia", null));
    }

    @Test
    public void should_pass_if_actual_contains_given_values_exactly_with_duplicate_elements() {
        actual = Arrays.array("Luke", "Yoda", "Yoda");
        arrays.assertContainsExactly(TestData.someInfo(), actual, Arrays.array("Luke", "Yoda", "Yoda"));
    }

    @Test
    public void should_pass_if_actual_and_given_values_are_empty() {
        arrays.assertContainsExactly(TestData.someInfo(), Arrays.array(), Arrays.array());
    }

    @Test
    public void should_fail_if_array_of_values_to_look_for_is_empty_and_actual_is_not() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> arrays.assertContainsExactly(someInfo(), actual, array()));
    }

    @Test
    public void should_fail_if_arrays_have_different_sizes() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> arrays.assertContainsExactly(someInfo(), actual, array("Luke", "Yoda")));
    }

    @Test
    public void should_throw_error_if_array_of_values_to_look_for_is_null() {
        Assertions.assertThatNullPointerException().isThrownBy(() -> arrays.assertContainsExactly(someInfo(), actual, null)).withMessage(ErrorMessages.valuesToLookForIsNull());
    }

    @Test
    public void should_fail_if_actual_is_null() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> arrays.assertContainsExactly(someInfo(), null, array("Yoda"))).withMessage(FailureMessages.actualIsNull());
    }

    @Test
    public void should_fail_if_actual_does_not_contain_given_values_exactly() {
        AssertionInfo info = TestData.someInfo();
        Object[] expected = new Object[]{ "Luke", "Yoda", "Han" };
        try {
            arrays.assertContainsExactly(info, actual, expected);
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, ShouldContainExactly.shouldContainExactly(actual, Arrays.asList(expected), Lists.newArrayList("Han"), Lists.newArrayList("Leia")));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }

    @Test
    public void should_fail_if_actual_contains_all_given_values_but_in_different_order() {
        AssertionInfo info = TestData.someInfo();
        Object[] expected = new Object[]{ "Luke", "Leia", "Yoda" };
        try {
            arrays.assertContainsExactly(info, actual, expected);
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, ShouldContainExactly.elementsDifferAtIndex("Yoda", "Leia", 1));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }

    @Test
    public void should_fail_if_actual_contains_all_given_values_but_size_differ() {
        AssertionInfo info = TestData.someInfo();
        actual = Arrays.array("Luke", "Leia", "Luke");
        Object[] expected = new Object[]{ "Luke", "Leia" };
        try {
            arrays.assertContainsExactly(info, actual, expected);
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, ShouldContainExactly.shouldContainExactly(actual, Arrays.asList(expected), Lists.newArrayList(), Lists.newArrayList("Luke"), StandardComparisonStrategy.instance()));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }

    @Test
    public void should_fail_if_arrays_have_different_sizes_for_large_arrays() {
        // GIVEN
        Object[] actual = new Object[2000];
        Object[] expected = new Object[(actual.length) + 1];
        for (int i = 0; i < (actual.length); i++) {
            actual[i] = String.valueOf(i);
            expected[i] = String.valueOf(i);
        }
        expected[actual.length] = "extra";
        AssertionInfo info = TestData.someInfo();
        // WHEN
        try {
            arrays.assertContainsExactly(info, actual, expected);
        } catch (AssertionError e) {
            // THEN
            Mockito.verify(failures).failure(info, ShouldContainExactly.shouldContainExactly(actual, Arrays.asList(expected), Lists.newArrayList("extra"), Lists.newArrayList(), StandardComparisonStrategy.instance()));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }

    // ------------------------------------------------------------------------------------------------------------------
    // tests using a custom comparison strategy
    // ------------------------------------------------------------------------------------------------------------------
    @Test
    public void should_pass_if_actual_contains_given_values_exactly_according_to_custom_comparison_strategy() {
        arraysWithCustomComparisonStrategy.assertContainsExactly(TestData.someInfo(), actual, Arrays.array("LUKE", "YODA", "Leia"));
    }

    @Test
    public void should_fail_if_actual_does_not_contain_given_values_exactly_according_to_custom_comparison_strategy() {
        AssertionInfo info = TestData.someInfo();
        Object[] expected = new Object[]{ "Luke", "Yoda", "Han" };
        try {
            arraysWithCustomComparisonStrategy.assertContainsExactly(info, actual, expected);
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, ShouldContainExactly.shouldContainExactly(actual, Arrays.asList(expected), Lists.newArrayList("Han"), Lists.newArrayList("Leia"), caseInsensitiveStringComparisonStrategy));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }

    @Test
    public void should_fail_if_actual_contains_all_given_values_in_different_order_according_to_custom_comparison_strategy() {
        AssertionInfo info = TestData.someInfo();
        Object[] expected = new Object[]{ "Luke", "Leia", "Yoda" };
        try {
            arraysWithCustomComparisonStrategy.assertContainsExactly(info, actual, expected);
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, ShouldContainExactly.elementsDifferAtIndex("Yoda", "Leia", 1, caseInsensitiveStringComparisonStrategy));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }

    @Test
    public void should_fail_if_actual_contains_all_given_values_but_size_differ_according_to_custom_comparison_strategy() {
        AssertionInfo info = TestData.someInfo();
        actual = Arrays.array("Luke", "Leia", "Luke");
        Object[] expected = new Object[]{ "Luke", "Leia" };
        try {
            arraysWithCustomComparisonStrategy.assertContainsExactly(info, actual, expected);
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, ShouldContainExactly.shouldContainExactly(actual, Arrays.asList(expected), Lists.newArrayList(), Lists.newArrayList("Luke"), caseInsensitiveStringComparisonStrategy));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }
}

