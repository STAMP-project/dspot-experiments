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


import java.io.IOException;
import org.apache.hadoop.test.GenericTestUtils;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test the behavior when disabling the Router quota.
 */
public class TestDisableRouterQuota {
    private static Router router;

    @Test
    public void testSetQuota() throws Exception {
        long nsQuota = 1024;
        long ssQuota = 1024;
        try {
            Quota quotaModule = TestDisableRouterQuota.router.getRpcServer().getQuotaModule();
            quotaModule.setQuota("/test", nsQuota, ssQuota, null);
            Assert.fail("The setQuota call should fail.");
        } catch (IOException ioe) {
            GenericTestUtils.assertExceptionContains("The quota system is disabled in Router.", ioe);
        }
    }

    @Test
    public void testGetQuotaUsage() throws Exception {
        try {
            Quota quotaModule = TestDisableRouterQuota.router.getRpcServer().getQuotaModule();
            quotaModule.getQuotaUsage("/test");
            Assert.fail("The getQuotaUsage call should fail.");
        } catch (IOException ioe) {
            GenericTestUtils.assertExceptionContains("The quota system is disabled in Router.", ioe);
        }
    }
}

