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
package org.assertj.core.internal.comparables;


import java.math.BigDecimal;
import org.assertj.core.api.AssertionInfo;
import org.assertj.core.api.Assertions;
import org.assertj.core.error.ShouldBeEqual;
import org.assertj.core.internal.ComparablesBaseTest;
import org.assertj.core.test.TestData;
import org.assertj.core.test.TestFailures;
import org.assertj.core.util.FailureMessages;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


/**
 * Tests for <code>{@link Comparables#assertEqualByComparison(AssertionInfo, Comparable, Comparable)}</code>.
 *
 * @author Alex Ruiz
 * @author Joel Costigliola
 */
public class Comparables_assertEqualByComparison_Test extends ComparablesBaseTest {
    @Test
    public void should_fail_if_actual_is_null() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> comparables.assertEqualByComparison(someInfo(), null, 8)).withMessage(FailureMessages.actualIsNull());
    }

    @Test
    public void should_pass_if_objects_are_equal() {
        BigDecimal a = new BigDecimal("10.0");
        BigDecimal e = new BigDecimal("10.000");
        // we use BigDecimal to ensure that 'compareTo' is being called, since BigDecimal is the only Comparable where
        // 'compareTo' is not consistent with 'equals'
        Assertions.assertThat(a.equals(e)).isFalse();
        comparables.assertEqualByComparison(TestData.someInfo(), a, e);
    }

    @Test
    public void should_fail_if_objects_are_not_equal() {
        AssertionInfo info = TestData.someInfo();
        try {
            comparables.assertEqualByComparison(info, "Luke", "Yoda");
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, ShouldBeEqual.shouldBeEqual("Luke", "Yoda", info.representation()));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }

    // ------------------------------------------------------------------------------------------------------------------
    // tests using a custom comparison strategy
    // ------------------------------------------------------------------------------------------------------------------
    @Test
    public void should_fail_if_actual_is_null_whatever_custom_comparison_strategy_is() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> comparablesWithCustomComparisonStrategy.assertEqualByComparison(someInfo(), null, BigDecimal.ONE)).withMessage(FailureMessages.actualIsNull());
    }

    @Test
    public void should_pass_if_objects_are_equal_whatever_custom_comparison_strategy_is() {
        BigDecimal a = new BigDecimal("10.0");
        BigDecimal e = new BigDecimal("10.000");
        // we use BigDecimal to ensure that 'compareTo' is being called, since BigDecimal is the only Comparable where
        // 'compareTo' is not consistent with 'equals'
        Assertions.assertThat(a.equals(e)).isFalse();
        comparablesWithCustomComparisonStrategy.assertEqualByComparison(TestData.someInfo(), a, e);
    }

    @Test
    public void should_fail_if_objects_are_not_equal_whatever_custom_comparison_strategy_is() {
        AssertionInfo info = TestData.someInfo();
        try {
            comparablesWithCustomComparisonStrategy.assertEqualByComparison(info, "Luke", "Yoda");
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, ShouldBeEqual.shouldBeEqual("Luke", "Yoda", info.representation()));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }
}

