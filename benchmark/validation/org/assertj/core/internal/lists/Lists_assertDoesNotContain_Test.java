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


import java.util.Collections;
import java.util.List;
import org.assertj.core.api.AssertionInfo;
import org.assertj.core.api.Assertions;
import org.assertj.core.data.Index;
import org.assertj.core.error.ShouldNotContainAtIndex;
import org.assertj.core.internal.ListsBaseTest;
import org.assertj.core.test.TestData;
import org.assertj.core.test.TestFailures;
import org.assertj.core.util.FailureMessages;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


/**
 * Tests for <code>{@link Lists#assertDoesNotContain(AssertionInfo, List, Object, Index)}</code>.
 *
 * @author Alex Ruiz
 * @author Joel Costigliola
 */
public class Lists_assertDoesNotContain_Test extends ListsBaseTest {
    private static List<String> actual = Lists.newArrayList("Yoda", "Luke", "Leia");

    @Test
    public void should_fail_if_actual_is_null() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> lists.assertDoesNotContain(someInfo(), null, "Yoda", someIndex())).withMessage(FailureMessages.actualIsNull());
    }

    @Test
    public void should_pass_if_actual_does_not_contain_value_at_index() {
        lists.assertDoesNotContain(TestData.someInfo(), Lists_assertDoesNotContain_Test.actual, "Yoda", Index.atIndex(1));
    }

    @Test
    public void should_pass_if_actual_is_empty() {
        lists.assertDoesNotContain(TestData.someInfo(), Collections.emptyList(), "Yoda", TestData.someIndex());
    }

    @Test
    public void should_throw_error_if_index_is_null() {
        Assertions.assertThatNullPointerException().isThrownBy(() -> lists.assertDoesNotContain(someInfo(), Lists_assertDoesNotContain_Test.actual, "Yoda", null)).withMessage("Index should not be null");
    }

    @Test
    public void should_pass_if_index_is_out_of_bounds() {
        lists.assertDoesNotContain(TestData.someInfo(), Lists_assertDoesNotContain_Test.actual, "Yoda", Index.atIndex(6));
    }

    @Test
    public void should_fail_if_actual_contains_value_at_index() {
        AssertionInfo info = TestData.someInfo();
        Index index = Index.atIndex(0);
        try {
            lists.assertDoesNotContain(info, Lists_assertDoesNotContain_Test.actual, "Yoda", index);
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, ShouldNotContainAtIndex.shouldNotContainAtIndex(Lists_assertDoesNotContain_Test.actual, "Yoda", index));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }

    @Test
    public void should_pass_if_actual_does_not_contain_value_at_index_according_to_custom_comparison_strategy() {
        listsWithCaseInsensitiveComparisonStrategy.assertDoesNotContain(TestData.someInfo(), Lists_assertDoesNotContain_Test.actual, "Yoda", Index.atIndex(1));
    }

    @Test
    public void should_fail_if_actual_contains_value_at_index_according_to_custom_comparison_strategy() {
        AssertionInfo info = TestData.someInfo();
        Index index = Index.atIndex(0);
        try {
            listsWithCaseInsensitiveComparisonStrategy.assertDoesNotContain(info, Lists_assertDoesNotContain_Test.actual, "YODA", index);
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, ShouldNotContainAtIndex.shouldNotContainAtIndex(Lists_assertDoesNotContain_Test.actual, "YODA", index, comparisonStrategy));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }
}

