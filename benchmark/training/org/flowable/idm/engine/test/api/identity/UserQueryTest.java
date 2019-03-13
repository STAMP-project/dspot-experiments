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


import java.util.List;
import org.flowable.common.engine.api.FlowableIllegalArgumentException;
import org.flowable.idm.api.User;
import org.flowable.idm.api.UserQuery;
import org.flowable.idm.engine.impl.persistence.entity.UserEntity;
import org.flowable.idm.engine.test.PluggableFlowableIdmTestCase;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 *
 *
 * @author Joram Barrez
 */
public class UserQueryTest extends PluggableFlowableIdmTestCase {
    @Test
    public void testQueryByNoCriteria() {
        UserQuery query = idmIdentityService.createUserQuery();
        verifyQueryResults(query, 3);
    }

    @Test
    public void testQueryById() {
        UserQuery query = idmIdentityService.createUserQuery().userId("kermit");
        verifyQueryResults(query, 1);
    }

    @Test
    public void testQueryByInvalidId() {
        UserQuery query = idmIdentityService.createUserQuery().userId("invalid");
        verifyQueryResults(query, 0);
        try {
            idmIdentityService.createUserQuery().userId(null).singleResult();
            Assertions.fail();
        } catch (FlowableIllegalArgumentException e) {
        }
    }

    @Test
    public void testQueryByIdIgnoreCase() {
        UserQuery query = idmIdentityService.createUserQuery().userIdIgnoreCase("KErmit");
        verifyQueryResults(query, 1);
    }

    @Test
    public void testQueryByFirstName() {
        UserQuery query = idmIdentityService.createUserQuery().userFirstName("Gonzo");
        verifyQueryResults(query, 1);
        User result = query.singleResult();
        Assertions.assertEquals("gonzo", result.getId());
    }

    @Test
    public void testQueryByInvalidFirstName() {
        UserQuery query = idmIdentityService.createUserQuery().userFirstName("invalid");
        verifyQueryResults(query, 0);
        try {
            idmIdentityService.createUserQuery().userFirstName(null).singleResult();
            Assertions.fail();
        } catch (FlowableIllegalArgumentException e) {
        }
    }

    @Test
    public void testQueryByFirstNameLike() {
        UserQuery query = idmIdentityService.createUserQuery().userFirstNameLike("%o%");
        verifyQueryResults(query, 2);
        query = idmIdentityService.createUserQuery().userFirstNameLike("Ker%");
        verifyQueryResults(query, 1);
    }

    @Test
    public void testQueryByInvalidFirstNameLike() {
        UserQuery query = idmIdentityService.createUserQuery().userFirstNameLike("%mispiggy%");
        verifyQueryResults(query, 0);
        try {
            idmIdentityService.createUserQuery().userFirstNameLike(null).singleResult();
            Assertions.fail();
        } catch (FlowableIllegalArgumentException e) {
        }
    }

    @Test
    public void testQueryByFirstNameLikeIgnoreCase() {
        UserQuery query = idmIdentityService.createUserQuery().userFirstNameLikeIgnoreCase("%O%");
        verifyQueryResults(query, 2);
        query = idmIdentityService.createUserQuery().userFirstNameLikeIgnoreCase("KEr%");
        verifyQueryResults(query, 1);
    }

    @Test
    public void testQueryByLastName() {
        UserQuery query = idmIdentityService.createUserQuery().userLastName("Bear");
        verifyQueryResults(query, 1);
        User result = query.singleResult();
        Assertions.assertEquals("fozzie", result.getId());
    }

    @Test
    public void testQueryByInvalidLastName() {
        UserQuery query = idmIdentityService.createUserQuery().userLastName("invalid");
        verifyQueryResults(query, 0);
        try {
            idmIdentityService.createUserQuery().userLastName(null).singleResult();
            Assertions.fail();
        } catch (FlowableIllegalArgumentException e) {
        }
    }

    @Test
    public void testQueryByLastNameLike() {
        UserQuery query = idmIdentityService.createUserQuery().userLastNameLike("%rog%");
        verifyQueryResults(query, 1);
        query = idmIdentityService.createUserQuery().userLastNameLike("%ea%");
        verifyQueryResults(query, 2);
    }

    @Test
    public void testQueryByLastNameLikeIgnoreCase() {
        UserQuery query = idmIdentityService.createUserQuery().userLastNameLikeIgnoreCase("%ROg%");
        verifyQueryResults(query, 1);
        query = idmIdentityService.createUserQuery().userLastNameLikeIgnoreCase("%Ea%");
        verifyQueryResults(query, 2);
    }

    @Test
    public void testQueryByFullNameLike() {
        UserQuery query = idmIdentityService.createUserQuery().userFullNameLike("%erm%");
        verifyQueryResults(query, 1);
        query = idmIdentityService.createUserQuery().userFullNameLike("%ea%");
        verifyQueryResults(query, 2);
        query = idmIdentityService.createUserQuery().userFullNameLike("%e%");
        verifyQueryResults(query, 3);
    }

    @Test
    public void testQueryByFullNameLikeIgnoreCase() {
        UserQuery query = idmIdentityService.createUserQuery().userFullNameLikeIgnoreCase("%ERm%");
        verifyQueryResults(query, 1);
        query = idmIdentityService.createUserQuery().userFullNameLikeIgnoreCase("%Ea%");
        verifyQueryResults(query, 2);
        query = idmIdentityService.createUserQuery().userFullNameLikeIgnoreCase("%E%");
        verifyQueryResults(query, 3);
    }

    @Test
    public void testQueryByFirstAndLastNameCombinedLike() {
        UserQuery query = idmIdentityService.createUserQuery().userFullNameLike("%ermit The%");
        verifyQueryResults(query, 1);
    }

    @Test
    public void testQueryByInvalidLastNameLike() {
        UserQuery query = idmIdentityService.createUserQuery().userLastNameLike("%invalid%");
        verifyQueryResults(query, 0);
        try {
            idmIdentityService.createUserQuery().userLastNameLike(null).singleResult();
            Assertions.fail();
        } catch (FlowableIllegalArgumentException e) {
        }
    }

    @Test
    public void testQueryByDisplayName() {
        UserQuery query = idmIdentityService.createUserQuery().userDisplayName("Fozzie Bear");
        verifyQueryResults(query, 1);
        User result = query.singleResult();
        Assertions.assertEquals("fozzie", result.getId());
    }

    @Test
    public void testQueryByInvalidDisplayName() {
        UserQuery query = idmIdentityService.createUserQuery().userDisplayName("invalid");
        verifyQueryResults(query, 0);
        try {
            idmIdentityService.createUserQuery().userDisplayName(null).singleResult();
            Assertions.fail();
        } catch (FlowableIllegalArgumentException e) {
        }
    }

    @Test
    public void testQueryByDisplayNameLike() {
        UserQuery query = idmIdentityService.createUserQuery().userDisplayNameLike("%rog%");
        verifyQueryResults(query, 1);
        query = idmIdentityService.createUserQuery().userDisplayNameLike("%ea%");
        verifyQueryResults(query, 2);
    }

    @Test
    public void testQueryByDisplayNameLikeIgnoreCase() {
        UserQuery query = idmIdentityService.createUserQuery().userDisplayNameLikeIgnoreCase("%ROg%");
        verifyQueryResults(query, 1);
        query = idmIdentityService.createUserQuery().userDisplayNameLikeIgnoreCase("%Ea%");
        verifyQueryResults(query, 2);
    }

    @Test
    public void testQueryByEmail() {
        UserQuery query = idmIdentityService.createUserQuery().userEmail("kermit@muppetshow.com");
        verifyQueryResults(query, 1);
    }

    @Test
    public void testQueryByInvalidEmail() {
        UserQuery query = idmIdentityService.createUserQuery().userEmail("invalid");
        verifyQueryResults(query, 0);
        try {
            idmIdentityService.createUserQuery().userEmail(null).singleResult();
            Assertions.fail();
        } catch (FlowableIllegalArgumentException e) {
        }
    }

    @Test
    public void testQueryByEmailLike() {
        UserQuery query = idmIdentityService.createUserQuery().userEmailLike("%muppetshow.com");
        verifyQueryResults(query, 3);
        query = idmIdentityService.createUserQuery().userEmailLike("%kermit%");
        verifyQueryResults(query, 1);
    }

    @Test
    public void testQueryByInvalidEmailLike() {
        UserQuery query = idmIdentityService.createUserQuery().userEmailLike("%invalid%");
        verifyQueryResults(query, 0);
        try {
            idmIdentityService.createUserQuery().userEmailLike(null).singleResult();
            Assertions.fail();
        } catch (FlowableIllegalArgumentException e) {
        }
    }

    @Test
    public void testQuerySorting() {
        // asc
        Assertions.assertEquals(3, idmIdentityService.createUserQuery().orderByUserId().asc().count());
        Assertions.assertEquals(3, idmIdentityService.createUserQuery().orderByUserEmail().asc().count());
        Assertions.assertEquals(3, idmIdentityService.createUserQuery().orderByUserFirstName().asc().count());
        Assertions.assertEquals(3, idmIdentityService.createUserQuery().orderByUserLastName().asc().count());
        // desc
        Assertions.assertEquals(3, idmIdentityService.createUserQuery().orderByUserId().desc().count());
        Assertions.assertEquals(3, idmIdentityService.createUserQuery().orderByUserEmail().desc().count());
        Assertions.assertEquals(3, idmIdentityService.createUserQuery().orderByUserFirstName().desc().count());
        Assertions.assertEquals(3, idmIdentityService.createUserQuery().orderByUserLastName().desc().count());
        // Combined with criteria
        UserQuery query = idmIdentityService.createUserQuery().userLastNameLike("%ea%").orderByUserFirstName().asc();
        List<User> users = query.list();
        Assertions.assertEquals(2, users.size());
        Assertions.assertEquals("Fozzie", users.get(0).getFirstName());
        Assertions.assertEquals("Gonzo", users.get(1).getFirstName());
    }

    @Test
    public void testQueryInvalidSortingUsage() {
        try {
            idmIdentityService.createUserQuery().orderByUserId().list();
            Assertions.fail();
        } catch (FlowableIllegalArgumentException e) {
        }
        try {
            idmIdentityService.createUserQuery().orderByUserId().orderByUserEmail().list();
            Assertions.fail();
        } catch (FlowableIllegalArgumentException e) {
        }
    }

    @Test
    public void testQueryByMemberOf() {
        UserQuery query = idmIdentityService.createUserQuery().memberOfGroup("muppets");
        verifyQueryResults(query, 3);
        query = idmIdentityService.createUserQuery().memberOfGroup("frogs");
        verifyQueryResults(query, 1);
        User result = query.singleResult();
        Assertions.assertEquals("kermit", result.getId());
    }

    @Test
    public void testQueryByInvalidMemberOf() {
        UserQuery query = idmIdentityService.createUserQuery().memberOfGroup("invalid");
        verifyQueryResults(query, 0);
        try {
            idmIdentityService.createUserQuery().memberOfGroup(null).list();
            Assertions.fail();
        } catch (FlowableIllegalArgumentException e) {
        }
    }

    @Test
    public void testNativeQuery() {
        Assertions.assertEquals("ACT_ID_USER", idmManagementService.getTableName(User.class));
        Assertions.assertEquals("ACT_ID_USER", idmManagementService.getTableName(UserEntity.class));
        String tableName = idmManagementService.getTableName(User.class);
        String baseQuerySql = "SELECT * FROM " + tableName;
        Assertions.assertEquals(3, idmIdentityService.createNativeUserQuery().sql(baseQuerySql).list().size());
        Assertions.assertEquals(1, idmIdentityService.createNativeUserQuery().sql((baseQuerySql + " where ID_ = #{id}")).parameter("id", "kermit").list().size());
        // paging
        Assertions.assertEquals(2, idmIdentityService.createNativeUserQuery().sql(baseQuerySql).listPage(0, 2).size());
        Assertions.assertEquals(2, idmIdentityService.createNativeUserQuery().sql(baseQuerySql).listPage(1, 3).size());
        Assertions.assertEquals(1, idmIdentityService.createNativeUserQuery().sql((baseQuerySql + " where ID_ = #{id}")).parameter("id", "kermit").listPage(0, 1).size());
    }
}

