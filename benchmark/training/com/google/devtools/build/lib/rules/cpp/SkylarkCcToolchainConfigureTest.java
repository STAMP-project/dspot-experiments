/**
 * Copyright 2018 The Bazel Authors. All rights reserved.
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
package com.google.devtools.build.lib.rules.cpp;


import com.google.devtools.build.lib.syntax.SkylarkList.MutableList;
import com.google.devtools.build.lib.syntax.util.EvaluationTestCase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests for cc autoconfiguration.
 */
@RunWith(JUnit4.class)
public class SkylarkCcToolchainConfigureTest extends EvaluationTestCase {
    @Test
    public void testSplitEscaped() throws Exception {
        newTest().testStatement("split_escaped('a:b:c', ':')", MutableList.of(env, "a", "b", "c")).testStatement("split_escaped('a%:b', ':')", MutableList.of(env, "a:b")).testStatement("split_escaped('a%%b', ':')", MutableList.of(env, "a%b")).testStatement("split_escaped('a:::b', ':')", MutableList.of(env, "a", "", "", "b")).testStatement("split_escaped('a:b%:c', ':')", MutableList.of(env, "a", "b:c")).testStatement("split_escaped('a%%:b:c', ':')", MutableList.of(env, "a%", "b", "c")).testStatement("split_escaped(':a', ':')", MutableList.of(env, "", "a")).testStatement("split_escaped('a:', ':')", MutableList.of(env, "a", "")).testStatement("split_escaped('::a::', ':')", MutableList.of(env, "", "", "a", "", "")).testStatement("split_escaped('%%%:a%%%%:b', ':')", MutableList.of(env, "%:a%%", "b")).testStatement("split_escaped('', ':')", MutableList.of(env, "")).testStatement("split_escaped('%', ':')", MutableList.of(env, "%")).testStatement("split_escaped('%%', ':')", MutableList.of(env, "%")).testStatement("split_escaped('%:', ':')", MutableList.of(env, ":")).testStatement("split_escaped(':', ':')", MutableList.of(env, "", "")).testStatement("split_escaped('a%%b', ':')", MutableList.of(env, "a%b")).testStatement("split_escaped('a%:', ':')", MutableList.of(env, "a:"));
    }
}

