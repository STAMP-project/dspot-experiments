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
package org.pentaho.di.ui.repository.repositoryexplorer;


import Mode.ADD;
import Mode.EDIT;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.pentaho.di.repository.RepositorySecurityManager;
import org.pentaho.di.repository.pur.model.EERoleInfo;
import org.pentaho.di.repository.pur.model.EEUserInfo;
import org.pentaho.di.repository.pur.model.IRole;
import org.pentaho.di.ui.repository.pur.repositoryexplorer.IUIRole;
import org.pentaho.di.ui.repository.pur.repositoryexplorer.model.UIEESecurityUser;


public class UIEESecurityUserTest implements Serializable {
    static final long serialVersionUID = -7328513894400990825L;/* EESOURCE: UPDATE SERIALVERUID */


    RepositorySecurityManager sm;

    private List<EEUserInfo> users = new ArrayList<EEUserInfo>();

    private List<IRole> roles = new ArrayList<IRole>();

    EEUserInfo joeUser;

    EEUserInfo patUser;

    EEUserInfo suzyUser;

    EEUserInfo tiffanyUser;

    EERoleInfo adminRole = new EERoleInfo("Admin", "Super User");

    EERoleInfo anonymousRole = new EERoleInfo("Anonymous", "User has not logged in");

    EERoleInfo authenticatedRole = new EERoleInfo("Authenticated", "User has logged in");

    EERoleInfo ceoRole = new EERoleInfo("ceo", "Chief Executive Officer");

    EERoleInfo ctoRole = new EERoleInfo("cto", "Chief Technology Officer");

    EERoleInfo devRole = new EERoleInfo("dev", "Developer");

    EERoleInfo devmgrRole = new EERoleInfo("devmgr", "Development Manager");

    EERoleInfo isRole = new EERoleInfo("is", "Information Services");

    @Test
    public void testEditUser() throws Exception {
        try {
            UIEESecurityUser user = new UIEESecurityUser(sm);
            List<IUIRole> rroles = new ArrayList<IUIRole>();
            for (IRole EERoleInfo : roles) {
                rroles.add(new org.pentaho.di.ui.repository.pur.repositoryexplorer.model.UIRepositoryRole(EERoleInfo));
            }
            user.setUser(new org.pentaho.di.ui.repository.repositoryexplorer.model.UIRepositoryUser(joeUser), rroles);
            user.setMode(EDIT);
            user.setPassword("newpassword");
            user.setDescription("new description");
            List<Object> rolesToAssign1 = new ArrayList<Object>();
            rolesToAssign1.add(new org.pentaho.di.ui.repository.pur.repositoryexplorer.model.UIRepositoryRole(adminRole));
            rolesToAssign1.add(new org.pentaho.di.ui.repository.pur.repositoryexplorer.model.UIRepositoryRole(authenticatedRole));
            user.assignRoles(rolesToAssign1);
            List<Object> rolesToAssign = new ArrayList<Object>();
            rolesToAssign.add(new org.pentaho.di.ui.repository.pur.repositoryexplorer.model.UIRepositoryRole(ctoRole));
            rolesToAssign.add(new org.pentaho.di.ui.repository.pur.repositoryexplorer.model.UIRepositoryRole(isRole));
            List<Object> rolesToUnAssign = new ArrayList<Object>();
            rolesToUnAssign.add(new org.pentaho.di.ui.repository.pur.repositoryexplorer.model.UIRepositoryRole(adminRole));
            rolesToUnAssign.add(new org.pentaho.di.ui.repository.pur.repositoryexplorer.model.UIRepositoryRole(authenticatedRole));
            user.assignRoles(rolesToAssign);
            user.unassignRoles(rolesToUnAssign);
            Assert.assertEquals(user.getMode(), EDIT);// Should have exactly 7 roles

            Assert.assertEquals(user.getPassword(), "newpassword");// $NON-NLS-1$

            Assert.assertEquals(user.getDescription(), "new description");// $NON-NLS-1$

            Assert.assertFalse(contains(user.getAssignedRoles(), new org.pentaho.di.ui.repository.pur.repositoryexplorer.model.UIRepositoryRole(adminRole)));
            Assert.assertFalse(contains(user.getAssignedRoles(), new org.pentaho.di.ui.repository.pur.repositoryexplorer.model.UIRepositoryRole(authenticatedRole)));
            Assert.assertTrue(contains(user.getAssignedRoles(), new org.pentaho.di.ui.repository.pur.repositoryexplorer.model.UIRepositoryRole(ctoRole)));
            Assert.assertTrue(contains(user.getAssignedRoles(), new org.pentaho.di.ui.repository.pur.repositoryexplorer.model.UIRepositoryRole(isRole)));
        } catch (Exception e) {
            Assert.fail();
        }
    }

    @Test
    public void testAddUser() throws Exception {
        try {
            UIEESecurityUser user = new UIEESecurityUser(sm);
            List<IUIRole> rroles = new ArrayList<IUIRole>();
            for (IRole EERoleInfo : roles) {
                rroles.add(new org.pentaho.di.ui.repository.pur.repositoryexplorer.model.UIRepositoryRole(EERoleInfo));
            }
            user.clear();
            user.setAvailableRoles(rroles);
            user.setMode(ADD);
            user.setName("newuser");
            user.setPassword("newpassword");
            user.setDescription("new description");
            List<Object> rolesToAssign = new ArrayList<Object>();
            rolesToAssign.add(new org.pentaho.di.ui.repository.pur.repositoryexplorer.model.UIRepositoryRole(ctoRole));
            rolesToAssign.add(new org.pentaho.di.ui.repository.pur.repositoryexplorer.model.UIRepositoryRole(isRole));
            rolesToAssign.add(new org.pentaho.di.ui.repository.pur.repositoryexplorer.model.UIRepositoryRole(adminRole));
            rolesToAssign.add(new org.pentaho.di.ui.repository.pur.repositoryexplorer.model.UIRepositoryRole(authenticatedRole));
            user.assignRoles(rolesToAssign);
            Assert.assertEquals(user.getMode(), ADD);
            Assert.assertEquals(user.getName(), "newuser");// $NON-NLS-1$

            Assert.assertEquals(user.getPassword(), "newpassword");// $NON-NLS-1$

            Assert.assertEquals(user.getDescription(), "new description");// $NON-NLS-1$

            Assert.assertTrue(contains(user.getAssignedRoles(), new org.pentaho.di.ui.repository.pur.repositoryexplorer.model.UIRepositoryRole(adminRole)));
            Assert.assertTrue(contains(user.getAssignedRoles(), new org.pentaho.di.ui.repository.pur.repositoryexplorer.model.UIRepositoryRole(authenticatedRole)));
            Assert.assertTrue(contains(user.getAssignedRoles(), new org.pentaho.di.ui.repository.pur.repositoryexplorer.model.UIRepositoryRole(ctoRole)));
            Assert.assertTrue(contains(user.getAssignedRoles(), new org.pentaho.di.ui.repository.pur.repositoryexplorer.model.UIRepositoryRole(isRole)));
        } catch (Exception e) {
            Assert.fail();
        }
    }
}

