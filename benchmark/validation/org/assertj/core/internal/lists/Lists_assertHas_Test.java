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


import java.util.List;
import org.assertj.core.api.AssertionInfo;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.TestCondition;
import org.assertj.core.data.Index;
import org.assertj.core.error.ShouldHaveAtIndex;
import org.assertj.core.internal.ListsBaseTest;
import org.assertj.core.test.TestData;
import org.assertj.core.test.TestFailures;
import org.assertj.core.util.FailureMessages;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


/**
 * Tests for <code>{@link Lists#assertHas(AssertionInfo, List, Condition, Index)}</code>.
 *
 * @author Bo Gotthardt
 */
public class Lists_assertHas_Test extends ListsBaseTest {
    private static TestCondition<String> condition;

    private static List<String> actual = Lists.newArrayList("Yoda", "Luke", "Leia");

    @Test
    public void should_fail_if_actual_is_null() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> lists.assertHas(someInfo(), null, Lists_assertHas_Test.condition, someIndex())).withMessage(FailureMessages.actualIsNull());
    }

    @Test
    public void should_fail_if_actual_is_empty() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> {
            List<String> empty = emptyList();
            lists.assertHas(someInfo(), empty, Lists_assertHas_Test.condition, someIndex());
        }).withMessage(FailureMessages.actualIsEmpty());
    }

    @Test
    public void should_throw_error_if_Index_is_null() {
        Assertions.assertThatNullPointerException().isThrownBy(() -> lists.assertHas(someInfo(), Lists_assertHas_Test.actual, Lists_assertHas_Test.condition, null)).withMessage("Index should not be null");
    }

    @Test
    public void should_throw_error_if_Index_is_out_of_bounds() {
        Assertions.assertThatExceptionOfType(IndexOutOfBoundsException.class).isThrownBy(() -> lists.assertHas(someInfo(), Lists_assertHas_Test.actual, Lists_assertHas_Test.condition, atIndex(6))).withMessageContaining(String.format("Index should be between <0> and <2> (inclusive) but was:%n <6>"));
    }

    @Test
    public void should_throw_error_if_Condition_is_null() {
        Assertions.assertThatNullPointerException().isThrownBy(() -> lists.assertHas(someInfo(), Lists_assertHas_Test.actual, null, someIndex())).withMessage("The condition to evaluate should not be null");
    }

    @Test
    public void should_fail_if_actual_does_not_satisfy_condition_at_index() {
        Lists_assertHas_Test.condition.shouldMatch(false);
        AssertionInfo info = TestData.someInfo();
        Index index = Index.atIndex(1);
        try {
            lists.assertHas(info, Lists_assertHas_Test.actual, Lists_assertHas_Test.condition, index);
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, ShouldHaveAtIndex.shouldHaveAtIndex(Lists_assertHas_Test.actual, Lists_assertHas_Test.condition, index, "Luke"));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }

    @Test
    public void should_pass_if_actual_satisfies_condition_at_index() {
        Lists_assertHas_Test.condition.shouldMatch(true);
        lists.assertHas(TestData.someInfo(), Lists_assertHas_Test.actual, Lists_assertHas_Test.condition, TestData.someIndex());
    }
}

