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
package org.assertj.core.internal.objectarrays;


import org.assertj.core.api.AssertionInfo;
import org.assertj.core.api.Assertions;
import org.assertj.core.error.ElementsShouldHaveExactly;
import org.assertj.core.internal.ObjectArraysBaseTest;
import org.assertj.core.internal.ObjectArraysWithConditionBaseTest;
import org.assertj.core.test.TestData;
import org.assertj.core.test.TestFailures;
import org.assertj.core.util.Arrays;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


/**
 * Tests for <code>{@link ObjectArrays#assertHaveExactly(AssertionInfo, Object[], int, Condition)}</code> .
 *
 * @author Nicolas Fran?ois
 * @author Mikhail Mazursky
 * @author Joel Costigliola
 */
public class ObjectArrays_assertHaveExactly_Test extends ObjectArraysWithConditionBaseTest {
    @Test
    public void should_pass_if_satisfies_exactly_times_condition() {
        actual = Arrays.array("Yoda", "Luke", "Leia");
        arrays.assertHaveExactly(TestData.someInfo(), actual, 2, jediPower);
        Mockito.verify(conditions).assertIsNotNull(jediPower);
    }

    @Test
    public void should_throw_error_if_condition_is_null() {
        Assertions.assertThatNullPointerException().isThrownBy(() -> {
            actual = array("Yoda", "Luke");
            arrays.assertHaveExactly(someInfo(), actual, 2, null);
        }).withMessage("The condition to evaluate should not be null");
        Mockito.verify(conditions).assertIsNotNull(null);
    }

    @Test
    public void should_fail_if_condition_is_not_met_enough() {
        testCondition.shouldMatch(false);
        AssertionInfo info = TestData.someInfo();
        try {
            actual = Arrays.array("Yoda", "Solo", "Leia");
            arrays.assertHaveExactly(TestData.someInfo(), actual, 2, jediPower);
        } catch (AssertionError e) {
            Mockito.verify(conditions).assertIsNotNull(jediPower);
            Mockito.verify(failures).failure(info, ElementsShouldHaveExactly.elementsShouldHaveExactly(actual, 2, jediPower));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }

    @Test
    public void should_fail_if_condition_is_met_much() {
        testCondition.shouldMatch(false);
        AssertionInfo info = TestData.someInfo();
        try {
            actual = Arrays.array("Yoda", "Luke", "Obiwan");
            arrays.assertHaveExactly(TestData.someInfo(), actual, 2, jediPower);
        } catch (AssertionError e) {
            Mockito.verify(conditions).assertIsNotNull(jediPower);
            Mockito.verify(failures).failure(info, ElementsShouldHaveExactly.elementsShouldHaveExactly(actual, 2, jediPower));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }
}

