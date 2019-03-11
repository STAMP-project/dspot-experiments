/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.flowable.idm.engine.test.api.identity;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.flowable.common.engine.api.FlowableIllegalArgumentException;
import org.flowable.idm.api.Group;
import org.flowable.idm.api.Privilege;
import org.flowable.idm.api.PrivilegeMapping;
import org.flowable.idm.api.User;
import org.flowable.idm.engine.impl.persistence.entity.PrivilegeEntity;
import org.flowable.idm.engine.test.PluggableFlowableIdmTestCase;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 *
 *
 * @author Joram Barrez
 */
public class PrivilegesTest extends PluggableFlowableIdmTestCase {
    @Test
    public void testCreateDuplicatePrivilege() {
        try {
            idmIdentityService.createPrivilege("access admin application");
            Assertions.fail();
        } catch (FlowableIllegalArgumentException e) {
        }
    }

    @Test
    public void testGetUsers() {
        String privilegeId = idmIdentityService.createPrivilegeQuery().privilegeName("access admin application").singleResult().getId();
        List<User> users = idmIdentityService.getUsersWithPrivilege(privilegeId);
        Assertions.assertEquals(1, users.size());
        Assertions.assertEquals("mispiggy", users.get(0).getId());
        Assertions.assertEquals(0, idmIdentityService.getUsersWithPrivilege("does not exist").size());
        try {
            idmIdentityService.getUsersWithPrivilege(null);
            Assertions.fail();
        } catch (Exception e) {
        }
    }

    @Test
    public void testGetGroups() {
        String privilegeId = idmIdentityService.createPrivilegeQuery().privilegeName("access modeler application").singleResult().getId();
        List<Group> groups = idmIdentityService.getGroupsWithPrivilege(privilegeId);
        Assertions.assertEquals(2, groups.size());
        Assertions.assertEquals("admins", groups.get(0).getId());
        Assertions.assertEquals("engineering", groups.get(1).getId());
        Assertions.assertEquals(0, idmIdentityService.getGroupsWithPrivilege("does not exist").size());
        try {
            idmIdentityService.getGroupsWithPrivilege(null);
            Assertions.fail();
        } catch (Exception e) {
        }
    }

    @Test
    public void testQueryAll() {
        List<Privilege> privileges = idmIdentityService.createPrivilegeQuery().list();
        Assertions.assertEquals(3, privileges.size());
        Assertions.assertEquals(3L, idmIdentityService.createPrivilegeQuery().count());
    }

    @Test
    public void testQueryByName() {
        List<Privilege> privileges = idmIdentityService.createPrivilegeQuery().privilegeName("access admin application").list();
        Assertions.assertEquals(1, privileges.size());
        Assertions.assertEquals(1, idmIdentityService.getUsersWithPrivilege(privileges.get(0).getId()).size());
        Assertions.assertEquals(1, idmIdentityService.getGroupsWithPrivilege(privileges.get(0).getId()).size());
    }

    @Test
    public void testQueryByInvalidName() {
        Assertions.assertEquals(0, idmIdentityService.createPrivilegeQuery().privilegeName("does not exist").list().size());
    }

    @Test
    public void testQueryByUserId() {
        List<Privilege> privileges = idmIdentityService.createPrivilegeQuery().userId("kermit").list();
        Assertions.assertEquals(1, privileges.size());
        Privilege privilege = privileges.get(0);
        Assertions.assertEquals("access modeler application", privilege.getName());
    }

    @Test
    public void testQueryByInvalidUserId() {
        Assertions.assertEquals(0, idmIdentityService.createPrivilegeQuery().userId("does not exist").list().size());
    }

    @Test
    public void testQueryByGroupId() {
        List<Privilege> privileges = idmIdentityService.createPrivilegeQuery().groupId("admins").list();
        Assertions.assertEquals(2, privileges.size());
    }

    @Test
    public void testQueryByInvalidGroupId() {
        Assertions.assertEquals(0, idmIdentityService.createPrivilegeQuery().groupId("does not exist").list().size());
    }

    @Test
    public void testQueryByGroupIds() {
        List<Privilege> privileges = idmIdentityService.createPrivilegeQuery().groupIds(Arrays.asList("admins")).list();
        Assertions.assertEquals(2, privileges.size());
        privileges = idmIdentityService.createPrivilegeQuery().groupIds(Arrays.asList("admins", "engineering")).list();
        Assertions.assertEquals(2, privileges.size());
        privileges = idmIdentityService.createPrivilegeQuery().groupIds(Arrays.asList("engineering")).list();
        Assertions.assertEquals(1, privileges.size());
        privileges = idmIdentityService.createPrivilegeQuery().groupIds(Arrays.asList("admins", "engineering")).listPage(0, 1);
        Assertions.assertEquals(1, privileges.size());
        privileges = idmIdentityService.createPrivilegeQuery().groupIds(Arrays.asList("admins", "engineering")).listPage(1, 1);
        Assertions.assertEquals(1, privileges.size());
    }

    @Test
    public void testQueryByInvalidGroupIds() {
        Assertions.assertEquals(0, idmIdentityService.createPrivilegeQuery().groupIds(Arrays.asList("does not exist")).list().size());
    }

    @Test
    public void testNativeQuery() {
        Assertions.assertEquals("ACT_ID_PRIV", idmManagementService.getTableName(Privilege.class));
        Assertions.assertEquals("ACT_ID_PRIV", idmManagementService.getTableName(PrivilegeEntity.class));
        String tableName = idmManagementService.getTableName(PrivilegeEntity.class);
        String baseQuerySql = ("SELECT * FROM " + tableName) + " where NAME_ = #{name}";
        Assertions.assertEquals(1, idmIdentityService.createNativeUserQuery().sql(baseQuerySql).parameter("name", "access admin application").list().size());
    }

    @Test
    public void testGetPrivilegeMappings() {
        Privilege modelerPrivilege = idmIdentityService.createPrivilegeQuery().privilegeName("access modeler application").singleResult();
        List<PrivilegeMapping> privilegeMappings = idmIdentityService.getPrivilegeMappingsByPrivilegeId(modelerPrivilege.getId());
        Assertions.assertEquals(3, privilegeMappings.size());
        List<String> users = new ArrayList<>();
        List<String> groups = new ArrayList<>();
        for (PrivilegeMapping privilegeMapping : privilegeMappings) {
            if ((privilegeMapping.getUserId()) != null) {
                users.add(privilegeMapping.getUserId());
            } else
                if ((privilegeMapping.getGroupId()) != null) {
                    groups.add(privilegeMapping.getGroupId());
                }

        }
        Assertions.assertTrue(users.contains("kermit"));
        Assertions.assertTrue(groups.contains("admins"));
        Assertions.assertTrue(groups.contains("engineering"));
    }

    @Test
    public void testPrivilegeUniqueName() {
        Privilege privilege = idmIdentityService.createPrivilege("test");
        try {
            idmIdentityService.createPrivilege("test");
            Assertions.fail();
        } catch (Exception e) {
            e.printStackTrace();
        }
        idmIdentityService.deletePrivilege(privilege.getId());
    }
}

