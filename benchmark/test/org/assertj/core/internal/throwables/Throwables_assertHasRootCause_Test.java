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


public class Throwables_assertHasRootCause_Test extends ThrowablesBaseTest {
    private static final AssertionInfo INFO = TestData.someInfo();

    // @format:on
    @Test
    public void should_fail_if_actual_is_null() {
        // GIVEN
        final Throwable throwable = null;
        final Throwable expected = new Throwable();
        // WHEN
        AssertionError actual = AssertionsUtil.expectAssertionError(() -> throwables.assertHasRootCause(INFO, throwable, expected));
        // THEN
        Assertions.assertThat(actual).hasMessage(FailureMessages.actualIsNull());
    }

    @Test
    public void should_fail_if_expected_root_cause_is_null() {
        // GIVEN
        Throwable rootCause = new NullPointerException();
        final Throwable throwable = Throwables_assertHasRootCause_Test.withRootCause(rootCause);
        final Throwable expected = null;
        // WHEN
        AssertionsUtil.expectAssertionError(() -> throwables.assertHasRootCause(INFO, throwable, expected));
        // THEN
        Mockito.verify(failures).failure(Throwables_assertHasRootCause_Test.INFO, ShouldHaveNoCause.shouldHaveNoCause(throwable));
    }
}

