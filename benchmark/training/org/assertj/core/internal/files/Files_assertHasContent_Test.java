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
import java.util.ArrayList;
import java.util.List;
import org.assertj.core.api.AssertionInfo;
import org.assertj.core.api.Assertions;
import org.assertj.core.error.ShouldBeFile;
import org.assertj.core.error.ShouldHaveContent;
import org.assertj.core.internal.FilesBaseTest;
import org.assertj.core.test.TestData;
import org.assertj.core.test.TestFailures;
import org.assertj.core.util.FailureMessages;
import org.assertj.core.util.Lists;
import org.assertj.core.util.diff.Delta;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


/**
 * Tests for <code>{@link Files#assertHasContent(AssertionInfo, File, String, Charset)}</code>.
 *
 * @author Olivier Michallat
 * @author Joel Costigliola
 */
public class Files_assertHasContent_Test extends FilesBaseTest {
    private static File actual;

    private static String expected;

    private static Charset charset;

    @Test
    public void should_throw_error_if_expected_is_null() {
        Assertions.assertThatNullPointerException().isThrownBy(() -> files.assertHasContent(someInfo(), Files_assertHasContent_Test.actual, null, Files_assertHasContent_Test.charset)).withMessage("The text to compare to should not be null");
    }

    @Test
    public void should_fail_if_actual_is_null() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> files.assertHasContent(someInfo(), null, Files_assertHasContent_Test.expected, Files_assertHasContent_Test.charset)).withMessage(FailureMessages.actualIsNull());
    }

    @Test
    public void should_fail_if_actual_is_not_file() {
        AssertionInfo info = TestData.someInfo();
        File notAFile = new File("xyz");
        try {
            files.assertHasContent(info, notAFile, Files_assertHasContent_Test.expected, Files_assertHasContent_Test.charset);
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, ShouldBeFile.shouldBeFile(notAFile));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }

    @Test
    public void should_pass_if_file_has_text_content() throws IOException {
        Mockito.when(diff.diff(Files_assertHasContent_Test.actual, Files_assertHasContent_Test.expected, Files_assertHasContent_Test.charset)).thenReturn(new ArrayList());
        files.assertHasContent(TestData.someInfo(), Files_assertHasContent_Test.actual, Files_assertHasContent_Test.expected, Files_assertHasContent_Test.charset);
    }

    @Test
    public void should_throw_error_wrapping_catched_IOException() throws IOException {
        IOException cause = new IOException();
        Mockito.when(diff.diff(Files_assertHasContent_Test.actual, Files_assertHasContent_Test.expected, Files_assertHasContent_Test.charset)).thenThrow(cause);
        Assertions.assertThatExceptionOfType(UncheckedIOException.class).isThrownBy(() -> files.assertHasContent(someInfo(), Files_assertHasContent_Test.actual, Files_assertHasContent_Test.expected, Files_assertHasContent_Test.charset)).withCause(cause);
    }

    @Test
    public void should_fail_if_file_does_not_have_expected_text_content() throws IOException {
        List<Delta<String>> diffs = Lists.newArrayList(delta);
        Mockito.when(diff.diff(Files_assertHasContent_Test.actual, Files_assertHasContent_Test.expected, Files_assertHasContent_Test.charset)).thenReturn(diffs);
        AssertionInfo info = TestData.someInfo();
        try {
            files.assertHasContent(info, Files_assertHasContent_Test.actual, Files_assertHasContent_Test.expected, Files_assertHasContent_Test.charset);
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, ShouldHaveContent.shouldHaveContent(Files_assertHasContent_Test.actual, Files_assertHasContent_Test.charset, diffs));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }
}

