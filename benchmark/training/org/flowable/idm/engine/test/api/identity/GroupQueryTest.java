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


import java.util.Arrays;
import java.util.List;
import org.flowable.common.engine.api.FlowableIllegalArgumentException;
import org.flowable.idm.api.Group;
import org.flowable.idm.api.GroupQuery;
import org.flowable.idm.engine.impl.persistence.entity.GroupEntity;
import org.flowable.idm.engine.test.PluggableFlowableIdmTestCase;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 *
 *
 * @author Joram Barrez
 */
public class GroupQueryTest extends PluggableFlowableIdmTestCase {
    @Test
    public void testQueryById() {
        GroupQuery query = idmIdentityService.createGroupQuery().groupId("muppets");
        verifyQueryResults(query, 1);
    }

    @Test
    public void testQueryByInvalidId() {
        GroupQuery query = idmIdentityService.createGroupQuery().groupId("invalid");
        verifyQueryResults(query, 0);
        try {
            idmIdentityService.createGroupQuery().groupId(null).list();
            Assertions.fail();
        } catch (FlowableIllegalArgumentException e) {
        }
    }

    @Test
    public void testQueryByName() {
        GroupQuery query = idmIdentityService.createGroupQuery().groupName("Muppet show characters");
        verifyQueryResults(query, 1);
        query = idmIdentityService.createGroupQuery().groupName("Famous frogs");
        verifyQueryResults(query, 1);
    }

    @Test
    public void testQueryByInvalidName() {
        GroupQuery query = idmIdentityService.createGroupQuery().groupName("invalid");
        verifyQueryResults(query, 0);
        try {
            idmIdentityService.createGroupQuery().groupName(null).list();
            Assertions.fail();
        } catch (FlowableIllegalArgumentException e) {
        }
    }

    @Test
    public void testQueryByNameLike() {
        GroupQuery query = idmIdentityService.createGroupQuery().groupNameLike("%Famous%");
        verifyQueryResults(query, 2);
        query = idmIdentityService.createGroupQuery().groupNameLike("Famous%");
        verifyQueryResults(query, 2);
        query = idmIdentityService.createGroupQuery().groupNameLike("%show%");
        verifyQueryResults(query, 1);
    }

    @Test
    public void testQueryByInvalidNameLike() {
        GroupQuery query = idmIdentityService.createGroupQuery().groupNameLike("%invalid%");
        verifyQueryResults(query, 0);
        try {
            idmIdentityService.createGroupQuery().groupNameLike(null).list();
            Assertions.fail();
        } catch (FlowableIllegalArgumentException e) {
        }
    }

    @Test
    public void testQueryByNameLikeIgnoreCase() {
        GroupQuery query = idmIdentityService.createGroupQuery().groupNameLikeIgnoreCase("%FAMOus%");
        verifyQueryResults(query, 2);
        query = idmIdentityService.createGroupQuery().groupNameLikeIgnoreCase("FAMOus%");
        verifyQueryResults(query, 2);
        query = idmIdentityService.createGroupQuery().groupNameLikeIgnoreCase("%SHoW%");
        verifyQueryResults(query, 1);
    }

    @Test
    public void testQueryByType() {
        GroupQuery query = idmIdentityService.createGroupQuery().groupType("user");
        verifyQueryResults(query, 3);
        query = idmIdentityService.createGroupQuery().groupType("admin");
        verifyQueryResults(query, 0);
    }

    @Test
    public void testQueryByInvalidType() {
        GroupQuery query = idmIdentityService.createGroupQuery().groupType("invalid");
        verifyQueryResults(query, 0);
        try {
            idmIdentityService.createGroupQuery().groupType(null).list();
            Assertions.fail();
        } catch (FlowableIllegalArgumentException e) {
        }
    }

    @Test
    public void testQueryByMember() {
        GroupQuery query = idmIdentityService.createGroupQuery().groupMember("fozzie");
        verifyQueryResults(query, 2);
        query = idmIdentityService.createGroupQuery().groupMember("kermit");
        verifyQueryResults(query, 3);
        query = query.orderByGroupId().asc();
        List<Group> groups = query.list();
        Assertions.assertEquals(3, groups.size());
        Assertions.assertEquals("admin", groups.get(0).getId());
        Assertions.assertEquals("frogs", groups.get(1).getId());
        Assertions.assertEquals("muppets", groups.get(2).getId());
        query = query.groupType("user");
        groups = query.list();
        Assertions.assertEquals(2, groups.size());
        Assertions.assertEquals("frogs", groups.get(0).getId());
        Assertions.assertEquals("muppets", groups.get(1).getId());
    }

    @Test
    public void testQueryByInvalidMember() {
        GroupQuery query = idmIdentityService.createGroupQuery().groupMember("invalid");
        verifyQueryResults(query, 0);
        try {
            idmIdentityService.createGroupQuery().groupMember(null).list();
            Assertions.fail();
        } catch (FlowableIllegalArgumentException e) {
        }
    }

    @Test
    public void testQueryByGroupMembers() {
        List<Group> groups = idmIdentityService.createGroupQuery().groupMembers(Arrays.asList("kermit", "fozzie")).orderByGroupId().asc().list();
        Assertions.assertEquals(4, groups.size());
        Assertions.assertEquals("admin", groups.get(0).getId());
        Assertions.assertEquals("frogs", groups.get(1).getId());
        Assertions.assertEquals("mammals", groups.get(2).getId());
        Assertions.assertEquals("muppets", groups.get(3).getId());
        groups = idmIdentityService.createGroupQuery().groupMembers(Arrays.asList("fozzie")).orderByGroupId().asc().list();
        Assertions.assertEquals(2, groups.size());
        Assertions.assertEquals("mammals", groups.get(0).getId());
        Assertions.assertEquals("muppets", groups.get(1).getId());
        groups = idmIdentityService.createGroupQuery().groupMembers(Arrays.asList("kermit", "fozzie")).orderByGroupId().asc().listPage(1, 2);
        Assertions.assertEquals(2, groups.size());
        Assertions.assertEquals("frogs", groups.get(0).getId());
        Assertions.assertEquals("mammals", groups.get(1).getId());
    }

    @Test
    public void testQueryByInvalidGroupMember() {
        GroupQuery query = idmIdentityService.createGroupQuery().groupMembers(Arrays.asList("invalid"));
        verifyQueryResults(query, 0);
        try {
            idmIdentityService.createGroupQuery().groupMembers(null).list();
            Assertions.fail();
        } catch (FlowableIllegalArgumentException e) {
        }
    }

    @Test
    public void testQuerySorting() {
        // asc
        Assertions.assertEquals(4, idmIdentityService.createGroupQuery().orderByGroupId().asc().count());
        Assertions.assertEquals(4, idmIdentityService.createGroupQuery().orderByGroupName().asc().count());
        Assertions.assertEquals(4, idmIdentityService.createGroupQuery().orderByGroupType().asc().count());
        // desc
        Assertions.assertEquals(4, idmIdentityService.createGroupQuery().orderByGroupId().desc().count());
        Assertions.assertEquals(4, idmIdentityService.createGroupQuery().orderByGroupName().desc().count());
        Assertions.assertEquals(4, idmIdentityService.createGroupQuery().orderByGroupType().desc().count());
        // Multiple sortings
        GroupQuery query = idmIdentityService.createGroupQuery().orderByGroupType().asc().orderByGroupName().desc();
        List<Group> groups = query.list();
        Assertions.assertEquals(4, query.count());
        Assertions.assertEquals("security", groups.get(0).getType());
        Assertions.assertEquals("user", groups.get(1).getType());
        Assertions.assertEquals("user", groups.get(2).getType());
        Assertions.assertEquals("user", groups.get(3).getType());
        Assertions.assertEquals("admin", groups.get(0).getId());
        Assertions.assertEquals("muppets", groups.get(1).getId());
        Assertions.assertEquals("mammals", groups.get(2).getId());
        Assertions.assertEquals("frogs", groups.get(3).getId());
    }

    @Test
    public void testQueryInvalidSortingUsage() {
        try {
            idmIdentityService.createGroupQuery().orderByGroupId().list();
            Assertions.fail();
        } catch (FlowableIllegalArgumentException e) {
        }
        try {
            idmIdentityService.createGroupQuery().orderByGroupId().orderByGroupName().list();
            Assertions.fail();
        } catch (FlowableIllegalArgumentException e) {
        }
    }

    @Test
    public void testNativeQuery() {
        Assertions.assertEquals("ACT_ID_GROUP", idmManagementService.getTableName(Group.class));
        Assertions.assertEquals("ACT_ID_GROUP", idmManagementService.getTableName(GroupEntity.class));
        String tableName = idmManagementService.getTableName(Group.class);
        String baseQuerySql = "SELECT * FROM " + tableName;
        Assertions.assertEquals(4, idmIdentityService.createNativeGroupQuery().sql(baseQuerySql).list().size());
        Assertions.assertEquals(1, idmIdentityService.createNativeGroupQuery().sql((baseQuerySql + " where ID_ = #{id}")).parameter("id", "admin").list().size());
        Assertions.assertEquals(3, idmIdentityService.createNativeGroupQuery().sql((((("SELECT aig.* from " + tableName) + " aig") + " inner join ACT_ID_MEMBERSHIP aim on aig.ID_ = aim.GROUP_ID_ ") + " inner join ACT_ID_USER aiu on aiu.ID_ = aim.USER_ID_ where aiu.ID_ = #{id}")).parameter("id", "kermit").list().size());
        // paging
        Assertions.assertEquals(2, idmIdentityService.createNativeGroupQuery().sql(baseQuerySql).listPage(0, 2).size());
        Assertions.assertEquals(3, idmIdentityService.createNativeGroupQuery().sql(baseQuerySql).listPage(1, 3).size());
        Assertions.assertEquals(1, idmIdentityService.createNativeGroupQuery().sql((baseQuerySql + " where ID_ = #{id}")).parameter("id", "admin").listPage(0, 1).size());
    }
}

