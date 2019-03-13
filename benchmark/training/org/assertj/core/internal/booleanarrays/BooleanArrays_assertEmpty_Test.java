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
import org.assertj.core.error.ShouldBeEmpty;
import org.assertj.core.internal.BooleanArraysBaseTest;
import org.assertj.core.test.BooleanArrays;
import org.assertj.core.test.TestData;
import org.assertj.core.test.TestFailures;
import org.assertj.core.util.FailureMessages;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


/**
 * Tests for <code>{@link BooleanArrays#assertEmpty(AssertionInfo, boolean[])}</code>.
 *
 * @author Alex Ruiz
 * @author Joel Costigliola
 */
public class BooleanArrays_assertEmpty_Test extends BooleanArraysBaseTest {
    @Test
    public void should_fail_if_actual_is_null() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> arrays.assertEmpty(someInfo(), null)).withMessage(FailureMessages.actualIsNull());
    }

    @Test
    public void should_fail_if_actual_is_not_empty() {
        AssertionInfo info = TestData.someInfo();
        boolean[] actual = new boolean[]{ true, false };
        try {
            arrays.assertEmpty(info, actual);
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, ShouldBeEmpty.shouldBeEmpty(actual));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }

    @Test
    public void should_pass_if_actual_is_empty() {
        arrays.assertEmpty(TestData.someInfo(), BooleanArrays.emptyArray());
    }
}

