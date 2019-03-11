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
import org.assertj.core.error.ShouldHaveName;
import org.assertj.core.internal.FilesBaseTest;
import org.assertj.core.test.TestData;
import org.assertj.core.test.TestFailures;
import org.assertj.core.util.FailureMessages;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


/**
 * Tests for
 * <code>{@link org.assertj.core.internal.Files#assertHasName(org.assertj.core.api.AssertionInfo, java.io.File, String)} </code>
 * .
 *
 * @author Jean-Christophe Gay
 */
public class Files_assertHasName_Test extends FilesBaseTest {
    private String expectedName = "expected.name";

    @Test
    public void should_throw_error_if_actual_is_null() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> files.assertHasName(someInfo(), null, expectedName)).withMessage(FailureMessages.actualIsNull());
    }

    @Test
    public void should_throw_npe_if_name_is_null() {
        Assertions.assertThatNullPointerException().isThrownBy(() -> files.assertHasName(someInfo(), actual, null)).withMessage("The expected name should not be null.");
    }

    @Test
    public void should_throw_error_if_actual_does_not_have_the_expected_name() {
        AssertionInfo info = TestData.someInfo();
        Mockito.when(actual.getName()).thenReturn("not.expected.name");
        try {
            files.assertHasName(info, actual, expectedName);
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, ShouldHaveName.shouldHaveName(actual, expectedName));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }

    @Test
    public void should_pass_if_actual_has_expected_extension() {
        Mockito.when(actual.getName()).thenReturn(expectedName);
        files.assertHasName(TestData.someInfo(), actual, expectedName);
    }
}

