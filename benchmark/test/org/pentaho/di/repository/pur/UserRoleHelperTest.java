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
package org.pentaho.di.repository.pur;


import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.repository.IUser;
import org.pentaho.di.repository.pur.model.EEUserInfo;
import org.pentaho.di.ui.repository.pur.services.IRoleSupportSecurityManager;
import org.pentaho.platform.security.userroledao.ws.ProxyPentahoUser;
import org.pentaho.platform.security.userroledao.ws.UserRoleSecurityInfo;
import org.pentaho.platform.security.userroledao.ws.UserToRoleAssignment;


/**
 *
 *
 * @author Andrey Khayrutdinov
 */
public class UserRoleHelperTest {
    @Test
    public void convertFromProxyPentahoUser_RetunsNull_WhenErrorOccurs() throws Exception {
        IRoleSupportSecurityManager manager = Mockito.mock(IRoleSupportSecurityManager.class);
        Mockito.when(manager.constructUser()).thenThrow(new KettleException());
        IUser user = UserRoleHelper.convertFromProxyPentahoUser(new ProxyPentahoUser(), Collections.<UserToRoleAssignment>emptyList(), manager);
        Assert.assertNull(user);
    }

    @Test
    public void convertFromProxyPentahoUser_CopiesDataFromInput() throws Exception {
        IRoleSupportSecurityManager manager = UserRoleHelperTest.mockSecurityManager(false);
        ProxyPentahoUser pentahoUser = UserRoleHelperTest.pentahoUser("name");
        pentahoUser.setPassword("password");
        pentahoUser.setDescription("desc");
        pentahoUser.setEnabled(true);
        IUser user = UserRoleHelper.convertFromProxyPentahoUser(pentahoUser, Collections.<UserToRoleAssignment>emptyList(), manager);
        Assert.assertNotNull(user);
        Assert.assertEquals(pentahoUser.getName(), user.getName());
        Assert.assertEquals(pentahoUser.getName(), user.getLogin());
        Assert.assertEquals(pentahoUser.getPassword(), user.getPassword());
        Assert.assertEquals(pentahoUser.getDescription(), user.getDescription());
        Assert.assertEquals(pentahoUser.getEnabled(), user.isEnabled());
    }

    @Test
    public void convertFromProxyPentahoUser_CopiesRolesForEeUser() throws Exception {
        IRoleSupportSecurityManager manager = UserRoleHelperTest.mockSecurityManager(true);
        ProxyPentahoUser pentahoUser = UserRoleHelperTest.pentahoUser("name");
        List<UserToRoleAssignment> assignments = Collections.singletonList(new UserToRoleAssignment("name", "role"));
        EEUserInfo user = ((EEUserInfo) (UserRoleHelper.convertFromProxyPentahoUser(pentahoUser, assignments, manager)));
        Assert.assertNotNull(user);
        Assert.assertEquals(pentahoUser.getName(), user.getName());
        Assert.assertEquals(1, user.getRoles().size());
        Assert.assertEquals("role", user.getRoles().iterator().next().getName());
    }

    @Test
    public void convertFromProxyPentahoUsers_ReturnsEmptyList_WhenUsersAreAbsent() throws Exception {
        UserRoleSecurityInfo info = new UserRoleSecurityInfo();
        info.setUsers(null);
        IRoleSupportSecurityManager manager = UserRoleHelperTest.mockSecurityManager(false);
        List<IUser> users = UserRoleHelper.convertFromProxyPentahoUsers(info, manager);
        Assert.assertNotNull(users);
        Assert.assertTrue(users.isEmpty());
    }

    @Test
    public void convertFromProxyPentahoUsers_CopiesEachUser() throws Exception {
        UserRoleSecurityInfo info = new UserRoleSecurityInfo();
        info.setUsers(Arrays.asList(UserRoleHelperTest.pentahoUser("user1"), UserRoleHelperTest.pentahoUser("user2")));
        IRoleSupportSecurityManager manager = UserRoleHelperTest.mockSecurityManager(false);
        List<IUser> users = UserRoleHelper.convertFromProxyPentahoUsers(info, manager);
        Assert.assertNotNull(users);
        Assert.assertEquals(2, users.size());
        Assert.assertEquals("user1", users.get(0).getName());
        Assert.assertEquals("user2", users.get(1).getName());
    }
}

