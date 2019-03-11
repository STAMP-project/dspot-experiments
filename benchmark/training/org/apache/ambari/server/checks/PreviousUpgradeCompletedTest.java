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


import Direction.UPGRADE;
import UpgradeCheckStatus.FAIL;
import UpgradeCheckStatus.PASS;
import org.apache.ambari.server.orm.entities.RepositoryVersionEntity;
import org.apache.ambari.server.orm.entities.UpgradeEntity;
import org.apache.ambari.server.state.Cluster;
import org.apache.ambari.server.state.Clusters;
import org.apache.ambari.server.state.StackId;
import org.apache.ambari.spi.upgrade.UpgradeCheckRequest;
import org.apache.ambari.spi.upgrade.UpgradeCheckResult;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


/**
 * Unit tests for PreviousUpgradeCompleted
 */
public class PreviousUpgradeCompletedTest {
    private final Clusters clusters = Mockito.mock(Clusters.class);

    private final Cluster cluster = Mockito.mock(Cluster.class);

    private StackId sourceStackId = new StackId("HDP", "2.2");

    private StackId targetStackId = new StackId("HDP", "2.2");

    private String destRepositoryVersion = "2.2.8.0-5678";

    private String clusterName = "cluster";

    private UpgradeCheckRequest checkRequest;

    private PreviousUpgradeCompleted puc = new PreviousUpgradeCompleted();

    private RepositoryVersionEntity toRepsitoryVersionEntity;

    @Test
    public void testPerform() throws Exception {
        // no existing upgrades
        Mockito.when(cluster.getUpgradeInProgress()).thenReturn(null);
        UpgradeCheckResult check = puc.perform(checkRequest);
        Assert.assertEquals(PASS, check.getStatus());
        // existing upgrade
        UpgradeEntity upgradeInProgress = Mockito.mock(UpgradeEntity.class);
        Mockito.when(upgradeInProgress.getDirection()).thenReturn(UPGRADE);
        Mockito.when(upgradeInProgress.getClusterId()).thenReturn(1L);
        Mockito.when(upgradeInProgress.getRepositoryVersion()).thenReturn(toRepsitoryVersionEntity);
        Mockito.when(cluster.getUpgradeInProgress()).thenReturn(upgradeInProgress);
        check = puc.perform(checkRequest);
        Assert.assertEquals(FAIL, check.getStatus());
    }
}

