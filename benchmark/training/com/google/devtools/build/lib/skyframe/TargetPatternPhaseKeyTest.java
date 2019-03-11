/**
 * Copyright 2016 The Bazel Authors. All rights reserved.
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
package com.google.devtools.build.lib.skyframe;


import com.google.common.collect.ImmutableList;
import com.google.common.testing.EqualsTester;
import com.google.common.testing.NullPointerTester;
import com.google.devtools.build.lib.skyframe.TargetPatternPhaseValue.TargetPatternPhaseKey;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests for {@link TargetPatternPhaseKey}.
 */
@RunWith(JUnit4.class)
public class TargetPatternPhaseKeyTest {
    static enum Flag {

        COMPILE_ONE_DEPENDENCY,
        BUILD_TESTS_ONLY,
        DETERMINE_TESTS;}

    @Test
    public void testEquality() throws Exception {
        new EqualsTester().addEqualityGroup(of(ImmutableList.of("a"), "offset")).addEqualityGroup(of(ImmutableList.of("b"), "offset")).addEqualityGroup(of(ImmutableList.of("b"), "")).addEqualityGroup(of(ImmutableList.of("c"), "")).addEqualityGroup(of(ImmutableList.<String>of(), "")).addEqualityGroup(of(ImmutableList.<String>of(), "", ImmutableList.<String>of(), false, true, null, TargetPatternPhaseKeyTest.Flag.COMPILE_ONE_DEPENDENCY)).addEqualityGroup(of(ImmutableList.<String>of(), "", ImmutableList.<String>of(), false, false, null, TargetPatternPhaseKeyTest.Flag.COMPILE_ONE_DEPENDENCY)).addEqualityGroup(of(ImmutableList.<String>of(), "", ImmutableList.<String>of(), true, true, null, TargetPatternPhaseKeyTest.Flag.COMPILE_ONE_DEPENDENCY)).addEqualityGroup(of(ImmutableList.<String>of(), "", ImmutableList.<String>of(), true, false, null, TargetPatternPhaseKeyTest.Flag.COMPILE_ONE_DEPENDENCY)).addEqualityGroup(of(ImmutableList.<String>of(), "", ImmutableList.<String>of(), false, true, emptyTestFilter(), TargetPatternPhaseKeyTest.Flag.BUILD_TESTS_ONLY)).addEqualityGroup(of(ImmutableList.<String>of(), "", ImmutableList.<String>of(), true, true, emptyTestFilter(), TargetPatternPhaseKeyTest.Flag.BUILD_TESTS_ONLY)).addEqualityGroup(of(ImmutableList.<String>of(), "", ImmutableList.<String>of(), false, true, emptyTestFilter(), TargetPatternPhaseKeyTest.Flag.DETERMINE_TESTS)).addEqualityGroup(of(ImmutableList.<String>of(), "", ImmutableList.<String>of(), true, true, emptyTestFilter(), TargetPatternPhaseKeyTest.Flag.DETERMINE_TESTS)).addEqualityGroup(of(ImmutableList.<String>of(), "", ImmutableList.<String>of("a"), false, true, null)).addEqualityGroup(of(ImmutableList.<String>of(), "", ImmutableList.<String>of("a"), true, true, null)).testEquals();
    }

    @Test
    public void testNull() throws Exception {
        new NullPointerTester().testAllPublicConstructors(TargetPatternPhaseKey.class);
    }
}

