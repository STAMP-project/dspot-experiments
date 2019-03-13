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
package org.apache.ambari.server.state.alerts;


import AlertDefinitionHash.NULL_MD5_HASH;
import Scope.HOST;
import SourceType.AGGREGATE;
import SourceType.PORT;
import category.AlertTest;
import com.google.inject.Binder;
import com.google.inject.Injector;
import com.google.inject.Module;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import junit.framework.TestCase;
import org.apache.ambari.server.orm.dao.AlertDefinitionDAO;
import org.apache.ambari.server.orm.entities.AlertDefinitionEntity;
import org.apache.ambari.server.state.Cluster;
import org.apache.ambari.server.state.Clusters;
import org.apache.ambari.server.state.ConfigHelper;
import org.apache.ambari.server.state.alert.AlertDefinition;
import org.apache.ambari.server.state.alert.AlertDefinitionHash;
import org.apache.commons.codec.binary.Hex;
import org.easymock.EasyMock;
import org.junit.Test;
import org.junit.experimental.categories.Category;


/**
 * Tests for {@link AlertDefinitionHash}.
 */
@Category({ AlertTest.class })
public class AlertDefinitionHashTest extends TestCase {
    private AlertDefinitionHash m_hash;

    private Clusters m_mockClusters;

    private Cluster m_mockCluster;

    private AlertDefinitionDAO m_mockDao;

    private Injector m_injector;

    private static final String CLUSTERNAME = "cluster1";

    private static final String HOSTNAME = "c6401.ambari.apache.org";

    private List<AlertDefinitionEntity> m_agentDefinitions;

    private AlertDefinitionEntity m_hdfsService;

    AlertDefinitionEntity m_hdfsHost;

    private ConfigHelper m_configHelper;

    /**
     * Test method for {@link org.apache.ambari.server.state.alert.AlertDefinitionHash#getHash(java.lang.String, java.lang.String)}.
     */
    @Test
    public void testGetHash() {
        String hash = m_hash.getHash(AlertDefinitionHashTest.CLUSTERNAME, AlertDefinitionHashTest.HOSTNAME);
        TestCase.assertNotNull(hash);
        TestCase.assertNotSame(NULL_MD5_HASH, hash);
        TestCase.assertEquals(hash, m_hash.getHash(AlertDefinitionHashTest.CLUSTERNAME, AlertDefinitionHashTest.HOSTNAME));
    }

    /**
     * Test method for {@link org.apache.ambari.server.state.alert.AlertDefinitionHash#getAlertDefinitions(java.lang.String, java.lang.String)}.
     */
    @Test
    public void testGetAlertDefinitions() {
        List<AlertDefinition> definitions = m_hash.getAlertDefinitions(AlertDefinitionHashTest.CLUSTERNAME, AlertDefinitionHashTest.HOSTNAME);
        TestCase.assertEquals(3, definitions.size());
    }

    /**
     * Test {@link AlertDefinitionHash#invalidateAll()}.
     */
    @Test
    public void testInvalidateAll() {
        String hash = m_hash.getHash(AlertDefinitionHashTest.CLUSTERNAME, AlertDefinitionHashTest.HOSTNAME);
        TestCase.assertNotNull(hash);
        m_hash.invalidateAll();
        String newHash = m_hash.getHash(AlertDefinitionHashTest.CLUSTERNAME, AlertDefinitionHashTest.HOSTNAME);
        TestCase.assertEquals(hash, newHash);
        m_hash.invalidateAll();
        // add a new alert definition, forcing new hash
        AlertDefinitionEntity agentScoped = new AlertDefinitionEntity();
        agentScoped.setDefinitionId(System.currentTimeMillis());
        agentScoped.setClusterId(1L);
        agentScoped.setHash(UUID.randomUUID().toString());
        agentScoped.setServiceName("AMBARI");
        agentScoped.setComponentName("AMBARI_AGENT");
        agentScoped.setScope(HOST);
        agentScoped.setScheduleInterval(1);
        m_agentDefinitions.add(agentScoped);
        newHash = m_hash.getHash(AlertDefinitionHashTest.CLUSTERNAME, AlertDefinitionHashTest.HOSTNAME);
        TestCase.assertNotSame(hash, newHash);
    }

    /**
     * Test {@link AlertDefinitionHash#isHashCached(String,String)}.
     */
    @Test
    public void testIsHashCached() {
        TestCase.assertFalse(m_hash.isHashCached(AlertDefinitionHashTest.CLUSTERNAME, AlertDefinitionHashTest.HOSTNAME));
        String hash = m_hash.getHash(AlertDefinitionHashTest.CLUSTERNAME, AlertDefinitionHashTest.HOSTNAME);
        TestCase.assertNotNull(hash);
        TestCase.assertTrue(m_hash.isHashCached(AlertDefinitionHashTest.CLUSTERNAME, AlertDefinitionHashTest.HOSTNAME));
        m_hash.invalidate(AlertDefinitionHashTest.HOSTNAME);
        TestCase.assertFalse(m_hash.isHashCached(AlertDefinitionHashTest.CLUSTERNAME, AlertDefinitionHashTest.HOSTNAME));
        hash = m_hash.getHash(AlertDefinitionHashTest.CLUSTERNAME, AlertDefinitionHashTest.HOSTNAME);
        TestCase.assertNotNull(hash);
        TestCase.assertTrue(m_hash.isHashCached(AlertDefinitionHashTest.CLUSTERNAME, AlertDefinitionHashTest.HOSTNAME));
        m_hash.invalidateAll();
        TestCase.assertFalse(m_hash.isHashCached(AlertDefinitionHashTest.CLUSTERNAME, AlertDefinitionHashTest.HOSTNAME));
        hash = m_hash.getHash(AlertDefinitionHashTest.CLUSTERNAME, AlertDefinitionHashTest.HOSTNAME);
        TestCase.assertNotNull(hash);
        TestCase.assertTrue(m_hash.isHashCached(AlertDefinitionHashTest.CLUSTERNAME, AlertDefinitionHashTest.HOSTNAME));
    }

    /**
     * Test {@link AlertDefinitionHash#invalidateHosts(AlertDefinitionEntity)}.
     */
    @Test
    public void testInvalidateHosts() {
        TestCase.assertFalse(m_hash.isHashCached(AlertDefinitionHashTest.CLUSTERNAME, AlertDefinitionHashTest.HOSTNAME));
        String hash = m_hash.getHash(AlertDefinitionHashTest.CLUSTERNAME, AlertDefinitionHashTest.HOSTNAME);
        TestCase.assertNotNull(hash);
        TestCase.assertTrue(m_hash.isHashCached(AlertDefinitionHashTest.CLUSTERNAME, AlertDefinitionHashTest.HOSTNAME));
        Set<String> invalidatedHosts = m_hash.invalidateHosts(m_hdfsHost);
        TestCase.assertFalse(m_hash.isHashCached(AlertDefinitionHashTest.CLUSTERNAME, AlertDefinitionHashTest.HOSTNAME));
        TestCase.assertNotNull(invalidatedHosts);
        TestCase.assertEquals(1, invalidatedHosts.size());
        TestCase.assertTrue(invalidatedHosts.contains(AlertDefinitionHashTest.HOSTNAME));
    }

    /**
     *
     */
    @Test
    public void testInvalidateHost() {
        TestCase.assertFalse(m_hash.isHashCached(AlertDefinitionHashTest.CLUSTERNAME, AlertDefinitionHashTest.HOSTNAME));
        TestCase.assertFalse(m_hash.isHashCached("foo", AlertDefinitionHashTest.HOSTNAME));
        String hash = m_hash.getHash(AlertDefinitionHashTest.CLUSTERNAME, AlertDefinitionHashTest.HOSTNAME);
        TestCase.assertNotNull(hash);
        TestCase.assertTrue(m_hash.isHashCached(AlertDefinitionHashTest.CLUSTERNAME, AlertDefinitionHashTest.HOSTNAME));
        TestCase.assertFalse(m_hash.isHashCached("foo", AlertDefinitionHashTest.HOSTNAME));
        // invalidate the fake cluster and ensure the original cluster still
        // contains a cached valie
        m_hash.invalidate("foo", AlertDefinitionHashTest.HOSTNAME);
        TestCase.assertTrue(m_hash.isHashCached(AlertDefinitionHashTest.CLUSTERNAME, AlertDefinitionHashTest.HOSTNAME));
        TestCase.assertFalse(m_hash.isHashCached("foo", AlertDefinitionHashTest.HOSTNAME));
        m_hash.invalidateAll();
        TestCase.assertFalse(m_hash.isHashCached(AlertDefinitionHashTest.CLUSTERNAME, AlertDefinitionHashTest.HOSTNAME));
        TestCase.assertFalse(m_hash.isHashCached("foo", AlertDefinitionHashTest.HOSTNAME));
    }

    @Test
    public void testAggregateIgnored() {
        Set<String> associatedHosts = m_hash.getAssociatedHosts(m_mockCluster, AGGREGATE, "definitionName", "HDFS", null);
        TestCase.assertEquals(0, associatedHosts.size());
        associatedHosts = m_hash.getAssociatedHosts(m_mockCluster, PORT, "definitionName", "HDFS", null);
        TestCase.assertEquals(1, associatedHosts.size());
    }

    @Test
    public void testHashingAlgorithm() throws Exception {
        List<String> uuids = new ArrayList<>();
        uuids.add(m_hdfsService.getHash());
        uuids.add(m_hdfsHost.getHash());
        for (AlertDefinitionEntity entity : m_agentDefinitions) {
            uuids.add(entity.getHash());
        }
        Collections.sort(uuids);
        MessageDigest digest = MessageDigest.getInstance("MD5");
        for (String uuid : uuids) {
            digest.update(uuid.getBytes());
        }
        byte[] hashBytes = digest.digest();
        String expected = Hex.encodeHexString(hashBytes);
        TestCase.assertEquals(expected, m_hash.getHash(AlertDefinitionHashTest.CLUSTERNAME, AlertDefinitionHashTest.HOSTNAME));
    }

    /**
     *
     */
    private class MockModule implements Module {
        /**
         *
         */
        @Override
        public void configure(Binder binder) {
            Cluster cluster = EasyMock.createNiceMock(Cluster.class);
            EasyMock.expect(cluster.getAllConfigs()).andReturn(new ArrayList()).anyTimes();
            binder.bind(Clusters.class).toInstance(EasyMock.createNiceMock(Clusters.class));
            binder.bind(Cluster.class).toInstance(cluster);
            binder.bind(AlertDefinitionDAO.class).toInstance(EasyMock.createNiceMock(AlertDefinitionDAO.class));
            binder.bind(ConfigHelper.class).toInstance(EasyMock.createNiceMock(ConfigHelper.class));
        }
    }
}

