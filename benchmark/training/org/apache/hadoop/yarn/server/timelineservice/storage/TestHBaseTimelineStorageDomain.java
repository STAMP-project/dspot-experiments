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
package org.apache.hadoop.yarn.server.timelineservice.storage;


import DomainColumn.CREATED_TIME;
import DomainColumn.DESCRIPTION;
import DomainColumn.MODIFICATION_TIME;
import DomainColumn.OWNER;
import DomainColumn.READERS;
import DomainColumn.WRITERS;
import DomainTableRW.DEFAULT_TABLE_NAME;
import DomainTableRW.TABLE_NAME_CONF_NAME;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.yarn.api.records.timelineservice.TimelineDomain;
import org.apache.hadoop.yarn.server.timelineservice.collector.TimelineCollectorContext;
import org.apache.hadoop.yarn.server.timelineservice.storage.common.BaseTableRW;
import org.apache.hadoop.yarn.server.timelineservice.storage.common.ColumnRWHelper;
import org.apache.hadoop.yarn.server.timelineservice.storage.domain.DomainRowKey;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test for timeline domain.
 */
public class TestHBaseTimelineStorageDomain {
    private static HBaseTestingUtility util;

    @Test
    public void testDomainIdTable() throws Exception {
        long l = System.currentTimeMillis();
        HBaseTimelineWriterImpl hbi = null;
        Configuration c1 = TestHBaseTimelineStorageDomain.util.getConfiguration();
        String clusterId = "yarn-cluster";
        TimelineCollectorContext context = new TimelineCollectorContext(clusterId, null, null, null, null, null);
        TimelineDomain domain2;
        try {
            hbi = new HBaseTimelineWriterImpl();
            hbi.init(c1);
            // write empty domain
            domain2 = new TimelineDomain();
            domain2.setCreatedTime(l);
            domain2.setDescription("domain-2");
            domain2.setId("domain-2");
            domain2.setModifiedTime(l);
            domain2.setOwner("owner1");
            domain2.setReaders("user1,user2 group1,group2");
            domain2.setWriters("writer1,writer2");
            hbi.write(context, domain2);
            // flush everything to hbase
            hbi.flush();
        } finally {
            if (hbi != null) {
                hbi.close();
            }
        }
        Connection conn = ConnectionFactory.createConnection(c1);
        Table table1 = conn.getTable(BaseTableRW.getTableName(c1, TABLE_NAME_CONF_NAME, DEFAULT_TABLE_NAME));
        byte[] startRow = getRowKey();
        Get g = new Get(startRow);
        Result result = table1.get(g);
        Assert.assertNotNull(result);
        Assert.assertTrue((!(result.isEmpty())));
        byte[] row = result.getRow();
        DomainRowKey domainRowKey = DomainRowKey.parseRowKey(row);
        Assert.assertEquals(domain2.getId(), domainRowKey.getDomainId());
        Assert.assertEquals(clusterId, domainRowKey.getClusterId());
        Long cTime = ((Long) (ColumnRWHelper.readResult(result, CREATED_TIME)));
        String description = ((String) (ColumnRWHelper.readResult(result, DESCRIPTION)));
        Long mTime = ((Long) (ColumnRWHelper.readResult(result, MODIFICATION_TIME)));
        String owners = ((String) (ColumnRWHelper.readResult(result, OWNER)));
        String readers = ((String) (ColumnRWHelper.readResult(result, READERS)));
        String writers = ((String) (ColumnRWHelper.readResult(result, WRITERS)));
        Assert.assertEquals(l, cTime.longValue());
        Assert.assertEquals(l, mTime.longValue());
        Assert.assertEquals("domain-2", description);
        Assert.assertEquals("owner1", owners);
        Assert.assertEquals("user1,user2 group1,group2", readers);
        Assert.assertEquals("writer1,writer2", writers);
    }
}

