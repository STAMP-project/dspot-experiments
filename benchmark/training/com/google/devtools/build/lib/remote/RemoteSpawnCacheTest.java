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
import EventKind.WARNING;
import ExecutionRequirements.LOCAL;
import ExecutionRequirements.NO_CACHE;
import ProgressStatus.CHECKING_CACHE;
import Status.NON_ZERO_EXIT;
import Status.SUCCESS;
import build.bazel.remote.execution.v2.Action;
import build.bazel.remote.execution.v2.ActionResult;
import build.bazel.remote.execution.v2.Command;
import build.bazel.remote.execution.v2.Digest;
import build.bazel.remote.execution.v2.OutputFile;
import build.bazel.remote.execution.v2.RequestMetadata;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.devtools.build.lib.actions.ActionInput;
import com.google.devtools.build.lib.actions.ActionInputHelper;
import com.google.devtools.build.lib.actions.Artifact;
import com.google.devtools.build.lib.actions.Artifact.ArtifactExpander;
import com.google.devtools.build.lib.actions.MetadataProvider;
import com.google.devtools.build.lib.actions.ResourceSet;
import com.google.devtools.build.lib.actions.SimpleSpawn;
import com.google.devtools.build.lib.actions.SpawnResult;
import com.google.devtools.build.lib.events.Event;
import com.google.devtools.build.lib.events.StoredEventHandler;
import com.google.devtools.build.lib.exec.SpawnCache.CacheHandle;
import com.google.devtools.build.lib.exec.SpawnRunner.ProgressStatus;
import com.google.devtools.build.lib.exec.SpawnRunner.SpawnExecutionContext;
import com.google.devtools.build.lib.exec.util.FakeOwner;
import com.google.devtools.build.lib.remote.util.DigestUtil;
import com.google.devtools.build.lib.remote.util.DigestUtil.ActionKey;
import com.google.devtools.build.lib.remote.util.TracingMetadataUtils;
import com.google.devtools.build.lib.util.Pair;
import com.google.devtools.build.lib.util.io.FileOutErr;
import com.google.devtools.build.lib.vfs.FileSystem;
import com.google.devtools.build.lib.vfs.Path;
import com.google.devtools.build.lib.vfs.PathFragment;
import io.grpc.Status.UNAVAILABLE;
import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.SortedMap;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;


/**
 * Tests for {@link RemoteSpawnCache}.
 */
@RunWith(JUnit4.class)
public class RemoteSpawnCacheTest {
    private static final ArtifactExpander SIMPLE_ARTIFACT_EXPANDER = new ArtifactExpander() {
        @Override
        public void expand(Artifact artifact, Collection<? super Artifact> output) {
            output.add(artifact);
        }
    };

    private FileSystem fs;

    private DigestUtil digestUtil;

    private Path execRoot;

    private SimpleSpawn simpleSpawn;

    private FakeActionInputFileCache fakeFileCache;

    @Mock
    private AbstractRemoteActionCache remoteCache;

    private RemoteSpawnCache cache;

    private FileOutErr outErr;

    private final List<Pair<ProgressStatus, String>> progressUpdates = new ArrayList();

    private StoredEventHandler eventHandler = new StoredEventHandler();

    private final SpawnExecutionContext simplePolicy = new SpawnExecutionContext() {
        @Override
        public int getId() {
            return 0;
        }

        @Override
        public void prefetchInputs() {
            // CachedLocalSpawnRunner should never prefetch itself, though the nested SpawnRunner may.
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
            new com.google.devtools.build.lib.exec.SpawnInputExpander(execRoot, false).getInputMapping(simpleSpawn, RemoteSpawnCacheTest.SIMPLE_ARTIFACT_EXPANDER, IDENTITY, fakeFileCache, true);
        }

        @Override
        public void report(ProgressStatus state, String name) {
            progressUpdates.add(Pair.of(state, name));
        }
    };

    @SuppressWarnings("unchecked")
    @Test
    public void cacheHit() throws Exception {
        ActionResult actionResult = ActionResult.getDefaultInstance();
        Mockito.when(remoteCache.getCachedActionResult(ArgumentMatchers.any(ActionKey.class))).thenAnswer(new Answer<ActionResult>() {
            @Override
            public ActionResult answer(InvocationOnMock invocation) {
                RequestMetadata meta = TracingMetadataUtils.fromCurrentContext();
                assertThat(meta.getCorrelatedInvocationsId()).isEqualTo("build-req-id");
                assertThat(meta.getToolInvocationId()).isEqualTo("command-id");
                return actionResult;
            }
        });
        Mockito.doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) {
                RequestMetadata meta = TracingMetadataUtils.fromCurrentContext();
                assertThat(meta.getCorrelatedInvocationsId()).isEqualTo("build-req-id");
                assertThat(meta.getToolInvocationId()).isEqualTo("command-id");
                return null;
            }
        }).when(remoteCache).download(actionResult, execRoot, outErr);
        CacheHandle entry = cache.lookup(simpleSpawn, simplePolicy);
        assertThat(entry.hasResult()).isTrue();
        SpawnResult result = entry.getResult();
        // All other methods on RemoteActionCache have side effects, so we verify all of them.
        Mockito.verify(remoteCache).download(actionResult, execRoot, outErr);
        Mockito.verify(remoteCache, Mockito.never()).upload(ArgumentMatchers.any(ActionKey.class), ArgumentMatchers.any(Action.class), ArgumentMatchers.any(Command.class), ArgumentMatchers.any(Path.class), ArgumentMatchers.any(Collection.class), ArgumentMatchers.any(FileOutErr.class));
        assertThat(result.setupSuccess()).isTrue();
        assertThat(result.exitCode()).isEqualTo(0);
        assertThat(result.isCacheHit()).isTrue();
        // We expect the CachedLocalSpawnRunner to _not_ write to outErr at all.
        assertThat(outErr.hasRecordedOutput()).isFalse();
        assertThat(outErr.hasRecordedStderr()).isFalse();
        assertThat(progressUpdates).containsExactly(Pair.of(CHECKING_CACHE, "remote-cache"));
    }

    @Test
    public void cacheMiss() throws Exception {
        CacheHandle entry = cache.lookup(simpleSpawn, simplePolicy);
        assertThat(entry.hasResult()).isFalse();
        SpawnResult result = new SpawnResult.Builder().setExitCode(0).setStatus(SUCCESS).setRunnerName("test").build();
        ImmutableList<Path> outputFiles = ImmutableList.of(fs.getPath("/random/file"));
        Mockito.doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) {
                RequestMetadata meta = TracingMetadataUtils.fromCurrentContext();
                assertThat(meta.getCorrelatedInvocationsId()).isEqualTo("build-req-id");
                assertThat(meta.getToolInvocationId()).isEqualTo("command-id");
                return null;
            }
        }).when(remoteCache).upload(ArgumentMatchers.any(ActionKey.class), ArgumentMatchers.any(Action.class), ArgumentMatchers.any(Command.class), ArgumentMatchers.any(Path.class), ArgumentMatchers.eq(outputFiles), ArgumentMatchers.eq(outErr));
        entry.store(result);
        Mockito.verify(remoteCache).upload(ArgumentMatchers.any(ActionKey.class), ArgumentMatchers.any(Action.class), ArgumentMatchers.any(Command.class), ArgumentMatchers.any(Path.class), ArgumentMatchers.eq(outputFiles), ArgumentMatchers.eq(outErr));
        assertThat(progressUpdates).containsExactly(Pair.of(CHECKING_CACHE, "remote-cache"));
    }

    @Test
    public void noCacheSpawns() throws Exception {
        // Checks that spawns satisfying Spawns.mayBeCached false are not looked up in the remote cache,
        // and also that their result and artifacts are not uploaded to the remote cache.
        for (String requirement : ImmutableList.of(NO_CACHE, LOCAL)) {
            SimpleSpawn uncacheableSpawn = /* arguments= */
            /* environment= */
            /* inputs= */
            /* outputs= */
            new SimpleSpawn(new FakeOwner("foo", "bar"), ImmutableList.of(), ImmutableMap.of(), ImmutableMap.of(requirement, ""), ImmutableList.of(), ImmutableList.of(ActionInputHelper.fromPath("/random/file")), ResourceSet.ZERO);
            CacheHandle entry = cache.lookup(uncacheableSpawn, simplePolicy);
            Mockito.verify(remoteCache, Mockito.never()).getCachedActionResult(ArgumentMatchers.any(ActionKey.class));
            assertThat(entry.hasResult()).isFalse();
            SpawnResult result = new SpawnResult.Builder().setExitCode(0).setStatus(SUCCESS).setRunnerName("test").build();
            entry.store(result);
            Mockito.verifyNoMoreInteractions(remoteCache);
            assertThat(progressUpdates).containsExactly();
        }
    }

    @Test
    public void failedActionsAreNotUploaded() throws Exception {
        // Only successful action results are uploaded to the remote cache.
        CacheHandle entry = cache.lookup(simpleSpawn, simplePolicy);
        Mockito.verify(remoteCache).getCachedActionResult(ArgumentMatchers.any(ActionKey.class));
        assertThat(entry.hasResult()).isFalse();
        SpawnResult result = new SpawnResult.Builder().setExitCode(1).setStatus(NON_ZERO_EXIT).setRunnerName("test").build();
        ImmutableList<Path> outputFiles = ImmutableList.of(fs.getPath("/random/file"));
        entry.store(result);
        Mockito.verify(remoteCache, Mockito.never()).upload(ArgumentMatchers.any(ActionKey.class), ArgumentMatchers.any(Action.class), ArgumentMatchers.any(Command.class), ArgumentMatchers.any(Path.class), ArgumentMatchers.eq(outputFiles), ArgumentMatchers.eq(outErr));
        assertThat(progressUpdates).containsExactly(Pair.of(CHECKING_CACHE, "remote-cache"));
    }

    @Test
    public void printWarningIfUploadFails() throws Exception {
        CacheHandle entry = cache.lookup(simpleSpawn, simplePolicy);
        assertThat(entry.hasResult()).isFalse();
        SpawnResult result = new SpawnResult.Builder().setExitCode(0).setStatus(SUCCESS).setRunnerName("test").build();
        ImmutableList<Path> outputFiles = ImmutableList.of(fs.getPath("/random/file"));
        Mockito.doThrow(new IOException("cache down")).when(remoteCache).upload(ArgumentMatchers.any(ActionKey.class), ArgumentMatchers.any(Action.class), ArgumentMatchers.any(Command.class), ArgumentMatchers.any(Path.class), ArgumentMatchers.eq(outputFiles), ArgumentMatchers.eq(outErr));
        entry.store(result);
        Mockito.verify(remoteCache).upload(ArgumentMatchers.any(ActionKey.class), ArgumentMatchers.any(Action.class), ArgumentMatchers.any(Command.class), ArgumentMatchers.any(Path.class), ArgumentMatchers.eq(outputFiles), ArgumentMatchers.eq(outErr));
        assertThat(eventHandler.getEvents()).hasSize(1);
        Event evt = eventHandler.getEvents().get(0);
        assertThat(evt.getKind()).isEqualTo(WARNING);
        assertThat(evt.getMessage()).contains("cache down");
        assertThat(progressUpdates).containsExactly(Pair.of(CHECKING_CACHE, "remote-cache"));
    }

    @Test
    public void printWarningIfDownloadFails() throws Exception {
        Mockito.doThrow(new IOException(UNAVAILABLE.asRuntimeException())).when(remoteCache).getCachedActionResult(ArgumentMatchers.any(ActionKey.class));
        CacheHandle entry = cache.lookup(simpleSpawn, simplePolicy);
        assertThat(entry.hasResult()).isFalse();
        SpawnResult result = new SpawnResult.Builder().setExitCode(0).setStatus(SUCCESS).setRunnerName("test").build();
        ImmutableList<Path> outputFiles = ImmutableList.of(fs.getPath("/random/file"));
        Mockito.doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) {
                RequestMetadata meta = TracingMetadataUtils.fromCurrentContext();
                assertThat(meta.getCorrelatedInvocationsId()).isEqualTo("build-req-id");
                assertThat(meta.getToolInvocationId()).isEqualTo("command-id");
                return null;
            }
        }).when(remoteCache).upload(ArgumentMatchers.any(ActionKey.class), ArgumentMatchers.any(Action.class), ArgumentMatchers.any(Command.class), ArgumentMatchers.any(Path.class), ArgumentMatchers.eq(outputFiles), ArgumentMatchers.eq(outErr));
        entry.store(result);
        Mockito.verify(remoteCache).upload(ArgumentMatchers.any(ActionKey.class), ArgumentMatchers.any(Action.class), ArgumentMatchers.any(Command.class), ArgumentMatchers.any(Path.class), ArgumentMatchers.eq(outputFiles), ArgumentMatchers.eq(outErr));
        assertThat(eventHandler.getEvents()).hasSize(1);
        Event evt = eventHandler.getEvents().get(0);
        assertThat(evt.getKind()).isEqualTo(WARNING);
        assertThat(evt.getMessage()).contains("UNAVAILABLE");
        assertThat(progressUpdates).containsExactly(Pair.of(CHECKING_CACHE, "remote-cache"));
    }

    @Test
    public void orphanedCachedResultIgnored() throws Exception {
        Digest digest = digestUtil.computeAsUtf8("bla");
        ActionResult actionResult = ActionResult.newBuilder().addOutputFiles(OutputFile.newBuilder().setPath("/random/file").setDigest(digest)).build();
        Mockito.when(remoteCache.getCachedActionResult(ArgumentMatchers.any(ActionKey.class))).thenAnswer(new Answer<ActionResult>() {
            @Override
            public ActionResult answer(InvocationOnMock invocation) {
                RequestMetadata meta = TracingMetadataUtils.fromCurrentContext();
                assertThat(meta.getCorrelatedInvocationsId()).isEqualTo("build-req-id");
                assertThat(meta.getToolInvocationId()).isEqualTo("command-id");
                return actionResult;
            }
        });
        Mockito.doThrow(new CacheNotFoundException(digest, digestUtil)).when(remoteCache).download(actionResult, execRoot, outErr);
        CacheHandle entry = cache.lookup(simpleSpawn, simplePolicy);
        assertThat(entry.hasResult()).isFalse();
        SpawnResult result = new SpawnResult.Builder().setExitCode(0).setStatus(SUCCESS).setRunnerName("test").build();
        ImmutableList<Path> outputFiles = ImmutableList.of(fs.getPath("/random/file"));
        Mockito.doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) {
                RequestMetadata meta = TracingMetadataUtils.fromCurrentContext();
                assertThat(meta.getCorrelatedInvocationsId()).isEqualTo("build-req-id");
                assertThat(meta.getToolInvocationId()).isEqualTo("command-id");
                return null;
            }
        }).when(remoteCache).upload(ArgumentMatchers.any(ActionKey.class), ArgumentMatchers.any(Action.class), ArgumentMatchers.any(Command.class), ArgumentMatchers.any(Path.class), ArgumentMatchers.eq(outputFiles), ArgumentMatchers.eq(outErr));
        entry.store(result);
        Mockito.verify(remoteCache).upload(ArgumentMatchers.any(ActionKey.class), ArgumentMatchers.any(Action.class), ArgumentMatchers.any(Command.class), ArgumentMatchers.any(Path.class), ArgumentMatchers.eq(outputFiles), ArgumentMatchers.eq(outErr));
        assertThat(progressUpdates).containsExactly(Pair.of(CHECKING_CACHE, "remote-cache"));
        assertThat(eventHandler.getEvents()).isEmpty();// no warning is printed.

    }
}

