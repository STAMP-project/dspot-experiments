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
import QuotaProtos.SpaceViolationPolicy.DISABLE;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.shaded.protobuf.ProtobufUtil;
import org.apache.hadoop.hbase.shaded.protobuf.generated.QuotaProtos.Quotas;
import org.apache.hadoop.hbase.shaded.protobuf.generated.QuotaProtos.SpaceLimitRequest;
import org.apache.hadoop.hbase.shaded.protobuf.generated.QuotaProtos.SpaceQuota;
import org.apache.hadoop.hbase.shaded.protobuf.generated.QuotaProtos.Throttle;
import org.apache.hadoop.hbase.shaded.protobuf.generated.QuotaProtos.TimedQuota;
import org.apache.hadoop.hbase.testclassification.SmallTests;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static SpaceViolationPolicy.NO_INSERTS;


/**
 * Test class for {@link QuotaSettingsFactory}.
 */
@Category(SmallTests.class)
public class TestQuotaSettingsFactory {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestQuotaSettingsFactory.class);

    @Test
    public void testAllQuotasAddedToList() {
        final SpaceQuota spaceQuota = // Disable the table
        // 50G
        SpaceQuota.newBuilder().setSoftLimit((((1024L * 1024L) * 1024L) * 50L)).setViolationPolicy(DISABLE).build();
        final long readLimit = 1000;
        final long writeLimit = 500;
        final Throttle throttle = // 500 write reqs/min
        // 1000 read reqs/min
        Throttle.newBuilder().setReadNum(TimedQuota.newBuilder().setSoftLimit(readLimit).setTimeUnit(MINUTES).build()).setWriteNum(TimedQuota.newBuilder().setSoftLimit(writeLimit).setTimeUnit(MINUTES).build()).build();
        final Quotas quotas = // Set some RPC limits
        // Set the FS quotas
        Quotas.newBuilder().setSpace(spaceQuota).setThrottle(throttle).build();
        final TableName tn = TableName.valueOf("my_table");
        List<QuotaSettings> settings = QuotaSettingsFactory.fromTableQuotas(tn, quotas);
        Assert.assertEquals(3, settings.size());
        boolean seenRead = false;
        boolean seenWrite = false;
        boolean seenSpace = false;
        for (QuotaSettings setting : settings) {
            if (setting instanceof ThrottleSettings) {
                ThrottleSettings throttleSettings = ((ThrottleSettings) (setting));
                switch (throttleSettings.getThrottleType()) {
                    case READ_NUMBER :
                        Assert.assertFalse("Should not have multiple read quotas", seenRead);
                        Assert.assertEquals(readLimit, throttleSettings.getSoftLimit());
                        Assert.assertEquals(TimeUnit.MINUTES, throttleSettings.getTimeUnit());
                        Assert.assertEquals(tn, throttleSettings.getTableName());
                        Assert.assertNull("Username should be null", throttleSettings.getUserName());
                        Assert.assertNull("Namespace should be null", throttleSettings.getNamespace());
                        Assert.assertNull("RegionServer should be null", throttleSettings.getRegionServer());
                        seenRead = true;
                        break;
                    case WRITE_NUMBER :
                        Assert.assertFalse("Should not have multiple write quotas", seenWrite);
                        Assert.assertEquals(writeLimit, throttleSettings.getSoftLimit());
                        Assert.assertEquals(TimeUnit.MINUTES, throttleSettings.getTimeUnit());
                        Assert.assertEquals(tn, throttleSettings.getTableName());
                        Assert.assertNull("Username should be null", throttleSettings.getUserName());
                        Assert.assertNull("Namespace should be null", throttleSettings.getNamespace());
                        Assert.assertNull("RegionServer should be null", throttleSettings.getRegionServer());
                        seenWrite = true;
                        break;
                    default :
                        Assert.fail(("Unexpected throttle type: " + (throttleSettings.getThrottleType())));
                }
            } else
                if (setting instanceof SpaceLimitSettings) {
                    Assert.assertFalse("Should not have multiple space quotas", seenSpace);
                    SpaceLimitSettings spaceLimit = ((SpaceLimitSettings) (setting));
                    Assert.assertEquals(tn, spaceLimit.getTableName());
                    Assert.assertNull("Username should be null", spaceLimit.getUserName());
                    Assert.assertNull("Namespace should be null", spaceLimit.getNamespace());
                    Assert.assertNull("RegionServer should be null", spaceLimit.getRegionServer());
                    Assert.assertTrue("SpaceLimitSettings should have a SpaceQuota", spaceLimit.getProto().hasQuota());
                    Assert.assertEquals(spaceQuota, spaceLimit.getProto().getQuota());
                    seenSpace = true;
                } else {
                    Assert.fail(("Unexpected QuotaSettings implementation: " + (setting.getClass())));
                }

        }
        Assert.assertTrue("Should have seen a read quota", seenRead);
        Assert.assertTrue("Should have seen a write quota", seenWrite);
        Assert.assertTrue("Should have seen a space quota", seenSpace);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNeitherTableNorNamespace() {
        final SpaceQuota spaceQuota = SpaceQuota.newBuilder().setSoftLimit(1L).setViolationPolicy(DISABLE).build();
        QuotaSettingsFactory.fromSpace(null, null, spaceQuota);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testBothTableAndNamespace() {
        final SpaceQuota spaceQuota = SpaceQuota.newBuilder().setSoftLimit(1L).setViolationPolicy(DISABLE).build();
        QuotaSettingsFactory.fromSpace(TableName.valueOf("foo"), "bar", spaceQuota);
    }

    @Test
    public void testSpaceLimitSettings() {
        final TableName tableName = TableName.valueOf("foo");
        final long sizeLimit = ((1024L * 1024L) * 1024L) * 75;// 75GB

        final SpaceViolationPolicy violationPolicy = NO_INSERTS;
        QuotaSettings settings = QuotaSettingsFactory.limitTableSpace(tableName, sizeLimit, violationPolicy);
        Assert.assertNotNull("QuotaSettings should not be null", settings);
        Assert.assertTrue("Should be an instance of SpaceLimitSettings", (settings instanceof SpaceLimitSettings));
        SpaceLimitSettings spaceLimitSettings = ((SpaceLimitSettings) (settings));
        SpaceLimitRequest protoRequest = spaceLimitSettings.getProto();
        Assert.assertTrue("Request should have a SpaceQuota", protoRequest.hasQuota());
        SpaceQuota quota = protoRequest.getQuota();
        Assert.assertEquals(sizeLimit, quota.getSoftLimit());
        Assert.assertEquals(violationPolicy, ProtobufUtil.toViolationPolicy(quota.getViolationPolicy()));
        Assert.assertFalse("The remove attribute should be false", quota.getRemove());
    }

    @Test
    public void testSpaceLimitSettingsForDeletes() {
        final String ns = "ns1";
        final TableName tn = TableName.valueOf("tn1");
        QuotaSettings nsSettings = QuotaSettingsFactory.removeNamespaceSpaceLimit(ns);
        Assert.assertNotNull("QuotaSettings should not be null", nsSettings);
        Assert.assertTrue("Should be an instance of SpaceLimitSettings", (nsSettings instanceof SpaceLimitSettings));
        SpaceLimitRequest nsProto = getProto();
        Assert.assertTrue("Request should have a SpaceQuota", nsProto.hasQuota());
        Assert.assertTrue("The remove attribute should be true", nsProto.getQuota().getRemove());
        QuotaSettings tableSettings = QuotaSettingsFactory.removeTableSpaceLimit(tn);
        Assert.assertNotNull("QuotaSettings should not be null", tableSettings);
        Assert.assertTrue("Should be an instance of SpaceLimitSettings", (tableSettings instanceof SpaceLimitSettings));
        SpaceLimitRequest tableProto = getProto();
        Assert.assertTrue("Request should have a SpaceQuota", tableProto.hasQuota());
        Assert.assertTrue("The remove attribute should be true", tableProto.getQuota().getRemove());
    }
}

