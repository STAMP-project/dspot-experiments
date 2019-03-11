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
package org.assertj.core.internal.bytes;


import org.assertj.core.api.Assertions;
import org.assertj.core.internal.BytesBaseTest;
import org.assertj.core.test.TestData;
import org.junit.jupiter.api.Test;


/**
 * Tests for <code>{@link Bytes#assertIsNegative(AssertionInfo, Comparable)}</code>.
 *
 * @author Alex Ruiz
 * @author Joel Costigliola
 */
public class Bytes_assertIsNegative_Test extends BytesBaseTest {
    @Test
    public void should_succeed_since_actual_is_negative() {
        bytes.assertIsNegative(TestData.someInfo(), ((byte) (-6)));
    }

    @Test
    public void should_fail_since_actual_is_not_negative() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> bytes.assertIsNegative(someInfo(), ((byte) (6)))).withMessage(String.format("%nExpecting:%n <6>%nto be less than:%n <0> "));
    }

    @Test
    public void should_fail_since_actual_is_not_negative_with_hex_representation() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> bytes.assertIsNegative(someHexInfo(), ((byte) (6)))).withMessage(String.format("%nExpecting:%n <0x06>%nto be less than:%n <0x00> "));
    }

    @Test
    public void should_fail_since_actual_is_not_negative_according_to_absolute_value_comparison_strategy() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> bytesWithAbsValueComparisonStrategy.assertIsNegative(someInfo(), ((byte) (-6)))).withMessage(String.format("%nExpecting:%n <-6>%nto be less than:%n <0> when comparing values using AbsValueComparator"));
    }

    @Test
    public void should_fail_since_actual_is_not_negative_according_to_absolute_value_comparison_strategy_in_hex_representation() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> bytesWithAbsValueComparisonStrategy.assertIsNegative(someHexInfo(), ((byte) (250)))).withMessage(String.format("%nExpecting:%n <0xFA>%nto be less than:%n <0x00> when comparing values using AbsValueComparator"));
    }

    @Test
    public void should_fail_since_actual_is_not_negative_according_to_absolute_value_comparison_strategy2() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> bytesWithAbsValueComparisonStrategy.assertIsNegative(someInfo(), ((byte) (6)))).withMessage(String.format("%nExpecting:%n <6>%nto be less than:%n <0> when comparing values using AbsValueComparator"));
    }

    @Test
    public void should_fail_since_actual_is_not_negative_according_to_absolute_value_comparison_strategy2_in_hex_representation() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> bytesWithAbsValueComparisonStrategy.assertIsNegative(someHexInfo(), ((byte) (6)))).withMessage(String.format("%nExpecting:%n <0x06>%nto be less than:%n <0x00> when comparing values using AbsValueComparator"));
    }
}

