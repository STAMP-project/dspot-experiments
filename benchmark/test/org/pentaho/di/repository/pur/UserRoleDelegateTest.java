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


import org.apache.commons.logging.Log;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.repository.IUser;
import org.pentaho.di.repository.UserInfo;
import org.pentaho.di.repository.pur.model.EERoleInfo;
import org.pentaho.di.repository.pur.model.IRole;
import org.pentaho.platform.security.userrole.ws.IUserRoleListWebService;
import org.pentaho.platform.security.userroledao.ws.IUserRoleWebService;
import org.pentaho.platform.security.userroledao.ws.ProxyPentahoRole;
import org.pentaho.platform.security.userroledao.ws.ProxyPentahoUser;


/**
 *
 *
 * @author Andrey Khayrutdinov
 */
public class UserRoleDelegateTest {
    @Mock
    private Log log;

    @Mock
    private IUserRoleListWebService roleListWebService;

    @Mock
    private IUserRoleWebService roleWebService;

    private UserRoleDelegate delegate;

    @Test(expected = KettleException.class)
    public void createUser_ProhibitsToCreate_WhenNameCollides() throws Exception {
        final String name = "user";
        IUser existing = new UserInfo(name);
        Mockito.when(roleWebService.getUsers()).thenReturn(new ProxyPentahoUser[]{ UserRoleHelper.convertToPentahoProxyUser(existing) });
        delegate.createUser(new UserInfo(name));
    }

    @Test
    public void createUser_CreatesSuccessfully_WhenNameIsUnique() throws Exception {
        final String name = "user";
        delegate.createUser(new UserInfo(name));
        Mockito.verify(roleWebService).createUser(ArgumentMatchers.any(ProxyPentahoUser.class));
    }

    @Test
    public void createUser_CreatesSuccessfully_WhenNameDiffersInCase() throws Exception {
        final String name = "user";
        final String upperCased = name.toUpperCase();
        IUser existing = new UserInfo(upperCased);
        Mockito.when(roleWebService.getUsers()).thenReturn(new ProxyPentahoUser[]{ UserRoleHelper.convertToPentahoProxyUser(existing) });
        delegate.createUser(new UserInfo(name));
        Mockito.verify(roleWebService).createUser(ArgumentMatchers.any(ProxyPentahoUser.class));
    }

    @Test(expected = KettleException.class)
    public void createRole_ProhibitsToCreate_WhenNameCollides() throws Exception {
        final String name = "role";
        IRole existing = new EERoleInfo(name);
        Mockito.when(roleWebService.getRoles()).thenReturn(new ProxyPentahoRole[]{ UserRoleHelper.convertToPentahoProxyRole(existing) });
        delegate.createRole(new EERoleInfo(name));
    }

    @Test
    public void createRole_CreatesSuccessfully_WhenNameIsUnique() throws Exception {
        final String name = "role";
        delegate.createRole(new EERoleInfo(name));
        Mockito.verify(roleWebService).createRole(ArgumentMatchers.any(ProxyPentahoRole.class));
    }

    @Test(expected = KettleException.class)
    public void createRole_CreatesSuccessfully_WhenNameDiffersInCase() throws Exception {
        final String name = "role";
        final String upperCased = name.toUpperCase();
        IRole existing = new EERoleInfo(upperCased);
        Mockito.when(roleWebService.getRoles()).thenReturn(new ProxyPentahoRole[]{ UserRoleHelper.convertToPentahoProxyRole(existing) });
        delegate.createRole(new EERoleInfo(name));
        Mockito.verify(roleWebService).createRole(ArgumentMatchers.any(ProxyPentahoRole.class));
    }
}

