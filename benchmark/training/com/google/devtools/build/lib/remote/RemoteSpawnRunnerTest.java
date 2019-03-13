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
package com.google.devtools.build.lib.remote;


import ArtifactPathResolver.IDENTITY;
import Code.DEADLINE_EXCEEDED;
import EventKind.WARNING;
import ExecutionRequirements.NO_CACHE;
import ExitCode.REMOTE_ERROR;
import Platform.Property;
import ProgressStatus.EXECUTING;
import Status.EXECUTION_FAILED;
import Status.NON_ZERO_EXIT;
import Status.SUCCESS;
import Status.TIMEOUT;
import build.bazel.remote.execution.v2.Action;
import build.bazel.remote.execution.v2.ActionResult;
import build.bazel.remote.execution.v2.Command;
import build.bazel.remote.execution.v2.Digest;
import build.bazel.remote.execution.v2.ExecuteRequest;
import build.bazel.remote.execution.v2.ExecuteResponse;
import build.bazel.remote.execution.v2.LogFile;
import build.bazel.remote.execution.v2.Platform;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.eventbus.EventBus;
import com.google.common.io.ByteStreams;
import com.google.common.util.concurrent.ListeningScheduledExecutorService;
import com.google.common.util.concurrent.SettableFuture;
import com.google.devtools.build.lib.actions.ActionInput;
import com.google.devtools.build.lib.actions.Artifact.ArtifactExpander;
import com.google.devtools.build.lib.actions.CommandLines.ParamFileActionInput;
import com.google.devtools.build.lib.actions.EnvironmentalExecException;
import com.google.devtools.build.lib.actions.MetadataProvider;
import com.google.devtools.build.lib.actions.ParameterFile.ParameterFileType;
import com.google.devtools.build.lib.actions.ResourceSet;
import com.google.devtools.build.lib.actions.Spawn;
import com.google.devtools.build.lib.actions.SpawnResult;
import com.google.devtools.build.lib.actions.SpawnResult.com.google.rpc.Status;
import com.google.devtools.build.lib.actions.cache.MetadataInjector;
import com.google.devtools.build.lib.events.Event;
import com.google.devtools.build.lib.events.Reporter;
import com.google.devtools.build.lib.events.StoredEventHandler;
import com.google.devtools.build.lib.exec.ExecutionOptions;
import com.google.devtools.build.lib.exec.SpawnExecException;
import com.google.devtools.build.lib.exec.SpawnRunner;
import com.google.devtools.build.lib.exec.SpawnRunner.ProgressStatus;
import com.google.devtools.build.lib.exec.SpawnRunner.SpawnExecutionContext;
import com.google.devtools.build.lib.exec.util.FakeOwner;
import com.google.devtools.build.lib.remote.util.DigestUtil;
import com.google.devtools.build.lib.remote.util.DigestUtil.ActionKey;
import com.google.devtools.build.lib.testutil.MoreAsserts;
import com.google.devtools.build.lib.util.io.FileOutErr;
import com.google.devtools.build.lib.vfs.Path;
import com.google.devtools.build.lib.vfs.PathFragment;
import com.google.devtools.common.options.Options;
import com.google.rpc.Status;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Collection;
import java.util.SortedMap;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;


/**
 * Tests for {@link com.google.devtools.build.lib.remote.RemoteSpawnRunner}
 */
@RunWith(JUnit4.class)
public class RemoteSpawnRunnerTest {
    private static final ImmutableMap<String, String> NO_CACHE = ImmutableMap.of(ExecutionRequirements.NO_CACHE, "");

    private static ListeningScheduledExecutorService retryService;

    private Path execRoot;

    private Path logDir;

    private DigestUtil digestUtil;

    private FakeActionInputFileCache fakeFileCache;

    private FileOutErr outErr;

    private RemoteOptions options;

    private RemoteRetrier retrier;

    @Mock
    private GrpcRemoteCache cache;

    @Mock
    private GrpcRemoteExecutor executor;

    @Mock
    private SpawnRunner localRunner;

    // The action key of the Spawn returned by newSimpleSpawn().
    private final String simpleActionId = "eb45b20cc979d504f96b9efc9a08c48103c6f017afa09c0df5c70a5f92a98ea8";

    @Test
    @SuppressWarnings("unchecked")
    public void nonCachableSpawnsShouldNotBeCached_remote() throws Exception {
        // Test that if a spawn is marked "NO_CACHE" then it's not fetched from a remote cache.
        // It should be executed remotely, but marked non-cacheable to remote execution, so that
        // the action result is not saved in the remote cache.
        options.remoteAcceptCached = true;
        options.remoteLocalFallback = false;
        options.remoteUploadLocalResults = true;
        options.remoteResultCachePriority = 1;
        options.remoteExecutionPriority = 2;
        RemoteSpawnRunner runner = /* cmdlineReporter= */
        new RemoteSpawnRunner(execRoot, options, Options.getDefaults(ExecutionOptions.class), new java.util.concurrent.atomic.AtomicReference(localRunner), true, null, "build-req-id", "command-id", cache, executor, retrier, digestUtil, logDir);
        ExecuteResponse succeeded = ExecuteResponse.newBuilder().setResult(ActionResult.newBuilder().setExitCode(0).build()).build();
        Mockito.when(executor.executeRemotely(ArgumentMatchers.any(ExecuteRequest.class))).thenReturn(succeeded);
        Spawn spawn = /* arguments= */
        /* environment= */
        /* inputs= */
        /* outputs= */
        new com.google.devtools.build.lib.actions.SimpleSpawn(new FakeOwner("foo", "bar"), ImmutableList.of(), ImmutableMap.of(), RemoteSpawnRunnerTest.NO_CACHE, ImmutableList.of(), ImmutableList.<ActionInput>of(), ResourceSet.ZERO);
        SpawnExecutionContext policy = new RemoteSpawnRunnerTest.FakeSpawnExecutionContext(spawn);
        runner.exec(spawn, policy);
        ArgumentCaptor<ExecuteRequest> requestCaptor = ArgumentCaptor.forClass(ExecuteRequest.class);
        Mockito.verify(executor).executeRemotely(requestCaptor.capture());
        assertThat(requestCaptor.getValue().getSkipCacheLookup()).isTrue();
        assertThat(requestCaptor.getValue().getResultsCachePolicy().getPriority()).isEqualTo(1);
        assertThat(requestCaptor.getValue().getExecutionPolicy().getPriority()).isEqualTo(2);
        // TODO(olaola): verify that the uploaded action has the doNotCache set.
        Mockito.verify(cache, Mockito.never()).getCachedActionResult(ArgumentMatchers.any(ActionKey.class));
        Mockito.verify(cache, Mockito.never()).upload(ArgumentMatchers.any(ActionKey.class), ArgumentMatchers.any(Action.class), ArgumentMatchers.any(Command.class), ArgumentMatchers.any(Path.class), ArgumentMatchers.any(Collection.class), ArgumentMatchers.any(FileOutErr.class));
        Mockito.verifyZeroInteractions(localRunner);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void nonCachableSpawnsShouldNotBeCached_local() throws Exception {
        // Test that if a spawn is executed locally, due to the local fallback, that its result is not
        // uploaded to the remote cache.
        options.remoteAcceptCached = true;
        options.remoteLocalFallback = true;
        options.remoteUploadLocalResults = true;
        RemoteSpawnRunner runner = /* cmdlineReporter= */
        new RemoteSpawnRunner(execRoot, options, Options.getDefaults(ExecutionOptions.class), new java.util.concurrent.atomic.AtomicReference(localRunner), true, null, "build-req-id", "command-id", cache, null, retrier, digestUtil, logDir);
        // Throw an IOException to trigger the local fallback.
        Mockito.when(executor.executeRemotely(ArgumentMatchers.any(ExecuteRequest.class))).thenThrow(IOException.class);
        Spawn spawn = /* arguments= */
        /* environment= */
        /* inputs= */
        /* outputs= */
        new com.google.devtools.build.lib.actions.SimpleSpawn(new FakeOwner("foo", "bar"), ImmutableList.of(), ImmutableMap.of(), RemoteSpawnRunnerTest.NO_CACHE, ImmutableList.of(), ImmutableList.<ActionInput>of(), ResourceSet.ZERO);
        SpawnExecutionContext policy = new RemoteSpawnRunnerTest.FakeSpawnExecutionContext(spawn);
        runner.exec(spawn, policy);
        Mockito.verify(localRunner).exec(spawn, policy);
        Mockito.verify(cache, Mockito.never()).getCachedActionResult(ArgumentMatchers.any(ActionKey.class));
        Mockito.verifyNoMoreInteractions(cache);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void failedLocalActionShouldNotBeUploaded() throws Exception {
        // Test that the outputs of a locally executed action that failed are not uploaded.
        options.remoteUploadLocalResults = true;
        RemoteSpawnRunner runner = Mockito.spy(/* cmdlineReporter= */
        new RemoteSpawnRunner(execRoot, options, Options.getDefaults(ExecutionOptions.class), new java.util.concurrent.atomic.AtomicReference(localRunner), true, null, "build-req-id", "command-id", cache, null, retrier, digestUtil, logDir));
        Spawn spawn = RemoteSpawnRunnerTest.newSimpleSpawn();
        SpawnExecutionContext policy = new RemoteSpawnRunnerTest.FakeSpawnExecutionContext(spawn);
        SpawnResult res = Mockito.mock(SpawnResult.class);
        Mockito.when(res.exitCode()).thenReturn(1);
        Mockito.when(res.status()).thenReturn(EXECUTION_FAILED);
        Mockito.when(localRunner.exec(ArgumentMatchers.eq(spawn), ArgumentMatchers.eq(policy))).thenReturn(res);
        assertThat(runner.exec(spawn, policy)).isSameAs(res);
        Mockito.verify(localRunner).exec(ArgumentMatchers.eq(spawn), ArgumentMatchers.eq(policy));
        /* uploadLocalResults= */
        Mockito.verify(runner).execLocallyAndUpload(ArgumentMatchers.eq(spawn), ArgumentMatchers.eq(policy), ArgumentMatchers.any(SortedMap.class), ArgumentMatchers.eq(cache), ArgumentMatchers.any(ActionKey.class), ArgumentMatchers.any(Action.class), ArgumentMatchers.any(Command.class), ArgumentMatchers.eq(true));
        Mockito.verify(cache, Mockito.never()).upload(ArgumentMatchers.any(ActionKey.class), ArgumentMatchers.any(Action.class), ArgumentMatchers.any(Command.class), ArgumentMatchers.any(Path.class), ArgumentMatchers.any(Collection.class), ArgumentMatchers.any(FileOutErr.class));
    }

    @Test
    public void dontAcceptFailedCachedAction() throws Exception {
        // Test that bazel fails if the remote cache serves a failed action.
        RemoteOptions options = Options.getDefaults(RemoteOptions.class);
        ActionResult failedAction = ActionResult.newBuilder().setExitCode(1).build();
        Mockito.when(cache.getCachedActionResult(ArgumentMatchers.any(ActionKey.class))).thenReturn(failedAction);
        Spawn spawn = RemoteSpawnRunnerTest.newSimpleSpawn();
        SpawnExecutionContext policy = new RemoteSpawnRunnerTest.FakeSpawnExecutionContext(spawn);
        RemoteSpawnRunner runner = Mockito.spy(/* cmdlineReporter= */
        new RemoteSpawnRunner(execRoot, options, Options.getDefaults(ExecutionOptions.class), new java.util.concurrent.atomic.AtomicReference(localRunner), true, null, "build-req-id", "command-id", cache, null, retrier, digestUtil, logDir));
        MoreAsserts.assertThrows(EnvironmentalExecException.class, () -> runner.exec(spawn, policy));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void printWarningIfCacheIsDown() throws Exception {
        // If we try to upload to a local cache, that is down a warning should be printed.
        options.remoteUploadLocalResults = true;
        options.remoteLocalFallback = true;
        Reporter reporter = new Reporter(new EventBus());
        StoredEventHandler eventHandler = new StoredEventHandler();
        reporter.addHandler(eventHandler);
        RemoteSpawnRunner runner = new RemoteSpawnRunner(execRoot, options, Options.getDefaults(ExecutionOptions.class), new java.util.concurrent.atomic.AtomicReference(localRunner), false, reporter, "build-req-id", "command-id", cache, null, retrier, digestUtil, logDir);
        Spawn spawn = RemoteSpawnRunnerTest.newSimpleSpawn();
        SpawnExecutionContext policy = new RemoteSpawnRunnerTest.FakeSpawnExecutionContext(spawn);
        Mockito.when(cache.getCachedActionResult(ArgumentMatchers.any(ActionKey.class))).thenThrow(new IOException("cache down"));
        Mockito.doThrow(new IOException("cache down")).when(cache).upload(ArgumentMatchers.any(ActionKey.class), ArgumentMatchers.any(Action.class), ArgumentMatchers.any(Command.class), ArgumentMatchers.any(Path.class), ArgumentMatchers.any(Collection.class), ArgumentMatchers.any(FileOutErr.class));
        SpawnResult res = new SpawnResult.Builder().setStatus(SUCCESS).setExitCode(0).setRunnerName("test").build();
        Mockito.when(localRunner.exec(ArgumentMatchers.eq(spawn), ArgumentMatchers.eq(policy))).thenReturn(res);
        assertThat(runner.exec(spawn, policy)).isEqualTo(res);
        Mockito.verify(localRunner).exec(ArgumentMatchers.eq(spawn), ArgumentMatchers.eq(policy));
        assertThat(eventHandler.getEvents()).hasSize(1);
        Event evt = eventHandler.getEvents().get(0);
        assertThat(evt.getKind()).isEqualTo(WARNING);
        assertThat(evt.getMessage()).contains("fail");
        assertThat(evt.getMessage()).contains("upload");
    }

    @Test
    public void noRemoteExecutorFallbackFails() throws Exception {
        // Errors from the fallback runner should be propogated out of the remote runner.
        options.remoteUploadLocalResults = true;
        options.remoteLocalFallback = true;
        RemoteSpawnRunner runner = /* cmdlineReporter= */
        new RemoteSpawnRunner(execRoot, options, Options.getDefaults(ExecutionOptions.class), new java.util.concurrent.atomic.AtomicReference(localRunner), true, null, "build-req-id", "command-id", cache, null, retrier, digestUtil, logDir);
        Spawn spawn = RemoteSpawnRunnerTest.newSimpleSpawn();
        SpawnExecutionContext policy = new RemoteSpawnRunnerTest.FakeSpawnExecutionContext(spawn);
        Mockito.when(cache.getCachedActionResult(ArgumentMatchers.any(ActionKey.class))).thenReturn(null);
        IOException err = new IOException("local execution error");
        Mockito.when(localRunner.exec(ArgumentMatchers.eq(spawn), ArgumentMatchers.eq(policy))).thenThrow(err);
        try {
            runner.exec(spawn, policy);
            Assert.fail("expected IOException to be raised");
        } catch (IOException e) {
            assertThat(e).isSameAs(err);
        }
        Mockito.verify(localRunner).exec(ArgumentMatchers.eq(spawn), ArgumentMatchers.eq(policy));
    }

    @Test
    public void remoteCacheErrorFallbackFails() throws Exception {
        // Errors from the fallback runner should be propogated out of the remote runner.
        options.remoteUploadLocalResults = true;
        options.remoteLocalFallback = true;
        RemoteSpawnRunner runner = /* cmdlineReporter= */
        new RemoteSpawnRunner(execRoot, options, Options.getDefaults(ExecutionOptions.class), new java.util.concurrent.atomic.AtomicReference(localRunner), true, null, "build-req-id", "command-id", cache, null, retrier, digestUtil, logDir);
        Spawn spawn = RemoteSpawnRunnerTest.newSimpleSpawn();
        SpawnExecutionContext policy = new RemoteSpawnRunnerTest.FakeSpawnExecutionContext(spawn);
        Mockito.when(cache.getCachedActionResult(ArgumentMatchers.any(ActionKey.class))).thenThrow(new IOException());
        IOException err = new IOException("local execution error");
        Mockito.when(localRunner.exec(ArgumentMatchers.eq(spawn), ArgumentMatchers.eq(policy))).thenThrow(err);
        try {
            runner.exec(spawn, policy);
            Assert.fail("expected IOException to be raised");
        } catch (IOException e) {
            assertThat(e).isSameAs(err);
        }
        Mockito.verify(localRunner).exec(ArgumentMatchers.eq(spawn), ArgumentMatchers.eq(policy));
    }

    @Test
    public void testLocalFallbackFailureRemoteExecutorFailure() throws Exception {
        options.remoteLocalFallback = true;
        RemoteSpawnRunner runner = /* cmdlineReporter= */
        new RemoteSpawnRunner(execRoot, options, Options.getDefaults(ExecutionOptions.class), new java.util.concurrent.atomic.AtomicReference(localRunner), true, null, "build-req-id", "command-id", cache, executor, retrier, digestUtil, logDir);
        Mockito.when(cache.getCachedActionResult(ArgumentMatchers.any(ActionKey.class))).thenReturn(null);
        Mockito.when(executor.executeRemotely(ArgumentMatchers.any(ExecuteRequest.class))).thenThrow(new IOException());
        Spawn spawn = RemoteSpawnRunnerTest.newSimpleSpawn();
        SpawnExecutionContext policy = new RemoteSpawnRunnerTest.FakeSpawnExecutionContext(spawn);
        IOException err = new IOException("local execution error");
        Mockito.when(localRunner.exec(ArgumentMatchers.eq(spawn), ArgumentMatchers.eq(policy))).thenThrow(err);
        try {
            runner.exec(spawn, policy);
            Assert.fail("expected IOException to be raised");
        } catch (IOException e) {
            assertThat(e).isSameAs(err);
        }
        Mockito.verify(localRunner).exec(ArgumentMatchers.eq(spawn), ArgumentMatchers.eq(policy));
    }

    @Test
    public void testHumanReadableServerLogsSavedForFailingAction() throws Exception {
        RemoteSpawnRunner runner = /* cmdlineReporter= */
        new RemoteSpawnRunner(execRoot, Options.getDefaults(RemoteOptions.class), Options.getDefaults(ExecutionOptions.class), new java.util.concurrent.atomic.AtomicReference(localRunner), true, null, "build-req-id", "command-id", cache, executor, retrier, digestUtil, logDir);
        Digest logDigest = digestUtil.computeAsUtf8("bla");
        Path logPath = logDir.getRelative(simpleActionId).getRelative("logname");
        Mockito.when(executor.executeRemotely(ArgumentMatchers.any(ExecuteRequest.class))).thenReturn(ExecuteResponse.newBuilder().putServerLogs("logname", LogFile.newBuilder().setHumanReadable(true).setDigest(logDigest).build()).setResult(ActionResult.newBuilder().setExitCode(31).build()).build());
        SettableFuture<Void> completed = SettableFuture.create();
        completed.set(null);
        Mockito.when(cache.downloadFile(ArgumentMatchers.eq(logPath), ArgumentMatchers.eq(logDigest))).thenReturn(completed);
        Spawn spawn = RemoteSpawnRunnerTest.newSimpleSpawn();
        SpawnExecutionContext policy = new RemoteSpawnRunnerTest.FakeSpawnExecutionContext(spawn);
        SpawnResult res = runner.exec(spawn, policy);
        assertThat(res.status()).isEqualTo(NON_ZERO_EXIT);
        Mockito.verify(executor).executeRemotely(ArgumentMatchers.any(ExecuteRequest.class));
        Mockito.verify(cache).downloadFile(ArgumentMatchers.eq(logPath), ArgumentMatchers.eq(logDigest));
    }

    @Test
    public void testHumanReadableServerLogsSavedForFailingActionWithStatus() throws Exception {
        RemoteSpawnRunner runner = /* cmdlineReporter= */
        new RemoteSpawnRunner(execRoot, Options.getDefaults(RemoteOptions.class), Options.getDefaults(ExecutionOptions.class), new java.util.concurrent.atomic.AtomicReference(localRunner), true, null, "build-req-id", "command-id", cache, executor, retrier, digestUtil, logDir);
        Digest logDigest = digestUtil.computeAsUtf8("bla");
        Path logPath = logDir.getRelative(simpleActionId).getRelative("logname");
        Status timeoutStatus = com.google.rpc.Status.newBuilder().setCode(DEADLINE_EXCEEDED.getNumber()).build();
        ExecuteResponse resp = ExecuteResponse.newBuilder().putServerLogs("logname", LogFile.newBuilder().setHumanReadable(true).setDigest(logDigest).build()).setStatus(timeoutStatus).build();
        Mockito.when(executor.executeRemotely(ArgumentMatchers.any(ExecuteRequest.class))).thenThrow(new IOException(new ExecutionStatusException(resp.getStatus(), resp)));
        SettableFuture<Void> completed = SettableFuture.create();
        completed.set(null);
        Mockito.when(cache.downloadFile(ArgumentMatchers.eq(logPath), ArgumentMatchers.eq(logDigest))).thenReturn(completed);
        Spawn spawn = RemoteSpawnRunnerTest.newSimpleSpawn();
        SpawnExecutionContext policy = new RemoteSpawnRunnerTest.FakeSpawnExecutionContext(spawn);
        SpawnResult res = runner.exec(spawn, policy);
        assertThat(res.status()).isEqualTo(TIMEOUT);
        Mockito.verify(executor).executeRemotely(ArgumentMatchers.any(ExecuteRequest.class));
        Mockito.verify(cache).downloadFile(ArgumentMatchers.eq(logPath), ArgumentMatchers.eq(logDigest));
    }

    @Test
    public void testNonHumanReadableServerLogsNotSaved() throws Exception {
        RemoteSpawnRunner runner = /* cmdlineReporter= */
        new RemoteSpawnRunner(execRoot, Options.getDefaults(RemoteOptions.class), Options.getDefaults(ExecutionOptions.class), new java.util.concurrent.atomic.AtomicReference(localRunner), true, null, "build-req-id", "command-id", cache, executor, retrier, digestUtil, logDir);
        Digest logDigest = digestUtil.computeAsUtf8("bla");
        ActionResult result = ActionResult.newBuilder().setExitCode(31).build();
        Mockito.when(executor.executeRemotely(ArgumentMatchers.any(ExecuteRequest.class))).thenReturn(ExecuteResponse.newBuilder().putServerLogs("logname", LogFile.newBuilder().setDigest(logDigest).build()).setResult(result).build());
        Spawn spawn = RemoteSpawnRunnerTest.newSimpleSpawn();
        SpawnExecutionContext policy = new RemoteSpawnRunnerTest.FakeSpawnExecutionContext(spawn);
        SpawnResult res = runner.exec(spawn, policy);
        assertThat(res.status()).isEqualTo(NON_ZERO_EXIT);
        Mockito.verify(executor).executeRemotely(ArgumentMatchers.any(ExecuteRequest.class));
        Mockito.verify(cache).download(ArgumentMatchers.eq(result), ArgumentMatchers.eq(execRoot), ArgumentMatchers.any(FileOutErr.class));
        Mockito.verify(cache, Mockito.never()).downloadFile(ArgumentMatchers.any(Path.class), ArgumentMatchers.any(Digest.class));
    }

    @Test
    public void testServerLogsNotSavedForSuccessfulAction() throws Exception {
        RemoteSpawnRunner runner = /* cmdlineReporter= */
        new RemoteSpawnRunner(execRoot, Options.getDefaults(RemoteOptions.class), Options.getDefaults(ExecutionOptions.class), new java.util.concurrent.atomic.AtomicReference(localRunner), true, null, "build-req-id", "command-id", cache, executor, retrier, digestUtil, logDir);
        Digest logDigest = digestUtil.computeAsUtf8("bla");
        ActionResult result = ActionResult.newBuilder().setExitCode(0).build();
        Mockito.when(executor.executeRemotely(ArgumentMatchers.any(ExecuteRequest.class))).thenReturn(ExecuteResponse.newBuilder().putServerLogs("logname", LogFile.newBuilder().setHumanReadable(true).setDigest(logDigest).build()).setResult(result).build());
        Spawn spawn = RemoteSpawnRunnerTest.newSimpleSpawn();
        SpawnExecutionContext policy = new RemoteSpawnRunnerTest.FakeSpawnExecutionContext(spawn);
        SpawnResult res = runner.exec(spawn, policy);
        assertThat(res.status()).isEqualTo(SUCCESS);
        Mockito.verify(executor).executeRemotely(ArgumentMatchers.any(ExecuteRequest.class));
        Mockito.verify(cache).download(ArgumentMatchers.eq(result), ArgumentMatchers.eq(execRoot), ArgumentMatchers.any(FileOutErr.class));
        Mockito.verify(cache, Mockito.never()).downloadFile(ArgumentMatchers.any(Path.class), ArgumentMatchers.any(Digest.class));
    }

    @Test
    public void cacheDownloadFailureTriggersRemoteExecution() throws Exception {
        // If downloading a cached action fails, remote execution should be tried.
        RemoteSpawnRunner runner = /* cmdlineReporter= */
        new RemoteSpawnRunner(execRoot, options, Options.getDefaults(ExecutionOptions.class), new java.util.concurrent.atomic.AtomicReference(localRunner), true, null, "build-req-id", "command-id", cache, executor, retrier, digestUtil, logDir);
        ActionResult cachedResult = ActionResult.newBuilder().setExitCode(0).build();
        Mockito.when(cache.getCachedActionResult(ArgumentMatchers.any(ActionKey.class))).thenReturn(cachedResult);
        Exception downloadFailure = new CacheNotFoundException(Digest.getDefaultInstance(), digestUtil);
        Mockito.doThrow(downloadFailure).when(cache).download(ArgumentMatchers.eq(cachedResult), ArgumentMatchers.any(Path.class), ArgumentMatchers.any(FileOutErr.class));
        ActionResult execResult = ActionResult.newBuilder().setExitCode(31).build();
        ExecuteResponse succeeded = ExecuteResponse.newBuilder().setResult(execResult).build();
        Mockito.when(executor.executeRemotely(ArgumentMatchers.any(ExecuteRequest.class))).thenReturn(succeeded);
        Mockito.doNothing().when(cache).download(ArgumentMatchers.eq(execResult), ArgumentMatchers.any(Path.class), ArgumentMatchers.any(FileOutErr.class));
        Spawn spawn = RemoteSpawnRunnerTest.newSimpleSpawn();
        SpawnExecutionContext policy = new RemoteSpawnRunnerTest.FakeSpawnExecutionContext(spawn);
        SpawnResult res = runner.exec(spawn, policy);
        assertThat(res.status()).isEqualTo(NON_ZERO_EXIT);
        assertThat(res.exitCode()).isEqualTo(31);
        Mockito.verify(executor).executeRemotely(ArgumentMatchers.any(ExecuteRequest.class));
    }

    @Test
    public void testRemoteExecutionTimeout() throws Exception {
        // If remote execution times out the SpawnResult status should be TIMEOUT.
        options.remoteLocalFallback = false;
        RemoteSpawnRunner runner = /* cmdlineReporter= */
        new RemoteSpawnRunner(execRoot, options, Options.getDefaults(ExecutionOptions.class), new java.util.concurrent.atomic.AtomicReference(localRunner), true, null, "build-req-id", "command-id", cache, executor, retrier, digestUtil, logDir);
        ActionResult cachedResult = ActionResult.newBuilder().setExitCode(0).build();
        Mockito.when(cache.getCachedActionResult(ArgumentMatchers.any(ActionKey.class))).thenReturn(null);
        ExecuteResponse resp = ExecuteResponse.newBuilder().setResult(cachedResult).setStatus(com.google.rpc.Status.newBuilder().setCode(DEADLINE_EXCEEDED.getNumber()).build()).build();
        Mockito.when(executor.executeRemotely(ArgumentMatchers.any(ExecuteRequest.class))).thenThrow(new IOException(new ExecutionStatusException(resp.getStatus(), resp)));
        Spawn spawn = RemoteSpawnRunnerTest.newSimpleSpawn();
        SpawnExecutionContext policy = new RemoteSpawnRunnerTest.FakeSpawnExecutionContext(spawn);
        SpawnResult res = runner.exec(spawn, policy);
        assertThat(res.status()).isEqualTo(TIMEOUT);
        Mockito.verify(executor).executeRemotely(ArgumentMatchers.any(ExecuteRequest.class));
        Mockito.verify(cache).download(ArgumentMatchers.eq(cachedResult), ArgumentMatchers.eq(execRoot), ArgumentMatchers.any(FileOutErr.class));
    }

    @Test
    public void testRemoteExecutionTimeoutDoesNotTriggerFallback() throws Exception {
        // If remote execution times out the SpawnResult status should be TIMEOUT, regardess of local
        // fallback option.
        options.remoteLocalFallback = true;
        RemoteSpawnRunner runner = /* cmdlineReporter= */
        new RemoteSpawnRunner(execRoot, options, Options.getDefaults(ExecutionOptions.class), new java.util.concurrent.atomic.AtomicReference(localRunner), true, null, "build-req-id", "command-id", cache, executor, retrier, digestUtil, logDir);
        ActionResult cachedResult = ActionResult.newBuilder().setExitCode(0).build();
        Mockito.when(cache.getCachedActionResult(ArgumentMatchers.any(ActionKey.class))).thenReturn(null);
        ExecuteResponse resp = ExecuteResponse.newBuilder().setResult(cachedResult).setStatus(com.google.rpc.Status.newBuilder().setCode(DEADLINE_EXCEEDED.getNumber()).build()).build();
        Mockito.when(executor.executeRemotely(ArgumentMatchers.any(ExecuteRequest.class))).thenThrow(new IOException(new ExecutionStatusException(resp.getStatus(), resp)));
        Spawn spawn = RemoteSpawnRunnerTest.newSimpleSpawn();
        SpawnExecutionContext policy = new RemoteSpawnRunnerTest.FakeSpawnExecutionContext(spawn);
        SpawnResult res = runner.exec(spawn, policy);
        assertThat(res.status()).isEqualTo(TIMEOUT);
        Mockito.verify(executor).executeRemotely(ArgumentMatchers.any(ExecuteRequest.class));
        Mockito.verify(cache).download(ArgumentMatchers.eq(cachedResult), ArgumentMatchers.eq(execRoot), ArgumentMatchers.any(FileOutErr.class));
        Mockito.verify(localRunner, Mockito.never()).exec(ArgumentMatchers.eq(spawn), ArgumentMatchers.eq(policy));
    }

    @Test
    public void testRemoteExecutionCommandFailureDoesNotTriggerFallback() throws Exception {
        options.remoteLocalFallback = true;
        RemoteSpawnRunner runner = /* cmdlineReporter= */
        new RemoteSpawnRunner(execRoot, options, Options.getDefaults(ExecutionOptions.class), new java.util.concurrent.atomic.AtomicReference(localRunner), true, null, "build-req-id", "command-id", cache, executor, retrier, digestUtil, logDir);
        ActionResult cachedResult = ActionResult.newBuilder().setExitCode(0).build();
        Mockito.when(cache.getCachedActionResult(ArgumentMatchers.any(ActionKey.class))).thenReturn(null);
        ExecuteResponse failed = ExecuteResponse.newBuilder().setResult(ActionResult.newBuilder().setExitCode(33).build()).build();
        Mockito.when(executor.executeRemotely(ArgumentMatchers.any(ExecuteRequest.class))).thenReturn(failed);
        Spawn spawn = RemoteSpawnRunnerTest.newSimpleSpawn();
        SpawnExecutionContext policy = new RemoteSpawnRunnerTest.FakeSpawnExecutionContext(spawn);
        SpawnResult res = runner.exec(spawn, policy);
        assertThat(res.status()).isEqualTo(NON_ZERO_EXIT);
        assertThat(res.exitCode()).isEqualTo(33);
        Mockito.verify(executor).executeRemotely(ArgumentMatchers.any(ExecuteRequest.class));
        Mockito.verify(cache, Mockito.never()).download(ArgumentMatchers.eq(cachedResult), ArgumentMatchers.eq(execRoot), ArgumentMatchers.any(FileOutErr.class));
        Mockito.verify(localRunner, Mockito.never()).exec(ArgumentMatchers.eq(spawn), ArgumentMatchers.eq(policy));
    }

    @Test
    public void testExitCode_executorfailure() throws Exception {
        // If we get a failure due to the remote cache not working, the exit code should be
        // ExitCode.REMOTE_ERROR.
        options.remoteLocalFallback = false;
        RemoteSpawnRunner runner = /* cmdlineReporter= */
        new RemoteSpawnRunner(execRoot, options, Options.getDefaults(ExecutionOptions.class), new java.util.concurrent.atomic.AtomicReference(localRunner), true, null, "build-req-id", "command-id", cache, executor, retrier, digestUtil, logDir);
        Mockito.when(cache.getCachedActionResult(ArgumentMatchers.any(ActionKey.class))).thenReturn(null);
        Mockito.when(executor.executeRemotely(ArgumentMatchers.any(ExecuteRequest.class))).thenThrow(new IOException("reasons"));
        Spawn spawn = RemoteSpawnRunnerTest.newSimpleSpawn();
        SpawnExecutionContext policy = new RemoteSpawnRunnerTest.FakeSpawnExecutionContext(spawn);
        try {
            runner.exec(spawn, policy);
            Assert.fail("Exception expected");
        } catch (SpawnExecException e) {
            assertThat(e.getSpawnResult().exitCode()).isEqualTo(REMOTE_ERROR.getNumericExitCode());
            assertThat(e.getSpawnResult().getDetailMessage("", "", false, false)).contains("reasons");
        }
    }

    @Test
    public void testExitCode_executionfailure() throws Exception {
        // If we get a failure due to the remote executor not working, the exit code should be
        // ExitCode.REMOTE_ERROR.
        options.remoteLocalFallback = false;
        RemoteSpawnRunner runner = /* cmdlineReporter= */
        new RemoteSpawnRunner(execRoot, options, Options.getDefaults(ExecutionOptions.class), new java.util.concurrent.atomic.AtomicReference(localRunner), true, null, "build-req-id", "command-id", cache, executor, retrier, digestUtil, logDir);
        Mockito.when(cache.getCachedActionResult(ArgumentMatchers.any(ActionKey.class))).thenThrow(new IOException("reasons"));
        Spawn spawn = RemoteSpawnRunnerTest.newSimpleSpawn();
        SpawnExecutionContext policy = new RemoteSpawnRunnerTest.FakeSpawnExecutionContext(spawn);
        try {
            runner.exec(spawn, policy);
            Assert.fail("Exception expected");
        } catch (SpawnExecException e) {
            assertThat(e.getSpawnResult().exitCode()).isEqualTo(REMOTE_ERROR.getNumericExitCode());
            assertThat(e.getSpawnResult().getDetailMessage("", "", false, false)).contains("reasons");
        }
    }

    @Test
    public void testMaterializeParamFiles() throws Exception {
        ExecutionOptions executionOptions = Options.parse(ExecutionOptions.class, "--materialize_param_files").getOptions();
        executionOptions.materializeParamFiles = true;
        RemoteSpawnRunner runner = /* cmdlineReporter= */
        new RemoteSpawnRunner(execRoot, Options.getDefaults(RemoteOptions.class), executionOptions, new java.util.concurrent.atomic.AtomicReference(localRunner), true, null, "build-req-id", "command-id", cache, executor, retrier, digestUtil, logDir);
        ExecuteResponse succeeded = ExecuteResponse.newBuilder().setResult(ActionResult.newBuilder().setExitCode(0).build()).build();
        Mockito.when(executor.executeRemotely(ArgumentMatchers.any(ExecuteRequest.class))).thenReturn(succeeded);
        ImmutableList<String> args = ImmutableList.of("--foo", "--bar");
        ParamFileActionInput input = new ParamFileActionInput(PathFragment.create("out/param_file"), args, ParameterFileType.UNQUOTED, StandardCharsets.ISO_8859_1);
        Spawn spawn = /* arguments= */
        /* environment= */
        /* executionInfo= */
        /* outputs= */
        new com.google.devtools.build.lib.actions.SimpleSpawn(new FakeOwner("foo", "bar"), ImmutableList.of(), ImmutableMap.of(), ImmutableMap.of(), ImmutableList.of(input), ImmutableList.<ActionInput>of(), ResourceSet.ZERO);
        SpawnExecutionContext policy = new RemoteSpawnRunnerTest.FakeSpawnExecutionContext(spawn);
        SpawnResult res = runner.exec(spawn, policy);
        assertThat(res.status()).isEqualTo(SUCCESS);
        Path paramFile = execRoot.getRelative("out/param_file");
        assertThat(paramFile.exists()).isTrue();
        try (InputStream inputStream = paramFile.getInputStream()) {
            assertThat(new String(ByteStreams.toByteArray(inputStream), StandardCharsets.UTF_8).split("\n")).asList().containsExactly("--foo", "--bar");
        }
    }

    @Test
    public void testParsePlatformSortsProperties() throws Exception {
        String s = String.join("\n", "properties: {", " name: \"b\"", " value: \"2\"", "}", "properties: {", " name: \"a\"", " value: \"1\"", "}");
        Platform expected = Platform.newBuilder().addProperties(Property.newBuilder().setName("a").setValue("1")).addProperties(Property.newBuilder().setName("b").setValue("2")).build();
        assertThat(RemoteSpawnRunner.parsePlatform(null, s)).isEqualTo(expected);
    }

    // TODO(buchgr): Extract a common class to be used for testing.
    class FakeSpawnExecutionContext implements SpawnExecutionContext {
        private final ArtifactExpander artifactExpander = ( artifact, output) -> output.add(artifact);

        private final Spawn spawn;

        FakeSpawnExecutionContext(Spawn spawn) {
            this.spawn = spawn;
        }

        @Override
        public int getId() {
            return 0;
        }

        @Override
        public void prefetchInputs() throws IOException {
            throw new UnsupportedOperationException();
        }

        @Override
        public void lockOutputFiles() throws InterruptedException {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean speculating() {
            return false;
        }

        @Override
        public MetadataProvider getMetadataProvider() {
            return fakeFileCache;
        }

        @Override
        public ArtifactExpander getArtifactExpander() {
            throw new UnsupportedOperationException();
        }

        @Override
        public Duration getTimeout() {
            return Duration.ZERO;
        }

        @Override
        public FileOutErr getFileOutErr() {
            return outErr;
        }

        @Override
        public SortedMap<PathFragment, ActionInput> getInputMapping(boolean expandTreeArtifactsInRunfiles) throws IOException {
            return /* strict */
            new com.google.devtools.build.lib.exec.SpawnInputExpander(execRoot, false).getInputMapping(spawn, artifactExpander, IDENTITY, fakeFileCache, true);
        }

        @Override
        public void report(ProgressStatus state, String name) {
            assertThat(state).isEqualTo(EXECUTING);
        }

        @Override
        public MetadataInjector getMetadataInjector() {
            throw new UnsupportedOperationException();
        }
    }
}

