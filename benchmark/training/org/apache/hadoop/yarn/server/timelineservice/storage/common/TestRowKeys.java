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
package org.apache.hadoop.yarn.server.timelineservice.storage.common;


import Separator.QUALIFIERS;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import org.apache.hadoop.yarn.api.records.timelineservice.TimelineEntity;
import org.apache.hadoop.yarn.server.timelineservice.storage.application.ApplicationRowKey;
import org.apache.hadoop.yarn.server.timelineservice.storage.application.ApplicationRowKeyPrefix;
import org.apache.hadoop.yarn.server.timelineservice.storage.apptoflow.AppToFlowRowKey;
import org.apache.hadoop.yarn.server.timelineservice.storage.domain.DomainRowKey;
import org.apache.hadoop.yarn.server.timelineservice.storage.entity.EntityRowKey;
import org.apache.hadoop.yarn.server.timelineservice.storage.entity.EntityRowKeyPrefix;
import org.apache.hadoop.yarn.server.timelineservice.storage.flow.FlowActivityRowKey;
import org.apache.hadoop.yarn.server.timelineservice.storage.flow.FlowActivityRowKeyPrefix;
import org.apache.hadoop.yarn.server.timelineservice.storage.flow.FlowRunRowKey;
import org.apache.hadoop.yarn.server.timelineservice.storage.subapplication.SubApplicationRowKey;
import org.junit.Assert;
import org.junit.Test;

import static Separator.VARIABLE_SIZE;


/**
 * Class to test the row key structures for various tables.
 */
public class TestRowKeys {
    private static final String QUALIFIER_SEP = QUALIFIERS.getValue();

    private static final byte[] QUALIFIER_SEP_BYTES = Bytes.toBytes(TestRowKeys.QUALIFIER_SEP);

    private static final String CLUSTER = ("cl" + (TestRowKeys.QUALIFIER_SEP)) + "uster";

    private static final String USER = (TestRowKeys.QUALIFIER_SEP) + "user";

    private static final String SUB_APP_USER = (TestRowKeys.QUALIFIER_SEP) + "subAppUser";

    private static final String FLOW_NAME = (("dummy_" + (TestRowKeys.QUALIFIER_SEP)) + "flow") + (TestRowKeys.QUALIFIER_SEP);

    private static final Long FLOW_RUN_ID;

    private static final String APPLICATION_ID;

    static {
        long runid = (Long.MAX_VALUE) - 900L;
        byte[] longMaxByteArr = Bytes.toBytes(Long.MAX_VALUE);
        byte[] byteArr = Bytes.toBytes(runid);
        int sepByteLen = TestRowKeys.QUALIFIER_SEP_BYTES.length;
        if (sepByteLen <= (byteArr.length)) {
            for (int i = 0; i < sepByteLen; i++) {
                byteArr[i] = ((byte) ((longMaxByteArr[i]) - (TestRowKeys.QUALIFIER_SEP_BYTES[i])));
            }
        }
        FLOW_RUN_ID = Bytes.toLong(byteArr);
        long clusterTs = System.currentTimeMillis();
        byteArr = Bytes.toBytes(clusterTs);
        if (sepByteLen <= (byteArr.length)) {
            for (int i = 0; i < sepByteLen; i++) {
                byteArr[(((byteArr.length) - sepByteLen) + i)] = ((byte) ((longMaxByteArr[(((byteArr.length) - sepByteLen) + i)]) - (TestRowKeys.QUALIFIER_SEP_BYTES[i])));
            }
        }
        clusterTs = Bytes.toLong(byteArr);
        int seqId = 222;
        APPLICATION_ID = ApplicationId.newInstance(clusterTs, seqId).toString();
    }

    @Test
    public void testApplicationRowKey() {
        byte[] byteRowKey = new ApplicationRowKey(TestRowKeys.CLUSTER, TestRowKeys.USER, TestRowKeys.FLOW_NAME, TestRowKeys.FLOW_RUN_ID, TestRowKeys.APPLICATION_ID).getRowKey();
        ApplicationRowKey rowKey = ApplicationRowKey.parseRowKey(byteRowKey);
        Assert.assertEquals(TestRowKeys.CLUSTER, rowKey.getClusterId());
        Assert.assertEquals(TestRowKeys.USER, rowKey.getUserId());
        Assert.assertEquals(TestRowKeys.FLOW_NAME, rowKey.getFlowName());
        Assert.assertEquals(TestRowKeys.FLOW_RUN_ID, rowKey.getFlowRunId());
        Assert.assertEquals(TestRowKeys.APPLICATION_ID, rowKey.getAppId());
        byte[] byteRowKeyPrefix = new ApplicationRowKeyPrefix(TestRowKeys.CLUSTER, TestRowKeys.USER, TestRowKeys.FLOW_NAME, TestRowKeys.FLOW_RUN_ID).getRowKeyPrefix();
        byte[][] splits = QUALIFIERS.split(byteRowKeyPrefix, new int[]{ VARIABLE_SIZE, VARIABLE_SIZE, VARIABLE_SIZE, Bytes.SIZEOF_LONG, VARIABLE_SIZE });
        Assert.assertEquals(5, splits.length);
        Assert.assertEquals(0, splits[4].length);
        Assert.assertEquals(TestRowKeys.FLOW_NAME, QUALIFIERS.decode(Bytes.toString(splits[2])));
        Assert.assertEquals(TestRowKeys.FLOW_RUN_ID, ((Long) (LongConverter.invertLong(Bytes.toLong(splits[3])))));
        TestRowKeys.verifyRowPrefixBytes(byteRowKeyPrefix);
        byteRowKeyPrefix = new ApplicationRowKeyPrefix(TestRowKeys.CLUSTER, TestRowKeys.USER, TestRowKeys.FLOW_NAME).getRowKeyPrefix();
        splits = QUALIFIERS.split(byteRowKeyPrefix, new int[]{ VARIABLE_SIZE, VARIABLE_SIZE, VARIABLE_SIZE, VARIABLE_SIZE });
        Assert.assertEquals(4, splits.length);
        Assert.assertEquals(0, splits[3].length);
        Assert.assertEquals(TestRowKeys.FLOW_NAME, QUALIFIERS.decode(Bytes.toString(splits[2])));
        TestRowKeys.verifyRowPrefixBytes(byteRowKeyPrefix);
    }

    /**
     * Tests the converters indirectly through the public methods of the
     * corresponding rowkey.
     */
    @Test
    public void testAppToFlowRowKey() {
        byte[] byteRowKey = new AppToFlowRowKey(TestRowKeys.APPLICATION_ID).getRowKey();
        AppToFlowRowKey rowKey = AppToFlowRowKey.parseRowKey(byteRowKey);
        Assert.assertEquals(TestRowKeys.APPLICATION_ID, rowKey.getAppId());
    }

    @Test
    public void testEntityRowKey() {
        TimelineEntity entity = new TimelineEntity();
        entity.setId("!ent!ity!!id!");
        entity.setType("entity!Type");
        entity.setIdPrefix(54321);
        byte[] byteRowKey = getRowKey();
        EntityRowKey rowKey = EntityRowKey.parseRowKey(byteRowKey);
        Assert.assertEquals(TestRowKeys.CLUSTER, rowKey.getClusterId());
        Assert.assertEquals(TestRowKeys.USER, rowKey.getUserId());
        Assert.assertEquals(TestRowKeys.FLOW_NAME, rowKey.getFlowName());
        Assert.assertEquals(TestRowKeys.FLOW_RUN_ID, rowKey.getFlowRunId());
        Assert.assertEquals(TestRowKeys.APPLICATION_ID, rowKey.getAppId());
        Assert.assertEquals(entity.getType(), rowKey.getEntityType());
        Assert.assertEquals(entity.getIdPrefix(), rowKey.getEntityIdPrefix().longValue());
        Assert.assertEquals(entity.getId(), rowKey.getEntityId());
        byte[] byteRowKeyPrefix = getRowKeyPrefix();
        byte[][] splits = QUALIFIERS.split(byteRowKeyPrefix, new int[]{ VARIABLE_SIZE, VARIABLE_SIZE, VARIABLE_SIZE, Bytes.SIZEOF_LONG, AppIdKeyConverter.getKeySize(), VARIABLE_SIZE, Bytes.SIZEOF_LONG, VARIABLE_SIZE });
        Assert.assertEquals(7, splits.length);
        Assert.assertEquals(TestRowKeys.APPLICATION_ID, new AppIdKeyConverter().decode(splits[4]));
        Assert.assertEquals(entity.getType(), QUALIFIERS.decode(Bytes.toString(splits[5])));
        TestRowKeys.verifyRowPrefixBytes(byteRowKeyPrefix);
        byteRowKeyPrefix = new EntityRowKeyPrefix(TestRowKeys.CLUSTER, TestRowKeys.USER, TestRowKeys.FLOW_NAME, TestRowKeys.FLOW_RUN_ID, TestRowKeys.APPLICATION_ID).getRowKeyPrefix();
        splits = QUALIFIERS.split(byteRowKeyPrefix, new int[]{ VARIABLE_SIZE, VARIABLE_SIZE, VARIABLE_SIZE, Bytes.SIZEOF_LONG, AppIdKeyConverter.getKeySize(), VARIABLE_SIZE });
        Assert.assertEquals(6, splits.length);
        Assert.assertEquals(0, splits[5].length);
        AppIdKeyConverter appIdKeyConverter = new AppIdKeyConverter();
        Assert.assertEquals(TestRowKeys.APPLICATION_ID, appIdKeyConverter.decode(splits[4]));
        TestRowKeys.verifyRowPrefixBytes(byteRowKeyPrefix);
    }

    @Test
    public void testFlowActivityRowKey() {
        Long ts = 1459900830000L;
        Long dayTimestamp = HBaseTimelineSchemaUtils.getTopOfTheDayTimestamp(ts);
        byte[] byteRowKey = new FlowActivityRowKey(TestRowKeys.CLUSTER, ts, TestRowKeys.USER, TestRowKeys.FLOW_NAME).getRowKey();
        FlowActivityRowKey rowKey = FlowActivityRowKey.parseRowKey(byteRowKey);
        Assert.assertEquals(TestRowKeys.CLUSTER, rowKey.getClusterId());
        Assert.assertEquals(dayTimestamp, rowKey.getDayTimestamp());
        Assert.assertEquals(TestRowKeys.USER, rowKey.getUserId());
        Assert.assertEquals(TestRowKeys.FLOW_NAME, rowKey.getFlowName());
        byte[] byteRowKeyPrefix = new FlowActivityRowKeyPrefix(TestRowKeys.CLUSTER).getRowKeyPrefix();
        byte[][] splits = QUALIFIERS.split(byteRowKeyPrefix, new int[]{ VARIABLE_SIZE, VARIABLE_SIZE });
        Assert.assertEquals(2, splits.length);
        Assert.assertEquals(0, splits[1].length);
        Assert.assertEquals(TestRowKeys.CLUSTER, QUALIFIERS.decode(Bytes.toString(splits[0])));
        TestRowKeys.verifyRowPrefixBytes(byteRowKeyPrefix);
        byteRowKeyPrefix = new FlowActivityRowKeyPrefix(TestRowKeys.CLUSTER, ts).getRowKeyPrefix();
        splits = QUALIFIERS.split(byteRowKeyPrefix, new int[]{ VARIABLE_SIZE, Bytes.SIZEOF_LONG, VARIABLE_SIZE });
        Assert.assertEquals(3, splits.length);
        Assert.assertEquals(0, splits[2].length);
        Assert.assertEquals(TestRowKeys.CLUSTER, QUALIFIERS.decode(Bytes.toString(splits[0])));
        Assert.assertEquals(ts, ((Long) (LongConverter.invertLong(Bytes.toLong(splits[1])))));
        TestRowKeys.verifyRowPrefixBytes(byteRowKeyPrefix);
    }

    @Test
    public void testFlowRunRowKey() {
        byte[] byteRowKey = new FlowRunRowKey(TestRowKeys.CLUSTER, TestRowKeys.USER, TestRowKeys.FLOW_NAME, TestRowKeys.FLOW_RUN_ID).getRowKey();
        FlowRunRowKey rowKey = FlowRunRowKey.parseRowKey(byteRowKey);
        Assert.assertEquals(TestRowKeys.CLUSTER, rowKey.getClusterId());
        Assert.assertEquals(TestRowKeys.USER, rowKey.getUserId());
        Assert.assertEquals(TestRowKeys.FLOW_NAME, rowKey.getFlowName());
        Assert.assertEquals(TestRowKeys.FLOW_RUN_ID, rowKey.getFlowRunId());
        byte[] byteRowKeyPrefix = new FlowRunRowKey(TestRowKeys.CLUSTER, TestRowKeys.USER, TestRowKeys.FLOW_NAME, null).getRowKey();
        byte[][] splits = QUALIFIERS.split(byteRowKeyPrefix, new int[]{ VARIABLE_SIZE, VARIABLE_SIZE, VARIABLE_SIZE, VARIABLE_SIZE });
        Assert.assertEquals(4, splits.length);
        Assert.assertEquals(0, splits[3].length);
        Assert.assertEquals(TestRowKeys.FLOW_NAME, QUALIFIERS.decode(Bytes.toString(splits[2])));
        TestRowKeys.verifyRowPrefixBytes(byteRowKeyPrefix);
    }

    @Test
    public void testSubAppRowKey() {
        TimelineEntity entity = new TimelineEntity();
        entity.setId("entity1");
        entity.setType("DAG");
        entity.setIdPrefix(54321);
        byte[] byteRowKey = getRowKey();
        SubApplicationRowKey rowKey = SubApplicationRowKey.parseRowKey(byteRowKey);
        Assert.assertEquals(TestRowKeys.CLUSTER, rowKey.getClusterId());
        Assert.assertEquals(TestRowKeys.SUB_APP_USER, rowKey.getSubAppUserId());
        Assert.assertEquals(entity.getType(), rowKey.getEntityType());
        Assert.assertEquals(entity.getIdPrefix(), rowKey.getEntityIdPrefix().longValue());
        Assert.assertEquals(entity.getId(), rowKey.getEntityId());
        Assert.assertEquals(TestRowKeys.USER, rowKey.getUserId());
    }

    @Test
    public void testDomainRowKey() {
        String clusterId = "cluster1@dc1";
        String domainId = "helloworld";
        byte[] byteRowKey = new DomainRowKey(clusterId, domainId).getRowKey();
        DomainRowKey rowKey = DomainRowKey.parseRowKey(byteRowKey);
        Assert.assertEquals(clusterId, rowKey.getClusterId());
        Assert.assertEquals(domainId, rowKey.getDomainId());
        String rowKeyStr = rowKey.getRowKeyAsString();
        DomainRowKey drk = DomainRowKey.parseRowKeyFromString(rowKeyStr);
        Assert.assertEquals(drk.getClusterId(), rowKey.getClusterId());
        Assert.assertEquals(drk.getDomainId(), rowKey.getDomainId());
    }

    @Test
    public void testDomainRowKeySpecialChars() {
        String clusterId = "cluster1!temp!dc1";
        String domainId = "hello=world";
        byte[] byteRowKey = new DomainRowKey(clusterId, domainId).getRowKey();
        DomainRowKey rowKey = DomainRowKey.parseRowKey(byteRowKey);
        Assert.assertEquals(clusterId, rowKey.getClusterId());
        Assert.assertEquals(domainId, rowKey.getDomainId());
        String rowKeyStr = rowKey.getRowKeyAsString();
        DomainRowKey drk = DomainRowKey.parseRowKeyFromString(rowKeyStr);
        Assert.assertEquals(drk.getClusterId(), rowKey.getClusterId());
        Assert.assertEquals(drk.getDomainId(), rowKey.getDomainId());
    }
}

