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
package org.apache.hadoop.hdfs.server.federation.metrics;


import java.io.IOException;
import java.util.Iterator;
import javax.management.MalformedObjectNameException;
import org.apache.hadoop.hdfs.server.federation.FederationTestUtils;
import org.apache.hadoop.hdfs.server.federation.store.records.MembershipState;
import org.apache.hadoop.hdfs.server.federation.store.records.MembershipStats;
import org.apache.hadoop.hdfs.server.federation.store.records.MountTable;
import org.apache.hadoop.hdfs.server.federation.store.records.RouterState;
import org.apache.hadoop.hdfs.server.federation.store.records.StateStoreVersion;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test the JMX interface for the {@link Router}.
 */
public class TestFederationMetrics extends TestMetricsBase {
    public static final String FEDERATION_BEAN = "Hadoop:service=Router,name=FederationState";

    public static final String STATE_STORE_BEAN = "Hadoop:service=Router,name=StateStore";

    public static final String RPC_BEAN = "Hadoop:service=Router,name=FederationRPC";

    @Test
    public void testClusterStatsJMX() throws IOException, MalformedObjectNameException {
        FederationMBean bean = FederationTestUtils.getBean(TestFederationMetrics.FEDERATION_BEAN, FederationMBean.class);
        validateClusterStatsBean(bean);
    }

    @Test
    public void testClusterStatsDataSource() throws IOException {
        FederationMetrics metrics = getRouter().getMetrics();
        validateClusterStatsBean(metrics);
    }

    @Test
    public void testMountTableStatsDataSource() throws IOException, JSONException {
        FederationMetrics metrics = getRouter().getMetrics();
        String jsonString = metrics.getMountTable();
        JSONArray jsonArray = new JSONArray(jsonString);
        Assert.assertEquals(jsonArray.length(), getMockMountTable().size());
        int match = 0;
        for (int i = 0; i < (jsonArray.length()); i++) {
            JSONObject json = jsonArray.getJSONObject(i);
            String src = json.getString("sourcePath");
            for (MountTable entry : getMockMountTable()) {
                if (entry.getSourcePath().equals(src)) {
                    Assert.assertEquals(entry.getDefaultLocation().getNameserviceId(), json.getString("nameserviceId"));
                    Assert.assertEquals(entry.getDefaultLocation().getDest(), json.getString("path"));
                    Assert.assertEquals(entry.getOwnerName(), json.getString("ownerName"));
                    Assert.assertEquals(entry.getGroupName(), json.getString("groupName"));
                    Assert.assertEquals(entry.getMode().toString(), json.getString("mode"));
                    Assert.assertEquals(entry.getQuota().toString(), json.getString("quota"));
                    assertNotNullAndNotEmpty(json.getString("dateCreated"));
                    assertNotNullAndNotEmpty(json.getString("dateModified"));
                    match++;
                }
            }
        }
        Assert.assertEquals(match, getMockMountTable().size());
    }

    @Test
    public void testNamenodeStatsDataSource() throws IOException, JSONException {
        FederationMetrics metrics = getRouter().getMetrics();
        String jsonString = metrics.getNamenodes();
        JSONObject jsonObject = new JSONObject(jsonString);
        Iterator<?> keys = jsonObject.keys();
        int nnsFound = 0;
        while (keys.hasNext()) {
            // Validate each entry against our mocks
            JSONObject json = jsonObject.getJSONObject(((String) (keys.next())));
            String nameserviceId = json.getString("nameserviceId");
            String namenodeId = json.getString("namenodeId");
            MembershipState mockEntry = this.findMockNamenode(nameserviceId, namenodeId);
            Assert.assertNotNull(mockEntry);
            Assert.assertEquals(json.getString("state"), mockEntry.getState().toString());
            MembershipStats stats = mockEntry.getStats();
            Assert.assertEquals(json.getLong("numOfActiveDatanodes"), stats.getNumOfActiveDatanodes());
            Assert.assertEquals(json.getLong("numOfDeadDatanodes"), stats.getNumOfDeadDatanodes());
            Assert.assertEquals(json.getLong("numOfDecommissioningDatanodes"), stats.getNumOfDecommissioningDatanodes());
            Assert.assertEquals(json.getLong("numOfDecomActiveDatanodes"), stats.getNumOfDecomActiveDatanodes());
            Assert.assertEquals(json.getLong("numOfDecomDeadDatanodes"), stats.getNumOfDecomDeadDatanodes());
            Assert.assertEquals(json.getLong("numOfBlocks"), stats.getNumOfBlocks());
            Assert.assertEquals(json.getString("rpcAddress"), mockEntry.getRpcAddress());
            Assert.assertEquals(json.getString("webAddress"), mockEntry.getWebAddress());
            nnsFound++;
        } 
        // Validate all memberships are present
        Assert.assertEquals(((getActiveMemberships().size()) + (getStandbyMemberships().size())), nnsFound);
    }

    @Test
    public void testNameserviceStatsDataSource() throws IOException, JSONException {
        FederationMetrics metrics = getRouter().getMetrics();
        String jsonString = metrics.getNameservices();
        JSONObject jsonObject = new JSONObject(jsonString);
        Iterator<?> keys = jsonObject.keys();
        int nameservicesFound = 0;
        while (keys.hasNext()) {
            JSONObject json = jsonObject.getJSONObject(((String) (keys.next())));
            String nameserviceId = json.getString("nameserviceId");
            String namenodeId = json.getString("namenodeId");
            MembershipState mockEntry = this.findMockNamenode(nameserviceId, namenodeId);
            Assert.assertNotNull(mockEntry);
            // NS should report the active NN
            Assert.assertEquals(mockEntry.getState().toString(), json.getString("state"));
            Assert.assertEquals("ACTIVE", json.getString("state"));
            // Stats in the NS should reflect the stats for the most active NN
            MembershipStats stats = mockEntry.getStats();
            Assert.assertEquals(stats.getNumOfFiles(), json.getLong("numOfFiles"));
            Assert.assertEquals(stats.getTotalSpace(), json.getLong("totalSpace"));
            Assert.assertEquals(stats.getAvailableSpace(), json.getLong("availableSpace"));
            Assert.assertEquals(stats.getNumOfBlocksMissing(), json.getLong("numOfBlocksMissing"));
            Assert.assertEquals(stats.getNumOfActiveDatanodes(), json.getLong("numOfActiveDatanodes"));
            Assert.assertEquals(stats.getNumOfDeadDatanodes(), json.getLong("numOfDeadDatanodes"));
            Assert.assertEquals(stats.getNumOfDecommissioningDatanodes(), json.getLong("numOfDecommissioningDatanodes"));
            Assert.assertEquals(stats.getNumOfDecomActiveDatanodes(), json.getLong("numOfDecomActiveDatanodes"));
            Assert.assertEquals(stats.getNumOfDecomDeadDatanodes(), json.getLong("numOfDecomDeadDatanodes"));
            Assert.assertEquals(stats.getProvidedSpace(), json.getLong("providedSpace"));
            nameservicesFound++;
        } 
        Assert.assertEquals(getNameservices().size(), nameservicesFound);
    }

    @Test
    public void testRouterStatsDataSource() throws IOException, JSONException {
        FederationMetrics metrics = getRouter().getMetrics();
        String jsonString = metrics.getRouters();
        JSONObject jsonObject = new JSONObject(jsonString);
        Iterator<?> keys = jsonObject.keys();
        int routersFound = 0;
        while (keys.hasNext()) {
            JSONObject json = jsonObject.getJSONObject(((String) (keys.next())));
            String address = json.getString("address");
            assertNotNullAndNotEmpty(address);
            RouterState router = findMockRouter(address);
            Assert.assertNotNull(router);
            Assert.assertEquals(router.getStatus().toString(), json.getString("status"));
            Assert.assertEquals(router.getCompileInfo(), json.getString("compileInfo"));
            Assert.assertEquals(router.getVersion(), json.getString("version"));
            Assert.assertEquals(router.getDateStarted(), json.getLong("dateStarted"));
            Assert.assertEquals(router.getDateCreated(), json.getLong("dateCreated"));
            Assert.assertEquals(router.getDateModified(), json.getLong("dateModified"));
            StateStoreVersion version = router.getStateStoreVersion();
            Assert.assertEquals(FederationMetrics.getDateString(version.getMembershipVersion()), json.get("lastMembershipUpdate"));
            Assert.assertEquals(FederationMetrics.getDateString(version.getMountTableVersion()), json.get("lastMountTableUpdate"));
            Assert.assertEquals(version.getMembershipVersion(), json.get("membershipVersion"));
            Assert.assertEquals(version.getMountTableVersion(), json.get("mountTableVersion"));
            routersFound++;
        } 
        Assert.assertEquals(getMockRouters().size(), routersFound);
    }
}

