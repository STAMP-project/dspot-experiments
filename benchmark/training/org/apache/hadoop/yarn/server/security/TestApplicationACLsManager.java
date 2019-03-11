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
package org.apache.hadoop.yarn.server.security;


import ApplicationAccessType.MODIFY_APP;
import ApplicationAccessType.VIEW_APP;
import YarnConfiguration.YARN_ACL_ENABLE;
import YarnConfiguration.YARN_ADMIN_ACL;
import java.util.HashMap;
import java.util.Map;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.hadoop.yarn.api.records.ApplicationAccessType;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import org.junit.Assert;
import org.junit.Test;


public class TestApplicationACLsManager {
    private static final String ADMIN_USER = "adminuser";

    private static final String APP_OWNER = "appuser";

    private static final String TESTUSER1 = "testuser1";

    private static final String TESTUSER2 = "testuser2";

    private static final String TESTUSER3 = "testuser3";

    @Test
    public void testCheckAccess() {
        Configuration conf = new Configuration();
        conf.setBoolean(YARN_ACL_ENABLE, true);
        conf.set(YARN_ADMIN_ACL, TestApplicationACLsManager.ADMIN_USER);
        ApplicationACLsManager aclManager = new ApplicationACLsManager(conf);
        Map<ApplicationAccessType, String> aclMap = new HashMap<ApplicationAccessType, String>();
        aclMap.put(VIEW_APP, (((TestApplicationACLsManager.TESTUSER1) + ",") + (TestApplicationACLsManager.TESTUSER3)));
        aclMap.put(MODIFY_APP, TestApplicationACLsManager.TESTUSER1);
        ApplicationId appId = ApplicationId.newInstance(1, 1);
        aclManager.addApplication(appId, aclMap);
        // User in ACL, should be allowed access
        UserGroupInformation testUser1 = UserGroupInformation.createRemoteUser(TestApplicationACLsManager.TESTUSER1);
        Assert.assertTrue(aclManager.checkAccess(testUser1, VIEW_APP, TestApplicationACLsManager.APP_OWNER, appId));
        Assert.assertTrue(aclManager.checkAccess(testUser1, MODIFY_APP, TestApplicationACLsManager.APP_OWNER, appId));
        // User NOT in ACL, should not be allowed access
        UserGroupInformation testUser2 = UserGroupInformation.createRemoteUser(TestApplicationACLsManager.TESTUSER2);
        Assert.assertFalse(aclManager.checkAccess(testUser2, VIEW_APP, TestApplicationACLsManager.APP_OWNER, appId));
        Assert.assertFalse(aclManager.checkAccess(testUser2, MODIFY_APP, TestApplicationACLsManager.APP_OWNER, appId));
        // User has View access, but not modify access
        UserGroupInformation testUser3 = UserGroupInformation.createRemoteUser(TestApplicationACLsManager.TESTUSER3);
        Assert.assertTrue(aclManager.checkAccess(testUser3, VIEW_APP, TestApplicationACLsManager.APP_OWNER, appId));
        Assert.assertFalse(aclManager.checkAccess(testUser3, MODIFY_APP, TestApplicationACLsManager.APP_OWNER, appId));
        // Application Owner should have all access
        UserGroupInformation appOwner = UserGroupInformation.createRemoteUser(TestApplicationACLsManager.APP_OWNER);
        Assert.assertTrue(aclManager.checkAccess(appOwner, VIEW_APP, TestApplicationACLsManager.APP_OWNER, appId));
        Assert.assertTrue(aclManager.checkAccess(appOwner, MODIFY_APP, TestApplicationACLsManager.APP_OWNER, appId));
        // Admin should have all access
        UserGroupInformation adminUser = UserGroupInformation.createRemoteUser(TestApplicationACLsManager.ADMIN_USER);
        Assert.assertTrue(aclManager.checkAccess(adminUser, VIEW_APP, TestApplicationACLsManager.APP_OWNER, appId));
        Assert.assertTrue(aclManager.checkAccess(adminUser, MODIFY_APP, TestApplicationACLsManager.APP_OWNER, appId));
    }

    @Test
    public void testCheckAccessWithNullACLS() {
        Configuration conf = new Configuration();
        conf.setBoolean(YARN_ACL_ENABLE, true);
        conf.set(YARN_ADMIN_ACL, TestApplicationACLsManager.ADMIN_USER);
        ApplicationACLsManager aclManager = new ApplicationACLsManager(conf);
        UserGroupInformation appOwner = UserGroupInformation.createRemoteUser(TestApplicationACLsManager.APP_OWNER);
        ApplicationId appId = ApplicationId.newInstance(1, 1);
        // Application ACL is not added
        // Application Owner should have all access even if Application ACL is not added
        Assert.assertTrue(aclManager.checkAccess(appOwner, MODIFY_APP, TestApplicationACLsManager.APP_OWNER, appId));
        Assert.assertTrue(aclManager.checkAccess(appOwner, VIEW_APP, TestApplicationACLsManager.APP_OWNER, appId));
        // Admin should have all access
        UserGroupInformation adminUser = UserGroupInformation.createRemoteUser(TestApplicationACLsManager.ADMIN_USER);
        Assert.assertTrue(aclManager.checkAccess(adminUser, VIEW_APP, TestApplicationACLsManager.APP_OWNER, appId));
        Assert.assertTrue(aclManager.checkAccess(adminUser, MODIFY_APP, TestApplicationACLsManager.APP_OWNER, appId));
        // A regular user should Not have access
        UserGroupInformation testUser1 = UserGroupInformation.createRemoteUser(TestApplicationACLsManager.TESTUSER1);
        Assert.assertFalse(aclManager.checkAccess(testUser1, VIEW_APP, TestApplicationACLsManager.APP_OWNER, appId));
        Assert.assertFalse(aclManager.checkAccess(testUser1, MODIFY_APP, TestApplicationACLsManager.APP_OWNER, appId));
    }

    @Test
    public void testCheckAccessWithPartialACLS() {
        Configuration conf = new Configuration();
        conf.setBoolean(YARN_ACL_ENABLE, true);
        conf.set(YARN_ADMIN_ACL, TestApplicationACLsManager.ADMIN_USER);
        ApplicationACLsManager aclManager = new ApplicationACLsManager(conf);
        UserGroupInformation appOwner = UserGroupInformation.createRemoteUser(TestApplicationACLsManager.APP_OWNER);
        // Add only the VIEW ACLS
        Map<ApplicationAccessType, String> aclMap = new HashMap<ApplicationAccessType, String>();
        aclMap.put(VIEW_APP, TestApplicationACLsManager.TESTUSER1);
        ApplicationId appId = ApplicationId.newInstance(1, 1);
        aclManager.addApplication(appId, aclMap);
        // Application Owner should have all access even if Application ACL is not added
        Assert.assertTrue(aclManager.checkAccess(appOwner, MODIFY_APP, TestApplicationACLsManager.APP_OWNER, appId));
        Assert.assertTrue(aclManager.checkAccess(appOwner, VIEW_APP, TestApplicationACLsManager.APP_OWNER, appId));
        // Admin should have all access
        UserGroupInformation adminUser = UserGroupInformation.createRemoteUser(TestApplicationACLsManager.ADMIN_USER);
        Assert.assertTrue(aclManager.checkAccess(adminUser, VIEW_APP, TestApplicationACLsManager.APP_OWNER, appId));
        Assert.assertTrue(aclManager.checkAccess(adminUser, MODIFY_APP, TestApplicationACLsManager.APP_OWNER, appId));
        // testuser1 should  have view access only
        UserGroupInformation testUser1 = UserGroupInformation.createRemoteUser(TestApplicationACLsManager.TESTUSER1);
        Assert.assertTrue(aclManager.checkAccess(testUser1, VIEW_APP, TestApplicationACLsManager.APP_OWNER, appId));
        Assert.assertFalse(aclManager.checkAccess(testUser1, MODIFY_APP, TestApplicationACLsManager.APP_OWNER, appId));
        // A testuser2 should Not have access
        UserGroupInformation testUser2 = UserGroupInformation.createRemoteUser(TestApplicationACLsManager.TESTUSER2);
        Assert.assertFalse(aclManager.checkAccess(testUser2, VIEW_APP, TestApplicationACLsManager.APP_OWNER, appId));
        Assert.assertFalse(aclManager.checkAccess(testUser2, MODIFY_APP, TestApplicationACLsManager.APP_OWNER, appId));
    }
}

