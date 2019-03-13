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
package org.assertj.core.internal.booleanarrays;


import org.assertj.core.api.AssertionInfo;
import org.assertj.core.api.Assertions;
import org.assertj.core.internal.BooleanArraysBaseTest;
import org.assertj.core.internal.ErrorMessages;
import org.assertj.core.test.BooleanArrays;
import org.assertj.core.test.TestData;
import org.assertj.core.test.TestFailures;
import org.assertj.core.util.FailureMessages;
import org.junit.jupiter.api.Test;


/**
 * Tests for <code>{@link BooleanArrays#assertStartsWith(AssertionInfo, boolean[], boolean[])}</code>.
 *
 * @author Alex Ruiz
 * @author Joel Costigliola
 */
public class BooleanArrays_assertStartsWith_Test extends BooleanArraysBaseTest {
    @Test
    public void should_throw_error_if_sequence_is_null() {
        Assertions.assertThatNullPointerException().isThrownBy(() -> arrays.assertStartsWith(someInfo(), actual, null)).withMessage(ErrorMessages.valuesToLookForIsNull());
    }

    @Test
    public void should_pass_if_actual_and_given_values_are_empty() {
        actual = BooleanArrays.emptyArray();
        arrays.assertContains(TestData.someInfo(), actual, BooleanArrays.emptyArray());
    }

    @Test
    public void should_fail_if_array_of_values_to_look_for_is_empty_and_actual_is_not() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> arrays.assertStartsWith(someInfo(), actual, emptyArray()));
    }

    @Test
    public void should_fail_if_actual_is_null() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> arrays.assertStartsWith(someInfo(), null, arrayOf(true))).withMessage(FailureMessages.actualIsNull());
    }

    @Test
    public void should_fail_if_sequence_is_bigger_than_actual() {
        AssertionInfo info = TestData.someInfo();
        boolean[] sequence = new boolean[]{ true, false, false, true, true, false };
        try {
            arrays.assertStartsWith(info, actual, sequence);
        } catch (AssertionError e) {
            verifyFailureThrownWhenSequenceNotFound(info, sequence);
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }

    @Test
    public void should_fail_if_actual_does_not_start_with_sequence() {
        AssertionInfo info = TestData.someInfo();
        boolean[] sequence = new boolean[]{ false, true };
        try {
            arrays.assertStartsWith(info, actual, sequence);
        } catch (AssertionError e) {
            verifyFailureThrownWhenSequenceNotFound(info, sequence);
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }

    @Test
    public void should_fail_if_actual_starts_with_first_elements_of_sequence_only() {
        AssertionInfo info = TestData.someInfo();
        boolean[] sequence = new boolean[]{ true, true };
        try {
            arrays.assertStartsWith(info, actual, sequence);
        } catch (AssertionError e) {
            verifyFailureThrownWhenSequenceNotFound(info, sequence);
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }

    @Test
    public void should_pass_if_actual_starts_with_sequence() {
        arrays.assertStartsWith(TestData.someInfo(), actual, BooleanArrays.arrayOf(true, false));
    }

    @Test
    public void should_pass_if_actual_and_sequence_are_equal() {
        actual = BooleanArrays.arrayOf(true, false, false, true);
        arrays.assertStartsWith(TestData.someInfo(), actual, BooleanArrays.arrayOf(true, false, false, true));
    }
}

