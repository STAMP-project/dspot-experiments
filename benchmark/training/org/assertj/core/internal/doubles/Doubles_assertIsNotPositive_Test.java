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
package org.assertj.core.internal.doubles;


import org.assertj.core.api.Assertions;
import org.assertj.core.internal.DoublesBaseTest;
import org.assertj.core.test.TestData;
import org.junit.jupiter.api.Test;


/**
 * Tests for <code>{@link org.assertj.core.internal.Doubles#assertIsNotPositive(org.assertj.core.api.AssertionInfo, Comparable)}</code>.
 *
 * @author Nicolas Fran?ois
 */
public class Doubles_assertIsNotPositive_Test extends DoublesBaseTest {
    @Test
    public void should_succeed_since_actual_is_not_positive() {
        doubles.assertIsNotPositive(TestData.someInfo(), (-6.0));
    }

    @Test
    public void should_succeed_since_actual_is_zero() {
        doubles.assertIsNotPositive(TestData.someInfo(), 0.0);
    }

    @Test
    public void should_fail_since_actual_is_positive() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> doubles.assertIsNotPositive(someInfo(), 6.0)).withMessage(String.format("%nExpecting:%n <6.0>%nto be less than or equal to:%n <0.0> "));
    }

    @Test
    public void should_fail_since_actual_can_be_positive_according_to_custom_comparison_strategy() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> doublesWithAbsValueComparisonStrategy.assertIsNotPositive(someInfo(), (-1.0))).withMessage(String.format("%nExpecting:%n <-1.0>%nto be less than or equal to:%n <0.0> when comparing values using AbsValueComparator"));
    }

    @Test
    public void should_fail_since_actual_is_positive_according_to_custom_comparison_strategy() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> doublesWithAbsValueComparisonStrategy.assertIsNotPositive(someInfo(), 1.0)).withMessage(String.format("%nExpecting:%n <1.0>%nto be less than or equal to:%n <0.0> when comparing values using AbsValueComparator"));
    }
}

