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
package org.assertj.core.internal.objects;


import org.assertj.core.api.AssertionInfo;
import org.assertj.core.api.Assertions;
import org.assertj.core.error.ShouldHaveToString;
import org.assertj.core.internal.ObjectsBaseTest;
import org.assertj.core.test.Person;
import org.assertj.core.test.TestData;
import org.assertj.core.test.TestFailures;
import org.assertj.core.util.FailureMessages;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


public class Objects_assertHasToString_Test extends ObjectsBaseTest {
    private Person actual;

    @Test
    public void should_pass_if_actual_toString_is_the_expected_String() {
        objects.assertHasToString(TestData.someInfo(), actual, "foo");
    }

    @Test
    public void should_fail_if_actual_is_null() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> objects.assertHasToString(someInfo(), null, "foo")).withMessage(FailureMessages.actualIsNull());
    }

    @Test
    public void should_fail_if_actual_toString_is_not_the_expected_String() {
        AssertionInfo info = TestData.someInfo();
        try {
            objects.assertHasToString(info, actual, "bar");
            TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
        } catch (AssertionError err) {
            Mockito.verify(failures).failure(info, ShouldHaveToString.shouldHaveToString(actual, "bar"));
        }
    }
}

