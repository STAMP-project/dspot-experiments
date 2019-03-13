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


import java.util.Collections;
import java.util.List;
import org.assertj.core.api.AssertionInfo;
import org.assertj.core.api.Assertions;
import org.assertj.core.error.ShouldContainExactlyInAnyOrder;
import org.assertj.core.internal.ErrorMessages;
import org.assertj.core.internal.IterablesBaseTest;
import org.assertj.core.internal.StandardComparisonStrategy;
import org.assertj.core.test.TestData;
import org.assertj.core.test.TestFailures;
import org.assertj.core.util.Arrays;
import org.assertj.core.util.FailureMessages;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


/**
 * Tests for <code>{@link Iterables#assertContainsExactlyInAnyOrder(AssertionInfo, Iterable, Object[])} </code>.
 *
 * @author Lovro Pandzic
 */
public class Iterables_assertContainsExactlyInAnyOrder_Test extends IterablesBaseTest {
    @Test
    public void should_pass_if_actual_contains_exactly_given_values() {
        iterables.assertContainsExactlyInAnyOrder(TestData.someInfo(), actual, Arrays.array("Luke", "Yoda", "Leia"));
    }

    @Test
    public void should_pass_if_actual_contains_given_values_exactly_with_null_elements() {
        iterables.assertContainsExactlyInAnyOrder(TestData.someInfo(), actual, Arrays.array("Leia", "Yoda", "Luke"));
        actual.add(null);
        iterables.assertContainsExactlyInAnyOrder(TestData.someInfo(), actual, Arrays.array("Leia", null, "Yoda", "Luke"));
    }

    @Test
    public void should_pass_if_actual_and_given_values_are_empty() {
        actual.clear();
        iterables.assertContainsExactlyInAnyOrder(TestData.someInfo(), actual, Arrays.array());
    }

    @Test
    public void should_fail_if_array_of_values_to_look_for_is_empty_and_actual_is_not() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> iterables.assertContainsExactlyInAnyOrder(someInfo(), actual, emptyArray()));
    }

    @Test
    public void should_fail_if_expected_is_null() {
        Assertions.assertThatNullPointerException().isThrownBy(() -> iterables.assertContainsExactlyInAnyOrder(someInfo(), emptyList(), null)).withMessage(ErrorMessages.valuesToLookForIsNull());
    }

    @Test
    public void should_fail_if_actual_is_null() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> iterables.assertContainsExactlyInAnyOrder(someInfo(), null, emptyArray())).withMessage(FailureMessages.actualIsNull());
    }

    @Test
    public void should_fail_if_actual_does_not_contain_given_values_exactly() {
        AssertionInfo info = TestData.someInfo();
        Object[] expected = new Object[]{ "Luke", "Yoda", "Han" };
        try {
            iterables.assertContainsExactlyInAnyOrder(info, actual, expected);
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, ShouldContainExactlyInAnyOrder.shouldContainExactlyInAnyOrder(actual, expected, Lists.newArrayList("Han"), Lists.newArrayList("Leia"), StandardComparisonStrategy.instance()));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }

    @Test
    public void should_pass_if_actual_contains_all_given_values_in_different_order() {
        AssertionInfo info = TestData.someInfo();
        Object[] expected = new Object[]{ "Luke", "Leia", "Yoda" };
        iterables.assertContainsExactlyInAnyOrder(info, actual, expected);
    }

    @Test
    public void should_fail_if_actual_contains_duplicates_and_expected_does_not() {
        AssertionInfo info = TestData.someInfo();
        actual = Lists.newArrayList("Luke", "Leia", "Luke");
        Object[] expected = new Object[]{ "Luke", "Leia" };
        try {
            iterables.assertContainsExactlyInAnyOrder(info, actual, expected);
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, ShouldContainExactlyInAnyOrder.shouldContainExactlyInAnyOrder(actual, expected, Collections.emptyList(), Lists.newArrayList("Luke"), StandardComparisonStrategy.instance()));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }

    @Test
    public void should_fail_if_expected_contains_duplicates_and_actual_does_not() {
        AssertionInfo info = TestData.someInfo();
        actual = Lists.newArrayList("Luke", "Leia");
        Object[] expected = new Object[]{ "Luke", "Leia", "Luke" };
        try {
            iterables.assertContainsExactlyInAnyOrder(info, actual, expected);
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, ShouldContainExactlyInAnyOrder.shouldContainExactlyInAnyOrder(actual, expected, Lists.newArrayList("Luke"), Collections.emptyList(), StandardComparisonStrategy.instance()));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }

    // ------------------------------------------------------------------------------------------------------------------
    // tests using a custom comparison strategy
    // ------------------------------------------------------------------------------------------------------------------
    @Test
    public void should_pass_if_actual_contains_given_values_exactly_according_to_custom_comparison_strategy() {
        iterablesWithCaseInsensitiveComparisonStrategy.assertContainsExactlyInAnyOrder(TestData.someInfo(), actual, Arrays.array("LUKE", "YODA", "Leia"));
    }

    @Test
    public void should_fail_if_actual_does_not_contain_given_values_exactly_according_to_custom_comparison_strategy() {
        AssertionInfo info = TestData.someInfo();
        Object[] expected = new Object[]{ "Luke", "Yoda", "Han" };
        try {
            iterablesWithCaseInsensitiveComparisonStrategy.assertContainsExactlyInAnyOrder(info, actual, expected);
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, ShouldContainExactlyInAnyOrder.shouldContainExactlyInAnyOrder(actual, expected, Lists.newArrayList("Han"), Lists.newArrayList("Leia"), comparisonStrategy));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }

    @Test
    public void should_pass_if_actual_contains_all_given_values_in_different_order_according_to_custom_comparison_strategy() {
        AssertionInfo info = TestData.someInfo();
        Object[] expected = new Object[]{ "Luke", "Leia", "Yoda" };
        iterablesWithCaseInsensitiveComparisonStrategy.assertContainsExactlyInAnyOrder(info, actual, expected);
    }

    @Test
    public void should_fail_if_actual_contains_all_given_values_but_size_differ_according_to_custom_comparison_strategy() {
        AssertionInfo info = TestData.someInfo();
        actual = Lists.newArrayList("Luke", "Leia", "Luke");
        Object[] expected = new Object[]{ "LUKE", "Leia" };
        try {
            iterablesWithCaseInsensitiveComparisonStrategy.assertContainsExactlyInAnyOrder(info, actual, expected);
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, ShouldContainExactlyInAnyOrder.shouldContainExactlyInAnyOrder(actual, expected, Collections.emptyList(), Lists.newArrayList("Luke"), comparisonStrategy));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }
}

