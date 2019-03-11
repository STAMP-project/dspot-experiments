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


import java.util.ArrayList;
import java.util.List;
import org.assertj.core.api.AssertionInfo;
import org.assertj.core.api.Assertions;
import org.assertj.core.error.ShouldContainOnlyNulls;
import org.assertj.core.internal.IterablesBaseTest;
import org.assertj.core.test.TestData;
import org.assertj.core.test.TestFailures;
import org.assertj.core.util.FailureMessages;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


/**
 * Tests for <code>{@link Iterables#assertContainsOnlyNulls(AssertionInfo, Iterable)}</code>.
 *
 * @author Billy Yuan
 */
public class Iterables_assertContainsOnlyNulls_Test extends IterablesBaseTest {
    private List<String> actual = new ArrayList<>();

    @Test
    public void should_pass_if_actual_contains_null_once() {
        actual.add(null);
        iterables.assertContainsOnlyNulls(TestData.someInfo(), actual);
    }

    @Test
    public void should_pass_if_actual_contains_null_more_than_once() {
        actual = Lists.newArrayList(null, null, null);
        iterables.assertContainsOnlyNulls(TestData.someInfo(), actual);
    }

    @Test
    public void should_fail_if_actual_is_null() {
        actual = null;
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> iterables.assertContainsOnlyNulls(someInfo(), actual)).withMessage(FailureMessages.actualIsNull());
    }

    @Test
    public void should_fail_if_actual_is_empty() {
        AssertionInfo info = TestData.someInfo();
        try {
            iterables.assertContainsOnlyNulls(info, actual);
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, ShouldContainOnlyNulls.shouldContainOnlyNulls(actual));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }

    @Test
    public void should_fail_if_actual_contains_null_and_non_null_elements() {
        AssertionInfo info = TestData.someInfo();
        actual = Lists.newArrayList(null, null, "person");
        List<String> nonNulls = Lists.newArrayList("person");
        try {
            iterables.assertContainsOnlyNulls(info, actual);
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, ShouldContainOnlyNulls.shouldContainOnlyNulls(actual, nonNulls));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }

    @Test
    public void should_fail_if_actual_contains_non_null_elements_only() {
        AssertionInfo info = TestData.someInfo();
        actual = Lists.newArrayList("person", "person2");
        List<String> nonNulls = Lists.newArrayList("person", "person2");
        try {
            iterables.assertContainsOnlyNulls(info, actual);
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, ShouldContainOnlyNulls.shouldContainOnlyNulls(actual, nonNulls));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }
}

