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
package org.assertj.core.internal.longs;


import org.assertj.core.api.Assertions;
import org.assertj.core.internal.LongsBaseTest;
import org.assertj.core.util.FailureMessages;
import org.junit.jupiter.api.Test;


public class Longs_assertIsNotCloseTo_Test extends LongsBaseTest {
    private static final Long ZERO = 0L;

    private static final Long ONE = 1L;

    @Test
    public void should_fail_if_actual_is_null() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> longs.assertIsNotCloseTo(someInfo(), null, ONE, byLessThan(ONE))).withMessage(FailureMessages.actualIsNull());
    }

    @Test
    public void should_fail_if_expected_value_is_null() {
        Assertions.assertThatNullPointerException().isThrownBy(() -> longs.assertIsNotCloseTo(someInfo(), ONE, null, byLessThan(ONE)));
    }

    @Test
    public void should_fail_if_offset_is_null() {
        Assertions.assertThatNullPointerException().isThrownBy(() -> longs.assertIsNotCloseTo(someInfo(), ONE, ZERO, null));
    }
}

