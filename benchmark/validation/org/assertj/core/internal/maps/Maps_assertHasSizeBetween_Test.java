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
package org.assertj.core.internal.maps;


import org.assertj.core.api.Assertions;
import org.assertj.core.error.ShouldHaveSizeBetween;
import org.assertj.core.internal.MapsBaseTest;
import org.assertj.core.test.TestData;
import org.assertj.core.util.FailureMessages;
import org.junit.jupiter.api.Test;


public class Maps_assertHasSizeBetween_Test extends MapsBaseTest {
    @Test
    public void should_fail_if_actual_is_null() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> maps.assertHasSizeBetween(someInfo(), null, 0, 6)).withMessage(FailureMessages.actualIsNull());
    }

    @Test
    public void should_throw_illegal_argument_exception_if_lower_boundary_is_greater_than_higher_boundary() {
        Assertions.assertThatIllegalArgumentException().isThrownBy(() -> maps.assertHasSizeBetween(someInfo(), actual, 4, 2)).withMessage("The higher boundary <2> must be greater than the lower boundary <4>.");
    }

    @Test
    public void should_fail_if_size_of_actual_is_not_greater_than_or_equal_to_lower_boundary() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> maps.assertHasSizeBetween(someInfo(), actual, 4, 6)).withMessage(ShouldHaveSizeBetween.shouldHaveSizeBetween(actual, actual.size(), 4, 6).create());
    }

    @Test
    public void should_fail_if_size_of_actual_is_not_less_than_higher_boundary() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> maps.assertHasSizeBetween(someInfo(), actual, 0, 1)).withMessage(ShouldHaveSizeBetween.shouldHaveSizeBetween(actual, actual.size(), 0, 1).create());
    }

    @Test
    public void should_pass_if_size_of_actual_is_between_boundaries() {
        maps.assertHasSizeBetween(TestData.someInfo(), actual, 1, 6);
        maps.assertHasSizeBetween(TestData.someInfo(), actual, actual.size(), actual.size());
    }
}

