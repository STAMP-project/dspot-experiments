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
import org.assertj.core.api.AssertionInfo;
import org.assertj.core.api.Assertions;
import org.assertj.core.error.ShouldHaveNoParent;
import org.assertj.core.internal.FilesBaseTest;
import org.assertj.core.test.TestData;
import org.assertj.core.test.TestFailures;
import org.assertj.core.util.FailureMessages;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


/**
 * Tests for
 * <code>{@link org.assertj.core.internal.Files#assertHasNoParent(org.assertj.core.api.AssertionInfo, java.io.File)}</code>
 * .
 *
 * @author Jean-Christophe Gay
 */
public class Files_assertHasNoParent_Test extends FilesBaseTest {
    @Test
    public void should_throw_error_if_actual_is_null() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> files.assertHasNoParent(someInfo(), null)).withMessage(FailureMessages.actualIsNull());
    }

    @Test
    public void should_fail_if_actual_has_parent() {
        AssertionInfo info = TestData.someInfo();
        Mockito.when(actual.getParentFile()).thenReturn(Mockito.mock(File.class));
        try {
            files.assertHasNoParent(info, actual);
        } catch (AssertionError e) {
            Mockito.verify(failures).failure(info, ShouldHaveNoParent.shouldHaveNoParent(actual));
            return;
        }
        TestFailures.failBecauseExpectedAssertionErrorWasNotThrown();
    }

    @Test
    public void should_pass_if_actual_has_no_parent() {
        Mockito.when(actual.getParentFile()).thenReturn(null);
        files.assertHasNoParent(TestData.someInfo(), actual);
    }
}

