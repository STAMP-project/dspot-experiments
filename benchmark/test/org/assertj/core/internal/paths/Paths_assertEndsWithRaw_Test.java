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
package org.assertj.core.internal.paths;


import org.assertj.core.api.Assertions;
import org.assertj.core.error.ShouldEndWithPath;
import org.assertj.core.internal.PathsBaseTest;
import org.assertj.core.test.TestFailures;
import org.assertj.core.util.FailureMessages;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


public class Paths_assertEndsWithRaw_Test extends MockPathsBaseTest {
    @Test
    public void should_fail_if_actual_is_null() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> paths.assertEndsWithRaw(info, null, other)).withMessage(FailureMessages.actualIsNull());
    }

    @Test
    public void should_fail_if_other_is_null() {
        Assertions.assertThatNullPointerException().isThrownBy(() -> paths.assertEndsWithRaw(info, actual, null)).withMessage("the expected end path should not be null");
    }

    @Test
    public void should_fail_if_actual_does_not_end_with_other() {
        // This is the default, but let's make this explicit
        Mockito.when(actual.endsWith(other)).thenReturn(false);
        try {
            paths.assertEndsWithRaw(info, actual, other);
            TestFailures.wasExpectingAssertionError();
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, ShouldEndWithPath.shouldEndWith(actual, other));
        }
    }

    @Test
    public void should_succeed_if_actual_ends_with_other() {
        Mockito.when(actual.endsWith(other)).thenReturn(true);
        paths.assertEndsWithRaw(info, actual, other);
    }
}

