/**
 * !
 * Copyright 2010 - 2017 Hitachi Vantara.  All rights reserved.
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
package org.pentaho.di.ui.repository.pur.repositoryexplorer.controller;


import RepositoryObjectType.JOB;
import RepositoryObjectType.TRANSFORMATION;
import TrashBrowseController.UIDeletedObject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.repository.ObjectId;
import org.pentaho.di.repository.Repository;
import org.pentaho.di.repository.RepositoryDirectoryInterface;
import org.pentaho.di.repository.RepositoryElementMetaInterface;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.ui.repository.pur.services.ITrashService;
import org.pentaho.di.ui.repository.repositoryexplorer.controllers.MainController;
import org.pentaho.di.ui.repository.repositoryexplorer.model.UIRepositoryDirectory;
import org.pentaho.ui.xul.components.XulMessageBox;
import org.pentaho.ui.xul.containers.XulDeck;


/**
 *
 *
 * @author Tatsiana_Kasiankova
 */
@RunWith(MockitoJUnitRunner.class)
public class TrashBrowseControllerTest {
    private TrashBrowseControllerTest.TrashBrowseControllerSpy trBrController;

    @Mock
    UIRepositoryDirectory repoDirMock;

    @Mock
    ITrashService trashServiceMock;

    @Mock
    Repository repositoryMock;

    @Mock
    List<TrashBrowseController.UIDeletedObject> selectedTrashFileItemsMock;

    @Mock
    UIDeletedObject uiDeleteObjectMock;

    @Mock
    UIDeletedObject uiDirectoryObjectMock;

    @Mock
    RepositoryElementMetaInterface objectInDirectory;

    @Mock
    ObjectId objectIdMock;

    @Mock
    ObjectId objectIdMock2;

    @Mock
    TransMeta transMetaMock;

    @Mock
    XulDeck deckMock;

    @Mock
    RepositoryDirectoryInterface actualDirMock;

    @Mock
    RepositoryDirectoryInterface actualSubDirMock;

    @Mock
    List<RepositoryElementMetaInterface> repositoryObjectsMock;

    @Mock
    List<RepositoryDirectoryInterface> repositoryDirectoryInterfaces;

    @Mock
    MainController mainControllerMock;

    @Mock
    XulMessageBox messageBoxMock;

    @Mock
    Map<ObjectId, UIRepositoryDirectory> dirMapMock;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void testShouldRefreshRepoDir_IfKettleExceptionOnRepoDirDeletion() throws Exception {
        KettleException ke = new KettleException("TEST MESSAGE");
        Mockito.doThrow(ke).when(repoDirMock).delete();
        try {
            trBrController.deleteFolder(repoDirMock);
            Assert.fail(("Expected appearance KettleException: " + (ke.getMessage())));
        } catch (KettleException e) {
            Assert.assertTrue(ke.getMessage().equals(e.getMessage()));
        }
        Mockito.verify(repoDirMock, Mockito.times(1)).refresh();
    }

    public class TrashBrowseControllerSpy extends TrashBrowseController {
        private static final long serialVersionUID = 1L;

        TrashBrowseControllerSpy() {
            super();
            this.repoDir = repoDirMock;
            this.selectedTrashFileItems = selectedTrashFileItemsMock;
            this.trashService = trashServiceMock;
            this.repository = repositoryMock;
            this.deck = deckMock;
            this.mainController = mainControllerMock;
            this.messageBox = messageBoxMock;
            this.dirMap = dirMapMock;
        }
    }

    @Test
    public void testUnDeleteNoFileSelected() throws Exception {
        Mockito.when(selectedTrashFileItemsMock.toArray()).thenReturn(new TrashBrowseController.UIDeletedObject[0]);
        Mockito.when(selectedTrashFileItemsMock.size()).thenReturn(0);
        expectedException.expect(RuntimeException.class);
        undelete();
    }

    @Test
    public void testUnDeleteTransformation() throws Exception {
        testUnDelete(TRANSFORMATION.name(), true);
        Mockito.verify(trashServiceMock, Mockito.times(1)).undelete(ArgumentMatchers.anyList());
        Mockito.verify(transMetaMock, Mockito.times(1)).clearChanged();
        Mockito.verify(repositoryMock, Mockito.times(1)).loadTransformation(objectIdMock, null);
        Mockito.verify(deckMock, Mockito.times(1)).setSelectedIndex(1);
    }

    @Test
    public void testUnDeleteJob() throws Exception {
        testUnDelete(JOB.name(), true);
        Mockito.verify(trashServiceMock, Mockito.times(1)).undelete(ArgumentMatchers.anyList());
        Mockito.verify(transMetaMock, Mockito.never()).clearChanged();
        Mockito.verify(repositoryMock, Mockito.never()).loadTransformation(objectIdMock, null);
        Mockito.verify(deckMock, Mockito.times(1)).setSelectedIndex(1);
    }

    @Test
    public void testClosestUIRepositoryDirectory() throws Exception {
        testUnDelete(JOB.name(), false);
        Mockito.verify(trashServiceMock, Mockito.times(1)).undelete(ArgumentMatchers.anyList());
        Mockito.verify(transMetaMock, Mockito.never()).clearChanged();
        Mockito.verify(repositoryMock, Mockito.never()).loadTransformation(objectIdMock, null);
        Mockito.verify(repoDirMock, Mockito.times(1)).refresh();
        Mockito.verify(deckMock, Mockito.times(1)).setSelectedIndex(1);
    }

    @Test
    public void testUnDeleteDirectory() throws Exception {
        Mockito.when(selectedTrashFileItemsMock.toArray()).thenReturn(new TrashBrowseController.UIDeletedObject[]{ uiDirectoryObjectMock });
        Mockito.when(selectedTrashFileItemsMock.size()).thenReturn(1);
        Mockito.doReturn(uiDirectoryObjectMock).when(selectedTrashFileItemsMock).get(0);
        Mockito.doReturn("/home/admin").when(uiDirectoryObjectMock).getOriginalParentPath();
        Mockito.doReturn(objectIdMock).when(uiDirectoryObjectMock).getId();
        Mockito.doReturn(null).when(uiDirectoryObjectMock).getType();
        Mockito.doReturn(Arrays.asList(uiDirectoryObjectMock)).when(trashServiceMock).getTrash();
        Mockito.doReturn(null).when(repositoryMock).findDirectory("/home/admin");
        Mockito.doReturn(transMetaMock).when(repositoryMock).loadTransformation(objectIdMock, null);
        Mockito.doReturn("directory").when(uiDirectoryObjectMock).getName();
        Mockito.doReturn(actualDirMock).when(repositoryMock).findDirectory("/home/admin/directory");
        Mockito.doReturn(null).when(actualDirMock).getChildren();
        Mockito.doReturn(TRANSFORMATION).when(objectInDirectory).getObjectType();
        Mockito.doReturn(objectIdMock).when(objectInDirectory).getObjectId();
        Mockito.doReturn(Arrays.asList(objectInDirectory)).when(actualDirMock).getRepositoryObjects();
        undelete();
        Mockito.verify(trashServiceMock, Mockito.times(1)).undelete(ArgumentMatchers.anyList());
        Mockito.verify(transMetaMock, Mockito.times(1)).clearChanged();
        Mockito.verify(repositoryMock, Mockito.times(1)).loadTransformation(objectIdMock, null);
        Mockito.verify(repositoryMock, Mockito.times(1)).findDirectory("/home/admin/directory");
        Mockito.verify(actualDirMock, Mockito.times(1)).getChildren();
        Mockito.verify(deckMock, Mockito.times(1)).setSelectedIndex(1);
    }

    @Test
    public void testUnDeleteDirectoryWSubDir() throws Exception {
        List<TrashBrowseController.UIDeletedObject> uiDeleteObjects = Arrays.asList(uiDirectoryObjectMock);
        Mockito.when(selectedTrashFileItemsMock.toArray()).thenReturn(new TrashBrowseController.UIDeletedObject[]{ uiDirectoryObjectMock });
        Mockito.when(selectedTrashFileItemsMock.size()).thenReturn(1);
        Mockito.doReturn(uiDirectoryObjectMock).when(selectedTrashFileItemsMock).get(0);
        Mockito.doReturn("/home/admin").when(uiDirectoryObjectMock).getOriginalParentPath();
        Mockito.doReturn(objectIdMock).when(uiDirectoryObjectMock).getId();
        Mockito.doReturn(null).when(uiDirectoryObjectMock).getType();
        Mockito.doReturn(uiDeleteObjects).when(trashServiceMock).getTrash();
        Mockito.doReturn(null).when(repositoryMock).findDirectory("/home/admin");
        Mockito.doReturn(transMetaMock).when(repositoryMock).loadTransformation(objectIdMock, null);
        Mockito.doReturn("directory").when(uiDirectoryObjectMock).getName();
        Mockito.doReturn(actualDirMock).when(repositoryMock).findDirectory("/home/admin/directory");
        Mockito.doReturn(Arrays.asList(actualSubDirMock)).when(actualDirMock).getChildren();
        Mockito.doReturn(null).when(actualSubDirMock).getChildren();
        Mockito.doReturn(Arrays.asList(objectInDirectory)).when(actualSubDirMock).getRepositoryObjects();
        Mockito.doReturn(TRANSFORMATION).when(objectInDirectory).getObjectType();
        Mockito.doReturn(objectIdMock).when(objectInDirectory).getObjectId();
        Mockito.doReturn(new ArrayList<RepositoryElementMetaInterface>()).when(actualDirMock).getRepositoryObjects();
        undelete();
        Mockito.verify(trashServiceMock, Mockito.times(1)).undelete(ArgumentMatchers.anyList());
        Mockito.verify(transMetaMock, Mockito.times(1)).clearChanged();
        Mockito.verify(repositoryMock, Mockito.times(1)).loadTransformation(objectIdMock, null);
        Mockito.verify(repositoryMock, Mockito.times(1)).findDirectory("/home/admin/directory");
        Mockito.verify(actualDirMock, Mockito.times(3)).getChildren();
        Mockito.verify(actualSubDirMock, Mockito.times(1)).getChildren();
        Mockito.verify(deckMock, Mockito.times(1)).setSelectedIndex(1);
    }

    @Test
    public void testExceptionHandle() throws Exception {
        RuntimeException runtimeException = new RuntimeException("Exception handle");
        Mockito.when(selectedTrashFileItemsMock.toArray()).thenReturn(new TrashBrowseController.UIDeletedObject[]{ uiDirectoryObjectMock });
        Mockito.when(selectedTrashFileItemsMock.size()).thenReturn(1);
        Mockito.doReturn(uiDirectoryObjectMock).when(selectedTrashFileItemsMock).get(0);
        Mockito.doThrow(runtimeException).when(trashServiceMock).undelete(ArgumentMatchers.anyList());
        Mockito.doReturn(false).when(mainControllerMock).handleLostRepository(ArgumentMatchers.any(Throwable.class));
        undelete();
        Mockito.verify(messageBoxMock).setTitle("Error");
        Mockito.verify(messageBoxMock).setAcceptLabel("OK");
        Mockito.verify(messageBoxMock).setMessage(ArgumentMatchers.contains("Exception handle"));
        Mockito.verify(messageBoxMock, Mockito.times(1)).open();
        Mockito.verify(deckMock, Mockito.never()).setSelectedIndex(1);
    }

    @Test
    public void testExceptionNotHandle() throws Exception {
        RuntimeException runtimeException = new RuntimeException("Exception handle");
        Mockito.when(selectedTrashFileItemsMock.toArray()).thenReturn(new TrashBrowseController.UIDeletedObject[]{ uiDirectoryObjectMock });
        Mockito.when(selectedTrashFileItemsMock.size()).thenReturn(1);
        Mockito.doReturn(uiDirectoryObjectMock).when(selectedTrashFileItemsMock).get(0);
        Mockito.doThrow(runtimeException).when(trashServiceMock).undelete(ArgumentMatchers.anyList());
        Mockito.doReturn(true).when(mainControllerMock).handleLostRepository(ArgumentMatchers.any(Throwable.class));
        undelete();
        Mockito.verify(messageBoxMock, Mockito.never()).setTitle("Error");
        Mockito.verify(messageBoxMock, Mockito.never()).setAcceptLabel("OK");
        Mockito.verify(messageBoxMock, Mockito.never()).setMessage(ArgumentMatchers.contains("Exception handle"));
        Mockito.verify(messageBoxMock, Mockito.never()).open();
        Mockito.verify(deckMock, Mockito.never()).setSelectedIndex(1);
    }

    @Test
    public void testUnDeleteNotFoundDir() throws Exception {
        Mockito.when(selectedTrashFileItemsMock.toArray()).thenReturn(new TrashBrowseController.UIDeletedObject[]{ uiDirectoryObjectMock });
        Mockito.when(selectedTrashFileItemsMock.size()).thenReturn(1);
        Mockito.doReturn(uiDirectoryObjectMock).when(selectedTrashFileItemsMock).get(0);
        Mockito.doReturn("/home/admin").when(uiDirectoryObjectMock).getOriginalParentPath();
        Mockito.doReturn(objectIdMock).when(uiDirectoryObjectMock).getId();
        Mockito.doReturn(null).when(uiDirectoryObjectMock).getType();
        Mockito.doReturn(Arrays.asList(uiDirectoryObjectMock)).when(trashServiceMock).getTrash();
        Mockito.doReturn(null).when(repositoryMock).findDirectory("/home/admin");
        Mockito.doReturn(transMetaMock).when(repositoryMock).loadTransformation(objectIdMock, null);
        Mockito.doReturn("directory").when(uiDirectoryObjectMock).getName();
        Mockito.doReturn(null).when(repositoryMock).findDirectory("/home/admin/directory");
        undelete();
        Mockito.verify(messageBoxMock).setTitle("Error");
        Mockito.verify(messageBoxMock).setAcceptLabel("OK");
        Mockito.verify(messageBoxMock).setMessage(ArgumentMatchers.eq("Unable to restore directory /home/admin/directory cause : Directory not found"));
        Mockito.verify(messageBoxMock, Mockito.times(1)).open();
        Mockito.verify(deckMock, Mockito.times(1)).setSelectedIndex(1);
    }
}

