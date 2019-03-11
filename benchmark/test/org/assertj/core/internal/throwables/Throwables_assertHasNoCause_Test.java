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
import org.assertj.core.util.FailureMessages;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


/**
 * Tests for <code>{@link Throwables#assertHasNoCause(AssertionInfo, Throwable, Class)}</code>.
 *
 * @author Joel Costigliola
 */
public class Throwables_assertHasNoCause_Test extends ThrowablesBaseTest {
    @Test
    public void should_pass_if_actual_has_no_cause() {
        throwables.assertHasNoCause(TestData.someInfo(), ThrowablesBaseTest.actual);
    }

    @Test
    public void should_fail_if_actual_is_null() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> throwables.assertHasNoCause(someInfo(), null)).withMessage(FailureMessages.actualIsNull());
    }

    @Test
    public void should_fail_if_actual_has_a_cause() {
        AssertionInfo info = TestData.someInfo();
        Throwable throwableWithCause = new Throwable(new NullPointerException());
        try {
            throwables.assertHasNoCause(info, throwableWithCause);
            Assertions.fail("AssertionError expected");
        } catch (AssertionError err) {
            Mockito.verify(failures).failure(info, ShouldHaveNoCause.shouldHaveNoCause(throwableWithCause));
        }
    }
}

