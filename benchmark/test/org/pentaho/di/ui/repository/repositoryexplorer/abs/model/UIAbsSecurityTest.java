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
import org.junit.Assert;
import org.junit.Test;
import org.pentaho.di.ui.repository.pur.repositoryexplorer.UIEEObjectRegistery;
import org.pentaho.di.ui.repository.pur.repositoryexplorer.abs.model.UIAbsRepositoryRole;
import org.pentaho.di.ui.repository.pur.repositoryexplorer.abs.model.UIAbsSecurity;
import org.pentaho.di.ui.repository.pur.services.IRoleSupportSecurityManager;


public class UIAbsSecurityTest implements Serializable {
    static final long serialVersionUID = -8052299792314313987L;/* EESOURCE: UPDATE SERIALVERUID */


    IRoleSupportSecurityManager sm;

    public static final String CREATE_CONTENT = "org.pentaho.di.creator";// $NON-NLS-1$


    public static final String READ_CONTENT = "org.pentaho.di.reader";// $NON-NLS-1$


    public static final String ADMINISTER_SECURITY = "org.pentaho.di.securityAdministrator";// $NON-NLS-1$


    @Test
    public void testUIAbsSecurity() throws Exception {
        UIEEObjectRegistery.getInstance().registerUIRepositoryRoleClass(UIAbsRepositoryRole.class);
        UIAbsSecurity security = new UIAbsSecurity(sm);
        security.setSelectedRole(new UIAbsRepositoryRole(sm.getRoles().get(0)));
        Assert.assertEquals(getLogicalRoles().size(), 0);
        security.addLogicalRole(UIAbsSecurityTest.CREATE_CONTENT);
        Assert.assertEquals(getLogicalRoles().size(), 1);
        security.removeLogicalRole(UIAbsSecurityTest.CREATE_CONTENT);
        Assert.assertEquals(getLogicalRoles().size(), 0);
    }
}

