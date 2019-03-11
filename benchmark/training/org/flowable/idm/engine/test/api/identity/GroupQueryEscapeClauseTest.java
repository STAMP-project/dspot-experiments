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


import org.flowable.idm.api.GroupQuery;
import org.flowable.idm.engine.test.ResourceFlowableIdmTestCase;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class GroupQueryEscapeClauseTest extends ResourceFlowableIdmTestCase {
    public GroupQueryEscapeClauseTest() {
        super("escapeclause/flowable.idm.cfg.xml");
    }

    @Test
    public void testQueryByNameLike() {
        GroupQuery query = idmIdentityService.createGroupQuery().groupNameLike("%\\%%");
        Assertions.assertEquals(1, query.list().size());
        Assertions.assertEquals(1, query.count());
        Assertions.assertEquals("muppets", query.singleResult().getId());
        query = idmIdentityService.createGroupQuery().groupNameLike("%\\_%");
        Assertions.assertEquals(1, query.list().size());
        Assertions.assertEquals(1, query.count());
        Assertions.assertEquals("frogs", query.singleResult().getId());
    }
}

