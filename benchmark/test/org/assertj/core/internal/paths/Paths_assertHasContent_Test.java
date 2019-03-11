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
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import org.assertj.core.api.AssertionInfo;
import org.assertj.core.api.Assertions;
import org.assertj.core.error.ShouldBeReadable;
import org.assertj.core.error.ShouldExist;
import org.assertj.core.error.ShouldHaveContent;
import org.assertj.core.internal.PathsBaseTest;
import org.assertj.core.test.TestData;
import org.assertj.core.test.TestFailures;
import org.assertj.core.util.FailureMessages;
import org.assertj.core.util.Lists;
import org.assertj.core.util.diff.Delta;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


/**
 * Tests for <code>{@link Paths#assertHasContent(AssertionInfo, Path, String, Charset)}</code>.
 *
 * @author Olivier Michallat
 * @author Joel Costigliola
 */
public class Paths_assertHasContent_Test extends PathsBaseTest {
    private static Path path;

    private static String expected;

    private static Charset charset;

    private Path mockPath;

    @Test
    public void should_pass_if_path_has_expected_text_content() throws IOException {
        Mockito.when(diff.diff(Paths_assertHasContent_Test.path, Paths_assertHasContent_Test.expected, Paths_assertHasContent_Test.charset)).thenReturn(new ArrayList());
        Mockito.when(nioFilesWrapper.exists(Paths_assertHasContent_Test.path)).thenReturn(true);
        Mockito.when(nioFilesWrapper.isReadable(Paths_assertHasContent_Test.path)).thenReturn(true);
        paths.assertHasContent(TestData.someInfo(), Paths_assertHasContent_Test.path, Paths_assertHasContent_Test.expected, Paths_assertHasContent_Test.charset);
    }

    @Test
    public void should_throw_error_if_expected_is_null() {
        Assertions.assertThatNullPointerException().isThrownBy(() -> paths.assertHasContent(someInfo(), Paths_assertHasContent_Test.path, null, Paths_assertHasContent_Test.charset)).withMessage("The text to compare to should not be null");
    }

    @Test
    public void should_fail_if_actual_is_null() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> paths.assertHasContent(someInfo(), null, Paths_assertHasContent_Test.expected, Paths_assertHasContent_Test.charset)).withMessage(FailureMessages.actualIsNull());
    }

    @Test
    public void should_fail_if_actual_path_does_not_exist() {
        AssertionInfo info = TestData.someInfo();
        Mockito.when(nioFilesWrapper.exists(mockPath)).thenReturn(false);
        try {
            paths.assertHasContent(info, mockPath, Paths_assertHasContent_Test.expected, Paths_assertHasContent_Test.charset);
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
            paths.assertHasContent(info, mockPath, Paths_assertHasContent_Test.expected, Paths_assertHasContent_Test.charset);
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, ShouldBeReadable.shouldBeReadable(mockPath));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }

    @Test
    public void should_throw_error_wrapping_catched_IOException() throws IOException {
        IOException cause = new IOException();
        Mockito.when(diff.diff(Paths_assertHasContent_Test.path, Paths_assertHasContent_Test.expected, Paths_assertHasContent_Test.charset)).thenThrow(cause);
        Mockito.when(nioFilesWrapper.exists(Paths_assertHasContent_Test.path)).thenReturn(true);
        Mockito.when(nioFilesWrapper.isReadable(Paths_assertHasContent_Test.path)).thenReturn(true);
        Assertions.assertThatExceptionOfType(UncheckedIOException.class).isThrownBy(() -> paths.assertHasContent(someInfo(), Paths_assertHasContent_Test.path, Paths_assertHasContent_Test.expected, Paths_assertHasContent_Test.charset)).withCause(cause);
    }

    @Test
    public void should_fail_if_path_does_not_have_expected_text_content() throws IOException {
        @SuppressWarnings("unchecked")
        List<Delta<String>> diffs = Lists.newArrayList(((Delta<String>) (Mockito.mock(Delta.class))));
        Mockito.when(diff.diff(Paths_assertHasContent_Test.path, Paths_assertHasContent_Test.expected, Paths_assertHasContent_Test.charset)).thenReturn(diffs);
        Mockito.when(nioFilesWrapper.exists(Paths_assertHasContent_Test.path)).thenReturn(true);
        Mockito.when(nioFilesWrapper.isReadable(Paths_assertHasContent_Test.path)).thenReturn(true);
        AssertionInfo info = TestData.someInfo();
        try {
            paths.assertHasContent(info, Paths_assertHasContent_Test.path, Paths_assertHasContent_Test.expected, Paths_assertHasContent_Test.charset);
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, ShouldHaveContent.shouldHaveContent(Paths_assertHasContent_Test.path, Paths_assertHasContent_Test.charset, diffs));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }
}

