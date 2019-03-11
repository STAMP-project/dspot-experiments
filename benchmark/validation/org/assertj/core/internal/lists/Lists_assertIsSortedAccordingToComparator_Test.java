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
package org.assertj.core.internal.lists;


import java.util.Comparator;
import java.util.List;
import org.assertj.core.api.AssertionInfo;
import org.assertj.core.api.Assertions;
import org.assertj.core.internal.ListsBaseTest;
import org.assertj.core.test.TestData;
import org.assertj.core.test.TestFailures;
import org.assertj.core.util.FailureMessages;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


/**
 * Tests for <code>{@link Lists#assertIsSortedAccordingToComparator(AssertionInfo, List, Comparator)}</code>.
 *
 * @author Joel Costigliola
 */
public class Lists_assertIsSortedAccordingToComparator_Test extends ListsBaseTest {
    private static Comparator<String> stringDescendingOrderComparator = ( s1, s2) -> -(s1.compareTo(s2));

    private static Comparator<Object> comparator = ( o1, o2) -> o1.toString().compareTo(o2.toString());

    @Test
    public void should_pass_if_actual_is_sorted_according_to_given_comparator() {
        lists.assertIsSortedAccordingToComparator(TestData.someInfo(), Lists.newArrayList("Yoda", "Vador", "Luke", "Leia", "Leia"), Lists_assertIsSortedAccordingToComparator_Test.stringDescendingOrderComparator);
    }

    @Test
    public void should_pass_if_actual_is_sorted_according_to_given_comparator_whatever_custom_comparison_strategy_is() {
        listsWithCaseInsensitiveComparisonStrategy.assertIsSortedAccordingToComparator(TestData.someInfo(), Lists.newArrayList("Yoda", "Vador", "Luke", "Leia", "Leia"), Lists_assertIsSortedAccordingToComparator_Test.stringDescendingOrderComparator);
    }

    @Test
    public void should_pass_if_actual_is_empty_whatever_given_comparator_is() {
        lists.assertIsSortedAccordingToComparator(TestData.someInfo(), Lists.newArrayList(), Lists_assertIsSortedAccordingToComparator_Test.stringDescendingOrderComparator);
        lists.assertIsSortedAccordingToComparator(TestData.someInfo(), Lists.newArrayList(), Lists_assertIsSortedAccordingToComparator_Test.comparator);
    }

    @Test
    public void should_pass_if_actual_contains_only_one_comparable_element() {
        lists.assertIsSortedAccordingToComparator(TestData.someInfo(), Lists.newArrayList("Obiwan"), Lists_assertIsSortedAccordingToComparator_Test.comparator);
    }

    @Test
    public void should_fail_if_actual_is_null() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> lists.assertIsSortedAccordingToComparator(someInfo(), null, Lists_assertIsSortedAccordingToComparator_Test.comparator)).withMessage(FailureMessages.actualIsNull());
    }

    @Test
    public void should_fail_if_comparator_is_null() {
        Assertions.assertThatNullPointerException().isThrownBy(() -> lists.assertIsSortedAccordingToComparator(someInfo(), newArrayList(), null));
    }

    @Test
    public void should_fail_if_actual_is_not_sorted_according_to_given_comparator() {
        AssertionInfo info = TestData.someInfo();
        List<String> actual = Lists.newArrayList("Yoda", "Vador", "Leia", "Leia", "Luke");
        try {
            lists.assertIsSortedAccordingToComparator(info, actual, Lists_assertIsSortedAccordingToComparator_Test.stringDescendingOrderComparator);
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, shouldBeSortedAccordingToGivenComparator(3, actual, Lists_assertIsSortedAccordingToComparator_Test.stringDescendingOrderComparator));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }

    @Test
    public void should_fail_if_actual_has_some_not_mutually_comparable_elements_according_to_given_comparator() {
        AssertionInfo info = TestData.someInfo();
        List<Object> actual = Lists.newArrayList();
        actual.add("bar");
        actual.add(new Integer(5));
        actual.add("foo");
        try {
            lists.assertIsSortedAccordingToComparator(info, actual, Lists_assertIsSortedAccordingToComparator_Test.stringDescendingOrderComparator);
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, shouldHaveComparableElementsAccordingToGivenComparator(actual, Lists_assertIsSortedAccordingToComparator_Test.stringDescendingOrderComparator));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }

    @Test
    public void should_fail_if_actual_has_one_element_only_not_comparable_according_to_given_comparator() {
        AssertionInfo info = TestData.someInfo();
        List<Object> actual = Lists.newArrayList(new Object());
        try {
            lists.assertIsSortedAccordingToComparator(info, actual, Lists_assertIsSortedAccordingToComparator_Test.stringDescendingOrderComparator);
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, shouldHaveComparableElementsAccordingToGivenComparator(actual, Lists_assertIsSortedAccordingToComparator_Test.stringDescendingOrderComparator));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }
}

