/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.ambari.server.upgrade;


import BlueprintProvisioningState.NONE;
import DBAccessor.DBColumnInfo;
import com.google.inject.Injector;
import java.lang.reflect.Method;
import org.apache.ambari.server.orm.DBAccessor;
import org.easymock.Capture;
import org.junit.Assert;
import org.junit.Test;


public class UpgradeCatalog272Test {
    private Injector injector;

    private DBAccessor dbAccessor;

    @Test
    public void testExecuteDMLUpdates() throws Exception {
        final Method renameLdapSynchCollisionBehaviorValue = UpgradeCatalog272.class.getDeclaredMethod("renameLdapSynchCollisionBehaviorValue");
        final Method createRoleAuthorizations = UpgradeCatalog272.class.getDeclaredMethod("createRoleAuthorizations");
        final UpgradeCatalog272 upgradeCatalog272 = createMockBuilder(UpgradeCatalog272.class).addMockedMethod(renameLdapSynchCollisionBehaviorValue).addMockedMethod(createRoleAuthorizations).createMock();
        expect(upgradeCatalog272.renameLdapSynchCollisionBehaviorValue()).andReturn(0).once();
        upgradeCatalog272.createRoleAuthorizations();
        expectLastCall().once();
        replay(upgradeCatalog272);
        upgradeCatalog272.executeDMLUpdates();
        verify(upgradeCatalog272);
    }

    @Test
    public void testExecuteDDLUpdates() throws Exception {
        dbAccessor.dropColumn(eq(UpgradeCatalog272.CLUSTERS_TABLE), eq(UpgradeCatalog272.BLUEPRINT_PROVISIONING_STATE_COLUMN));
        expectLastCall().once();
        Capture<DBAccessor.DBColumnInfo> blueprintProvisioningStateColumnCapture = newCapture(CaptureType.ALL);
        dbAccessor.addColumn(eq(UpgradeCatalog272.HOST_COMPONENT_DESIRED_STATE_TABLE), capture(blueprintProvisioningStateColumnCapture));
        expectLastCall().once();
        replay(dbAccessor, injector);
        UpgradeCatalog272 upgradeCatalog272 = new UpgradeCatalog272(injector);
        upgradeCatalog272.dbAccessor = dbAccessor;
        upgradeCatalog272.executeDDLUpdates();
        DBAccessor.DBColumnInfo capturedBlueprintProvisioningStateColumn = blueprintProvisioningStateColumnCapture.getValue();
        Assert.assertEquals(UpgradeCatalog272.BLUEPRINT_PROVISIONING_STATE_COLUMN, capturedBlueprintProvisioningStateColumn.getName());
        Assert.assertEquals(NONE, capturedBlueprintProvisioningStateColumn.getDefaultValue());
        Assert.assertEquals(String.class, capturedBlueprintProvisioningStateColumn.getType());
        verify(dbAccessor);
    }

    @Test
    public void shouldRenameCollisionBehaviorLdapCategoryPropertyNameIfTableWithDataExists() throws Exception {
        final int expectedResult = 3;
        expect(dbAccessor.tableExists(UpgradeCatalog270.AMBARI_CONFIGURATION_TABLE)).andReturn(true).once();
        expect(dbAccessor.executeUpdate(UpgradeCatalog272.RENAME_COLLISION_BEHAVIOR_PROPERTY_SQL)).andReturn(expectedResult).once();
        replay(dbAccessor);
        final UpgradeCatalog272 upgradeCatalog272 = new UpgradeCatalog272(injector);
        upgradeCatalog272.dbAccessor = dbAccessor;
        Assert.assertEquals(expectedResult, upgradeCatalog272.renameLdapSynchCollisionBehaviorValue());
        verify(dbAccessor);
    }

    @Test
    public void shouldNotRenameCollisionBehaviorLdapCategoryPropertyNameIfTableDoesNotExist() throws Exception {
        final int expectedResult = 0;
        expect(dbAccessor.tableExists(UpgradeCatalog270.AMBARI_CONFIGURATION_TABLE)).andReturn(false).once();
        replay(dbAccessor);
        final UpgradeCatalog272 upgradeCatalog272 = new UpgradeCatalog272(injector);
        upgradeCatalog272.dbAccessor = dbAccessor;
        Assert.assertEquals(expectedResult, upgradeCatalog272.renameLdapSynchCollisionBehaviorValue());
        verify(dbAccessor);
    }
}

