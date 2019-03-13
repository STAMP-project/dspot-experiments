/**
 * Copyright 2017 LinkedIn Corp.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package azkaban.execapp;


import FlowPreparer.PROJECT_DIR_SIZE_FILE_NAME;
import azkaban.executor.ExecutableFlow;
import azkaban.executor.ExecutorManagerException;
import azkaban.utils.FileIOUtils;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mockito;


public class FlowPreparerTest {
    public static final String SAMPLE_FLOW_01 = "sample_flow_01";

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    private File executionsDir;

    private File projectsDir;

    private FlowPreparer instance;

    @Test
    public void testProjectDirSizeIsSet() throws Exception {
        final ProjectDirectoryMetadata proj = new ProjectDirectoryMetadata(12, 34, new File(this.projectsDir, FlowPreparerTest.SAMPLE_FLOW_01));
        final File tmp = this.instance.downloadProjectIfNotExists(proj, 123);
        final long actualDirSize = 1048835;
        assertThat(proj.getDirSizeInByte()).isEqualTo(actualDirSize);
        assertThat(FileIOUtils.readNumberFromFile(Paths.get(tmp.getPath(), PROJECT_DIR_SIZE_FILE_NAME))).isEqualTo(actualDirSize);
    }

    @Test
    public void testDownloadingProjectIfNotExists() throws Exception {
        final ProjectDirectoryMetadata proj = new ProjectDirectoryMetadata(12, 34, new File(this.projectsDir, FlowPreparerTest.SAMPLE_FLOW_01));
        final File tmp = this.instance.downloadProjectIfNotExists(proj, 124);
        final Path projectDirSizeFile = Paths.get(proj.getInstalledDir().getPath(), PROJECT_DIR_SIZE_FILE_NAME);
        Mockito.verify(this.instance, Mockito.never()).updateLastModifiedTime(projectDirSizeFile);
        assertThat(tmp).isNotNull();
        assertThat(tmp.list()).contains(FlowPreparerTest.SAMPLE_FLOW_01);
    }

    @Test
    public void testNotDownloadingProjectIfExists() throws Exception {
        final ProjectDirectoryMetadata proj = new ProjectDirectoryMetadata(12, 34, new File(this.projectsDir, FlowPreparerTest.SAMPLE_FLOW_01));
        File tmp = this.instance.downloadProjectIfNotExists(proj, 125);
        Files.move(tmp.toPath(), proj.getInstalledDir().toPath());
        // Try downloading the same project again
        tmp = this.instance.downloadProjectIfNotExists(proj, 126);
        final Path projectDirSizeFile = Paths.get(proj.getInstalledDir().getPath(), PROJECT_DIR_SIZE_FILE_NAME);
        Mockito.verify(this.instance).updateLastModifiedTime(projectDirSizeFile);
        assertThat(tmp).isNull();
    }

    @Test
    public void testSetupFlowByMultipleThreads() {
        final int threadNum = 4;
        final ExecutableFlow[] executableFlows = new ExecutableFlow[]{ mockExecutableFlow(1, 12, 34), mockExecutableFlow(2, 12, 34), mockExecutableFlow(3, 12, 34), mockExecutableFlow(4, 12, 34) };
        final ExecutorService service = Executors.newFixedThreadPool(threadNum);
        final List<Future> futures = new ArrayList<>();
        for (int i = 0; i < threadNum; i++) {
            final int finalI = i;
            futures.add(service.submit(() -> {
                assertThatCode(() -> this.instance.setup(executableFlows[finalI])).doesNotThrowAnyException();
            }));
        }
        for (final Future future : futures) {
            assertThatCode(() -> future.get()).doesNotThrowAnyException();
        }
        service.shutdownNow();
        for (final ExecutableFlow flow : executableFlows) {
            final File execDir = new File(this.executionsDir, String.valueOf(flow.getExecutionId()));
            Assert.assertTrue(execDir.exists());
            Assert.assertTrue(new File(execDir, FlowPreparerTest.SAMPLE_FLOW_01).exists());
        }
    }

    @Test
    public void testSetupFlow() throws ExecutorManagerException {
        final ExecutableFlow executableFlow = Mockito.mock(ExecutableFlow.class);
        Mockito.when(executableFlow.getExecutionId()).thenReturn(12345);
        Mockito.when(executableFlow.getProjectId()).thenReturn(12);
        Mockito.when(executableFlow.getVersion()).thenReturn(34);
        this.instance.setup(executableFlow);
        final File execDir = new File(this.executionsDir, "12345");
        Assert.assertTrue(execDir.exists());
        Assert.assertTrue(new File(execDir, FlowPreparerTest.SAMPLE_FLOW_01).exists());
    }
}

