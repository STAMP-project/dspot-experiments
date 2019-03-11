/**
 * Copyright 2014 The Bazel Authors. All rights reserved.
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
package com.google.devtools.build.lib.events;


import EventKind.ALL_EVENTS;
import EventKind.ERRORS;
import EventKind.ERRORS_AND_WARNINGS;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests {@link AbstractEventHandler}.
 */
@RunWith(JUnit4.class)
public class AbstractEventHandlerTest {
    @Test
    public void retainsEventMask() {
        assertThat(AbstractEventHandlerTest.create(ALL_EVENTS).getEventMask()).isEqualTo(ALL_EVENTS);
        assertThat(AbstractEventHandlerTest.create(ERRORS_AND_WARNINGS).getEventMask()).isEqualTo(ERRORS_AND_WARNINGS);
        assertThat(AbstractEventHandlerTest.create(ERRORS).getEventMask()).isEqualTo(ERRORS);
    }
}

