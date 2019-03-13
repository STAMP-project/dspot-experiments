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
package org.apache.hadoop.hbase.quotas;


import HBaseProtos.TimeUnit.MINUTES;
import QuotaProtos.QuotaScope.MACHINE;
import QuotaProtos.Quotas;
import QuotaProtos.SpaceQuota;
import QuotaProtos.SpaceViolationPolicy.DISABLE;
import QuotaProtos.SpaceViolationPolicy.NO_WRITES;
import QuotaProtos.SpaceViolationPolicy.NO_WRITES_COMPACTIONS;
import QuotaProtos.Throttle;
import QuotaProtos.ThrottleRequest;
import QuotaProtos.ThrottleType.WRITE_NUMBER;
import QuotaProtos.TimedQuota;
import java.io.IOException;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.shaded.protobuf.generated.QuotaProtos;
import org.apache.hadoop.hbase.testclassification.SmallTests;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static SpaceViolationPolicy.DISABLE;
import static SpaceViolationPolicy.NO_WRITES_COMPACTIONS;


@Category(SmallTests.class)
public class TestGlobalQuotaSettingsImpl {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestGlobalQuotaSettingsImpl.class);

    TimedQuota REQUEST_THROTTLE = TimedQuota.newBuilder().setScope(MACHINE).setSoftLimit(100).setTimeUnit(MINUTES).build();

    Throttle THROTTLE = Throttle.newBuilder().setReqNum(REQUEST_THROTTLE).build();

    SpaceQuota SPACE_QUOTA = SpaceQuota.newBuilder().setSoftLimit((1024L * 1024L)).setViolationPolicy(NO_WRITES).build();

    @Test
    public void testMergeThrottle() throws IOException {
        QuotaProtos.Quotas quota = Quotas.newBuilder().setThrottle(THROTTLE).build();
        QuotaProtos.TimedQuota writeQuota = REQUEST_THROTTLE.toBuilder().setSoftLimit(500).build();
        // Unset the req throttle, set a write throttle
        QuotaProtos.ThrottleRequest writeThrottle = ThrottleRequest.newBuilder().setTimedQuota(writeQuota).setType(WRITE_NUMBER).build();
        GlobalQuotaSettingsImpl settings = new GlobalQuotaSettingsImpl("joe", null, null, null, quota);
        GlobalQuotaSettingsImpl merged = settings.merge(new ThrottleSettings("joe", null, null, null, writeThrottle));
        QuotaProtos.Throttle mergedThrottle = merged.getThrottleProto();
        // Verify the request throttle is in place
        Assert.assertTrue(mergedThrottle.hasReqNum());
        QuotaProtos.TimedQuota actualReqNum = mergedThrottle.getReqNum();
        Assert.assertEquals(REQUEST_THROTTLE.getSoftLimit(), actualReqNum.getSoftLimit());
        // Verify the write throttle is in place
        Assert.assertTrue(mergedThrottle.hasWriteNum());
        QuotaProtos.TimedQuota actualWriteNum = mergedThrottle.getWriteNum();
        Assert.assertEquals(writeQuota.getSoftLimit(), actualWriteNum.getSoftLimit());
    }

    @Test
    public void testMergeSpace() throws IOException {
        TableName tn = TableName.valueOf("foo");
        QuotaProtos.Quotas quota = Quotas.newBuilder().setSpace(SPACE_QUOTA).build();
        GlobalQuotaSettingsImpl settings = new GlobalQuotaSettingsImpl(null, tn, null, null, quota);
        // Switch the violation policy to DISABLE
        GlobalQuotaSettingsImpl merged = settings.merge(new SpaceLimitSettings(tn, SPACE_QUOTA.getSoftLimit(), DISABLE));
        QuotaProtos.SpaceQuota mergedSpaceQuota = merged.getSpaceProto();
        Assert.assertEquals(SPACE_QUOTA.getSoftLimit(), mergedSpaceQuota.getSoftLimit());
        Assert.assertEquals(DISABLE, mergedSpaceQuota.getViolationPolicy());
    }

    @Test
    public void testMergeThrottleAndSpace() throws IOException {
        final String ns = "org1";
        QuotaProtos.Quotas quota = Quotas.newBuilder().setThrottle(THROTTLE).setSpace(SPACE_QUOTA).build();
        GlobalQuotaSettingsImpl settings = new GlobalQuotaSettingsImpl(null, null, ns, null, quota);
        QuotaProtos.TimedQuota writeQuota = REQUEST_THROTTLE.toBuilder().setSoftLimit(500).build();
        // Add a write throttle
        QuotaProtos.ThrottleRequest writeThrottle = ThrottleRequest.newBuilder().setTimedQuota(writeQuota).setType(WRITE_NUMBER).build();
        GlobalQuotaSettingsImpl merged = settings.merge(new ThrottleSettings(null, null, ns, null, writeThrottle));
        GlobalQuotaSettingsImpl finalQuota = merged.merge(new SpaceLimitSettings(ns, SPACE_QUOTA.getSoftLimit(), NO_WRITES_COMPACTIONS));
        // Verify both throttle quotas
        QuotaProtos.Throttle throttle = finalQuota.getThrottleProto();
        Assert.assertTrue(throttle.hasReqNum());
        QuotaProtos.TimedQuota reqNumQuota = throttle.getReqNum();
        Assert.assertEquals(REQUEST_THROTTLE.getSoftLimit(), reqNumQuota.getSoftLimit());
        Assert.assertTrue(throttle.hasWriteNum());
        QuotaProtos.TimedQuota writeNumQuota = throttle.getWriteNum();
        Assert.assertEquals(writeQuota.getSoftLimit(), writeNumQuota.getSoftLimit());
        // Verify space quota
        QuotaProtos.SpaceQuota finalSpaceQuota = finalQuota.getSpaceProto();
        Assert.assertEquals(SPACE_QUOTA.getSoftLimit(), finalSpaceQuota.getSoftLimit());
        Assert.assertEquals(NO_WRITES_COMPACTIONS, finalSpaceQuota.getViolationPolicy());
    }
}

