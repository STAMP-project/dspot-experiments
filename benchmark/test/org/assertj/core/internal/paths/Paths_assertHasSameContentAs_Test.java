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
import java.util.ArrayList;
import java.util.List;
import org.assertj.core.api.AssertionInfo;
import org.assertj.core.api.Assertions;
import org.assertj.core.error.ShouldBeReadable;
import org.assertj.core.error.ShouldExist;
import org.assertj.core.error.ShouldHaveSameContent;
import org.assertj.core.internal.PathsBaseTest;
import org.assertj.core.test.TestData;
import org.assertj.core.test.TestFailures;
import org.assertj.core.util.FailureMessages;
import org.assertj.core.util.Lists;
import org.assertj.core.util.diff.Delta;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


/**
 * Tests for <code>{@link Paths#assertHasSameContentAs(AssertionInfo, Path, Charset, Path, Charset)}</code>.
 */
public class Paths_assertHasSameContentAs_Test extends MockPathsBaseTest {
    @Test
    public void should_pass_if_path_has_same_content_as_other() throws IOException {
        Mockito.when(diff.diff(actual, Charset.defaultCharset(), other, Charset.defaultCharset())).thenReturn(new ArrayList());
        Mockito.when(nioFilesWrapper.exists(actual)).thenReturn(true);
        Mockito.when(nioFilesWrapper.isReadable(actual)).thenReturn(true);
        Mockito.when(nioFilesWrapper.isReadable(other)).thenReturn(true);
        paths.assertHasSameContentAs(TestData.someInfo(), actual, Charset.defaultCharset(), other, Charset.defaultCharset());
    }

    @Test
    public void should_throw_error_if_other_is_null() {
        Assertions.assertThatNullPointerException().isThrownBy(() -> paths.assertHasSameContentAs(someInfo(), actual, defaultCharset(), null, defaultCharset())).withMessage("The given Path to compare actual content to should not be null");
    }

    @Test
    public void should_fail_if_actual_is_null() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> {
            when(nioFilesWrapper.isReadable(other)).thenReturn(true);
            paths.assertHasSameContentAs(someInfo(), null, defaultCharset(), other, defaultCharset());
        }).withMessage(FailureMessages.actualIsNull());
    }

    @Test
    public void should_fail_if_actual_path_does_not_exist() {
        AssertionInfo info = TestData.someInfo();
        Mockito.when(nioFilesWrapper.exists(actual)).thenReturn(false);
        Mockito.when(nioFilesWrapper.isReadable(other)).thenReturn(true);
        try {
            paths.assertHasSameContentAs(info, actual, Charset.defaultCharset(), other, Charset.defaultCharset());
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, ShouldExist.shouldExist(actual));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }

    @Test
    public void should_fail_if_actual_is_not_a_readable_file() {
        AssertionInfo info = TestData.someInfo();
        Mockito.when(nioFilesWrapper.exists(actual)).thenReturn(true);
        Mockito.when(nioFilesWrapper.isReadable(actual)).thenReturn(false);
        Mockito.when(nioFilesWrapper.isReadable(other)).thenReturn(true);
        try {
            paths.assertHasSameContentAs(info, actual, Charset.defaultCharset(), other, Charset.defaultCharset());
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, ShouldBeReadable.shouldBeReadable(actual));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }

    @Test
    public void should_fail_if_other_is_not_a_readable_file() {
        Mockito.when(nioFilesWrapper.isReadable(other)).thenReturn(false);
        Assertions.assertThatIllegalArgumentException().isThrownBy(() -> paths.assertHasSameContentAs(someInfo(), actual, defaultCharset(), other, defaultCharset())).withMessage(String.format("The given Path <%s> to compare actual content to should be readable", other));
    }

    @Test
    public void should_throw_error_wrapping_catched_IOException() throws IOException {
        IOException cause = new IOException();
        Mockito.when(diff.diff(actual, Charset.defaultCharset(), other, Charset.defaultCharset())).thenThrow(cause);
        Mockito.when(nioFilesWrapper.exists(actual)).thenReturn(true);
        Mockito.when(nioFilesWrapper.isReadable(actual)).thenReturn(true);
        Mockito.when(nioFilesWrapper.isReadable(other)).thenReturn(true);
        Assertions.assertThatExceptionOfType(UncheckedIOException.class).isThrownBy(() -> paths.assertHasSameContentAs(someInfo(), actual, defaultCharset(), other, defaultCharset())).withCause(cause);
    }

    @Test
    public void should_fail_if_actual_and_given_path_does_not_have_the_same_content() throws IOException {
        @SuppressWarnings("unchecked")
        List<Delta<String>> diffs = Lists.newArrayList(((Delta<String>) (Mockito.mock(Delta.class))));
        Mockito.when(diff.diff(actual, Charset.defaultCharset(), other, Charset.defaultCharset())).thenReturn(diffs);
        Mockito.when(nioFilesWrapper.exists(actual)).thenReturn(true);
        Mockito.when(nioFilesWrapper.isReadable(actual)).thenReturn(true);
        Mockito.when(nioFilesWrapper.isReadable(other)).thenReturn(true);
        AssertionInfo info = TestData.someInfo();
        try {
            paths.assertHasSameContentAs(info, actual, Charset.defaultCharset(), other, Charset.defaultCharset());
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, ShouldHaveSameContent.shouldHaveSameContent(actual, other, diffs));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }
}

