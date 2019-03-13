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
package org.apache.hadoop.net;


import NetworkTopology.DEFAULT_RACK;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.hadoop.conf.Configuration;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Test the static mapping class.
 * Because the map is actually static, this map needs to be reset for every test
 */
public class TestStaticMapping extends Assert {
    private static final Logger LOG = LoggerFactory.getLogger(TestStaticMapping.class);

    @Test
    public void testStaticIsSingleSwitchOnNullScript() throws Throwable {
        StaticMapping mapping = newInstance(null);
        mapping.setConf(createConf(null));
        assertSingleSwitch(mapping);
    }

    @Test
    public void testStaticIsMultiSwitchOnScript() throws Throwable {
        StaticMapping mapping = newInstance("ls");
        assertMultiSwitch(mapping);
    }

    @Test
    public void testAddResolveNodes() throws Throwable {
        StaticMapping mapping = newInstance();
        StaticMapping.addNodeToRack("n1", "/r1");
        List<String> queryList = createQueryList();
        List<String> resolved = mapping.resolve(queryList);
        Assert.assertEquals(2, resolved.size());
        Assert.assertEquals("/r1", resolved.get(0));
        Assert.assertEquals(DEFAULT_RACK, resolved.get(1));
        // get the switch map and examine it
        Map<String, String> switchMap = mapping.getSwitchMap();
        String topology = dumpTopology();
        TestStaticMapping.LOG.info(topology);
        Assert.assertEquals(topology, 1, switchMap.size());
        Assert.assertEquals(topology, "/r1", switchMap.get("n1"));
    }

    /**
     * Verify that a configuration string builds a topology
     */
    @Test
    public void testReadNodesFromConfig() throws Throwable {
        StaticMapping mapping = newInstance();
        Configuration conf = new Configuration();
        conf.set(StaticMapping.KEY_HADOOP_CONFIGURED_NODE_MAPPING, "n1=/r1,n2=/r2");
        mapping.setConf(conf);
        // even though we have inserted elements into the list, because
        // it is driven by the script key in the configuration, it still
        // thinks that it is single rack
        assertSingleSwitch(mapping);
        List<String> l1 = new ArrayList<String>(3);
        l1.add("n1");
        l1.add("unknown");
        l1.add("n2");
        List<String> resolved = mapping.resolve(l1);
        Assert.assertEquals(3, resolved.size());
        Assert.assertEquals("/r1", resolved.get(0));
        Assert.assertEquals(DEFAULT_RACK, resolved.get(1));
        Assert.assertEquals("/r2", resolved.get(2));
        Map<String, String> switchMap = mapping.getSwitchMap();
        String topology = dumpTopology();
        TestStaticMapping.LOG.info(topology);
        Assert.assertEquals(topology, 2, switchMap.size());
        Assert.assertEquals(topology, "/r1", switchMap.get("n1"));
        Assert.assertNull(topology, switchMap.get("unknown"));
    }

    /**
     * Verify that if the inner mapping is single-switch, so is the cached one
     *
     * @throws Throwable
     * 		on any problem
     */
    @Test
    public void testCachingRelaysSingleSwitchQueries() throws Throwable {
        // create a single switch map
        StaticMapping staticMapping = newInstance(null);
        assertSingleSwitch(staticMapping);
        CachedDNSToSwitchMapping cachedMap = new CachedDNSToSwitchMapping(staticMapping);
        TestStaticMapping.LOG.info(((("Mapping: " + cachedMap) + "\n") + (cachedMap.dumpTopology())));
        assertSingleSwitch(cachedMap);
    }

    /**
     * Verify that if the inner mapping is multi-switch, so is the cached one
     *
     * @throws Throwable
     * 		on any problem
     */
    @Test
    public void testCachingRelaysMultiSwitchQueries() throws Throwable {
        StaticMapping staticMapping = newInstance("top");
        assertMultiSwitch(staticMapping);
        CachedDNSToSwitchMapping cachedMap = new CachedDNSToSwitchMapping(staticMapping);
        TestStaticMapping.LOG.info(((("Mapping: " + cachedMap) + "\n") + (cachedMap.dumpTopology())));
        assertMultiSwitch(cachedMap);
    }

    /**
     * This test verifies that resultion queries get relayed to the inner rack
     *
     * @throws Throwable
     * 		on any problem
     */
    @Test
    public void testCachingRelaysResolveQueries() throws Throwable {
        StaticMapping mapping = newInstance();
        mapping.setConf(createConf("top"));
        StaticMapping staticMapping = mapping;
        CachedDNSToSwitchMapping cachedMap = new CachedDNSToSwitchMapping(staticMapping);
        assertMapSize(cachedMap, 0);
        // add a node to the static map
        StaticMapping.addNodeToRack("n1", "/r1");
        // verify it is there
        assertMapSize(staticMapping, 1);
        // verify that the cache hasn't picked it up yet
        assertMapSize(cachedMap, 0);
        // now relay the query
        cachedMap.resolve(createQueryList());
        // and verify the cache is no longer empty
        assertMapSize(cachedMap, 2);
    }

    /**
     * This test verifies that resultion queries get relayed to the inner rack
     *
     * @throws Throwable
     * 		on any problem
     */
    @Test
    public void testCachingCachesNegativeEntries() throws Throwable {
        StaticMapping staticMapping = newInstance();
        CachedDNSToSwitchMapping cachedMap = new CachedDNSToSwitchMapping(staticMapping);
        assertMapSize(cachedMap, 0);
        assertMapSize(staticMapping, 0);
        List<String> resolved = cachedMap.resolve(createQueryList());
        // and verify the cache is no longer empty while the static map is
        assertMapSize(staticMapping, 0);
        assertMapSize(cachedMap, 2);
    }
}

