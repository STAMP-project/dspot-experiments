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
package org.apache.hadoop.hbase.zookeeper;


import HConstants.ZOOKEEPER_CLIENT_PORT;
import HConstants.ZOOKEEPER_DATA_DIR;
import HConstants.ZOOKEEPER_QUORUM;
import java.util.Properties;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HBaseZKTestingUtility;
import org.apache.hadoop.hbase.testclassification.MediumTests;
import org.apache.hadoop.hbase.testclassification.ZKTests;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;


/**
 * Test for HQuorumPeer.
 */
@Category({ ZKTests.class, MediumTests.class })
public class TestHQuorumPeer {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestHQuorumPeer.class);

    private static final HBaseZKTestingUtility TEST_UTIL = new HBaseZKTestingUtility();

    private static int PORT_NO = 21818;

    private Path dataDir;

    @Test
    public void testMakeZKProps() {
        Configuration conf = new Configuration(getConfiguration());
        conf.set(ZOOKEEPER_DATA_DIR, this.dataDir.toString());
        Properties properties = ZKConfig.makeZKProps(conf);
        Assert.assertEquals(dataDir.toString(), ((String) (properties.get("dataDir"))));
        Assert.assertEquals(Integer.valueOf(TestHQuorumPeer.PORT_NO), Integer.valueOf(properties.getProperty("clientPort")));
        Assert.assertEquals("localhost:2888:3888", properties.get("server.0"));
        Assert.assertEquals(null, properties.get("server.1"));
        String oldValue = conf.get(ZOOKEEPER_QUORUM);
        conf.set(ZOOKEEPER_QUORUM, "a.foo.bar,b.foo.bar,c.foo.bar");
        properties = ZKConfig.makeZKProps(conf);
        Assert.assertEquals(dataDir.toString(), properties.get("dataDir"));
        Assert.assertEquals(Integer.valueOf(TestHQuorumPeer.PORT_NO), Integer.valueOf(properties.getProperty("clientPort")));
        Assert.assertEquals("a.foo.bar:2888:3888", properties.get("server.0"));
        Assert.assertEquals("b.foo.bar:2888:3888", properties.get("server.1"));
        Assert.assertEquals("c.foo.bar:2888:3888", properties.get("server.2"));
        Assert.assertEquals(null, properties.get("server.3"));
        conf.set(ZOOKEEPER_QUORUM, oldValue);
    }

    @Test
    public void testShouldAssignDefaultZookeeperClientPort() {
        Configuration config = HBaseConfiguration.create();
        config.clear();
        Properties p = ZKConfig.makeZKProps(config);
        Assert.assertNotNull(p);
        Assert.assertEquals(2181, p.get("clientPort"));
    }

    @Test
    public void testGetZKQuorumServersString() {
        Configuration config = new Configuration(getConfiguration());
        config.setInt(ZOOKEEPER_CLIENT_PORT, 8888);
        config.set(ZOOKEEPER_QUORUM, "foo:1234,bar:5678,baz,qux:9012");
        String s = ZKConfig.getZKQuorumServersString(config);
        Assert.assertEquals("foo:1234,bar:5678,baz:8888,qux:9012", s);
    }
}

