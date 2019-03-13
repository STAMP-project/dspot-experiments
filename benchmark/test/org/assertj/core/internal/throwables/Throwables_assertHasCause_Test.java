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
package org.assertj.core.internal.throwables;


import org.assertj.core.api.AssertionInfo;
import org.assertj.core.api.Assertions;
import org.assertj.core.error.ShouldHaveNoCause;
import org.assertj.core.internal.ThrowablesBaseTest;
import org.assertj.core.test.TestData;
import org.assertj.core.util.AssertionsUtil;
import org.assertj.core.util.FailureMessages;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


public class Throwables_assertHasCause_Test extends ThrowablesBaseTest {
    private static final AssertionInfo INFO = TestData.someInfo();

    @Test
    public void should_pass_if_cause_has_expected_type_and_message() {
        // GIVEN
        Throwable cause = new IllegalArgumentException("wibble");
        Throwable expected = new IllegalArgumentException("wibble");
        Throwable throwable = Throwables_assertHasCause_Test.withCause(cause);
        // WHEN
        throwables.assertHasCause(Throwables_assertHasCause_Test.INFO, throwable, expected);
        // THEN
        // no exception thrown
    }

    @Test
    public void should_pass_if_actual_has_no_cause_and_expected_cause_is_null() {
        // GIVEN
        Throwable cause = null;
        Throwable expected = null;
        Throwable throwable = Throwables_assertHasCause_Test.withCause(cause);
        // WHEN
        throwables.assertHasCause(Throwables_assertHasCause_Test.INFO, throwable, expected);
        // THEN
        // no exception thrown
    }

    // @format:on
    @Test
    public void should_fail_if_expected_cause_is_null() {
        // GIVEN
        final Throwable throwable = Throwables_assertHasCause_Test.withCause(new Throwable());
        final Throwable expected = null;
        // WHEN
        AssertionsUtil.expectAssertionError(() -> throwables.assertHasCause(INFO, throwable, expected));
        // THEN
        Mockito.verify(failures).failure(Throwables_assertHasCause_Test.INFO, ShouldHaveNoCause.shouldHaveNoCause(throwable));
    }

    @Test
    public void should_fail_if_actual_is_null() {
        // GIVEN
        final Throwable throwable = null;
        final Throwable expected = new Throwable();
        // WHEN
        AssertionError actual = AssertionsUtil.expectAssertionError(() -> throwables.assertHasCause(INFO, throwable, expected));
        // THEN
        Assertions.assertThat(actual).hasMessage(FailureMessages.actualIsNull());
    }
}

