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
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.repository.ObjectId;
import org.pentaho.di.repository.ObjectRecipient;
import org.pentaho.di.repository.ObjectRecipient.Type;
import org.pentaho.di.repository.Repository;
import org.pentaho.di.repository.RepositoryDirectory;
import org.pentaho.di.repository.pur.PurRepository;
import org.pentaho.di.repository.pur.model.ObjectAcl;
import org.pentaho.di.repository.pur.model.RepositoryObjectAcl;
import org.pentaho.di.ui.repository.pur.services.IAclService;
import org.pentaho.di.ui.repository.repositoryexplorer.AccessDeniedException;
import org.pentaho.di.ui.repository.repositoryexplorer.model.UIRepositoryDirectories;
import org.pentaho.di.ui.repository.repositoryexplorer.model.UIRepositoryDirectory;
import org.pentaho.di.ui.repository.repositoryexplorer.model.UIRepositoryObjects;


/**
 *
 *
 * @author tkafalas
 */
public class UIEERepositoryDirectoryTest {
    private static final String ID = "ID";

    private ObjectId mockObjectId;

    private RepositoryDirectory mockRepositoryDirectory;

    private UIRepositoryDirectory mockParent;

    private IAclService mockAclService;

    private Repository mockRepository;

    private PurRepository mockPurRepository;

    UIEERepositoryDirectory uiRepDir;

    UIEERepositoryDirectory uiPurRepDir;

    @Test
    public void testAcls() throws Exception {
        final String owner = "owner";
        final String role = "role";
        ObjectRecipient mockObjectRecipient = Mockito.mock(ObjectRecipient.class);
        Mockito.when(mockObjectRecipient.getName()).thenReturn(owner);
        ObjectAcl mockAcl = Mockito.mock(ObjectAcl.class);
        Mockito.when(mockAcl.getOwner()).thenReturn(mockObjectRecipient);
        Mockito.when(mockAclService.getAcl(mockObjectId, false)).thenReturn(mockAcl);
        uiRepDir.clearAcl();
        UIRepositoryObjectAcls acls = new UIRepositoryObjectAcls();
        uiRepDir.getAcls(acls);
        Mockito.verify(mockAclService, Mockito.times(1)).getAcl(mockObjectId, false);
        Assert.assertEquals(owner, acls.getOwner().getName());
        acls = new UIRepositoryObjectAcls();
        uiRepDir.getAcls(acls, false);
        Mockito.verify(mockAclService, Mockito.times(2)).getAcl(mockObjectId, false);
        Assert.assertEquals(owner, acls.getOwner().getName());
        acls = new UIRepositoryObjectAcls();
        RepositoryObjectAcl repObjectAcl = new RepositoryObjectAcl(new org.pentaho.di.repository.pur.model.RepositoryObjectRecipient(role, Type.ROLE));
        acls.setObjectAcl(new RepositoryObjectAcl(new org.pentaho.di.repository.pur.model.RepositoryObjectRecipient(role, Type.ROLE)));
        uiRepDir.setAcls(acls);
        Mockito.verify(mockAclService).setAcl(mockObjectId, repObjectAcl);
        Mockito.when(mockAclService.getAcl(mockObjectId, false)).thenThrow(new KettleException(""));
        uiRepDir.clearAcl();
        try {
            uiRepDir.getAcls(acls);
            Assert.fail("Expected an exception");
        } catch (AccessDeniedException e) {
            // Test Succeeded if here
        }
        Mockito.when(mockAclService.getAcl(mockObjectId, true)).thenThrow(new KettleException(""));
        uiRepDir.clearAcl();
        try {
            uiRepDir.getAcls(acls, true);
            Assert.fail("Expected an exception");
        } catch (AccessDeniedException e) {
            // Test Succeeded if here
        }
        Mockito.doThrow(new KettleException("")).when(mockAclService).setAcl(ArgumentMatchers.any(ObjectId.class), ArgumentMatchers.any(ObjectAcl.class));
        uiRepDir.clearAcl();
        try {
            uiRepDir.setAcls(acls);
            Assert.fail("Expected an exception");
        } catch (AccessDeniedException e) {
            // Test Succeeded if here
        }
    }

    @Test
    public void testDelete() throws Exception {
        UIRepositoryDirectories mockUIRepositoryDirectories = Mockito.mock(UIRepositoryDirectories.class);
        Mockito.when(mockUIRepositoryDirectories.contains(uiRepDir)).thenReturn(true);
        Mockito.when(mockParent.getChildren()).thenReturn(mockUIRepositoryDirectories);
        UIRepositoryObjects mockUIRepositoryObjects = Mockito.mock(UIRepositoryObjects.class);
        Mockito.when(mockUIRepositoryObjects.contains(uiRepDir)).thenReturn(true);
        Mockito.when(mockParent.getRepositoryObjects()).thenReturn(mockUIRepositoryObjects);
        uiRepDir.delete(false);
        Mockito.verify(mockRepository).deleteRepositoryDirectory(mockRepositoryDirectory);
        Mockito.verify(mockUIRepositoryDirectories, Mockito.times(1)).remove(uiRepDir);
        Mockito.verify(mockUIRepositoryObjects, Mockito.times(1)).remove(uiRepDir);
        Mockito.verify(mockParent, Mockito.times(1)).refresh();
        uiPurRepDir.delete(false);
        Mockito.verify(mockPurRepository).deleteRepositoryDirectory(mockRepositoryDirectory, false);
        Mockito.verify(mockUIRepositoryDirectories, Mockito.times(2)).remove(uiPurRepDir);
        Mockito.verify(mockUIRepositoryObjects, Mockito.times(2)).remove(uiPurRepDir);
        Mockito.verify(mockParent, Mockito.times(2)).refresh();
    }

    @Test
    public void testSetName() throws Exception {
        final String newDirName = "foo";
        Mockito.when(mockRepositoryDirectory.getName()).thenReturn("dirName");
        uiRepDir.setName(newDirName, true);
        Mockito.verify(mockRepository).renameRepositoryDirectory(mockRepositoryDirectory.getObjectId(), null, newDirName);
        uiPurRepDir.setName(newDirName, true);
        Mockito.verify(mockPurRepository).renameRepositoryDirectory(mockRepositoryDirectory.getObjectId(), null, newDirName, true);
    }

    @Test
    public void testAccess() throws Exception {
        Mockito.when(mockAclService.hasAccess(mockObjectId, READ)).thenReturn(true);
        Mockito.when(mockAclService.hasAccess(mockObjectId, WRITE)).thenReturn(false);
        Assert.assertTrue(uiPurRepDir.hasAccess(READ));
        Assert.assertFalse(uiPurRepDir.hasAccess(WRITE));
    }

    @Test
    public void testBadConstructor() throws Exception {
        Mockito.when(mockRepository.hasService(IAclService.class)).thenReturn(false);
        try {
            new UIEERepositoryDirectory(mockRepositoryDirectory, mockParent, mockRepository);
            Assert.fail("Expected an exception");
        } catch (IllegalStateException e) {
            // Test Succeeded if here
        }
    }
}

