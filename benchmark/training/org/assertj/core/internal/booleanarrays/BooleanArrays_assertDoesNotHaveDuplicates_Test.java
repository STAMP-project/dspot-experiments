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
import org.assertj.core.error.ShouldNotHaveDuplicates;
import org.assertj.core.internal.BooleanArraysBaseTest;
import org.assertj.core.test.BooleanArrays;
import org.assertj.core.test.TestData;
import org.assertj.core.test.TestFailures;
import org.assertj.core.util.FailureMessages;
import org.assertj.core.util.Sets;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


/**
 * Tests for <code>{@link BooleanArrays#assertDoesNotHaveDuplicates(AssertionInfo, boolean[])}</code>.
 *
 * @author Alex Ruiz
 * @author Joel Costigliola
 */
public class BooleanArrays_assertDoesNotHaveDuplicates_Test extends BooleanArraysBaseTest {
    @Test
    public void should_pass_if_actual_does_not_have_duplicates() {
        arrays.assertDoesNotHaveDuplicates(TestData.someInfo(), actual);
    }

    @Test
    public void should_pass_if_actual_is_empty() {
        arrays.assertDoesNotHaveDuplicates(TestData.someInfo(), BooleanArrays.emptyArray());
    }

    @Test
    public void should_fail_if_actual_is_null() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> arrays.assertDoesNotHaveDuplicates(someInfo(), null)).withMessage(FailureMessages.actualIsNull());
    }

    @Test
    public void should_fail_if_actual_contains_duplicates() {
        actual = BooleanArrays.arrayOf(true, true, false);
        AssertionInfo info = TestData.someInfo();
        try {
            arrays.assertDoesNotHaveDuplicates(info, actual);
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, ShouldNotHaveDuplicates.shouldNotHaveDuplicates(actual, Sets.newLinkedHashSet(true)));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }
}

