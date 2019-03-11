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
import org.assertj.core.error.ShouldHaveParent;
import org.assertj.core.internal.PathsBaseTest;
import org.assertj.core.test.TestFailures;
import org.assertj.core.util.FailureMessages;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


public class Paths_assertHasParent_Test extends MockPathsBaseTest {
    private Path canonicalActual;

    private Path expected;

    private Path canonicalExpected;

    @Test
    public void should_fail_if_actual_is_null() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> paths.assertHasParent(info, null, expected)).withMessage(FailureMessages.actualIsNull());
    }

    @Test
    public void should_fail_if_given_parent_is_null() {
        Assertions.assertThatNullPointerException().isThrownBy(() -> paths.assertHasParent(info, actual, null)).withMessage("expected parent path should not be null");
    }

    @Test
    public void should_fail_if_actual_cannot_be_canonicalized() throws IOException {
        final IOException exception = new IOException();
        Mockito.when(actual.toRealPath()).thenThrow(exception);
        Assertions.assertThatExceptionOfType(PathsException.class).isThrownBy(() -> paths.assertHasParent(info, actual, expected)).withMessage("failed to resolve actual real path").withCause(exception);
    }

    @Test
    public void should_fail_if_expected_parent_cannot_be_canonicalized() throws IOException {
        final IOException exception = new IOException();
        Mockito.when(actual.toRealPath()).thenReturn(canonicalActual);
        Mockito.when(expected.toRealPath()).thenThrow(exception);
        Assertions.assertThatExceptionOfType(PathsException.class).isThrownBy(() -> paths.assertHasParent(info, actual, expected)).withMessage("failed to resolve argument real path").withCause(exception);
    }

    @Test
    public void should_fail_if_actual_has_no_parent() throws IOException {
        Mockito.when(actual.toRealPath()).thenReturn(canonicalActual);
        Mockito.when(expected.toRealPath()).thenReturn(canonicalExpected);
        // This is the default, but...
        Mockito.when(canonicalActual.getParent()).thenReturn(null);
        try {
            paths.assertHasParent(info, actual, expected);
            TestFailures.wasExpectingAssertionError();
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, ShouldHaveParent.shouldHaveParent(actual, expected));
        }
    }

    @Test
    public void should_fail_if_actual_parent_is_not_expected_parent() throws IOException {
        final Path actualParent = Mockito.mock(Path.class);
        Mockito.when(actual.toRealPath()).thenReturn(canonicalActual);
        Mockito.when(expected.toRealPath()).thenReturn(canonicalExpected);
        Mockito.when(canonicalActual.getParent()).thenReturn(actualParent);
        try {
            paths.assertHasParent(info, actual, expected);
            TestFailures.wasExpectingAssertionError();
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, ShouldHaveParent.shouldHaveParent(actual, actualParent, expected));
        }
    }

    @Test
    public void should_succeed_if_canonical_actual_has_expected_parent() throws IOException {
        Mockito.when(actual.toRealPath()).thenReturn(canonicalActual);
        Mockito.when(expected.toRealPath()).thenReturn(canonicalExpected);
        Mockito.when(canonicalActual.getParent()).thenReturn(canonicalExpected);
        paths.assertHasParent(info, actual, expected);
    }
}

