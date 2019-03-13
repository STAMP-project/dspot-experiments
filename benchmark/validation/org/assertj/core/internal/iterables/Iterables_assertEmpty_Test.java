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
import java.util.Collections;
import org.assertj.core.api.AssertionInfo;
import org.assertj.core.api.Assertions;
import org.assertj.core.error.ShouldBeEmpty;
import org.assertj.core.internal.IterablesBaseTest;
import org.assertj.core.test.TestData;
import org.assertj.core.test.TestFailures;
import org.assertj.core.util.FailureMessages;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


/**
 * Tests for <code>{@link Iterables#assertEmpty(AssertionInfo, Collection)}</code>.
 *
 * @author Alex Ruiz
 * @author Joel Costigliola
 */
public class Iterables_assertEmpty_Test extends IterablesBaseTest {
    @Test
    public void should_pass_if_actual_is_empty() {
        iterables.assertEmpty(TestData.someInfo(), Collections.emptyList());
    }

    @Test
    public void should_fail_if_actual_is_null() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> iterables.assertEmpty(someInfo(), null)).withMessage(FailureMessages.actualIsNull());
    }

    @Test
    public void should_fail_if_actual_has_elements() {
        AssertionInfo info = TestData.someInfo();
        Collection<String> actual = Lists.newArrayList("Yoda");
        try {
            iterables.assertEmpty(info, actual);
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, ShouldBeEmpty.shouldBeEmpty(actual));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }

    @Test
    public void should_pass_if_actual_is_empty_whatever_custom_comparison_strategy_is() {
        iterablesWithCaseInsensitiveComparisonStrategy.assertEmpty(TestData.someInfo(), Collections.emptyList());
    }

    @Test
    public void should_fail_if_actual_is_null_whatever_custom_comparison_strategy_is() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> iterablesWithCaseInsensitiveComparisonStrategy.assertEmpty(someInfo(), null)).withMessage(FailureMessages.actualIsNull());
    }

    @Test
    public void should_fail_if_actual_has_elements_whatever_custom_comparison_strategy_is() {
        AssertionInfo info = TestData.someInfo();
        Collection<String> actual = Lists.newArrayList("Yoda");
        try {
            iterablesWithCaseInsensitiveComparisonStrategy.assertEmpty(info, actual);
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, ShouldBeEmpty.shouldBeEmpty(actual));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }
}

