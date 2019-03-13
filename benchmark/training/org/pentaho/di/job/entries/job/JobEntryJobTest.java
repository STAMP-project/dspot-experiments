/**
 * ! ******************************************************************************
 *
 * Pentaho Data Integration
 *
 * Copyright (C) 2002-2018 by Hitachi Vantara : http://www.pentaho.com
 *
 * ******************************************************************************
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * ****************************************************************************
 */
package org.pentaho.di.job.entries.job;


import ObjectLocationSpecificationMethod.FILENAME;
import ObjectLocationSpecificationMethod.REPOSITORY_BY_NAME;
import ObjectLocationSpecificationMethod.REPOSITORY_BY_REFERENCE;
import java.util.List;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.pentaho.di.cluster.SlaveServer;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.util.CurrentDirectoryResolver;
import org.pentaho.di.core.variables.VariableSpace;
import org.pentaho.di.job.JobMeta;
import org.pentaho.di.repository.ObjectId;
import org.pentaho.di.repository.Repository;
import org.pentaho.di.repository.RepositoryDirectoryInterface;
import org.pentaho.di.repository.StringObjectId;
import org.pentaho.di.resource.ResourceNamingInterface;
import org.pentaho.metastore.api.IMetaStore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;


@RunWith(PowerMockRunner.class)
@PrepareForTest(JobEntryJob.class)
public class JobEntryJobTest {
    private final String JOB_ENTRY_JOB_NAME = "My Job";

    private final StringObjectId JOB_ENTRY_JOB_OBJECT_ID = new StringObjectId("00x1");

    private final String JOB_ENTRY_FILE_NAME = "job.kjb";

    private final String JOB_ENTRY_FILE_DIRECTORY = "/public/test";

    private final String JOB_ENTRY_FILE_PATH = "/home/ljm/job.kjb";

    private final String JOB_ENTRY_DESCRIPTION = "This is yet another job";

    private Repository repository = Mockito.mock(Repository.class);

    private List<DatabaseMeta> databases = Mockito.mock(List.class);

    private List<SlaveServer> servers = Mockito.mock(List.class);

    private IMetaStore store = Mockito.mock(IMetaStore.class);

    private VariableSpace space = Mockito.mock(VariableSpace.class);

    private CurrentDirectoryResolver resolver = Mockito.mock(CurrentDirectoryResolver.class);

    private RepositoryDirectoryInterface rdi = Mockito.mock(RepositoryDirectoryInterface.class);

    private RepositoryDirectoryInterface directory = Mockito.mock(RepositoryDirectoryInterface.class);

    /**
     * When disconnected from the repository and {@link JobEntryJob} contains no info,
     * default to {@link ObjectLocationSpecificationMethod}.{@code FILENAME}
     */
    @Test
    public void testNotConnectedLoad_NoInfo() throws Exception {
        JobEntryJob jej = Mockito.spy(new JobEntryJob(JOB_ENTRY_JOB_NAME));
        jej.loadXML(getNode(jej), databases, servers, null, store);
        Assert.assertEquals(FILENAME, jej.getSpecificationMethod());
    }

    /**
     * When disconnected from the repository and {@link JobEntryJob} references a child job by {@link ObjectId},
     * this reference will be invalid to run such job.
     * Default to {@link ObjectLocationSpecificationMethod}.{@code FILENAME} with a {@code null} file path.
     */
    @Test
    public void testNotConnectedLoad_RepByRef() throws Exception {
        JobEntryJob jej = Mockito.spy(new JobEntryJob(JOB_ENTRY_JOB_NAME));
        jej.setSpecificationMethod(REPOSITORY_BY_REFERENCE);
        jej.setJobObjectId(JOB_ENTRY_JOB_OBJECT_ID);
        jej.loadXML(getNode(jej), databases, servers, null, store);
        jej.getJobMeta(null, store, space);
        Assert.assertEquals(FILENAME, jej.getSpecificationMethod());
        verifyNew(JobMeta.class).withArguments(space, null, null, store, null);
    }

    /**
     * When disconnected from the repository and {@link JobEntryJob} references a child job by name,
     * this reference will be invalid to run such job.
     * Default to {@link ObjectLocationSpecificationMethod}.{@code FILENAME} with a {@code null} file path.
     */
    @Test
    public void testNotConnectedLoad_RepByName() throws Exception {
        JobEntryJob jej = Mockito.spy(new JobEntryJob(JOB_ENTRY_JOB_NAME));
        jej.setSpecificationMethod(REPOSITORY_BY_NAME);
        jej.setJobName(JOB_ENTRY_FILE_NAME);
        jej.setDirectory(JOB_ENTRY_FILE_DIRECTORY);
        jej.loadXML(getNode(jej), databases, servers, null, store);
        jej.getJobMeta(null, store, space);
        Assert.assertEquals(FILENAME, jej.getSpecificationMethod());
        verifyNew(JobMeta.class).withArguments(space, null, null, store, null);
    }

    /**
     * When disconnected from the repository and {@link JobEntryJob} references a child job by file path,
     * {@link ObjectLocationSpecificationMethod} will be {@code FILENAME}.
     */
    @Test
    public void testNotConnectedLoad_Filename() throws Exception {
        JobEntryJob jej = Mockito.spy(new JobEntryJob(JOB_ENTRY_JOB_NAME));
        jej.setSpecificationMethod(FILENAME);
        jej.setFileName(JOB_ENTRY_FILE_PATH);
        jej.loadXML(getNode(jej), databases, servers, null, store);
        jej.getJobMeta(null, store, space);
        Assert.assertEquals(FILENAME, jej.getSpecificationMethod());
        verifyNew(JobMeta.class).withArguments(space, JOB_ENTRY_FILE_PATH, null, store, null);
    }

    /**
     * When connected to the repository and {@link JobEntryJob} contains no info,
     * default to {@link ObjectLocationSpecificationMethod}.{@code REPOSITORY_BY_NAME}
     */
    @Test
    public void testConnectedImport_NoInfo() throws Exception {
        JobEntryJob jej = Mockito.spy(new JobEntryJob(JOB_ENTRY_JOB_NAME));
        jej.loadXML(getNode(jej), databases, servers, repository, store);
        Assert.assertEquals(REPOSITORY_BY_NAME, jej.getSpecificationMethod());
    }

    /**
     * When connected to the repository and {@link JobEntryJob} references a child job by {@link ObjectId},
     * keep {@link ObjectLocationSpecificationMethod} as {@code REPOSITORY_BY_REFERENCE}.
     * Load the job from the repository using the specified {@link ObjectId}.
     */
    @Test
    public void testConnectedImport_RepByRef() throws Exception {
        JobEntryJob jej = Mockito.spy(new JobEntryJob(JOB_ENTRY_JOB_NAME));
        jej.setSpecificationMethod(REPOSITORY_BY_REFERENCE);
        jej.setJobObjectId(JOB_ENTRY_JOB_OBJECT_ID);
        jej.loadXML(getNode(jej), databases, servers, repository, store);
        jej.getJobMeta(repository, store, space);
        Assert.assertEquals(REPOSITORY_BY_REFERENCE, jej.getSpecificationMethod());
        Mockito.verify(repository, Mockito.times(1)).loadJob(JOB_ENTRY_JOB_OBJECT_ID, null);
    }

    /**
     * When connected to the repository and {@link JobEntryJob} references a child job by name,
     * keep {@link ObjectLocationSpecificationMethod} as {@code REPOSITORY_BY_NAME}.
     * Load the job from the repository using the specified job name and directory.
     */
    @Test
    public void testConnectedImport_RepByName() throws Exception {
        JobEntryJob jej = Mockito.spy(new JobEntryJob(JOB_ENTRY_JOB_NAME));
        jej.setSpecificationMethod(REPOSITORY_BY_NAME);
        jej.setJobName(JOB_ENTRY_FILE_NAME);
        jej.setDirectory(JOB_ENTRY_FILE_DIRECTORY);
        jej.loadXML(getNode(jej), databases, servers, repository, store);
        jej.getJobMeta(repository, store, space);
        Assert.assertEquals(REPOSITORY_BY_NAME, jej.getSpecificationMethod());
        Mockito.verify(repository, Mockito.times(1)).loadJob(JOB_ENTRY_FILE_NAME, directory, null, null);
    }

    /**
     * When connected to the repository and {@link JobEntryJob} references a child job by file path,
     * keep {@link ObjectLocationSpecificationMethod} as {@code FILENAME}.
     * Load the job from the repository using the specified file path.
     */
    @Test
    public void testConnectedImport_Filename() throws Exception {
        JobEntryJob jej = Mockito.spy(new JobEntryJob(JOB_ENTRY_JOB_NAME));
        jej.setSpecificationMethod(FILENAME);
        jej.setFileName(JOB_ENTRY_FILE_PATH);
        jej.loadXML(getNode(jej), databases, servers, repository, store);
        jej.getJobMeta(repository, store, space);
        Assert.assertEquals(FILENAME, jej.getSpecificationMethod());
        verifyNew(JobMeta.class).withArguments(space, JOB_ENTRY_FILE_PATH, repository, store, null);
    }

    /**
     * When connected to the repository and {@link JobEntryJob} references a child job by {@link ObjectId},
     * guess {@link ObjectLocationSpecificationMethod} as {@code REPOSITORY_BY_REFERENCE}.
     * Load the job from the repository using the specified {@link ObjectId}.
     */
    @Test
    public void testConnectedImport_RepByRef_Guess() throws Exception {
        JobEntryJob jej = Mockito.spy(new JobEntryJob(JOB_ENTRY_JOB_NAME));
        jej.setJobObjectId(JOB_ENTRY_JOB_OBJECT_ID);
        jej.loadXML(getNode(jej), databases, servers, repository, store);
        jej.getJobMeta(repository, store, space);
        Assert.assertEquals(REPOSITORY_BY_REFERENCE, jej.getSpecificationMethod());
        Mockito.verify(repository, Mockito.times(1)).loadJob(JOB_ENTRY_JOB_OBJECT_ID, null);
    }

    /**
     * When connected to the repository and {@link JobEntryJob} references a child job by name,
     * keep {@link ObjectLocationSpecificationMethod} as {@code REPOSITORY_BY_NAME}.
     * Load the job from the repository using the specified job name and directory.
     */
    @Test
    public void testConnectedImport_RepByName_Guess() throws Exception {
        JobEntryJob jej = Mockito.spy(new JobEntryJob(JOB_ENTRY_JOB_NAME));
        jej.setJobName(JOB_ENTRY_FILE_NAME);
        jej.setDirectory(JOB_ENTRY_FILE_DIRECTORY);
        jej.loadXML(getNode(jej), databases, servers, repository, store);
        jej.getJobMeta(repository, store, space);
        Assert.assertEquals(REPOSITORY_BY_NAME, jej.getSpecificationMethod());
        Mockito.verify(repository, Mockito.times(1)).loadJob(JOB_ENTRY_FILE_NAME, directory, null, null);
    }

    /**
     * When connected to the repository and {@link JobEntryJob} references a child job by file path,
     * guess {@link ObjectLocationSpecificationMethod} as {@code FILENAME}.
     * Load the job from the repository using the specified file path.
     */
    @Test
    public void testConnectedImport_Filename_Guess() throws Exception {
        JobEntryJob jej = Mockito.spy(new JobEntryJob(JOB_ENTRY_JOB_NAME));
        jej.setFileName(JOB_ENTRY_FILE_PATH);
        jej.loadXML(getNode(jej), databases, servers, repository, store);
        jej.getJobMeta(repository, store, space);
        Assert.assertEquals(FILENAME, jej.getSpecificationMethod());
        verifyNew(JobMeta.class).withArguments(space, JOB_ENTRY_FILE_PATH, repository, store, null);
    }

    /**
     * When connected to the repository and {@link JobEntryJob} contains no info,
     * default to {@link ObjectLocationSpecificationMethod}.{@code REPOSITORY_BY_NAME}
     */
    @Test
    public void testConnectedLoad_NoInfo() throws Exception {
        JobEntryJob jej = Mockito.spy(new JobEntryJob(JOB_ENTRY_JOB_NAME));
        jej.loadRep(repository, store, JOB_ENTRY_JOB_OBJECT_ID, databases, servers);
        Assert.assertEquals(REPOSITORY_BY_NAME, jej.getSpecificationMethod());
    }

    /**
     * When connected to the repository and {@link JobEntryJob} references a child job by {@link ObjectId},
     * keep {@link ObjectLocationSpecificationMethod} as {@code REPOSITORY_BY_REFERENCE}.
     * Load the job from the repository using the specified {@link ObjectId}.
     */
    @Test
    public void testConnectedLoad_RepByRef() throws Exception {
        Repository myrepo = Mockito.mock(Repository.class);
        Mockito.doReturn(true).when(myrepo).isConnected();
        Mockito.doReturn(null).when(myrepo).getJobEntryAttributeString(ArgumentMatchers.any(ObjectId.class), ArgumentMatchers.anyString());
        Mockito.doReturn("rep_ref").when(myrepo).getJobEntryAttributeString(JOB_ENTRY_JOB_OBJECT_ID, "specification_method");
        Mockito.doReturn(JOB_ENTRY_JOB_OBJECT_ID.toString()).when(myrepo).getJobEntryAttributeString(JOB_ENTRY_JOB_OBJECT_ID, "job_object_id");
        JobEntryJob jej = Mockito.spy(new JobEntryJob(JOB_ENTRY_JOB_NAME));
        jej.loadRep(myrepo, store, JOB_ENTRY_JOB_OBJECT_ID, databases, servers);
        jej.getJobMeta(myrepo, store, space);
        Assert.assertEquals(REPOSITORY_BY_REFERENCE, jej.getSpecificationMethod());
        Mockito.verify(myrepo, Mockito.times(1)).loadJob(JOB_ENTRY_JOB_OBJECT_ID, null);
    }

    /**
     * When connected to the repository and {@link JobEntryJob} references a child job by name,
     * keep {@link ObjectLocationSpecificationMethod} as {@code REPOSITORY_BY_NAME}.
     * Load the job from the repository using the specified job name and directory.
     */
    @Test
    public void testConnectedLoad_RepByName() throws Exception {
        Repository myrepo = Mockito.mock(Repository.class);
        Mockito.doReturn(true).when(myrepo).isConnected();
        Mockito.doReturn(rdi).when(myrepo).loadRepositoryDirectoryTree();
        Mockito.doReturn(null).when(myrepo).getJobEntryAttributeString(ArgumentMatchers.any(ObjectId.class), ArgumentMatchers.anyString());
        Mockito.doReturn("rep_name").when(myrepo).getJobEntryAttributeString(JOB_ENTRY_JOB_OBJECT_ID, "specification_method");
        Mockito.doReturn(JOB_ENTRY_FILE_NAME).when(myrepo).getJobEntryAttributeString(JOB_ENTRY_JOB_OBJECT_ID, "name");
        Mockito.doReturn(JOB_ENTRY_FILE_DIRECTORY).when(myrepo).getJobEntryAttributeString(JOB_ENTRY_JOB_OBJECT_ID, "dir_path");
        JobEntryJob jej = Mockito.spy(new JobEntryJob(JOB_ENTRY_JOB_NAME));
        jej.loadRep(myrepo, store, JOB_ENTRY_JOB_OBJECT_ID, databases, servers);
        jej.getJobMeta(myrepo, store, space);
        Assert.assertEquals(REPOSITORY_BY_NAME, jej.getSpecificationMethod());
        Mockito.verify(myrepo, Mockito.times(1)).loadJob(JOB_ENTRY_FILE_NAME, directory, null, null);
    }

    /**
     * When connected to the repository and {@link JobEntryJob} references a child job by file path,
     * keep {@link ObjectLocationSpecificationMethod} as {@code FILENAME}.
     * Load the job from the repository using the specified file path.
     */
    @Test
    public void testConnectedLoad_Filename() throws Exception {
        Repository myrepo = Mockito.mock(Repository.class);
        Mockito.doReturn(true).when(myrepo).isConnected();
        Mockito.doReturn(null).when(myrepo).getJobEntryAttributeString(ArgumentMatchers.any(ObjectId.class), ArgumentMatchers.anyString());
        Mockito.doReturn("filename").when(myrepo).getJobEntryAttributeString(JOB_ENTRY_JOB_OBJECT_ID, "specification_method");
        Mockito.doReturn(JOB_ENTRY_FILE_PATH).when(myrepo).getJobEntryAttributeString(JOB_ENTRY_JOB_OBJECT_ID, "file_name");
        JobEntryJob jej = Mockito.spy(new JobEntryJob(JOB_ENTRY_JOB_NAME));
        jej.loadRep(myrepo, store, JOB_ENTRY_JOB_OBJECT_ID, databases, servers);
        jej.getJobMeta(myrepo, store, space);
        Assert.assertEquals(FILENAME, jej.getSpecificationMethod());
        verifyNew(JobMeta.class).withArguments(space, JOB_ENTRY_FILE_PATH, myrepo, store, null);
    }

    /**
     * When connected to the repository and {@link JobEntryJob} references a child job by name,
     * keep {@link ObjectLocationSpecificationMethod} as {@code REPOSITORY_BY_NAME}.
     * Load the job from the repository using the specified job name and directory.
     */
    @Test
    public void testConnectedLoad_RepByName_HDFS() throws Exception {
        Repository myrepo = Mockito.mock(Repository.class);
        Mockito.doReturn(true).when(myrepo).isConnected();
        Mockito.doReturn(null).when(myrepo).getJobEntryAttributeString(ArgumentMatchers.any(ObjectId.class), ArgumentMatchers.anyString());
        Mockito.doReturn("rep_name").when(myrepo).getJobEntryAttributeString(JOB_ENTRY_JOB_OBJECT_ID, "specification_method");
        Mockito.doReturn("job").when(myrepo).getJobEntryAttributeString(JOB_ENTRY_JOB_OBJECT_ID, "name");
        Mockito.doReturn("${hdfs}").when(myrepo).getJobEntryAttributeString(JOB_ENTRY_JOB_OBJECT_ID, "dir_path");
        JobEntryJob jej = Mockito.spy(new JobEntryJob(JOB_ENTRY_JOB_NAME));
        jej.loadRep(myrepo, store, JOB_ENTRY_JOB_OBJECT_ID, databases, servers);
        jej.getJobMeta(myrepo, store, space);
        Assert.assertEquals(REPOSITORY_BY_NAME, jej.getSpecificationMethod());
        verifyNew(JobMeta.class).withArguments(space, "hdfs://server/path/job.kjb", myrepo, store, null);
    }

    /**
     * When connected to the repository and {@link JobEntryJob} references a child job by name using a single parameter,
     * keep {@link ObjectLocationSpecificationMethod} as {@code REPOSITORY_BY_NAME}.
     * Load the job from the repository using the specified job name and directory.
     */
    @Test
    public void testConnectedLoad_RepByName_SingleParameter() throws Exception {
        Repository myrepo = Mockito.mock(Repository.class);
        Mockito.doReturn(true).when(myrepo).isConnected();
        Mockito.doReturn(rdi).when(myrepo).loadRepositoryDirectoryTree();
        Mockito.doReturn(null).when(myrepo).getJobEntryAttributeString(ArgumentMatchers.any(ObjectId.class), ArgumentMatchers.anyString());
        Mockito.doReturn("rep_name").when(myrepo).getJobEntryAttributeString(JOB_ENTRY_JOB_OBJECT_ID, "specification_method");
        Mockito.doReturn("${repositoryfullfilepath}").when(myrepo).getJobEntryAttributeString(JOB_ENTRY_JOB_OBJECT_ID, "name");
        JobEntryJob jej = Mockito.spy(new JobEntryJob(JOB_ENTRY_JOB_NAME));
        jej.loadRep(myrepo, store, JOB_ENTRY_JOB_OBJECT_ID, databases, servers);
        jej.getJobMeta(myrepo, store, space);
        Assert.assertEquals(REPOSITORY_BY_NAME, jej.getSpecificationMethod());
        Mockito.verify(myrepo, Mockito.times(1)).loadJob("job.kjb", directory, null, null);
    }

    /**
     * When connected to the repository and {@link JobEntryJob} references a child job by name using multiple parameters,
     * keep {@link ObjectLocationSpecificationMethod} as {@code REPOSITORY_BY_NAME}.
     * Load the job from the repository using the specified job name and directory.
     */
    @Test
    public void testConnectedLoad_RepByName_MultipleParameters() throws Exception {
        Repository myrepo = Mockito.mock(Repository.class);
        Mockito.doReturn(true).when(myrepo).isConnected();
        Mockito.doReturn(rdi).when(myrepo).loadRepositoryDirectoryTree();
        Mockito.doReturn(null).when(myrepo).getJobEntryAttributeString(ArgumentMatchers.any(ObjectId.class), ArgumentMatchers.anyString());
        Mockito.doReturn("rep_name").when(myrepo).getJobEntryAttributeString(JOB_ENTRY_JOB_OBJECT_ID, "specification_method");
        Mockito.doReturn("${jobname}").when(myrepo).getJobEntryAttributeString(JOB_ENTRY_JOB_OBJECT_ID, "name");
        Mockito.doReturn("${repositorypath}").when(myrepo).getJobEntryAttributeString(JOB_ENTRY_JOB_OBJECT_ID, "dir_path");
        JobEntryJob jej = Mockito.spy(new JobEntryJob(JOB_ENTRY_JOB_NAME));
        jej.loadRep(myrepo, store, JOB_ENTRY_JOB_OBJECT_ID, databases, servers);
        jej.getJobMeta(myrepo, store, space);
        Assert.assertEquals(REPOSITORY_BY_NAME, jej.getSpecificationMethod());
        Mockito.verify(myrepo, Mockito.times(1)).loadJob("job.kjb", directory, null, null);
    }

    /**
     * When connected to the repository and {@link JobEntryJob} references a child job by {@link ObjectId},
     * guess {@link ObjectLocationSpecificationMethod} as {@code REPOSITORY_BY_REFERENCE}.
     * Load the job from the repository using the specified {@link ObjectId}.
     */
    @Test
    public void testConnectedLoad_RepByRef_Guess() throws Exception {
        Repository myrepo = Mockito.mock(Repository.class);
        Mockito.doReturn(true).when(myrepo).isConnected();
        Mockito.doReturn(null).when(myrepo).getJobEntryAttributeString(ArgumentMatchers.any(ObjectId.class), ArgumentMatchers.anyString());
        Mockito.doReturn(JOB_ENTRY_JOB_OBJECT_ID.toString()).when(myrepo).getJobEntryAttributeString(JOB_ENTRY_JOB_OBJECT_ID, "job_object_id");
        JobEntryJob jej = Mockito.spy(new JobEntryJob(JOB_ENTRY_JOB_NAME));
        jej.loadRep(myrepo, store, JOB_ENTRY_JOB_OBJECT_ID, databases, servers);
        jej.getJobMeta(myrepo, store, space);
        Assert.assertEquals(REPOSITORY_BY_REFERENCE, jej.getSpecificationMethod());
        Mockito.verify(myrepo, Mockito.times(1)).loadJob(JOB_ENTRY_JOB_OBJECT_ID, null);
    }

    /**
     * When connected to the repository and {@link JobEntryJob} references a child job by name,
     * guess {@link ObjectLocationSpecificationMethod} as {@code REPOSITORY_BY_NAME}.
     * Load the job from the repository using the specified job name and directory.
     */
    @Test
    public void testConnectedLoad_RepByName_Guess() throws Exception {
        Repository myrepo = Mockito.mock(Repository.class);
        Mockito.doReturn(true).when(myrepo).isConnected();
        Mockito.doReturn(rdi).when(myrepo).loadRepositoryDirectoryTree();
        Mockito.doReturn(null).when(myrepo).getJobEntryAttributeString(ArgumentMatchers.any(ObjectId.class), ArgumentMatchers.anyString());
        Mockito.doReturn(JOB_ENTRY_FILE_NAME).when(myrepo).getJobEntryAttributeString(JOB_ENTRY_JOB_OBJECT_ID, "name");
        Mockito.doReturn(JOB_ENTRY_FILE_DIRECTORY).when(myrepo).getJobEntryAttributeString(JOB_ENTRY_JOB_OBJECT_ID, "dir_path");
        JobEntryJob jej = Mockito.spy(new JobEntryJob(JOB_ENTRY_JOB_NAME));
        jej.loadRep(myrepo, store, JOB_ENTRY_JOB_OBJECT_ID, databases, servers);
        jej.getJobMeta(myrepo, store, space);
        Assert.assertEquals(REPOSITORY_BY_NAME, jej.getSpecificationMethod());
        Mockito.verify(myrepo, Mockito.times(1)).loadJob(JOB_ENTRY_FILE_NAME, directory, null, null);
    }

    /**
     * When connected to the repository and {@link JobEntryJob} references a child job by file path,
     * guess {@link ObjectLocationSpecificationMethod} as {@code FILENAME}.
     * Load the job from the repository using the specified file path.
     */
    @Test
    public void testConnectedLoad_Filename_Guess() throws Exception {
        Repository myrepo = Mockito.mock(Repository.class);
        Mockito.doReturn(true).when(myrepo).isConnected();
        Mockito.doReturn(null).when(myrepo).getJobEntryAttributeString(ArgumentMatchers.any(ObjectId.class), ArgumentMatchers.anyString());
        Mockito.doReturn(JOB_ENTRY_FILE_PATH).when(myrepo).getJobEntryAttributeString(JOB_ENTRY_JOB_OBJECT_ID, "file_name");
        JobEntryJob jej = Mockito.spy(new JobEntryJob(JOB_ENTRY_JOB_NAME));
        jej.loadRep(myrepo, store, JOB_ENTRY_JOB_OBJECT_ID, databases, servers);
        jej.getJobMeta(myrepo, store, space);
        Assert.assertEquals(FILENAME, jej.getSpecificationMethod());
        verifyNew(JobMeta.class).withArguments(space, JOB_ENTRY_FILE_PATH, myrepo, store, null);
    }

    @Test
    public void testCurrDirListener() throws Exception {
        JobMeta meta = Mockito.mock(JobMeta.class);
        JobEntryJob jej = new JobEntryJob(JOB_ENTRY_JOB_NAME);
        jej.setParentJobMeta(null);
        jej.setParentJobMeta(meta);
        jej.setParentJobMeta(null);
        Mockito.verify(meta, Mockito.times(1)).addCurrentDirectoryChangedListener(ArgumentMatchers.any());
        Mockito.verify(meta, Mockito.times(1)).removeCurrentDirectoryChangedListener(ArgumentMatchers.any());
    }

    @Test
    public void testExportResources() throws Exception {
        JobMeta meta = Mockito.mock(JobMeta.class);
        JobEntryJob jej = Mockito.spy(new JobEntryJob(JOB_ENTRY_JOB_NAME));
        jej.setDescription(JOB_ENTRY_DESCRIPTION);
        Mockito.doReturn(meta).when(jej).getJobMeta(ArgumentMatchers.any(Repository.class), ArgumentMatchers.any(IMetaStore.class), ArgumentMatchers.any(VariableSpace.class));
        Mockito.doReturn(JOB_ENTRY_JOB_NAME).when(meta).exportResources(ArgumentMatchers.any(JobMeta.class), ArgumentMatchers.any(Map.class), ArgumentMatchers.any(ResourceNamingInterface.class), ArgumentMatchers.any(Repository.class), ArgumentMatchers.any(IMetaStore.class));
        jej.exportResources(null, null, null, null, null);
        Mockito.verify(meta).setFilename(((("${" + (Const.INTERNAL_VARIABLE_ENTRY_CURRENT_DIRECTORY)) + "}/") + (JOB_ENTRY_JOB_NAME)));
        Mockito.verify(jej).setSpecificationMethod(FILENAME);
    }
}

