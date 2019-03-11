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
import org.assertj.core.error.ShouldNotBeIn;
import org.assertj.core.internal.ErrorMessages;
import org.assertj.core.internal.ObjectsBaseTest;
import org.assertj.core.test.TestData;
import org.assertj.core.test.TestFailures;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


/**
 * Tests for <code>{@link Objects#assertIsNotIn(AssertionInfo, Object, Iterable)}</code>.
 *
 * @author Joel Costigliola
 * @author Alex Ruiz
 * @author Yvonne Wang
 * @author Nicolas Fran?ois
 */
public class Objects_assertIsNotIn_with_Iterable_Test extends ObjectsBaseTest {
    private static Iterable<String> values;

    @Test
    public void should_throw_error_if_Iterable_is_null() {
        Assertions.assertThatNullPointerException().isThrownBy(() -> {
            Iterable<String> c = null;
            objects.assertIsNotIn(someInfo(), "Luke", c);
        }).withMessage(ErrorMessages.iterableIsNull());
    }

    @Test
    public void should_throw_error_if_Iterable_is_empty() {
        Assertions.assertThatIllegalArgumentException().isThrownBy(() -> objects.assertIsNotIn(someInfo(), "Luke", emptyList())).withMessage(ErrorMessages.iterableIsEmpty());
    }

    @Test
    public void should_pass_if_actual_is_not_in_Iterable() {
        objects.assertIsNotIn(TestData.someInfo(), "Luke", Objects_assertIsNotIn_with_Iterable_Test.values);
    }

    @Test
    public void should_pass_if_actual_is_null_and_array_does_not_contain_null() {
        objects.assertIsNotIn(TestData.someInfo(), null, Objects_assertIsNotIn_with_Iterable_Test.values);
    }

    @Test
    public void should_fail_if_actual_is_in_Iterable() {
        AssertionInfo info = TestData.someInfo();
        try {
            objects.assertIsNotIn(info, "Yoda", Objects_assertIsNotIn_with_Iterable_Test.values);
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, ShouldNotBeIn.shouldNotBeIn("Yoda", Objects_assertIsNotIn_with_Iterable_Test.values));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }

    @Test
    public void should_pass_if_actual_is_not_in_Iterable_according_to_custom_comparison_strategy() {
        objectsWithCustomComparisonStrategy.assertIsNotIn(TestData.someInfo(), "Luke", Objects_assertIsNotIn_with_Iterable_Test.values);
    }

    @Test
    public void should_fail_if_actual_is_in_Iterable_according_to_custom_comparison_strategy() {
        AssertionInfo info = TestData.someInfo();
        try {
            objectsWithCustomComparisonStrategy.assertIsNotIn(info, "YODA", Objects_assertIsNotIn_with_Iterable_Test.values);
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, ShouldNotBeIn.shouldNotBeIn("YODA", Objects_assertIsNotIn_with_Iterable_Test.values, customComparisonStrategy));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }
}

