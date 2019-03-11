/**
 * Copyright 2018 Red Hat, Inc. and/or its affiliates
 * and other contributors as indicated by the @author tags.
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
package org.keycloak.testsuite.admin.user;


import OperationType.CREATE;
import ResourceType.GROUP_MEMBERSHIP;
import ResourceType.USER;
import java.util.List;
import javax.ws.rs.core.Response;
import org.junit.Assert;
import org.junit.Test;
import org.keycloak.representations.idm.GroupRepresentation;
import org.keycloak.testsuite.admin.AbstractAdminTest;
import org.keycloak.testsuite.admin.ApiUtil;
import org.keycloak.testsuite.util.AdminEventPaths;
import org.keycloak.testsuite.util.UserBuilder;

import static org.keycloak.testsuite.Assert.assertNames;


/**
 *
 *
 * @author <a href="mailto:volker.suschke@bosch-si.com">Volker Suschke</a>
 * @author <a href="mailto:leon.graser@bosch-si.com">Leon Graser</a>
 */
public class UserGroupMembershipTest extends AbstractAdminTest {
    @Test
    public void verifyCreateUser() {
        createUser();
    }

    @Test
    public void groupMembershipPaginated() {
        Response response = realm.users().create(UserBuilder.create().username("user-a").build());
        String userId = ApiUtil.getCreatedId(response);
        response.close();
        assertAdminEvents.assertEvent(realmId, CREATE, AdminEventPaths.userResourcePath(userId), USER);
        for (int i = 1; i <= 10; i++) {
            GroupRepresentation group = new GroupRepresentation();
            group.setName(("group-" + i));
            String groupId = createGroup(realm, group).getId();
            realm.users().get(userId).joinGroup(groupId);
            assertAdminEvents.assertEvent(realmId, CREATE, AdminEventPaths.userGroupPath(userId, groupId), group, GROUP_MEMBERSHIP);
        }
        List<GroupRepresentation> groups = realm.users().get(userId).groups(5, 6);
        Assert.assertEquals(groups.size(), 5);
        assertNames(groups, "group-5", "group-6", "group-7", "group-8", "group-9");
    }

    @Test
    public void groupMembershipSearch() {
        Response response = realm.users().create(UserBuilder.create().username("user-b").build());
        String userId = ApiUtil.getCreatedId(response);
        response.close();
        assertAdminEvents.assertEvent(realmId, CREATE, AdminEventPaths.userResourcePath(userId), USER);
        for (int i = 1; i <= 10; i++) {
            GroupRepresentation group = new GroupRepresentation();
            group.setName(("group-" + i));
            String groupId = createGroup(realm, group).getId();
            realm.users().get(userId).joinGroup(groupId);
            assertAdminEvents.assertEvent(realmId, CREATE, AdminEventPaths.userGroupPath(userId, groupId), group, GROUP_MEMBERSHIP);
        }
        List<GroupRepresentation> groups = realm.users().get(userId).groups("-3", 0, 10);
        Assert.assertEquals(1, groups.size());
        assertNames(groups, "group-3");
        List<GroupRepresentation> groups2 = realm.users().get(userId).groups("1", 0, 10);
        Assert.assertEquals(2, groups2.size());
        assertNames(groups2, "group-1", "group-10");
        List<GroupRepresentation> groups3 = realm.users().get(userId).groups("1", 2, 10);
        Assert.assertEquals(0, groups3.size());
        List<GroupRepresentation> groups4 = realm.users().get(userId).groups("gr", 2, 10);
        Assert.assertEquals(8, groups4.size());
        List<GroupRepresentation> groups5 = realm.users().get(userId).groups("Gr", 2, 10);
        Assert.assertEquals(8, groups5.size());
    }
}

