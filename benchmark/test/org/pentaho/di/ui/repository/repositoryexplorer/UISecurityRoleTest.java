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
import org.pentaho.di.repository.UserInfo;
import org.pentaho.di.repository.pur.model.EERoleInfo;
import org.pentaho.di.ui.repository.pur.repositoryexplorer.model.UISecurityRole;
import org.pentaho.di.ui.repository.repositoryexplorer.model.IUIUser;


public class UISecurityRoleTest implements Serializable {
    static final long serialVersionUID = -5870525772742725819L;/* EESOURCE: UPDATE SERIALVERUID */


    private List<UserInfo> users = new ArrayList<UserInfo>();

    private List<EERoleInfo> roles = new ArrayList<EERoleInfo>();

    UserInfo joeUser;

    UserInfo patUser;

    UserInfo suzyUser;

    UserInfo tiffanyUser;

    EERoleInfo adminRole = new EERoleInfo("Admin", "Super User");

    EERoleInfo anonymousRole = new EERoleInfo("Anonymous", "User has not logged in");

    EERoleInfo authenticatedRole = new EERoleInfo("Authenticated", "User has logged in");

    EERoleInfo ceoRole = new EERoleInfo("ceo", "Chief Executive Officer");

    EERoleInfo ctoRole = new EERoleInfo("cto", "Chief Technology Officer");

    EERoleInfo devRole = new EERoleInfo("dev", "Developer");

    EERoleInfo devmgrRole = new EERoleInfo("devmgr", "Development Manager");

    EERoleInfo isRole = new EERoleInfo("is", "Information Services");

    @Test
    public void testAddRole() throws Exception {
        try {
            UISecurityRole role = new UISecurityRole();
            List<IUIUser> rusers = new ArrayList<IUIUser>();
            for (UserInfo userInfo : users) {
                rusers.add(new org.pentaho.di.ui.repository.pur.repositoryexplorer.model.UIEERepositoryUser(userInfo));
            }
            role.setAvailableUsers(rusers);
            role.setMode(ADD);
            role.setName("newrole");
            role.setDescription("new description");
            List<Object> usersToAssign = new ArrayList<Object>();
            usersToAssign.add(new org.pentaho.di.ui.repository.repositoryexplorer.model.UIRepositoryUser(suzyUser));
            usersToAssign.add(new org.pentaho.di.ui.repository.repositoryexplorer.model.UIRepositoryUser(tiffanyUser));
            usersToAssign.add(new org.pentaho.di.ui.repository.repositoryexplorer.model.UIRepositoryUser(joeUser));
            role.assignUsers(usersToAssign);
            Assert.assertEquals(role.getMode(), ADD);// Should have exactly 7 roles

            Assert.assertEquals(role.getName(), "newrole");// $NON-NLS-1$

            Assert.assertEquals(role.getDescription(), "new description");// $NON-NLS-1$

            Assert.assertTrue(contains(role.getAssignedUsers(), new org.pentaho.di.ui.repository.repositoryexplorer.model.UIRepositoryUser(joeUser)));
            Assert.assertTrue(contains(role.getAssignedUsers(), new org.pentaho.di.ui.repository.repositoryexplorer.model.UIRepositoryUser(tiffanyUser)));
            Assert.assertTrue(contains(role.getAssignedUsers(), new org.pentaho.di.ui.repository.repositoryexplorer.model.UIRepositoryUser(suzyUser)));
        } catch (Exception e) {
            Assert.fail();
        }
    }

    @Test
    public void testEditRole() throws Exception {
        try {
            UISecurityRole role = new UISecurityRole();
            List<IUIUser> rusers = new ArrayList<IUIUser>();
            for (UserInfo userInfo : users) {
                rusers.add(new org.pentaho.di.ui.repository.pur.repositoryexplorer.model.UIEERepositoryUser(userInfo));
            }
            role.setRole(new org.pentaho.di.ui.repository.pur.repositoryexplorer.model.UIRepositoryRole(adminRole), rusers);
            role.setMode(EDIT);
            role.setDescription("new description");
            List<Object> usersToAssign = new ArrayList<Object>();
            usersToAssign.add(new org.pentaho.di.ui.repository.repositoryexplorer.model.UIRepositoryUser(suzyUser));
            usersToAssign.add(new org.pentaho.di.ui.repository.repositoryexplorer.model.UIRepositoryUser(tiffanyUser));
            List<Object> usersToUnAssign = new ArrayList<Object>();
            usersToUnAssign.add(new org.pentaho.di.ui.repository.repositoryexplorer.model.UIRepositoryUser(patUser));
            usersToUnAssign.add(new org.pentaho.di.ui.repository.repositoryexplorer.model.UIRepositoryUser(joeUser));
            role.assignUsers(usersToAssign);
            role.unassignUsers(usersToUnAssign);
            Assert.assertEquals(role.getMode(), EDIT);// Should have exactly 7 roles

            Assert.assertEquals(role.getDescription(), "new description");// $NON-NLS-1$

            Assert.assertFalse(contains(role.getAssignedUsers(), new org.pentaho.di.ui.repository.repositoryexplorer.model.UIRepositoryUser(joeUser)));
            Assert.assertFalse(contains(role.getAssignedUsers(), new org.pentaho.di.ui.repository.repositoryexplorer.model.UIRepositoryUser(patUser)));
            Assert.assertTrue(contains(role.getAssignedUsers(), new org.pentaho.di.ui.repository.repositoryexplorer.model.UIRepositoryUser(tiffanyUser)));
            Assert.assertTrue(contains(role.getAssignedUsers(), new org.pentaho.di.ui.repository.repositoryexplorer.model.UIRepositoryUser(suzyUser)));
        } catch (Exception e) {
            Assert.fail();
        }
    }
}

