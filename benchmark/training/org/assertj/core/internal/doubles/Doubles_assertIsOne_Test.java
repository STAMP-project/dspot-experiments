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
 * Tests for <code>{@link Doubles#assertIsOne(AssertionInfo, Number)}}</code>.
 *
 * @author Drummond Dawson
 */
public class Doubles_assertIsOne_Test extends DoublesBaseTest {
    @Test
    public void should_succeed_since_actual_is_one() {
        doubles.assertIsOne(TestData.someInfo(), 1.0);
    }

    @Test
    public void should_fail_since_actual_is_not_one() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> doubles.assertIsOne(someInfo(), 0.0)).withMessage(String.format("%nExpecting:%n <0.0>%nto be equal to:%n <1.0>%nbut was not."));
    }

    @Test
    public void should_succeed_since_actual_is_one_whatever_custom_comparison_strategy_is() {
        doublesWithAbsValueComparisonStrategy.assertIsOne(TestData.someInfo(), 1.0);
    }

    @Test
    public void should_fail_since_actual_is_not_one_whatever_custom_comparison_strategy_is() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> doublesWithAbsValueComparisonStrategy.assertIsOne(someInfo(), 0.0)).withMessage(String.format("%nExpecting:%n <0.0>%nto be equal to:%n <1.0>%nbut was not."));
    }
}

