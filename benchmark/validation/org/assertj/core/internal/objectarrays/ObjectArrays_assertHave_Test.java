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
import org.assertj.core.error.ElementsShouldHave;
import org.assertj.core.internal.ObjectArraysBaseTest;
import org.assertj.core.internal.ObjectArraysWithConditionBaseTest;
import org.assertj.core.test.TestData;
import org.assertj.core.test.TestFailures;
import org.assertj.core.util.Arrays;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


/**
 * Tests for
 * <code>{@link ObjectArrays#assertHave(org.assertj.core.api.AssertionInfo, Object[], org.assertj.core.api.Condition)}</code> .
 *
 * @author Nicolas Fran?ois
 * @author Mikhail Mazursky
 * @author Joel Costigliola
 */
public class ObjectArrays_assertHave_Test extends ObjectArraysWithConditionBaseTest {
    @Test
    public void should_pass_if_each_element_satisfies_condition() {
        actual = Arrays.array("Yoda", "Luke");
        arrays.assertHave(TestData.someInfo(), actual, jediPower);
        Mockito.verify(conditions).assertIsNotNull(jediPower);
    }

    @Test
    public void should_throw_error_if_condition_is_null() {
        Assertions.assertThatNullPointerException().isThrownBy(() -> arrays.assertHave(someInfo(), actual, null)).withMessage("The condition to evaluate should not be null");
        Mockito.verify(conditions).assertIsNotNull(null);
    }

    @Test
    public void should_fail_if_Condition_is_not_met() {
        testCondition.shouldMatch(false);
        AssertionInfo info = TestData.someInfo();
        try {
            actual = Arrays.array("Yoda", "Luke", "Leia");
            arrays.assertHave(TestData.someInfo(), actual, jediPower);
        } catch (AssertionError e) {
            Mockito.verify(conditions).assertIsNotNull(jediPower);
            Mockito.verify(failures).failure(info, ElementsShouldHave.elementsShouldHave(actual, Lists.newArrayList("Leia"), jediPower));
            Mockito.verify(failures).failure(info, ElementsShouldHave.elementsShouldHave(actual, Lists.newArrayList("Leia"), jediPower));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }
}

