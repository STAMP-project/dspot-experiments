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


import java.awt.Rectangle;
import org.assertj.core.api.AssertionInfo;
import org.assertj.core.api.Assertions;
import org.assertj.core.error.ShouldContainsOnlyOnce;
import org.assertj.core.internal.ErrorMessages;
import org.assertj.core.internal.ObjectArraysBaseTest;
import org.assertj.core.test.ObjectArrays;
import org.assertj.core.test.TestData;
import org.assertj.core.test.TestFailures;
import org.assertj.core.util.Arrays;
import org.assertj.core.util.FailureMessages;
import org.assertj.core.util.Sets;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


/**
 * Tests for <code>{@link ObjectArrays#assertContainsOnlyOnce(AssertionInfo, Object[], Object[])}</code>.
 *
 * @author William Delanoue
 */
public class ObjectArrays_assertContainsOnlyOnce_Test extends ObjectArraysBaseTest {
    @Test
    public void should_pass_if_actual_contains_given_values_only_once() {
        arrays.assertContainsOnlyOnce(TestData.someInfo(), actual, Arrays.array("Luke", "Yoda", "Leia"));
    }

    @Test
    public void should_pass_if_actual_contains_given_values_only_once_even_if_actual_type_is_not_comparable() {
        // Rectangle class does not implement Comparable
        Rectangle r1 = new Rectangle(1, 1);
        Rectangle r2 = new Rectangle(2, 2);
        arrays.assertContainsOnlyOnce(TestData.someInfo(), Arrays.array(r1, r2, r2), Arrays.array(r1));
    }

    @Test
    public void should_pass_if_actual_contains_given_values_only_in_different_order() {
        arrays.assertContainsOnlyOnce(TestData.someInfo(), actual, Arrays.array("Leia", "Yoda", "Luke"));
    }

    @Test
    public void should_fail_if_actual_contains_given_values_more_than_once() {
        AssertionInfo info = TestData.someInfo();
        actual = Arrays.array("Luke", "Yoda", "Han", "Luke", "Yoda", "Han", "Yoda", "Luke");
        String[] expected = new String[]{ "Luke", "Yoda", "Leia" };
        try {
            arrays.assertContainsOnlyOnce(info, actual, expected);
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, ShouldContainsOnlyOnce.shouldContainsOnlyOnce(actual, expected, Sets.newLinkedHashSet("Leia"), Sets.newLinkedHashSet("Luke", "Yoda")));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }

    @Test
    public void should_pass_if_actual_contains_given_values_only_once_even_if_duplicated() {
        arrays.assertContainsOnlyOnce(TestData.someInfo(), actual, Arrays.array("Luke", "Yoda", "Leia", "Luke", "Yoda", "Leia"));
    }

    @Test
    public void should_pass_if_actual_and_given_values_are_empty() {
        actual = new String[]{  };
        arrays.assertContainsOnlyOnce(TestData.someInfo(), actual, ObjectArrays.emptyArray());
    }

    @Test
    public void should_fail_if_array_of_values_to_look_for_is_empty_and_actual_is_not() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> arrays.assertContainsOnlyOnce(someInfo(), actual, emptyArray()));
    }

    @Test
    public void should_throw_error_if_array_of_values_to_look_for_is_null() {
        Assertions.assertThatNullPointerException().isThrownBy(() -> arrays.assertContainsOnlyOnce(someInfo(), actual, null)).withMessage(ErrorMessages.valuesToLookForIsNull());
    }

    @Test
    public void should_fail_if_actual_is_null() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> arrays.assertContainsOnlyOnce(someInfo(), null, array("Yoda"))).withMessage(FailureMessages.actualIsNull());
    }

    @Test
    public void should_fail_if_actual_does_not_contain_all_given_values() {
        AssertionInfo info = TestData.someInfo();
        String[] expected = new String[]{ "Luke", "Yoda", "Han" };
        try {
            arrays.assertContainsOnlyOnce(info, actual, expected);
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, ShouldContainsOnlyOnce.shouldContainsOnlyOnce(actual, expected, Sets.newLinkedHashSet("Han"), Sets.newLinkedHashSet()));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }

    @Test
    public void should_pass_if_actual_contains_given_values_only_according_to_custom_comparison_strategy() {
        arraysWithCustomComparisonStrategy.assertContainsOnlyOnce(TestData.someInfo(), actual, Arrays.array("Luke", "yoda", "Leia"));
    }

    @Test
    public void should_pass_if_actual_contains_given_values_only_in_different_order_according_to_custom_comparison_strategy() {
        arraysWithCustomComparisonStrategy.assertContainsOnlyOnce(TestData.someInfo(), actual, Arrays.array("Leia", "yoda", "Luke"));
    }

    @Test
    public void should_fail_if_actual_contains_given_values_more_than_once_according_to_custom_comparison_strategy() {
        AssertionInfo info = TestData.someInfo();
        actual = Arrays.array("Luke", "yODA", "Han", "luke", "yoda", "Han", "YodA");
        String[] expected = new String[]{ "Luke", "yOda", "Leia" };
        try {
            arraysWithCustomComparisonStrategy.assertContainsOnlyOnce(info, actual, expected);
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, ShouldContainsOnlyOnce.shouldContainsOnlyOnce(actual, expected, Sets.newLinkedHashSet("Leia"), Sets.newLinkedHashSet("Luke", "yOda"), caseInsensitiveStringComparisonStrategy));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }

    @Test
    public void should_pass_if_actual_contains_given_values_only_once_according_to_custom_comparison_strategy_even_if_duplicated_() {
        arraysWithCustomComparisonStrategy.assertContainsOnlyOnce(TestData.someInfo(), actual, Arrays.array("Luke", "Yoda", "Leia", "Luke", "yODA", "LeiA"));
    }

    @Test
    public void should_fail_if_array_of_values_to_look_for_is_empty_and_actual_is_not_whatever_custom_comparison_strategy_is() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> arraysWithCustomComparisonStrategy.assertContainsOnlyOnce(someInfo(), actual, emptyArray()));
    }

    @Test
    public void should_throw_error_if_array_of_values_to_look_for_is_null_whatever_custom_comparison_strategy_is() {
        Assertions.assertThatNullPointerException().isThrownBy(() -> arraysWithCustomComparisonStrategy.assertContainsOnlyOnce(someInfo(), actual, null)).withMessage(ErrorMessages.valuesToLookForIsNull());
    }

    @Test
    public void should_fail_if_actual_is_null_whatever_custom_comparison_strategy_is() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> arraysWithCustomComparisonStrategy.assertContainsOnlyOnce(someInfo(), null, array("yoda"))).withMessage(FailureMessages.actualIsNull());
    }

    @Test
    public void should_fail_if_actual_does_not_contain_all_given_values_only_once_according_to_custom_comparison_strategy() {
        AssertionInfo info = TestData.someInfo();
        String[] expected = new String[]{ "Luke", "yoda", "han" };
        try {
            arraysWithCustomComparisonStrategy.assertContainsOnlyOnce(info, actual, expected);
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, ShouldContainsOnlyOnce.shouldContainsOnlyOnce(actual, expected, Sets.newLinkedHashSet("han"), Sets.newLinkedHashSet(), caseInsensitiveStringComparisonStrategy));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }
}

