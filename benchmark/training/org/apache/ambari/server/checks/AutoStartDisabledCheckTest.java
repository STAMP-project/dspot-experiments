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


import AutoStartDisabledCheck.RECOVERY_AUTO_START;
import AutoStartDisabledCheck.RECOVERY_ENABLED_KEY;
import AutoStartDisabledCheck.RECOVERY_TYPE_KEY;
import UpgradeCheckStatus.FAIL;
import UpgradeCheckStatus.PASS;
import com.google.common.collect.Lists;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.ambari.server.state.CheckHelper;
import org.apache.ambari.server.state.Clusters;
import org.apache.ambari.spi.ClusterInformation;
import org.apache.ambari.spi.RepositoryVersion;
import org.apache.ambari.spi.upgrade.UpgradeCheck;
import org.apache.ambari.spi.upgrade.UpgradeCheckRequest;
import org.apache.ambari.spi.upgrade.UpgradeCheckResult;
import org.apache.ambari.spi.upgrade.UpgradeType;
import org.apache.commons.lang.StringUtils;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests for {@link AutoStartDisabledCheck}
 */
public class AutoStartDisabledCheckTest {
    private final AutoStartDisabledCheck m_check = new AutoStartDisabledCheck();

    private final Clusters m_clusters = EasyMock.createMock(Clusters.class);

    private Map<String, String> m_configMap = new HashMap<>();

    RepositoryVersion repositoryVersion;

    ClusterInformation clusterInformation;

    @Test
    public void testIsApplicable() throws Exception {
        UpgradeCheckRequest request = new UpgradeCheckRequest(clusterInformation, UpgradeType.ROLLING, repositoryVersion, m_configMap, null);
        CheckHelper checkHelper = new CheckHelper();
        List<UpgradeCheck> applicableChecks = checkHelper.getApplicableChecks(request, Lists.newArrayList(m_check));
        Assert.assertTrue(((applicableChecks.size()) == 1));
    }

    @Test
    public void testNoAutoStart() throws Exception {
        UpgradeCheckRequest request = new UpgradeCheckRequest(clusterInformation, UpgradeType.ROLLING, repositoryVersion, null, null);
        UpgradeCheckResult check = m_check.perform(request);
        Assert.assertEquals(PASS, check.getStatus());
        Assert.assertTrue(StringUtils.isBlank(check.getFailReason()));
    }

    @Test
    public void testAutoStartFalse() throws Exception {
        UpgradeCheckRequest request = new UpgradeCheckRequest(clusterInformation, UpgradeType.ROLLING, repositoryVersion, null, null);
        m_configMap.put(RECOVERY_ENABLED_KEY, "false");
        UpgradeCheckResult check = m_check.perform(request);
        Assert.assertEquals(PASS, check.getStatus());
        Assert.assertTrue(StringUtils.isBlank(check.getFailReason()));
    }

    @Test
    public void testAutoStartTrue() throws Exception {
        UpgradeCheckRequest request = new UpgradeCheckRequest(clusterInformation, UpgradeType.ROLLING, repositoryVersion, null, null);
        m_configMap.put(RECOVERY_ENABLED_KEY, "true");
        m_configMap.put(RECOVERY_TYPE_KEY, RECOVERY_AUTO_START);
        UpgradeCheckResult check = m_check.perform(request);
        Assert.assertEquals(FAIL, check.getStatus());
        Assert.assertTrue(StringUtils.isNotBlank(check.getFailReason()));
        Assert.assertEquals(("Auto Start must be disabled before performing an Upgrade. To disable Auto Start, navigate to " + "Admin > Service Auto Start. Turn the toggle switch off to Disabled and hit Save."), check.getFailReason());
    }
}

