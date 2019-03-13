/**
 * Copyright 2017 The Bazel Authors. All rights reserved.
 */
/**
 *
 */
/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
/**
 * you may not use this file except in compliance with the License.
 */
/**
 * You may obtain a copy of the License at
 */
/**
 *
 */
/**
 * http://www.apache.org/licenses/LICENSE-2.0
 */
/**
 *
 */
/**
 * Unless required by applicable law or agreed to in writing, software
 */
/**
 * distributed under the License is distributed on an "AS IS" BASIS,
 */
/**
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 */
/**
 * See the License for the specific language governing permissions and
 */
/**
 * limitations under the License.
 */
package com.google.devtools.build.lib.shell;


import com.google.devtools.build.lib.testutil.MoreAsserts;
import java.time.Duration;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Unit tests for {@link TerminationStatus}.
 */
@RunWith(JUnit4.class)
public final class TerminationStatusTest {
    @Test
    public void testBuilder_WithNoWaitResponse() {
        MoreAsserts.assertThrows(IllegalStateException.class, () -> TerminationStatus.builder().setTimedOut(false).build());
    }

    @Test
    public void testBuilder_WithNoTimedOut() {
        MoreAsserts.assertThrows(IllegalStateException.class, () -> TerminationStatus.builder().setWaitResponse(0).build());
    }

    @Test
    public void testBuilder_WithNoExecutionTime() {
        TerminationStatus terminationStatus = TerminationStatus.builder().setWaitResponse(0).setTimedOut(false).build();
        assertThat(terminationStatus.getWallExecutionTime()).isEmpty();
        assertThat(terminationStatus.getUserExecutionTime()).isEmpty();
        assertThat(terminationStatus.getSystemExecutionTime()).isEmpty();
    }

    @Test
    public void testBuilder_WithExecutionTime() {
        TerminationStatus terminationStatus = TerminationStatus.builder().setWaitResponse(0).setTimedOut(false).setWallExecutionTime(Duration.ofMillis(1929)).setUserExecutionTime(Duration.ofMillis(1492)).setSystemExecutionTime(Duration.ofMillis(1787)).build();
        assertThat(terminationStatus.getWallExecutionTime()).isPresent();
        assertThat(terminationStatus.getWallExecutionTime()).hasValue(Duration.ofMillis(1929));
        assertThat(terminationStatus.getUserExecutionTime()).isPresent();
        assertThat(terminationStatus.getUserExecutionTime()).hasValue(Duration.ofMillis(1492));
        assertThat(terminationStatus.getSystemExecutionTime()).isPresent();
        assertThat(terminationStatus.getSystemExecutionTime()).hasValue(Duration.ofMillis(1787));
    }
}

