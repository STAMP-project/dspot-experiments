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
import org.assertj.core.error.ShouldContainCharSequence;
import org.assertj.core.internal.ThrowablesBaseTest;
import org.assertj.core.test.TestData;
import org.assertj.core.util.FailureMessages;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


/**
 * Tests for <code>{@link Throwables#assertHasMessageContaining(AssertionInfo, Throwable, String)}</code>.
 *
 * @author Joel Costigliola
 */
public class Throwables_assertHasMessageContaining_Test extends ThrowablesBaseTest {
    @Test
    public void should_pass_if_actual_has_message_containing_with_expected_description() {
        throwables.assertHasMessageContaining(TestData.someInfo(), ThrowablesBaseTest.actual, "able");
    }

    @Test
    public void should_fail_if_actual_is_null() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> throwables.assertHasMessageContaining(someInfo(), null, "Throwable")).withMessage(FailureMessages.actualIsNull());
    }

    @Test
    public void should_fail_if_actual_has_message_not_containing_with_expected_description() {
        AssertionInfo info = TestData.someInfo();
        try {
            throwables.assertHasMessageContaining(info, ThrowablesBaseTest.actual, "expected description part");
            Assertions.fail("AssertionError expected");
        } catch (AssertionError err) {
            Mockito.verify(failures).failure(info, ShouldContainCharSequence.shouldContain(ThrowablesBaseTest.actual.getMessage(), "expected description part"));
        }
    }
}

