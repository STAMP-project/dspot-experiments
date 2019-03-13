/**
 * Copyright 2018-present Facebook, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.facebook.litho;


import com.facebook.litho.testing.logging.TestComponentsReporter;
import com.facebook.litho.testing.testrunner.ComponentsTestRunner;
import org.assertj.core.api.ThrowableAssert;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * Tests {@link ComponentsReporterTest}
 */
@RunWith(ComponentsTestRunner.class)
public class ComponentsReporterTest {
    private static final String FATAL_MSG = "fatal";

    private static final String ERROR_MSG = "error";

    private static final String WARNING_MSG = "warning";

    private TestComponentsReporter mReporter;

    @Test
    public void testEmitFatalMessage() {
        final Throwable throwable = catchThrowable(new ThrowableAssert.ThrowingCallable() {
            @Override
            public void call() throws Throwable {
                ComponentsReporter.emitMessage(LogLevel.FATAL, ComponentsReporterTest.FATAL_MSG);
                assertThat(mReporter.getLevel()).isEqualTo(LogLevel.FATAL);
                assertThat(mReporter.getMessage()).isEqualTo(ComponentsReporterTest.FATAL_MSG);
            }
        });
        assertThat(throwable).isInstanceOf(RuntimeException.class);
        assertThat(throwable.getMessage()).isEqualTo(ComponentsReporterTest.FATAL_MSG);
    }

    @Test
    public void testEmitErrorMessage() {
        ComponentsReporter.emitMessage(LogLevel.ERROR, ComponentsReporterTest.ERROR_MSG);
        assertThat(mReporter.getLevel()).isEqualTo(LogLevel.ERROR);
        assertThat(mReporter.getMessage()).isEqualTo(ComponentsReporterTest.ERROR_MSG);
    }

    @Test
    public void testEmitWarningMessage() {
        ComponentsReporter.emitMessage(LogLevel.WARNING, ComponentsReporterTest.WARNING_MSG);
        assertThat(mReporter.getLevel()).isEqualTo(LogLevel.WARNING);
        assertThat(mReporter.getMessage()).isEqualTo(ComponentsReporterTest.WARNING_MSG);
    }
}

