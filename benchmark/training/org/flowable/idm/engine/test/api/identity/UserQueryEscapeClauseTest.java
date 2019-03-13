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


import org.flowable.idm.api.UserQuery;
import org.flowable.idm.engine.test.ResourceFlowableIdmTestCase;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class UserQueryEscapeClauseTest extends ResourceFlowableIdmTestCase {
    public UserQueryEscapeClauseTest() {
        super("escapeclause/flowable.idm.cfg.xml");
    }

    @Test
    public void testQueryByFirstNameLike() {
        UserQuery query = idmIdentityService.createUserQuery().userFirstNameLike("%\\%%");
        Assertions.assertEquals(1, query.list().size());
        Assertions.assertEquals(1, query.count());
        Assertions.assertEquals("kermit", query.singleResult().getId());
        query = idmIdentityService.createUserQuery().userFirstNameLike("%\\_%");
        Assertions.assertEquals(1, query.list().size());
        Assertions.assertEquals(1, query.count());
        Assertions.assertEquals("fozzie", query.singleResult().getId());
    }

    @Test
    public void testQueryByLastNameLike() {
        UserQuery query = idmIdentityService.createUserQuery().userLastNameLike("%\\%%");
        Assertions.assertEquals(1, query.list().size());
        Assertions.assertEquals(1, query.count());
        Assertions.assertEquals("kermit", query.singleResult().getId());
        query = idmIdentityService.createUserQuery().userLastNameLike("%\\_%");
        Assertions.assertEquals(1, query.list().size());
        Assertions.assertEquals(1, query.count());
        Assertions.assertEquals("fozzie", query.singleResult().getId());
    }

    @Test
    public void testQueryByFullNameLike() {
        UserQuery query = idmIdentityService.createUserQuery().userFullNameLike("%og\\%%");
        Assertions.assertEquals(1, query.list().size());
        Assertions.assertEquals(1, query.count());
        Assertions.assertEquals("kermit", query.singleResult().getId());
        query = idmIdentityService.createUserQuery().userFullNameLike("%it\\%%");
        Assertions.assertEquals(1, query.list().size());
        Assertions.assertEquals(1, query.count());
        Assertions.assertEquals("kermit", query.singleResult().getId());
        query = idmIdentityService.createUserQuery().userFullNameLike("%ar\\_%");
        Assertions.assertEquals(1, query.list().size());
        Assertions.assertEquals(1, query.count());
        Assertions.assertEquals("fozzie", query.singleResult().getId());
        query = idmIdentityService.createUserQuery().userFullNameLike("%ie\\_%");
        Assertions.assertEquals(1, query.list().size());
        Assertions.assertEquals(1, query.count());
        Assertions.assertEquals("fozzie", query.singleResult().getId());
    }

    @Test
    public void testQueryByEmailLike() {
        UserQuery query = idmIdentityService.createUserQuery().userEmailLike("%\\%%");
        Assertions.assertEquals(1, query.list().size());
        Assertions.assertEquals(1, query.count());
        Assertions.assertEquals("kermit", query.singleResult().getId());
        query = idmIdentityService.createUserQuery().userEmailLike("%\\_%");
        Assertions.assertEquals(1, query.list().size());
        Assertions.assertEquals(1, query.count());
        Assertions.assertEquals("fozzie", query.singleResult().getId());
    }
}

