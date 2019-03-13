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
package com.google.devtools.build.lib.exec.local;


import ExecutionRequirements.DISABLE_LOCAL_PREFETCH;
import ProgressStatus.EXECUTING;
import ProgressStatus.SCHEDULING;
import SpawnResult.Status.EXECUTION_DENIED;
import SpawnResult.Status.EXECUTION_FAILED;
import SpawnResult.Status.NON_ZERO_EXIT;
import SpawnResult.Status.SUCCESS;
import StreamAction.REDIRECT;
import com.google.common.collect.ImmutableList;
import com.google.common.io.ByteStreams;
import com.google.devtools.build.lib.actions.ActionInput;
import com.google.devtools.build.lib.actions.Artifact.ArtifactExpander;
import com.google.devtools.build.lib.actions.CommandLines.ParamFileActionInput;
import com.google.devtools.build.lib.actions.MetadataProvider;
import com.google.devtools.build.lib.actions.ParameterFile.ParameterFileType;
import com.google.devtools.build.lib.actions.ResourceManager;
import com.google.devtools.build.lib.actions.Spawn;
import com.google.devtools.build.lib.actions.SpawnResult;
import com.google.devtools.build.lib.actions.cache.MetadataInjector;
import com.google.devtools.build.lib.exec.BinTools;
import com.google.devtools.build.lib.exec.SpawnRunner.ProgressStatus;
import com.google.devtools.build.lib.exec.SpawnRunner.SpawnExecutionContext;
import com.google.devtools.build.lib.exec.util.SpawnBuilder;
import com.google.devtools.build.lib.shell.Subprocess;
import com.google.devtools.build.lib.shell.SubprocessBuilder;
import com.google.devtools.build.lib.shell.SubprocessFactory;
import com.google.devtools.build.lib.util.NetUtil;
import com.google.devtools.build.lib.util.OS;
import com.google.devtools.build.lib.util.OsUtils;
import com.google.devtools.build.lib.util.io.FileOutErr;
import com.google.devtools.build.lib.vfs.DigestHashFunction;
import com.google.devtools.build.lib.vfs.FileSystem;
import com.google.devtools.build.lib.vfs.FileSystemUtils;
import com.google.devtools.build.lib.vfs.Path;
import com.google.devtools.build.lib.vfs.PathFragment;
import com.google.devtools.common.options.Options;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import static LocalEnvProvider.UNMODIFIED;
import static org.mockito.ArgumentMatchers.any;


/**
 * Unit tests for {@link LocalSpawnRunner}.
 */
@RunWith(JUnit4.class)
public class LocalSpawnRunnerTest {
    private static final boolean USE_WRAPPER = true;

    private static final boolean NO_WRAPPER = false;

    private static class TestedLocalSpawnRunner extends LocalSpawnRunner {
        public TestedLocalSpawnRunner(Path execRoot, Path embeddedBin, LocalExecutionOptions localExecutionOptions, ResourceManager resourceManager, boolean useProcessWrapper, OS localOs, LocalEnvProvider localEnvProvider) {
            super(execRoot, localExecutionOptions, resourceManager, useProcessWrapper, localOs, localEnvProvider, (useProcessWrapper ? BinTools.forEmbeddedBin(embeddedBin, ImmutableList.of(("process-wrapper" + (OsUtils.executableExtension(localOs))))) : null));
        }

        // Rigged to act on supplied filesystem (e.g. InMemoryFileSystem) for testing purposes
        // TODO(b/70572634): Update FileSystem abstraction to support createTempDirectory() from
        // the java.nio.file.Files package.
        @Override
        protected Path createActionTemp(Path execRoot) throws IOException {
            Path tempDirPath;
            do {
                String idStr = ((Long.toHexString(Thread.currentThread().getId())) + "_") + (Long.toHexString(ThreadLocalRandom.current().nextLong()));
                tempDirPath = execRoot.getRelative(("tmp" + idStr));
            } while (tempDirPath.exists() );
            if (!(tempDirPath.createDirectory())) {
                throw new IOException(String.format("Could not create temp directory '%s'", tempDirPath));
            }
            return tempDirPath;
        }
    }

    private static class FinishedSubprocess implements Subprocess {
        private final int exitCode;

        public FinishedSubprocess(int exitCode) {
            this.exitCode = exitCode;
        }

        @Override
        public boolean destroy() {
            return false;
        }

        @Override
        public int exitValue() {
            return exitCode;
        }

        @Override
        public boolean finished() {
            return true;
        }

        @Override
        public boolean timedout() {
            return false;
        }

        @Override
        public void waitFor() throws InterruptedException {
            // Do nothing.
        }

        @Override
        public OutputStream getOutputStream() {
            return ByteStreams.nullOutputStream();
        }

        @Override
        public InputStream getInputStream() {
            return new ByteArrayInputStream(new byte[0]);
        }

        @Override
        public InputStream getErrorStream() {
            return new ByteArrayInputStream(new byte[0]);
        }

        @Override
        public void close() {
            // Do nothing.
        }
    }

    private static final Spawn SIMPLE_SPAWN = new SpawnBuilder("/bin/echo", "Hi!").withEnvironment("VARIABLE", "value").build();

    private static final class SubprocessInterceptor implements SubprocessFactory {
        @Override
        public Subprocess create(SubprocessBuilder params) throws IOException {
            throw new UnsupportedOperationException();
        }
    }

    private final class SpawnExecutionContextForTesting implements SpawnExecutionContext {
        private final List<ProgressStatus> reportedStatus = new ArrayList<>();

        private final TreeMap<PathFragment, ActionInput> inputMapping = new TreeMap<>();

        private long timeoutMillis;

        private boolean prefetchCalled;

        private boolean lockOutputFilesCalled;

        private FileOutErr fileOutErr;

        public SpawnExecutionContextForTesting(FileOutErr fileOutErr) {
            this.fileOutErr = fileOutErr;
        }

        @Override
        public int getId() {
            return 0;
        }

        @Override
        public void prefetchInputs() throws IOException {
            prefetchCalled = true;
        }

        @Override
        public void lockOutputFiles() throws InterruptedException {
            lockOutputFilesCalled = true;
        }

        @Override
        public boolean speculating() {
            return false;
        }

        @Override
        public MetadataProvider getMetadataProvider() {
            return mockFileCache;
        }

        @Override
        public ArtifactExpander getArtifactExpander() {
            throw new UnsupportedOperationException();
        }

        @Override
        public Duration getTimeout() {
            return Duration.ofMillis(timeoutMillis);
        }

        @Override
        public FileOutErr getFileOutErr() {
            return fileOutErr;
        }

        @Override
        public SortedMap<PathFragment, ActionInput> getInputMapping(boolean expandTreeArtifactsInRunfiles) {
            return inputMapping;
        }

        @Override
        public void report(ProgressStatus state, String name) {
            reportedStatus.add(state);
        }

        @Override
        public MetadataInjector getMetadataInjector() {
            throw new UnsupportedOperationException();
        }
    }

    private final MetadataProvider mockFileCache = Mockito.mock(MetadataProvider.class);

    private final ResourceManager resourceManager = ResourceManager.instanceForTestingOnly();

    private Logger logger;

    @Test
    public void vanillaZeroExit() throws Exception {
        // TODO(#3536): Make this test work on Windows.
        // The Command API implicitly absolutizes the path, and we get weird paths on Windows:
        // T:\execroot\execroot\_bin\process-wrapper
        Assume.assumeTrue(((OS.getCurrent()) != (OS.WINDOWS)));
        FileSystem fs = setupEnvironmentForFakeExecution();
        SubprocessFactory factory = Mockito.mock(SubprocessFactory.class);
        ArgumentCaptor<SubprocessBuilder> captor = ArgumentCaptor.forClass(SubprocessBuilder.class);
        Mockito.when(factory.create(captor.capture())).thenReturn(new LocalSpawnRunnerTest.FinishedSubprocess(0));
        SubprocessBuilder.setDefaultSubprocessFactory(factory);
        LocalExecutionOptions options = Options.getDefaults(LocalExecutionOptions.class);
        options.localSigkillGraceSeconds = 456;
        LocalSpawnRunner runner = new LocalSpawnRunnerTest.TestedLocalSpawnRunner(fs.getPath("/execroot"), fs.getPath("/embedded_bin"), options, resourceManager, LocalSpawnRunnerTest.USE_WRAPPER, OS.LINUX, UNMODIFIED);
        FileOutErr fileOutErr = new FileOutErr(fs.getPath("/out/stdout"), fs.getPath("/out/stderr"));
        LocalSpawnRunnerTest.SpawnExecutionContextForTesting policy = new LocalSpawnRunnerTest.SpawnExecutionContextForTesting(fileOutErr);
        policy.timeoutMillis = 123 * 1000L;
        assertThat(fs.getPath("/execroot").createDirectory()).isTrue();
        SpawnResult result = runner.execAsync(LocalSpawnRunnerTest.SIMPLE_SPAWN, policy).get();
        Mockito.verify(factory).create(any(SubprocessBuilder.class));
        assertThat(result.status()).isEqualTo(SUCCESS);
        assertThat(result.exitCode()).isEqualTo(0);
        assertThat(result.setupSuccess()).isTrue();
        assertThat(result.getExecutorHostName()).isEqualTo(NetUtil.getCachedShortHostName());
        assertThat(captor.getValue().getArgv()).containsExactlyElementsIn(ImmutableList.of("/embedded_bin/process-wrapper", "--timeout=123", "--kill_delay=456", "/bin/echo", "Hi!"));
        assertThat(captor.getValue().getEnv()).containsExactly("VARIABLE", "value");
        assertThat(captor.getValue().getTimeoutMillis()).isEqualTo(0);
        assertThat(captor.getValue().getStdout()).isEqualTo(REDIRECT);
        assertThat(captor.getValue().getStdoutFile()).isEqualTo(new File("/out/stdout"));
        assertThat(captor.getValue().getStderr()).isEqualTo(REDIRECT);
        assertThat(captor.getValue().getStderrFile()).isEqualTo(new File("/out/stderr"));
        assertThat(policy.lockOutputFilesCalled).isTrue();
        assertThat(policy.reportedStatus).containsExactly(SCHEDULING, EXECUTING).inOrder();
    }

    @Test
    public void testParamFiles() throws Exception {
        // TODO(#3536): Make this test work on Windows.
        // The Command API implicitly absolutizes the path, and we get weird paths on Windows:
        // T:\execroot\execroot\_bin\process-wrapper
        Assume.assumeTrue(((OS.getCurrent()) != (OS.WINDOWS)));
        FileSystem fs = setupEnvironmentForFakeExecution();
        SubprocessFactory factory = Mockito.mock(SubprocessFactory.class);
        Mockito.when(factory.create(ArgumentMatchers.any())).thenReturn(new LocalSpawnRunnerTest.FinishedSubprocess(0));
        SubprocessBuilder.setDefaultSubprocessFactory(factory);
        LocalExecutionOptions options = Options.getDefaults(LocalExecutionOptions.class);
        options.localSigkillGraceSeconds = 456;
        Path execRoot = fs.getPath("/execroot");
        LocalSpawnRunner runner = new LocalSpawnRunnerTest.TestedLocalSpawnRunner(execRoot, fs.getPath("/embedded_bin"), options, resourceManager, LocalSpawnRunnerTest.USE_WRAPPER, OS.LINUX, UNMODIFIED);
        ParamFileActionInput paramFileActionInput = new ParamFileActionInput(PathFragment.create("some/dir/params"), ImmutableList.of("--foo", "--bar"), ParameterFileType.UNQUOTED, StandardCharsets.UTF_8);
        Spawn spawn = new SpawnBuilder("/bin/echo", "Hi!").withInput(paramFileActionInput).withEnvironment("VARIABLE", "value").build();
        FileOutErr fileOutErr = new FileOutErr(fs.getPath("/out/stdout"), fs.getPath("/out/stderr"));
        LocalSpawnRunnerTest.SpawnExecutionContextForTesting policy = new LocalSpawnRunnerTest.SpawnExecutionContextForTesting(fileOutErr);
        policy.timeoutMillis = 123 * 1000L;
        assertThat(fs.getPath("/execroot").createDirectory()).isTrue();
        SpawnResult result = runner.execAsync(spawn, policy).get();
        assertThat(result.status()).isEqualTo(SUCCESS);
        assertThat(result.exitCode()).isEqualTo(0);
        assertThat(result.setupSuccess()).isTrue();
        assertThat(result.getExecutorHostName()).isEqualTo(NetUtil.getCachedShortHostName());
        Path paramFile = execRoot.getRelative("some/dir/params");
        assertThat(paramFile.exists()).isTrue();
        try (InputStream inputStream = paramFile.getInputStream()) {
            assertThat(new String(ByteStreams.toByteArray(inputStream), StandardCharsets.UTF_8).split("\n")).asList().containsExactly("--foo", "--bar");
        }
    }

    @Test
    public void noProcessWrapper() throws Exception {
        // TODO(#3536): Make this test work on Windows.
        // The Command API implicitly absolutizes the path, and we get weird paths on Windows:
        // T:\execroot\bin\echo
        Assume.assumeTrue(((OS.getCurrent()) != (OS.WINDOWS)));
        FileSystem fs = setupEnvironmentForFakeExecution();
        SubprocessFactory factory = Mockito.mock(SubprocessFactory.class);
        ArgumentCaptor<SubprocessBuilder> captor = ArgumentCaptor.forClass(SubprocessBuilder.class);
        Mockito.when(factory.create(captor.capture())).thenReturn(new LocalSpawnRunnerTest.FinishedSubprocess(0));
        SubprocessBuilder.setDefaultSubprocessFactory(factory);
        LocalExecutionOptions options = Options.getDefaults(LocalExecutionOptions.class);
        options.localSigkillGraceSeconds = 456;
        LocalSpawnRunner runner = new LocalSpawnRunnerTest.TestedLocalSpawnRunner(fs.getPath("/execroot"), fs.getPath("/embedded_bin"), options, resourceManager, LocalSpawnRunnerTest.NO_WRAPPER, OS.LINUX, UNMODIFIED);
        FileOutErr fileOutErr = new FileOutErr(fs.getPath("/out/stdout"), fs.getPath("/out/stderr"));
        LocalSpawnRunnerTest.SpawnExecutionContextForTesting policy = new LocalSpawnRunnerTest.SpawnExecutionContextForTesting(fileOutErr);
        policy.timeoutMillis = 123 * 1000L;
        assertThat(fs.getPath("/execroot").createDirectory()).isTrue();
        SpawnResult result = runner.execAsync(LocalSpawnRunnerTest.SIMPLE_SPAWN, policy).get();
        Mockito.verify(factory).create(ArgumentMatchers.any());
        assertThat(result.status()).isEqualTo(SUCCESS);
        assertThat(result.exitCode()).isEqualTo(0);
        assertThat(result.setupSuccess()).isTrue();
        assertThat(result.getExecutorHostName()).isEqualTo(NetUtil.getCachedShortHostName());
        assertThat(captor.getValue().getArgv()).containsExactlyElementsIn(ImmutableList.of("/bin/echo", "Hi!"));
        assertThat(captor.getValue().getEnv()).containsExactly("VARIABLE", "value");
        // Without the process wrapper, we use the Command API to enforce the timeout.
        assertThat(captor.getValue().getTimeoutMillis()).isEqualTo(policy.timeoutMillis);
        assertThat(policy.lockOutputFilesCalled).isTrue();
    }

    @Test
    public void nonZeroExit() throws Exception {
        // TODO(#3536): Make this test work on Windows.
        // The Command API implicitly absolutizes the path, and we get weird paths on Windows:
        // T:\execroot\execroot\_bin\process-wrapper
        Assume.assumeTrue(((OS.getCurrent()) != (OS.WINDOWS)));
        FileSystem fs = setupEnvironmentForFakeExecution();
        SubprocessFactory factory = Mockito.mock(SubprocessFactory.class);
        ArgumentCaptor<SubprocessBuilder> captor = ArgumentCaptor.forClass(SubprocessBuilder.class);
        Mockito.when(factory.create(captor.capture())).thenReturn(new LocalSpawnRunnerTest.FinishedSubprocess(3));
        SubprocessBuilder.setDefaultSubprocessFactory(factory);
        LocalExecutionOptions options = Options.getDefaults(LocalExecutionOptions.class);
        LocalSpawnRunner runner = new LocalSpawnRunnerTest.TestedLocalSpawnRunner(fs.getPath("/execroot"), fs.getPath("/embedded_bin"), options, resourceManager, LocalSpawnRunnerTest.USE_WRAPPER, OS.LINUX, UNMODIFIED);
        assertThat(fs.getPath("/execroot").createDirectory()).isTrue();
        FileOutErr fileOutErr = new FileOutErr(fs.getPath("/out/stdout"), fs.getPath("/out/stderr"));
        LocalSpawnRunnerTest.SpawnExecutionContextForTesting policy = new LocalSpawnRunnerTest.SpawnExecutionContextForTesting(fileOutErr);
        SpawnResult result = runner.execAsync(LocalSpawnRunnerTest.SIMPLE_SPAWN, policy).get();
        Mockito.verify(factory).create(any(SubprocessBuilder.class));
        assertThat(result.status()).isEqualTo(NON_ZERO_EXIT);
        assertThat(result.exitCode()).isEqualTo(3);
        assertThat(result.setupSuccess()).isTrue();
        assertThat(result.getExecutorHostName()).isEqualTo(NetUtil.getCachedShortHostName());
        assertThat(captor.getValue().getArgv()).containsExactlyElementsIn(// process-wrapper timeout grace_time stdout stderr
        ImmutableList.of("/embedded_bin/process-wrapper", "--timeout=0", "--kill_delay=15", "/bin/echo", "Hi!"));
        assertThat(captor.getValue().getEnv()).containsExactly("VARIABLE", "value");
        assertThat(captor.getValue().getStdout()).isEqualTo(REDIRECT);
        assertThat(captor.getValue().getStdoutFile()).isEqualTo(new File("/out/stdout"));
        assertThat(captor.getValue().getStderr()).isEqualTo(REDIRECT);
        assertThat(captor.getValue().getStderrFile()).isEqualTo(new File("/out/stderr"));
        assertThat(policy.lockOutputFilesCalled).isTrue();
    }

    @Test
    public void processStartupThrows() throws Exception {
        FileSystem fs = setupEnvironmentForFakeExecution();
        SubprocessFactory factory = Mockito.mock(SubprocessFactory.class);
        ArgumentCaptor<SubprocessBuilder> captor = ArgumentCaptor.forClass(SubprocessBuilder.class);
        Mockito.when(factory.create(captor.capture())).thenThrow(new IOException("I'm sorry, Dave"));
        SubprocessBuilder.setDefaultSubprocessFactory(factory);
        LocalExecutionOptions options = Options.getDefaults(LocalExecutionOptions.class);
        LocalSpawnRunner runner = new LocalSpawnRunnerTest.TestedLocalSpawnRunner(fs.getPath("/execroot"), fs.getPath("/embedded_bin"), options, resourceManager, LocalSpawnRunnerTest.USE_WRAPPER, OS.LINUX, UNMODIFIED);
        assertThat(fs.getPath("/out").createDirectory()).isTrue();
        assertThat(fs.getPath("/execroot").createDirectory()).isTrue();
        FileOutErr fileOutErr = new FileOutErr(fs.getPath("/out/stdout"), fs.getPath("/out/stderr"));
        LocalSpawnRunnerTest.SpawnExecutionContextForTesting policy = new LocalSpawnRunnerTest.SpawnExecutionContextForTesting(fileOutErr);
        SpawnResult result = runner.execAsync(LocalSpawnRunnerTest.SIMPLE_SPAWN, policy).get();
        Mockito.verify(factory).create(any(SubprocessBuilder.class));
        assertThat(result.status()).isEqualTo(EXECUTION_FAILED);
        assertThat(result.exitCode()).isEqualTo((-1));
        assertThat(result.setupSuccess()).isFalse();
        assertThat(result.getWallTime()).isEmpty();
        assertThat(result.getUserTime()).isEmpty();
        assertThat(result.getSystemTime()).isEmpty();
        assertThat(result.getExecutorHostName()).isEqualTo(NetUtil.getCachedShortHostName());
        assertThat(FileSystemUtils.readContent(fs.getPath("/out/stderr"), StandardCharsets.UTF_8)).isEqualTo("Action failed to execute: java.io.IOException: I\'m sorry, Dave\n");
        assertThat(policy.lockOutputFilesCalled).isTrue();
    }

    @Test
    public void disallowLocalExecution() throws Exception {
        FileSystem fs = setupEnvironmentForFakeExecution();
        LocalExecutionOptions options = Options.getDefaults(LocalExecutionOptions.class);
        options.allowedLocalAction = Pattern.compile("none");
        LocalSpawnRunner runner = new LocalSpawnRunnerTest.TestedLocalSpawnRunner(fs.getPath("/execroot"), fs.getPath("/embedded_bin"), options, resourceManager, LocalSpawnRunnerTest.USE_WRAPPER, OS.LINUX, UNMODIFIED);
        assertThat(fs.getPath("/execroot").createDirectory()).isTrue();
        FileOutErr fileOutErr = new FileOutErr();
        LocalSpawnRunnerTest.SpawnExecutionContextForTesting policy = new LocalSpawnRunnerTest.SpawnExecutionContextForTesting(fileOutErr);
        SpawnResult reply = runner.execAsync(LocalSpawnRunnerTest.SIMPLE_SPAWN, policy).get();
        assertThat(reply.status()).isEqualTo(EXECUTION_DENIED);
        assertThat(reply.exitCode()).isEqualTo((-1));
        assertThat(reply.setupSuccess()).isFalse();
        assertThat(reply.getWallTime()).isEmpty();
        assertThat(reply.getUserTime()).isEmpty();
        assertThat(reply.getSystemTime()).isEmpty();
        assertThat(reply.getExecutorHostName()).isEqualTo(NetUtil.getCachedShortHostName());
        // TODO(ulfjack): Maybe we should only lock after checking?
        assertThat(policy.lockOutputFilesCalled).isTrue();
    }

    @Test
    public void interruptedException() throws Exception {
        FileSystem fs = setupEnvironmentForFakeExecution();
        SubprocessFactory factory = Mockito.mock(SubprocessFactory.class);
        ArgumentCaptor<SubprocessBuilder> captor = ArgumentCaptor.forClass(SubprocessBuilder.class);
        Mockito.when(factory.create(captor.capture())).thenReturn(new LocalSpawnRunnerTest.FinishedSubprocess(3) {
            private boolean destroyed;

            @Override
            public boolean destroy() {
                destroyed = true;
                return true;
            }

            @Override
            public void waitFor() throws InterruptedException {
                if (!(destroyed)) {
                    throw new InterruptedException();
                }
            }
        });
        SubprocessBuilder.setDefaultSubprocessFactory(factory);
        LocalExecutionOptions options = Options.getDefaults(LocalExecutionOptions.class);
        LocalSpawnRunner runner = new LocalSpawnRunnerTest.TestedLocalSpawnRunner(fs.getPath("/execroot"), fs.getPath("/embedded_bin"), options, resourceManager, LocalSpawnRunnerTest.USE_WRAPPER, OS.LINUX, UNMODIFIED);
        FileOutErr fileOutErr = new FileOutErr(fs.getPath("/out/stdout"), fs.getPath("/out/stderr"));
        LocalSpawnRunnerTest.SpawnExecutionContextForTesting policy = new LocalSpawnRunnerTest.SpawnExecutionContextForTesting(fileOutErr);
        assertThat(fs.getPath("/execroot").createDirectory()).isTrue();
        try {
            runner.execAsync(LocalSpawnRunnerTest.SIMPLE_SPAWN, policy).get();
            Assert.fail();
        } catch (InterruptedException expected) {
            // Clear the interrupted status or subsequent tests in the same process will fail.
            Thread.interrupted();
        }
        assertThat(policy.lockOutputFilesCalled).isTrue();
    }

    @Test
    public void checkPrefetchCalled() throws Exception {
        FileSystem fs = setupEnvironmentForFakeExecution();
        SubprocessFactory factory = Mockito.mock(SubprocessFactory.class);
        Mockito.when(factory.create(ArgumentMatchers.any())).thenReturn(new LocalSpawnRunnerTest.FinishedSubprocess(0));
        SubprocessBuilder.setDefaultSubprocessFactory(factory);
        LocalExecutionOptions options = Options.getDefaults(LocalExecutionOptions.class);
        LocalSpawnRunner runner = new LocalSpawnRunnerTest.TestedLocalSpawnRunner(fs.getPath("/execroot"), fs.getPath("/embedded_bin"), options, resourceManager, LocalSpawnRunnerTest.USE_WRAPPER, OS.LINUX, UNMODIFIED);
        FileOutErr fileOutErr = new FileOutErr(fs.getPath("/out/stdout"), fs.getPath("/out/stderr"));
        LocalSpawnRunnerTest.SpawnExecutionContextForTesting policy = new LocalSpawnRunnerTest.SpawnExecutionContextForTesting(fileOutErr);
        policy.timeoutMillis = 123 * 1000L;
        assertThat(fs.getPath("/execroot").createDirectory()).isTrue();
        runner.execAsync(LocalSpawnRunnerTest.SIMPLE_SPAWN, policy).get();
        assertThat(policy.prefetchCalled).isTrue();
    }

    @Test
    public void checkNoPrefetchCalled() throws Exception {
        FileSystem fs = setupEnvironmentForFakeExecution();
        SubprocessFactory factory = Mockito.mock(SubprocessFactory.class);
        Mockito.when(factory.create(ArgumentMatchers.any())).thenReturn(new LocalSpawnRunnerTest.FinishedSubprocess(0));
        SubprocessBuilder.setDefaultSubprocessFactory(factory);
        LocalExecutionOptions options = Options.getDefaults(LocalExecutionOptions.class);
        LocalSpawnRunner runner = new LocalSpawnRunnerTest.TestedLocalSpawnRunner(fs.getPath("/execroot"), fs.getPath("/embedded_bin"), options, resourceManager, LocalSpawnRunnerTest.USE_WRAPPER, OS.LINUX, UNMODIFIED);
        FileOutErr fileOutErr = new FileOutErr(fs.getPath("/out/stdout"), fs.getPath("/out/stderr"));
        LocalSpawnRunnerTest.SpawnExecutionContextForTesting policy = new LocalSpawnRunnerTest.SpawnExecutionContextForTesting(fileOutErr);
        policy.timeoutMillis = 123 * 1000L;
        Spawn spawn = new SpawnBuilder("/bin/echo", "Hi!").withExecutionInfo(DISABLE_LOCAL_PREFETCH, "").build();
        assertThat(fs.getPath("/execroot").createDirectory()).isTrue();
        runner.execAsync(spawn, policy).get();
        assertThat(policy.prefetchCalled).isFalse();
    }

    @Test
    public void checkLocalEnvProviderCalled() throws Exception {
        FileSystem fs = setupEnvironmentForFakeExecution();
        SubprocessFactory factory = Mockito.mock(SubprocessFactory.class);
        Mockito.when(factory.create(ArgumentMatchers.any())).thenReturn(new LocalSpawnRunnerTest.FinishedSubprocess(0));
        SubprocessBuilder.setDefaultSubprocessFactory(factory);
        LocalEnvProvider localEnvProvider = Mockito.mock(LocalEnvProvider.class);
        LocalExecutionOptions options = Options.getDefaults(LocalExecutionOptions.class);
        LocalSpawnRunner runner = new LocalSpawnRunnerTest.TestedLocalSpawnRunner(fs.getPath("/execroot"), fs.getPath("/embedded_bin"), options, resourceManager, LocalSpawnRunnerTest.USE_WRAPPER, OS.LINUX, localEnvProvider);
        FileOutErr fileOutErr = new FileOutErr(fs.getPath("/out/stdout"), fs.getPath("/out/stderr"));
        LocalSpawnRunnerTest.SpawnExecutionContextForTesting policy = new LocalSpawnRunnerTest.SpawnExecutionContextForTesting(fileOutErr);
        policy.timeoutMillis = 123 * 1000L;
        assertThat(fs.getPath("/execroot").createDirectory()).isTrue();
        runner.execAsync(LocalSpawnRunnerTest.SIMPLE_SPAWN, policy).get();
        Mockito.verify(localEnvProvider).rewriteLocalEnv(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.matches("^/execroot/tmp[0-9a-fA-F]+_[0-9a-fA-F]+/work$"));
    }

    @Test
    public void useCorrectExtensionOnWindows() throws Exception {
        // TODO(#3536): Make this test work on Windows.
        // The Command API implicitly absolutizes the path, and we get weird paths on Windows:
        // T:\execroot\execroot\_bin\process-wrapper.exe
        Assume.assumeTrue(((OS.getCurrent()) != (OS.WINDOWS)));
        FileSystem fs = setupEnvironmentForFakeExecution();
        SubprocessFactory factory = Mockito.mock(SubprocessFactory.class);
        ArgumentCaptor<SubprocessBuilder> captor = ArgumentCaptor.forClass(SubprocessBuilder.class);
        Mockito.when(factory.create(captor.capture())).thenReturn(new LocalSpawnRunnerTest.FinishedSubprocess(0));
        SubprocessBuilder.setDefaultSubprocessFactory(factory);
        LocalExecutionOptions options = Options.getDefaults(LocalExecutionOptions.class);
        options.localSigkillGraceSeconds = 654;
        LocalSpawnRunner runner = new LocalSpawnRunnerTest.TestedLocalSpawnRunner(fs.getPath("/execroot"), fs.getPath("/embedded_bin"), options, resourceManager, LocalSpawnRunnerTest.USE_WRAPPER, OS.WINDOWS, UNMODIFIED);
        FileOutErr fileOutErr = new FileOutErr(fs.getPath("/out/stdout"), fs.getPath("/out/stderr"));
        LocalSpawnRunnerTest.SpawnExecutionContextForTesting policy = new LocalSpawnRunnerTest.SpawnExecutionContextForTesting(fileOutErr);
        policy.timeoutMillis = 321 * 1000L;
        assertThat(fs.getPath("/execroot").createDirectory()).isTrue();
        SpawnResult result = runner.execAsync(LocalSpawnRunnerTest.SIMPLE_SPAWN, policy).get();
        Mockito.verify(factory).create(any(SubprocessBuilder.class));
        assertThat(result.status()).isEqualTo(SUCCESS);
        assertThat(captor.getValue().getArgv()).containsExactlyElementsIn(// process-wrapper timeout grace_time stdout stderr
        ImmutableList.of("/embedded_bin/process-wrapper.exe", "--timeout=321", "--kill_delay=654", "/bin/echo", "Hi!"));
    }

    @Test
    public void hasExecutionStatistics_whenOptionIsEnabled() throws Exception {
        // TODO(b/62588075) Currently no process-wrapper or execution statistics support in Windows.
        Assume.assumeTrue(((OS.getCurrent()) != (OS.WINDOWS)));
        FileSystem fs = new com.google.devtools.build.lib.unix.UnixFileSystem(DigestHashFunction.DEFAULT_HASH_FOR_TESTS);
        LocalExecutionOptions options = Options.getDefaults(LocalExecutionOptions.class);
        options.collectLocalExecutionStatistics = true;
        Duration minimumWallTimeToSpend = Duration.ofSeconds(10);
        Duration minimumUserTimeToSpend = minimumWallTimeToSpend;
        // Under normal loads we should be able to use a much lower bound for maxUserTime, but be
        // generous here in case of hardware issues.
        Duration maximumUserTimeToSpend = minimumUserTimeToSpend.plus(Duration.ofSeconds(20));
        Duration minimumSystemTimeToSpend = Duration.ZERO;
        // Under normal loads we should be able to use a much lower bound for maxSysTime, but be
        // generous here in case of hardware issues.
        Duration maximumSystemTimeToSpend = minimumSystemTimeToSpend.plus(Duration.ofSeconds(20));
        Path execRoot = getTemporaryExecRoot(fs);
        Path embeddedBinaries = getTemporaryEmbeddedBin(fs);
        BinTools binTools = BinTools.forEmbeddedBin(embeddedBinaries, ImmutableList.of("process-wrapper"));
        copyProcessWrapperIntoExecRoot(binTools.getEmbeddedPath("process-wrapper"));
        Path cpuTimeSpenderPath = copyCpuTimeSpenderIntoExecRoot(execRoot);
        LocalSpawnRunner runner = new LocalSpawnRunner(execRoot, options, resourceManager, LocalSpawnRunnerTest.USE_WRAPPER, OS.LINUX, UNMODIFIED, binTools);
        Spawn spawn = new SpawnBuilder(cpuTimeSpenderPath.getPathString(), String.valueOf(minimumUserTimeToSpend.getSeconds()), String.valueOf(minimumSystemTimeToSpend.getSeconds())).build();
        FileOutErr fileOutErr = new FileOutErr(fs.getPath("/dev/null"), fs.getPath("/dev/null"));
        LocalSpawnRunnerTest.SpawnExecutionContextForTesting policy = new LocalSpawnRunnerTest.SpawnExecutionContextForTesting(fileOutErr);
        SpawnResult spawnResult = runner.execAsync(spawn, policy).get();
        assertThat(spawnResult.status()).isEqualTo(SUCCESS);
        assertThat(spawnResult.exitCode()).isEqualTo(0);
        assertThat(spawnResult.setupSuccess()).isTrue();
        assertThat(spawnResult.getExecutorHostName()).isEqualTo(NetUtil.getCachedShortHostName());
        assertThat(spawnResult.getWallTime()).isPresent();
        assertThat(spawnResult.getWallTime().get()).isAtLeast(minimumWallTimeToSpend);
        // Under heavy starvation, max wall time could be anything, so don't check it here.
        assertThat(spawnResult.getUserTime()).isPresent();
        assertThat(spawnResult.getUserTime().get()).isAtLeast(minimumUserTimeToSpend);
        assertThat(spawnResult.getUserTime().get()).isAtMost(maximumUserTimeToSpend);
        assertThat(spawnResult.getSystemTime()).isPresent();
        assertThat(spawnResult.getSystemTime().get()).isAtLeast(minimumSystemTimeToSpend);
        assertThat(spawnResult.getSystemTime().get()).isAtMost(maximumSystemTimeToSpend);
        assertThat(spawnResult.getNumBlockOutputOperations().get()).isAtLeast(0L);
        assertThat(spawnResult.getNumBlockInputOperations().get()).isAtLeast(0L);
        assertThat(spawnResult.getNumInvoluntaryContextSwitches().get()).isAtLeast(0L);
    }

    @Test
    public void hasNoExecutionStatistics_whenOptionIsDisabled() throws Exception {
        // TODO(b/62588075) Currently no process-wrapper or execution statistics support in Windows.
        Assume.assumeTrue(((OS.getCurrent()) != (OS.WINDOWS)));
        FileSystem fs = new com.google.devtools.build.lib.unix.UnixFileSystem(DigestHashFunction.DEFAULT_HASH_FOR_TESTS);
        LocalExecutionOptions options = Options.getDefaults(LocalExecutionOptions.class);
        options.collectLocalExecutionStatistics = false;
        Duration minimumWallTimeToSpend = Duration.ofSeconds(1);
        Duration minimumUserTimeToSpend = minimumWallTimeToSpend;
        Duration minimumSystemTimeToSpend = Duration.ZERO;
        Path execRoot = getTemporaryExecRoot(fs);
        Path embeddedBinaries = getTemporaryEmbeddedBin(fs);
        BinTools binTools = BinTools.forEmbeddedBin(embeddedBinaries, ImmutableList.of("process-wrapper"));
        copyProcessWrapperIntoExecRoot(binTools.getEmbeddedPath("process-wrapper"));
        Path cpuTimeSpenderPath = copyCpuTimeSpenderIntoExecRoot(execRoot);
        LocalSpawnRunner runner = new LocalSpawnRunner(execRoot, options, resourceManager, LocalSpawnRunnerTest.USE_WRAPPER, OS.LINUX, UNMODIFIED, binTools);
        Spawn spawn = new SpawnBuilder(cpuTimeSpenderPath.getPathString(), String.valueOf(minimumUserTimeToSpend.getSeconds()), String.valueOf(minimumSystemTimeToSpend.getSeconds())).build();
        FileOutErr fileOutErr = new FileOutErr(fs.getPath("/dev/null"), fs.getPath("/dev/null"));
        LocalSpawnRunnerTest.SpawnExecutionContextForTesting policy = new LocalSpawnRunnerTest.SpawnExecutionContextForTesting(fileOutErr);
        SpawnResult spawnResult = runner.execAsync(spawn, policy).get();
        assertThat(spawnResult.status()).isEqualTo(SUCCESS);
        assertThat(spawnResult.exitCode()).isEqualTo(0);
        assertThat(spawnResult.setupSuccess()).isTrue();
        assertThat(spawnResult.getExecutorHostName()).isEqualTo(NetUtil.getCachedShortHostName());
        assertThat(spawnResult.getWallTime()).isPresent();
        assertThat(spawnResult.getWallTime().get()).isAtLeast(minimumWallTimeToSpend);
        // Under heavy starvation, max wall time could be anything, so don't check it here.
        assertThat(spawnResult.getUserTime()).isEmpty();
        assertThat(spawnResult.getSystemTime()).isEmpty();
        assertThat(spawnResult.getNumBlockOutputOperations()).isEmpty();
        assertThat(spawnResult.getNumBlockInputOperations()).isEmpty();
        assertThat(spawnResult.getNumInvoluntaryContextSwitches()).isEmpty();
    }

    // Check that relative paths in the Spawn are absolutized relative to the execroot passed to the
    // LocalSpawnRunner.
    @Test
    public void relativePath() throws Exception {
        // TODO(#3536): Make this test work on Windows.
        // The Command API implicitly absolutizes the path, and we get weird paths on Windows:
        // T:\execroot\execroot\_bin\process-wrapper
        Assume.assumeTrue(((OS.getCurrent()) != (OS.WINDOWS)));
        FileSystem fs = setupEnvironmentForFakeExecution();
        SubprocessFactory factory = Mockito.mock(SubprocessFactory.class);
        ArgumentCaptor<SubprocessBuilder> captor = ArgumentCaptor.forClass(SubprocessBuilder.class);
        Mockito.when(factory.create(captor.capture())).thenReturn(new LocalSpawnRunnerTest.FinishedSubprocess(0));
        SubprocessBuilder.setDefaultSubprocessFactory(factory);
        LocalSpawnRunner runner = new LocalSpawnRunnerTest.TestedLocalSpawnRunner(fs.getPath("/execroot"), fs.getPath("/embedded_bin"), Options.getDefaults(LocalExecutionOptions.class), resourceManager, LocalSpawnRunnerTest.NO_WRAPPER, OS.LINUX, UNMODIFIED);
        FileOutErr fileOutErr = new FileOutErr(fs.getPath("/out/stdout"), fs.getPath("/out/stderr"));
        LocalSpawnRunnerTest.SpawnExecutionContextForTesting policy = new LocalSpawnRunnerTest.SpawnExecutionContextForTesting(fileOutErr);
        assertThat(fs.getPath("/execroot").createDirectory()).isTrue();
        runner.execAsync(new SpawnBuilder("foo/bar", "Hi!").build(), policy).get();
        Mockito.verify(factory).create(any(SubprocessBuilder.class));
        assertThat(captor.getValue().getArgv()).containsExactly("/execroot/foo/bar", "Hi!");
    }
}

