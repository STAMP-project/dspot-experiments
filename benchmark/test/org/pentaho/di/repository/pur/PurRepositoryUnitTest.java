/**
 * !
 * Copyright 2010 - 2018 Hitachi Vantara.  All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.pentaho.di.repository.pur;


import Const.KETTLE_GLOBAL_LOG_VARIABLES_CLEAR_ON_EXPORT;
import Import.ROOT_DIRECTORY;
import KettleExtensionPoint.TransImportAfterSaveToRepo.id;
import RepositoryObjectType.JOB;
import RepositoryObjectType.TRANSFORMATION;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.hamcrest.core.Is;
import org.hamcrest.core.IsInstanceOf;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.extension.ExtensionPointInterface;
import org.pentaho.di.core.extension.ExtensionPointPluginType;
import org.pentaho.di.core.logging.JobEntryLogTable;
import org.pentaho.di.core.logging.LogChannelInterface;
import org.pentaho.di.core.logging.LogTableInterface;
import org.pentaho.di.core.logging.StepLogTable;
import org.pentaho.di.core.plugins.ClassLoadingPluginInterface;
import org.pentaho.di.core.plugins.PluginInterface;
import org.pentaho.di.core.plugins.PluginRegistry;
import org.pentaho.di.core.variables.VariableSpace;
import org.pentaho.di.repository.ObjectId;
import org.pentaho.di.repository.RepositoryDirectory;
import org.pentaho.di.repository.RepositoryDirectoryInterface;
import org.pentaho.di.repository.RepositoryElementInterface;
import org.pentaho.di.repository.RepositoryElementMetaInterface;
import org.pentaho.di.repository.RepositoryObjectType;
import org.pentaho.di.repository.RepositoryTestLazySupport;
import org.pentaho.di.repository.StringObjectId;
import org.pentaho.di.repository.pur.model.EERepositoryObject;
import org.pentaho.di.trans.HasDatabasesInterface;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.ui.repository.pur.services.ILockService;
import org.pentaho.platform.api.repository2.unified.IRepositoryFileData;
import org.pentaho.platform.api.repository2.unified.IUnifiedRepository;
import org.pentaho.platform.api.repository2.unified.RepositoryFile;
import org.pentaho.platform.api.repository2.unified.RepositoryFileTree;
import org.pentaho.platform.api.repository2.unified.RepositoryRequest;
import org.pentaho.platform.repository2.ClientRepositoryPaths;


public class PurRepositoryUnitTest extends RepositoryTestLazySupport {
    private VariableSpace mockedVariableSpace;

    private HasDatabasesInterface mockedHasDbInterface;

    public PurRepositoryUnitTest(Boolean lazyRepo) {
        super(lazyRepo);
    }

    @Test
    public void testGetObjectInformationGetsAclByFileId() throws KettleException {
        PurRepository purRepository = new PurRepository();
        IUnifiedRepository mockRepo = Mockito.mock(IUnifiedRepository.class);
        RepositoryConnectResult result = Mockito.mock(RepositoryConnectResult.class);
        Mockito.when(result.getUnifiedRepository()).thenReturn(mockRepo);
        RepositoryServiceRegistry registry = Mockito.mock(RepositoryServiceRegistry.class);
        UnifiedRepositoryLockService lockService = new UnifiedRepositoryLockService(mockRepo);
        Mockito.when(registry.getService(ILockService.class)).thenReturn(lockService);
        Mockito.when(result.repositoryServiceRegistry()).thenReturn(registry);
        IRepositoryConnector connector = Mockito.mock(IRepositoryConnector.class);
        Mockito.when(connector.connect(ArgumentMatchers.anyString(), ArgumentMatchers.anyString())).thenReturn(result);
        PurRepositoryMeta mockMeta = Mockito.mock(PurRepositoryMeta.class);
        purRepository.init(mockMeta);
        purRepository.setPurRepositoryConnector(connector);
        // purRepository.setTest( mockRepo );
        ObjectId objectId = Mockito.mock(ObjectId.class);
        RepositoryFile mockFile = Mockito.mock(RepositoryFile.class);
        RepositoryFile mockRootFolder = Mockito.mock(RepositoryFile.class);
        RepositoryObjectType repositoryObjectType = RepositoryObjectType.TRANSFORMATION;
        RepositoryFileTree mockRepositoryTree = Mockito.mock(RepositoryFileTree.class);
        String testId = "TEST_ID";
        String testFileId = "TEST_FILE_ID";
        Mockito.when(objectId.getId()).thenReturn(testId);
        Mockito.when(mockRepo.getFileById(testId)).thenReturn(mockFile);
        Mockito.when(mockFile.getPath()).thenReturn("/home/testuser/path.ktr");
        Mockito.when(mockFile.isLocked()).thenReturn(false);
        Mockito.when(mockFile.getId()).thenReturn(testFileId);
        Mockito.when(mockRepo.getTree(ArgumentMatchers.anyString(), ArgumentMatchers.anyInt(), ArgumentMatchers.anyString(), ArgumentMatchers.anyBoolean())).thenReturn(mockRepositoryTree);
        Mockito.when(mockRepositoryTree.getFile()).thenReturn(mockRootFolder);
        Mockito.when(mockRootFolder.getId()).thenReturn("/");
        Mockito.when(mockRootFolder.getPath()).thenReturn("/");
        Mockito.when(mockRepo.getFile("/")).thenReturn(mockRootFolder);
        purRepository.connect("TEST_USER", "TEST_PASSWORD");
        purRepository.getObjectInformation(objectId, repositoryObjectType);
        Mockito.verify(mockRepo).getAcl(testFileId);
    }

    @Test
    public void testRootIsNotVisible() throws KettleException {
        PurRepository purRepository = new PurRepository();
        IUnifiedRepository mockRepo = Mockito.mock(IUnifiedRepository.class);
        RepositoryConnectResult result = Mockito.mock(RepositoryConnectResult.class);
        Mockito.when(result.getUnifiedRepository()).thenReturn(mockRepo);
        IRepositoryConnector connector = Mockito.mock(IRepositoryConnector.class);
        Mockito.when(connector.connect(ArgumentMatchers.anyString(), ArgumentMatchers.anyString())).thenReturn(result);
        PurRepositoryMeta mockMeta = Mockito.mock(PurRepositoryMeta.class);
        purRepository.init(mockMeta);
        purRepository.setPurRepositoryConnector(connector);
        RepositoryFile mockRootFolder = Mockito.mock(RepositoryFile.class);
        Mockito.when(mockRootFolder.getId()).thenReturn("/");
        Mockito.when(mockRootFolder.getPath()).thenReturn("/");
        // for Lazy Repo
        Mockito.when(mockRepo.getFile("/")).thenReturn(mockRootFolder);
        // for Eager Repo
        RepositoryFileTree repositoryFileTree = Mockito.mock(RepositoryFileTree.class);
        Mockito.when(mockRepo.getTree("/", (-1), null, true)).thenReturn(repositoryFileTree);
        Mockito.when(repositoryFileTree.getFile()).thenReturn(mockRootFolder);
        purRepository.connect("TEST_USER", "TEST_PASSWORD");
        RepositoryDirectoryInterface rootDir = purRepository.getRootDir();
        Assert.assertFalse(rootDir.isVisible());
    }

    @Test
    public void testEtcIsNotThereInGetChildren() throws KettleException {
        PurRepository purRepository = new PurRepository();
        IUnifiedRepository mockRepo = Mockito.mock(IUnifiedRepository.class);
        RepositoryConnectResult result = Mockito.mock(RepositoryConnectResult.class);
        Mockito.when(result.getUnifiedRepository()).thenReturn(mockRepo);
        IRepositoryConnector connector = Mockito.mock(IRepositoryConnector.class);
        Mockito.when(connector.connect(ArgumentMatchers.anyString(), ArgumentMatchers.anyString())).thenReturn(result);
        PurRepositoryMeta mockMeta = Mockito.mock(PurRepositoryMeta.class);
        purRepository.init(mockMeta);
        purRepository.setPurRepositoryConnector(connector);
        // purRepository.setTest( mockRepo );
        ObjectId objectId = Mockito.mock(ObjectId.class);
        RepositoryFile mockFile = Mockito.mock(RepositoryFile.class);
        RepositoryFile mockRootFolder = Mockito.mock(RepositoryFile.class);
        RepositoryObjectType repositoryObjectType = RepositoryObjectType.TRANSFORMATION;
        RepositoryFileTree mockRepositoryTree = Mockito.mock(RepositoryFileTree.class);
        String testId = "TEST_ID";
        String testFileId = "TEST_FILE_ID";
        Mockito.when(objectId.getId()).thenReturn(testId);
        Mockito.when(mockRepo.getFileById(testId)).thenReturn(mockFile);
        Mockito.when(mockFile.getPath()).thenReturn("/etc");
        Mockito.when(mockFile.getId()).thenReturn(testFileId);
        Mockito.when(mockRepositoryTree.getFile()).thenReturn(mockRootFolder);
        Mockito.when(mockRootFolder.getId()).thenReturn("/");
        Mockito.when(mockRootFolder.getPath()).thenReturn("/");
        List<RepositoryFile> rootChildren = new ArrayList(Collections.singletonList(mockFile));
        Mockito.when(mockRepo.getChildren(ArgumentMatchers.argThat(IsInstanceOf.<RepositoryRequest>instanceOf(RepositoryRequest.class)))).thenReturn(rootChildren);
        // for Lazy Repo
        Mockito.when(mockRepo.getFile("/")).thenReturn(mockRootFolder);
        // for Eager Repo
        RepositoryFileTree repositoryFileTree = Mockito.mock(RepositoryFileTree.class);
        Mockito.when(mockRepo.getTree("/", (-1), null, true)).thenReturn(repositoryFileTree);
        Mockito.when(repositoryFileTree.getFile()).thenReturn(mockRootFolder);
        purRepository.connect("TEST_USER", "TEST_PASSWORD");
        List<RepositoryDirectoryInterface> children = purRepository.getRootDir().getChildren();
        MatcherAssert.assertThat(children, empty());
    }

    @Test
    public void testEtcIsNotThereInGetNrDirectories() throws KettleException {
        PurRepository purRepository = new PurRepository();
        IUnifiedRepository mockRepo = Mockito.mock(IUnifiedRepository.class);
        RepositoryConnectResult result = Mockito.mock(RepositoryConnectResult.class);
        Mockito.when(result.getUnifiedRepository()).thenReturn(mockRepo);
        IRepositoryConnector connector = Mockito.mock(IRepositoryConnector.class);
        Mockito.when(connector.connect(ArgumentMatchers.anyString(), ArgumentMatchers.anyString())).thenReturn(result);
        PurRepositoryMeta mockMeta = Mockito.mock(PurRepositoryMeta.class);
        purRepository.init(mockMeta);
        purRepository.setPurRepositoryConnector(connector);
        ObjectId objectId = Mockito.mock(ObjectId.class);
        RepositoryFile mockEtcFolder = Mockito.mock(RepositoryFile.class);
        RepositoryFile mockFolderVisible = Mockito.mock(RepositoryFile.class);
        RepositoryFile mockRootFolder = Mockito.mock(RepositoryFile.class);
        RepositoryFileTree mockRepositoryTree = Mockito.mock(RepositoryFileTree.class);
        String testId = "TEST_ID";
        String visibleFolderId = testId + "2";
        Mockito.when(objectId.getId()).thenReturn(testId);
        Mockito.when(mockRepo.getFileById(testId)).thenReturn(mockEtcFolder);
        Mockito.when(mockRepo.getFile(ClientRepositoryPaths.getEtcFolderPath())).thenReturn(mockEtcFolder);
        Mockito.when(mockRepo.getFileById(visibleFolderId)).thenReturn(mockFolderVisible);
        Mockito.when(mockEtcFolder.getPath()).thenReturn("/etc");
        Mockito.when(mockEtcFolder.getName()).thenReturn("etc");
        Mockito.when(mockEtcFolder.isFolder()).thenReturn(true);
        Mockito.when(mockEtcFolder.getId()).thenReturn(testId);
        Mockito.when(mockFolderVisible.getPath()).thenReturn("/visible");
        Mockito.when(mockFolderVisible.getName()).thenReturn("visible");
        Mockito.when(mockFolderVisible.isFolder()).thenReturn(true);
        Mockito.when(mockFolderVisible.getId()).thenReturn(visibleFolderId);
        Mockito.when(mockRepositoryTree.getFile()).thenReturn(mockRootFolder);
        Mockito.when(mockRootFolder.getId()).thenReturn("/");
        Mockito.when(mockRootFolder.getPath()).thenReturn("/");
        List<RepositoryFile> rootChildren = new ArrayList(Arrays.asList(mockEtcFolder, mockFolderVisible));
        Mockito.when(mockRepo.getChildren(ArgumentMatchers.argThat(IsInstanceOf.<RepositoryRequest>instanceOf(RepositoryRequest.class)))).thenReturn(rootChildren);
        // for Lazy Repo
        Mockito.when(mockRepo.getFile("/")).thenReturn(mockRootFolder);
        // for Eager Repo
        RepositoryFileTree repositoryFileTree = Mockito.mock(RepositoryFileTree.class);
        Mockito.when(mockRepo.getTree("/", (-1), null, true)).thenReturn(repositoryFileTree);
        Mockito.when(repositoryFileTree.getFile()).thenReturn(mockRootFolder);
        RepositoryFileTree mockEtcFolderTree = Mockito.mock(RepositoryFileTree.class);
        Mockito.when(mockEtcFolderTree.getFile()).thenReturn(mockEtcFolder);
        RepositoryFileTree mockFolderVisibleTree = Mockito.mock(RepositoryFileTree.class);
        Mockito.when(mockFolderVisibleTree.getFile()).thenReturn(mockFolderVisible);
        Mockito.when(repositoryFileTree.getChildren()).thenReturn(new ArrayList<RepositoryFileTree>(Arrays.asList(mockEtcFolderTree, mockFolderVisibleTree)));
        purRepository.connect("TEST_USER", "TEST_PASSWORD");
        int children = purRepository.getRootDir().getNrSubdirectories();
        MatcherAssert.assertThat(children, Matchers.equalTo(1));
    }

    @Test
    public void onlyGlobalVariablesOfLogTablesSetToNull() {
        try {
            System.setProperty(KETTLE_GLOBAL_LOG_VARIABLES_CLEAR_ON_EXPORT, "true");
            PurRepositoryExporter purRepoExporter = new PurRepositoryExporter(Mockito.mock(PurRepository.class));
            String hardcodedString = "hardcoded";
            String globalParam = ("${" + (Const.KETTLE_TRANS_LOG_TABLE)) + "}";
            StepLogTable stepLogTable = StepLogTable.getDefault(mockedVariableSpace, mockedHasDbInterface);
            stepLogTable.setConnectionName(hardcodedString);
            stepLogTable.setSchemaName(hardcodedString);
            stepLogTable.setTimeoutInDays(hardcodedString);
            stepLogTable.setTableName(globalParam);
            JobEntryLogTable jobEntryLogTable = JobEntryLogTable.getDefault(mockedVariableSpace, mockedHasDbInterface);
            jobEntryLogTable.setConnectionName(hardcodedString);
            jobEntryLogTable.setSchemaName(hardcodedString);
            jobEntryLogTable.setTimeoutInDays(hardcodedString);
            jobEntryLogTable.setTableName(globalParam);
            List<LogTableInterface> logTables = new ArrayList<>();
            logTables.add(jobEntryLogTable);
            logTables.add(stepLogTable);
            purRepoExporter.setGlobalVariablesOfLogTablesNull(logTables);
            for (LogTableInterface logTable : logTables) {
                Assert.assertEquals(logTable.getConnectionName(), hardcodedString);
                Assert.assertEquals(logTable.getSchemaName(), hardcodedString);
                Assert.assertEquals(logTable.getTimeoutInDays(), hardcodedString);
                Assert.assertEquals(logTable.getTableName(), null);
            }
        } finally {
            System.setProperty(KETTLE_GLOBAL_LOG_VARIABLES_CLEAR_ON_EXPORT, "false");
        }
    }

    @Test
    public void globalVariablesOfLogTablesNotSetToNull() {
        PurRepositoryExporter purRepoExporter = new PurRepositoryExporter(Mockito.mock(PurRepository.class));
        String globalParam = ("${" + (Const.KETTLE_TRANS_LOG_TABLE)) + "}";
        StepLogTable stepLogTable = StepLogTable.getDefault(mockedVariableSpace, mockedHasDbInterface);
        stepLogTable.setConnectionName(globalParam);
        stepLogTable.setSchemaName(globalParam);
        stepLogTable.setTimeoutInDays(globalParam);
        stepLogTable.setTableName(globalParam);
        JobEntryLogTable jobEntryLogTable = JobEntryLogTable.getDefault(mockedVariableSpace, mockedHasDbInterface);
        jobEntryLogTable.setConnectionName(globalParam);
        jobEntryLogTable.setSchemaName(globalParam);
        jobEntryLogTable.setTimeoutInDays(globalParam);
        jobEntryLogTable.setTableName(globalParam);
        List<LogTableInterface> logTables = new ArrayList<>();
        logTables.add(jobEntryLogTable);
        logTables.add(stepLogTable);
        purRepoExporter.setGlobalVariablesOfLogTablesNull(logTables);
        for (LogTableInterface logTable : logTables) {
            Assert.assertEquals(logTable.getConnectionName(), globalParam);
            Assert.assertEquals(logTable.getSchemaName(), globalParam);
            Assert.assertEquals(logTable.getTimeoutInDays(), globalParam);
            Assert.assertEquals(logTable.getTableName(), globalParam);
        }
    }

    @Test
    public void testRevisionsEnabled() throws KettleException {
        PurRepository purRepository = new PurRepository();
        IUnifiedRepository mockRepo = Mockito.mock(IUnifiedRepository.class);
        RepositoryConnectResult result = Mockito.mock(RepositoryConnectResult.class);
        Mockito.when(result.getUnifiedRepository()).thenReturn(mockRepo);
        IRepositoryConnector connector = Mockito.mock(IRepositoryConnector.class);
        Mockito.when(connector.connect(ArgumentMatchers.anyString(), ArgumentMatchers.anyString())).thenReturn(result);
        RepositoryServiceRegistry registry = Mockito.mock(RepositoryServiceRegistry.class);
        UnifiedRepositoryLockService lockService = new UnifiedRepositoryLockService(mockRepo);
        Mockito.when(registry.getService(ILockService.class)).thenReturn(lockService);
        Mockito.when(result.repositoryServiceRegistry()).thenReturn(registry);
        PurRepositoryMeta mockMeta = Mockito.mock(PurRepositoryMeta.class);
        purRepository.init(mockMeta);
        purRepository.setPurRepositoryConnector(connector);
        // purRepository.setTest( mockRepo );
        ObjectId objectId = Mockito.mock(ObjectId.class);
        RepositoryFile mockFileVersioningEnabled = Mockito.mock(RepositoryFile.class);
        RepositoryFile mockFileVersioningNotEnabled = Mockito.mock(RepositoryFile.class);
        RepositoryFileTree mockRepositoryTreeChildVersioningEnabled = Mockito.mock(RepositoryFileTree.class);
        RepositoryFileTree mockRepositoryTreeChildVersioningNotEnabled = Mockito.mock(RepositoryFileTree.class);
        RepositoryFile publicFolder = Mockito.mock(RepositoryFile.class);
        RepositoryFileTree publicFolderTree = Mockito.mock(RepositoryFileTree.class);
        RepositoryFile mockRootFolder = Mockito.mock(RepositoryFile.class);
        RepositoryObjectType repositoryObjectType = RepositoryObjectType.TRANSFORMATION;
        RepositoryFileTree mockRepositoryTree = Mockito.mock(RepositoryFileTree.class);
        String testId = "TEST_ID";
        String testFileId = "TEST_FILE_ID";
        List<RepositoryFileTree> children = Arrays.asList(mockRepositoryTreeChildVersioningEnabled, mockRepositoryTreeChildVersioningNotEnabled);
        Mockito.when(objectId.getId()).thenReturn(testId);
        Mockito.when(mockRepo.getFileById(testId)).thenReturn(mockFileVersioningEnabled);
        Mockito.when(mockFileVersioningEnabled.getPath()).thenReturn("/public/path.ktr");
        Mockito.when(mockFileVersioningEnabled.getId()).thenReturn(testFileId);
        Mockito.when(mockFileVersioningEnabled.getName()).thenReturn("path.ktr");
        Mockito.when(mockFileVersioningNotEnabled.getPath()).thenReturn("/public/path2.ktr");
        Mockito.when(mockFileVersioningNotEnabled.getId()).thenReturn((testFileId + "2"));
        Mockito.when(mockFileVersioningNotEnabled.getName()).thenReturn("path2.ktr");
        Mockito.when(publicFolder.getPath()).thenReturn("/public");
        Mockito.when(publicFolder.getName()).thenReturn("public");
        Mockito.when(publicFolder.getId()).thenReturn((testFileId + "3"));
        Mockito.when(publicFolder.isFolder()).thenReturn(true);
        Mockito.when(publicFolderTree.getFile()).thenReturn(publicFolder);
        Mockito.when(mockRepositoryTreeChildVersioningEnabled.getFile()).thenReturn(mockFileVersioningEnabled);
        Mockito.when(mockRepositoryTreeChildVersioningEnabled.getVersionCommentEnabled()).thenReturn(true);
        Mockito.when(mockRepositoryTreeChildVersioningEnabled.getVersioningEnabled()).thenReturn(true);
        Mockito.when(mockRepositoryTreeChildVersioningNotEnabled.getFile()).thenReturn(mockFileVersioningNotEnabled);
        Mockito.when(mockRepositoryTreeChildVersioningNotEnabled.getVersionCommentEnabled()).thenReturn(false);
        Mockito.when(mockRepositoryTreeChildVersioningNotEnabled.getVersioningEnabled()).thenReturn(false);
        Mockito.when(mockRepo.getTree(ArgumentMatchers.anyString(), ArgumentMatchers.anyInt(), ArgumentMatchers.anyString(), ArgumentMatchers.anyBoolean())).thenReturn(mockRepositoryTree);
        Mockito.when(mockRepo.getTree(ArgumentMatchers.any(RepositoryRequest.class))).thenReturn(mockRepositoryTree);
        Mockito.when(mockRepo.getTree(ArgumentMatchers.argThat(new ArgumentMatcher<RepositoryRequest>() {
            @Override
            public boolean matches(Object argument) {
                return getPath().equals("/public");
            }
        }))).thenReturn(publicFolderTree);
        Mockito.when(mockRepositoryTree.getFile()).thenReturn(mockRootFolder);
        Mockito.when(mockRepositoryTree.getChildren()).thenReturn(new ArrayList(Arrays.asList(publicFolderTree)));
        Mockito.when(publicFolderTree.getChildren()).thenReturn(children);
        Mockito.when(mockRootFolder.getId()).thenReturn("/");
        Mockito.when(mockRootFolder.getPath()).thenReturn("/");
        Mockito.when(mockRepo.getFile("/")).thenReturn(mockRootFolder);
        Mockito.when(mockRepo.getFile("/public")).thenReturn(publicFolder);
        purRepository.connect("TEST_USER", "TEST_PASSWORD");
        List<RepositoryElementMetaInterface> repositoryObjects = purRepository.findDirectory("/public").getRepositoryObjects();
        MatcherAssert.assertThat(repositoryObjects.size(), Is.is(2));
        // Test Enabled
        RepositoryElementMetaInterface element = repositoryObjects.get(0);
        MatcherAssert.assertThat(element, Is.is(Matchers.instanceOf(EERepositoryObject.class)));
        EERepositoryObject eeElement = ((EERepositoryObject) (element));
        MatcherAssert.assertThat(eeElement.getVersioningEnabled(), Is.is(true));
        MatcherAssert.assertThat(eeElement.getVersionCommentEnabled(), Is.is(true));
        // Test Not Enabled
        RepositoryElementMetaInterface element2 = repositoryObjects.get(1);
        MatcherAssert.assertThat(element2, Is.is(Matchers.instanceOf(EERepositoryObject.class)));
        EERepositoryObject eeElement2 = ((EERepositoryObject) (element));
        MatcherAssert.assertThat(eeElement2.getVersioningEnabled(), Is.is(true));
        MatcherAssert.assertThat(eeElement2.getVersionCommentEnabled(), Is.is(true));
    }

    interface PluginMockInterface extends ClassLoadingPluginInterface , PluginInterface {}

    @Test
    public void testTransRepoAfterSaveExtensionPoint() throws KettleException {
        PurRepositoryUnitTest.PluginMockInterface pluginInterface = Mockito.mock(PurRepositoryUnitTest.PluginMockInterface.class);
        Mockito.when(getName()).thenReturn(id);
        Mockito.when(getMainType()).thenReturn(((Class) (ExtensionPointInterface.class)));
        Mockito.when(getIds()).thenReturn(new String[]{ "extensionpointId" });
        ExtensionPointInterface extensionPoint = Mockito.mock(ExtensionPointInterface.class);
        Mockito.when(loadClass(ExtensionPointInterface.class)).thenReturn(extensionPoint);
        PluginRegistry.addPluginType(ExtensionPointPluginType.getInstance());
        PluginRegistry.getInstance().registerPlugin(ExtensionPointPluginType.class, pluginInterface);
        PurRepository rep = Mockito.mock(PurRepository.class);
        Mockito.doCallRealMethod().when(rep).saveTransOrJob(ArgumentMatchers.any(ISharedObjectsTransformer.class), ArgumentMatchers.any(RepositoryElementInterface.class), ArgumentMatchers.anyString(), ArgumentMatchers.any(Calendar.class), ArgumentMatchers.anyBoolean(), ArgumentMatchers.anyBoolean(), ArgumentMatchers.anyBoolean(), ArgumentMatchers.anyBoolean(), ArgumentMatchers.anyBoolean());
        IUnifiedRepository pur = Mockito.mock(IUnifiedRepository.class);
        Mockito.doCallRealMethod().when(rep).setTest(ArgumentMatchers.same(pur));
        PurRepositoryMeta mockMeta = Mockito.mock(PurRepositoryMeta.class);
        Mockito.doCallRealMethod().when(rep).init(ArgumentMatchers.same(mockMeta));
        rep.init(mockMeta);
        rep.setTest(pur);
        RepositoryFile file = Mockito.mock(RepositoryFile.class);
        Mockito.when(file.getId()).thenReturn("id");
        Mockito.when(pur.createFile(ArgumentMatchers.any(Serializable.class), ArgumentMatchers.any(RepositoryFile.class), ArgumentMatchers.any(IRepositoryFileData.class), ArgumentMatchers.anyString())).thenReturn(file);
        TransMeta trans = Mockito.mock(TransMeta.class);
        Mockito.when(trans.getRepositoryElementType()).thenReturn(TRANSFORMATION);
        Mockito.when(trans.getName()).thenReturn("trans");
        RepositoryDirectory dir = Mockito.mock(RepositoryDirectory.class);
        Mockito.when(dir.getObjectId()).thenReturn(new StringObjectId("id"));
        Mockito.when(trans.getRepositoryDirectory()).thenReturn(dir);
        TransMeta transFromRepo = Mockito.mock(TransMeta.class);
        Mockito.when(rep.loadTransformation(ArgumentMatchers.any(ObjectId.class), ArgumentMatchers.isNull(String.class))).thenReturn(transFromRepo);
        ISharedObjectsTransformer transformer = Mockito.mock(ISharedObjectsTransformer.class);
        rep.saveTransOrJob(transformer, trans, "", Calendar.getInstance(), false, false, false, false, false);
        Mockito.verify(extensionPoint, Mockito.times(1)).callExtensionPoint(ArgumentMatchers.any(LogChannelInterface.class), ArgumentMatchers.same(transFromRepo));
    }

    @Test(expected = KettleException.class)
    public void testSaveTransOrJob() throws KettleException {
        PurRepository purRepository = new PurRepository();
        RepositoryElementInterface element = Mockito.mock(RepositoryElementInterface.class);
        RepositoryDirectoryInterface directoryInterface = Mockito.mock(RepositoryDirectoryInterface.class);
        Mockito.when(element.getRepositoryDirectory()).thenReturn(directoryInterface);
        Mockito.when(element.getRepositoryElementType()).thenReturn(TRANSFORMATION);
        Mockito.when(directoryInterface.toString()).thenReturn(ROOT_DIRECTORY);
        purRepository.saveTransOrJob(null, element, null, null, false, false, false, false, false);
    }

    @Test
    public void testGetJobPathWithoutExtension() {
        PurRepository pur = new PurRepository();
        RepositoryDirectoryInterface rdi = Mockito.mock(RepositoryDirectoryInterface.class);
        Mockito.doReturn(Mockito.mock(ObjectId.class)).when(rdi).getObjectId();
        getPath();
        Assert.assertEquals("/home/admin/job.kjb", pur.getPath("job", rdi, JOB));
    }

    @Test
    public void testGetJobPathWithExtension() {
        PurRepository pur = new PurRepository();
        RepositoryDirectoryInterface rdi = Mockito.mock(RepositoryDirectoryInterface.class);
        Mockito.doReturn(Mockito.mock(ObjectId.class)).when(rdi).getObjectId();
        getPath();
        Assert.assertEquals("/home/admin/job.kjb", pur.getPath("job.kjb", rdi, JOB));
    }

    @Test
    public void testGetTransPathWithoutExtension() {
        PurRepository pur = new PurRepository();
        RepositoryDirectoryInterface rdi = Mockito.mock(RepositoryDirectoryInterface.class);
        Mockito.doReturn(Mockito.mock(ObjectId.class)).when(rdi).getObjectId();
        getPath();
        Assert.assertEquals("/home/admin/trans.ktr", pur.getPath("trans", rdi, TRANSFORMATION));
    }

    @Test
    public void testGetTransPathWithExtension() {
        PurRepository pur = new PurRepository();
        RepositoryDirectoryInterface rdi = Mockito.mock(RepositoryDirectoryInterface.class);
        Mockito.doReturn(Mockito.mock(ObjectId.class)).when(rdi).getObjectId();
        getPath();
        Assert.assertEquals("/home/admin/trans.ktr", pur.getPath("trans.ktr", rdi, TRANSFORMATION));
    }

    @Test
    public void testCreateValidRepositoryDirectoryForRootHomeFolder() throws KettleException {
        RepositoryDirectoryInterface treeMocked = Mockito.mock(RepositoryDirectoryInterface.class);
        PurRepository repository = Mockito.mock(PurRepository.class);
        LazyUnifiedRepositoryDirectory lazy = Mockito.mock(LazyUnifiedRepositoryDirectory.class);
        String newDirectory = "home/admin1";
        // if root then we can create any folder at home or public folders
        Mockito.when(treeMocked.isRoot()).thenReturn(true);
        Mockito.when(treeMocked.getPath()).thenReturn(newDirectory);
        Mockito.when(repository.findDirectory(ArgumentMatchers.anyString())).thenReturn(lazy);
        Mockito.when(repository.createRepositoryDirectory(treeMocked, newDirectory)).thenCallRealMethod();
        Assert.assertEquals("/home/admin1", getPath());
    }

    @Test
    public void testCreateValidRepositoryDirectoryForRootPublicFolder() throws KettleException {
        RepositoryDirectoryInterface treeMocked = Mockito.mock(RepositoryDirectoryInterface.class);
        PurRepository repository = Mockito.mock(PurRepository.class);
        LazyUnifiedRepositoryDirectory lazy = Mockito.mock(LazyUnifiedRepositoryDirectory.class);
        String newDirectory = "public/admin1";
        // if root then we can create any folder at home or public folders
        Mockito.when(treeMocked.isRoot()).thenReturn(true);
        Mockito.when(treeMocked.getPath()).thenReturn(newDirectory);
        Mockito.when(repository.findDirectory(ArgumentMatchers.anyString())).thenReturn(lazy);
        Mockito.when(repository.createRepositoryDirectory(treeMocked, newDirectory)).thenCallRealMethod();
        Assert.assertEquals("/public/admin1", getPath());
    }

    @Test(expected = KettleException.class)
    public void testCreateInvalidRepositoryDirectoryForRootAnyOtherFolders() throws KettleException {
        RepositoryDirectoryInterface treeMocked = Mockito.mock(RepositoryDirectoryInterface.class);
        PurRepository repository = Mockito.mock(PurRepository.class);
        LazyUnifiedRepositoryDirectory lazy = Mockito.mock(LazyUnifiedRepositoryDirectory.class);
        String newDirectory = "dummy/admin1";
        // if root then we can ony create folders at home or public folders
        Mockito.when(treeMocked.isRoot()).thenReturn(true);
        Mockito.when(treeMocked.getPath()).thenReturn(newDirectory);
        Mockito.when(repository.findDirectory(ArgumentMatchers.anyString())).thenReturn(lazy);
        Mockito.when(repository.createRepositoryDirectory(treeMocked, newDirectory)).thenCallRealMethod();
        Assert.assertNull(getPath());
    }

    @Test(expected = KettleException.class)
    public void testCreateInvalidRepositoryDirectoryForRoot() throws KettleException {
        RepositoryDirectoryInterface treeMocked = Mockito.mock(RepositoryDirectoryInterface.class);
        PurRepository repository = Mockito.mock(PurRepository.class);
        LazyUnifiedRepositoryDirectory lazy = Mockito.mock(LazyUnifiedRepositoryDirectory.class);
        String newDirectory = "admin1";
        // if root then we can ony create folders at home or public folders
        Mockito.when(treeMocked.isRoot()).thenReturn(true);
        Mockito.when(treeMocked.getPath()).thenReturn(newDirectory);
        Mockito.when(repository.findDirectory(ArgumentMatchers.anyString())).thenReturn(lazy);
        Mockito.when(repository.createRepositoryDirectory(treeMocked, newDirectory)).thenCallRealMethod();
        Assert.assertNull(getPath());
    }

    @Test
    public void testCreateValidRepositoryDirectoryForNotRoot() throws KettleException {
        RepositoryDirectoryInterface treeMocked = Mockito.mock(RepositoryDirectoryInterface.class);
        PurRepository repository = Mockito.mock(PurRepository.class);
        LazyUnifiedRepositoryDirectory lazy = Mockito.mock(LazyUnifiedRepositoryDirectory.class);
        String newDirectory = "admin1";
        // if not root then we can create any folder
        Mockito.when(treeMocked.isRoot()).thenReturn(false);
        Mockito.when(treeMocked.getPath()).thenReturn(newDirectory);
        Mockito.when(repository.findDirectory(ArgumentMatchers.anyString())).thenReturn(lazy);
        Mockito.when(repository.createRepositoryDirectory(treeMocked, newDirectory)).thenCallRealMethod();
        Assert.assertEquals("/admin1", getPath());
    }
}

