/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.flume.sink.hbase;


import HBaseSinkConfigurationConstants.ASYNC_ZK_BASEPATH_KEY;
import HBaseSinkConfigurationConstants.ASYNC_ZK_QUORUM_KEY;
import HBaseSinkConfigurationConstants.ZK_QUORUM;
import HBaseSinkConfigurationConstants.ZK_ZNODE_PARENT;
import org.apache.flume.Context;
import org.apache.flume.conf.Configurables;
import org.junit.Assert;
import org.junit.Test;

import static HBaseSinkConfigurationConstants.ASYNC_PREFIX;
import static HBaseSinkConfigurationConstants.ASYNC_ZK_BASEPATH_KEY;
import static HBaseSinkConfigurationConstants.ASYNC_ZK_QUORUM_KEY;


public class TestAsyncHBaseSinkConfiguration {
    private static final String tableName = "TestHbaseSink";

    private static final String columnFamily = "TestColumnFamily";

    private static Context ctx = new Context();

    // FLUME-3186 Make asyncHbaseClient configuration parameters available from flume config
    @Test
    public void testAsyncConfigBackwardCompatibility() throws Exception {
        // Old way: zookeeperQuorum
        String oldZkQuorumTestValue = "old_zookeeper_quorum_test_value";
        String oldZkZnodeParentValue = "old_zookeeper_znode_parent_test_value";
        TestAsyncHBaseSinkConfiguration.ctx.put(ZK_QUORUM, oldZkQuorumTestValue);
        TestAsyncHBaseSinkConfiguration.ctx.put(ZK_ZNODE_PARENT, oldZkZnodeParentValue);
        AsyncHBaseSink sink = new AsyncHBaseSink();
        Configurables.configure(sink, TestAsyncHBaseSinkConfiguration.ctx);
        Assert.assertEquals(oldZkQuorumTestValue, sink.asyncClientConfig.getString(ASYNC_ZK_QUORUM_KEY));
        Assert.assertEquals(oldZkZnodeParentValue, sink.asyncClientConfig.getString(ASYNC_ZK_BASEPATH_KEY));
    }

    @Test
    public void testAsyncConfigNewStyleOverwriteOldOne() throws Exception {
        // Old way: zookeeperQuorum
        String oldZkQuorumTestValue = "old_zookeeper_quorum_test_value";
        String oldZkZnodeParentValue = "old_zookeeper_znode_parent_test_value";
        TestAsyncHBaseSinkConfiguration.ctx.put(ZK_QUORUM, oldZkQuorumTestValue);
        TestAsyncHBaseSinkConfiguration.ctx.put(ZK_ZNODE_PARENT, oldZkZnodeParentValue);
        String newZkQuorumTestValue = "new_zookeeper_quorum_test_value";
        String newZkZnodeParentValue = "new_zookeeper_znode_parent_test_value";
        TestAsyncHBaseSinkConfiguration.ctx.put(((ASYNC_PREFIX) + (ASYNC_ZK_QUORUM_KEY)), newZkQuorumTestValue);
        TestAsyncHBaseSinkConfiguration.ctx.put(((ASYNC_PREFIX) + (ASYNC_ZK_BASEPATH_KEY)), newZkZnodeParentValue);
        AsyncHBaseSink sink = new AsyncHBaseSink();
        Configurables.configure(sink, TestAsyncHBaseSinkConfiguration.ctx);
        Assert.assertEquals(newZkQuorumTestValue, sink.asyncClientConfig.getString(ASYNC_ZK_QUORUM_KEY));
        Assert.assertEquals(newZkZnodeParentValue, sink.asyncClientConfig.getString(ASYNC_ZK_BASEPATH_KEY));
    }

    @Test
    public void testAsyncConfigAnyKeyCanBePassed() throws Exception {
        String valueOfANewProp = "vale of the new property";
        String keyOfANewProp = "some.key.to.be.passed";
        TestAsyncHBaseSinkConfiguration.ctx.put(((ASYNC_PREFIX) + keyOfANewProp), valueOfANewProp);
        AsyncHBaseSink sink = new AsyncHBaseSink();
        Configurables.configure(sink, TestAsyncHBaseSinkConfiguration.ctx);
        Assert.assertEquals(valueOfANewProp, sink.asyncClientConfig.getString(keyOfANewProp));
    }
}

