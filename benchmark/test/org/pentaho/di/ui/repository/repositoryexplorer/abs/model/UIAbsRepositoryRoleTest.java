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
package org.pentaho.di.ui.repository.repositoryexplorer.abs.model;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.pentaho.di.repository.pur.model.AbsRoleInfo;
import org.pentaho.di.repository.pur.model.IRole;
import org.pentaho.di.ui.repository.pur.repositoryexplorer.abs.model.UIAbsRepositoryRole;


public class UIAbsRepositoryRoleTest implements Serializable {
    static final long serialVersionUID = -3922751737899149745L;/* EESOURCE: UPDATE SERIALVERUID */


    public static final String CREATE_CONTENT = "org.pentaho.di.creator";// $NON-NLS-1$


    public static final String READ_CONTENT = "org.pentaho.di.reader";// $NON-NLS-1$


    public static final String ADMINISTER_SECURITY = "org.pentaho.di.securityAdministrator";// $NON-NLS-1$


    @Test
    public void testUIAbsRepositoryRole() throws Exception {
        IRole role = new AbsRoleInfo();
        role.setDescription("role description");
        role.setName("myrole");
        UIAbsRepositoryRole uiRole = new UIAbsRepositoryRole(role);
        Assert.assertEquals(uiRole.getLogicalRoles().size(), 0);
        uiRole.addLogicalRole(UIAbsRepositoryRoleTest.CREATE_CONTENT);
        Assert.assertEquals(uiRole.getLogicalRoles().size(), 1);
        uiRole.removeLogicalRole(UIAbsRepositoryRoleTest.CREATE_CONTENT);
        Assert.assertEquals(uiRole.getLogicalRoles().size(), 0);
        List<String> logicalRoles = new ArrayList<String>();
        logicalRoles.add(UIAbsRepositoryRoleTest.CREATE_CONTENT);
        logicalRoles.add(UIAbsRepositoryRoleTest.READ_CONTENT);
        logicalRoles.add(UIAbsRepositoryRoleTest.ADMINISTER_SECURITY);
        uiRole.setLogicalRoles(logicalRoles);
        Assert.assertEquals(uiRole.getLogicalRoles().size(), 3);
    }
}

