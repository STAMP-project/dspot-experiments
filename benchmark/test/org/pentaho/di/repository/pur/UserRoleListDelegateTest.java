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


import java.io.Serializable;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.pentaho.platform.api.engine.security.userroledao.UserRoleInfo;
import org.pentaho.platform.core.mt.Tenant;
import org.pentaho.platform.security.userrole.ws.IUserRoleListWebService;


public class UserRoleListDelegateTest implements Serializable {
    static final long serialVersionUID = -125535373810768433L;/* EESOURCE: UPDATE SERIALVERUID */


    UserRoleListDelegate listDelegate;

    IUserRoleListWebService service;

    static List<String> roles;

    static List<String> users;

    @Test
    public void testService() throws Exception {
        Assert.assertEquals(listDelegate.getAllUsers().size(), 4);
        Assert.assertEquals(listDelegate.getAllRoles().size(), 7);
        Assert.assertEquals(listDelegate.getUserRoleInfo().getRoles().size(), 7);
        Assert.assertEquals(listDelegate.getUserRoleInfo().getUsers().size(), 4);
    }

    public static class UserDetailsRoleListService implements IUserRoleListWebService {
        public List<String> getAllRoles() {
            return UserRoleListDelegateTest.roles;
        }

        public List<String> getAllUsers() {
            return UserRoleListDelegateTest.users;
        }

        public UserRoleInfo getUserRoleInfo() {
            UserRoleInfo info = new UserRoleInfo();
            info.setRoles(UserRoleListDelegateTest.roles);
            info.setUsers(UserRoleListDelegateTest.users);
            return info;
        }

        @Override
        public List<String> getAllRolesForTenant(Tenant arg0) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public List<String> getAllUsersForTenant(Tenant arg0) {
            // TODO Auto-generated method stub
            return null;
        }
    }
}

