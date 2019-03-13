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


import org.assertj.core.api.AssertionInfo;
import org.assertj.core.api.Assertions;
import org.assertj.core.error.ShouldBeFile;
import org.assertj.core.error.ShouldHaveExtension;
import org.assertj.core.internal.FilesBaseTest;
import org.assertj.core.test.TestData;
import org.assertj.core.test.TestFailures;
import org.assertj.core.util.FailureMessages;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


/**
 * Tests for
 * <code>{@link org.assertj.core.internal.Files#assertHasExtension(org.assertj.core.api.AssertionInfo, java.io.File, String)}</code>
 * .
 *
 * @author Jean-Christophe Gay
 */
public class Files_assertHasExtension_Test extends FilesBaseTest {
    private String expectedExtension = "java";

    @Test
    public void should_throw_error_if_actual_is_null() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> files.assertHasExtension(someInfo(), null, expectedExtension)).withMessage(FailureMessages.actualIsNull());
    }

    @Test
    public void should_throw_npe_if_extension_is_null() {
        Assertions.assertThatNullPointerException().isThrownBy(() -> files.assertHasExtension(someInfo(), actual, null)).withMessage("The expected extension should not be null.");
    }

    @Test
    public void should_throw_error_if_actual_is_not_a_file() {
        AssertionInfo info = TestData.someInfo();
        Mockito.when(actual.isFile()).thenReturn(false);
        try {
            files.assertHasExtension(info, actual, expectedExtension);
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, ShouldBeFile.shouldBeFile(actual));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }

    @Test
    public void should_throw_error_if_actual_does_not_have_the_expected_extension() {
        AssertionInfo info = TestData.someInfo();
        Mockito.when(actual.isFile()).thenReturn(true);
        Mockito.when(actual.getName()).thenReturn("file.png");
        try {
            files.assertHasExtension(info, actual, expectedExtension);
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, ShouldHaveExtension.shouldHaveExtension(actual, "png", expectedExtension));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }

    @Test
    public void should_pass_if_actual_has_expected_extension() {
        Mockito.when(actual.isFile()).thenReturn(true);
        Mockito.when(actual.getName()).thenReturn("file.java");
        files.assertHasExtension(TestData.someInfo(), actual, expectedExtension);
    }
}

