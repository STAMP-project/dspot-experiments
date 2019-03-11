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
package org.apache.ambari.server.checks;


import UpgradeCheckStatus.FAIL;
import UpgradeCheckStatus.PASS;
import com.google.common.collect.Sets;
import java.util.Set;
import org.apache.ambari.server.orm.entities.RepositoryVersionEntity;
import org.apache.ambari.server.state.repository.VersionDefinitionXml;
import org.apache.ambari.spi.ClusterInformation;
import org.apache.ambari.spi.RepositoryVersion;
import org.apache.ambari.spi.upgrade.UpgradeCheckRequest;
import org.apache.ambari.spi.upgrade.UpgradeCheckResult;
import org.apache.ambari.spi.upgrade.UpgradeType;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


/**
 * Tests {@link RequiredServicesInRepositoryCheck}.
 */
@RunWith(MockitoJUnitRunner.class)
public class RequiredServicesInRepositoryCheckTest {
    private static final String CLUSTER_NAME = "c1";

    @Mock
    private VersionDefinitionXml m_vdfXml;

    @Mock
    private RepositoryVersion m_repositoryVersion;

    @Mock
    private RepositoryVersionEntity m_repositoryVersionEntity;

    private MockCheckHelper m_checkHelper = new MockCheckHelper();

    private RequiredServicesInRepositoryCheck m_requiredServicesCheck;

    /**
     * Used to return the missing dependencies for the test.
     */
    private Set<String> m_missingDependencies = Sets.newTreeSet();

    /**
     * Tests that a no missing services results in a passed test.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testNoMissingServices() throws Exception {
        ClusterInformation clusterInformation = new ClusterInformation(RequiredServicesInRepositoryCheckTest.CLUSTER_NAME, false, null, null, null);
        UpgradeCheckRequest request = new UpgradeCheckRequest(clusterInformation, UpgradeType.ROLLING, m_repositoryVersion, null, null);
        UpgradeCheckResult check = m_requiredServicesCheck.perform(request);
        Assert.assertEquals(PASS, check.getStatus());
        Assert.assertTrue(check.getFailedDetail().isEmpty());
    }

    /**
     * Tests that a missing required service causes the test to fail.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testMissingRequiredService() throws Exception {
        m_missingDependencies.add("BAR");
        ClusterInformation clusterInformation = new ClusterInformation(RequiredServicesInRepositoryCheckTest.CLUSTER_NAME, false, null, null, null);
        UpgradeCheckRequest request = new UpgradeCheckRequest(clusterInformation, UpgradeType.ROLLING, m_repositoryVersion, null, null);
        UpgradeCheckResult check = m_requiredServicesCheck.perform(request);
        Assert.assertEquals(FAIL, check.getStatus());
        Assert.assertFalse(check.getFailedDetail().isEmpty());
    }
}

