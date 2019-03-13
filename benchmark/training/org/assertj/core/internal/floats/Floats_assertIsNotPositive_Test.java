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
package org.assertj.core.internal.floats;


import org.assertj.core.api.Assertions;
import org.assertj.core.internal.FloatsBaseTest;
import org.assertj.core.test.TestData;
import org.junit.jupiter.api.Test;


/**
 * Tests for <code>{@link org.assertj.core.internal.Floats#assertIsNotPositive(org.assertj.core.api.AssertionInfo,
 * Comparable)} ())}</code>.
 *
 * @author Nicolas Fran?ois
 */
public class Floats_assertIsNotPositive_Test extends FloatsBaseTest {
    @Test
    public void should_succeed_since_actual_is_not_positive() {
        floats.assertIsNotPositive(TestData.someInfo(), (-6.0F));
    }

    @Test
    public void should_succeed_since_actual_is_zero() {
        floats.assertIsNotPositive(TestData.someInfo(), 0.0F);
    }

    @Test
    public void should_fail_since_actual_is_positive() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> floats.assertIsNotPositive(someInfo(), 6.0F)).withMessage(String.format("%nExpecting:%n <6.0f>%nto be less than or equal to:%n <0.0f> "));
    }

    @Test
    public void should_fail_since_actual_can_be_positive_according_to_custom_comparison_strategy() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> floatsWithAbsValueComparisonStrategy.assertIsNotPositive(someInfo(), (-1.0F))).withMessage(String.format("%nExpecting:%n <-1.0f>%nto be less than or equal to:%n <0.0f> when comparing values using AbsValueComparator"));
    }

    @Test
    public void should_fail_since_actual_is_positive_according_to_custom_comparison_strategy() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> floatsWithAbsValueComparisonStrategy.assertIsNotPositive(someInfo(), 1.0F)).withMessage(String.format("%nExpecting:%n <1.0f>%nto be less than or equal to:%n <0.0f> when comparing values using AbsValueComparator"));
    }
}

