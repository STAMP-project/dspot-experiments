/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.yarn.server.timelineservice.reader;


import YarnConfiguration.FILTER_ENTITY_LIST_BY_USER;
import YarnConfiguration.YARN_ACL_ENABLE;
import YarnConfiguration.YARN_ADMIN_ACL;
import java.util.Set;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.hadoop.yarn.api.records.timelineservice.TimelineEntity;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.apache.hadoop.yarn.webapp.ForbiddenException;
import org.junit.Assert;
import org.junit.Test;


public class TestTimelineReaderWebServicesBasicAcl {
    private TimelineReaderManager manager;

    private static String adminUser = "admin";

    private static UserGroupInformation adminUgi = UserGroupInformation.createRemoteUser(TestTimelineReaderWebServicesBasicAcl.adminUser);

    private Configuration config;

    @Test
    public void testTimelineReaderManagerAclsWhenDisabled() throws Exception {
        config.setBoolean(YARN_ACL_ENABLE, false);
        config.set(YARN_ADMIN_ACL, TestTimelineReaderWebServicesBasicAcl.adminUser);
        manager = new TimelineReaderManager(null);
        manager.init(config);
        manager.start();
        // when acls are disabled, always return true
        Assert.assertTrue(manager.checkAccess(null));
        // filter is disabled, so should return false
        Assert.assertFalse(TimelineReaderWebServices.isDisplayEntityPerUserFilterEnabled(config));
    }

    @Test
    public void testTimelineReaderManagerAclsWhenEnabled() throws Exception {
        Configuration config = new YarnConfiguration();
        config.setBoolean(YARN_ACL_ENABLE, true);
        config.setBoolean(FILTER_ENTITY_LIST_BY_USER, true);
        config.set(YARN_ADMIN_ACL, TestTimelineReaderWebServicesBasicAcl.adminUser);
        manager = new TimelineReaderManager(null);
        manager.init(config);
        manager.start();
        String user1 = "user1";
        String user2 = "user2";
        UserGroupInformation user1Ugi = UserGroupInformation.createRemoteUser(user1);
        UserGroupInformation user2Ugi = UserGroupInformation.createRemoteUser(user2);
        // false because ugi is null
        Assert.assertFalse(TimelineReaderWebServices.validateAuthUserWithEntityUser(manager, null, user1));
        // false because ugi is null in non-secure cluster. User must pass
        // ?user.name as query params in REST end points.
        try {
            TimelineReaderWebServices.checkAccess(manager, null, user1);
            Assert.fail("user1Ugi is not allowed to view user1");
        } catch (ForbiddenException e) {
            // expected
        }
        // incoming ugi is admin asking for entity owner user1
        Assert.assertTrue(TimelineReaderWebServices.checkAccess(manager, TestTimelineReaderWebServicesBasicAcl.adminUgi, user1));
        // incoming ugi is admin asking for entity owner user1
        Assert.assertTrue(TimelineReaderWebServices.checkAccess(manager, TestTimelineReaderWebServicesBasicAcl.adminUgi, user2));
        // incoming ugi is non-admin i.e user1Ugi asking for entity owner user2
        try {
            TimelineReaderWebServices.checkAccess(manager, user1Ugi, user2);
            Assert.fail("user1Ugi is not allowed to view user2");
        } catch (ForbiddenException e) {
            // expected
        }
        // incoming ugi is non-admin i.e user2Ugi asking for entity owner user1
        try {
            TimelineReaderWebServices.checkAccess(manager, user1Ugi, user2);
            Assert.fail("user2Ugi is not allowed to view user1");
        } catch (ForbiddenException e) {
            // expected
        }
        String userKey = "user";
        // incoming ugi is admin asking for entities
        Set<TimelineEntity> entities = createEntities(10, userKey);
        TimelineReaderWebServices.checkAccess(manager, TestTimelineReaderWebServicesBasicAcl.adminUgi, entities, userKey, true);
        // admin is allowed to view other entities
        Assert.assertTrue(((entities.size()) == 10));
        // incoming ugi is user1Ugi asking for entities
        // only user1 entities are allowed to view
        entities = createEntities(5, userKey);
        TimelineReaderWebServices.checkAccess(manager, user1Ugi, entities, userKey, true);
        Assert.assertTrue(((entities.size()) == 1));
        Assert.assertEquals(user1, entities.iterator().next().getInfo().get(userKey));
        // incoming ugi is user2Ugi asking for entities
        // only user2 entities are allowed to view
        entities = createEntities(8, userKey);
        TimelineReaderWebServices.checkAccess(manager, user2Ugi, entities, userKey, true);
        Assert.assertTrue(((entities.size()) == 1));
        Assert.assertEquals(user2, entities.iterator().next().getInfo().get(userKey));
    }
}

