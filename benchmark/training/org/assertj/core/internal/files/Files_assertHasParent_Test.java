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
import org.assertj.core.error.ShouldHaveParent;
import org.assertj.core.internal.FilesBaseTest;
import org.assertj.core.test.TestData;
import org.assertj.core.test.TestFailures;
import org.assertj.core.util.FailureMessages;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


/**
 * Tests for
 * <code>{@link org.assertj.core.internal.Files#assertHasParent(org.assertj.core.api.AssertionInfo, java.io.File, java.io.File)}</code>
 * .
 *
 * @author Jean-Christophe Gay
 */
public class Files_assertHasParent_Test extends FilesBaseTest {
    private File actual = new File("./some/test");

    private File expectedParent = new File("./some");

    @Test
    public void should_throw_error_if_actual_is_null() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> files.assertHasParent(someInfo(), null, expectedParent)).withMessage(FailureMessages.actualIsNull());
    }

    @Test
    public void should_throw_npe_if_expected_is_null() {
        Assertions.assertThatNullPointerException().isThrownBy(() -> files.assertHasParent(someInfo(), actual, null)).withMessage("The expected parent file should not be null.");
    }

    @Test
    public void should_fail_if_actual_has_no_parent() {
        AssertionInfo info = TestData.someInfo();
        File withoutParent = new File("without-parent");
        try {
            files.assertHasParent(info, withoutParent, expectedParent);
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, ShouldHaveParent.shouldHaveParent(withoutParent, expectedParent));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }

    @Test
    public void should_fail_if_actual_does_not_have_the_expected_parent() {
        AssertionInfo info = TestData.someInfo();
        File expectedParent = new File("./expected-parent");
        try {
            files.assertHasParent(info, actual, expectedParent);
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, ShouldHaveParent.shouldHaveParent(actual, expectedParent));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }

    @Test
    public void should_pass_if_actual_has_expected_parent() {
        files.assertHasParent(TestData.someInfo(), actual, expectedParent);
    }

    @Test
    public void should_pass_if_actual_has_expected_parent_when_actual_form_is_absolute() {
        files.assertHasParent(TestData.someInfo(), actual.getAbsoluteFile(), expectedParent);
    }

    @Test
    public void should_pass_if_actual_has_expected_parent_when_actual_form_is_canonical() throws Exception {
        files.assertHasParent(TestData.someInfo(), actual.getCanonicalFile(), expectedParent);
    }

    @Test
    public void should_throw_exception_when_canonical_form_representation_fail() throws Exception {
        File actual = Mockito.mock(File.class);
        File expectedParent = Mockito.mock(File.class);
        Mockito.when(actual.getParentFile()).thenReturn(expectedParent);
        Mockito.when(expectedParent.getCanonicalFile()).thenThrow(new IOException());
        Assertions.assertThatExceptionOfType(UncheckedIOException.class).isThrownBy(() -> files.assertHasParent(someInfo(), actual, expectedParent));
    }

    @Test
    public void should_throw_exception_when_canonical_form_representation_fail_for_expected_parent() throws Exception {
        File expectedParent = Mockito.mock(File.class);
        Mockito.when(expectedParent.getCanonicalFile()).thenThrow(new IOException());
        Assertions.assertThatExceptionOfType(UncheckedIOException.class).isThrownBy(() -> files.assertHasParent(someInfo(), actual, expectedParent));
    }
}

