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


import java.util.HashMap;
import java.util.Map;
import org.assertj.core.api.AssertionInfo;
import org.assertj.core.api.Assertions;
import org.assertj.core.error.ShouldContainValues;
import org.assertj.core.internal.MapsBaseTest;
import org.assertj.core.test.TestData;
import org.assertj.core.test.TestFailures;
import org.assertj.core.util.FailureMessages;
import org.assertj.core.util.Sets;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


/**
 * Tests for <code>{@link org.assertj.core.internal.Maps#assertContainsValue(org.assertj.core.api.AssertionInfo, java.util.Map, Object)}</code>.
 *
 * @author Nicolas Fran?ois
 * @author Joel Costigliola
 * @author Alexander Bischof
 */
public class Maps_assertContainsValues_Test extends MapsBaseTest {
    @Test
    public void should_pass_if_actual_contains_given_values() {
        maps.assertContainsValues(TestData.someInfo(), actual, "Yoda", "Jedi");
    }

    @Test
    public void should_fail_if_actual_is_null() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> maps.assertContainsValues(someInfo(), null, "Yoda")).withMessage(FailureMessages.actualIsNull());
    }

    @Test
    public void should_fail_if_value_is_null() {
        Assertions.assertThatNullPointerException().isThrownBy(() -> maps.assertContainsValues(someInfo(), actual, ((String[]) (null)))).withMessage("The array of values to look for should not be null");
    }

    @Test
    public void should_pass_if_actual_and_given_values_are_empty() {
        actual = new HashMap<>();
        maps.assertContainsValues(TestData.someInfo(), actual);
    }

    @Test
    public void should_success_if_values_contains_null() {
        maps.assertContainsValues(TestData.someInfo(), actual, "Yoda", null);
    }

    @Test
    public void should_fail_if_actual_does_not_contain_value() {
        AssertionInfo info = TestData.someInfo();
        String value = "veryOld";
        String value2 = "veryOld2";
        try {
            maps.assertContainsValues(info, actual, value, value2);
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, ShouldContainValues.shouldContainValues(actual, Sets.newLinkedHashSet(value, value2)));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }
}

