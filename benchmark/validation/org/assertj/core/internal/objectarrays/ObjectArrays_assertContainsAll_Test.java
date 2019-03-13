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


import java.util.List;
import org.assertj.core.api.AssertionInfo;
import org.assertj.core.api.Assertions;
import org.assertj.core.error.ShouldContain;
import org.assertj.core.internal.ErrorMessages;
import org.assertj.core.internal.ObjectArraysBaseTest;
import org.assertj.core.test.TestData;
import org.assertj.core.test.TestFailures;
import org.assertj.core.util.Arrays;
import org.assertj.core.util.FailureMessages;
import org.assertj.core.util.Lists;
import org.assertj.core.util.Sets;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


/**
 * Tests for <code>{@link ObjectArrays#assertContainsAll(AssertionInfo, Object[], Iterable)}</code>.
 *
 * @author Joel Costigliola
 */
public class ObjectArrays_assertContainsAll_Test extends ObjectArraysBaseTest {
    @Test
    public void should_pass_if_actual_contains_all_iterable_values() {
        arrays.assertContainsAll(TestData.someInfo(), actual, Lists.newArrayList("Luke", "Yoda", "Leia"));
        arrays.assertContainsAll(TestData.someInfo(), actual, Lists.newArrayList("Luke", "Yoda"));
        // order is not important
        arrays.assertContainsAll(TestData.someInfo(), actual, Lists.newArrayList("Yoda", "Luke"));
    }

    @Test
    public void should_pass_if_actual_contains_all_iterable_values_more_than_once() {
        actual = Arrays.array("Luke", "Yoda", "Leia", "Luke", "Luke");
        arrays.assertContainsAll(TestData.someInfo(), actual, Lists.newArrayList("Luke"));
    }

    @Test
    public void should_pass_if_iterable_is_empty() {
        arrays.assertContainsAll(TestData.someInfo(), actual, Lists.newArrayList());
    }

    @Test
    public void should_throw_error_if_iterable_to_look_for_is_null() {
        Assertions.assertThatNullPointerException().isThrownBy(() -> arrays.assertContainsAll(someInfo(), actual, null)).withMessage(ErrorMessages.iterableToLookForIsNull());
    }

    @Test
    public void should_fail_if_actual_is_null() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> arrays.assertContainsAll(someInfo(), null, newArrayList("Yoda"))).withMessage(FailureMessages.actualIsNull());
    }

    @Test
    public void should_fail_if_actual_does_not_contain_all_iterable_values() {
        AssertionInfo info = TestData.someInfo();
        List<String> expected = Lists.newArrayList("Han", "Luke");
        try {
            arrays.assertContainsAll(info, actual, expected);
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, ShouldContain.shouldContain(actual, expected.toArray(), Sets.newLinkedHashSet("Han")));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }

    // ------------------------------------------------------------------------------------------------------------------
    // tests using a custom comparison strategy
    // ------------------------------------------------------------------------------------------------------------------
    @Test
    public void should_pass_if_actual_contains_all_iterable_values_according_to_custom_comparison_strategy() {
        arraysWithCustomComparisonStrategy.assertContainsAll(TestData.someInfo(), actual, Lists.newArrayList("LUKE"));
    }

    @Test
    public void should_pass_if_actual_contains_all_iterable_values_in_different_order_according_to_custom_comparison_strategy() {
        arraysWithCustomComparisonStrategy.assertContainsAll(TestData.someInfo(), actual, Lists.newArrayList("LEIa", "YodA"));
    }

    @Test
    public void should_pass_if_actual_contains_all_all_iterable_values_according_to_custom_comparison_strategy() {
        arraysWithCustomComparisonStrategy.assertContainsAll(TestData.someInfo(), actual, Lists.newArrayList("LukE", "YodA", "LeiA"));
    }

    @Test
    public void should_pass_if_actual_contains_all_iterable_values_more_than_once_according_to_custom_comparison_strategy() {
        actual = Arrays.array("Luke", "Yoda", "Leia", "Luke", "Luke");
        arraysWithCustomComparisonStrategy.assertContainsAll(TestData.someInfo(), actual, Lists.newArrayList("LUKE"));
    }

    @Test
    public void should_pass_if_actual_contains_all_iterable_values_even_if_duplicated_according_to_custom_comparison_strategy() {
        arraysWithCustomComparisonStrategy.assertContainsAll(TestData.someInfo(), actual, Lists.newArrayList("LUKE", "LUKE"));
    }

    @Test
    public void should_pass_if_iterable_to_look_for_is_empty_whatever_custom_comparison_strategy_is() {
        arraysWithCustomComparisonStrategy.assertContainsAll(TestData.someInfo(), actual, Lists.newArrayList());
    }

    @Test
    public void should_throw_error_if_iterable_to_look_for_is_null_whatever_custom_comparison_strategy_is() {
        Assertions.assertThatNullPointerException().isThrownBy(() -> arraysWithCustomComparisonStrategy.assertContainsAll(someInfo(), actual, null)).withMessage(ErrorMessages.iterableToLookForIsNull());
    }

    @Test
    public void should_fail_if_actual_does_not_contain_values_according_to_custom_comparison_strategy() {
        AssertionInfo info = TestData.someInfo();
        List<String> expected = Lists.newArrayList("Han", "LUKE");
        try {
            arraysWithCustomComparisonStrategy.assertContainsAll(info, actual, expected);
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, ShouldContain.shouldContain(actual, expected.toArray(), Sets.newLinkedHashSet("Han"), caseInsensitiveStringComparisonStrategy));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }
}

