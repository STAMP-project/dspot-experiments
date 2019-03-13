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


import java.io.IOException;
import java.nio.file.Path;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.exception.PathsException;
import org.assertj.core.error.ShouldEndWithPath;
import org.assertj.core.internal.PathsBaseTest;
import org.assertj.core.test.TestFailures;
import org.assertj.core.util.FailureMessages;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


public class Paths_assertEndsWith_Test extends MockPathsBaseTest {
    @Test
    public void should_fail_if_actual_is_null() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> paths.assertEndsWith(info, null, other)).withMessage(FailureMessages.actualIsNull());
    }

    @Test
    public void should_fail_if_other_is_null() {
        Assertions.assertThatNullPointerException().isThrownBy(() -> paths.assertEndsWith(info, actual, null)).withMessage("the expected end path should not be null");
    }

    @Test
    public void should_fail_with_PathsException_if_actual_cannot_be_resolved() throws IOException {
        final IOException causeException = new IOException();
        Mockito.when(actual.toRealPath()).thenThrow(causeException);
        Assertions.assertThatExceptionOfType(PathsException.class).isThrownBy(() -> paths.assertEndsWith(info, actual, other)).withMessage("failed to resolve actual real path").withCause(causeException);
    }

    @Test
    public void should_fail_if_canonical_actual_does_not_end_with_normalized_other() throws IOException {
        final Path canonicalActual = Mockito.mock(Path.class);
        final Path normalizedOther = Mockito.mock(Path.class);
        Mockito.when(actual.toRealPath()).thenReturn(canonicalActual);
        Mockito.when(other.normalize()).thenReturn(normalizedOther);
        // This is the default, but...
        Mockito.when(canonicalActual.endsWith(normalizedOther)).thenReturn(false);
        try {
            paths.assertEndsWith(info, actual, other);
            TestFailures.wasExpectingAssertionError();
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, ShouldEndWithPath.shouldEndWith(actual, other));
        }
    }

    @Test
    public void should_succeed_if_canonical_actual_ends_with_normalized_other() throws IOException {
        final Path canonicalActual = Mockito.mock(Path.class);
        final Path normalizedOther = Mockito.mock(Path.class);
        Mockito.when(actual.toRealPath()).thenReturn(canonicalActual);
        Mockito.when(other.normalize()).thenReturn(normalizedOther);
        // We want the canonical versions to be compared, not the arguments
        Mockito.when(canonicalActual.endsWith(normalizedOther)).thenReturn(true);
        paths.assertEndsWith(info, actual, other);
    }
}

