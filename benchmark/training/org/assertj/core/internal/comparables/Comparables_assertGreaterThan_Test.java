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


import org.assertj.core.api.AssertionInfo;
import org.assertj.core.api.Assertions;
import org.assertj.core.error.ShouldBeGreater;
import org.assertj.core.internal.ComparablesBaseTest;
import org.assertj.core.test.TestData;
import org.assertj.core.test.TestFailures;
import org.assertj.core.util.FailureMessages;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


/**
 * Tests for <code>{@link Comparables#assertGreaterThan(AssertionInfo, Comparable, Comparable)}</code>.
 *
 * @author Alex Ruiz
 * @author Joel Costigliola
 */
public class Comparables_assertGreaterThan_Test extends ComparablesBaseTest {
    @Test
    public void should_fail_if_actual_is_null() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> comparables.assertGreaterThan(someInfo(), null, 8)).withMessage(FailureMessages.actualIsNull());
    }

    @Test
    public void should_pass_if_actual_is_greater_than_other() {
        comparables.assertGreaterThan(TestData.someInfo(), 8, 6);
    }

    @Test
    public void should_fail_if_actual_is_equal_to_other() {
        AssertionInfo info = TestData.someInfo();
        try {
            comparables.assertGreaterThan(info, "Yoda", "Yoda");
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, ShouldBeGreater.shouldBeGreater("Yoda", "Yoda"));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }

    @Test
    public void should_fail_if_actual_is_less_than_other() {
        AssertionInfo info = TestData.someInfo();
        try {
            comparables.assertGreaterThan(info, 6, 8);
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, ShouldBeGreater.shouldBeGreater(6, 8));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }

    // ------------------------------------------------------------------------------------------------------------------
    // tests using a custom comparison strategy
    // ------------------------------------------------------------------------------------------------------------------
    @Test
    public void should_pass_if_actual_is_greater_than_other_according_to_custom_comparison_strategy() {
        comparablesWithCustomComparisonStrategy.assertGreaterThan(TestData.someInfo(), (-8), 6);
    }

    @Test
    public void should_fail_if_actual_is_equal_to_other_according_to_custom_comparison_strategy() {
        AssertionInfo info = TestData.someInfo();
        try {
            comparablesWithCustomComparisonStrategy.assertGreaterThan(info, 7, (-7));
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, ShouldBeGreater.shouldBeGreater(7, (-7), customComparisonStrategy));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }

    @Test
    public void should_fail_if_actual_is_less_than_other_according_to_custom_comparison_strategy() {
        AssertionInfo info = TestData.someInfo();
        try {
            comparablesWithCustomComparisonStrategy.assertGreaterThan(info, (-6), 8);
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, ShouldBeGreater.shouldBeGreater((-6), 8, customComparisonStrategy));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }
}

