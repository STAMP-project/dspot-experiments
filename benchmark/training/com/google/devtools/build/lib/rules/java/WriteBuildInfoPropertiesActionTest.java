/**
 * Copyright 2015 The Bazel Authors. All rights reserved.
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
package com.google.devtools.build.lib.rules.java;


import com.google.common.base.Joiner;
import com.google.devtools.build.lib.testutil.FoundationTestCase;
import java.io.IOException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests for {@link WriteBuildInfoPropertiesAction} utilities methods
 */
@RunWith(JUnit4.class)
public class WriteBuildInfoPropertiesActionTest extends FoundationTestCase {
    private static final Joiner LINE_JOINER = Joiner.on("\r\n");

    private static final Joiner LINEFEED_JOINER = Joiner.on("\n");

    @Test
    public void testStripFirstLine() throws IOException {
        assertStripFirstLine("", "");
        assertStripFirstLine("", "no linefeed");
        assertStripFirstLine("", "no", "linefeed");
        assertStripFirstLine(WriteBuildInfoPropertiesActionTest.LINEFEED_JOINER.join("toto", "titi"), WriteBuildInfoPropertiesActionTest.LINEFEED_JOINER.join("# timestamp comment", "toto", "titi"));
        assertStripFirstLine(WriteBuildInfoPropertiesActionTest.LINE_JOINER.join("toto", "titi"), WriteBuildInfoPropertiesActionTest.LINE_JOINER.join("# timestamp comment", "toto", "titi"));
        assertStripFirstLine(WriteBuildInfoPropertiesActionTest.LINEFEED_JOINER.join("toto", "titi"), "# timestamp comment\n", "toto\n", "titi");
        assertStripFirstLine(WriteBuildInfoPropertiesActionTest.LINE_JOINER.join("toto", "titi"), "# timestamp comment\r\n", "toto\r\n", "titi");
    }
}

