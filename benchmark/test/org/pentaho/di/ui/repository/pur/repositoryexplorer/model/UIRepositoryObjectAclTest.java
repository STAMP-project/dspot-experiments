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


import ObjectRecipient.Type;
import ObjectRecipient.Type.ROLE;
import ObjectRecipient.Type.USER;
import RepositoryFilePermission.ALL;
import RepositoryFilePermission.DELETE;
import RepositoryFilePermission.READ;
import RepositoryFilePermission.WRITE;
import java.util.EnumSet;
import org.junit.Assert;
import org.junit.Test;
import org.pentaho.platform.api.repository2.unified.RepositoryFilePermission;


/**
 *
 *
 * @author tkafalas
 */
public class UIRepositoryObjectAclTest {
    private final String RECIPIENT1 = "Elmer Fudd";

    private final String RECIPIENT2 = "Bugs Bunny";

    @Test
    public void testGetPermissionSet() {
        UIRepositoryObjectAcl uiAcl = new UIRepositoryObjectAcl(createObjectAce());
        EnumSet<RepositoryFilePermission> permissions = uiAcl.getPermissionSet();
        Assert.assertNotNull(permissions);
        Assert.assertEquals(1, permissions.size());
        Assert.assertTrue(permissions.contains(ALL));
    }

    @Test
    public void testSetPermission() {
        UIRepositoryObjectAcl uiAcl = new UIRepositoryObjectAcl(createObjectAce());
        uiAcl.setPermissionSet(READ, WRITE);
        EnumSet<RepositoryFilePermission> permissions = uiAcl.getPermissionSet();
        Assert.assertNotNull(permissions);
        Assert.assertEquals(2, permissions.size());
        Assert.assertTrue(permissions.contains(READ));
        Assert.assertTrue(permissions.contains(WRITE));
        uiAcl.setPermissionSet(EnumSet.of(DELETE, WRITE));
        permissions = uiAcl.getPermissionSet();
        Assert.assertNotNull(permissions);
        Assert.assertEquals(2, permissions.size());
        Assert.assertTrue(permissions.contains(DELETE));
        Assert.assertTrue(permissions.contains(WRITE));
        uiAcl.addPermission(READ);
        permissions = uiAcl.getPermissionSet();
        Assert.assertEquals(permissions, EnumSet.of(READ, DELETE, WRITE));
        uiAcl.removePermission(READ);
        permissions = uiAcl.getPermissionSet();
        Assert.assertEquals(permissions, EnumSet.of(DELETE, WRITE));
    }

    @Test
    public void testEquals() {
        UIRepositoryObjectAcl uiAcl1 = new UIRepositoryObjectAcl(createObjectAce());
        UIRepositoryObjectAcl uiAcl2 = new UIRepositoryObjectAcl(new org.pentaho.di.repository.pur.model.RepositoryObjectAce(new org.pentaho.di.repository.pur.model.RepositoryObjectRecipient(RECIPIENT1, Type.USER), EnumSet.of(ALL)));
        Assert.assertTrue(uiAcl1.equals(uiAcl2));
        uiAcl2 = new UIRepositoryObjectAcl(new org.pentaho.di.repository.pur.model.RepositoryObjectAce(new org.pentaho.di.repository.pur.model.RepositoryObjectRecipient(RECIPIENT1, Type.SYSTEM_ROLE), EnumSet.of(ALL)));
        Assert.assertFalse(uiAcl1.equals(uiAcl2));
        uiAcl2 = new UIRepositoryObjectAcl(new org.pentaho.di.repository.pur.model.RepositoryObjectAce(new org.pentaho.di.repository.pur.model.RepositoryObjectRecipient(RECIPIENT1, Type.USER), EnumSet.of(READ, ALL)));
        Assert.assertFalse(uiAcl1.equals(uiAcl2));
        uiAcl2 = new UIRepositoryObjectAcl(new org.pentaho.di.repository.pur.model.RepositoryObjectAce(new org.pentaho.di.repository.pur.model.RepositoryObjectRecipient(RECIPIENT2, Type.USER), EnumSet.of(ALL)));
        Assert.assertFalse(uiAcl1.equals(uiAcl2));
        Assert.assertFalse(uiAcl1.equals(null));
    }

    @Test
    public void testRecipient() {
        UIRepositoryObjectAcl uiAcl = new UIRepositoryObjectAcl(createObjectAce());
        Assert.assertEquals(RECIPIENT1, uiAcl.getRecipientName());
        uiAcl.setRecipientName(RECIPIENT2);
        Assert.assertEquals(RECIPIENT2, uiAcl.getRecipientName());
    }

    @Test
    public void testRecipientType() {
        UIRepositoryObjectAcl uiAcl = new UIRepositoryObjectAcl(createObjectAce());
        Assert.assertEquals(USER, uiAcl.getRecipientType());
        uiAcl.setRecipientType(ROLE);
        Assert.assertEquals(ROLE, uiAcl.getRecipientType());
    }

    @Test
    public void testToString() {
        UIRepositoryObjectAcl uiAcl = new UIRepositoryObjectAcl(createObjectAce());
        String s = uiAcl.toString();
        Assert.assertNotNull(s);
        Assert.assertTrue(s.contains(RECIPIENT1));
    }
}

