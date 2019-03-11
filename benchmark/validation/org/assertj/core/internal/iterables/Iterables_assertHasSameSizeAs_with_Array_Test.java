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


import java.util.Collection;
import org.assertj.core.api.AssertionInfo;
import org.assertj.core.api.Assertions;
import org.assertj.core.error.ShouldHaveSameSizeAs;
import org.assertj.core.internal.IterablesBaseTest;
import org.assertj.core.test.TestData;
import org.assertj.core.util.Arrays;
import org.assertj.core.util.FailureMessages;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Test;


/**
 * Tests for <code>{@link Iterables#assertHasSameSizeAs(AssertionInfo, Iterable, Object[])}</code>.
 *
 * @author Nicolas Fran?ois
 */
public class Iterables_assertHasSameSizeAs_with_Array_Test extends IterablesBaseTest {
    @Test
    public void should_pass_if_size_of_actual_is_equal_to_expected_size() {
        iterables.assertHasSameSizeAs(TestData.someInfo(), Lists.newArrayList("Yoda", "Luke"), Arrays.array("Solo", "Leia"));
    }

    @Test
    public void should_fail_if_actual_is_null() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> iterables.assertHasSameSizeAs(someInfo(), null, newArrayList("Solo", "Leia"))).withMessage(FailureMessages.actualIsNull());
    }

    @Test
    public void should_fail_if_other_is_null() {
        Assertions.assertThatNullPointerException().isThrownBy(() -> {
            Iterable<?> other = null;
            iterables.assertHasSameSizeAs(someInfo(), newArrayList("Yoda", "Luke"), other);
        }).withMessage("The Iterable to compare actual size with should not be null");
    }

    @Test
    public void should_fail_if_actual_size_is_not_equal_to_other_size() {
        AssertionInfo info = TestData.someInfo();
        Collection<String> actual = Lists.newArrayList("Yoda");
        String[] other = Arrays.array("Solo", "Luke", "Leia");
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> iterables.assertHasSameSizeAs(info, actual, other)).withMessage(String.format(ShouldHaveSameSizeAs.shouldHaveSameSizeAs(actual, actual.size(), other.length).create(null, info.representation())));
    }

    @Test
    public void should_pass_if_actual_has_same_size_as_other_whatever_custom_comparison_strategy_is() {
        iterablesWithCaseInsensitiveComparisonStrategy.assertHasSameSizeAs(TestData.someInfo(), Lists.newArrayList("Luke", "Yoda"), Arrays.array("Solo", "Leia"));
    }

    @Test
    public void should_fail_if_actual_is_null_whatever_custom_comparison_strategy_is() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> iterablesWithCaseInsensitiveComparisonStrategy.assertHasSameSizeAs(someInfo(), null, array("Solo", "Leia"))).withMessage(FailureMessages.actualIsNull());
    }

    @Test
    public void should_fail_if_other_is_null_whatever_custom_comparison_strategy_is() {
        Assertions.assertThatNullPointerException().isThrownBy(() -> {
            Iterable<?> other = null;
            iterables.assertHasSameSizeAs(someInfo(), newArrayList("Yoda", "Luke"), other);
        }).withMessage("The Iterable to compare actual size with should not be null");
    }

    @Test
    public void should_fail_if_actual_size_is_not_equal_to_other_size_whatever_custom_comparison_strategy_is() {
        AssertionInfo info = TestData.someInfo();
        Collection<String> actual = Lists.newArrayList("Yoda");
        String[] other = Arrays.array("Solo", "Luke", "Leia");
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> iterablesWithCaseInsensitiveComparisonStrategy.assertHasSameSizeAs(info, actual, other)).withMessage(ShouldHaveSameSizeAs.shouldHaveSameSizeAs(actual, actual.size(), other.length).create(null, info.representation()));
    }
}

