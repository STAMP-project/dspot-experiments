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
 * Tests for <code>{@link Bytes#assertIsOne(AssertionInfo, Comparable)}</code>.
 *
 * @author Drummond Dawson
 */
public class Bytes_assertIsOne_Test extends BytesBaseTest {
    @Test
    public void should_succeed_since_actual_is_one() {
        bytes.assertIsOne(TestData.someInfo(), ((byte) (1)));
    }

    @Test
    public void should_fail_since_actual_is_not_one() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> bytes.assertIsOne(someInfo(), ((byte) (0)))).withMessage(String.format("%nExpecting:%n <0>%nto be equal to:%n <1>%nbut was not."));
    }

    @Test
    public void should_fail_since_actual_is_not_one_in_hex_representation() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> bytes.assertIsOne(someHexInfo(), ((byte) (0)))).withMessage(String.format("%nExpecting:%n <0x00>%nto be equal to:%n <0x01>%nbut was not."));
    }

    @Test
    public void should_succeed_since_actual_is_one_whatever_custom_comparison_strategy_is() {
        bytesWithAbsValueComparisonStrategy.assertIsOne(TestData.someInfo(), ((byte) (1)));
    }

    @Test
    public void should_succeed_since_actual_is_one_whatever_custom_comparison_strategy_is_in_hex_representation() {
        bytesWithAbsValueComparisonStrategy.assertIsOne(TestData.someHexInfo(), ((byte) (1)));
    }

    @Test
    public void should_fail_since_actual_is_not_one_whatever_custom_comparison_strategy_is() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> bytesWithAbsValueComparisonStrategy.assertIsOne(someInfo(), ((byte) (0)))).withMessage(String.format("%nExpecting:%n <0>%nto be equal to:%n <1>%nbut was not."));
    }

    @Test
    public void should_fail_since_actual_is_not_one_whatever_custom_comparison_strategy_is_in_hex_representation() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> bytesWithAbsValueComparisonStrategy.assertIsOne(someHexInfo(), ((byte) (0)))).withMessage(String.format("%nExpecting:%n <0x00>%nto be equal to:%n <0x01>%nbut was not."));
    }
}

