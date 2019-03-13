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


import BoxUser.Info;
import com.box.sdk.BoxUser;
import com.box.sdk.CreateUserParams;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.camel.component.box.internal.BoxApiCollection;
import org.apache.camel.component.box.internal.BoxUsersManagerApiMethod;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Test class for {@link BoxUsersManager}
 * APIs.
 */
public class BoxUsersManagerIntegrationTest extends AbstractBoxTestSupport {
    private static final Logger LOG = LoggerFactory.getLogger(BoxUsersManagerIntegrationTest.class);

    private static final String PATH_PREFIX = BoxApiCollection.getCollection().getApiName(BoxUsersManagerApiMethod.class).getName();

    private static final String CAMEL_TEST_USER_EMAIL_ALIAS = "camel@example.com";

    private static final String CAMEL_TEST_USER_JOB_TITLE = "Camel Tester";

    private static final String CAMEL_TEST_CREATE_APP_USER_NAME = "Wilma";

    private static final String CAMEL_TEST_CREATE_ENTERPRISE_USER_NAME = "fred";

    private static final String CAMEL_TEST_CREATE_ENTERPRISE_USER_LOGIN = "fred@example.com";

    private static final String CAMEL_TEST_CREATE_ENTERPRISE_USER2_NAME = "gregory";

    private static final String CAMEL_TEST_CREATE_ENTERPRISE_USER2_LOGIN = "gregory@example.com";

    private BoxUser testUser;

    @Test
    public void testCreateAppUser() throws Exception {
        BoxUser result = null;
        try {
            CreateUserParams params = new CreateUserParams();
            params.setSpaceAmount(1073741824);// 1 GB

            final Map<String, Object> headers = new HashMap<>();
            // parameter type is String
            headers.put("CamelBox.name", BoxUsersManagerIntegrationTest.CAMEL_TEST_CREATE_APP_USER_NAME);
            // parameter type is com.box.sdk.CreateUserParams
            headers.put("CamelBox.params", params);
            result = requestBodyAndHeaders("direct://CREATEAPPUSER", null, headers);
            assertNotNull("createAppUser result", result);
            BoxUsersManagerIntegrationTest.LOG.debug(("createAppUser: " + result));
        } finally {
            if (result != null) {
                try {
                    result.delete(false, true);
                } catch (Throwable t) {
                }
            }
        }
    }

    @Test
    public void testCreateEnterpriseUser() throws Exception {
        BoxUser result = null;
        try {
            CreateUserParams params = new CreateUserParams();
            params.setSpaceAmount(1073741824);// 1 GB

            final Map<String, Object> headers = new HashMap<>();
            // parameter type is String
            headers.put("CamelBox.login", BoxUsersManagerIntegrationTest.CAMEL_TEST_CREATE_ENTERPRISE_USER_LOGIN);
            // parameter type is String
            headers.put("CamelBox.name", BoxUsersManagerIntegrationTest.CAMEL_TEST_CREATE_ENTERPRISE_USER_NAME);
            // parameter type is com.box.sdk.CreateUserParams
            headers.put("CamelBox.params", params);
            result = requestBodyAndHeaders("direct://CREATEENTERPRISEUSER", null, headers);
            assertNotNull("createEnterpriseUser result", result);
            BoxUsersManagerIntegrationTest.LOG.debug(("createEnterpriseUser: " + result));
        } finally {
            if (result != null) {
                try {
                    result.delete(false, true);
                } catch (Throwable t) {
                }
            }
        }
    }

    @Test
    public void testDeleteUser() throws Exception {
        BoxUser.Info info = BoxUser.createAppUser(getConnection(), BoxUsersManagerIntegrationTest.CAMEL_TEST_CREATE_APP_USER_NAME);
        final Map<String, Object> headers = new HashMap<>();
        // parameter type is String
        headers.put("CamelBox.userId", info.getID());
        headers.put("CamelBox.notifyUser", Boolean.FALSE);
        headers.put("CamelBox.force", Boolean.FALSE);
        requestBodyAndHeaders("direct://DELETEUSER", null, headers);
        Iterable<BoxUser.Info> it = BoxUser.getAllEnterpriseUsers(getConnection(), BoxUsersManagerIntegrationTest.CAMEL_TEST_CREATE_APP_USER_NAME);
        int searchResults = sizeOfIterable(it);
        boolean exists = (searchResults > 0) ? true : false;
        assertEquals("deleteUser exists", false, exists);
        BoxUsersManagerIntegrationTest.LOG.debug(("deleteUser: exists? " + exists));
    }

    @Test
    public void testGetAllEnterpriseOrExternalUsers() throws Exception {
        final Map<String, Object> headers = new HashMap<>();
        // parameter type is String
        headers.put("CamelBox.filterTerm", null);
        // parameter type is String[]
        headers.put("CamelBox.fields", null);
        @SuppressWarnings("rawtypes")
        final List result = requestBodyAndHeaders("direct://GETALLENTERPRISEOREXTERNALUSERS", null, headers);
        assertNotNull("getAllEnterpriseOrExternalUsers result", result);
        BoxUsersManagerIntegrationTest.LOG.debug(("getAllEnterpriseOrExternalUsers: " + result));
    }

    @Test
    public void testGetCurrentUser() throws Exception {
        final BoxUser result = requestBody("direct://GETCURRENTUSER", testUser.getID());
        assertNotNull("getCurrentUser result", result);
        BoxUsersManagerIntegrationTest.LOG.debug(("getCurrentUser: " + result));
    }

    @Test
    public void testGetUserEmailAlias() throws Exception {
        // using String message body for single parameter "userId"
        @SuppressWarnings("rawtypes")
        final Collection result = requestBody("direct://GETUSEREMAILALIAS", testUser.getID());
        assertNotNull("getUserEmailAlias result", result);
        BoxUsersManagerIntegrationTest.LOG.debug(("getUserEmailAlias: " + result));
    }

    @Test
    public void testGetUserInfo() throws Exception {
        // using String message body for single parameter "userId"
        final BoxUser.Info result = requestBody("direct://GETUSERINFO", testUser.getID());
        assertNotNull("getUserInfo result", result);
        BoxUsersManagerIntegrationTest.LOG.debug(("getUserInfo: " + result));
    }

    @Test
    public void testUpdateUserInfo() throws Exception {
        BoxUser.Info info = testUser.getInfo();
        info.setJobTitle(BoxUsersManagerIntegrationTest.CAMEL_TEST_USER_JOB_TITLE);
        try {
            final Map<String, Object> headers = new HashMap<>();
            // parameter type is String
            headers.put("CamelBox.userId", testUser.getID());
            // parameter type is com.box.sdk.BoxUser.Info
            headers.put("CamelBox.info", info);
            final BoxUser result = requestBodyAndHeaders("direct://UPDATEUSERINFO", null, headers);
            assertNotNull("updateUserInfo result", result);
            BoxUsersManagerIntegrationTest.LOG.debug(("updateUserInfo: " + result));
        } finally {
            info = testUser.getInfo();
            info.setJobTitle("");
            testUser.updateInfo(info);
        }
    }

    @Test
    public void testmMoveFolderToUser() throws Exception {
        BoxUser.Info user1 = BoxUser.createEnterpriseUser(getConnection(), BoxUsersManagerIntegrationTest.CAMEL_TEST_CREATE_ENTERPRISE_USER_LOGIN, BoxUsersManagerIntegrationTest.CAMEL_TEST_CREATE_ENTERPRISE_USER_NAME);
        BoxUser.Info user2 = BoxUser.createEnterpriseUser(getConnection(), BoxUsersManagerIntegrationTest.CAMEL_TEST_CREATE_ENTERPRISE_USER2_LOGIN, BoxUsersManagerIntegrationTest.CAMEL_TEST_CREATE_ENTERPRISE_USER2_NAME);
        final Map<String, Object> headers = new HashMap<>();
        // parameter type is String
        headers.put("CamelBox.userId", user1.getID());
        headers.put("CamelBox.sourceUserId", user2.getID());
        final com.box.sdk.BoxFolder.Info result = requestBodyAndHeaders("direct://MOVEFOLDERTOUSER", null, headers);
        assertNotNull("moveFolderToUser result", result);
    }
}

