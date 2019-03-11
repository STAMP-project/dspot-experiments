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
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.List;
import org.assertj.core.api.AssertionInfo;
import org.assertj.core.api.Assertions;
import org.assertj.core.error.ShouldBeFile;
import org.assertj.core.error.ShouldHaveSameContent;
import org.assertj.core.internal.BinaryDiffResult;
import org.assertj.core.internal.FilesBaseTest;
import org.assertj.core.test.TestData;
import org.assertj.core.test.TestFailures;
import org.assertj.core.util.FailureMessages;
import org.assertj.core.util.Lists;
import org.assertj.core.util.diff.Delta;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


/**
 * Tests for <code>{@link org.assertj.core.internal.Files#assertSameContentAs(org.assertj.core.api.AssertionInfo, java.io.File, java.nio.charset.Charset, java.io.File,  java.nio.charset.Charset)}</code>.
 *
 * @author Yvonne Wang
 * @author Joel Costigliola
 */
public class Files_assertSameContentAs_Test extends FilesBaseTest {
    private static File actual;

    private static File expected;

    @Test
    public void should_throw_error_if_expected_is_null() {
        Assertions.assertThatNullPointerException().isThrownBy(() -> files.assertSameContentAs(someInfo(), Files_assertSameContentAs_Test.actual, defaultCharset(), null, defaultCharset())).withMessage("The file to compare to should not be null");
    }

    @Test
    public void should_throw_error_if_expected_is_not_file() {
        Assertions.assertThatIllegalArgumentException().isThrownBy(() -> {
            File notAFile = new File("xyz");
            files.assertSameContentAs(someInfo(), Files_assertSameContentAs_Test.actual, defaultCharset(), notAFile, defaultCharset());
        }).withMessage("Expected file:<'xyz'> should be an existing file");
    }

    @Test
    public void should_fail_if_actual_is_null() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> files.assertSameContentAs(someInfo(), null, defaultCharset(), Files_assertSameContentAs_Test.expected, defaultCharset())).withMessage(FailureMessages.actualIsNull());
    }

    @Test
    public void should_fail_if_actual_is_not_file() {
        AssertionInfo info = TestData.someInfo();
        File notAFile = new File("xyz");
        try {
            files.assertSameContentAs(info, notAFile, Charset.defaultCharset(), Files_assertSameContentAs_Test.expected, Charset.defaultCharset());
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, ShouldBeFile.shouldBeFile(notAFile));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }

    @Test
    public void should_pass_if_files_have_equal_content() {
        unMockedFiles.assertSameContentAs(TestData.someInfo(), Files_assertSameContentAs_Test.actual, Charset.defaultCharset(), Files_assertSameContentAs_Test.actual, Charset.defaultCharset());
    }

    @Test
    public void should_throw_error_wrapping_catched_IOException() throws IOException {
        IOException cause = new IOException();
        Mockito.when(diff.diff(Files_assertSameContentAs_Test.actual, Charset.defaultCharset(), Files_assertSameContentAs_Test.expected, Charset.defaultCharset())).thenThrow(cause);
        Assertions.assertThatExceptionOfType(UncheckedIOException.class).isThrownBy(() -> files.assertSameContentAs(someInfo(), Files_assertSameContentAs_Test.actual, defaultCharset(), Files_assertSameContentAs_Test.expected, defaultCharset())).withCause(cause);
    }

    @Test
    public void should_fail_if_files_do_not_have_equal_content() throws IOException {
        List<Delta<String>> diffs = Lists.newArrayList(delta);
        Mockito.when(diff.diff(Files_assertSameContentAs_Test.actual, Charset.defaultCharset(), Files_assertSameContentAs_Test.expected, Charset.defaultCharset())).thenReturn(diffs);
        Mockito.when(binaryDiff.diff(Files_assertSameContentAs_Test.actual, Files.readAllBytes(Files_assertSameContentAs_Test.expected.toPath()))).thenReturn(new BinaryDiffResult(1, (-1), (-1)));
        AssertionInfo info = TestData.someInfo();
        try {
            files.assertSameContentAs(info, Files_assertSameContentAs_Test.actual, Charset.defaultCharset(), Files_assertSameContentAs_Test.expected, Charset.defaultCharset());
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, ShouldHaveSameContent.shouldHaveSameContent(Files_assertSameContentAs_Test.actual, Files_assertSameContentAs_Test.expected, diffs));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }

    @Test
    public void should_throw_an_error_if_files_cant_be_compared_with_the_given_charsets_even_if_binary_identical() {
        Assertions.assertThatExceptionOfType(UncheckedIOException.class).isThrownBy(() -> unMockedFiles.assertSameContentAs(someInfo(), createFileWithNonUTF8Character(), StandardCharsets.UTF_8, createFileWithNonUTF8Character(), StandardCharsets.UTF_8)).withMessageStartingWith("Unable to compare contents of files");
    }

    @Test
    public void should_fail_if_files_are_not_binary_identical() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> unMockedFiles.assertSameContentAs(someInfo(), createFileWithNonUTF8Character(), StandardCharsets.UTF_8, Files_assertSameContentAs_Test.expected, StandardCharsets.UTF_8)).withMessageEndingWith(String.format(("does not have expected binary content at offset <0>, expecting:%n" + ((" <\"EOF\">%n" + "but was:%n") + " <\"0x0\">"))));
    }
}

