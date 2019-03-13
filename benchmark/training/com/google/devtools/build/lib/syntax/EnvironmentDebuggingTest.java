/**
 * Copyright 2018 The Bazel Authors. All Rights Reserved.
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
package com.google.devtools.build.lib.syntax;


import Location.BUILTIN;
import Stepping.INTO;
import Stepping.NONE;
import Stepping.OUT;
import Stepping.OVER;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.devtools.build.lib.events.Location;
import com.google.devtools.build.lib.events.Location.LineAndColumn;
import com.google.devtools.build.lib.syntax.Debuggable.ReadyToPause;
import com.google.devtools.build.lib.vfs.PathFragment;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Unit tests of {@link Environment}s implementation of {@link Debuggable}.
 */
@RunWith(JUnit4.class)
public class EnvironmentDebuggingTest {
    @Test
    public void testListFramesFromGlobalFrame() throws Exception {
        Environment env = EnvironmentDebuggingTest.newEnvironment();
        env.update("a", 1);
        env.update("b", 2);
        env.update("c", 3);
        ImmutableList<DebugFrame> frames = env.listFrames(BUILTIN);
        assertThat(frames).hasSize(1);
        assertThat(frames.get(0)).isEqualTo(DebugFrame.builder().setFunctionName("<top level>").setLocation(BUILTIN).setGlobalBindings(ImmutableMap.of("a", 1, "b", 2, "c", 3)).build());
    }

    @Test
    public void testListFramesFromChildFrame() throws Exception {
        Environment env = EnvironmentDebuggingTest.newEnvironment();
        env.update("a", 1);
        env.update("b", 2);
        env.update("c", 3);
        Location funcallLocation = Location.fromPathAndStartColumn(PathFragment.create("foo/bar"), 0, 0, new LineAndColumn(12, 0));
        EnvironmentDebuggingTest.enterFunctionScope(env, "function", funcallLocation);
        env.update("a", 4);// shadow parent frame var

        env.update("y", 5);
        env.update("z", 6);
        ImmutableList<DebugFrame> frames = env.listFrames(BUILTIN);
        assertThat(frames).hasSize(2);
        assertThat(frames.get(0)).isEqualTo(DebugFrame.builder().setFunctionName("function").setLocation(BUILTIN).setLexicalFrameBindings(ImmutableMap.of("a", 4, "y", 5, "z", 6)).setGlobalBindings(ImmutableMap.of("a", 1, "b", 2, "c", 3)).build());
        assertThat(frames.get(1)).isEqualTo(DebugFrame.builder().setFunctionName("<top level>").setLocation(funcallLocation).setGlobalBindings(ImmutableMap.of("a", 1, "b", 2, "c", 3)).build());
    }

    @Test
    public void testStepIntoFunction() {
        Environment env = EnvironmentDebuggingTest.newEnvironment();
        ReadyToPause predicate = env.stepControl(INTO);
        EnvironmentDebuggingTest.enterFunctionScope(env, "function", BUILTIN);
        assertThat(predicate.test(env)).isTrue();
    }

    @Test
    public void testStepIntoFallsBackToStepOver() {
        // test that when stepping into, we'll fall back to stopping at the next statement in the
        // current frame
        Environment env = EnvironmentDebuggingTest.newEnvironment();
        ReadyToPause predicate = env.stepControl(INTO);
        assertThat(predicate.test(env)).isTrue();
    }

    @Test
    public void testStepIntoFallsBackToStepOut() {
        // test that when stepping into, we'll fall back to stopping when exiting the current frame
        Environment env = EnvironmentDebuggingTest.newEnvironment();
        EnvironmentDebuggingTest.enterFunctionScope(env, "function", BUILTIN);
        ReadyToPause predicate = env.stepControl(INTO);
        env.exitScope();
        assertThat(predicate.test(env)).isTrue();
    }

    @Test
    public void testStepOverFunction() {
        Environment env = EnvironmentDebuggingTest.newEnvironment();
        ReadyToPause predicate = env.stepControl(OVER);
        EnvironmentDebuggingTest.enterFunctionScope(env, "function", BUILTIN);
        assertThat(predicate.test(env)).isFalse();
        env.exitScope();
        assertThat(predicate.test(env)).isTrue();
    }

    @Test
    public void testStepOverFallsBackToStepOut() {
        // test that when stepping over, we'll fall back to stopping when exiting the current frame
        Environment env = EnvironmentDebuggingTest.newEnvironment();
        EnvironmentDebuggingTest.enterFunctionScope(env, "function", BUILTIN);
        ReadyToPause predicate = env.stepControl(OVER);
        env.exitScope();
        assertThat(predicate.test(env)).isTrue();
    }

    @Test
    public void testStepOutOfInnerFrame() {
        Environment env = EnvironmentDebuggingTest.newEnvironment();
        EnvironmentDebuggingTest.enterFunctionScope(env, "function", BUILTIN);
        ReadyToPause predicate = env.stepControl(OUT);
        assertThat(predicate.test(env)).isFalse();
        env.exitScope();
        assertThat(predicate.test(env)).isTrue();
    }

    @Test
    public void testStepOutOfOutermostFrame() {
        Environment env = EnvironmentDebuggingTest.newEnvironment();
        assertThat(env.stepControl(OUT)).isNull();
    }

    @Test
    public void testStepControlWithNoSteppingReturnsNull() {
        Environment env = EnvironmentDebuggingTest.newEnvironment();
        assertThat(env.stepControl(NONE)).isNull();
    }

    @Test
    public void testEvaluateVariableInScope() throws Exception {
        Environment env = EnvironmentDebuggingTest.newEnvironment();
        env.update("a", 1);
        Object result = env.evaluate("a");
        assertThat(result).isEqualTo(1);
    }

    @Test
    public void testEvaluateVariableNotInScopeFails() throws Exception {
        Environment env = EnvironmentDebuggingTest.newEnvironment();
        env.update("a", 1);
        try {
            env.evaluate("b");
            Assert.fail();
        } catch (EvalException e) {
            assertThat(e).hasMessageThat().isEqualTo("name 'b' is not defined");
        }
    }

    @Test
    public void testEvaluateExpressionOnVariableInScope() throws Exception {
        Environment env = EnvironmentDebuggingTest.newEnvironment();
        env.update("a", "string");
        Object result = env.evaluate("a.startswith(\"str\")");
        assertThat(result).isEqualTo(Boolean.TRUE);
    }
}

