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
package org.assertj.core.internal.floatarrays;


import org.assertj.core.api.Assertions;
import org.assertj.core.error.ShouldHaveSize;
import org.assertj.core.internal.FloatArraysBaseTest;
import org.assertj.core.test.TestData;
import org.assertj.core.util.FailureMessages;
import org.junit.jupiter.api.Test;


/**
 * Tests for <code>{@link FloatArrays#assertHasSize(AssertionInfo, float[], int)}</code>.
 *
 * @author Alex Ruiz
 * @author Joel Costigliola
 */
public class FloatArrays_assertHasSize_Test extends FloatArraysBaseTest {
    @Test
    public void should_fail_if_actual_is_null() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> arrays.assertHasSize(someInfo(), null, 3)).withMessage(FailureMessages.actualIsNull());
    }

    @Test
    public void should_fail_if_size_of_actual_is_not_equal_to_expected_size() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> arrays.assertHasSize(someInfo(), actual, 6)).withMessage(ShouldHaveSize.shouldHaveSize(actual, actual.length, 6).create());
    }

    @Test
    public void should_pass_if_size_of_actual_is_equal_to_expected_size() {
        arrays.assertHasSize(TestData.someInfo(), actual, 3);
    }
}

