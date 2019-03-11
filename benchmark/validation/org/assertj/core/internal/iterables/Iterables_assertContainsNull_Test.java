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
import org.assertj.core.error.ShouldContainNull;
import org.assertj.core.internal.IterablesBaseTest;
import org.assertj.core.test.TestData;
import org.assertj.core.test.TestFailures;
import org.assertj.core.util.FailureMessages;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


/**
 * Tests for <code>{@link Iterables#assertContainsNull(AssertionInfo, Collection)}</code>.
 *
 * @author Joel Costigliola
 */
public class Iterables_assertContainsNull_Test extends IterablesBaseTest {
    private List<String> actual = Lists.newArrayList("Luke", "Yoda", null);

    @Test
    public void should_pass_if_actual_contains_null() {
        iterables.assertContainsNull(TestData.someInfo(), actual);
    }

    @Test
    public void should_pass_if_actual_contains_only_null_values() {
        actual = Lists.newArrayList(null, null);
        iterables.assertContainsNull(TestData.someInfo(), actual);
    }

    @Test
    public void should_pass_if_actual_contains_null_more_than_once() {
        actual.add(null);
        iterables.assertContainsNull(TestData.someInfo(), actual);
    }

    @Test
    public void should_fail_if_actual_is_null() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> iterables.assertContainsNull(someInfo(), null)).withMessage(FailureMessages.actualIsNull());
    }

    @Test
    public void should_fail_if_actual_does_not_contain_null() {
        AssertionInfo info = TestData.someInfo();
        actual = Lists.newArrayList("Luke", "Yoda");
        try {
            iterables.assertContainsNull(info, actual);
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, ShouldContainNull.shouldContainNull(actual));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }

    @Test
    public void should_pass_if_actual_contains_null_whatever_custom_comparison_strategy_is() {
        iterablesWithCaseInsensitiveComparisonStrategy.assertContainsNull(TestData.someInfo(), actual);
    }

    @Test
    public void should_pass_if_actual_contains_only_null_values_whatever_custom_comparison_strategy_is() {
        actual = Lists.newArrayList(null, null);
        iterablesWithCaseInsensitiveComparisonStrategy.assertContainsNull(TestData.someInfo(), actual);
    }

    @Test
    public void should_pass_if_actual_contains_null_more_than_once_whatever_custom_comparison_strategy_is() {
        actual.add(null);
        iterablesWithCaseInsensitiveComparisonStrategy.assertContainsNull(TestData.someInfo(), actual);
    }

    @Test
    public void should_fail_if_actual_is_null_whatever_custom_comparison_strategy_is() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> iterablesWithCaseInsensitiveComparisonStrategy.assertContainsNull(someInfo(), null)).withMessage(FailureMessages.actualIsNull());
    }

    @Test
    public void should_fail_if_actual_does_not_contain_null_whatever_custom_comparison_strategy_is() {
        AssertionInfo info = TestData.someInfo();
        actual = Lists.newArrayList("Luke", "Yoda");
        try {
            iterablesWithCaseInsensitiveComparisonStrategy.assertContainsNull(info, actual);
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, ShouldContainNull.shouldContainNull(actual));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }
}

