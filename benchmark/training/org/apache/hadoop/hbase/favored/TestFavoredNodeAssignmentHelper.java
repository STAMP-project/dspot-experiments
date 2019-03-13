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
package org.apache.hadoop.hbase.favored;


import HConstants.EMPTY_END_ROW;
import HConstants.EMPTY_START_ROW;
import ServerName.NON_STARTCODE;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.ServerName;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.RegionInfo;
import org.apache.hadoop.hbase.client.RegionInfoBuilder;
import org.apache.hadoop.hbase.master.RackManager;
import org.apache.hadoop.hbase.testclassification.MasterTests;
import org.apache.hadoop.hbase.testclassification.SmallTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.hbase.util.Triple;
import org.apache.hbase.thirdparty.com.google.common.collect.Lists;
import org.apache.hbase.thirdparty.com.google.common.collect.Sets;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.TestName;
import org.mockito.Mockito;


@Category({ MasterTests.class, SmallTests.class })
public class TestFavoredNodeAssignmentHelper {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestFavoredNodeAssignmentHelper.class);

    private static List<ServerName> servers = new ArrayList<>();

    private static Map<String, List<ServerName>> rackToServers = new HashMap<>();

    private static RackManager rackManager = Mockito.mock(RackManager.class);

    // Some tests have randomness, so we run them multiple times
    private static final int MAX_ATTEMPTS = 100;

    @Rule
    public TestName name = new TestName();

    @Test
    public void testSmallCluster() {
        // Test the case where we cannot assign favored nodes (because the number
        // of nodes in the cluster is too less)
        Map<String, Integer> rackToServerCount = new HashMap<>();
        rackToServerCount.put("rack1", 2);
        List<ServerName> servers = TestFavoredNodeAssignmentHelper.getServersFromRack(rackToServerCount);
        FavoredNodeAssignmentHelper helper = new FavoredNodeAssignmentHelper(servers, new Configuration());
        helper.initialize();
        Assert.assertFalse(helper.canPlaceFavoredNodes());
    }

    @Test
    public void testPlacePrimaryRSAsRoundRobin() {
        // Test the regular case where there are many servers in different racks
        // Test once for few regions and once for many regions
        primaryRSPlacement(6, null, 10, 10, 10);
        // now create lots of regions and try to place them on the limited number of machines
        primaryRSPlacement(600, null, 10, 10, 10);
    }

    @Test
    public void testRoundRobinAssignmentsWithUnevenSizedRacks() {
        // In the case of uneven racks, the regions should be distributed
        // proportionately to the rack sizes
        primaryRSPlacement(6, null, 10, 10, 10);
        primaryRSPlacement(600, null, 10, 10, 5);
        primaryRSPlacement(600, null, 10, 5, 10);
        primaryRSPlacement(600, null, 5, 10, 10);
        primaryRSPlacement(500, null, 10, 10, 5);
        primaryRSPlacement(500, null, 10, 5, 10);
        primaryRSPlacement(500, null, 5, 10, 10);
        primaryRSPlacement(500, null, 9, 7, 8);
        primaryRSPlacement(500, null, 8, 7, 9);
        primaryRSPlacement(500, null, 7, 9, 8);
        primaryRSPlacement(459, null, 7, 9, 8);
    }

    @Test
    public void testSecondaryAndTertiaryPlacementWithSingleRack() {
        // Test the case where there is a single rack and we need to choose
        // Primary/Secondary/Tertiary from a single rack.
        Map<String, Integer> rackToServerCount = new HashMap<>();
        rackToServerCount.put("rack1", 10);
        // have lots of regions to test with
        Triple<Map<RegionInfo, ServerName>, FavoredNodeAssignmentHelper, List<RegionInfo>> primaryRSMapAndHelper = secondaryAndTertiaryRSPlacementHelper(60000, rackToServerCount);
        FavoredNodeAssignmentHelper helper = primaryRSMapAndHelper.getSecond();
        Map<RegionInfo, ServerName> primaryRSMap = primaryRSMapAndHelper.getFirst();
        List<RegionInfo> regions = primaryRSMapAndHelper.getThird();
        Map<RegionInfo, ServerName[]> secondaryAndTertiaryMap = helper.placeSecondaryAndTertiaryRS(primaryRSMap);
        // although we created lots of regions we should have no overlap on the
        // primary/secondary/tertiary for any given region
        for (RegionInfo region : regions) {
            ServerName[] secondaryAndTertiaryServers = secondaryAndTertiaryMap.get(region);
            Assert.assertNotNull(secondaryAndTertiaryServers);
            Assert.assertTrue(primaryRSMap.containsKey(region));
            Assert.assertTrue((!(secondaryAndTertiaryServers[0].equals(primaryRSMap.get(region)))));
            Assert.assertTrue((!(secondaryAndTertiaryServers[1].equals(primaryRSMap.get(region)))));
            Assert.assertTrue((!(secondaryAndTertiaryServers[0].equals(secondaryAndTertiaryServers[1]))));
        }
    }

    @Test
    public void testSecondaryAndTertiaryPlacementWithSingleServer() {
        // Test the case where we have a single node in the cluster. In this case
        // the primary can be assigned but the secondary/tertiary would be null
        Map<String, Integer> rackToServerCount = new HashMap<>();
        rackToServerCount.put("rack1", 1);
        Triple<Map<RegionInfo, ServerName>, FavoredNodeAssignmentHelper, List<RegionInfo>> primaryRSMapAndHelper = secondaryAndTertiaryRSPlacementHelper(1, rackToServerCount);
        FavoredNodeAssignmentHelper helper = primaryRSMapAndHelper.getSecond();
        Map<RegionInfo, ServerName> primaryRSMap = primaryRSMapAndHelper.getFirst();
        List<RegionInfo> regions = primaryRSMapAndHelper.getThird();
        Map<RegionInfo, ServerName[]> secondaryAndTertiaryMap = helper.placeSecondaryAndTertiaryRS(primaryRSMap);
        // no secondary/tertiary placement in case of a single RegionServer
        Assert.assertTrue(((secondaryAndTertiaryMap.get(regions.get(0))) == null));
    }

    @Test
    public void testSecondaryAndTertiaryPlacementWithMultipleRacks() {
        // Test the case where we have multiple racks and the region servers
        // belong to multiple racks
        Map<String, Integer> rackToServerCount = new HashMap<>();
        rackToServerCount.put("rack1", 10);
        rackToServerCount.put("rack2", 10);
        Triple<Map<RegionInfo, ServerName>, FavoredNodeAssignmentHelper, List<RegionInfo>> primaryRSMapAndHelper = secondaryAndTertiaryRSPlacementHelper(60000, rackToServerCount);
        FavoredNodeAssignmentHelper helper = primaryRSMapAndHelper.getSecond();
        Map<RegionInfo, ServerName> primaryRSMap = primaryRSMapAndHelper.getFirst();
        Assert.assertTrue(((primaryRSMap.size()) == 60000));
        Map<RegionInfo, ServerName[]> secondaryAndTertiaryMap = helper.placeSecondaryAndTertiaryRS(primaryRSMap);
        Assert.assertTrue(((secondaryAndTertiaryMap.size()) == 60000));
        // for every region, the primary should be on one rack and the secondary/tertiary
        // on another (we create a lot of regions just to increase probability of failure)
        for (Map.Entry<RegionInfo, ServerName[]> entry : secondaryAndTertiaryMap.entrySet()) {
            ServerName[] allServersForRegion = entry.getValue();
            String primaryRSRack = TestFavoredNodeAssignmentHelper.rackManager.getRack(primaryRSMap.get(entry.getKey()));
            String secondaryRSRack = TestFavoredNodeAssignmentHelper.rackManager.getRack(allServersForRegion[0]);
            String tertiaryRSRack = TestFavoredNodeAssignmentHelper.rackManager.getRack(allServersForRegion[1]);
            Set<String> racks = Sets.newHashSet(primaryRSRack);
            racks.add(secondaryRSRack);
            racks.add(tertiaryRSRack);
            Assert.assertTrue(((racks.size()) >= 2));
        }
    }

    @Test
    public void testSecondaryAndTertiaryPlacementWithLessThanTwoServersInRacks() {
        // Test the case where we have two racks but with less than two servers in each
        // We will not have enough machines to select secondary/tertiary
        Map<String, Integer> rackToServerCount = new HashMap<>();
        rackToServerCount.put("rack1", 1);
        rackToServerCount.put("rack2", 1);
        Triple<Map<RegionInfo, ServerName>, FavoredNodeAssignmentHelper, List<RegionInfo>> primaryRSMapAndHelper = secondaryAndTertiaryRSPlacementHelper(6, rackToServerCount);
        FavoredNodeAssignmentHelper helper = primaryRSMapAndHelper.getSecond();
        Map<RegionInfo, ServerName> primaryRSMap = primaryRSMapAndHelper.getFirst();
        List<RegionInfo> regions = primaryRSMapAndHelper.getThird();
        Assert.assertTrue(((primaryRSMap.size()) == 6));
        Map<RegionInfo, ServerName[]> secondaryAndTertiaryMap = helper.placeSecondaryAndTertiaryRS(primaryRSMap);
        for (RegionInfo region : regions) {
            // not enough secondary/tertiary room to place the regions
            Assert.assertTrue(((secondaryAndTertiaryMap.get(region)) == null));
        }
    }

    @Test
    public void testSecondaryAndTertiaryPlacementWithMoreThanOneServerInPrimaryRack() {
        // Test the case where there is only one server in one rack and another rack
        // has more servers. We try to choose secondary/tertiary on different
        // racks than what the primary is on. But if the other rack doesn't have
        // enough nodes to have both secondary/tertiary RSs, the tertiary is placed
        // on the same rack as the primary server is on
        Map<String, Integer> rackToServerCount = new HashMap<>();
        rackToServerCount.put("rack1", 2);
        rackToServerCount.put("rack2", 1);
        Triple<Map<RegionInfo, ServerName>, FavoredNodeAssignmentHelper, List<RegionInfo>> primaryRSMapAndHelper = secondaryAndTertiaryRSPlacementHelper(6, rackToServerCount);
        FavoredNodeAssignmentHelper helper = primaryRSMapAndHelper.getSecond();
        Map<RegionInfo, ServerName> primaryRSMap = primaryRSMapAndHelper.getFirst();
        List<RegionInfo> regions = primaryRSMapAndHelper.getThird();
        Assert.assertTrue(((primaryRSMap.size()) == 6));
        Map<RegionInfo, ServerName[]> secondaryAndTertiaryMap = helper.placeSecondaryAndTertiaryRS(primaryRSMap);
        Assert.assertTrue(((secondaryAndTertiaryMap.size()) == (regions.size())));
        for (RegionInfo region : regions) {
            ServerName s = primaryRSMap.get(region);
            ServerName secondaryRS = secondaryAndTertiaryMap.get(region)[0];
            ServerName tertiaryRS = secondaryAndTertiaryMap.get(region)[1];
            Set<String> racks = Sets.newHashSet(TestFavoredNodeAssignmentHelper.rackManager.getRack(s));
            racks.add(TestFavoredNodeAssignmentHelper.rackManager.getRack(secondaryRS));
            racks.add(TestFavoredNodeAssignmentHelper.rackManager.getRack(tertiaryRS));
            Assert.assertTrue(((racks.size()) >= 2));
        }
    }

    @Test
    public void testConstrainedPlacement() throws Exception {
        List<ServerName> servers = Lists.newArrayList();
        servers.add(ServerName.valueOf((("foo" + 1) + ":1234"), (-1)));
        servers.add(ServerName.valueOf((("foo" + 2) + ":1234"), (-1)));
        servers.add(ServerName.valueOf((("foo" + 15) + ":1234"), (-1)));
        FavoredNodeAssignmentHelper helper = new FavoredNodeAssignmentHelper(servers, TestFavoredNodeAssignmentHelper.rackManager);
        helper.initialize();
        Assert.assertTrue(helper.canPlaceFavoredNodes());
        List<RegionInfo> regions = new ArrayList<>(20);
        for (int i = 0; i < 20; i++) {
            regions.add(RegionInfoBuilder.newBuilder(TableName.valueOf(name.getMethodName())).setStartKey(Bytes.toBytes(i)).setEndKey(Bytes.toBytes((i + 1))).build());
        }
        Map<ServerName, List<RegionInfo>> assignmentMap = new HashMap<ServerName, List<RegionInfo>>();
        Map<RegionInfo, ServerName> primaryRSMap = new HashMap<RegionInfo, ServerName>();
        helper.placePrimaryRSAsRoundRobin(assignmentMap, primaryRSMap, regions);
        Assert.assertTrue(((primaryRSMap.size()) == (regions.size())));
        Map<RegionInfo, ServerName[]> secondaryAndTertiary = helper.placeSecondaryAndTertiaryRS(primaryRSMap);
        Assert.assertEquals(regions.size(), secondaryAndTertiary.size());
    }

    @Test
    public void testGetOneRandomRack() throws IOException {
        Map<String, Integer> rackToServerCount = new HashMap<>();
        Set<String> rackList = Sets.newHashSet("rack1", "rack2", "rack3");
        for (String rack : rackList) {
            rackToServerCount.put(rack, 2);
        }
        List<ServerName> servers = TestFavoredNodeAssignmentHelper.getServersFromRack(rackToServerCount);
        FavoredNodeAssignmentHelper helper = new FavoredNodeAssignmentHelper(servers, TestFavoredNodeAssignmentHelper.rackManager);
        helper.initialize();
        Assert.assertTrue(helper.canPlaceFavoredNodes());
        // Check we don't get a bad rack on any number of attempts
        for (int attempts = 0; attempts < (TestFavoredNodeAssignmentHelper.MAX_ATTEMPTS); attempts++) {
            Assert.assertTrue(rackList.contains(helper.getOneRandomRack(Sets.newHashSet())));
        }
        // Check skipRack multiple times when an invalid rack is specified
        Set<String> skipRacks = Sets.newHashSet("rack");
        for (int attempts = 0; attempts < (TestFavoredNodeAssignmentHelper.MAX_ATTEMPTS); attempts++) {
            Assert.assertTrue(rackList.contains(helper.getOneRandomRack(skipRacks)));
        }
        // Check skipRack multiple times when an valid rack is specified
        skipRacks = Sets.newHashSet("rack1");
        Set<String> validRacks = Sets.newHashSet("rack2", "rack3");
        for (int attempts = 0; attempts < (TestFavoredNodeAssignmentHelper.MAX_ATTEMPTS); attempts++) {
            Assert.assertTrue(validRacks.contains(helper.getOneRandomRack(skipRacks)));
        }
    }

    @Test
    public void testGetRandomServerSingleRack() throws IOException {
        Map<String, Integer> rackToServerCount = new HashMap<>();
        final String rack = "rack1";
        rackToServerCount.put(rack, 4);
        List<ServerName> servers = TestFavoredNodeAssignmentHelper.getServersFromRack(rackToServerCount);
        FavoredNodeAssignmentHelper helper = new FavoredNodeAssignmentHelper(servers, TestFavoredNodeAssignmentHelper.rackManager);
        helper.initialize();
        Assert.assertTrue(helper.canPlaceFavoredNodes());
        // Check we don't get a bad node on any number of attempts
        for (int attempts = 0; attempts < (TestFavoredNodeAssignmentHelper.MAX_ATTEMPTS); attempts++) {
            ServerName sn = helper.getOneRandomServer(rack, Sets.newHashSet());
            Assert.assertTrue(((("Server:" + sn) + " does not belong to list: ") + servers), servers.contains(sn));
        }
        // Check skipServers multiple times when an invalid server is specified
        Set<ServerName> skipServers = Sets.newHashSet(ServerName.valueOf("invalidnode:1234", NON_STARTCODE));
        for (int attempts = 0; attempts < (TestFavoredNodeAssignmentHelper.MAX_ATTEMPTS); attempts++) {
            ServerName sn = helper.getOneRandomServer(rack, skipServers);
            Assert.assertTrue(((("Server:" + sn) + " does not belong to list: ") + servers), servers.contains(sn));
        }
        // Check skipRack multiple times when an valid servers are specified
        ServerName skipSN = ServerName.valueOf("foo1:1234", NON_STARTCODE);
        skipServers = Sets.newHashSet(skipSN);
        for (int attempts = 0; attempts < (TestFavoredNodeAssignmentHelper.MAX_ATTEMPTS); attempts++) {
            ServerName sn = helper.getOneRandomServer(rack, skipServers);
            Assert.assertNotEquals("Skip server should not be selected ", skipSN.getHostAndPort(), sn.getHostAndPort());
            Assert.assertTrue(((("Server:" + sn) + " does not belong to list: ") + servers), servers.contains(sn));
        }
    }

    @Test
    public void testGetRandomServerMultiRack() throws IOException {
        Map<String, Integer> rackToServerCount = new HashMap<>();
        Set<String> rackList = Sets.newHashSet("rack1", "rack2", "rack3");
        for (String rack : rackList) {
            rackToServerCount.put(rack, 4);
        }
        List<ServerName> servers = TestFavoredNodeAssignmentHelper.getServersFromRack(rackToServerCount);
        FavoredNodeAssignmentHelper helper = new FavoredNodeAssignmentHelper(servers, TestFavoredNodeAssignmentHelper.rackManager);
        helper.initialize();
        Assert.assertTrue(helper.canPlaceFavoredNodes());
        // Check we don't get a bad node on any number of attempts
        for (int attempts = 0; attempts < (TestFavoredNodeAssignmentHelper.MAX_ATTEMPTS); attempts++) {
            for (String rack : rackList) {
                ServerName sn = helper.getOneRandomServer(rack, Sets.newHashSet());
                Assert.assertTrue(((("Server:" + sn) + " does not belong to rack servers: ") + (TestFavoredNodeAssignmentHelper.rackToServers.get(rack))), TestFavoredNodeAssignmentHelper.rackToServers.get(rack).contains(sn));
            }
        }
        // Check skipServers multiple times when an invalid server is specified
        Set<ServerName> skipServers = Sets.newHashSet(ServerName.valueOf("invalidnode:1234", NON_STARTCODE));
        for (int attempts = 0; attempts < (TestFavoredNodeAssignmentHelper.MAX_ATTEMPTS); attempts++) {
            for (String rack : rackList) {
                ServerName sn = helper.getOneRandomServer(rack, skipServers);
                Assert.assertTrue(((("Server:" + sn) + " does not belong to rack servers: ") + (TestFavoredNodeAssignmentHelper.rackToServers.get(rack))), TestFavoredNodeAssignmentHelper.rackToServers.get(rack).contains(sn));
            }
        }
        // Check skipRack multiple times when an valid servers are specified
        ServerName skipSN1 = ServerName.valueOf("foo1:1234", NON_STARTCODE);
        ServerName skipSN2 = ServerName.valueOf("foo10:1234", NON_STARTCODE);
        ServerName skipSN3 = ServerName.valueOf("foo20:1234", NON_STARTCODE);
        skipServers = Sets.newHashSet(skipSN1, skipSN2, skipSN3);
        for (int attempts = 0; attempts < (TestFavoredNodeAssignmentHelper.MAX_ATTEMPTS); attempts++) {
            for (String rack : rackList) {
                ServerName sn = helper.getOneRandomServer(rack, skipServers);
                Assert.assertFalse("Skip server should not be selected ", skipServers.contains(sn));
                Assert.assertTrue(((("Server:" + sn) + " does not belong to rack servers: ") + (TestFavoredNodeAssignmentHelper.rackToServers.get(rack))), TestFavoredNodeAssignmentHelper.rackToServers.get(rack).contains(sn));
            }
        }
    }

    @Test
    public void testGetFavoredNodes() throws IOException {
        Map<String, Integer> rackToServerCount = new HashMap<>();
        Set<String> rackList = Sets.newHashSet("rack1", "rack2", "rack3");
        for (String rack : rackList) {
            rackToServerCount.put(rack, 4);
        }
        List<ServerName> servers = TestFavoredNodeAssignmentHelper.getServersFromRack(rackToServerCount);
        FavoredNodeAssignmentHelper helper = new FavoredNodeAssignmentHelper(servers, TestFavoredNodeAssignmentHelper.rackManager);
        helper.initialize();
        Assert.assertTrue(helper.canPlaceFavoredNodes());
        RegionInfo region = RegionInfoBuilder.newBuilder(TableName.valueOf(name.getMethodName())).setStartKey(EMPTY_START_ROW).setEndKey(EMPTY_END_ROW).build();
        for (int maxattempts = 0; maxattempts < (TestFavoredNodeAssignmentHelper.MAX_ATTEMPTS); maxattempts++) {
            List<ServerName> fn = helper.generateFavoredNodes(region);
            checkDuplicateFN(fn);
            checkFNRacks(fn);
        }
    }

    @Test
    public void testGenMissingFavoredNodeOneRack() throws IOException {
        Map<String, Integer> rackToServerCount = new HashMap<>();
        final String rack = "rack1";
        rackToServerCount.put(rack, 6);
        List<ServerName> servers = TestFavoredNodeAssignmentHelper.getServersFromRack(rackToServerCount);
        FavoredNodeAssignmentHelper helper = new FavoredNodeAssignmentHelper(servers, TestFavoredNodeAssignmentHelper.rackManager);
        helper.initialize();
        Assert.assertTrue(helper.canPlaceFavoredNodes());
        ServerName snRack1SN1 = ServerName.valueOf("foo1:1234", NON_STARTCODE);
        ServerName snRack1SN2 = ServerName.valueOf("foo2:1234", NON_STARTCODE);
        ServerName snRack1SN3 = ServerName.valueOf("foo3:1234", NON_STARTCODE);
        List<ServerName> fn = Lists.newArrayList(snRack1SN1, snRack1SN2);
        for (int attempts = 0; attempts < (TestFavoredNodeAssignmentHelper.MAX_ATTEMPTS); attempts++) {
            checkDuplicateFN(fn, helper.generateMissingFavoredNode(fn));
        }
        fn = Lists.newArrayList(snRack1SN1, snRack1SN2);
        List<ServerName> skipServers = Lists.newArrayList(snRack1SN3);
        for (int attempts = 0; attempts < (TestFavoredNodeAssignmentHelper.MAX_ATTEMPTS); attempts++) {
            ServerName genSN = helper.generateMissingFavoredNode(fn, skipServers);
            checkDuplicateFN(fn, genSN);
            Assert.assertNotEquals("Generated FN should not match excluded one", snRack1SN3, genSN);
        }
    }

    @Test
    public void testGenMissingFavoredNodeMultiRack() throws IOException {
        ServerName snRack1SN1 = ServerName.valueOf("foo1:1234", NON_STARTCODE);
        ServerName snRack1SN2 = ServerName.valueOf("foo2:1234", NON_STARTCODE);
        ServerName snRack2SN1 = ServerName.valueOf("foo10:1234", NON_STARTCODE);
        ServerName snRack2SN2 = ServerName.valueOf("foo11:1234", NON_STARTCODE);
        Map<String, Integer> rackToServerCount = new HashMap<>();
        Set<String> rackList = Sets.newHashSet("rack1", "rack2");
        for (String rack : rackList) {
            rackToServerCount.put(rack, 4);
        }
        List<ServerName> servers = TestFavoredNodeAssignmentHelper.getServersFromRack(rackToServerCount);
        FavoredNodeAssignmentHelper helper = new FavoredNodeAssignmentHelper(servers, TestFavoredNodeAssignmentHelper.rackManager);
        helper.initialize();
        Assert.assertTrue(helper.canPlaceFavoredNodes());
        List<ServerName> fn = Lists.newArrayList(snRack1SN1, snRack1SN2);
        for (int attempts = 0; attempts < (TestFavoredNodeAssignmentHelper.MAX_ATTEMPTS); attempts++) {
            ServerName genSN = helper.generateMissingFavoredNode(fn);
            checkDuplicateFN(fn, genSN);
            checkFNRacks(fn, genSN);
        }
        fn = Lists.newArrayList(snRack1SN1, snRack2SN1);
        for (int attempts = 0; attempts < (TestFavoredNodeAssignmentHelper.MAX_ATTEMPTS); attempts++) {
            ServerName genSN = helper.generateMissingFavoredNode(fn);
            checkDuplicateFN(fn, genSN);
            checkFNRacks(fn, genSN);
        }
        fn = Lists.newArrayList(snRack1SN1, snRack2SN1);
        List<ServerName> skipServers = Lists.newArrayList(snRack2SN2);
        for (int attempts = 0; attempts < (TestFavoredNodeAssignmentHelper.MAX_ATTEMPTS); attempts++) {
            ServerName genSN = helper.generateMissingFavoredNode(fn, skipServers);
            checkDuplicateFN(fn, genSN);
            checkFNRacks(fn, genSN);
            Assert.assertNotEquals("Generated FN should not match excluded one", snRack2SN2, genSN);
        }
    }
}

