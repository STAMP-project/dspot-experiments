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


import QuotaType.SPACE;
import SpaceViolationPolicy.DISABLE;
import SpaceViolationPolicy.NO_WRITES;
import java.io.IOException;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.shaded.protobuf.ProtobufUtil;
import org.apache.hadoop.hbase.shaded.protobuf.generated.MasterProtos.SetQuotaRequest;
import org.apache.hadoop.hbase.shaded.protobuf.generated.QuotaProtos.SpaceLimitRequest;
import org.apache.hadoop.hbase.shaded.protobuf.generated.QuotaProtos.SpaceQuota;
import org.apache.hadoop.hbase.testclassification.SmallTests;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static SpaceViolationPolicy.NO_INSERTS;
import static SpaceViolationPolicy.NO_WRITES;


/**
 * Test class for {@link SpaceLimitSettings}.
 */
@Category({ SmallTests.class })
public class TestSpaceLimitSettings {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestSpaceLimitSettings.class);

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidTableQuotaSizeLimit() {
        new SpaceLimitSettings(TableName.valueOf("foo"), (-1), NO_INSERTS);
    }

    @Test(expected = NullPointerException.class)
    public void testNullTableName() {
        TableName tn = null;
        new SpaceLimitSettings(tn, 1, NO_INSERTS);
    }

    @Test(expected = NullPointerException.class)
    public void testNullTableViolationPolicy() {
        new SpaceLimitSettings(TableName.valueOf("foo"), 1, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidNamespaceQuotaSizeLimit() {
        new SpaceLimitSettings("foo_ns", (-1), NO_INSERTS);
    }

    @Test(expected = NullPointerException.class)
    public void testNullNamespace() {
        String ns = null;
        new SpaceLimitSettings(ns, 1, NO_INSERTS);
    }

    @Test(expected = NullPointerException.class)
    public void testNullNamespaceViolationPolicy() {
        new SpaceLimitSettings("foo_ns", 1, null);
    }

    @Test
    public void testTableQuota() {
        final TableName tableName = TableName.valueOf("foo");
        final long sizeLimit = 1024 * 1024;
        final SpaceViolationPolicy policy = NO_WRITES;
        SpaceLimitSettings settings = new SpaceLimitSettings(tableName, sizeLimit, policy);
        SetQuotaRequest proto = QuotaSettings.buildSetQuotaRequestProto(settings);
        Assert.assertFalse("User should be missing", proto.hasUserName());
        Assert.assertFalse("Namespace should be missing", proto.hasNamespace());
        Assert.assertEquals(ProtobufUtil.toProtoTableName(tableName), proto.getTableName());
        SpaceLimitRequest spaceLimitReq = proto.getSpaceLimit();
        Assert.assertNotNull("SpaceLimitRequest was null", spaceLimitReq);
        SpaceQuota spaceQuota = spaceLimitReq.getQuota();
        Assert.assertNotNull("SpaceQuota was null", spaceQuota);
        Assert.assertEquals(sizeLimit, spaceQuota.getSoftLimit());
        Assert.assertEquals(ProtobufUtil.toProtoViolationPolicy(policy), spaceQuota.getViolationPolicy());
        Assert.assertEquals(SPACE, settings.getQuotaType());
        SpaceLimitSettings copy = new SpaceLimitSettings(tableName, sizeLimit, policy);
        Assert.assertEquals(settings, copy);
        Assert.assertEquals(settings.hashCode(), copy.hashCode());
    }

    @Test
    public void testNamespaceQuota() {
        final String namespace = "foo_ns";
        final long sizeLimit = 1024 * 1024;
        final SpaceViolationPolicy policy = NO_WRITES;
        SpaceLimitSettings settings = new SpaceLimitSettings(namespace, sizeLimit, policy);
        SetQuotaRequest proto = QuotaSettings.buildSetQuotaRequestProto(settings);
        Assert.assertFalse("User should be missing", proto.hasUserName());
        Assert.assertFalse("TableName should be missing", proto.hasTableName());
        Assert.assertEquals(namespace, proto.getNamespace());
        SpaceLimitRequest spaceLimitReq = proto.getSpaceLimit();
        Assert.assertNotNull("SpaceLimitRequest was null", spaceLimitReq);
        SpaceQuota spaceQuota = spaceLimitReq.getQuota();
        Assert.assertNotNull("SpaceQuota was null", spaceQuota);
        Assert.assertEquals(sizeLimit, spaceQuota.getSoftLimit());
        Assert.assertEquals(ProtobufUtil.toProtoViolationPolicy(policy), spaceQuota.getViolationPolicy());
        Assert.assertEquals(SPACE, settings.getQuotaType());
        SpaceLimitSettings copy = new SpaceLimitSettings(namespace, sizeLimit, policy);
        Assert.assertEquals(settings, copy);
        Assert.assertEquals(settings.hashCode(), copy.hashCode());
    }

    @Test
    public void testQuotaMerging() throws IOException {
        TableName tn = TableName.valueOf("foo");
        QuotaSettings originalSettings = QuotaSettingsFactory.limitTableSpace(tn, (1024L * 1024L), DISABLE);
        QuotaSettings largerSizeLimit = QuotaSettingsFactory.limitTableSpace(tn, ((5L * 1024L) * 1024L), DISABLE);
        QuotaSettings differentPolicy = QuotaSettingsFactory.limitTableSpace(tn, (1024L * 1024L), NO_WRITES);
        QuotaSettings incompatibleSettings = QuotaSettingsFactory.limitNamespaceSpace("ns1", ((5L * 1024L) * 1024L), NO_WRITES);
        Assert.assertEquals(originalSettings.merge(largerSizeLimit), largerSizeLimit);
        Assert.assertEquals(originalSettings.merge(differentPolicy), differentPolicy);
        try {
            originalSettings.merge(incompatibleSettings);
            Assert.fail("Should not be able to merge a Table space quota with a namespace space quota.");
        } catch (IllegalArgumentException e) {
            // pass
        }
    }
}

