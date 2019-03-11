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
import org.assertj.core.error.ShouldContain;
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
 * Tests for <code>{@link Iterables#assertContainsAll(AssertionInfo, Iterable, Iterable)}</code>.
 *
 * @author Joel Costigliola
 */
public class Iterables_assertContainsAll_Test extends IterablesBaseTest {
    @Test
    public void should_pass_if_actual_contains_all_iterable_values() {
        iterables.assertContainsAll(TestData.someInfo(), actual, Lists.newArrayList("Luke"));
        // order does not matter
        iterables.assertContainsAll(TestData.someInfo(), actual, Lists.newArrayList("Leia", "Yoda"));
    }

    @Test
    public void should_pass_if_actual_contains_all_iterable_values_more_than_once() {
        actual.addAll(Lists.newArrayList("Luke", "Luke"));
        iterables.assertContainsAll(TestData.someInfo(), actual, Lists.newArrayList("Luke"));
    }

    @Test
    public void should_pass_if_actual_contains_all_iterable_values_even_if_duplicated() {
        iterables.assertContainsAll(TestData.someInfo(), actual, Lists.newArrayList("Luke", "Luke"));
    }

    @Test
    public void should_throw_error_if_array_of_values_to_look_for_is_null() {
        Assertions.assertThatNullPointerException().isThrownBy(() -> iterables.assertContainsAll(someInfo(), actual, null)).withMessage(ErrorMessages.iterableToLookForIsNull());
    }

    @Test
    public void should_fail_if_actual_is_null() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> iterables.assertContainsAll(someInfo(), null, newArrayList("Yoda"))).withMessage(FailureMessages.actualIsNull());
    }

    @Test
    public void should_fail_if_actual_does_not_contain_values() {
        AssertionInfo info = TestData.someInfo();
        List<String> expected = Lists.newArrayList("Han", "Luke");
        try {
            iterables.assertContainsAll(info, actual, expected);
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, ShouldContain.shouldContain(actual, expected.toArray(), Sets.newLinkedHashSet("Han")));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }

    // ------------------------------------------------------------------------------------------------------------------
    // tests using a custom comparison strategy
    // ------------------------------------------------------------------------------------------------------------------
    @Test
    public void should_pass_if_actual_contains_all_iterable_values_according_to_custom_comparison_strategy() {
        iterablesWithCaseInsensitiveComparisonStrategy.assertContainsAll(TestData.someInfo(), actual, Lists.newArrayList("LUKE"));
    }

    @Test
    public void should_pass_if_actual_contains_all_iterable_values_in_different_order_according_to_custom_comparison_strategy() {
        iterablesWithCaseInsensitiveComparisonStrategy.assertContainsAll(TestData.someInfo(), actual, Lists.newArrayList("LEIA", "yODa"));
    }

    @Test
    public void should_pass_if_actual_contains_all_all_iterable_values_according_to_custom_comparison_strategy() {
        iterablesWithCaseInsensitiveComparisonStrategy.assertContainsAll(TestData.someInfo(), actual, Lists.newArrayList("luke", "YODA"));
    }

    @Test
    public void should_pass_if_actual_contains_all_iterable_values_more_than_once_according_to_custom_comparison_strategy() {
        actual.addAll(Lists.newArrayList("Luke", "Luke"));
        iterablesWithCaseInsensitiveComparisonStrategy.assertContainsAll(TestData.someInfo(), actual, Lists.newArrayList("LUke"));
    }

    @Test
    public void should_pass_if_actual_contains_all_iterable_values_even_if_duplicated_according_to_custom_comparison_strategy() {
        iterablesWithCaseInsensitiveComparisonStrategy.assertContainsAll(TestData.someInfo(), actual, Lists.newArrayList("LUke", "LuKe"));
    }

    @Test
    public void should_fail_if_actual_does_not_contain_values_according_to_custom_comparison_strategy() {
        AssertionInfo info = TestData.someInfo();
        List<String> expected = Lists.newArrayList("Han", "LUKE");
        try {
            iterablesWithCaseInsensitiveComparisonStrategy.assertContainsAll(info, actual, expected);
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, ShouldContain.shouldContain(actual, expected.toArray(), Sets.newLinkedHashSet("Han"), comparisonStrategy));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }
}

