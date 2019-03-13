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
package org.apache.ambari.server;


import RepositoryVersionState.INSTALLED;
import RepositoryVersionState.INSTALLING;
import RepositoryVersionState.INSTALL_FAILED;
import RepositoryVersionState.OUT_OF_SYNC;
import com.google.common.collect.Lists;
import com.google.inject.AbstractModule;
import com.google.inject.Injector;
import org.apache.ambari.server.orm.dao.HostVersionDAO;
import org.apache.ambari.server.orm.dao.ServiceComponentDesiredStateDAO;
import org.apache.ambari.server.state.RepositoryVersionState;
import org.easymock.Capture;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Test;


public class StateRecoveryManagerTest {
    private Injector injector;

    private HostVersionDAO hostVersionDAOMock;

    private ServiceComponentDesiredStateDAO serviceComponentDesiredStateDAOMock;

    @Test
    public void testCheckHostAndClusterVersions() throws Exception {
        StateRecoveryManager stateRecoveryManager = injector.getInstance(StateRecoveryManager.class);
        // Adding all possible host version states
        final Capture<RepositoryVersionState> installFailedHostVersionCapture = EasyMock.newCapture();
        final Capture<RepositoryVersionState> installingHostVersionCapture = EasyMock.newCapture();
        final Capture<RepositoryVersionState> installedHostVersionCapture = EasyMock.newCapture();
        final Capture<RepositoryVersionState> outOfSyncHostVersionCapture = EasyMock.newCapture();
        final Capture<RepositoryVersionState> upgradeFailedHostVersionCapture = EasyMock.newCapture();
        final Capture<RepositoryVersionState> upgradingHostVersionCapture = EasyMock.newCapture();
        final Capture<RepositoryVersionState> upgradedHostVersionCapture = EasyMock.newCapture();
        final Capture<RepositoryVersionState> currentHostVersionCapture = EasyMock.newCapture();
        expect(hostVersionDAOMock.findAll()).andReturn(Lists.newArrayList(getHostVersionMock("install_failed_version", INSTALL_FAILED, installFailedHostVersionCapture), getHostVersionMock("installing_version", INSTALLING, installingHostVersionCapture), getHostVersionMock("installed_version", INSTALLED, installedHostVersionCapture), getHostVersionMock("out_of_sync_version", OUT_OF_SYNC, outOfSyncHostVersionCapture)));
        // Adding all possible cluster version states
        final Capture<RepositoryVersionState> installFailedClusterVersionCapture = EasyMock.newCapture();
        final Capture<RepositoryVersionState> installingClusterVersionCapture = EasyMock.newCapture();
        final Capture<RepositoryVersionState> installedClusterVersionCapture = EasyMock.newCapture();
        final Capture<RepositoryVersionState> outOfSyncClusterVersionCapture = EasyMock.newCapture();
        final Capture<RepositoryVersionState> upgradeFailedClusterVersionCapture = EasyMock.newCapture();
        final Capture<RepositoryVersionState> upgradingClusterVersionCapture = EasyMock.newCapture();
        final Capture<RepositoryVersionState> upgradedClusterVersionCapture = EasyMock.newCapture();
        expect(serviceComponentDesiredStateDAOMock.findAll()).andReturn(Lists.newArrayList(getDesiredStateEntityMock("install_failed_version", INSTALL_FAILED, installFailedClusterVersionCapture), getDesiredStateEntityMock("installing_version", INSTALLING, installingClusterVersionCapture), getDesiredStateEntityMock("installed_version", INSTALLED, installedClusterVersionCapture), getDesiredStateEntityMock("out_of_sync_version", OUT_OF_SYNC, outOfSyncClusterVersionCapture)));
        replay(hostVersionDAOMock, serviceComponentDesiredStateDAOMock);
        stateRecoveryManager.checkHostAndClusterVersions();
        // Checking that only invalid host version states have been changed
        Assert.assertFalse(installFailedHostVersionCapture.hasCaptured());
        Assert.assertEquals(installingHostVersionCapture.getValue(), INSTALL_FAILED);
        Assert.assertFalse(installedHostVersionCapture.hasCaptured());
        Assert.assertFalse(outOfSyncHostVersionCapture.hasCaptured());
        Assert.assertFalse(upgradeFailedHostVersionCapture.hasCaptured());
        Assert.assertFalse(upgradingHostVersionCapture.hasCaptured());
        Assert.assertFalse(upgradedHostVersionCapture.hasCaptured());
        Assert.assertFalse(currentHostVersionCapture.hasCaptured());
        // Checking that only invalid cluster version states have been changed
        Assert.assertFalse(installFailedClusterVersionCapture.hasCaptured());
        Assert.assertEquals(installingClusterVersionCapture.getValue(), INSTALL_FAILED);
        Assert.assertFalse(installedClusterVersionCapture.hasCaptured());
        Assert.assertFalse(outOfSyncClusterVersionCapture.hasCaptured());
        Assert.assertFalse(upgradeFailedClusterVersionCapture.hasCaptured());
        Assert.assertFalse(upgradingClusterVersionCapture.hasCaptured());
        Assert.assertFalse(upgradedClusterVersionCapture.hasCaptured());
    }

    public class MockModule extends AbstractModule {
        @Override
        protected void configure() {
            bind(HostVersionDAO.class).toInstance(hostVersionDAOMock);
            bind(ServiceComponentDesiredStateDAO.class).toInstance(serviceComponentDesiredStateDAOMock);
        }
    }
}

