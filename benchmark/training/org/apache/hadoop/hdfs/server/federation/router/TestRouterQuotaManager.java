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
package org.apache.hadoop.hdfs.server.federation.router;


import HdfsConstants.QUOTA_RESET;
import RouterQuotaUsage.Builder;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests for class {@link RouterQuotaManager}.
 */
public class TestRouterQuotaManager {
    private static RouterQuotaManager manager;

    @Test
    public void testGetChildrenPaths() {
        RouterQuotaUsage quotaUsage = new RouterQuotaUsage.Builder().build();
        TestRouterQuotaManager.manager.put("/path1", quotaUsage);
        TestRouterQuotaManager.manager.put("/path2", quotaUsage);
        TestRouterQuotaManager.manager.put("/path1/subdir", quotaUsage);
        TestRouterQuotaManager.manager.put("/path1/subdir/subdir", quotaUsage);
        Set<String> childrenPaths = TestRouterQuotaManager.manager.getPaths("/path1");
        Assert.assertEquals(3, childrenPaths.size());
        Assert.assertTrue((((childrenPaths.contains("/path1/subdir")) && (childrenPaths.contains("/path1/subdir/subdir"))) && (childrenPaths.contains("/path1"))));
        // test for corner case
        TestRouterQuotaManager.manager.put("/path3", quotaUsage);
        TestRouterQuotaManager.manager.put("/path3/subdir", quotaUsage);
        TestRouterQuotaManager.manager.put("/path3-subdir", quotaUsage);
        childrenPaths = TestRouterQuotaManager.manager.getPaths("/path3");
        Assert.assertEquals(2, childrenPaths.size());
        // path /path3-subdir should not be returned
        Assert.assertTrue((((childrenPaths.contains("/path3")) && (childrenPaths.contains("/path3/subdir"))) && (!(childrenPaths.contains("/path3-subdir")))));
    }

    @Test
    public void testGetQuotaUsage() {
        RouterQuotaUsage quotaGet;
        // test case1: get quota with an non-exist path
        quotaGet = TestRouterQuotaManager.manager.getQuotaUsage("/non-exist-path");
        Assert.assertNull(quotaGet);
        // test case2: get quota from an no-quota set path
        RouterQuotaUsage.Builder quota = new RouterQuotaUsage.Builder().quota(QUOTA_RESET).spaceQuota(QUOTA_RESET);
        TestRouterQuotaManager.manager.put("/noQuotaSet", quota.build());
        quotaGet = TestRouterQuotaManager.manager.getQuotaUsage("/noQuotaSet");
        // it should return null
        Assert.assertNull(quotaGet);
        // test case3: get quota from an quota-set path
        quota.quota(1);
        quota.spaceQuota(QUOTA_RESET);
        TestRouterQuotaManager.manager.put("/hasQuotaSet", quota.build());
        quotaGet = TestRouterQuotaManager.manager.getQuotaUsage("/hasQuotaSet");
        Assert.assertEquals(1, quotaGet.getQuota());
        Assert.assertEquals(QUOTA_RESET, quotaGet.getSpaceQuota());
        // test case4: get quota with an non-exist child path
        quotaGet = TestRouterQuotaManager.manager.getQuotaUsage("/hasQuotaSet/file");
        // it will return the nearest ancestor which quota was set
        Assert.assertEquals(1, quotaGet.getQuota());
        Assert.assertEquals(QUOTA_RESET, quotaGet.getSpaceQuota());
        // test case5: get quota with an child path which its parent
        // wasn't quota set
        quota.quota(QUOTA_RESET);
        quota.spaceQuota(QUOTA_RESET);
        TestRouterQuotaManager.manager.put("/hasQuotaSet/noQuotaSet", quota.build());
        // here should returns the quota of path /hasQuotaSet
        // (the nearest ancestor which quota was set)
        quotaGet = TestRouterQuotaManager.manager.getQuotaUsage("/hasQuotaSet/noQuotaSet/file");
        Assert.assertEquals(1, quotaGet.getQuota());
        Assert.assertEquals(QUOTA_RESET, quotaGet.getSpaceQuota());
        // test case6: get quota with an child path which its parent was quota set
        quota.quota(2);
        quota.spaceQuota(QUOTA_RESET);
        TestRouterQuotaManager.manager.put("/hasQuotaSet/hasQuotaSet", quota.build());
        // here should return the quota of path /hasQuotaSet/hasQuotaSet
        quotaGet = TestRouterQuotaManager.manager.getQuotaUsage("/hasQuotaSet/hasQuotaSet/file");
        Assert.assertEquals(2, quotaGet.getQuota());
        Assert.assertEquals(QUOTA_RESET, quotaGet.getSpaceQuota());
    }
}

