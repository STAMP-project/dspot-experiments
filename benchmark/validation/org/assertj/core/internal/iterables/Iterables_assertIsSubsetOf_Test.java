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
import org.assertj.core.error.ShouldBeSubsetOf;
import org.assertj.core.internal.ErrorMessages;
import org.assertj.core.internal.IterablesBaseTest;
import org.assertj.core.test.TestData;
import org.assertj.core.test.TestFailures;
import org.assertj.core.util.FailureMessages;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


/**
 * Tests for <code>{@link Iterables#assertIsSubsetOf(AssertionInfo, Collection, Object[])}</code>.
 *
 * @author Maciej Jaskowski
 */
public class Iterables_assertIsSubsetOf_Test extends IterablesBaseTest {
    @Test
    public void should_pass_if_actual_is_subset_of_set() {
        actual = Lists.newArrayList("Yoda", "Luke");
        iterables.assertIsSubsetOf(TestData.someInfo(), actual, Lists.newArrayList("Luke", "Yoda", "Obi-Wan"));
    }

    @Test
    public void should_pass_if_actual_has_the_same_elements_as_set() {
        actual = Lists.newArrayList("Yoda", "Luke");
        iterables.assertIsSubsetOf(TestData.someInfo(), actual, Lists.newArrayList("Luke", "Yoda"));
    }

    @Test
    public void should_pass_if_actual_is_empty() {
        actual = Lists.newArrayList();
        iterables.assertIsSubsetOf(TestData.someInfo(), actual, Lists.newArrayList("Luke", "Yoda"));
    }

    @Test
    public void should_pass_if_actual_and_set_are_both_empty() {
        actual = Lists.newArrayList();
        iterables.assertIsSubsetOf(TestData.someInfo(), actual, Lists.newArrayList());
    }

    @Test
    public void should_pass_if_actual_has_duplicates_but_all_elements_are_in_values() {
        actual = Lists.newArrayList("Yoda", "Yoda");
        iterables.assertIsSubsetOf(TestData.someInfo(), actual, Lists.newArrayList("Yoda"));
    }

    @Test
    public void should_pass_if_values_has_duplicates_but_all_elements_are_in_values() {
        actual = Lists.newArrayList("Yoda", "C-3PO");
        iterables.assertIsSubsetOf(TestData.someInfo(), actual, Lists.newArrayList("Yoda", "Yoda", "C-3PO"));
    }

    @Test
    public void should_pass_if_both_actual_and_values_have_duplicates_but_all_elements_are_in_values() {
        actual = Lists.newArrayList("Yoda", "Yoda", "Yoda", "C-3PO", "Obi-Wan");
        iterables.assertIsSubsetOf(TestData.someInfo(), actual, Lists.newArrayList("Yoda", "Yoda", "C-3PO", "C-3PO", "Obi-Wan"));
    }

    @Test
    public void should_throw_error_if_set_is_null() {
        actual = Lists.newArrayList("Yoda", "Luke");
        Assertions.assertThatNullPointerException().isThrownBy(() -> iterables.assertIsSubsetOf(someInfo(), actual, null)).withMessage(ErrorMessages.iterableToLookForIsNull());
    }

    @Test
    public void should_throw_error_if_actual_is_null() {
        actual = null;
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> iterables.assertIsSubsetOf(someInfo(), actual, newArrayList())).withMessage(FailureMessages.actualIsNull());
    }

    @Test
    public void should_fail_if_actual_is_not_subset_of_values() {
        AssertionInfo info = TestData.someInfo();
        actual = Lists.newArrayList("Yoda");
        List<String> values = Lists.newArrayList("C-3PO", "Leila");
        List<String> extra = Lists.newArrayList("Yoda");
        try {
            iterables.assertIsSubsetOf(info, actual, values);
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, ShouldBeSubsetOf.shouldBeSubsetOf(actual, values, extra));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }

    // ------------------------------------------------------------------------------------------------------------------
    // tests using a custom comparison strategy
    // ------------------------------------------------------------------------------------------------------------------
    @Test
    public void should_pass_if_actual_is_subset_of_values_according_to_custom_comparison_strategy() {
        actual = Lists.newArrayList("Yoda", "Luke");
        iterablesWithCaseInsensitiveComparisonStrategy.assertIsSubsetOf(TestData.someInfo(), actual, Lists.newArrayList("yoda", "lUKE"));
    }

    @Test
    public void should_pass_if_actual_contains_duplicates_according_to_custom_comparison_strategy() {
        actual = Lists.newArrayList("Luke", "Luke");
        iterablesWithCaseInsensitiveComparisonStrategy.assertIsSubsetOf(TestData.someInfo(), actual, Lists.newArrayList("LUke", "yoda"));
    }

    @Test
    public void should_pass_if_actual_contains_given_values_even_if_duplicated_according_to_custom_comparison_strategy() {
        actual = Lists.newArrayList("Yoda", "Luke");
        iterablesWithCaseInsensitiveComparisonStrategy.assertIsSubsetOf(TestData.someInfo(), actual, Lists.newArrayList("LUke", "LuKe", "yoda"));
    }

    @Test
    public void should_fail_if_actual_is_not_subset_of_values_according_to_custom_comparison_strategy() {
        AssertionInfo info = TestData.someInfo();
        actual = Lists.newArrayList("Yoda", "Luke");
        List<String> values = Lists.newArrayList("yoda", "C-3PO");
        List<String> extra = Lists.newArrayList("Luke");
        try {
            iterablesWithCaseInsensitiveComparisonStrategy.assertIsSubsetOf(info, actual, values);
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, ShouldBeSubsetOf.shouldBeSubsetOf(actual, values, extra, comparisonStrategy));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }
}

