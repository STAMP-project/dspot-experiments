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
import java.io.UncheckedIOException;
import java.nio.file.Path;
import org.assertj.core.api.AssertionInfo;
import org.assertj.core.api.Assertions;
import org.assertj.core.error.ShouldBeReadable;
import org.assertj.core.error.ShouldExist;
import org.assertj.core.error.ShouldHaveBinaryContent;
import org.assertj.core.internal.BinaryDiffResult;
import org.assertj.core.internal.PathsBaseTest;
import org.assertj.core.test.TestData;
import org.assertj.core.test.TestFailures;
import org.assertj.core.util.FailureMessages;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


/**
 * Tests for <code>{@link Paths#assertHasBinaryContent(AssertionInfo, Path, byte[])}</code>.
 */
public class Paths_assertHasBinaryContent_Test extends PathsBaseTest {
    private static Path path;

    private static byte[] expected;

    private Path mockPath;

    @Test
    public void should_pass_if_path_has_expected_text_content() throws IOException {
        Mockito.when(binaryDiff.diff(Paths_assertHasBinaryContent_Test.path, Paths_assertHasBinaryContent_Test.expected)).thenReturn(BinaryDiffResult.noDiff());
        Mockito.when(nioFilesWrapper.exists(Paths_assertHasBinaryContent_Test.path)).thenReturn(true);
        Mockito.when(nioFilesWrapper.isReadable(Paths_assertHasBinaryContent_Test.path)).thenReturn(true);
        paths.assertHasBinaryContent(TestData.someInfo(), Paths_assertHasBinaryContent_Test.path, Paths_assertHasBinaryContent_Test.expected);
    }

    @Test
    public void should_throw_error_if_expected_is_null() {
        Assertions.assertThatNullPointerException().isThrownBy(() -> paths.assertHasBinaryContent(someInfo(), Paths_assertHasBinaryContent_Test.path, null)).withMessage("The binary content to compare to should not be null");
    }

    @Test
    public void should_fail_if_actual_is_null() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> paths.assertHasBinaryContent(someInfo(), null, Paths_assertHasBinaryContent_Test.expected)).withMessage(FailureMessages.actualIsNull());
    }

    @Test
    public void should_fail_if_actual_path_does_not_exist() {
        AssertionInfo info = TestData.someInfo();
        Mockito.when(nioFilesWrapper.exists(mockPath)).thenReturn(false);
        try {
            paths.assertHasBinaryContent(info, mockPath, Paths_assertHasBinaryContent_Test.expected);
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, ShouldExist.shouldExist(mockPath));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }

    @Test
    public void should_fail_if_actual_is_not_a_readable_file() {
        AssertionInfo info = TestData.someInfo();
        Mockito.when(nioFilesWrapper.exists(mockPath)).thenReturn(true);
        Mockito.when(nioFilesWrapper.isReadable(mockPath)).thenReturn(false);
        try {
            paths.assertHasBinaryContent(info, mockPath, Paths_assertHasBinaryContent_Test.expected);
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, ShouldBeReadable.shouldBeReadable(mockPath));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }

    @Test
    public void should_throw_error_wrapping_catched_IOException() throws IOException {
        IOException cause = new IOException();
        Mockito.when(binaryDiff.diff(Paths_assertHasBinaryContent_Test.path, Paths_assertHasBinaryContent_Test.expected)).thenThrow(cause);
        Mockito.when(nioFilesWrapper.exists(Paths_assertHasBinaryContent_Test.path)).thenReturn(true);
        Mockito.when(nioFilesWrapper.isReadable(Paths_assertHasBinaryContent_Test.path)).thenReturn(true);
        Assertions.assertThatExceptionOfType(UncheckedIOException.class).isThrownBy(() -> paths.assertHasBinaryContent(someInfo(), Paths_assertHasBinaryContent_Test.path, Paths_assertHasBinaryContent_Test.expected)).withCause(cause);
    }

    @Test
    public void should_fail_if_path_does_not_have_expected_binary_content() throws IOException {
        BinaryDiffResult binaryDiffs = new BinaryDiffResult(15, ((byte) (202)), ((byte) (254)));
        Mockito.when(binaryDiff.diff(Paths_assertHasBinaryContent_Test.path, Paths_assertHasBinaryContent_Test.expected)).thenReturn(binaryDiffs);
        Mockito.when(nioFilesWrapper.exists(Paths_assertHasBinaryContent_Test.path)).thenReturn(true);
        Mockito.when(nioFilesWrapper.isReadable(Paths_assertHasBinaryContent_Test.path)).thenReturn(true);
        AssertionInfo info = TestData.someInfo();
        try {
            paths.assertHasBinaryContent(info, Paths_assertHasBinaryContent_Test.path, Paths_assertHasBinaryContent_Test.expected);
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, ShouldHaveBinaryContent.shouldHaveBinaryContent(Paths_assertHasBinaryContent_Test.path, binaryDiffs));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }
}

