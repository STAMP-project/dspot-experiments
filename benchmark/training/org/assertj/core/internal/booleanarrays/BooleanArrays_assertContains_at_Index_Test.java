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
import org.assertj.core.data.Index;
import org.assertj.core.error.ShouldContainAtIndex;
import org.assertj.core.internal.BooleanArraysBaseTest;
import org.assertj.core.test.TestData;
import org.assertj.core.test.TestFailures;
import org.assertj.core.util.FailureMessages;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


/**
 * Tests for <code>{@link BooleanArrays#assertContains(AssertionInfo, boolean[], boolean, Index)}</code>.
 *
 * @author Alex Ruiz
 * @author Joel Costigliola
 */
public class BooleanArrays_assertContains_at_Index_Test extends BooleanArraysBaseTest {
    @Test
    public void should_fail_if_actual_is_null() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> arrays.assertContains(someInfo(), null, true, someIndex())).withMessage(FailureMessages.actualIsNull());
    }

    @Test
    public void should_fail_if_actual_is_empty() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> arrays.assertContains(someInfo(), emptyArray(), true, someIndex())).withMessage(FailureMessages.actualIsEmpty());
    }

    @Test
    public void should_throw_error_if_Index_is_null() {
        Assertions.assertThatNullPointerException().isThrownBy(() -> arrays.assertContains(someInfo(), actual, true, null)).withMessage("Index should not be null");
    }

    @Test
    public void should_throw_error_if_Index_is_out_of_bounds() {
        Assertions.assertThatExceptionOfType(IndexOutOfBoundsException.class).isThrownBy(() -> arrays.assertContains(someInfo(), actual, true, atIndex(6))).withMessageContaining(String.format("Index should be between <0> and <1> (inclusive) but was:%n <6>"));
    }

    @Test
    public void should_fail_if_actual_does_not_contain_value_at_index() {
        AssertionInfo info = TestData.someInfo();
        boolean value = true;
        Index index = Index.atIndex(1);
        try {
            arrays.assertContains(info, actual, value, index);
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, ShouldContainAtIndex.shouldContainAtIndex(actual, value, index, false));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }

    @Test
    public void should_pass_if_actual_contains_value_at_index() {
        arrays.assertContains(TestData.someInfo(), actual, false, Index.atIndex(1));
    }
}

