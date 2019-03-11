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
package org.assertj.core.internal.shortarrays;


import org.assertj.core.api.AssertionInfo;
import org.assertj.core.api.Assertions;
import org.assertj.core.error.ShouldHaveSameSizeAs;
import org.assertj.core.internal.ShortArraysBaseTest;
import org.assertj.core.test.TestData;
import org.assertj.core.util.Arrays;
import org.assertj.core.util.FailureMessages;
import org.junit.jupiter.api.Test;


public class ShortArrays_assertHasSameSizeAs_with_Array_Test extends ShortArraysBaseTest {
    private static short[] actual;

    @Test
    public void should_fail_if_actual_is_null() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> arrays.assertHasSameSizeAs(someInfo(), null, array("Solo", "Leia"))).withMessage(FailureMessages.actualIsNull());
    }

    @Test
    public void should_fail_if_size_of_actual_is_not_equal_to_expected_size() {
        AssertionInfo info = TestData.someInfo();
        String[] other = Arrays.array("Solo", "Leia", "Yoda");
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> arrays.assertHasSameSizeAs(info, ShortArrays_assertHasSameSizeAs_with_Array_Test.actual, other)).withMessage(ShouldHaveSameSizeAs.shouldHaveSameSizeAs(ShortArrays_assertHasSameSizeAs_with_Array_Test.actual, ShortArrays_assertHasSameSizeAs_with_Array_Test.actual.length, other.length).create(null, info.representation()));
    }

    @Test
    public void should_pass_if_size_of_actual_is_equal_to_expected_size() {
        arrays.assertHasSameSizeAs(TestData.someInfo(), ShortArrays_assertHasSameSizeAs_with_Array_Test.actual, Arrays.array("Solo", "Leia"));
    }
}

