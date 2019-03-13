/**
 * Copyright 2007 The Bazel Authors. All rights reserved.
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
package com.google.devtools.build.lib.analysis.config;


import com.google.common.collect.ImmutableList;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests {@link RunUnderConverter}.
 */
@RunWith(JUnit4.class)
public class RunUnderConverterTest {
    @Test
    public void testConverter() throws Exception {
        assertEqualsRunUnder("command", null, "command", ImmutableList.<String>of());
        assertEqualsRunUnder("command -c", null, "command", ImmutableList.of("-c"));
        assertEqualsRunUnder("command -c --out=all", null, "command", ImmutableList.of("-c", "--out=all"));
        assertEqualsRunUnder("//run:under", "//run:under", null, ImmutableList.<String>of());
        assertEqualsRunUnder("//run:under -c", "//run:under", null, ImmutableList.of("-c"));
        assertEqualsRunUnder("//run:under -c --out=all", "//run:under", null, ImmutableList.of("-c", "--out=all"));
        assertRunUnderFails("", "Empty command");
    }
}

