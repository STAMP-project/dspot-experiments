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
package org.apache.hadoop.yarn.server.timeline.security;


import ApplicationAccessType.MODIFY_APP;
import ApplicationAccessType.VIEW_APP;
import TimelineStore.SystemFilter.ENTITY_OWNER;
import YarnConfiguration.YARN_ACL_ENABLE;
import YarnConfiguration.YARN_ADMIN_ACL;
import java.io.IOException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.hadoop.yarn.api.records.timeline.TimelineDomain;
import org.apache.hadoop.yarn.api.records.timeline.TimelineEntity;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.apache.hadoop.yarn.exceptions.YarnException;
import org.apache.hadoop.yarn.server.timeline.MemoryTimelineStore;
import org.junit.Assert;
import org.junit.Test;


public class TestTimelineACLsManager {
    private static TimelineDomain domain;

    static {
        TestTimelineACLsManager.domain = new TimelineDomain();
        TestTimelineACLsManager.domain.setId("domain_id_1");
        TestTimelineACLsManager.domain.setOwner("owner");
        TestTimelineACLsManager.domain.setReaders("reader");
        TestTimelineACLsManager.domain.setWriters("writer");
    }

    @Test
    public void testYarnACLsNotEnabledForEntity() throws Exception {
        Configuration conf = new YarnConfiguration();
        conf.setBoolean(YARN_ACL_ENABLE, false);
        TimelineACLsManager timelineACLsManager = new TimelineACLsManager(conf);
        timelineACLsManager.setTimelineStore(new TestTimelineACLsManager.TestTimelineStore());
        TimelineEntity entity = new TimelineEntity();
        entity.addPrimaryFilter(ENTITY_OWNER.toString(), "owner");
        entity.setDomainId("domain_id_1");
        Assert.assertTrue("Always true when ACLs are not enabled", timelineACLsManager.checkAccess(UserGroupInformation.createRemoteUser("user"), VIEW_APP, entity));
        Assert.assertTrue("Always true when ACLs are not enabled", timelineACLsManager.checkAccess(UserGroupInformation.createRemoteUser("user"), MODIFY_APP, entity));
    }

    @Test
    public void testYarnACLsEnabledForEntity() throws Exception {
        Configuration conf = new YarnConfiguration();
        conf.setBoolean(YARN_ACL_ENABLE, true);
        conf.set(YARN_ADMIN_ACL, "admin");
        TimelineACLsManager timelineACLsManager = new TimelineACLsManager(conf);
        timelineACLsManager.setTimelineStore(new TestTimelineACLsManager.TestTimelineStore());
        TimelineEntity entity = new TimelineEntity();
        entity.addPrimaryFilter(ENTITY_OWNER.toString(), "owner");
        entity.setDomainId("domain_id_1");
        Assert.assertTrue("Owner should be allowed to view", timelineACLsManager.checkAccess(UserGroupInformation.createRemoteUser("owner"), VIEW_APP, entity));
        Assert.assertTrue("Reader should be allowed to view", timelineACLsManager.checkAccess(UserGroupInformation.createRemoteUser("reader"), VIEW_APP, entity));
        Assert.assertFalse("Other shouldn't be allowed to view", timelineACLsManager.checkAccess(UserGroupInformation.createRemoteUser("other"), VIEW_APP, entity));
        Assert.assertTrue("Admin should be allowed to view", timelineACLsManager.checkAccess(UserGroupInformation.createRemoteUser("admin"), VIEW_APP, entity));
        Assert.assertTrue("Owner should be allowed to modify", timelineACLsManager.checkAccess(UserGroupInformation.createRemoteUser("owner"), MODIFY_APP, entity));
        Assert.assertTrue("Writer should be allowed to modify", timelineACLsManager.checkAccess(UserGroupInformation.createRemoteUser("writer"), MODIFY_APP, entity));
        Assert.assertFalse("Other shouldn't be allowed to modify", timelineACLsManager.checkAccess(UserGroupInformation.createRemoteUser("other"), MODIFY_APP, entity));
        Assert.assertTrue("Admin should be allowed to modify", timelineACLsManager.checkAccess(UserGroupInformation.createRemoteUser("admin"), MODIFY_APP, entity));
    }

    @Test
    public void testCorruptedOwnerInfoForEntity() throws Exception {
        Configuration conf = new YarnConfiguration();
        conf.setBoolean(YARN_ACL_ENABLE, true);
        conf.set(YARN_ADMIN_ACL, "owner");
        TimelineACLsManager timelineACLsManager = new TimelineACLsManager(conf);
        timelineACLsManager.setTimelineStore(new TestTimelineACLsManager.TestTimelineStore());
        TimelineEntity entity = new TimelineEntity();
        try {
            timelineACLsManager.checkAccess(UserGroupInformation.createRemoteUser("owner"), VIEW_APP, entity);
            Assert.fail("Exception is expected");
        } catch (YarnException e) {
            Assert.assertTrue("It's not the exact expected exception", e.getMessage().contains("doesn't exist."));
        }
    }

    @Test
    public void testYarnACLsNotEnabledForDomain() throws Exception {
        Configuration conf = new YarnConfiguration();
        conf.setBoolean(YARN_ACL_ENABLE, false);
        TimelineACLsManager timelineACLsManager = new TimelineACLsManager(conf);
        TimelineDomain domain = new TimelineDomain();
        domain.setOwner("owner");
        Assert.assertTrue("Always true when ACLs are not enabled", timelineACLsManager.checkAccess(UserGroupInformation.createRemoteUser("user"), domain));
    }

    @Test
    public void testYarnACLsEnabledForDomain() throws Exception {
        Configuration conf = new YarnConfiguration();
        conf.setBoolean(YARN_ACL_ENABLE, true);
        conf.set(YARN_ADMIN_ACL, "admin");
        TimelineACLsManager timelineACLsManager = new TimelineACLsManager(conf);
        TimelineDomain domain = new TimelineDomain();
        domain.setOwner("owner");
        Assert.assertTrue("Owner should be allowed to access", timelineACLsManager.checkAccess(UserGroupInformation.createRemoteUser("owner"), domain));
        Assert.assertFalse("Other shouldn't be allowed to access", timelineACLsManager.checkAccess(UserGroupInformation.createRemoteUser("other"), domain));
        Assert.assertTrue("Admin should be allowed to access", timelineACLsManager.checkAccess(UserGroupInformation.createRemoteUser("admin"), domain));
    }

    @Test
    public void testCorruptedOwnerInfoForDomain() throws Exception {
        Configuration conf = new YarnConfiguration();
        conf.setBoolean(YARN_ACL_ENABLE, true);
        conf.set(YARN_ADMIN_ACL, "owner");
        TimelineACLsManager timelineACLsManager = new TimelineACLsManager(conf);
        TimelineDomain domain = new TimelineDomain();
        try {
            timelineACLsManager.checkAccess(UserGroupInformation.createRemoteUser("owner"), domain);
            Assert.fail("Exception is expected");
        } catch (YarnException e) {
            Assert.assertTrue("It's not the exact expected exception", e.getMessage().contains("is corrupted."));
        }
    }

    private static class TestTimelineStore extends MemoryTimelineStore {
        @Override
        public TimelineDomain getDomain(String domainId) throws IOException {
            if (domainId == null) {
                return null;
            } else {
                return TestTimelineACLsManager.domain;
            }
        }
    }
}

