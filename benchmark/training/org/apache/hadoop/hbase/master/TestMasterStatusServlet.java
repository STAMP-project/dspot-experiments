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
package org.apache.hadoop.hbase.master;


import java.io.IOException;
import java.io.StringWriter;
import java.util.List;
import java.util.Set;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HRegionInfo;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.ServerName;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.testclassification.MasterTests;
import org.apache.hadoop.hbase.testclassification.MediumTests;
import org.apache.hadoop.hbase.tmpl.master.MasterStatusTmpl;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hbase.thirdparty.com.google.common.collect.Lists;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;


/**
 * Tests for the master status page and its template.
 */
@Category({ MasterTests.class, MediumTests.class })
public class TestMasterStatusServlet {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestMasterStatusServlet.class);

    private HMaster master;

    private Configuration conf;

    private Admin admin;

    static final ServerName FAKE_HOST = ServerName.valueOf("fakehost", 12345, 1234567890);

    static final HTableDescriptor FAKE_TABLE = new HTableDescriptor(TableName.valueOf("mytable"));

    static final HRegionInfo FAKE_HRI = new HRegionInfo(TestMasterStatusServlet.FAKE_TABLE.getTableName(), Bytes.toBytes("a"), Bytes.toBytes("b"));

    @Test
    public void testStatusTemplateNoTables() throws IOException {
        new MasterStatusTmpl().render(new StringWriter(), master);
    }

    @Test
    public void testStatusTemplateMetaAvailable() throws IOException {
        setupMockTables();
        new MasterStatusTmpl().setMetaLocation(ServerName.valueOf("metaserver,123,12345")).render(new StringWriter(), master);
    }

    @Test
    public void testStatusTemplateWithServers() throws IOException {
        setupMockTables();
        List<ServerName> servers = Lists.newArrayList(ServerName.valueOf("rootserver,123,12345"), ServerName.valueOf("metaserver,123,12345"));
        Set<ServerName> deadServers = new java.util.HashSet(Lists.newArrayList(ServerName.valueOf("badserver,123,12345"), ServerName.valueOf("uglyserver,123,12345")));
        new MasterStatusTmpl().setMetaLocation(ServerName.valueOf("metaserver,123,12345")).setServers(servers).setDeadServers(deadServers).render(new StringWriter(), master);
    }
}

