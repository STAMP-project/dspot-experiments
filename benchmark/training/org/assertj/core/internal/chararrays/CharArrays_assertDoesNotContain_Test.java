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
package org.assertj.core.internal.chararrays;


import org.assertj.core.api.AssertionInfo;
import org.assertj.core.api.Assertions;
import org.assertj.core.error.ShouldNotContain;
import org.assertj.core.internal.CharArraysBaseTest;
import org.assertj.core.internal.ErrorMessages;
import org.assertj.core.test.CharArrays;
import org.assertj.core.test.TestData;
import org.assertj.core.test.TestFailures;
import org.assertj.core.util.FailureMessages;
import org.assertj.core.util.Sets;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


/**
 * Tests for <code>{@link CharArrays#assertDoesNotContain(AssertionInfo, char[], char[])}</code>.
 *
 * @author Alex Ruiz
 * @author Joel Costigliola
 */
public class CharArrays_assertDoesNotContain_Test extends CharArraysBaseTest {
    @Test
    public void should_pass_if_actual_does_not_contain_given_values() {
        arrays.assertDoesNotContain(TestData.someInfo(), actual, CharArrays.arrayOf('d'));
    }

    @Test
    public void should_pass_if_actual_does_not_contain_given_values_even_if_duplicated() {
        arrays.assertDoesNotContain(TestData.someInfo(), actual, CharArrays.arrayOf('d', 'd', 'e'));
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
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> arrays.assertDoesNotContain(someInfo(), null, arrayOf('a'))).withMessage(FailureMessages.actualIsNull());
    }

    @Test
    public void should_fail_if_actual_contains_given_values() {
        AssertionInfo info = TestData.someInfo();
        char[] expected = new char[]{ 'a', 'b', 'd' };
        try {
            arrays.assertDoesNotContain(info, actual, expected);
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, ShouldNotContain.shouldNotContain(actual, expected, Sets.newLinkedHashSet('a', 'b')));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }

    @Test
    public void should_pass_if_actual_does_not_contain_given_values_according_to_custom_comparison_strategy() {
        arraysWithCustomComparisonStrategy.assertDoesNotContain(TestData.someInfo(), actual, CharArrays.arrayOf('d'));
    }

    @Test
    public void should_pass_if_actual_does_not_contain_given_values_even_if_duplicated_according_to_custom_comparison_strategy() {
        arraysWithCustomComparisonStrategy.assertDoesNotContain(TestData.someInfo(), actual, CharArrays.arrayOf('d', 'd', 'e'));
    }

    @Test
    public void should_throw_error_if_array_of_values_to_look_for_is_empty_whatever_custom_comparison_strategy_is() {
        Assertions.assertThatIllegalArgumentException().isThrownBy(() -> arraysWithCustomComparisonStrategy.assertDoesNotContain(someInfo(), actual, emptyArray())).withMessage(ErrorMessages.valuesToLookForIsEmpty());
    }

    @Test
    public void should_throw_error_if_array_of_values_to_look_for_is_null_whatever_custom_comparison_strategy_is() {
        Assertions.assertThatNullPointerException().isThrownBy(() -> arraysWithCustomComparisonStrategy.assertDoesNotContain(someInfo(), actual, null)).withMessage(ErrorMessages.valuesToLookForIsNull());
    }

    @Test
    public void should_fail_if_actual_is_null_whatever_custom_comparison_strategy_is() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> arraysWithCustomComparisonStrategy.assertDoesNotContain(someInfo(), null, arrayOf('A'))).withMessage(FailureMessages.actualIsNull());
    }

    @Test
    public void should_fail_if_actual_contains_given_values_according_to_custom_comparison_strategy() {
        AssertionInfo info = TestData.someInfo();
        char[] expected = new char[]{ 'A', 'b', 'd' };
        try {
            arraysWithCustomComparisonStrategy.assertDoesNotContain(info, actual, expected);
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, ShouldNotContain.shouldNotContain(actual, expected, Sets.newLinkedHashSet('A', 'b'), caseInsensitiveComparisonStrategy));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }
}

