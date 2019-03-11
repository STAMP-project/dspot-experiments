/**
 * Copyright 2017 The Bazel Authors. All Rights Reserved.
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


import SpawnCache.NO_CACHE;
import Status.NON_ZERO_EXIT;
import Status.SUCCESS;
import com.google.devtools.build.lib.actions.ActionExecutionContext;
import com.google.devtools.build.lib.actions.Artifact;
import com.google.devtools.build.lib.actions.ArtifactRoot;
import com.google.devtools.build.lib.actions.FutureSpawn;
import com.google.devtools.build.lib.actions.MetadataProvider;
import com.google.devtools.build.lib.actions.Spawn;
import com.google.devtools.build.lib.actions.SpawnResult;
import com.google.devtools.build.lib.exec.Protos.Digest;
import com.google.devtools.build.lib.exec.Protos.EnvironmentVariable;
import com.google.devtools.build.lib.exec.Protos.File;
import com.google.devtools.build.lib.exec.Protos.SpawnExec;
import com.google.devtools.build.lib.exec.SpawnCache.CacheHandle;
import com.google.devtools.build.lib.exec.SpawnRunner.SpawnExecutionContext;
import com.google.devtools.build.lib.exec.util.SpawnBuilder;
import com.google.devtools.build.lib.testutil.Scratch;
import com.google.devtools.build.lib.testutil.Suite;
import com.google.devtools.build.lib.testutil.TestSpec;
import com.google.devtools.build.lib.util.io.MessageOutputStream;
import com.google.devtools.build.lib.vfs.FileSystem;
import com.google.devtools.build.lib.vfs.Path;
import com.google.devtools.build.lib.vfs.inmemoryfs.InMemoryFileSystem;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;


/**
 * Tests for {@link BlazeExecutor}.
 */
@RunWith(JUnit4.class)
@TestSpec(size = Suite.SMALL_TESTS)
public class AbstractSpawnStrategyTest {
    private static class TestedSpawnStrategy extends AbstractSpawnStrategy {
        public TestedSpawnStrategy(Path execRoot, SpawnRunner spawnRunner) {
            super(execRoot, spawnRunner);
        }
    }

    private static final Spawn SIMPLE_SPAWN = new SpawnBuilder("/bin/echo", "Hi!").withEnvironment("VARIABLE", "value").build();

    private final FileSystem fs = new InMemoryFileSystem();

    private final Path execRoot = fs.getPath("/execroot");

    private Scratch scratch;

    private ArtifactRoot rootDir;

    @Mock
    private SpawnRunner spawnRunner;

    @Mock
    private ActionExecutionContext actionExecutionContext;

    @Mock
    private MessageOutputStream messageOutput;

    @Test
    public void testZeroExit() throws Exception {
        Mockito.when(actionExecutionContext.getContext(ArgumentMatchers.eq(SpawnCache.class))).thenReturn(NO_CACHE);
        Mockito.when(actionExecutionContext.getExecRoot()).thenReturn(execRoot);
        SpawnResult spawnResult = new SpawnResult.Builder().setStatus(SUCCESS).setRunnerName("test").build();
        Mockito.when(spawnRunner.execAsync(ArgumentMatchers.any(Spawn.class), ArgumentMatchers.any(SpawnExecutionContext.class))).thenReturn(FutureSpawn.immediate(spawnResult));
        List<SpawnResult> spawnResults = new AbstractSpawnStrategyTest.TestedSpawnStrategy(execRoot, spawnRunner).exec(AbstractSpawnStrategyTest.SIMPLE_SPAWN, actionExecutionContext);
        assertThat(spawnResults).containsExactly(spawnResult);
        // Must only be called exactly once.
        Mockito.verify(spawnRunner).execAsync(ArgumentMatchers.any(Spawn.class), ArgumentMatchers.any(SpawnExecutionContext.class));
    }

    @Test
    public void testNonZeroExit() throws Exception {
        Mockito.when(actionExecutionContext.getContext(ArgumentMatchers.eq(SpawnCache.class))).thenReturn(NO_CACHE);
        Mockito.when(actionExecutionContext.getExecRoot()).thenReturn(execRoot);
        SpawnResult result = new SpawnResult.Builder().setStatus(NON_ZERO_EXIT).setExitCode(1).setRunnerName("test").build();
        Mockito.when(spawnRunner.execAsync(ArgumentMatchers.any(Spawn.class), ArgumentMatchers.any(SpawnExecutionContext.class))).thenReturn(FutureSpawn.immediate(result));
        try {
            // Ignoring the List<SpawnResult> return value.
            new AbstractSpawnStrategyTest.TestedSpawnStrategy(execRoot, spawnRunner).exec(AbstractSpawnStrategyTest.SIMPLE_SPAWN, actionExecutionContext);
            Assert.fail("Expected SpawnExecException");
        } catch (SpawnExecException e) {
            assertThat(e.getSpawnResult()).isSameAs(result);
        }
        // Must only be called exactly once.
        Mockito.verify(spawnRunner).execAsync(ArgumentMatchers.any(Spawn.class), ArgumentMatchers.any(SpawnExecutionContext.class));
    }

    @Test
    public void testCacheHit() throws Exception {
        SpawnCache cache = Mockito.mock(SpawnCache.class);
        SpawnResult spawnResult = new SpawnResult.Builder().setStatus(SUCCESS).setRunnerName("test").build();
        Mockito.when(cache.lookup(ArgumentMatchers.any(Spawn.class), ArgumentMatchers.any(SpawnExecutionContext.class))).thenReturn(SpawnCache.success(spawnResult));
        Mockito.when(actionExecutionContext.getContext(ArgumentMatchers.eq(SpawnCache.class))).thenReturn(cache);
        Mockito.when(actionExecutionContext.getExecRoot()).thenReturn(execRoot);
        List<SpawnResult> spawnResults = new AbstractSpawnStrategyTest.TestedSpawnStrategy(execRoot, spawnRunner).exec(AbstractSpawnStrategyTest.SIMPLE_SPAWN, actionExecutionContext);
        assertThat(spawnResults).containsExactly(spawnResult);
        Mockito.verify(spawnRunner, Mockito.never()).execAsync(ArgumentMatchers.any(Spawn.class), ArgumentMatchers.any(SpawnExecutionContext.class));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testCacheMiss() throws Exception {
        SpawnCache cache = Mockito.mock(SpawnCache.class);
        CacheHandle entry = Mockito.mock(CacheHandle.class);
        Mockito.when(cache.lookup(ArgumentMatchers.any(Spawn.class), ArgumentMatchers.any(SpawnExecutionContext.class))).thenReturn(entry);
        Mockito.when(entry.hasResult()).thenReturn(false);
        Mockito.when(entry.willStore()).thenReturn(true);
        Mockito.when(actionExecutionContext.getContext(ArgumentMatchers.eq(SpawnCache.class))).thenReturn(cache);
        Mockito.when(actionExecutionContext.getExecRoot()).thenReturn(execRoot);
        SpawnResult spawnResult = new SpawnResult.Builder().setStatus(SUCCESS).setRunnerName("test").build();
        Mockito.when(spawnRunner.execAsync(ArgumentMatchers.any(Spawn.class), ArgumentMatchers.any(SpawnExecutionContext.class))).thenReturn(FutureSpawn.immediate(spawnResult));
        List<SpawnResult> spawnResults = new AbstractSpawnStrategyTest.TestedSpawnStrategy(execRoot, spawnRunner).exec(AbstractSpawnStrategyTest.SIMPLE_SPAWN, actionExecutionContext);
        assertThat(spawnResults).containsExactly(spawnResult);
        // Must only be called exactly once.
        Mockito.verify(spawnRunner).execAsync(ArgumentMatchers.any(Spawn.class), ArgumentMatchers.any(SpawnExecutionContext.class));
        Mockito.verify(entry).store(ArgumentMatchers.eq(spawnResult));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testCacheMissWithNonZeroExit() throws Exception {
        SpawnCache cache = Mockito.mock(SpawnCache.class);
        CacheHandle entry = Mockito.mock(CacheHandle.class);
        Mockito.when(cache.lookup(ArgumentMatchers.any(Spawn.class), ArgumentMatchers.any(SpawnExecutionContext.class))).thenReturn(entry);
        Mockito.when(entry.hasResult()).thenReturn(false);
        Mockito.when(entry.willStore()).thenReturn(true);
        Mockito.when(actionExecutionContext.getContext(ArgumentMatchers.eq(SpawnCache.class))).thenReturn(cache);
        Mockito.when(actionExecutionContext.getExecRoot()).thenReturn(execRoot);
        SpawnResult result = new SpawnResult.Builder().setStatus(NON_ZERO_EXIT).setExitCode(1).setRunnerName("test").build();
        Mockito.when(spawnRunner.execAsync(ArgumentMatchers.any(Spawn.class), ArgumentMatchers.any(SpawnExecutionContext.class))).thenReturn(FutureSpawn.immediate(result));
        try {
            // Ignoring the List<SpawnResult> return value.
            new AbstractSpawnStrategyTest.TestedSpawnStrategy(execRoot, spawnRunner).exec(AbstractSpawnStrategyTest.SIMPLE_SPAWN, actionExecutionContext);
            Assert.fail("Expected SpawnExecException");
        } catch (SpawnExecException e) {
            assertThat(e.getSpawnResult()).isSameAs(result);
        }
        // Must only be called exactly once.
        Mockito.verify(spawnRunner).execAsync(ArgumentMatchers.any(Spawn.class), ArgumentMatchers.any(SpawnExecutionContext.class));
        Mockito.verify(entry).store(ArgumentMatchers.eq(result));
    }

    @Test
    public void testLogSpawn() throws Exception {
        Mockito.when(actionExecutionContext.getContext(ArgumentMatchers.eq(SpawnCache.class))).thenReturn(NO_CACHE);
        Mockito.when(actionExecutionContext.getExecRoot()).thenReturn(execRoot);
        Mockito.when(actionExecutionContext.getContext(ArgumentMatchers.eq(SpawnLogContext.class))).thenReturn(new SpawnLogContext(execRoot, messageOutput));
        Mockito.when(spawnRunner.execAsync(ArgumentMatchers.any(Spawn.class), ArgumentMatchers.any(SpawnExecutionContext.class))).thenReturn(FutureSpawn.immediate(new SpawnResult.Builder().setStatus(NON_ZERO_EXIT).setExitCode(23).setRunnerName("runner").build()));
        Mockito.when(actionExecutionContext.getMetadataProvider()).thenReturn(Mockito.mock(MetadataProvider.class));
        Artifact input = new Artifact(scratch.file("/execroot/foo", "1"), rootDir);
        scratch.file("/execroot/out1", "123");
        scratch.file("/execroot/out2", "123");
        Spawn spawn = new SpawnBuilder("/bin/echo", "Foo!").withEnvironment("FOO", "v1").withEnvironment("BAR", "v2").withMnemonic("MyMnemonic").withProgressMessage("my progress message").withInput(input).withOutputs("out2", "out1").build();
        try {
            new AbstractSpawnStrategyTest.TestedSpawnStrategy(execRoot, spawnRunner).exec(spawn, actionExecutionContext);
            Assert.fail("expected failure");
        } catch (SpawnExecException expected) {
            // Should throw.
        }
        SpawnExec expectedSpawnLog = SpawnExec.newBuilder().addCommandArgs("/bin/echo").addCommandArgs("Foo!").addEnvironmentVariables(EnvironmentVariable.newBuilder().setName("BAR").setValue("v2").build()).addEnvironmentVariables(EnvironmentVariable.newBuilder().setName("FOO").setValue("v1").build()).addInputs(File.newBuilder().setPath("foo").setDigest(Digest.newBuilder().setHash("b026324c6904b2a9cb4b88d6d61c81d1").setSizeBytes(2).setHashFunctionName("MD5").build()).build()).addListedOutputs("out1").addListedOutputs("out2").addActualOutputs(File.newBuilder().setPath("out1").setDigest(Digest.newBuilder().setHash("ba1f2511fc30423bdbb183fe33f3dd0f").setSizeBytes(4).setHashFunctionName("MD5").build()).build()).addActualOutputs(File.newBuilder().setPath("out2").setDigest(Digest.newBuilder().setHash("ba1f2511fc30423bdbb183fe33f3dd0f").setSizeBytes(4).setHashFunctionName("MD5").build()).build()).setStatus("NON_ZERO_EXIT").setExitCode(23).setRemotable(true).setCacheable(true).setProgressMessage("my progress message").setMnemonic("MyMnemonic").setRunner("runner").build();
        Mockito.verify(messageOutput).write(expectedSpawnLog);
    }
}

