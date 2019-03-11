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
package org.apache.ambari.server.orm.dao;


import RepositoryVersionState.CURRENT;
import RepositoryVersionState.INSTALLED;
import RepositoryVersionState.INSTALLING;
import RepositoryVersionState.INSTALL_FAILED;
import com.google.inject.Injector;
import org.apache.ambari.server.orm.OrmTestHelper;
import org.apache.ambari.server.orm.entities.HostEntity;
import org.apache.ambari.server.orm.entities.HostVersionEntity;
import org.apache.ambari.server.orm.entities.RepositoryVersionEntity;
import org.apache.ambari.server.state.RepositoryVersionState;
import org.apache.ambari.server.state.StackId;
import org.junit.Assert;
import org.junit.Test;


/**
 * {@link org.apache.ambari.server.orm.dao.HostVersionDAO} unit tests.
 */
public class HostVersionDAOTest {
    private static Injector injector;

    private ResourceTypeDAO resourceTypeDAO;

    private ClusterDAO clusterDAO;

    private StackDAO stackDAO;

    private HostDAO hostDAO;

    private HostVersionDAO hostVersionDAO;

    private OrmTestHelper helper;

    private static final StackId HDP_22_STACK = new StackId("HDP", "2.2.0");

    private static final StackId BAD_STACK = new StackId("BADSTACK", "1.0");

    private static final String repoVersion_2200 = "2.2.0.0-1";

    private static final String repoVersion_2201 = "2.2.0.1-2";

    private static final String repoVersion_2202 = "2.2.0.2-3";

    /**
     * Test the {@link HostVersionDAO#findAll()} method.
     */
    @Test
    public void testFindAll() {
        Assert.assertEquals(3, hostVersionDAO.findAll().size());
    }

    /**
     * Test the {@link HostVersionDAO#findByHost(String)} method.
     */
    @Test
    public void testFindByHost() {
        Assert.assertEquals(1, hostVersionDAO.findByHost("test_host1").size());
        Assert.assertEquals(1, hostVersionDAO.findByHost("test_host2").size());
        Assert.assertEquals(1, hostVersionDAO.findByHost("test_host3").size());
        addMoreVersions();
        Assert.assertEquals(3, hostVersionDAO.findByHost("test_host1").size());
        Assert.assertEquals(3, hostVersionDAO.findByHost("test_host2").size());
        Assert.assertEquals(3, hostVersionDAO.findByHost("test_host3").size());
    }

    /**
     * Test the {@link HostVersionDAO#findByClusterStackAndVersion(String, org.apache.ambari.server.state.StackId, String)} method.
     */
    @Test
    public void testFindByClusterStackAndVersion() {
        Assert.assertEquals(3, hostVersionDAO.findByClusterStackAndVersion("test_cluster1", HostVersionDAOTest.HDP_22_STACK, HostVersionDAOTest.repoVersion_2200).size());
        Assert.assertEquals(3, hostVersionDAO.findAll().size());
        addMoreVersions();
        Assert.assertEquals(3, hostVersionDAO.findByClusterStackAndVersion("test_cluster1", HostVersionDAOTest.HDP_22_STACK, HostVersionDAOTest.repoVersion_2201).size());
        Assert.assertEquals(3, hostVersionDAO.findByClusterStackAndVersion("test_cluster1", HostVersionDAOTest.HDP_22_STACK, HostVersionDAOTest.repoVersion_2202).size());
        Assert.assertEquals(9, hostVersionDAO.findAll().size());
    }

    /**
     * Test the {@link HostVersionDAO#findByClusterAndHost(String, String)} method.
     */
    @Test
    public void testFindByClusterAndHost() {
        Assert.assertEquals(1, hostVersionDAO.findByClusterAndHost("test_cluster1", "test_host1").size());
        Assert.assertEquals(1, hostVersionDAO.findByClusterAndHost("test_cluster1", "test_host2").size());
        Assert.assertEquals(1, hostVersionDAO.findByClusterAndHost("test_cluster1", "test_host3").size());
        addMoreVersions();
        Assert.assertEquals(3, hostVersionDAO.findByClusterAndHost("test_cluster1", "test_host1").size());
        Assert.assertEquals(3, hostVersionDAO.findByClusterAndHost("test_cluster1", "test_host2").size());
        Assert.assertEquals(3, hostVersionDAO.findByClusterAndHost("test_cluster1", "test_host3").size());
    }

    /**
     * Test the {@link HostVersionDAO#findByCluster(String)} method.
     */
    @Test
    public void testFindByCluster() {
        Assert.assertEquals(3, hostVersionDAO.findByCluster("test_cluster1").size());
        addMoreVersions();
        Assert.assertEquals(9, hostVersionDAO.findByCluster("test_cluster1").size());
    }

    /**
     * Test the {@link HostVersionDAO#findByClusterHostAndState(String, String, org.apache.ambari.server.state.RepositoryVersionState)} method.
     */
    @Test
    public void testFindByClusterHostAndState() {
        Assert.assertEquals(1, hostVersionDAO.findByClusterHostAndState("test_cluster1", "test_host1", CURRENT).size());
        Assert.assertEquals(0, hostVersionDAO.findByClusterHostAndState("test_cluster1", "test_host1", INSTALLED).size());
        Assert.assertEquals(0, hostVersionDAO.findByClusterHostAndState("test_cluster1", "test_host2", INSTALLING).size());
        Assert.assertEquals(0, hostVersionDAO.findByClusterHostAndState("test_cluster1", "test_host3", INSTALL_FAILED).size());
        addMoreVersions();
        Assert.assertEquals(2, hostVersionDAO.findByClusterHostAndState("test_cluster1", "test_host1", INSTALLED).size());
        Assert.assertEquals(2, hostVersionDAO.findByClusterHostAndState("test_cluster1", "test_host2", INSTALLED).size());
        Assert.assertEquals(2, hostVersionDAO.findByClusterHostAndState("test_cluster1", "test_host3", INSTALLED).size());
        Assert.assertEquals(1, hostVersionDAO.findByClusterHostAndState("test_cluster1", "test_host1", CURRENT).size());
        Assert.assertEquals(1, hostVersionDAO.findByClusterHostAndState("test_cluster1", "test_host2", INSTALLING).size());
        Assert.assertEquals(1, hostVersionDAO.findByClusterHostAndState("test_cluster1", "test_host3", INSTALL_FAILED).size());
    }

    /**
     * Test the {@link HostVersionDAO#findByClusterStackVersionAndHost(String, StackId, String, String)} method.
     */
    @Test
    public void testFindByClusterStackVersionAndHost() {
        HostEntity host1 = hostDAO.findByName("test_host1");
        HostEntity host2 = hostDAO.findByName("test_host2");
        HostEntity host3 = hostDAO.findByName("test_host3");
        HostVersionEntity hostVersionEntity1 = new HostVersionEntity(host1, helper.getOrCreateRepositoryVersion(HostVersionDAOTest.HDP_22_STACK, HostVersionDAOTest.repoVersion_2200), RepositoryVersionState.CURRENT);
        hostVersionEntity1.setId(1L);
        HostVersionEntity hostVersionEntity2 = new HostVersionEntity(host2, helper.getOrCreateRepositoryVersion(HostVersionDAOTest.HDP_22_STACK, HostVersionDAOTest.repoVersion_2200), RepositoryVersionState.INSTALLED);
        hostVersionEntity2.setId(2L);
        HostVersionEntity hostVersionEntity3 = new HostVersionEntity(host3, helper.getOrCreateRepositoryVersion(HostVersionDAOTest.HDP_22_STACK, HostVersionDAOTest.repoVersion_2200), RepositoryVersionState.INSTALLED);
        hostVersionEntity3.setId(3L);
        hostVersionEntity1.equals(hostVersionDAO.findByClusterStackVersionAndHost("test_cluster1", HostVersionDAOTest.HDP_22_STACK, HostVersionDAOTest.repoVersion_2200, "test_host1"));
        Assert.assertEquals(hostVersionEntity1, hostVersionDAO.findByClusterStackVersionAndHost("test_cluster1", HostVersionDAOTest.HDP_22_STACK, HostVersionDAOTest.repoVersion_2200, "test_host1"));
        Assert.assertEquals(hostVersionEntity2, hostVersionDAO.findByClusterStackVersionAndHost("test_cluster1", HostVersionDAOTest.HDP_22_STACK, HostVersionDAOTest.repoVersion_2200, "test_host2"));
        Assert.assertEquals(hostVersionEntity3, hostVersionDAO.findByClusterStackVersionAndHost("test_cluster1", HostVersionDAOTest.HDP_22_STACK, HostVersionDAOTest.repoVersion_2200, "test_host3"));
        // Test non-existent objects
        Assert.assertEquals(null, hostVersionDAO.findByClusterStackVersionAndHost("non_existent_cluster", HostVersionDAOTest.HDP_22_STACK, HostVersionDAOTest.repoVersion_2200, "test_host3"));
        Assert.assertEquals(null, hostVersionDAO.findByClusterStackVersionAndHost("test_cluster1", HostVersionDAOTest.BAD_STACK, HostVersionDAOTest.repoVersion_2200, "test_host3"));
        Assert.assertEquals(null, hostVersionDAO.findByClusterStackVersionAndHost("test_cluster1", HostVersionDAOTest.HDP_22_STACK, "non_existent_version", "test_host3"));
        Assert.assertEquals(null, hostVersionDAO.findByClusterStackVersionAndHost("test_cluster1", HostVersionDAOTest.HDP_22_STACK, "non_existent_version", "non_existent_host"));
        addMoreVersions();
        // Expected
        HostVersionEntity hostVersionEntity1LastExpected = new HostVersionEntity(host1, helper.getOrCreateRepositoryVersion(HostVersionDAOTest.HDP_22_STACK, HostVersionDAOTest.repoVersion_2202), RepositoryVersionState.INSTALLED);
        HostVersionEntity hostVersionEntity2LastExpected = new HostVersionEntity(host2, helper.getOrCreateRepositoryVersion(HostVersionDAOTest.HDP_22_STACK, HostVersionDAOTest.repoVersion_2202), RepositoryVersionState.INSTALLING);
        HostVersionEntity hostVersionEntity3LastExpected = new HostVersionEntity(host3, helper.getOrCreateRepositoryVersion(HostVersionDAOTest.HDP_22_STACK, HostVersionDAOTest.repoVersion_2202), RepositoryVersionState.INSTALL_FAILED);
        // Actual
        HostVersionEntity hostVersionEntity1LastActual = hostVersionDAO.findByClusterStackVersionAndHost("test_cluster1", HostVersionDAOTest.HDP_22_STACK, HostVersionDAOTest.repoVersion_2202, "test_host1");
        HostVersionEntity hostVersionEntity2LastActual = hostVersionDAO.findByClusterStackVersionAndHost("test_cluster1", HostVersionDAOTest.HDP_22_STACK, HostVersionDAOTest.repoVersion_2202, "test_host2");
        HostVersionEntity hostVersionEntity3LastActual = hostVersionDAO.findByClusterStackVersionAndHost("test_cluster1", HostVersionDAOTest.HDP_22_STACK, HostVersionDAOTest.repoVersion_2202, "test_host3");
        // Trying to Mock the actual objects to override the getId() method will not work because the class that mockito creates
        // is still a Mockito wrapper. Instead, take advantage of an overloaded constructor that ignores the Id.
        Assert.assertEquals(hostVersionEntity1LastExpected, new HostVersionEntity(hostVersionEntity1LastActual));
        Assert.assertEquals(hostVersionEntity2LastExpected, new HostVersionEntity(hostVersionEntity2LastActual));
        Assert.assertEquals(hostVersionEntity3LastExpected, new HostVersionEntity(hostVersionEntity3LastActual));
    }

    @Test
    public void testDuplicates() throws Exception {
        HostEntity host1 = hostDAO.findByName("test_host1");
        RepositoryVersionEntity repoVersion = helper.getOrCreateRepositoryVersion(HostVersionDAOTest.HDP_22_STACK, HostVersionDAOTest.repoVersion_2200);
        HostVersionEntity hostVersionEntity1 = new HostVersionEntity(host1, repoVersion, RepositoryVersionState.CURRENT);
        try {
            hostVersionDAO.create(hostVersionEntity1);
            Assert.fail("Each host can have a relationship to a repo version, but cannot have more than one for the same repo");
        } catch (Exception e) {
            // expected
        }
    }
}

