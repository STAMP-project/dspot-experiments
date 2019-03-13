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
package org.pentaho.di.ui.repository.pur.repositoryexplorer.model;


import RepositoryFilePermission.READ;
import RepositoryFilePermission.WRITE;
import java.io.File;
import java.util.Arrays;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.repository.ObjectId;
import org.pentaho.di.repository.ObjectRecipient;
import org.pentaho.di.repository.ObjectRecipient.Type;
import org.pentaho.di.repository.ObjectRevision;
import org.pentaho.di.repository.Repository;
import org.pentaho.di.repository.RepositoryDirectory;
import org.pentaho.di.repository.RepositoryElementMetaInterface;
import org.pentaho.di.repository.pur.model.EERepositoryObject;
import org.pentaho.di.repository.pur.model.ObjectAcl;
import org.pentaho.di.repository.pur.model.RepositoryLock;
import org.pentaho.di.repository.pur.model.RepositoryObjectAcl;
import org.pentaho.di.ui.repository.pur.services.IAclService;
import org.pentaho.di.ui.repository.pur.services.ILockService;
import org.pentaho.di.ui.repository.pur.services.IRevisionService;
import org.pentaho.di.ui.repository.repositoryexplorer.AccessDeniedException;
import org.pentaho.di.ui.repository.repositoryexplorer.model.UIRepositoryDirectory;


/**
 *
 *
 * @author tkafalas
 */
public class UIEETransformationTest {
    private static final String LOCK_MESSAGE = "lockMessage";

    private static final String LOCK_NOTE = "lockNote";

    private static final String TRANS_ID = "transId";

    private ObjectId mockObjectId;

    private UIEETransformation uiTransformation;

    private EERepositoryObject mockEERepositoryObject;

    private ILockService mockLockService;

    private RepositoryLock mockRepositoryLock;

    private UIRepositoryDirectory mockParent;

    private IRevisionService mockRevisionService;

    private IAclService mockAclService;

    private Repository mockRepository;

    @Test
    public void testGetImage() {
        String image = uiTransformation.getImage();
        Assert.assertNotNull(image);
        File f = new File(image);
        Mockito.when(mockEERepositoryObject.isLocked()).thenReturn(true);
        String image2 = uiTransformation.getImage();
        Assert.assertNotNull(image2);
        f = new File(image2);
        Assert.assertNotEquals(image, image2);
    }

    @Test
    public void testGetLockMessage() throws Exception {
        Mockito.when(mockEERepositoryObject.getLockMessage()).thenReturn(UIEETransformationTest.LOCK_MESSAGE);
        Assert.assertEquals(UIEETransformationTest.LOCK_MESSAGE, uiTransformation.getLockMessage());
    }

    @Test
    public void testLock() throws Exception {
        Mockito.when(mockLockService.lockTransformation(mockObjectId, UIEETransformationTest.LOCK_NOTE)).thenReturn(mockRepositoryLock);
        uiTransformation.lock(UIEETransformationTest.LOCK_NOTE);
        Mockito.verify(mockEERepositoryObject).setLock(mockRepositoryLock);
        Mockito.verify(mockParent).fireCollectionChanged();
        uiTransformation.unlock();
        Mockito.verify(mockEERepositoryObject).setLock(null);
        Mockito.verify(mockParent, Mockito.times(2)).fireCollectionChanged();
    }

    @Test
    public void testRevisions() throws Exception {
        final String revisionName = "revisionName";
        final String commitMessage = "commitMessage";
        ObjectRevision mockObjectRevision = Mockito.mock(ObjectRevision.class);
        Mockito.when(mockObjectRevision.getName()).thenReturn(revisionName);
        List<ObjectRevision> mockRevisions = Arrays.asList(new ObjectRevision[]{ mockObjectRevision });
        Mockito.when(mockRevisionService.getRevisions(ArgumentMatchers.any(ObjectId.class))).thenReturn(mockRevisions);
        uiTransformation.refreshRevisions();
        Mockito.verify(mockRevisionService, Mockito.times(1)).getRevisions(mockObjectId);
        UIRepositoryObjectRevisions revisions = uiTransformation.getRevisions();
        Assert.assertEquals(1, revisions.size());
        Assert.assertEquals("revisionName", revisions.get(0).getName());
        Mockito.verify(mockRevisionService, Mockito.times(1)).getRevisions(mockObjectId);
        uiTransformation.restoreRevision(revisions.get(0), commitMessage);
        Mockito.verify(mockRevisionService).restoreTransformation(mockObjectId, revisionName, commitMessage);
        Mockito.verify(mockParent, Mockito.times(1)).fireCollectionChanged();
    }

    @Test
    public void testAcls() throws Exception {
        final String owner = "owner";
        final String role = "role";
        ObjectRecipient mockObjectRecipient = Mockito.mock(ObjectRecipient.class);
        Mockito.when(mockObjectRecipient.getName()).thenReturn(owner);
        ObjectAcl mockAcl = Mockito.mock(ObjectAcl.class);
        Mockito.when(mockAcl.getOwner()).thenReturn(mockObjectRecipient);
        Mockito.when(mockAclService.getAcl(mockObjectId, false)).thenReturn(mockAcl);
        uiTransformation.clearAcl();
        UIRepositoryObjectAcls acls = new UIRepositoryObjectAcls();
        uiTransformation.getAcls(acls);
        Mockito.verify(mockAclService).getAcl(mockObjectId, false);
        Assert.assertEquals(owner, acls.getOwner().getName());
        acls = new UIRepositoryObjectAcls();
        RepositoryObjectAcl repObjectAcl = new RepositoryObjectAcl(new org.pentaho.di.repository.pur.model.RepositoryObjectRecipient(role, Type.ROLE));
        acls.setObjectAcl(new RepositoryObjectAcl(new org.pentaho.di.repository.pur.model.RepositoryObjectRecipient(role, Type.ROLE)));
        uiTransformation.setAcls(acls);
        Mockito.verify(mockAclService).setAcl(mockObjectId, repObjectAcl);
        Mockito.when(mockAclService.getAcl(mockObjectId, false)).thenThrow(new KettleException(""));
        uiTransformation.clearAcl();
        try {
            uiTransformation.getAcls(acls);
            Assert.fail("Expected an exception");
        } catch (AccessDeniedException e) {
            // Test Succeeded if here
        }
        Mockito.doThrow(new KettleException("")).when(mockAclService).setAcl(ArgumentMatchers.any(ObjectId.class), ArgumentMatchers.any(RepositoryObjectAcl.class));
        try {
            uiTransformation.setAcls(acls);
            Assert.fail("Expected an exception");
        } catch (AccessDeniedException e) {
            // Test Succeeded if here
        }
    }

    @Test
    public void testAccess() throws Exception {
        Mockito.when(mockAclService.hasAccess(mockObjectId, READ)).thenReturn(true);
        Mockito.when(mockAclService.hasAccess(mockObjectId, WRITE)).thenReturn(false);
        Assert.assertTrue(uiTransformation.hasAccess(READ));
        Assert.assertFalse(uiTransformation.hasAccess(WRITE));
    }

    @Test
    public void testRename() throws Exception {
        final String newName = "newName";
        RepositoryDirectory repDir = Mockito.mock(RepositoryDirectory.class);
        uiTransformation.renameTransformation(mockObjectId, repDir, newName);
        Mockito.verify(mockRevisionService, Mockito.times(1)).getRevisions(mockObjectId);
    }

    @Test
    public void testVersionFlags() throws Exception {
        Assert.assertFalse(uiTransformation.getVersioningEnabled());
        Mockito.when(mockEERepositoryObject.getVersioningEnabled()).thenReturn(true);
        Assert.assertTrue(uiTransformation.getVersioningEnabled());
        Assert.assertFalse(uiTransformation.getVersionCommentEnabled());
        Mockito.when(mockEERepositoryObject.getVersionCommentEnabled()).thenReturn(true);
        Assert.assertTrue(uiTransformation.getVersionCommentEnabled());
    }

    @Test(expected = IllegalStateException.class)
    public void testIllegalStateOnAclService() throws Exception {
        Mockito.when(mockRepository.hasService(IAclService.class)).thenReturn(false);
        uiTransformation = new UIEETransformation(mockEERepositoryObject, mockParent, mockRepository);
    }

    @Test(expected = IllegalStateException.class)
    public void testIllegalStateOnLockService() throws Exception {
        Mockito.when(mockRepository.hasService(ILockService.class)).thenReturn(false);
        uiTransformation = new UIEETransformation(mockEERepositoryObject, mockParent, mockRepository);
    }

    @Test(expected = IllegalStateException.class)
    public void testIllegalStateOnRevisionService() throws Exception {
        Mockito.when(mockRepository.hasService(IRevisionService.class)).thenReturn(false);
        uiTransformation = new UIEETransformation(mockEERepositoryObject, mockParent, mockRepository);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testIllegalArgumentOnConstructor() throws Exception {
        RepositoryElementMetaInterface badObject = Mockito.mock(RepositoryElementMetaInterface.class);
        uiTransformation = new UIEETransformation(badObject, mockParent, mockRepository);
    }
}

