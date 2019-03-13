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
package org.assertj.core.internal.inputstreams;


import java.io.IOException;
import java.util.Collections;
import java.util.List;
import org.assertj.core.api.AssertionInfo;
import org.assertj.core.api.Assertions;
import org.assertj.core.error.ShouldHaveSameContent;
import org.assertj.core.internal.InputStreamsBaseTest;
import org.assertj.core.internal.InputStreamsException;
import org.assertj.core.test.TestData;
import org.assertj.core.util.FailureMessages;
import org.assertj.core.util.Lists;
import org.assertj.core.util.diff.Delta;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;


/**
 * Tests for <code>{@link InputStreams#assertHasContent(AssertionInfo, InputStream, String)}</code>.
 *
 * @author Stephan Windm?ller
 */
public class InputStreams_assertHasContent_Test extends InputStreamsBaseTest {
    @Test
    public void should_throw_error_if_expected_is_null() {
        Assertions.assertThatNullPointerException().isThrownBy(() -> inputStreams.assertHasContent(someInfo(), InputStreamsBaseTest.actual, null)).withMessage("The String to compare to should not be null");
    }

    @Test
    public void should_fail_if_actual_is_null() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> inputStreams.assertHasContent(someInfo(), null, "")).withMessage(FailureMessages.actualIsNull());
    }

    @Test
    public void should_pass_if_inputstream_and_string_have_equal_content() throws IOException {
        // GIVEN
        BDDMockito.given(diff.diff(InputStreamsBaseTest.actual, InputStreamsBaseTest.expected)).willReturn(Collections.emptyList());
        // THEN
        inputStreams.assertHasContent(TestData.someInfo(), InputStreamsBaseTest.actual, InputStreamsBaseTest.expectedString);
    }

    @Test
    public void should_throw_error_wrapping_catched_IOException() throws IOException {
        // GIVEN
        IOException cause = new IOException();
        BDDMockito.given(diff.diff(InputStreamsBaseTest.actual, InputStreamsBaseTest.expectedString)).willThrow(cause);
        // WHEN
        Throwable error = Assertions.catchThrowable(() -> inputStreams.assertHasContent(someInfo(), InputStreamsBaseTest.actual, InputStreamsBaseTest.expectedString));
        // THEN
        Assertions.assertThat(error).isInstanceOf(InputStreamsException.class).hasCause(cause);
    }

    @Test
    public void should_fail_if_inputstream_and_string_do_not_have_equal_content() throws IOException {
        // GIVEN
        List<Delta<String>> diffs = Lists.list(((Delta<String>) (Mockito.mock(Delta.class))));
        BDDMockito.given(diff.diff(InputStreamsBaseTest.actual, InputStreamsBaseTest.expectedString)).willReturn(diffs);
        AssertionInfo info = TestData.someInfo();
        // WHEN
        Assertions.catchThrowable(() -> inputStreams.assertHasContent(someInfo(), InputStreamsBaseTest.actual, InputStreamsBaseTest.expectedString));
        // THEN
        Mockito.verify(failures).failure(info, ShouldHaveSameContent.shouldHaveSameContent(InputStreamsBaseTest.actual, InputStreamsBaseTest.expectedString, diffs));
    }
}

