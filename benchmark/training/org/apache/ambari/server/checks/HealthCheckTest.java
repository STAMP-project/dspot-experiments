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


import AlertState.CRITICAL;
import AlertState.WARNING;
import UpgradeCheckStatus.PASS;
import java.util.Collections;
import org.apache.ambari.server.AmbariException;
import org.apache.ambari.server.orm.dao.AlertsDAO;
import org.apache.ambari.spi.ClusterInformation;
import org.apache.ambari.spi.upgrade.UpgradeCheckRequest;
import org.apache.ambari.spi.upgrade.UpgradeCheckResult;
import org.apache.ambari.spi.upgrade.UpgradeType;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class HealthCheckTest {
    private static final String CLUSTER_NAME = "cluster1";

    private static final long CLUSTER_ID = 1L;

    private static final String ALERT_HOSTNAME = "some hostname 1";

    private static final String ALERT_DEFINITION_LABEL = "label 1";

    private HealthCheck healthCheck;

    private AlertsDAO alertsDAO = Mockito.mock(AlertsDAO.class);

    @Test
    public void testWarningWhenNoAlertsExist() throws AmbariException {
        Mockito.when(alertsDAO.findCurrentByCluster(ArgumentMatchers.eq(HealthCheckTest.CLUSTER_ID))).thenReturn(Collections.emptyList());
        ClusterInformation clusterInformation = new ClusterInformation(HealthCheckTest.CLUSTER_NAME, false, null, null, null);
        UpgradeCheckRequest request = new UpgradeCheckRequest(clusterInformation, UpgradeType.ROLLING, null, null, null);
        UpgradeCheckResult result = healthCheck.perform(request);
        Assert.assertEquals(PASS, result.getStatus());
        Assert.assertTrue(result.getFailedDetail().isEmpty());
    }

    @Test
    public void testWarningWhenCriticalAlertExists() throws AmbariException {
        expectWarning(CRITICAL);
    }

    @Test
    public void testWarningWhenWarningAlertExists() throws AmbariException {
        expectWarning(WARNING);
    }
}

