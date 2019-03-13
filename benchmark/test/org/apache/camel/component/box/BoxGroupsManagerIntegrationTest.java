/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.component.box;


import BoxGroup.Info;
import BoxGroupMembership.Role.ADMIN;
import BoxGroupMembership.Role.MEMBER;
import com.box.sdk.BoxGroup;
import com.box.sdk.BoxGroupMembership;
import com.box.sdk.BoxUser;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.apache.camel.component.box.internal.BoxApiCollection;
import org.apache.camel.component.box.internal.BoxGroupsManagerApiMethod;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Test class for {@link BoxGroupsManager} APIs.
 */
public class BoxGroupsManagerIntegrationTest extends AbstractBoxTestSupport {
    private static final Logger LOG = LoggerFactory.getLogger(BoxGroupsManagerIntegrationTest.class);

    private static final String PATH_PREFIX = BoxApiCollection.getCollection().getApiName(BoxGroupsManagerApiMethod.class).getName();

    private static final String CAMEL_TEST_GROUP_DESCRIPTION = "CamelTestGroupDescription";

    private static final String CAMEL_TEST_GROUP_NAME = "CamelTestGroup";

    private static final String CAMEL_TEST_CREATE_GROUP_NAME = "CamelTestCreateGroup";

    private BoxGroup testGroup;

    private BoxUser testUser;

    @Test
    public void testAddGroupMembership() throws Exception {
        final Map<String, Object> headers = new HashMap<>();
        // parameter type is String
        headers.put("CamelBox.groupId", testGroup.getID());
        // parameter type is String
        headers.put("CamelBox.userId", testUser.getID());
        // parameter type is com.box.sdk.BoxGroupMembership.Role
        headers.put("CamelBox.role", null);
        final BoxGroupMembership result = requestBodyAndHeaders("direct://ADDGROUPMEMBERSHIP", null, headers);
        assertNotNull("addGroupMembership result", result);
        BoxGroupsManagerIntegrationTest.LOG.debug(("addGroupMembership: " + result));
    }

    @Test
    public void testCreateGroup() throws Exception {
        BoxGroup result = null;
        try {
            // using String message body for single parameter "name"
            result = requestBody("direct://CREATEGROUP", BoxGroupsManagerIntegrationTest.CAMEL_TEST_CREATE_GROUP_NAME);
            assertNotNull("createGroup result", result);
            assertEquals(BoxGroupsManagerIntegrationTest.CAMEL_TEST_CREATE_GROUP_NAME, result.getInfo().getName());
            BoxGroupsManagerIntegrationTest.LOG.debug(("createGroup: " + result));
        } finally {
            if (result != null) {
                try {
                    result.delete();
                } catch (Throwable t) {
                }
            }
        }
    }

    @Test
    public void testDeleteGroup() throws Exception {
        // using String message body for single parameter "groupId"
        requestBody("direct://DELETEGROUP", testGroup.getID());
        testGroup = null;
        Iterable<BoxGroup.Info> it = BoxGroup.getAllGroups(getConnection());
        int searchResults = sizeOfIterable(it);
        boolean exists = (searchResults > 0) ? true : false;
        assertEquals("deleteGroup exists", false, exists);
        BoxGroupsManagerIntegrationTest.LOG.debug(("deleteGroup: exists? " + exists));
    }

    @Test
    public void testDeleteGroupMembership() throws Exception {
        BoxGroupMembership.Info info = testGroup.addMembership(testUser, MEMBER);
        // using String message body for single parameter "groupMembershipId"
        requestBody("direct://DELETEGROUPMEMBERSHIP", info.getID());
        Collection<BoxGroupMembership.Info> memberships = testGroup.getMemberships();
        assertNotNull("deleteGroupMemberships memberships", memberships);
        assertEquals("deleteGroupMemberships memberships exists", 0, memberships.size());
    }

    @Test
    public void testGetAllGroups() throws Exception {
        @SuppressWarnings("rawtypes")
        final Collection result = requestBody("direct://GETALLGROUPS", null);
        assertNotNull("getAllGroups result", result);
        BoxGroupsManagerIntegrationTest.LOG.debug(("getAllGroups: " + result));
    }

    @Test
    public void testGetGroupInfo() throws Exception {
        // using String message body for single parameter "groupId"
        final BoxGroup.Info result = requestBody("direct://GETGROUPINFO", testGroup.getID());
        assertNotNull("getGroupInfo result", result);
        BoxGroupsManagerIntegrationTest.LOG.debug(("getGroupInfo: " + result));
    }

    @Test
    public void testUpdateGroupInfo() throws Exception {
        BoxGroup.Info info = testGroup.getInfo();
        info.setDescription(BoxGroupsManagerIntegrationTest.CAMEL_TEST_GROUP_DESCRIPTION);
        try {
            final Map<String, Object> headers = new HashMap<>();
            // parameter type is String
            headers.put("CamelBox.groupId", testGroup.getID());
            // parameter type is com.box.sdk.BoxGroup.Info
            headers.put("CamelBox.groupInfo", info);
            final BoxGroup result = requestBodyAndHeaders("direct://UPDATEGROUPINFO", null, headers);
            assertNotNull("updateGroupInfo result", result);
            BoxGroupsManagerIntegrationTest.LOG.debug(("updateGroupInfo: " + result));
        } finally {
            info = testGroup.getInfo();
            info.setDescription("");
            testGroup.updateInfo(info);
        }
    }

    @Test
    public void testGetGroupMembershipInfo() throws Exception {
        BoxGroupMembership.Info info = testGroup.addMembership(testUser, MEMBER);
        // using String message body for single parameter "groupMemebershipId"
        final BoxGroupMembership.Info result = requestBody("direct://GETGROUPMEMBERSHIPINFO", info.getID());
        assertNotNull("getGroupMembershipInfo result", result);
        BoxGroupsManagerIntegrationTest.LOG.debug(("getGroupMembershipInfo: " + result));
    }

    @Test
    public void testGetGroupMemberships() throws Exception {
        // using String message body for single parameter "groupId"
        @SuppressWarnings("rawtypes")
        final Collection result = requestBody("direct://GETGROUPMEMBERSHIPS", testGroup.getID());
        assertNotNull("getGroupMemberships result", result);
        BoxGroupsManagerIntegrationTest.LOG.debug(("getGroupMemberships: " + result));
    }

    @Test
    public void testUpdateGroupMembershipInfo() throws Exception {
        BoxGroupMembership.Info info = testGroup.addMembership(testUser, MEMBER);
        info.setRole(ADMIN);
        final Map<String, Object> headers = new HashMap<>();
        // parameter type is String
        headers.put("CamelBox.groupMemebershipId", info.getID());
        // parameter type is com.box.sdk.BoxGroupMembership.Info
        headers.put("CamelBox.info", info);
        final BoxGroupMembership result = requestBodyAndHeaders("direct://UPDATEGROUPMEMBERSHIPINFO", null, headers);
        assertNotNull("updateGroupMembershipInfo result", result);
        BoxGroupsManagerIntegrationTest.LOG.debug(("updateGroupMembershipInfo: " + result));
    }
}

