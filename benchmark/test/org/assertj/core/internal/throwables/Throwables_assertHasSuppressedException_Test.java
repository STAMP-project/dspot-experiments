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
import org.assertj.core.error.ShouldHaveSuppressedException;
import org.assertj.core.internal.ThrowablesBaseTest;
import org.assertj.core.test.TestData;
import org.assertj.core.test.TestFailures;
import org.assertj.core.util.FailureMessages;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


public class Throwables_assertHasSuppressedException_Test extends ThrowablesBaseTest {
    private static final String IAE_EXCEPTION_MESSAGE = "invalid arg";

    private static final String NPE_EXCEPTION_MESSAGE = "null arg";

    private Throwable throwableSuppressedException;

    @Test
    public void should_pass_if_one_of_the_suppressed_exception_has_the_expected_type_and_message() {
        throwables.assertHasSuppressedException(TestData.someInfo(), throwableSuppressedException, new IllegalArgumentException(Throwables_assertHasSuppressedException_Test.IAE_EXCEPTION_MESSAGE));
    }

    @Test
    public void should_fail_if_actual_is_null() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> throwables.assertHasSuppressedException(someInfo(), null, new Throwable())).withMessage(FailureMessages.actualIsNull());
    }

    @Test
    public void should_fail_if_expected_suppressed_exception_is_null() {
        Assertions.assertThatNullPointerException().isThrownBy(() -> throwables.assertHasSuppressedException(someInfo(), new Throwable(), null)).withMessage("The expected suppressed exception should not be null");
    }

    @Test
    public void should_fail_if_actual_has_no_suppressed_exception_and_expected_suppressed_exception_is_not_null() {
        AssertionInfo info = TestData.someInfo();
        Throwable expectedSuppressedException = new Throwable();
        try {
            throwables.assertHasSuppressedException(info, ThrowablesBaseTest.actual, expectedSuppressedException);
        } catch (AssertionError err) {
            Mockito.verify(failures).failure(info, ShouldHaveSuppressedException.shouldHaveSuppressedException(ThrowablesBaseTest.actual, expectedSuppressedException));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }

    @Test
    public void should_fail_if_suppressed_exception_is_not_instance_of_expected_type() {
        AssertionInfo info = TestData.someInfo();
        Throwable expectedSuppressedException = new NullPointerException(Throwables_assertHasSuppressedException_Test.IAE_EXCEPTION_MESSAGE);
        try {
            throwables.assertHasSuppressedException(info, throwableSuppressedException, expectedSuppressedException);
        } catch (AssertionError err) {
            Mockito.verify(failures).failure(info, ShouldHaveSuppressedException.shouldHaveSuppressedException(throwableSuppressedException, expectedSuppressedException));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }

    @Test
    public void should_fail_if_suppressed_exception_has_not_the_expected_message() {
        AssertionInfo info = TestData.someInfo();
        Throwable expectedSuppressedException = new IllegalArgumentException(((Throwables_assertHasSuppressedException_Test.IAE_EXCEPTION_MESSAGE) + "foo"));
        try {
            throwables.assertHasSuppressedException(info, throwableSuppressedException, expectedSuppressedException);
        } catch (AssertionError err) {
            Mockito.verify(failures).failure(info, ShouldHaveSuppressedException.shouldHaveSuppressedException(throwableSuppressedException, expectedSuppressedException));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }

    @Test
    public void should_fail_if_suppressed_exception_has_no_message_and_the_expected_suppressed_exception_has_one() {
        AssertionInfo info = TestData.someInfo();
        Throwable expectedSuppressedException = new IllegalArgumentException("error cause");
        throwableSuppressedException = new Throwable(new IllegalArgumentException());
        try {
            throwables.assertHasSuppressedException(info, throwableSuppressedException, expectedSuppressedException);
        } catch (AssertionError err) {
            Mockito.verify(failures).failure(info, ShouldHaveSuppressedException.shouldHaveSuppressedException(throwableSuppressedException, expectedSuppressedException));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }

    @Test
    public void should_fail_if_suppressed_exception_has_different_type_and_message_to_expected_cause() {
        AssertionInfo info = TestData.someInfo();
        Throwable expectedSuppressedException = new NullPointerException("error cause");
        try {
            throwables.assertHasSuppressedException(info, throwableSuppressedException, expectedSuppressedException);
        } catch (AssertionError err) {
            Mockito.verify(failures).failure(info, ShouldHaveSuppressedException.shouldHaveSuppressedException(throwableSuppressedException, expectedSuppressedException));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }
}

