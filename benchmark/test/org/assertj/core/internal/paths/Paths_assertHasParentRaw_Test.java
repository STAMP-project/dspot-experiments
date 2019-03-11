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


import java.nio.file.Path;
import org.assertj.core.api.Assertions;
import org.assertj.core.error.ShouldHaveParent;
import org.assertj.core.internal.PathsBaseTest;
import org.assertj.core.test.TestFailures;
import org.assertj.core.util.FailureMessages;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


public class Paths_assertHasParentRaw_Test extends MockPathsBaseTest {
    private Path expectedParent;

    @Test
    public void should_fail_if_actual_is_null() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> paths.assertHasParentRaw(info, null, expectedParent)).withMessage(FailureMessages.actualIsNull());
    }

    @Test
    public void should_fail_if_provided_parent_is_null() {
        Assertions.assertThatNullPointerException().isThrownBy(() -> paths.assertHasParentRaw(info, actual, null)).withMessage("expected parent path should not be null");
    }

    @Test
    public void should_fail_if_actual_has_no_parent() {
        // This is the default, but...
        Mockito.when(actual.getParent()).thenReturn(null);
        try {
            paths.assertHasParentRaw(info, actual, expectedParent);
            TestFailures.wasExpectingAssertionError();
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, ShouldHaveParent.shouldHaveParent(actual, expectedParent));
        }
    }

    @Test
    public void should_fail_if_actual_parent_is_not_expected_parent() {
        final Path actualParent = Mockito.mock(Path.class);
        Mockito.when(actual.getParent()).thenReturn(actualParent);
        try {
            paths.assertHasParentRaw(info, actual, expectedParent);
            TestFailures.wasExpectingAssertionError();
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, ShouldHaveParent.shouldHaveParent(actual, actualParent, expectedParent));
        }
    }

    @Test
    public void should_succeed_if_parent_is_expected_parent() {
        Mockito.when(actual.getParent()).thenReturn(expectedParent);
        paths.assertHasParentRaw(info, actual, expectedParent);
    }
}

