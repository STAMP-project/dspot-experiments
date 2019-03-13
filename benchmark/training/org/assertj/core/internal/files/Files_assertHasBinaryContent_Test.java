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
package org.assertj.core.internal.files;


import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import org.assertj.core.api.AssertionInfo;
import org.assertj.core.api.Assertions;
import org.assertj.core.error.ShouldBeFile;
import org.assertj.core.error.ShouldHaveBinaryContent;
import org.assertj.core.internal.BinaryDiffResult;
import org.assertj.core.internal.FilesBaseTest;
import org.assertj.core.test.TestData;
import org.assertj.core.test.TestFailures;
import org.assertj.core.util.FailureMessages;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


/**
 * Tests for <code>{@link Files#assertHasBinaryContent(org.assertj.core.api.AssertionInfo, File, byte[])}</code>.
 *
 * @author Olivier Michallat
 * @author Joel Costigliola
 */
public class Files_assertHasBinaryContent_Test extends FilesBaseTest {
    private static File actual;

    private static byte[] expected;

    @Test
    public void should_throw_error_if_expected_is_null() {
        Assertions.assertThatNullPointerException().isThrownBy(() -> files.assertHasBinaryContent(someInfo(), Files_assertHasBinaryContent_Test.actual, null)).withMessage("The binary content to compare to should not be null");
    }

    @Test
    public void should_fail_if_actual_is_null() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> files.assertHasBinaryContent(someInfo(), null, Files_assertHasBinaryContent_Test.expected)).withMessage(FailureMessages.actualIsNull());
    }

    @Test
    public void should_fail_if_actual_is_not_file() {
        AssertionInfo info = TestData.someInfo();
        File notAFile = new File("xyz");
        try {
            files.assertHasBinaryContent(info, notAFile, Files_assertHasBinaryContent_Test.expected);
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, ShouldBeFile.shouldBeFile(notAFile));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }

    @Test
    public void should_pass_if_file_has_expected_binary_content() throws IOException {
        Mockito.when(binaryDiff.diff(Files_assertHasBinaryContent_Test.actual, Files_assertHasBinaryContent_Test.expected)).thenReturn(BinaryDiffResult.noDiff());
        files.assertHasBinaryContent(TestData.someInfo(), Files_assertHasBinaryContent_Test.actual, Files_assertHasBinaryContent_Test.expected);
    }

    @Test
    public void should_throw_error_wrapping_catched_IOException() throws IOException {
        IOException cause = new IOException();
        Mockito.when(binaryDiff.diff(Files_assertHasBinaryContent_Test.actual, Files_assertHasBinaryContent_Test.expected)).thenThrow(cause);
        Assertions.assertThatExceptionOfType(UncheckedIOException.class).isThrownBy(() -> files.assertHasBinaryContent(someInfo(), Files_assertHasBinaryContent_Test.actual, Files_assertHasBinaryContent_Test.expected)).withCause(cause);
    }

    @Test
    public void should_fail_if_file_does_not_have_expected_binary_content() throws IOException {
        BinaryDiffResult diff = new BinaryDiffResult(15, ((byte) (202)), ((byte) (254)));
        Mockito.when(binaryDiff.diff(Files_assertHasBinaryContent_Test.actual, Files_assertHasBinaryContent_Test.expected)).thenReturn(diff);
        AssertionInfo info = TestData.someInfo();
        try {
            files.assertHasBinaryContent(info, Files_assertHasBinaryContent_Test.actual, Files_assertHasBinaryContent_Test.expected);
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, ShouldHaveBinaryContent.shouldHaveBinaryContent(Files_assertHasBinaryContent_Test.actual, diff));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }
}

