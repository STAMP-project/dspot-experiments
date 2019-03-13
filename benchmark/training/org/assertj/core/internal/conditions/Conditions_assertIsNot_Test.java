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
package org.assertj.core.internal.conditions;


import org.assertj.core.api.AssertionInfo;
import org.assertj.core.api.Assertions;
import org.assertj.core.error.ShouldNotBe;
import org.assertj.core.internal.ConditionsBaseTest;
import org.assertj.core.test.TestData;
import org.assertj.core.test.TestFailures;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


/**
 * Tests for <code>{@link Conditions#assertIsNot(AssertionInfo, Object, Condition)}</code>.
 *
 * @author Alex Ruiz
 * @author Yvonne Wang
 * @author Joel Costigliola
 */
public class Conditions_assertIsNot_Test extends ConditionsBaseTest {
    @Test
    public void should_throw_error_if_Condition_is_null() {
        Assertions.assertThatNullPointerException().isThrownBy(() -> conditions.assertIsNot(someInfo(), ConditionsBaseTest.actual, null)).withMessage("The condition to evaluate should not be null");
    }

    @Test
    public void should_pass_if_Condition_is_not_met() {
        condition.shouldMatch(false);
        conditions.assertIsNot(TestData.someInfo(), ConditionsBaseTest.actual, condition);
    }

    @Test
    public void should_fail_if_Condition_is_met() {
        condition.shouldMatch(true);
        AssertionInfo info = TestData.someInfo();
        try {
            conditions.assertIsNot(info, ConditionsBaseTest.actual, condition);
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, ShouldNotBe.shouldNotBe(ConditionsBaseTest.actual, condition));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }
}

