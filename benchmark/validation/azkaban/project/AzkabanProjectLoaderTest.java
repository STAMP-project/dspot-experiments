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
package azkaban.project;


import azkaban.executor.ExecutableFlow;
import azkaban.executor.ExecutorLoader;
import azkaban.executor.ExecutorManagerException;
import azkaban.flow.Flow;
import azkaban.storage.StorageManager;
import azkaban.test.executions.ExecutionsTestUtil;
import azkaban.user.User;
import com.google.common.collect.ImmutableMap;
import java.io.File;
import java.net.URL;
import java.util.Arrays;
import java.util.Objects;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class AzkabanProjectLoaderTest {
    private static final String DIRECTORY_FLOW_REPORT_KEY = "Directory Flow";

    private static final String BASIC_FLOW_YAML_DIR = "basicflowyamltest";

    private static final String BASIC_FLOW_FILE = "basic_flow.flow";

    private static final String PROJECT_ZIP = "Archive.zip";

    @Rule
    public final TemporaryFolder TEMP_DIR = new TemporaryFolder();

    private final int ID = 107;

    private final int VERSION = 10;

    private final Project project = new Project(this.ID, "project1");

    private AzkabanProjectLoader azkabanProjectLoader;

    private StorageManager storageManager;

    private ProjectLoader projectLoader;

    private ExecutorLoader executorLoader;

    @Test
    public void uploadProject() throws ExecutorManagerException {
        Mockito.when(this.projectLoader.getLatestProjectVersion(this.project)).thenReturn(this.VERSION);
        final URL resource = Objects.requireNonNull(getClass().getClassLoader().getResource("sample_flow_01.zip"));
        final File projectZipFile = new File(resource.getPath());
        final User uploader = new User("test_user");
        // to test excluding running versions in args of cleanOlderProjectVersion
        final ExecutableFlow runningFlow = new ExecutableFlow(this.project, new Flow("x"));
        runningFlow.setVersion(this.VERSION);
        Mockito.when(this.executorLoader.fetchUnfinishedFlowsMetadata()).thenReturn(ImmutableMap.of((-1), new azkaban.utils.Pair(null, runningFlow)));
        this.project.setVersion(this.VERSION);
        checkValidationReport(this.azkabanProjectLoader.uploadProject(this.project, projectZipFile, "zip", uploader, null));
        Mockito.verify(this.storageManager).uploadProject(this.project, ((this.VERSION) + 1), projectZipFile, uploader);
        Mockito.verify(this.projectLoader).cleanOlderProjectVersion(this.project.getId(), ((this.VERSION) - 3), Arrays.asList(this.VERSION));
    }

    @Test
    public void getProjectFile() throws Exception {
        Mockito.when(this.projectLoader.getLatestProjectVersion(this.project)).thenReturn(this.VERSION);
        // Run the code
        this.azkabanProjectLoader.getProjectFile(this.project, (-1));
        Mockito.verify(this.projectLoader).getLatestProjectVersion(this.project);
        Mockito.verify(this.storageManager).getProjectFile(this.ID, this.VERSION);
    }

    @Test
    public void uploadProjectWithYamlFiles() throws Exception {
        final File projectZipFile = ExecutionsTestUtil.getFlowFile(AzkabanProjectLoaderTest.BASIC_FLOW_YAML_DIR, AzkabanProjectLoaderTest.PROJECT_ZIP);
        final int flowVersion = 0;
        final User uploader = new User("test_user");
        Mockito.when(this.projectLoader.getLatestProjectVersion(this.project)).thenReturn(this.VERSION);
        Mockito.when(this.projectLoader.getLatestFlowVersion(this.ID, this.VERSION, AzkabanProjectLoaderTest.BASIC_FLOW_FILE)).thenReturn(flowVersion);
        checkValidationReport(this.azkabanProjectLoader.uploadProject(this.project, projectZipFile, "zip", uploader, null));
        Mockito.verify(this.storageManager).uploadProject(this.project, ((this.VERSION) + 1), projectZipFile, uploader);
        Mockito.verify(this.projectLoader).uploadFlowFile(ArgumentMatchers.eq(this.ID), ArgumentMatchers.eq(((this.VERSION) + 1)), ArgumentMatchers.any(File.class), ArgumentMatchers.eq((flowVersion + 1)));
    }
}

