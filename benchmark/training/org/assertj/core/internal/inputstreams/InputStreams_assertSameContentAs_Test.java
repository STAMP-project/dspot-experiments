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
import java.util.ArrayList;
import java.util.List;
import org.assertj.core.api.AssertionInfo;
import org.assertj.core.api.Assertions;
import org.assertj.core.error.ShouldHaveSameContent;
import org.assertj.core.internal.InputStreamsBaseTest;
import org.assertj.core.internal.InputStreamsException;
import org.assertj.core.test.TestData;
import org.assertj.core.test.TestFailures;
import org.assertj.core.util.FailureMessages;
import org.assertj.core.util.Lists;
import org.assertj.core.util.diff.Delta;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


/**
 * Tests for <code>{@link InputStreams#assertSameContentAs(AssertionInfo, InputStream, InputStream)}</code>.
 *
 * @author Matthieu Baechler
 */
public class InputStreams_assertSameContentAs_Test extends InputStreamsBaseTest {
    @Test
    public void should_throw_error_if_expected_is_null() {
        Assertions.assertThatNullPointerException().isThrownBy(() -> inputStreams.assertSameContentAs(someInfo(), InputStreamsBaseTest.actual, null)).withMessage("The InputStream to compare to should not be null");
    }

    @Test
    public void should_fail_if_actual_is_null() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> inputStreams.assertSameContentAs(someInfo(), null, InputStreamsBaseTest.expected)).withMessage(FailureMessages.actualIsNull());
    }

    @Test
    public void should_pass_if_inputstreams_have_equal_content() throws IOException {
        Mockito.when(diff.diff(InputStreamsBaseTest.actual, InputStreamsBaseTest.expected)).thenReturn(new ArrayList());
        inputStreams.assertSameContentAs(TestData.someInfo(), InputStreamsBaseTest.actual, InputStreamsBaseTest.expected);
    }

    @Test
    public void should_throw_error_wrapping_catched_IOException() throws IOException {
        IOException cause = new IOException();
        Mockito.when(diff.diff(InputStreamsBaseTest.actual, InputStreamsBaseTest.expected)).thenThrow(cause);
        Assertions.assertThatExceptionOfType(InputStreamsException.class).isThrownBy(() -> inputStreams.assertSameContentAs(someInfo(), InputStreamsBaseTest.actual, InputStreamsBaseTest.expected)).withCause(cause);
    }

    @Test
    public void should_fail_if_inputstreams_do_not_have_equal_content() throws IOException {
        List<Delta<String>> diffs = Lists.newArrayList(((Delta<String>) (Mockito.mock(Delta.class))));
        Mockito.when(diff.diff(InputStreamsBaseTest.actual, InputStreamsBaseTest.expected)).thenReturn(diffs);
        AssertionInfo info = TestData.someInfo();
        try {
            inputStreams.assertSameContentAs(info, InputStreamsBaseTest.actual, InputStreamsBaseTest.expected);
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, ShouldHaveSameContent.shouldHaveSameContent(InputStreamsBaseTest.actual, InputStreamsBaseTest.expected, diffs));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }
}

