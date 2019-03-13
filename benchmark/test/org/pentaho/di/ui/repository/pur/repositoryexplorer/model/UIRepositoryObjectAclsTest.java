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


import RepositoryFilePermission.DELETE;
import RepositoryFilePermission.READ;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.pentaho.di.repository.ObjectRecipient.Type;
import org.pentaho.di.repository.pur.model.ObjectAcl;
import org.pentaho.di.repository.pur.model.RepositoryObjectAcl;


/**
 *
 *
 * @author tkafalas
 */
public class UIRepositoryObjectAclsTest {
    private final String RECIPIENT0 = "Looney Tunes";

    private final String RECIPIENT1 = "Elmer Fudd";

    private final String RECIPIENT2 = "Bug Bunny";

    private final String RECIPIENT3 = "Daffy Duck";

    UIRepositoryObjectAcls repositoryObjectAcls;

    RepositoryObjectAcl repObjectAcl;

    UIRepositoryObjectAcl objectAcl1;

    UIRepositoryObjectAcl objectAcl2;

    UIRepositoryObjectAcl objectAcl3;

    @Test
    public void testSetObjectAcl() {
        ObjectAcl objectAcl = repositoryObjectAcls.getObjectAcl();
        Assert.assertEquals(repObjectAcl, objectAcl);
    }

    @Test
    public void testSetAndGetAcls() {
        List<UIRepositoryObjectAcl> originalUIAcls = Arrays.asList(new UIRepositoryObjectAcl[]{ objectAcl1, objectAcl2 });
        // Call the method being tested
        repositoryObjectAcls.setAcls(originalUIAcls);
        // Assert that the two acls are present
        assertListMatches(originalUIAcls, repositoryObjectAcls.getAcls());
        Assert.assertEquals(objectAcl1, repositoryObjectAcls.getAcl(RECIPIENT1));
        Assert.assertNull(repositoryObjectAcls.getAcl("not there"));
    }

    @Test
    public void testAddAndRemoveAcls() {
        List<UIRepositoryObjectAcl> originalUIAcls = Arrays.asList(new UIRepositoryObjectAcl[]{ objectAcl1, objectAcl2, objectAcl3 });
        // Call the method being tested
        repositoryObjectAcls.addAcls(originalUIAcls);
        // Assert that the two acls are present
        assertListMatches(originalUIAcls, repositoryObjectAcls.getAcls());
        repositoryObjectAcls.removeAcls(Arrays.asList(new UIRepositoryObjectAcl[]{ objectAcl1, objectAcl3 }));
        assertListMatches(Arrays.asList(new UIRepositoryObjectAcl[]{ objectAcl2 }), repositoryObjectAcls.getAcls());
        repositoryObjectAcls.addDefaultAcl(objectAcl1);
        // The permissions in the acls (in the list also) will be set to READ only.
        Assert.assertEquals(EnumSet.of(READ), objectAcl1.getPermissionSet());
        assertListMatches(Arrays.asList(new UIRepositoryObjectAcl[]{ objectAcl1, objectAcl2 }), repositoryObjectAcls.getAcls());
        repositoryObjectAcls.removeAcl(objectAcl1.getRecipientName());
        repositoryObjectAcls.removeAcl(objectAcl2.getRecipientName());
        Assert.assertEquals(0, repositoryObjectAcls.getAcls().size());
        repositoryObjectAcls.addDefaultAcls(originalUIAcls);
        // The permissions in the acls (in the list also) will be set to READ only.
        Assert.assertEquals(EnumSet.of(READ), objectAcl1.getPermissionSet());
        assertListMatches(originalUIAcls, repositoryObjectAcls.getAcls());
    }

    @Test
    public void testAddAndRemoveAcl() {
        List<UIRepositoryObjectAcl> originalUIAcls = Arrays.asList(new UIRepositoryObjectAcl[]{ objectAcl1, objectAcl2 });
        // Call the method being tested
        repositoryObjectAcls.addAcl(objectAcl1);
        repositoryObjectAcls.addAcl(objectAcl2);
        // Assert that the two acls are present
        assertListMatches(originalUIAcls, repositoryObjectAcls.getAcls());
        repositoryObjectAcls.removeAcl(RECIPIENT2);
        assertListMatches(Arrays.asList(new UIRepositoryObjectAcl[]{ objectAcl1 }), repositoryObjectAcls.getAcls());
        repositoryObjectAcls.removeAcl(RECIPIENT1);
        Assert.assertEquals(0, repositoryObjectAcls.getAcls().size());
    }

    @Test
    public void testUpdateAcl() {
        List<UIRepositoryObjectAcl> originalUIAcls = Arrays.asList(new UIRepositoryObjectAcl[]{ objectAcl1, objectAcl2 });
        repositoryObjectAcls.addAcls(originalUIAcls);
        objectAcl2.addPermission(DELETE);
        repositoryObjectAcls.updateAcl(objectAcl2);
        // Assert that the delete permissions is added
        for (UIRepositoryObjectAcl uiAcl : repositoryObjectAcls.getAcls()) {
            if (objectAcl2.getRecipientName().equals(uiAcl.getRecipientName())) {
                Assert.assertEquals("Delete permission was not added", objectAcl2.getPermissionSet(), uiAcl.getPermissionSet());
            }
        }
    }

    @Test
    public void testSetRemoveSelectedAcls() {
        List<UIRepositoryObjectAcl> originalUIAcls = Arrays.asList(new UIRepositoryObjectAcl[]{ objectAcl1, objectAcl2, objectAcl3 });
        repositoryObjectAcls.addAcls(originalUIAcls);
        List<UIRepositoryObjectAcl> selectedAcls = Arrays.asList(new UIRepositoryObjectAcl[]{ objectAcl1, objectAcl3 });
        // Call the method being tested
        repositoryObjectAcls.setSelectedAclList(selectedAcls);
        assertListMatches(selectedAcls, repositoryObjectAcls.getSelectedAclList());
        repositoryObjectAcls.removeSelectedAcls();
        assertListMatches(Arrays.asList(new UIRepositoryObjectAcl[]{ objectAcl2 }), repositoryObjectAcls.getAcls());
    }

    @Test
    public void testClear() {
        List<UIRepositoryObjectAcl> originalUIAcls = Arrays.asList(new UIRepositoryObjectAcl[]{ objectAcl1, objectAcl2, objectAcl3 });
        repositoryObjectAcls.addAcls(originalUIAcls);
        repositoryObjectAcls.setRemoveEnabled(true);
        Assert.assertTrue(repositoryObjectAcls.isRemoveEnabled());
        // Call the method being tested
        repositoryObjectAcls.clear();
        Assert.assertTrue(repositoryObjectAcls.getSelectedAclList().isEmpty());
        Assert.assertFalse(repositoryObjectAcls.isRemoveEnabled());
        Assert.assertFalse(repositoryObjectAcls.isModelDirty());
        Assert.assertTrue(repositoryObjectAcls.isEntriesInheriting());
    }

    @Test
    public void testBooleanFlags() {
        List<UIRepositoryObjectAcl> originalUIAcls = Arrays.asList(new UIRepositoryObjectAcl[]{ objectAcl1, objectAcl2, objectAcl3 });
        UIRepositoryObjectAcls repositoryObjectAcls = new UIRepositoryObjectAcls();
        repositoryObjectAcls.setObjectAcl(new RepositoryObjectAcl(new org.pentaho.di.repository.pur.model.RepositoryObjectRecipient(RECIPIENT1, Type.USER)));
        Assert.assertFalse(repositoryObjectAcls.isModelDirty());
        Assert.assertFalse(repositoryObjectAcls.isRemoveEnabled());
        Assert.assertFalse(repositoryObjectAcls.hasManageAclAccess());
        Assert.assertTrue(repositoryObjectAcls.isEntriesInheriting());
        repositoryObjectAcls.addAcls(originalUIAcls);
        Assert.assertTrue(repositoryObjectAcls.isModelDirty());
        Assert.assertTrue(repositoryObjectAcls.isEntriesInheriting());
        Assert.assertFalse(repositoryObjectAcls.isRemoveEnabled());
        repositoryObjectAcls.setRemoveEnabled(true);
        Assert.assertTrue(repositoryObjectAcls.isRemoveEnabled());
        repositoryObjectAcls.setModelDirty(true);
        Assert.assertTrue(repositoryObjectAcls.isModelDirty());
        Assert.assertTrue(repositoryObjectAcls.isRemoveEnabled());
        Assert.assertTrue(repositoryObjectAcls.isEntriesInheriting());
        repositoryObjectAcls.setModelDirty(false);
        Assert.assertFalse(repositoryObjectAcls.isModelDirty());
        Assert.assertTrue(repositoryObjectAcls.isEntriesInheriting());
        repositoryObjectAcls.setEntriesInheriting(true);
        Assert.assertFalse(repositoryObjectAcls.isModelDirty());
        Assert.assertTrue(repositoryObjectAcls.isEntriesInheriting());
        repositoryObjectAcls.setEntriesInheriting(false);
        Assert.assertTrue(repositoryObjectAcls.isModelDirty());
        Assert.assertFalse(repositoryObjectAcls.isRemoveEnabled());
        Assert.assertFalse(repositoryObjectAcls.isEntriesInheriting());
        repositoryObjectAcls.setHasManageAclAccess(true);
        Assert.assertTrue(repositoryObjectAcls.hasManageAclAccess());
    }

    @Test
    public void testGetOwner() {
        Assert.assertEquals(RECIPIENT0, repositoryObjectAcls.getOwner().getName());
        repositoryObjectAcls = new UIRepositoryObjectAcls();
        Assert.assertNull(repositoryObjectAcls.getOwner());
    }

    @Test
    public void testGetAceIndex() {
        List<UIRepositoryObjectAcl> originalUIAcls = Arrays.asList(new UIRepositoryObjectAcl[]{ objectAcl1, objectAcl2, objectAcl3 });
        repositoryObjectAcls.addAcls(originalUIAcls);
        int i = repositoryObjectAcls.getAceIndex(objectAcl2.getAce());
        Assert.assertTrue(objectAcl2.equals(repositoryObjectAcls.getAcls().get(i)));
    }
}

