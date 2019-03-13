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
import org.assertj.core.error.ElementsShouldBeAtMost;
import org.assertj.core.internal.ObjectArraysBaseTest;
import org.assertj.core.internal.ObjectArraysWithConditionBaseTest;
import org.assertj.core.test.TestData;
import org.assertj.core.test.TestFailures;
import org.assertj.core.util.Arrays;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


/**
 * Tests for <code>{@link ObjectArrays#assertAreAtMost(AssertionInfo, Object[], int, Condition)}</code> .
 *
 * @author Nicolas Fran?ois
 * @author Mikhail Mazursky
 * @author Joel Costigliola
 */
public class ObjectArrays_assertAreAtMost_Test extends ObjectArraysWithConditionBaseTest {
    @Test
    public void should_pass_if_satisfies_at_least_times_condition() {
        actual = Arrays.array("Yoda", "Luke", "Leia");
        arrays.assertAreAtMost(TestData.someInfo(), actual, 2, jedi);
        Mockito.verify(conditions).assertIsNotNull(jedi);
    }

    @Test
    public void should_pass_if_all_satisfies_condition_() {
        actual = Arrays.array("Chewbacca", "Leia", "Obiwan");
        arrays.assertAreAtMost(TestData.someInfo(), actual, 2, jedi);
        Mockito.verify(conditions).assertIsNotNull(jedi);
    }

    @Test
    public void should_throw_error_if_condition_is_null() {
        Assertions.assertThatNullPointerException().isThrownBy(() -> {
            actual = array("Yoda", "Luke");
            arrays.assertAreAtMost(someInfo(), actual, 2, null);
        }).withMessage("The condition to evaluate should not be null");
        Mockito.verify(conditions).assertIsNotNull(null);
    }

    @Test
    public void should_fail_if_condition_is_not_met_enough() {
        testCondition.shouldMatch(false);
        AssertionInfo info = TestData.someInfo();
        try {
            actual = Arrays.array("Yoda", "Luke", "Obiwan");
            arrays.assertAreAtMost(TestData.someInfo(), actual, 2, jedi);
        } catch (AssertionError e) {
            Mockito.verify(conditions).assertIsNotNull(jedi);
            Mockito.verify(failures).failure(info, ElementsShouldBeAtMost.elementsShouldBeAtMost(actual, 2, jedi));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }
}

