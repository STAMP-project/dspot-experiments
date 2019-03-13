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


import org.apache.hadoop.yarn.api.records.ApplicationId;
import org.apache.hadoop.yarn.api.records.timelineservice.TimelineEntity;
import org.apache.hadoop.yarn.server.timelineservice.reader.TimelineReaderUtils;
import org.apache.hadoop.yarn.server.timelineservice.storage.application.ApplicationRowKey;
import org.apache.hadoop.yarn.server.timelineservice.storage.entity.EntityRowKey;
import org.apache.hadoop.yarn.server.timelineservice.storage.flow.FlowActivityRowKey;
import org.apache.hadoop.yarn.server.timelineservice.storage.flow.FlowRunRowKey;
import org.apache.hadoop.yarn.server.timelineservice.storage.subapplication.SubApplicationRowKey;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test for row key as string.
 */
public class TestRowKeysAsString {
    private static final String CLUSTER = (("cl" + (TimelineReaderUtils.DEFAULT_DELIMITER_CHAR)) + "uster") + (TimelineReaderUtils.DEFAULT_ESCAPE_CHAR);

    private static final String USER = (TimelineReaderUtils.DEFAULT_ESCAPE_CHAR) + "user";

    private static final String SUB_APP_USER = (TimelineReaderUtils.DEFAULT_ESCAPE_CHAR) + "subAppUser";

    private static final String FLOW_NAME = ((("dummy_" + (TimelineReaderUtils.DEFAULT_DELIMITER_CHAR)) + (TimelineReaderUtils.DEFAULT_ESCAPE_CHAR)) + "flow") + (TimelineReaderUtils.DEFAULT_DELIMITER_CHAR);

    private static final Long FLOW_RUN_ID = System.currentTimeMillis();

    private static final String APPLICATION_ID = ApplicationId.newInstance(System.currentTimeMillis(), 1).toString();

    @Test(timeout = 10000)
    public void testApplicationRow() {
        String rowKeyAsString = new ApplicationRowKey(TestRowKeysAsString.CLUSTER, TestRowKeysAsString.USER, TestRowKeysAsString.FLOW_NAME, TestRowKeysAsString.FLOW_RUN_ID, TestRowKeysAsString.APPLICATION_ID).getRowKeyAsString();
        ApplicationRowKey rowKey = ApplicationRowKey.parseRowKeyFromString(rowKeyAsString);
        Assert.assertEquals(TestRowKeysAsString.CLUSTER, rowKey.getClusterId());
        Assert.assertEquals(TestRowKeysAsString.USER, rowKey.getUserId());
        Assert.assertEquals(TestRowKeysAsString.FLOW_NAME, rowKey.getFlowName());
        Assert.assertEquals(TestRowKeysAsString.FLOW_RUN_ID, rowKey.getFlowRunId());
        Assert.assertEquals(TestRowKeysAsString.APPLICATION_ID, rowKey.getAppId());
    }

    @Test(timeout = 10000)
    public void testEntityRowKey() {
        char del = TimelineReaderUtils.DEFAULT_DELIMITER_CHAR;
        char esc = TimelineReaderUtils.DEFAULT_ESCAPE_CHAR;
        String id = (((((((((((del + esc) + "ent") + esc) + del) + "ity") + esc) + del) + esc) + "id") + esc) + del) + esc;
        String type = ((("entity" + esc) + del) + esc) + "Type";
        TimelineEntity entity = new TimelineEntity();
        entity.setId(id);
        entity.setType(type);
        entity.setIdPrefix(54321);
        String rowKeyAsString = getRowKeyAsString();
        EntityRowKey rowKey = EntityRowKey.parseRowKeyFromString(rowKeyAsString);
        Assert.assertEquals(TestRowKeysAsString.CLUSTER, rowKey.getClusterId());
        Assert.assertEquals(TestRowKeysAsString.USER, rowKey.getUserId());
        Assert.assertEquals(TestRowKeysAsString.FLOW_NAME, rowKey.getFlowName());
        Assert.assertEquals(TestRowKeysAsString.FLOW_RUN_ID, rowKey.getFlowRunId());
        Assert.assertEquals(TestRowKeysAsString.APPLICATION_ID, rowKey.getAppId());
        Assert.assertEquals(entity.getType(), rowKey.getEntityType());
        Assert.assertEquals(entity.getIdPrefix(), rowKey.getEntityIdPrefix().longValue());
        Assert.assertEquals(entity.getId(), rowKey.getEntityId());
    }

    @Test(timeout = 10000)
    public void testFlowActivityRowKey() {
        Long ts = 1459900830000L;
        Long dayTimestamp = HBaseTimelineSchemaUtils.getTopOfTheDayTimestamp(ts);
        String rowKeyAsString = new FlowActivityRowKey(TestRowKeysAsString.CLUSTER, ts, TestRowKeysAsString.USER, TestRowKeysAsString.FLOW_NAME).getRowKeyAsString();
        FlowActivityRowKey rowKey = FlowActivityRowKey.parseRowKeyFromString(rowKeyAsString);
        Assert.assertEquals(TestRowKeysAsString.CLUSTER, rowKey.getClusterId());
        Assert.assertEquals(dayTimestamp, rowKey.getDayTimestamp());
        Assert.assertEquals(TestRowKeysAsString.USER, rowKey.getUserId());
        Assert.assertEquals(TestRowKeysAsString.FLOW_NAME, rowKey.getFlowName());
    }

    @Test(timeout = 10000)
    public void testFlowRunRowKey() {
        String rowKeyAsString = new FlowRunRowKey(TestRowKeysAsString.CLUSTER, TestRowKeysAsString.USER, TestRowKeysAsString.FLOW_NAME, TestRowKeysAsString.FLOW_RUN_ID).getRowKeyAsString();
        FlowRunRowKey rowKey = FlowRunRowKey.parseRowKeyFromString(rowKeyAsString);
        Assert.assertEquals(TestRowKeysAsString.CLUSTER, rowKey.getClusterId());
        Assert.assertEquals(TestRowKeysAsString.USER, rowKey.getUserId());
        Assert.assertEquals(TestRowKeysAsString.FLOW_NAME, rowKey.getFlowName());
        Assert.assertEquals(TestRowKeysAsString.FLOW_RUN_ID, rowKey.getFlowRunId());
    }

    @Test(timeout = 10000)
    public void testSubApplicationRowKey() {
        char del = TimelineReaderUtils.DEFAULT_DELIMITER_CHAR;
        char esc = TimelineReaderUtils.DEFAULT_ESCAPE_CHAR;
        String id = (((((((((((del + esc) + "ent") + esc) + del) + "ity") + esc) + del) + esc) + "id") + esc) + del) + esc;
        String type = ((("entity" + esc) + del) + esc) + "Type";
        TimelineEntity entity = new TimelineEntity();
        entity.setId(id);
        entity.setType(type);
        entity.setIdPrefix(54321);
        String rowKeyAsString = getRowKeyAsString();
        SubApplicationRowKey rowKey = SubApplicationRowKey.parseRowKeyFromString(rowKeyAsString);
        Assert.assertEquals(TestRowKeysAsString.SUB_APP_USER, rowKey.getSubAppUserId());
        Assert.assertEquals(TestRowKeysAsString.CLUSTER, rowKey.getClusterId());
        Assert.assertEquals(entity.getType(), rowKey.getEntityType());
        Assert.assertEquals(entity.getIdPrefix(), rowKey.getEntityIdPrefix().longValue());
        Assert.assertEquals(entity.getId(), rowKey.getEntityId());
        Assert.assertEquals(TestRowKeysAsString.USER, rowKey.getUserId());
    }
}

