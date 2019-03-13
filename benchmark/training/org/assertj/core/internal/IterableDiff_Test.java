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
package org.assertj.core.internal;


import java.util.List;
import org.assertj.core.api.Assertions;
import org.assertj.core.util.CaseInsensitiveStringComparator;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Test;


/**
 * Class for testing <code>{@link IterableDiff}</code>
 *
 * @author Billy Yuan
 */
public class IterableDiff_Test {
    private List<String> actual;

    private List<String> expected;

    private ComparisonStrategy comparisonStrategy;

    @Test
    public void should_not_report_any_differences_between_two_identical_iterables() {
        // GIVEN
        actual = Lists.newArrayList("#", "$");
        expected = Lists.newArrayList("#", "$");
        // WHEN
        IterableDiff diff = IterableDiff.diff(actual, expected, comparisonStrategy);
        // THEN
        IterableDiff_Test.assertThatNoDiff(diff);
    }

    @Test
    public void should_not_report_any_differences_between_two_iterables_with_elements_in_a_different_order() {
        // GIVEN
        actual = Lists.newArrayList("#", "$");
        expected = Lists.newArrayList("$", "#");
        // WHEN
        IterableDiff diff = IterableDiff.diff(actual, expected, comparisonStrategy);
        // THEN
        IterableDiff_Test.assertThatNoDiff(diff);
    }

    @Test
    public void should_not_report_any_differences_between_two_iterables_with_duplicate_elements_in_a_different_order() {
        // GIVEN
        actual = Lists.newArrayList("#", "#", "$", "$");
        expected = Lists.newArrayList("$", "$", "#", "#");
        // WHEN
        IterableDiff diff = IterableDiff.diff(actual, expected, comparisonStrategy);
        // THEN
        IterableDiff_Test.assertThatNoDiff(diff);
    }

    @Test
    public void should_report_difference_between_two_different_iterables_without_duplicate_elements() {
        // GIVEN
        actual = Lists.newArrayList("A", "B", "C");
        expected = Lists.newArrayList("X", "Y", "Z");
        // WHEN
        IterableDiff diff = IterableDiff.diff(actual, expected, comparisonStrategy);
        // THEN
        Assertions.assertThat(diff.differencesFound()).isTrue();
        Assertions.assertThat(diff.missing).containsExactly("X", "Y", "Z");
        Assertions.assertThat(diff.unexpected).containsExactly("A", "B", "C");
    }

    @Test
    public void should_report_difference_between_two_different_iterables_with_duplicate_elements() {
        // GIVEN
        actual = Lists.newArrayList("#", "#", "$");
        expected = Lists.newArrayList("$", "$", "#");
        // WHEN
        IterableDiff diff = IterableDiff.diff(actual, expected, comparisonStrategy);
        // THEN
        Assertions.assertThat(diff.differencesFound()).isTrue();
        Assertions.assertThat(diff.missing).containsExactly("$");
        Assertions.assertThat(diff.unexpected).containsExactly("#");
    }

    @Test
    public void should_not_report_any_differences_between_two_case_sensitive_iterables_according_to_custom_comparison_strategy() {
        // GIVEN
        comparisonStrategy = new ComparatorBasedComparisonStrategy(CaseInsensitiveStringComparator.instance);
        actual = Lists.newArrayList("a", "b", "C", "D");
        expected = Lists.newArrayList("A", "B", "C", "D");
        // WHEN
        IterableDiff diff = IterableDiff.diff(actual, expected, comparisonStrategy);
        // THEN
        IterableDiff_Test.assertThatNoDiff(diff);
    }

    @Test
    public void should_not_report_any_differences_between_two_same_iterables_with_custom_objects() {
        // GIVEN
        IterableDiff_Test.Foo foo1 = new IterableDiff_Test.Foo();
        IterableDiff_Test.Foo foo2 = new IterableDiff_Test.Foo();
        IterableDiff_Test.Foo foo3 = new IterableDiff_Test.Foo();
        List<IterableDiff_Test.Foo> actual = Lists.newArrayList(foo1, foo2, foo3);
        List<IterableDiff_Test.Foo> expected = Lists.newArrayList(foo1, foo2, foo3);
        // WHEN
        IterableDiff diff = IterableDiff.diff(actual, expected, comparisonStrategy);
        // THEN
        IterableDiff_Test.assertThatNoDiff(diff);
    }

    @Test
    public void should_report_difference_between_two_iterables_with_duplicate_objects() {
        // GIVEN
        IterableDiff_Test.Foo foo1 = new IterableDiff_Test.Foo();
        IterableDiff_Test.Foo foo2 = new IterableDiff_Test.Foo();
        List<IterableDiff_Test.Foo> actual = Lists.newArrayList(foo1, foo1, foo2);
        List<IterableDiff_Test.Foo> expected = Lists.newArrayList(foo1, foo2, foo2);
        // WHEN
        IterableDiff diff = IterableDiff.diff(actual, expected, comparisonStrategy);
        // THEN
        Assertions.assertThat(diff.differencesFound()).isTrue();
        Assertions.assertThat(diff.missing).containsExactly(foo2);
        Assertions.assertThat(diff.unexpected).containsExactly(foo1);
    }

    private class Foo {}
}

