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


import java.util.Arrays;
import org.assertj.core.api.AssertionInfo;
import org.assertj.core.api.Assertions;
import org.assertj.core.error.ShouldNotBeIn;
import org.assertj.core.internal.ErrorMessages;
import org.assertj.core.internal.ObjectsBaseTest;
import org.assertj.core.test.TestData;
import org.assertj.core.test.TestFailures;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


/**
 * Tests for <code>{@link Objects#assertIsNotIn(AssertionInfo, Object, Object[])}</code>.
 *
 * @author Joel Costigliola
 * @author Alex Ruiz
 * @author Yvonne Wang
 */
public class Objects_assertIsNotIn_with_array_Test extends ObjectsBaseTest {
    private static String[] values;

    @Test
    public void should_throw_error_if_array_is_null() {
        Assertions.assertThatNullPointerException().isThrownBy(() -> {
            Object[] array = null;
            objects.assertIsNotIn(someInfo(), "Yoda", array);
        }).withMessage(ErrorMessages.arrayIsNull());
    }

    @Test
    public void should_throw_error_if_array_is_empty() {
        Assertions.assertThatIllegalArgumentException().isThrownBy(() -> objects.assertIsNotIn(someInfo(), "Yoda", emptyArray())).withMessage(ErrorMessages.arrayIsEmpty());
    }

    @Test
    public void should_pass_if_actual_is_in_not_array() {
        objects.assertIsNotIn(TestData.someInfo(), "Luke", Objects_assertIsNotIn_with_array_Test.values);
    }

    @Test
    public void should_pass_if_actual_is_null_and_array_does_not_contain_null() {
        objects.assertIsNotIn(TestData.someInfo(), null, Objects_assertIsNotIn_with_array_Test.values);
    }

    @Test
    public void should_fail_if_actual_is_not_in_array() {
        AssertionInfo info = TestData.someInfo();
        try {
            objects.assertIsNotIn(info, "Yoda", Objects_assertIsNotIn_with_array_Test.values);
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, ShouldNotBeIn.shouldNotBeIn("Yoda", Arrays.asList(Objects_assertIsNotIn_with_array_Test.values)));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }

    @Test
    public void should_pass_if_actual_is_in_not_array_according_to_custom_comparison_strategy() {
        objectsWithCustomComparisonStrategy.assertIsNotIn(TestData.someInfo(), "Luke", Objects_assertIsNotIn_with_array_Test.values);
    }

    @Test
    public void should_fail_if_actual_is_not_in_array_according_to_custom_comparison_strategy() {
        AssertionInfo info = TestData.someInfo();
        try {
            objectsWithCustomComparisonStrategy.assertIsNotIn(info, "YODA", Objects_assertIsNotIn_with_array_Test.values);
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, ShouldNotBeIn.shouldNotBeIn("YODA", Arrays.asList(Objects_assertIsNotIn_with_array_Test.values), customComparisonStrategy));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }
}

