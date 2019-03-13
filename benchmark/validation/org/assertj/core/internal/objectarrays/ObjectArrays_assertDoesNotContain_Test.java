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
import org.assertj.core.error.ShouldNotContain;
import org.assertj.core.internal.ErrorMessages;
import org.assertj.core.internal.ObjectArraysBaseTest;
import org.assertj.core.test.TestData;
import org.assertj.core.test.TestFailures;
import org.assertj.core.util.Arrays;
import org.assertj.core.util.FailureMessages;
import org.assertj.core.util.Sets;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


/**
 * Tests for <code>{@link ObjectArrays#assertDoesNotContain(AssertionInfo, Object[], Object[])}</code>.
 *
 * @author Alex Ruiz
 * @author Joel Costigliola
 */
public class ObjectArrays_assertDoesNotContain_Test extends ObjectArraysBaseTest {
    @Test
    public void should_pass_if_actual_does_not_contain_given_values() {
        arrays.assertDoesNotContain(TestData.someInfo(), actual, Arrays.array("Han"));
    }

    @Test
    public void should_pass_if_actual_does_not_contain_given_values_even_if_duplicated() {
        arrays.assertDoesNotContain(TestData.someInfo(), actual, Arrays.array("Han", "Han", "Anakin"));
    }

    @Test
    public void should_throw_error_if_array_of_values_to_look_for_is_empty() {
        Assertions.assertThatIllegalArgumentException().isThrownBy(() -> arrays.assertDoesNotContain(someInfo(), actual, emptyArray())).withMessage(ErrorMessages.valuesToLookForIsEmpty());
    }

    @Test
    public void should_throw_error_if_array_of_values_to_look_for_is_null() {
        Assertions.assertThatNullPointerException().isThrownBy(() -> arrays.assertDoesNotContain(someInfo(), actual, null)).withMessage(ErrorMessages.valuesToLookForIsNull());
    }

    @Test
    public void should_fail_if_actual_is_null() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> arrays.assertDoesNotContain(someInfo(), null, array("Yoda"))).withMessage(FailureMessages.actualIsNull());
    }

    @Test
    public void should_fail_if_actual_contains_given_values() {
        AssertionInfo info = TestData.someInfo();
        Object[] expected = new Object[]{ "Luke", "Yoda", "Han" };
        try {
            arrays.assertDoesNotContain(info, actual, expected);
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, ShouldNotContain.shouldNotContain(actual, expected, Sets.newLinkedHashSet("Luke", "Yoda")));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }

    @Test
    public void should_pass_if_actual_does_not_contain_given_values_according_to_custom_comparison_strategy() {
        arraysWithCustomComparisonStrategy.assertDoesNotContain(TestData.someInfo(), actual, Arrays.array("Han"));
    }

    @Test
    public void should_pass_if_actual_does_not_contain_given_values_even_if_duplicated_according_to_custom_comparison_strategy() {
        arraysWithCustomComparisonStrategy.assertDoesNotContain(TestData.someInfo(), actual, Arrays.array("Han", "HAn", "Anakin"));
    }

    @Test
    public void should_throw_error_if_array_of_values_to_look_for_is_null_whatever_custom_comparison_strategy_is() {
        Assertions.assertThatNullPointerException().isThrownBy(() -> arraysWithCustomComparisonStrategy.assertDoesNotContain(someInfo(), actual, null)).withMessage(ErrorMessages.valuesToLookForIsNull());
    }

    @Test
    public void should_fail_if_actual_contains_given_values_according_to_custom_comparison_strategy() {
        AssertionInfo info = TestData.someInfo();
        Object[] expected = new Object[]{ "LUKE", "Yoda", "Han" };
        try {
            arraysWithCustomComparisonStrategy.assertDoesNotContain(info, actual, expected);
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, ShouldNotContain.shouldNotContain(actual, expected, Sets.newLinkedHashSet("LUKE", "Yoda"), caseInsensitiveStringComparisonStrategy));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }
}

