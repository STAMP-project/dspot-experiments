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
package org.assertj.core.internal.iterables;


import java.util.List;
import org.assertj.core.api.AssertionInfo;
import org.assertj.core.api.Assertions;
import org.assertj.core.error.ShouldNotContain;
import org.assertj.core.internal.ErrorMessages;
import org.assertj.core.internal.IterablesBaseTest;
import org.assertj.core.test.TestData;
import org.assertj.core.test.TestFailures;
import org.assertj.core.util.FailureMessages;
import org.assertj.core.util.Lists;
import org.assertj.core.util.Sets;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


/**
 * Tests for <code>{@link Iterables#assertDoesNotContainAnyElementsOf(AssertionInfo, Iterable, Iterable)}</code>.
 *
 * @author Alex Ruiz
 * @author Joel Costigliola
 * @author William Delanoue
 */
public class Iterables_assertDoesNotContainAnyElementsOf_Test extends IterablesBaseTest {
    @Test
    public void should_pass_if_actual_does_not_contain_any_elements_of_given_iterable() {
        iterables.assertDoesNotContainAnyElementsOf(TestData.someInfo(), actual, Lists.newArrayList("Han"));
    }

    @Test
    public void should_pass_if_actual_does_not_contain_any_elements_of_given_iterable_even_if_duplicated() {
        iterables.assertDoesNotContainAnyElementsOf(TestData.someInfo(), actual, Lists.newArrayList("Han", "Han", "Anakin"));
    }

    @Test
    public void should_throw_error_if_given_iterable_is_empty() {
        Assertions.assertThatIllegalArgumentException().isThrownBy(() -> iterables.assertDoesNotContainAnyElementsOf(someInfo(), actual, emptyList())).withMessage(ErrorMessages.iterableValuesToLookForIsEmpty());
    }

    @Test
    public void should_throw_error_if_given_iterable_is_null() {
        Assertions.assertThatNullPointerException().isThrownBy(() -> iterables.assertDoesNotContainAnyElementsOf(someInfo(), actual, null)).withMessage(ErrorMessages.iterableValuesToLookForIsNull());
    }

    @Test
    public void should_fail_if_actual_is_null() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> iterables.assertDoesNotContainAnyElementsOf(someInfo(), null, newArrayList("Yoda"))).withMessage(FailureMessages.actualIsNull());
    }

    @Test
    public void should_fail_if_actual_contains_one_element_of_given_iterable() {
        AssertionInfo info = TestData.someInfo();
        List<String> list = Lists.newArrayList("Vador", "Yoda", "Han");
        try {
            iterables.assertDoesNotContainAnyElementsOf(info, actual, list);
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, ShouldNotContain.shouldNotContain(actual, list.toArray(), Sets.newLinkedHashSet("Yoda")));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }

    // ------------------------------------------------------------------------------------------------------------------
    // tests using a custom comparison strategy
    // ------------------------------------------------------------------------------------------------------------------
    @Test
    public void should_pass_if_actual_does_not_contain_any_elements_of_given_iterable_according_to_custom_comparison_strategy() {
        iterablesWithCaseInsensitiveComparisonStrategy.assertDoesNotContainAnyElementsOf(TestData.someInfo(), actual, Lists.newArrayList("Han"));
    }

    @Test
    public void should_pass_if_actual_does_not_contain_any_elements_of_given_iterable_even_if_duplicated_according_to_custom_comparison_strategy() {
        iterablesWithCaseInsensitiveComparisonStrategy.assertDoesNotContainAnyElementsOf(TestData.someInfo(), actual, Lists.newArrayList("Han", "Han", "Anakin"));
    }

    @Test
    public void should_fail_if_actual_contains_one_element_of_given_iterable_according_to_custom_comparison_strategy() {
        AssertionInfo info = TestData.someInfo();
        List<String> expected = Lists.newArrayList("LuKe", "YODA", "Han");
        try {
            iterablesWithCaseInsensitiveComparisonStrategy.assertDoesNotContainAnyElementsOf(info, actual, expected);
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, ShouldNotContain.shouldNotContain(actual, expected.toArray(), Sets.newLinkedHashSet("LuKe", "YODA"), comparisonStrategy));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }
}

