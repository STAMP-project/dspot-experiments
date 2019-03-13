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
package com.google.devtools.build.lib.exec;


import SpawnActionContextMaps.Builder;
import com.google.common.collect.ImmutableList;
import com.google.common.eventbus.EventBus;
import com.google.devtools.build.lib.actions.ActionContext;
import com.google.devtools.build.lib.actions.ActionExecutionContext;
import com.google.devtools.build.lib.actions.ExecException;
import com.google.devtools.build.lib.actions.ExecutionStrategy;
import com.google.devtools.build.lib.actions.Spawn;
import com.google.devtools.build.lib.actions.SpawnActionContext;
import com.google.devtools.build.lib.actions.SpawnResult;
import com.google.devtools.build.lib.analysis.test.TestActionContext;
import com.google.devtools.build.lib.analysis.test.TestResult;
import com.google.devtools.build.lib.analysis.test.TestRunnerAction;
import com.google.devtools.build.lib.events.Reporter;
import com.google.devtools.build.lib.testutil.Suite;
import com.google.devtools.build.lib.testutil.TestSpec;
import com.google.devtools.build.lib.util.RegexFilter.RegexFilterConverter;
import com.google.devtools.build.lib.vfs.Path;
import com.google.devtools.build.lib.view.test.TestStatus.TestResultData;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests of {@link SpawnActionContextMaps}.
 */
@RunWith(JUnit4.class)
@TestSpec(size = Suite.SMALL_TESTS)
public class SpawnActionContextMapsTest {
    private Builder builder;

    private final RegexFilterConverter converter = new RegexFilterConverter();

    private final EventBus bus = new EventBus();

    private final Reporter reporter = new Reporter(bus);

    private static final SpawnActionContextMapsTest.AC1 ac1 = new SpawnActionContextMapsTest.AC1();

    private static final SpawnActionContextMapsTest.AC2 ac2 = new SpawnActionContextMapsTest.AC2();

    private static final ImmutableList<ActionContextProvider> PROVIDERS = ImmutableList.of(new ActionContextProvider() {
        @Override
        public Iterable<? extends ActionContext> getActionContexts() {
            return ImmutableList.of(SpawnActionContextMapsTest.ac1, SpawnActionContextMapsTest.ac2, new SpawnActionContextMapsTest.ACTest());
        }
    });

    @Test
    public void duplicateMnemonics_bothGetStored() throws Exception {
        builder.strategyByMnemonicMap().put("Spawn1", "ac1");
        builder.strategyByMnemonicMap().put("Spawn1", "ac2");
        SpawnActionContextMaps maps = builder.build(SpawnActionContextMapsTest.PROVIDERS, "actest", true);
        List<SpawnActionContext> result = maps.getSpawnActionContexts(mockSpawn("Spawn1", null), reporter);
        assertThat(result).containsExactly(SpawnActionContextMapsTest.ac1, SpawnActionContextMapsTest.ac2);
    }

    @Test
    public void emptyStrategyFallsBackToEmptyMnemonicNotToDefault() throws Exception {
        builder.strategyByMnemonicMap().put("Spawn1", "");
        builder.strategyByMnemonicMap().put("", "ac2");
        SpawnActionContextMaps maps = builder.build(SpawnActionContextMapsTest.PROVIDERS, "actest", false);
        List<SpawnActionContext> result = maps.getSpawnActionContexts(mockSpawn("Spawn1", null), reporter);
        assertThat(result).containsExactly(SpawnActionContextMapsTest.ac2);
    }

    @Test
    public void multipleRegexps_firstMatchWins() throws Exception {
        builder.addStrategyByRegexp(converter.convert("foo"), ImmutableList.of("ac1"));
        builder.addStrategyByRegexp(converter.convert("foo/bar"), ImmutableList.of("ac2"));
        SpawnActionContextMaps maps = builder.build(SpawnActionContextMapsTest.PROVIDERS, "actest", false);
        List<SpawnActionContext> result = maps.getSpawnActionContexts(mockSpawn(null, "Doing something with foo/bar/baz"), reporter);
        assertThat(result).containsExactly(SpawnActionContextMapsTest.ac1);
    }

    @Test
    public void regexpAndMnemonic_regexpWins() throws Exception {
        builder.strategyByMnemonicMap().put("Spawn1", "ac1");
        builder.addStrategyByRegexp(converter.convert("foo/bar"), ImmutableList.of("ac2"));
        SpawnActionContextMaps maps = builder.build(SpawnActionContextMapsTest.PROVIDERS, "actest", false);
        List<SpawnActionContext> result = maps.getSpawnActionContexts(mockSpawn("Spawn1", "Doing something with foo/bar/baz"), reporter);
        assertThat(result).containsExactly(SpawnActionContextMapsTest.ac2);
    }

    @Test
    public void duplicateContext_noException() throws Exception {
        builder.strategyByContextMap().put(SpawnActionContextMapsTest.AC1.class, "one");
        builder.strategyByContextMap().put(SpawnActionContextMapsTest.AC1.class, "two");
        builder.strategyByContextMap().put(SpawnActionContextMapsTest.AC1.class, "");
    }

    @ExecutionStrategy(contextType = SpawnActionContext.class, name = "ac1")
    private static class AC1 implements SpawnActionContext {
        @Override
        public List<SpawnResult> exec(Spawn spawn, ActionExecutionContext actionExecutionContext) throws ExecException, InterruptedException {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean canExec(Spawn spawn) {
            return true;
        }
    }

    @ExecutionStrategy(contextType = SpawnActionContext.class, name = "ac2")
    private static class AC2 implements SpawnActionContext {
        @Override
        public List<SpawnResult> exec(Spawn spawn, ActionExecutionContext actionExecutionContext) throws ExecException, InterruptedException {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean canExec(Spawn spawn) {
            return true;
        }
    }

    @ExecutionStrategy(contextType = TestActionContext.class, name = "actest")
    private static class ACTest implements TestActionContext {
        @Override
        public TestRunnerSpawn createTestRunnerSpawn(TestRunnerAction testRunnerAction, ActionExecutionContext actionExecutionContext) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean isTestKeepGoing() {
            throw new UnsupportedOperationException();
        }

        @Override
        public TestResult newCachedTestResult(Path execRoot, TestRunnerAction action, TestResultData cached) {
            throw new UnsupportedOperationException();
        }
    }
}

