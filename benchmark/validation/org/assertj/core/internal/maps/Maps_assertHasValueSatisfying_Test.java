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
package org.assertj.core.internal.maps;


import org.assertj.core.api.AssertionInfo;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.Condition;
import org.assertj.core.error.ShouldContainValue;
import org.assertj.core.internal.MapsBaseTest;
import org.assertj.core.test.TestData;
import org.assertj.core.test.TestFailures;
import org.assertj.core.util.FailureMessages;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


/**
 * Tests for <code>{@link Maps#assertHasValueSatisfying(AssertionInfo, Map, Condition)} (AssertionInfo, Map, Condition)}</code>.
 */
public class Maps_assertHasValueSatisfying_Test extends MapsBaseTest {
    private Condition<String> isGreen = new Condition<String>("green color condition") {
        @Override
        public boolean matches(String value) {
            return "green".equals(value);
        }
    };

    private Condition<Object> isBlack = new Condition<Object>("black color condition") {
        @Override
        public boolean matches(Object value) {
            return "black".equals(value);
        }
    };

    @Test
    public void should_fail_if_condition_is_null() {
        Assertions.assertThatNullPointerException().isThrownBy(() -> maps.assertHasValueSatisfying(someInfo(), actual, null)).withMessage("The condition to evaluate should not be null");
    }

    @Test
    public void should_fail_if_actual_is_null() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> maps.assertHasValueSatisfying(someInfo(), null, isGreen)).withMessage(FailureMessages.actualIsNull());
    }

    @Test
    public void should_fail_if_actual_does_not_contain_value_matching_condition() {
        AssertionInfo info = TestData.someInfo();
        try {
            maps.assertHasValueSatisfying(info, actual, isBlack);
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, ShouldContainValue.shouldContainValue(actual, isBlack));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }

    @Test
    public void should_pass_if_actual_contains_a_value_matching_the_given_condition() {
        maps.assertHasValueSatisfying(TestData.someInfo(), actual, isGreen);
    }
}

