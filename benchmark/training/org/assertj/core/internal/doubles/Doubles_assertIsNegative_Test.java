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
 * Tests for <code>{@link Doubles#assertIsNegative(AssertionInfo, Double)}</code>.
 *
 * @author Alex Ruiz
 * @author Joel Costigliola
 */
public class Doubles_assertIsNegative_Test extends DoublesBaseTest {
    @Test
    public void should_succeed_since_actual_is_negative() {
        doubles.assertIsNegative(TestData.someInfo(), (-6.0));
    }

    @Test
    public void should_fail_since_actual_is_not_negative() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> doubles.assertIsNegative(someInfo(), 6.0)).withMessage(String.format("%nExpecting:%n <6.0>%nto be less than:%n <0.0> "));
    }

    @Test
    public void should_fail_since_actual_is_not_negative_according_to_absolute_value_comparison_strategy() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> doublesWithAbsValueComparisonStrategy.assertIsNegative(someInfo(), (-6.0))).withMessage(String.format("%nExpecting:%n <-6.0>%nto be less than:%n <0.0> when comparing values using AbsValueComparator"));
    }

    @Test
    public void should_fail_since_actual_is_not_negative_according_to_absolute_value_comparison_strategy2() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> doublesWithAbsValueComparisonStrategy.assertIsNegative(someInfo(), 6.0)).withMessage(String.format("%nExpecting:%n <6.0>%nto be less than:%n <0.0> when comparing values using AbsValueComparator"));
    }
}

