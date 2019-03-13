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


import ObjectRecipient.Type.ROLE;
import ObjectRecipient.Type.USER;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;
import org.pentaho.di.repository.IUser;
import org.pentaho.di.repository.pur.model.EERoleInfo;
import org.pentaho.di.repository.pur.model.EEUserInfo;
import org.pentaho.di.repository.pur.model.IRole;
import org.pentaho.di.ui.repository.pur.repositoryexplorer.IUIRole;
import org.pentaho.di.ui.repository.pur.repositoryexplorer.model.UIEERepositoryUser;
import org.pentaho.di.ui.repository.pur.repositoryexplorer.model.UIEESecurity;
import org.pentaho.di.ui.repository.pur.repositoryexplorer.model.UIRepositoryRole;
import org.pentaho.di.ui.repository.repositoryexplorer.model.IUIUser;


public class UIEESecurityTest implements Serializable {
    static final long serialVersionUID = -7889725393698207931L;/* EESOURCE: UPDATE SERIALVERUID */


    private List<IUser> users = new ArrayList<IUser>();

    private List<IRole> roles = new ArrayList<IRole>();

    private UIEESecurity security = new UIEESecurity();

    List<IUIRole> rroles;

    List<IUIUser> rusers;

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
    public void testAddUser() throws Exception {
        try {
            security.setSelectedDeck(USER);
            UIEERepositoryUser userToAdd = new UIEERepositoryUser(new EEUserInfo());
            userToAdd.setName("newuser");
            userToAdd.setPassword("newpassword");
            userToAdd.setDescription("new description");
            Set<IUIRole> rolesToAssign = new HashSet<IUIRole>();
            rolesToAssign.add(new UIRepositoryRole(ctoRole));
            rolesToAssign.add(new UIRepositoryRole(isRole));
            rolesToAssign.add(new UIRepositoryRole(adminRole));
            rolesToAssign.add(new UIRepositoryRole(authenticatedRole));
            userToAdd.setRoles(rolesToAssign);
            security.addUser(userToAdd);
            Assert.assertEquals(security.getSelectedUser(), userToAdd);
            Assert.assertEquals(security.getSelectedDeck(), USER);
            Assert.assertEquals(security.getUserList().size(), 5);
        } catch (Exception e) {
            Assert.fail();
        }
    }

    @Test
    public void testUpdateUser() throws Exception {
        try {
            UIEERepositoryUser userToAdd = new UIEERepositoryUser(new EEUserInfo());
            userToAdd.setName("newuser");
            userToAdd.setPassword("newpassword");
            userToAdd.setDescription("new description");
            Set<IUIRole> rolesToAssign = new HashSet<IUIRole>();
            rolesToAssign.add(new UIRepositoryRole(ctoRole));
            rolesToAssign.add(new UIRepositoryRole(isRole));
            rolesToAssign.add(new UIRepositoryRole(adminRole));
            rolesToAssign.add(new UIRepositoryRole(authenticatedRole));
            userToAdd.setRoles(rolesToAssign);
            security.addUser(userToAdd);
            IUIUser selectedUser = security.getSelectedUser();
            selectedUser.setPassword("newpassword123");
            selectedUser.setDescription("new description 123");
            addRole(new UIRepositoryRole(ctoRole));
            addRole(new UIRepositoryRole(isRole));
            removeRole(new UIRepositoryRole(adminRole));
            removeRole(new UIRepositoryRole(authenticatedRole));
            security.updateUser(selectedUser, rolesToAssign);
            Assert.assertEquals(selectedUser.getPassword(), "newpassword123");// $NON-NLS-1$

            Assert.assertEquals(selectedUser.getDescription(), "new description 123");// $NON-NLS-1$

            Assert.assertEquals(security.getSelectedUser(), selectedUser);
            Assert.assertEquals(security.getUserList().size(), 5);
        } catch (Exception e) {
            Assert.fail();
        }
    }

    @Test
    public void testRemoveUser() throws Exception {
        try {
            UIEERepositoryUser userToAdd = new UIEERepositoryUser(new EEUserInfo());
            userToAdd.setName("newuser");
            userToAdd.setPassword("newpassword");
            userToAdd.setDescription("new description");
            Set<IUIRole> rolesToAssign = new HashSet<IUIRole>();
            rolesToAssign.add(new UIRepositoryRole(ctoRole));
            rolesToAssign.add(new UIRepositoryRole(isRole));
            rolesToAssign.add(new UIRepositoryRole(adminRole));
            rolesToAssign.add(new UIRepositoryRole(authenticatedRole));
            userToAdd.setRoles(rolesToAssign);
            security.addUser(userToAdd);
            // IUIUser selectedUser = security.getSelectedUser();
            int removeUserIndex = security.getSelectedUserIndex();
            security.removeUser("newuser");
            Assert.assertEquals(security.getSelectedUserIndex(), (removeUserIndex - 1));
            Assert.assertEquals(security.getUserList().size(), 4);
        } catch (Exception e) {
            Assert.fail();
        }
    }

    @Test
    public void testAddRole() throws Exception {
        try {
            security.setSelectedDeck(ROLE);
            UIRepositoryRole roleToAdd = new UIRepositoryRole(new EERoleInfo());
            roleToAdd.setName("newrole");
            roleToAdd.setDescription("new description");
            Set<EEUserInfo> usersToAssign = new HashSet<EEUserInfo>();
            usersToAssign.add(suzyUser);
            usersToAssign.add(tiffanyUser);
            usersToAssign.add(joeUser);
            security.addRole(roleToAdd);
            Assert.assertEquals(security.getSelectedRole(), roleToAdd);
            Assert.assertEquals(security.getSelectedDeck(), ROLE);
            Assert.assertEquals(security.getRoleList().size(), 9);
        } catch (Exception e) {
            Assert.fail();
        }
    }

    @Test
    public void testUpdateRole() throws Exception {
        try {
            IUIRole roleToAdd = new UIRepositoryRole(new EERoleInfo());
            roleToAdd.setName("newrole");
            roleToAdd.setDescription("new description");
            Set<IUIUser> usersToAssign = new HashSet<IUIUser>();
            usersToAssign.add(new org.pentaho.di.ui.repository.repositoryexplorer.model.UIRepositoryUser(suzyUser));
            usersToAssign.add(new org.pentaho.di.ui.repository.repositoryexplorer.model.UIRepositoryUser(tiffanyUser));
            usersToAssign.add(new org.pentaho.di.ui.repository.repositoryexplorer.model.UIRepositoryUser(joeUser));
            roleToAdd.setUsers(usersToAssign);
            security.addRole(roleToAdd);
            security.setSelectedRole(findRole("newrole"));
            IUIRole selectedRole = security.getSelectedRole();
            selectedRole.setDescription("new description 123");
            selectedRole.addUser(new org.pentaho.di.ui.repository.repositoryexplorer.model.UIRepositoryUser(patUser));
            selectedRole.removeUser(new org.pentaho.di.ui.repository.repositoryexplorer.model.UIRepositoryUser(suzyUser));
            selectedRole.removeUser(new org.pentaho.di.ui.repository.repositoryexplorer.model.UIRepositoryUser(tiffanyUser));
            security.updateRole(selectedRole, usersToAssign);
            Assert.assertEquals(selectedRole.getDescription(), "new description 123");// $NON-NLS-1$

            Assert.assertEquals(security.getSelectedRole(), selectedRole);
            Assert.assertEquals(security.getRoleList().size(), 9);
        } catch (Exception e) {
            Assert.fail();
        }
    }

    @Test
    public void testRemoveRole() throws Exception {
        try {
            UIRepositoryRole roleToAdd = new UIRepositoryRole(new EERoleInfo());
            roleToAdd.setName("newrole");
            roleToAdd.setDescription("new description");
            Set<EEUserInfo> usersToAssign = new HashSet<EEUserInfo>();
            usersToAssign.add(suzyUser);
            usersToAssign.add(tiffanyUser);
            usersToAssign.add(joeUser);
            security.addRole(roleToAdd);
            // IUIRole selectedRole = security.getSelectedRole();
            int removeRoleIndex = security.getSelectedRoleIndex();
            security.removeRole("newrole");
            Assert.assertEquals(security.getSelectedRoleIndex(), (removeRoleIndex - 1));
            Assert.assertEquals(security.getRoleList().size(), 8);
        } catch (Exception e) {
            Assert.fail();
        }
    }
}

